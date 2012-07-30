package com.github.zhongl.hs4j.kit.results;

import java.math.BigInteger;
import java.sql.*;

/**
 * {@link BigIntegerResultSetGetter}
 * 
 * @author <a href=mailto:mk@newit.pl>drago</a>
 * @created 2012-7-26
 */
class BigIntegerResultSetGetter implements ResultSetGetter<BigInteger> {

  @Override
  public BigInteger get(ResultSet resultSet, String columnLabel) throws SQLException {
    return resultSet.getBigDecimal(columnLabel).toBigInteger();
  }

}
