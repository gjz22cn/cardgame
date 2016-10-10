package com.lordcard.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.lordcard.common.task.GenericTask;
import com.lordcard.common.task.TaskFeedback;
import com.lordcard.common.task.base.TaskParams;
import com.lordcard.common.task.base.TaskResult;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameTask;
import com.lordcard.network.cmdmgr.CmdUtils;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.ui.dizhu.DoudizhuRoomListActivity;

public class PackageReceiver extends BroadcastReceiver {
	static PackageManager packageManager;

	private static PackageReceiver mReceiver = new PackageReceiver();
	private static IntentFilter mIntentFilter;

	public static void registerReceiver(Context con) {
		packageManager = con.getPackageManager();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addDataScheme("package");
		mIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		mIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		mIntentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		con.registerReceiver(mReceiver, mIntentFilter);
	}

	public static void unregisterReceiver(Context context) {
		context.unregisterReceiver(mReceiver);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals("android.intent.action.PACKAGE_ADDED")
				&& Database.currentActivity.getLocalClassName().equals(
						DoudizhuRoomListActivity.class.getName())&&Database.ASSISTANT_DW) {
			Database.ASSISTANT_DW=false;
			String packageName = intent.getDataString();
			Toast.makeText(context, "安装了：" + packageName, Toast.LENGTH_LONG).show();
			GenericTask downloadSoftTask = new DownloadSoftTask();
			downloadSoftTask.setFeedback(TaskFeedback.getInstance(TaskFeedback.DIALOG_MODE));
			TaskParams params = new TaskParams();
			params.put("softPN", packageName);
			downloadSoftTask.execute(params);
		}
		if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			String str = intent.getDataString();
			String packageName = str.substring(str.indexOf(":") + 1);
			for (int j = 0; j < Database.packageNames.size(); j++) {
				String packageNameServer = Database.packageNames.get(j);

				if (packageNameServer.equals(packageName)) {
					Intent startIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
					context.startActivity(startIntent);
					// int count = sharedata.getInt(packageName, 0);
					// if (count == 1) {
					// 提交手机号码
					GenericTask downloadSoftTask = new DownloadSoftTask();
					downloadSoftTask.setFeedback(TaskFeedback.getInstance(TaskFeedback.DIALOG_MODE));
					TaskParams params = new TaskParams();
					params.put("softPN", packageName);
					downloadSoftTask.execute(params);
					// }

				}
			}

		} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
			// Toast.makeText(context, "有应用被删除", Toast.LENGTH_LONG).show();
		}

		else if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
			// Toast.makeText(context, "有应用被改变", Toast.LENGTH_LONG).show();
		}

		if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			// Toast.makeText(context, "有应用被替换", Toast.LENGTH_LONG).show();
		}

		else if (Intent.ACTION_PACKAGE_RESTARTED.equals(action)) {
			// Toast.makeText(context, "有应用被重启", Toast.LENGTH_LONG).show();
		}

		else if (Intent.ACTION_PACKAGE_INSTALL.equals(action)) {
			// Toast.makeText(context, "有应用被安装", Toast.LENGTH_LONG).show();
		}
	}

	// private Handler packageHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// // 定义一个Handler，用于处理下载线程与UI间通讯
	// switch (msg.what) {
	// case 0:
	// break;
	// }
	// super.handleMessage(msg);
	// }
	// };

	/**
	 * 应用下载
	 */
	private class DownloadSoftTask extends GenericTask {
		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				TaskParams param = null;
				if (params.length <= 0) {
					return TaskResult.FAILED;
				}
				param = params[0];

				String result = HttpRequest.downSoftTask(param.getString("softPN"));

				if (HttpRequest.FAIL_STATE.equals(result)) { // 失败
					DialogUtils.mesTip("软件下载失败，请稍候在试!", false);
				} else if (HttpRequest.TOKEN_ILLEGAL.equals(result)) { // 用户登录Token过期
					DialogUtils.reLogin(Database.currentActivity);
				} else {
					final GameTask resultTask = JsonHelper.fromJson(result, GameTask.class);
					Database.currentActivity.runOnUiThread(new Runnable() {
						public void run() {
							if (CmdUtils.SUCCESS_CODE.equals(resultTask.getValue())) {// 成功
								Toast.makeText(Database.currentActivity, "恭喜您，获得下载应用赠送的" + resultTask.getCount() + " 金豆!", Toast.LENGTH_LONG).show();
							} else if (CmdUtils.FAIL_CODE.equals(resultTask.getValue())) { // 失败
								Toast.makeText(Database.currentActivity, "获取下载应用赠送金豆失败!", Toast.LENGTH_LONG).show();
							}
						}
					});
				}

			} catch (Exception e) {
			}
			return TaskResult.OK;
		}
	}
}