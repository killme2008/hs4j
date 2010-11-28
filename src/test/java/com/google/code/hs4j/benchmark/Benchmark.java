package com.google.code.hs4j.benchmark;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.apache.commons.dbcp.BasicDataSource;

import com.google.code.hs4j.HSClient;
import com.google.code.hs4j.IndexSession;
import com.google.code.hs4j.impl.HSClientImpl;
import com.google.code.hs4j.network.util.ResourcesUtils;

/**
 * A benchmark between mysql driver and hsclient
 * 
 * @author dennis
 * @date 2010-11-27
 */
public class Benchmark {

	static final int connectionPoolSize = 100;
	static final int threads = 50;
	static final int repeats = 1000;
	static final int remarkSize = 1024;
	static final byte[] remarkData = new byte[remarkSize];

	public static void main(String[] args) throws Exception {
		// testMysql();

		HSClient hsClient = new HSClientImpl(new InetSocketAddress(9999), 100);
		final String[] columns = { "id", "last_name", "first_name", "duty",
				"cellphone", "housephone", "telephone", "office_fax",
				"home_address", "office_address", "remark" };
		IndexSession session = hsClient.openIndexSession("mytest", "user",
				"PRIMARY", columns);
		CyclicBarrier barrier = new CyclicBarrier(threads + 1);
		String remark = new String(remarkData);
		for (int i = 0; i < threads; i++) {
			HSClientThread mysqlThread = new HSClientThread(barrier, repeats,
					i, session, remark);
			mysqlThread.start();
		}
		long start = System.currentTimeMillis();
		barrier.await();
		barrier.await();
		long end = System.currentTimeMillis();
		long duration = end - start;
		long tps = repeats * threads * 1000 / duration;
		System.out.println("Concurrency " + threads + " threads,repeats="
				+ repeats + ",duration=" + duration + "ms,tps=" + tps);

		hsClient.shutdown();
	}

	private static void testMysql() throws IOException, InterruptedException,
			BrokenBarrierException, SQLException {
		Properties props = new Properties();
		props.load(ResourcesUtils.getResourceAsStream("benchmark.properties"));
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setInitialSize(threads);
		dataSource.setMaxActive(Integer.parseInt(props
				.getProperty("dataSource.maxActive")));
		dataSource
				.setDriverClassName(props.getProperty("jdbc.driverClassName"));
		dataSource.setUrl(props.getProperty("jdbc.url"));
		dataSource.setUsername(props.getProperty("jdbc.username"));
		dataSource.setPassword(props.getProperty("jdbc.password"));
		dataSource.setMaxWait(Long.parseLong(props
				.getProperty("dataSource.maxWait")));
		dataSource.setMaxIdle(Integer.parseInt(props
				.getProperty("dataSource.maxIdle")));
		dataSource.setMinIdle(Integer.parseInt(props
				.getProperty("dataSource.minIdle")));

		CyclicBarrier barrier = new CyclicBarrier(threads + 1);
		String remark = new String(remarkData);
		for (int i = 0; i < threads; i++) {
			MysqlThread mysqlThread = new MysqlThread(barrier, repeats, i,
					dataSource, remark);
			mysqlThread.start();
		}
		long start = System.currentTimeMillis();
		barrier.await();
		barrier.await();
		long end = System.currentTimeMillis();
		long duration = end - start;
		long tps = repeats * threads * 1000 / duration;
		System.out.println("Concurrency " + threads + " threads,repeats="
				+ repeats + ",duration=" + duration + "ms,tps=" + tps);

		dataSource.close();
	}
}
