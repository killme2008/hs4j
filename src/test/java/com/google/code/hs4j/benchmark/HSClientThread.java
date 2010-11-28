package com.google.code.hs4j.benchmark;

import java.util.concurrent.CyclicBarrier;

import com.google.code.hs4j.IndexSession;

public class HSClientThread extends Thread {

	private final CyclicBarrier barrier;

	private final int repeats;

	private final int index;

	private final IndexSession session;

	private final String remark;

	public HSClientThread(CyclicBarrier barrier, int repeats, int index,
			IndexSession dataSource, String remark) {
		super();
		this.barrier = barrier;
		this.repeats = repeats;
		this.index = index;
		this.session = dataSource;
		this.remark = remark;
	}

	@Override
	public void run() {
		try {
			this.barrier.await();
		} catch (Exception e) {
			// ignore
		}
		for (int i = 0; i < this.repeats; i++) {
			String postfix = this.index + "_" + i;
			final String[] values = new String[11];
			values[0] = String.valueOf(this.index * this.repeats + i);
			values[1] = "first_name_" + postfix;
			values[2] = "last_name_" + postfix;
			values[3] = "myduty_" + postfix;
			String phone = String.valueOf(i);
			values[4] = phone;
			values[5] = phone;
			values[6] = phone;
			values[7] = phone;
			values[8] = "my_home_address_" + postfix;
			values[9] = "my_office_address_" + postfix;
			values[10] = this.remark;
			try {
				if (!this.session.insert(values)) {
					System.out.println("error");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		try {
			this.barrier.await();
		} catch (Exception e) {
			// ignore
		}

	}
}
