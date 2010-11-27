package com.google.code.hs4j.command.text;

import java.io.UnsupportedEncodingException;

import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.command.AbstractCommand;
import com.google.code.hs4j.network.buffer.IoBuffer;
import com.google.code.hs4j.network.hs.HandlerSocketSession;

/**
 * A find modify command
 * 
 * @author dennis
 * @date 2010-11-27
 */
public class ModifyCommand extends AbstractCommand {
	private final String id;
	private final String operator;
	private final String[] values;
	private final String modOperation;
	private final String[] fieldList;
	private final int limit;
	private final int offset;

	public ModifyCommand(String id, FindOperator operator, String[] values,
			int limit, int offset, String[] fieldList, String modOperation) {
		super();
		this.id = id;
		this.operator = operator.getValue();
		this.values = values;
		this.limit = limit;
		this.offset = offset;
		this.fieldList = fieldList;
		this.modOperation = modOperation;
	}

	@Override
	public void decodeBody(HandlerSocketSession session, IoBuffer buffer,
			int index) {
		byte[] data = new byte[index - buffer.position()];
		buffer.get(data);
		buffer.position(buffer.position() + 1);
		this.result = data[0]-0x30;
	}

	public void encode() {
		IoBuffer buf = IoBuffer.allocate(this.id.length() + 1
				+ this.operator.length() + 1 + this.length(this.values)
				+ this.values.length + 1 + 10);
		buf.setAutoExpand(true);

		// id
		this.writeToken(buf, this.id);
		this.writeTokenSeparator(buf);
		// operator
		this.writeToken(buf, this.operator);
		this.writeTokenSeparator(buf);
		// key nums
		this.writeToken(buf, String.valueOf(this.values.length));
		this.writeTokenSeparator(buf);
		for (String key : this.values) {
			this.writeToken(buf, key == null ? null : this.getBytes(key));
			this.writeTokenSeparator(buf);
		}
		// limit
		this.writeToken(buf, String.valueOf(this.limit));
		this.writeTokenSeparator(buf);
		// offset
		this.writeToken(buf, String.valueOf(this.offset));
		this.writeTokenSeparator(buf);
		// modify operator
		this.writeToken(buf, this.modOperation);
		this.writeTokenSeparator(buf);

		// modify values

		for (int i = 0; i < this.values.length; i++) {
			this.writeToken(buf, this.values[i] == null ? null : this
					.getBytes(this.values[i]));
			if (i == this.values.length - 1) {
				this.writeCommandTerminate(buf);
			} else {
				this.writeTokenSeparator(buf);
			}
		}

		buf.flip();
		this.buffer = buf;
	}

	private byte[] getBytes(String key) {
		try {
			return key.getBytes(this.encoding);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unsupported encoding :" + this.encoding);
		}
	}

}
