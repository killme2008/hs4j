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
package com.google.code.hs4j;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.concurrent.TimeoutException;

import com.google.code.hs4j.exception.HandlerSocketException;

/**
 * A HandlerSocket client,it is thread-safe.
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
	 * Just like openIndex method,but return a IndexSession,please reuse
	 * IndexSession as much as possible.
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
	 * Open a IndexSession with a auto-generated indexId,please reuse
	 * IndexSession as much as possible.
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
	 * Getting data from mysql, use filter
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
	 * @param filters
	 *            specifies a filter 
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public ResultSet find(int indexId, String[] keys, FindOperator operator,
			int limit, int offset, Filter[] filters ) throws InterruptedException,
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
	 * @param keys
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public ResultSet find(int indexId, String[] keys)
			throws InterruptedException, TimeoutException,
			HandlerSocketException;

	/**
	 * Update data
	 * 
	 * @param indexId
	 *            This number must be an indexId specified by a 'open_index'
	 *            request executed previously on the same connection.
	 * @param keys
	 *            keys to compare with index columns
	 * 
	 * @param values
	 *            modify values
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
	public int update(int indexId, String[] keys, String[] values,
			FindOperator operator, int limit, int offset)
			throws InterruptedException, TimeoutException,
			HandlerSocketException;

	/**
	 * Update data,set limit to 1 and offset to 0.
	 * 
	 * @see update
	 * @param indexId
	 * @param keys
	 * @param values
	 * @param operator
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public int update(int indexId, String[] keys, String[] values,
			FindOperator operator) throws InterruptedException,
			TimeoutException, HandlerSocketException;

	/**
	 * Delete data from mysql
	 * 
	 * @param indexId
	 *            This number must be an indexId specified by a 'open_index'
	 *            request executed previously on the same connection.
	 * 
	 * @param keys
	 *            keys to compare with index columns
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
	public int delete(int indexId, String[] keys, FindOperator operator,
			int limit, int offset) throws InterruptedException,
			TimeoutException, HandlerSocketException;

	/**
	 * Delete data from mysql,set limit to 1 and offset to 0.
	 * 
	 * @param indexId
	 * @param keys
	 * @param operator
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public int delete(int indexId, String[] keys, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException;

	/**
	 * Delete data from mysql,set limit to 1,offset to 0 and FindOperator to EQ
	 * 
	 * @see delete
	 * @param indexId
	 * @param keys
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public int delete(int indexId, String[] keys) throws InterruptedException,
			TimeoutException, HandlerSocketException;

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

	/**
	 * Shutdown client
	 * 
	 * @throws IOException
	 */
	public void shutdown() throws IOException;

	/**
	 * Set heal connection interval in milliseconds,default is two seconds.
	 * 
	 * @param interval
	 */
	public void setHealConnectionInterval(long interval);

	/**
	 * Returns heal connection interval in milliseconds,default is two seconds.
	 * 
	 * @return
	 */
	public long getHealConnectionInterval();

	/**
	 * Returns whether allowing client to auto-reconnect connection when it was
	 * closed by exception or error.It's true by default.
	 * 
	 * @return
	 */
	public boolean isAllowAutoReconnect();

	/**
	 * Set client to auto-reconnect connection when it was closed by exception
	 * or error,true is allow,and it is true by default.
	 * 
	 * @param allowAutoReconnect
	 */
	public void setAllowAutoReconnect(boolean allowAutoReconnect);

	/**
	 * Set character encoding,default is utf-8
	 * 
	 * @param encoding
	 */
	public void setEncoding(String encoding);

	/**
	 * Returns current character encoding
	 * 
	 * @return
	 */
	public String getEncoding();

	/**
	 * Create a modify statement for special indexID
	 * 
	 * @param indexId
	 * @return
	 * @throws HandlerSocketException
	 */
	public ModifyStatement createStatement(int indexId)
			throws HandlerSocketException;

	/**
	 * Default thread number for reading nio's receive buffer and dispatch
	 * commands.Recommend users to set it equal or less to the HandlerSocket
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

	public static final int DEFAULT_CONNECTION_POOL_SIZE = 1;

	/**
	 * Default encoding
	 */
	public static final String DEFAULT_ENCODING = "utf-8";

}
