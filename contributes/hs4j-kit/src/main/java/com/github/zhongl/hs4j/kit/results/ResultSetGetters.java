package com.github.zhongl.hs4j.kit.results;

import java.util.*;

/**
 * {@link ResultSetGetters}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
public class ResultSetGetters {

  private static final Map<Class<?>, ResultSetGetter<?>> map;

  static {
    final ResultSetGetter<Boolean> booleanResultSetGetter = new BooleanResultSetGetter();
    final ResultSetGetter<Short> shortResultSetGetter = new ShortResultSetGetter();
    final ResultSetGetter<Integer> integerResultSetGetter = new IntegerResultSetGetter();
    final ResultSetGetter<Long> longResultSetGetter = new LongResultSetGetter();
    final ResultSetGetter<String> stringResultSetGetter = new StringResultSetGetter();

    map = new HashMap<Class<?>, ResultSetGetter<?>>();
    map.put(boolean.class, booleanResultSetGetter);
    map.put(Boolean.class, booleanResultSetGetter);
    map.put(short.class, shortResultSetGetter);
    map.put(Short.class, shortResultSetGetter);
    map.put(int.class, integerResultSetGetter);
    map.put(Integer.class, integerResultSetGetter);
    map.put(long.class, longResultSetGetter);
    map.put(Long.class, longResultSetGetter);
    map.put(String.class, stringResultSetGetter);
  }

  @SuppressWarnings("unchecked")
  public static <T> ResultSetGetter<T> resultSetGetterOf(Class<T> clazz) {
    final ResultSetGetter<T> resultSetGetter = (ResultSetGetter<T>) map.get(clazz);
    if (resultSetGetter == null) throw new IllegalArgumentException("Unsupported class: " + clazz);
    return resultSetGetter;
  }
}
