package com.google.code.hs4j.impl;

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ResultSetImpl implements ResultSet {
	private final List<List<byte[]>> rows;
	private final String[] fieldList;
	private int rowNo = -1;
	private final String encoding;

	public ResultSetImpl(List<List<byte[]>> rows, String[] fieldList,
			String encoding) {
		super();
		this.rows = rows;
		this.fieldList = fieldList;
		this.encoding = encoding;
	}

	public boolean absolute(int row) throws SQLException {
		this.checkEmpty();
		if (row > this.rows.size()) {
			return false;
		} else {
			this.rowNo = row - 1;
			return true;
		}

	}

	public void afterLast() throws SQLException {
		this.checkEmpty();
		this.rowNo = this.rows.size();

	}

	public void beforeFirst() throws SQLException {
		this.checkEmpty();
		this.rowNo = -1;
	}

	public void cancelRowUpdates() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void clearWarnings() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void close() throws SQLException {
		// do nothing
	}

	public void deleteRow() throws SQLException {
		this.rows.remove(this.rowNo);
	}

	public int findColumn(String columnName) throws SQLException {
		int index = -1;
		for (String name : this.fieldList) {
			index++;
			if (name.equalsIgnoreCase(columnName)) {
				return index;
			}
		}
		return index;
	}

	public boolean first() throws SQLException {
		this.checkEmpty();
		this.rowNo = 0;
		return true;
	}

	private void checkEmpty() throws SQLException {
		if (this.rows.isEmpty()) {
			throw new SQLException("Empty row set");
		}
	}

	public Array getArray(int i) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Array getArray(String colName) throws SQLException {
		return this.getArray(this.findColumn(colName));
	}

	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getAsciiStream(String columnName) throws SQLException {
		return this.getAsciiStream(this.findColumn(columnName));
	}

	public BigDecimal getBigDecimal(int columnIndex, int scale)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public BigDecimal getBigDecimal(String columnName, int scale)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getBinaryStream(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Blob getBlob(int i) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Blob getBlob(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getBoolean(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getBoolean(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public byte getByte(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte getByte(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte[] getBytes(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getBytes(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getCharacterStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getCharacterStream(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Clob getClob(int i) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Clob getClob(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getCursorName() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDate(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDate(String columnName, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDate(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public double getDouble(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getDouble(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getFloat(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getFloat(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getInt(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getInt(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getLong(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getLong(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getObject(int i, Map<String, Class<?>> map)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getObject(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getObject(String colName, Map<String, Class<?>> map)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getObject(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Ref getRef(int i) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Ref getRef(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getRow() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public short getShort(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public short getShort(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Statement getStatement() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString(int columnIndex) throws SQLException {
		if (columnIndex <= 0 && columnIndex > this.fieldList.length) {
			throw new SQLException("columnIndex of bounds");
		}
		List<byte[]> row = this.rows.get(this.rowNo);
		if (row == null) {
			return null;
		}
		byte[] data = row.get(columnIndex);
		if (data == null) {
			return null;
		}
		return this.encodeString(data);
	}

	private String encodeString(byte[] data) throws SQLException {
		try {
			return new String(data, this.encoding);
		} catch (UnsupportedEncodingException e) {
			throw new SQLException("Unsupported encoding:" + this.encoding);
		}
	}

	public String getString(String columnName) throws SQLException {
		return this.getString(this.findColumn(columnName));
	}

	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Time getTime(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Time getTime(String columnName, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Time getTime(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Timestamp getTimestamp(int columnIndex, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Timestamp getTimestamp(String columnName, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Timestamp getTimestamp(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getType() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getUnicodeStream(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public URL getURL(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public URL getURL(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void insertRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	public boolean isAfterLast() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isBeforeFirst() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFirst() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isLast() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean last() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void moveToCurrentRow() throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void moveToInsertRow() throws SQLException {
		throw new UnsupportedOperationException();

	}

	public boolean next() throws SQLException {
		if (this.rowNo + 1 >= this.rows.size()) {
			return false;
		}
		this.rowNo++;
		return true;
	}

	public boolean previous() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void refreshRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	public boolean relative(int rows) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean rowDeleted() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean rowInserted() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean rowUpdated() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void setFetchDirection(int direction) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void setFetchSize(int rows) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateArray(int columnIndex, Array x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void updateArray(String columnName, Array x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void updateAsciiStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateAsciiStream(String columnName, InputStream x, int length)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBigDecimal(int columnIndex, BigDecimal x)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBigDecimal(String columnName, BigDecimal x)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void updateBinaryStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBinaryStream(String columnName, InputStream x, int length)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBlob(String columnName, Blob x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBoolean(String columnName, boolean x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateByte(int columnIndex, byte x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateByte(String columnName, byte x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBytes(String columnName, byte[] x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateCharacterStream(int columnIndex, Reader x, int length)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateCharacterStream(String columnName, Reader reader,
			int length) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateClob(int columnIndex, Clob x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateClob(String columnName, Clob x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateDate(int columnIndex, Date x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateDate(String columnName, Date x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateDouble(int columnIndex, double x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateDouble(String columnName, double x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateFloat(int columnIndex, float x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void updateFloat(String columnName, float x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateInt(int columnIndex, int x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateInt(String columnName, int x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateLong(int columnIndex, long x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateLong(String columnName, long x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateNull(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateNull(String columnName) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateObject(int columnIndex, Object x, int scale)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateObject(int columnIndex, Object x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateObject(String columnName, Object x, int scale)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateObject(String columnName, Object x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateRef(int columnIndex, Ref x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void updateRef(String columnName, Ref x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateRow() throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateShort(int columnIndex, short x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateShort(String columnName, short x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateString(int columnIndex, String x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateString(String columnName, String x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateTime(int columnIndex, Time x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateTime(String columnName, Time x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateTimestamp(int columnIndex, Timestamp x)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateTimestamp(String columnName, Timestamp x)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public boolean wasNull() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
