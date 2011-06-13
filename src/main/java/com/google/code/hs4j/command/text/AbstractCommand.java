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

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.code.hs4j.Command;
import com.google.code.hs4j.network.buffer.IoBuffer;
import com.google.code.hs4j.network.hs.HandlerSocketSession;
import com.google.code.hs4j.network.util.ByteBufferMatcher;
import com.google.code.hs4j.network.util.ShiftAndByteBufferMatcher;

public abstract class AbstractCommand implements Command {
	public static final byte TOKEN_SEPARATOR = 0x09;
	public static final byte COMMAND_TERMINATE = 0x0a;

	public static final String OPERATOR_OPEN_INDEX = "P";
	public static final String OPERATOR_INSERT = "+";
	public static final String OPERATOR_UPDATE = "U";
	public static final String OPERATOR_DELETE = "D";

	protected static IoBuffer TERMINATER = IoBuffer.allocate(1);
	protected static IoBuffer SEPERATOR = IoBuffer.allocate(1);
	protected static ByteBufferMatcher TERMIATER_MATCHER;
	protected static ByteBufferMatcher SEPERATOR_MATCHER;
	static {
		TERMINATER.put(COMMAND_TERMINATE);
		TERMINATER.flip();
		SEPERATOR.put(TOKEN_SEPARATOR);
		SEPERATOR.flip();
		TERMIATER_MATCHER = new ShiftAndByteBufferMatcher(TERMINATER);
		SEPERATOR_MATCHER = new ShiftAndByteBufferMatcher(SEPERATOR);
	}

	public static final String DEFAULT_ENCODING = "UTF-8";
	protected String encoding = DEFAULT_ENCODING;

	private final CountDownLatch latch;
	protected IoBuffer buffer;
	protected Object result;
	private Future<Boolean> writeFuture;
	private String exceptionMsg;

	public AbstractCommand() {
		super();
		this.latch = new CountDownLatch(1);
	}

	public boolean await(long timeout, TimeUnit unit)
			throws InterruptedException {
		return this.latch.await(timeout, unit);
	}

