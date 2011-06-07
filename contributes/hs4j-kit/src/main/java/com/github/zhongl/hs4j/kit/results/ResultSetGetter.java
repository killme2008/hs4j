package com.github.zhongl.hs4j.kit.results;

import java.sql.*;

/**
 * {@link ResultSetGetter}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
public interface ResultSetGetter<T> {

  public abstract T get(ResultSet resultSet, String columnLabel) throws SQLException;
}
