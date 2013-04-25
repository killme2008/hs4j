/**
 *Copyright [2010-2011] [dennis zhuang(killme2008@gmail.com)]
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *             http://www.apache.org/licenses/LICENSE-2.0
 *Unless required by applicable law or agreed to in writing,
 *software distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *either express or implied. See the License for the specific language governing permissions and limitations under the License
 */
package com.google.code.hs4j.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * A java.sql.ResultSet implementation,most methods are not supported,use
 * getString as much as possible.
 *
 * @author dennis
 * @date 2010-11-28
 */
public class ResultSetImpl implements ResultSet {
	private final List<List<byte[]>> rows;
	private final String[] fieldList;
	private int rowNo = this.BEFORE_FIRST;
	private final String encoding;

	private final int BEFORE_FIRST = Integer.MIN_VALUE;

	private final int AFTER_LAST = Integer.MAX_VALUE;

	private boolean lastWasNull = false;

	public ResultSetImpl(List<List<byte[]>> rows, String[] fieldList,
			String encoding) {
		super();
		this.rows = rows;
		this.fieldList = fieldList;
		this.encoding = encoding;
	}

	public boolean absolute(int row) throws SQLException {
		if (this.rows.isEmpty()) {
			return false;
		}
		if (row == 0) {
			throw new SQLException("row must not be zero");
		}
		if (row > this.rows.size()) {
			this.rowNo = this.AFTER_LAST;
			return false;
		} else if (row < 0 && -row > this.rows.size()) {
			this.rowNo = this.BEFORE_FIRST;
			return false;
		} else {
			if (row < 0) {
				int newPos = this.rows.size() + row + 1;
				return this.absolute(newPos);
			} else {
				this.rowNo = row - 1;
			}
			return true;
		}

	}

	public void afterLast() throws SQLException {
		if (this.rows.isEmpty()) {
			return;
		}
		this.rowNo = this.AFTER_LAST;
	}

	public void beforeFirst() throws SQLException {
		if (this.rows.isEmpty()) {
			return;
		}
		this.rowNo = this.BEFORE_FIRST;
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
		throw new UnsupportedOperationException();
	}

	public int findColumn(String columnName) throws SQLException {
		int index = -1;
		for (String name : this.fieldList) {
			index++;
			if (name.equalsIgnoreCase(columnName)) {
				return index + 1;
			}
		}
		if (index == -1) {
			throw new SQLException("columnName " + columnName
					+ " is not in result set");
		} else {
			return index + 1;
		}
	}

	public boolean first() throws SQLException {
		if (this.rows.isEmpty()) {
			return false;
		}
		this.rowNo = 0;
		return true;
	}

	public Array getArray(int i) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Array getArray(String colName) throws SQLException {
		return this.getArray(this.findColumn(colName));
	}

