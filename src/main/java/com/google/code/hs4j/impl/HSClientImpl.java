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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.code.hs4j.Command;
import com.google.code.hs4j.CommandFactory;
import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.Filter;
import com.google.code.hs4j.HSClient;
import com.google.code.hs4j.HSClientStateListener;
import com.google.code.hs4j.IndexSession;
import com.google.code.hs4j.ModifyStatement;
import com.google.code.hs4j.command.text.TextCommandFactory;
import com.google.code.hs4j.exception.HandlerSocketException;
import com.google.code.hs4j.network.core.Session;
import com.google.code.hs4j.network.core.SocketOption;
import com.google.code.hs4j.network.hs.HandlerSocketClientStateListenerAdapter;
import com.google.code.hs4j.network.hs.HandlerSocketConnector;
import com.google.code.hs4j.network.hs.HandlerSocketConnectorImpl;
import com.google.code.hs4j.network.hs.HandlerSocketHandler;
import com.google.code.hs4j.network.hs.HandlerSocketSession;
import com.google.code.hs4j.network.hs.codec.HandlerSocketCodecFactory;
import com.google.code.hs4j.utils.HSUtils;

/**
 * HSClient implementation
 * 
 * @author dennis
 * @date 2010-11-29
 */
public class HSClientImpl implements HSClient {
	private boolean started = false;

	private final CommandFactory commandFactory;

	private HandlerSocketConnectorImpl connector;

	private long opTimeout = DEFAULT_OP_TIMEOUT;

	private HandlerSocketHandler ioHandler;

	private String encoding = DEFAULT_ENCODING;

	/**
	 * Index id counter
	 */
	private static AtomicInteger INDEX_COUNTER = new AtomicInteger();

	@SuppressWarnings("unchecked")
	private Map<SocketOption, Object> socketOptions = HSClientBuilderImpl
			.getDefaultSocketOptions();

	private final ConcurrentHashMap<Integer/* index id */, IndexRecord/*
																		 * index
																		 * info
																		 */> indexMap = new ConcurrentHashMap<Integer, IndexRecord>();

	private final CopyOnWriteArrayList<HSClientStateListener> hsClientStateListeners = new CopyOnWriteArrayList<HSClientStateListener>();

	private final InetSocketAddress remoteAddr;

