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

import com.google.code.hs4j.Command;
import com.google.code.hs4j.CommandFactory;
import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.HSClient;
import com.google.code.hs4j.HSClientStateListener;
import com.google.code.hs4j.command.text.TextCommandFactory;
import com.google.code.hs4j.exception.HandlerSocketException;
import com.google.code.hs4j.network.config.Configuration;
import com.google.code.hs4j.network.core.SocketOption;
import com.google.code.hs4j.network.core.impl.StandardSocketOption;
import com.google.code.hs4j.network.hs.HandlerSocketConnector;
import com.google.code.hs4j.network.hs.HandlerSocketHandler;
import com.google.code.hs4j.network.hs.codec.HandlerSocketCodecFactory;

public class HSClientImpl implements HSClient {
	private boolean started = false;

	private final CommandFactory commandFactory;

	private HandlerSocketConnector connector;

	private long opTimeout = DEFAULT_OP_TIMEOUT;

	private HandlerSocketHandler ioHandler;

	private final Map<SocketOption, Object> socketOptions = getDefaultSocketOptions();

	private final ConcurrentHashMap<String/* index id */, IndexRecord/*
																	 * index
																	 * info
																	 */> indexMap = new ConcurrentHashMap<String, IndexRecord>();

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

	public CopyOnWriteArrayList<HSClientStateListener> getHSClientStateListeners() {
		return this.hsClientStateListeners;
	}

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

	public ResultSet find(String id, String[] values, FindOperator operator,
			int limit, int offset) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		IndexRecord indexRecord = this.indexMap.get(id);
		Command cmd = this.commandFactory.createFindCommand(id, operator,
				values, limit, offset, indexRecord.fieldList);
		this.connector.send(cmd);
		this.awaitResponse(cmd);
		return (ResultSet) cmd.getResult();
	}

	public ResultSet find(String id, String[] values)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		return this.find(id, values, FindOperator.EQ, 1, 0);
	}

	public boolean insert(String id, String[] values)
			throws InterruptedException, TimeoutException,
			HandlerSocketException {
		IndexRecord indexRecord = this.indexMap.get(id);
		Command cmd = this.commandFactory.createOpenIndexCommand(id,
				indexRecord.db, indexRecord.tableName, indexRecord.indexName,
				indexRecord.fieldList);
		this.connector.send(cmd);
		this.awaitResponse(cmd);
		return cmd.getResponseStatus() == 0;

	}

	public boolean isStarted() {
		return this.started;
	}

	public boolean openIndex(String id, String db, String tableName,
			String indexName, String[] fieldList) throws InterruptedException,
			TimeoutException, HandlerSocketException {
		IndexRecord record = new IndexRecord(id, db, tableName, indexName,
				fieldList);
		if (this.indexMap.put(id, record) != null) {
			throw new HandlerSocketException("Duplicate index id:" + id);
		}
		Command cmd = this.commandFactory.createOpenIndexCommand(id, db,
				tableName, indexName, fieldList);
		this.connector.send(cmd);
		this.awaitResponse(cmd);
		return cmd.getResponseStatus() == 0;

	}

	private void awaitResponse(Command cmd) throws InterruptedException,
			TimeoutException {
		if (!cmd.await(this.opTimeout, TimeUnit.MILLISECONDS)) {
			throw new TimeoutException("Operation timeout in " + this.opTimeout
					+ " ms.");
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

	public static void main(String[] args) throws Exception {
		//[48, 9, 61, 9, 49, 9, 107, 101, 118, 105, 110, 9, 49, 9, 48, 10]
		//[48, 9, 61, 9, 49, 9, 107, 101, 118, 105, 110, 9, 49, 9, 48, 10, 0, 0, 0, 0, 0]
		HSClient client = new HSClientImpl(new TextCommandFactory(),
				new InetSocketAddress(9998));
		final String[] fieldList = { "user_name", "user_email", "created" };
		System.out.println(client.openIndex("0", "mytest", "user", "INDEX_01",
				fieldList));
		String[] values = { "kevin","John" };
		ResultSet rs=client.find("0", values);
		System.out.println(rs);
		while(rs.next()){
			System.out.println(rs.getString("user_name"));
			System.out.println(rs.getString("user_email"));
			System.out.println(rs.getString("created"));
		}
	}

}
