package com.github.zhongl.hs4j.kit.arguments;

/**
 * {@link LimitCollector}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
class LimitCollector implements Collector<Integer> {

  private static final int DEFAULT = Integer.MAX_VALUE;

  public LimitCollector(int index) {
    this.index = index;
  }

  @Override
  public Integer collectFrom(Object[] args) {
    try {
      return Integer.valueOf(args[index].toString());
    } catch (final Exception e) {
      return DEFAULT;
    }
  }

  private final int index;
}
