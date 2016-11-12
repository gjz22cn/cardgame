package com.lordcard.ui;

import com.zzyddz.shui.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
/* james hard code remove cm module */
//import cn.cmgame.billing.api.GameInterface;
import cn.egame.terminal.paysdk.EgamePay;
import cn.play.dserv.CheckTool;
import cn.play.dserv.ExitCallBack;

import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.mydb.DBHelper;
import com.lordcard.common.schedule.AutoTask;
import com.lordcard.common.schedule.ScheduledTask;
import com.lordcard.common.task.GenericTask;
import com.lordcard.common.task.base.TaskParams;
import com.lordcard.common.task.base.TaskResult;
import com.lordcard.common.upgrade.UpdateService;
import com.lordcard.common.upgrade.UpdateUtils;
import com.lordcard.common.util.ActivityPool;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.DateUtil;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.EncodeUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.PatternUtils;
import com.lordcard.common.util.PreferenceHelper;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.JsonResult;
import com.lordcard.entity.NoticesVo;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.ui.base.BaseActivity;
import com.lordcard.ui.base.FastJoinTask;
import com.lordcard.ui.base.ILoginView;
import com.lordcard.ui.dizhu.DoudizhuRoomListActivity;
import com.lordcard.ui.personal.PersonnalDoudizhuActivity;
import com.lordcard.ui.view.dialog.AccountBindDialog;
import com.lordcard.ui.view.dialog.ChangeAccountDialog;
import com.lordcard.ui.view.dialog.GameDialog;
import com.umeng.analytics.MobclickAgent;

@SuppressLint({ "HandlerLeak", "DefaultLocale", "SimpleDateFormat", "WorldReadableFiles" })
public class LoginActivity extends BaseActivity implements ILoginView, OnTouchListener, OnGestureListener {

