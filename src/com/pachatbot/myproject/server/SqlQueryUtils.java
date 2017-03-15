/**
 * 
 */
package com.pachatbot.myproject.server;

import java.util.Locale;

import com.pachatbot.myproject.shared.PreDefinedEnum.UGroup;
import com.pachatbot.myproject.shared.PreDefinedEnum.UStatus;

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
	static QueryResult selectAllFromTable(DB database, Tables table) {
		sql = "SELECT * FROM " + table;
		return Database.executeQuery(database, sql);
	}
	
	
	static QueryResult queryForStdAnswer(Locale locale_lang, String std_question) {
		sql = "SELECT `" + TAnswers.Column.STDANS + "` FROM `" + Tables.STDANS + "`"
				+ " WHERE `" + TAnswers.Column.STDQUEST + "` = \"" + std_question.toLowerCase(locale_lang) + "\"";
//						+ " AND `locale` = \"" + locale_lang + "\"";
		return Database.executeQuery(sql);
	}

	
	static QueryResult queryForClientLoginByUsername(String username, String password) {
		sql = "SELECT * FROM `" + Tables.CLOGIN + "`"
				+ " WHERE `" + TLogin.Column.USERNAME + "` = \"" + username + "\""
				+ " AND `" + TLogin.Column.PASSWD + "` = AES_ENCRYPT('" + password + "','" + Database.KEY_STR + "')";
		return Database.executeQuery(DB.CLIENTS, sql);
	}
	
	
	static QueryResult queryForClientLoginByUID(long uid, String password) {
		sql = "SELECT * FROM `" + Tables.CLOGIN + "`"
				+ " WHERE `" + TLogin.Column.UID + "` = " + uid + ""
				+ " AND `" + TLogin.Column.PASSWD + "` = AES_ENCRYPT('" + password + "','" + Database.KEY_STR + "')";
		return Database.executeQuery(DB.CLIENTS, sql);
	}
	
	
	static QueryResult queryForClientInfoByPrimaryID(long uid) {
		sql = "SELECT " + TInfo.Column.UID + ","
						+ TInfo.Column.LOCALE + "," 
						+ TInfo.Column.FIRSTNAME + "," 
						+ TInfo.Column.LASTNAME + ","
						+ TInfo.Column.CELLPHONE + "," 
						+ TInfo.Column.EMAIL 
			+ " FROM `" + Tables.CINFO + "`"
			+ " WHERE `" + TInfo.Column.UID + "` = " + uid + "";
		return Database.executeQuery(DB.CLIENTS, sql);
	}
	
	static QueryResult queryForClientInfoByEmail(String email) {
		sql = "SELECT " + TInfo.Column.UID + ","
						+ TInfo.Column.LOCALE + "," 
						+ TInfo.Column.FIRSTNAME + "," 
						+ TInfo.Column.LASTNAME + ","
						+ TInfo.Column.CELLPHONE + ","
						+ TInfo.Column.EMAIL 
			+ " FROM `" + Tables.CINFO + "`"
			+ " WHERE `" + TInfo.Column.EMAIL + "` = \"" + email + "\"";
		return Database.executeQuery(DB.CLIENTS, sql);
	}
	
	
	static QueryResult queryForClientInfoByCellphone(String cellphone) {
		sql = "SELECT " + TInfo.Column.UID + ","
						+ TInfo.Column.LOCALE + "," 
						+ TInfo.Column.FIRSTNAME + "," 
						+ TInfo.Column.LASTNAME + ","
						+ TInfo.Column.CELLPHONE + ","
						+ TInfo.Column.EMAIL 
			+ " FROM `" + Tables.CINFO + "`"
			+ " WHERE `" + TInfo.Column.CELLPHONE + "` = \"" + cellphone + "\"";
		return Database.executeQuery(DB.CLIENTS, sql);
	}
	
	
	static QueryResult insertNewClientLogin (String username, String passwd, Enum<UGroup> group) {
		String dml = "INSERT INTO `" + Tables.CLOGIN + "` "
				+ "(`" + TLogin.Column.USERNAME + "`, `" + TLogin.Column.PASSWD + "`, `" + TLogin.Column.GROUP + "`) "
				+ "VALUES "
				+ "('" + username + "', AES_ENCRYPT('" + passwd + "','" + Database.KEY_STR + "'), '" + group + "');";
		sql = "SELECT * FROM `" + Tables.CLOGIN + "` WHERE `" + TLogin.Column.UID + "` = LAST_INSERT_ID();";
		return Database.executeUpdateAndGetPrimaryID(DB.CLIENTS, dml, sql);
	}
	
	
	static QueryResult insertNewClientInfo (long uid, String firstname, String lastname, 
			String email, String cellphone, String locale) {
		String dml = "INSERT INTO `" + Tables.CINFO + "` "
				+ "(`" + TInfo.Column.UID + "`, `" + TInfo.Column.FIRSTNAME + "`, `" + TInfo.Column.LASTNAME + "`,"
				+ " `" + TInfo.Column.EMAIL + "`, `" + TInfo.Column.CELLPHONE + "`, `" + TInfo.Column.LOCALE + "`) "
				+ "VALUES "
				+ "('" + uid + "', '" + firstname + "', '" + lastname + "',"
				+ " '" + email + "', '" + cellphone + "', '" + locale + "');";
		sql = "SELECT * FROM `" + Tables.CINFO + "` WHERE `" + TInfo.Column.UID + "` = LAST_INSERT_ID();";
		return Database.executeUpdateAndGetPrimaryID(DB.CLIENTS, dml, sql);
	}
	
	/**
	 * 
	 * @param uid
	 * @param oldPasswd
	 * @param newPasswd
	 * @return -1, if failed; the row count, if succeeded.
	 */
	static int updateUserPasswd (long uid, String oldPasswd, String newPasswd) {
		sql = "UPDATE `" + Tables.CLOGIN + "` "
			+ "SET `" + TLogin.Column.PASSWD + "` = AES_ENCRYPT('" + newPasswd + "','" + Database.KEY_STR + "') "
			+ "WHERE `" + TLogin.Column.UID + "` = " + uid + " "
			+ "AND `" + TLogin.Column.PASSWD + "` = AES_ENCRYPT('" + oldPasswd + "','" + Database.KEY_STR + "')";
		return Database.executeUpdate(DB.CLIENTS, sql);
	}
	
	/**
	 * update user last login time, IP address and active/offline status
	 * @param uid
	 * @param ip
	 * @param status
	 * @return -1, if failed; the row count, if succeeded.
	 */
	static int updateUserStatus (long uid, String ip, Enum<UStatus> status) {
		sql = "UPDATE `" + Tables.CLOGIN + "` "
			+ "SET `" + TLogin.Column.LASTACT + "` = NOW(), "
				+ "`" + TLogin.Column.LASTIP + "` = '" + ip + "', "
				+ "`" + TLogin.Column.STATUS + "` = '" + status + "' "
			+ "WHERE `" + TLogin.Column.UID + "` = " + uid + "";
		return Database.executeUpdate(DB.CLIENTS, sql);
	}
	
	/**
	 * only update user status
	 * @param uid
	 * @param status
	 * @return -1, if failed; the row count, if succeeded.
	 */
	static int updateOnlyUserStatus (long uid, Enum<UStatus> status) {
		sql = "UPDATE `" + Tables.CLOGIN + "` "
				+ "SET `" + TLogin.Column.STATUS + "` = '" + status + "' "
				+ "WHERE `" + TLogin.Column.UID + "` = " + uid + "";
		return Database.executeUpdate(DB.CLIENTS, sql); 
	}
	
	
	
	
}
