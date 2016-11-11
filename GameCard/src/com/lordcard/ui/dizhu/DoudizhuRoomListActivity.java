package com.lordcard.ui.dizhu;

import com.zzyddz.shui.R;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpException;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lordcard.adapter.FGPlaceListAdapter;
import com.lordcard.adapter.FHGPlaceListAdapter;
import com.lordcard.adapter.RoomlistAdapter;
import com.lordcard.broadcast.PackageReceiver;
import com.lordcard.common.anim.AnimUtils;
import com.lordcard.common.bean.AssistantBean;
import com.lordcard.common.bean.DataCentreBean;
import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.schedule.AutoTask;
import com.lordcard.common.schedule.ScheduledTask;
import com.lordcard.common.task.GenericTask;
import com.lordcard.common.task.base.TaskParams;
import com.lordcard.common.task.base.TaskResult;
import com.lordcard.common.util.ActivityPool;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.DateUtil;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.AssistantBtnContent;
import com.lordcard.entity.ContactPeople;
import com.lordcard.entity.GameAsistantContent;
import com.lordcard.entity.GameHallView;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.MessageCenter;
import com.lordcard.entity.Room;
import com.lordcard.entity.RoomSignup;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.network.cmdmgr.CmdDetail;
import com.lordcard.network.cmdmgr.CmdUtils;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.lordcard.ui.CGChargeActivity;
import com.lordcard.ui.DataCentreActivity;
import com.lordcard.ui.InviteToDowanloadActivity;
import com.lordcard.ui.LoginActivity;
import com.lordcard.ui.base.BaseActivity;
import com.lordcard.ui.view.Assistant;
import com.lordcard.ui.view.MainMenuBar;
import com.lordcard.ui.view.RecordPorkerView;
import com.lordcard.ui.view.dialog.GameIqUpgradeDialog;
import com.lordcard.ui.view.dialog.IqGradeDialog;
import com.lordcard.ui.view.dialog.SignDialog;
import com.sdk.constant.SDKConstant;
import com.sdk.jd.sms.util.JDSMSPayUtil;
import com.sdk.util.PaySite;
import com.sdk.util.PayTipUtils;
import com.sdk.util.RechargeUtils;
import com.sdk.util.SDKFactory;
import com.umeng.analytics.MobclickAgent;

@SuppressLint({ "WorldReadableFiles", "HandlerLeak" })
public class DoudizhuRoomListActivity extends BaseActivity implements OnClickListener {

