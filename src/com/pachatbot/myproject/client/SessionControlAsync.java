package com.pachatbot.myproject.client;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.pachatbot.myproject.shared.PreDefinedEnum.USERSTATUS;
import com.pachatbot.myproject.shared.Bean.Account;


/**
 * @author Micro
 *
 */
public interface SessionControlAsync {
	
	void login(String name, String password, AsyncCallback<Account> callback);
	
	void logout(AsyncCallback<Void> callback);
	
	void changePassword(String name, String newPassword, AsyncCallback<Boolean> callback);
	
	void loginFromSessionServer(AsyncCallback<Account> callback);
	
	void register(String firstname, String lastname, String email, String cellphone,
    		String username, String password, AsyncCallback<Account> callback);
	
	void checkUserStatus(Account account, AsyncCallback<USERSTATUS> callback);
	
}
