package com.google.code.hs4j.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.code.hs4j.Command;
import com.google.code.hs4j.CommandFactory;
import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.HSClient;
import com.google.code.hs4j.HSClientStateListener;
import com.google.code.hs4j.IndexSession;
import com.google.code.hs4j.command.text.TextCommandFactory;
import com.google.code.hs4j.exception.HandlerSocketException;
import com.google.code.hs4j.network.config.Configuration;
import com.google.code.hs4j.network.core.SocketOption;
import com.google.code.hs4j.network.core.impl.StandardSocketOption;
import com.google.code.hs4j.network.hs.HandlerSocketConnector;
import com.google.code.hs4j.network.hs.HandlerSocketHandler;
import com.google.code.hs4j.network.hs.codec.HandlerSocketCodecFactory;
import com.google.code.hs4j.utils.HSUtils;

public class HSClientImpl implements HSClient {
	private boolean started = false;

	private final CommandFactory commandFactory;

	private HandlerSocketConnector connector;

	private long opTimeout = DEFAULT_OP_TIMEOUT;

	private HandlerSocketHandler ioHandler;

	/**
	 * Index id counter
	 */
	private static AtomicInteger INDEX_COUNTER = new AtomicInteger();

	@SuppressWarnings("unchecked")
	private final Map<SocketOption, Object> socketOptions = getDefaultSocketOptions();

	private final ConcurrentHashMap<Integer/* index id */, IndexRecord/*
																		 * index
																		 * info
																		 */> indexMap = new ConcurrentHashMap<Integer, IndexRecord>();

	private final CopyOnWriteArrayList<HSClientStateListener> hsClientStateListeners = new CopyOnWriteArrayList<HSClientStateListener>();

	private final InetSocketAddress remoteAddr;

	public static final Configuration getDefaultConfiguration() {
		final Configuration configuration = new Configuration();
		configuration.setSessionReadBufferSize(DEFAULT_SESSION_READ_BUFF_SIZE);
		configuration.setReadThreadCount(DEFAULT_READ_THREAD_COUNT);
		configuration.setSessionIdleTimeout(DEFAULT_SESSION_IDLE_TIMEOUT);
		configuration.setWriteThreadCount(0);
		return configuration;
	}

	@SuppressWarnings("unchecked")
	public static final Map<SocketOption, Object> getDefaultSocketOptions() {
		Map<SocketOption, Object> map = new HashMap<SocketOption, Object>();
		map.put(StandardSocketOption.TCP_NODELAY, DEFAULT_TCP_NO_DELAY);
		map.put(StandardSocketOption.SO_RCVBUF, DEFAULT_TCP_RECV_BUFF_SIZE);
		map.put(StandardSocketOption.SO_KEEPALIVE, DEFAULT_TCP_KEEPLIVE);
		map.put(StandardSocketOption.SO_SNDBUF, DEFAULT_TCP_SEND_BUFF_SIZE);
		map.put(StandardSocketOption.SO_LINGER, 0);
		map.put(StandardSocketOption.SO_REUSEADDR, true);
		return map;
	}

