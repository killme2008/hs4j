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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.code.hs4j.Filter;
import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.impl.ResultSetImpl;
import com.google.code.hs4j.network.buffer.IoBuffer;
import com.google.code.hs4j.network.hs.HandlerSocketSession;
import com.google.code.hs4j.utils.HSUtils;

/**
 * A find command
 * 
 * @author dennis
 * @date 2010-11-27
 */
public class FindCommand extends AbstractCommand {
	private static final Filter[] EMPTY_FILTER = new Filter[0];
	private final String id;
	private final String operator;
	private final String[] keys;
	private final int limit;
	private final int offset;
	private final String[] fieldList;
	private Filter[] filters;

	public FindCommand(String id, FindOperator operator, String[] keys,
			   int limit, int offset, String[] fieldList){
		this(id, operator, keys, limit, offset, fieldList, null);
	}
	public FindCommand(String id, FindOperator operator, String[] keys,
					   int limit, int offset, String[] fieldList, Filter[] filters) {
		super();
		this.id = id;
		this.operator = operator.getValue();
		this.keys = keys;
		this.limit = limit;
		this.offset = offset;
		this.fieldList = fieldList;
		if(filters !=null)
			this.filters = filters;
		else
			this.filters = EMPTY_FILTER;
	}

	@Override
	protected void onDone() {
		if (this.result == null) {
			this.result = new ResultSetImpl(Collections
					.<List<byte[]>> emptyList(), this.fieldList, this.encoding);
		}
	}

	@Override
	protected void decodeBody(HandlerSocketSession session, byte[] data,
			int index) {
		List<List<byte[]>> rows = new ArrayList<List<byte[]>>(this
				.getNumColumns());
		int colOffset = 0;
		int cols = this.fieldList.length;
		List<byte[]> currentRow = new ArrayList<byte[]>(this.fieldList.length);
		int currentCols = 0;
		int colBytesSize = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] == TOKEN_SEPARATOR || data[i] == COMMAND_TERMINATE) {
				/**
				 * Patched by sam.tingleff
				 * "A character in the range [0x00 - 0x0f] is prefixed by 0x01 and shifted by 0x40"
				 */
				byte[] colData = new byte[colBytesSize];
				boolean shift = false;
				int colDataIndex = 0;
				// j must be less than i
				for (int j = colOffset; j < i; ++j) {
					byte b = data[j];
					if (b == 0x01)
						shift = true;
					else {
						colData[colDataIndex] = (shift) ? (byte) (b & ~0x40)
								: b;
						shift = false;
						++colDataIndex;
					}
				}
				currentRow.add(colData);
				colBytesSize = 0; // colBytesSize must reset to zero
				currentCols++;
				colOffset = i + 1;
				if (currentCols == cols) {
					currentCols = 0;
					rows.add(currentRow);
					currentRow = new ArrayList<byte[]>(this.fieldList.length);
				}
			} else if (data[i] != 0x01) {
				++colBytesSize;
			}

		}
		if (!currentRow.isEmpty()) {
			rows.add(currentRow);
		}
		this.result = new ResultSetImpl(rows, this.fieldList, this.encoding);
	}

	public void encode() {
		String kenLen = String.valueOf(this.keys.length);
		String limitStr = String.valueOf(this.limit);
		String offsetStr = String.valueOf(this.offset);

		byte[][] keyBytes = HSUtils.getByteArrayFromStringArray(keys,this.encoding);
		
		int flen =  filters.length;
		String ftype[] = new String[flen];
		String fop[] = new String[flen];
		String fcol[] = new String[flen];
		byte fvals[][][] = new byte[flen][][];
		
		int filterAllLength=0;
		for(int i=0; i< flen;i++) {
			Filter f = filters[i];
			ftype[i] = f.getTyep().getValue();
			fop[i] = f.getOperator().getValue();
			fcol[i] = String.valueOf(f.getColumn());
			fvals[i] = HSUtils.getByteArrayFromStringArray(f.getValue(), this.encoding);
			filterAllLength += ftype[i].length()+1 + fop.length+1 + fcol[i].length()+1 + this.length(fvals[i])+1;
		}

		int capacity = this.id.length() + 1
				+ this.operator.length() + 1 + kenLen.length() + 1
				+ this.length(keyBytes) + this.keys.length + limitStr.length()
				+ 1 + offsetStr.length() + 1+1
				+ filterAllLength;

		IoBuffer buf = IoBuffer.allocate(capacity);
		buf.setAutoExpand(true);
		// id
		this.writeToken(buf, this.id);
		this.writeTokenSeparator(buf);
		// operator
		this.writeToken(buf, this.operator);
		this.writeTokenSeparator(buf);
		// value nums

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

		for(int i=0; i< flen;i++) {
			this.writeTokenSeparator(buf);
			// filter type
			this.writeToken(buf, ftype[i]);
			this.writeTokenSeparator(buf);
			// filter operator
			this.writeToken(buf, fop[i]);
			this.writeTokenSeparator(buf);
			
			this.writeToken(buf, fcol[i]);
			this.writeTokenSeparator(buf);
			boolean isFirst = true;
			// filter value
			for(byte[] data : fvals[i]){
				if(!isFirst) this.writeTokenSeparator(buf);
				else isFirst = false;
				this.writeToken(buf, data);
			}
		}
		this.writeCommandTerminate(buf);

		buf.flip();
		this.buffer = buf;
	}
}
