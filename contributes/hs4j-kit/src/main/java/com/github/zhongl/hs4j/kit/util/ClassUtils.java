package com.github.zhongl.hs4j.kit.util;

import java.lang.reflect.*;

/**
 * {@link ClassUtils}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-5
 * 
 */
public class ClassUtils {
  public static void assertMethodReturnType(Method method, Class<?> returnType) {
    if (method.getReturnType().equals(returnType)) return;
    throw new IllegalArgumentException(returnType + " should return by method: " + method);
  }

  @SuppressWarnings("unchecked")
  public static <T> Class<T> getOnlyOneTypeArgumentClassFrom(ParameterizedType parameterizedType) {
    final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
    if (actualTypeArguments.length > 1)
      throw new IllegalArgumentException(parameterizedType + " has more than one type arguments");
    return (Class<T>) actualTypeArguments[0];
  }

  public static boolean isIterable(Class<?> rawType) {
    return Iterable.class.isAssignableFrom(rawType);
  }

  public static boolean isParameterizedType(Type type) {
    return type instanceof ParameterizedType;
  }

  public static ParameterizedType parameterized(Type type) {
    return (ParameterizedType) type;
  }

  private ClassUtils() {}
}
