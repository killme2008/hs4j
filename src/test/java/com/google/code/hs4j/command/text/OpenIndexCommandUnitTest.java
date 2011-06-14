package com.google.code.hs4j.command.text;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.code.hs4j.command.text.AbstractCommand.ParseState;
import com.google.code.hs4j.network.buffer.IoBuffer;

public class OpenIndexCommandUnitTest extends AbstractCommandUnitTest {


	@Override
	public AbstractCommand createCommand() {
		final String id = "1";
		final String db = "mytest";
		final String tableName = "user";
		final String indexName = "INDEX_1";
		final String[] fieldList = { "id", "name", "password" };
		return  new OpenIndexCommand(id, db, tableName, indexName, fieldList);
	}

	public AbstractCommand createCommandWithFilter() {
		final String id = "1";
		final String db = "mytest";
		final String tableName = "user";
		final String indexName = "INDEX_1";
		final String[] fieldList = { "id", "name", "password" };
		final String[] filterList = { "age" ,"name"};
		return  new OpenIndexCommand(id, db, tableName, indexName, fieldList, filterList);
	}

	@Test
	public void testEncodeDecode() {
		assertNull(this.cmd.getIoBuffer());
		this.cmd.encode();
		IoBuffer buf = this.cmd.getIoBuffer();
		assertNotNull(buf);
		assertEquals(0, buf.position());
		assertTrue(buf.limit() > 0);

		assertEquals("P\t1\tmytest\tuser\tINDEX_1\tid,name,password\n",
				new String(buf.array(), 0, buf.limit()));

		IoBuffer buffer = IoBuffer.allocate(4);
		buffer.put("0\t1\n".getBytes());
		buffer.flip();

		assertEquals(1, this.cmd.getLatch().getCount());
		assertTrue(this.cmd.decode(null, buffer));
		assertEquals(0, this.cmd.getResponseStatus());
		assertEquals(1, this.cmd.getNumColumns());

		assertEquals(ParseState.DONE, this.cmd.getCurrentState());
		assertEquals(0, this.cmd.getLatch().getCount());
		assertNull(this.cmd.getExceptionMessage());

	}

	@Test
	public void testEncodeFilter() {
		AbstractCommand cmd = createCommandWithFilter();
		assertNull(cmd.getIoBuffer());
		cmd.encode();
		IoBuffer buf = cmd.getIoBuffer();
		assertNotNull(buf);
		assertEquals(0, buf.position());
		assertTrue(buf.limit() > 0);

		assertEquals("P\t1\tmytest\tuser\tINDEX_1\tid,name,password\tage,name\n",
				new String(buf.array(), 0, buf.limit()));

		IoBuffer buffer = IoBuffer.allocate(4);
		buffer.put("0\t1\n".getBytes());
		buffer.flip();

		assertEquals(1, cmd.getLatch().getCount());
		assertTrue(cmd.decode(null, buffer));
		assertEquals(0, cmd.getResponseStatus());
		assertEquals(1, cmd.getNumColumns());

		assertEquals(ParseState.DONE, cmd.getCurrentState());
		assertEquals(0, cmd.getLatch().getCount());
		assertNull(cmd.getExceptionMessage());

	}

}