	/** 创建请求报名状态计时器*/
	public static final int HANDLER_WHAT_ROOM_LIST_SIGN_UP_TIME = 2000;
	/** 请求报名状态*/
	public static final int HANDLER_WHAT_ROOM_LIST_GET_SIGN_UP = 2001;
	/**刷新系统时间*/
	public static final int HANDLER_WHAT_ROOM_LIST_CHANGE_SYSTEM_TIME = 2002;
	/** 更新快速赛场Adapter*/
	public static final int HANDLER_WHAT_ROOM_LIST_CHANGE_FAST = 2003;
	/** 更新复合赛场Adapter*/
	public static final int HANDLER_WHAT_ROOM_LIST_CHANGE_SORT = 2004;
	/** 刷新游戏大厅Adapter*/
	public static final int HANDLER_WHAT_ROOM_LIST_REFRESH_ROOM_LIST = 2005;
	/**弹出等级升级对话框*/
	public static final int HANDLER_WHAT_ROOM_LIST_SHOW_IQ_GRADE_MIN = 2006;
	/**弹出等级地图对话框*/
	public static final int HANDLER_WHAT_ROOM_LIST_SHOW_IQ_GRADE_MAX = 2007;
	/**设置经验进度*/
	public static final int HANDLER_WHAT_ROOM_LIST_SET_IQ_GRADE_PG = 2008;
	/** 房间列表界面：游戏签到*/
	public static final int HANDLER_WHAT_ROOM_LIST_GAME_SIGN = 2009;
	/** 房间列表界面：显示游戏助理*/
	public static final int HANDLER_WHAT_ROOM_LIST_SHOW_ASSISTANT = 2010;
	/** 房间列表界面：设置游戏助理数据*/
	public static final int HANDLER_WHAT_ROOM_LIST_SET_ASSISTANT_DATA = 2011;
	/** 刷新快速赛场Adapter*/
	public static final int HANDLER_WHAT_ROOM_LIST_REFRESH_FAST = 2012;
	/** 刷新复合赛场Adapter*/
	public static final int HANDLER_WHAT_ROOM_LIST_REFRESH_SORT = 2013;
	/** 刷新游戏大厅人数*/
	public static final int HANDLER_WHAT_ROOM_PERSON_COUNT = 2014;
	/**游戏助理Map*/
	public static Map<String, String> assMap;
//	private SharedPreferences sharedPrefrences;
//	private Editor editor;
	private RoomlistAdapter roomListAdapter = null;
	//private GridView roomListGridView, fhgpRoomListGridView;//,fgpRoomListGridView;//普通房间，快速赛场列表,复合赛场列表
	private LinearLayout roomTopll;// 顶部的布局
	private Button commonRoomBtn, gamePlaceBtn;//, fgpBtn;//普通场，快速赛场，复合赛场切换按钮
	private LinearLayout roomVipView;
	private Button vipRoomBtn;
	private EditText joinRoomText;
	private TextView ratioText;
	private Button vipJoinBtn, vipRatioAdd, vipRatioLost, vipRoomCreate;
	private RelativeLayout roomListLayout, roomListGuide;
	private ImageView igetitView;
	private int signCount;
	private boolean signSuccess = false;
	private SharedPreferences shareGuide; // sharedPrefrences
											// prefrences, sharedHall
//	private Editor hallEditor; // editor, editor2,
	public boolean cutGame = false; // 是否切换游戏
	private MainMenuBar mMainMenuBar;// 菜单栏
	private Handler mHandler;
	private FGPlaceListAdapter mFGPlaceListAdapter;//快速赛场
	private FHGPlaceListAdapter mFHGPlaceListAdapter;//复合赛场
	private int page;
	private GenericTask vipJoinTask, vipCreateCheckTask;
	private TextView roomCenterBg;
	private AutoTask refreshTvTask = null;
	private AutoTask loadRoomTask = null;
	private AutoTask loadGamePlaceTask = null;
	private Timer creatAT;//游戏助理timer
	private LinearLayout asslayout, xiaomeiLayout; //游戏助理layout
	private AssistantBtnContent BTN_CONTENT = null;//游戏助理按钮内容
	private List<HashMap<String, Object>> GAME_ASSISTANT = null;//游戏助理内容
	private List<HashMap<String, Object>> GAME_ASSISTANT2 = null;//游戏助理内容
	private ImageView xiaomeiBtn;//游戏助理显示按钮
	private boolean assistantClear = true; // 消息条件是否符合
	private String assjson;
	private long asstime, asstime2;//计时在线时间（游戏助理）
	TimerTask task = null; //游戏助理task
	private ImageView newsIv;// 消息图标
	private ImageView starVi;
	private ProgressBar zhiLiPb;//经验进度
	private TextView zhiliTv;//经验进度值
	private RelativeLayout zhiShangLl;//等级表入口
	private LinearLayout newsRl;//消息容器
	private TextView timeShiTv, zhishangTv;// 系统时间(时、分),等级值
	private MyBroadcastReciver mReciver;//刷新系统时间的广播接收器
	private IqGradeDialog mIqGradeDialog = null;
    private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_list);
		PackageReceiver.registerReceiver(this);
		Bundle bundle = this.getIntent().getExtras();
		context = this;
		initView();
		initHandler();
		loadRoomItem();
		if (bundle != null) {
			page = bundle.getInt("page");
			if (page == 2) {
				showMatch();
			}
		}
		// 房间列表指南，
		roomListGuide = (RelativeLayout) findViewById(R.id.guide_view);
		roomListGuide.setOnClickListener(clickListener);
		shareGuide = getSharedPreferences("room_guide", MODE_WORLD_READABLE);
		int isfirst = shareGuide.getInt("roomguide", 1);
		isfirst = 2;
		if (isfirst == 1) {
			ThreadPool.startWork(new Runnable() {

				public void run() {
					Database.IQ_DATA = HttpRequest.getGameIq();
				}
			});
			roomListGuide.setVisibility(View.VISIBLE);
			igetitView = (ImageView) findViewById(R.id.igetit_view);
			// 我知道了
			igetitView.setOnClickListener(clickListener);
		} else {
			Editor guideEditor = shareGuide.edit();
			guideEditor.putInt("roomguide", isfirst > 2 ? isfirst : (isfirst + 1));
			guideEditor.commit();
			roomListGuide.setVisibility(View.GONE);
		}
		//注册广播，用于接收广播刷新系统时间
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constant.SYSTEM_TIME_CHANGE_ACTION);
		mReciver = new MyBroadcastReciver();
		this.registerReceiver(mReciver, intentFilter);
		ThreadPool.startWork(new Runnable() {

			public void run() {
				getTvMessageDate();
				sign();
				getAssistant();
				getSettingDates();
			}
		});
		mst.adjustView(roomListLayout);
		
		/* james add: force enter vip room Tab */
		{
			Context ctx = CrashApplication.getInstance();
			MobclickAgent.onEvent(ctx, "房间Tabvip房");
			roomTopll.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.room_list_top_ll_bg2, true));
			//roomListGridView.setVisibility(View.GONE);
			roomVipView.setVisibility(View.VISIBLE);
			//fhgpRoomListGridView.setVisibility(View.GONE);
			SDKConstant.PAY_ROOM = 1;
		}
	}

	/**
	 * 初始化handler
	 */
	private void initHandler() {
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				String result = "";
				switch (msg.what) {
					case HANDLER_WHAT_ROOM_LIST_SIGN_UP_TIME:// 报名状态更新获取定时器
						setSignUpTimer();
						break;
					case HANDLER_WHAT_ROOM_LIST_GET_SIGN_UP: // 获取报名状态:
						new Thread() {
							public void run() {
								getSignUp();
							};
						}.start();
						break;
					case HANDLER_WHAT_ROOM_LIST_CHANGE_SYSTEM_TIME:// 刷新列表时间
						timeShiTv.setText("" + ActivityUtils.getTimeShort());
						//roomListAdapter.setRoomPersonCount();
						/*int sec = ActivityUtils.getTime_Min();
						if(sec %30 ==0)//30秒刷新一次
						{
							roomListAdapter.setRoomPersonCount();
						}*/
						break;
					case HANDLER_WHAT_ROOM_LIST_REFRESH_FAST:// 刷新快速赛场
						mFGPlaceListAdapter.notifyDataSetChanged();
						break;
					case HANDLER_WHAT_ROOM_LIST_REFRESH_SORT:// 刷新复合赛场
						mFHGPlaceListAdapter.notifyDataSetChanged();
						break;
					case HANDLER_WHAT_ROOM_LIST_CHANGE_FAST:// 更新快速赛场
						mFGPlaceListAdapter.initData();
						mFGPlaceListAdapter.notifyDataSetChanged();
						break;
					case HANDLER_WHAT_ROOM_LIST_CHANGE_SORT:// 更新复合赛场
						mFHGPlaceListAdapter.initData();
						mFHGPlaceListAdapter.notifyDataSetChanged();
						break;
					case HANDLER_WHAT_ROOM_LIST_REFRESH_ROOM_LIST:// 刷新游戏大厅
						roomListAdapter.setRoomList();
						roomListAdapter.notifyDataSetChanged();
						//roomListAdapter.setRoomPersonCount();
						break;
					case HANDLER_WHAT_ROOM_LIST_SHOW_IQ_GRADE_MIN://升小级
						showIqGradeMin(msg);
						break;
					case HANDLER_WHAT_ROOM_LIST_SHOW_IQ_GRADE_MAX://升大级
						showIqGradeMax();
						break;
					case HANDLER_WHAT_ROOM_LIST_SET_IQ_GRADE_PG://显示经验进度
						try {
							new LoadUserInfoTask().execute();
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case HANDLER_WHAT_ROOM_LIST_GAME_SIGN:
						result = msg.getData().getString("result");
						if (!TextUtils.isEmpty(result) && "0".equals(result)) {
							signSuccess = true;
						} else {
							signCount = signCount - 1;
						}
						SignDialog mSignDialog = new SignDialog(DoudizhuRoomListActivity.this, R.style.dialog, signCount, signSuccess);
						mSignDialog.show();
						break;
					case HANDLER_WHAT_ROOM_LIST_SET_ASSISTANT_DATA:
						setAssistantData();
						break;
					case HANDLER_WHAT_ROOM_LIST_SHOW_ASSISTANT://显示游戏助理
						asslayout.removeAllViews();
						Assistant assistant = new Assistant(Database.currentActivity, mHandler, BTN_CONTENT, GAME_ASSISTANT, xiaomeiLayout, xiaomeiBtn);
						asslayout.addView(assistant);
						//						deleteData();// 消息看过后就从数据库中删除
						break;
					case HANDLER_WHAT_ROOM_PERSON_COUNT:
						//roomListAdapter.setRoomPersonCount();
						break;
				}
			}

			/**
			 * 显示等级升级对话框max
			 */
			private void showIqGradeMax() {
				if (null == mIqGradeDialog || !mIqGradeDialog.isShowing()) {
					//弹出等级等级对话框
					int iq = -1;
					GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
					if (null != cacheUser) {
						iq = cacheUser.getIq();
					}
					mIqGradeDialog = new IqGradeDialog(DoudizhuRoomListActivity.this, iq);
					mIqGradeDialog.setContentView(R.layout.iq_grade_dialog);
					android.view.WindowManager.LayoutParams lay1 = mIqGradeDialog.getWindow().getAttributes();
					setParams(lay1);
					Window window1 = mIqGradeDialog.getWindow();
					window1.setGravity(Gravity.BOTTOM); //此处可以设置dialog显示的位置
					window1.setWindowAnimations(R.style.mystyle); //添加动画
					mIqGradeDialog.show();
				}
			}

			/**
			 * 显示等级升级对话框min
			 * @param msg
			 */
			private void showIqGradeMin(Message msg) {
				String result;
				result = msg.getData().getString("getCelebratedText");
				GameIqUpgradeDialog mGameIqUpgradeDialog = new GameIqUpgradeDialog(DoudizhuRoomListActivity.this, mHandler, result, "", false, null, HANDLER_WHAT_ROOM_LIST_SHOW_IQ_GRADE_MAX);
				android.view.WindowManager.LayoutParams lay = mGameIqUpgradeDialog.getWindow().getAttributes();
				setParams(lay);
				Window window = mGameIqUpgradeDialog.getWindow();
				window.setGravity(Gravity.CENTER); //此处可以设置dialog显示的位置
				window.setWindowAnimations(R.style.mystyle2); //添加动画
				mGameIqUpgradeDialog.show();
			}

			/**
			 * 设置游戏助理信息
			 */
			private void setAssistantData() {
				getMessageStart();//星星
				assMap = new HashMap<String, String>();
				//消息中心插入数据
				if (Database.MESSAGE_CENTER != null && !Database.MESSAGE_CENTER.equals("1")) {
					if (Database.MESSAGE_CENTER.size() > 0) {
						for (int i = 0; i < Database.MESSAGE_CENTER.size(); i++) {
							ContentValues values3 = new ContentValues();
							String id;
							if (Database.MESSAGE_CENTER.get(i).getType() == 0) {
								values3.put(DataCentreBean.DATA_ID, Database.MESSAGE_CENTER.get(i).getId() + "0");
								id = Database.MESSAGE_CENTER.get(i).getId() + "0";
							} else {
								values3.put(DataCentreBean.DATA_ID, Database.MESSAGE_CENTER.get(i).getId() + "1");
								id = Database.MESSAGE_CENTER.get(i).getId() + "1";
							}
							values3.put(DataCentreBean.DATA_CONTENT, Database.MESSAGE_CENTER.get(i).getContent());
							values3.put(DataCentreBean.DATA_RACE, Database.MESSAGE_CENTER.get(i).getType());
							values3.put(DataCentreBean.DATA_TIME, Database.MESSAGE_CENTER.get(i).getCtime());
							values3.put(DataCentreBean.DATA_CLICK, "0");
							values3.put(DataCentreBean.DATA_TITLE, Database.MESSAGE_CENTER.get(i).getTitle());
							String[] ids = { id };
							DataCentreBean.getInstance().save(LoginActivity.dbHelper, values3, ids);
						}
						Database.MESSAGE_CENTER = null;
					}
				}
				//游戏助理插入数据
				if (!TextUtils.isEmpty(assjson) && assjson != "1") {
					try {
						Map<String, String> map = JsonHelper.fromJson(assjson, new TypeToken<Map<String, String>>() {});
						if (map != null && !map.equals("")) {
							if (!map.get("ac").equals("")) {
								List<GameAsistantContent> list = JsonHelper.fromJson(map.get("ac"), new TypeToken<List<GameAsistantContent>>() {});
								ContentValues values = new ContentValues();
								for (int i = 0; i < list.size(); i++) {
									int type = list.get(i).getType();
									values.put(AssistantBean.AS_ID, Integer.parseInt(list.get(i).getId().toString()));
									values.put(AssistantBean.AS_ICON, list.get(i).getAsstIcon());
									values.put(AssistantBean.AS_SMALL_ICON, list.get(i).getSmallIcon());
									values.put(AssistantBean.AS_DISPLAY, list.get(i).getDisplay());
									values.put(AssistantBean.AS_CONTENT, list.get(i).getContent());
									values.put(AssistantBean.AS_BTNAC, list.get(i).getBtnAc());
									values.put(AssistantBean.AS_LEVEL, list.get(i).getLevel());
									values.put(AssistantBean.AS_TIME, list.get(i).getValidTime());
									values.put(AssistantBean.AS_CONDITION, list.get(i).getBind());
									values.put(AssistantBean.AS_TITLE, list.get(i).getTitle());
									values.put(AssistantBean.AS_JOINCODE, list.get(i).getJoinCode());
									values.put(AssistantBean.AS_PUSHTIME, list.get(i).getPushTime());
									values.put(AssistantBean.AS_ORDER, list.get(i).getOrder());
									values.put(AssistantBean.AS_TYPE, type);
									String[] id = { list.get(i).getId().toString() };
//									long size = 
									AssistantBean.getInstance().save(LoginActivity.dbHelper, values, id);
								}
							}
						}
					} catch (Exception e) {}
				}
				if (assMap == null || assMap.size() == 0) {
					List<HashMap<String, Object>> list = AssistantBean.getInstance().findInfo(LoginActivity.dbHelper);
					if (list != null && list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							String key = Integer.valueOf(Double.valueOf(list.get(i).get(AssistantBean.AS_TYPE).toString()).intValue()).toString();
							if (key != null && !key.equals("")) {
								assMap.put(key, "");
							}
						}
					}
				}
				//第一次必须（assMap赋值推送时间+开始时间+id）
				if (assMap != null && assMap.size() != 0) {
					for (String key : assMap.keySet()) {
						Date curDate = new Date(System.currentTimeMillis());//获取当前时间
						asstime = DateUtil.getMillis(curDate);
						List<HashMap<String, Object>> type = AssistantBean.getInstance().findListType(LoginActivity.dbHelper, new String[] { key });
						if (type != null && !type.equals("") && type.size() != 0) {
							assMap.put(key, String.valueOf(type.get(0).get(AssistantBean.AS_PUSHTIME)) + "-" + asstime + "-" + Integer.valueOf(Double.valueOf(type.get(0).get(AssistantBean.AS_ID).toString()).intValue()).toString());
						}
					}
					creatAT = new Timer(true);
					creatAT.schedule(task = new TimerTask() {

						@Override
						public void run() {
							try {
								if (assMap == null || assMap.size() == 0) {
									if (creatAT != null) {
										creatAT.cancel();
										creatAT = null;
										task.cancel();
									}
								}
								Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
								asstime2 = DateUtil.getMillis(curDate);
								List<String> cancelKey = new ArrayList<String>();
								List<String> updateMap = new ArrayList<String>();
								for (String key : assMap.keySet()) {
									if (assMap.get(key) != null && !assMap.get(key).equals("")) {
										String timeAndId[] = assMap.get(key).split("-");
										// pushtime-(现在时间-开始时间)<=0就推送游戏助理
										long time = Long.valueOf(timeAndId[0]) - (asstime2 - Long.valueOf(timeAndId[1]));
										if (time <= 0) {
											// 时间到推送游戏助理
											if (Database.currentActivity.getClass().equals(DoudizhuRoomListActivity.class) && Database.ASSCLOSE) {
												Database.ASSCLOSE = false;
												List<HashMap<String, Object>> type = AssistantBean.getInstance().findListType(LoginActivity.dbHelper, new String[] { key });
												if (type == null || type.size() == 0) {
													cancelKey.add(key);
												} else {
													updateMap.add(key);
													chooseAssistant(timeAndId[2]);
												}
											}
										} else {
											// 更新推送时间
											if (!Database.currentActivity.getClass().equals(DataCentreActivity.class)) {
												AssistantBean.getInstance().update(LoginActivity.dbHelper, new String[] { timeAndId[2] }, new String[] { String.valueOf(time) });
											}
										}
									}
								}
								if (cancelKey.size() != 0) {
									// map遍历完后方可删除
									for (int i = 0; i < cancelKey.size(); i++) {
										assMap.remove(cancelKey.get(i));
									}
								}
								// map遍历完后更新map
								if (updateMap.size() != 0) {
									for (int i = 0; i < updateMap.size(); i++) {
										assMap.put(updateMap.get(i), "");
									}
									getAssMap();
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					}, 5000, 5000);
				}
			}
		};
	}

	/**
	 * 初始化UI
	 */
	private void initView() {
		roomListLayout = (RelativeLayout) findViewById(R.id.room_list_layout);
		startLeftLight();//左边光束
		startrightLight();//右边光束
		startrightLight_left();//右边-左
		startrightLight_right();//右边-右
		startRotateStar();//旋转星星
		startTopLight();
		roomListLayout.setBackgroundResource(R.drawable.join_bj);
		roomListLayout.setOnClickListener(this);
		roomCenterBg = (TextView) findViewById(R.id.room_center_bg);
		roomCenterBg.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.room_center_bg, true));
		Database.ContactPeopleList = new ArrayList<ContactPeople>();
		roomTopll = (LinearLayout) findViewById(R.id.room_list_top_ll);
		roomTopll.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.room_list_top_ll_bg1, true));
		//commonRoomBtn = (Button) findViewById(R.id.common_room_btn);
		//commonRoomBtn.setOnClickListener(clickListener);
		starVi = (ImageView) findViewById(R.id.star_view1);
