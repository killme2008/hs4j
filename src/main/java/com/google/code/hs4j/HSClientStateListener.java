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
package com.google.code.hs4j;

import java.net.InetSocketAddress;

/**
 * HSClient state listener.When client startup,shutdown,connected to a
 * HandlerSocket server or disconnected happened,client will notify the listener
 * instance which implemented this interface.Please don't do any operations
 * which may block in these callback methods.
 * 
 * @author dennis
 * 
 */
public interface HSClientStateListener {
	/**
	 * After client is started.
	 * 
	 * @param client
	 */
	public void onStarted(HSClient client);

	/**
	 * After client is shutdown.
	 * 
	 * @param client
	 */
	public void onShutDown(HSClient client);

	/**
	 * After a server is connected,don't do any operations may block here.
	 * 
	 * @param client
	 * @param inetSocketAddress
	 */
	public void onConnected(HSClient client, InetSocketAddress inetSocketAddress);

	/**
	 * After a server is disconnected,don't do any operations may block here.
	 * 
	 * @param client
	 * @param inetSocketAddress
	 */
	public void onDisconnected(HSClient client,
			InetSocketAddress inetSocketAddress);

	/**
	 * When exceptions occur
	 * 
	 * @param client
	 * @param throwable
	 */
	public void onException(HSClient client, Throwable throwable);
}
