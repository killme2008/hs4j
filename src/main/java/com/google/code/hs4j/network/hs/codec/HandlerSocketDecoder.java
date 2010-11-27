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
package com.google.code.hs4j.network.hs.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.hs4j.Command;
import com.google.code.hs4j.network.buffer.IoBuffer;
import com.google.code.hs4j.network.core.Session;
import com.google.code.hs4j.network.core.CodecFactory.Decoder;
import com.google.code.hs4j.network.hs.HandlerSocketSession;

/**
 * HandlerSocket protocol decoder
 * 
 * @author dennis
 * 
 */
public class HandlerSocketDecoder implements Decoder {

	public static final Logger log = LoggerFactory
			.getLogger(HandlerSocketDecoder.class);

	public HandlerSocketDecoder() {
		super();
	}

	public Object decode(IoBuffer buffer, Session origSession) {
		HandlerSocketSession session = (HandlerSocketSession) origSession;
		if (session.getCurrentCommand() != null) {
			return this.decode0(buffer, session);
		} else {
			session.takeCurrentCommand();
			return this.decode0(buffer, session);
		}
	}

	private Object decode0(IoBuffer buffer, HandlerSocketSession session) {
		if (session.getCurrentCommand().decode(session, buffer)) {
			final Command command = session.getCurrentCommand();
			session.setCurrentCommand(null);
			return command;
		}
		return null;
	}
}
