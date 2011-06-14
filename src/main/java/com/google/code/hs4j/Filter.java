package com.google.code.hs4j;

/**
 * filter data
 * 
 * @author hoshino
 * @date 2011-06-07
 */
public class Filter {
	private FilterType type;
	private FindOperator op;
	private int col;
	private String value;

	public enum FilterType {
		FILTER("F"),
		WHILE("W");

		private String val;

		FilterType(String val) {
			this.val = val;
		}

		public String getValue() {
			return this.val;
		}
	}

	public Filter(FilterType type, FindOperator op, int col, String value) {
		this.type = type;
		this.op = op;
		this.col = col;
		this.value = value;
	}

	public FilterType getTyep() {
		return type;
	}

	public FindOperator getOperator() {
		return op;
	}

	public int getColumn() {
		return col;
	}

	public String getValue() {
		return value;
	}

}
