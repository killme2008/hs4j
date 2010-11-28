package com.google.code.hs4j.command.text;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;

import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.command.text.AbstractCommand.ParseState;
import com.google.code.hs4j.network.buffer.IoBuffer;

public class ModifyCommandUnitTest extends AbstractCommandUnitTest {
	@Override
	public AbstractCommand createCommand() {
		final String id = "1";
		final String[] values = { "1", "green", "password" };
		final String[] keys = { "dennis" };
		return new ModifyCommand(id, FindOperator.EQ, keys, values, 1, 0, "U");
	}

	@Test
	public void testEncodeDecode() throws SQLException {
		assertNull(this.cmd.getIoBuffer());
		this.cmd.encode();
		IoBuffer buf = this.cmd.getIoBuffer();
		assertNotNull(buf);
		assertEquals(0, buf.position());
		assertTrue(buf.limit() > 0);

		assertEquals("1\t=\t1\tdennis\t1\t0\tU\t1\tgreen\tpassword\n",
				new String(buf.array(), 0, buf.limit()));

		IoBuffer buffer = IoBuffer.wrap("0\t1\t3\n".getBytes());

		assertEquals(1, this.cmd.getLatch().getCount());
		assertTrue(this.cmd.decode(null, buffer));
		assertEquals(0, this.cmd.getResponseStatus());
		assertEquals(1, this.cmd.getNumColumns());

		assertEquals(3, this.cmd.getResult());
		assertEquals(ParseState.DONE, this.cmd.getCurrentState());
		assertEquals(0, this.cmd.getLatch().getCount());
		assertNull(this.cmd.getExceptionMessage());
	}
}
