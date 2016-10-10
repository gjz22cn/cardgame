/**
 * UpdateService.java [v 1.0.0]
 * classes : com.lordcard.common.upgrade.UpdateService
 * auth : yinhongbiao
 * time : 2013 2013-4-1 下午3:41:01
 */
package com.lordcard.common.upgrade;


import com.zzyddz.shui.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.network.http.HttpURL;
import com.lordcard.ui.StartActivity;
import com.lordcard.ui.dizhu.DoudizhuMainGameActivity;

/**
 * 升级更新服务
 * com.lordcard.common.upgrade.UpdateService
 * @author Administrator <br/>
 *         create at 2013 2013-4-1 下午3:41:01
 */
public class UpdateService extends Service {
	public static final String UPDATE_SERVICE = "game.intent.update.service";
	public final static int NOTIFICATION_ID = 1;
	private NotificationManager notificationManager = null; // 通知栏
	private Notification notification = null;
	private File file;
	private Context ctx;
	long fileSize = -1; // 文件大小

	int downFileSize = 0;
	int progress = 0;
	private final IBinder binder = new MyBinder();

	public class MyBinder extends Binder {
		UpdateService getService() {
			return UpdateService.this;
		}
	}

	public IBinder onBind(Intent intent) {
		return binder;
	}

	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		ctx = getApplicationContext();
		notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);// 多次下载的
		try {
			if (Database.UPDATED_STYLE) {
				Log.i("newVersionCode", "后台下载更新");
				downLoadBackstage();
				Database.UPDATED_STYLE = false;
			} else {
				Log.i("newVersionCode", "下载更新");
				downLoadNewVesion();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载新版本
	 */
	public void downLoadNewVesion() {
		String notimsg = ctx.getResources().getString(R.string.app_name) + UPVersion.versionName + "正在下载更新";
		notification = new Notification();
		notification.icon = R.drawable.icon;
		notification.tickerText = notimsg;

		// 放置在"正在运行"栏目中
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.contentView = new RemoteViews(ctx.getPackageName(), R.layout.download_notify);
		notification.contentView.setProgressBar(R.id.download_progressBar, 100, 0, false);

		new Thread() {
			public void run() {
				file = downFile(HttpURL.CONFIG_SER + UPVersion.apkName); // 文件下载

				if (file != null && file.exists() && downFileSize == fileSize) { // 下载成功
					notification.flags = Notification.FLAG_AUTO_CANCEL;

					Intent finishIntent = ActivityUtils.getInstallIntent(file);
					notification.contentView.setTextViewText(R.id.download_textView, "下载成功,点击安装。");
					notification.contentView.setProgressBar(R.id.download_progressBar, 100, 100, false);
					notification.defaults = Notification.DEFAULT_SOUND; // 设置铃声
					notification.contentIntent = PendingIntent.getActivity(ctx, 0, finishIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					notificationManager.notify(NOTIFICATION_ID, notification);
					ctx.startActivity(finishIntent);
					stopService(finishIntent);

				} else { // 下载失败
					notification.flags = Notification.FLAG_AUTO_CANCEL;
					notification.contentView.setTextViewText(R.id.download_textView, "下载失败");
					notificationManager.notify(NOTIFICATION_ID, notification);
					Database.UPDATEING = false;

				}
			};
		}.start();
	}

	/**
	 * 后台下载
	 */
	public void downLoadBackstage() {
		new Thread() {
			public void run() {
				file = downAPK(HttpURL.CONFIG_SER + UPVersion.apkName); // 文件下载
				if (file != null && file.exists() && downFileSize == fileSize) { // 下载成功
//					DialogUtils.mesToastTip("后台下载成功！");
					SharedPreferences sharedData = CrashApplication.getInstance().getSharedPreferences(Constant.UPDATECODE, Context.MODE_PRIVATE);
					Editor editor = sharedData.edit();
					editor.putInt(Constant.SAVECODE, UPVersion.versionCode);
					editor.commit();
					if(null != Database.currentActivity){
						Database.currentActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if(null != Database.currentActivity 
										&& !DoudizhuMainGameActivity.class.toString().equals(Database.currentActivity.getClass().toString())){
									UpdateUtils.newVersionTip();
								}
								
							}
						});
					}
				} else { // 下载失败
//					DialogUtils.mesToastTip("后台下载失败！");
					Database.UPDATEING = false;
				}
			};
		}.start();
	}

	@SuppressLint("WorldReadableFiles")
	public File downFile(final String url) {
		File file = null;
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			fileSize = entity.getContentLength();
			notification.when = fileSize;
			notificationManager.cancel(NOTIFICATION_ID);
			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			if (is != null) {
				boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
				if (sdCardExist) { // 下载到sd卡
					file = new File(Environment.getExternalStorageDirectory(), UPVersion.apkName);
					if (file.exists()) {
						file.delete();
						downFileSize = 0;//防止超过200%情况
					}
					ActivityUtils.createFile(file);

					fileOutputStream = new FileOutputStream(file);
				} else { // 下载到手机内存
					file = ctx.getFileStreamPath(UPVersion.apkName);
					if (file.exists()) {
						file.delete();
						downFileSize = 0;//防止超过200%情况
					}
					fileOutputStream = ctx.openFileOutput(UPVersion.apkName, Context.MODE_WORLD_READABLE);
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
						// 下载完成
					} else if (tempProgress != progress) {
						// 下载进度发生改变，则发送Message
						Intent taskIt = new Intent();
						taskIt.setClass(UpdateService.this, StartActivity.class);
						notification.contentIntent = PendingIntent.getActivity(UpdateService.this, 0, taskIt, PendingIntent.FLAG_UPDATE_CURRENT);
						notification.contentView.setProgressBar(R.id.download_progressBar, 100, progress, false);
						notification.contentView.setTextViewText(R.id.download_textView, "进度" + progress + "%");
						//						notification.setLatestEventInfo(UpdateService.this, "点击查看", "点击查看详细内容", notification.contentIntent);
						notificationManager.notify(NOTIFICATION_ID, notification);
						tempProgress = progress;
					}
				}
			}
			fileOutputStream.flush();
			if (fileOutputStream != null) {
				fileOutputStream.close();
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	@SuppressLint("WorldReadableFiles")
	public File downAPK(final String url) {
		File file = null;
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			fileSize = entity.getContentLength();
			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			if (is != null) {

				boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
				if (sdCardExist) { // 下载到sd卡
					file = new File(Environment.getExternalStorageDirectory(), UPVersion.apkName);

					if (file.exists()) {
						file.delete();
						downFileSize = 0;//防止超过200%情况
					}
					ActivityUtils.createFile(file);

					fileOutputStream = new FileOutputStream(file);
				} else { // 下载到手机内存
					file = ctx.getFileStreamPath(UPVersion.apkName);
					if (file.exists()) {
						file.delete();
						downFileSize = 0;//防止超过200%情况
					}
					fileOutputStream = ctx.openFileOutput(UPVersion.apkName, Context.MODE_WORLD_READABLE);

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
						// 下载完成
					} else if (tempProgress != progress) {
					}
				}
			}
			fileOutputStream.flush();
			if (fileOutputStream != null) {
				fileOutputStream.close();
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

}
