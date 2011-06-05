package com.github.zhongl.hs4j.kit;

import static com.github.zhongl.hs4j.kit.annotations.HandlerSocket.Action.*;
import static com.google.code.hs4j.FindOperator.*;

import com.github.zhongl.hs4j.kit.annotations.*;
import com.github.zhongl.hs4j.kit.results.*;

/**
 * {@link UserRepository}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-2
 * 
 */
@Repository(database = "test", table = "user_t")
interface UserRepository {
  @HandlerSocket(INSERT)
  @EntityClass(User.class)
  void add(User user);

  @HandlerSocket(INSERT)
  @Columns({ "seq", "name", "age" })
  void addUser(long id, String name, int age);

  @HandlerSocket(DELELT)
  @EntityClass(User.class)
  void delete(User user);

  @HandlerSocket(DELELT)
  @Index("AGE")
  @Columns({ "seq", "name", "age" })
  void deleteUserAgeLessThan(@Operator(LT) int age);

  @HandlerSocket(FIND)
  @Index("AGE")
  @Columns({ "seq", "name", "age" })
  ResultIterator<User> findUserAgeGreaterThan(@Operator(GT) int age, @Offset int offset, @Limit int limit);

  @HandlerSocket(FIND)
  @Columns({ "name", "age" })
  ResultIterator<User> findUserBy(@Operator(EQ) long id);

  @HandlerSocket(UPDATE)
  @EntityClass(User.class)
  void update(User user);

  @HandlerSocket(UPDATE)
  @Columns("age")
  @Index("NAME")
  void updateUserAge(@Operator(EQ) String name, int age);

}
