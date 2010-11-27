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

import com.google.code.hs4j.HSClient;
import com.google.code.hs4j.HSClientStateListener;
import com.google.code.hs4j.network.core.Controller;
import com.google.code.hs4j.network.core.ControllerStateListener;

/**
 * Adapte HandlerSocketClientStateListener to network ControllStateListener
 * 
 * @author dennis
 * 
 */
public class HandlerSocketClientStateListenerAdapter implements
		ControllerStateListener {
	private final HSClientStateListener handlerSocketClientStateListener;
	private final HSClient hsClient;

	public HandlerSocketClientStateListenerAdapter(
			HSClientStateListener handlerSocketClientStateListener,
			HSClient hsClient) {
		super();
		this.handlerSocketClientStateListener = handlerSocketClientStateListener;
		this.hsClient = hsClient;
	}

	public final HSClientStateListener getHandlerSocketClientStateListener() {
		return this.handlerSocketClientStateListener;
	}

	public HSClient getHSClient() {
		return this.hsClient;
	}

	public final void onAllSessionClosed(Controller acceptor) {

	}

	public final void onException(Controller acceptor, Throwable t) {
		this.handlerSocketClientStateListener.onException(this.hsClient, t);

	}

	public final void onReady(Controller acceptor) {

	}

	public final void onStarted(Controller acceptor) {
		this.handlerSocketClientStateListener.onStarted(this.hsClient);

	}

	public final void onStopped(Controller acceptor) {
		this.handlerSocketClientStateListener.onShutDown(this.hsClient);

	}

}
