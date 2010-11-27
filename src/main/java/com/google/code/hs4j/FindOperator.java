package com.google.code.hs4j;

/**
 * Operator for finding data
 * 
 * @author dennis
 * @date 2010-11-27
 */
public enum FindOperator {
	/**
	 * '=' operator
	 */
	EQ,
	/**
	 * '>' operator
	 */
	GT,
	/**
	 * '>=' operator
	 */
	GE,
	/**
	 * '<=' opeartor
	 */
	LE,
/**
	 * '<' opeartor
	 */
	LT;

	/**
	 * Returns operator string value
	 * 
	 * @return
	 */
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
