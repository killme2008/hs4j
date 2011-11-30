package com.google.code.hs4j.impl;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.ModifyStatement;
import com.google.code.hs4j.exception.HandlerSocketException;
import com.google.code.hs4j.utils.HSUtils;

/**
 * A handlersocket modify statement implementation
 * 
 * @author dennis
 * 
 */
public class HandlerSocketModifyStatement implements ModifyStatement {

	private byte[][] values;

	private HSClientImpl hsClientImpl;

	private String encoding;

	private int indexId;

	public HandlerSocketModifyStatement(int indexId, int fieldLength,
			HSClientImpl hsClientImpl) {
		this.indexId = indexId;
		this.hsClientImpl = hsClientImpl;
		this.values = new byte[fieldLength][0];
		this.encoding = this.hsClientImpl.getEncoding();
	}

	byte[][] getValues() {
		return this.values;
	}

	public boolean insert() throws HandlerSocketException,
			InterruptedException, TimeoutException {
		return this.hsClientImpl.insert0(indexId, values);
	}

	public int update(String[] keys, FindOperator operator,
			int limit, int offset) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		return this.hsClientImpl.update0(indexId, keys, operator, limit,
				offset, values);
	}

	public int update( String[] keys, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		return update(keys,operator,1,0);
	}

	public int incr(String[] keys, FindOperator operator) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		return this.hsClientImpl.incr(indexId, keys, operator, 1,
				0, values);
	}

	public int decr(String[] keys, FindOperator operator) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		return this.hsClientImpl.decr(indexId, keys, operator, 1,
				0, values);
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x) {
		setString(parameterIndex, String.valueOf(x));
	}

	public void setBlob(int parameterIndex, Blob x) {
		checkRange(parameterIndex);
		try {
			values[parameterIndex - 1] = x.getBytes(0, (int) x.length());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void setBoolean(int parameterIndex, boolean x) {
		setString(parameterIndex, x ? String.valueOf("1") : String.valueOf("0"));
	}

	public void setByte(int parameterIndex, byte x) {
		setString(parameterIndex, String.valueOf(x));

	}

	public void setBytes(int parameterIndex, byte[] x) {
		checkRange(parameterIndex);
		values[parameterIndex - 1] = x;

	}

	public void setClob(int parameterIndex, Clob x) {
		try {
			setString(parameterIndex, x.getSubString(0, (int) x.length()));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void setDouble(int parameterIndex, double x) {
		setString(parameterIndex, String.valueOf(x));

	}

	public void setFloat(int parameterIndex, float x) {
		setString(parameterIndex, String.valueOf(x));

	}

	public void setInt(int parameterIndex, int x) {
		setString(parameterIndex, String.valueOf(x));

	}

	public void setLong(int parameterIndex, long x) {
		setString(parameterIndex, String.valueOf(x));

	}

	public void setShort(int parameterIndex, short x) {
		setString(parameterIndex, String.valueOf(x));
	}

	public void setString(int parameterIndex, String x) {

		checkRange(parameterIndex);
		int index = parameterIndex - 1;
		values[index] = HSUtils.decodeString(x, encoding);
	}

	private void checkRange(int parameterIndex) {
		if (parameterIndex > this.values.length) {
			byte[][] newValues = new byte[this.values.length + 1][0];
			System.arraycopy(values, 0, newValues, 0, this.values.length);
			this.values = newValues;
		}
	}

}
