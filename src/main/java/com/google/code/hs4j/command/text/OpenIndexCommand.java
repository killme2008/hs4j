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

import com.google.code.hs4j.Filter;
import com.google.code.hs4j.network.buffer.IoBuffer;
import com.google.code.hs4j.utils.HSUtils;

/**
 * Open index command
 * 
 * @author dennis
 * @date 2010-11-27
 */
public class OpenIndexCommand extends AbstractCommand {

	private static final String[] EMPTY= new String[0];

	private final String id;
	private final String db;
	private final String tableName;
	private final String indexName;
	private final String[] fieldList;
	private final String[] filterFieldList;

	public OpenIndexCommand(String id, String db, String tableName,
			String indexName, String[] fieldList) {
		this(id, db, tableName, indexName, fieldList, null);
	}

	
	public OpenIndexCommand(String id, String db, String tableName,
			String indexName, String[] fieldList, String[] filterFieldList) {
		super();
		this.id = id;
		this.db = db;
		this.tableName = tableName;
		this.indexName = indexName;
		this.fieldList = fieldList;
		if(filterFieldList != null){
			this.filterFieldList = filterFieldList;
		}else{
			this.filterFieldList = EMPTY;
		}
	}

	public void encode() {
		byte [][]fieldBytes=HSUtils.getByteArrayFromStringArray(this.fieldList, this.encoding);
		
		IoBuffer buf = IoBuffer.allocate(2 + this.id.length() + 1
				+ this.db.length() + 1 + this.tableName.length() + 1
				+ this.indexName.length() + 1 + this.length(fieldBytes)
				+ this.fieldList.length+ this.fieldList.length);
		buf.setAutoExpand(true);

		// header
		this.writeToken(buf, OPERATOR_OPEN_INDEX);
		this.writeTokenSeparator(buf);
		// id
		this.writeToken(buf, this.id);
		this.writeTokenSeparator(buf);
		// db name
		this.writeToken(buf, this.db);
		this.writeTokenSeparator(buf);
		// tableName
		this.writeToken(buf, this.tableName);
		this.writeTokenSeparator(buf);
		// indexName
		this.writeToken(buf, this.indexName);
		this.writeTokenSeparator(buf);
		// field list
		this.writeToken(buf, join(this.fieldList));

		// filter field list
		if(filterFieldList.length != 0) {
			this.writeTokenSeparator(buf);
			this.writeToken(buf, join(this.filterFieldList));
			this.writeCommandTerminate(buf);
		}else{
			this.writeCommandTerminate(buf);

		}

		buf.flip();

		this.buffer = buf;
	}

}
