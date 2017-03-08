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
		clients_login ("clients_login"),
		clients_info ("clients_info");
		
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

	// Default activateRemoteMode value (try with local host)
	private static boolean activateRemoteMode = false;
	
	
//	private static InitialContext ctx;
//	private static DataSource ds;
	
//	static Connection getConnection() throws SQLException, NamingException {
//		ctx = new InitialContext();
//		ds = (DataSource)ctx.lookup("java:comp/env/jdbc/MySQLDB");
//		return ds.getConnection(user, passwd);
//	}
	
	private static final BasicDataSource dataSource = new BasicDataSource();
	
	static {
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUsername(user);
		dataSource.setPassword(passwd);
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
		if (activateRemoteMode)
			dataSource.setUrl("jdbc:mysql://" + remotehost + ":3306/" + db + config);
		else
			dataSource.setUrl("jdbc:mysql://" + localhost + ":3306/" + db + config);
		
		try {
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			if (!activateRemoteMode) 
				System.err.println("[WARN] \"localhost\" unavailable! Try with remote host at " + remotehost + "...");
//			e.printStackTrace();
		} finally {
			if (activateRemoteMode && conn == null) {
				System.err.println("[ERROR] Unable to connect to " + dataSource.getUrl());
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
	static QueryResult runQuery(String sql) {
		return runQuery(DB.basic, sql);
	}
	
	/**
	 * Executes the given SQL statement, which returns a single {@link QueryResult} object.
	 * 
	 * @param db you need to specify the database
	 * @param sql an SQL statement to be sent to the database
	 * @return a {@link QueryResult} object that contains the data produced by the given query.
	 * Never null.
	 */
	static QueryResult runQuery(DB db, String sql) {
		
		QueryResult re = new QueryResult();
		re.setSql(sql);
		try (
			Connection conn = getConnection(db);
			Statement stmt = conn.createStatement();
		) {
			re = new QueryResult(stmt.executeQuery(sql));
		} catch (SQLException e) {
			System.err.println("[ERROR] SQL query error occurred during \"" + sql + "\"");
//			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("[ERROR] Failed to execute SQL query due to connection failure.");
//			e.printStackTrace();
		}
		return re;
	}

}
