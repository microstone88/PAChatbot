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
		String lower = str.toLowerCase();
		return lower.substring(0, 1).toUpperCase() + lower.substring(1);
	}
	
	public static String CapAllLetter(String str) {
		return str.toUpperCase();
	}
	
}
