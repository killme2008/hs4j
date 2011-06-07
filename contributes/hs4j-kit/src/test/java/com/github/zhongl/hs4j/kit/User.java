package com.github.zhongl.hs4j.kit;

import com.github.zhongl.hs4j.kit.annotations.*;

class User {

  public User(Long id, String name, Integer age) {
    super();
    this.id = id;
    this.name = name;
    this.age = age;
  }

  @ColumnName("seq")
  public final Long id;

  public final String name;

  public final int age;

}
