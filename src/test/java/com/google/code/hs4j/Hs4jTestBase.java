package com.google.code.hs4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;

import com.google.code.hs4j.impl.HSClientImpl;
import com.google.code.hs4j.network.util.ResourcesUtils;

public abstract class Hs4jTestBase {
	protected HSClient hsClient;
	protected Properties props;
	protected String dbname;
	protected String hostName;

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
		URI url = new URI(props.getProperty("jdbc.url").substring(5));
		this.hostName = url.getHost();
		this.hsClient = new HSClientImpl(this.hostName, 9999);
		this.dbname = url.getPath().substring(1);
		BufferedReader reader = null;
		String createTableSql = null;
		try {
			in = ResourcesUtils.getResourceAsStream("test.sql");
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			createTableSql = sb.toString();
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
		if (createTableSql != null) {
			Connection conn = getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(createTableSql);
			stmt.close();
			conn.close();
		}
	}

	@After
	public void tearDown() throws Exception {
		if (this.hsClient != null) {
			hsClient.shutdown();
		}
	}

	protected Connection getConnection() throws Exception {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection conn = DriverManager.getConnection(
				props.getProperty("jdbc.url"), props.getProperty("jdbc.user"), props.getProperty("jdbc.password"));
		return conn;
	}
}
