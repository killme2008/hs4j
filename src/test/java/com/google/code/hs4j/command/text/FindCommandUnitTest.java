package com.google.code.hs4j.command.text;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import com.google.code.hs4j.Filter;
import com.google.code.hs4j.Filter.FilterType;
import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.command.text.AbstractCommand.ParseState;
import com.google.code.hs4j.network.buffer.IoBuffer;

public class FindCommandUnitTest extends AbstractCommandUnitTest {

	@Override
	public AbstractCommand createCommand() {
		final String id = "1";
		final String[] keys = { "dennis" };
		int limit = 40;
		int offset = 0;
		final String[] fieldList = { "id", "name", "password" };
		return new FindCommand(id, FindOperator.EQ, keys, limit, offset,
				fieldList);
	}

	public AbstractCommand createCommandWithFilter() {
		final String id = "1";
		final String[] keys = { "dennis" };
		int limit = 40;
		int offset = 0;
		final String[] fieldList = { "id", "name", "password" };
		final Filter[] filterList = { new Filter(FilterType.FILTER,FindOperator.EQ,1,"27") };
		return new FindCommand(id, FindOperator.EQ, keys, limit, offset,
				fieldList,filterList);
	}

	@Test
	public void testDecodeTwice() throws SQLException {
		IoBuffer buffer = IoBuffer.wrap("0\t1\t1\tdennis\tpassword".getBytes());

		assertEquals(1, this.cmd.getLatch().getCount());
		assertFalse(this.cmd.decode(null, buffer));
		assertEquals(0, this.cmd.getResponseStatus());
		assertEquals(1, this.cmd.getNumColumns());

		assertEquals(ParseState.BODY, this.cmd.getCurrentState());
		assertEquals(1, this.cmd.getLatch().getCount());
		assertNull(this.cmd.getExceptionMessage());

		buffer = IoBuffer.wrap("\t2\tdennis\thello\n0\t1\n".getBytes());
		assertTrue(this.cmd.decode(null, buffer));

		ResultSet rs = (ResultSet) this.cmd.getResult();
		assertTrue(rs.next());
		assertEquals("1", rs.getString(1));
		assertEquals("dennis", rs.getString(2));
		assertEquals("password", rs.getString(3));

		assertEquals("1", rs.getString("id"));
		assertEquals("dennis", rs.getString("name"));
		assertEquals("password", rs.getString("password"));

		assertTrue(rs.next());
		assertEquals("2", rs.getString(1));
		assertEquals("dennis", rs.getString(2));
		assertEquals("hello", rs.getString(3));

		assertEquals("2", rs.getString("id"));
		assertEquals("dennis", rs.getString("name"));
		assertEquals("hello", rs.getString("password"));

		assertFalse(rs.next());
	}

	@Test
	public void testEncodeDecode() throws SQLException {
		assertNull(this.cmd.getIoBuffer());
		this.cmd.encode();
		IoBuffer buf = this.cmd.getIoBuffer();
		assertNotNull(buf);
		assertEquals(0, buf.position());
		assertTrue(buf.limit() > 0);

		assertEquals("1\t=\t1\tdennis\t40\t0\n", new String(buf.array(), 0, buf
				.limit()));

		IoBuffer buffer = IoBuffer
				.wrap("0\t1\t1\tdennis\tpassword\t2\tdennis\thello\n"
						.getBytes());

		assertEquals(1, this.cmd.getLatch().getCount());
		assertTrue(this.cmd.decode(null, buffer));
		assertEquals(0, this.cmd.getResponseStatus());
		assertEquals(1, this.cmd.getNumColumns());

		assertEquals(ParseState.DONE, this.cmd.getCurrentState());
		assertEquals(0, this.cmd.getLatch().getCount());
		assertNull(this.cmd.getExceptionMessage());

		ResultSet rs = (ResultSet) this.cmd.getResult();
		assertTrue(rs.next());
		assertEquals("1", rs.getString(1));
		assertEquals("dennis", rs.getString(2));
		assertEquals("password", rs.getString(3));

		assertEquals("1", rs.getString("id"));
		assertEquals("dennis", rs.getString("name"));
		assertEquals("password", rs.getString("password"));

		assertTrue(rs.next());
		assertEquals("2", rs.getString(1));
		assertEquals("dennis", rs.getString(2));
		assertEquals("hello", rs.getString(3));

		assertEquals("2", rs.getString("id"));
		assertEquals("dennis", rs.getString("name"));
		assertEquals("hello", rs.getString("password"));

		assertFalse(rs.next());
	}

	@Test
	public void testEncodeWithFilter() {
		AbstractCommand cmd = createCommandWithFilter();
		assertNull(cmd.getIoBuffer());
		cmd.encode();
		IoBuffer buf = cmd.getIoBuffer();
		assertNotNull(buf);
		assertEquals(0, buf.position());
		assertTrue(buf.limit() > 0);

		assertEquals("1\t=\t1\tdennis\t40\t0\tF\t=\t1\t27\n", new String(buf.array(), 0, buf
				.limit()));
	}

}
