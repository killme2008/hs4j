package com.google.code.hs4j.command.text;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;

import com.google.code.hs4j.command.text.AbstractCommand.ParseState;
import com.google.code.hs4j.network.buffer.IoBuffer;

public class InsertCommandUnitTest extends AbstractCommandUnitTest {
	@Override
	public AbstractCommand createCommand() {
		final String id = "1";
		final String[] values = { "1", "dennis", "password" };
		return new InsertCommand(id, values);
	}

	@Test
	public void testEncodeDecode() throws SQLException {
		assertNull(this.cmd.getIoBuffer());
		this.cmd.encode();
		IoBuffer buf = this.cmd.getIoBuffer();
		assertNotNull(buf);
		assertEquals(0, buf.position());
		assertTrue(buf.limit() > 0);

		assertEquals("1\t+\t3\t1\tdennis\tpassword\n", new String(buf.array(),
				0, buf.limit()));

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
}
