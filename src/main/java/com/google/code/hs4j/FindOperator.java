package com.google.code.hs4j;

/**
 * Operator for finding
 * 
 * @author dennis
 * @date 2010-11-27
 */
public enum FindOperator {
	EQ, GT, GE, LE, LT;

	public String getValue() {
		switch (this) {
		case EQ:
			return "=";
		case GT:
			return ">";
		case GE:
			return ">=";
		case LE:
			return "<=";
		case LT:
			return "<";
		default:
			throw new RuntimeException("Unknow find operator " + this);
		}
	}
}
