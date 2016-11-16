package com.lordcard.ui.dizhu;

import com.zzyddz.shui.R;
import com.zzyddz.shui.R.color;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.anim.AnimUtils;
import com.lordcard.common.anim.PlayCardEffect;
import com.lordcard.common.listener.HasTiShiListenner;
import com.lordcard.common.schedule.AutoTask;
import com.lordcard.common.schedule.ScheduledTask;
import com.lordcard.common.task.GenericTask;
import com.lordcard.common.task.base.TaskParams;
import com.lordcard.common.task.base.TaskResult;
import com.lordcard.common.util.ActivityPool;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.AudioPlayUtils;
import com.lordcard.common.util.Base64Util;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.EncodeUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.ImageUtil.ImageCallback;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.PatternUtils;
import com.lordcard.common.util.PreferenceHelper;
import com.lordcard.common.util.Vibrate;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.GamePropsType;
import com.lordcard.entity.GameScoreTradeRank;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.GenLandowners;
import com.lordcard.entity.Grab;
import com.lordcard.entity.LastCards;
import com.lordcard.entity.MarqueeText;
import com.lordcard.entity.Play;
import com.lordcard.entity.Poker;
import com.lordcard.entity.PrizeGoods;
import com.lordcard.entity.ReLink;
import com.lordcard.entity.ReLinkUser;
import com.lordcard.entity.Room;
import com.lordcard.entity.TiLa;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.network.cmdmgr.Client;
import com.lordcard.network.cmdmgr.ClientCmdMgr;
import com.lordcard.network.cmdmgr.CmdDetail;
import com.lordcard.network.cmdmgr.CmdUtils;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.lordcard.network.socket.HURLEncoder;
import com.lordcard.network.socket.ICallback;
import com.lordcard.network.task.GetRankTask;
import com.lordcard.rule.DouDiZhuData;
import com.lordcard.rule.DoudizhuRule;
import com.lordcard.rule.HintPokerUtil;
import com.lordcard.rule.PokerUtil;
import com.lordcard.ui.base.BaseActivity;
import com.lordcard.ui.base.IGameView;
import com.lordcard.ui.interfaces.ChangeProInterface;
import com.lordcard.ui.interfaces.InitMainGameInterface;
import com.lordcard.ui.view.ADWideget;
import com.lordcard.ui.view.GameWaitView;
import com.lordcard.ui.view.JiPaiQiTurnPlateView;
import com.lordcard.ui.view.JiPaiQiTurnPlateView.Location;
import com.lordcard.ui.view.MainGameGuideView;
import com.lordcard.ui.view.RecordPorkerView;
import com.lordcard.ui.view.TouchRelativeLayout;
import com.lordcard.ui.view.dialog.ChatDialog;
import com.lordcard.ui.view.dialog.GameEndDialog;
import com.lordcard.ui.view.dialog.GameOverDialog;
import com.lordcard.ui.view.dialog.MatchRankDialog;
import com.lordcard.ui.view.dialog.PhotoDialog;
import com.lordcard.ui.view.dialog.SettingDialog;
import com.lordcard.ui.view.dialog.TipsDialog;
import com.lordcard.ui.view.notification.NotificationService;
//import com.sdk.jd.sms.util.JDSMSPayUtil;
import com.sdk.util.RechargeUtils;
import com.umeng.analytics.MobclickAgent;

@SuppressLint({ "HandlerLeak", "UseSparseArrays" })
public class DoudizhuMainGameActivity extends BaseActivity implements IGameView, OnTouchListener, HasTiShiListenner, OnGestureListener, InitMainGameInterface {

