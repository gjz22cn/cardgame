/**
 * BaseActivity.java [v 1.0.0]
 * classes : ui.base.BaseActivity
 * auth : yinhongbiao
 * time : 2012 2012-11-7 下午10:27:50
 */
package com.lordcard.ui.base;

import com.zzyddz.shui.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;

import com.lordcard.common.task.TaskFeedback;
import com.lordcard.common.task.TaskManager;
import com.lordcard.common.task.base.Feedback;
import com.lordcard.common.upgrade.UpdateUtils;
import com.lordcard.common.util.ActivityPool;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.AudioPlayUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.network.cmdmgr.ClientCmdMgr;
import com.lordcard.network.http.GameHttpTask;
import com.lordcard.ui.StartActivity;
import com.lordcard.ui.dizhu.DoudizhuMainGameActivity;
import com.sdk.util.SDKFactory;

/**
 * ui.base.BaseActivity
 * 
 * @author yinhb <br/>
 *         create at 2012 2012-11-7 下午10:27:50
 */
public class BaseActivity extends Activity {

	protected TaskManager taskManager = new TaskManager();
	protected Feedback feedback = TaskFeedback.getInstance(TaskFeedback.DIALOG_MODE);
	protected MultiScreenTool mst = null;

	@SuppressWarnings("rawtypes")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Database.currentActivity = this;
		overridePendingTransition(R.anim.fade, R.anim.out_righttoleft);
		Database.GAME_TYPE = Constant.GAME_TYPE_DIZHU;
		// 判断横屏竖屏,初始化多屏幕适应工具
		if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			mst = MultiScreenTool.singleTonHolizontal();
		} else {
			mst = MultiScreenTool.singleTonVertical();
		}
		mst.checkWidthAndHeight();
		if (this.getClass().equals(SDKFactory.getLoginView())) {
			return;
		}
		ActivityPool.push(this);
		boolean isActiv = false; // 是否可以激活
		// 进入游戏等待页面激活
		if (Database.currentActivity != null) {
			Class clazz = getClass();
			if (clazz.equals(DoudizhuMainGameActivity.class)) {
				isActiv = true;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_DOWN://游戏音量减小
				AudioPlayUtils.getInstance().lowerVoice();
				return true;
			case KeyEvent.KEYCODE_VOLUME_UP://游戏音量增大
				AudioPlayUtils.getInstance().raiseVoice();
				return true;
			default:
				break;
		}
		return super.onKeyDown(keyCode, event);
	}

//	private void activite() {
//		ThreadPool.startWork(new Runnable() {
//
//			public void run() {
//				String batchId = ChannelUtils.getBatchId();
//				if (TextUtils.isEmpty(batchId))
//					return; // 没有批次号 则直接返回
//				final SharedPreferences preferences = getApplication().getSharedPreferences(Constant.GAME_ACTIVITE, Context.MODE_PRIVATE);
//				boolean is_activite = preferences.getBoolean("is_activite", false);
//				if (!is_activite) { // 未激活
//					String result = HttpRequest.activateFromFront(batchId);
//					if ("0".equals(result) || "6".equals(result)) {// 激活成功 或 已注册
//						Editor editor = preferences.edit();
//						editor.putBoolean("is_activite", true); // 已激活
//						editor.commit();
//					}
//				}
//			}
//		});
//	}

	@Override
	protected void onStart() {
		super.onStart();
		Database.currentActivity = this;
		Class<?> clazz = this.getClass();
		if (!clazz.equals(StartActivity.class)) {// 是否是启动页面
			if (!ActivityUtils.isGameView() && !ActivityUtils.isPayView()) { //非游戏界面,和支付界面
				ClientCmdMgr.closeClient();
				// 检查网络情况(非游戏界面，游戏界面交给游戏内部处理 )
				if (!ActivityUtils.isNetworkAvailable()) {
					//所有的无网络请求都转到登录界面处理
					if (!clazz.equals(SDKFactory.getLoginView())) {
						Intent intent = new Intent();
						intent.setClass(this, SDKFactory.getLoginView());
						startActivity(intent);
					}
				} else {
					//有网时检查相关初始配置是否存在
					GameHttpTask.checkInitCfg();
				}
			}
			mst.checkWidthAndHeight();
		}
		// 有新版本 提示下载
		if (Database.HAS_NEW_VERSION) {
			boolean isWifiOpen = ActivityUtils.isOpenWifi();//wifi是否打开
			if (isWifiOpen) {
				Database.UPDATED_STYLE = true;
				Database.HAS_NEW_VERSION = false;
				UpdateUtils.downLoadNewVesionSev();// 静默
				Log.d("newVersionCode", "后台下载更新");
			} else {
				Database.UPDATED = true;
				UpdateUtils.newVersionTip();
				// 新版本提示 每次启动只提示一次
				Database.HAS_NEW_VERSION = false;
			}
		}
	}

	public void onResume() {
		super.onResume();
		Database.currentActivity = this;
		// 获取得屏幕分辨率
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if (dm.widthPixels > dm.heightPixels) {
			Database.SCREEN_WIDTH = dm.widthPixels;
			Database.SCREEN_HEIGHT = dm.heightPixels;
		} else {
			Database.SCREEN_WIDTH = dm.heightPixels;
			Database.SCREEN_HEIGHT = dm.widthPixels;
		}
		try {
			//MobclickAgent.onResume(this, ChannelUtils.getUappKey(), ChannelUtils.getUChannel());
		} catch (Exception e) {}
	}

	public void onPause() {
		super.onPause();
		//MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageUtil.clearGifCache();
		taskManager.cancelAll();
		taskManager = null;
		feedback = null;
		mst = null;
		if (!ActivityUtils.isGameView()) { //非游戏界面
			if (Database.userMap != null) {
				Database.userMap.clear();
			}
			ClientCmdMgr.closeClient();
		}
	}

	public void finishSelf() {
		ActivityPool.remove(this);
		this.finish();
	}
}
