package com.google.code.hs4j.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CyclicBarrier;

import javax.sql.DataSource;

public class MysqlThread extends Thread {

	private final CyclicBarrier barrier;

	private final int repeats;

	private final int index;

	private final DataSource dataSource;

	private final String remark;

	public MysqlThread(CyclicBarrier barrier, int repeats, int index,
			DataSource dataSource, String remark) {
		super();
		this.barrier = barrier;
		this.repeats = repeats;
		this.index = index;
		this.dataSource = dataSource;
		this.remark = remark;
	}

	@Override
	public void run() {
		try {
			this.barrier.await();
		} catch (Exception e) {
			// ignore
		}
		final String insertSQL = "insert into user values(?,?,?,?,?,?,?,?,?,?,?)";
		for (int i = 0; i < this.repeats; i++) {
			String postfix = this.index + "_" + i;
			Connection conn = null;
			try {
				conn = this.dataSource.getConnection();
				PreparedStatement pstmt = null;
				try {
					pstmt = conn.prepareStatement(insertSQL);

					pstmt.setInt(1, 0);// id
					pstmt.setString(2, "my_first_name_" + postfix);
					pstmt.setString(3, "last_name_" + postfix);
					pstmt.setString(4, "myduty_" + postfix);
					String phone = String.valueOf(i);
					pstmt.setString(5, phone);
					pstmt.setString(6, phone);
					pstmt.setString(7, phone);
					pstmt.setString(8, phone);
					pstmt.setString(9, "my_home_address_" + postfix);
					pstmt.setString(10, "my_office_address_" + postfix);
					pstmt.setString(11, this.remark);

					pstmt.executeUpdate();

				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (pstmt != null) {
						try {

							pstmt.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}

		}
		try {
			this.barrier.await();
		} catch (Exception e) {
			// ignore
		}

	}
}