//		fgpBtn = (Button) findViewById(R.id.fast_game_place_room_btn);
//		fgpBtn.setOnClickListener(clickListener);
		//gamePlaceBtn = (Button) findViewById(R.id.game_place_room_btn);
		//gamePlaceBtn.setOnClickListener(clickListener);
		//roomListGridView = (GridView) findViewById(R.id.room_list_grid_view);

//		fgpRoomListGridView = (GridView) findViewById(R.id.fpg_list_grid_view);
		//fhgpRoomListGridView = (GridView) findViewById(R.id.fhpg_list_grid_view);
		mMainMenuBar = (MainMenuBar) findViewById(R.id.main_page_bottom_rl);
		asslayout = (LinearLayout) findViewById(R.id.assistant_view);
		xiaomeiLayout = (LinearLayout) findViewById(R.id.xiao_LinearLayout);
		xiaomeiBtn = (ImageView) findViewById(R.id.xiaomei);
		xiaomeiBtn.setOnClickListener(clickListener);
		newsRl = (LinearLayout) findViewById(R.id.room_news_rl);
		newsRl.setOnClickListener(clickListener);
		newsIv = (ImageView) findViewById(R.id.room_news_iv);
		timeShiTv = (TextView) findViewById(R.id.time_shi_tv);
		Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/LCD.ttf");
		timeShiTv.setTypeface(typeface);
		timeShiTv.setText("" + ActivityUtils.getTimeShort());
		zhishangTv = (TextView) findViewById(R.id.room_iq_tv);
		zhiShangLl = (RelativeLayout) findViewById(R.id.zhisahngbiao_ll);
		zhiShangLl.setOnClickListener(clickListener);
		zhiLiPb = (ProgressBar) findViewById(R.id.room_iq_pg);
		zhiliTv = (TextView) findViewById(R.id.room_iq_pg_tv);
		//vipRoomBtn = (Button) findViewById(R.id.vip_room_btn);
		//vipRoomBtn.setOnClickListener(clickListener);
		vipJoinBtn = (Button) findViewById(R.id.vip_join_btn);
		vipJoinBtn.setOnClickListener(clickListener);
		roomVipView = (LinearLayout) findViewById(R.id.room_vip_view);
		joinRoomText = (EditText) findViewById(R.id.join_room_text);
		ratioText = (TextView) findViewById(R.id.vip_room_ratio);
		vipRatioAdd = (Button) findViewById(R.id.vip_ratio_add);
		vipRatioAdd.setOnClickListener(clickListener);
		vipRatioLost = (Button) findViewById(R.id.vip_ratio_lost);
		vipRatioLost.setOnClickListener(clickListener);
		vipRoomCreate = (Button) findViewById(R.id.vip_room_create);
		vipRoomCreate.setOnClickListener(clickListener);
		
		
	}

	/**
	 * 获取游戏助理 消息中心
	 */
	private void getAssistant() {
		/* james hard code */
		/*
		try {
			// 第一次取游戏助理根据账号(数据库)来获取数据
			assjson = HttpRequest.getAsstContent();
			getGameMessage();
			if (mHandler == null)
				return;
			Message message = new Message();
			message.what = HANDLER_WHAT_ROOM_LIST_SET_ASSISTANT_DATA;
			mHandler.sendMessage(message);
		} catch (Exception e) {}
		*/
	}

	/**
	 * 获取各界面提示内容信息
	 */
	private void getTvMessageDate() {
		String result = HttpRequest.getTextViewMessageDate();
		if (!TextUtils.isEmpty(result)) {
			try {
				result = new String(result.getBytes("ISO-8859-1"), Constant.CHAR);
				GameCache.putStr(CacheKey.KEY_TEXT_VIEW_MESSAGE_DATA, result);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取各种配置数据
	 */
	@SuppressWarnings("unchecked")
	private void getSettingDates() {
		HashMap<String, String> TaskMenuMap = (HashMap<String, String>) GameCache.getObj(CacheKey.ALL_SETTING_KEY);
		if (null != TaskMenuMap) {
			try {
				/**获取记牌免费使用的次数*/
				if (TaskMenuMap.containsKey("tl_recored_card_num")) {
					Calendar calendar = Calendar.getInstance(Locale.CHINA);
					String timeString = ActivityUtils.getSharedValue(RecordPorkerView.JIPAIQI_USE_TIME);
					if (!TextUtils.isEmpty(timeString)) {
						Date date = new Date(Long.parseLong(timeString));
						if (date.getDate() != calendar.get(Calendar.DATE)) {
							/**记牌器今天未使用过*/
							Database.JI_PAI_QI_FREE_COUNT = Integer.parseInt(TaskMenuMap.get("tl_recored_card_num"));
						} else {
							/**记牌器今天使用过*/
							String jipaiqi_use_count = ActivityUtils.getSharedValue(RecordPorkerView.JIPAIQI_USE_COUNT);
							if (TextUtils.isEmpty(jipaiqi_use_count)) {
								Database.JI_PAI_QI_FREE_COUNT = 1;
								ActivityUtils.addSharedValue(RecordPorkerView.JIPAIQI_USE_COUNT, String.valueOf(Database.JI_PAI_QI_FREE_COUNT));
							} else {
								/**记牌器记牌器的剩余使用次数*/
								Database.JI_PAI_QI_FREE_COUNT = Integer.parseInt(jipaiqi_use_count);
							}
						}
					} else {
						/**记牌器未使用过*/
						Database.JI_PAI_QI_FREE_COUNT = Integer.parseInt(TaskMenuMap.get("tl_recored_card_num"));
					}
				} else {
					Database.JI_PAI_QI_FREE_COUNT = 1;
				}
			} catch (Exception e) {
				Database.JI_PAI_QI_FREE_COUNT = 1;
			}
		}
	}

	/**
	 * 游戏公告推送插入数据库
	 */
	private void getGameMessage() {
		Database.MESSAGE_CENTER = JsonHelper.fromJson(HttpRequest.getGameData(), new TypeToken<List<MessageCenter>>() {});
	}

	/**
	 * 获取喇叭动画波浪
	 */
	public void getMessageStart() {
		AnimUtils.playAnim(newsIv, ImageUtil.getResAnimaSoft("news"), 0);
		/*String[] valuePrivate = { "0" };
		List<HashMap<String, Object>> list = DataCentreBean.getInstance().findListclick(LoginActivity.dbHelper, valuePrivate);
		if (null != list) {
			int size = list.size();
			if (0 < size) {
				AnimUtils.playAnim(newsIv, ImageUtil.getResAnimaSoft("news"), 0);
			} else {
				newsIv.setBackgroundResource(R.drawable.news0);
			}
		} else {
			newsIv.setBackgroundResource(R.drawable.news0);
		}*/
	}
	
	private void startStar()
	{
		AnimUtils.playAnim(starVi, ImageUtil.createStart(), 0);
	}
	ImageView view_TopLight;//右边光束
	private void startTopLight()
	{
		if(view_TopLight == null)
		{
			view_TopLight = ((ImageView)findViewById(R.id.imageView_toplight));
		}
		Animation animationjg = AnimationUtils.loadAnimation(this, R.anim.alpha_main_toplight);
		view_TopLight.startAnimation(animationjg);
		animationjg.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				startTopLight2();
				mHandler.sendEmptyMessage(HANDLER_WHAT_ROOM_LIST_CHANGE_SYSTEM_TIME);
				
			}
		});
	}
	ImageView view_TopLight2;//
	private void startTopLight2()
	{
		if(view_TopLight2 == null)
		{
			view_TopLight2 = ((ImageView)findViewById(R.id.imageView_toplight));
		}
		Animation animationjg = AnimationUtils.loadAnimation(this, R.anim.alpha_main_toplight_out);
		view_TopLight2.startAnimation(animationjg);
		animationjg.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				startTopLight();
			}
		});
	}
	/**
	 * 播放旋转动画
	 */
	ImageView view_leftLight;//左边光束
	ImageView view_rightLight;//右边光束
	private byte runCount = 0;
	private void startLeftLight()
	{
		if(view_leftLight == null)
		{
			view_leftLight = ((ImageView)findViewById(R.id.light_view_left));
		}
		Animation animationjg = AnimationUtils.loadAnimation(this, R.anim.rotate_main_leftlight_in);
		view_leftLight.startAnimation(animationjg);
		animationjg.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				startLeftLigh2();
				runCount++;
				if(runCount>=2)
				{
					//roomListAdapter.setRoomPersonCount();
					runCount = 0;
					mHandler.sendEmptyMessage(HANDLER_WHAT_ROOM_PERSON_COUNT);
					/*Random r = new Random();
					if(r.nextInt(100)<40)
					{
						roomListAdapter.setRoomPersonCount();
					}*/
				}
			}
		});
	}
	private void startLeftLigh2()
	{
		if(view_leftLight == null)
		{
			view_leftLight = ((ImageView)findViewById(R.id.light_view_left));
		}
		Animation animationjg = AnimationUtils.loadAnimation(this, R.anim.rotate_main_leftlight_out);
		view_leftLight.startAnimation(animationjg);
		animationjg.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				startLeftLight();
			}
		});
	}
	
	private void startrightLight()
	{
		if(view_rightLight == null)
		{
			view_rightLight = ((ImageView)findViewById(R.id.light_view_right));
		}
		Animation animationjg = AnimationUtils.loadAnimation(this, R.anim.rotate_main_rightlight_in);
		view_rightLight.startAnimation(animationjg);
		animationjg.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				startrightLigh2();
			}
		});
	}
	private void startrightLigh2()
	{
		if(view_rightLight == null)
		{
			view_rightLight = ((ImageView)findViewById(R.id.light_view_right));
		}
		Animation animationjg = AnimationUtils.loadAnimation(this, R.anim.rotate_main_rightlight_out);
		view_rightLight.startAnimation(animationjg);
		animationjg.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				startrightLight();
			}
		});
	}
	/**
	 * 右边光束-左
	 */
	ImageView view_rightLight_left;
	private void startrightLight_left()
	{
		if(view_rightLight_left == null)
		{
			view_rightLight_left = ((ImageView)findViewById(R.id.light_right_view_left));
		}
		Animation animationjg = AnimationUtils.loadAnimation(this, R.anim.rotate_main_rightlight_out);
		view_rightLight_left.startAnimation(animationjg);
		animationjg.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				startrightLight_left2();
			}
		});
	}
	private void startrightLight_left2()
	{
		if(view_rightLight_left == null)
		{
			view_rightLight_left = ((ImageView)findViewById(R.id.light_right_view_left));
		}
		Animation animationjg = AnimationUtils.loadAnimation(this, R.anim.rotate_main_rightlight_in);
		view_rightLight_left.startAnimation(animationjg);
		animationjg.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				startrightLight_left();
			}
		});
	}
	/**
	 * 右边光束-右
	 */
	ImageView view_rightLight_right;
	private void startrightLight_right()
	{
		if(view_rightLight_right == null)
		{
			view_rightLight_right = ((ImageView)findViewById(R.id.light_right_view_right));
		}
		Animation animationjg = AnimationUtils.loadAnimation(this, R.anim.rotate_main_rightlight_in);
		view_rightLight_right.startAnimation(animationjg);
		animationjg.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				startrightLight_right2();
			}
		});
	}
	private void startrightLight_right2()
	{
		if(view_rightLight_right == null)
		{
			view_rightLight_right = ((ImageView)findViewById(R.id.light_right_view_right));
		}
		Animation animationjg = AnimationUtils.loadAnimation(this, R.anim.rotate_main_rightlight_out);
		view_rightLight_right.startAnimation(animationjg);
		animationjg.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				startrightLight_right();
			}
		});
	}
	/**
	 * 星光
	 */
	ImageView view_star;
	ImageView view_star2;
	private void startRotateStar()
	{
		if (mMainMenuBar != null) {
			mMainMenuBar.changeLingZhiDouBG();
		}
		if(view_star == null)
		{
			view_star = ((ImageView)findViewById(R.id.star_view2));
		}
		if(view_star2 == null)
		{
			view_star2 = ((ImageView)findViewById(R.id.star_view3));
		}
		Animation animationjg = AnimationUtils.loadAnimation(this, R.anim.rotate_main_star_in);
		view_star.startAnimation(animationjg);
		animationjg.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				startRotateStar2();
			}
		});
		view_star2.startAnimation(animationjg);
		animationjg.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				startRotateStar2();
			}
		});
	}
	private void startRotateStar2()
	{
		if (mMainMenuBar != null) {
			mMainMenuBar.changeLingZhiDouBG();
		}
		if(view_star == null)
		{
			view_star = ((ImageView)findViewById(R.id.star_view2));
		}
		if(view_star2 == null)
		{
			view_star2 = ((ImageView)findViewById(R.id.star_view3));
		}
		Animation animationjg = AnimationUtils.loadAnimation(this, R.anim.rotate_main_star_out);
		view_star.startAnimation(animationjg);
		animationjg.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				startRotateStar();
			}
		});
		view_star2.startAnimation(animationjg);
		animationjg.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				startRotateStar();
			}
		});
	}
	/**
	 * assMap赋值推送时间 开始时间  id
	 * 
	 */
	public void getAssMap() {
		if (assMap != null && assMap.size() != 0) {
			for (String key : assMap.keySet()) {
				if (assMap.get(key).equals("")) {
					Date curDate = new Date(System.currentTimeMillis());//获取当前时间
					asstime = DateUtil.getMillis(curDate);
					List<HashMap<String, Object>> type = AssistantBean.getInstance().findListType(LoginActivity.dbHelper, new String[] { key });
					if (type != null && !type.equals("") && type.size() != 0) {
						assMap.put(key, String.valueOf(type.get(0).get(AssistantBean.AS_PUSHTIME)) + "-" + asstime + "-" + Integer.valueOf(Double.valueOf(type.get(0).get(AssistantBean.AS_ID).toString()).intValue()).toString());
					}
				}
			}
		}
	}

	/**
	 * 更新数据库存储assMap
	 * 
	 */
	public void UpData() {
		if (assMap != null) {
			String json = JsonHelper.toJson(assMap);
			AssistantBean.getInstance().updateJson(LoginActivity.dbHelper, new String[] { "0" }, new String[] { json });
		}
	}

	/**
	 * 数据库数据删除
	 * 
	 */
	public void deleteData() {
		String[] values = { GAME_ASSISTANT2.get(0).get(AssistantBean.AS_ID).toString() };
		AssistantBean.getInstance().delete(LoginActivity.dbHelper, "id=?", values);
	}

	/**
	 * 游戏助理赋值方法
	 * 
	 */
	public void assistantData(final List<HashMap<String, Object>> list) {
		AssistantBtnContent content = JsonHelper.fromJson(String.valueOf(list.get(0).get(AssistantBean.AS_BTNAC)), AssistantBtnContent.class);
		BTN_CONTENT = content;
		GAME_ASSISTANT = list;
		deleteData();// 消息看过后就从数据库中删除
		Message message = new Message();
		message.what = HANDLER_WHAT_ROOM_LIST_SHOW_ASSISTANT;
		mHandler.sendMessage(message);
	}

	private void chooseAssistant(String id) {
		String wifi = null;
		long memory = ActivityUtils.getRomMemroy();
		assistantClear = true;
		if (ActivityUtils.isOpenWifi()) {
			wifi = "1";
		} else {
			wifi = "0";
		}
		while (assistantClear) {
			List<HashMap<String, Object>> list2 = AssistantBean.getInstance().findList(LoginActivity.dbHelper, new String[] { id });
			if (list2 == null || list2.size() == 0) {
				Database.ASSCLOSE = true;// 如果没有数据默认当做已关闭
				assistantClear = false;
				return;
			}
			if (list2 != null && list2.size() != 0) {
				GAME_ASSISTANT2 = list2;// 用于删除数据库同步
			}
			// 如果条件不为空
			if (list2.size() != 0 && !list2.get(0).get(AssistantBean.AS_CONDITION).equals("")) {
				Map<String, String> map2 = JsonHelper.fromJson(String.valueOf(list2.get(0).get(AssistantBean.AS_CONDITION)), new TypeToken<Map<String, String>>() {});
				// 不需要wifi
				if (map2.get("wifi").equals("0")) {
					// 如果有sd卡正常运行，跳出循环
					if (ActivityUtils.isSDCardEabled()) {
						assistantData(list2);
						assistantClear = false;
					} else {
						// 没卡且内存小于规定的值删除数据
						if (Integer.valueOf(map2.get("memory")) > memory) {
							deleteData();
						}
						// 内存足够则正常运行，跳出循环
						else {
							assistantData(list2);
							assistantClear = false;
						}
					}
				}
				// 需要wifi
				else {
					// 如果当前网络为wifi
					if (map2.get("wifi").equals(wifi)) {
						// 如果有sd卡正常运行，跳出循环
						if (ActivityUtils.isSDCardEabled()) {
							assistantData(list2);
							assistantClear = false;
						} else {
							// 没卡且内存小于规定的值删除数据
							if (Integer.valueOf(map2.get("memory")) > memory) {
								deleteData();
							}
							// 内存足够则正常运行，跳出循环
							else {
								assistantData(list2);
								assistantClear = false;
							}
						}
					} else {
						// 如果当前网络不为wifi删除数据，继续循环
						deleteData();
					}
				}
				// 如果条件为空照常运行，跳出循环
			} else if (list2.size() != 0 && list2.get(0).get(AssistantBean.AS_CONDITION).equals("")) {
				assistantData(list2);
				assistantClear = false;
			}
			// 如果没数据跳出循环且结束计时器
			else if (list2.size() == 0) {
				assistantClear = false;
			}
		}
	}

	/**
	 * 加载房间资源
	 */
	private void loadRoomItem() {
		try {
			//使用默认房间资源，前确认缓存数据结构是否正确
			String hallResult = GameCache.getStr(CacheKey.ROOM_HALL);
			Database.HALL_CACHE = JsonHelper.fromJson(hallResult, GameHallView.class);
			if (Database.HALL_CACHE == null) {
				ThreadPool.startWork(new Runnable() {

					public void run() {
						try {
							// 获取缓存的房间资源
							boolean hasUpdate = true;
							String updateTime = GameCache.getStr(Constant.UPDATETIME);
							//若最后的房间信息更新时间和服务器是否一致，若一致说明无已经是最新
							if (Database.ROOM_UPDATE != null && Database.ROOM_UPDATE.equals(updateTime)) {
								hasUpdate = false;
							} else {
								GameCache.putStr(Constant.UPDATETIME, Database.ROOM_UPDATE);
							}
							if (!hasUpdate) { //有缓存，且后设置不需要更新房间数据
								return;
							}
							if (Database.HALL_CACHE != null && !TextUtils.isEmpty(Database.HALL_CACHE.getLoginToken())) {
								return;
							}
							String hallResult = HttpRequest.loginGame(true); // 登录获取数据
							GameHallView tempHall = JsonHelper.fromJson(hallResult, GameHallView.class);
							if (tempHall != null) {
								Database.HALL_CACHE = tempHall;
								GameCache.putStr(CacheKey.ROOM_HALL, hallResult); //存储数据
							} else {
//								DialogUtils.loginFail(); // 失败
								return;
							}
							runOnUiThread(new Runnable() {

								public void run() {
									setListViewData();
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			if (Database.HALL_CACHE == null) {
				GameHallView hall = new GameHallView();
				hall.setLoginToken("");
				hall.setFastRoomList(new ArrayList<Room>());
				hall.setSortRoomList(new ArrayList<Room>());
				List<Room> roomList = new ArrayList<Room>();
				for (int i = 1; i <= 6; i++) {
					Room rm = new Room();
					rm.setRatio(1);
					rm.setBasePoint(200);
					rm.setUnitType(1);
					rm.setLimit(500);
					rm.setLimitGroupNum(3);
					rm.setHomeType(0);
					rm.setCommissionNum(150);
					rm.setHallCode("1");
					rm.setRoomType(0);
					String name = null;
					switch (i) {
						case 1:
							name = "新手场";
							break;
						case 2:
							name = "初级场";
							break;
						case 3:
							name = "中级场";
							break;
						case 4:
							name = "高级场";
							break;
						case 5:
							name = "钻石挖矿场";
							break;
						case 6:
							name = "豪门争霸赛";
							break;
						default:
							break;
					}
					rm.setCode("109");
					rm.setName(name);
					rm.setSort(i);
					roomList.add(rm);
				}
				hall.setGameRoomList(roomList);
				Database.HALL_CACHE = hall;
			}
			setListViewData();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 设置ListView数据
	 */
	private void setListViewData() {
		// 游戏大厅
		roomListAdapter = new RoomlistAdapter(DoudizhuRoomListActivity.this, taskManager);
		//roomListGridView.setAdapter(roomListAdapter);
		//先展示房间列表
		mFGPlaceListAdapter = new FGPlaceListAdapter(DoudizhuRoomListActivity.this, taskManager, mHandler);
		mFHGPlaceListAdapter = new FHGPlaceListAdapter(DoudizhuRoomListActivity.this, taskManager, mHandler);
		//fhgpRoomListGridView.setAdapter(mFHGPlaceListAdapter);
		ThreadPool.startWork(new Runnable() {

			public void run() {
				getSignUp();
				runOnUiThread(new Runnable() {

					public void run() {
						mFGPlaceListAdapter = new FGPlaceListAdapter(DoudizhuRoomListActivity.this, taskManager, mHandler);
						mFHGPlaceListAdapter = new FHGPlaceListAdapter(DoudizhuRoomListActivity.this, taskManager, mHandler);
						//fhgpRoomListGridView.setAdapter(mFHGPlaceListAdapter);
					}
				});
			}
		});
	}

	/**
	 * 获取报名状态(比赛场)
	 */
	private void getSignUp() {
		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			paramMap.put("account", cacheUser.getAccount());
			String result = HttpUtils.post(HttpURL.FH_ROOM_SIGNUP, paramMap);
			Log.d("refreshTvList", "获取报名状态==result:" + result);
			List<RoomSignup> rg = null;
			if (null != result && !"1".equals(result)) {
				rg = JsonHelper.fromJson(result, new TypeToken<List<RoomSignup>>() {});
				Database.ROOM_SIGN_UP.clear();
				Database.ROOM_SIGN_UP.addAll(rg);
			}
			mHandler.sendEmptyMessage(HANDLER_WHAT_ROOM_LIST_SIGN_UP_TIME);
			Log.d("refreshTvList", "获取报名状态");
		} catch (Exception e) {}
	}

	/**
	 * 设置报名状态计时器(比赛场)
	 */
	private void setSignUpTimer() {
		Log.d("refreshTvList", "设置报名状态计时器");
		if (null != Database.ROOM_SIGN_UP) {
			long times = 0;
			for (int i = 0; i < Database.ROOM_SIGN_UP.size(); i++) {
				if ("1".equals(Database.ROOM_SIGN_UP.get(i).getSignUp())) {
					if (0 == times) {
						times = Database.ROOM_SIGN_UP.get(i).getStopTime();
					} else if (times > Database.ROOM_SIGN_UP.get(i).getStopTime()) {
						times = Database.ROOM_SIGN_UP.get(i).getStopTime();
					}
				}
			}
			if (0 != times) {
				if (refreshTvTask != null) {
					refreshTvTask.stop(true);
					refreshTvTask = null;
				}
				refreshTvTask = new AutoTask() {

					public void run() {
						try {
							GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
							Map<String, String> paramMap = new HashMap<String, String>();
							paramMap.put("account", cacheUser.getAccount());
							String result = HttpUtils.post(HttpURL.FH_ROOM_SIGNUP, paramMap);
							Log.d("refreshTvList", "定时请求result:" + result);
							List<RoomSignup> rg = null;
							if (null != result && !"1".equals(result)) {
								rg = JsonHelper.fromJson(result, new TypeToken<List<RoomSignup>>() {});
								Database.ROOM_SIGN_UP.clear();
								Database.ROOM_SIGN_UP.addAll(rg);
								Database.currentActivity.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										mFGPlaceListAdapter.ChangeSignUp();
										mFHGPlaceListAdapter.ChangeSignUp();
										mHandler.sendEmptyMessage(HANDLER_WHAT_ROOM_LIST_SIGN_UP_TIME);
									}
								});
							}
						} catch (Exception e) {}
					}
				};
				ScheduledTask.addDelayTask(refreshTvTask, times);
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		getMessageStart();
		startStar();
		ImageUtil.clearHeadMapCache();
		Database.ASSCLOSE = true;
		Constant.startCount += 1;
		if (Database.GAME_TYPE == 0) {
			Database.GAME_TYPE = Constant.GAME_TYPE_DIZHU; // 默认斗地主
		}
		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		if (cacheUser == null) {
			Intent intent = new Intent();
			intent.setClass(this, SDKFactory.getLoginView());
			startActivity(intent);
			finish();
		} else {
			/**弹出绑定对话框条件
			 * 1.金豆大于20万
			 * 2.第一次进入房间列表(每次进入应用只弹出一次)
			 * 3.未绑定过账号*/
			if ((cacheUser.getBean() > 200000) && 1 == Constant.startCount && !ActivityUtils.isBindAccount()/* && !CGChargeActivity.isYd(this)*/) {
				HashMap<String, String> TaskMenuMap = (HashMap<String, String>) GameCache.getObj(CacheKey.ALL_SETTING_KEY);
				if (null == TaskMenuMap || !TaskMenuMap.containsKey(Constant.KEY_ACCOUNT_BIND_DIALOG_SHOW_COUNT) || TextUtils.isEmpty(TaskMenuMap.get(Constant.KEY_ACCOUNT_BIND_DIALOG_SHOW_COUNT))) {
					ActivityUtils.showAccountBindDialog();
				}
			}
			cacheUser.setRound(0);// 还原比赛轮数为0
			GameCache.putObj(CacheKey.GAME_USER, cacheUser);
		}
		if (mMainMenuBar != null) {
			mMainMenuBar.changeLingZhiDouBG();
		}
		//		Database.VIP_GAME_TYPE = DoudizhuMainGameActivity.class;
		if (null != roomListAdapter && null != mFGPlaceListAdapter && null != mFHGPlaceListAdapter) {
			new Thread() {

				public void run() {
					try {
						Map<String, String> map = HttpRequest.roomHasChanged();
						if (null != map) {
							String time = null; //更新时间
							String[] roomType = null; //更新房间类型
							if (map.containsKey("at")) {
								String atUp = map.get("at");
								SharedPreferences sharedData = Database.currentActivity.getApplication().getSharedPreferences("assistant", Context.MODE_PRIVATE);
								String up = sharedData.getString("assistantUp", "");
								if (up.equals("") || !up.equals(atUp)) {
									Editor editor = sharedData.edit();
									editor.putString("assistantUp", atUp);
									editor.commit();
									if (creatAT != null) {
										creatAT.cancel();
										creatAT = null;
										task.cancel();
										getAssistant();
									}
								}
							}
							if (map.containsKey("ut") && map.containsKey("ty")) {
								time = map.get("ut");
								GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
								String time2 = gu.getRoomTime();
								roomType = map.get("ty").split(",");
								Log.d("hallResult", "UpdateDate: " + time2 + "---------time:" + time);
								if (!time2.equals(time)) {
									for (int i = 0; i < roomType.length; i++) {
										//all:更新所有房间,gen:普通房间,rk:复合赛房间,fast:快速赛房间
										if ("all".equals(roomType[i])) {//all:更新所有房间
											try {
												String hallResult = HttpRequest.loginGame(true); // 登录
												//正确认的数据缓存
												Database.HALL_CACHE = JsonHelper.fromJson(hallResult, GameHallView.class);
												GameCache.putStr(CacheKey.ROOM_HALL, hallResult);
											} catch (Exception e) {}
											mHandler.sendEmptyMessage(HANDLER_WHAT_ROOM_LIST_CHANGE_SORT);
											mHandler.sendEmptyMessage(HANDLER_WHAT_ROOM_LIST_CHANGE_FAST);
											mHandler.sendEmptyMessage(HANDLER_WHAT_ROOM_LIST_REFRESH_ROOM_LIST);
											//所有房间
										} else if ("rk".equals(roomType[i])) {//rk:复合赛房间
											List<Room> room = HttpRequest.getRoomInfo(roomType[i]);
											if (null != room) {
												Database.HALL_CACHE.getSortRoomList().clear();
												Database.HALL_CACHE.setSortRoomList(room);
												mHandler.sendEmptyMessage(HANDLER_WHAT_ROOM_LIST_CHANGE_SORT);
												Log.d("hallResult", "REFRESH_SORT     " + room);
											}
										} else if ("fast".equals(roomType[i])) {//fast:快速赛房间
											List<Room> room = HttpRequest.getRoomInfo(roomType[i]);
											if (null != room) {
												Database.HALL_CACHE.getFastRoomList().clear();
												Database.HALL_CACHE.setFastRoomList(room);
												mHandler.sendEmptyMessage(HANDLER_WHAT_ROOM_LIST_CHANGE_FAST);
												Log.d("hallResult", "REFRESH_FAST     " + room);
											}
										} else if ("gen".equals(roomType[i])) {//gen:普通房间
											List<Room> room = HttpRequest.getRoomInfo(roomType[i]);
											if (null != room) {
												Database.HALL_CACHE.getGameRoomList().clear();
												Database.HALL_CACHE.setGameRoomList(room);
												mHandler.sendEmptyMessage(HANDLER_WHAT_ROOM_LIST_REFRESH_ROOM_LIST);
												Log.d("hallResult", "REFRESH_ROOM_LIST     " + room);
											}
										}
									}
									gu.setRoomTime(time);
									gu.getRoomTime();
									GameCache.putObj(CacheKey.GAME_USER, gu);
								}
							}
						}
					} catch (Exception e) {}
				};
			}.start();
		}
		try {
			new LoadUserInfoTask().execute();
//			setUserInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *刷新用户信息
	 */
	private void setUserInfo() {
		if (mMainMenuBar != null) {
			mMainMenuBar.setUserInfo();
		}
		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		if (cacheUser != null && null != cacheUser.getIq()) {
			zhishangTv.setText("" + cacheUser.getIq());
			zhiLiPb.setMax(100);
			float step = cacheUser.getNextIntellect() / 100f;
			zhiLiPb.setProgress(Math.round(cacheUser.getIntellect() / step));
			zhiliTv.setText("" + cacheUser.getIntellect() + "/" + cacheUser.getNextIntellect());
		}
	}

	/**
	 * 获取用户信息异步线程
	 * @author Administrator
	 */
	private class LoadUserInfoTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			if (cacheUser != null) {
				cacheUser = HttpRequest.getGameUserDetail(cacheUser.getLoginToken());
				cacheUser.setRound(0);
				GameCache.putObj(CacheKey.GAME_USER, cacheUser);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			setUserInfo(); // 设置内容
		}
	}

	OnClickListener clickListener = new OnClickListener() {

		public void onClick(View v) {
			Context ctx = CrashApplication.getInstance();
			switch (v.getId()) {
				/*case R.id.common_room_btn: // 普通房
					MobclickAgent.onEvent(ctx, "房间Tab普通房");
					roomTopll.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.room_list_top_ll_bg1, true));
					roomListGridView.setVisibility(View.VISIBLE);
//					fgpRoomListGridView.setVisibility(View.GONE);
					roomVipView.setVisibility(View.GONE);
					fhgpRoomListGridView.setVisibility(View.GONE);
					SDKConstant.PAY_ROOM = 0;
					break;*/
				//		case R.id.fast_game_place_room_btn: // 快速赛场
				/*
				case R.id.vip_room_btn: // vip房
					MobclickAgent.onEvent(ctx, "房间Tabvip房");
					roomTopll.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.room_list_top_ll_bg2, true));
					//roomListGridView.setVisibility(View.GONE);
					roomVipView.setVisibility(View.VISIBLE);
					//fhgpRoomListGridView.setVisibility(View.GONE);
					SDKConstant.PAY_ROOM = 1;
					break;*/
				/*case R.id.game_place_room_btn: // 比赛场
					MobclickAgent.onEvent(ctx, "房间Tab比赛场");
					showMatch();
					break;*/
				case R.id.vip_join_btn: // 加入vip包房
					MobclickAgent.onEvent(ctx, "加入vip包房");
					vipJoin();
					break;
				case R.id.vip_ratio_add: // 创建vip增加倍数
					addRatio();
					break;
				case R.id.vip_ratio_lost: // 创建vip减少加倍数
					lostRatio();
					break;
				case R.id.vip_room_create: // 创建vip房
					vipRoomCreate();
					break;
				case R.id.xiaomei:
					break;
				case R.id.room_news_rl://消息中心
					Intent intent = new Intent();
					intent.setClass(DoudizhuRoomListActivity.this, DataCentreActivity.class);
					startActivity(intent);
					break;
				case R.id.zhisahngbiao_ll://等级表入口
					goinZhiShangBiao();
					break;
				case R.id.igetit_view://引导（我知道了）
					final int isfirst = shareGuide.getInt("roomguide", 1);
					Editor guideEditor = shareGuide.edit();
					guideEditor.putInt("roomguide", isfirst + 1);
					guideEditor.commit();
					roomListGuide.setVisibility(View.GONE);
					goinZhiShangBiao();
					break;
				case R.id.guide_view:
					final int isfirst1 = shareGuide.getInt("roomguide", 1);
					Editor guideEditor1 = shareGuide.edit();
					guideEditor1.putInt("roomguide", isfirst1 + 1);
					guideEditor1.commit();
					roomListGuide.setVisibility(View.GONE);
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 进入等级地图表
	 */
	private void goinZhiShangBiao() {
		if (null == mIqGradeDialog || !mIqGradeDialog.isShowing()) {
			int iq = -1;
			GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			if (null != cacheUser) {
				iq = cacheUser.getIq();
			}
			mIqGradeDialog = new IqGradeDialog(DoudizhuRoomListActivity.this, iq);
			mIqGradeDialog.setContentView(R.layout.iq_grade_dialog);
			android.view.WindowManager.LayoutParams lay = mIqGradeDialog.getWindow().getAttributes();
			setParams(lay);
			Window window = mIqGradeDialog.getWindow();
			window.setGravity(Gravity.BOTTOM); //此处可以设置dialog显示的位置
			window.setWindowAnimations(R.style.mystyle); //添加动画
			mIqGradeDialog.show();
		}
	}

	private void setParams(android.view.WindowManager.LayoutParams lay) {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Rect rect = new Rect();
		View view = getWindow().getDecorView();
		view.getWindowVisibleDisplayFrame(rect);
		lay.height = dm.heightPixels - rect.top;
		lay.width = dm.widthPixels;
	}

	private void showMatch() {
		roomTopll.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.room_list_top_ll_bg3, true));
		//roomListGridView.setVisibility(View.GONE);
		roomVipView.setVisibility(View.GONE);
		//fhgpRoomListGridView.setVisibility(View.VISIBLE);
	}

	/**
	* 创建vip增加倍数
	*/
	public void addRatio() {
		if (ratioText.getText().toString().equals("2")) {
			ratioText.setText(String.valueOf(5));
			return;
		}
		if (ratioText.getText().toString().equals("5")) {
			ratioText.setText(String.valueOf(10));
			return;
		}
		if (!ratioText.getText().toString().equals("100")) {
			ratioText.setText(String.valueOf(Integer.parseInt(ratioText.getText().toString()) + 10));
		}
	}

	/**
	 * 创建vip减少加倍数
	 */
	public void lostRatio() {
		if (ratioText.getText().toString().equals("2")) {
			ratioText.setText(String.valueOf(2));
			return;
		}
		if (ratioText.getText().toString().equals("5")) {
			ratioText.setText(String.valueOf(2));
			return;
		}
		if (ratioText.getText().toString().equals("10")) {
			ratioText.setText(String.valueOf(5));
			return;
		}
		if (!ratioText.getText().toString().equals("10")) {
			ratioText.setText(String.valueOf(Integer.parseInt(ratioText.getText().toString()) - 10));
		}
	}

	/**
	 * 创建房间
	 */
	public synchronized void vipRoomCreate() {
		Context ctx = CrashApplication.getInstance();
		int ratio = Integer.parseInt(ratioText.getText().toString()); // Hip包房倍数
		MobclickAgent.onEvent(ctx, "创建房间" + ratio);
		// 加入游戏
		vipCreateCheckTask = new VipRoomCreateCheckTask();
		vipCreateCheckTask.setFeedback(feedback);
		TaskParams params = new TaskParams();
		params.put("ratio", ratio);
		vipCreateCheckTask.execute(params);
		taskManager.addTask(vipCreateCheckTask);
	}

	/**
	 * 加入vip包房
	 */
	public synchronized void vipJoin() {
		String joinHomeId = joinRoomText.getText().toString();
		int homeId = 0;
		try {
			homeId = Integer.parseInt(joinHomeId);
			if (homeId < 100) {
				throw new Exception("");
			}
		} catch (Exception e) {
			Toast toask = Toast.makeText(DoudizhuRoomListActivity.this, "房间号必须是大于100的数字", Toast.LENGTH_LONG);
			toask.setGravity(Gravity.CENTER, 0, 0);
			toask.show();
			return;
		}
		// 加入游戏
		if (vipJoinTask != null) {
			vipJoinTask.cancel(true);
			vipJoinTask = null;
		}
		vipJoinTask = new VipReadyJoinTask();
		vipJoinTask.setFeedback(feedback);
		TaskParams params = new TaskParams();
		params.put("homeCode", joinHomeId);
		params.put("passwd", "");
		vipJoinTask.execute(params);
		taskManager.addTask(vipJoinTask);
	}

	/**
	 * 加入VIP包房线程任务
	 */
	private class VipReadyJoinTask extends GenericTask {

		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				TaskParams param = null;
				if (params != null && params.length <= 0) {
					return TaskResult.FAILED;
				}
				param = params[0];
				String homeCode = param.getString("homeCode");
				String pwd = param.getString("passwd");
				String result = HttpRequest.rjoin(homeCode, pwd);
				CmdDetail detail = JsonHelper.fromJson(result, CmdDetail.class);
				if (CmdUtils.CMD_ERR_RJOIN.equals(detail.getCmd())) { // 加入失败
					String v = detail.getDetail();
					if (HttpRequest.NO_LOGIN.equals(v) || HttpRequest.TOKEN_ILLEGAL.equals(v)) { // 未登录,用户登录token非法
						DialogUtils.reLogin(Database.currentActivity);
					} else if (HttpRequest.NO_HOME.equals(v)) { // 加入的房间不存在
						DialogUtils.mesTip(getString(R.string.no_join_home), false);
					} else if (HttpRequest.NO_SERVER.equals(v)) { // 游戏服务器不存在
						DialogUtils.mesTip(getString(R.string.no_game_server), false);
					}
				} else if (CmdUtils.CMD_HDETAIL.equals(detail.getCmd())) { //金豆不足
					Room room = JsonHelper.fromJson(detail.getDetail(), Room.class);
//					DialogUtils.rechargeTip(room, false, null);
					JDSMSPayUtil.setContext(context);
					double b = RechargeUtils.calRoomJoinMoney(room);
					PayTipUtils.showTip(b, PaySite.VIP_CREATE); //配置的提示方式
				} else if (CmdUtils.CMD_RJOIN.equals(detail.getCmd())) { // 成功
																			// 返回游戏服务器IP地址
					Room room = JsonHelper.fromJson(detail.getDetail(), Room.class);
					Database.JOIN_ROOM = room; // 加入的房间
					Database.GAME_SERVER = room.getGameServer(); // 游戏服务器
					Database.GAME_BG_DRAWABLEID = R.drawable.background_3;
					Database.JOIN_ROOM_CODE = room.getCode();
					Database.JOIN_ROOM_RATIO = room.getRatio();
					Database.JOIN_ROOM_BASEPOINT = room.getBasePoint();
					Intent in = new Intent();
					//					in.setClass(DoudizhuRoomListActivity.this, Database.VIP_GAME_TYPE);
					in.setClass(DoudizhuRoomListActivity.this, DoudizhuMainGameActivity.class);
					in.putExtra("isviproom", true); // 标识是vip包房
					startActivity(in);
					// finish();
				}
			} catch (HttpException e) {
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}
	}

	/**
	 * 创建VIP包房线程任务
	 */
	private class VipRoomCreateCheckTask extends GenericTask {

		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				TaskParams param = null;
				if (params.length <= 0) {
					return TaskResult.FAILED;
				}
				param = params[0];
				Database.GAME_GROUP_NUM = 3;
				int ratio = param.getInt("ratio");
				String result = HttpRequest.createRoom(Database.GAME_GROUP_NUM, ratio, true);
				// 创建失败
				if (result.equals(HttpRequest.FAIL_STATE)) {
					DialogUtils.mesTip(getString(R.string.room_create_fail), false);
					return TaskResult.FAILED;
				} else if (HttpRequest.TOKEN_ILLEGAL.equals(result)) { // 用户登录Token过期
					DialogUtils.reLogin(Database.currentActivity);
					return TaskResult.NO_LOGIN;
				} else {
					CmdDetail detail = JsonHelper.fromJson(result, CmdDetail.class);
					if (CmdUtils.CMD_ERR_CREATE.equals(detail.getCmd())) { // 创建失败
						String v = detail.getDetail();
						if (HttpRequest.NO_LOGIN.equals(v) || HttpRequest.TOKEN_ILLEGAL.equals(v)) { // 未登录,用户登录token非法
							DialogUtils.reLogin(Database.currentActivity);
						} else if (HttpRequest.NO_SERVER.equals(v)) { // 游戏服务器不存在
							DialogUtils.mesTip(getString(R.string.no_game_server), false);
						}
						return TaskResult.FAILED;
					} else if (CmdUtils.CMD_HDETAIL.equals(detail.getCmd())) { //金豆不足
						Room vipRoom = new Room();
						vipRoom.setLimit(Long.parseLong(detail.getDetail()));
						vipRoom.setHomeType(Constant.ROOM_VIP_PRIVATE);
//						DialogUtils.rechargeTip(vipRoom, false, null);
						JDSMSPayUtil.setContext(context);
						double b = RechargeUtils.calRoomJoinMoney(vipRoom);
						PayTipUtils.showTip(b, PaySite.VIP_CREATE); //配置的提示方式
						return TaskResult.FAILED;
					} else if (CmdUtils.CMD_CREATE.equals(detail.getCmd())) { // 成功
						if (HttpRequest.SUCCESS_STATE.equals(detail.getDetail())) { // 校验通过
							Database.JOIN_ROOM_RATIO = ratio; // vip包房倍数
							Intent in = new Intent();
							in.putExtra("isviproom", true); // 标识是vip包房
							in.putExtra("type", 1); // 邀请加入vip
							in.setClass(DoudizhuRoomListActivity.this, InviteToDowanloadActivity.class);
							startActivity(in);
						}
					}
				}
			} catch (Exception e) {
				DialogUtils.mesTip(getString(R.string.vip_create_fail), false);
				e.printStackTrace();
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.room_list_layout: // 点击屏幕
				if (mMainMenuBar.getGoodsLayout().getVisibility() == View.VISIBLE) {
					mMainMenuBar.getGoodsLayout().setVisibility(View.GONE);
					mMainMenuBar.getTransparentTv().setVisibility(View.GONE);
				}
				break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		final Context ctx = CrashApplication.getInstance();
		// 重写返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mMainMenuBar.getGoodsLayout().getVisibility() == View.VISIBLE) {
				mMainMenuBar.getGoodsLayout().setVisibility(View.GONE);
				mMainMenuBar.getTransparentTv().setVisibility(View.GONE);
				return true;
			} else {
//				try {
//					GameDialog exitDialog = new GameDialog(DoudizhuRoomListActivity.this) {
//
//						public void okClick() {
//							MobclickAgent.onEvent(ctx, "房间列表确认退出游戏");
//							Intent in = new Intent();
//							in.setClass(DoudizhuRoomListActivity.this, SDKFactory.getLoginView());
//							startActivity(in);
//						}
//
//						public void cancelClick() {
//							dismiss();
//							MobclickAgent.onEvent(ctx, "房间列表取消退出游戏");
//						};
//					};
//					exitDialog.show();
//					exitDialog.setText("是否退出游戏？");
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 游戏签到
	 * */
	private void sign() {
		ThreadPool.startWork(new Runnable() {

			public void run() {
				try {
					String result1 = HttpRequest.checkSign();
					if (!("1".equals(result1))) {
						Map<String, Integer> phoneMap = JsonHelper.fromJson(result1, new TypeToken<Map<String, Integer>>() {});
						int sign = phoneMap.get("sign");
						signCount = phoneMap.get("signCount");
						if (0 == sign) {
							final String result = HttpRequest.setSign();
							Message msg = new Message();
							msg.what = HANDLER_WHAT_ROOM_LIST_GAME_SIGN;
							Bundle b = new Bundle();
							b.putString("result", result);
							msg.setData(b);
							mHandler.sendMessage(msg);
						}
					}
				} catch (Exception e) {}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			PackageReceiver.unregisterReceiver(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (creatAT != null) {
			creatAT.cancel();
			creatAT = null;
			task.cancel();
		}
		AssistantBean.assistantBean = null;
		DataCentreBean.datacentrebean = null;
		//		小美女图标或者消息内容本身存在时，有新消息但没显示时，把旧消息加入消息中心
		if (Database.ADD_DATA_CENTRE && Assistant.GAME_ASSISTANT != null) {
			String json = JsonHelper.toJson(Assistant.GAME_ASSISTANT);
			ContentValues values = new ContentValues();
			values.put(DataCentreBean.DATA_ID, Integer.parseInt(Assistant.GAME_ASSISTANT.get(0).get(AssistantBean.AS_ID).toString()));
			values.put(DataCentreBean.DATA_CONTENT, json);
			values.put(DataCentreBean.DATA_RACE, DataCentreBean.RACE_AS);
			values.put(DataCentreBean.DATA_CLICK, "0");
			String[] id = { Assistant.GAME_ASSISTANT.get(0).get(AssistantBean.AS_ID).toString().toString() };
			DataCentreBean.getInstance().save(LoginActivity.dbHelper, values, id);
		}
		this.mHandler = null;
		if (vipJoinTask != null) {
			vipJoinTask.cancel(true);
			vipJoinTask = null;
		}
		if (vipCreateCheckTask != null) {
			vipCreateCheckTask.cancel(true);
			vipCreateCheckTask = null;
		}
		if (refreshTvTask != null) {
			refreshTvTask.stop(true);
			refreshTvTask = null;
		}
		if (loadRoomTask != null) {
			loadRoomTask.stop(true);
			loadRoomTask = null;
		}
		if (loadGamePlaceTask != null) {
			loadGamePlaceTask.stop(true);
			loadGamePlaceTask = null;
		}
		ImageUtil.releaseDrawable(roomCenterBg.getBackground());
		ImageUtil.releaseDrawable(roomTopll.getBackground());
		ImageUtil.releaseDrawable(roomListLayout.getBackground());
		ImageUtil.releaseDrawable(commonRoomBtn.getBackground());
//		ImageUtil.releaseDrawable(fgpBtn.getBackground());
		ImageUtil.releaseDrawable(gamePlaceBtn.getBackground());
		ImageUtil.releaseDrawable(vipRoomBtn.getBackground());
		ImageUtil.releaseDrawable(roomVipView.getBackground());
		ImageUtil.releaseDrawable(vipJoinBtn.getBackground());
		ImageUtil.releaseDrawable(vipRatioAdd.getBackground());
		ImageUtil.releaseDrawable(vipRatioLost.getBackground());
		ImageUtil.releaseDrawable(vipRoomCreate.getBackground());
		ActivityPool.remove(this);
		if (null != roomListLayout) {
			roomListLayout.removeAllViews();
			roomListLayout = null;
		}
		if (null != roomListGuide) {
			roomListGuide.removeAllViews();
			roomListGuide = null;
		}
		if (roomListGuide != null) {
			roomListGuide.removeAllViews();
			roomListGuide = null;
		}
		if (mFGPlaceListAdapter != null) {
			mFGPlaceListAdapter.onDestory();
			mFGPlaceListAdapter = null;
		}
		if (null != mFHGPlaceListAdapter) {
			mFHGPlaceListAdapter.onDestory();
			mFHGPlaceListAdapter = null;
		}
		mst = null;
		if (null != roomListAdapter) {
			roomListAdapter.onDestory();
			roomListAdapter = null;
		}
		if (null != mMainMenuBar) {
			mMainMenuBar.onDestory();
			mMainMenuBar = null;
		}
		try {
			DoudizhuRoomListActivity.this.unregisterReceiver(mReciver);//注销广播	
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		//		ImageUtil.clearAdvImageCacheMap();// 清空Bitmap缓存
		ImageUtil.clearImageWeakMap();
		//		小美女图标或者消息内容本身存在时，有新消息但没显示时，把旧消息加入消息中心
		if (Database.ADD_DATA_CENTRE && Assistant.GAME_ASSISTANT != null) {
			String json = JsonHelper.toJson(Assistant.GAME_ASSISTANT);
			ContentValues values = new ContentValues();
			values.put(DataCentreBean.DATA_ID, Integer.parseInt(Assistant.GAME_ASSISTANT.get(0).get(AssistantBean.AS_ID).toString()));
			values.put(DataCentreBean.DATA_CONTENT, json);
			values.put(DataCentreBean.DATA_RACE, DataCentreBean.RACE_AS);
			values.put(DataCentreBean.DATA_CLICK, "0");
			String[] id = { Assistant.GAME_ASSISTANT.get(0).get(AssistantBean.AS_ID).toString().toString() };
			DataCentreBean.getInstance().save(LoginActivity.dbHelper, values, id);
		}
	}

	/**
	 * 此广播用于接收通知刷新系统时间的显示
	 * @author Administrator
	 */
	private class MyBroadcastReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Constant.SYSTEM_TIME_CHANGE_ACTION)) {
				mHandler.sendEmptyMessage(HANDLER_WHAT_ROOM_LIST_CHANGE_SYSTEM_TIME);
			}
		}
	}
}
