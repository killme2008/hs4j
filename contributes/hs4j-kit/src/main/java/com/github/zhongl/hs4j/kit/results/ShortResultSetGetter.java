package com.github.zhongl.hs4j.kit.results;

import java.sql.*;

/**
 * {@link ShortResultSetGetter}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
class ShortResultSetGetter implements ResultSetGetter<Short> {

  @Override
  public Short get(ResultSet resultSet, String columnLabel) throws SQLException {
    return resultSet.getShort(columnLabel);
  }

}
