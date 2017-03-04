package com.pachatbot.myproject.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.pachatbot.myproject.shared.Bean.Account;

/**
 * @author Micro
 *
 */
@RemoteServiceRelativePath("SessionControl")
public interface SessionControl extends RemoteService {
	
	public Account getAccountFromSession();
	
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		
		private static SessionControlAsync instance;
		
		public static SessionControlAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(SessionControl.class);
			}
			return instance;
		}
	}
	
	Account loginServer(String name, String password);
	 
    Account loginFromSessionServer();
     
    boolean changePassword(String name, String newPassword);
 
    void logout();
	
}
