/**
 * 
 */
package com.pachatbot.myproject.shared.Bean;

import java.io.Serializable;

/**
 * @author micro
 *
 */
public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message = "";

	/**
	 * 
	 */
	public Message() {
		// Nothing to do
	}
	
	public Message(String message) {
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
