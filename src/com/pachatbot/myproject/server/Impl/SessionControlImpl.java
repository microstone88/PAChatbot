package com.pachatbot.myproject.server.Impl;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.pachatbot.myproject.client.SessionControl;
import com.pachatbot.myproject.server.AccUtils;
import com.pachatbot.myproject.server.QueryResult;
import com.pachatbot.myproject.server.Impl.Database.TLogin;
import com.pachatbot.myproject.shared.FieldVerifier;
import com.pachatbot.myproject.shared.PreDefined.TInfo;
import com.pachatbot.myproject.shared.PreDefined.UGroup;
import com.pachatbot.myproject.shared.PreDefined.ULocale;
import com.pachatbot.myproject.shared.PreDefined.UStatus;
import com.pachatbot.myproject.shared.Bean.Account;

/**
 * @author micro
 *
 */
public class SessionControlImpl extends RemoteServiceServlet implements SessionControl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String USER_ACCOUNT = "pachatbot";

	@Override
	public Account login(String identifier, String password) {
		
		long uid = 0; Account re = new Account();
		QueryResult qrInfo = new QueryResult();
		QueryResult qrLogin = new QueryResult();
		
		if (FieldVerifier.Email.isValid(identifier)) {
			// Try "identifier" as email address
			qrInfo = SqlQueryUtils.queryForClientInfoByEmail(identifier);
			if (qrInfo.isUniqueRow()) {
				uid = (long) qrInfo.getValue(1, TInfo.Column.UID);
				// Verify password
				qrLogin = SqlQueryUtils.queryForClientLoginByUID(uid, password);
			} else {re.setUid(-2); return re;} // email doesn't exist (login not successful!)
		}
		
		if (FieldVerifier.Cellphone.isValid(identifier)) {
			// Try "identifier" as cellphone number
			qrInfo = SqlQueryUtils.queryForClientInfoByCellphone(identifier);
			if (qrInfo.isUniqueRow()) {
				uid = (long) qrInfo.getValue(1, TInfo.Column.UID);
				// Verify password
				qrLogin = SqlQueryUtils.queryForClientLoginByUID(uid, password);
			} else {re.setUid(-3); return re;} // cellphone number doesn't exist (login not successful!)
		}
		
		if (FieldVerifier.Username.isValid(identifier)) { 
			// Try "identifier" as username
			if (SqlQueryUtils.queryForClientLoginByUsername(identifier).isEmpty()) {
				re.setUid(-1); return re; // username doesn't exist (login not successful!)
			}
			// Verify password
			qrLogin = SqlQueryUtils.queryForClientLoginByUsername(identifier, password);
		}
		
		if (!qrLogin.isUniqueRow()) {re.setUid(-4); return re;}// wrong combination (login not successful!)
		else {
			uid = (long) qrLogin.getValue(1, TLogin.Column.UID);
			if (!qrInfo.isUniqueRow()) 
				qrInfo = SqlQueryUtils.queryForClientInfoByUID(uid); // uid is always unique
			// Combine the query results from the two tables (both have unique row)
			re = combine(qrLogin, qrInfo);
			// Store the account/session identification
			storeUserInSession(re);
			// Update user status
			String ip_server = this.getThreadLocalRequest().getRemoteAddr();
			SqlQueryUtils.updateUserStatus(uid, ip_server, UStatus.active);
		}
		return re;
	}
	
	@Override
	public Account register(String firstname, String lastname, 
			String email, String cellphone, 
			String username, String password) {
		
		Account re = new Account();
		
		if (!SqlQueryUtils.queryForClientLoginByUsername(username).isEmpty()) {
			re.setUid(-1); return re; // username already exists!
		}
		
		if (!email.equals("NULL")) {
			if (!SqlQueryUtils.queryForClientInfoByEmail(email).isEmpty())
				{re.setUid(-2); return re;} // email address already registered!
		}
		if (!cellphone.equals("NULL")) {
			if (!SqlQueryUtils.queryForClientInfoByCellphone(cellphone).isEmpty())
				{re.setUid(-3); return re;} // cellphone number already registered!
		}
		
		String ip_address = this.getThreadLocalRequest().getRemoteAddr();
		QueryResult qrLogin = SqlQueryUtils.insertNewClientLogin(
				username, password, UGroup.user, ip_address, UStatus.active);
		
		if (qrLogin.isUniqueRow()) {
			long uid = (long) qrLogin.getValue(1, TLogin.Column.UID);
			QueryResult qrInfo = SqlQueryUtils.insertNewClientInfo(
					uid, firstname, lastname, email, cellphone, ULocale.fr_FR);
			// Combine the query results from the two tables (both have unique row)
			re = combine(qrLogin, qrInfo);
			// store the account/session identification
			storeUserInSession(re);
		}
		return re;
	}
	
	private Account combine(QueryResult qrLogin, QueryResult qrInfo) {
		Account re = new Account();
		long uid_l = (long) qrLogin.getValue(1, TLogin.Column.UID);
		long uid_i = (long) qrInfo.getValue(1, TInfo.Column.UID);
		if (uid_l == uid_i) {
			re.setUid(uid_l);
			re.setLastActive((Timestamp) qrLogin.getValue(1, TLogin.Column.LASTACT));
			re.setLastIP((String) qrLogin.getValue(1, TLogin.Column.LASTIP));
			re.setStatus(UStatus.valueOf((String) qrLogin.getValue(1, TLogin.Column.STATUS)));
			re.setGroup(UGroup.valueOf((String) qrLogin.getValue(1, TLogin.Column.GROUP)));
			
			AccUtils.updateAccountInfoFrom(qrInfo, re);
			
//			re.setFirstname((String) qrInfo.getValue(1, TInfo.Column.FIRSTNAME));
//			re.setLastname((String) qrInfo.getValue(1, TInfo.Column.LASTNAME));
//			re.setEmail((String) qrInfo.getValue(1, TInfo.Column.EMAIL));
//			re.setCellphone((String) qrInfo.getValue(1, TInfo.Column.CELLPHONE));
//			re.setLocale(ULocale.valueOf((String) qrInfo.getValue(1, TInfo.Column.LOCALE)));
			
			// Add payment information
			QueryResult qr = SqlQueryUtils.queryForClientPaymentInfoByUID(uid_l);
			AccUtils.updateAccountInfoFrom(qr, re);
			
//			re.setPayPal((String) qr.getValue(1, TInfo.Column.PayPal));
//			re.setAlipay((String) qr.getValue(1, TInfo.Column.Alipay));
//			re.setWeChat((String) qr.getValue(1, TInfo.Column.WeChat));
			
		}
		else throw new IllegalArgumentException("uid dosen't match! login uid = " + uid_l + ", info uid = " + uid_i);
		return re;
	}

	@Override
	public void logout(long uid) {
		// Update user status
		String ip_server = this.getThreadLocalRequest().getRemoteAddr();
		SqlQueryUtils.updateUserStatus(uid, ip_server, UStatus.offline);
		deleteUserFromSession();
	}

	@Override
	public boolean changePassword(long uid, String oldPasswd, String newPasswd) {
		int row_count = SqlQueryUtils.updateUserPasswd(uid, oldPasswd, newPasswd);
		if (row_count == 1) return true;
		else return false;
	}

	@Override
	public Account loginFromSessionServer(long uid) {
		Account re = fetchUserFromSession();
		if (re.getUid() == 0) 
			SqlQueryUtils.updateOnlyUserStatus(uid, UStatus.expired);
		else if (re.getUid() == uid) {
			QueryResult qrCont = SqlQueryUtils.queryForClientInfoByUID(re.getUid());
			int count_c = AccUtils.updateAccountInfoFrom(qrCont, re);
			QueryResult qrPaym = SqlQueryUtils.queryForClientPaymentInfoByUID(re.getUid());
			int count_p = AccUtils.updateAccountInfoFrom(qrPaym, re);
			re.setNumOfIncoherents(count_c + count_p);
		}
		return re;
	}
	
	@Override
	public Account update(long uid, TInfo.Column ref, String newStr) {
		Account re = fetchUserFromSession();
		if (re.getUid() == uid) {
			
			switch (ref) {
			case EMAIL:
				QueryResult qrEmail = SqlQueryUtils.queryForClientInfoByEmail(newStr);
				if (!qrEmail.isEmpty()) { re.setUid(-1); return re; }
				break;
			case CELLPHONE:
				QueryResult qrCell = SqlQueryUtils.queryForClientInfoByCellphone(newStr);
				if (!qrCell.isEmpty()) { re.setUid(-1); return re; }
				break;
			default:
				break;
			}
			
			QueryResult qr = SqlQueryUtils.updateClientInfo(uid, ref, newStr);
			if (qr.isUniqueValue()) {
				if (qr.getUniqueValue() != null) re.set(ref, qr.getUniqueValue().toString());
				else re.set(ref, null);
			}
			
			// update the account/session identification
			// (!!! Not necessary! user stored in session is automatically synchronized !!!)
//			storeUserInSession(re); 
		}
		return re;
	}
	
	@Override
	public UStatus checkUserStatus(Account account) {
		// TODO check user status
		return null;
	}
	
	/**
	 * fetch the user account from the active session, if the user has 
	 * already been identified. Otherwise, return null.
	 * 
	 * @return an account object or null
	 */
    private Account fetchUserFromSession() {
        Account account = new Account();
        HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
        HttpSession session = httpServletRequest.getSession();
        Object userObj = session.getAttribute(USER_ACCOUNT);
        if (userObj != null && userObj instanceof Account) {
            account = (Account) userObj;
        }
        return account;
    }
 
    /**
     * store the user account in the active session.
     * 
     * @param account
     */
    private void storeUserInSession(Account account) {
        HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute(USER_ACCOUNT, account);
        session.setMaxInactiveInterval(60*20); // for 20 minutes
    }
 
    /**
     * remove any user account from the active session.
     */
    private void deleteUserFromSession() {
        HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
        HttpSession session = httpServletRequest.getSession();
        session.removeAttribute(USER_ACCOUNT);
    }


}
