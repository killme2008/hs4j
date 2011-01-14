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
package com.google.code.hs4j.command.text;

import com.google.code.hs4j.network.buffer.IoBuffer;

/**
 * A insert command
 * 
 * @author dennis
 * @date 2010-11-27
 */
public class InsertCommand extends AbstractCommand {
	private final String id;
	private final byte[][] values;

	public InsertCommand(String id, byte[][] values) {
		super();
		this.id = id;
		this.values = values;
	}

	public void encode() {
		String valueLen = String.valueOf(this.values.length);
		int capacity = this.id.length() + 1 + OPERATOR_INSERT.length() + 1
				+ this.length(this.values) + this.values.length
				+ valueLen.length() + 2;
		IoBuffer buf = IoBuffer.allocate(capacity);
		buf.setAutoExpand(true);

		// id
		this.writeToken(buf, this.id);
		this.writeTokenSeparator(buf);
		// operator
		this.writeToken(buf, OPERATOR_INSERT);
		this.writeTokenSeparator(buf);
		// key nums

		this.writeToken(buf, valueLen);
		this.writeTokenSeparator(buf);
		for (int i = 0; i < this.values.length; i++) {
			this
					.writeToken(buf, this.values[i] == null ? null
							: this.values[i]);
			if (i == this.values.length - 1) {
				this.writeCommandTerminate(buf);
			} else {
				this.writeTokenSeparator(buf);
			}
		}
		buf.flip();
		this.buffer = buf;

	}

}