	private TextView accountTv, goldTv; // 账号，金豆
	private ImageView headIv;// 头像
	private Button loginBtn, changeAccountBtn, bindAccountBtn, quickMatch, quickLogin, updateBtn;
	private AccountBindDialog mAccountBindDialog;// 绑定账号对话框
	private SharedPreferences sharedPrefrences;
//	private SharedPreferences sharedViewfiper;
	private Editor editor;
	private GameDialog netWorkDialog = null;
	public static final String LOGIN_VIEW_FLIPPER = "login_view_flipper";
//	private boolean isBackState = false;//退出状态，标记当前是否准备退出的状态（点击两次退出，两次间隔时间超过10秒表示重新开始标记）
	public static final String KEY_USER = "user_key";
	/**更新界面账号*/
	public static final int HANDLER_WHAT_LOGIN_UPDATE_USER = 1000;
	/**注册账号*/
	public static final int HANDLER_WHAT_LOGIN_RESIGSTER_USER = 1001;
	/**公告打开*/
	public static final int HANDLER_WHAT_LOGIN_ANNOUNCEMENT_OPEN = 1003;
	/**公告关闭*/
	public static final int HANDLER_WHAT_LOGIN_ANNOUNCEMENT_CLOSE = 1004;
	/**公告显示*/
	public static final int HANDLER_WHAT_LOGIN_ANNOUNCEMENT_VISIBLE = 1005;
	/**公告隐藏*/
	public static final int HANDLER_WHAT_LOGIN_ANNOUNCEMENT_GONE = 1006;
	// 登录的时候需要的几个常量
	public final static String ACCOUNT = "account";
	public final static String PASSWORD = "userPwd";
	private RelativeLayout gameBg = null;
	private RelativeLayout katong, loginBg = null;
	private ChangeAccountDialog mChangeAccountDialog = null;
	// private TextView zhezhao;
	// 是否更新支付数据
	private RelativeLayout ggdetaiLayout;
	private TextView titleView, contentView, timeView, textName, textTeam;
	private static Boolean boolean1;
	private static int i;
	/**像素增加*/
	private static int PXZ;
	/**像素最大值*/
	private static int PX_MST;
	/** 像素最大值 */
	private static int PX_LAST_MST;
	/** 是否有公告内容 */
	private static boolean HAS_GONGGAO = false;
	private TextView t1;
	private ScrollView scrollView;
	private Button gonggao;
	private GenericTask rjoinTask;
	public static DBHelper dbHelper;
	private GestureDetector mGestureDetector = null;
	private ViewFlipper mViewFlipper;
	private boolean isShown;
	private int[] imageId = new int[] {/* R.drawable.login_guide1, R.drawable.login_guide2, R.drawable.login_guide3 xs_del*/};
	private AutoTask autoTask; // 定时任务
	private ProgressDialog loginProgress;
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case HANDLER_WHAT_LOGIN_UPDATE_USER:
					setUserInfo();
					break;
				case HANDLER_WHAT_LOGIN_RESIGSTER_USER:// 注册账号
					GameUser gameUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
					if (gameUser != null) {
						DialogUtils.mesToastTip("当前已有账号登录,无需再注册!");
						return;
					}
					loginProgress.setMessage("登录中,请稍候...");
					loginProgress.show();
					register();
					break;
				case HANDLER_WHAT_LOGIN_ANNOUNCEMENT_OPEN:// 公告展开
					PXZ = PXZ + PX_MST;
					LayoutParams lp = (LayoutParams) t1.getLayoutParams();
					lp.height = PXZ;
					t1.setLayoutParams(lp);
					i = i + 1;
					break;
				case HANDLER_WHAT_LOGIN_ANNOUNCEMENT_CLOSE:// 公告收拢
					PXZ = PXZ - PX_MST;
					LayoutParams lp2 = (LayoutParams) t1.getLayoutParams();
					lp2.height = PXZ;
					t1.setLayoutParams(lp2);
					i = i - 1;
					break;
				case HANDLER_WHAT_LOGIN_ANNOUNCEMENT_VISIBLE://公告显示
					PXZ = PX_LAST_MST;
					i = 20;
					if (autoTask != null) {
						autoTask.stop(true);
						autoTask = null;
					}
					scrollView.setVisibility(View.VISIBLE);
					gonggao.setClickable(true);
					break;
				case HANDLER_WHAT_LOGIN_ANNOUNCEMENT_GONE://公告隐藏
					PXZ = 0;
					i = 1;
					if (autoTask != null) {
						autoTask.stop(true);
						autoTask = null;
					}
					ggdetaiLayout.setVisibility(View.GONE);
					gonggao.setClickable(true);
					break;
				default:
					break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置标题栏不显示
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.game_login);
		mGestureDetector = new GestureDetector(this);
		initView();
		i = 1;
		PXZ = 0;
		PX_MST = mst.adjustYIgnoreDensity(13);// 公告每段高度
		PX_LAST_MST = PX_MST * 20;// 高度总高度
		t1 = (TextView) findViewById(R.id.t1);
		boolean1 = true; // 公告标志
		dbHelper = new DBHelper(CrashApplication.getInstance());
		GenericTask gameNoticeTask = new GameNoticeTask();
		gameNoticeTask.execute();
		taskManager.addTask(gameNoticeTask);
		mst.adjustView(gameBg);
		loginProgress = DialogUtils.getWaitProgressDialog(this, "登录中,请稍候...");
		login(); // 登录
		EgamePay.init(this);
	    //PreferenceHelper.getMyPreference().getEditor().putBoolean("jingyin", !GameInterface.isMusicEnabled()).commit();
	    PreferenceHelper.getMyPreference().getEditor().putBoolean("jingyin", true).commit();
	}

	@Override
	public void onBackPressed() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		// 关闭升级下载通知
		notificationManager.cancel(UpdateService.NOTIFICATION_ID);// 多次下载的
		UpdateUtils.stopDownLoadNewVesionSev(this);
		ActivityPool.exitApp();
	}

	/**
	 * 初始化view控件
	 */
	private void initView() {
		mViewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
		mViewFlipper.setOnTouchListener(new android.view.View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		});
		mViewFlipper.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				return;
			}
		});
		String isShow = GameCache.getStr(LOGIN_VIEW_FLIPPER);
