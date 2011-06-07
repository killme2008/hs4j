package com.github.zhongl.hs4j.kit.arguments;

/**
 * {@link Collector}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-3
 * 
 */
public interface Collector<T> {
	T collectFrom(Object[] args);
}
