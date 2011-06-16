package com.github.zhongl.hs4j.kit.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

import com.google.code.hs4j.*;

/**
 * {@link Operator}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-2
 * 
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Operator {
  FindOperator value();
}
