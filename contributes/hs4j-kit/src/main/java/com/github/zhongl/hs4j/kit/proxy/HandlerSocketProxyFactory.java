package com.github.zhongl.hs4j.kit.proxy;

import static com.github.zhongl.hs4j.kit.annotations.HandlerSocket.Action.*;
import static com.github.zhongl.hs4j.kit.util.ClassUtils.*;
import static com.github.zhongl.hs4j.kit.util.StringUtils.*;
import static org.apache.commons.codec.digest.DigestUtils.*;

import java.lang.reflect.*;
import java.util.*;

import com.github.zhongl.hs4j.kit.annotations.*;
import com.github.zhongl.hs4j.kit.annotations.HandlerSocket.Action;
import com.github.zhongl.hs4j.kit.results.*;
import com.google.code.hs4j.*;

/**
 * {@link HandlerSocketProxyFactory}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-2
 * 
 */
public class HandlerSocketProxyFactory extends ProxyFactory {

  private static String[] getColumnsFrom(Field[] fields) {
    final String[] columns = new String[fields.length];
    for (int i = 0; i < columns.length; i++) {
      final ColumnName columnName = fields[i].getAnnotation(ColumnName.class);
      columns[i] = (columnName == null) ? fields[i].getName() : columnName.value();
    }
    return columns;
  }

  private static boolean has(Columns columns) {
    return columns != null && columns.value().length > 0;
  }

  public HandlerSocketProxyFactory(HSClient hsClient) {
    this.hsClient = hsClient;
    indexSessionCache = new HashMap<Object, IndexSession>();
  }

  @Override
  protected InvocationHandler createInvocationHandlerWith(Method method, String database, String table) {
    final HandlerSocket handlerSocket = method.getAnnotation(HandlerSocket.class);
    if ((handlerSocket == null)) return null;
    final Action action = handlerSocket.value();
    if (action == FIND) assertMethodReturnType(method, ResultIterator.class);
    else assertMethodReturnType(method, void.class);
    final IndexSession session = getOrCreateIndexSessionWith(method, database, table);
    return action.createInvocationHandlerWith(method, session);
  }

  private String[] columnsOf(Method method) {
    final Columns columns = method.getAnnotation(Columns.class);
    if (has(columns)) return columns.value();

    final EntityClass entityClass = method.getAnnotation(EntityClass.class);
    if (entityClass == null)
      throw new IllegalArgumentException("Either @Columns or @EntityClass should be annotated to method: " + method);
    return getColumnsFrom(entityClass.value().getFields());
  }

  private IndexSession getOrCreateIndexSessionWith(Method method, String database, String table) {
    synchronized (indexSessionCache) {
      final String index = indexOf(method);
      final String[] columns = columnsOf(method);
      final Object key = md5Hex(join(index, Arrays.toString(columns)));

      IndexSession session = indexSessionCache.get(key);
      if (session != null) return session;

      try {
        session = hsClient.openIndexSession(database, table, index, columns);
        indexSessionCache.put(key, session);
        return session;
      } catch (final Exception e) {
        if (e instanceof InterruptedException) Thread.currentThread().interrupt();
        throw new IllegalStateException("HandlerSocket can't open index.", e);
      }
    }
  }

  private String indexOf(Method method) {
    final Index index = method.getAnnotation(Index.class);
    return (index == null) ? "PRIMARY" : index.value();
  }

  private final HSClient hsClient;
  private final Map<Object, IndexSession> indexSessionCache;
}
