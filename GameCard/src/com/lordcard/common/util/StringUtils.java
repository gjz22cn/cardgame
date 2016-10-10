package com.lordcard.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StringUtils {
	
	/**
	* 字符串压缩
	* @param str
	* @return
	*/
	public static String compress(String str) {
		try {
			if (str == null || str.length() == 0) {
				return str;
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(out);
			gzip.write(str.getBytes());
			gzip.close();
			return out.toString("ISO-8859-1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 解压缩
	 * 
	 * @param str
	 * @return
	 */
	public static String uncompress(String str) {
		try {
			if (str == null || str.length() == 0) {
				return str;
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
			GZIPInputStream gunzip = new GZIPInputStream(in);
			byte[] buffer = new byte[256];
			int n;
			while ((n = gunzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
			return out.toString("utf-8");
		} catch (Exception e) {
		}
		return str;
	}

}
