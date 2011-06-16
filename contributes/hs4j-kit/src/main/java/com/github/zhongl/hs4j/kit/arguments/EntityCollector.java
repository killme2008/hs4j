package com.github.zhongl.hs4j.kit.arguments;

import net.vidageek.mirror.dsl.*;

/**
 * {@link EntityCollector}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-5
 * 
 */
public abstract class EntityCollector implements Collector<String[]> {

  public EntityCollector(int entityIndex, int primaryIndex) {
    super();
    this.primaryIndex = primaryIndex;
    this.entityIndex = entityIndex;
    mirror = new Mirror();
  }

  protected final int primaryIndex;
  protected final int entityIndex;
  protected final Mirror mirror;

}
