package com.google.code.hs4j.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.google.code.hs4j.HSClient;

import junit.framework.TestCase;

public class HandlerSocketBinaryDecodeUnitTest extends TestCase {
	private Connection conn;

	public void setUp() throws Exception {
		conn = getConnection();
		createTable(conn);
	}

	public void tearDown() throws Exception {
		dropTable(conn);
		conn.close();
	}

	public void testHs4j() throws Exception {
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
		assertEquals(b1.length, b2.length);
		for (int i = 0; i < b1.length; ++i) {
			assertEquals(b1[i], b2[i]);
		}
	}

	private byte[] readBytesHS4J(String key) throws Exception {
		HSClient client = new HSClientImpl("localhost", 9998);
		client.openIndex(0, "mytest", "hs4jtest", "PRIMARY",
				new String[] { "value" });
		ResultSet rs = client.find(0, new String[] { key });
		try {
			if (rs.next()) {
				byte[] bytes = rs.getBytes(1);
				return bytes;
			} else
				return new byte[] {};
		} finally {
			rs.close();
			client.shutdown();
		}
	}

	private byte[] readBytesJDBC(String key) throws Exception {
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
		}
	}

	private void dropTable(Connection conn) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("drop table hs4jtest");
			ps.execute();
		} finally {
			ps.close();
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
		}
	}

	private Connection getConnection() throws Exception {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/mytest",
				"root", "212002");
		return conn;
	}
}