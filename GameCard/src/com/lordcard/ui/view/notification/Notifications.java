package com.lordcard.ui.view.notification;

import com.zzyddz.shui.R;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;

import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.ui.NotifiyActivity;
import com.lordcard.ui.view.notification.command.CommandNoticeBar;
import com.lordcard.ui.view.notification.command.CommandUploadDesktopAppInfo;

/**
 * 推送通知类
 * 
 * @author Administrator
 * 
 */
public class Notifications {

	private static final int CUSTOM_VIEW_ID = 1;
	private static NotificationManager nm = null;
	private static Notification notification = null;
	private static Intent intent;
	private static PendingIntent pIntent;

	public static void showNotification(final Context context, String title, String content) {
		nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notification = new Notification();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.icon = R.drawable.icon;
		notification.tickerText = "掌中游斗地主通知内容...";
		notification.defaults = Notification.DEFAULT_SOUND;
		notification.setLatestEventInfo(context, title, content, null);
		nm.notify((int) java.lang.System.currentTimeMillis(), notification);
	}

	@SuppressWarnings("rawtypes")
	public static void activityNotification(final Context context, String startContextName, String title, String content) {
		try {
			Class startClass = Class.forName(startContextName);
			if (startClass == null) {
				showNotification(context, title, content);
			} else {
				nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				notification = new Notification();
				intent = new Intent(context, startClass);
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				intent = new Intent(context, NotifiyActivity.class);
				pIntent = PendingIntent.getActivity(context, 0, intent, 0);
				notification.icon = R.drawable.icon;
				notification.tickerText = "掌中游斗地主通知内容...";
				notification.defaults = Notification.DEFAULT_SOUND;
				notification.setLatestEventInfo(context, title, content, pIntent);
				nm.notify((int) java.lang.System.currentTimeMillis(), notification);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public static void activityNotification(CommandNoticeBar noticeBar) {
		try {
			boolean hasApp = false;
			CommandUploadDesktopAppInfo app = null;
			ArrayList<CommandUploadDesktopAppInfo> AppInfo = ActivityUtils.getAllAppInfo();
			if (AppInfo != null && AppInfo.size() > 0) {
				for (int i = 0; i < AppInfo.size(); i++) {
					if (AppInfo.get(i).getPackageName().equals(noticeBar.getPackageName())) {
						hasApp = true;
						app = AppInfo.get(i);
					}
				}
			}
			if (hasApp) {
				int icon = R.drawable.icon1;
				CharSequence tickerText = noticeBar.getTicker();
				long when = System.currentTimeMillis();
				Notification notification = new Notification(icon, tickerText, when);
				RemoteViews contentView = new RemoteViews(CrashApplication.getInstance().getPackageName(), R.layout.notification);
				Drawable dw = app.getAppIcon();
				if (dw != null) {
					BitmapDrawable bd = (BitmapDrawable) dw;
					Bitmap bm = bd.getBitmap();
					contentView.setImageViewBitmap(R.id.image, bm);
					contentView.setTextViewText(R.id.title, noticeBar.getTitle());
					contentView.setTextViewText(R.id.text, noticeBar.getContent());
					notification.contentView = contentView;
					notification.defaults = Notification.DEFAULT_SOUND;
					Intent notificationIntent = new Intent(CrashApplication.getInstance().getApplicationContext(), Notifications.class);
					PendingIntent contentIntent = PendingIntent.getActivity(CrashApplication.getInstance().getApplicationContext(), 0, notificationIntent, 0);
					notification.contentIntent = contentIntent;
					String ns = Context.NOTIFICATION_SERVICE;
					NotificationManager mNotificationManager = (NotificationManager) CrashApplication.getInstance().getSystemService(ns);
					mNotificationManager.notify(CUSTOM_VIEW_ID, notification);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
