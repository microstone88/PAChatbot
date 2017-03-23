/**
 * 
 */
package com.pachatbot.myproject.server;

import java.util.Locale;

import com.pachatbot.myproject.shared.Bean.QueryResult;

/**
 * @author micro
 *
 */
public abstract class PiBrain {
	
	public static Locale detectLanguage(String question) {
		//TODO auto-detect language
		
		
		
		return Locale.US;
	}
	
	public static String parseToStdQuestion(String question) {
		//TODO parse a given question to standard question
		return question.trim();
		
	}
	
	public static void reduceAnswers(QueryResult qResult) {
		//TODO reduce answers (server side)
		
		
	}
	
}
