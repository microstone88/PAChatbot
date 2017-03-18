/**
 * 
 */
package com.pachatbot.myproject.shared.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;


/**
 * @author micro
 *
 * This is
 *
 */
public class QueryResult extends LinkedHashSet<LinkedHashMap<String, Object>> implements Serializable {

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
	 * get the object at the first row and the first column 
	 * of the query result. This should usually be called 
	 * after checking if the query result is a unique value.
	 * 
	 * @return the object
	 */
	public Object getUniqueValue() {
		return this.getValue(1, 1);
	}
	
	/**
	 * get the object at the given row and column.
	 * @param row starts from "1"
	 * @param column starts from "1"
	 * @return the object
	 */
	public Object getValue(int row, int column) {
		if (column > getColumnCount())
			throw new IndexOutOfBoundsException("Column: " + column + ", Size: " + getColumnCount());
		return this.getValue(row, getColumnName(column));
	}
	
	/**
	 * get the object at the given row and the specified column.
	 * @param row starts from "1"
	 * @param column an existing column
	 * @return the object
	 */
	public Object getValue(int row, @SuppressWarnings("rawtypes") Enum column) {
		return this.getValue(row, column.toString());
	}
	
	/**
	 * get the object at the given row and the specified column.
	 * @param row starts from "1"
	 * @param colname an existing column name
	 * @return the object
	 */
	public Object getValue(int row, String colname) {
		if (row > getRowCount()) 
			throw new IndexOutOfBoundsException("Row: " + row + ", Size: " + getRowCount());
		if (!columnNames.contains(colname))
			throw new IllegalArgumentException("Column \"" + colname + "\" doesn't exist!");
		int r = 0; LinkedHashMap<String, Object> targetRow = new LinkedHashMap<>(getColumnCount());
		Iterator<LinkedHashMap<String, Object>> it = this.iterator();
		while (r++ < row) targetRow = it.next();
		return targetRow.get(colname);
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
	@Override
	public boolean isEmpty() {
		if (
			this.getRowCount() < 1
			&& this.getColumnCount() > 0 // it contains column names
		) return true;
		else return false;
	}
	
	public boolean isUniqueValue() {
		if (
			this.getColumnCount() == 1	// only one column
			&& this.getRowCount() == 1			// only one row
		) return true;
		else return false;
	}
	
	public boolean isUniqueRow() {
		if (this.getRowCount() == 1) return true;
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
	 * Returns the number of rows in this QueryResult object.
	 * @return the number of columns
	 */
	public int getRowCount() {
		return this.size();
	}
	
	/**
	 * Returns the number of columns in this QueryResult object.
	 * @return the number of columns
	 */
	public int getColumnCount() {
		return columnNames.size();
	}
	
	
	/**
	 * Get the designated column's name.
	 * @param column the first column is 1, the second is 2, ...
	 * @return column name
	 */
	public String getColumnName(int column) {
		return columnNames.get(column - 1);
	}

	/**
	 * @return the columnNames
	 */
	public ArrayList<String> getColumnNames() {
		return columnNames;
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
