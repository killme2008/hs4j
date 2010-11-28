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
	private final String[] values;

	public InsertCommand(String id, String[] values) {
		super();
		this.id = id;
		this.values = values;
	}

	public void encode() {
		IoBuffer buf = IoBuffer.allocate(this.id.length() + 1 + 2
				+ this.length(this.values) + this.values.length + 1 + 10);

		// id
		this.writeToken(buf, this.id);
		this.writeTokenSeparator(buf);
		// operator
		this.writeToken(buf, OPERATOR_INSERT);
		this.writeTokenSeparator(buf);
		// key nums
		this.writeToken(buf, String.valueOf(this.values.length));
		this.writeTokenSeparator(buf);
		try {
			for (int i = 0; i < this.values.length; i++) {
				this.writeToken(buf, this.values[i] == null ? null
						: this.values[i].getBytes(this.encoding));
				if (i == this.values.length - 1) {
					this.writeCommandTerminate(buf);
				} else {
					this.writeTokenSeparator(buf);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Encoding InsertCommand error", e);
		}

		buf.flip();
		//System.out.println(Arrays.toString(buf.array()));
		this.buffer = buf;

	}

}
