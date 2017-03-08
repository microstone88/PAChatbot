/**
 * 
 */
package com.pachatbot.myproject.server;

import java.util.Locale;

/**
 * @author micro
 *
 */
abstract class SqlQueryUtils extends Database {
	
	private static String sql = "";
	
	/**
	 * Select all content from an existing table in a given database.
	 * Only for debugging.
	 * 
	 * @param database a database
	 * @param table a table in the given database
	 * @return
	 */
	static QueryResult selectAllFromTable(DB database, TABLES table) {
		sql = "SELECT * FROM " + table;
		return Database.runQuery(database, sql);
	}
	
	
	static QueryResult queryForStdAnswer(Locale locale_lang, String std_question) {
		sql = "SELECT `std_answer` FROM " + TABLES.basic_std_answers 
				+ " WHERE `std_question` = \"" + std_question.toLowerCase(locale_lang) + "\"";
//						+ " AND `locale` = \"" + locale_lang + "\"";
		return Database.runQuery(sql);
	}
	
	
	static QueryResult queryForClientInfoByPrimaryID(Integer uid) {
		sql = "SELECT * FROM " + TABLES.clients_info 
				+ " WHERE `uid` = " + uid + "";
		return Database.runQuery(DB.clients, sql);
	}
	
	

	
}
