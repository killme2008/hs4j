package com.github.zhongl.hs4j.kit.annotations;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.*;

/**
 * {@link Repository}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-2
 * 
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Repository {
  String database();

  String table();
}
