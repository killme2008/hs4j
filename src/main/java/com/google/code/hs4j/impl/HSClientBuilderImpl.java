package com.google.code.hs4j.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.code.hs4j.HSClient;
import com.google.code.hs4j.HSClientBuilder;
import com.google.code.hs4j.HSClientStateListener;
import com.google.code.hs4j.command.text.TextCommandFactory;
import com.google.code.hs4j.network.config.Configuration;
import com.google.code.hs4j.network.core.SocketOption;
import com.google.code.hs4j.network.core.impl.StandardSocketOption;

/**
 * A hsclient builder implementation
 * 
 * @author dennis
 * @date 2010-12-1
 */
public class HSClientBuilderImpl implements HSClientBuilder {
	private InetSocketAddress serverAddr;
	private List<HSClientStateListener> listeners;
	@SuppressWarnings("unchecked")
	private Map<SocketOption, Object> socketOptions;
	private int poolSize = 1;

	public void addStateListeners(HSClientStateListener listener) {
		if (this.listeners == null) {
			this.listeners = new ArrayList<HSClientStateListener>();
		}
		this.listeners.add(listener);
	}

	public void setConnectionPoolSize(int poolSize) {
		if (poolSize <= 0) {
			throw new IllegalArgumentException(
					"poolSize must be greater than zero");
		}
		this.poolSize = poolSize;
	}

	public HSClient build() throws IOException {
		return new HSClientImpl(new TextCommandFactory(), this.serverAddr,
				this.listeners, this.socketOptions, this.poolSize);
	}

	public void setServerAddress(InetSocketAddress inetSocketAddress) {
		if (this.serverAddr != null) {
			throw new IllegalStateException("Server Address is existed");
		}
		if (inetSocketAddress == null) {
			throw new IllegalArgumentException("Null inetSocketAddress");
		}
		this.serverAddr = inetSocketAddress;

	}

	public void setServerAddress(String hostname, int port) {
		InetSocketAddress addr = new InetSocketAddress(hostname, port);
		if (this.serverAddr != null) {
			throw new IllegalStateException("Server Address is existed");
		}
		if (addr == null) {
			throw new IllegalArgumentException("Null inetSocketAddress");
		}
		this.serverAddr = addr;
	}

	public <T> void setSocketOption(SocketOption<T> socketOption, T value) {
		if (this.socketOptions == null) {
			this.socketOptions = getDefaultSocketOptions();
		}
		this.socketOptions.put(socketOption, value);
	}

	@SuppressWarnings("unchecked")
	public static final Map<SocketOption, Object> getDefaultSocketOptions() {
		Map<SocketOption, Object> map = new HashMap<SocketOption, Object>();
		map
				.put(StandardSocketOption.TCP_NODELAY,
						HSClient.DEFAULT_TCP_NO_DELAY);
		map.put(StandardSocketOption.SO_RCVBUF,
				HSClient.DEFAULT_TCP_RECV_BUFF_SIZE);
		map.put(StandardSocketOption.SO_KEEPALIVE,
				HSClient.DEFAULT_TCP_KEEPLIVE);
		map.put(StandardSocketOption.SO_SNDBUF,
				HSClient.DEFAULT_TCP_SEND_BUFF_SIZE);
		map.put(StandardSocketOption.SO_LINGER, 0);
		map.put(StandardSocketOption.SO_REUSEADDR, true);
		return map;
	}

	public static final Configuration getDefaultConfiguration() {
		final Configuration configuration = new Configuration();
		configuration
				.setSessionReadBufferSize(HSClient.DEFAULT_SESSION_READ_BUFF_SIZE);
		configuration.setReadThreadCount(HSClient.DEFAULT_READ_THREAD_COUNT);
		configuration.setSessionIdleTimeout(-1);
		configuration.setWriteThreadCount(0);
		return configuration;
	}

}
