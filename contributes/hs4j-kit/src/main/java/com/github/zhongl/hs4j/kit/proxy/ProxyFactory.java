package com.github.zhongl.hs4j.kit.proxy;

import java.lang.reflect.*;
import java.util.*;

import com.github.zhongl.hs4j.kit.annotations.*;

/**
 * {@link ProxyFactory}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-2
 * 
 */
public abstract class ProxyFactory {

  @SuppressWarnings("unchecked")
  public <T> T newProxyOf(Class<T> clazz) {
    scanAndMapMethodToInvacationHandlerWith(clazz);
    Class<?>[] interfaces = { clazz };
    return (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, dispatcher);
  }

  protected abstract InvocationHandler createInvocationHandlerWith(Method method, String database, String table);

  private void scanAndMapMethodToInvacationHandlerWith(Class<?> clazz) {
    final Repository repository = clazz.getAnnotation(Repository.class);
    if (repository == null)
      throw new IllegalArgumentException(Repository.class + " should be annotated to class: " + clazz);
    final String database = repository.database();
    final String table = repository.table();
    Method[] methods = clazz.getMethods();
    for (Method method : methods) {
      InvocationHandler handler = createInvocationHandlerWith(method, database, table);
      if ((handler == null)) continue; // no need to map, so skip.
      invocationMap.put(method, handler);
    }
  }

  protected final Map<Method, InvocationHandler> invocationMap = new HashMap<Method, InvocationHandler>();
  private final Dispatcher dispatcher = new Dispatcher();

  private final class Dispatcher implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      InvocationHandler invocationHandler = invocationMap.get(method);
      if (invocationHandler == null) throw new UnsupportedOperationException("No implement method: " + method);
      return invocationHandler.invoke(proxy, method, args);
    }
  }

}
