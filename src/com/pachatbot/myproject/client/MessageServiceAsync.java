/**
 * 
 */
package com.pachatbot.myproject.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.pachatbot.myproject.shared.Bean.Message;

/**
 * @author micro
 *
 */
public interface MessageServiceAsync {
	
	void getGreetingMessage(String input, AsyncCallback<Message> callback);
	
	void connectToDB(AsyncCallback<String> callback);
	
	void getResponse(String question, AsyncCallback<Message> callback);
	
}
