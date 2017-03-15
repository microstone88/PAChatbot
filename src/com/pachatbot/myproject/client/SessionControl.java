package com.pachatbot.myproject.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.pachatbot.myproject.shared.PreDefinedEnum.UStatus;
import com.pachatbot.myproject.shared.Bean.Account;

/**
 * @author Micro
 *
 */
@RemoteServiceRelativePath("SessionControl")
public interface SessionControl extends RemoteService {
	
	Account login(String name, String password);
	
	void logout(long uid);
	
	boolean changePassword(long uid, String oldPasswd, String newPasswd);
	
    Account loginFromSessionServer();
    
    Account register(String firstname, String lastname, String email, String cellphone,
    		String username, String password);
    
    UStatus checkUserStatus(Account account);
    
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	static class Utils {
		
		private static SessionControlAsync instance;

		static SessionControlAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(SessionControl.class);
			}
			return instance;
		}
	}
 
}
