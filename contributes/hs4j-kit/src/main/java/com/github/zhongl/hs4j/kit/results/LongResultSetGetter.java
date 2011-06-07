package com.github.zhongl.hs4j.kit.results;

import java.sql.*;

/**
 * {@link LongResultSetGetter}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
class LongResultSetGetter implements ResultSetGetter<Long> {

  @Override
  public Long get(ResultSet resultSet, String columnLabel) throws SQLException {
    return resultSet.getLong(columnLabel);
  }

}
