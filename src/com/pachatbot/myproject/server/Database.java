/**
 * 
 */
package com.pachatbot.myproject.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * @author micro
 *
 */
abstract class Database {
	
	// Login informations
	private final static String user = "pa";
	private final static String passwd = "hWNeYRKv5qpFMYkN";
	
	// AES key string
	final static String KEY_STR = "mypachatbot";
	
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
		@Override
		public String toString(){
			return database;
		}
	}
	
	// List of available tables
	enum TABLES {
		
		// From "pa_basic"
		std_answers ("std_answers"),
		
		// From "pa_clients"
		info ("clients_info"),
		login ("clients_login"),
		country ("lst_country");
		
		private String table = "";
		TABLES(String table){
			this.table = table;
		}
		@Override
		public String toString(){
			return table;
		}
	}
	
	enum COLNAME {
		
		// From table "std_answers"
//		locale ("locale"), status ("status"),
		std_id ("std_id"), std_Question ("std_question"), std_Answer("std_answer"),
		
		// From table "clients_info"
//		uid ("uid"),
		locale ("locale"), firstname ("first_name"), lastname ("last_name"),
		// Contact information
		cellphone ("cell"), email ("email"),
		// Payment information
		WeChat ("wechat"), PayPal ("paypal"), Alipay ("alipay"),
		
		// From table "clients_login"
		uid ("uid"), username ("usr_name"), password ("passwd"), group ("usergroup"), 
		createdat ("created_at"), lastactive ("last_active"), lastip ("last_ip"), status ("status");	

		
		private String colname = "";
		COLNAME(String colname){
			this.colname = colname;
		}
		@Override
		public String toString(){
			return colname;
		}
	}
	
	// Specific configuration
	private static String config = "?useUnicode=true&characterEncoding=utf-8";

	// Default activateRemoteMode value (try with local host)
	private static boolean activateRemoteMode = false;
	
	
//	private static InitialContext ctx;
//	private static DataSource ds;
	
//	static Connection getConnection() throws SQLException, NamingException {
//		ctx = new InitialContext();
//		ds = (DataSource)ctx.lookup("java:comp/env/jdbc/MySQLDB");
//		return ds.getConnection(user, passwd);
//	}
	
	/**
	 * Declare separate connection pool for different databases
	 */
	private static final BasicDataSource basicDS = new BasicDataSource();
	private static final BasicDataSource clientsDS = new BasicDataSource();
	
	static {
		basicDS.setDriverClassName("com.mysql.jdbc.Driver");
		basicDS.setUsername(user);
		basicDS.setPassword(passwd);
		
		clientsDS.setDriverClassName("com.mysql.jdbc.Driver");
		clientsDS.setUsername(user);
		clientsDS.setPassword(passwd);
		
//		try {
//			// Look up the JNDI data source only once at init time
//			Context envCtx = (Context) new InitialContext().lookup("java:comp/env");
//			dataSource = (BasicDataSource) envCtx.lookup("jdbc/mySource");
//		} catch (NamingException e) {
//			e.printStackTrace();
//		}
	}
	
	@SuppressWarnings("unused")
	private static Connection getJNDIConnection() throws SQLException {
		return basicDS.getConnection();
	}
	
	
	/**
	 * Attempts to establish a connection to the "default" database.
	 * Try with local host, first. If failed, then try with remote host
	 * (default = "{@link DB#basic}").
	 * 
	 * @return a database connection, if succeeded. null, if interrupted.
	 */
	@SuppressWarnings("unused")
	private static Connection getConnection() {
		return getConnection(DB.basic);
	}
	
	/**
	 * Attempts to establish a connection to the given database.
	 * Try with local host, first. If failed, then try with remote host.
	 * 
	 * @param db a specific database in {@link DB}
	 * @return a database connection, if succeeded. null, if interrupted.
	 */
	private static Connection getConnection(DB db) {
		
		Connection conn = null;
		if (activateRemoteMode) {
			basicDS.setUrl("jdbc:mysql://" + remotehost + ":3306/" + DB.basic + config);
			clientsDS.setUrl("jdbc:mysql://" + remotehost + ":3306/" + DB.clients + config);
		} else {
			basicDS.setUrl("jdbc:mysql://" + localhost + ":3306/" + DB.basic + config);
			clientsDS.setUrl("jdbc:mysql://" + localhost + ":3306/" + DB.clients + config);
		}
		
		try {
			if (db.equals(DB.basic)) conn = basicDS.getConnection();
			if (db.equals(DB.clients)) conn = clientsDS.getConnection();
		} catch (SQLException e) {
			if (!activateRemoteMode) 
				System.err.println("[WARN] \"localhost\" unavailable! Try with remote host at " + remotehost + "...");
//			e.printStackTrace();
		} finally {
			if (activateRemoteMode && conn == null) {
				if (db.equals(DB.basic))
					System.err.println("[ERROR] Unable to connect to " + basicDS.getUrl());
				if (db.equals(DB.clients))
					System.err.println("[ERROR] Unable to connect to " + clientsDS.getUrl());
				throw new NullPointerException("connection interrupted!");
			}
			if (!activateRemoteMode && conn == null) {
				activateRemoteMode = true;
				conn = getConnection(db);
			}
		}
		return conn;
	}
	
	/**
	 * Executes the given SQL statement, which returns a single {@link QueryResult} object
	 * (default database = {@link DB#basic}).
	 * 
	 * @param sql an SQL statement to be sent to the database
	 * @return a {@link QueryResult} object that contains the data produced by the given query.
	 */
	static QueryResult executeQuery(String sql) {
		return executeQuery(DB.basic, sql);
	}
	
	/**
	 * Executes the given SQL statement, which returns a single {@link QueryResult} object.
	 * 
	 * @param db you need to specify the database
	 * @param sql an SQL statement to be sent to the database
	 * @return a {@link QueryResult} object that contains the data produced by the given query.
	 * Never null.
	 */
	static QueryResult executeQuery(DB db, String sql) {
		
		QueryResult re = new QueryResult();
		re.setSql(sql);
		try (
			Connection conn = getConnection(db);
//			Connection conn = getJNDIConnection();
			Statement stmt = conn.createStatement();
		) {
			re = new QueryResult(stmt.executeQuery(sql));
		} catch (SQLException e) {
			System.err.println("[ERROR] SQL query error occurred during \"" + sql + "\"");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("[ERROR] Failed to execute SQL query due to connection failure.");
//			e.printStackTrace();
		}
		return re;
	}
	
	/**
	 * 
	 * @param db
	 * @param dml an SQL Data Manipulation Language (DML) statement, 
	 * such as INSERT or UPDATE (not DELETE)
	 * @param sql
	 * @return
	 */
	static QueryResult executeUpdateAndGetPrimaryID(DB db, String dml, String sql) {
		
		QueryResult re = new QueryResult();
		re.setSql(sql);
		int count = -1;
		try (
			Connection conn = getConnection(db);
//			Connection conn = getJNDIConnection();
			Statement stmt = conn.createStatement();
		) {
			count = stmt.executeUpdate(dml);
			if (count == 1) re = new QueryResult(stmt.executeQuery(sql));
		} catch (SQLException e) {
			System.err.println("[ERROR] SQL query error occurred during \"" + sql + "\"");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("[ERROR] Failed to execute SQL query due to connection failure.");
//			e.printStackTrace();
		}
		return re;
	}
	
	

}
