package com.github.zhongl.hs4j.kit.arguments;

import static java.util.Collections.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * {@link ParameterAnnotationLocator}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
@SuppressWarnings("unchecked")
public class ParameterAnnotationLocator {

  public ParameterAnnotationLocator(Method method, Class<? extends Annotation>... annotationClasses) {
    this.method = method;
    annotationlessParameterIndexs = new ArrayList<Integer>();
    map = new HashMap<Class<? extends Annotation>, List<Coordinate>>();
    initMapBy(annotationClasses);
    scanMethodParameterAnnotations();
  }

  public List<Integer> getAnnotationlessParameterIndexs() {
    return unmodifiableList(annotationlessParameterIndexs);
  }

  public <T extends Annotation> List<T> getAnnotationsOf(Class<T> annotationClass) {
    final List<Coordinate> coordinates = getCoordinatesOf(annotationClass);
    return new AbstractList<T>() {

      @Override
      public T get(int index) {
        return (T) getAnnotationBy(coordinates.get(index));
      }

      @Override
      public int size() {
        return coordinates.size();
      }
    };
  }

  public List<Coordinate> getCoordinatesOf(Class<? extends Annotation> annotationClass) {
    final List<Coordinate> coordinates = map.get(annotationClass);
    if (coordinates != null) return coordinates;
    throw new NoSuchElementException(annotationClass.toString());
  }

  public Method getMethod() {
    return method;
  }

  public <T extends Annotation> T getOnlyOneAnnotation(Class<T> annotationClass) {
    return (T) getAnnotationBy(getOnlyOneCoordinateOf(annotationClass));
  }

  public <T extends Annotation> T getOnlyOneMoreAnnotation(Class<T> annotationClass) {
    final Coordinate coordinate = getOnlyOneMoreCoordinateOf(annotationClass);
    return (coordinate == null) ? null : (T) method.getParameterAnnotations()[coordinate.x][coordinate.y];
  }

  public int getOnlyOneMoreParamterIndexAnnotatedBy(Class<? extends Annotation> annotationClass) {
    final Coordinate coordinate = getOnlyOneMoreCoordinateOf(annotationClass);
    return (coordinate == null) ? -1 : coordinate.x; // no exist index.
  }

  public int getOnlyOneParamterIndexAnnotatedBy(Class<? extends Annotation> annotationClass) {
    return getOnlyOneCoordinateOf(annotationClass).x;
  }

  private <T extends Annotation> T getAnnotationBy(Coordinate coordinate) {
    return (T) method.getParameterAnnotations()[coordinate.x][coordinate.y];
  }

  private Coordinate getOnlyOneCoordinateOf(Class<? extends Annotation> annotationClass) {
    final List<Coordinate> coordinates = getCoordinatesOf(annotationClass);
    if (coordinates.size() != 1)
      throw new IllegalArgumentException(annotationClass + " should be annotated just once");
    return coordinates.get(0);
  }

  private Coordinate getOnlyOneMoreCoordinateOf(Class<? extends Annotation> annotationClass) {
    final List<Coordinate> coordinates = getCoordinatesOf(annotationClass);
    if (coordinates.size() > 1)
      throw new IllegalArgumentException(annotationClass + " should be annotated only one more");
    if (coordinates.isEmpty()) return null;
    return coordinates.get(0);
  }

  private void initMapBy(Class<? extends Annotation>... annotationClasses) {
    for (final Class<? extends Annotation> annotationClass : annotationClasses) {
      map.put(annotationClass, new ArrayList<Coordinate>());
    }
  }

  private void scanMethodParameterAnnotations() {
    final Annotation[][] parameterAnnotations = method.getParameterAnnotations();;
    for (int x = 0; x < parameterAnnotations.length; x++) {
      if (parameterAnnotations[x].length == 0) {
        annotationlessParameterIndexs.add(x);
        continue;
      }

      for (int y = 0; y < parameterAnnotations[x].length; y++) {
        final Class<? extends Annotation> annotationClass = parameterAnnotations[x][y].annotationType();
        final List<Coordinate> coordinates = map.get(annotationClass);
        if (coordinates != null) coordinates.add(new Coordinate(x, y));
      }
    }
  }

  private final Method method;
  private final List<Integer> annotationlessParameterIndexs;
  private final Map<Class<? extends Annotation>, List<Coordinate>> map;

  public final class Coordinate {
    private Coordinate(int x, int y) {
      super();
      this.x = x;
      this.y = y;
    }

    public final int x;
    public final int y;
  }
}
