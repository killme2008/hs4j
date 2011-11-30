package com.google.code.hs4j;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.TimeoutException;

import com.google.code.hs4j.exception.HandlerSocketException;

/**
 * HandlerSocket modify statement
 * 
 * @author dennis
 * 
 */
public interface ModifyStatement {

	/**
	 * Sets the designated parameter to the given Java <code>boolean</code>
	 * value. The driver converts this to an SQL <code>BIT</code> or
	 * <code>BOOLEAN</code> value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 */
	void setBoolean(int parameterIndex, boolean x);

	/**
	 * Sets the designated parameter to the given Java <code>byte</code> value.
	 * The driver converts this to an SQL <code>TINYINT</code> value when it
	 * sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 */
	void setByte(int parameterIndex, byte x);

	/**
	 * Sets the designated parameter to the given Java <code>short</code> value.
	 * The driver converts this to an SQL <code>SMALLINT</code> value when it
	 * sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 */
	void setShort(int parameterIndex, short x);

	/**
	 * Sets the designated parameter to the given Java <code>int</code> value.
	 * The driver converts this to an SQL <code>INTEGER</code> value when it
	 * sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 */
	void setInt(int parameterIndex, int x);

	/**
	 * Sets the designated parameter to the given Java <code>long</code> value.
	 * The driver converts this to an SQL <code>BIGINT</code> value when it
	 * sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 */
	void setLong(int parameterIndex, long x);

	/**
	 * Sets the designated parameter to the given Java <code>float</code> value.
	 * The driver converts this to an SQL <code>REAL</code> value when it sends
	 * it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 */
	void setFloat(int parameterIndex, float x);

	/**
	 * Sets the designated parameter to the given Java <code>double</code>
	 * value. The driver converts this to an SQL <code>DOUBLE</code> value when
	 * it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 */
	void setDouble(int parameterIndex, double x);

	/**
	 * Sets the designated parameter to the given
	 * <code>java.math.BigDecimal</code> value. The driver converts this to an
	 * SQL <code>NUMERIC</code> value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 */
	void setBigDecimal(int parameterIndex, BigDecimal x);

	/**
	 * Sets the designated parameter to the given Java <code>String</code>
	 * value. The driver converts this to an SQL <code>VARCHAR</code> or
	 * <code>LONGVARCHAR</code> value (depending on the argument's size relative
	 * to the driver's limits on <code>VARCHAR</code> values) when it sends it
	 * to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 */
	void setString(int parameterIndex, String x);

	/**
	 * Sets the designated parameter to the given Java array of bytes. The
	 * driver converts this to an SQL <code>VARBINARY</code> or
	 * <code>LONGVARBINARY</code> (depending on the argument's size relative to
	 * the driver's limits on <code>VARBINARY</code> values) when it sends it to
	 * the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 */
	void setBytes(int parameterIndex, byte x[]);

	/**
	 * Sets the designated parameter to the given <code>java.sql.Blob</code>
	 * object. The driver converts this to an SQL <code>BLOB</code> value when
	 * it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            a <code>Blob</code> object that maps an SQL <code>BLOB</code>
	 *            value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 */
	void setBlob(int parameterIndex, Blob x);

	/**
	 * Sets the designated parameter to the given <code>java.sql.Clob</code>
	 * object. The driver converts this to an SQL <code>CLOB</code> value when
	 * it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            a <code>Clob</code> object that maps an SQL <code>CLOB</code>
	 *            value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 */
	void setClob(int parameterIndex, Clob x);

	/**
	 * Insert modify statement
	 * 
	 * @return
	 */
	public boolean insert() throws HandlerSocketException,
			InterruptedException, TimeoutException;

	/**
	 * Update modify statement
	 * 
	 * @param indexId
	 * @param keys
	 * @param values
	 * @param operator
	 * @param limit
	 * @param offset
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public int update(String[] keys, FindOperator operator, int limit,
			int offset) throws InterruptedException, TimeoutException,
			HandlerSocketException;

	/**
	 * Update data.Offset is zero,and limit is one.
	 * 
	 * @param indexId
	 * @param keys
	 * @param operator
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public int update(String[] keys, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException;

	/**
	 * Incrment data.Offset is zero,and limit is one.
	 *
	 * @param indexId
	 * @param keys
	 * @param operator
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public int incr(String[] keys, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException;

	/**
	 * Decrment data.Offset is zero,and limit is one.
	 *
	 * @param indexId
	 * @param keys
	 * @param operator
	 * @return
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws HandlerSocketException
	 */
	public int decr(String[] keys, FindOperator operator)
			throws InterruptedException, TimeoutException,
			HandlerSocketException;
}
