package com.google.code.hs4j;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.google.code.hs4j.network.core.SocketOption;
import com.google.code.hs4j.network.core.impl.StandardSocketOption;

/**
 * HSClient builder
 * 
 * @author dennis
 * @date 2010-12-1
 */
public interface HSClientBuilder {
	/**
	 * Build a HSClient with current configuration
	 * 
	 * @return
	 */
	public HSClient build()throws IOException;

	/**
	 * Set connection pool size
	 * 
	 * @param poolSize
	 */
	public void setConnectionPoolSize(int poolSize);

	/**
	 * Set handlersocket server address
	 * 
	 * @param host
	 *            hostname
	 * @param port
	 *            port
	 */
	public void setServerAddress(String host, int port);

	/**
	 * Set handlersocket server address
	 * 
	 * @param inetSocketAddress
	 */
	public void setServerAddress(InetSocketAddress inetSocketAddress);

	/**
	 * Set TCP socket option
	 * 
	 * @see StandardSocketOption
	 * @param socketOption
	 * @param value
	 */
	public <T> void setSocketOption(SocketOption<T> socketOption, T value);

	/**
	 * Add a client state listener
	 * 
	 * @param listener
	 */
	public void addStateListeners(HSClientStateListener listener);

}
