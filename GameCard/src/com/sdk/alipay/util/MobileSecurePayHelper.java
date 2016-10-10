/*
 * Copyright (C) 2012
 * All right reserved.
 * author:courage0620@gmail.com
 */

package com.sdk.alipay.util;

import com.zzyddz.shui.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.sdk.alipay.AliConfig;

@SuppressLint("HandlerLeak")
public class MobileSecurePayHelper {
	static final String TAG = "MobileSecurePayHelper";

	private ProgressDialog mProgress = null;
	Context mContext = null;

	private boolean isNewestVersion;

	public boolean isNewestVersion() {
		return isNewestVersion;
	}

	public void setNewestVersion(boolean isNewestVersion) {
		this.isNewestVersion = isNewestVersion;
	}

	private PackageInfo currentPackageInfo;// 当前版本

	public PackageInfo getCurrentPackageInfo() {
		return currentPackageInfo;
	}

	public void setCurrentPackageInfo(PackageInfo currentPackageInfo) {
		this.currentPackageInfo = currentPackageInfo;
	}

	public MobileSecurePayHelper(Context context) {
		this.mContext = context;
	}

	public boolean detectMobile_sp() {

		// 检测用户是否已经安装服务
		boolean isMobile_spExist = isMobile_spExist();
		// get the cacheDir.
		File cacheDir = mContext.getCacheDir();
		final String cachePath = cacheDir.getAbsolutePath() + "/temp.apk";
		if (!isMobile_spExist) {

			// 捆绑安装
			retrieveApkFromAssets(mContext, AliConfig.ALIPAY_PLUGIN_NAME, cachePath);
			mProgress = BaseHelper.showProgress(mContext, null, "正在检测安全支付服务版本", false, true);

			new Thread(new Runnable() {
				public void run() {

					// 检测是否有新的版本
					PackageInfo apkInfo = getApkInfo(mContext, cachePath);
					String newApkdlUrl = checkNewUpdate(apkInfo);

					// 动态下载
					if (newApkdlUrl != null) {

						retrieveApkFromNet(mContext, newApkdlUrl, cachePath);
					}
					Message msg = new Message();
					msg.what = AlixId.RQF_INSTALL_CHECK;
					msg.obj = cachePath;
					mHandler.sendMessage(msg);

				}
			}).start();
		}
		return isMobile_spExist;
	}

	/**
	 * 提示安装支付宝安全支付服务对话框
	 * @param context
	 * @param cachePath
	 */
	public void showInstallConfirmDialog(final Context context, final String cachePath) {
		AlertDialog.Builder tDialog = new AlertDialog.Builder(context);
		tDialog.setIcon(R.drawable.info);
		tDialog.setTitle(context.getResources().getString(R.string.confirm_install_hint));
		tDialog.setMessage(context.getResources().getString(R.string.confirm_install));

		tDialog.setPositiveButton(R.string.Ensure, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				// 修改apk权限
				BaseHelper.chmod("777", cachePath);
				// install the apk.
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.parse("file://" + cachePath), "application/vnd.android.package-archive");

				Intent bIntent = new Intent();
				bIntent.setAction("com.xrl.creditcard.install");
				context.sendBroadcast(bIntent);
				context.startActivity(intent);
			}
		});

		tDialog.setNegativeButton(context.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});

		tDialog.show();
	}

	public boolean isMobile_spExist() {
		PackageManager manager = mContext.getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (int i = 0; i < pkgList.size(); i++) {
			PackageInfo pI = pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase("com.alipay.android.app")) {
				currentPackageInfo = pI;// set the installed package information
										// to this current package information
				return true;
			}
		}

		return false;
	}

	//
	// 捆绑安装
	public boolean retrieveApkFromAssets(Context context, String fileName, String path) {
		boolean bRet = false;

		try {
			InputStream is = context.getAssets().open(fileName);

			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);

			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}

			fos.close();
			is.close();

			bRet = true;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return bRet;
	}

	/**
	 * 获取未安装的APK信息
	 * 
	 * @param context
	 * @param archiveFilePath
	 *            APK文件的路径。如：/sdcard/download/XX.apk
	 */
	public static PackageInfo getApkInfo(Context context, String archiveFilePath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo apkInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_META_DATA);
		return apkInfo;
	}

	//
	// 检查是否有新的版本，如果有，返回apk的下载地址。
	public String checkNewUpdate(PackageInfo packageInfo) {
		String url = null;

		try {
			JSONObject resp = sendCheckNewUpdate(packageInfo.versionName);
			// JSONObject resp = sendCheckNewUpdate("1.0.0");
			if (resp.getString("needUpdate").equalsIgnoreCase("true")) {
				url = resp.getString("updateUrl");
			}
			// else ok.
		} catch (Exception e) {
			// MyLog.d(TAG, e.getMessage());
			e.printStackTrace();
		}

		return url;
	}

	public JSONObject sendCheckNewUpdate(String versionName) {
		JSONObject objResp = null;
		try {
			JSONObject req = new JSONObject();
			req.put(AlixDefine.action, AlixDefine.actionUpdate);

			JSONObject data = new JSONObject();
			data.put(AlixDefine.platform, "android");
			data.put(AlixDefine.VERSION, versionName);
			data.put(AlixDefine.partner, "");

			req.put(AlixDefine.data, data);

			objResp = sendRequest(req.toString());
		} catch (JSONException e) {
			// MyLog.d(TAG, e.getMessage());
			e.printStackTrace();
		}

		return objResp;
	}

	public JSONObject sendRequest(final String content) {
		NetworkManager nM = new NetworkManager(this.mContext);

		//
		JSONObject jsonResponse = null;
		try {
			String response = null;

			synchronized (nM) {
				response = nM.SendAndWaitResponse(content, AliConfig.ALI_APP_URL);
			}

			jsonResponse = new JSONObject(response);
		} catch (Exception e) {
			// MyLog.d(TAG, e.getMessage());
			e.printStackTrace();
		}

		return jsonResponse;
	}

	//
	// 动态下载
	public boolean retrieveApkFromNet(Context context, String strurl, String filename) {
		boolean bRet = false;

		try {
			NetworkManager nM = new NetworkManager(this.mContext);
			bRet = nM.urlDownloadToFile(context, strurl, filename);
		} catch (Exception e) {
			// MyLog.d(TAG, e.getMessage());
			e.printStackTrace();
		}

		return bRet;
	}

	// close the progress bar
	void closeProgress() {
		try {
			if (mProgress != null) {
				mProgress.dismiss();
				mProgress = null;
			}
		} catch (Exception e) {
			// MyLog.d(TAG, e.getMessage());
			e.printStackTrace();
		}
	}

	// the handler use to receive the install check result.
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case AlixId.RQF_INSTALL_CHECK: {
					// 关闭进度对话框
					closeProgress();
					String cachePath = (String) msg.obj;

					showInstallConfirmDialog(mContext, cachePath);
				}
					break;
				}

				super.handleMessage(msg);
			} catch (Exception e) {
				// MyLog.d(TAG, e.getMessage());
				e.printStackTrace();
			}
		}
	};
}