	public void countDown() {
		this.latch.countDown();

	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public IoBuffer getIoBuffer() {
		return this.buffer;
	}

	public String getExceptionMessage() {
		return this.exceptionMsg;
	}

	public void setExceptionMessage(String t) {
		this.exceptionMsg = t;

	}

	public Object getResult() {
		return this.result;
	}

	public Future<Boolean> getWriteFuture() {
		return this.writeFuture;
	}

	public void setWriteFuture(Future<Boolean> future) {
		this.writeFuture = future;

	}

	protected void writeToken(IoBuffer buf, String token) {
		if (token == null) {
			buf.put((byte) 0x00);
		} else {
			byte[] bytes = decodeString(token);
			writeToken(buf, bytes);
		}
	}

	protected void writeToken(IoBuffer buf, byte[] token) {
		if (token == null) {
			buf.put((byte) 0x00);
		} else {
			for (byte b : token) {
				if (b >= 0 && b <= 0x0f) {
					buf.put((byte) 0x01);
					buf.put((byte) (b | 0x40));
				} else {
					buf.put(b);
				}
			}
		}
	}

	protected static String join(String[] values) {
		return join(values, ",");
	}

	protected static String join(String[] values, String split) {
		if (values == null || values.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		boolean wasFirst = true;
		for (String value : values) {
			if (wasFirst) {
				sb.append(value);
				wasFirst = false;
			} else {
				sb.append(split).append(value);
			}
		}
		return sb.toString();
	}

	protected int length(byte[][] values) {
		if (values == null || values.length == 0) {
			return 0;
		}
		int result = 0;
		for (byte[] value : values) {
			if (value != null) {
				result += value.length;
			}
		}
		return result;
	}


	private ParseState currentState;
	private int responseStatus;
	private int numColumns;
	private final StringBuilder numColumnsAppender = new StringBuilder(5);
	private byte[] body;

	public int getResponseStatus() {
		return this.responseStatus;
	}

	public int getNumColumns() {
		return this.numColumns;
	}

	static enum ParseState {
		STATUS, NUMCOLUMNS, BODY, DONE
	}

	protected static void skipSeperator(IoBuffer buffer) {
		byte b = buffer.get();
		if (b != TOKEN_SEPARATOR) {
			throw new RuntimeException(
					"Decode error,expect sepeartor 0x09,but was " + b);
		}
	}

	public boolean decode(HandlerSocketSession session, IoBuffer buffer) {
		if (this.currentState == null) {
			this.currentState = ParseState.STATUS;
		}
		LABEL: while (true) {
			switch (this.currentState) {
			case STATUS:
				if (buffer.remaining() < 2) {
					return false;
				}
				this.responseStatus = buffer.get() - 0x30;
				skipSeperator(buffer);
				this.currentState = ParseState.NUMCOLUMNS;
				continue;
			case NUMCOLUMNS:
				if (!buffer.hasRemaining()) {
					return false;
				}
				int remaining = buffer.remaining();
				for (int i = 0; i < remaining; i++) {
					byte b = buffer.get();
					if (b == COMMAND_TERMINATE) {
						this.numColumns = Integer
								.parseInt(this.numColumnsAppender.toString());
						this.currentState = ParseState.DONE;
						continue LABEL;
					} else if (b == TOKEN_SEPARATOR) {
						this.numColumns = Integer
								.parseInt(this.numColumnsAppender.toString());
						this.currentState = ParseState.BODY;
						continue LABEL;
					} else {
						this.numColumnsAppender.append(b - 0x30);
					}
				}
				return false;
			case BODY:
				if (!buffer.hasRemaining()) {
					return false;
				}
				int index = TERMIATER_MATCHER.matchFirst(buffer);
				if (index > 0) {
					if (this.responseStatus == 0) {
						this.copyDataFromBufferToBody(buffer, index
								- buffer.position() + 1);
						this.decodeBody(session, this.body, index);
					} else {
						this.copyDataFromBufferToBody(buffer, index
								- buffer.position());
						// skip terminator
						buffer.position(buffer.position() + 1);
						this.setExceptionMessage("Error message from server:"
								+ this.encodingString(this.body));
					}
					this.currentState = ParseState.DONE;
					continue;
				} else {
					if (buffer.hasRemaining()) {
						this.copyDataFromBufferToBody(buffer, buffer
								.remaining());
					}
					return false;
				}
			case DONE:
				this.onDone();
				this.countDown();
				return true;
			}
		}

	}

	private void copyDataFromBufferToBody(IoBuffer buffer, int length) {
		if (this.body == null) {
			this.body = new byte[length];
			buffer.get(this.body);
		} else {
			int oldLen = this.body.length;
			byte[] newBody = new byte[oldLen + length];
			// copy body to new body
			System.arraycopy(this.body, 0, newBody, 0, oldLen);
			this.body = newBody;
			buffer.get(this.body, oldLen, length);
		}
	}

	protected void onDone() {

	}

	public String encodingString(byte[] data) {
		try {
			return new String(data, this.encoding);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unsupported encoding:" + this.encoding,
					e);
		}
	}

	public byte[] decodeString(String s) {
		try {
			return s.getBytes(this.encoding);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unsupported encoding:" + this.encoding,
					e);
		}
	}

	protected void decodeBody(HandlerSocketSession session, byte[] body,
			int index) {

	}

	CountDownLatch getLatch() {
		return this.latch;
	}

	ParseState getCurrentState() {
		return this.currentState;
	}

	protected void writeTokenSeparator(IoBuffer buf) {
		buf.put(TOKEN_SEPARATOR);
	}

	protected void writeCommandTerminate(IoBuffer buf) {
		buf.put(COMMAND_TERMINATE);
	}

	@Override
	public String toString(){
		if(buffer !=null){
			return String.valueOf(buffer.array());
		}
		return super.toString();
	}
}
