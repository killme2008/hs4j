package com.google.code.hs4j.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;

import com.google.code.hs4j.Filter;
import com.google.code.hs4j.Filter.FilterType;
import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.Hs4jTestBase;
import com.google.code.hs4j.IndexSession;
import com.google.code.hs4j.ModifyStatement;

public class IndexSessionImplUnitTest extends Hs4jTestBase {
	private IndexSession session;
	private final String[] columns = { "user_id", "user_name", "user_email", "age" };
	private final String tableName = "test_user";
	private final String indexName = "NAME_MAIL_INDEX";
	@Before
	public void setUp() throws Exception {
		super.setUp();
		this.session = this.hsClient.openIndexSession(dbname, tableName,
				indexName, columns);
	}

	@Test
	public void insertByHS4J_FindByJDBC_DeleteByJDBC_FindByHS4J()
			throws Exception {
		assertTrue(this.session.insert(new String[] { "0", "阿丹", "阿丹@中国", "27",
				"2010-11-28 13:24:00" }));

		// find by jdbc
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt
				.executeQuery("select * from test_user where user_name='阿丹'");

		assertTrue(rs.next());

		System.out.println(rs.getInt(1));
		assertEquals("阿丹", rs.getString(2));
		assertEquals("阿丹@中国", rs.getString(3));
		assertEquals(27, rs.getInt(4));
		assertFalse(rs.next());
		rs.close();
		stmt.close();

		// find by hs4j
		final String[] keys = { "阿丹", "阿丹@中国" };
		rs = this.session.find(keys);
		assertTrue(rs.next());

		System.out.println(rs.getInt(1));
		assertEquals("阿丹", rs.getString(2));
		assertEquals("阿丹@中国", rs.getString(3));
		assertEquals(27, rs.getInt(4));
		assertFalse(rs.next());

		stmt = conn.createStatement();
		assertEquals(1, stmt
				.executeUpdate("delete from test_user where user_name='阿丹'"));
		stmt.close();
		rs = this.session.find(keys);
		assertFalse(rs.next());

	}

	@Test
	public void insertFindDelete_Chinese_ByHS4j() throws Exception {
		// find null
		final String[] keys = { "阿丹", "阿丹@中国" };
		ResultSet rs = this.session.find(keys);
		assertFalse(rs.next());

		// insert
		assertTrue(this.session.insert(new String[] { "0", "阿丹", "阿丹@中国", "27",
				"2010-11-28 13:24:00" }));

		// find once
		rs = this.session.find(keys);
		assertTrue(rs.next());

		System.out.println(rs.getInt(1));
		assertEquals("阿丹", rs.getString(2));
		assertEquals("阿丹@中国", rs.getString(3));
		assertEquals(27, rs.getInt(4));
		assertFalse(rs.next());

		// update
		assertEquals(1, this.session.update(keys, new String[] { "1", "阿丹",
				"阿丹@杭州", "109" }, FindOperator.EQ));

		// find twice
		rs = this.session.find(new String[] { "阿丹" });
		assertTrue(rs.next());

		System.out.println(rs.getInt("user_id"));
		assertEquals("阿丹", rs.getString("user_name"));
		assertEquals("阿丹@杭州", rs.getString("user_email"));
		assertEquals(109, rs.getInt("age"));
		assertFalse(rs.next());

		// delete
		assertEquals(1, this.session.delete(new String[] { "阿丹" }));
		// find null
		rs = this.session.find(keys);
		assertFalse(rs.next());
	}

	@Test
	public void testFindInsertFindUpdateFindDeleteFind_ByHS4j()
			throws Exception {
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

	@Test
	public void testFindInsertFindUpdateFindDeleteFind_ByModifyStatement_ByHS4j()
			throws Exception {
		// find null
		final String[] keys = { "dennis", "killme2008@\tgmail.com" };
		ResultSet rs = this.session.find(keys);
		assertFalse(rs.next());

		ModifyStatement stmt = this.session.createStatement();
		stmt.setInt(1, 0);
		stmt.setString(2, "dennis");
		stmt.setString(3, "killme2008@\tgmail.com");
		stmt.setInt(4, 27);
		stmt.setString(5, "2010-11-28 13:24:00");

		// insert
		assertTrue(stmt.insert());

		// find once
		rs = this.session.find(keys);
		assertTrue(rs.next());

		System.out.println(rs.getInt(1));
		assertEquals("dennis", rs.getString(2));
		assertEquals("killme2008@\tgmail.com", rs.getString(3));
		assertEquals(27, rs.getInt(4));
		assertFalse(rs.next());

		// update
		stmt = this.session.createStatement();
		stmt.setInt(1, 1);
		stmt.setString(2, "dennis");
		stmt.setString(3, "test@163.com");
		stmt.setInt(4, 109);
		assertEquals(1, stmt.update(keys, FindOperator.EQ));

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

	@Test
	public void testInsertByJDBC_FindByHS4J() throws Exception {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		assertEquals(
				1,
				stmt
						.executeUpdate("insert into test_user values(0,'dennis','test@163.com','27','2010-11-28 13:24:00')"));

		final String[] keys = { "dennis", "test@163.com" };
		ResultSet rs = this.session.find(keys);
		assertTrue(rs.next());

		System.out.println(rs.getInt(1));
		assertEquals("dennis", rs.getString(2));
		assertEquals("test@163.com", rs.getString(3));
		assertEquals(27, rs.getInt(4));
		assertFalse(rs.next());
	}
	@Test
	public void testInsertByJDBC_FindFilterByHS4J() throws Exception {
		Connection conn = getConnection();
		Statement stmt = conn.createStatement();
		String[] fcolumn = {"age"};
		IndexSession session = this.hsClient.openIndexSession(dbname, tableName,
				indexName, columns, fcolumn);

		assertEquals(
				1,
				stmt
						.executeUpdate("insert into test_user values(0,'dennis','test@163.com','27','2010-11-28 13:24:00')"));

		final String[] keys = { "dennis", "test@163.com" };
		Filter[] filters ={new Filter(FilterType.FILTER,FindOperator.EQ, 0, "27")};
		ResultSet rs = session.find(keys,FindOperator.EQ,1,0,filters);
		assertTrue(rs.next());

		System.out.println(rs.getInt(1));
		assertEquals("dennis", rs.getString(2));
		assertEquals("test@163.com", rs.getString(3));
		assertEquals(27, rs.getInt(4));
		assertFalse(rs.next());

		filters = new Filter[]{new Filter(FilterType.FILTER,FindOperator.GT, 0, "27")};
		rs = session.find(keys,FindOperator.EQ,1,0,filters);
		assertFalse(rs.next());		
	}

}
