package com.google.code.hs4j;

/**
 * A factory to create HandlerSocket commands
 * 
 * @author dennis
 * @date 2010-11-27
 */
public interface CommandFactory {

	Protocol getProtocol();

	public Command createOpenIndexCommand(String id, String db,
			String tableName, String indexName, String[] fieldList);

	public Command createInsertCommand(String id, String[] values);

	public Command createFindCommand(String id, FindOperator operator,
			String[] values, int limit, int offset, String[] fieldList);

	public Command createUpdateCommand(String id, FindOperator operator,
			String[] values, int limit, int offset, String[] fieldList);

	public Command createDeleteCommand(String id, FindOperator operator,
			String[] values, int limit, int offset, String[] fieldList);

}
