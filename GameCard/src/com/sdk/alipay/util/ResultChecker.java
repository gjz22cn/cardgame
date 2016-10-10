package com.sdk.alipay.util;

import org.json.JSONObject;

import android.util.Log;

import com.sdk.alipay.AliConfig;

public class ResultChecker {
	public static final int RESULT_INVALID_PARAM = 0;
	public static final int RESULT_CHECK_SIGN_FAILED = 1;
	public static final int RESULT_CHECK_SIGN_SUCCEED = 2;
	public final String TAG = "ResultChecker";
	String mContent;

	public ResultChecker(String content) {
		this.mContent = content;
	}

	public String getSuccess() {
		String success = null;

		try {
			JSONObject objContent = BaseHelper.string2JSON(this.mContent, ";");
			String result = objContent.getString("result");

			Log.v("ResultChecker", result);
			result = result.substring(1, result.length() - 1);

			JSONObject objResult = BaseHelper.string2JSON(result, "&");
			success = objResult.getString("success");
			success = success.replace("\"", "");
		} catch (Exception e) {
			// MyLog.e(TAG, e.getMessage());
			e.printStackTrace();
		}

		return success;
	}

	public int checkSign() {
		int retVal = RESULT_CHECK_SIGN_SUCCEED;

		try {
			JSONObject objContent = BaseHelper.string2JSON(this.mContent, ";");
			String result = objContent.getString("result");

			result = result.substring(1, result.length() - 1);

			int iSignContentEnd = result.indexOf("&sign_type=");
			String signContent = result.substring(0, iSignContentEnd);

			JSONObject objResult = BaseHelper.string2JSON(result, "&");
			String signType = objResult.getString("sign_type");
			signType = signType.replace("\"", "");

			String sign = objResult.getString("sign");
			sign = sign.replace("\"", "");

			if (signType.equalsIgnoreCase("RSA")) {
				if (!Rsa.doCheck(signContent, sign, AliConfig.RSA_ALIPAY_PUBLIC))
					retVal = RESULT_CHECK_SIGN_FAILED;
			}
		} catch (Exception e) {
			retVal = RESULT_INVALID_PARAM;
			e.printStackTrace();
		}

		return retVal;
	}

	public boolean isPayOk() {
		boolean isPayOk = false;

		String success = getSuccess();
		if ("true".equalsIgnoreCase(success) && checkSign() == RESULT_CHECK_SIGN_SUCCEED)
			isPayOk = true;

		return isPayOk;
	}
}