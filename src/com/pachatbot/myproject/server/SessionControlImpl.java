package com.pachatbot.myproject.server;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.pachatbot.myproject.client.SessionControl;
import com.pachatbot.myproject.server.Database.COLNAME;
import com.pachatbot.myproject.shared.PreDefinedEnum.LOCALE;
import com.pachatbot.myproject.shared.PreDefinedEnum.STATUS;
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
		
		int uid = 0; Account re = new Account();
		QueryResult qrInfo = new QueryResult();
		
		// Try "identifier" as user name
		QueryResult qrLogin = SqlQueryUtils.queryForClientLoginByUsername(identifier, password);
		
		if (qrLogin.isEmpty()) {
			
			// Try "identifier" as email address or cellphone number
			QueryResult qrInfo_e = SqlQueryUtils.queryForClientInfoByEmail(identifier);
			QueryResult qrInfo_c = SqlQueryUtils.queryForClientInfoByCellphone(identifier);
			
			if (!qrInfo_e.isEmpty() || !qrInfo_c.isEmpty()) {
				qrInfo = (!qrInfo_e.isEmpty()) ? qrInfo_e : qrInfo_c;
				uid = (int) qrInfo.getValue(1, COLNAME.uid.toString());
				qrLogin = SqlQueryUtils.queryForClientLoginByUID(uid, password);
			} else return re; // return empty account
		}
		
		if (qrLogin.isUniqueRow()) {
			uid = (int) qrLogin.getValue(1, COLNAME.uid.toString());
			re.setUid(uid);
			re.setLastActive((Timestamp) qrLogin.getValue(1, COLNAME.lastactive.toString()));
			re.setLastIP((String) qrLogin.getValue(1, COLNAME.lastip.toString()));
			re.setStatus(STATUS.valueOf((String) qrLogin.getValue(1, COLNAME.status.toString())));
			
			qrInfo = SqlQueryUtils.queryForClientInfoByPrimaryID(uid);
			re.setFirstname((String) qrInfo.getValue(1, COLNAME.firstname.toString()));
			re.setLastname((String) qrInfo.getValue(1, COLNAME.lastname.toString()));
			re.setEmail((String) qrInfo.getValue(1, COLNAME.email.toString()));
			re.setCellphone((String) qrInfo.getValue(1, COLNAME.cellphone.toString()));
			re.setLocale(LOCALE.valueOf((String) qrInfo.getValue(1, COLNAME.locale.toString())));
			
			// store the account/session identification
			storeUserInSession(re);
		}
		return re;
	}

	@Override
	public void logout() {
		deleteUserFromSession();
	}

	@Override
	public boolean changePassword(String name, String newPassword) {
		// TODO change password logic
		return false;
	}

	@Override
	public Account loginFromSessionServer() {
		return fetchUserFromSession();
	}
	
	/**
	 * fetch the user account from the active session, if the user has 
	 * already been identified. Otherwise, return null.
	 * 
	 * @return an account object or null
	 */
    private Account fetchUserFromSession() {
        Account account = null;
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
