package com.github.zhongl.hs4j.kit.arguments;

/**
 * {@link OffsetCollector}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
class OffsetCollector implements Collector<Integer> {

  private static final int DEFAULT = 0;

  public OffsetCollector(int index) {
    this.index = index;
  }

  @Override
  public Integer collectFrom(Object[] args) {
    try {
      return Integer.parseInt(args[index].toString());
    } catch (final Exception e) {
      return DEFAULT;
    }
  }

  private final int index;

}
