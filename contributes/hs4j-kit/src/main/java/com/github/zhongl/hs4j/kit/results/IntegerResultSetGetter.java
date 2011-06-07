package com.github.zhongl.hs4j.kit.results;

import java.sql.*;

/**
 * {@link IntegerResultSetGetter}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
class IntegerResultSetGetter implements ResultSetGetter<Integer> {

  @Override
  public Integer get(ResultSet resultSet, String columnLabel) throws SQLException {
    return resultSet.getInt(columnLabel);
  }

}
