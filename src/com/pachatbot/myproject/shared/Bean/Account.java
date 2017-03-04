/**
 * 
 */
package com.pachatbot.myproject.shared.Bean;

import java.io.Serializable;

/**
 * This bean object can be used to transfer the information 
 * that helps to identify the current user on the client side.
 * 
 * @author micro
 */
public class Account implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 *  List of the information that could be transfer 
	 *  to the client side via the Account object.
	 *  
	 *  Attention: some sensitive information about 
	 *  the current user should NEVER be transfered 
	 *  to the client side. 
	 */
	private String username;
	private String password;
	
	private String firstname;
	private String lastname;
	
	/**
	 * 
	 */
	public Account() {
		
	}
	
	public Account(String username, String password) {
		this.setUsername(username);
		this.setPassword(password);
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
}
