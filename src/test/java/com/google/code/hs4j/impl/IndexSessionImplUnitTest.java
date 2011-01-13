package com.google.code.hs4j.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.HSClient;
import com.google.code.hs4j.IndexSession;
import com.google.code.hs4j.network.util.ResourcesUtils;

public class IndexSessionImplUnitTest {
	private IndexSession session;

	private HSClient hsClient;
	private Properties props;

	@Before
	public void setUp() throws Exception {
		this.props = new Properties();
		InputStream in = null;
		try {
			in = ResourcesUtils.getResourceAsStream("jdbc.properties");
			this.props.load(in);
		} catch (IOException e) {

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {

				}
			}
		}
		this.hsClient = new HSClientImpl(this.props.getProperty("hs.hostname"),
				Integer.parseInt(this.props.getProperty("hs.port")));
		String dbname = this.props.getProperty("hs.db");
		final String[] columns = { "user_id", "user_name", "user_email", "age" };
		this.session = this.hsClient.openIndexSession(dbname, "test_user",
				"NAME_MAIL_INDEX", columns);
	}

	@Test
	public void testFindInsertFindUpdateFindDeleteFind() throws Exception {
		// find null
		final String[] keys = { "dennis", "killme2008@\tgmail.com" };
		ResultSet rs = this.session.find(keys);
		assertFalse(rs.next());

		// insert
		assertTrue(this.session.insert(new String[] { "0", "dennis",
				"killme2008@\tgmail.com", "27", "2010-11-28 13:24:00" }));

		// find once
		rs = this.session.find(keys);
		assertTrue(rs.next());

		System.out.println(rs.getInt(1));
		assertEquals("dennis", rs.getString(2));
		assertEquals("killme2008@\tgmail.com", rs.getString(3));
		assertEquals(27, rs.getInt(4));
		assertFalse(rs.next());

		// update
		assertEquals(1, this.session.update(keys, new String[] { "1", "dennis",
				"test@163.com", "109" }, FindOperator.EQ));

		// find twice
		rs = this.session.find(new String[] { "dennis" });
		assertTrue(rs.next());

		System.out.println(rs.getInt("user_id"));
		assertEquals("dennis", rs.getString("user_name"));
		assertEquals("test@163.com", rs.getString("user_email"));
		assertEquals(109, rs.getInt("age"));
		assertFalse(rs.next());

		// delete
		assertEquals(1, this.session.delete(new String[] { "dennis" },
				FindOperator.EQ));
		// find null
		rs = this.session.find(keys);
		assertFalse(rs.next());
	}

	@After
	public void tearDown() throws Exception {
		this.hsClient.shutdown();
	}

}
