package com.google.code.hs4j.network.hs;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import com.google.code.hs4j.Command;
import com.google.code.hs4j.Protocol;
import com.google.code.hs4j.exception.HandlerSocketException;
import com.google.code.hs4j.impl.ReconnectRequest;
import com.google.code.hs4j.network.core.Session;

/**
 * Networking connector
 * 
 * @author dennis
 * @date 2010-11-29
 */
public interface HandlerSocketConnector {
	public boolean isStarted();

	public void setHealSessionInterval(long healConnectionInterval);

	public long getHealSessionInterval();

	public Protocol getProtocol();

	public void addSession(Session session);

	public void removeSession(Session session);

	public void addToWatingQueue(ReconnectRequest request);

	public Future<Boolean> connect(InetSocketAddress remoteAddr)
			throws IOException;

	public CopyOnWriteArrayList<Session> getSessionList();

	public void send(final Command msg) throws HandlerSocketException;

	public boolean isAllowAutoReconnect();

	public void setAllowAutoReconnect(boolean allowAutoReconnect);

}