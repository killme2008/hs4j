package com.github.zhongl.hs4j.kit.results;

import java.sql.*;

/**
 * {@link BooleanResultSetGetter}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
class BooleanResultSetGetter implements ResultSetGetter<Boolean> {

  @Override
  public Boolean get(ResultSet resultSet, String columnLabel) throws SQLException {
    return resultSet.getBoolean(columnLabel);
  }

}
