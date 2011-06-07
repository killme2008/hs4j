package com.github.zhongl.hs4j.kit.arguments;

import java.util.*;

/**
 * {@link ArgumentsValuesCollector}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
class ArgumentsValuesCollector implements Collector<String[]> {

  private final List<Integer> indexs;

  public ArgumentsValuesCollector(List<Integer> indexs) {
    if (indexs.isEmpty()) throw new IllegalArgumentException("No value can be collected from method: " + indexs);
    this.indexs = indexs;
  }

  @Override
  public String[] collectFrom(Object[] args) {
    final String[] values = new String[indexs.size()];
    for (int i = 0; i < values.length; i++) {
      values[i] = args[indexs.get(i)].toString();
    }
    return values;
  }

}
