/**
 *Copyright [2009-2010] [dennis zhuang(killme2008@gmail.com)]
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
import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import com.google.code.hs4j.Command;
import com.google.code.hs4j.CommandFactory;
import com.google.code.hs4j.network.core.WriteMessage;
import com.google.code.hs4j.network.nio.NioSessionConfig;
import com.google.code.hs4j.network.nio.impl.NioTCPSession;
import com.google.code.hs4j.network.util.LinkedTransferQueue;
import com.google.code.hs4j.network.util.SystemUtils;

/**
 * HandlerSokcetSession implementation
 * 
 * @author dennis
 */
public class HandlerSocketSessionImpl extends NioTCPSession implements
		HandlerSocketSession {

	/**
	 * Command which are already sent
	 */
	protected BlockingQueue<Command> commandAlreadySent;

	private final AtomicReference<Command> currentCommand = new AtomicReference<Command>();

	private SocketAddress remoteSocketAddress; // prevent channel is closed

	private volatile boolean allowReconnect = true;

	public HandlerSocketSessionImpl(NioSessionConfig sessionConfig,
			int readRecvBufferSize, int readThreadCount,
			CommandFactory commandFactory) {
		super(sessionConfig, readRecvBufferSize);
		this.commandAlreadySent = new LinkedTransferQueue<Command>();
	}

	@Override
	public String toString() {
		return SystemUtils.getRawAddress(this.getRemoteSocketAddress()) + ":"
				+ this.getRemoteSocketAddress().getPort();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.hs4j.network.hs.HandlerSocket#destroy()
	 */
	public void destroy() {
		Command command = this.currentCommand.get();
		if (command != null) {
			command.setExceptionMessage("Connection has been closed");
			command.countDown();
		}
		while ((command = this.commandAlreadySent.poll()) != null) {
			command.setExceptionMessage("Connection has been closed");
			command.countDown();
		}

	}

	@Override
	public InetSocketAddress getRemoteSocketAddress() {
		InetSocketAddress result = super.getRemoteSocketAddress();
		if (result == null && this.remoteSocketAddress != null) {
			result = (InetSocketAddress) this.remoteSocketAddress;
		}
		return result;
	}

	@Override
	protected final WriteMessage wrapMessage(Object msg,
			Future<Boolean> writeFuture) {
		((Command) msg).encode();
		((Command) msg).setWriteFuture(writeFuture);
		if (log.isDebugEnabled()) {
			log.debug("After encoding" + ((Command) msg).toString());
		}
		return super.wrapMessage(msg, writeFuture);
	}

	/**
	 * get current command from queue
	 * 
	 * @return
	 */
	private final Command takeExecutingCommand() {
		try {
			return this.commandAlreadySent.take();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.hs4j.network.hs.HandlerSocket#isAllowReconnect()
	 */
	public boolean isAllowReconnect() {
		return this.allowReconnect;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.hs4j.network.hs.HandlerSocket#setAllowReconnect(boolean)
	 */
	public void setAllowReconnect(boolean reconnected) {
		this.allowReconnect = reconnected;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.hs4j.network.hs.HandlerSocket#addCommand(com.google.code
	 * .hs4j.Command)
	 */
	public final void addCommand(Command command) {
		this.commandAlreadySent.add(command);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.code.hs4j.network.hs.HandlerSocket#setCurrentCommand(com.google
	 * .code.hs4j.Command)
	 */
	public final void setCurrentCommand(Command cmd) {
		this.currentCommand.set(cmd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.hs4j.network.hs.HandlerSocket#getCurrentCommand()
	 */
	public final Command getCurrentCommand() {
		return this.currentCommand.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.code.hs4j.network.hs.HandlerSocket#takeCurrentCommand()
	 */
	public final void takeCurrentCommand() {
		this.setCurrentCommand(this.takeExecutingCommand());
	}
}