	public IndexSession openIndexSession(int indexId, String dbname,
			String tableName, String indexName, String[] columns)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
			return this.openIndexSession(indexId, dbname, tableName, indexName, columns, null);
	}

	public IndexSession openIndexSession(int indexId, String dbname,
			String tableName, String indexName, String[] columns, String[] fcolumns)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		this.checkParams(dbname, tableName, indexName, columns, fcolumns);
		if (this.openIndex(indexId, dbname, tableName, indexName, columns, fcolumns)) {
			return new IndexSessionImpl(this, indexId, columns);
		} else {
			return null;
		}
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		if (encoding == null || encoding.trim().length() == 0)
			throw new IllegalArgumentException("Invalid encoding:" + encoding);
		this.encoding = encoding;
		if (this.commandFactory != null) {
			this.commandFactory.setEncoding(encoding);
		}
	}

	public Map<Integer, IndexRecord> getIndexMap() {
		return Collections
				.<Integer, IndexRecord> unmodifiableMap(this.indexMap);
	}

	private void checkParams(String dbname, String tableName, String indexName,
			String[] columns, String[] fcolumns) {
		if (HSUtils.isBlank(dbname)) {
			throw new IllegalArgumentException("blank dbname:" + dbname);
		}
		if (HSUtils.isBlank(tableName)) {
			throw new IllegalArgumentException("blank tableName:" + tableName);
		}
		if (HSUtils.isBlank(indexName)) {
			throw new IllegalArgumentException("blank indexName:" + indexName);
		}
		if (columns == null || columns.length == 0) {
			throw new IllegalArgumentException("empty columns");
		}
		for (String col : columns) {
			if (HSUtils.isBlank(col)) {
				throw new IllegalArgumentException("blank column name:" + col);
			}
		}
		if (fcolumns != null && fcolumns.length != 0) {
			for (String col : fcolumns) {
				if (HSUtils.isBlank(col)) {
					throw new IllegalArgumentException("blank fcolumn name:" + col);
				}
			}
		}
	}

	public IndexSession openIndexSession(String dbname, String tableName,
			String indexName, String[] columns) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		return this.openIndexSession(INDEX_COUNTER.incrementAndGet(), dbname,
				tableName, indexName, columns);
	}

	public IndexSession openIndexSession(String dbname, String tableName,
			String indexName, String[] columns, String[] fcolumns) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		return this.openIndexSession(INDEX_COUNTER.incrementAndGet(), dbname,
				tableName, indexName, columns, fcolumns);
	}


	public CopyOnWriteArrayList<HSClientStateListener> getHSClientStateListeners() {
		return this.hsClientStateListeners;
	}

	/**
	 * New a HSFClient instance with host and port
	 * 
	 * @param hostname
	 *            HandlerSocket hostname
	 * @param port
	 *            HandlerSocket port
	 * @throws IOException
	 */
	public HSClientImpl(String hostname, int port) throws IOException {
		this(hostname, port, 1);
	}

	/**
	 * New a HSFClient instance with host and port
	 * 
	 * @param hostname
	 *            HandlerSocket hostname
	 * @param port
	 *            HandlerSocket port
	 * @throws IOException
	 */
	public HSClientImpl(String hostname, int port, int poolSize)
			throws IOException {
		this(new InetSocketAddress(hostname, port), poolSize);
	}

	/**
	 * New a HSFClient instance with a InetSocketAddress
	 * 
	 * @param inetSocketAddress
	 *            HandlerSocket address
	 * @throws IOException
	 */
	public HSClientImpl(InetSocketAddress inetSocketAddress) throws IOException {
		this(inetSocketAddress, DEFAULT_CONNECTION_POOL_SIZE);
	}

	/**
	 * New a HSFClient instance with a InetSocketAddress and poolSize
	 * 
	 * @param inetSocketAddress
	 *            HandlerSocket address
	 * @throws IOException
	 */
	public HSClientImpl(InetSocketAddress inetSocketAddress, int poolSize)
			throws IOException {
		this(new TextCommandFactory(), inetSocketAddress, null, null, poolSize);
	}

	/**
	 * New a HSFClient
	 * 
	 * @param commandFactory
	 *            The protocol commands factory
	 * @param remoteAddr
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public HSClientImpl(CommandFactory commandFactory,
			InetSocketAddress remoteAddr,
			List<HSClientStateListener> stateListeners,
			final Map<SocketOption, Object> socketOptions, int poolSize)
			throws IOException {
		super();
		if (commandFactory == null) {
			throw new NullPointerException("null commandFactory");
		}
		if (remoteAddr == null) {
			throw new NullPointerException("null remoteAddr");
		}
		if (stateListeners != null) {
			this.hsClientStateListeners.addAll(stateListeners);
		}
		if (poolSize <= 0) {
			throw new IllegalArgumentException(
					"poolSize must be greater than zero");
		}
		if (socketOptions != null) {
			this.socketOptions = socketOptions;
		}
		this.commandFactory = commandFactory;
		if (this.commandFactory != null) {
			this.commandFactory.setEncoding(this.encoding);
		}
		this.remoteAddr = remoteAddr;
		this.initConnectorAndConnect(commandFactory, remoteAddr, poolSize);
	}

	public InetSocketAddress getRemoteAddr() {
		return this.remoteAddr;
	}

	private void initConnectorAndConnect(CommandFactory commandFactory,
			InetSocketAddress remoteAddr, int poolSize) throws IOException {
		this.connector = new HandlerSocketConnectorImpl(HSClientBuilderImpl
				.getDefaultConfiguration(), commandFactory, poolSize, this);
		this.ioHandler = new HandlerSocketHandler(this);
		this.connector.setHandler(this.ioHandler);
		this.connector.setCodecFactory(new HandlerSocketCodecFactory());
		this.connector.setSessionTimeout(-1);
		this.connector.setSocketOptions(this.socketOptions);
		for (HSClientStateListener listener : this.hsClientStateListeners) {
			this.connector
					.addStateListener(new HandlerSocketClientStateListenerAdapter(
							listener, this));
		}
		this.connector.start();
		for (int i = 0; i < poolSize; i++) {
			try {
				if (!this.connector.connect(remoteAddr).get(
						DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)) {
					throw new IOException("Connect to " + remoteAddr + " fail");
				}
			} catch (Exception e) {
				throw new IOException("Connect to " + remoteAddr
						+ " fail,cause by:" + e.getMessage());
			}
		}
		// waiting pool ready
		while (this.connector.getSessionList().size() < poolSize) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		this.started = true;
	}

	public HandlerSocketConnector getConnector() {
		return this.connector;
	}

	public ResultSet find(int indexId, String[] keys, FindOperator operator,
			int limit, int offset) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		return this.find(indexId, keys, operator, limit, offset, null );
	}

	public ResultSet find(int indexId, String[] keys, FindOperator operator,
			int limit, int offset, Filter[] filters ) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		IndexRecord indexRecord = this.getRecord(indexId);
		Command cmd = this.commandFactory.createFindCommand(String
				.valueOf(indexId), operator, keys, limit, offset,
				indexRecord.fieldList, filters);
		this.connector.send(cmd);
		this.awaitResponse(cmd);
		return (ResultSet) cmd.getResult();
	}

	private IndexRecord getRecord(int indexId) throws HandlerSocketException {
		IndexRecord indexRecord = this.indexMap.get(indexId);
		if (indexRecord == null) {
			throw new HandlerSocketException("Please open index first,indexId="
					+ indexId);
		}
		return indexRecord;
	}

	public ResultSet find(int indexId, String[] keys)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		return this.find(indexId, keys, FindOperator.EQ, 1, 0);
	}

	public boolean insert(int indexId, String[] values)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		byte[][] bytes = HSUtils.getByteArrayFromStringArray(values,this.encoding);
		return insert0(indexId, bytes);
	}

	public ModifyStatement createStatement(int indexId)
			throws HandlerSocketException {
		IndexRecord indexRecord = getRecord(indexId);
		return new HandlerSocketModifyStatement(indexId,
				indexRecord.fieldList.length, this);
	}

	protected boolean insert0(int indexId, byte[][] bytes)
			throws HandlerSocketException, InterruptedException,
			TimeoutException {
		Command cmd = this.commandFactory.createInsertCommand(String
				.valueOf(indexId), bytes);
		this.connector.send(cmd);
		this.awaitResponse(cmd);
		return cmd.getResponseStatus() == 0;
	}

	public void notifyConnected(HandlerSocketSession session) {
		for (HSClientStateListener listener : this.hsClientStateListeners) {
			listener.onConnected(this, session.getRemoteSocketAddress());
		}
	}

	public int delete(int indexId, String[] keys, FindOperator operator,
			int limit, int offset) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		Command cmd = this.commandFactory.createDeleteCommand(String
				.valueOf(indexId), operator, keys, limit, offset);
		this.connector.send(cmd);
		this.awaitResponse(cmd);
		return (Integer) cmd.getResult();
	}

	public int delete(int indexId, String[] keys, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		return this.delete(indexId, keys, operator, 1, 0);
	}

	public int delete(int indexId, String[] keys) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		return this.delete(indexId, keys, FindOperator.EQ);
	}

	public int update(int indexId, String[] keys, String[] values,
			FindOperator operator, int limit, int offset)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		byte[][] bytes = HSUtils.getByteArrayFromStringArray(values,this.encoding);
		return update0(indexId, keys, operator, limit, offset, bytes);
	}

	protected int update0(int indexId, String[] keys, FindOperator operator,
			int limit, int offset, byte[][] bytes)
			throws HandlerSocketException, InterruptedException,
			TimeoutException {
		Command cmd = this.commandFactory.createUpdateCommand(String
				.valueOf(indexId), operator, keys, bytes, limit, offset);
		this.connector.send(cmd);
		this.awaitResponse(cmd);
		return (Integer) cmd.getResult();
	}

	public int update(int indexId, String[] keys, String[] values,
			FindOperator operator) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		return this.update(indexId, keys, values, operator, 1, 0);
	}

	public boolean isStarted() {
		return this.started;
	}

	public boolean openIndex(int indexId, String dbname, String tableName,
			String indexName, String[] columns, String[] fcolumns) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		this.checkParams(dbname, tableName, indexName, columns, fcolumns);
		IndexRecord record = new IndexRecord(indexId, dbname, tableName,
				indexName, columns, fcolumns);
		this.indexMap.put(indexId, record);

		List<Session> sessionList = this.connector.getSessionList();
		if (sessionList == null || sessionList.isEmpty()) {
			throw new HandlerSocketException("Empty connections");
		}

		boolean result = true;
		for (Session session : sessionList) {
			Command cmd = this.commandFactory.createOpenIndexCommand(String
					.valueOf(indexId), dbname, tableName, indexName, columns, fcolumns);
			session.write(cmd);
			this.awaitResponse(cmd);
			result = result && cmd.getResponseStatus() == 0;
		}
		return result;

	}

	public boolean openIndex(int indexId, String dbname, String tableName,
			String indexName, String[] columns) throws InterruptedException,
			TimeoutException, HandlerSocketException {
			return this.openIndex(indexId, dbname, tableName, indexName, columns, null);
	}

	private void awaitResponse(Command cmd) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		if (!cmd.await(this.opTimeout, TimeUnit.MILLISECONDS)) {
			throw new TimeoutException("Operation timeout in " + this.opTimeout
					+ " ms.");
		}
		if (cmd.getExceptionMessage() != null) {
			throw new HandlerSocketException(cmd.getExceptionMessage());
		}
	}

	public void setOpTimeout(long opTimeout) {
		if (opTimeout <= 0) {
			throw new IllegalArgumentException(
					"opTimeout must be greater than zero");
		}
		this.opTimeout = opTimeout;

	}

	public void setHealConnectionInterval(long interval) {
		this.connector.setHealSessionInterval(interval);
	}

	public long getHealConnectionInterval() {
		if (null != this.connector) {
			return this.connector.getHealSessionInterval();
		}
		return -1L;
	}

	public boolean isAllowAutoReconnect() {
		if (null != this.connector) {
			return this.connector.isAllowAutoReconnect();
		}
		return false;
	}

	public void setAllowAutoReconnect(boolean allowAutoReconnect) {
		if (null != this.connector) {
			this.connector.setAllowAutoReconnect(allowAutoReconnect);
		}
	}

	/**
	 * Set tcp socket option
	 * 
	 * @param socketOption
	 * @param value
	 */
	public <T> void setSocketOption(SocketOption<T> socketOption, T value) {
		this.socketOptions.put(socketOption, value);
	}

	public synchronized void shutdown() throws IOException {
		if (!this.started) {
			return;
		}
		this.started = false;
		this.connector.stop();

	}

}
