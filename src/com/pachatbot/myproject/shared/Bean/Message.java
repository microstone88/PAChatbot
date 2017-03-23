/**
 * 
 */
package com.pachatbot.myproject.shared.Bean;

import java.io.Serializable;
import java.sql.Timestamp;

import com.pachatbot.myproject.shared.PreDefined.ULocale;

/**
 * @author micro
 *
 */
public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Timestamp timeStamp;
	private ULocale locale;
	private String message;

	private boolean isGreeting = false;
	
	
	/**
	 * A message bean object only for RPC uses. Better not use this to instantiate a new message. 
	 * If it is the case, then do not forget to separately set the 
	 * message as well as the associated time stamp.
	 * 
	 */
	public Message() {
		//Nothing to do
	}
	
	/**
	 * a message object contains a String message as well 
	 * as a time stamp which is initialized at the time 
	 * when the message is instantiated.
	 * 
	 * @param message
	 */
	public Message(String message) {
		this.message = message;
		this.timeStamp = new Timestamp(System.currentTimeMillis());
		this.setLocale(ULocale.en_US);
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

	/**
	 * @return the timeStamp
	 */
	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @return the locale
	 */
	public ULocale getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(ULocale locale) {
		this.locale = locale;
	}

	/**
	 * @return the isGreeting
	 */
	public boolean isGreeting() {
		return isGreeting;
	}

	/**
	 * @param isGreeting the isGreeting to set
	 */
	public void setGreeting(boolean isGreeting) {
		this.isGreeting = isGreeting;
	}

}
