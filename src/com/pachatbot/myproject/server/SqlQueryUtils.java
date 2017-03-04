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
abstract class SqlQueryUtils extends Database {
	
	private static String sql = "";
	
	static QueryResult queryForStdAnswer(Locale locale_lang, String std_question) {
		sql = "SELECT `std_answer` FROM " + TABLES.basic_std_answers 
				+ " WHERE `std_question` = \"" + std_question.toLowerCase(locale_lang) + "\"";
		return Database.runQuery(sql);
	}
	
	
	static QueryResult queryForClientInfoByPrimaryID(Integer uid) {
		sql = "SELECT * FROM " + TABLES.clients_info 
				+ " WHERE `uid` = " + uid + "";
		return Database.runQuery(DB.clients, sql);
	}
	
	
	
	
	/**
	 * This is only for debugging.
	 * 
	 * @return the content in {@link TABLES#basic_std_answers}
	 */
	static QueryResult testQuery() {
		String sql = "SELECT * FROM " + TABLES.basic_std_answers;
		return Database.runQuery(sql);
	}

}
