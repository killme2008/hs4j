package com.google.code.hs4j.exception;

/**
 * An exception threw by hs4j client
 * 
 * @author dennis
 * @date 2010-11-27
 */
public class HandlerSocketException extends Exception {

	private static final long serialVersionUID = -1L;

	public HandlerSocketException() {
		super();

	}

	public HandlerSocketException(String message, Throwable cause) {
		super(message, cause);

	}

	public HandlerSocketException(String message) {
		super(message);

	}

	public HandlerSocketException(Throwable cause) {
		super(cause);

	}

}
