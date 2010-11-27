package com.google.code.hs4j;

import com.google.code.hs4j.command.AbstractCommand;
import com.google.code.hs4j.command.text.FindCommand;
import com.google.code.hs4j.command.text.InsertCommand;
import com.google.code.hs4j.command.text.ModifyCommand;
import com.google.code.hs4j.command.text.OpenIndexCommand;

/**
 * A command factory to create commands
 * 
 * @author dennis
 * @date 2010-11-27
 */
public interface CommandFactory {
	
	Protocol getProtocol();

	public Command createOpenIndexCommand(String id, String db,
			String tableName, String indexName, String[] fieldList);

	public Command createInsertCommand(String id, String[] values);

	public Command createFindCommand(String id, FindOperator operator, String[] values,
			int limit, int offset, String[] fieldList);

	public Command createUpdateCommand(String id, FindOperator operator, String[] values,
			int limit, int offset, String[] fieldList);

	public Command createDeleteCommand(String id, FindOperator operator, String[] values,
			int limit, int offset, String[] fieldList);

}
