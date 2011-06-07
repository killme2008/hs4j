package com.github.zhongl.hs4j.kit;

import static com.github.zhongl.hs4j.kit.util.StringUtils.*;
import static com.google.code.hs4j.FindOperator.*;
import static org.mockito.Mockito.*;

import org.junit.*;

import com.github.zhongl.hs4j.kit.proxy.*;
import com.google.code.hs4j.*;

/**
 * {@link HandlerSocketProxyTest}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-2
 * 
 */
public class HandlerSocketProxyTest {

  private static final int DEFAULT_LIMIT = Integer.MAX_VALUE;
  private static final int DEFAULT_OFFSET = 0;

  @Test
  public void addUser() throws Exception {
    doReturnSessionWhenHsClientOpenIndexSession("PRIMARY", new String[] { "seq", "name", "age" });

    final UserRepository proxy = proxyFactory.newProxyOf(UserRepository.class);
    final long id = 1L;
    final String name = "jushi";
    final int age = 30;
    proxy.addUser(id, name, age);

    verify(session).insert(toStringArray(id, name, age));
  }

  @Test
  public void addUserObject() throws Exception {
    doReturnSessionWhenHsClientOpenIndexSession("PRIMARY", new String[] { "seq", "name", "age" });

    final UserRepository proxy = proxyFactory.newProxyOf(UserRepository.class);
    final long id = 1L;
    final String name = "jushi";
    final int age = 30;
    proxy.add(new User(id, name, age));

    verify(session).insert(toStringArray(id, name, age));
  }

  @Test
  public void findUserAgeGreateThan() throws Exception {
    final int age = 30;
    doReturnSessionWhenHsClientOpenIndexSession("AGE", new String[] { "seq", "name", "age" });

    final UserRepository proxy = proxyFactory.newProxyOf(UserRepository.class);
    final int offset = 0;
    final int limit = 100;
    proxy.findUserAgeGreaterThan(age, offset, limit);

    verify(session).find(toStringArray(age), GT, limit, offset);
  }

  @Test
  public void findUserById() throws Exception {
    final long id = 1L;
    doReturnSessionWhenHsClientOpenIndexSession("PRIMARY", new String[] { "name", "age" });

    final UserRepository proxy = proxyFactory.newProxyOf(UserRepository.class);
    proxy.findUserBy(id);

    verify(session).find(toStringArray(id), EQ, DEFAULT_LIMIT, 0);
  }

  @Before
  public void setUp() throws Exception {
    reset(hsClient, session);
  }

  @Test
  public void updateUserAge() throws Exception {
    doReturnSessionWhenHsClientOpenIndexSession("NAME", new String[] { "age" });

    final UserRepository proxy = proxyFactory.newProxyOf(UserRepository.class);
    final String name = "zhongl";
    final int age = 22;
    proxy.updateUserAge(name, age);

    verify(session).update(toStringArray(name), toStringArray(age), EQ, DEFAULT_LIMIT, DEFAULT_OFFSET);
  }

  @Test
  public void updateUserObject() throws Exception {
    doReturnSessionWhenHsClientOpenIndexSession("PRIMARY", new String[] { "seq", "name", "age" });

    final UserRepository proxy = proxyFactory.newProxyOf(UserRepository.class);
    final long id = 1L;
    final String name = "zhongl";
    final int age = 22;
    proxy.update(new User(id, name, age));

    verify(session).update(toStringArray(id), toStringArray(name, age), EQ, DEFAULT_LIMIT, DEFAULT_OFFSET);
  }

  @Test
  public void deleteUser() throws Exception {
    doReturnSessionWhenHsClientOpenIndexSession("PRIMARY", new String[] { "seq", "name", "age" });

    final UserRepository proxy = proxyFactory.newProxyOf(UserRepository.class);
    final long id = 1L;
    final String name = "zhongl";
    final int age = 22;
    proxy.delete(new User(id, name, age));

    verify(session).delete(toStringArray(id), EQ, DEFAULT_LIMIT, DEFAULT_OFFSET);
  }

  @Test
  public void deleteUserAgeLessThan() throws Exception {
    doReturnSessionWhenHsClientOpenIndexSession("AGE", new String[] { "seq", "name", "age" });

    final UserRepository proxy = proxyFactory.newProxyOf(UserRepository.class);
    final int age = 22;
    proxy.deleteUserAgeLessThan(age);

    verify(session).delete(toStringArray(age), LT, DEFAULT_LIMIT, DEFAULT_OFFSET);

  }

  private void doReturnSessionWhenHsClientOpenIndexSession(String index, final String[] columns) throws Exception {
    doReturn(session).when(hsClient).openIndexSession("test", "user_t", index, columns);
  }

  private final HSClient hsClient = mock(HSClient.class);
  private final IndexSession session = mock(IndexSession.class);
  private final ProxyFactory proxyFactory = new HandlerSocketProxyFactory(hsClient);

}
