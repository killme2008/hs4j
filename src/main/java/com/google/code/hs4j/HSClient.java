package com.google.code.hs4j;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.concurrent.TimeoutException;

import com.google.code.hs4j.exception.HandlerSocketException;

/**
 * A HandlerSocket client
 * 
 * @author dennis
 * @date 2010-11-27
 */
public interface HSClient {

	public void setOpTimeout(long opTimeout);

	public boolean isStarted();

	public boolean openIndex(String id, String db, String table, String index,
			String[] fieldList) throws InterruptedException, TimeoutException,
			HandlerSocketException;

	public ResultSet find(String id, String[] values, FindOperator operator,
			int limit, int offset) throws InterruptedException,
			TimeoutException, HandlerSocketException;

	public ResultSet find(String id, String[] values)
			throws InterruptedException, TimeoutException,
			HandlerSocketException;

	public boolean insert(String id, String[] values)
			throws InterruptedException, TimeoutException,
			HandlerSocketException;

	public void shutdown() throws IOException;

	/**
	 * Default thread number for reading nio's receive buffer and dispatch
	 * commands.Recommend users to set it equal or less to the memcached
	 * server's number on linux platform,keep default on windows.Default is 0.
	 */
	public static final int DEFAULT_READ_THREAD_COUNT = 0;

	/**
	 * Default TCP keeplive option,which is true
	 */
	public static final boolean DEFAULT_TCP_KEEPLIVE = true;
	/**
	 * Default connect timeout,1 minutes
	 */
	public static final int DEFAULT_CONNECT_TIMEOUT = 60000;
	/**
	 * Default socket's send buffer size,8k
	 */
	public static final int DEFAULT_TCP_SEND_BUFF_SIZE = 32 * 1024;
	/**
	 * Disable Nagle algorithm by default
	 */
	public static final boolean DEFAULT_TCP_NO_DELAY = true;
	/**
	 * Default session read buffer size,16k
	 */
	public static final int DEFAULT_SESSION_READ_BUFF_SIZE = 128 * 1024;
	/**
	 * Default socket's receive buffer size,16k
	 */
	public static final int DEFAULT_TCP_RECV_BUFF_SIZE = 64 * 1024;
	/**
	 * Default operation timeout,if the operation is not returned in 1
	 * second,throw TimeoutException
	 */
	public static final long DEFAULT_OP_TIMEOUT = 1000L;
	/**
	 * With java nio,there is only one connection to a memcached.In a high
	 * concurrent enviroment,you may want to pool memcached clients.But a
	 * xmemcached client has to start a reactor thread and some thread pools,if
	 * you create too many clients,the cost is very large. Xmemcached supports
	 * connection pool instreadof client pool.you can create more connections to
	 * one or more memcached servers,and these connections share the same
	 * reactor and thread pools,it will reduce the cost of system.Default pool
	 * size is 1.
	 */
	public static final int DEFAULT_CONNECTION_POOL_SIZE = 1;

	/**
	 * Default session idle timeout,if session is idle,xmemcached will do a
	 * heartbeat action to check if connection is alive.
	 */
	public static final int DEFAULT_SESSION_IDLE_TIMEOUT = 5000;
}
