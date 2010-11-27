package com.google.code.hs4j.impl;

import java.sql.ResultSet;
import java.util.concurrent.TimeoutException;

import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.HSClient;
import com.google.code.hs4j.IndexSession;
import com.google.code.hs4j.exception.HandlerSocketException;

/**
 * A index session implementation
 * 
 * @author dennis
 * @date 2010-11-27
 */
public class IndexSessionImpl implements IndexSession {
	private final HSClient client;
	private final int indexId;
	private final String[] columns;

	public String[] getColumns() {
		return this.columns;
	}

	public IndexSessionImpl(HSClient client, int indexId, String[] columns) {
		super();
		this.client = client;
		this.indexId = indexId;
		this.columns = columns;
	}

	public int delete(String[] values, FindOperator operator, int limit,
			int offset) throws InterruptedException, TimeoutException,
			HandlerSocketException {
		return this.client
				.delete(this.indexId, values, operator, limit, offset);
	}

	public int delete(String[] values, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		return this.delete(values, operator, 1, 0);
	}

	public ResultSet find(String[] values, FindOperator operator, int limit,
			int offset) throws InterruptedException, TimeoutException,
			HandlerSocketException {
		return this.client.find(this.indexId, values, operator, limit, offset);
	}

	public ResultSet find(String[] values) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		return this.find(values, FindOperator.EQ, 1, 0);
	}

	public int getIndexId() {
		return this.indexId;
	}

	public boolean insert(String[] values) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		return this.client.insert(this.indexId, values);
	}

	public int update(String[] values, FindOperator operator, int limit,
			int offset) throws InterruptedException, TimeoutException,
			HandlerSocketException {
		return this.client
				.update(this.indexId, values, operator, limit, offset);
	}

	public int update(String[] values, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		return this.update(values, operator, 1, 0);
	}

}
