package com.lordcard.common.upgrade;


import com.zzyddz.shui.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.RemoteViews;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.lordcard.ui.LoginActivity;
import com.lordcard.ui.TaskMenuActivity;
import com.lordcard.ui.view.dialog.UpdateDialog;
import com.umeng.analytics.MobclickAgent;

public class UpdateUtils {

	private static NotificationManager notificationManager = null; // 通知栏
	private static Notification notification = null;
	private static Intent newIntent = null;
	/**
	 * 安装文件地址
	 */
	private static File tempFile;

	/**
	 * 检查版本信息
	 * @param context
	 * @param serverUrl
	 *            服务器地址
	 * @param versionUrl
	 *            版本文件地址
	 */
	public static boolean checkNewVersion(final String url, String versionUrl) {
		String serverUrl = url + versionUrl;
		if (!getServerVerCode(serverUrl)) {
			Log.i("newVersionCode", "有新版本--检查新版本出错");
			return false; // 检查新版本出错 则直接跳过
		}
		String vercode = ActivityUtils.getVersionCode(); // 当前版本
		if (UPVersion.versionCode <= Integer.parseInt(vercode))
			return false; // 没有新版本 直接退出
		Log.i("newVersionCode", "有新版本");
		return true;
	}

	/**
	 * 下载更新提示
	 * @param contex
	 */
	public static void newVersionTip() {
		final Activity activity = Database.currentActivity;
		new Thread() {
			public void run() {
				boolean isQuick = false;
				SharedPreferences sharedData = CrashApplication.getInstance().getSharedPreferences(Constant.UPDATECODE, Context.MODE_PRIVATE);
				int verCode = sharedData.getInt(Constant.VERSIONCODE, 0);
				File file = null;
				//如果本地的版本与服务器不相等，表示需要更新
				if (verCode != UPVersion.versionCode) {
					// 判断sd卡是否存在
					boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
					SharedPreferences sharedsaveData = CrashApplication.getInstance().getSharedPreferences(Constant.UPDATECODE, Context.MODE_PRIVATE);
					int saveCode = sharedsaveData.getInt(Constant.SAVECODE, 0);
					//本地保存的包的版本和服务器一直，表示此包已经后台下载好了
					if (saveCode == UPVersion.versionCode) {
						if (sdCardExist) {
							file = new File(Environment.getExternalStorageDirectory(), UPVersion.apkName);
							//SD卡是否已经存在此包
							if (file.exists()) {
								isQuick = true;
							}
						} else {
							file = activity.getFileStreamPath(UPVersion.apkName);
							//内存中是否存在此包
							if (file.exists()) {
								isQuick = true;
							}
						}
					}
					String versionCode = ActivityUtils.getVersionCode();
					if (!TextUtils.isEmpty(UPVersion.upcodes)) {
						int verson = Integer.parseInt(ActivityUtils.getVersionCode());
						//如果升级状态为强制升级，且当前版本号与服务器的版本不一致，则强制升级
						if (UPVersion.UP_STRONG_ALL.equals(UPVersion.upcodes) && verson != UPVersion.versionCode) {
							showTip(isQuick, false, file);
						} else if (UPVersion.UP_ALL.equals(UPVersion.upcodes) && verson != UPVersion.versionCode) {
							showTip(isQuick, true, file);
						} else {
							boolean upCversion = false; // 是否强制升级当前版本
							String[] codes = UPVersion.upcodes.split(",");
							for (int i = 0; i < codes.length; i++) {
								if (versionCode.equals(codes[i].trim())) {
									upCversion = true;
								}
							}
							if (upCversion && verson != UPVersion.versionCode) {
								showTip(isQuick, false, file);
							}
						}
					}
				}
			};
		}.start();
	}

	/**
	 * 弹出更新提示对话框
	 * @param quickUpdate  一秒安装
	 * @param canCancel 是否有取消按钮
	 * @param file APK文件
	 */
	public static void showTip(boolean quickUpdate, boolean canCancel, File file) {
		final Activity activity = Database.currentActivity;
		if (quickUpdate && file != null) {
			updateTip(file, quickUpdate, UPVersion.infolis, activity, false);
		} else {
			if (Database.currentActivity.getClass().equals(LoginActivity.class)) {
				updateTip(file, quickUpdate, UPVersion.infolis,activity, canCancel);
			}
		}
	}

