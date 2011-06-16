package com.github.zhongl.hs4j.kit.results;

import static com.github.zhongl.hs4j.kit.results.ResultSetGetters.*;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import net.vidageek.mirror.dsl.*;
import net.vidageek.mirror.list.dsl.*;

import com.github.zhongl.hs4j.kit.annotations.*;

/**
 * {@link ResultSetIterator}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
public final class ResultSetIterator implements Iterator<Object> {

  public ResultSetIterator(Class<?> clazz, ResultSet resultSet) {
    this(clazz.getName(), resultSet);
  }

  public ResultSetIterator(String className, ResultSet resultSet) {
    this.resultSet = resultSet;
    mirror = new Mirror();
    classController = mirror.on(className);
  }

  @Override
  public boolean hasNext() {
    try {
      return resultSet.next();
    } catch (final SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Object next() {
    final Object instance = classController.invoke().constructor().bypasser();
    classController.reflectAll().fields().mappingTo(new Mapper<Field, Void>() {

      @Override
      public Void map(Field field) {
        try {
          final Object value = getValueFromResultSetBy(field);
          mirror.on(instance).set().field(field).withValue(value);
        } catch (final SQLException e) {
          // TODO log as debug.
        }
        return null;
      }
    });
    return instance;
  }

  @Override
  public void remove() {
    // TODO Auto-generated method stub

  }

  private String columnLabelOf(Field field) {
    final ColumnName columnName = field.getAnnotation(ColumnName.class);
    return (columnName != null) ? columnName.value() : field.getName();
  }

  private Object getValueFromResultSetBy(Field field) throws SQLException {
    return resultSetGetterOf(field.getType()).get(resultSet, columnLabelOf(field));
  }

  private final ResultSet resultSet;
  private final Mirror mirror;
  private final ClassController<?> classController;
}
