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

import com.google.code.hs4j.network.core.CodecFactory;

/**
 * HandlerSocket protocol codec factory
 * 
 * @author dennis
 * 
 * @param <Command>
 */
public class HandlerSocketCodecFactory implements CodecFactory {

	private final HandlerSocketEncoder encoder;

	private final HandlerSocketDecoder decoder;

	public HandlerSocketCodecFactory() {
		super();
		this.encoder = new HandlerSocketEncoder();
		this.decoder = new HandlerSocketDecoder();
	}

	/**
	 * return the hs4j protocol decoder
	 */

	public final CodecFactory.Decoder getDecoder() {
		return this.decoder;

	}

	/**
	 * return the hs4j protocol encoder
	 */

	public final CodecFactory.Encoder getEncoder() {
		return this.encoder;
	}
}
