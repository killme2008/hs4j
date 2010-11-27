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

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.code.hs4j.network.buffer.IoBuffer;
import com.google.code.hs4j.network.hs.HandlerSocketSession;

/**
 * A HandlerSocket protocol command
 * 
 * @author dennis
 * @date 2010-11-27
 */
public interface Command {
	public void setExceptionMessage(String t);

	public String getExceptionMessage();

	public Object getResult();

	public boolean await(long timeout, TimeUnit unit)
			throws InterruptedException;

	public void countDown();

	public IoBuffer getIoBuffer();

	public boolean decode(HandlerSocketSession session, IoBuffer buffer);

	public void encode();

	public Future<Boolean> getWriteFuture();

	public void setWriteFuture(Future<Boolean> future);

	public int getResponseStatus();

	public int getNumColumns();
}