//		sharedViewfiper = getSharedPreferences("viewflipper", MODE_WORLD_READABLE);
//		isShown = sharedViewfiper.getBoolean("flipper", false);
//		if (!isShown) {
		/*xs_del
		 * if (isShow == null || "0".equals(isShow)) { //没有展示过
			mViewFlipper.setVisibility(View.VISIBLE);
			for (int i = 0; i < imageId.length; i++) {
				ImageView mImageView = new ImageView(this);
				mImageView.setBackgroundDrawable(ImageUtil.getResDrawable(imageId[i], false));
				mImageView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				mViewFlipper.addView(mImageView);
			}
			ImageView mImageView = new ImageView(this);
			mImageView.setBackgroundColor(Color.TRANSPARENT);
			mImageView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			mViewFlipper.addView(mImageView);
		}*/
		mChangeAccountDialog = new ChangeAccountDialog(this, handler);
		mAccountBindDialog = new AccountBindDialog(this, handler);
		updateBtn = (Button) findViewById(R.id.update);
		updateBtn.setOnClickListener(mOnClickListener);
		loginBtn = (Button) findViewById(R.id.game_login_in);
		loginBtn.setOnClickListener(mOnClickListener);
		quickMatch = (Button) findViewById(R.id.game_quick_match);
		quickMatch.setOnClickListener(mOnClickListener);
		quickLogin = (Button) findViewById(R.id.game_quick_login);
		quickLogin.setOnClickListener(mOnClickListener);
		changeAccountBtn = (Button) findViewById(R.id.game_login_change_account);
		changeAccountBtn.setOnClickListener(mOnClickListener);
		bindAccountBtn = (Button) findViewById(R.id.game_login_bind_account);
		//移动不允许账号绑定
		bindAccountBtn.setEnabled(false);
		bindAccountBtn.setVisibility(View.INVISIBLE);
		bindAccountBtn.setText("");
		//切换账号
		changeAccountBtn.setEnabled(false);
		changeAccountBtn.setVisibility(View.INVISIBLE);
		bindAccountBtn.setOnClickListener(mOnClickListener);
		accountTv = (TextView) findViewById(R.id.game_login_id);
		goldTv = (TextView) findViewById(R.id.game_login_gold);
		accountTv.setText("游客"); // 账号
		goldTv.setText("20000"); //金豆
		headIv = (ImageView) findViewById(R.id.game_login_img);
		gameBg = (RelativeLayout) findViewById(R.id.layout);
		gameBg.setBackgroundResource(R.drawable.mmbg);
		katong = (RelativeLayout) findViewById(R.id.katong);
		katong.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.katong, true));
		titleView = (TextView) findViewById(R.id.gg_title);
		contentView = (TextView) findViewById(R.id.gg_content);
		timeView = (TextView) findViewById(R.id.gg_time);
		textName = (TextView) findViewById(R.id.gg_name);
		textTeam = (TextView) findViewById(R.id.gg_team);
		ggdetaiLayout = (RelativeLayout) findViewById(R.id.gg_detail);
		scrollView = (ScrollView) findViewById(R.id.room_list_scrollView);
		gonggao = (Button) findViewById(R.id.gonggao);
		gonggao.setOnClickListener(mOnClickListener);
		loginBg = (RelativeLayout) findViewById(R.id.login_bg);
		loginBg.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.loginbj, true));
	}

	@Override
	public void onResume() {
		super.onResume();
		if (loginProgress != null) {
			loginProgress.dismiss();
		}
		//如果有网络，则关闭提示框
		if (!ActivityUtils.isNetworkAvailable()) {
			showNetWorkDialog();
			return;
		}
		GameUser gameUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		if (gameUser == null) {
			String isShow = GameCache.getStr(LOGIN_VIEW_FLIPPER);
			if ("1".equals(isShow)) {
				loginProgress.setMessage("登录中,请稍候...");
				loginProgress.show();
			}
			login();
		} else {
			setUserInfo();
		}
	}

	/**
	 * 设置用户信息
	 * 
	 * @param gameUser
	 */
	private void setUserInfo() {
		GameUser gameUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		if (gameUser != null) {
			String account = gameUser.getAccount();
			if (!TextUtils.isEmpty(gameUser.getRelaAccount())) {
				account = gameUser.getRelaAccount();
			}
			accountTv.setText(account); // 账号
			goldTv.setText(PatternUtils.changeZhidou(0 > gameUser.getBean() ? 0 : gameUser.getBean())); //金豆
			if (!TextUtils.isEmpty(gameUser.getGender()) && "1".equals(gameUser.getGender())) {// 女性
				headIv.setBackgroundResource(R.drawable.nv_user_img);
			} else {
				headIv.setBackgroundResource(R.drawable.nan_user_img);
			}
		}
	}

	private synchronized void login() {
		Database.LOGIN_TIME = DateUtil.formatTimesTampDate(new Date());
		GameUser localUser = ActivityUtils.loadLocalAccount();// 加载最近登录账号
		if (localUser == null) { // MAC注册新账号
			register();
		} else { // 本地账号登录
			// 登录游戏
			GenericTask loginTask = new LoginTask();
			TaskParams params = new TaskParams();
			params.put(ACCOUNT, localUser.getAccount());
			params.put(PASSWORD, localUser.getMd5Pwd());
			loginTask.execute(params);
			taskManager.addTask(loginTask);
		}
	}

	/**
	 * 注册 账号
	 */
	private void register() {
		GenericTask registerTask = new RegisterTask();
		registerTask.execute();
		taskManager.addTask(registerTask);
	}

	/**
	 * 登录跳转
	 * 
	 * @param gameUser
	 */
	private void userLogin(GameUser gameUser) {
//		Database.USER = gameUser;
		Database.ROOM_FRESH_TIME = gameUser.getRoomTime();
		Database.GAME_SERVER = gameUser.getGameServer();
		gameUser.setRound(0);
//		Database.SIGN_KEY = gameUser.getAuthKey();
		GameCache.putObj(CacheKey.GAME_USER, gameUser);
		//保存登录过的账号到本地
		SharedPreferences sharedData = getApplication().getSharedPreferences(Constant.GAME_ACTIVITE, Context.MODE_PRIVATE);
		Editor editor = sharedData.edit();
		editor.putString(ACCOUNT, gameUser.getAccount());
		editor.commit();
		runOnUiThread(new Runnable() {

			public void run() {
				setUserInfo();
			}
		});
	}

	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mViewFlipper.getVisibility() != View.VISIBLE) {
				switch (v.getId()) {
					case R.id.gonggao:
						gonggao.setClickable(false);
						if (boolean1 == false) {
							scrollView.setVisibility(View.GONE);
							if (autoTask != null) {
								autoTask.stop(true);
								autoTask = null;
							}
							/** 画卷收拢 */
							autoTask = new AutoTask() {

								public void run() {
									if (i >= 1) {
										handler.sendEmptyMessage(HANDLER_WHAT_LOGIN_ANNOUNCEMENT_CLOSE);
									} else {
										PXZ = 0;
										i = 1;
										stop(true);
										handler.sendEmptyMessage(HANDLER_WHAT_LOGIN_ANNOUNCEMENT_GONE);
									}
								}
							};
							ScheduledTask.addRateTask(autoTask, 30);
							boolean1 = true;
						} else {
							if (HAS_GONGGAO) {
								gonggao.setClickable(false);
								ggdetaiLayout.setVisibility(View.VISIBLE);
								if (autoTask != null) {
									autoTask.stop(true);
									autoTask = null;
								}
								/** 画卷展开 */
								autoTask = new AutoTask() {

									public void run() {
										if (i >= 0 && i <= 20) {
											handler.sendEmptyMessage(HANDLER_WHAT_LOGIN_ANNOUNCEMENT_OPEN);
										} else {
											PXZ = PX_LAST_MST;
											i = 20;
											handler.sendEmptyMessage(HANDLER_WHAT_LOGIN_ANNOUNCEMENT_VISIBLE);
											stop(true);
										}
									}
								};
								ScheduledTask.addRateTask(autoTask, 30);
								boolean1 = false;
							} else {
								runOnUiThread(new Runnable() {

									public void run() {
										DialogUtils.mesToastTip("亲，暂时没有公告哟~");
										gonggao.setClickable(true);
									}
								});
							}
						}
						break;
					case R.id.game_login_in:// 游戏大厅
						MobclickAgent.onEvent(LoginActivity.this, "游戏大厅点击");
						if (!ActivityUtils.isNetworkAvailable()) {
							showNetWorkDialog();
						} else {
							GameUser gameUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
							if (gameUser == null) {
//								login(true);
								showLoginDialog();
							} else {
								Intent intent = new Intent();
								intent.setClass(LoginActivity.this, DoudizhuRoomListActivity.class);
								startActivity(intent);
							}
						}
						break;
					case R.id.game_login_change_account:// 切换账号
						MobclickAgent.onEvent(LoginActivity.this, "切换账号点击");
						if (!ActivityUtils.isNetworkAvailable()) {
							showNetWorkDialog();
						} else {
							showLoginDialog();
						}
						break;
					case R.id.game_login_bind_account:// 绑定账号
						MobclickAgent.onEvent(LoginActivity.this, "绑定账号点击");
						if (!ActivityUtils.isNetworkAvailable()) {
							showNetWorkDialog();
						} else {
							GameUser gameUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
							if (gameUser == null) {
//								login(true);
								showLoginDialog();
							} else {
								if (!mAccountBindDialog.isShowing()) {
									mAccountBindDialog.show();
									mAccountBindDialog.initView();
								}
							}
						}
						break;
					case R.id.game_quick_match:// 单机
						MobclickAgent.onEvent(LoginActivity.this, "单机游戏点击");
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this, PersonnalDoudizhuActivity.class);
						startActivity(intent);
						break;
					case R.id.game_quick_login:// 快速游戏
						MobclickAgent.onEvent(LoginActivity.this, "快速游戏点击");
						if (!ActivityUtils.isNetworkAvailable()) {
							showNetWorkDialog();
						} else {
							GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
							if (gu == null) {
								showLoginDialog();
							} else {
								FastJoinTask.fastJoin();
							}
								
						}
						break;
					case R.id.update:// 升级游戏
						if (Database.UPDATED) {
							UpdateUtils.newVersionTip();
						} else {
							if (Math.abs(System.currentTimeMillis() - Constant.CLICK_TIME) >= Constant.SPACING_TIME) {
								Constant.CLICK_TIME = System.currentTimeMillis();
								DialogUtils.mesToastTip("您当前已经是最新版本，暂时无需更新！");
							}
						}
						break;
					default:
						break;
				}
			}
		}
	};

	/**
	 * 登录游戏 com.lordcard.ui.LoginTask
	 * 
	 * @author Administrator <br/>
	 *         create at 2013 2013-4-8 下午4:17:56
	 */
	private class LoginTask extends GenericTask {

		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				TaskParams param = null;
				if (params.length <= 0) {
					return TaskResult.FAILED;
				}
				param = params[0];
				// 登录
				String loginPwd = param.getString(PASSWORD);
				if (loginPwd.length() < 30) { // 长度大于30则认为当前密码是保存的密文
					loginPwd = EncodeUtils.MD5(loginPwd);
				}
				String result = HttpRequest.login(param.getString(ACCOUNT), loginPwd);
				if (TextUtils.isEmpty(result)) {
					return TaskResult.FAILED;
				}
				JsonResult jsonResult = JsonHelper.fromJson(result, JsonResult.class);
				if (JsonResult.SUCCESS.equals(jsonResult.getMethodCode())) {
					loginProgress.dismiss();
					String gameUserJson = jsonResult.getMethodMessage();
					Log.d("gameUserJson", "登录gameUserJson: " + gameUserJson);
					GameUser gameUser = JsonHelper.fromJson(gameUserJson, GameUser.class);
					String account = gameUser.getAccount();
					if (!TextUtils.isEmpty(gameUser.getRelaAccount())) {
						account = gameUser.getRelaAccount();
					}
					ActivityUtils.saveAccount(account, gameUser.getMd5Pwd());
//					Database.GAME_USER_AUTH_KEY = gameUser.getAuthKey();
					userLogin(gameUser);
					// 获取房间更新时间
					String roomTime = gameUser.getRoomTime();
					if (roomTime != null) {
						Database.ROOM_UPDATE = roomTime;
					}
				} else {
					mesTip(jsonResult.getMethodMessage(), false, false);
					return TaskResult.FAILED;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}
	}

	/**
	 * 注册账号 com.lordcard.ui.LoginTask
	 * 
	 * @author Administrator <br/>
	 *         create at 2013 2013-4-8 下午4:17:56
	 */
	private class RegisterTask extends GenericTask {

		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				// 注册
				String result = HttpRequest.register();
				if (TextUtils.isEmpty(result)) {
					return TaskResult.FAILED;
				}
				JsonResult jsonResult = JsonHelper.fromJson(result, JsonResult.class);
				if (JsonResult.SUCCESS.equals(jsonResult.getMethodCode())) {
					loginProgress.dismiss();
					String gameUserJson = jsonResult.getMethodMessage();
					GameUser gameUser = JsonHelper.fromJson(gameUserJson, GameUser.class);
					// 保存注册账号和密码
					String account = gameUser.getAccount();
					if (!TextUtils.isEmpty(gameUser.getRelaAccount())) {
						account = gameUser.getRelaAccount();
					}
					ActivityUtils.saveAccount(account, gameUser.getMd5Pwd());
					ActivityUtils.saveAccount(account, gameUser.getMd5Pwd());
					Log.d("saveLoginAccount", "登录界面》注册》账户：" + gameUser.getAccount() + "密码：" + gameUser.getMd5Pwd());
//					Database.GAME_USER_AUTH_KEY = gameUser.getAuthKey();
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
				final NoticesVo notices = (NoticesVo) GameCache.getObj(CacheKey.GAME_NOTICE);
				if (notices == null)
					return TaskResult.FAILED;
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				Date dt = format.parse(notices.getCtime());
				format = new SimpleDateFormat("yyyy-MM-dd");
				final String time1 = format.format(dt);
				final String content1 = notices.getContent();// 获取的公告内容
				sharedPrefrences = getSharedPreferences("account_ban", MODE_WORLD_READABLE);
				final String time2 = sharedPrefrences.getString("time", null);
				// final String content2 = sharedPrefrences.getString("content",
				// null);// 保存的公告内容
				runOnUiThread(new Runnable() {

					public void run() {
						ggdetaiLayout.setOnClickListener(null);
						ggdetaiLayout.setVisibility(View.VISIBLE);
						if (time1.equals(time2)) {
							ggdetaiLayout.setVisibility(View.GONE);
							titleView.setText(notices.getTitle());
							contentView.setText("    " + notices.getContent());
							timeView.setText(time1);
							textName.setText("尊敬的玩家：");
							textTeam.setText("掌中游斗地主运营团队");
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
							if (autoTask != null) {
								autoTask.stop(true);
								autoTask = null;
							}
							/** 画卷展开 */
							autoTask = new AutoTask() {

								public void run() {
									if (i <= 20 && i >= 0) {
										handler.sendEmptyMessage(HANDLER_WHAT_LOGIN_ANNOUNCEMENT_OPEN);
									} else {
										i = 20;
										PXZ = PX_LAST_MST;
										handler.sendEmptyMessage(HANDLER_WHAT_LOGIN_ANNOUNCEMENT_VISIBLE);
										cancel(true);
									}
								}
							};
							ScheduledTask.addRateTask(autoTask, 2000, 30);
						}
						if (!TextUtils.isEmpty(notices.getTitle().trim())) {
							HAS_GONGGAO = true;
						}
					}
				});
			} catch (Exception e) {
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		recyleDrawable();
		if (autoTask != null) {
			autoTask.stop(true);
		}
		if (rjoinTask != null) {
			rjoinTask.cancel(true);
			rjoinTask = null;
		}
	}

	public void recyleDrawable() {
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
		layout.removeAllViews();
	}

	private void mesTip(final String msg, final boolean showCancel, final boolean isFinish) {
		try {
			Database.currentActivity.runOnUiThread(new Runnable() {

				public void run() {
					GameDialog gameDialog = new GameDialog(Database.currentActivity, showCancel) {

						public void okClick() {
							if (isFinish) {
								ActivityUtils.finishAcitivity();
							}
							if (null != mChangeAccountDialog && !mChangeAccountDialog.isShowing()) {
								mChangeAccountDialog = new ChangeAccountDialog(LoginActivity.this, handler);
								mChangeAccountDialog.show();
							}
						}
					};
					gameDialog.show();
					gameDialog.setText(msg);
				}
			});
		} catch (Exception e) {}
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		Log.d("forTag", " onFling : ");
		if (!isShown) {
			if (e1.getX() - e2.getX() > 30) {// 向右滑动
				if (mViewFlipper.getDisplayedChild() == imageId.length) {
					mViewFlipper.setVisibility(View.GONE);
					GameCache.putStr(LOGIN_VIEW_FLIPPER, "1");
//					Editor editor = sharedViewfiper.edit();
//					editor.putBoolean("flipper", true);
//					editor.commit();
				} else {
					mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
					mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
					mViewFlipper.showNext();
					if (mViewFlipper.getDisplayedChild() == imageId.length) {
						new Thread() {

							public void run() {
								try {
									sleep(700);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								LoginActivity.this.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										mViewFlipper.setVisibility(View.GONE);
									}
								});
							};
						}.start();
						GameCache.putStr(LOGIN_VIEW_FLIPPER, "1");
//						Editor editor = sharedViewfiper.edit();
//						editor.putBoolean("flipper", true);
//						editor.commit();
						handler.sendEmptyMessage(HANDLER_WHAT_LOGIN_RESIGSTER_USER);
					}
					return true;
				}
			} else if (e2.getX() - e1.getX() > 30) {// 向左滑动
				if (null != mViewFlipper && View.VISIBLE == mViewFlipper.getVisibility() && mViewFlipper.getDisplayedChild() == 0) {
//				if (mViewFlipper.getDisplayedChild() == 0) {
					DialogUtils.toastTip("亲，已经是第一张了");
				} else {
					mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
					mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
					mViewFlipper.showPrevious();
					return true;
				}
			}
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		return this.mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if (Math.abs(System.currentTimeMillis() - Constant.CLICK_TIME) >= 5000) {
//				isBackState = false;
//				Constant.CLICK_TIME = System.currentTimeMillis();
//				Toast.makeText(LoginActivity.this, "再按一次退出", 1000).show();
//			} else {
//				isBackState = !isBackState;
//				Constant.CLICK_TIME = System.currentTimeMillis();
//				if (isBackState) {
//					return super.onKeyDown(keyCode, event);
//				} else {
//					Toast.makeText(LoginActivity.this, "再按一次退出", 1000).show();
//				}
//			}
			/*String appName = getResources().getString(R.string.app_name);
			AlertDialog.Builder builder = new Builder(LoginActivity.this);
			builder.setMessage("是否退出   <<" + appName + ">> ?");
			builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					ActivityPool.exitApp();
				}
			});
			builder.setNegativeButton("再玩一会", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();*/

				this.runOnUiThread(new Runnable() {		
					@Override
					public void run() {
						CheckTool.exit(LoginActivity.this, new ExitCallBack() {
						@Override
						public void exit() {
							ActivityPool.exitApp();
						}

						@Override
						public void cancel() {

						}
						});
					}
				});
		}
		return false;
	}

	/**
	 * 提示没有网络
	 * @Title: showNetWorkDialog  
	 * @param 
	 * @return void
	 * @throws
	 */
	public void showNetWorkDialog() {
		if (netWorkDialog == null) {
			netWorkDialog = DialogUtils.getNetWorkDialog();
		} else if (!netWorkDialog.isShowing()) {
			netWorkDialog.show();
		}
	}

	/**
	 * 展示登录或账号切换窗口
	 * @Title: showLoginDialog  
	 * @param 
	 * @return void
	 * @throws
	 */
	public void showLoginDialog() {
		if (mChangeAccountDialog == null) {
			mChangeAccountDialog = new ChangeAccountDialog(LoginActivity.this, handler);
		}
		if (!mChangeAccountDialog.isShowing()) {
			mChangeAccountDialog.show();
		}
//		if (null != mChangeAccountDialog && !mChangeAccountDialog.isShowing()) {
//			mChangeAccountDialog = new ChangeAccountDialog(LoginActivity.this, handler);
//			mChangeAccountDialog.show();
//		}
	}
}