	/**
	 * @param file APK文件
	 * @param isQuick 是否一秒安装
	 * @param list 更新内容列表
	 * @param content 上下文
	 * @param iscancle  是否有取消按钮
	 */
	public static void updateTip(final File file, final boolean isQuick, final List<String> list,final Activity content, final boolean iscancle) {
		try {
			Database.currentActivity.runOnUiThread(new Runnable() {
				public void run() {
					UpdateDialog gameDialog = new UpdateDialog(list, Database.currentActivity, iscancle) {
						public void okClick() {
							//若本地有最新包,且该文件不为空
							if (isQuick && file != null) {
								Intent finishIntent = ActivityUtils.getInstallIntent(file);
								Database.currentActivity.startActivity(finishIntent);
								SharedPreferences sharedData = CrashApplication.getInstance().getSharedPreferences(Constant.UPDATECODE, Context.MODE_PRIVATE);
								Editor editor = sharedData.edit();
								editor.putInt(Constant.SAVECODE, 0);
								editor.commit();
							} else {
								if (Database.UPDATEING) {
									dismiss();
								} else {
									Database.UPDATEING = true;
									String showText = "后台正在下载更新包，您可以继续玩游戏！";
									if (UPVersion.UP_STRONG_ALL.equals(UPVersion.upcodes)) {
										Database.UPDATEING = false;
										showText = "游戏正在更新，请稍候！";
									}
									DialogUtils.mesToastTip(showText);
									MobclickAgent.onEvent(content,"更新");
									MobclickAgent.onEvent(content,"静默更新");
									downLoadNewVesionSev();// 静默
								}
							}
						}

						public void cancelClick() {
							boolean isWifiOpen = ActivityUtils.isOpenWifi();//wifi是否打开
							if (!isQuick && isWifiOpen) {
								Database.UPDATED_STYLE = true;
								DialogUtils.mesToastTip("WIFI环境下自动下载更新包。");
								downLoadNewVesionSev();// 静默
							}
						}
					};
					gameDialog.show();
					if (isQuick && file != null) {
						gameDialog.setButton("一秒安装");
					}
				}
			});
		} catch (Exception e) {}
	}

