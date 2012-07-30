package com.github.zhongl.hs4j.kit.results;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {@link BigDecimalResultSetGetter}
 * 
 * @author <a href=mailto:mk@newit.pl>drago</a>
 * @created 2012-7-30
 */
public class BigDecimalResultSetGetter implements ResultSetGetter<BigDecimal> {

  @Override
  public BigDecimal get(ResultSet resultSet, String columnLabel) throws SQLException {
    return resultSet.getBigDecimal(columnLabel);
  }

}
