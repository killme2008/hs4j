package com.github.zhongl.hs4j.kit.results;

import static com.github.zhongl.hs4j.kit.results.ResultSetGetters.*;

import java.lang.reflect.*;
import java.sql.*;

import net.vidageek.mirror.dsl.*;
import net.vidageek.mirror.list.dsl.*;

import com.github.zhongl.hs4j.kit.annotations.*;

/**
 * {@link ResultIterator}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-5
 * 
 */
public class ResultIterator<T> {
  
  public ResultIterator(Class<T> clazz, ResultSet resultSet) {
    this.classController = new Mirror().on(clazz);
    this.resultSet = resultSet;
  }

  public void clear() {
    try {
      resultSet.close();
    } catch (final SQLException e) {
      // TODO log e as debug.
    }
  }

  public T get() {
    final T instance = classController.invoke().constructor().bypasser();
    classController.reflectAll().fields().mappingTo(new Mapper<Field, Void>() {

      @Override
      public Void map(Field field) {
        try {
          final Object value = getValueFromResultSetBy(field);
          new Mirror().on(instance).set().field(field).withValue(value);
        } catch (final SQLException e) {
          // TODO log e as debug.
        }
        return null;
      }
    });
    return instance;
  }

  public boolean next() throws SQLException {
    return resultSet.next();
  }

  private String columnLabelOf(Field field) {
    final ColumnName columnName = field.getAnnotation(ColumnName.class);
    return (columnName != null) ? columnName.value() : field.getName();
  }

  private Object getValueFromResultSetBy(Field field) throws SQLException {
    return resultSetGetterOf(field.getType()).get(resultSet, columnLabelOf(field));
  }

  private final ResultSet resultSet;
  private final ClassController<T> classController;
}
