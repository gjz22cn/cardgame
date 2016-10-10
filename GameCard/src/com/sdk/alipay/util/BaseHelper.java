/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.sdk.alipay.util;

import com.zzyddz.shui.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.sdk.alipay.ui.AlixPayActivity;

public class BaseHelper {
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			// MyLog.d("BaseHelper", e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// MyLog.d("BaseHelper", e.getMessage());
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static void showDialog(final Activity context, String strTitle, String strText, int icon, DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder tDialog = new AlertDialog.Builder(context);
		tDialog.setIcon(icon);
		tDialog.setTitle(strTitle);
		tDialog.setMessage(strText);
		tDialog.setPositiveButton(R.string.Ensure, onClickListener);
		tDialog.show();
	}

	public static void chmod(String permission, String path) {
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//
	// show the progress bar.
	public static ProgressDialog showProgress(Context context, CharSequence title, CharSequence message, boolean indeterminate, boolean cancelable) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(false);
		// dialog.setDefaultButton(false);
		dialog.setOnCancelListener(new AlixPayActivity.AlixOnCancelListener((Activity) context));

		dialog.show();
		return dialog;
	}

	public static JSONObject string2JSON(String str, String split) {
		JSONObject json = new JSONObject();
		try {
			String[] arrStr = str.split(split);
			for (int i = 0; i < arrStr.length; i++) {
				String[] arrKeyValue = arrStr[i].split("=");
				json.put(arrKeyValue[0], arrStr[i].substring(arrKeyValue[0].length() + 1));
			}
		}

		catch (Exception e) {
			// MyLog.d("BaseHelper", e.getMessage());
			e.printStackTrace();
		}

		return json;
	}

	public static String getSignType() {
		String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
		return getSignType;
	}

	public static String getCharset() {
		String charset = "charset=" + "\"" + "utf-8" + "\"";
		return charset;
	}
}