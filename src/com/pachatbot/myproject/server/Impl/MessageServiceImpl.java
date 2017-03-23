/**
 * 
 */
package com.pachatbot.myproject.server.Impl;

import java.util.Locale;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.pachatbot.myproject.client.MessageService;
import com.pachatbot.myproject.server.Database.DB;
import com.pachatbot.myproject.server.Database.Tables;
import com.pachatbot.myproject.server.PiBrain;
import com.pachatbot.myproject.server.SQLQueryUtils;
import com.pachatbot.myproject.server.WNUtils;
import com.pachatbot.myproject.shared.Bean.Message;
import com.pachatbot.myproject.shared.Bean.QueryResult;

import edu.mit.jwi.item.POS;

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
	public Message getGreetingMessage(String input) {
		String messageString = "Hello " + input + "!";
		Message message = new Message(messageString);
		return message;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.pachatbot.myproject.client.MessageService#connectToDB()
	 */
	@Override
	public String connectToDB() {
		QueryResult qr = SQLQueryUtils.selectAllFromTable(DB.BASIC, Tables.STDANS);
		if (qr.isEmpty()) return "[INFO] The returned QueryResult is empty.\n"
				+ "SQL = " + qr.getSql();
		if (qr.isNull()) return "[WARN] The returned QueryResult is null due to query failure. "
				+ "Multiple errors may occur! "
				+ "Please verify both the MySQL server and the SQL statement.\n"
				+ "SQL = " + qr.getSql();
		else return qr.toString();
	}


	/* (non-Javadoc)
	 * @see com.pachatbot.myproject.client.MessageService#getResponse(java.lang.String)
	 */
	@Override
	public Message getResponse(String question) {
		
		String std_question = PiBrain.parseToStdQuestion(question);
		Locale locale_lang = PiBrain.detectLanguage(std_question);
		
		QueryResult qr = SQLQueryUtils.queryForStdAnswer(locale_lang, std_question);
		
		//TODO internalization of the following error message
		if (qr.isNull()) return new Message("Oops! I'm stuck now. Please try again.");
		
		if (qr.isEmpty()) {
//			return new Message("Sorry, I don't get it.");
			
			String gloss = WNUtils.getGlossof(question, POS.NOUN);
			Message msg = new Message(gloss);
			
			return msg;
		}
		
		if (qr.isUniqueValue()) {
			String response = (String) qr.getUniqueValue();
			Message msg = new Message(response);
			
			String gloss = WNUtils.getGlossof(response, POS.NOUN);
			if (gloss.contains("greeting")) msg.setGreeting(true);
			return msg;
		} 
		
		else {
			return new Message(qr.toString());
		}
			
	}


}
