package com.lordcard.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class Encrypt {
	/**
	 * 
	 * 功能说明：将批次号、MAC地址、应用ID一起加密 参数说明：@param batchId 批次号 参数说明：@param mac MAC地址
	 * 参数说明：@param sysbId 应用ID 参数说明：@return 加密后的密码 返回值说明:String 创建者:xupeng
	 * 创建日期:2012-8-2
	 */
	@SuppressLint("UseValueOf")
	public static String encrypt(String batchId, String mac, String sysbId) {

		/** 获得mac是以:还是-分开 */
		String macJianGeStr = "";
		if (-1 != mac.indexOf(":")) {
			macJianGeStr = ":";
		} else if (-1 != mac.indexOf("-")) {
			macJianGeStr = "-";
		}
		/** 将batchId每位数+5取个位数再分别插入到mac以macJianGeStr分开的每个单元的中间 */
		String[] macArray = mac.split(macJianGeStr);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < macArray.length; i++) {
			String tempMac = macArray[i];
			String tempBatchId = new Integer((Integer.parseInt(batchId.substring(i, i + 1)) + 5) % 10).toString();
			sb.append(tempMac.substring(0, 1) + tempBatchId + tempMac.substring(1));
		}
		/** 将sysbId拆分成2部分，第一部分插入到sb的第10位之后，第二部分插入到sb第13位后面 */
		String newTargetStr = null;
		newTargetStr = sb.subSequence(0, 10).toString() + sysbId.substring(0, 1).toString() + sb.substring(10, 13).toString() + sysbId.substring(1)
				+ sb.substring(13).toString();
		/** 将newTargetStr中的0换成B，2换成5，A换成7，D换成C */
		newTargetStr = newTargetStr.replaceAll("0", "B").replaceAll("2", "5").replaceAll("A", "7").replaceAll("D", "C");
		// System.out.println(newTargetStr);
		/** 取newTargetStr第一个数字对5取余数，在newTargetStr前截取余数个数 */
		Pattern pattern = Pattern.compile("[0-9]");
		Matcher matcher = pattern.matcher(newTargetStr);
		int firstYu = 0;
		if (matcher.find()) {
			String firstNum = matcher.group();
			firstYu = Integer.parseInt(firstNum) % 5 - 1;
		}
		/** MD5加密 */
		newTargetStr = newTargetStr.substring(firstYu + 1);
		return EncodeUtils.MD5(newTargetStr).toUpperCase();
	}

}
