package com.github.zhongl.hs4j.kit.annotations;

import static com.github.zhongl.hs4j.kit.arguments.StaticCollectorFactory.*;
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
      InvocationHandler createInvocationHandlerWith(final IndexSession session, ParameterAnnotationLocator locator) {
        final Collector<String[]> valuesCollector = createValuesCollectorFrom(locator);
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
      InvocationHandler createInvocationHandlerWith(final IndexSession session, ParameterAnnotationLocator locator) {
        final FindOperator operator = getOrDefaultEqualOperatorBy(locator);
        final Collector<String[]> keysCollector = createKeysCollectorBy(locator);
        final Collector<String[]> valuesCollector = createValuesCollectorFrom(locator);
        final Collector<Integer> limitCollector = createLimitCollectorBy(locator);
        final Collector<Integer> offsetCollector = createOffsetCollectorBy(locator);
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
      InvocationHandler createInvocationHandlerWith(final IndexSession session, ParameterAnnotationLocator locator) {
        final FindOperator operator = getOrDefaultEqualOperatorBy(locator);
        final Collector<String[]> keysCollector = createKeysCollectorBy(locator);
        final Collector<Integer> limitCollector = createLimitCollectorBy(locator);
        final Collector<Integer> offsetCollector = createOffsetCollectorBy(locator);

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
      InvocationHandler createInvocationHandlerWith(final IndexSession session, ParameterAnnotationLocator locator) {
        final FindOperator operator = locator.getOnlyOneAnnotation(Operator.class).value();
        final Collector<String[]> keysCollector = createKeysCollectorBy(locator);
        final Collector<Integer> limitCollector = createLimitCollectorBy(locator);
        final Collector<Integer> offsetCollector = createOffsetCollectorBy(locator);

        return new InvocationHandler() {

          @Override
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            final ResultSet resultSet =
              session.find(keysCollector.collectFrom(args),
                           operator,
                           limitCollector.collectFrom(args),
                           offsetCollector.collectFrom(args));
            return new ResultSetIterator(getGenericClassNameFrom(method.getGenericReturnType()), resultSet);
          }

        };
      }

    };

    @SuppressWarnings("unchecked")
    private static ParameterAnnotationLocator createParameterAnnotationLocatorBy(Method method) {
      return new ParameterAnnotationLocator(method, Operator.class, Limit.class, Offset.class);
    }

    private static String getGenericClassNameFrom(Type genericReturnType) {
      final String sigin = genericReturnType.toString();
      final int begin = sigin.indexOf('<') + 1;
      final int end = sigin.indexOf('>');
      return sigin.substring(begin, end); // java.util.Iterator<$GenericClassName>
    }

    private static FindOperator getOrDefaultEqualOperatorBy(ParameterAnnotationLocator locator) {
      final Operator operator = locator.getOnlyOneMoreAnnotation(Operator.class);
      return (operator == null) ? FindOperator.EQ : operator.value();
    }

    public final InvocationHandler createInvocationHandlerWith(Method method, IndexSession session) {
      try {
        return createInvocationHandlerWith(session, createParameterAnnotationLocatorBy(method));
      } catch (final IllegalArgumentException e) {
        throw new IllegalArgumentException(method.toString(), e);
      }
    }

    abstract InvocationHandler createInvocationHandlerWith(IndexSession session, ParameterAnnotationLocator locator);
  }
}
