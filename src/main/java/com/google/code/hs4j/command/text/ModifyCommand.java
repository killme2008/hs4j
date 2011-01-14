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

import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.network.buffer.IoBuffer;
import com.google.code.hs4j.network.hs.HandlerSocketSession;
import com.google.code.hs4j.utils.HSUtils;

/**
 * A find modify command
 * 
 * @author dennis
 * @date 2010-11-27
 */
public class ModifyCommand extends AbstractCommand {
	private final String id;
	private final String operator;
	private final String[] keys;
	private final byte[][] values;
	private final String modOperation;
	private final int limit;
	private final int offset;

	public ModifyCommand(String id, FindOperator operator, String[] keys,
			byte[][] values, int limit, int offset, String modOperation) {
		super();
		this.id = id;
		this.operator = operator.getValue();
		this.values = values;
		this.limit = limit;
		this.offset = offset;
		this.modOperation = modOperation;
		this.keys = keys;
	}

	@Override
	public void decodeBody(HandlerSocketSession session, byte[] data, int index) {
		this.result = data[0] - 0x30;
	}

	public void encode() {
		byte[][] keyBytes = HSUtils.getByteArrayFromStringArray(keys,
				this.encoding);
		String limitStr = String.valueOf(this.limit);
		String offsetStr = String.valueOf(this.offset);
		String kenLen = String.valueOf(this.keys.length);

		int capacity = this.id.length() + 1 + this.operator.length() + 1
				+ this.length(this.values) + this.length(keyBytes)
				+ this.values.length + 1 + this.keys.length + limitStr.length()
				+ 1 + kenLen.length() + 1 + offsetStr.length() + 2;
		IoBuffer buf = IoBuffer.allocate(capacity);
		buf.setAutoExpand(true);

		// id
		this.writeToken(buf, this.id);
		this.writeTokenSeparator(buf);
		// operator
		this.writeToken(buf, this.operator);
		this.writeTokenSeparator(buf);
		// key nums
		this.writeToken(buf, kenLen);
		this.writeTokenSeparator(buf);
		for (byte[] data : keyBytes) {
			this.writeToken(buf, data);
			this.writeTokenSeparator(buf);
		}
		// limit
		this.writeToken(buf, limitStr);
		this.writeTokenSeparator(buf);
		// offset
		this.writeToken(buf, offsetStr);
		this.writeTokenSeparator(buf);
		// modify operator
		this.writeToken(buf, this.modOperation);
		this.writeTokenSeparator(buf);

		// modify values

		for (int i = 0; i < this.values.length; i++) {
			this.writeToken(buf, this.values[i]);
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
