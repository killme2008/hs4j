package com.google.code.hs4j.network.hs;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.google.code.hs4j.Command;
import com.google.code.hs4j.command.text.InsertCommand;
import com.google.code.hs4j.impl.HSClientImpl;

public class HandlerSocketHandlerUnitTest {
	private HandlerSocketHandler handler;
	private HSClientImpl hsClient;

	@Before
	public void setUp() {
		this.hsClient = EasyMock.createMock(HSClientImpl.class);
		this.handler = new HandlerSocketHandler(this.hsClient);
	}

	@Test
	public void testOnMessageSent() {
		Command cmd = new InsertCommand("1", null);
		HandlerSocketSession session = EasyMock
				.createMock(HandlerSocketSession.class);
		session.addCommand(cmd);
		EasyMock.expectLastCall();
		EasyMock.replay(session);
		this.handler.onMessageSent(session, cmd);
		EasyMock.verify(session);
	}

	// @Test
	// public void testOnSessionClosed() {
	// HandlerSocketConnector connector = EasyMock
	// .createMock(HandlerSocketConnector.class);
	// EasyMock.expect(this.hsClient.getHealConnectionInterval()).andReturn(2000L);
	// EasyMock.expect(this.hsClient.getConnector()).andReturn(connector)
	// .times(3);
	// EasyMock.expect(this.hsClient.isStarted()).andReturn(true);
	// EasyMock.expect(this.hsClient.getHSClientStateListeners()).andReturn(
	// new CopyOnWriteArrayList<HSClientStateListener>());
	// EasyMock.expect(connector.isStarted()).andReturn(true);
	// HandlerSocketSession session = EasyMock
	// .createMock(HandlerSocketSession.class);
	// session.destroy();
	// EasyMock.expectLastCall();
	// EasyMock.expect(session.isAllowReconnect()).andReturn(true);
	// InetSocketAddress inetSocketAddr = new InetSocketAddress(9999);
	// EasyMock.expect(session.getRemoteSocketAddress()).andReturn(
	// inetSocketAddr).times(2);
	// connector
	// .addToWatingQueue(new ReconnectRequest(inetSocketAddr, 0, 2000));
	// EasyMock.expectLastCall();
	//
	// connector.removeSession(session);
	// EasyMock.expectLastCall();
	//
	// EasyMock.replay(this.hsClient, session, connector);
	// this.handler.onSessionClosed(session);
	// EasyMock.verify(session, connector, this.hsClient);
	// }
}
