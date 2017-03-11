/**
 * 
 */
package com.pachatbot.myproject.shared.Bean;

import java.io.Serializable;
import java.sql.Timestamp;
import com.pachatbot.myproject.shared.PreDefinedEnum.LOCALE;
import com.pachatbot.myproject.shared.PreDefinedEnum.USERSTATUS;

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
	private int uid = 0;
	
//	private String username;
//	private String password;
	
	private String firstname;
	private String lastname;
	
	private String email;
	private String cellphone;
	
	private String lastIP;
	private Timestamp lastActive;
	
	private LOCALE locale;
	private USERSTATUS status;
	
	
//	private Locale javaLocale; // Need to customize the serialization (and it dosen't work!).
	
	private static final int LOCAL_VARS_COUNT = 9;
	
	/**
	 * An empty account will return "uid = 0".
	 */
	public Account() {
		// Nothing to do
	}
	
	public Account(int uid) {
		this.setUid(uid);
//		this.setUsername(username);
//		this.setPassword(password);
	}
	
	public Account(String... str) {
		if (str.length != LOCAL_VARS_COUNT)
			throw new IllegalArgumentException(
					"Expected: " + LOCAL_VARS_COUNT + "length: " + str.length);
		else {
			this.setUid(Integer.valueOf(str[0]));
			this.setFirstname(str[1]);
			this.setLastname(str[2]);
			this.setLocale(LOCALE.valueOf(str[3]));
			this.setEmail(str[4]);
			this.setCellphone(str[5]);
			this.setLastActive(Timestamp.valueOf(str[6]));
			this.setLastIP(str[7]);
			this.setStatus(USERSTATUS.valueOf(str[8]));
//			String[] loc = str[9].split("_");
//			this.setJavaLocale(new Locale(loc[0], loc[1]));
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getUid() + "||" 
				+ this.getFirstname() + "||" 
				+ this.getLastname() + "||"
				+ this.getLocale() + "||" 
				+ this.getEmail() + "||" 
				+ this.getCellphone() + "||"
				+ this.getLastActive() + "||" 
				+ this.getLastIP() + "||" 
				+ this.getStatus();
//				+ this.getJavaLocale();
	}

	/**
	 * @return the uid
	 */
	public int getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(int uid) {
		this.uid = uid;
	}

//	/**
//	 * @return the username
//	 */
//	public String getUsername() {
//		return username;
//	}
//
//	/**
//	 * @param username the username to set
//	 */
//	public void setUsername(String username) {
//		this.username = username;
//	}
//
//	/**
//	 * @return the password
//	 */
//	public String getPassword() {
//		return password;
//	}
//
//	/**
//	 * @param password the password to set
//	 */
//	public void setPassword(String password) {
//		this.password = password;
//	}

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

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the cellphone
	 */
	public String getCellphone() {
		return cellphone;
	}

	/**
	 * @param cellphone the cellphone to set
	 */
	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}


	/**
	 * @return the lastIP
	 */
	public String getLastIP() {
		return lastIP;
	}

	/**
	 * @param lastIP the lastIP to set
	 */
	public void setLastIP(String lastIP) {
		this.lastIP = lastIP;
	}

	/**
	 * @return the lastActive
	 */
	public Timestamp getLastActive() {
		return lastActive;
	}

	/**
	 * @param lastActive the lastActive to set
	 */
	public void setLastActive(Timestamp lastActive) {
		this.lastActive = lastActive;
	}

	/**
	 * @return the locale
	 */
	public LOCALE getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(LOCALE locale) {
		this.locale = locale;
	}

	/**
	 * @return the status
	 */
	public USERSTATUS getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(USERSTATUS status) {
		this.status = status;
	}

//	/**
//	 * @return the javaLocale
//	 */
//	public Locale getJavaLocale() {
//		return javaLocale;
//	}
//
//	/**
//	 * @param javaLocale the javaLocale to set
//	 */
//	public void setJavaLocale(Locale javaLocale) {
//		this.javaLocale = javaLocale;
//	}
	
}
