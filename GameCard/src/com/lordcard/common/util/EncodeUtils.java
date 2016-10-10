package com.lordcard.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 各种格式的编码加码工具类.
 * 
 * 集成Commons-Codec,Commons-Lang及JDK提供的编解码方法.
 * 
 */
public class EncodeUtils {

	private static final String DEFAULT_URL_ENCODING = "UTF-8";
	/**
	 * URL 解码, Encode默认为UTF-8.
	 */
	public static String urlDecode(String input) {
		return urlDecode(input, DEFAULT_URL_ENCODING);
	}

	/**
	 * URL 解码.
	 */
	public static String urlDecode(String input, String encoding) {
		try {
			return URLDecoder.decode(input, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unsupported Encoding Exception", e);
		}
	}

	/**
	 * 转码iso-8859-1
	 */
	public static String strEncode(String str) {
		try {
			str = new String(str.getBytes(DEFAULT_URL_ENCODING), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 转码utf-8
	 */
/*	public static String strDecode(String str) {
		try {
			str = new String(str.getBytes("ISO-8859-1"), DEFAULT_URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}*/
	/**
	 * 转码utf-8
	 */
	public static String strDecode(String str) {
		try {
			str = new String(str.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
	/**
	 * 对字符串进行MD5加密
	 * 
	 * @param plainText
	 *            String
	 * @return String
	 */
	public static String MD5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void main(String[] args) {
		System.out.println(MD5("123456"));
	}
}
