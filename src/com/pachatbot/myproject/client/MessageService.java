/**
 * 
 */
package com.pachatbot.myproject.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.pachatbot.myproject.shared.Bean.Message;

/**
 * @author micro
 *
 */
@RemoteServiceRelativePath("message")
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
}