	/**
	 * 浏览器下载更新。
	 * @param activity
	 */
	public static void downLoadNewVesionWeb(final Activity activity) {
		String urlStr = HttpURL.CONFIG_SER + UPVersion.apkName;
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlStr));
		activity.startActivity(intent);
	}

	/**
	 * 静默安装更新
	 */
	public static void downLoadNewVesionSev() {
		Context ctx = CrashApplication.getInstance().getApplicationContext();
		newIntent = new Intent(ctx, UpdateService.class);
		ctx.stopService(newIntent);
		ctx.startService(newIntent);
	}

	/**
	 * 关闭 静默安装更新服务
	 * @param activity
	 */
	public static void stopDownLoadNewVesionSev(final Activity activity) {
		if (newIntent != null) {
			activity.stopService(newIntent);
		}
	}

	/**
	 * 获取服务器游戏安装包版本信息
	 * @return
	 */
	public static boolean getServerVerCode(String url) {
		try {
			String verjson = HttpUtils.post(url, null, true);
			verjson = new String(verjson.getBytes("ISO-8859-1"), Constant.CHAR);
			JSONArray array = new JSONArray(verjson);
			if (array.length() > 0) {
				JSONObject obj = array.getJSONObject(0);
				try {
					UPVersion.versionCode = Integer.parseInt(obj.getString("versionCode"));
					UPVersion.versionName = obj.getString("versionName");
					UPVersion.apkName = obj.getString("apkName");
					UPVersion.upcodes = obj.getString("upcodes");
					try {
						UPVersion.infolis = JsonHelper.fromJson(obj.getString("infolis"), new TypeToken<List<String>>() {});
					} catch (Exception e) {}
				} catch (Exception e) {
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 安装新版本APK
	 * @param context
	 * @param installApk
	 */
	public static Intent installnewApk(Context context, File installApk) {
		Intent intent = ActivityUtils.getInstallIntent(installApk);
		context.startActivity(intent);
		return intent;
	}

	/**
	 * 拿到APK资源
	 * @param strUrl
	 */
	@SuppressWarnings("resource")
	public static void getDataSource(Context context, String strUrl) {
		URL url = createUrl(strUrl);
		String prefix = strUrl.substring(strUrl.lastIndexOf("/"), strUrl.lastIndexOf("."));
		String suffix = ".apk";
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			InputStream in = conn.getInputStream();
			// length = conn.getContentLength();
			if (null == in) {
				Log.d(Constant.LOG_TAG, "Wrong InputStream");
			} else {
				if (conn.getResponseCode() >= 400) {
					Log.d(Constant.LOG_TAG, "connect timeout");
				}
				byte[] buffer = new byte[1024];
				// 创建一个安装文件地址
				tempFile = File.createTempFile(prefix, suffix);
				// 获取绝对路径
				FileOutputStream out = new FileOutputStream(tempFile);
				do {
					int tempLength = in.read(buffer);
					if (tempLength <= 0) {
						break;
					}
					out.write(buffer, 0, tempLength);
				} while (true);
			}
			install(context);
		} catch (IOException e) {
			Log.w("IOException", e);
		}
	}

	public static void install(Context conext) {
		Intent it = new Intent(Intent.ACTION_VIEW); // 实例化一个View标识Intent
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 安装完成后、显示桌面
		it.setDataAndType(Uri.fromFile(tempFile), // 赋值安装文件
				"application/vnd.android.package-archive");
		if (null != conext) { // 启动安装界面
			conext.startActivity(it);
		}
	}

	/**
	 * @param strUrl
	 * @return 获取URL
	 */
	private static URL createUrl(String strUrl) {
		URL url = null;
		try {
			url = new URL(strUrl);
			// 判断地址是否是URL格式
			if (!URLUtil.isNetworkUrl(strUrl)) {
				return null;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

	/**
	 * 下载apk
	 * @param url
	 * @param context
	 * @return
	 */
	@SuppressLint("WorldReadableFiles")
	public static void downApk(final Context context, final String name, String downUrl, String apkName, final Handler mHandler) {
		final String url = downUrl + apkName;
		final int notificationId = apkName.hashCode();
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		String notimsg = name + " 应用正在下载";
		notification = new Notification();
		notification.icon = android.R.drawable.stat_sys_download;
		notification.tickerText = notimsg;
		// 放置在"正在运行"栏目中
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.contentView = new RemoteViews(context.getPackageName(), R.layout.download_notify);
		notification.contentView.setImageViewResource(R.id.download_icon, android.R.drawable.stat_sys_download);
		notification.contentView.setProgressBar(R.id.download_progressBar, 100, 0, false);
		notification.contentView.setTextViewText(R.id.download_textView, notimsg);
		notification.contentIntent = PendingIntent.getActivity(context, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
		new Thread(new Runnable() {

			@Override
			public void run() {
				long fileSize = -1; // 文件大小
				int downFileSize = 0;
				int progress = 0;
				File file = null;
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					fileSize = entity.getContentLength();
					notification.when = fileSize;
					notificationManager.cancel(notificationId);
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
						if (sdCardExist) { // 下载到sd卡
							file = new File(Environment.getExternalStorageDirectory(), UPVersion.apkName);
							if (!file.exists()) {
								ActivityUtils.createFile(file);
							}
							fileOutputStream = new FileOutputStream(file);
						} else { // 下载到手机内存
							file = context.getFileStreamPath(UPVersion.apkName);
							fileOutputStream = context.openFileOutput(UPVersion.apkName, Context.MODE_WORLD_READABLE);
						}
						int tempProgress = -1;
						byte[] buf = new byte[1024];
						int ch = 0;
						while ((ch = is.read(buf)) != -1) {
							downFileSize = downFileSize + ch;
							// 下载进度
							progress = (int) (downFileSize * 100.0 / fileSize);
							fileOutputStream.write(buf, 0, ch);
							if (downFileSize == fileSize) {
								Bundle b = new Bundle();
								b.putString("APKpath", file.getPath());
								Message msg = new Message();
								msg.what = TaskMenuActivity.HANDLER_WHAT_TASK_MENU_NOTIFY_DOWNLOAD_DATA;
								msg.setData(b);
								mHandler.sendMessage(msg);
								// 下载完成
							} else if (tempProgress != progress) {
								// 下载进度发生改变，则发送Message
								notification.contentView.setProgressBar(R.id.download_progressBar, 100, progress, false);
								notification.contentView.setTextViewText(R.id.download_textView, name + "下载进度" + progress + "%");
								notificationManager.notify(notificationId, notification);
								tempProgress = progress;
							}
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
						is.close();
					}
					notification.flags = Notification.FLAG_AUTO_CANCEL;
					notification.contentView.setTextViewText(R.id.download_textView, name + "下载成功,点击安装.");
					notification.contentView.setProgressBar(R.id.download_progressBar, 100, 100, false);
					notification.defaults = Notification.DEFAULT_SOUND; // 设置铃声
					notification.contentIntent = PendingIntent.getActivity(context, 0, ActivityUtils.getInstallIntent(file), PendingIntent.FLAG_UPDATE_CURRENT);
					notificationManager.notify(notificationId, notification);
					// installnewApk(context, file);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 下载apk
	 * @param url
	 * @param context
	 * @return
	 */
	@SuppressLint("WorldReadableFiles")
	public static void downApkAssistant(final Context context, final String name, String downUrl, final String apkName, final Handler mHandler) {
		final String url = downUrl + apkName;
		final int notificationId = apkName.hashCode();
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		String notimsg = name + " 应用正在下载";
		notification = new Notification();
		notification.icon = android.R.drawable.stat_sys_download;
		notification.tickerText = notimsg;
		// 放置在"正在运行"栏目中
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.contentView = new RemoteViews(context.getPackageName(), R.layout.download_notify);
		notification.contentView.setImageViewResource(R.id.download_icon, android.R.drawable.stat_sys_download);
		notification.contentView.setProgressBar(R.id.download_progressBar, 100, 0, false);
		notification.contentView.setTextViewText(R.id.download_textView, notimsg);
		notification.contentIntent = PendingIntent.getActivity(context, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
		new Thread(new Runnable() {

			@Override
			public void run() {
				long fileSize = -1; // 文件大小
				int downFileSize = 0;
				int progress = 0;
				File file = null;
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					fileSize = entity.getContentLength();
					notification.when = fileSize;
					notificationManager.cancel(notificationId);
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
						if (sdCardExist) { // 下载到sd卡
							file = new File(Environment.getExternalStorageDirectory(), UPVersion.apkName);
							if (!file.exists()) {
								ActivityUtils.createFile(file);
							}
							fileOutputStream = new FileOutputStream(file);
						} else { // 下载到手机内存
							file = context.getFileStreamPath(apkName);
							fileOutputStream = context.openFileOutput(apkName, Context.MODE_WORLD_READABLE);
						}
						int tempProgress = -1;
						byte[] buf = new byte[1024];
						int ch = 0;
						while ((ch = is.read(buf)) != -1) {
							downFileSize = downFileSize + ch;
							// 下载进度
							progress = (int) (downFileSize * 100.0 / fileSize);
							fileOutputStream.write(buf, 0, ch);
							if (downFileSize == fileSize) {
								Bundle b = new Bundle();
								b.putString("APKpath", file.getPath());
								Message msg = new Message();
								msg.what = TaskMenuActivity.HANDLER_WHAT_TASK_MENU_NOTIFY_DOWNLOAD_DATA;
								msg.setData(b);
								mHandler.sendMessage(msg);
								Database.ASSISTANT_DW = true;//标示安装应用
								// 下载完成
							} else if (tempProgress != progress) {
								// 下载进度发生改变，则发送Message
								notification.contentView.setProgressBar(R.id.download_progressBar, 100, progress, false);
								notification.contentView.setTextViewText(R.id.download_textView, name + "下载进度" + progress + "%");
								notificationManager.notify(notificationId, notification);
								tempProgress = progress;
							}
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
						is.close();
					}
					notification.flags = Notification.FLAG_AUTO_CANCEL;
					notification.contentView.setTextViewText(R.id.download_textView, name + "下载成功,点击安装.");
					notification.contentView.setProgressBar(R.id.download_progressBar, 100, 100, false);
					notification.defaults = Notification.DEFAULT_SOUND; // 设置铃声
					notification.contentIntent = PendingIntent.getActivity(context, 0, ActivityUtils.getInstallIntent(file), PendingIntent.FLAG_UPDATE_CURRENT);
					notificationManager.notify(notificationId, notification);
					// installnewApk(context, file);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