	public IndexSession openIndexSession(int indexId, String dbname,
			String tableName, String indexName, String[] columns)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		this.checkParams(dbname, tableName, indexName, columns);
		this.openIndex(indexId, dbname, tableName, indexName, columns);
		return new IndexSessionImpl(this, indexId, columns);
	}

	private void checkParams(String dbname, String tableName, String indexName,
			String[] columns) {
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
	}

	public IndexSession openIndexSession(String dbname, String tableName,
			String indexName, String[] columns) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		return this.openIndexSession(INDEX_COUNTER.incrementAndGet(), dbname,
				tableName, indexName, columns);
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
		this(new InetSocketAddress(hostname, port));
	}

	/**
	 * New a HSFClient instance with a InetSocketAddress
	 * 
	 * @param inetSocketAddress
	 *            HandlerSocket address
	 * @throws IOException
	 */
	public HSClientImpl(InetSocketAddress inetSocketAddress) throws IOException {
		this(new TextCommandFactory(), inetSocketAddress);
	}

	/**
	 * New a HSFClient
	 * 
	 * @param commandFactory
	 *            The protocol commands factory
	 * @param remoteAddr
	 * @throws IOException
	 */
	public HSClientImpl(CommandFactory commandFactory,
			InetSocketAddress remoteAddr) throws IOException {
		super();
		if (commandFactory == null) {
			throw new NullPointerException("null commandFactory");
		}
		if (remoteAddr == null) {
			throw new NullPointerException("null remoteAddr");
		}
		this.commandFactory = commandFactory;
		this.remoteAddr = remoteAddr;
		this.initConnectorAndConnect(commandFactory, remoteAddr);
	}

	public InetSocketAddress getRemoteAddr() {
		return this.remoteAddr;
	}

	private void initConnectorAndConnect(CommandFactory commandFactory,
			InetSocketAddress remoteAddr) throws IOException {
		this.connector = new HandlerSocketConnector(getDefaultConfiguration(),
				commandFactory, 1);
		this.ioHandler = new HandlerSocketHandler(this);
		this.connector.setHandler(this.ioHandler);
		this.connector.setCodecFactory(new HandlerSocketCodecFactory());
		this.connector.setSessionTimeout(-1);
		this.connector.setSocketOptions(this.socketOptions);
		this.connector.start();
		try {
			if (!this.connector.connect(remoteAddr).get(
					DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)) {
				throw new IOException("Connect to " + remoteAddr + " fail");
			}
		} catch (Exception e) {
			throw new IOException("Connect to " + remoteAddr
					+ " fail,cause by:" + e.getMessage());
		}
		this.started = true;
	}

	public HandlerSocketConnector getConnector() {
		return this.connector;
	}

	public ResultSet find(int indexId, String[] values, FindOperator operator,
			int limit, int offset) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		IndexRecord indexRecord = this.getRecord(indexId);
		Command cmd = this.commandFactory.createFindCommand(String
				.valueOf(indexId), operator, values, limit, offset,
				indexRecord.fieldList);
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

	public ResultSet find(int indexId, String[] values)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		return this.find(indexId, values, FindOperator.EQ, 1, 0);
	}

	public boolean insert(int indexId, String[] values)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		Command cmd = this.commandFactory.createInsertCommand(String
				.valueOf(indexId), values);
		this.connector.send(cmd);
		this.awaitResponse(cmd);
		return cmd.getResponseStatus() == 0;

	}

	public int delete(int indexId, String[] values, FindOperator operator,
			int limit, int offset) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		IndexRecord indexRecord = this.getRecord(indexId);
		Command cmd = this.commandFactory.createDeleteCommand(String
				.valueOf(indexId), operator, values, limit, offset,
				indexRecord.fieldList);
		this.connector.send(cmd);
		this.awaitResponse(cmd);
		return (Integer) cmd.getResult();
	}

	public int delete(int indexId, String[] values, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		return this.delete(indexId, values, operator, 1, 0);
	}

	public int update(int indexId, String[] values, FindOperator operator,
			int limit, int offset) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		IndexRecord indexRecord = this.getRecord(indexId);
		Command cmd = this.commandFactory.createUpdateCommand(String
				.valueOf(indexId), operator, values, limit, offset,
				indexRecord.fieldList);
		this.connector.send(cmd);
		this.awaitResponse(cmd);
		return (Integer) cmd.getResult();
	}

	public int update(int indexId, String[] values, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		return this.update(indexId, values, operator, 1, 0);
	}

	public boolean isStarted() {
		return this.started;
	}

	public boolean openIndex(int indexId, String dbname, String tableName,
			String indexName, String[] columns) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		this.checkParams(dbname, tableName, indexName, columns);
		IndexRecord record = new IndexRecord(indexId, dbname, tableName,
				indexName, columns);
		this.indexMap.put(indexId, record);
		Command cmd = this.commandFactory.createOpenIndexCommand(String
				.valueOf(indexId), dbname, tableName, indexName, columns);
		this.connector.send(cmd);
		this.awaitResponse(cmd);
		return cmd.getResponseStatus() == 0;

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

	public synchronized void shutdown() throws IOException {
		if (!this.started) {
			return;
		}
		this.started = false;
		this.connector.stop();

	}

}
