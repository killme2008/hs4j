package com.google.code.hs4j;

import java.sql.ResultSet;
import java.util.concurrent.TimeoutException;

import com.google.code.hs4j.exception.HandlerSocketException;

/**
 * A session with a special index id
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
	 * Getting data from mysql
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
	public ResultSet find(String[] values, FindOperator operator, int limit,
			int offset) throws InterruptedException, TimeoutException,
			HandlerSocketException;

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
	public ResultSet find(String[] values) throws InterruptedException,
			TimeoutException, HandlerSocketException;

	/**
	 * Update data
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
	public int update(String[] values, FindOperator operator, int limit,
			int offset) throws InterruptedException, TimeoutException,
			HandlerSocketException;

	/**
	 * Update data,set limit to 1 and offset to 0.
	 * 
	 * @see update
	 * @param values
	 * @param operator
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public int update(String[] values, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException;

	/**
	 * Delete data from mysql
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
	public int delete(String[] values, FindOperator operator, int limit,
			int offset) throws InterruptedException, TimeoutException,
			HandlerSocketException;

	/**
	 * Delete data from mysql,set limit to 1 and offset to 0.
	 * 
	 * @param values
	 * @param operator
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public int delete(String[] values, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException;

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
}
