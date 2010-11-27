package com.google.code.hs4j.command.text;

import com.google.code.hs4j.Command;
import com.google.code.hs4j.CommandFactory;
import com.google.code.hs4j.FindOperator;
import com.google.code.hs4j.Protocol;

/**
 * HandlerSocket text protocol command factory
 * 
 * @author dennis
 * @date 2010-11-27
 */
public class TextCommandFactory implements CommandFactory {

	public Protocol getProtocol() {
		return Protocol.Text;
	}

	public Command createOpenIndexCommand(String id, String db,
			String tableName, String indexName, String[] fieldList) {
		return new OpenIndexCommand(id, db, tableName, indexName, fieldList);
	}

	public Command createInsertCommand(String id, String[] values) {
		return new InsertCommand(id, values);
	}

	public Command createFindCommand(String id, FindOperator operator,
			String[] values, int limit, int offset, String[] fieldList) {
		return new FindCommand(id, operator, values, limit, offset, fieldList);
	}

}
