/**
 * 
 */
package com.pachatbot.myproject.server;

import java.util.Locale;

import com.pachatbot.myproject.shared.PreDefined.TInfo;
import com.pachatbot.myproject.shared.PreDefined.UGroup;
import com.pachatbot.myproject.shared.PreDefined.ULocale;
import com.pachatbot.myproject.shared.PreDefined.UStatus;
import com.pachatbot.myproject.shared.Bean.QueryResult;

/**
 * @author micro
 *
 */
public abstract class SQLQueryUtils extends Database {
	
	/**
	 * Select all content from an existing table in a given database.
	 * Only for debugging.
	 * 
	 * @param database a database
	 * @param table a table in the given database
	 * @return
	 */
	public static QueryResult selectAllFromTable(DB database, Tables table) {
		String sql = "SELECT * FROM " + table;
		return Database.executeQuery(database, sql);
	}
	
	
	public static QueryResult queryForStdAnswer(Locale locale_lang, String std_question) {
		String sql = "SELECT `" + TAnswers.Column.STDANS + "` FROM `" + Tables.STDANS + "`"
				+ " WHERE `" + TAnswers.Column.STDQUEST + "` = '" + std_question.toLowerCase(locale_lang) + "'";
//						+ " AND `locale` = \"" + locale_lang + "\"";
		return Database.executeQuery(sql);
	}

	
	public static QueryResult queryForClientLoginByUsername(String username, String password) {
		String sql = "SELECT * FROM `" + Tables.CLOGIN + "`"
				+ " WHERE `" + TLogin.Column.USERNAME + "` = '" + username + "'"
				+ " AND `" + TLogin.Column.PASSWD + "` = AES_ENCRYPT('" + password + "','" + Database.KEY_STR + "')";
		return Database.executeQuery(DB.CLIENTS, sql);
	}
	
	
	public static QueryResult queryForClientLoginByUsername(String username) {
		String sql = "SELECT `" + TLogin.Column.UID + "` "
			+ "FROM `" + Tables.CLOGIN + "` "
			+ "WHERE `" + TLogin.Column.USERNAME + "` = '" + username + "'";
		return Database.executeQuery(DB.CLIENTS, sql);
	}
	
	
	public static QueryResult queryForClientLoginByUID(long uid, String password) {
		String sql = "SELECT * FROM `" + Tables.CLOGIN + "`"
				+ " WHERE `" + TLogin.Column.UID + "` = " + uid + ""
				+ " AND `" + TLogin.Column.PASSWD + "` = AES_ENCRYPT('" + password + "','" + Database.KEY_STR + "')";
		return Database.executeQuery(DB.CLIENTS, sql);
	}
	
	
	public static QueryResult queryForClientInfoByUID(long uid) {
		String sql = "SELECT " + TInfo.Column.UID + ","
						+ TInfo.Column.LOCALE + "," 
						+ TInfo.Column.CIVILITY + ","
						+ TInfo.Column.FIRSTNAME + "," 
						+ TInfo.Column.LASTNAME + ","
						+ TInfo.Column.CELLPHONE + "," 
						+ TInfo.Column.EMAIL 
			+ " FROM `" + Tables.CINFO + "`"
			+ " WHERE `" + TInfo.Column.UID + "` = " + uid + "";
		return Database.executeQuery(DB.CLIENTS, sql);
	}
	
