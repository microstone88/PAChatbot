/**
 * 
 */
package com.pachatbot.myproject.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp2.BasicDataSource;

import com.pachatbot.myproject.server.Utils.QueryResultUtils;
import com.pachatbot.myproject.shared.Bean.QueryResult;

/**
 * @author micro
 *
 */
public abstract class Database {
	
	// Login informations
	private final static String user = "pa";
	private final static String passwd = "hWNeYRKv5qpFMYkN";
	
	// AES key string
	final static String KEY_STR = "mypachatbot";
	
	// Remote host information
	private final static String remotehost = "192.168.1.57";
	private final static String localhost = "127.0.0.1";
	
	// List of available databases
	public enum DB {
		
		ROOT (user),
		BASIC (user + "_basic"),
		CLIENTS (user + "_clients"),
		WORDNET (user + "_WordNet");
		
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
	public enum Tables {
		
		// From "pa_basic"
		STDANS ("std_answers"),
		
		// From "pa_clients"
		CINFO ("clients_info"),
		CLOGIN ("clients_login"),
		COUNTRY ("countries_info");
		
		private String table = "";
		Tables(String table){
			this.table = table;
		}
		@Override
		public String toString(){
			return table;
		}
	}
	
	
	public abstract static class TAnswers {
		
		enum Column {
			
			LOCALE ("locale"), STATUS ("status"), STDID ("std_id"), 
			STDQUEST ("std_question"), STDANS("std_answer");
			
			private String colname = "";
			Column(String colname){this.colname = colname;}
			@Override
			public String toString(){return colname;}
		}
		
	}
	
	public abstract static class TLogin {
		
		public enum Column {
			
			UNDEFINED ("undefined"),
			
			UID ("uid"), USERNAME ("usr_name"), PASSWD ("passwd"), GROUP ("usergroup"), 
			CREAT ("created_at"), LASTACT ("last_active"), LASTIP ("last_ip"), STATUS ("status");
			
			private String colname = "";
			Column(String colname){this.colname = colname;}
			@Override
			public String toString(){return colname;}
		}
		
	}
	
	
	public abstract static class TCountry {
		
		public enum Column {
			
			ID ("id"), ISO ("iso"), NAME ("name"), NICENAME ("nicename"),
			ISO3 ("iso3"), NUMCODE ("numcode"), PHONECODE ("phonecode");
			
			private String colname = "";
			Column(String colname){this.colname = colname;}
			@Override
			public String toString(){return colname;}
		}
		
	}
	
	
	// Specific configuration
	private static String config = "?useUnicode=true&characterEncoding=utf-8"
			+ "&autoReconnect=true&failOverReadOnly=false";

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
	private static final BasicDataSource wnDS = new BasicDataSource();
	
	static {
		basicDS.setDriverClassName("com.mysql.jdbc.Driver");
		basicDS.setUsername(user);
		basicDS.setPassword(passwd);
		basicDS.setUrl("jdbc:mysql://" + localhost + ":3306/" + DB.BASIC + config);
		
		clientsDS.setDriverClassName("com.mysql.jdbc.Driver");
		clientsDS.setUsername(user);
		clientsDS.setPassword(passwd);
		clientsDS.setUrl("jdbc:mysql://" + localhost + ":3306/" + DB.CLIENTS + config);
		
		wnDS.setDriverClassName("com.mysql.jdbc.Driver");
		wnDS.setUsername(user);
		wnDS.setPassword(passwd);
		wnDS.setUrl("jdbc:mysql://" + localhost + ":3306/" + DB.WORDNET + config);
		
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
	 * Attempts to establish a connection to the given database.
	 * Try with local host, first. If failed, then try with remote host.
	 * 
	 * @param db a specific database in {@link DB}
	 * @return a database connection, if succeeded. null, if interrupted.
	 */
	private static Connection getConnection(DB db) {
		
		Connection conn = null;
		
		if (activateRemoteMode) {
			if (!basicDS.getUrl().contains(remotehost))
				basicDS.setUrl("jdbc:mysql://" + remotehost + ":3306/" + DB.BASIC + config);
			if (!clientsDS.getUrl().contains(remotehost))
				clientsDS.setUrl("jdbc:mysql://" + remotehost + ":3306/" + DB.CLIENTS + config);
			if (!wnDS.getUrl().contains(remotehost))
				wnDS.setUrl("jdbc:mysql://" + remotehost + ":3306/" + DB.WORDNET + config);
		} 
		
		try {
			
			if (db.equals(DB.BASIC)) conn = basicDS.getConnection();
			if (db.equals(DB.CLIENTS)) conn = clientsDS.getConnection();
			if (db.equals(DB.WORDNET)) conn = wnDS.getConnection();
			
		} catch (SQLException e) {
			
			if (!activateRemoteMode) 
				System.err.println("[WARN] \"localhost\" unavailable! Try with remote host at " + remotehost + "...");
//			e.printStackTrace();
			
		} finally {
			
			if (activateRemoteMode && conn == null) {
				if (db.equals(DB.BASIC))
					System.err.println("[ERROR] Unable to connect to " + basicDS.getUrl());
				if (db.equals(DB.CLIENTS))
					System.err.println("[ERROR] Unable to connect to " + clientsDS.getUrl());
				if (db.equals(DB.WORDNET))
					System.err.println("[ERROR] Unable to connect to " + wnDS.getUrl());
				throw new NullPointerException("connection interrupted!");
			}
			if (!activateRemoteMode && conn == null) {
				activateRemoteMode = true;
				conn = getConnection(db);
			}
			
		}
		
//		System.out.println("basic active = " + basicDS.getNumActive() + "  idle = " + basicDS.getNumIdle());
//		System.out.println("client active = " + clientsDS.getNumActive() + "  idle = " + clientsDS.getNumIdle());
		
		return conn;
		
	}
	
	/**
	 * Executes the given SQL statement, which returns a single {@link QueryResult} object
	 * (default database = {@link DB#BASIC}).
	 * 
	 * @param sql an SQL statement to be sent to the database
	 * @return a {@link QueryResult} object that contains the data produced by the given query.
	 */
	static QueryResult executeQuery(String sql) {
		return executeQuery(DB.BASIC, sql);
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
			re = QueryResultUtils.convertFrom(stmt.executeQuery(sql));
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
	 * Executes the given SQL statement
	 * 
	 * @param db specify the database
	 * @param sql an SQL statement
	 * @return -1 or the row count
	 */
	static int executeUpdate(DB db, String sql) {
		
		int re = -1;
		try (
			Connection conn = getConnection(db);
//			Connection conn = getJNDIConnection();
			Statement stmt = conn.createStatement();
		) {
			re = stmt.executeUpdate(sql);
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
	static QueryResult executeUpdateAndQuery(DB db, String dml, String sql) {
		
		QueryResult re = new QueryResult();
		re.setSql(sql);
		int count = -1;
		try (
			Connection conn = getConnection(db);
//			Connection conn = getJNDIConnection();
			Statement stmt = conn.createStatement();
		) {
			count = stmt.executeUpdate(dml);
			if (count == 1) re = QueryResultUtils.convertFrom(stmt.executeQuery(sql));
		} catch (SQLException e) {
			System.err.println("[ERROR] SQL query error occurred during \"" + dml + sql + "\"");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.err.println("[ERROR] Failed to execute SQL query due to connection failure.");
//			e.printStackTrace();
		}
		return re;
	}
	
	
		
		

}
