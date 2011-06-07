package com.github.zhongl.hs4j.kit.arguments;

import static com.github.zhongl.hs4j.kit.util.StringUtils.*;

import java.lang.reflect.*;

import net.vidageek.mirror.list.dsl.*;

/**
 * {@link EntityKeysCollector}
 * 
 * @author <a href=mailto:jushi@taobao.com>jushi</a>
 * @created 2011-6-5
 * 
 */
public class EntityKeysCollector extends EntityCollector {

  public EntityKeysCollector(int entityIndex, int primaryIndex) {
    super(entityIndex, primaryIndex);
  }

  @Override
  public String[] collectFrom(Object[] args) {
    final MirrorList<Field> fields = mirror.on(args[entityIndex].getClass()).reflectAll().fields();
    final Object value = mirror.on(args[entityIndex]).get().field(fields.get(primaryIndex));
    return toStringArray(value);
  }

}