	private void checkRowCol(int columnIndex) throws SQLException {
		if (this.rowNo < 0 || this.rowNo + 1 > this.rows.size()) {
			throw new SQLException("invalid row:" + this.rowNo);
		}
		if (columnIndex <= 0 || columnIndex > this.fieldList.length) {
			throw new SQLException("invalid col:" + columnIndex);
		}
	}

	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		this.checkRowCol(columnIndex);
		byte[] data = this.getColumnData(columnIndex);
		if (data == null) {
			return null;
		}
		return new ByteArrayInputStream(data);
	}

	public InputStream getAsciiStream(String columnName) throws SQLException {
		return this.getAsciiStream(this.findColumn(columnName));
	}

	public BigDecimal getBigDecimal(int columnIndex, int scale)
			throws SQLException {
		this.checkRowCol(columnIndex);
		byte[] data = this.getColumnData(columnIndex);
		if (data == null) {
			return null;
		}
		String stringVal = this.getString(columnIndex);
		return this.getBigDecimalFromString(stringVal, columnIndex, scale);
	}

	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		this.checkRowCol(columnIndex);
		byte[] data = this.getColumnData(columnIndex);
		if (data == null) {
			return null;
		}
		String stringVal = this.getString(columnIndex);
		BigDecimal val;

		if (stringVal != null) {
			if (stringVal.length() == 0) {
				val = new BigDecimal("0");
				return val;
			}
			try {
				val = new BigDecimal(stringVal);
				return val;
			} catch (NumberFormatException ex) {
				throw new SQLException(
						"ResultSet.Bad_format_for_BigDecimal: value="
								+ stringVal);
			}
		}

		return null;

	}

	public BigDecimal getBigDecimal(String columnName, int scale)
			throws SQLException {
		return this.getBigDecimal(this.findColumn(columnName), scale);
	}

	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		return this.getBigDecimal(this.findColumn(columnName));
	}

	private final BigDecimal getBigDecimalFromString(String stringVal,
			int columnIndex, int scale) throws SQLException {
		BigDecimal bdVal;

		if (stringVal != null) {
			if (stringVal.length() == 0) {
				bdVal = new BigDecimal("0");
				try {
					return bdVal.setScale(scale);
				} catch (ArithmeticException ex) {
					try {
						return bdVal.setScale(scale, BigDecimal.ROUND_HALF_UP);
					} catch (ArithmeticException arEx) {
						throw new SQLException(
								"ResultSet.Bad_format_for_BigDecimal: value="
										+ stringVal + ",scale=" + scale);
					}
				}
			}
			try {
				try {
					return new BigDecimal(stringVal).setScale(scale);
				} catch (ArithmeticException ex) {
					try {
						return new BigDecimal(stringVal).setScale(scale,
								BigDecimal.ROUND_HALF_UP);
					} catch (ArithmeticException arEx) {
						throw new SQLException(
								"ResultSet.Bad_format_for_BigDecimal: value="
										+ stringVal + ",scale=" + scale);
					}
				}
			} catch (NumberFormatException ex) {
				throw new SQLException(
						"ResultSet.Bad_format_for_BigDecimal: value="
								+ stringVal + ",scale=" + scale);
			}
		}

		return null;
	}

	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return this.getAsciiStream(columnIndex);
	}

	public InputStream getBinaryStream(String columnName) throws SQLException {
		return this.getBinaryStream(this.findColumn(columnName));
	}

	public Blob getBlob(int i) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Blob getBlob(String colName) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean getBoolean(int columnIndex) throws SQLException {
		String stringVal = this.getString(columnIndex);
		return this.getBooleanFromString(stringVal);
	}

	public boolean getBoolean(String columnName) throws SQLException {
		return this.getBoolean(this.findColumn(columnName));
	}

	private final boolean getBooleanFromString(String stringVal)
			throws SQLException {
		if (stringVal != null && stringVal.length() > 0) {
			int c = Character.toLowerCase(stringVal.charAt(0));
			return c == 't' || c == 'y' || c == '1' || stringVal.equals("-1");
		}

		return false;
	}

	public byte getByte(int columnIndex) throws SQLException {
		String stringVal = this.getString(columnIndex);
		return this.getByteFromString(stringVal);
	}

	public byte getByte(String columnName) throws SQLException {
		return this.getByte(this.findColumn(columnName));
	}

	private final byte getByteFromString(String stringVal) throws SQLException {

		if (stringVal != null && stringVal.length() == 0) {
			return (byte) 0;
		}

		if (stringVal == null) {
			return 0;
		}

		stringVal = stringVal.trim();

		try {
			int decimalIndex = stringVal.indexOf(".");

			if (decimalIndex != -1) {
				double valueAsDouble = Double.parseDouble(stringVal);
				return (byte) valueAsDouble;
			}

			long valueAsLong = Long.parseLong(stringVal);

			return (byte) valueAsLong;
		} catch (NumberFormatException NFE) {
			throw new SQLException("Parse byte value error:" + stringVal);
		}
	}

	public byte[] getBytes(int columnIndex) throws SQLException {
		this.checkRowCol(columnIndex);
		return this.getColumnData(columnIndex);
	}

	public byte[] getBytes(String columnName) throws SQLException {
		return this.getBytes(this.findColumn(columnName));
	}

	public Reader getCharacterStream(int columnIndex) throws SQLException {
		String stringVal = this.getString(columnIndex);
		if (stringVal == null) {
			return null;
		}
		return new StringReader(stringVal);
	}

	public Reader getCharacterStream(String columnName) throws SQLException {
		return this.getCharacterStream(this.findColumn(columnName));
	}

	public Clob getClob(int i) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Clob getClob(String colName) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getConcurrency() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public String getCursorName() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		this.checkRowCol(columnIndex);
		String stringVal = this.getString(columnIndex);
		return this.getDateFromString(stringVal, cal);
	}

	public Date getDate(int columnIndex) throws SQLException {
		return this.getDate(columnIndex, null);
	}

	public Date getDate(String columnName, Calendar cal) throws SQLException {
		return this.getDate(this.findColumn(columnName), cal);
	}

	public Date getDate(String columnName) throws SQLException {
		return this.getDate(columnName, null);
	}

	private Date getDateFromString(String stringVal, Calendar cal)
			throws SQLException {
		if (stringVal == null) {
			this.lastWasNull = true;
			return null;
		}
		String val = stringVal.trim();
		if (val.length() == 0) {
			this.lastWasNull = true;
			return null;
		}
		if (val.equals("0") || val.equals("0000-00-00")
				|| val.equals("0000-00-00 00:00:00")
				|| val.equals("00000000000000") || val.equals("0")) {
			Calendar calendar = null;
			if (cal != null) {
				calendar = Calendar.getInstance(cal.getTimeZone());
			} else {
				calendar = Calendar.getInstance();
			}
			calendar.set(Calendar.YEAR, 1);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			return new Date(calendar.getTimeInMillis());
		}

		DateFormat dateFormat = DateFormat.getDateTimeInstance();
		if (cal != null) {
			TimeZone timeZone = cal.getTimeZone();
			dateFormat.setTimeZone(timeZone);
		}
		try {
			return new Date(dateFormat.parse(val).getTime());
		} catch (ParseException e) {
			throw new SQLException("Parse date failure:" + val);
		}
	}

	public double getDouble(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public double getDouble(String columnName) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getFetchDirection() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getFetchSize() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public float getFloat(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public float getFloat(String columnName) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getInt(int columnIndex) throws SQLException {
		return this.getIntFromString(this.getString(columnIndex));

	}

	private int getIntFromString(String stringVal) throws SQLException {
		if (stringVal == null || stringVal.trim().length() == 0) {
			this.lastWasNull = true;
			return 0;
		}
		try {
			int decimalIndex = stringVal.indexOf(".");

			if (decimalIndex != -1) {
				double valueAsDouble = Double.parseDouble(stringVal);
				return (int) valueAsDouble;
			}

			return Integer.parseInt(stringVal);
		} catch (NumberFormatException e) {
			throw new SQLException("Parse integer error:" + stringVal);
		}
	}

	public int getInt(String columnName) throws SQLException {
		return this.getInt(this.findColumn(columnName));
	}

	public long getLong(int columnIndex) throws SQLException {
		String stringVal = this.getString(columnIndex);
		return this.getLongFromString(stringVal);
	}

	public long getLong(String columnName) throws SQLException {
		return this.getLong(this.findColumn(columnName));
	}

	private long getLongFromString(String stringVal) throws SQLException {
		if (stringVal == null || stringVal.trim().length() == 0) {
			this.lastWasNull = true;
			return 0L;
		}
		try {
			int decimalIndex = stringVal.indexOf(".");

			if (decimalIndex != -1) {
				double valueAsDouble = Double.parseDouble(stringVal);
				return (long) valueAsDouble;
			}

			return Long.parseLong(stringVal);
		} catch (NumberFormatException e) {
			throw new SQLException("Parse integer error:" + stringVal);
		}
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Object getObject(int i, Map<String, Class<?>> map)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

  public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
    throw new UnsupportedOperationException();
  }

	public Object getObject(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Object getObject(String colName, Map<String, Class<?>> map)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

  public <T> T getObject(String colName, Class<T> type) throws SQLException {
    throw new UnsupportedOperationException();
  }

	public Object getObject(String columnName) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Ref getRef(int i) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Ref getRef(String colName) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public short getShort(int columnIndex) throws SQLException {
		return this.getShortFromString(this.getString(columnIndex));
	}

	public short getShort(String columnName) throws SQLException {
		return this.getShort(this.findColumn(columnName));
	}

	private short getShortFromString(String stringVal) throws SQLException {
		if (stringVal == null || stringVal.trim().length() == 0) {
			this.lastWasNull = true;
			return 0;
		}
		try {
			int decimalIndex = stringVal.indexOf(".");

			if (decimalIndex != -1) {
				double valueAsDouble = Double.parseDouble(stringVal);
				return (short) valueAsDouble;
			}

			return Short.parseShort(stringVal);
		} catch (NumberFormatException e) {
			throw new SQLException("Parse integer error:" + stringVal);
		}
	}

	public Statement getStatement() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public String getString(int columnIndex) throws SQLException {
		this.checkRowCol(columnIndex);
		byte[] data = this.getColumnData(columnIndex);
		if (data == null) {
			return null;
		}
		return this.encodeString(data);
	}

	private byte[] getColumnData(int columnIndex) {
		List<byte[]> row = this.rows.get(this.rowNo);
		if (row == null) {
			this.lastWasNull = true;
			return null;
		}
		byte[] data = row.get(columnIndex - 1);
		if (data == null) {
			this.lastWasNull = true;
			return null;
		}
		this.lastWasNull = false;
		return data;
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
		throw new UnsupportedOperationException();
	}

	public Time getTime(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Time getTime(String columnName, Calendar cal) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Time getTime(String columnName) throws SQLException {
		throw new UnsupportedOperationException();
	}

	private Timestamp getTimestampFromString(String stringVal, Calendar cal)
			throws SQLException {
		if (stringVal == null) {
			this.lastWasNull = true;
			return null;
		}
		String val = stringVal.trim();
		if (val.length() == 0) {
			this.lastWasNull = true;
			return null;
		}

		if (val.equals("0") || val.equals("0000-00-00")
				|| val.equals("0000-00-00 00:00:00")
				|| val.equals("00000000000000") || val.equals("0")) {
			Calendar calendar = null;
			if (cal != null) {
				calendar = Calendar.getInstance(cal.getTimeZone());
			} else {
				calendar = Calendar.getInstance();
			}
			calendar.set(Calendar.YEAR, 1);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return new Timestamp(calendar.getTimeInMillis());
		}

		DateFormat dateFormat = DateFormat.getDateTimeInstance();
		if (cal != null) {
			TimeZone timeZone = cal.getTimeZone();
			dateFormat.setTimeZone(timeZone);
		}
		try {
			return new Timestamp(dateFormat.parse(val).getTime());
		} catch (ParseException e) {
			throw new SQLException("Parse date failure:" + val);
		}
	}

	public Timestamp getTimestamp(int columnIndex, Calendar cal)
			throws SQLException {
		this.checkRowCol(columnIndex);
		String stringVal = this.getString(columnIndex);
		return this.getTimestampFromString(stringVal, cal);
	}

	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return this.getTimestamp(columnIndex, null);
	}

	public Timestamp getTimestamp(String columnName, Calendar cal)
			throws SQLException {
		return this.getTimestamp(this.findColumn(columnName), cal);
	}

	public Timestamp getTimestamp(String columnName) throws SQLException {
		return this.getTimestamp(columnName, null);
	}

	public int getType() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public InputStream getUnicodeStream(String columnName) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public URL getURL(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public URL getURL(String columnName) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public SQLWarning getWarnings() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void insertRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean isAfterLast() throws SQLException {
		return this.rowNo == this.AFTER_LAST;
	}

	public boolean isBeforeFirst() throws SQLException {
		return this.rowNo == this.BEFORE_FIRST;
	}

	public boolean isFirst() throws SQLException {
		if (this.rows.isEmpty()) {
			return false;
		}
		return this.rowNo == 0;
	}

	public boolean isLast() throws SQLException {
		if (this.rows.isEmpty()) {
			return false;
		}
		return this.rowNo + 1 == this.rows.size();
	}

	public boolean last() throws SQLException {
		if (this.rows.isEmpty()) {
			return false;
		}
		this.rowNo = this.rows.size() - 1;
		return true;
	}

	public void moveToCurrentRow() throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void moveToInsertRow() throws SQLException {
		throw new UnsupportedOperationException();

	}

	public boolean next() throws SQLException {
		if (this.rows.isEmpty()) {
			return false;
		}
		this.normalRowNO();
		if (this.rowNo + 1 >= this.rows.size()) {
			return false;
		}
		this.rowNo++;
		return true;
	}

	private void normalRowNO() {
		if (this.rowNo == this.BEFORE_FIRST) {
			this.rowNo = -1;
		} else if (this.rowNo == this.AFTER_LAST) {
			this.rowNo = this.rows.size();
		}
	}

	public boolean previous() throws SQLException {
		if (this.rows.isEmpty()) {
			return false;
		}
		this.normalRowNO();
		if (this.rowNo == 0) {
			this.rowNo = this.BEFORE_FIRST;
			return false;
		}
		this.rowNo--;
		return true;

	}

	public void refreshRow() throws SQLException {
		throw new UnsupportedOperationException();

	}

	public boolean relative(int rows) throws SQLException {
		this.normalRowNO();
		return this.absolute(this.rowNo + rows + 1);
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
		return this.lastWasNull;
	}

	public int getHoldability() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public NClob getNClob(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public NClob getNClob(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public String getNString(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public String getNString(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public RowId getRowId(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public RowId getRowId(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean isClosed() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void updateAsciiStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateAsciiStream(int columnIndex, InputStream x)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateAsciiStream(String columnLabel, InputStream x, long length)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateAsciiStream(String columnLabel, InputStream x)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBinaryStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBinaryStream(int columnIndex, InputStream x)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBinaryStream(String columnLabel, InputStream x,
			long length) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBinaryStream(String columnLabel, InputStream x)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBlob(int columnIndex, InputStream inputStream, long length)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBlob(int columnIndex, InputStream inputStream)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBlob(String columnLabel, InputStream inputStream,
			long length) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateBlob(String columnLabel, InputStream inputStream)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateClob(String columnLabel, Reader reader)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateNCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateNCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateNCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateNCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateNClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateNClob(String columnLabel, NClob nClob)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateNClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateNClob(String columnLabel, Reader reader)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateNString(int columnIndex, String nString)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateNString(String columnLabel, String nString)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateSQLXML(int columnIndex, SQLXML xmlObject)
			throws SQLException {
		throw new UnsupportedOperationException();

	}

	public void updateSQLXML(String columnLabel, SQLXML xmlObject)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

}
