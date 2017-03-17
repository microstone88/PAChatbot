package com.pachatbot.myproject.client;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.pachatbot.myproject.shared.PreDefined.TInfo;
import com.pachatbot.myproject.shared.PreDefined.UStatus;
import com.pachatbot.myproject.shared.Bean.Account;


/**
 * @author Micro
 *
 */
public interface SessionControlAsync {
	
	void login(String name, String password, AsyncCallback<Account> callback);
	
	void logout(long uid, AsyncCallback<Void> callback);
	
	void changePassword(long uid, String oldPasswd, String newPasswd, AsyncCallback<Boolean> callback);
	
	void loginFromSessionServer(long uid, AsyncCallback<Account> callback);
	
	void register(String firstname, String lastname, String email, String cellphone,
    		String username, String password, AsyncCallback<Account> callback);
	
	void update(long uid, TInfo.Column ref, String newStr, AsyncCallback<Account> callback);
	
	void checkUserStatus(Account account, AsyncCallback<UStatus> callback);
	
}
