package com.pachatbot.myproject.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.pachatbot.myproject.client.SessionControl;
import com.pachatbot.myproject.shared.Bean.Account;

/**
 * @author Micro
 *
 */
public class SessionControlImpl extends RemoteServiceServlet implements SessionControl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String USER_ACCOUNT = "pac_account";

	
	@Override
	public Account getAccountFromSession() {
		HttpSession session = getThreadLocalRequest().getSession();
		return  (Account) session.getAttribute(USER_ACCOUNT);
	}


	@Override
	public Account loginServer(String name, String password) {
		
		// TODO validate user name and password
		
		// store the account/session identification
		Account account = new Account(name, password);
		storeUserInSession(account);
		return account;
	}


	@Override
	public Account loginFromSessionServer() {
		return getUserAlreadyFromSession();
	}


	@Override
	public boolean changePassword(String name, String newPassword) {
		// TODO change password logic
		return false;
	}


	@Override
	public void logout() {
		deleteUserFromSession();
	}
	
    private Account getUserAlreadyFromSession()
    {
        Account user = null;
        HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
        HttpSession session = httpServletRequest.getSession();
        Object userObj = session.getAttribute("user");
        if (userObj != null && userObj instanceof Account)
        {
            user = (Account) userObj;
        }
        return user;
    }
 
    private void storeUserInSession(Account account)
    {
        HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute(USER_ACCOUNT, account);
    }
 
    private void deleteUserFromSession()
    {
        HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
        HttpSession session = httpServletRequest.getSession();
        session.removeAttribute(USER_ACCOUNT);
    }

}
