/**
 * 
 */
package com.pachatbot.myproject.server.Utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import com.pachatbot.myproject.shared.Bean.QueryResult;

/**
 * @author micro
 *
 */
public abstract class QueryResultUtils {
	
	/**
	 * Convert a given ResultSet object into a QueryResult object.
	 * Note: Remember to set SQL statement that produced the ResultSet object.
	 * 
	 * @param resultSet a given ResultSet object
	 * @return a QueryResult object that preserves the data
	 * @throws SQLException
	 */
	public static QueryResult convertFrom(ResultSet resultSet) throws SQLException {
		
		QueryResult re = new QueryResult();
		
		if (resultSet == null)
			throw new NullPointerException("trying to convert a null ResultSet object");
		
		// convert column names
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnCount = rsmd.getColumnCount();
		for (int c = 1; c <= columnCount; c++) {
			re.getColumnNames().add(rsmd.getColumnName(c));
		}

		// convert rows
		while (resultSet.next()) {
			LinkedHashMap<String, Object> row = new LinkedHashMap<>(columnCount);
			for (int c = 1; c <= columnCount; c++) {
				row.put(re.getColumnName(c), resultSet.getObject(c));
			}
			re.add(row);
		}
			
		return re;
	}
	
	

}
