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

	/**
	 * Set operation timeout,if response does'nt return in this time,it will
	 * throw TimeoutException,ten seconds by default.
	 * 
	 * @param opTimeout
	 */
	public void setOpTimeout(long opTimeout);

	/**
	 * Whether the client is started;
	 * 
	 * @return
	 */
	public boolean isStarted();

	/**
	 * Just like openIndex method,but return a IndexSession
	 * 
	 * @param indexId
	 * @param dbname
	 * @param tableName
	 * @param indexName
	 * @param columns
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public IndexSession openIndexSession(int indexId, String dbname,
			String tableName, String indexName, String[] columns)
			throws InterruptedException, TimeoutException,
			HandlerSocketException;

	/**
	 * Open a IndexSession with a auto-generated indexId
	 * 
	 * @param dbname
	 * @param tableName
	 * @param indexName
	 * @param columns
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public IndexSession openIndexSession(String dbname, String tableName,
			String indexName, String[] columns) throws InterruptedException,
			TimeoutException, HandlerSocketException;

	/**
	 * Once an 'open_index' request is issued, the HandlerSocket plugin opens
	 * the specified index and keep it open until the client connection is
	 * closed. Each open index is identified by indexid. If indexid is already
	 * open, the old open index is closed. You can open the same combination of
	 * dbname tablename indexname multple times, possibly with different
	 * columns. For efficiency, keep indexid small as far as possible.
	 * 
	 * @param indexId
	 * @param dbname
	 * @param tableName
	 * @param indexName
	 * @param columns
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public boolean openIndex(int indexId, String dbname, String tableName,
			String indexName, String[] columns) throws InterruptedException,
			TimeoutException, HandlerSocketException;

	/**
	 * Getting data from mysql
	 * 
	 * @param indexId
	 *            This number must be an indexId specified by a 'open_index'
	 *            request executed previously on the same connection.
	 * 
	 * @param values
	 *            values to compare with index keys
	 * @param operator
	 *            specifies the comparison operation to use
	 * @param limit
	 *            limit fetch count
	 * @param offset
	 *            fetch offset
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public ResultSet find(int indexId, String[] values, FindOperator operator,
			int limit, int offset) throws InterruptedException,
			TimeoutException, HandlerSocketException;

	/**
	 * Get data from mysql,set limit to 1, offset to 0 and operator to '='
	 * 
	 * @see find
	 * @param indexId
	 * @param values
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public ResultSet find(int indexId, String[] values)
			throws InterruptedException, TimeoutException,
			HandlerSocketException;

	/**
	 * Update data
	 * 
	 * @param indexId
	 *            This number must be an indexId specified by a 'open_index'
	 *            request executed previously on the same connection.
	 * 
	 * @param values
	 *            values to compare with index keys
	 * @param operator
	 *            specifies the comparison operation to use
	 * @param limit
	 *            limit fetch count
	 * @param offset
	 *            fetch offset
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public int update(int indexId, String[] values, FindOperator operator,
			int limit, int offset) throws InterruptedException,
			TimeoutException, HandlerSocketException;

	/**
	 * Update data,set limit to 1 and offset to 0.
	 * 
	 * @see update
	 * @param indexId
	 * @param values
	 * @param operator
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public int update(int indexId, String[] values, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException;

	/**
	 * Delete data from mysql
	 * 
	 * @param indexId
	 *            This number must be an indexId specified by a 'open_index'
	 *            request executed previously on the same connection.
	 * 
	 * @param values
	 *            values to compare with index keys
	 * @param operator
	 *            specifies the comparison operation to use
	 * @param limit
	 *            limit fetch count
	 * @param offset
	 *            fetch offset
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public int delete(int indexId, String[] values, FindOperator operator,
			int limit, int offset) throws InterruptedException,
			TimeoutException, HandlerSocketException;

	/**
	 * Delete data from mysql,set limit to 1 and offset to 0.
	 * 
	 * @param indexId
	 * @param values
	 * @param operator
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public int delete(int indexId, String[] values, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException;

	/**
	 * Insert data
	 * 
	 * @param indexId
	 * @param values
	 *            the column values to set
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public boolean insert(int indexId, String[] values)
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
	public static final long DEFAULT_OP_TIMEOUT = 10000L;
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
