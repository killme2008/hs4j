package com.google.code.hs4j.example;

import java.net.InetSocketAddress;
import java.sql.ResultSet;

import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.HSClient;
import com.google.code.hs4j.impl.HSClientImpl;

public class HS4JExample {
	public static void main(String[] args) throws Exception {
		// [48, 9, 43, 9, 50, 9, 100, 101, 110, 110, 105, 115, 9, 107, 105, 108,
		// 108, 109, 101, 50, 48, 48, 56, 64, 103, 109, 97, 105, 108, 46, 99,
		// 111, 109, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0]
		HSClient client = new HSClientImpl(new InetSocketAddress(9999), 20);
		final String[] fieldList = { "user_id", "user_name", "user_email",
				"created" };
		System.out.println(client.openIndex(0, "mytest", "test_user", "NAME_MAIL_INDEX",
				fieldList));
		String[] values = { "kevin" };
		ResultSet rs = client.find(0, values);
		while (rs.next()) {
			System.out.println(rs.getString("user_name"));
			System.out.println(rs.getString("user_email"));
			System.out.println(rs.getString("user_id"));
			System.out.println(rs.getTimestamp("created"));
		}
		values = new String[] { "4", "dennis", "test@gmail.com" };
		System.out.println(client.insert(0, values));

		values = new String[] { "dennis" };
		// for (int i = 0; i < 1000; i++) {
		rs = client.find(0, values);
		while (rs.next()) {
			System.out.println(rs.getString("user_name"));
			System.out.println(rs.getString("user_email"));
			System.out.println(rs.getTimestamp("created"));
		}
		// }

		System.out.println(client.delete(0, values, FindOperator.EQ));
		rs = client.find(0, values);
		while (rs.next()) {
			System.out.println(rs.getString("user_name"));
			System.out.println(rs.getString("user_email"));
			// System.out.println(rs.getString("created"));
		}
		client.shutdown();
	}
}
