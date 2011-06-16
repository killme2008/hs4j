package com.github.zhongl.hs4j.kit.arguments;

import java.lang.reflect.*;
import java.util.*;

/**
 * {@link EntityValuesCollector}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
public class EntityValuesCollector extends EntityCollector {

  public EntityValuesCollector(int entityIndex, int primaryIndex, boolean excludePrimary) {
    super(entityIndex, primaryIndex);
    this.excludePrimary = excludePrimary;
  }

  @Override
  public String[] collectFrom(final Object[] args) {
    final Object entity = args[entityIndex];
    final Field[] fields = entity.getClass().getFields();
    final List<String> values = new ArrayList<String>();
    for (int i = 0; i < fields.length; i++) {
      if (primaryIndex == i && excludePrimary) continue;
      values.add(mirror.on(entity).get().field(fields[i]).toString());
    }
    return values.toArray(new String[0]);
  }

  private final boolean excludePrimary;
}
