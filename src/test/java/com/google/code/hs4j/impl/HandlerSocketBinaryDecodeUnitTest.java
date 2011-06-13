package com.google.code.hs4j.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.code.hs4j.Hs4jTestBase;
import com.google.code.hs4j.ModifyStatement;

public class HandlerSocketBinaryDecodeUnitTest extends Hs4jTestBase {

	@Before
	@Override
	public void setUp() throws Exception {

		super.setUp();
		createTable(getConnection());
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		dropTable(getConnection());
	}

	@Test
	public void testWriteByHs4j_ReadBinaryData() throws Exception {
		String key = "test.key";
		byte[] written = new byte[] { 0, 104, 101, 108, 108, 111, 32, 119, 111,
				114, 108, 100 };
		byte[] read = null;

		// write over jdbc
		writeBytesHsj4(key, written);

		// read over jdbc
		read = readBytesJDBC(key);
		assertEquals(written, read);

		read = readBytesHS4J(key);
		assertEquals(written, read);
	}

	@Test
	public void testWriteByJDBC_ReadBinaryData() throws Exception {
		String key = "test.key";
		byte[] written = new byte[] { 0, 104, 101, 108, 108, 111, 32, 119, 111,
				114, 108, 100 };
		byte[] read = null;

		// write over jdbc
		writeBytesJDBC(key, written);

		// read over jdbc
		read = readBytesJDBC(key);
		assertEquals(written, read);

		read = readBytesHS4J(key);
		assertEquals(written, read);
	}

	public void assertEquals(byte[] b1, byte[] b2) {
		assertNotNull(b1);
		assertNotNull(b2);
		assertTrue(b1.length == b2.length);
		for (int i = 0; i < b1.length; ++i) {
			assertTrue(b1[i] == b2[i]);
		}
	}

	private byte[] readBytesHS4J(String key) throws Exception {

		hsClient.openIndex(0, "mytest", "hs4jtest", "PRIMARY",
				new String[] { "value" });
		ResultSet rs = hsClient.find(0, new String[] { key });
		try {
			if (rs.next()) {
				byte[] bytes = rs.getBytes(1);
				return bytes;
			} else
				return new byte[] {};
		} finally {
			rs.close();
		}
	}

	private byte[] readBytesJDBC(String key) throws Exception {
		Connection conn = getConnection();
		PreparedStatement ps = conn
				.prepareStatement("select value from hs4jtest where id = ?");
		try {
			ps.setString(1, key);
			ResultSet rs = ps.executeQuery();
			assertTrue(rs.next());
			byte[] bytes = rs.getBytes(1);
			return bytes;
		} finally {
			ps.close();
			conn.close();
		}
	}

	private void writeBytesJDBC(String key, byte[] bytes) throws Exception {
		Connection conn = getConnection();
		try {
			writeBytes(conn, key, bytes);
		} finally {
			conn.close();
		}
	}

	public void writeBytesHsj4(String id, byte[] bytes) throws Exception {
		hsClient.openIndex(0, "mytest", "hs4jtest", "PRIMARY",
				new String[] { "id", "value" });
		ModifyStatement stmt = hsClient.createStatement(0);

		stmt.setString(1, id);
		stmt.setBytes(2, bytes);
		stmt.insert();

	}

	private void writeBytes(Connection conn, String id, byte[] bytes)
			throws Exception {
		PreparedStatement ps = conn
				.prepareStatement("insert into hs4jtest (id, value) values (?, ?)");
		try {
			ps.setString(1, id);
			ps.setBytes(2, bytes);
			ps.execute();
		} finally {
			ps.close();
			conn.close();
		}
	}

	private void dropTable(Connection conn) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("drop table hs4jtest");
			ps.execute();
		} finally {
			ps.close();
			conn.close();
		}
	}

	private void createTable(Connection conn) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = conn
					.prepareStatement("create table hs4jtest ( id varchar(32) primary key not null, value blob ) ENGINE=InnoDB DEFAULT CHARSET=latin1");
			ps.execute();
		} finally {
			ps.close();
			conn.close();
		}
	}

}