	/** 动画-无结束监听 */
	public static final int IS_NONE = 11100;
	/** 动画-飞机 */
	public static final int IS_FEIJI_ANIM = 11102;
	/** 动画-王炸 */
	public static final int IS_WANGZHA_ANIM = 11103;
	/** 动画-炸弹 */
	public static final int IS_ZHADAN_ANIM = 11105;
	private Context context;
	// 适应多屏幕的工具
	private List<ImageView> girlView;
	private boolean isWait5Second = false;
	private Handler handler = null;
	private Poker[] poker = null; // 扑克牌
	private TouchRelativeLayout myCardsTouchLayout;
	private RelativeLayout doudizhuLayout, doudizhuBackGround;
	private LinearLayout playBtnLayout;
	private RelativeLayout tuoGuanLayout;
	private RelativeLayout mySelfHeadRl;// 自己头像布局
	private TextView nullTv, nullTv2;// 做布局撑自己头像布局用的
	private int[] pai = null;
	private Button chupai, tishi, buchu = null;
	private AutoTask selfTask, leftTask, rightTask, pubTask, adTask, gameTask, task2;
	private MarqueeText marqueeText;
	private TextView play1SurplusCount, play3SurplusCount, play2SurplusCount = null;
	private List<Poker> nowcard = null; // 现在手中的牌
	private List<Poker> chupaicard = null; // 准备出的牌
	private List<Poker> checkpai = null;
	private List<Poker> otherplay1 = null; // 准备出的牌
	private List<BitmapVO> cashList = null;
	private int[] paixu;
	private int[] bierenchupai = null;
	private int typeMe = 0; // 自己出牌的类型
	private int valueMe = -1;
	private int typeplay1 = 0;// 别人出牌的类型
	private int mySelfOrder;// 自己出牌的顺序为
	private String mySelfId = null; // 自己的id
	private Thread popThread;// popwindow线程
	private int card_jiange = 37;
	private boolean firstChupai = true, isTuoguan = false;
	private TextView play1Timer, play3Timer, play2Timer = null;
	private TextView zhidou = null;
	private TextView netSlowTip = null;
	private ImageView play1Icon, play3Icon, play2Icon = null;// 头像
	private ImageView zhezhao1, zhezhao3, zhezhao2 = null;
	private RelativeLayout dpRl1, dpRl2, dpRl3;// 盾牌1，盾牌2，盾牌3
	private static final int DP_WIDTH = 22, DP_HEIGHT = 25, DP_PANDING = 2;
	private int masterOrder = 0;
	private JSONObject advList = null;
	private ADWideget adWidget = null;
	private String beishuNumber = null;
	private RelativeLayout.LayoutParams adWidgetLayoutParam = null;
	private SettingDialog settingDialog = null;
	private ImageButton gameRobot, gameSet, tuoGuan, gamePay;
	// 玩家名称
	private TextView playTextView1, playTextView3, playTextView2 = null;
	private TextView wolTv1, wolTv2, wolTv3;// 玩家输赢金豆动画Tv
	private TextView iqTv1, iqTv2, iqTv3;// 等级值
	private RelativeLayout play1PassLayout, play2PassLayout, play3PassLayout;
	private RelativeLayout mSystemInfoRl;// 信息总布局，系统信息布局
	private boolean isSystemInfo = false;
	private TextView systemTime = null;
	private ImageView systemWifi, systemPower = null;
	private ImageView zhadanImageView, wangzhaImageView, shunzImageView, feijiImageView;
	private int PLAY2ICON_ID, PLAY3ICON_ID, ZHEZHAO2_ID, ZHEZHAO3_ID, JIABEI2_ID, JIABEI3_ID;
	// 玩家位置
	private TextView play1Order, play2Order, play3Order = null;
	public static boolean play2IsTuoGuan = false;
	public static boolean play3IsTuoGuan = false;
	private RelativeLayout userinfoshowView = null; // playBusyLayout
	private TextView userInfoText = null;
	private ImageView messbtnView;
	private Activity ctx = null;
	private LinearLayout myFrame, rightFrame, leftFrame, girlLeftFrame, girlRightFrame;
	private TextView girlLeftTv, girlRightTv;
	private boolean canFlipper = true;
	private GameWaitView gameWaitLayout = null;
	private Button rankTop;// 排名按钮
	private int tiShiCount = -1;
	//private RelativeLayout baoXiangLayout, baoxiang;// 宝箱布局,宝箱
	//private TextView baoText;
	private int quang, allQuan = 0;
	private boolean baoFlag = false;
	private LinearLayout publicLayout;
	private boolean isTurnMySelf = true; // 是否轮到自己
	private TextView networkSlowtip; // 网络慢提示语
	private boolean areButtonsShowing;
	private MainGameGuideView mainGameGuideVI;// 手势提示引导View
	private TextView gpType, gpRound, gpScore, gpCount, gpRank = null;
	// private ImageView slipIv;
	private LinearLayout gpRl;
	private ImageView zhadanIv; // 炸弹
	private Map<Integer, Boolean> warn = null; // 警告记录(是否有警告过)
	private int callPoint = 0;
	private int type = 0;
	private GoodsValuesAdapter valueAdapter;
	private RelativeLayout nextPlayLayout;
	private TextView countdownTv;// 倒计时Tv
	private MyBroadcastReciver mReciver;// 刷新系统时间的广播接收器
	private MyBatteryReceiver mMyBatteryReceiver = null;// 刷新系统电量的广播接收器
	private long countDownTime = 0;// 剩余时间
	private GenericTask checkJoinTask;
	private boolean hasCallReady = false;// 后台是否请求准备状态
	private boolean hasEnd = false;// 后台返回结束命令
	private List<Map<String, String>> girls;
	private ImageView girlItems;// 美女图鉴按钮
	private PopupWindow popupWindow;// 美女图鉴弹出框
	private ImageView imageNewIv;// 新美女图鉴提示标签
	private GridView girlimgList;
	private LinearLayout gridlLayout;
	private List<GamePropsType> toolList, usetool;
	private ViewFlipper viewFlipper = null;
	// private GestureDetector mGestureDetector;
	private GestureDetector gestureDetector = null;
	private List<Map<String, String>> girlList;
	private Button back;
	private View popupWindow_view;
	private int curPage;// 当前第几张，做标志用
	private ChatDialog mChatDialog;//聊天对话框
	private GameEndDialog mGameEndDialog;// 结束对话框
	private MatchRankDialog mrDialog;// 排名对话框(快速赛)
	private GameOverDialog god;// 比赛结束排名对话框
	// private Vibrate vibrate;
	// private boolean offVibrate = false;
	private int jiaofenNum;
	private int jiao;// 手势点击（叫分）的次数
	private boolean jiao1 = false, jiao2 = false, jiao3 = false;// （手势）可以叫分标识
	private int jia;// 手势点击（加倍）的次数
	boolean isLongClickModule = false;
	float startX, startY;
	Timer timer;
	private boolean selfIsMove = false;// 自己的头像是否移动过
	/** 美女组件更新标志是否显示 */
	private static boolean newImageIsShow = false;
	private RelativeLayout beansInsufficientRl;// 智斗不足显示布局
	private TextView beansInsufficientTv;// 智斗不足显示Tv
	private LinearLayout rechargeLl;// 充值按钮容器布局
	private Button rechargeBtn;// 充值按钮
	private int rechargeMoney = 0;//预充值金额
	private Button borrowBeansBtn;// 借点豆
	/** 是否已显示预充值画面 **/
	private boolean isShowPrerechargeDialog = false;
	/** 自己加倍倍数**/
	private int DoubleNum = 1;
	/** 下家的加倍倍数**/
	private int DoubleNum2 = 1;
	/** 上家的加倍倍数**/
	private int DoubleNum3 = 1;
	private static String gameOverDetail = "";// 比赛结束对话框信息
	/** 记牌器 **/
	private JiPaiQiTurnPlateView leftJiPaiQiTurnPlateView;
	private JiPaiQiTurnPlateView rightJiPaiQiTurnPlateView;
	private RecordPorkerView recordPorkerView;
	private Dialog jiPaiQiChargeDialog = null;
	private View topLeftUserView;
	private View topRightUserView;
	private View topLeftShieldView;
	private View topRightShieldView;
	private View cardStatView;
	//private View leftJiPaiQiLayout;
	//private View rightJiPaiQiLayout;
	private boolean isJiPaiQiEnable = false;
	private String jiPaiQiTipsMsg;
	private Button btn_jipaiqi;
	private List<Bitmap> bitmapList = null;
	private TextView text_kingb;
	private TextView text_kings;
	private TextView text_2;
	private TextView text_A;
	private TextView text_K;
	private TextView text_Q;
	private TextView text_J;
	private TextView text_10;
	private TextView text_9;
	private TextView text_8;
	private TextView text_7;
	private TextView text_6;
	private TextView text_5;
	private TextView text_4;
	private TextView text_3;
	/*james add start */
	private int initCardNum=16;
	/*james add end */
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doudizhu_gameview);
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			type = bundle.getInt("type", 0);
		}
		initGame(type);
		context = this;
	}

	private void initGame(int type) {
		/** 刷新用户物品信息 **/
		this.type = type;
		selfIsMove = false;
		newImageIsShow = false;
		isSystemInfo = false;
		play2IsTuoGuan = false;
		play3IsTuoGuan = false;
		callPoint = 0;
		HttpRequest.getCacheServer(true);
		ctx = this;
		AudioPlayUtils.isPlay = true;
		card_jiange = mst.adjustXIgnoreDensity(37);// 每一张牌的间隔
		warn = new HashMap<Integer, Boolean>();
		// 设置常亮
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// 初始化界面元素
		gestureDetector = new GestureDetector(this);
		initView();
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		initHandler();
		gameWaitLayout = new GameWaitView(this, handler);
		doudizhuLayout.addView(gameWaitLayout, layoutParams);
		gameWaitLayout.setOnClickListener(null);
		// 显示用户信息
		setUserInfo();
		tishi.setOnClickListener(clickListener); // 提示监听
		buchu.setOnClickListener(clickListener); // 不出
		chupai.setOnClickListener(clickListener); // 出牌
		messbtnView.setOnClickListener(clickListener);
		joinGame(true);
	}

	/**
	 * 校验游戏
	 */
	private void joinGame(boolean hasmst) {
		// 加入游戏前校验
		checkJoinTask = new CheckJoinTask();
		TaskParams params = new TaskParams();
		params.put("homeCode", Database.JOIN_ROOM_CODE);
		params.put("passwd", "");
		checkJoinTask.execute(params);
		taskManager.addTask(checkJoinTask);
		if (hasmst) {
			mst.adjustView(doudizhuLayout, false);
		}
	}

	OnClickListener clickListener = new OnClickListener() {

		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.playimageView1 || id == R.id.zhezhao1) { // 位置1玩家图像
				MobclickAgent.onEvent(ctx,"游戏中点玩家1头像");
				String p1o = play1Order.getText().toString();
				if (!TextUtils.isEmpty(p1o)) {
					photoClick(play1Icon, Integer.parseInt(p1o), mySelfOrder == masterOrder);
				}
			} else if (id == PLAY2ICON_ID || id == ZHEZHAO2_ID) { // 位置2玩家图像
				MobclickAgent.onEvent(ctx,"游戏中点玩家2头像");
				String p2o = play2Order.getText().toString();
				if (!TextUtils.isEmpty(p2o)) {
					photoClick(play2Icon, Integer.parseInt(p2o), (PLAY2ICON_ID - 1300) == masterOrder);
				}
			} else if (id == PLAY3ICON_ID || id == ZHEZHAO3_ID) { // 位置3玩家图像
				MobclickAgent.onEvent(ctx,"游戏中点玩家3头像");
				String p3o = play3Order.getText().toString();
				if (!TextUtils.isEmpty(p3o)) {
					photoClick(play3Icon, Integer.parseInt(p3o), (PLAY3ICON_ID - 1300) == masterOrder);
				}
			} else {
				switch (id) {
					case R.id.mess_btn_view:
						if (Math.abs(System.currentTimeMillis() - Constant.CLICK_TIME) >= Constant.SPACING_TIME) {// 防止重复刷新
							Constant.CLICK_TIME = System.currentTimeMillis();
							if (null == mChatDialog) {
								mChatDialog = new ChatDialog(ctx, handler);
							}
							if (!mChatDialog.isShowing()) {
								mChatDialog.show();
							}
							MobclickAgent.onEvent(ctx,"游戏中聊天互动");
						}
						break;
					case R.id.chupai_button:// 出牌
						setTishiGone();
						TextView myTime = (TextView) findViewById(R.id.play1Time);
						if (null != myTime) {
							int time = Integer.parseInt(myTime.getText().toString());
							if (time > 0 && time != 20) {
								playCard(false);
							}
						}
						break;
					case R.id.pass_button:// 不要
						setTishiGone();
						passCard();
						break;
					case R.id.tishi_button:// 提示
						setTishi();
						break;
					case R.id.game_back:
						MobclickAgent.onEvent(ctx,"游戏中退出");
						DialogUtils.exitGame(ctx);
						break;
					case R.id.game_robot: // "点击托管"
						gameRobotClick();
						break;
					case R.id.game_set:
						if (Math.abs(System.currentTimeMillis() - Constant.CLICK_TIME) >= Constant.SPACING_TIME) {// 防止重复刷新
							Constant.CLICK_TIME = System.currentTimeMillis();
							settingDialog.show();
							settingDialog.setPro();
						}
						break;
					case R.id.tuo_guan_btn:
					case R.id.tuo_guan_layout: // "取消托管"
						cancelTuoGuan();
						break;
					case R.id.girl_right_frame:// 点击右边美女图片
						handler.sendEmptyMessage(406);
						break;
					case R.id.girl_left_frame:// 点击左边美女图片
						handler.sendEmptyMessage(405);
						break;
					case R.id.gp_top_btn:// 排名(快速赛制)
						if (1 == Database.JOIN_ROOM.getRoomType()) {
							if (Math.abs(System.currentTimeMillis() - Constant.CLICK_TIME) >= Constant.SPACING_TIME) {
								Constant.CLICK_TIME = System.currentTimeMillis();
								GetRankTask getRankTask = new GetRankTask();
								getRankTask.execute();
							}
						}
						break;
					case R.id.game_gilr_items:
						MobclickAgent.onEvent(ctx,"游戏中美女道具");
						girlItems.setVisibility(View.INVISIBLE);
						showPopWindow(true);
						if (null != toolList) {
							setImageNewGone(toolList.size());
						}
						break;
					case R.id.dzed_borrow_beans_btn:// 借点豆
						int allDouble = getCallDoubleNum();
						GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
						long currentWager = (Integer.valueOf(beishuNumber) * Database.JOIN_ROOM_BASEPOINT) * allDouble;
						break;
				}
			}
		}
	};

	private void showPopWindow(final boolean isShow) {
		popThread = new Thread() {

			public void run() {
				if (usetool == null) {
					GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("loginToken", cacheUser.getLoginToken());
					String result = HttpUtils.post(HttpURL.MAIN_DAOJU_URL, paramMap, true);
					try {
						if (!TextUtils.isEmpty(result)) {
							GameCache.putStr(Constant.GAME_GIRL_CACHE, result);// 保存美女供单机使用
							usetool = JsonHelper.fromJson(result, new TypeToken<List<GamePropsType>>() {});
						}
					} catch (Exception e) {}
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						try {
							if (toolList == null) {
								if (usetool != null && usetool.size() > 0) {
									List<GamePropsType> alllList = usetool;
									for (int i = 0; i < alllList.size(); i++) {
										boolean hasAll = true;
										if (alllList.get(i).getType().equals("1")) {
											if (null != ImageUtil.getGirlBitmap(HttpURL.URL_PIC_ALL + alllList.get(i).getPicPath(), false, false)) {
												girlList = JsonHelper.fromJson(alllList.get(i).getContent(), new TypeToken<List<Map<String, String>>>() {});
												for (int j = 0; j < girlList.size(); j++) {
													if (null != ImageUtil.getGirlBitmap(HttpURL.URL_PIC_ALL + girlList.get(j).get("path"), false, false)) {} else {
														hasAll = false;
													}
												}
											} else {
												hasAll = false;
											}
											if (hasAll) {} else {
												usetool.get(i).setType("-2");
											}
										}
									}
								}
								toolList = new ArrayList<GamePropsType>();
								if (usetool != null && usetool.size() > 0) {
									for (int i = 0; i < usetool.size(); i++) {
										if (usetool.get(i).getType().equals("1")) {
											toolList.add(usetool.get(i));
										}
									}
								}
								// 新建一个道具复原，type为"-1"
								GamePropsType reback = new GamePropsType();
								reback.setType("-1");
								toolList.add(reback);
							}
							if (isShow) {
								getPopupWindow();
								popupWindow.showAsDropDown(findViewById(R.id.pop_iv), 0, 0);
							} else {
								setImageNewVisible(toolList.size());
							}
						} catch (Exception e) {}
					}
				});
			};
		};
		popThread.start();
	}

	private void getPopupWindow() {
		if (null != popupWindow) {
			popupWindow.dismiss();
			return;
		} else {
			initPopuptWindow();
		}
	}

	protected void initPopuptWindow() {
		girlItems.setVisibility(View.INVISIBLE);
		popupWindow_view = getLayoutInflater().inflate(R.layout.pop, null, false);
		mst.unRegisterView(popupWindow_view.findViewById(R.id.login_sliding_content));
		mst.adjustView(popupWindow_view.findViewById(R.id.login_sliding_content));
		RelativeLayout layout = (RelativeLayout) popupWindow_view.findViewById(R.id.login_sliding_content);
		popupWindow = new PopupWindow(popupWindow_view, mst.adjustXIgnoreDensity(450), mst.adjustYIgnoreDensity(105), true);
		popupWindow.setFocusable(true);
		girlimgList = (GridView) popupWindow_view.findViewById(R.id.valuesgrid);
		girlimgList.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridlLayout = (LinearLayout) popupWindow_view.findViewById(R.id.grid_layout);
		gridlLayout.setGravity(Gravity.CENTER_VERTICAL);
		int space = 6;
		int numColumn = 95;
		int size = toolList.size();
		android.widget.LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) girlimgList.getLayoutParams(); // 取控件mGrid当前的布局参数
		linearParams.width = size * (mst.adjustXIgnoreDensity(numColumn + space)) + 20;
		girlimgList.setLayoutParams(linearParams);
		girlimgList.setGravity(Gravity.CENTER_VERTICAL);
		girlimgList.setNumColumns(size);
		girlimgList.setColumnWidth(mst.adjustXIgnoreDensity(numColumn));
		girlimgList.setHorizontalSpacing(mst.adjustXIgnoreDensity(space));
		girlimgList.setStretchMode(GridView.NO_STRETCH);
		valueAdapter = new GoodsValuesAdapter(toolList);
		girlimgList.setAdapter(valueAdapter);
		// 道具点击事件，目前只有美女和复原
		girlimgList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int posision, long arg3) {
				try {
					if (toolList.get(posision).getType().equals("1")) {
						viewFlipper.setVisibility(View.VISIBLE);
						GameCache.putStr(Constant.GAME_BACKGROUND, toolList.get(posision).getContent());
						initViewFlipper(toolList.get(posision).getContent());
						girls = JsonHelper.fromJson(toolList.get(posision).getContent(), new TypeToken<List<Map<String, String>>>() {});
						mainGameGuideVI.setArrowLeftRightVisible();
					} else if (toolList.get(posision).getType().equals("-1")) {// 复原
						GameCache.putStr(Constant.GAME_BACKGROUND, "");
						viewFlipper.removeAllViews();
						girls = null;
						valueAdapter = null;
						viewFlipper.setVisibility(View.INVISIBLE);
					}
				} catch (Exception e) {}
				popoDismiss();
				ImageUtil.clearGirlBitMapCache();
			}
		});
		layout.setFocusableInTouchMode(true);// 能够获得焦点
		layout.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
						case KeyEvent.KEYCODE_BACK:
							popoDismiss();
							break;
					}
				}
				return true;
			}
		});
		back = (Button) popupWindow_view.findViewById(R.id.back_btn);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popoDismiss();
			}
		});
		popupWindow.setAnimationStyle(R.style.AnimationFade);
		popupWindow_view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				popoDismiss();
				return false;
			}
		});
	}

	/**
	 * 记录玩家已出牌
	 * 
	 * @param order
	 *            当前打牌的玩家顺序
	 * @param mPokers
	 *            要出的牌
	 */
	public void addOutPokers(int order, List<Poker> mPokers) {
		if (mPokers == null || order > 3 || order < 1)
			return;
		List<Poker> pokers = new ArrayList<Poker>();
		for (Poker poker : mPokers) {
			pokers.add(poker);
		}
		/** flag可为 1,2,3 1：代表自己出牌；2代表右手边玩家出牌；3代表左手边玩家出牌 **/
		int flag = 1;
		if (order == getPerOrder(mySelfOrder)) {
			flag = 3;
		} else if (order == getNextOrder(mySelfOrder)) {
			flag = 2;
		}
		switch (flag) {
			case 1:
				recordPorkerView.addCardList(pokers);
				break;
			case 2:
				rightJiPaiQiTurnPlateView.addCardList(pokers);
				break;
			case 3:
				leftJiPaiQiTurnPlateView.addCardList(pokers);
				break;
		}
	}

	public void refreshCardCountData() {
		int count = 4;
		for (int i = 3; i <= initCardNum; i++) {
			if (i == (initCardNum-1) || i == initCardNum)
				count = 1;
			else
				count = 4;
			/** 左边玩家已出牌 **/
			for (List<Poker> leftPokerList : leftJiPaiQiTurnPlateView.getCardList()) {
				for (Poker mPoker : leftPokerList)
					if (mPoker.getValue() == i)
						count--;
			}
			/** 右边玩家已出牌 **/
			for (List<Poker> rightPokerList : rightJiPaiQiTurnPlateView.getCardList()) {
				for (Poker mPoker : rightPokerList)
					if (mPoker.getValue() == i)
						count--;
			}
			/** 自己已出牌 **/
			for (List<Poker> myPokerList : recordPorkerView.getCardList()) {
				for (Poker mPoker : myPokerList)
					if (mPoker.getValue() == i)
						count--;
			}
			/** 自己当前拥有的出牌 **/
			for (Poker myOwnPoker : nowcard) {
				if (myOwnPoker.getValue() == i)
					count--;
			}
			switch (i) {
				case 3:
					text_3.setText(String.valueOf(count));
					break;
				case 4:
					text_4.setText(String.valueOf(count));
					break;
				case 5:
					text_5.setText(String.valueOf(count));
					break;
				case 6:
					text_6.setText(String.valueOf(count));
					break;
				case 7:
					text_7.setText(String.valueOf(count));
					break;
				case 8:
					text_8.setText(String.valueOf(count));
					break;
				case 9:
					text_9.setText(String.valueOf(count));
					break;
				case 10:
					text_10.setText(String.valueOf(count));
					break;
				case 11:
					text_J.setText(String.valueOf(count));
					break;
				case 12:
					text_Q.setText(String.valueOf(count));
					break;
				case 13:
					text_K.setText(String.valueOf(count));
					break;
				case 14:
					text_A.setText(String.valueOf(count));
					break;
				case 15:
					text_2.setText(String.valueOf(count));
					break;
				case 16:
					text_kings.setText(String.valueOf(count));
					break;
				case 17:
					text_kingb.setText(String.valueOf(count));
					break;
			}
		}
	}

	/**
	 * 获取自己应结算的加倍数
	 * @return
	 */
	private int getCallDoubleNum() {
		int allDouble = DoubleNum;
		//如果自己是地主,倍数未所有人倍数之和
		if (0 != masterOrder && masterOrder == mySelfOrder) {
			int callDouble2 = (1 == DoubleNum2) ? 1 : (DoubleNum * DoubleNum2);
			int callDouble3 = (1 == DoubleNum3) ? 1 : (DoubleNum * DoubleNum3);
			allDouble = callDouble2 + callDouble3;
		} else {
			if (masterOrder == getNextOrder(mySelfOrder)) {
				allDouble = (1==DoubleNum) ? 1 : DoubleNum * DoubleNum2;
			} else {
				allDouble = (1==DoubleNum) ? 1 : DoubleNum * DoubleNum3;
			}
		}
		return allDouble;
	}

	/**
	 * 刷新用户物品信息
	 */
	public void refreshUserGoodsInfo() {
		ThreadPool.startWork(new Runnable() {

			public void run() {
				HttpRequest.getGameUserGoods(false);
			}
		});
	}

	private void initViewFlipper(String girlList) {
		canFlipper = true;
		viewFlipper.removeAllViews();
		curPage = 0;
		girlView = null;
		girlView = new ArrayList<ImageView>();
		viewFlipper.setVisibility(View.VISIBLE);
		girls = JsonHelper.fromJson(girlList, new TypeToken<List<Map<String, String>>>() {});
		for (int i = 0; i < 3; i++) {
			ImageView image = new ImageView(DoudizhuMainGameActivity.this);
			girlView.add(image);
		}
		for (int i = 0; i < girlView.size(); i++) {
			int point = curPage - 1;
			if (point < 0) {
				point = girls.size() - 1;
			}
			Drawable draw = ImageUtil.getcutBitmap(HttpURL.URL_PIC_ALL + girls.get(i).get("path"), false);
			if (null != draw) {
				girlView.get(i).setBackgroundDrawable(draw);
				girlView.get(i).setScaleType(ImageView.ScaleType.FIT_XY);
				viewFlipper.addView(girlView.get(i), new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			}
		}
		readdView();
	}

	private void popoDismiss() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow_view = null;
			popupWindow = null;
			ScheduledTask.addDelayTask(new AutoTask() {

				@Override
				public void run() {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							girlItems.setVisibility(View.VISIBLE);
							if (!newImageIsShow) {
								if (null != toolList) {
									setImageNewVisible(toolList.size());
								}
								newImageIsShow = true;
							}
						}
					});
				}
			}, 600);
		}
	}

	public void giveCoupon() {
		new Thread() {

			public void run() {
				try {
					GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("loginToken", cacheUser.getLoginToken());
					paramMap.put("count", String.valueOf(quang));
					String count = HttpUtils.post(HttpURL.COUPON_GIVE_URL, paramMap);
					if (count.equals("0")) {
						if (6 == Database.JOIN_ROOM.getHomeType()) {
							DialogUtils.mesToastTip("亲，您目前未能获得钻石，继续加油吧！");
						} else {
							DialogUtils.mesToastTip("很遗憾,未获得任何物品");
						}
					} else {
						DialogUtils.mesToastTip(count);
					}
				} catch (Exception e) {}
			}
		}.start();
	}

	public void sendTextMessage(String talk, int clickType) {
		if (talk.equals("")) {
			Toast.makeText(DoudizhuMainGameActivity.this, "发送消息不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (talk.contains(";")) {
			talk = talk.replaceAll("\\;", " ");
		}
		CmdDetail chat = new CmdDetail();
		chat.setCmd(CmdUtils.CMD_CHAT);
		// chat.setGameToken(Database.USER.getLoginToken());
		CmdDetail detail = new CmdDetail();
		detail.setType(clickType);
		detail.setValue(talk);
		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		detail.setFromUserId(cacheUser.getAccount());
		String dj = JsonHelper.toJson(detail);
		chat.setDetail(dj);
		chat.urlEncode();
		myFrame.removeAllViews();
		startTask(myFrame, selfTask);
		messageFrame(myFrame, talk, clickType, null);
		CmdUtils.sendMessageCmd(chat);
	}

	/**
	 * 托管
	 */
	public void gameRobotClick() {
		tuoGuan.setClickable(true);
		isTuoguan = true;
		// 托管时的牌不可按
		for (int i = 0; i < myCardsTouchLayout.getChildCount(); i++) {
			myCardsTouchLayout.getChildAt(i).setClickable(false);
		}
		gameRobot.setClickable(false);
		tuoGuanLayout.setVisibility(View.VISIBLE);
		CmdUtils.sendIsRobot();
		AnimUtils.startScaleAnimationIn(tuoGuanLayout, ctx);
		areButtonsShowing = !areButtonsShowing;
		if (playBtnLayout.getVisibility() == View.VISIBLE) { // 如果打牌的时候托管
			setTuoGuan();
		}
	}

	/**
	 * 取消托管
	 */
	public void cancelTuoGuan() {
		cancelTuoGuanState();
		CmdUtils.sendCancelRobot();
		// 取消托管时的牌可按
		for (int i = 0; i < myCardsTouchLayout.getChildCount(); i++) {
			myCardsTouchLayout.getChildAt(i).setClickable(false);
		}
	}

	/**
	 * 打牌前的叫分
	 * 
	 * @param tempRatio
	 *            分数
	 * @param info
	 *            View
	 * @param order
	 *            玩家位置
	 * @param isLocal
	 *            是否是本地叫
	 */
	private void callPoints(int tempRatio, ImageView info, int order, boolean isLocal) {
		AudioPlayUtils audioPlayUtils = AudioPlayUtils.getInstance();
		String gender = Database.userMap.get(order).getGender();
		if (tempRatio == 0) {
			if ("1".equals(gender)) {
				AudioPlayUtils.getInstance().playSound(R.raw.nv_bujiao);
			} else {
				AudioPlayUtils.getInstance().playSound(R.raw.nan_bujiao); // 不叫
			}
			info.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.call_bujiao, true));
		} else if (tempRatio == 1) {
			if ("1".equals(gender)) {
				audioPlayUtils.playMusic(false, R.raw.nv_1fen); // 别人叫1分
			} else {
				audioPlayUtils.playMusic(false, R.raw.nan_1fen); // 别人叫1分
			}
			info.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.callone, true));
		} else if (tempRatio == 2) {
			if ("1".equals(gender)) {
				audioPlayUtils.playMusic(false, R.raw.nv_2fen); // 别人叫2分
			} else {
				audioPlayUtils.playMusic(false, R.raw.nan_2fen); // 别人叫2分
			}
			info.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.calltwo, true));
		} else if (tempRatio == 3) {
			// 自己叫的三分，要在产生地主的时候发出声音，以免声音重复
			if (!isLocal) {
				if ("1".equals(gender)) {
					audioPlayUtils.playMusic(false, R.raw.nv_3fen); // 别人叫3分
				} else {
					audioPlayUtils.playMusic(false, R.raw.nan_3fen); // 别人叫3分
				}
				info.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.callthree, true));
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		playOrStopBgMusic();
		leftJiPaiQiTurnPlateView.setLocation(Location.Top_Left);
	}

	@Override
	public void onPause() {
		super.onPause();
		AudioPlayUtils.isPlay = false;
		AudioPlayUtils.getInstance().stopBgMusic();
		AudioPlayUtils.getInstance().stopMusic();
	}

	/**
	 * 开启或关闭背景音乐
	 */
	private void playOrStopBgMusic() {
		AudioPlayUtils.isPlay = true;
		SharedPreferences sharedPreferences = PreferenceHelper.getMyPreference().getSetting();
		if (!sharedPreferences.getBoolean("bgmusic", true)) {
			AudioPlayUtils.getInstance().stopBgMusic();
		} else {
			AudioManager audiomanage = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
			int currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
			if(currentVolume>70)currentVolume = 70;
			AudioPlayUtils.getInstance().SetVoice(sharedPreferences.getInt("music", currentVolume));// 如果没有设置过音量，就获取系统的音量
			AudioPlayUtils.getInstance().playBgMusic(R.raw.mg_bg);
		}
	}

	// 捕获实体按钮事件
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			int nowVol = PreferenceHelper.getMyPreference().getSetting().getInt("music", 0);
			if (nowVol != 15) {
				PreferenceHelper.getMyPreference().getEditor().putInt("music", nowVol + 1);
				PreferenceHelper.getMyPreference().getEditor().commit();
				AudioPlayUtils.getInstance().SetVoice(PreferenceHelper.getMyPreference().getSetting().getInt("music", 0));
			}
			return false;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			int nowVol = PreferenceHelper.getMyPreference().getSetting().getInt("music", 0);
			if (nowVol != 0) {
				PreferenceHelper.getMyPreference().getEditor().putInt("music", nowVol - 1);
				PreferenceHelper.getMyPreference().getEditor().commit();
				AudioPlayUtils.getInstance().SetVoice(PreferenceHelper.getMyPreference().getSetting().getInt("music", 0));
			}
			return false;
		} else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (popupWindow != null && popupWindow.isShowing()) {
				girlItems.setVisibility(View.VISIBLE);
				popupWindow.dismiss();
				popupWindow_view = null;
				ImageUtil.clearGirlBitMapCache();
				popupWindow = null;
			} else {
				MobclickAgent.onEvent(DoudizhuMainGameActivity.this,"游戏中实体退出");
				DialogUtils.exitGame(DoudizhuMainGameActivity.this);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			AudioPlayUtils.isPlay = false;
			AudioPlayUtils.getInstance().stopBgMusic();
			AudioPlayUtils.getInstance().stopMusic();
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void onDestroy() {
		this.ctx = null;
		this.handler = null;
		gestureDetector = null;
		if (popThread != null) {
			popThread.interrupt();
			// popThread = null;
		}
		valueAdapter = null;
		AudioPlayUtils.getInstance().stopMusic();
		AudioPlayUtils.getInstance().stopBgMusic();
		gameWaitLayout.closeTimer();
		if (gameWaitLayout != null) {
			gameWaitLayout.onDestory();
		}
		gameWaitLayout = null;
		if (checkJoinTask != null) {
			checkJoinTask.cancel(true);
			checkJoinTask.setFeedback(null);
			checkJoinTask.setListener(null);
		}
		checkJoinTask = null;
		if (selfTask != null) {
			selfTask.stop(true);
			selfTask = null;
		}
		if (leftTask != null) {
			leftTask.stop(true);
			leftTask = null;
		}
		if (rightTask != null) {
			rightTask.stop(true);
			rightTask = null;
		}
		if (pubTask != null) {
			pubTask.stop(true);
			pubTask = null;
		}
		if (task2 != null) {
			task2.stop(true);
			task2 = null;
		}
		if (adTask != null) {
			adTask.stop(true);
			adTask = null;
		}
		cancelTimer();
		ImageUtil.releaseDrawable(doudizhuBackGround.getBackground());// 释放背景图片占的内存
		ImageUtil.releaseDrawable(play1Icon.getBackground());
		ImageUtil.releaseDrawable(play3Icon.getBackground());
		ImageUtil.releaseDrawable(play2Icon.getBackground());
		marqueeText.onDestory();
		marqueeText = null;
		if (null != cashList && cashList.size() > 0) {
			for (int i = 0; i < cashList.size();) {
				if (null != cashList.get(i) && !cashList.get(i).getImage().isRecycled()) {
					cashList.get(i).getImage().recycle();
				}
				cashList.remove(i);
				i = 0;
			}
			cashList.clear();
		}
		cashList = null;
		if (null != settingDialog && settingDialog.isShowing()) {
			settingDialog.dismiss();
		}
		settingDialog = null;
		if (null != mGameEndDialog && mGameEndDialog.isShowing()) {
			mGameEndDialog.dismiss();
		}
		mGameEndDialog = null;
		if (null != mChatDialog && mChatDialog.isShowing()) {
			mChatDialog.dismiss();
		}
		mChatDialog = null;
		if (null != god && god.isShowing()) {
			god.dismiss();
		}
		god = null;
		if (null != mrDialog && mrDialog.isShowing()) {
			mrDialog.dismiss();
		}
		mrDialog = null;
		pai = null;
		paixu = null;
		bierenchupai = null;
		if (null != warn) {
			warn.clear();
			warn = null;
		}
		myFrame.removeAllViews();
		myFrame = null;
		rightFrame.removeAllViews();
		rightFrame = null;
		leftFrame.removeAllViews();
		leftFrame = null;
		girlLeftFrame.removeAllViews();
		girlLeftFrame = null;
		girlRightFrame.removeAllViews();
		girlRightFrame = null;
		clickListener = null;
		if (null != nowcard) {
			for (Poker card : nowcard) {
				if (card != null) {
					card.onDestory();
				}
				card = null;
			}
			nowcard.clear();
			nowcard = null;
		}
		if (null != chupaicard) {
			for (Poker card : chupaicard) {
				if (card != null) {
					card.onDestory();
				}
				card = null;
			}
			chupaicard.clear();
			chupaicard = null;
		}
		if (null != checkpai) {
			for (Poker card : checkpai) {
				if (card != null) {
					card.onDestory();
				}
				card = null;
			}
			checkpai.clear();
			checkpai = null;
		}
		if (null != otherplay1) {
			for (Poker card : otherplay1) {
				if (card != null) {
					card.onDestory();
				}
				card = null;
			}
			otherplay1.clear();
			otherplay1 = null;
		}
		// 释放所有扑克牌所占的资源
		if (poker != null) {
			for (Poker card : poker) {
				if (card != null) {
					card.onDestory();
				}
				card = null;
			}
			poker = null;
		}
		cleanAllChuPaiInfo();
		play1PassLayout = null;
		play2PassLayout = null;
		play3PassLayout = null;

		if (myCardsTouchLayout != null) {
			myCardsTouchLayout.setListenner(null);
			myCardsTouchLayout.onDestory();
		}
		myCardsTouchLayout = null;
		if (nextPlayLayout != null) {
			nextPlayLayout.removeAllViews();
			nextPlayLayout = null;
		}
		userInfoText = null;
		networkSlowtip = null;
		tishi.setOnClickListener(null); // 提示监听
		buchu.setOnClickListener(null); // 不出
		chupai.setOnClickListener(null); // 出牌
		messbtnView.setOnClickListener(null);
		play1Icon.setOnClickListener(null);
		play2Icon.setOnClickListener(null);
		play3Icon.setOnClickListener(null);
		zhezhao1.setOnClickListener(null);
		zhezhao2.setOnClickListener(null);
		zhezhao3.setOnClickListener(null);
		gameRobot.setOnClickListener(null);
		gameSet.setOnClickListener(null);
		tuoGuan.setOnClickListener(null);
		playBtnLayout.removeAllViews();
		playBtnLayout = null;
		tuoGuanLayout.removeAllViews();
		tuoGuanLayout = null;
		gpRank = null;
		gpType = null;
		gpRound = null;
		gpScore = null;
		gpCount = null;
		play1SurplusCount = null;
		play3SurplusCount = null;
		play2SurplusCount = null;
		ImageUtil.releaseDrawable(play1Timer.getBackground());
		ImageUtil.releaseDrawable(play2Timer.getBackground());
		ImageUtil.releaseDrawable(play3Timer.getBackground());
		if (gameTask != null) {
			gameTask.stop(true);
			gameTask = null;
		}
		play1Timer = null;
		play2Timer = null;
		play3Timer = null;
		ImageUtil.releaseDrawable(girlLeftTv.getBackground());
		ImageUtil.releaseDrawable(girlRightTv.getBackground());
		girlLeftTv = null;
		girlRightTv = null;
		playTextView1 = null;
		playTextView3 = null;
		playTextView2 = null;
		iqTv1 = null;
		iqTv2 = null;
		iqTv3 = null;
		if (viewFlipper != null) {
			if (girls != null) {
				for (int i = 0; i < girls.size(); i++) {// 释放没有调用的bitmap
					ImageUtil.clearsingleCache(HttpURL.URL_PIC_ALL + girls.get(i).get("path"));
				}
			}
			viewFlipper.removeAllViews();
			girls = null;
			valueAdapter = null;
		}
		ImageUtil.releaseDrawable(play1Order.getBackground());
		ImageUtil.releaseDrawable(play2Order.getBackground());
		ImageUtil.releaseDrawable(play3Order.getBackground());
		play1Order = null;
		play2Order = null;
		play3Order = null;
		//beishuNumView = null;
		zhidou = null;
		ImageUtil.releaseDrawable(play1Icon.getBackground());
		ImageUtil.releaseDrawable(play2Icon.getBackground());
		ImageUtil.releaseDrawable(play3Icon.getBackground());
		play1Icon = null;
		play2Icon = null;
		play3Icon = null;
		zhezhao1 = null;
		zhezhao2 = null;
		zhezhao3 = null;
		ImageUtil.releaseDrawable(zhadanImageView.getBackground());
		ImageUtil.releaseDrawable(wangzhaImageView.getBackground());
		ImageUtil.releaseDrawable(shunzImageView.getBackground());
		ImageUtil.releaseDrawable(feijiImageView.getBackground());
		zhadanImageView = null;
		wangzhaImageView = null;
		shunzImageView = null;
		feijiImageView = null;
		publicLayout.removeAllViews();
		publicLayout = null;
		doudizhuBackGround.setOnTouchListener(null);
		doudizhuBackGround.removeAllViews();
		doudizhuBackGround.removeAllViewsInLayout();
		doudizhuBackGround = null;
		doudizhuLayout.setOnClickListener(null);
		doudizhuLayout.removeAllViews();
		doudizhuLayout.removeAllViewsInLayout();
		doudizhuLayout = null;
		gpRl.removeAllViews();
		gpRl = null;
		if (userinfoshowView != null) {
			userinfoshowView.removeAllViews();
			userinfoshowView = null;
		}
		// ImageUtil.clearImageWeakMap();
		ActivityPool.remove(this);
		if (null != mReciver) {
			this.unregisterReceiver(mReciver);
			mReciver = null;
		}
		if (null != mMyBatteryReceiver) {
			this.unregisterReceiver(mMyBatteryReceiver);
			mMyBatteryReceiver = null;
		}
		NotificationService.joinRoomCode = "";
		masterOrder = 0;
		super.onDestroy();
	}

	/**
	 * 设置提示
	 */
	public void setTishi() {
		for (int i = 0; i < nowcard.size(); i++) {
			poker[nowcard.get(i).getNumber()].params.topMargin = mst.adjustYIgnoreDensity(20);
			poker[nowcard.get(i).getNumber()].setLayoutParams(poker[nowcard.get(i).getNumber()].params);
			poker[nowcard.get(i).getNumber()].ischeck = false;
		}
		boolean arrowUp = false;// 向上引导布局
		if (bierenchupai == null) {
			DouDiZhuData data = new DouDiZhuData(nowcard);
			DouDiZhuData datas = new DouDiZhuData(nowcard);
			data.fillPokerList();
			List<List<Poker>> tishiList = data.getTiShi();
			// List<List<Poker>> tishiList = data.getTiShi(otherplay1);
			datas.fillAllPokerList();
			List<List<Poker>> tishiList2 = datas.getTiShi();
			// List<List<Poker>> tishiList = new ArrayList<List<Poker>>();
			HintPokerUtil aList = new HintPokerUtil();
			tishiList = aList.filterHintPoker(tishiList, tishiList2);
			if (tishiList == null || tishiList.size() == 0) {
				poker[nowcard.get(nowcard.size() - 1).getNumber()].params.topMargin = 0;
				poker[nowcard.get(nowcard.size() - 1).getNumber()].setLayoutParams(poker[nowcard.get(nowcard.size() - 1).getNumber()].params);
				poker[nowcard.get(nowcard.size() - 1).getNumber()].ischeck = true;
			}
			setTiShiCount();
			if (getTiShiCount() > tishiList.size() - 1) {
				initTiShiCount();
				setTiShiCount();
			}
			List<Poker> tiShiPoker = tishiList.get(getTiShiCount());
			for (int i = 0; i < tiShiPoker.size(); i++) {
				poker[tiShiPoker.get(i).getNumber()].params.topMargin = 0;
				poker[tiShiPoker.get(i).getNumber()].setLayoutParams(poker[tiShiPoker.get(i).getNumber()].params);
				poker[tiShiPoker.get(i).getNumber()].ischeck = true;
				arrowUp = true;
			}
		} else {
			checkOtherChupai(bierenchupai);
			// int tishi[] = DoudizhuRule.GettiShi(otherplay1, nowcard);
			DouDiZhuData data = new DouDiZhuData(nowcard);
			DouDiZhuData datas = new DouDiZhuData(nowcard);
			data.fillPokerList();
			List<List<Poker>> tishiList = data.getTiShi(otherplay1);
			// List<List<Poker>> tishiList = data.getTiShi(otherplay1);
			checkOtherChupai(bierenchupai);
			datas.fillAllPokerList();
			List<List<Poker>> tishiList2 = datas.getTiShi(otherplay1);
			// List<List<Poker>> tishiList = new ArrayList<List<Poker>>();
			HintPokerUtil aList = new HintPokerUtil();
			if (tishiList != null && tishiList2 != null) {
				tishiList = aList.filterHintPoker(tishiList, tishiList2);
			}
			if (tishiList == null) {
				passCard();
				setTishiGone();
				initTiShiCount();
				return;
			}
			if (tishiList != null && tishiList.size() == 0) {
				passCard();
				setTishiGone();
				initTiShiCount();
				return;
			}
			setTiShiCount();
			if (getTiShiCount() > tishiList.size() - 1) {
				initTiShiCount();
				setTiShiCount();
			}
			List<Poker> tiShiPoker = tishiList.get(getTiShiCount());
			for (int i = 0; i < tiShiPoker.size(); i++) {
				poker[tiShiPoker.get(i).getNumber()].params.topMargin = 0;
				poker[tiShiPoker.get(i).getNumber()].setLayoutParams(poker[tiShiPoker.get(i).getNumber()].params);
				poker[tiShiPoker.get(i).getNumber()].ischeck = true;
				arrowUp = true;
			}
		}
		// 有牌弹出且没托管的情况下就弹出
		if (arrowUp && tuoGuanLayout.getVisibility() != View.VISIBLE) {
			mainGameGuideVI.setArrowUpVisible();
			mainGameGuideVI.setDoublePointVisible();
		}
	}

	/**
	 * 设置是否5秒倒计时
	 */
	public void isWaitFiveSecond() {
		if (bierenchupai != null) {
			checkOtherChupai(bierenchupai);
			DouDiZhuData data = new DouDiZhuData(nowcard);
			DouDiZhuData datas = new DouDiZhuData(nowcard);
			data.fillPokerList();
			List<List<Poker>> tishiList = data.getTiShi(otherplay1);
			// List<List<Poker>> tishiList = data.getTiShi(otherplay1);
			checkOtherChupai(bierenchupai);
			datas.fillAllPokerList();
			List<List<Poker>> tishiList2 = datas.getTiShi(otherplay1);
			// List<List<Poker>> tishiList = new ArrayList<List<Poker>>();
			HintPokerUtil aList = new HintPokerUtil();
			if (tishiList != null && tishiList2 != null) {
				tishiList = aList.filterHintPoker(tishiList, tishiList2);
			}
			if (tishiList == null) {
				isWait5Second = true;
			}
			if (tishiList != null && tishiList.size() == 0) {
				isWait5Second = true;
			}
		}
		startPlayTimer(R.id.play1Time);
		if (isWait5Second) {
			mainGameGuideVI.setArrowDownVisible();
			View toastRoot = getLayoutInflater().inflate(R.layout.my_toast, null);
			Toast toast = new Toast(getApplicationContext());
			toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.setMargin(0f, 0.1f);
			toast.setView(toastRoot);
			TextView tv = (TextView) toastRoot.findViewById(R.id.TextViewInfo);
			tv.setText("");
			// toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			TextView now = (TextView) findViewById(R.id.play1Time);
			now.setText("5");
		}
	}

	/**
	 * 把自己的牌显示出来
	 * @param now  手牌
	 */
	public void addCard(final int[] now) {
		myCardsTouchLayout.removeAllViews();
		paixu = DoudizhuRule.sort(now, poker);
		for (int i = 0; i < paixu.length; i++) {
			Poker card = poker[paixu[i]];
			card.getPokeImage().setImageDrawable(ImageUtil.getResDrawable(poker[paixu[i]].getBitpamResID(), true));
			card.setId(i + 100);
			card_jiange = (now != null && now.length > 1) ? ((int) (800 - 90) / (now.length - 1)) : card_jiange;
			if (card_jiange > 50) {
				card_jiange = 50;
			}
			myCardsTouchLayout.setDistance(mst.adjustXIgnoreDensity(card_jiange));
			card.params.leftMargin = mst.adjustXIgnoreDensity((card_jiange) * i);
			card.params.topMargin = mst.adjustYIgnoreDensity(20);
			card.getInnerLayout().setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.poker_div, true));
			card.getInnerLayout().setVisibility(View.GONE);
			card.setClickable(false);
			myCardsTouchLayout.addView(card, card.params);
			nowcard.add(card);
			/** 刷新记牌器数据 **/
			refreshCardCountData();
		}
	}

	/**
	 * 初始化界面元素
	 */
	private void initView() {
		Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/LCD.ttf");
		Typeface num = Typeface.createFromAsset(getAssets(), "fonts/NUM.ttf");
		poker = PokerUtil.getPoker(this);
		nowcard = new ArrayList<Poker>();
		chupaicard = new ArrayList<Poker>();
		otherplay1 = new ArrayList<Poker>();
		cashList = new ArrayList<BitmapVO>();
		girlItems = (ImageView) findViewById(R.id.game_gilr_items);// 美女图鉴
		girlItems.setOnClickListener(clickListener);
		viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);// 美女背景滑动
		imageNewIv = (ImageView) findViewById(R.id.image_new_iv);
		imageNewIv.setVisibility(View.GONE);
		publicLayout = (LinearLayout) findViewById(R.id.dzm_public_mess_layout);
		publicLayout.getBackground().setAlpha(85);
		messbtnView = (ImageView) findViewById(R.id.mess_btn_view);
		chupai = (Button) findViewById(R.id.chupai_button);
		buchu = (Button) findViewById(R.id.pass_button);
		jiao = 0;
		tishi = (Button) findViewById(R.id.tishi_button);
		countdownTv = (TextView) findViewById(R.id.countdown);
		countdownTv.setTypeface(typeface);
		netSlowTip = (TextView) findViewById(R.id.netslow_tip);
		play1SurplusCount = (TextView) findViewById(R.id.play1_surplus_count);
		play2SurplusCount = (TextView) findViewById(R.id.play2_surplus_count);
		play3SurplusCount = (TextView) findViewById(R.id.play3_surplus_count);
		play1SurplusCount.setTypeface(num);
		play2SurplusCount.setTypeface(num);
		play3SurplusCount.setTypeface(num);
		play1Timer = (TextView) findViewById(R.id.play1Time);
		play2Timer = (TextView) findViewById(R.id.play2Time);
		play3Timer = (TextView) findViewById(R.id.play3Time);
		zhidou = (TextView) findViewById(R.id.zhidou_TextView);
		myCardsTouchLayout = (TouchRelativeLayout) findViewById(R.id.play_cards);
		myCardsTouchLayout.setListenner(this);
		myCardsTouchLayout.setDistance(mst.adjustXIgnoreDensity(card_jiange));
		play1PassLayout = (RelativeLayout) findViewById(R.id.play1_pass_card);
		play2PassLayout = (RelativeLayout) findViewById(R.id.play2pass_card);
		play3PassLayout = (RelativeLayout) findViewById(R.id.play3_pass_card);
		mSystemInfoRl = (RelativeLayout) findViewById(R.id.system_info_rl);
		systemTime = (TextView) findViewById(R.id.system_time_tv);
		systemTime.setTypeface(typeface);
		systemTime.setText(ActivityUtils.getTimeShort());
		systemWifi = (ImageView) findViewById(R.id.system_wifi_iv);
		int wifi = ActivityUtils.getWifiLevel();// 获取系统 WIFI信号
		systemWifi.setImageLevel(wifi);
		systemPower = (ImageView) findViewById(R.id.system_power_iv);
		playBtnLayout = (LinearLayout) findViewById(R.id.play_choice);
		doudizhuLayout = (RelativeLayout) findViewById(R.id.doudizhu_layout);
		doudizhuBackGround = (RelativeLayout) findViewById(R.id.doudizhugame_relative);
		// doudizhuBackGround.setOnTouchListener(this);
		play1Icon = (ImageView) findViewById(R.id.playimageView1);
		play1Icon.setOnClickListener(clickListener);
		zhezhao1 = (ImageView) findViewById(R.id.zhezhao1);
		zhezhao1.setOnClickListener(clickListener);
		zhezhao1.setVisibility(View.GONE);
		play2Icon = (ImageView) findViewById(R.id.playimageView2);
		play2Icon.setOnClickListener(clickListener);
		zhezhao2 = (ImageView) findViewById(R.id.zhezhao2);
		zhezhao2.setOnClickListener(clickListener);
		zhezhao2.setVisibility(View.GONE);
		play3Icon = (ImageView) findViewById(R.id.playimageView3);
		play3Icon.setOnClickListener(clickListener);
		zhezhao3 = (ImageView) findViewById(R.id.zhezhao3);
		zhezhao3.setOnClickListener(clickListener);
		zhezhao3.setVisibility(View.GONE);
		dpRl1 = (RelativeLayout) findViewById(R.id.dunpairl1);
		dpRl2 = (RelativeLayout) findViewById(R.id.dunpairl2);
		dpRl3 = (RelativeLayout) findViewById(R.id.dunpairl3);
		//baoXiangLayout = (RelativeLayout) findViewById(R.id.bao_xiang_layout);
		//baoxiang = (RelativeLayout) findViewById(R.id.baoxiang);
		Map<String, String> roomRuleMap = JsonHelper.fromJson(Database.JOIN_ROOM.getRule(), new TypeToken<Map<String, String>>() {});
		//baoText = (TextView) findViewById(R.id.bao_xiang_text);
		mySelfHeadRl = (RelativeLayout) findViewById(R.id.game_self_ll);
		nullTv = (TextView) findViewById(R.id.null_tv);
		nullTv.setVisibility(View.GONE);
		nullTv2 = (TextView) findViewById(R.id.null_tv2);
		nullTv2.setVisibility(View.VISIBLE);
		playTextView1 = (TextView) findViewById(R.id.playTextView1);
		playTextView2 = (TextView) findViewById(R.id.playTextView2);
		playTextView3 = (TextView) findViewById(R.id.playTextView3);
		wolTv1 = (TextView) findViewById(R.id.play1_winners_or_losers_tv);
		wolTv2 = (TextView) findViewById(R.id.play2_winners_or_losers_tv);
		wolTv3 = (TextView) findViewById(R.id.play3_winners_or_losers_tv);
		wolTv1.setVisibility(View.GONE);
		wolTv2.setVisibility(View.GONE);
		wolTv3.setVisibility(View.GONE);
		wolTv1.setTypeface(num);
		wolTv2.setTypeface(num);
		wolTv3.setTypeface(num);
		iqTv1 = (TextView) findViewById(R.id.iq1_tv);
		iqTv2 = (TextView) findViewById(R.id.iq2_tv);
		iqTv3 = (TextView) findViewById(R.id.iq3_tv);
		play1Order = (TextView) findViewById(R.id.play1Order);
		play2Order = (TextView) findViewById(R.id.play2Order);
		play3Order = (TextView) findViewById(R.id.play3Order);
		marqueeText = (MarqueeText) findViewById(R.id.public_textview);
		adWidgetLayoutParam = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		zhadanImageView = (ImageView) findViewById(R.id.play_anim_layout_zhadan);
		shunzImageView = (ImageView) findViewById(R.id.play_anim_layout_shunzi);
		feijiImageView = (ImageView) findViewById(R.id.play_anim_layout_feiji);
		wangzhaImageView = (ImageView) findViewById(R.id.play_anim_layout_wangzha);
		userinfoshowView = (RelativeLayout) findViewById(R.id.playinfoview);
		userInfoText = (TextView) findViewById(R.id.userinfotext);
		// 游戏界面返回键，暂时去掉
		gameRobot = (ImageButton) findViewById(R.id.game_robot);
		gameRobot.setOnClickListener(clickListener);
		gameSet = (ImageButton) findViewById(R.id.game_set);
		gameSet.setOnClickListener(clickListener);
		tuoGuanLayout = (RelativeLayout) findViewById(R.id.tuo_guan_layout);
		tuoGuanLayout.setOnClickListener(clickListener);
		tuoGuan = (ImageButton) findViewById(R.id.tuo_guan_btn);
		tuoGuan.setOnClickListener(clickListener);
		myFrame = (LinearLayout) findViewById(R.id.my_frame);
		rightFrame = (LinearLayout) findViewById(R.id.right_frame);
		leftFrame = (LinearLayout) findViewById(R.id.left_frame);
		girlLeftFrame = (LinearLayout) findViewById(R.id.girl_left_frame);
		girlLeftFrame.setOnClickListener(clickListener);
		girlLeftTv = (TextView) findViewById(R.id.gir_left_tv);
		girlRightFrame = (LinearLayout) findViewById(R.id.girl_right_frame);
		girlRightFrame.setOnClickListener(clickListener);
		networkSlowtip = (TextView) findViewById(R.id.network_slow_tip);
		networkSlowtip.getBackground().setAlpha(85);
		girlRightTv = (TextView) findViewById(R.id.gir_right_tv);
		mainGameGuideVI = (MainGameGuideView) findViewById(R.id.main_game_guide_view);
		mainGameGuideVI.setVisibility(View.GONE);
		settingDialog = new SettingDialog(ctx) {

			public void setDismiss() {
				super.setDismiss();
				settingDialog.dismiss();
			}
		};
		// 设置游戏的背景
		doudizhuBackGround.setBackgroundDrawable(ImageUtil.getResDrawable(Database.GAME_BG_DRAWABLEID, false));
		// 设置选定的背景图
		String gameBackGround = GameCache.getStr(Constant.GAME_BACKGROUND);
		if (!TextUtils.isEmpty(gameBackGround)) {
			initViewFlipper(gameBackGround);
		}
		gpRl = (LinearLayout) findViewById(R.id.doudizhu_gp_rl);
		gpCount = (TextView) findViewById(R.id.doudizhu_kuai_img);
		gpType = (TextView) findViewById(R.id.doudizhu_game_type_tv);
		gpRound = (TextView) findViewById(R.id.doudizhu_game_round_tv);
		gpScore = (TextView) findViewById(R.id.doudizhu_game_sorce_tv);
		gpRank = (TextView) findViewById(R.id.doudizhu_game_rank_tv);
		// slipIv = (ImageView) findViewById(R.id.doudizhu_slip_img);
		zhadanIv = (ImageView) findViewById(R.id.zhadan_iv);
		setViewInitData();
		rankTop = (Button) findViewById(R.id.gp_top_btn);
		rankTop.setOnClickListener(clickListener);
		rankTop.setVisibility(View.GONE);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constant.SYSTEM_TIME_CHANGE_ACTION);
		mReciver = new MyBroadcastReciver();
		this.registerReceiver(mReciver, intentFilter);
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		mFilter.addAction(Intent.ACTION_BATTERY_LOW);
		mFilter.addAction(Intent.ACTION_BATTERY_OKAY);
		mFilter.addAction(Intent.ACTION_POWER_CONNECTED);
		mMyBatteryReceiver = new MyBatteryReceiver();// 注册电量，WIFI信号广播接收器
		this.registerReceiver(mMyBatteryReceiver, mFilter);
		bitmapList = new ArrayList<Bitmap>();
		Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.card_kingf);
		for (int i = 0; i <= 10; i++) {
			bitmapList.add(bitmap);
		}
		/** 记牌器 **/
		//leftJiPaiQiLayout = findViewById(R.id.jipaiqi_layout_left);
		//rightJiPaiQiLayout = findViewById(R.id.jipaiqi_layout_right);
		leftJiPaiQiTurnPlateView = (JiPaiQiTurnPlateView) findViewById(R.id.jipaiqi_left);
		leftJiPaiQiTurnPlateView.setLocation(Location.Top_Left);
		rightJiPaiQiTurnPlateView = (JiPaiQiTurnPlateView) findViewById(R.id.jipaiqi_right);
		leftJiPaiQiTurnPlateView.setLocation(Location.Top_Right);
		recordPorkerView = (RecordPorkerView) findViewById(R.id.jipaiqi_record_view);
		topRightShieldView = findViewById(R.id.dun_layout_right);
		cardStatView = findViewById(R.id.layout_jipaiqi);
		topLeftUserView = findViewById(R.id.top_left_head);
		topRightUserView = findViewById(R.id.top_right_head);
		topLeftShieldView = findViewById(R.id.dun_layout_left);
		topRightShieldView = findViewById(R.id.dun_layout_right);
		initCardCountLayout();
		/**金豆不足，充值 */
		beansInsufficientRl = (RelativeLayout) findViewById(R.id.dzed_beans_insufficient_rl);
		beansInsufficientTv = (TextView) findViewById(R.id.dzed_beans_insufficient_tv);
		beansInsufficientRl.setVisibility(View.GONE);
		rechargeLl = (LinearLayout) findViewById(R.id.dzed_recharge_ll);
		//rechargeBtn = (Button) findViewById(R.id.dzed_recharge_beans_btn);
		//rechargeBtn.setOnClickListener(clickListener);
		borrowBeansBtn = (Button) findViewById(R.id.dzed_borrow_beans_btn);
		borrowBeansBtn.setOnClickListener(clickListener);
		rechargeLl.setVisibility(View.GONE);
		mSystemInfoRl.setVisibility(View.VISIBLE);
	}

	public void initCardCountLayout() {
		text_kingb = (TextView) findViewById(R.id.pork_kingb);
		text_kings = (TextView) findViewById(R.id.pork_kings);
		text_2 = (TextView) findViewById(R.id.pork_2);
		text_A = (TextView) findViewById(R.id.pork_A);
		text_K = (TextView) findViewById(R.id.pork_K);
		text_Q = (TextView) findViewById(R.id.pork_Q);
		text_J = (TextView) findViewById(R.id.pork_J);
		text_10 = (TextView) findViewById(R.id.pork_10);
		text_9 = (TextView) findViewById(R.id.pork_9);
		text_8 = (TextView) findViewById(R.id.pork_8);
		text_7 = (TextView) findViewById(R.id.pork_7);
		text_6 = (TextView) findViewById(R.id.pork_6);
		text_5 = (TextView) findViewById(R.id.pork_5);
		text_4 = (TextView) findViewById(R.id.pork_4);
		text_3 = (TextView) findViewById(R.id.pork_3);
	}

	/**
	 * 给View设置初始值
	 */
	private void setViewInitData() {
		play1Icon.setImageDrawable(ImageUtil.getResDrawable(R.drawable.nongmin, true));
		play2Icon.setImageDrawable(ImageUtil.getResDrawable(R.drawable.nongmin, true));
		play3Icon.setImageDrawable(ImageUtil.getResDrawable(R.drawable.nongmin, true));
		play1SurplusCount.setText(Integer.toString(initCardNum));
		play2SurplusCount.setText(Integer.toString(initCardNum));
		play3SurplusCount.setText(Integer.toString(initCardNum));
		bierenchupai = null;
		baoFlag = false;
	}

	/**
	 * 
	 * 收到信息更新游戏界面
	 */
	private void initHandler() {
		handler = new Handler() {

			@SuppressWarnings("unchecked")
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case 0: // 发牌更新界面
						MobclickAgent.onEventBegin(ctx,"在线斗地主");
						ClientCmdMgr.setClientStatus(Client.PLAYING);
						cancelTimer(); // 取消定时
						gameWaitLayout.closeTimer();
						doudizhuLayout.removeView(gameWaitLayout);
						visibleOrGoneRankBtn();
						Play fapai = (Play) msg.getData().get("fapai");
						setOrder(fapai.getOrder());
						nowcard.clear();// 清除自己手中的牌
						addCard(pai);
						mySelfOrder = fapai.getOrder();
						GameUser mGameUser = Database.userMap.get(mySelfOrder);
						// // 自己
						playTextView1.setText(TextUtils.isEmpty(mGameUser.getNickname()) ? "" : mGameUser.getNickname()); // 自己
						if (!TextUtils.isEmpty(mGameUser.getNickname())) {// 保存自己的名字供单机使用
							GameCache.putStr(Constant.GAME_NAME_CACHE, mGameUser.getNickname());
						}
//						ActivityUtils.setDefaultHead(ctx, play1Icon, mGameUser.getGender(), false);
						play1Order.setText(String.valueOf(mySelfOrder));
						iqTv1.setText("" + mGameUser.getIq());
						Map<String, String> dpMap = mGameUser.getLevelImg();
						try {
							for (Entry<String, String> entry : dpMap.entrySet()) {
								final int count = TextUtils.isEmpty(entry.getKey()) ? 0 : Integer.parseInt(entry.getKey());
								String path = entry.getValue();// 图片链接
								if (!TextUtils.isEmpty(path)) {
									dpRl1.removeAllViews();
									ImageUtil.setImg(HttpURL.URL_PIC_ALL + path, null, new ImageCallback() {

										public void imageLoaded(Bitmap bitmap, ImageView view) {
											if (null != bitmap) {
												for (int i = 0; i < count; i++) {
													ImageView img = new ImageView(ctx);
													img.setBackgroundDrawable(new BitmapDrawable(bitmap));
													RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mst.adjustXIgnoreDensity(DP_WIDTH), mst.adjustYIgnoreDensity(DP_HEIGHT));
													if (i < 3) {
														params.leftMargin = mst.adjustXIgnoreDensity(DP_WIDTH * i);
													} else {
														params.topMargin = mst.adjustYIgnoreDensity(DP_HEIGHT / 2 + DP_PANDING);
														params.leftMargin = mst.adjustXIgnoreDensity(DP_WIDTH / 2 + DP_WIDTH * (i - 3));
													}
													img.setLayoutParams(params);
													dpRl1.addView(img);
												}
											}
										}
									});
								}
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						int p2o = getNextOrder(mySelfOrder);
						GameUser mGameUser2 = Database.userMap.get(p2o);
						playTextView2.setText(mGameUser2.getNickname()); // 下家
//						ActivityUtils.setDefaultHead(ctx, play2Icon, mGameUser2.getGender(), false);
						play2Order.setText(String.valueOf(p2o));
						iqTv2.setText("" + mGameUser2.getIq());
						Map<String, String> dpMap2 = mGameUser2.getLevelImg();
						try {
							for (Entry<String, String> entry : dpMap2.entrySet()) {
								final int count = TextUtils.isEmpty(entry.getKey()) ? 0 : Integer.parseInt(entry.getKey());
								String path = entry.getValue();// 图片链接
								if (!TextUtils.isEmpty(path)) {
									dpRl2.removeAllViews();
									ImageUtil.setImg(HttpURL.URL_PIC_ALL + path, null, new ImageCallback() {

										public void imageLoaded(Bitmap bitmap, ImageView view) {
											if (null != bitmap) {
												for (int i = 0; i < count; i++) {
													ImageView img = new ImageView(ctx);
													img.setBackgroundDrawable(new BitmapDrawable(bitmap));
													RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mst.adjustXIgnoreDensity(DP_WIDTH), mst.adjustYIgnoreDensity(DP_HEIGHT));
													if (i < 3) {
														params.leftMargin = mst.adjustXIgnoreDensity(DP_WIDTH * i);
													} else {
														params.topMargin = mst.adjustYIgnoreDensity(DP_HEIGHT / 2 + DP_PANDING);
														params.leftMargin = mst.adjustXIgnoreDensity(DP_WIDTH / 2 + DP_WIDTH * (i - 3));
													}
													img.setLayoutParams(params);
													dpRl2.addView(img);
												}
											}
										}
									});
								}
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						int p3o = getPerOrder(mySelfOrder);
						GameUser mGameUser3 = Database.userMap.get(p3o);
						playTextView3.setText(mGameUser3.getNickname()); // 下家
//						ActivityUtils.setDefaultHead(ctx, play3Icon, mGameUser3.getGender(), false);
						play3Order.setText(String.valueOf(p3o));
						iqTv3.setText("" + mGameUser3.getIq());
						Map<String, String> dpMap3 = mGameUser3.getLevelImg();
						try {
							for (Entry<String, String> entry : dpMap3.entrySet()) {
								final int count = TextUtils.isEmpty(entry.getKey()) ? 0 : Integer.parseInt(entry.getKey());
								String path = entry.getValue();// 图片链接
								if (!TextUtils.isEmpty(path)) {
									dpRl3.removeAllViews();
									ImageUtil.setImg(HttpURL.URL_PIC_ALL + path, null, new ImageCallback() {

										public void imageLoaded(Bitmap bitmap, ImageView view) {
											if (null != bitmap) {
												for (int i = 0; i < count; i++) {
													ImageView img = new ImageView(ctx);
													img.setImageBitmap(bitmap);
													RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mst.adjustXIgnoreDensity(DP_WIDTH), mst.adjustYIgnoreDensity(DP_HEIGHT));
													if (i < 3) {
														params.leftMargin = mst.adjustXIgnoreDensity(DP_WIDTH * i);
													} else {
														params.topMargin = mst.adjustYIgnoreDensity(DP_HEIGHT / 2 + DP_PANDING);
														params.leftMargin = mst.adjustXIgnoreDensity(DP_WIDTH / 2 + DP_WIDTH * (i - 3));
													}
													img.setLayoutParams(params);
													dpRl3.addView(img);
												}
											}
										}
									});
								}
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						cleanAllChuPaiInfo();
						// 托管时的牌可按
						for (int i = 0; i < myCardsTouchLayout.getChildCount(); i++) {
							myCardsTouchLayout.getChildAt(i).setClickable(false);
						}
						cancelTuoGuanState();
						if (null != warn) {
							warn.clear();
						}
						warn.put(1101, false);
						warn.put(1102, false);
						warn.put(1103, false);
						if (0 == PreferenceHelper.getMyPreference().getSetting().getInt("newImage", 0)) {
							girlItems.setVisibility(View.INVISIBLE);
							showPopWindow(true);
						}
						
						HashMap<String, String> purchase = new HashMap<String, String>();
						purchase.put("room",Database.JOIN_ROOM.getName());
						purchase.put("account",mGameUser.getAccount());
						purchase.put("userctime",mGameUser.getCreateDate());
						MobclickAgent.onEvent(ctx,"斗地主游戏中",purchase);
						break;
					case 3: // 收到打牌消息
						hiddenPlayBtn();
						Play play = (Play) msg.getData().get("play");
						playCard(play, false);
						setShengxiaPai(play.getCount(), getPerOrder(play.getNextOrder()));
						refreshCardCountData();
						/** 更新记牌器头像 **/
						//refreshJiPaiQiAvatar();
						break;
					case 4: // 收到打完这盘的牌消息
						MobclickAgent.onEventEnd(ctx,"在线斗地主");
						hiddenPlayBtn();
						LinkedList<Play> playResult = (LinkedList<Play>) msg.getData().get("playResult");
						setEndDonghua(playResult);
						break;
					case 6:// 收到打牌定时器时间更新消息
						TextView now = (TextView) findViewById(msg.arg1);
						if (null != now) {
							int playtimeleast = Integer.parseInt(now.getText().toString()) - 1;
							if (playtimeleast != -1) {// 时间没有到
								now.setText(String.valueOf(playtimeleast));
								// 如果打得起，并且倒计时小于5秒还没出牌，就震动提示
								if (View.VISIBLE == playBtnLayout.getVisibility() && !isWait5Second && playtimeleast == 5) {
									// 实例化震动
									Vibrate vibrate = new Vibrate(ctx);
									if (PreferenceHelper.getMyPreference().getSetting().getBoolean("zhendong", true)) {
										vibrate.playVibrate1(-1);
									}
									setTweenAnim(now, R.anim.shake, IS_NONE);
								}
							} else {
								cancelTimer();
								now.setText(String.valueOf(Constant.WAIT_TIME));
								if (msg.arg1 == R.id.play1Time) {// 如果是自己出牌的话
									if (isWait5Second) {
										passCard();// 没有托管，也没打的起的牌
										isWait5Second = false;
									} else {
										gameRobotClick();
									}
									setTishiGone();
								}
							}
						}
						break;
					case 7:// 收到打牌定时器消息
						DialogUtils.mesTip(getString(R.string.game_playing), true);
						break;
					case 8:// 收到广告消息
						if (adTask != null) {
							adTask.stop(true);
							adTask = null;
						}
						adTask = new AutoTask() {
							public void run() {
								try {
									runOnUiThread(new Runnable() {
										public void run() {
											adWidgetLayoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
											adWidget = new ADWideget(ctx, advList);
											doudizhuBackGround.addView(adWidget, adWidgetLayoutParam);
										}
									});
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						};
						ScheduledTask.addDelayTask(adTask, Constant.AD_PLAY_TIME);
						break;
					case 11:// 接收当前玩家的踢拉选择，提示下家是“踢”或"拉"
						getTiLaMsg(msg);
						/** 更新记牌器头像 **/
						//refreshJiPaiQiAvatar();
						break;
					case 12:// 刷新倒计时
						countDownTime -= 60;
						if (countDownTime <= 0) {
							countDownTime = 0;
						}
						if (0 < countDownTime) {
							countdownTv.setVisibility(View.VISIBLE);
							countdownTv.setText(ActivityUtils.getCountDown(countDownTime));
						} else {
							countdownTv.setVisibility(View.GONE);
						}
						break;
					case 13:// 收到踢拉定时器消息
						int timeleast1 = 0;
						// （-1 上家 0 自己 1 下载）
						int callOrder1 = msg.arg1;
						if (callOrder1 == 0) { // 自己叫地主
							timeleast1 = Integer.parseInt(play1Timer.getText().toString()) - 1;
							if (timeleast1 == 0) {
								//callBuJiaBei();
							} else {
								play1Timer.setText(String.valueOf(timeleast1));
							}
						} else if (callOrder1 == 1) {
							timeleast1 = Integer.parseInt(play2Timer.getText().toString()) - 1;
							if (timeleast1 != 0) {
								play2Timer.setText(String.valueOf(timeleast1));
							}
						} else if (callOrder1 == -1) {
							timeleast1 = Integer.parseInt(play3Timer.getText().toString()) - 1;
							if (timeleast1 != 0) {
								play3Timer.setText(String.valueOf(timeleast1));
							}
						}
						if (timeleast1 == 6) { // 播放警告声音
							AudioPlayUtils.getInstance().playSound(R.raw.warn);
						}
						break;
					case 17: // 退出游戏返回房间
						DialogUtils.quitGameTip();
						break;
					case 18:// 收到聊天定时器消息
						CmdDetail mess = (CmdDetail) msg.getData().get(CmdUtils.CMD_CHAT);
						showMessage(mess);
						break;
					case 19:// 收到系统公告
						String pubMess = (String) msg.getData().get("publicmess");
						showPubMess(pubMess);
						break;
					case 20:// 退出游戏
						DialogUtils.exitGame(DoudizhuMainGameActivity.this);
						break;
					case 22:// 淘汰或比赛结束
						if (!TextUtils.isEmpty(gameOverDetail)) {
							// String details = msg.getData().getString("detail");
							Log.i("joinBottomll", "detail:" + gameOverDetail);
							Map<String, String> map = JsonHelper.fromJson(gameOverDetail, new TypeToken<Map<String, String>>() {});
							final String rank = map.get("rank");
							String prize = map.get("prize");
							List<PrizeGoods> prizeGoods = null;
							if ("-1".equals(prize)) {// -1表示为没人任何奖品
								prizeGoods = new ArrayList<PrizeGoods>();
							} else {
								prizeGoods = JsonHelper.fromJson(prize, new TypeToken<List<PrizeGoods>>() {});
							}
							final List<PrizeGoods> prizeGoods2 = prizeGoods;
							GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
							cacheUser.setRound(0);// 还原比赛轮数位0
							GameCache.putObj(CacheKey.GAME_USER, cacheUser);
							gameWaitLayout.setjoinBottomllVisible();
							showGameOverDialog(rank, prizeGoods2);
						}
						break;
					case 24:// 重新设置数据（普通赛制/快速赛）
						handler.sendEmptyMessage(22);
						startAgain(false);
						hasEnd = false;
						if (hasCallReady) {// 若后台有请求过准备状态，则此时立刻回复
							CmdUtils.ready();
							hasCallReady = false;
						}
						break;
					case 25:// 返回大厅
						// 离开房间
						ClientCmdMgr.closeClient();
						finishSelf();
						break;
					case 26:// 再来一局
						startAgain(true);
						break;
					case 200:// 重连
						String relink = (String) msg.getData().get("relink");
						doReLink(relink);
						//reSetJiPaiQiDataForRelink(relink);
						//refreshJiPaiQiAvatar();
						break;
					case 300:// 添加广告
						try {
							adWidgetLayoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
							adWidget = new ADWideget(ctx, advList);
							doudizhuBackGround.addView(adWidget, adWidgetLayoutParam);
						} catch (Exception e) {}
						break;
					case 301:// 隐藏自己的头像
						if (!selfIsMove) {
							selfIsMove = true;
							AnimUtils.startAnimationsOut1(mySelfHeadRl, 300, 150, nullTv, nullTv2);
							nullTv2.setVisibility(View.GONE);
							nullTv.setVisibility(View.VISIBLE);
						}
						break;
					case 303:// 隐藏美女图鉴
						popoDismiss();
						break;
					case 304:// 隐藏预充值提示
						if (null != beansInsufficientRl && beansInsufficientRl.getVisibility() == View.VISIBLE) {
							beansInsufficientRl.setVisibility(View.GONE);
						}
						break;
					case 400:// 隐藏网络缓存提示信息
						networkSlowtip.setVisibility(View.GONE);
						break;
					case 401:// 展示网络缓存提示信息
						networkSlowtip.setText("对方网络缓慢 ,请稍候 ...");
						networkSlowtip.setVisibility(View.VISIBLE);
						break;
					case 402:// 设置游戏的背景
						doudizhuBackGround.setBackgroundDrawable(ImageUtil.getResDrawable(Database.GAME_BG_DRAWABLEID, false));
						break;
					case 403:// 其他玩家断线通知
						int breakOrder = msg.getData().getInt("breakOrder");
						if (breakOrder == getNextOrder(mySelfOrder)) { // 我的下家断线
							play2Icon.setImageDrawable(ImageUtil.getResDrawable(R.drawable.robot, true));
							play2IsTuoGuan = true;
						} else if (breakOrder == getPerOrder(mySelfOrder)) {// 我的上家断线
							play3Icon.setImageDrawable(ImageUtil.getResDrawable(R.drawable.robot, true));
							play3IsTuoGuan = true;
						}
						break;
					case 404:// 显示比赛场积分\排名等信息
						gpRl.setVisibility(View.VISIBLE);
						gpCount.setVisibility(View.VISIBLE);
						/**
						 * 复合赛制显示：积分/排名 普通赛制显示：积分/排名/类型/第几轮
						 **/
						GameUser gu = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
						if (0 != gu.getRound()) {// 第几轮不为空，则是快速赛
							gpRound.setVisibility(View.VISIBLE);
							gpType.setVisibility(View.VISIBLE);
							Log.i("detail", "第" + gu.getRound() + "轮");
							gpRound.setText("第" + gu.getRound() + "轮");
							gpType.setText(ImageUtil.getGameType(gu.getLevel()));
							gpCount.setText(Database.JOIN_ROOM.getName()); // 赛场标题
						} else {
							gpRound.setVisibility(View.GONE);
							gpType.setVisibility(View.GONE);
							Log.i("detail", "复合赛roomName:" + gu.getRoomName());
							gpCount.setText(gu.getRoomName()); // 赛场标题
						}
						gpScore.setText("积分" + PatternUtils.changeZhidou(gu.getCred()));
						gpRank.setText("第" + gu.getRank() + "名");
						break;
					case 666:// 收到金豆超过上限的命令
						String tipsMess = (String) msg.getData().get("tipsmess");
						TipsDialog tips = new TipsDialog(ctx) {

							public void okClick() {
								CmdUtils.sendFastJoinRoomCmd();
							}

							public void cancelClick() {
								CmdUtils.exitGame();
								MobclickAgent.onEvent(ctx,"超过上限取消");
								// 记录逃跑日志
								GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
								if (cacheUser != null) {
									cacheUser.setRound(0);
									GameCache.putObj(CacheKey.GAME_USER, cacheUser);
								}
								ClientCmdMgr.closeClient();
								ActivityUtils.finishAcitivity();
							}
						};
						tips.show();
						tips.setText("您的金豆超过上限");
						break;
					case Constant.HANDLER_WHAT_GAME_VIEW_SEND_MESS_TEXT://发送聊天信息
						sendTextMessage(msg.getData().getString(Constant.GAME_VIEW_SEND_MESS_TEXT), msg.getData().getInt(Constant.GAME_VIEW_SEND_MESS_CLICK_TYPE));
						break;
					case Constant.HANDLER_WHAT_GAME_VIEW_SEND_MESS_GIF://GIF表情
						MobclickAgent.onEvent(ctx,"游戏中发送表情");
						final GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
						String imageName = msg.getData().getString(Constant.GAME_VIEW_SEND_MESS_GIF);
						int clickType = msg.getData().getInt(Constant.GAME_VIEW_SEND_MESS_CLICK_TYPE);
						CmdDetail chat = new CmdDetail();
						chat.setCmd(CmdUtils.CMD_CHAT);
						// chat.setGameToken(Database.USER.getLoginToken());
						CmdDetail detail = new CmdDetail();
						detail.setType(clickType);
						detail.setValue(imageName);
						detail.setFromUserId(cacheUser.getAccount());
						String dj = JsonHelper.toJson(detail);
						chat.setDetail(dj);
						myFrame.removeAllViews();
						// girlLeftFrame.removeAllViews();
						girlLeftFrame.setVisibility(View.GONE);
						startTask(myFrame, selfTask);
						messageFrame(myFrame, imageName, clickType, null);
						CmdUtils.sendMessageCmd(chat);
						break;
				}
			}

			/**
			 * 接收当前玩家的踢拉选择，提示下家是“踢”或"拉"
			 * 
			 * @param msg
			 */
			private void getTiLaMsg(Message msg) {
				TiLa tila = (TiLa) msg.getData().get("tila");
				ImageView info = new ImageView(ctx);
				Log.i("Ordersss", "加倍玩家的位置:" + tila.getOrder() + "\n下一个踢or拉玩家的位置:" + tila.getNextOrder() + "\n加倍的倍数 (1:不加倍,2:加2倍,4:加4倍):" + tila.getRatio() + "\n下一家是否可踢啦:" + tila.getNextCan());
				String gd = "0";
				if (null != Database.userMap && Database.userMap.containsKey(tila.getOrder())) {
					gd = Database.userMap.get(tila.getOrder()).getGender();
				}
				AudioPlayUtils apu2 = AudioPlayUtils.getInstance();
				// 关闭当前踢拉定时器
				if (null != tila.getOrder()) {
					if (mySelfOrder == tila.getOrder()) {// 自己
						stopTimer(0); // 暂停定时器
					} else if (JIABEI2_ID == (1600 + tila.getOrder())) {// 下家
						stopTimer(1); // 暂停定时器
					} else if (JIABEI3_ID == (1600 + tila.getOrder())) {// 上家
						stopTimer(-1); // 暂停定时器
					}
				}
				// 开启下一家踢拉定时器
				if (tila.getNextCan() && null != tila.getNextOrder()) {
					if (mySelfOrder == tila.getNextOrder()) {// 自己
						isTurnMySelf = true;
						startTiLaTimer(0);
					} else if (JIABEI2_ID == (1600 + tila.getNextOrder())) {// 下家
						isTurnMySelf = false;
						startTiLaTimer(1);
					} else if (JIABEI3_ID == (1600 + tila.getNextOrder())) {// 上家
						isTurnMySelf = false;
						startTiLaTimer(-1);
					}
				}
			}
		};
	}

	/**
	 * 显示心跳界面
	 */
	private void showWaitView() {
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		doudizhuLayout.removeView(gameWaitLayout);
		doudizhuLayout.addView(gameWaitLayout, layoutParams);
		gameWaitLayout.setjoinBottomllVisible();
		gameWaitLayout.startTimer();
		gameWaitLayout.setOnClickListener(null);
		gameWaitLayout.setjoinBottomllInVisible();
		gameWaitLayout.setRoomName(Database.JOIN_ROOM);
	}

	/**
	 * 再来一局
	 * 
	 * @param joinAgain
	 *            是否重新join
	 */
	private void startAgain(boolean joinAgain) {
		initAgainGameData();
		if (joinAgain) {
			joinGame(false);
		}
	}

	/**
	 * 重新初始化数据
	 */
	private void initAgainGameData() {
		/** 刷新用户物品信息 **/
		refreshUserGoodsInfo();
		setUserInfo();
		masterOrder = 0;
		callPoint = 0;
		jiao = 0;
		play2IsTuoGuan = false;
		play3IsTuoGuan = false;
		myCardsTouchLayout.removeAllViews();
		play1PassLayout.removeAllViews();
		play2PassLayout.removeAllViews();
		play3PassLayout.removeAllViews();
		wolTv1.setVisibility(View.GONE);
		wolTv2.setVisibility(View.GONE);
		wolTv3.setVisibility(View.GONE);
		beansInsufficientRl.setVisibility(View.GONE);
		rechargeLl.setVisibility(View.GONE);
		if (selfIsMove) {
			selfIsMove = false;
			nullTv.setVisibility(View.GONE);
			nullTv2.setVisibility(View.VISIBLE);
		}
		cancelTuoGuanState();
		dismissDialog();

		if (null != nowcard) {
			for (Poker card : nowcard) {
				if (card != null) {
					card.onDestory();
				}
				card = null;
			}
			nowcard.clear();
			nowcard = null;
		}
		if (null != chupaicard) {
			for (Poker card : chupaicard) {
				if (card != null) {
					card.onDestory();
				}
				card = null;
			}
			chupaicard.clear();
			chupaicard = null;
		}
		if (null != checkpai) {
			for (Poker card : checkpai) {
				if (card != null) {
					card.onDestory();
				}
				card = null;
			}
			checkpai.clear();
			checkpai = null;
		}
		if (null != otherplay1) {
			for (Poker card : otherplay1) {
				if (card != null) {
					card.onDestory();
				}
				card = null;
			}
			otherplay1.clear();
			otherplay1 = null;
		}
		// 释放所有扑克牌所占的资源
		if (poker != null) {
			for (Poker card : poker) {
				if (card != null) {
					card.onDestory();
				}
				card = null;
			}
			poker = null;
		}
		poker = PokerUtil.getPoker(ctx);
		nowcard = new ArrayList<Poker>();
		chupaicard = new ArrayList<Poker>();
		otherplay1 = new ArrayList<Poker>();
		// ImageUtil.clearPokerMap();
		setViewInitData();
		showWaitView();
		AudioPlayUtils.isPlay = true;
		playOrStopBgMusic();
	}

	/**
	 * 隐藏对话框
	 */
	private void dismissDialog() {
		if (null != popupWindow) {
			popupWindow.dismiss();
		}
		if (null != settingDialog && settingDialog.isShowing()) {
			settingDialog.dismiss();
		}
		if (null != mGameEndDialog && mGameEndDialog.isShowing()) {
			mGameEndDialog.dismiss();
		}
		if (null != mrDialog && mrDialog.isShowing()) {
			mrDialog.dismiss();
			mrDialog = null;
		}
		if (null != mChatDialog && mChatDialog.isShowing()) {
			mChatDialog.dismiss();
			mChatDialog = null;
		}
	}

	/**
	 * 弹出淘汰或胜利对话框
	 * 
	 * @param rank
	 * @param prizeGoods2
	 */
	private void showGameOverDialog(final String rank, final List<PrizeGoods> prizeGoods2) {
		god = new GameOverDialog(DoudizhuMainGameActivity.this, R.style.dialog, prizeGoods2, taskManager, rank);
		god.setInterface(DoudizhuMainGameActivity.this);
		if (null != god && !god.isShowing()) {
			god.setContentView(R.layout.game_place_end_dailog);
			android.view.WindowManager.LayoutParams lay = god.getWindow().getAttributes();
			setParams(lay);
			god.show();
			gameOverDetail = "";
			Log.i("gameOverDetail", "清空-----gameOverDetail:" + gameOverDetail);
		}
		// ClientCmdMgr.closeClient();// 断开Socket
	}

	// 表情点击事件
	public void photoClick(String mess) {
		try {
			userinfoshowView.setVisibility(View.VISIBLE);
			LayoutParams layoutParams = new LayoutParams(mst.adjustXIgnoreDensity(300), mst.adjustXIgnoreDensity(180));
			LayoutParams textParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			int tipWidth = 340;
			int tipHeight = 227;
			float sx = (float) Database.SCREEN_HEIGHT / (float) Constant.DEFAULT_HEIGHT;
			float sy = (float) Database.SCREEN_WIDTH / (float) Constant.DEFAULT_WIDTH;
			BitmapDrawable bitmapDrawable = (BitmapDrawable) ImageUtil.getResDrawable(R.drawable.tip_msg, true);
			Bitmap bitmap = null;
			layoutParams.setMargins(mst.adjustXIgnoreDensity(100), mst.adjustXIgnoreDensity(30), 0, 0);
			bitmap = Bitmap.createBitmap(bitmapDrawable.getBitmap(), 0, 0, tipWidth, tipHeight);
			ImageUtil.releaseDrawable(bitmapDrawable);
			String bitMapKey = String.valueOf(R.drawable.tip_msg) + "_small";
			bitmap = ImageUtil.getBitmap(bitMapKey, false);
			if (bitmap == null) {
				bitmap = ImageUtil.resizeBitmap(bitmap, tipWidth * sx, tipHeight * sy);
				ImageUtil.addBitMap2Cache(bitMapKey, bitmap);
			}
			textParam.setMargins(0, mst.adjustXIgnoreDensity(30), 0, 0);
			textParam.setMargins(0, 30, 0, 0);
			userinfoshowView.setBackgroundDrawable(new BitmapDrawable(bitmap));
			userinfoshowView.setLayoutParams(layoutParams);
			StringBuffer msgBuffer = new StringBuffer();
			msgBuffer.append(mess);
			// .append(u.getAccount()).append("</font><br/>");
			userInfoText.setLayoutParams(textParam);
			userInfoText.setText(mess);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 自己不出，pass
	 */
	private void passCard() {
		setTishiGone();
		isWait5Second = false;
		for (int i = 0; i < nowcard.size(); i++) {
			poker[nowcard.get(i).getNumber()].params.topMargin = mst.adjustYIgnoreDensity(20);
			poker[nowcard.get(i).getNumber()].setLayoutParams(poker[nowcard.get(i).getNumber()].params);
			poker[nowcard.get(i).getNumber()].ischeck = false;
		}
		initTiShiCount();
		CmdUtils.pass();
		ImageView im = new ImageView(ctx);
		im.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.play_buchu, true));
		play1PassLayout.removeAllViews();
		play1PassLayout.addView(im, mst.getAdjustLayoutParamsForImageView(im));
		ActivityUtils.startScaleAnim(play1PassLayout, ctx);// 播放缩放动画
		stopTimer(0);// 停止自己的定时器
		startPlayTimer(getNextOrder(mySelfOrder) + 1200);
		String gender = Database.userMap.get(mySelfOrder).getGender();
		if ("1".equals(gender)) {
			AudioPlayUtils.getInstance().playSound(R.raw.nv_pass);// 不要
		} else {
			AudioPlayUtils.getInstance().playSound(R.raw.nan_pass);// 不要
		}
		// 头像高亮切换
		zhezhao1.setVisibility(View.VISIBLE);
		ImageView img = (ImageView) findViewById(getNextOrder(mySelfOrder) + 1400);
		img.setVisibility(View.GONE);
		// 抹去上轮出的牌
		RelativeLayout res = (RelativeLayout) findViewById((getNextOrder(mySelfOrder)) + 1000);
		res.removeAllViews();
		hiddenPlayBtn();
	}

	/**
	 * 隐藏手势提示
	 */
	private void setTishiGone() {
		if (mainGameGuideVI.isArrowIsUp()) {
			mainGameGuideVI.setArrowUpGone(false);
		}
		if (mainGameGuideVI.isArrowIsDown()) {
			mainGameGuideVI.setArrowDownGone(false);
		}
		if (mainGameGuideVI.isPoint()) {
			mainGameGuideVI.setPointGone(false);
		}
		if (mainGameGuideVI.isArrowLeftRightVisible()) {
			mainGameGuideVI.setArrowLeftRightGone(false);
		}
		if (mainGameGuideVI.isDoublePoint()) {
			mainGameGuideVI.setDoublePointGone(false);
		}
	}

	/**
	 * 取消本地托管状态
	 */
	private void cancelTuoGuanState() {
		if (isTuoguan) {// 如果处于托管状态，则取消托管
			tuoGuan.setClickable(false);
			isTuoguan = false;
			AnimUtils.startScaleAnimationOut(tuoGuanLayout, ctx);
			gameRobot.setClickable(true);
		}
	}

	/**
	 * 自己打牌
	 * 
	 * @param comeOnFling
	 *            是否为onFling调用
	 */
	private void playCard(boolean comeOnFling) {
		/** 标识自己是否出牌 **/
		valueMe = -1;
		boolean isPlayCard = false;
		setTishiGone();
		chupaicard.clear();
		initTiShiCount();
		for (Poker card : nowcard) {
			if (card.ischeck) {
				chupaicard.add(card);
			}
		}
		// 检测别人出过牌没有
		if (bierenchupai == null) {
			firstChupai = true;
		}
		// 检测自己的出牌类型
		if (chupaicard.size() != 0) {
			checkpai = new ArrayList<Poker>();
			for (Poker card : chupaicard) {
				checkpai.add(card);
			}
			typeMe = DoudizhuRule.checkpai(checkpai);
			valueMe = DoudizhuRule.checkpaiValue(typeMe,checkpai);
		} else {
			playError(comeOnFling);
			return;
		}
		// 对自己的牌型进行检测
		if (typeMe == 0) {
			playError(comeOnFling);
			return;
		}
		// 检查别人出什么牌
		if (!firstChupai) {
			typeplay1 = checkOtherChupai(bierenchupai);
			// 检查谁大
			if (DoudizhuRule.compterpai(typeplay1, typeMe, DoudizhuRule.getMaxNumber(otherplay1), DoudizhuRule.getMaxNumber(chupaicard), bierenchupai.length, chupaicard.size())) {
				cardAddview(chupaicard, false);
				initTiShiCount();
				// 发送出牌消失
				CmdUtils.play(chupaicard);
				/** 标识自己是否出牌 **/
				isPlayCard = true;
				hiddenPlayBtn();
				firstChupai = true;
				bierenchupai = null;
				playDiZhuCardAudio(typeMe,valueMe, Database.userMap.get(mySelfOrder).getGender()); // 出牌的声音
				// 如果是炸弹显示特效
				PlayCardEffect.bomEffect(typeMe, doudizhuBackGround);
				if (myCardsTouchLayout.getChildCount() > 0) {
					// 头像高亮切换
					zhezhao1.setVisibility(View.VISIBLE);
					ImageView img = (ImageView) findViewById(getNextOrder(mySelfOrder) + 1400);
					img.setVisibility(View.GONE);
					// 抹去上轮出的牌
					RelativeLayout res = (RelativeLayout) findViewById((getNextOrder(mySelfOrder)) + 1000);
					res.removeAllViews();
				}
			} else {
				playError(comeOnFling);
			}
		} else {
			cardAddview(chupaicard, false);
			initTiShiCount();
			// 发送出牌消息
			CmdUtils.play(chupaicard);
			/** 标识自己是否出牌 **/
			isPlayCard = true;
			hiddenPlayBtn();
			firstChupai = true;
			// 自己出完牌后将别人出牌清空
			bierenchupai = null;
			playDiZhuCardAudio(typeMe, valueMe,Database.userMap.get(mySelfOrder).getGender()); // 出牌的声音
			// 如果是炸弹显示特效
			PlayCardEffect.bomEffect(typeMe, doudizhuBackGround);
			if (myCardsTouchLayout.getChildCount() > 0) {
				// 头像高亮切换
				zhezhao1.setVisibility(View.VISIBLE);
				ImageView img = (ImageView) findViewById(getNextOrder(mySelfOrder) + 1400);
				img.setVisibility(View.GONE);
				// 抹去上轮出的牌
				RelativeLayout res = (RelativeLayout) findViewById((getNextOrder(mySelfOrder)) + 1000);
				res.removeAllViews();
			}
		}
		play1SurplusCount.setText("" + myCardsTouchLayout.getChildCount());
		if (comeOnFling) {
			myCardsTouchLayout.chekCard();
		}
		if (isPlayCard) {
			if (null != chupaicard)
				addOutPokers(mySelfOrder, chupaicard);
			refreshCardCountData();
		}
	}

	/**
	 * 打的牌不符合规矩
	 * 
	 * @param comeOnFling
	 */
	private void playError(boolean comeOnFling) {
		if (comeOnFling) {
			myCardsTouchLayout.chekCard();
		}
		Toast.makeText(ctx, "你出的牌不符合规矩", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示用户信息
	 */
	public void setUserInfo() {
		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		if (cacheUser != null) {
			zhidou.setText(String.valueOf(cacheUser.getBean()));
		} else {
			zhidou.setText(String.valueOf("20000"));
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

	/**
	 * 判断输赢并显示出來
	 * 
	 * @param users
	 */
	public void setEndDonghua(final LinkedList<Play> users) {
		AudioPlayUtils.getInstance().stopMusic();
		AudioPlayUtils.getInstance().stopBgMusic();
		cancelTimer();
		dismissDialog();
		
		LinkedList<Play> userLink = new LinkedList<Play>(users);
		AudioPlayUtils.getInstance().playSound(R.raw.get_glod); // 数金豆声音
		for (Play end : userLink) {
			// 如果是自己的顺序
			String[] last = end.getCards().substring(1, end.getCards().length() - 1).split(",");
			int nextPlay = getNextOrder(mySelfOrder);
			if (end.getOrder() == mySelfOrder) {
				int now[] = new int[last.length];
				for (int j = 0; j < last.length; j++) {
					now[j] = Integer.parseInt(last[j]);
				}
				addCard(now);
				play1PassLayout.removeAllViews();
				setWolTvNum(wolTv1, end);
			} else if (end.getOrder() == nextPlay) {
				addCard(last, play2PassLayout, true);
				setWolTvNum(wolTv2, end);
			} else {
				addCard(last, play3PassLayout, false);
				wolTv3.setVisibility(View.VISIBLE);
				setWolTvNum(wolTv3, end);
			}
		}
		if (selfIsMove) {
			selfIsMove = false;
			nullTv.setVisibility(View.GONE);
			nullTv2.setVisibility(View.VISIBLE);
		}
		if(null != beansInsufficientRl && View.VISIBLE== beansInsufficientRl.getVisibility()){
			beansInsufficientRl.setVisibility(View.GONE);
		}

		AutoTask goOutTask = new AutoTask() {

			public void run() {
				ctx.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						dismissDialog();
						GameEndDialog mGameEndDialog = new GameEndDialog(ctx, users, mySelfOrder, handler);
						mGameEndDialog.setContentView(R.layout.doudizhu_end);
						android.view.WindowManager.LayoutParams lay = mGameEndDialog.getWindow().getAttributes();
						setParams(lay);
						mGameEndDialog.show();
					}
				});
			}
		};
		ScheduledTask.addDelayTask(goOutTask, (3000));
	}

	/**
	 * 设置输赢金豆变动的数量
	 * 
	 * @param view
	 *            显示Tv
	 * @param end
	 *            玩家
	 */
	private void setWolTvNum(final TextView view, final Play end) {
		view.setVisibility(View.VISIBLE);
		if (0 > end.getPayment()) {
			view.setText("-" + Math.abs(end.getPayment()));
			view.setTextColor(ctx.getResources().getColor(R.color.chestnut_red));
		} else {
			view.setTextColor(ctx.getResources().getColor(R.color.gold));
			view.setText("+" + Math.abs(end.getPayment()));
		}
		new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					int num = (int) Math.abs(end.getPayment());
					int step = 0;// 步长
					int count = 0;// 循环次数
					int yushu = 0;// 余数
					if (num >= 200) {
						step = num / 200;
						count = 200;
						yushu = num % 200;
					} else {
						step = 1;
						count = num;
					}
					// 先显示余数
					if (end.getPayment() > 0) {
						view.setText("+" + yushu);
					} else {
						view.setText("-" + yushu);
					}
					for (int i = 1; i <= count; i++) {
						// 每间隔10毫秒递增
						sleep(10);
						yushu += step;
						final int yushu1 = yushu;
						ctx.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if (end.getPayment() > 0) {
									view.setText("+" + yushu1);
								} else {
									view.setText("-" + yushu1);
								}
							}
						});
					}
				} catch (Exception e) {}
			}
		}.start();
	}

	private int cardSpace = 0;

	/**
	 * 把剩下的牌显示出来
	 * 
	 * @param now
	 */
	public void addCard(String[] str, RelativeLayout rel, boolean isLeft) {
		try {
			int now[] = new int[str.length];
			for (int j = 0; j < str.length; j++) {
				now[j] = Integer.parseInt(str[j]);
			}
			rel.removeAllViews();
			int[] paixu = DoudizhuRule.sort(now, poker);
			cardSpace = mst.adjustXIgnoreDensity(cardSpace);
			for (int i = 0; i < paixu.length; i++) {
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mst.adjustXIgnoreDensity(50), mst.adjustYIgnoreDensity(68));
				Poker card = poker[paixu[i]];
				card.getPokeImage().setImageDrawable(ImageUtil.getResDrawable(card.getBitpamResID(), true));
				if (i < 9) {
					params.topMargin = mst.adjustYIgnoreDensity(20);
					params.leftMargin = mst.adjustXIgnoreDensity(20 * i);
				} else {
					params.topMargin = mst.adjustYIgnoreDensity(54);
					params.leftMargin = mst.adjustXIgnoreDensity(20 * (i - 9));
				}
				card.setLayoutParams(params);
				rel.addView(poker[paixu[i]]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 清除出牌提示信息
	 */
	public void cleanAllChuPaiInfo() {
		play1PassLayout.removeAllViews();
		play3PassLayout.removeAllViews();
		play2PassLayout.removeAllViews();
	}

	/**
	 * 检查别人出什么类型的牌
	 * 
	 * @param chu
	 * @return
	 */
	public int checkOtherChupai(int[] chu) {
		// 首先将对方的牌拍好序
		int[] otherpaixu = DoudizhuRule.sort(chu, poker);
		otherplay1.clear();
		for (int i = 0; i < otherpaixu.length; i++) {
			otherplay1.add(poker[otherpaixu[i]]);
		}
		int type = DoudizhuRule.checkpai(otherplay1);
		return type;
	}

	/**
	 * 显示打出去的牌,并减少自己的牌
	 * 
	 * @param passCardList
	 */
	public void cardAddview(List<Poker> passCardList, boolean sigleTime) {
		play1PassLayout.removeAllViews();
		for (int i = 0; i < passCardList.size(); i++) {
			RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(mst.adjustXIgnoreDensity(50), mst.adjustXIgnoreDensity(68));
			Poker image = new Poker(this);
			image.getPokeImage().setImageDrawable(ImageUtil.getResDrawable(passCardList.get(i).getBitpamResID(), true));;
			// image.setImageResource(pass.get(i).getBitpamResID());
			params3.leftMargin = mst.adjustXIgnoreDensity(20 * i);
			play1PassLayout.addView(image, params3);// 显示出来
			// mst2.adjustView(image);
			// 清除图片
			ImageUtil.releaseDrawable(passCardList.get(i).getPokeImage().getDrawable());
			// 移除出的牌
			nowcard.remove(passCardList.get(i));
			myCardsTouchLayout.removeView(passCardList.get(i));
			// 让剩下的牌居中
			for (int j = 0; j < nowcard.size(); j++) {
				if (nowcard.size() > 1) {
					card_jiange = (int) ((800 - 90) / (nowcard.size() - 1));
					if (card_jiange > 50) {
						card_jiange = 50;
					}
				} else {
					card_jiange = 50;
				}
				myCardsTouchLayout.setDistance(mst.adjustXIgnoreDensity(card_jiange));
				nowcard.get(j).params.leftMargin = super.mst.adjustXIgnoreDensity(card_jiange * j);
			}
			stopTimer(0);// 停止自己的定时器
			if (!sigleTime) {
				startPlayTimer(getNextOrder(mySelfOrder) + 1200);
			}
		}
		// 打完牌后取消定时器
		if (myCardsTouchLayout.getChildCount() == 0) {
			cancelTimer();
		}
	}

	/**
	 * 将选中的牌添加到移动的View中
	 * 
	 * @param passCardList
	 */
	public void cardAddMoveView(List<Poker> passCardList) {
		play1PassLayout.removeAllViews();
		for (int i = 0; i < passCardList.size(); i++) {
			RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(mst.adjustXIgnoreDensity(50), mst.adjustXIgnoreDensity(68));
			Poker image = new Poker(this);
			image.getPokeImage().setImageDrawable(ImageUtil.getResDrawable(passCardList.get(i).getBitpamResID(), true));;
			// image.setImageResource(pass.get(i).getBitpamResID());
			params3.leftMargin = mst.adjustXIgnoreDensity(20 * i);
			play1PassLayout.addView(image, params3);// 显示出来
			// mst2.adjustView(image);
			// 清除图片
			ImageUtil.releaseDrawable(passCardList.get(i).getPokeImage().getDrawable());
			// 移除出的牌
			nowcard.remove(passCardList.get(i));
			myCardsTouchLayout.removeView(passCardList.get(i));
			// 让剩下的牌居中
			for (int j = 0; j < nowcard.size(); j++) {
				if (nowcard.size() > 1) {
					card_jiange = (int) ((800 - 90) / (nowcard.size() - 1));
					if (card_jiange > 50) {
						card_jiange = 50;
					}
				} else {
					card_jiange = 50;
				}
				myCardsTouchLayout.setDistance(mst.adjustXIgnoreDensity(card_jiange));
				nowcard.get(j).params.leftMargin = super.mst.adjustXIgnoreDensity(card_jiange * j);
			}
		}
	}

	/**
	 * 托管
	 */
	public void setTuoGuan() {
		for (int i = 0; i < nowcard.size(); i++) {
			poker[nowcard.get(i).getNumber()].params.topMargin = mst.adjustYIgnoreDensity(20);
			poker[nowcard.get(i).getNumber()].setLayoutParams(poker[nowcard.get(i).getNumber()].params);
			poker[nowcard.get(i).getNumber()].ischeck = false;
		}
		if (bierenchupai == null) {// 如果是自己先出牌
			if (nowcard.size() != 0) {
				stopTimer(0);// 停止自己的定时器
				List<Poker> tuoguanCards = new ArrayList<Poker>();
				tuoguanCards.add(nowcard.get(nowcard.size() - 1));
				cardAddview(tuoguanCards, true);
				CmdUtils.play(tuoguanCards);
				if (myCardsTouchLayout.getChildCount() > 0) {
					startPlayTimer(getNextOrder(mySelfOrder) + 1200);
					initTiShiCount();
					zhezhao1.setVisibility(View.VISIBLE);
					ImageView img = (ImageView) findViewById(getNextOrder(mySelfOrder) + 1400);
					img.setVisibility(View.GONE);
					// 抹去上轮出的牌
					RelativeLayout res = (RelativeLayout) findViewById((getNextOrder(mySelfOrder)) + 1000);
					res.removeAllViews();
				}
				play1SurplusCount.setText("" + myCardsTouchLayout.getChildCount());
			}
		} else {
			checkOtherChupai(bierenchupai);
			DouDiZhuData data = new DouDiZhuData(nowcard);
			DouDiZhuData datas = new DouDiZhuData(nowcard);
			data.fillPokerList();
			List<List<Poker>> tishiList = data.getTiShi(otherplay1);
			checkOtherChupai(bierenchupai);
			datas.fillAllPokerList();
			List<List<Poker>> tishiList2 = datas.getTiShi(otherplay1);
			HintPokerUtil aList = new HintPokerUtil();
			if (tishiList != null && tishiList2 != null) {
				tishiList = aList.filterHintPoker(tishiList, tishiList2);
			}
			if (tishiList == null) {
				passCard();
				return;
			}
			if (tishiList != null && tishiList.size() == 0) {
				passCard();
				return;
			}
			// tiShiCount=-1;
			setTiShiCount();
			if (getTiShiCount() > tishiList.size() - 1) {
				initTiShiCount();
				setTiShiCount();
			}
			List<Poker> tiShiPoker = tishiList.get(getTiShiCount());
			if (tiShiPoker == null) {
				passCard();
				return;
			}
			for (int i = 0; i < tiShiPoker.size(); i++) {
				poker[tiShiPoker.get(i).getNumber()].params.topMargin = 0;
				poker[tiShiPoker.get(i).getNumber()].setLayoutParams(poker[tiShiPoker.get(i).getNumber()].params);
				poker[tiShiPoker.get(i).getNumber()].ischeck = true;
			}
			initTiShiCount();
			playCard(false);
		}
		hiddenPlayBtn();
	}

	/**
	 * 显示剩下的牌数
	 * 
	 * @param Rid
	 * @param paiCount
	 * @param order
	 */
	public void setShengxiaPai(int paiCount, final int order) {
		if (order != mySelfOrder) {
			final TextView nowView = (TextView) findViewById(1100 + order);
			nowView.setText(String.valueOf(paiCount));
			// 牌数少于三张,且之前没有警告过
			if (paiCount <= 3 && null != warn && warn.containsKey(1100 + order) && !warn.get(1100 + order)) {
				AudioPlayUtils.getInstance().playSound(R.raw.audio_warn);// 警告
				Animation animationjg = AnimationUtils.loadAnimation(this, R.anim.my_scale_action);
				nowView.startAnimation(animationjg);
				animationjg.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {}

					@Override
					public void onAnimationRepeat(Animation animation) {}

					@Override
					public void onAnimationEnd(Animation animation) {
						nowView.setTextColor(getResources().getColor(color.gold));
						warn.put(1100 + order, true);
					}
				});
				nowView.setTextColor(getResources().getColor(color.red));
			}
		} else { // 自己出的牌
			play1SurplusCount.setText(String.valueOf(paiCount));
		}
	}

	/**
	 * 把牌的字符串转化成数组
	 * 
	 * @param cards
	 * @return
	 */
	public int[] setFirstCard(String cards) {
		String str = cards.substring(1, cards.length() - 1);
		String[] listCard = str.split(",");
		pai = new int[listCard.length];
		for (int i = 0; i < listCard.length; i++) {
			pai[i] = Integer.parseInt(listCard[i]);
		}
		return pai;
	}

	/**
	 * 设置出牌的顺序
	 * 
	 * @param myOrder
	 *            自己的出牌顺序
	 */
	public void setOrder(int myOrder) {
		if (myOrder == 1) {
			play2PassLayout.setId(1002);
			play3PassLayout.setId(1003);
			play2SurplusCount.setId(1102);
			play3SurplusCount.setId(1103);
			play2Timer.setId(1202);
			play3Timer.setId(1203);
			PLAY2ICON_ID = 1302;
			PLAY3ICON_ID = 1303;
			ZHEZHAO2_ID = 1402;
			ZHEZHAO3_ID = 1403;
			JIABEI2_ID = 1602;
			JIABEI3_ID = 1603;
		} else if (myOrder == 2) {
			play2PassLayout.setId(1003);
			play3PassLayout.setId(1001);
			play2SurplusCount.setId(1103);
			play3SurplusCount.setId(1101);
			play2Timer.setId(1203);
			play3Timer.setId(1201);
			PLAY2ICON_ID = 1303;
			PLAY3ICON_ID = 1301;
			ZHEZHAO2_ID = 1403;
			ZHEZHAO3_ID = 1401;
			JIABEI2_ID = 1603;
			JIABEI3_ID = 1601;
		} else if (myOrder == 3) {
			play2PassLayout.setId(1001);
			play3PassLayout.setId(1002);
			play2SurplusCount.setId(1101);
			play3SurplusCount.setId(1102);
			play2Timer.setId(1201);
			play3Timer.setId(1202);
			PLAY2ICON_ID = 1301;
			PLAY3ICON_ID = 1302;
			ZHEZHAO2_ID = 1401;
			ZHEZHAO3_ID = 1402;
			JIABEI2_ID = 1601;
			JIABEI3_ID = 1602;
		}
		play2Icon.setId(PLAY2ICON_ID);
		play3Icon.setId(PLAY3ICON_ID);
		zhezhao2.setId(ZHEZHAO2_ID);
		zhezhao3.setId(ZHEZHAO3_ID);
	}

	/**
	 * 当前玩家的上一家位置编号
	 * 
	 * @param currentOrder
	 * @return
	 */
	public int getPerOrder(int currentOrder) {
		int now = 0;
		if (currentOrder == 1) {
			now = 3;
		} else {
			now = currentOrder - 1;
		}
		return now;
	}

	/**
	 * 根据自己的order 得出下一个order是谁
	 * 
	 * @param nextOrder
	 * @return
	 */
	public int getNextOrder(int Order) {
		int next = 0;
		if (Order == 3) {
			next = 1;
		} else {
			next = Order + 1;
		}
		return next;
	}

	/**
	 * 地主产生后开始定时器
	 */
	public void startOtherTimer() {
		if (mySelfOrder != masterOrder) {
			startPlayTimer(masterOrder + 1200);
		}
	}

	/**
	 * 抢地主定时器开始 （-1 上家 0 自己 1 下家）
	 */
	public void startQiangTimer(int callOrder) {
		if (gameTask != null) {
			gameTask.stop(true);
			gameTask = null;
		}
		gameTask = new DizhuTask(callOrder);
		ScheduledTask.addRateTask(gameTask, 1000);
	}

	/**
	 * 踢拉定时器开始 （-1 上家 0 自己 1 下家）
	 */
	public void startTiLaTimer(int callOrder) {
		if (gameTask != null) {
			gameTask.stop(true);
			gameTask = null;
		}
		gameTask = new TiLaTask(callOrder);
		ScheduledTask.addRateTask(gameTask, 1000);
	}

	/**
	 * 打牌定时器开始
	 */
	public void startPlayTimer(int id) {
		// 在0秒后执行此任务,每次间隔1秒,如果传递一个Data参数,就可以在某个固定的时间执行这个任务.
		if (gameTask != null) {
			gameTask.stop(true);
			gameTask = null;
		}
		gameTask = new DapaiTask(id);
		ScheduledTask.addRateTask(gameTask, 1000);
	}

	/**
	 * 取消并重置自己的定时器
	 * 
	 * @param timerOrder
	 *            当前定时的人 0自己 1下家 -1上家
	 */
	public void stopTimer(int timerOrder) {
		cancelTimer();
		if (timerOrder == 0) {
			play1Timer.setVisibility(View.GONE);
			play1Timer.setText(String.valueOf(Constant.WAIT_TIME));
		} else if (timerOrder == 1) {
			play2Timer.setVisibility(View.GONE);
			play2Timer.setText(String.valueOf(Constant.WAIT_TIME));
		} else if (timerOrder == -1) {
			play3Timer.setVisibility(View.GONE);
			play3Timer.setText(String.valueOf(Constant.WAIT_TIME));
		}
	}

	/**
	 * 取消并重置打牌的定时器
	 * 
	 */
	public void setTimer0(int id) {
		cancelTimer();
		TextView now = (TextView) findViewById(id);
		if (now != null) {
			now.setVisibility(View.GONE);
			now.setText(String.valueOf(Constant.WAIT_TIME));
		}
	}

	/**
	 * 根据地主产生来决定是否需要更新自己的牌
	 * 
	 * @param master
	 */
	public void genxinMycard(String id, String lastCards, int mastOder) {
		// 清除叫地主信息
		// cleanAllChuPaiInfo();
		for (int i = 0; i < nowcard.size(); i++) {
			poker[nowcard.get(i).getNumber()].params.topMargin = mst.adjustYIgnoreDensity(20);
			poker[nowcard.get(i).getNumber()].setLayoutParams(poker[nowcard.get(i).getNumber()].params);
			poker[nowcard.get(i).getNumber()].ischeck = false;
		}
		// 如果自己是地主的话
		if (id.equals(mySelfId)) {
			String dipai = lastCards;
			String str = dipai.substring(1, dipai.length() - 1);
			String[] dizhuCard = str.split(",");//地主牌
			int[] now = new int[20];
			for (int i = 0; i < pai.length; i++) {
				now[i] = pai[i];
			}
			for (int i = 0; i < dizhuCard.length; i++) {
				now[i + 17] = Integer.parseInt(dizhuCard[i]);
			}
			// 清除再添加自己手中的牌
			for (int i = 0; i < paixu.length; i++) {
				myCardsTouchLayout.removeView(poker[paixu[i]]);
			}
			nowcard.clear();
			addCard(now);
			// 把地主的牌数加上3
			play1SurplusCount.setText("" + now.length);
			play1PassLayout.removeAllViews();
			showPlayBtn(true);
			if (isTuoguan) { // 如果当前正在托管
				setTuoGuan();
			} else {
				// 开启一个定时器
				startPlayTimer(R.id.play1Time);
			}
		} else { // 如果自己不是地主的话
			String dipai = lastCards;
			String str = dipai.substring(1, dipai.length() - 1);
			String[] dizhuCard = str.split(",");
			// 把地主的牌数加上3
			TextView cardCount = (TextView) findViewById(1100 + mastOder);
			cardCount.setText("20");
		}
	}

	/**
	 * 打牌更新界面之类的
	 * 
	 * @param play
	 * @param reShow
	 *            是否是重连显示操作
	 */
	public void playCard(Play play, boolean reShow) {
		// 取得别人出的牌并消除之前出过的牌
		String pai = play.getCards();
		String str = pai.substring(1, pai.length() - 1);
		int nextPlayId = getPerOrder(play.getNextOrder()) + 1000;
		nextPlayLayout = (RelativeLayout) findViewById(nextPlayId);
		// 设置上一轮的计时器清理 高亮头像切换
		if (play.getCount() > 0) {
			int playOrder = getPerOrder(play.getNextOrder());
			if (playOrder == mySelfOrder) {
				setTimer0(R.id.play1Time);
				zhezhao1.setVisibility(View.VISIBLE);
			} else {
				setTimer0(playOrder + 1200);
				ImageView img = (ImageView) findViewById(playOrder + 1400);
				if(img != null)
				  img.setVisibility(View.VISIBLE);
			}
			if (nextPlayLayout.getChildCount() != 0) {
				nextPlayLayout.removeAllViews();
			}
		} else {
			cancelTimer();
		}
		// 如果别人不要
		if (str.equals("")) {
			int playOrder = 1;
			if (play.getCount() > 0) {
				playOrder = getPerOrder(play.getNextOrder());
			}
			String gender = Database.userMap.get(playOrder).getGender();
			if ("1".equals(gender)) {
				AudioPlayUtils.getInstance().playSound(R.raw.nv_pass);// 不出
			} else {
				AudioPlayUtils.getInstance().playSound(R.raw.nan_pass);// 不出
			}
			ImageView im = new ImageView(ctx);
			im.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.play_buchu, true));
			nextPlayLayout.addView(im, mst.getAdjustLayoutParamsForImageView(im));
			ActivityUtils.startScaleAnim(nextPlayLayout, ctx);// 播放缩放动画
		} else {
			// 别人只要出牌了到自己出牌的时候就要检测对手牌
			firstChupai = false;
			String[] otherPlayCards = str.split(",");
			// 别人出牌显示出来哇
			List<Poker> carList = new ArrayList<Poker>();
			for (int i = 0; i < otherPlayCards.length; i++) {
				Poker c = poker[Integer.parseInt(otherPlayCards[i])];
				c.getPokeImage().setImageDrawable(ImageUtil.getResDrawable(poker[Integer.parseInt(otherPlayCards[i])].getBitpamResID(), true));
				c.params.leftMargin = mst.adjustXIgnoreDensity(20) * i;
				c.params.topMargin = 0;
				c.params.height = mst.adjustXIgnoreDensity(68); // 91
				c.params.width = mst.adjustXIgnoreDensity(50); // 68
				if (c.getParent() != null) {
					((RelativeLayout) c.getParent()).removeView(c);
				}
				nextPlayLayout.addView(c, c.params);
				if (nextPlayLayout != null) {}
				carList.add(c);
			}
			/** 记录别人已经出的牌 **/
			addOutPokers(play.getOrder(), carList);
			int type = DoudizhuRule.checkpai(carList);
			int value = DoudizhuRule.checkpaiValue(type,carList);
			int playOrder = 1;
			if (play.getCount() > 0) {
				playOrder = getPerOrder(play.getNextOrder());
			}
			
			playDiZhuCardAudio(type,value, TextUtils.isEmpty(Database.userMap.get(playOrder).getGender()) ? "0" : Database.userMap.get(playOrder).getGender()); // 出牌的声音
			// 如果是炸弹显示特效
			PlayCardEffect.bomEffect(type, doudizhuBackGround);
			if (play.getCount() > 0) {
				// 如果下一个打牌的人是自己的话
				if (play.getNextOrder().intValue() == mySelfOrder) {
					play1PassLayout.removeAllViews();
				}
			}
			int[] cad = new int[otherPlayCards.length];
			for (int i = 0; i < otherPlayCards.length; i++) {
				cad[i] = Integer.parseInt(otherPlayCards[i]);
			}
			bierenchupai = cad;
		}
		if (play.getCount() == 0) { // 已出完牌 此牌为最后一手
			return;
		}
		// 如果下一个是自己出牌
		if (play.getNextOrder() == mySelfOrder) {
			isTurnMySelf = true;
			zhezhao1.setVisibility(View.GONE);
			play1PassLayout.removeAllViews();
			if (bierenchupai == null) { // 如果别人都没有出牌，说明这轮你先出牌
				showPlayBtn(true);
			} else {
				showPlayBtn(false);
			}
			if (!isTuoguan) { // 如果没有托管
				isWaitFiveSecond();
				play1PassLayout.removeAllViews();
			} else {
				setTuoGuan();
			}
		} else {
			isTurnMySelf = false;
			// 最后开启别人一个打牌的定时器
			startPlayTimer(play.getNextOrder() + 1200);
			ImageView img = (ImageView) findViewById(play.getNextOrder() + 1400);
			img.setVisibility(View.GONE);
			// 抹去上轮出的牌
			RelativeLayout res = (RelativeLayout) findViewById(play.getNextOrder() + 1000);
			res.removeAllViews();
		}
	}

	/**
	 * 叫地主定时器
	 * 
	 * @author Administrator
	 */
	class DizhuTask extends AutoTask { // TimerTask { // 叫地主定时器

		private int callOrder; // 是否是自己叫地主 （-1 上家 0 自己 1 下载）

		public DizhuTask(int callOrder) {
			this.callOrder = callOrder;
			if (callOrder == 0) { // 自己
				play1Timer.setVisibility(View.VISIBLE);
				play1Timer.setText(String.valueOf(Constant.WAIT_TIME));
			} else if (callOrder == 1) { // 下家
				play2Timer.setVisibility(View.VISIBLE);
				play2Timer.setText(String.valueOf(Constant.WAIT_TIME));
			} else if (callOrder == -1) { // 上家
				play3Timer.setVisibility(View.VISIBLE);
				play3Timer.setText(String.valueOf(Constant.WAIT_TIME));
			}
		}

		public void run() {
			Message msg = new Message();
			msg.arg1 = callOrder;
			msg.what = 5;
			handler.sendMessage(msg);
		}
	}

	/**
	 * 踢拉定时器
	 * 
	 * @author Administrator
	 */
	class TiLaTask extends AutoTask { // TimerTask { //

		private int callOrder; // 是否是自己叫地主 （-1 上家 0 自己 1 下载）

		public TiLaTask(int callOrder) {
			this.callOrder = callOrder;
			if (callOrder == 0) { // 自己
				play1Timer.setVisibility(View.VISIBLE);
				play1Timer.setText(String.valueOf(Constant.WAIT_TIME));
			} else if (callOrder == 1) { // 下家
				play2Timer.setVisibility(View.VISIBLE);
				play2Timer.setText(String.valueOf(Constant.WAIT_TIME));
			} else if (callOrder == -1) { // 上家
				play3Timer.setVisibility(View.VISIBLE);
				play3Timer.setText(String.valueOf(Constant.WAIT_TIME));
			}
		}

		public void run() {
			Message msg = new Message();
			msg.arg1 = callOrder;
			msg.what = 13;
			handler.sendMessage(msg);
		}
	}

	/**
	 * dapai定时器
	 * 
	 * @author Administrator
	 * 
	 */
	class DapaiTask extends AutoTask { // TimerTask { // dapai定时器

		private int id = 0;

		public DapaiTask(int id) {
			this.id = id;
			TextView time = (TextView) findViewById(id);
			time.setVisibility(View.VISIBLE);
			time.setText(String.valueOf(Constant.WAIT_TIME));
		}

		public void run() {
			Message msg = new Message();
			msg.what = 6;
			msg.arg1 = id;
			handler.sendMessage(msg);
		}
	}

	/**
	 * 取消播放
	 */
	public void stopBusyLoadAnim(int busyResId) {
		ImageView busyImageView = (ImageView) findViewById(busyResId);
		busyImageView.setVisibility(View.GONE);
	}

	/**
	 * 斗地主出牌声音
	 */
	public static int[][] sound_single = {
		{
			R.raw.m_1_3,R.raw.m_1_4,R.raw.m_1_5,R.raw.m_1_6,
			R.raw.m_1_7,R.raw.m_1_8,R.raw.m_1_9,R.raw.m_1_10,
			R.raw.m_1_11,R.raw.m_1_12,R.raw.m_1_13,R.raw.m_1_14,
			R.raw.m_1_15,R.raw.m_1_16,R.raw.m_1_17},
		{
			R.raw.w_1_3,R.raw.w_1_4,R.raw.w_1_5,R.raw.w_1_6,
			R.raw.w_1_7,R.raw.w_1_8,R.raw.w_1_9,R.raw.w_1_10,
			R.raw.w_1_11,R.raw.w_1_12,R.raw.w_1_13,R.raw.w_1_14,
			R.raw.w_1_15,R.raw.w_1_16,R.raw.w_1_17},
	};
	public static int[][] sound_pair = {
		{
			R.raw.m_2_3,R.raw.m_2_4,R.raw.m_2_5,R.raw.m_2_6,
			R.raw.m_2_7,R.raw.m_2_8,R.raw.m_2_9,R.raw.m_2_10,
			R.raw.m_2_11,R.raw.m_2_12,R.raw.m_2_13,R.raw.m_2_14,
			R.raw.m_2_15},
		{
			R.raw.w_2_3,R.raw.w_2_4,R.raw.w_2_5,R.raw.w_2_6,
			R.raw.w_2_7,R.raw.w_2_8,R.raw.w_2_9,R.raw.w_2_10,
			R.raw.w_2_11,R.raw.w_2_12,R.raw.w_2_13,R.raw.w_2_14,
			R.raw.w_2_15},
	};
	public static int[][] sound_three = {
		{
			R.raw.m_tuple3,R.raw.m_tuple4,R.raw.m_tuple5,R.raw.m_tuple6,
			R.raw.m_tuple7,R.raw.m_tuple8,R.raw.m_tuple9,R.raw.m_tuple10,
			R.raw.m_tuple11,R.raw.m_tuple12,R.raw.m_tuple13,R.raw.m_tuple14,
			R.raw.m_tuple15,
		},
		{
			R.raw.w_tuple3,R.raw.w_tuple4,R.raw.w_tuple5,R.raw.w_tuple6,
			R.raw.w_tuple7,R.raw.w_tuple8,R.raw.w_tuple9,R.raw.w_tuple10,
			R.raw.w_tuple11,R.raw.w_tuple12,R.raw.w_tuple13,R.raw.w_tuple14,
			R.raw.w_tuple15,
		}
	};
	public void playDiZhuCardAudio(int audioType,int value, final String gender) {
		switch (audioType) {
			case DoudizhuRule.Danpai: // 如果是一张牌
				//AudioPlayUtils.getInstance().playSound(R.raw.outcard); // 出牌
				if(value>0)
				{
					value -= 3;
					if(value>=0 && value<sound_single[0].length)
					{
						AudioPlayUtils.getInstance().playSound(sound_single["1".equals(gender)?1:0][value]); // 出牌
					}
				}
				break;
			case DoudizhuRule.Yidui:// 如果是两张牌
				//AudioPlayUtils.getInstance().playSound(R.raw.outcard); // 出牌
				if(value>0)
				{
					value -= 3;
					if(value>=0 && value<sound_pair[0].length)
					{
						AudioPlayUtils.getInstance().playSound(sound_pair["1".equals(gender)?1:0][value]); // 出牌
					}
				}
				break;
			case DoudizhuRule.Zhadan: // 如果是四张牌 炸弹
				zhadanIv.setVisibility(View.VISIBLE);
				setTweenAnim(zhadanIv, R.anim.zhadang_play, IS_ZHADAN_ANIM);
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playMultiMusic2(R.raw.nv_bomb, R.raw.boombeffect);
				} else {
					AudioPlayUtils.getInstance().playMultiMusic2(R.raw.nan_bomb, R.raw.boombeffect);
				}
				break;
			case DoudizhuRule.Sandaier:// 如果是五张牌 三待二
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playSound(R.raw.nv_3dai2);
				} else {
					AudioPlayUtils.getInstance().playSound(R.raw.nan_3dai2);
				}
				break;
			case DoudizhuRule.Shunzi: // 顺牌
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playSound(R.raw.nv_shunzi);
				} else {
					AudioPlayUtils.getInstance().playSound(R.raw.nan_shunzi);
				}
				shunzImageView.setVisibility(View.VISIBLE);
				AnimUtils.playAnim(shunzImageView, ImageUtil.getResAnimaSoft("shunz"), 3000);
				break;
			case DoudizhuRule.Liandui: // 如果是6张 连对
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playSound(R.raw.nv_liandui);
				} else {
					AudioPlayUtils.getInstance().playSound(R.raw.nan_liandui);
				}
				break;
			case DoudizhuRule.Feiji: // 如果是6张 飞机
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playMultiMusic2(R.raw.nv_feiji, R.raw.planeeffect);
				} else {
					AudioPlayUtils.getInstance().playMultiMusic2(R.raw.nan_feiji, R.raw.planeeffect);
				}
				feijiImageView.setVisibility(View.VISIBLE);
				AnimUtils.playAnim(feijiImageView, ImageUtil.getResAnimaSoft("feiji"), 2000);
				setTweenAnim(feijiImageView, R.anim.feiji_out, IS_FEIJI_ANIM);
				break;
			default:
				AudioPlayUtils.getInstance().playSound(R.raw.outcard);
		}
	}

	/**
	 * 点击头像
	 * 
	 * @param view
	 * @param playOrder
	 * @param viewOrder
	 * @param isMaster
	 *            是否是地主
	 */
	public void photoClick(ImageView view, Integer playOrder, boolean isMaster) {
		/**地主未产生，禁止弹出用户信息对话框*/
		if (0 != masterOrder) {
			if (Database.userMap == null)
				return;
			if (Math.abs(System.currentTimeMillis() - Constant.CLICK_TIME) >= Constant.SPACING_TIME) {// 防止重复刷新
				Constant.CLICK_TIME = System.currentTimeMillis();
				try {
					GameUser gu = Database.userMap.get(playOrder);
					String showAccount = gu.getAccount();
					String[] tokenAccount = gu.getLoginToken().split("-");
					if (tokenAccount.length == 3) {
						showAccount = tokenAccount[2];
					}
					// ImageUtil.clearPhotoDialogImg();
					PhotoDialog photoDialog = new PhotoDialog(view, isMaster, DoudizhuMainGameActivity.this, showAccount, gu);
					if (null != photoDialog && !photoDialog.isShowing()) {
						photoDialog.show();
					}
					gu = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 屏幕点击事件
	 */
	public boolean onTouch(View view, MotionEvent event) {
		if (view.getId() == doudizhuBackGround.getId()) {
			if (userinfoshowView.getVisibility() == View.VISIBLE) {
				userinfoshowView.setVisibility(View.GONE);
				userInfoText.setText("");
				BitmapDrawable drawable = (BitmapDrawable) userinfoshowView.getBackground();
				drawable.getBitmap().recycle();
				drawable.setCallback(null);
			}
		}
		return this.gestureDetector.onTouchEvent(event);
	}

	public void recyleDrawable() {
		doudizhuLayout.removeAllViews();
	}

	public void cancelTimer() {
		if (gameTask != null) {
			gameTask.stop(true);
			gameTask = null;
			play1Timer.setVisibility(View.GONE);
			play2Timer.setVisibility(View.GONE);
			play3Timer.setVisibility(View.GONE);
		}
	}

	public void setTiShiCount() {
		tiShiCount++;
		tishi.setBackgroundResource(R.drawable.again_btn_bg);
	}

	public void initTiShiCount() {
		tiShiCount = -1;
		tishi.setBackgroundResource(R.drawable.tishi_btn_bg);
	}

	public int getTiShiCount() {
		return tiShiCount;
	}

	/**
	 * 展示聊天消息
	 * 
	 * @param mess
	 */
	public void showMessage(CmdDetail mess) {
		if (null == Database.userMap) {
			return;
		}
		if (play2Order.getText().toString().equals("") || play3Order.getText().toString().equals("")) {
			return;
		}
		int play2O = Integer.parseInt(play2Order.getText().toString());
		int play3O = Integer.parseInt(play3Order.getText().toString());
		for (int i = 1; i <= 3; i++) {
			GameUser gu = Database.userMap.get(i);
			if (gu.getLoginToken().endsWith(mess.getFromUserId()) || gu.getAccount().equals(mess.getFromUserId())) {
				TextView textView = new TextView(this);
				textView.setTextColor(android.graphics.Color.RED);
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				textView.setLayoutParams(params);
				if (i == play2O) {
					rightFrame.removeAllViews();
					if (mess.getType() == Constant.MESSAGE_TYPE_ZERO) {
						textView.setBackgroundResource(R.drawable.you_mess_view);
						textView.setGravity(Gravity.CENTER);
						rightFrame.addView(textView);
					} else if (mess.getType() == Constant.MESSAGE_TYPE_TWO) {} else if (mess.getType() == Constant.MESSAGE_TYPE_ONE) {
						textView.setBackgroundResource(R.drawable.you_mess_view);
						textView.setGravity(Gravity.CENTER);
						rightFrame.addView(textView);
					}
					if (mess.getType() == Constant.MESSAGE_TYPE_THREE) {
//						startTask(girlRightFrame, rightTask);
//						messageFrame(girlRightFrame, null, 0, mess, textView);
					} else {
						startTask(rightFrame, rightTask);
						messageFrame(rightFrame, null, 0, mess, textView);
					}
				} else if (i == play3O) {
					leftFrame.removeAllViews();
					if (mess.getType() == Constant.MESSAGE_TYPE_ZERO) {
						textView.setBackgroundResource(R.drawable.zuo_mess_view);
						textView.setGravity(Gravity.CENTER);
						leftFrame.addView(textView);
					} else if (mess.getType() == Constant.MESSAGE_TYPE_TWO) {} else if (mess.getType() == Constant.MESSAGE_TYPE_ONE) {
						textView.setBackgroundResource(R.drawable.zuo_mess_view);
						textView.setGravity(Gravity.CENTER);
						leftFrame.addView(textView);
					}
					if (mess.getType() == Constant.MESSAGE_TYPE_THREE) {
//						startTask(girlLeftFrame, leftTask);
//						messageFrame(girlLeftFrame, null, 0, mess, textView);
					} else {
						startTask(leftFrame, leftTask);
						messageFrame(leftFrame, null, 0, mess, textView);
					}
				}
			}
			gu = null;
		}
	}

	/**
	 * @param layout
	 * @param talk
	 *            不为空表示自己在说话
	 * @param messType
	 * @param mess
	 * @param textView
	 */
	public void messageFrame(final LinearLayout layout, String talk, int messType, CmdDetail mess, TextView... textView) {
		if (layout != null) {
			if (layout.getBackground() != null) {
				ImageUtil.releaseDrawable(layout.getBackground());
			}
			ImageUtil.clearGifCache();
			// layout.removeAllViews();
		}
		if (null == talk) {
			int type = mess.getType();
			if (type == Constant.MESSAGE_TYPE_ZERO || type == Constant.MESSAGE_TYPE_ONE) {
				if (layout.getId() == leftFrame.getId()) {// 文字聊天
					girlLeftFrame.setVisibility(View.GONE);
				}
				if (layout.getId() == rightFrame.getId()) {
					girlRightFrame.setVisibility(View.GONE);
				}
				if (textView.length > 0) {
					textView[0].setText(mess.getValue());
				}
			} else if (type == Constant.MESSAGE_TYPE_TWO) {// 表情动画
				if (layout.getId() == leftFrame.getId()) {
					girlLeftFrame.setVisibility(View.GONE);
				}
				if (layout.getId() == rightFrame.getId()) {
					girlRightFrame.setVisibility(View.GONE);
				}
				if (mess.getValue().contains("gif")) {
					String imageName = mess.getValue().substring(0, mess.getValue().lastIndexOf("."));
					if (ImageUtil.getGifDrawable(imageName).getParent() != null) {
						((ViewGroup) ImageUtil.getGifDrawable(imageName).getParent()).removeView(ImageUtil.getGifDrawable(imageName));
					}
					ImageUtil.getGifDrawable(imageName).showAnimation();
					// ImageUtil.getGifDrawable(imageName).setShowDimension(mst.adjustXIgnoreDensity(150),
					// mst.adjustYIgnoreDensity(150));
					layout.addView(ImageUtil.getGifDrawable(imageName), new ViewGroup.LayoutParams(mst.adjustXIgnoreDensity(150), mst.adjustYIgnoreDensity(150)));
				}
			}
		} else {
			if (messType == Constant.MESSAGE_TYPE_ZERO || messType == Constant.MESSAGE_TYPE_ONE) {
				TextView myView = new TextView(this);
				myView.setText(talk);
				myView.setTextColor(android.graphics.Color.RED);
				myView.setBackgroundResource(R.drawable.mine_mess_view);
				myView.setGravity(Gravity.CENTER);
				girlLeftFrame.setVisibility(View.GONE);
				layout.addView(myView);
			}
			else if (messType == Constant.MESSAGE_TYPE_TWO) {
				if (talk.contains("gif")) {
					talk = talk.substring(0, talk.lastIndexOf("."));
					if (ImageUtil.getGifDrawable(talk).getParent() != null) {
						((ViewGroup) ImageUtil.getGifDrawable(talk).getParent()).removeView(ImageUtil.getGifDrawable(talk));
					}
					girlLeftFrame.setVisibility(View.GONE);
					ImageUtil.getGifDrawable(talk).showAnimation();
					// ImageUtil.getGifDrawable(talk).setShowDimension(mst.adjustXIgnoreDensity(150),
					// mst.adjustYIgnoreDensity(150));
					layout.addView(ImageUtil.getGifDrawable(talk), new ViewGroup.LayoutParams(mst.adjustXIgnoreDensity(150), mst.adjustYIgnoreDensity(150)));
				}
			}
		}
		layout.setVisibility(View.VISIBLE);
	}

	public void startTask(final LinearLayout layout, AutoTask autoTask) {
		if (autoTask != null) {
			autoTask.stop(true);
			autoTask = null;
		}
		autoTask = new AutoTask() {

			public void run() {
				Database.currentActivity.runOnUiThread(new Runnable() {

					public void run() {
						layout.setVisibility(View.GONE);
					}
				});
			}
		};
		ScheduledTask.addDelayTask(autoTask, 3000);
	}

	/**
	 * 游戏指令回调
	 */
	private class GameCallBack implements ICallback {

		ChangeProInterface interfac;

		public void setInterface(ChangeProInterface interfac) {
			this.interfac = interfac;
		}

		@Override
		public synchronized void messageHandler(CmdDetail cmdDtl) {
			String cmd = cmdDtl.getCmd();
			String detail = cmdDtl.getDetail();
			Log.d("zhilishengji", "cmd:" + cmd + "-------detail:" + detail);
			if (CmdUtils.CMD_STARTREADY.equals(cmd)) {// 如果是准备的话)
				if (1 == Database.JOIN_ROOM.getRoomType()) {// 如果是超快赛
					if (hasEnd) {
						// 如果普通赛制的结束对话框弹出来了，
						// 则不马上回复准备，而是等对话框消失后回复准备
						hasCallReady = true;
						return;
					}
				}
				CmdUtils.ready();
				return;
			}
			if (CmdUtils.NO_SCOPE_BEAN.equals(cmd)) {//金豆超过
				Message message = new Message();
				String mess = cmdDtl.getMes();
				if (mess != null && !TextUtils.isEmpty(mess)) {
					message.what = 666;
					Bundle bundle = new Bundle();
					bundle.putString("tipsmess", mess);
					message.setData(bundle);
					handler.sendMessage(message);
				}
				return;
			}
			if (CmdUtils.CMD_START.equals(cmd)) {// 如果是发牌的话
				hideSlowTip();
				Message message = new Message();
				AudioPlayUtils.getInstance().playSound(R.raw.start);// 发牌
				// 创建一个代表JSON对象的Bean
				Play fapai = JsonHelper.fromJson(detail, Play.class); // jsonData是一个Json对象
				setFirstCard(fapai.getCards());
				GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
				if (cacheUser == null) {
					cacheUser = Database.userMap.get(fapai.getOrder());
					GameCache.putObj(CacheKey.GAME_USER, cacheUser);
				}
				mySelfId = fapai.getId();
				message.what = 0;
				Bundle bundle = new Bundle();
				bundle.putSerializable("fapai", fapai);
				message.setData(bundle);
				handler.sendMessage(message);
				return;
			}
			if (CmdUtils.CMD_PLAYING.equals(cmd)) {// 如果是打牌的話
				hideSlowTip();
				Message message = new Message();
				Play play = JsonHelper.fromJson(detail, Play.class); // jsonData是一个Json对象
				message.what = 3;
				Bundle bundle = new Bundle();
				bundle.putSerializable("play", play);
				message.setData(bundle);
				handler.sendMessage(message);
				return;
			}
			if (CmdUtils.CMD_END.equals(cmd)) {// 如果是打完牌的話)
				isTurnMySelf = true;
				if (1 == Database.JOIN_ROOM.getRoomType()) {// 如果是超快赛
					hasEnd = true;
				}
				Message message = new Message();
				LinkedList<Play> playResult = JsonHelper.fromJson(detail, new TypeToken<LinkedList<Play>>() {});
				message.what = 4;
				Bundle bundle = new Bundle();
				bundle.putSerializable("playResult", playResult);
				message.setData(bundle);
				handler.sendMessage(message);
				return;
			}
			if (CmdUtils.CMD_SYSMSG.equals(cmd)) {// 如果是上一局没有打完的话
				handler.sendEmptyMessage(7);
				return;
			}
			if (CmdUtils.CMD_USER.equals(cmd)) { // 用户信息
				Database.userMap = JsonHelper.fromJson(detail, new TypeToken<Map<Integer, GameUser>>() {}); // 用户的基本信息
				if (Database.userMap != null) {
					GameUser users = null;
					GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
					for (GameUser gu : Database.userMap.values()) {
						//玩家本人
						if (gu.getAccount().equals(cacheUser.getAccount())) {
							users = gu;
							/** 设置记牌器是否可用 **/
							if (users.getJiPaiQiTime() > 0)
								isJiPaiQiEnable = true;
							jiPaiQiTipsMsg = users.getTipMes();
							if (0 != users.getBean()) {
								cacheUser.setBean(users.getBean());
								GameCache.putObj(CacheKey.GAME_USER, cacheUser);
							}
							ActivityUtils.addSharedValue(RecordPorkerView.MSG_TIPS, jiPaiQiTipsMsg);
							Log.i("gameUserInfo", "" + users.getIqImg().toString());
							break;
						} else {
							jiPaiQiTipsMsg = ActivityUtils.getSharedValue(RecordPorkerView.MSG_TIPS);
						}
					}
					if (null != users) {
						final GameUser users2 = users;
						runOnUiThread(new Runnable() {

							public void run() {
								setUserRankInfo(users2);
							}
						});
					}
					users = null;
				}
				return;
			}
			if (CmdUtils.CMD_QUIT.equals(cmd)) { // 强制退出游戏回到房间列表
				handler.sendEmptyMessage(17);
				return;
			}
			if (CmdUtils.CMD_CHAT.equals(cmd)) { // 聊天
				Message message = new Message();
				CmdDetail mess = JsonHelper.fromJson(detail, CmdDetail.class);
				message.what = 18;
				Bundle bundle = new Bundle();
				bundle.putSerializable(CmdUtils.CMD_CHAT, mess);
				message.setData(bundle);
				handler.sendMessage(message);
				return;
			}
			if (CmdUtils.CMD_MES.equals(cmd)) { // 收到系统公告消息
				Message message = new Message();
				String mess = detail;
				if (mess != null && mess != "") {
					message.what = 19;
					Bundle bundle = new Bundle();
					bundle.putString("publicmess", mess);
					message.setData(bundle);
					handler.sendMessage(message);
				}
				return;
			}
			if (CmdUtils.CMD_ROUTING.equals(cmd)) { // 收到服务器返回的快速游戏房间信息
				String mess = detail;
				if (mess != null && mess != "") {
					Database.JOIN_ROOM = JsonHelper.fromJson(mess, new TypeToken<Room>() {});
					Database.JOIN_ROOM_RATIO = Database.JOIN_ROOM.getRatio();
					Database.JOIN_ROOM_BASEPOINT = Database.JOIN_ROOM.getBasePoint();
					Database.JOIN_ROOM_CODE = Database.JOIN_ROOM.getCode();
				}
				return;
			}
			if (CmdUtils.CMD_ERR_RJOIN.equals(cmd)) { // 加入游戏校验失败
				if (HttpRequest.NO_LOGIN.equals(detail) || HttpRequest.TOKEN_ILLEGAL.equals(detail)) { // 未登录,用户登录token非法
					DialogUtils.reLogin(Database.currentActivity);
				} else if (HttpRequest.NO_HOME.equals(detail)) { // 加入的房间不存在
					DialogUtils.mesTip(getString(R.string.no_join_home), true);
				}
				return;
			}
			
			if (CmdUtils.CMD_RANK.equals(cmd)) { // 排名（普通赛制）
				Log.i("paiming", "排名信息：" + cmd);
				final List<GameScoreTradeRank> gstList = JsonHelper.fromJson(detail, new TypeToken<ArrayList<GameScoreTradeRank>>() {});
				interfac.setRank(gstList);// 设置等待界面的排名
				runOnUiThread(new Runnable() {

					public void run() {
						if (null != mrDialog && mrDialog.isShowing()) {
							mrDialog.dismiss();
							mrDialog = null;
						}
						mrDialog = new MatchRankDialog(ctx, R.style.dialog, gstList);
						mrDialog.setContentView(R.layout.match_rank_dialog);
						android.view.WindowManager.LayoutParams lay = mrDialog.getWindow().getAttributes();
						setParams(lay);
						mrDialog.show();
						List<GameScoreTradeRank> gstLists = gstList;
						GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
						for (int i = 0, count = gstLists.size(); i < count; i++) {// 获取自己的排名和积分，用于刷新打牌界面的积分和排名
							GameScoreTradeRank gctRank = gstLists.get(i);
							if (cacheUser.getAccount().equals(gctRank.getAccount())) {
								cacheUser.setRank(Integer.parseInt(gctRank.getRank().trim()));
								cacheUser.setCred(Long.parseLong(gctRank.getScore().trim()));
								GameCache.putObj(CacheKey.GAME_USER, cacheUser);
							}
						}
						gpScore.setText("积分" + PatternUtils.changeZhidou(cacheUser.getCred()));
						gpRank.setText("第" + cacheUser.getRank() + "名");
					}
				});
				return;
			}
			if (CmdUtils.CMD_WAIT_GROUP.equals(cmd)) { // 桌等待( 还有多少桌在比赛)
				interfac.setPro(Integer.parseInt(detail));
				return;
			}
			if (CmdUtils.CMD_WAIT_USER.equals(cmd)) { // 用户等待( 还差多少人开赛)
				interfac.setPro(Integer.parseInt(detail));
				return;
			}
			if (CmdUtils.CMD_DIE_OUT.equals(cmd)) { // 淘汰用户
				overOrOut(detail);
				return;
			}
			if (CmdUtils.CMD_PLAY_OVER.equals(cmd)) { // 比赛结束
				overOrOut(detail);
				return;
			}
			// 其他玩家断线通知
			if (CmdUtils.CMD_SLOW.equals(cmd)) {
				if (CmdUtils.MY.equals(detail)) { // 自己的网络慢
					showNetSlowTip();
				} else {
					showSlowTip();
				}
				return;
			}
			// 其他玩家断线通知
			if (CmdUtils.CMD_BREAK.equals(cmd)) {
				final int breakOrder = Integer.parseInt(detail); // 当前断线玩家的位置编号
				runOnUiThread(new Runnable() {

					public void run() {
						if (breakOrder == getNextOrder(mySelfOrder)) { // 我的下家断线
							play2Icon.setImageDrawable(ImageUtil.getResDrawable(R.drawable.robot, true));
							play2IsTuoGuan = true;
						} else if (breakOrder == getPerOrder(mySelfOrder)) {// 我的上家断线
							play3Icon.setImageDrawable(ImageUtil.getResDrawable(R.drawable.robot, true));
							play3IsTuoGuan = true;
						}
					}
				});
				return;
			}
			// 重连命令
			if (CmdUtils.CMD_RLINK.equals(cmd)) {
				Log.e("CMD_RLINK", "重连命令");
				Message message = new Message();
				message.what = 200;
				Bundle bundle = new Bundle();
				bundle.putString("relink", detail);
				message.setData(bundle);
				handler.sendMessage(message);
				return;
			}
		}

		/**
		 * 被淘汰或比赛结束
		 * 
		 * @param detail
		 */
		private void overOrOut(String detail) {
			gameOverDetail = detail;
			Log.i("gameOverDetail", "赋值-----gameOverDetail:" + gameOverDetail);
			if (!hasEnd) {
				handler.sendEmptyMessage(22);
			}
		}
	}

	/**
	 * 设置玩家比赛场信息
	 * 
	 * @throws
	 */
	private void setUserRankInfo(GameUser gameUser) {
		if (gameUser.isPlay()) {// 是否为比赛场
			GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			if (0 != gameUser.getRound()) {// 第几轮不为空，则是快速赛
				cacheUser.setRound(gameUser.getRound());
				cacheUser.setLevel(gameUser.getLevel());
			} else {
				cacheUser.setRoomName(gameUser.getRoomName());
			}
			cacheUser.setRank(gameUser.getRank());
			cacheUser.setCred(gameUser.getCred());
			GameCache.putObj(CacheKey.GAME_USER, cacheUser);
			Log.d("detail", " 第" + gameUser.getRank() + "名，积分：" + gameUser.getCred());
			if (null != gameUser.getOverTime()) {
				countDownTime = gameUser.getOverTime();
			}
			if (null == gpRl) {
				gpRl = (LinearLayout) findViewById(R.id.doudizhu_gp_rl);
			}
			gpRl.setVisibility(View.VISIBLE);
			gpCount.setVisibility(View.VISIBLE);
			/**
			 * 复合赛制显示：积分/排名 普通赛制显示：积分/排名/类型/第几轮
			 **/
			if (0 != cacheUser.getRound()) {// 第几轮不为空，则是快速赛
				gpRound.setVisibility(View.VISIBLE);
				gpType.setVisibility(View.VISIBLE);
				Log.i("detail", "第" + cacheUser.getRound() + "轮");
				gpRound.setText("第" + cacheUser.getRound() + "轮");
				gpType.setText(ImageUtil.getGameType(cacheUser.getLevel()));
				gpCount.setText(Database.JOIN_ROOM.getName()); // 赛场标题
			} else {
				gpRound.setVisibility(View.GONE);
				gpType.setVisibility(View.GONE);
				Log.i("detail", "复合赛roomName:" + cacheUser.getRoomName());
				gpCount.setText(cacheUser.getRoomName()); // 赛场标题
			}
			gpScore.setText("积分" + PatternUtils.changeZhidou(cacheUser.getCred()));
			gpRank.setText("第" + cacheUser.getRank() + "名");
			if (0 != countDownTime) {
				countdownTv.setVisibility(View.VISIBLE);
				countdownTv.setText(ActivityUtils.getCountDown(countDownTime));
			}
		}
		gameUser = null;
	}

	/**
	 * 断网重连处理
	 * 
	 * @throws
	 */
	private void doReLink(String detail) {
		try {
			setTishiGone();
			newImageIsShow = true;
			play3IsTuoGuan = false;
			play2IsTuoGuan = false;
			// 重连数据为空，则重连失败
			if (TextUtils.isEmpty(detail)) {
				DialogUtils.mesTip("本局已结束,请退出后重新开始", false, true);
				return;
			}
			// 获取重连数据
			ReLink relink = JsonHelper.fromJson(detail, ReLink.class);
			if (relink.getTarget() == ReLink.TARGET_OTHER) {
				int relinkOrder = relink.getOrder();
				if (relinkOrder == getNextOrder(mySelfOrder)) { // 我的下家连线
					int p2o = getNextOrder(mySelfOrder);// p3o==relink.getMasterOrder()
					ActivityUtils.setHead(ctx, play2Icon, Database.userMap.get(p2o).getGender(), (p2o == masterOrder), Database.userMap.get(p2o).getIqImg(), false);// 设置头像
				} else if (relinkOrder == getPerOrder(mySelfOrder)) {// 我的上家连线
					int p3o = getPerOrder(mySelfOrder);
					GameUser mGameUser3 = Database.userMap.get(p3o);
					ActivityUtils.setHead(ctx, play3Icon, mGameUser3.getGender(), (p3o == masterOrder), mGameUser3.getIqImg(), false);// 设置头像
				}
				return;
			}
			showPopWindow(false);
			ClientCmdMgr.setClientStatus(Client.PLAYING);
			ClientCmdMgr.resetRelinkCount();
			// ////////////////////////初始化基础数据///////////////
			Database.GAME_SERVER = relink.getGameServer(); // 游戏服务器
			Database.JOIN_ROOM = relink.getRoom();
			Database.JOIN_ROOM_CODE = Database.JOIN_ROOM.getCode();
			Database.JOIN_ROOM_RATIO = relink.getRoom().getRatio();
			Database.JOIN_ROOM_BASEPOINT = Database.JOIN_ROOM.getBasePoint();
			beishuNumber = String.valueOf(relink.getRatio());
			mySelfId = ActivityUtils.getAndroidId();
			List<Integer> myCardList = relink.getMyCardList();
			pai = new int[myCardList.size()];
			for (int i = 0; i < myCardList.size(); i++) {
				pai[i] = myCardList.get(i);
			}
			cancelTimer(); // 取消定时
			netSlowTip.setVisibility(View.GONE); // 隐藏网络慢提示
			DialogUtils.mesToastTip("您当前游戏已恢复");
			cancelTuoGuanState();
			doudizhuLayout.removeView(gameWaitLayout); // 去掉等待页面
			gameWaitLayout.closeTimer();
			visibleOrGoneRankBtn();
			setOrder(relink.getOrder()); // 设置位置
			nowcard.clear();// 清除自己手中的牌
			addCard(pai);
			mySelfOrder = relink.getOrder();
			if ((6 == Database.JOIN_ROOM.getHomeType() && quang < allQuan) || (6 != Database.JOIN_ROOM.getHomeType() && quang < 5)) {
				handler.sendEmptyMessage(302);
			}
			// 用户
			Map<Integer, ReLinkUser> userMap = relink.getUserInfo();
			String nickName = "";
			// 自己的信息
			ReLinkUser tempUser = userMap.get(mySelfOrder);
			setUserRankInfo(tempUser.getGameUser());
			Database.userMap.put(tempUser.getOrder(), tempUser.getGameUser());
			nickName = tempUser.getGameUser().getNickname();
			playTextView1.setText((nickName != null) ? nickName : tempUser.getGameUser().getAccount()); // 自己
			ActivityUtils.setHead(ctx, play1Icon, Database.userMap.get(mySelfOrder).getGender().trim(), (mySelfOrder == relink.getMasterOrder()), Database.userMap.get(mySelfOrder).getIqImg(), false);// 设置头像
			Log.i("gameUserInfo", "断网重连：" + Database.userMap.get(mySelfOrder).getIqImg().toString());
			play1Order.setText(String.valueOf(mySelfOrder));
			iqTv1.setText("" + Database.userMap.get(mySelfOrder).getIq());
			Map<String, String> dpMap = tempUser.getGameUser().getLevelImg();
			try {
				for (Entry<String, String> entry : dpMap.entrySet()) {
					final int count = TextUtils.isEmpty(entry.getKey()) ? 0 : Integer.parseInt(entry.getKey());
					String path = entry.getValue();// 图片链接
					if (!TextUtils.isEmpty(path)) {
						dpRl1.removeAllViews();
						ImageUtil.setImg(HttpURL.URL_PIC_ALL + path, null, new ImageCallback() {

							public void imageLoaded(Bitmap bitmap, ImageView view) {
								if (null != bitmap) {
									for (int i = 0; i < count; i++) {
										ImageView img = new ImageView(ctx);
										img.setBackgroundDrawable(new BitmapDrawable(bitmap));
										RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mst.adjustXIgnoreDensity(DP_WIDTH), mst.adjustYIgnoreDensity(DP_HEIGHT));
										if (i < 3) {
											params.leftMargin = mst.adjustXIgnoreDensity(DP_WIDTH * i);
										} else {
											params.topMargin = mst.adjustYIgnoreDensity(DP_HEIGHT / 2 + DP_PANDING);
											params.leftMargin = mst.adjustXIgnoreDensity(DP_WIDTH / 2 + DP_WIDTH * (i - 3));
										}
										img.setLayoutParams(params);
										dpRl1.addView(img);
									}
								}
							}
						});
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			int p2o = getNextOrder(mySelfOrder);
			tempUser = userMap.get(p2o);
			Database.userMap.put(tempUser.getOrder(), tempUser.getGameUser());
			nickName = tempUser.getGameUser().getNickname();
			playTextView2.setText(!TextUtils.isEmpty(nickName) ? nickName : tempUser.getGameUser().getAccount()); // 下家
			ActivityUtils.setHead(ctx, play2Icon, Database.userMap.get(p2o).getGender(), (p2o == relink.getMasterOrder()), Database.userMap.get(p2o).getIqImg(), false);// 设置头像
			play2Order.setText(String.valueOf(p2o));
			if (tempUser.getIsAuto() == 1) {
				play2Icon.setImageDrawable(ImageUtil.getResDrawable(R.drawable.robot, true));
				play2IsTuoGuan = true;
			}
			iqTv2.setText("" + Database.userMap.get(p2o).getIq());
			Map<String, String> dpMap2 = tempUser.getGameUser().getLevelImg();
			try {
				for (Entry<String, String> entry : dpMap2.entrySet()) {
					final int count = TextUtils.isEmpty(entry.getKey()) ? 0 : Integer.parseInt(entry.getKey());
					String path = entry.getValue();// 图片链接
					if (!TextUtils.isEmpty(path)) {
						dpRl2.removeAllViews();
						ImageUtil.setImg(HttpURL.URL_PIC_ALL + path, null, new ImageCallback() {

							public void imageLoaded(Bitmap bitmap, ImageView view) {
								if (null != bitmap) {
									for (int i = 0; i < count; i++) {
										ImageView img = new ImageView(ctx);
										img.setBackgroundDrawable(new BitmapDrawable(bitmap));
										RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mst.adjustXIgnoreDensity(DP_WIDTH), mst.adjustYIgnoreDensity(DP_HEIGHT));
										if (i < 3) {
											params.leftMargin = mst.adjustXIgnoreDensity(DP_WIDTH * i);
										} else {
											params.topMargin = mst.adjustYIgnoreDensity(DP_HEIGHT / 2 + DP_PANDING);
											params.leftMargin = mst.adjustXIgnoreDensity(DP_WIDTH / 2 + DP_WIDTH * (i - 3));
										}
										img.setLayoutParams(params);
										dpRl2.addView(img);
									}
								}
							}
						});
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			int p3o = getNextOrder(p2o);
			tempUser = userMap.get(p3o);
			Database.userMap.put(tempUser.getOrder(), tempUser.getGameUser());
			nickName = tempUser.getGameUser().getNickname();
			playTextView3.setText((nickName != null) ? nickName : tempUser.getGameUser().getAccount()); // 上家
			ActivityUtils.setHead(ctx, play3Icon, Database.userMap.get(p3o).getGender(), (p3o == relink.getMasterOrder()), Database.userMap.get(p3o).getIqImg(), false);// 设置头像
			play3Order.setText(String.valueOf(p3o));
			Map<String, String> dpMap3 = tempUser.getGameUser().getLevelImg();
			try {
				for (Entry<String, String> entry : dpMap3.entrySet()) {
					final int count = TextUtils.isEmpty(entry.getKey()) ? 0 : Integer.parseInt(entry.getKey());
					String path = entry.getValue();// 图片链接
					if (!TextUtils.isEmpty(path)) {
						dpRl3.removeAllViews();
						ImageUtil.setImg(HttpURL.URL_PIC_ALL + path, null, new ImageCallback() {

							public void imageLoaded(Bitmap bitmap, ImageView view) {
								if (null != bitmap) {
									for (int i = 0; i < count; i++) {
										ImageView img = new ImageView(ctx);
										img.setImageBitmap(bitmap);
										RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mst.adjustXIgnoreDensity(DP_WIDTH), mst.adjustYIgnoreDensity(DP_HEIGHT));
										if (i < 3) {
											params.leftMargin = mst.adjustXIgnoreDensity(DP_WIDTH * i);
										} else {
											params.topMargin = mst.adjustYIgnoreDensity(DP_HEIGHT / 2 + DP_PANDING);
											params.leftMargin = mst.adjustXIgnoreDensity(DP_WIDTH / 2 + DP_WIDTH * (i - 3));
										}
										img.setLayoutParams(params);
										dpRl3.addView(img);
									}
								}
							}
						});
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (tempUser.getIsAuto() == 1) {
				play3Icon.setImageDrawable(ImageUtil.getResDrawable(R.drawable.robot, true));
				play3IsTuoGuan = true;
			}
			iqTv3.setText("" + Database.userMap.get(p3o).getIq());
			cleanAllChuPaiInfo();
			// 托管时的牌可按
			for (int i = 0; i < myCardsTouchLayout.getChildCount(); i++) {
				myCardsTouchLayout.getChildAt(i).setClickable(false);
			}
			// 设置其他玩家牌数量
			for (Integer uo : userMap.keySet()) {
				setShengxiaPai(userMap.get(uo).getCardCount(), uo);
			}
			int relStatus = relink.getStatus(); // 当前服务器状态
			int myDo = relink.getIsMyDo();
			boolean isMyDo = (myDo == 1) ? true : false; // 是否轮到我操作
			hiddenPlayBtn(); // 隐藏出牌按钮

			if (relStatus == Constant.STATUS_PLAYING) { // 正在打牌时重连
				masterOrder = relink.getMasterOrder();
				selfIsMove = true;
				nullTv2.setVisibility(View.GONE);
				nullTv.setVisibility(View.VISIBLE);
				try {
					String masterCards = relink.getMasterCard();
					String[] dzCard = masterCards.substring(1, masterCards.length() - 1).split(",");
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (isMyDo) { // 轮到自己打牌
					play1PassLayout.removeAllViews();
					zhezhao1.setVisibility(View.GONE);
					if (bierenchupai != null) {
						showPlayBtn(false);
					} else {
						showPlayBtn(true);
					}
				}
				// 前2手或一手出牌记录显示
				showHistoryPlay(relink);
				if (isMyDo) { // 轮到自己打牌
					isTurnMySelf = true;
					stopTimer(0);
					setTuoGuan();
				} else {
					isTurnMySelf = false;
					if (getNextOrder(masterOrder) == mySelfOrder) { // 上家
						stopTimer(-1);
					} else {
						stopTimer(1);
					}
					// 最后开启别人一个打牌的定时器
					startPlayTimer(relink.getNextPlayOrder() + 1200);
					ImageView img = (ImageView) findViewById(relink.getNextPlayOrder() + 1400);
					img.setVisibility(View.GONE);
				}
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示或隐藏”排名“按钮
	 */
	private void visibleOrGoneRankBtn() {
		// 如果是比赛场并且不是第一场，则显示”排名“按钮
		if (1 == Database.JOIN_ROOM.getRoomType()) {// 普通赛制
			rankTop.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 显示重连前2手牌
	 * 
	 * @throws
	 */
	public void showHistoryPlay(ReLink relink) {
		List<String> playCardList = relink.getPlayCardList();
		int playCardSize = playCardList.size();
		if (playCardSize > 0) { // 别人有出牌
			for (int i = playCardSize - 1; i >= 0; i--) {
				int tmpOrder = 0;
				if (i == 1) {// 下家出的
					tmpOrder = getNextOrder(relink.getNextPlayOrder());
				} else if (i == 0) { // 上家
					tmpOrder = getPerOrder(relink.getNextPlayOrder());
				}
				if (tmpOrder == mySelfOrder) { // 自己出的牌
					List<Integer> pcardList = JsonHelper.fromJson(playCardList.get(i), new TypeToken<List<Integer>>() {});
					if (pcardList.size() == 0) { // 自己不出
						ImageView im = new ImageView(ctx);
						im.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.play_buchu, true));
						play1PassLayout.removeAllViews();
						play1PassLayout.addView(im, mst.getAdjustLayoutParamsForImageView(im));
						ActivityUtils.startScaleAnim(play1PassLayout, ctx);// 播放缩放动画
					} else {
						// 将出的牌转为Poker对象
						int[] passcard = new int[pcardList.size()];
						for (int j = 0; j < pcardList.size(); j++) {
							passcard[j] = pcardList.get(j);
						}
						List<Poker> passPokerList = new ArrayList<Poker>();
						passcard = DoudizhuRule.sort(passcard, poker);
						for (int j = 0; j < passcard.length; j++) {
							Poker pk = poker[passcard[j]];
							passPokerList.add(pk);
						}
						// 展示出的牌
						for (int j = 0; j < passPokerList.size(); j++) {
							RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(mst.adjustXIgnoreDensity(50), mst.adjustYIgnoreDensity(68));
							Poker image = new Poker(this);
							image.getPokeImage().setImageDrawable(ImageUtil.getResDrawable(passPokerList.get(j).getBitpamResID(), true));
							params3.leftMargin = mst.adjustXIgnoreDensity(20 * j);
							play1PassLayout.addView(image, params3);// 显示出来
							// 清除图片
							ImageUtil.releaseDrawable(passPokerList.get(j).getPokeImage().getDrawable());
						}
					}
					hiddenPlayBtn();
				} else {
					tmpOrder = getNextOrder(tmpOrder); // 处理掉playCard中的getPerOrder
					Play p = new Play();
					p.setCards(playCardList.get(i));
					p.setCount(relink.getUserInfo().get(tmpOrder).getCardCount()); // 剩余牌
					p.setNextOrder(tmpOrder);
					playCard(p, true);
				}
			}
		}
	}

	/**
	 * 加入房间前校验线程任务
	 */
	private class CheckJoinTask extends GenericTask {

		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				TaskParams param = null;
				if (params.length <= 0) {
					return TaskResult.FAILED;
				}
				param = params[0];
				Database.JOIN_ROOM_CODE = param.getString("homeCode");
				GameCallBack gameCallBack = new GameCallBack();
				gameCallBack.setInterface(gameWaitLayout);
				if (ClientCmdMgr.startClient(gameCallBack)) {
					startJoinGame();
					return TaskResult.OK;
				} else {
					if (ClientCmdMgr.startClient(gameCallBack)) {
						startJoinGame();
						return TaskResult.OK;
					} else {
//						DialogUtils.mesTip(getString(R.string.link_server_fail), false);
						DialogUtils.netSlowTip();
						return TaskResult.FAILED;
					}
				}
			} catch (Exception e) {}
			return TaskResult.FAILED;
		}
	}

	private void startJoinGame() {
		GameUser cacheuser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		ClientCmdMgr.setClientStatus(Client.STARTING);
		if (cacheuser == null && type == Constant.FASTJOIN_TYPE) {
			CmdUtils.sendFastJoinRoomCmd();
		} else if (0 == cacheuser.getRound()) {
			if (type == Constant.FASTJOIN_TYPE) {
				CmdUtils.sendFastJoinRoomCmd();
			} else {
				// 发送加入游戏命令
				CmdUtils.sendJoinRoomCmd(cacheuser.getLoginToken(), String.valueOf(Database.JOIN_ROOM_CODE));
			}
		}
	}

	public void showPubMess(String mess) {
		if (Database.userMap == null) {
			return;
		} else if (mess != null && !mess.equals("")) {
			publicLayout.removeAllViews();
			publicLayout.setVisibility(View.VISIBLE);
			marqueeText.currentScrollX = 0;
			marqueeText.setText(mess);
			marqueeText.stopScroll();
			marqueeText.startScroll();
			marqueeText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
			marqueeText.setSingleLine(true);
			marqueeText.setFocusable(true);
			int messLong = mess.length();
			int timeValue = messLong / 2;
		
			publicLayout.addView(marqueeText);
			pubTimer(publicLayout, pubTask, timeValue);
		}
	}

	public void pubTimer(final LinearLayout layout, AutoTask autoTask, int value) {
		if (autoTask != null) {
			autoTask.stop(true);
			autoTask = null;
		}
		autoTask = new AutoTask() {

			public void run() {
				Database.currentActivity.runOnUiThread(new Runnable() {

					public void run() {
						layout.setVisibility(View.GONE);
					}
				});
			}
		};
		int timelong = value * 1000;
		ScheduledTask.addDelayTask(autoTask, timelong);
	}

	private class BitmapVO {
		private Bitmap image;

		public Bitmap getImage() {
			return image;
		}
	}

	@Override
	public boolean hasTiShi(Poker mPoker, int indexs) {
		boolean isTiShi = false; // 是否提示对应牌型
		boolean check = false; // 点击的牌是否弹出状态
		A2:
		for (int i = 0, count = nowcard.size(); i < count; i++) {
			if (nowcard.get(i).ischeck && (mPoker.getValue() == nowcard.get(i).getValue())) {
				check = true;
				break A2;
			}
		}
		// 如果上家有出过牌并且不是单牌，并且我点击的牌处于没弹出状态
		if (bierenchupai != null && 1 != bierenchupai.length && !check) {
			checkOtherChupai(bierenchupai);
			DouDiZhuData data = new DouDiZhuData(nowcard);
			DouDiZhuData datas = new DouDiZhuData(nowcard);
			data.fillPokerList();
			List<List<Poker>> tishiList = data.getTiShi(otherplay1);
			checkOtherChupai(bierenchupai);
			datas.fillAllPokerList();
			List<List<Poker>> tishiList2 = datas.getTiShi(otherplay1);
			HintPokerUtil aList = new HintPokerUtil();
			if (tishiList != null && tishiList2 != null) {
				tishiList = aList.filterHintPoker(tishiList, tishiList2);
			}
			if (tishiList != null && tishiList.size() > 0) {
				int index = -1;
				A1:
				for (int i = 0, count = tishiList.size(); i < count; i++) {
					for (int j = 0, count2 = tishiList.get(i).size(); j < count2; j++) {
						if (tishiList.get(i).get(j).getValue() == mPoker.getValue()) {
							index = i;
							break A1;
						}
					}
				}
				if (index != -1) {
					for (int i = 0; i < nowcard.size(); i++) {
						poker[nowcard.get(i).getNumber()].params.topMargin = mst.adjustYIgnoreDensity(20);
						poker[nowcard.get(i).getNumber()].setLayoutParams(poker[nowcard.get(i).getNumber()].params);
						poker[nowcard.get(i).getNumber()].ischeck = false;
					}
					isTiShi = true;
					List<Poker> tiShiPoker = tishiList.get(index);
					for (int i = 0; i < tiShiPoker.size(); i++) {
						poker[tiShiPoker.get(i).getNumber()].params.topMargin = 0;
						poker[tiShiPoker.get(i).getNumber()].setLayoutParams(poker[tiShiPoker.get(i).getNumber()].params);
						poker[tiShiPoker.get(i).getNumber()].ischeck = true;
					}
				}
			}
		}
		return isTiShi;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {}

	private long startTap = System.currentTimeMillis();
	private long endTap = 0;

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// 手势区域在上面头像以下区域
		if (e.getRawY() > play3Icon.getHeight()) {
			// 手势开启
			if (PreferenceHelper.getMyPreference().getSetting().getBoolean("shoushi", true)) {
				// endTap = 0 表示是一个新的请求
				if (0 == endTap) {
					endTap = System.currentTimeMillis();
					long tapTime = endTap - startTap;
					/** 收集两次点击的时间来区分单击与双击 **/
					if (tapTime > 0 && tapTime < 200) {
						if (playBtnLayout.getVisibility() == View.VISIBLE) {// 双击取消选牌
							for (int i = 0; i < nowcard.size(); i++) {
								poker[nowcard.get(i).getNumber()].params.topMargin = mst.adjustXIgnoreDensity(20);
								poker[nowcard.get(i).getNumber()].setLayoutParams(poker[nowcard.get(i).getNumber()].params);
								poker[nowcard.get(i).getNumber()].ischeck = false;
							}
							startTap = System.currentTimeMillis();
							endTap = 0;
							mainGameGuideVI.setDoublePointGone(true);
							return true;
						}
					} else {
						// 单击提示牌型
						if (playBtnLayout.getVisibility() == View.VISIBLE) {
							if (mainGameGuideVI.isPoint()) {
								mainGameGuideVI.setPointGone(true);
							}
							setTishi();
							startTap = System.currentTimeMillis();
							endTap = 0;
							return true;
						}
					}
				}
				startTap = System.currentTimeMillis();
				endTap = 0;
			}
		}
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		int length = mst.adjustYIgnoreDensity(80);
		// 手势引导是否打开,打开才可以出发手势
		if (PreferenceHelper.getMyPreference().getSetting().getBoolean("shoushi", true)) {
			if (null == e1 || null == e2) {
				return true;
			}
			if ((e1.getY() - e2.getY() < -length)) {// 向下滑动
				// 手势提示 出牌 不出牌
				if (playBtnLayout.getVisibility() == View.VISIBLE && buchu.getVisibility() != View.GONE) {
					if (mainGameGuideVI.isArrowIsDown()) {// 当前提示是向下滑动的提示
						mainGameGuideVI.setArrowDownGone(true);
					} else {// 若不是
						if (mainGameGuideVI.isArrowIsUp()) {
							mainGameGuideVI.setArrowUpGone(false);
						}
						if (mainGameGuideVI.isPoint()) {
							mainGameGuideVI.setPointGone(false);
						}
						if (mainGameGuideVI.isDoublePoint()) {
							mainGameGuideVI.setDoublePointGone(false);
						}
					}
					passCard();
					return true;
				}
			} else if (e1.getY() - e2.getY() > mst.adjustYIgnoreDensity(40)) {// 向上滑动
				// 手势提示 出牌 不出牌
				if (playBtnLayout.getVisibility() == View.VISIBLE) {
					if (mainGameGuideVI.isArrowIsUp()) {
						mainGameGuideVI.setArrowUpGone(true);
					} else {
						if (mainGameGuideVI.isArrowIsDown()) {
							mainGameGuideVI.setArrowDownGone(false);
						}
						if (mainGameGuideVI.isPoint()) {
							mainGameGuideVI.setPointGone(false);
						}
						if (mainGameGuideVI.isDoublePoint()) {
							mainGameGuideVI.setDoublePointGone(false);
						}
					}
					TextView myTime = (TextView) findViewById(R.id.play1Time);
					if (null != myTime) {
						int time = Integer.parseInt(myTime.getText().toString());
						if (time > 0 && time != 20) {
							playCard(false);
						}
					}
					return true;
				}
			}
		}
		// 对手指滑动的距离进行了计算，如果滑动距离大于120像素，就做切换动作，否则不做任何切换动作。
		// 从左向右滑动jiaofenLayout.getVisibility()==View.VISIBLE
		if (girls != null && girls.size() > 0) {
			if (e1.getX() - e2.getX() > length) {
				// 添加动画
				if (canFlipper) {
					canFlipper = false;
					this.viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
					this.viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
					this.viewFlipper.showNext();
					curPage = curPage + 1;
					if (curPage >= girls.size()) {
						curPage = 0;
					}
					readdView();
				}
				mainGameGuideVI.setArrowLeftRightGone(true);
				return true;
			}// 从右向左滑动
			else if (e1.getX() - e2.getX() < -length) {
				if (canFlipper) {
					canFlipper = false;
					this.viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
					this.viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
					this.viewFlipper.showPrevious();
					curPage = curPage - 1;
					if (curPage < 0) {
						curPage = girls.size() - 1;
					}
					readdView();
				}
				mainGameGuideVI.setArrowLeftRightGone(true);
				return true;
			}
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return this.gestureDetector.onTouchEvent(ev);
	}

	@Override
	public void InitMainGame() {
		startAgain(true);
	}

	/**
	 * 展示网络缓存提示信息
	 */
	private void showSlowTip() {
		runOnUiThread(new Runnable() {

			public void run() {
				if (networkSlowtip != null) {
					networkSlowtip.setText("对方网络缓慢 ,请稍候 ...");
					networkSlowtip.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	/**
	 * 隐藏网络缓存提示信息
	 */
	private void hideSlowTip() {
		// handler.sendEmptyMessage(400);
		runOnUiThread(new Runnable() {

			public void run() {
				if (networkSlowtip != null) {
					networkSlowtip.setVisibility(View.GONE);
				}
			}
		});
	}

	/**
	 * 隐藏出牌按钮
	 * 
	 * @Title: hiddenPlayBtn
	 * @param
	 * @return void
	 * @throws
	 */
	private void hiddenPlayBtn() {
		chupai.setVisibility(View.GONE);
		tishi.setVisibility(View.GONE);
		buchu.setVisibility(View.GONE);
		if (null != playBtnLayout) {
			playBtnLayout.setVisibility(View.GONE); // 隐藏出牌按钮
		}
	}

	/**
	 * 展示出牌提示按钮
	 * 
	 * @param startPlay
	 *            是否自己带头出牌
	 */
	private void showPlayBtn(boolean startPlay) {
		playBtnLayout.setVisibility(View.VISIBLE); // 显示出牌按钮
		chupai.setVisibility(View.VISIBLE);
		tishi.setVisibility(View.VISIBLE);
		if (tuoGuanLayout.getVisibility() != View.VISIBLE) {
			mainGameGuideVI.setPointVisible();
		}
		if (startPlay) {
			buchu.setVisibility(View.GONE);
		} else {
			buchu.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 显示网络缓存提示消息
	 * 
	 * @Title: showNetSlowTIp
	 * @param
	 * @return void
	 * @throws
	 */
	private void showNetSlowTip() {
		runOnUiThread(new Runnable() {

			public void run() {
				netSlowTip.setVisibility(View.VISIBLE);
				hiddenPlayBtn();
			}
		});
	}

	/**
	 * 初始化美女道具栏
	 * */
	private class GoodsValuesAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<GamePropsType> list;

		public GoodsValuesAdapter(List<GamePropsType> toolList) {
			this.list = toolList;
			this.mInflater = LayoutInflater.from(ctx);
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.girl_gif_item, null);
			RelativeLayout la = (RelativeLayout) convertView.findViewById(R.id.mainview);
			mst.adjustView(la);
			final ImageView iv = (ImageView) convertView.findViewById(R.id.goodsview);
			// iv.setBackgroundDrawable(ImageUtil.getResDrawable(gifInt[position]));
			if (list.get(position).getType().equals("1")) {
				ImageUtil.setImg(HttpURL.URL_PIC_ALL + list.get(position).getPicPath(), iv, new ImageCallback() {

					public void imageLoaded(final Bitmap bitmap, final ImageView view) {
						// view.setImageBitmap(bitmap);
						BitmapDrawable bd = new BitmapDrawable(bitmap);
						view.setBackgroundDrawable(bd);
					}
				});
			} else if (list.get(position).getType().equals("-1")) {
				iv.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.game_items_pic, true));
			}
			return convertView;
		}
	}

	/**
	 * 重新加载ViewFiper前面一个和后面一个imageview的背景
	 * */
	private void readdView() {
		ScheduledTask.addDelayTask(new AutoTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						canFlipper = true;
						int curId = DoudizhuMainGameActivity.this.viewFlipper.getDisplayedChild();
						int point = curPage - 1;
						int postion = curPage + 1;
						int curpoint = curId - 1;
						int curpostion = curId + 1;
						if (point < 0) {
							point = girls.size() - 1;
						}
						if (postion > girls.size() - 1) {
							postion = 0;
						}
						if (curpoint < 0) {
							curpoint = 2;
						}
						if (curpostion > 2) {
							curpostion = 0;
						}
						Drawable draw = ImageUtil.getcutBitmap(HttpURL.URL_PIC_ALL + girls.get(point).get("path"), false);
						if (null != draw) {
							DoudizhuMainGameActivity.this.viewFlipper.getChildAt(curpoint).setBackgroundDrawable(draw);
						}

						Drawable draw1 = ImageUtil.getcutBitmap(HttpURL.URL_PIC_ALL + girls.get(postion).get("path"), false);
						if (null != draw1) {
							DoudizhuMainGameActivity.this.viewFlipper.getChildAt(curpostion).setBackgroundDrawable(draw1);
						}
						Log.d("forTag", " curId : " + curId);
						for (int i = 0; i < girls.size(); i++) {// 释放没有调用的bitmap
							if (i == point || i == curPage || i == postion) {} else {
								ImageUtil.clearsingleCache(HttpURL.URL_PIC_ALL + girls.get(i).get("path"));
							}
						}
					}
				});
			}
		}, 700);
	};

	/**
	 * 此广播用于接收通知刷新系统时间的显示
	 * 
	 * @author Administrator
	 */
	private class MyBroadcastReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Constant.SYSTEM_TIME_CHANGE_ACTION)) {
				if (null != handler) {
					handler.sendEmptyMessage(12);
				}
			}
		}
	}

	/**
	 * 新美女图鉴提示标签显示
	 */
	public void setImageNewVisible(int size) {
		int count = PreferenceHelper.getMyPreference().getSetting().getInt("newImage", 0);
		if (count < size) {
			imageNewIv.setVisibility(View.VISIBLE);
			AnimUtils.playAnim(imageNewIv, ImageUtil.getResAnimaSoft("new"), 0);
		}
	}

	/**
	 * 新美女图鉴提示标签隐藏
	 */
	public void setImageNewGone(int count) {
		PreferenceHelper.getMyPreference().getEditor().putInt("newImage", count).commit();
		imageNewIv.setVisibility(View.GONE);
	}

	/**
	 * 设置动画
	 * 
	 * @param view
	 * @param animId
	 */
	private void setTweenAnim(View view, int animId, final int type) {
		Animation anim = AnimationUtils.loadAnimation(ctx, animId);
		view.startAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				switch (type) {
					case IS_FEIJI_ANIM:
						feijiImageView.setVisibility(View.INVISIBLE);
						break;
					case IS_WANGZHA_ANIM:
						wangzhaImageView.setVisibility(View.INVISIBLE);
						break;
					case IS_ZHADAN_ANIM:
						zhadanIv.setVisibility(View.GONE);
						zhadanImageView.setVisibility(View.VISIBLE);
						AnimUtils.playAnim(zhadanImageView, ImageUtil.getResAnimaSoft("bomb"), 1500);
						break;
					default:
						break;
				}
			}
		});
	}

	boolean isAdd = false;
	boolean isChose = false;// 是否弹起

	@Override
	public void onScrollListenner(int e1x, int e1y, int e2x, int e2y, int startIndex) {
	}

	@Override
	public void onTouchUpListenner(float x, float y, int startIndex) {
	}

	@Override
	public void onFling() {
		if ((playBtnLayout.getVisibility() == View.VISIBLE)) {
			setTishiGone();
			TextView myTime = (TextView) findViewById(R.id.play1Time);
			if (null != myTime) {
				int time = Integer.parseInt(myTime.getText().toString());
				if (time > 0 && time != 20) {
					playCard(true);
				}
			}
			int count = myCardsTouchLayout.getChildCount();
			for (int i = 0; i < count; i++) {
				((Poker) myCardsTouchLayout.getChildAt(i)).setDefaultParams2();
			}
		} else {
			myCardsTouchLayout.chekCard();
		}
	}

	/**
	 * 获取系统电量
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyBatteryReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) { // 重写onReceiver方法
			try {
				if (null != intent) {
					int current = intent.getExtras().getInt("level"); // 获得当前电量
					int total = intent.getExtras().getInt("scale"); // 获得总电量
					int percent = current * 100 / total; // 计算百分比
					int level = 1;
					if (percent >= 80) {
						level = 5;
					} else if (percent >= 60) {
						level = 4;
					} else if (percent >= 40) {
						level = 3;
					} else if (percent >= 20) {
						level = 2;
					} else {
						level = 1;
					}
					systemPower.setImageLevel(level);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}