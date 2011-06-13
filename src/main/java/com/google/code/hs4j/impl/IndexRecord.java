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
package com.google.code.hs4j.impl;

/**
 * Represent an opened index
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
	public final String[] filterFieldList;

	public IndexRecord(int id, String db, String tableName, String indexName,
			String[] fieldList, String[] filterFieldList) {
		super();
		this.id = id;
		this.db = db;
		this.tableName = tableName;
		this.indexName = indexName;
		this.fieldList = fieldList;
		this.filterFieldList = filterFieldList;
	}

}
