package com.github.zhongl.hs4j.kit.results;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.*;

import org.junit.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;

import com.github.zhongl.hs4j.kit.*;

/**
 * {@link ResultSetIteratorTest}
 * 
 * @author <a href=mailto:zhong.lunfu@gmail.com>zhongl</a>
 * @created 2011-6-5
 * 
 */
public class ResultSetIteratorTest {
  @Test
  public void user() throws Exception {
    final long id = 1L;
    final String name = "zhongl";
    final int age = 23;
    doAnswer(new NextAnswer(1)).when(resultSet).next();
    doReturn(id).when(resultSet).getLong("seq");
    doReturn(name).when(resultSet).getString("name");
    doReturn(age).when(resultSet).getInt("age");

    final ResultSetIterator iterator = new ResultSetIterator(User.class.getName(), resultSet);
    assertThat(iterator.next(), is(true));
    final User user = (User) iterator.get();
    assertThat(user.id, is(id));
    assertThat(user.name, is(name));
    assertThat(user.age, is(age));
  }

  private final ResultSet resultSet = mock(ResultSet.class);

  private final class NextAnswer implements Answer<Boolean> {
    public NextAnswer(int size) {
      this.size = size;
    }

    @Override
    public Boolean answer(InvocationOnMock invocation) throws Throwable {
      return size-- >= 0;
    }

    private int size;
  }
}
