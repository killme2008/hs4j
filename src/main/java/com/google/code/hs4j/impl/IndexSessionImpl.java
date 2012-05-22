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

import java.sql.ResultSet;
import java.util.concurrent.TimeoutException;

import com.google.code.hs4j.Filter;
import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.HSClient;
import com.google.code.hs4j.IndexSession;
import com.google.code.hs4j.ModifyStatement;
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

	public int delete(String[] keys) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		return this.delete(keys, FindOperator.EQ);
	}

	public ResultSet find(String[] keys, FindOperator operator, int limit,
			int offset, Filter[] filters) throws InterruptedException, TimeoutException,
			HandlerSocketException {
		return this.client.find(this.indexId, keys, operator, limit, offset, filters);
	}

	public ResultSet find(String[] keys, FindOperator operator, int limit,
			int offset) throws InterruptedException, TimeoutException,
			HandlerSocketException {
		return this.client.find(this.indexId, keys, operator, limit, offset);
	}

	public ResultSet find(String[] keys) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		return this.find(keys, FindOperator.EQ, 1, 0);
	}

	public int getIndexId() {
		return this.indexId;
	}

	public boolean insert(String[] values) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		return this.client.insert(this.indexId, values);
	}

	public int update(String[] keys, String[] values, FindOperator operator,
			int limit, int offset) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		return this.client.update(this.indexId, keys, values, operator, limit,
				offset);
	}

	public int update(String[] keys, String[] values, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		return this.update(keys, values, operator, 1, 0);
	}

	public ModifyStatement createStatement()throws HandlerSocketException {
		return this.client.createStatement(this.indexId);
	}

}