	public static QueryResult queryForClientInfoByEmail(String email) {
		String sql = "SELECT " + TInfo.Column.UID + ","
						+ TInfo.Column.LOCALE + "," 
						+ TInfo.Column.CIVILITY + ","
						+ TInfo.Column.FIRSTNAME + "," 
						+ TInfo.Column.LASTNAME + ","
						+ TInfo.Column.CELLPHONE + ","
						+ TInfo.Column.EMAIL 
			+ " FROM `" + Tables.CINFO + "`"
			+ " WHERE `" + TInfo.Column.EMAIL + "` = \"" + email + "\"";
		return Database.executeQuery(DB.CLIENTS, sql);
	}
	
	
	public static QueryResult queryForClientInfoByCellphone(String cellphone) {
		String sql = "SELECT " + TInfo.Column.UID + ","
						+ TInfo.Column.LOCALE + "," 
						+ TInfo.Column.CIVILITY + ","
						+ TInfo.Column.FIRSTNAME + "," 
						+ TInfo.Column.LASTNAME + ","
						+ TInfo.Column.CELLPHONE + ","
						+ TInfo.Column.EMAIL 
			+ " FROM `" + Tables.CINFO + "`"
			+ " WHERE `" + TInfo.Column.CELLPHONE + "` = \"" + cellphone + "\"";
		return Database.executeQuery(DB.CLIENTS, sql);
	}
	
	
	public static QueryResult queryForClientPaymentInfoByUID(long uid) {
		String sql = "SELECT " + TInfo.Column.UID + ","
						+ TInfo.Column.PayPal + "," 
						+ TInfo.Column.Alipay + "," 
						+ TInfo.Column.WeChat 
			+ " FROM `" + Tables.CINFO + "`"
			+ " WHERE `" + TInfo.Column.UID + "` = " + uid + "";
		return Database.executeQuery(DB.CLIENTS, sql);
	}
	
	
	public static QueryResult insertNewClientLogin(String username, String passwd, UGroup group, String ip, UStatus status) {
		String dml = "INSERT INTO `" + Tables.CLOGIN + "` "
				+ "(`" + TLogin.Column.USERNAME + "`, `" + TLogin.Column.PASSWD + "`, `" + TLogin.Column.GROUP + "`,"
				+ " `" + TLogin.Column.LASTACT + "`, `" + TLogin.Column.LASTIP + "`, `" + TLogin.Column.STATUS + "`) "
				+ "VALUES "
				+ "('" + username + "', AES_ENCRYPT('" + passwd + "','" + Database.KEY_STR + "'), '" + group + "',"
				+ " NOW(), '" + ip + "', '" + status.name() + "');";
		String sql = "SELECT * FROM `" + Tables.CLOGIN + "` "
			+ "WHERE `" + TLogin.Column.UID + "` = LAST_INSERT_ID();";
		return Database.executeUpdateAndQuery(DB.CLIENTS, dml, sql);
	}
	
	/**
	 * 
	 * @param uid
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @param cellphone
	 * @param locale a String represent the locale name. e.g., "fr_FR".
	 * @return
	 */
	public static QueryResult insertNewClientInfo(long uid, String firstname, String lastname, 
			String email, String cellphone, ULocale locale) {
		
		if (!email.equals("NULL")) email = "'" + email + "'";
		if (!cellphone.equals("NULL")) cellphone = "'" + cellphone + "'";
		
		String dml = "INSERT INTO `" + Tables.CINFO + "` "
				+ "(`" + TInfo.Column.UID + "`, `" + TInfo.Column.FIRSTNAME + "`, `" + TInfo.Column.LASTNAME + "`,"
				+ " `" + TInfo.Column.EMAIL + "`, `" + TInfo.Column.CELLPHONE + "`, `" + TInfo.Column.LOCALE + "`) "
				+ "VALUES "
				+ "('" + uid + "', '" + firstname + "', '" + lastname + "',"
				+ " " + email + ", " + cellphone + ", '" + locale.name() + "');";
		String sql = "SELECT * FROM `" + Tables.CINFO + "` "
			+ "WHERE `" + TInfo.Column.UID + "` = LAST_INSERT_ID();";
		return Database.executeUpdateAndQuery(DB.CLIENTS, dml, sql);
	}
	
	/**
	 * 
	 * @param uid
	 * @param oldPasswd
	 * @param newPasswd
	 * @return -1, if failed; the row count, if succeeded.
	 */
	public static int updateUserPasswd(long uid, String oldPasswd, String newPasswd) {
		String sql = "UPDATE `" + Tables.CLOGIN + "` "
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
	public static int updateUserStatus(long uid, String ip, UStatus status) {
		String sql = "UPDATE `" + Tables.CLOGIN + "` "
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
	public static int updateOnlyUserStatus(long uid, UStatus status) {
		String sql = "UPDATE `" + Tables.CLOGIN + "` "
			+ "SET `" + TLogin.Column.STATUS + "` = '" + status + "' "
			+ "WHERE `" + TLogin.Column.UID + "` = " + uid + "";
		return Database.executeUpdate(DB.CLIENTS, sql); 
	}
	
	/**
	 * 
	 * @param uid
	 * @param column
	 * @param value
	 * @return the value of the updated column
	 */
	public static QueryResult updateClientInfo(long uid, TInfo.Column column, String value) {
		if (!value.equals("NULL")) value = "'" + value + "'";
		String dml = "UPDATE `" + Tables.CINFO + "` "
				   + "SET `" + column + "` = " + value + " "
				   + "WHERE `" + TInfo.Column.UID + "` = " + uid + ";";
		String sql = "SELECT `" + column + "` "
				   + "FROM `" + Tables.CINFO + "` "
				   + "WHERE `" + TInfo.Column.UID + "` = " + uid + ";";
		return Database.executeUpdateAndQuery(DB.CLIENTS, dml, sql);
	}
	
	
	
}
