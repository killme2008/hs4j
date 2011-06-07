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
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> fa88cc2... refactor code.
=======
>>>>>>> fa88cc2... refactor code.
  public static void assertMethodReturnType(Method method, Class<?> returnType) {
    if (method.getReturnType().equals(returnType)) return;
    throw new IllegalArgumentException(returnType + " should return by method: " + method);
  }

<<<<<<< HEAD
<<<<<<< HEAD
  @SuppressWarnings("unchecked")
  public static <T> Class<T> getOnlyOneTypeArgumentClassFrom(ParameterizedType parameterizedType) {
    final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
<<<<<<< HEAD
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
=======
=======
>>>>>>> fa88cc2... refactor code.
=======
>>>>>>> fa88cc2... refactor code.
  @SuppressWarnings("unchecked")
<<<<<<< HEAD
  public static <T> Class<T> getGenericClassFrom(Type genericReturnType) throws ClassNotFoundException {
    final String sigin = genericReturnType.toString();
    final int begin = sigin.indexOf('<') + 1;
    final int end = sigin.indexOf('>');
    return (Class<T>) Class.forName(sigin.substring(begin, end));
>>>>>>> 4c0b133... Abstract ClassUtils and fix some javadoc.
=======
  public static <T> Class<T> getOnlyOneTypeArgumentClassFrom(ParameterizedType parameterizedType) {
    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
=======
>>>>>>> 313c7bc... abstract method to ClassUtils.
    if (actualTypeArguments.length > 1)
      throw new IllegalArgumentException(parameterizedType + " has more than one type arguments");
    return (Class<T>) actualTypeArguments[0];
  }

  public static boolean isIterable(Class<?> rawType) {
    return Iterable.class.isAssignableFrom(rawType);
  }

  public static boolean isParameterizedType(Type type) {
    return type instanceof ParameterizedType;
>>>>>>> 6409ac1... User ParameterizedType refactor ClassUtils.
  }

  public static ParameterizedType parameterized(Type type) {
    return (ParameterizedType) type;
  }

  private ClassUtils() {}
}
