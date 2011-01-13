package com.google.code.hs4j.impl;

import static org.junit.Assert.*;
import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import com.google.code.hs4j.ModifyStatement;

public class ModifyStatementUnitTest {
	private IMocksControl mocksControl;

	private HSClientImpl hsClientImpl;

	@Before
	public void setUp() {
		mocksControl = EasyMock.createControl();
		this.hsClientImpl = this.mocksControl.createMock(HSClientImpl.class);
		EasyMock.expect(this.hsClientImpl.getEncoding()).andReturn("utf-8")
				.anyTimes();
	}

	@Test
	public void testSetStringAutoExpand() {
		this.mocksControl.replay();
		HandlerSocketModifyStatement stmt = new HandlerSocketModifyStatement(1,
				1, hsClientImpl);
		for (int i = 0; i < 10; i++) {
			stmt.setString(i + 1, "hello");
		}
		byte[][] values = stmt.getValues();
		assertEquals(10, values.length);
		for (byte[] data : values) {
			assertEquals("hello", new String(data));
		}
		this.mocksControl.verify();
	}

}
