/**
 * 
 */
package com.pachatbot.myproject.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.pachatbot.myproject.client.MessageService;
import com.pachatbot.myproject.server.Database.DB;
import com.pachatbot.myproject.server.Database.TABLES;
import com.pachatbot.myproject.shared.Bean.Message;
import com.pachatbot.myproject.shared.Bean.QueryResult;

/**
 * @author micro
 *
 */
public class MessageServiceImpl extends RemoteServiceServlet implements MessageService {

	private static final long serialVersionUID = 1L;

	public MessageServiceImpl() {
		// Nothing to do
	}

	/**
	 * @param delegate
	 */
	public MessageServiceImpl(Object delegate) {
		super(delegate);
	}

	/* (non-Javadoc)
	 * @see com.pachatbot.myproject.client.MessageService#getMessage(java.lang.String)
	 */
	@Override
	public Message getMessage(String input) {
		String messageString = "Hello " + input + "!";
		Message message = new Message();
		message.setMessage(messageString);
		return message;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.pachatbot.myproject.client.MessageService#connectToDB()
	 */
	@Override
	public String connectToDB() {
		QueryResult qr = SqlQueryUtils.selectAllFromTable(DB.basic, TABLES.basic_std_answers);
		if (qr.isEmpty()) return "[INFO] The returned QueryResult is empty.\n"
				+ "SQL = " + qr.getSql();
		if (qr.isNull()) return "[WARN] Multiple errors may occur! The returned QueryResult is null due to query failure."
				+ "Please verify the SQL statement and the database server.\n"
				+ "SQL = " + qr.getSql();
		else return qr.toString();
	}


	/* (non-Javadoc)
	 * @see com.pachatbot.myproject.client.MessageService#getResponse(java.lang.String)
	 */
	@Override
	public Message getResponse(String question) {
		// TODO Auto-generated method stub
		return null;
	}


}
