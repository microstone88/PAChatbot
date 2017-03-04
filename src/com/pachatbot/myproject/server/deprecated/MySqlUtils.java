/**
 * 
 */
package com.pachatbot.myproject.server.deprecated;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * @author micro
 *
 */
@Deprecated
final class MySqlUtils {

	private static boolean defaultRemoteValue = false;
	
	private static Connection conn = null;
	private static Statement statement = null;
	private static ResultSet resultSet = null;
	private static String sql = "";
	
	// Login informations
	private final static String user = "pa";
	private final static String passwd = "hWNeYRKv5qpFMYkN";
	
	// Remote host information
	private final static String remotehost = "192.168.1.57";
	private final static String localhost = "127.0.0.1";
	
	// List of available databases
	enum DB {
		root (user),
		basic (user + "_basic"),
		clients (user + "_clients");
		
		private String database = "";
		DB(String database){
			this.database = database;
		}
		public String toString(){
			return database;
		}
	}
	
	// List of available tables
	enum TABLES {
		
		// From "pa_basic"
		basic_std_answers ("std_answers"),
		
		// From "pa_clients"
		clients_base ("clients_base"),
		clients_login ("clients_login");
		
		private String table = "";
		TABLES(String table){
			this.table = table;
		}
		public String toString(){
			return table;
		}
	}
	
	// Specific configuration
	private static String config = "?useUnicode=true&characterEncoding=utf-8";
	
	/**
	 * Attempts to establish a connection to the "default" database.
	 * Try with local host, first. If failed, then try with remote host
	 * (default = "{@link DB#basic}").
	 * 
	 * @return url, if succeeded.
	 */
	static String connect() {
		return connect(DB.basic);
	}
	
	/**
	 * Attempts to establish a connection to the given database.
	 * Try with local host, first. If failed, then try with remote host.
	 * 
	 * @param database a specific database
	 * @return url, if succeeded.
	 */
	static String connect(DB database) {
		// Try with local host (defaultRemoteValue = false)
		String str = connect(defaultRemoteValue, database);
		if (str.startsWith("jdbc:mysql://")) return str;
		// Try with remote host
		else return connect(!defaultRemoteValue, database);
	}
	
	/**
	 * Attempts to establish a connection to the given database.
	 * 
	 * @param remote true, connect to remote host. 
	 * Otherwise, connect to local host.
	 * @return url, if succeeded.
	 */
	private static String connect(boolean remote, DB database) {
		
		String host = "";
		if (remote) {
			host = remotehost;
			System.err.print("[WARN] Trying with remote host at " + remotehost + "... ");
		}
		else host = localhost;
		String url = "jdbc:mysql://" + host + ":3306/" + database + config;
		
		try {
//			System.err.println("[Debug] Connecting to server... ");
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, passwd);
			if (conn != null) {
				System.err.println("succeeded!");
				return url;
			}
			else return "Connection failed!";
		} catch (ClassNotFoundException e) {
//			System.err.println("[Error] Driver class not found!");
//			e.printStackTrace();
			return e.toString();
		} catch (SQLException e) {
//			System.err.println("[Error] Connection interrupted!");
//			e.printStackTrace();
			return e.toString();
		} finally {
			if (conn == null) 
				System.err.println("[WARN] Failed to connect to "
						+ "jdbc:mysql://" + host + ":3306/" + database + " (as \"" + user +"\")");
		}
	}
	
	
	static void disconnect() {
		
		try {
			if (conn != null) conn.close();
			if (statement != null) statement.close();
			if (resultSet != null) resultSet.close();
		} catch (Exception e) {
			System.err.println("[WARN] Manually release connection failed!");
//			e.printStackTrace();
		}
	}
	
	
	static ResultSet runQuery(String sql) {
		
		MySqlUtils.sql = sql; // Keep the SQL query phrase
		try {
			statement = conn.createStatement();
			resultSet = statement.executeQuery(sql);
		} catch (SQLException e) {
			System.err.println("[WARN] Cannot get query result of \"" + sql + "\"");
			e.printStackTrace();
		}
		return resultSet;
	}
	
	@Deprecated
	static String testQuery() {
		
		String str = "";
		
		connect();
		ResultSet rs = runQuery("SELECT * FROM " + TABLES.basic_std_answers);
		
		try {
			
			ResultSetMetaData rsmd = rs.getMetaData();
			
			int columnCount = rsmd.getColumnCount();
			
			for (int c = 1; c <= columnCount; c++) {
				System.out.print(rsmd.getColumnName(c) + "	");
			}
			System.out.println("");
			
			while (rs.next()) {
				for (int c = 1; c <= columnCount; c++) {
					System.out.print(rs.getString(c) + "	");
				}
				System.out.println("");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		
		str = rs.toString();
		
		disconnect();
		return str;
	}
	
	
	
	
	
	/**
	 * @return the last used SQL query phrase
	 */
	String getLastSql() {
		return sql;
	}

	/**
	 * @return the statement after the last query
	 */
	static Statement getLastStatement() {
		return statement;
	}

	/**
	 * @return the resultSet after the last query
	 */
	static ResultSet getLastResultSet() {
		return resultSet;
	}

}
