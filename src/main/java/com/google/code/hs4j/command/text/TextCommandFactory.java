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

import com.google.code.hs4j.Command;
import com.google.code.hs4j.CommandFactory;
import com.google.code.hs4j.Filter;
import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.Protocol;

/**
 * HandlerSocket text protocol command factory
 * 
 * @author dennis
 * @date 2010-11-27
 */
public class TextCommandFactory implements CommandFactory {
	private String encoding;

	public void setEncoding(String encoding) {
		if (encoding == null || encoding.trim().length() == 0)
			throw new IllegalArgumentException("Invalid encoding:" + encoding);
		this.encoding = encoding;
	}

	public Protocol getProtocol() {
		return Protocol.Text;
	}

	public Command createOpenIndexCommand(String id, String db,
			String tableName, String indexName, String[] fieldList, String[] filterFieldList) {
		OpenIndexCommand result = new OpenIndexCommand(id, db, tableName,
				indexName, fieldList, filterFieldList);
		result.setEncoding(encoding);
		return result;
	}

	public Command createInsertCommand(String id, byte[][] values) {
		InsertCommand result = new InsertCommand(id, values);
		result.setEncoding(encoding);
		return result;
	}

	public Command createFindCommand(String id, FindOperator operator,
			String[] keys, int limit, int offset, String[] fieldList, Filter[] filter) {
		FindCommand result = new FindCommand(id, operator, keys, limit, offset,
				fieldList, filter);
		result.setEncoding(encoding);
		return result;
	}

	public Command createUpdateCommand(String id, FindOperator operator,
			String[] keys, byte[][] values, int limit, int offset) {
		ModifyCommand result = new ModifyCommand(id, operator, keys, values,
				limit, offset, AbstractCommand.OPERATOR_UPDATE);
		result.setEncoding(encoding);
		return result;
	}

	public Command createDeleteCommand(String id, FindOperator operator,
			String[] keys, int limit, int offset) {
		ModifyCommand result = new ModifyCommand(id, operator, keys,
				new byte[keys.length][0], limit, offset,
				AbstractCommand.OPERATOR_DELETE);
		result.setEncoding(encoding);
		return result;
	}

}
