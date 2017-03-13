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
		return Database.executeQuery(database, sql);
	}
	
	
	static QueryResult queryForStdAnswer(Locale locale_lang, String std_question) {
		sql = "SELECT `" + COLNAME.std_Answer + "` FROM `" + TABLES.std_answers + "`"
				+ " WHERE `" + COLNAME.std_Question + "` = \"" + std_question.toLowerCase(locale_lang) + "\"";
//						+ " AND `locale` = \"" + locale_lang + "\"";
		return Database.executeQuery(sql);
	}

	
	static QueryResult queryForClientInfoByPrimaryID(long uid) {
		sql = "SELECT " + COLNAME.locale + "," 
						+ COLNAME.firstname + "," 
						+ COLNAME.lastname + ","
						+ COLNAME.email + "," 
						+ COLNAME.cellphone 
			+ " FROM `" + TABLES.info + "`"
			+ " WHERE `" + COLNAME.uid + "` = " + uid + "";
		return Database.executeQuery(DB.clients, sql);
	}
	
	
	static QueryResult queryForClientLoginByUsername(String username, String password) {
		sql = "SELECT * FROM `" + TABLES.login + "`"
				+ " WHERE `" + COLNAME.username + "` = \"" + username + "\""
				+ " AND `" + COLNAME.password + "` = AES_ENCRYPT('" + password + "','" + Database.KEY_STR + "')";
		return Database.executeQuery(DB.clients, sql);
	}
	
	
	static QueryResult queryForClientLoginByUID(long uid, String password) {
		sql = "SELECT * FROM `" + TABLES.login + "`"
				+ " WHERE `" + COLNAME.uid + "` = " + uid + ""
				+ " AND `" + COLNAME.password + "` = AES_ENCRYPT('" + password + "','" + Database.KEY_STR + "')";
		return Database.executeQuery(DB.clients, sql);
	}
	
	
	static QueryResult queryForClientInfoByEmail(String email) {
		sql = "SELECT " + COLNAME.uid + ","
						+ COLNAME.locale + "," 
						+ COLNAME.firstname + "," 
						+ COLNAME.lastname + ","
						+ COLNAME.cellphone 
			+ " FROM `" + TABLES.info + "`"
			+ " WHERE `" + COLNAME.email + "` = \"" + email + "\"";
		return Database.executeQuery(DB.clients, sql);
	}
	
	
	static QueryResult queryForClientInfoByCellphone(String cellphone) {
		sql = "SELECT " + COLNAME.uid + ","
						+ COLNAME.locale + "," 
						+ COLNAME.firstname + "," 
						+ COLNAME.lastname + ","
						+ COLNAME.email 
			+ " FROM `" + TABLES.info + "`"
			+ " WHERE `" + COLNAME.cellphone + "` = \"" + cellphone + "\"";
		return Database.executeQuery(DB.clients, sql);
	}
	
	
	static QueryResult updateUserPasswd (String username, String passwd, String newPasswd) {
		sql = "UPDATE `" + TABLES.login + "` "
			+ "SET `" + COLNAME.password + "` = AES_ENCRYPT('" + newPasswd + "','" + Database.KEY_STR + "') "
			+ "WHERE `" + COLNAME.username + "` = '" + username + "' "
			+ "AND `" + COLNAME.password + "` = AES_ENCRYPT('" + passwd + "','" + Database.KEY_STR + "')";
		return Database.executeQuery(DB.clients, sql);
	}
	

	static QueryResult insertNewClientLogin (String username, String passwd, String usergroup) {
		String dml = "INSERT INTO `" + TABLES.login + "` "
				+ "(`" + COLNAME.username + "`, `" + COLNAME.password + "`, `" + COLNAME.group + "`) "
				+ "VALUES "
				+ "('" + username + "', AES_ENCRYPT('" + passwd + "','" + Database.KEY_STR + "'), '" + usergroup + "');";
		sql = "SELECT * FROM `" + TABLES.login + "` WHERE `" + COLNAME.uid + "` = LAST_INSERT_ID();";
		return Database.executeUpdateAndGetPrimaryID(DB.clients, dml, sql);
	}
	
	
	static QueryResult insertNewClientInfo (long uid, String firstname, String lastname, 
			String email, String cellphone, String locale) {
		String dml = "INSERT INTO `" + TABLES.info + "` "
				+ "(`" + COLNAME.uid + "`, `" + COLNAME.firstname + "`, `" + COLNAME.lastname + "`,"
				+ " `" + COLNAME.email + "`, `" + COLNAME.cellphone + "`, `" + COLNAME.locale + "`) "
				+ "VALUES "
				+ "('" + uid + "', '" + firstname + "', '" + lastname + "',"
				+ " '" + email + "', '" + cellphone + "', '" + locale + "');";
		sql = "SELECT * FROM `" + TABLES.info + "` WHERE `" + COLNAME.uid + "` = LAST_INSERT_ID();";
		return Database.executeUpdateAndGetPrimaryID(DB.clients, dml, sql);
	}
	
	
	
	
	
	
	
}
