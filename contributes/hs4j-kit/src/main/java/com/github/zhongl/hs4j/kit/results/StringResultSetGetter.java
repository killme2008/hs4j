package com.github.zhongl.hs4j.kit.results;

import java.sql.*;

/**
 * {@link StringResultSetGetter}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
class StringResultSetGetter implements ResultSetGetter<String> {

  @Override
  public String get(ResultSet resultSet, String columnLabel) throws SQLException {
    return resultSet.getString(columnLabel);
  }

}
