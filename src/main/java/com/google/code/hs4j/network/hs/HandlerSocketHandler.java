/**
 *Copyright [2010-2011] [dennis zhuang(killme2008@gmail.com)]
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License. 
 *You may obtain a copy of the License at 
 *             http://www.apache.org/licenses/LICENSE-2.0 
 *Unless required by applicable law or agreed to in writing, 
 *software distributed under the License is distributed on an "AS IS" BASIS, 
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 *either express or implied. See the License for the specific language governing permissions and limitations under the License
 */
package com.google.code.hs4j.network.hs;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.hs4j.Command;
import com.google.code.hs4j.HSClientStateListener;
import com.google.code.hs4j.impl.HSClientImpl;
import com.google.code.hs4j.impl.ReconnectRequest;
import com.google.code.hs4j.network.core.Session;
import com.google.code.hs4j.network.core.impl.HandlerAdapter;

/**
 * HandlerSocket io event handler
 * 
 * @author dennis
 * @date 2010-11-27
 */
public class HandlerSocketHandler extends HandlerAdapter {
	static final Logger log = LoggerFactory
			.getLogger(HandlerSocketHandler.class);

	private final HSClientImpl hsClient;

	public HandlerSocketHandler(HSClientImpl hsClient) {
		super();
		this.hsClient = hsClient;
	}

	/**
	 * put command which have been sent to queue
	 */
	@Override
	public final void onMessageSent(Session session, Object msg) {
		Command command = (Command) msg;
		((HandlerSocketSession) session).addCommand(command);

	}

	@Override
	public void onExceptionCaught(Session session, Throwable throwable) {
		log.error("hs4j network layout exception", throwable);
	}

	/**
	 * Check if have to reconnect on session closed
	 */
	@Override
	public final void onSessionClosed(Session session) {
		this.hsClient.getConnector().removeSession(session);
		HandlerSocketSession hSession = (HandlerSocketSession) session;

		hSession.destroy();
		if (this.hsClient.getConnector().isStarted()
				&& hSession.isAllowReconnect()) {
			this.reconnect(session);
		}
		for (HSClientStateListener listener : this.hsClient
				.getHSClientStateListeners()) {
			listener.onDisconnected(this.hsClient, session
					.getRemoteSocketAddress());
		}
	}

	/**
	 * Auto reconect request to hs4j server
	 * 
	 * @param session
	 */
	protected void reconnect(Session session) {
		if (this.hsClient.isStarted()) {
			if (log.isDebugEnabled()) {
				log.debug("Add reconnectRequest to connector "
						+ session.getRemoteSocketAddress());
			}
			HandlerSocketSession hSession = (HandlerSocketSession) session;
			InetSocketAddress addr = hSession.getRemoteSocketAddress();
			this.hsClient.getConnector().addToWatingQueue(
					new ReconnectRequest(addr, 0, this.hsClient
							.getHealConnectionInterval()));
		}
	}
}
