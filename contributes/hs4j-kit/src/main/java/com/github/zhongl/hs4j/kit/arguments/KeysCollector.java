package com.github.zhongl.hs4j.kit.arguments;

/**
 * {@link KeysCollector}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
class KeysCollector implements Collector<String[]> {

  private static boolean isStringArray(Object keys) {
    return keys.getClass().equals(String[].class);
  }

  private static String[] toStringArray(Object... objects) {
    final String[] values = new String[objects.length];
    for (int i = 0; i < values.length; i++) {
      values[i] = objects[i].toString();
    }
    return values;
  }

  public KeysCollector(int index) {
    this.index = index;
  }

  @Override
  public String[] collectFrom(Object[] args) {
    final Object keys = args[index];
    if (isStringArray(keys)) return (String[]) keys;
    return toStringArray(keys);
  }

  private final int index;

}
