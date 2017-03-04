/**
 * 
 */
package com.pachatbot.myproject.shared.Bean;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author micro
 *
 */
public class QueryResult extends LinkedHashSet<LinkedHashMap<String, Object>> implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<String> columnNames = new ArrayList<String>();
	
	private String sql = "";
	
	public QueryResult() {
		// Nothing to do
	}
	
	public QueryResult(String sql) {
		this.setSql(sql);
	}
	
	public QueryResult(QueryResult queryResult) {
		
		// throw an exception if the original query result is null
		if (queryResult == null)
			throw new NullPointerException("trying to copy a null QueryResult object");
		
		// copy SQL statement
		this.setSql(queryResult.getSql());
		
		// copy the column names
		int columnCount = queryResult.getColumnCount();
		for (int c = 1; c <= columnCount; c++) {
			this.columnNames.add(queryResult.getColumnName(c));
		}
		
		// copy rows
		Iterator<LinkedHashMap<String, Object>> iterator = queryResult.iterator();
		while (iterator.hasNext()) {
			LinkedHashMap<String, Object> old_row = iterator.next();
			LinkedHashMap<String, Object> new_row = new LinkedHashMap<>(columnCount);
			for (int c = 1; c <= columnCount; c++) {
				String key = this.getColumnName(c);
				new_row.put(key, old_row.get(key));
			}
			this.add(new_row);
		}
	}
	
	/**
	 * Convert a given ResultSet object into a QueryResult object.
	 * Note: Remember to set SQL statement that produced the ResultSet object.
	 * 
	 * @param resultSet a given ResultSet object
	 * @throws SQLException
	 */
	public QueryResult(ResultSet resultSet) throws SQLException {
		
		if (resultSet == null)
			throw new NullPointerException("trying to convert a null ResultSet object");
		
		// convert column names
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnCount = rsmd.getColumnCount();
		for (int c = 1; c <= columnCount; c++) {
			columnNames.add(rsmd.getColumnName(c));
		}

		// convert rows
		while (resultSet.next()) {
			LinkedHashMap<String, Object> row = new LinkedHashMap<>(columnCount);
			for (int c = 1; c <= columnCount; c++) {
				row.put(this.getColumnName(c), resultSet.getObject(c));
			}
			this.add(row);
		}		
	}
	
	/**
	 * this can be used to check if the query has been successfully executed.
	 * 
	 * @return
	 */
	public boolean isNull() {
		if (this.getColumnCount() < 1) return true;
		else return false;
	}
	
	/**
	 * this can be used to check if the query result is empty 
	 * (after the SQL statement has been successfully executed).
	 * 
	 */
	public boolean isEmpty() {
		if (
			this.size() < 1
			&& this.getColumnCount() > 0 // it contains column names
		) return true;
		else return false;
	}
	
	public boolean isUniqueValue() {
		if (
			this.getColumnCount() == 1	// only one column
			&& this.size() == 1			// only one row
		) return true;
		else return false;
	}
	
	public boolean isUniqueRow() {
		if (this.size() == 1) return true;
		else return false;
	}
	
	public boolean isUniqueColumn() {
		if (this.getColumnCount() == 1) return true;
		else return false;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractCollection#toString()
	 */
	@Override
	public String toString() {
		
		String str = "";
		int columnCount = this.getColumnCount();
		for (int c = 1; c <= columnCount; c++) {
			str += this.getColumnName(c) + "	";
		}
		str += "\n";
		
		Iterator<LinkedHashMap<String, Object>> iterator = this.iterator();
		while (iterator.hasNext()) {
			LinkedHashMap<String, Object> row = iterator.next();
			for (int c = 1; c <= columnCount; c++) {
				str += row.get(this.getColumnName(c)) + "	";
			}
			str += "\n";
		}
		return str;
	}
	
	
	/**
	 * Returns the number of columns in this QueryResult object.
	 * 
	 * @return the number of columns
	 */
	public int getColumnCount() {
		return columnNames.size();
	}
	
	
	/**
	 * Get the designated column's name.
	 * 
	 * @param column the first column is 1, the second is 2, ...
	 * @return column name
	 */
	public String getColumnName(int column) {
		return columnNames.get(column - 1);
	}

	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @param sql the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	
}
