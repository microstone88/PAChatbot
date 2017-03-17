/**
 * 
 */
package com.pachatbot.myproject.shared;


/**
 * @author micro
 *
 */
public abstract class StringUtils {

	public static String CapFstLetter(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	public static String CapAllLetter(String str) {
		return str.toUpperCase();
	}
	
}
