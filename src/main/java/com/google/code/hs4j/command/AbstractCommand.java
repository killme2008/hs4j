package com.google.code.hs4j.command;

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
	protected static ByteBufferMatcher TERMIATER_MATCHER;
	static {
		TERMINATER.put(COMMAND_TERMINATE);
		TERMINATER.flip();
		TERMIATER_MATCHER = new ShiftAndByteBufferMatcher(TERMINATER);
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
			for (char c : token.toCharArray()) {
				if (c > 255) {
					buf.putChar(c);
				} else {
					buf.put((byte) c);

				}
			}
		}
	}

	protected void writeToken(IoBuffer buf, byte[] token) {
		if (token == null) {
			buf.put((byte) 0x00);
		} else {
			for (byte b : token) {
				buf.put(b);
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

	protected int length(String[] values) {
		if (values == null || values.length == 0) {
			return 0;
		}
		int result = 0;
		for (String value : values) {
			result += value.length();
		}
		return result;
	}

	private ParseState currentState;
	private int responseStatus;
	private int numColumns;

	public int getResponseStatus() {
		return this.responseStatus;
	}

	public int getNumColumns() {
		return this.numColumns;
	}

	static enum ParseState {
		HEAD, BODY, DONE
	}

	protected static void skipSeperator(IoBuffer buffer) {
		byte b = buffer.get();
		if (b != TOKEN_SEPARATOR) {
			throw new RuntimeException(
					"Decode error,expect sepeartor 0x09,but was " + b);
		}
	}

	public boolean decode(HandlerSocketSession session, IoBuffer buffer) {
		if (buffer.remaining() < 4) {
			return false;
		}
		if (this.currentState == null) {
			this.currentState = ParseState.HEAD;
		}
		while (true) {
			switch (this.currentState) {
			case HEAD:
				this.responseStatus = buffer.get() - 0x30;
				skipSeperator(buffer);
				this.numColumns = buffer.get() - 0x30;
				byte next = buffer.get();
				if (next == COMMAND_TERMINATE) {
					this.currentState = ParseState.DONE;
				} else {
					this.currentState = ParseState.BODY;
				}
				continue;
			case BODY:
				int index = TERMIATER_MATCHER.matchFirst(buffer);
				if (index > 0) {
					if (this.responseStatus == 0) {
						this.decodeBody(session, buffer, index);
					} else {
						byte[] data = new byte[index - buffer.position()];
						buffer.get(data);
						// skip terminator
						buffer.position(buffer.position() + 1);
						this.setExceptionMessage("Error message from server:"
								+ this.encodingString(data));
					}
					this.currentState = ParseState.DONE;
					continue;
				} else {
					return false;
				}
			case DONE:
				this.onDone();
				this.countDown();
				return true;
			}
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

	protected void decodeBody(HandlerSocketSession session, IoBuffer buffer,
			int index) {

	}

	protected void writeTokenSeparator(IoBuffer buf) {
		buf.put(TOKEN_SEPARATOR);
	}

	protected void writeCommandTerminate(IoBuffer buf) {
		buf.put(COMMAND_TERMINATE);
	}

}
