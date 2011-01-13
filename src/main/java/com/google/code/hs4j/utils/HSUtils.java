package com.google.code.hs4j.utils;

import java.io.UnsupportedEncodingException;

public class HSUtils {
	private HSUtils() {

	}

	/**
	 * Whether string is a space or null
	 * 
	 * @param s
	 * @return
	 */
	public static final boolean isBlank(String s) {
		if (s == null || s.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static byte[] decodeString(String s, String encoding) {
		if (s == null)
			return null;
		try {
			return s.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unsupported encoding " + encoding, e);
		}
	}

}
