package com.google.code.hs4j.command.text;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.code.hs4j.command.text.AbstractCommand.ParseState;
import com.google.code.hs4j.network.buffer.IoBuffer;

public abstract class AbstractCommandUnitTest {

	protected AbstractCommand cmd;

	@Before
	public void setUp() {
		this.cmd = this.createCommand();
	}

	public abstract AbstractCommand createCommand();

	@Test
	public void testDecodeErrorMessage() {
		IoBuffer buffer = IoBuffer.wrap("1\t1\terror message\n".getBytes());

		assertEquals(1, this.cmd.getLatch().getCount());
		assertTrue(this.cmd.decode(null, buffer));
		assertEquals(1, this.cmd.getResponseStatus());
		assertEquals(1, this.cmd.getNumColumns());

		assertEquals(ParseState.DONE, this.cmd.getCurrentState());
		assertEquals(0, this.cmd.getLatch().getCount());
		assertEquals("Error message from server:error message", this.cmd
				.getExceptionMessage());

	}
}
