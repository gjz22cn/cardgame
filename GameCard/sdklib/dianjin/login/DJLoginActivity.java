package com.sdk.dianjin.login;

import com.zzyddz.shui.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bodong.dpaysdk.DPayConfig;
import com.bodong.dpaysdk.DPayManager;
import com.bodong.dpaysdk.listener.DPayLoginListener;
import com.bodong.dpaysdk.listener.DPayLogoutListener;
import com.bodong.dpaysdk.listener.DPaySDKExitListener;
import com.umeng.analytics.MobclickAgent;
import com.lordcard.common.task.GenericTask;
import com.lordcard.common.task.base.TaskParams;
import com.lordcard.common.task.base.TaskResult;
import com.lordcard.common.upgrade.UPVersion;
import com.lordcard.common.upgrade.UpdateUtils;
import com.lordcard.common.util.ActivityPool;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.JsonResult;
import com.lordcard.entity.NoticesVo;
import com.lordcard.net.http.HttpRequest;
import com.lordcard.net.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.lordcard.ui.base.BaseActivity;
import com.lordcard.ui.dizhu.DoudizhuRoomListActivity;
import com.lordcard.ui.view.dialog.FindPwdDialog;

@SuppressLint({ "HandlerLeak", "DefaultLocale" })
public class DJLoginActivity extends BaseActivity implements OnClickListener {
	private Button loginBtn, gonggao, updateBtn;
	private RelativeLayout gameBg = null;
	private RelativeLayout katong, loginBg = null;
	private static Boolean boolean1;
	private int djUserID;
	private String djUserName;
	private Map<String, String> djMap = null;
	private SharedPreferences sharedPrefrences;
	private Editor editor;
	private TextView titleView, contentView, timeView, textName, textTeam;
	private RelativeLayout ggdetaiLayout;
	private static int i;
	private static int pxz;
	private TextView t1;
	private Timer timer;
	private boolean ToastIsShow = false;//土司弹出过（避免土司重复弹出，霸占屏幕）
	private ScrollView scrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置标题栏不显示
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.dj_login);
		initComponent();
		i = 1;
		pxz = 0;
		t1 = (TextView) findViewById(R.id.t1);
		timer = new Timer();
		boolean1 = true; //公告标志
		mst.adjustView(findViewById(R.id.layout));
		GenericTask gameNoticeTask = new GameNoticeTask();
		gameNoticeTask.execute();
		taskManager.addTask(gameNoticeTask);
		// 退出登录注销
		if (getIntent() != null) {
			if (getIntent().getExtras() != null) {
				if (getIntent().getExtras().get("logout") != null) {
					DPayManager.logout();
				}
			}
		}

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				pxz = pxz + 13;
				LayoutParams lp = (LayoutParams) t1.getLayoutParams();
				lp.height = mst.adjustYIgnoreDensity(pxz);
				t1.setLayoutParams(lp);
				i = i + 1;
				break;
			case 2:
				pxz = pxz - 13;
				LayoutParams lp2 = (LayoutParams) t1.getLayoutParams();
				lp2.height = mst.adjustYIgnoreDensity(pxz);
				t1.setLayoutParams(lp2);
				i = i - 1;
				break;
			case 3:
				scrollView.setVisibility(View.VISIBLE);
				gonggao.setClickable(true);
				break;
			case 4:
				ggdetaiLayout.setVisibility(View.GONE);
				gonggao.setClickable(true);
				break;
			default:
				break;
			}
		};
	};

	private void initComponent() {
		initView();// 初始化View控件
		// 初始化sdk
		DPayManager.init(this);
		// 设置SDK横竖屏，默认随系统设置
		// DPayManager.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// 设置退出平台监听
		DPayManager.setSDKExitListener(new DPaySDKExitListener() {
			public void onExit() {}
		});
		// 设置登录监听
		DPayManager.setLoginListener(new DPayLoginListener() {
			@Override
			public void onLogin() {
				if (DPayManager.isUserLoggedIn()) {
					djUserID = DPayManager.getUserId();
					djUserName = DPayManager.getUserName();

					djMap = new HashMap<String, String>();
					djMap.put("thirdUid", String.valueOf(djUserID));
					djMap.put("thirdUname", djUserName);
					djMap.put("memo", JsonHelper.toJson(djMap));

					DJRegisterTask task = new DJRegisterTask();
					task.setFeedback(feedback);
					task.execute();
					taskManager.addTask(task);
				}
			}
		});
		// 设置注销监听
		DPayManager.setLogoutListener(new DPayLogoutListener() {
			@Override
			public void onLogout() {}
		});
	}

	@Override
	public void onBackPressed() {
		DPayManager.logout();
		ActivityPool.exitApp();
	}

	/**
	 * 初始化view控件
	 */
	private void initView() {
		loginBtn = (Button) findViewById(R.id.login_btn);
		gonggao = (Button) findViewById(R.id.gonggao);
		gameBg = (RelativeLayout) findViewById(R.id.game_bg);
		gameBg.setBackgroundResource(R.drawable.join_bj);

		katong = (RelativeLayout) findViewById(R.id.katong);
		katong.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.katong,true));

		loginBg = (RelativeLayout) findViewById(R.id.login_bg);
		loginBg.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.entry_bar,true));

		loginBtn.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		titleView = (TextView) findViewById(R.id.gg_title);
		contentView = (TextView) findViewById(R.id.gg_content);
		timeView = (TextView) findViewById(R.id.gg_time);
		textName = (TextView) findViewById(R.id.gg_name);
		textTeam = (TextView) findViewById(R.id.gg_team);
		updateBtn = (Button) findViewById(R.id.update);
		updateBtn.setOnClickListener(this);
		ggdetaiLayout = (RelativeLayout) findViewById(R.id.gg_detail);
		scrollView = (ScrollView) findViewById(R.id.room_list_scrollView);
		gonggao.setOnClickListener(this);
	}

	/**
	 * 处理单击事件的onClickListener
	 */

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.login_btn:
			djLogin();
			break;
		case R.id.forget_pwd:
			FindPwdDialog findPwdDialog = new FindPwdDialog(DJLoginActivity.this);
			findPwdDialog.show();
			break;
		case R.id.gonggao:
			MobclickAgent.onEvent(DJLoginActivity.this, "公告");
			gonggao.setClickable(false);
			if (boolean1 == false) {
				scrollView.setVisibility(View.GONE);
				timer.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (i <= 20 && i >= 2) {
							handler.sendEmptyMessage(2);
						} else {
							pxz = 0;
							i = 1;
							this.cancel();
							timer.scheduleAtFixedRate(new TimerTask() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									handler.sendEmptyMessage(4);
									this.cancel();
								}
							}, 500l, 30l);

						}

					}
				}, 0l, 30l);
				boolean1 = true;
			} else {
				gonggao.setClickable(false);
				ggdetaiLayout.setVisibility(View.VISIBLE);
				timer.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						if (i >= 0 && i <= 20) {
							handler.sendEmptyMessage(1);
						} else {
							pxz = 260;
							i = 20;
							handler.sendEmptyMessage(3);

							this.cancel();
						}

					}
				}, 0l, 30l);
				boolean1 = false;
			}
			break;
		case R.id.update:// 更新游戏
			if (Database.UPDATED) {
				UpdateUtils.updateTip(null,false,UPVersion.infolis, this, true);
			} else {
				if (!ToastIsShow) {
					DialogUtils.mesToastTip("您当前已经是最新版本，暂时无需更新！");
					ToastIsShow = true;
				}
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 登录跳转
	 * 
	 * @param gameUser
	 */
	private synchronized void userLogin(GameUser gameUser) {
		Database.USER = gameUser;
		if (null == Database.USER) {
			Log.e("debugs", "DJLoginActivity--登录跳转---Database.USER  为空");
		}
		Database.SIGN_KEY = gameUser.getAuthKey();

		Intent intent = new Intent();
		intent.setClass(DJLoginActivity.this, DoudizhuRoomListActivity.class);
		startActivity(intent);
	}

	private synchronized void djLogin() {
		if (!DPayManager.isUserLoggedIn()) {
			DPayManager.startLoginActivity(DJLoginActivity.this, DPayConfig.Action.ACTION_LOGIN);
		} else {
			djUserID = DPayManager.getUserId();
			djUserName = DPayManager.getUserName();

			djMap = new HashMap<String, String>();
			djMap.put("thirdUid", String.valueOf(djUserID));
			djMap.put("thirdUname", djUserName);
			djMap.put("memo", JsonHelper.toJson(djMap));

			GenericTask task = new DJRegisterTask();
			task.setFeedback(feedback);
			task.execute();
			taskManager.addTask(task);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		recyleDrawable();
		// 移除平台监听
		DPayManager.setSDKExitListener(null);
		DPayManager.setLoginListener(null);
		DPayManager.setLogoutListener(null);
	}

	public void recyleDrawable() {
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
		layout.removeAllViews();
	}

	/**
	 * 点金注册账号 com.lordcard.ui.LoginTask
	 * 
	 * @author Administrator <br/>
	 *         create at 2013 2013-4-8 下午4:17:56
	 */
	private class DJRegisterTask extends GenericTask {

		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				String result = HttpRequest.thirdlogin(djMap);
				JsonResult jsonResult = JsonHelper.fromJson(result, JsonResult.class);
				if (JsonResult.SUCCESS.equals(jsonResult.getMethodCode())) {
					String gameUserJson = jsonResult.getMethodMessage();
					GameUser gameUser = JsonHelper.fromJson(gameUserJson, GameUser.class);
					//保存注册账号和密码
					String account = gameUser.getAccount();
					if (!TextUtils.isEmpty(gameUser.getRelaAccount())) {
						account = gameUser.getRelaAccount();
					}
					ActivityUtils.saveAccount(account, gameUser.getMd5Pwd());
					userLogin(gameUser);
				} else {
					DialogUtils.mesTip(jsonResult.getMethodMessage(), false);
				}

			} catch (Exception e) {
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}
	}

	/**
	 * 游戏公告信息
	 */
	private class GameNoticeTask extends GenericTask {

		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				String result = HttpUtils.post(HttpURL.GAME_NOTICE_URL, null);
				final NoticesVo notices = JsonHelper.fromJson(result, NoticesVo.class);

				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				Date dt = format.parse(notices.getCtime());
				format = new SimpleDateFormat("yyyy-MM-dd");
				final String time1 = format.format(dt);
				final String content1 = notices.getContent();// 获取的公告内容
				sharedPrefrences = getSharedPreferences("account_ban", MODE_WORLD_READABLE);
				final String time2 = sharedPrefrences.getString("time", null);
				final String content2 = sharedPrefrences.getString("content", null);// 保存的公告内容
				//				prefrences = getSharedPreferences("account_gg", MODE_WORLD_READABLE);

				runOnUiThread(new Runnable() {
					public void run() {

						ggdetaiLayout.setOnClickListener(null);
						ggdetaiLayout.setVisibility(View.VISIBLE);

						if (time1.equals(time2) && content1.equals(content2)) {
							ggdetaiLayout.setVisibility(View.GONE);
							titleView.setText(notices.getTitle());
							contentView.setText("    " + notices.getContent());
							timeView.setText(time1);
							textName.setText("尊敬的玩家：");
							textTeam.setText("掌中游斗地主");
							boolean1 = true;
						} else {
							boolean1 = false;
							ggdetaiLayout.setVisibility(View.VISIBLE);
							titleView.setText(notices.getTitle());
							contentView.setText("    " + notices.getContent());
							timeView.setText(time1);
							textName.setText("尊敬的玩家：");
							textTeam.setText("掌中游斗地主运营团队");
							editor = sharedPrefrences.edit();
							editor.putString("content", content1);
							editor.putString("time", time1);
							editor.commit();
							timer.schedule(new TimerTask() {

								@Override
								public void run() {
									if (i <= 20 && i >= 0) {
										handler.sendEmptyMessage(1);
									} else {
										i = 20;
										pxz = 260;
										handler.sendEmptyMessage(3);

										this.cancel();

									}

								}
							}, 2000l, 30l);
						}
					}
				});

			} catch (Exception e) {
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}
	}

}
