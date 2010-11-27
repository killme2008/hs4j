package com.google.code.hs4j.impl;

/**
 * An open-index connection record
 * 
 * @author dennis
 * @date 2010-11-27
 */
public class IndexRecord {
	public final int id;
	public final String db;
	public final String tableName;
	public final String indexName;
	public final String[] fieldList;

	public IndexRecord(int id, String db, String tableName,
			String indexName, String[] fieldList) {
		super();
		this.id = id;
		this.db = db;
		this.tableName = tableName;
		this.indexName = indexName;
		this.fieldList = fieldList;
	}

}
