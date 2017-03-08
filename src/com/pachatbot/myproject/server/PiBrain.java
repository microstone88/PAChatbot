/**
 * 
 */
package com.pachatbot.myproject.server;

import java.util.Locale;

/**
 * @author micro
 *
 */
abstract class PiBrain {

	static Locale detectLanguage(String question) {
		//TODO auto-detect language
		return Locale.US;
	}
	
	static String parseToStdQuestion(String question) {
		//TODO parse a given question to standard question
		return question.trim();
		
	}
	
	
	
}
