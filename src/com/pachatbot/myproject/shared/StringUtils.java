/**
 * 
 */
package com.pachatbot.myproject.shared;


/**
 * @author micro
 *
 */
public abstract class StringUtils {

	public static String CapitalizeFstLetter(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
}