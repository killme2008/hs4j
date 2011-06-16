package com.github.zhongl.hs4j.kit.annotations;

import static com.github.zhongl.hs4j.kit.arguments.StaticCollectorFactory.*;
import static com.github.zhongl.hs4j.kit.util.ClassUtils.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.sql.*;

import com.github.zhongl.hs4j.kit.arguments.*;
import com.github.zhongl.hs4j.kit.results.*;
import com.google.code.hs4j.*;

/**
 * {@link HandlerSocket}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface HandlerSocket {
  Action value();

  public enum Action {
    INSERT {

      @Override
      InvocationHandler createInvocationHandlerWith(final IndexSession session, ParameterAnnotations annotations) {
        final Collector<String[]> valuesCollector = createValuesCollectorFrom(annotations);
        return new InvocationHandler() {

          @Override
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            session.insert(valuesCollector.collectFrom(args));
            return null;
          }
        };
      }

    },
    UPDATE {

      @Override
      InvocationHandler createInvocationHandlerWith(final IndexSession session, ParameterAnnotations annotations) {
        final FindOperator operator = getOrDefaultEqualOperatorBy(annotations);
        final Collector<String[]> keysCollector = createKeysCollectorBy(annotations);
        final Collector<String[]> valuesCollector = createValuesCollectorFrom(annotations);
        final Collector<Integer> limitCollector = createLimitCollectorBy(annotations);
        final Collector<Integer> offsetCollector = createOffsetCollectorBy(annotations);
        return new InvocationHandler() {

          @Override
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            session.update(keysCollector.collectFrom(args),
                           valuesCollector.collectFrom(args),
                           operator,
                           limitCollector.collectFrom(args),
                           offsetCollector.collectFrom(args));
            return null;
          }
        };
      }

    },
    DELELT {

      @Override
      InvocationHandler createInvocationHandlerWith(final IndexSession session, ParameterAnnotations annotations) {
        final FindOperator operator = getOrDefaultEqualOperatorBy(annotations);
        final Collector<String[]> keysCollector = createKeysCollectorBy(annotations);
        final Collector<Integer> limitCollector = createLimitCollectorBy(annotations);
        final Collector<Integer> offsetCollector = createOffsetCollectorBy(annotations);

        return new InvocationHandler() {

          @Override
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            session.delete(keysCollector.collectFrom(args),
                           operator,
                           limitCollector.collectFrom(args),
                           offsetCollector.collectFrom(args));
            return null;
          }
        };
      }

    },
    FIND {

      @Override
      InvocationHandler createInvocationHandlerWith(final IndexSession session, ParameterAnnotations annotations) {
        final FindOperator operator = annotations.getOnlyOneAnnotation(Operator.class).value();
        final Collector<String[]> keysCollector = createKeysCollectorBy(annotations);
        final Collector<Integer> limitCollector = createLimitCollectorBy(annotations);
        final Collector<Integer> offsetCollector = createOffsetCollectorBy(annotations);

        return new InvocationHandler() {

          @Override
          @SuppressWarnings({ "rawtypes", "unchecked" })
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            final ResultSet resultSet =
              session.find(keysCollector.collectFrom(args),
                           operator,
                           limitCollector.collectFrom(args),
                           offsetCollector.collectFrom(args));
            ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
            return new ResultIterator(getOnlyOneTypeArgumentClassFrom(parameterizedType), resultSet);
          }

        };
      }

    };

    @SuppressWarnings("unchecked")
    private static ParameterAnnotations createParameterAnnotationLocatorBy(Method method) {
      return new ParameterAnnotations(method, Operator.class, Limit.class, Offset.class);
    }

    private static FindOperator getOrDefaultEqualOperatorBy(ParameterAnnotations annotations) {
      final Operator operator = annotations.getOnlyOneMoreAnnotation(Operator.class);
      return (operator == null) ? FindOperator.EQ : operator.value();
    }

    public final InvocationHandler createInvocationHandlerWith(Method method, IndexSession session) {
      try {
        return createInvocationHandlerWith(session, createParameterAnnotationLocatorBy(method));
      } catch (final IllegalArgumentException e) {
        throw new IllegalArgumentException(method.toString(), e);
      }
    }

    abstract InvocationHandler createInvocationHandlerWith(IndexSession session, ParameterAnnotations annotations);
  }
}
