package com.github.zhongl.hs4j.kit.arguments;

import java.lang.reflect.*;

import com.github.zhongl.hs4j.kit.annotations.*;
import com.github.zhongl.hs4j.kit.annotations.HandlerSocket.Action;

/**
 * {@link StaticCollectorFactory}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
public class StaticCollectorFactory {
  private static final int ENTITY_INDEX = 0;
  private static final int PRIMARY_INDEX = 0;

  public static Collector<String[]> createKeysCollectorBy(ParameterAnnotations annotations) {
    final Method method = annotations.getMethod();
    final EntityClass entityClass = method.getAnnotation(EntityClass.class);
    if (entityClass != null) { // to find parameter type is entity class.
      assertNotBothEntityClassAndOperatorAreAnnotatedBy(annotations);
      assertParameterTypeJustOnlyBeEntityClassOn(method);
      return new EntityKeysCollector(ENTITY_INDEX, PRIMARY_INDEX);
    }
    return new KeysCollector(annotations.getOnlyOneParamterIndexAnnotatedBy(Operator.class));
  }

  private static void assertNotBothEntityClassAndOperatorAreAnnotatedBy(ParameterAnnotations annotations) {
    if (!annotations.getAnnotationsOf(Operator.class).isEmpty())
      throw new IllegalArgumentException("Either " + EntityClass.class + " or " + Operator.class + " can be annotated");
  }

  public static Collector<Integer> createLimitCollectorBy(ParameterAnnotations annotations) {
    return new LimitCollector(annotations.getOnlyOneMoreParamterIndexAnnotatedBy(Limit.class));
  }

  public static Collector<Integer> createOffsetCollectorBy(ParameterAnnotations annotations) {
    return new OffsetCollector(annotations.getOnlyOneMoreParamterIndexAnnotatedBy(Offset.class));
  }

  public static Collector<String[]> createValuesCollectorFrom(ParameterAnnotations annotations) {
    final Method method = annotations.getMethod();
    final EntityClass entityClass = method.getAnnotation(EntityClass.class);
    if (entityClass != null) { // to find parameter type is entity class.
      assertParameterTypeJustOnlyBeEntityClassOn(method);
      final boolean excludePrimary = method.getAnnotation(HandlerSocket.class).value() != Action.INSERT;
      return new EntityValuesCollector(ENTITY_INDEX, PRIMARY_INDEX, excludePrimary);
    }
    return new ArgumentsValuesCollector(annotations.getAnnotationlessParameterIndexs());
  }

  private static void assertParameterTypeJustOnlyBeEntityClassOn(final Method method) {
    final Class<?>[] parameterTypes = method.getParameterTypes();
    Class<?> entityClass = method.getAnnotation(EntityClass.class).value();
    if (parameterTypes.length != 1 || !parameterTypes[0].equals(entityClass))
      throw new IllegalArgumentException("Paramter Types should only be " + entityClass);
  }

  private StaticCollectorFactory() {}

}
