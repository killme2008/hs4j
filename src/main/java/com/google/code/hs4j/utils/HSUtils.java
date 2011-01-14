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

	public static byte[][] getByteArrayFromStringArray(String[] a,String encoding) {
		byte[] [] result=new byte[a.length][0];
		int index=0;
		for(String s:a){
			result[index++]=decodeString(s, encoding);
		}
		return result;
	}

}
