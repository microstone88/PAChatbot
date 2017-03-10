/**
 * 
 */
package com.pachatbot.myproject.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.pachatbot.myproject.shared.Bean.Message;

/**
 * @author micro
 *
 */
@RemoteServiceRelativePath("MessageService")
public interface MessageService extends RemoteService {
	
	/**
	 * get a simple greeting message
	 * 
	 * @param input
	 * @return "Hello!"
	 */
	Message getGreetingMessage(String input);
	
	/**
	 * try to connect to the database.
	 * @return "", if successful. Otherwise, exception message.
	 */
	String connectToDB();
	
	/**
	 * get a response to the question
	 * 
	 * @param question 
	 * @return the response to the question
	 */
	Message getResponse(String question);
	
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	static class Utils {
		
		private static MessageServiceAsync instance;

		static MessageServiceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(MessageService.class);
			}
			return instance;
		}
	}
}
