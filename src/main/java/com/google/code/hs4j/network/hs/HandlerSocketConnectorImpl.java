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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.code.hs4j.Command;
import com.google.code.hs4j.CommandFactory;
import com.google.code.hs4j.HSClient;
import com.google.code.hs4j.Protocol;
import com.google.code.hs4j.exception.HandlerSocketException;
import com.google.code.hs4j.impl.HSClientImpl;
import com.google.code.hs4j.impl.IndexRecord;
import com.google.code.hs4j.impl.ReconnectRequest;
import com.google.code.hs4j.network.config.Configuration;
import com.google.code.hs4j.network.core.Controller;
import com.google.code.hs4j.network.core.ControllerStateListener;
import com.google.code.hs4j.network.core.EventType;
import com.google.code.hs4j.network.core.Session;
import com.google.code.hs4j.network.core.WriteMessage;
import com.google.code.hs4j.network.nio.NioSession;
import com.google.code.hs4j.network.nio.NioSessionConfig;
import com.google.code.hs4j.network.nio.impl.SocketChannelController;
import com.google.code.hs4j.network.util.SystemUtils;

/**
 * Connector implementation
 * 
 * @author dennis
 */
public class HandlerSocketConnectorImpl extends SocketChannelController
		implements HandlerSocketConnector {

	private final DelayQueue<ReconnectRequest> waitingQueue = new DelayQueue<ReconnectRequest>();

	private final ExecutorService openIndexExectur = Executors
			.newCachedThreadPool();

	private volatile long healSessionInterval = 2000L;
	private final int connectionPoolSize; // session pool size
	protected Protocol protocol;

	private volatile boolean allowAutoReconnect = true;

	private final CommandFactory commandFactory;

	private final HSClientImpl hsClient;

	/**
	 * When session was created,it must be added to sessionList,but before
	 * that,the opening index must reopen on this session.
	 * 
	 * @author dennis
	 * @date 2010-11-28
	 */
	private final class AddSessionTask implements Runnable {
		private final HandlerSocketSessionImpl session;

		private AddSessionTask(HandlerSocketSessionImpl session) {
			this.session = session;
		}

		public void run() {
			try {
				Map<Integer, IndexRecord> indexMap = HandlerSocketConnectorImpl.this.hsClient
						.getIndexMap();
				if (!indexMap.isEmpty()) {
					for (IndexRecord record : indexMap.values()) {
						try {
							Command cmd = commandFactory
									.createOpenIndexCommand(String
											.valueOf(record.id), record.db,
											record.tableName, record.indexName,
											record.fieldList, record.filterFieldList);
							session.write(cmd);
							cmd.await(3000, TimeUnit.MILLISECONDS);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						} catch (Exception e) {
							log
									.error(
											"Open  index failure when connection was connected",
											e);
						}
					}
				}
			} finally {
				HandlerSocketConnectorImpl.this.addSession(this.session);
			}

		}
	}

	/**
	 * Session monitor for healing sessions.
	 * 
	 * @author dennis
	 * 
	 */
	class SessionMonitor extends Thread {
		public SessionMonitor() {
			this.setName("Heal-Session-Thread");
		}

		@Override
		public void run() {
			while (HandlerSocketConnectorImpl.this.isStarted()) {

				try {
					ReconnectRequest request = HandlerSocketConnectorImpl.this.waitingQueue
							.take();

					InetSocketAddress address = request.getRemoteAddr();

					boolean connected = false;
					Future<Boolean> future = HandlerSocketConnectorImpl.this
							.connect(request.getRemoteAddr());
					request.setTries(request.getTries() + 1);
					try {
						log.warn("Trying to connect to "
								+ address.getAddress().getHostAddress() + ":"
								+ address.getPort() + " for "
								+ request.getTries() + " times");
						if (!future.get(HSClient.DEFAULT_CONNECT_TIMEOUT,
								TimeUnit.MILLISECONDS)) {
							connected = false;
						} else {
							connected = true;
							break;
						}
					} catch (TimeoutException e) {
						future.cancel(true);
					} catch (ExecutionException e) {
						future.cancel(true);
					} finally {
						if (!connected) {
							// update timestamp for next reconnecting
							request
									.updateNextReconnectTimeStamp(HandlerSocketConnectorImpl.this.healSessionInterval
											* request.getTries());
							log.error("Reconnect to "
									+ address.getAddress().getHostAddress()
									+ ":" + address.getPort() + " fail");
							// add to tail
							HandlerSocketConnectorImpl.this.waitingQueue
									.offer(request);
						} else {
							continue;
						}
					}

				} catch (InterruptedException e) {
					// ignore,check status
				} catch (Exception e) {
					log.error("SessionMonitor connect error", e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.hs4j.network.hs.HandlerCOnnector#setHealSessionInterval
	 * (long)
	 */
	public final void setHealSessionInterval(long healConnectionInterval) {
		this.healSessionInterval = healConnectionInterval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.hs4j.network.hs.HandlerCOnnector#getHealSessionInterval()
	 */
	public long getHealSessionInterval() {
		return this.healSessionInterval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.hs4j.network.hs.HandlerCOnnector#getProtocol()
	 */
	public Protocol getProtocol() {
		return this.protocol;
	}

	protected final CopyOnWriteArrayList<Session> sessionList = new CopyOnWriteArrayList<Session>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.hs4j.network.hs.HandlerCOnnector#addSession(com.google
	 * .code.hs4j.network.core.Session)
	 */
	public void addSession(Session session) {
		InetSocketAddress remoteSocketAddress = session
				.getRemoteSocketAddress();
		log.warn("Add a session: "
				+ SystemUtils.getRawAddress(remoteSocketAddress) + ":"
				+ remoteSocketAddress.getPort());
		this.sessionList.add(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.hs4j.network.hs.HandlerCOnnector#removeSession(com.google
	 * .code.hs4j.network.core.Session)
	 */
	public void removeSession(Session session) {
		InetSocketAddress remoteSocketAddress = session
				.getRemoteSocketAddress();
		log.warn("Remove a session: "
				+ SystemUtils.getRawAddress(remoteSocketAddress) + ":"
				+ remoteSocketAddress.getPort());
		this.sessionList.remove(session);
	}

	@Override
	protected void doStart() throws IOException {
		this.setLocalSocketAddress(new InetSocketAddress("localhost", 0));
	}

	@Override
	public void onConnect(SelectionKey key) throws IOException {
		key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT);
		ConnectFuture future = (ConnectFuture) key.attachment();
		if (future == null || future.isCancelled()) {
			key.channel().close();
			key.cancel();
			return;
		}
		try {
			if (!((SocketChannel) key.channel()).finishConnect()) {
				future.failure(new IOException("Connect to "
						+ SystemUtils.getRawAddress(future.getRemoteAddr())
						+ ":" + future.getRemoteAddr().getPort() + " fail"));
			} else {
				key.attach(null);
				this.createSession((SocketChannel) key.channel());
				future.setResult(Boolean.TRUE);
			}
		} catch (Exception e) {
			future.failure(e);
			key.cancel();
			throw new IOException("Connect to "
					+ SystemUtils.getRawAddress(future.getRemoteAddr()) + ":"
					+ future.getRemoteAddr().getPort() + " fail,"
					+ e.getMessage());
		}
	}

	protected HandlerSocketSessionImpl createSession(SocketChannel socketChannel) {
		final HandlerSocketSessionImpl session = (HandlerSocketSessionImpl) this
				.buildSession(socketChannel);
		this.selectorManager.registerSession(session, EventType.ENABLE_READ);
		session.start();
		session.onEvent(EventType.CONNECTED, null);
		this.hsClient.notifyConnected(session);
		this.openIndexExectur.execute(new AddSessionTask(session));
		return session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.hs4j.network.hs.HandlerCOnnector#addToWatingQueue(com
	 * .google.code.hs4j.impl.ReconnectRequest)
	 */
	public void addToWatingQueue(ReconnectRequest request) {
		this.waitingQueue.add(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.google.code.hs4j.network.hs.HandlerCOnnector#connect(java.net.
	 * InetSocketAddress)
	 */
	public Future<Boolean> connect(InetSocketAddress remoteAddr)
			throws IOException {
		if (remoteAddr == null) {
			throw new NullPointerException("Null Address");
		}
		SocketChannel socketChannel = SocketChannel.open();
		this.configureSocketChannel(socketChannel);
		ConnectFuture future = new ConnectFuture(remoteAddr);
		if (!socketChannel.connect(remoteAddr)) {
			this.selectorManager.registerChannel(socketChannel,
					SelectionKey.OP_CONNECT, future);
		} else {
			this.createSession(socketChannel);
			future.setResult(true);
		}
		return future;
	}

	public void closeChannel(Selector selector) throws IOException {
		// do nothing
	}

	private final AtomicInteger sets = new AtomicInteger();

	public Session selectSession() throws HandlerSocketException {
		Session session = this.sessionList.get(this.sets.incrementAndGet()
				% this.sessionList.size());
		int retryCount = 0;
		while ((session == null || session.isClosed()) && retryCount++ < 6) {
			session = this.sessionList.get(this.sets.incrementAndGet()
					% this.sessionList.size());
		}
		if (session == null || session.isClosed()) {
			throw new HandlerSocketException(
					"Could not find an open connection");
		}
		return session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.hs4j.network.hs.HandlerCOnnector#getSessionList()
	 */
	public CopyOnWriteArrayList<Session> getSessionList() {
		return this.sessionList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.hs4j.network.hs.HandlerCOnnector#send(com.google.code
	 * .hs4j.Command)
	 */
	public void send(final Command msg) throws HandlerSocketException {
		Session session = this.selectSession();
		session.write(msg);
	}

	/**
	 * Inner state listenner,manage session monitor.
	 * 
	 * @author boyan
	 * 
	 */
	class InnerControllerStateListener implements ControllerStateListener {
		private final SessionMonitor sessionMonitor = new SessionMonitor();

		public void onAllSessionClosed(Controller controller) {

		}

		public void onException(Controller controller, Throwable t) {
			log.error("Exception occured in controller", t);
		}

		public void onReady(Controller controller) {
			this.sessionMonitor.start();
		}

		public void onStarted(Controller controller) {

		}

		public void onStopped(Controller controller) {
			this.sessionMonitor.interrupt();
			HandlerSocketConnectorImpl.this.openIndexExectur.shutdown();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.hs4j.network.hs.HandlerCOnnector#isAllowAutoReconnect()
	 */
	public boolean isAllowAutoReconnect() {
		return this.allowAutoReconnect;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.hs4j.network.hs.HandlerCOnnector#setAllowAutoReconnect
	 * (boolean)
	 */
	public void setAllowAutoReconnect(boolean allowAutoReconnect) {
		this.allowAutoReconnect = allowAutoReconnect;
	}

	public HandlerSocketConnectorImpl(Configuration configuration,
			CommandFactory commandFactory, int poolSize, HSClientImpl hsClient) {
		super(configuration, null);
		this.protocol = commandFactory.getProtocol();
		this.addStateListener(new InnerControllerStateListener());
		this.connectionPoolSize = poolSize;
		this.soLingerOn = true;
		this.commandFactory = commandFactory;
		this.hsClient = hsClient;
		this
				.setSelectorPoolSize(2 * Runtime.getRuntime()
						.availableProcessors());
	}

	@Override
	protected NioSession buildSession(SocketChannel sc) {
		Queue<WriteMessage> queue = this.buildQueue();
		final NioSessionConfig sessionCofig = this
				.buildSessionConfig(sc, queue);
		HandlerSocketSessionImpl session = new HandlerSocketSessionImpl(
				sessionCofig, this.configuration.getSessionReadBufferSize(),
				this.getReadThreadCount(), this.commandFactory);
		session.setAllowReconnect(this.allowAutoReconnect);
		return session;
	}

}