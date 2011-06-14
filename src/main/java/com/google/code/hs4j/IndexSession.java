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

import java.sql.ResultSet;
import java.util.concurrent.TimeoutException;

import com.google.code.hs4j.exception.HandlerSocketException;

/**
 * A session with a special index id,it is thread-safe.
 * 
 * @author dennis
 * @date 2010-11-27
 */
public interface IndexSession {

	/**
	 * Returns current session's indexId
	 * 
	 * @return
	 */
	public int getIndexId();

	/**
	 * Returns current session's columns
	 * 
	 * @return
	 */
	public String[] getColumns();

	
	/**
	 * Getting data from mysql, use filter
	 * 
	 * @param keys
	 *            keys to compare with index columns
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
	public ResultSet find(String[] keys, FindOperator operator, int limit,
			int offset, Filter[] filters) throws InterruptedException, TimeoutException,
			HandlerSocketException;
	
	/**
	 * Getting data from mysql
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
	public ResultSet find(String[] keys, FindOperator operator, int limit,
			int offset) throws InterruptedException, TimeoutException,
			HandlerSocketException;

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
	public ResultSet find(String[] keys) throws InterruptedException,
			TimeoutException, HandlerSocketException;

	/**
	 * Update data
	 * 
	 * @param keys
	 *            keys to compare with index columns
	 * @param values
	 *            values to set
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
	public int update(String[] keys, String[] values, FindOperator operator,
			int limit, int offset) throws InterruptedException,
			TimeoutException, HandlerSocketException;

	/**
	 * Update data,set limit to 1 and offset to 0.
	 * 
	 * @param keys
	 *            keys to compare with index columns
	 * @see update
	 * @param values
	 * @param operator
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public int update(String[] keys, String[] values, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException;

	/**
	 * Delete data from mysql
	 * 
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
	public int delete(String[] keys, FindOperator operator, int limit,
			int offset) throws InterruptedException, TimeoutException,
			HandlerSocketException;

	/**
	 * Delete data from mysql,set limit to 1 and offset to 0.
	 * 
	 * @param keys
	 * @param operator
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public int delete(String[] keys, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException;

	/**
	 * Delete data from mysql,set limit to 1,offset to 0 and FindOperator to EQ
	 * 
	 * @see delete
	 * @param keys
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public int delete(String[] keys) throws InterruptedException,
			TimeoutException, HandlerSocketException;

	/**
	 * Insert data
	 * 
	 * @param values
	 *            the column values to set
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public boolean insert(String[] values) throws InterruptedException,
			TimeoutException, HandlerSocketException;

	/**
	 * Create a modify statement for this index session
	 * 
	 * @return
	 */
	public ModifyStatement createStatement() throws HandlerSocketException;
}
