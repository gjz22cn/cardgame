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
import com.lordcard.prerecharge.PrerechargeManager;
import com.lordcard.prerecharge.PrerechargeManager.PrerechargeDialogType;
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
import com.lordcard.ui.view.dialog.JiPaiQiChargeDialog;
import com.lordcard.ui.view.dialog.MatchRankDialog;
import com.lordcard.ui.view.dialog.PhotoDialog;
import com.lordcard.ui.view.dialog.SettingDialog;
import com.lordcard.ui.view.dialog.TipsDialog;
import com.lordcard.ui.view.notification.NotificationService;
import com.sdk.jd.sms.util.JDSMSPayUtil;
import com.sdk.util.PaySite;
import com.sdk.util.PayTipUtils;
import com.sdk.util.RechargeUtils;
import com.umeng.analytics.MobclickAgent;

@SuppressLint({ "HandlerLeak", "UseSparseArrays" })
public class DoudizhuMainGameActivity extends BaseActivity implements IGameView, OnTouchListener, HasTiShiListenner, OnGestureListener, InitMainGameInterface {

	/** 动画-无结束监听 */
	public static final int IS_NONE = 11100;
	/** 动画-头像移动 */
	public static final int IS_HEAD_ANIM = 11101;
	/** 动画-飞机 */
	public static final int IS_FEIJI_ANIM = 11102;
	/** 动画-王炸 */
	public static final int IS_WANGZHA_ANIM = 11103;
	/** 动画-宝箱移动 */
	public static final int IS_BAOXIANG_ANIM = 11104;
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
	private LinearLayout playBtnLayout, jiaofenLayout;
	private RelativeLayout tuoGuanLayout;
	private RelativeLayout mySelfHeadRl;// 自己头像布局
	private TextView nullTv, nullTv2;// 做布局撑自己头像布局用的
	private int[] pai = null;
	private Button chupai, tishi, buchu = null;
	private AutoTask selfTask, leftTask, rightTask, pubTask, adTask, gameTask, task2, baoXiangTask, headTask, prerechargeTask;
	private MarqueeText marqueeText;
	private Button bujiao, fen1, fen2, fen3 = null;
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
	// private String waitTime = "20";
	private TextView beishuNumView = null;
	private TextView dishu = null; // 底数
	private String beishuNumber = null;
	private RelativeLayout.LayoutParams adWidgetLayoutParam = null;
	private SettingDialog settingDialog = null;
	private ImageButton gameRobot, gameSet, tuoGuan, gamePay;
	// 玩家名称
	private TextView playTextView1, playTextView3, playTextView2 = null;
	private TextView wolTv1, wolTv2, wolTv3;// 玩家输赢金豆动画Tv
	private TextView iqTv1, iqTv2, iqTv3;// 等级值
	private RelativeLayout play1PassLayout, play2PassLayout, play3PassLayout, dizhuPaiLayout = null;
	private RelativeLayout mInfoRl, mSystemInfoRl;// 信息总布局，系统信息布局
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
	private ImageView baoXiangStar;// 宝箱上的星星
	private RelativeLayout baoXiangLayout, baoxiang;// 宝箱布局,宝箱
	private TextView baoText;
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
	private LinearLayout tilaLayout; // 踢拉父布局
	private Button tiLaBtn2, tiLaBtn4, buTiLaBtn;// 加倍2,加倍4，不加倍
	private ImageView jiabei1Iv, jiabei2Iv, jiabei3Iv;
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
	private boolean jiaBei2 = false, jiaBei4 = false;// （手势）可以加倍标识
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
	private View leftJiPaiQiLayout;
	private View rightJiPaiQiLayout;
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
		/* james removed
		refreshUserGoodsInfo();
		*/
		this.type = type;
		selfIsMove = false;
		newImageIsShow = false;
		isSystemInfo = false;
		play2IsTuoGuan = false;
		play3IsTuoGuan = false;
		callPoint = 0;
		HttpRequest.getCacheServer(true);
//		if (TextUtils.isEmpty(Database.GAME_SERVER)) {
//			Database.GAME_SERVER = ConfigUtil.getCfg("fast.game.ip");
//		}
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
		// 为叫地主的按钮设置监听器
		bujiao.setOnClickListener(clickListener); // 不叫地主
		fen1.setOnClickListener(clickListener); // 叫1分
		fen2.setOnClickListener(clickListener); // 叫2分
		fen3.setOnClickListener(clickListener); // 叫3fen
		tishi.setOnClickListener(clickListener); // 提示监听
		buchu.setOnClickListener(clickListener); // 不出
		chupai.setOnClickListener(clickListener); // 出牌
		messbtnView.setOnClickListener(clickListener);
		clearPrerechargeFlag();
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
					case R.id.fen1Button:
						callPoint(1);
						break;
					case R.id.fen2Button:
						callPoint(2);
						break;
					case R.id.fen3Button:
						callPoint(3);
						break;
					case R.id.bujiaoButton:// 不叫
						callPoint(0);
						break;
					case R.id.game_back:
						MobclickAgent.onEvent(ctx,"游戏中退出");
						DialogUtils.exitGame(ctx);
						break;
					case R.id.game_robot: // "点击托管"
						gameRobotClick();
						break;
					case R.id.game_pay:// 充值(游戏界面菜单栏充值按钮)
						JDSMSPayUtil.setContext(ctx);
						MobclickAgent.onEvent(ctx,"游戏中充值");
						double b = RechargeUtils.calRoomJoinMoney(Database.JOIN_ROOM);	//计算当前房间进入需要的基本金豆
						PayTipUtils.showTip(b,PaySite.PLAYING_CLICK); //配置的提示方式
//						SDKFactory.smsPay(0, SDKConstant.PLAYING);
						break;
					case R.id.dzed_recharge_beans_btn:// 充值(通过预充值提示充值)
						JDSMSPayUtil.setContext(ctx);
						// 充值额度的处理未接入，等待接入
						MobclickAgent.onEvent(ctx,"游戏中预充值");
						PayTipUtils.showTip(rechargeMoney,PaySite.PREPARERECHARGE); //配置的提示方式
//						SDKFactory.smsPay(rechargeMoney, SDKConstant.PLAYING);
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
					case R.id.bao_xiang_layout:// 宝箱
						clickBaoxiang();
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
					case R.id.tila_button_2:// 加倍2
						callJiabei(2);
						break;
					case R.id.tila_button_4:// 加倍4
						callJiabei(4);
						break;
					case R.id.bu_tila_button:// 不加倍
						callBuJiaBei();
						break;
					case R.id.dizhucard_rl:// 切换地主底牌和系统时间等信息布局
						rotationDizhuRl();
						break;
					case R.id.btn_jipaiqi:// 记牌器
						//地主产生前不允许使用记牌器
						if (0 != masterOrder) {
							if (isJiPaiQiEnable || isFirstTimeUseJiPaiQiOneDay()) {
								if (leftJiPaiQiLayout.getVisibility() == View.VISIBLE)
									setJiPaiQiVisibility(false);
								else {
									setJiPaiQiVisibility(true);
									if (null != beansInsufficientRl && beansInsufficientRl.getVisibility() == View.VISIBLE) {
										gonePrerechargeTv(10);
									}
								}
							} else {
								JDSMSPayUtil.setContext(context);
								if (null == jiPaiQiChargeDialog)
									jiPaiQiChargeDialog = new JiPaiQiChargeDialog(DoudizhuMainGameActivity.this, jiPaiQiTipsMsg);
								Log.i("JI_PAI_QI_FREE_COUNT", "jiPaiQiTipsMsg:" + jiPaiQiTipsMsg);
								if (!jiPaiQiChargeDialog.isShowing())
									jiPaiQiChargeDialog.show();
							}
						} else {
							DialogUtils.mesToastTip("亲，叫地主前不能使用记牌器哟~！");
						}
						break;
					case R.id.dzed_borrow_beans_btn:// 借点豆
						int allDouble = getCallDoubleNum();
						GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
						long currentWager = (Integer.valueOf(beishuNumber) * Database.JOIN_ROOM_BASEPOINT) * allDouble;
						if ((!isShowPrerechargeDialog) && (cacheUser.getBean() >= 0) && (currentWager > cacheUser.getBean())) {
							PrerechargeManager.createPrerechargeDialog(PrerechargeDialogType.Dialog_ingame, DoudizhuMainGameActivity.this, null, null, Integer.valueOf(beishuNumber) * allDouble).show();
							isShowPrerechargeDialog = true;
							borrowBeansBtn.setVisibility(View.INVISIBLE);
							MobclickAgent.onEvent(ctx, "游戏中借点豆");
						}
						break;
				}
			}
		}
	};

	/**
	 * 切换地主底牌和系统时间等信息布局
	 */
	private void rotationDizhuRl() {
		// 通过AnimationUtils得到动画配置文件(/res/anim/back_scale.xml)
		Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.back_scale);
		animation.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (isSystemInfo) {
					dizhuPaiLayout.setVisibility(View.VISIBLE);
					mSystemInfoRl.setVisibility(View.GONE);
				} else {
					dizhuPaiLayout.setVisibility(View.GONE);
					mSystemInfoRl.setVisibility(View.VISIBLE);
				}
				isSystemInfo = !isSystemInfo;
				// 通过AnimationUtils得到动画配置文件(/res/anim/front_scale.xml),然后在把动画交给ImageView
				mInfoRl.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.front_scale));
			}
		});
		animation.setDuration(500);
		mInfoRl.startAnimation(animation);
	}

	/**
	 * 不加倍
	 */
	private void callBuJiaBei() {
		stopTimer(0); // 暂停定时器
		startTiLaTimer(1);
		// 发送"不加倍"信息
		CmdDetail chat2 = new CmdDetail();
		chat2.setCmd(CmdUtils.CMD_TILA);
		// 1:不加倍,2:加2倍,4:加4倍
		chat2.setDetail("1");
		CmdUtils.sendMessageCmd(chat2);
		tilaLayout.setVisibility(View.GONE);
		moveMyHead();
		// 提示"不加倍"声音
		AudioPlayUtils apu = AudioPlayUtils.getInstance();
		String gd1 = Database.userMap.get(mySelfOrder).getGender();
		if ("1".equals(gd1)) {// 女
			// 女声
			apu.playSound(R.raw.nv_bujiabei);
		} else {
			// 男声
			apu.playSound(R.raw.nan_bujiabei);
		}
		// 显示"加倍"，"不加倍"
		ImageView info1 = new ImageView(ctx);
		info1.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.not_doubling, true));
		play1PassLayout.removeAllViews();
		// play2PassLayout.removeAllViews();
		// play3PassLayout.removeAllViews();
		info1.setPadding(0, 0, 0, 60);
		play1PassLayout.addView(info1, mst.getAdjustLayoutParamsForImageView(info1));
		// play1PassLayout.addView(info1, info1.getLayoutParams());
		ActivityUtils.startScaleAnim(play1PassLayout, ctx);// 播放缩放动画
	}

	/**
	 * 加倍
	 * 
	 * @param multiple
	 *            加倍的倍数( 2:加2倍 4:加4倍)
	 */
	private void callJiabei(int multiple) {
		stopTimer(0); // 暂停定时器
		startTiLaTimer(1);
		CmdDetail chat = new CmdDetail();
		// 发送"加倍"信息，
		chat.setCmd(CmdUtils.CMD_TILA);
		MobclickAgent.onEvent(ctx, "游戏中加"+multiple+"倍");
		jiabei1Iv.setVisibility(View.VISIBLE);
		ImageView info = new ImageView(ctx);
		// 1:不加倍,2:加2倍,4:加4倍
		if (2 == multiple) {
			chat.setDetail("2");
			// 给自己头像加上x2
			jiabei1Iv.setImageResource(R.drawable.jiabei_x_2);
			// 显示"加2倍"
			info.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.double_2, true));
		} else {
			chat.setDetail("4");
			// 给自己头像加上x4
			jiabei1Iv.setImageResource(R.drawable.jiabei_x_4);
			// 显示"加4倍"
			info.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.double_4, true));
		}
		CmdUtils.sendMessageCmd(chat);
		tilaLayout.setVisibility(View.GONE);
		setTweenAnim(jiabei1Iv, R.anim.jump, IS_HEAD_ANIM);
		// 提示"加倍"声音
		AudioPlayUtils apu2 = AudioPlayUtils.getInstance();
		String gd = Database.userMap.get(mySelfOrder).getGender();
		if ("1".equals(gd)) {// 女
			// 女声
			apu2.playSound(R.raw.nv_jiabei);
		} else {
			// 男声
			apu2.playSound(R.raw.nan_jiabei);
		}
		play1PassLayout.removeAllViews();
		// play2PassLayout.removeAllViews();
		// play3PassLayout.removeAllViews();
		info.setPadding(0, 0, 0, 60);
		play1PassLayout.addView(info, mst.getAdjustLayoutParamsForImageView(info));
		ActivityUtils.startScaleAnim(play1PassLayout, ctx);// 播放缩放动画
		/** 更改自己加倍的倍数 **/
		if (multiple == 2 || multiple == 4) {
			DoubleNum = multiple;
		}
	}

	/**
	 * 点击宝箱
	 */
	private void clickBaoxiang() {
		// "恭某某某干了什么事情或得的谁的什么什么奖励，特此公告以资表扬，望大家以此为榜样，共创辉煌";
		MobclickAgent.onEvent(ctx,"游戏中点击宝箱");
		if (6 == Database.JOIN_ROOM.getHomeType()) {
			if (quang == allQuan) {
				try {
					AnimUtils.startAnimationsOut(baoXiangLayout, 300, 50);
					quang = Database.userMap.get(mySelfOrder).getHasWin();
					baoText.setText("");
					baoText.setVisibility(View.INVISIBLE);
					giveCoupon();
				} catch (Exception e) {
					DialogUtils.mesToastTip("亲，您目前未能获得钻石，继续加油吧！");
				}
			}
		} else {
			if (quang < 5 && Math.abs(System.currentTimeMillis() - Constant.CLICK_TIME) >= Constant.SPACING_TIME) {
				Constant.CLICK_TIME = System.currentTimeMillis();
				DialogUtils.mesToastTip("打满五局开启神秘宝箱，宝物话费送不停！");
			} else if (quang >= 5 && allQuan < 20) {
				try {
					SharedPreferences sharedData = getApplication().getSharedPreferences(Constant.GAME_ACTIVITE, Context.MODE_PRIVATE);
					Editor editor = sharedData.edit();
					editor.putInt(Constant.QUANG_KEY, 0);
					editor.commit();
					AnimUtils.startAnimationsOut(baoXiangLayout, 300, 50);
					quang = sharedData.getInt(Constant.QUANG_KEY, 0);
					baoText.setText("");
					baoText.setVisibility(View.INVISIBLE);
					giveCoupon();
				} catch (Exception e) {
					DialogUtils.mesToastTip("很遗憾,未获得任何物品!");
				}
			} else if (quang >= 5 && allQuan >= 20) {
				try {
					SharedPreferences sharedData = getApplication().getSharedPreferences(Constant.GAME_ACTIVITE, Context.MODE_PRIVATE);
					Editor editor = sharedData.edit();
					editor.putInt(Constant.QUANG_KEY, 0);
					editor.commit();
					SharedPreferences sharedPrefer = getApplication().getSharedPreferences(Constant.GAME_GETFEE, Context.MODE_PRIVATE);
					Editor editall = sharedPrefer.edit();
					editall.putInt(Constant.ALL_QUAN, 0);
					editall.commit();
					AnimUtils.startAnimationsOut(baoXiangLayout, 300, 50);
					quang = sharedData.getInt(Constant.QUANG_KEY, 0);
					allQuan = 20;
					baoText.setText("");
					baoText.setVisibility(View.INVISIBLE);
					giveCoupon();
				} catch (Exception e) {
					DialogUtils.mesToastTip("很遗憾,未获得任何物品");
				}
			}
		}
	}

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

	public void refreshJiPaiQiAvatar() {
		Bitmap templeBitmap = ImageUtil.Drawable2Bitmap(play3Icon.getDrawable());
		if (null != templeBitmap)
			leftJiPaiQiTurnPlateView.setAvatar(templeBitmap);
		templeBitmap = ImageUtil.Drawable2Bitmap(play2Icon.getDrawable());
		if (null != templeBitmap)
			rightJiPaiQiTurnPlateView.setAvatar(templeBitmap);
	}

	/**
	 * 记牌器是否可使用
	 * 1,每天可以使用N次
	 * @return
	 */
	public boolean isFirstTimeUseJiPaiQiOneDay() {
		Log.i("JI_PAI_QI_FREE_COUNT", "Database.JI_PAI_QI_FREE_COUNT:" + Database.JI_PAI_QI_FREE_COUNT);
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		String timeString = ActivityUtils.getSharedValue(RecordPorkerView.JIPAIQI_USE_TIME);
		if (null != timeString) {
			Date date = new Date(Long.parseLong(timeString));
			if (date.getDate() != calendar.get(Calendar.DATE) || (date.getDate() == calendar.get(Calendar.DATE) && (0 != Database.JI_PAI_QI_FREE_COUNT))) {
				if (Database.JI_PAI_QI_FREE_COUNT >= 1) {
					Database.JI_PAI_QI_FREE_COUNT--;
					ActivityUtils.addSharedValue(RecordPorkerView.JIPAIQI_USE_COUNT, String.valueOf(Database.JI_PAI_QI_FREE_COUNT));
				}
				isJiPaiQiEnable = true;
				ActivityUtils.addSharedValue(RecordPorkerView.JIPAIQI_USE_TIME, String.valueOf(calendar.getTimeInMillis()));
			} else {
				isJiPaiQiEnable = false;
			}
		} else {
			//第一天使用
			if (Database.JI_PAI_QI_FREE_COUNT >= 1) {
				Database.JI_PAI_QI_FREE_COUNT--;
				ActivityUtils.addSharedValue(RecordPorkerView.JIPAIQI_USE_COUNT, String.valueOf(Database.JI_PAI_QI_FREE_COUNT));
			}
			isJiPaiQiEnable = true;
			ActivityUtils.addSharedValue(RecordPorkerView.JIPAIQI_USE_TIME, String.valueOf(calendar.getTimeInMillis()));
		}
		return isJiPaiQiEnable;
	}

	/**
	 * 设置记牌器是否可用
	 * 1.地主为产生(0==masterOrder)，不能使用
	 * 2.当天使用次数已经达到当天可使用次数后，不能使用
	 */
	private void setJipaiqiAvailableOrNotAvailable() {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		String timeString2 = ActivityUtils.getSharedValue(RecordPorkerView.JIPAIQI_USE_TIME);
		if (0 == masterOrder) {
			btn_jipaiqi.setBackgroundResource(R.drawable.button_card_record_down);
		} else if (null != timeString2) {
			Date date = new Date(Long.parseLong(timeString2));
			//如果是记录时间当天或者时间记录是当天，但使用次数不为0，则可以使用，否则不能使用
			if (date.getDate() != calendar.get(Calendar.DATE) || (date.getDate() == calendar.get(Calendar.DATE) && (0 != Database.JI_PAI_QI_FREE_COUNT))) {
				btn_jipaiqi.setBackgroundResource(R.drawable.btn_jipaiqi_bg);
			} else {
				btn_jipaiqi.setBackgroundResource(R.drawable.button_card_record_down);
			}
		} else {
			btn_jipaiqi.setBackgroundResource(R.drawable.btn_jipaiqi_bg);
		}
	}

	public void setJiPaiQiVisibility(boolean isVisible) {
		if (isVisible) {
			topLeftUserView.setVisibility(View.INVISIBLE);
			topRightUserView.setVisibility(View.INVISIBLE);
			leftJiPaiQiLayout.setVisibility(View.VISIBLE);
			leftJiPaiQiTurnPlateView.invalidate();
			rightJiPaiQiLayout.setVisibility(View.VISIBLE);
			rightJiPaiQiTurnPlateView.invalidate();
			recordPorkerView.setVisibility(View.VISIBLE);
			recordPorkerView.invalidate();
			cardStatView.setVisibility(View.VISIBLE);
			topLeftShieldView.setVisibility(View.INVISIBLE);
			topRightShieldView.setVisibility(View.INVISIBLE);
			girlItems.setVisibility(View.INVISIBLE);
			// imageNewIv.setVisibility(View.INVISIBLE);
		} else {
			leftJiPaiQiLayout.setVisibility(View.INVISIBLE);
			rightJiPaiQiLayout.setVisibility(View.INVISIBLE);
			recordPorkerView.setVisibility(View.INVISIBLE);
			cardStatView.setVisibility(View.INVISIBLE);
			topLeftUserView.setVisibility(View.VISIBLE);
			topRightUserView.setVisibility(View.VISIBLE);
			topLeftShieldView.setVisibility(View.VISIBLE);
			topRightShieldView.setVisibility(View.VISIBLE);
			girlItems.setVisibility(View.VISIBLE);
			// imageNewIv.setVisibility(View.VISIBLE);
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

	public void reSetJiPaiQiDataForRelink(String relinkString) {
		if (null == relinkString || TextUtils.isEmpty(relinkString))
			return;
		ReLink relink = JsonHelper.fromJson(relinkString, ReLink.class);
		Map<String, List<Integer>> userPlayCardRecordMap = relink.getAllPlayCardMap();
		if (null == userPlayCardRecordMap || userPlayCardRecordMap.size() != 3)
			return;
		List<Poker> mPokers = new ArrayList<Poker>();
		int orders[] = new int[] { mySelfOrder, getPerOrder(mySelfOrder), getNextOrder(mySelfOrder) };
		for (int order : orders) {
			mPokers.clear();
			GameUser gameUser = Database.userMap.get(order);
			if (null != gameUser) {
				String account = null;
				if (gameUser.getType() == 1) {
					String[] splitStrings = gameUser.getLoginToken().split("-");
					if (null != splitStrings) {
						account = splitStrings[splitStrings.length - 1];
					}
				} else {
					account = gameUser.getAccount();
				}
				for (Integer number : userPlayCardRecordMap.get(account)) {
					mPokers.add(PokerUtil.getPokerFromNumber(number, DoudizhuMainGameActivity.this));
				}
				if (order == mySelfOrder) {
					/** 重连获取是否可使用记牌器状态 **/
					if (gameUser.getJiPaiQiTime() > 0)
						isJiPaiQiEnable = true;
					recordPorkerView.clearCardList();
				} else if (order == getPerOrder(mySelfOrder)) {
					leftJiPaiQiTurnPlateView.clearCardList();
				} else if (order == getNextOrder(mySelfOrder)) {
					rightJiPaiQiTurnPlateView.clearCardList();
				}
				addOutPokers(order, mPokers);
			}
		}
	}

	public void clearJiPaiQiData() {
		isJiPaiQiEnable = false;
		rightJiPaiQiTurnPlateView.clearCardList();
		leftJiPaiQiTurnPlateView.clearCardList();
		recordPorkerView.clearCardList();
	}

	/**
	 * 显示预充值提示信息布局
	 */
	public void showPrerechargeLl() {
		/**
		 * 1.当前堵注大于自身金豆数 2.beansInsufficientRl 未显示 3.rechargeLl 未显示
		 **/
		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		int allDouble = getCallDoubleNum();
		long currentWager = (Integer.valueOf(beishuNumber) * Database.JOIN_ROOM_BASEPOINT) * allDouble;
		if ((cacheUser.getBean() >= 0) && (currentWager > cacheUser.getBean())) {
			beansInsufficientTv.setText("本局可赢" + PatternUtils.changeZhidou(currentWager) + "，您的金豆不足只能赢" + PatternUtils.changeZhidou(cacheUser.getBean()));
			Log.i("freshUserInfo", "您的金豆不足只能赢" + cacheUser.getBean());
//			int[] price = PrerechargeManager.calculatePrerechargePrice(cacheUser.getBean(), currentWager, ActivityUtils.getSimType());
			int[] price = PrerechargeManager.calculatePrerechargePrice(cacheUser.getBean(), currentWager);
			if (price.length == 0 || 0 == price[0]) {
				rechargeBtn.setText("充值");
			} else {
				rechargeBtn.setText("充值" + price[0] + "元");
				rechargeMoney = price[0];
			}
			//地主产生后才显示
			if ((beansInsufficientRl.getVisibility() != View.VISIBLE) && (rechargeLl.getVisibility() != View.VISIBLE) && 0 != masterOrder) {
				beansInsufficientRl.setVisibility(View.VISIBLE);
				rechargeLl.setVisibility(View.VISIBLE);
				if (cardStatView.getVisibility() == View.VISIBLE) {
					gonePrerechargeTv(5000);
				}
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
	 * 定时隐藏预充值提示文字布局
	 * @param time
	 *            延迟时间
	 */
	private void gonePrerechargeTv(long time) {
		if (prerechargeTask != null) {
			prerechargeTask.stop(true);
			prerechargeTask = null;
		}
		prerechargeTask = new PrerechargeTask();
		ScheduledTask.addDelayTask(prerechargeTask, time);
	}

	public void showPrerechargeDialog() {
		/**
		 * 1、如果当局还没有显示过预充值界面
		 * 2、当前剩余牌数小于等于@PrerechargeManager.showPrerechargelastCardCount
		 * 3、当前金豆大于等于0 4、当前赌注大于预充值条件
		 **/
		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		int allDouble = getCallDoubleNum();
		long currentWager = (Integer.valueOf(beishuNumber) * Database.JOIN_ROOM_BASEPOINT) * allDouble;
		if ((!isShowPrerechargeDialog) && (nowcard.size() <= PrerechargeManager.showPrerechargelastCardCount) && (cacheUser.getBean() >= 0) && (currentWager > (PrerechargeManager.showPrerechargePercent * cacheUser.getBean()))) {
			PrerechargeManager.createPrerechargeDialog(PrerechargeDialogType.Dialog_ingame, DoudizhuMainGameActivity.this, null, null, Integer.valueOf(beishuNumber) * allDouble).show();
			isShowPrerechargeDialog = true;
		}
	}

	public void clearPrerechargeFlag() {
		/** 重置预充值显示状态 **/
		isShowPrerechargeDialog = false;
		borrowBeansBtn.setVisibility(View.VISIBLE);
		/** 重置加倍倍数 **/
		DoubleNum = 1;
		rechargeMoney = 0;
		DoubleNum2 = 1;
		DoubleNum3 = 1;
		/** 清空预充值信息 **/
		PrerechargeManager.clearPrerechargeInfo();
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

	public void dismissPrechargeDialog() {
		if (PrerechargeManager.isShowPrerechargeDialog()) {
			PrerechargeManager.dismissPrerechargeDialog();
		}
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
			// girlItems.setVisibility(View.VISIBLE);
			// back.setVisibility(View.INVISIBLE);
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

	/**
	 * 
	 * @Title: 宝箱展示
	 * @param @param isAdd 是否增加一次宝箱记数
	 * @throws
	 */
	public void showBaoXiang(boolean isAdd) {
		if (6 == Database.JOIN_ROOM.getHomeType()) {
			if (null != Database.userMap.get(mySelfOrder)) {
				quang = Database.userMap.get(mySelfOrder).getHasWin();
				allQuan = Database.userMap.get(mySelfOrder).getNeedWin();
			}
			if (0 != quang && quang == allQuan) {
				baoxiang.setBackgroundResource(R.drawable.zhizuang);
				baoXiangLayout.setVisibility(View.VISIBLE);
				AnimUtils.startAnimationsIn(baoXiangLayout, 300, 50);
				baoXiangStar.setVisibility(View.VISIBLE);
				AnimUtils.playAnim(baoXiangStar, ImageUtil.getResAnimaSoft("baoxiang_start_"), 0);
				baoText.setText(quang + "/" + allQuan);
				baoText.setVisibility(View.INVISIBLE);
			} else {
				baoText.setVisibility(View.VISIBLE);
				baoText.setText(quang + "/" + allQuan);
			}
		} else {
			SharedPreferences sharedData = getApplication().getSharedPreferences(Constant.GAME_ACTIVITE, Context.MODE_PRIVATE);
			quang = sharedData.getInt(Constant.QUANG_KEY, 0);
			if (isAdd) {
				quang++;
			}
			Editor editor = sharedData.edit();
			editor.putInt(Constant.QUANG_KEY, quang);
			editor.commit();
			SharedPreferences sharedPrefer = getApplication().getSharedPreferences(Constant.GAME_GETFEE, Context.MODE_PRIVATE);
			allQuan = sharedPrefer.getInt(Constant.ALL_QUAN, 0);
			if (isAdd) {
				allQuan++;
			}
			Editor editall = sharedPrefer.edit();
			editall.putInt(Constant.ALL_QUAN, allQuan);
			editall.commit();
			if (quang < 5) {
				baoText.setVisibility(View.VISIBLE);
				baoText.setText(quang + "/5");
			} else {
				baoXiangLayout.setVisibility(View.VISIBLE);
				AnimUtils.startAnimationsIn(baoXiangLayout, 300, 50);
				baoXiangStar.setVisibility(View.VISIBLE);
				AnimUtils.playAnim(baoXiangStar, ImageUtil.getResAnimaSoft("baoxiang_start_"), 0);
				baoText.setText("");
				baoText.setVisibility(View.INVISIBLE);
			}
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
		// preLayout = myFrame;
		myFrame.removeAllViews();
		// if (selfTimer != null) {
		// selfTimer.cancel();
		// selfTimer.purge();
		// selfTimer = null;
		// }
		// selfTimer = new Timer();
		// startTimer(myFrame, selfTimer);
		startTask(myFrame, selfTask);
		messageFrame(myFrame, talk, clickType, null);
		CmdUtils.sendMessageCmd(chat);
//		dialog.dismiss();
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
		// if (areButtonsShowing) {
		tuoGuanLayout.setVisibility(View.VISIBLE);
		CmdUtils.sendIsRobot();
		// AnimUtils.startAnimationsIn(tuoGuanLayout, 800);
		AnimUtils.startScaleAnimationIn(tuoGuanLayout, ctx);
		areButtonsShowing = !areButtonsShowing;
		// }
		if (playBtnLayout.getVisibility() == View.VISIBLE) { // 如果打牌的时候托管
			setTuoGuan();
		}
		if (jiaofenLayout.getVisibility() == View.VISIBLE) {// 如果叫地主的时候托管
			callPoint(0);
		}
		if (tilaLayout.getVisibility() == View.VISIBLE) {// 如果叫地主的时候托管
			callBuJiaBei();
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

	/**
	 * 叫分
	 * 
	 * @param point
	 *            叫的分数
	 */
	private void callPoint(int point) {
		for (int i = 0; i < nowcard.size(); i++) {
			nowcard.get(i).ischeck = false;
		}
		jiaofenLayout.setVisibility(View.GONE);
		stopTimer(0);
		// setJiaofenXianshi(point, mySelfOrder);
		ImageView info = new ImageView(ctx);
		callPoints(point, info, mySelfOrder, true);
		// ========================================================
		// String gender = Database.userMap.get(mySelfOrder).getGender();
		// // play1PassLayout.removeAllViews();
		// // play2PassLayout.removeAllViews();
		// // play3PassLayout.removeAllViews();
		// if (point == 0) {
		// if ("1".equals(gender)) {
		// AudioPlayUtils.getInstance().playSound(R.raw.nv_bujiao);
		// } else {
		// AudioPlayUtils.getInstance().playSound(R.raw.nan_bujiao); // 叫0分
		// }
		// info.setBackgroundDrawable(ImageUtil.getResDrawable(
		// R.drawable.call_bujiao, true));
		// } else if (point == 1) {
		// if ("1".equals(gender)) {
		// AudioPlayUtils.getInstance().playSound(R.raw.nv_1fen);
		// } else {
		// AudioPlayUtils.getInstance().playSound(R.raw.nan_1fen); // 叫1分
		// }
		// info.setBackgroundDrawable(ImageUtil.getResDrawable(
		// R.drawable.callone, true));
		// } else if (point == 2) {
		// if ("1".equals(gender)) {
		// AudioPlayUtils.getInstance().playSound(R.raw.nv_2fen);
		// } else {
		// AudioPlayUtils.getInstance().playSound(R.raw.nan_2fen); // 叫2分
		// }
		// info.setBackgroundDrawable(ImageUtil.getResDrawable(
		// R.drawable.calltwo, true));
		// } else if (point == 3) {
		// }
		// ========================================================
		Log.i("jiaofenss", "callPoint:" + point);
		if (point != 3) {
			play1PassLayout.removeAllViews();
			play1PassLayout.addView(info, mst.getAdjustLayoutParamsForImageView(info));
			ActivityUtils.startScaleAnim(play1PassLayout, ctx);// 播放缩放动画
		}
		if (0 != point) {
			setBeiShuNumber(point);
		}
		CmdUtils.callDizhu(String.valueOf(point));
		if (point < 3) { // 继续等待下家叫地主
			startQiangTimer(1); // 开启抢地主定时器
		}
	}

	/**
	 * 计算倍数
	 * @param point
	 */
	private void setBeiShuNumber(int point) {
		beishuNumber = String.valueOf(point * Database.JOIN_ROOM_RATIO);
		beishuNumView.setText(beishuNumber);
		showPrerechargeLl();
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
		if (headTask != null) {
			headTask.stop(true);
			headTask = null;
		}
		if (baoXiangTask != null) {
			baoXiangTask.stop(true);
			baoXiangTask = null;
		}
		if (prerechargeTask != null) {
			prerechargeTask.stop(true);
			prerechargeTask = null;
		}
		cancelTimer();
		ImageUtil.releaseDrawable(doudizhuBackGround.getBackground());// 释放背景图片占的内存
		ImageUtil.releaseDrawable(play1Icon.getBackground());
		ImageUtil.releaseDrawable(play3Icon.getBackground());
		ImageUtil.releaseDrawable(play2Icon.getBackground());
		// ImageUtil.releaseDrawable(zhezhao1.getBackground());
		// ImageUtil.releaseDrawable(zhezhao3.getBackground());
		// ImageUtil.releaseDrawable(zhezhao2.getBackground());
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
		dizhuPaiLayout.removeAllViews();
		dizhuPaiLayout = null;
		// mGestureDetector = null;
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
		bujiao.setOnClickListener(null); // 不叫地主
		fen1.setOnClickListener(null); // 叫1分
		fen2.setOnClickListener(null); // 叫2分
		fen3.setOnClickListener(null); // 叫3fen
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
		baoXiangLayout.setOnClickListener(null);
		// gameBack.setOnClickListener(null);
		gameRobot.setOnClickListener(null);
		gamePay.setOnClickListener(null);
		gameSet.setOnClickListener(null);
		tuoGuan.setOnClickListener(null);
		jiaofenLayout.removeAllViews();
		jiaofenLayout = null;
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
		beishuNumView = null;
		zhidou = null;
		ImageUtil.releaseDrawable(play1Icon.getBackground());
		ImageUtil.releaseDrawable(play2Icon.getBackground());
		ImageUtil.releaseDrawable(play3Icon.getBackground());
		play1Icon = null;
		play2Icon = null;
		play3Icon = null;
		// ImageUtil.releaseDrawable(zhezhao1.getBackground());
		// ImageUtil.releaseDrawable(zhezhao2.getBackground());
		// ImageUtil.releaseDrawable(zhezhao3.getBackground());
		zhezhao1 = null;
		zhezhao2 = null;
		zhezhao3 = null;
		baoText = null;
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
			// poker[nowcard.get(nowcard.size() -
			// 1).getNumber()].params.topMargin = 0;
			// poker[nowcard.get(nowcard.size() -
			// 1).getNumber()].setLayoutParams(poker[nowcard.get(nowcard.size()
			// - 1).getNumber()].params);
			// poker[nowcard.get(nowcard.size() - 1).getNumber()].ischeck =
			// true;
			// DouDiZhuData data = new DouDiZhuData(nowcard);
			// data.fillPokerList();
			// List<List<Poker>> tishiList = data.getTiShi();
			// setTiShiCount();
			//
			// if (getTiShiCount() > tishiList.size() - 1) {
			// initTiShiCount();
			// setTiShiCount();
			// }
			// List<Poker> tiShiPoker = tishiList.get(getTiShiCount());
			// for (int i = 0; i < tiShiPoker.size(); i++) {
			// poker[tiShiPoker.get(i).getNumber()].params.topMargin = 0;
			// poker[tiShiPoker.get(i).getNumber()].setLayoutParams(poker[tiShiPoker.get(i).getNumber()].params);
			// poker[tiShiPoker.get(i).getNumber()].ischeck = true;
			// }
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
		fen1 = (Button) findViewById(R.id.fen1Button);
		fen2 = (Button) findViewById(R.id.fen2Button);
		fen3 = (Button) findViewById(R.id.fen3Button);
		jiao = 0;
		fen1.setClickable(true);
		fen2.setClickable(true);
		fen3.setClickable(true);
		bujiao = (Button) findViewById(R.id.bujiaoButton);
		tishi = (Button) findViewById(R.id.tishi_button);
		tilaLayout = (LinearLayout) findViewById(R.id.tila_ll);
		tiLaBtn2 = (Button) findViewById(R.id.tila_button_2);
		tiLaBtn2.setOnClickListener(clickListener);
		tiLaBtn4 = (Button) findViewById(R.id.tila_button_4);
		tiLaBtn4.setOnClickListener(clickListener);
		buTiLaBtn = (Button) findViewById(R.id.bu_tila_button);
		buTiLaBtn.setOnClickListener(clickListener);
		jiabei1Iv = (ImageView) findViewById(R.id.double1_iv);
		jiabei2Iv = (ImageView) findViewById(R.id.double2_iv);
		jiabei3Iv = (ImageView) findViewById(R.id.double3_iv);
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
		jiaofenLayout = (LinearLayout) findViewById(R.id.jiaofenRelative);
		// waitingLayout = (RelativeLayout) findViewById(R.id.waiting_layout);
		dizhuPaiLayout = (RelativeLayout) findViewById(R.id.dizhucard);
		mInfoRl = (RelativeLayout) findViewById(R.id.dizhucard_rl);
		mInfoRl.setOnClickListener(clickListener);
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
		// 设置选定的背景图
		// if(HttpCache.readBitmapData(Constant.GAME_BACK) != null){
		// BitmapDrawable bd = new
		// BitmapDrawable(HttpCache.readBitmapData(Constant.GAME_BACK));
		// doudizhuLayout.setBackgroundDrawable(bd);
		// }
		// doudizhuLayout.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (ActivityUtils.isFastDoubleClick(350)) {
		// // 双击处理
		// for (int i = 0; i < nowcard.size(); i++) {
		// poker[nowcard.get(i).getNumber()].params.topMargin =
		// mst.adjustXIgnoreDensity(20);
		// poker[nowcard.get(i).getNumber()].setLayoutParams(poker[nowcard.get(i).getNumber()].params);
		// poker[nowcard.get(i).getNumber()].ischeck = false;
		// }
		// }
		// }
		// });
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
		beishuNumView = (TextView) findViewById(R.id.beishunumber);
		dishu = (TextView) findViewById(R.id.dishunumber);
		baoXiangLayout = (RelativeLayout) findViewById(R.id.bao_xiang_layout);
		baoxiang = (RelativeLayout) findViewById(R.id.baoxiang);
		Map<String, String> roomRuleMap = JsonHelper.fromJson(Database.JOIN_ROOM.getRule(), new TypeToken<Map<String, String>>() {});
		if (6 == Database.JOIN_ROOM.getHomeType()) {
			if (roomRuleMap.containsKey("path")) {
				baoxiang.setBackgroundDrawable(new BitmapDrawable(null == ImageUtil.getGirlBitmap(HttpURL.URL_PIC_ALL + roomRuleMap.get("path"), true, false) ? BitmapFactory.decodeResource(ctx.getResources(), R.drawable.baoxiang) : ImageUtil.getGirlBitmap(HttpURL.URL_PIC_ALL + roomRuleMap.get("path"), true, false)));
			} else {
				baoxiang.setBackgroundResource(R.drawable.baoxiang);
			}
		} else {
			baoxiang.setBackgroundResource(R.drawable.baoxiang);
		}
		baoXiangLayout.setOnClickListener(clickListener);
		baoXiangStar = (ImageView) findViewById(R.id.bao_xiang_star);
		baoXiangStar.setVisibility(View.INVISIBLE);
		baoText = (TextView) findViewById(R.id.bao_xiang_text);
		// vipRoomBg = (ImageView) findViewById(R.id.vip_no_img);
		// vipRoomBg.setBackgroundDrawable(ImageUtil.getResDrawable(ctx,R.drawable.vip));
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
		// playAnimLayout = (RelativeLayout)
		// findViewById(R.id.play_anim_layout);
		zhadanImageView = (ImageView) findViewById(R.id.play_anim_layout_zhadan);
		shunzImageView = (ImageView) findViewById(R.id.play_anim_layout_shunzi);
		feijiImageView = (ImageView) findViewById(R.id.play_anim_layout_feiji);
		wangzhaImageView = (ImageView) findViewById(R.id.play_anim_layout_wangzha);
		userinfoshowView = (RelativeLayout) findViewById(R.id.playinfoview);
		userInfoText = (TextView) findViewById(R.id.userinfotext);
		// 游戏界面返回键，暂时去掉
		// gameBack = (ImageButton) findViewById(R.id.game_back);
		// gameBack.setOnClickListener(clickListener);
		gameRobot = (ImageButton) findViewById(R.id.game_robot);
		gameRobot.setOnClickListener(clickListener);
		gamePay = (ImageButton) findViewById(R.id.game_pay);
		gamePay.setOnClickListener(clickListener);
		// 判断可否短信充值
//		if (!SmsPayUtil.canUseSmsPay()) {
//			gamePay.setBackgroundResource(R.drawable.mianpage_pay_no);
//			gamePay.setClickable(false);
//		}
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
		// // 注册广播，用于接收广播刷新系统时间,计算比赛倒计时
		// if (0 != Database.JOIN_ROOM.getRoomType()) {// 除了大厅房，都显示倒计时
		// IntentFilter intentFilter = new IntentFilter();
		// intentFilter.addAction(Constant.SYSTEM_TIME_CHANGE_ACTION);
		// mReciver = new MyBroadcastReciver();
		// this.registerReceiver(mReciver, intentFilter);
		// }
		bitmapList = new ArrayList<Bitmap>();
		Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.card_kingf);
		for (int i = 0; i <= 10; i++) {
			bitmapList.add(bitmap);
		}
		/** 记牌器 **/
		leftJiPaiQiLayout = findViewById(R.id.jipaiqi_layout_left);
		rightJiPaiQiLayout = findViewById(R.id.jipaiqi_layout_right);
		leftJiPaiQiTurnPlateView = (JiPaiQiTurnPlateView) findViewById(R.id.jipaiqi_left);
		leftJiPaiQiTurnPlateView.setLocation(Location.Top_Left);
		rightJiPaiQiTurnPlateView = (JiPaiQiTurnPlateView) findViewById(R.id.jipaiqi_right);
		leftJiPaiQiTurnPlateView.setLocation(Location.Top_Right);
		recordPorkerView = (RecordPorkerView) findViewById(R.id.jipaiqi_record_view);
		topRightShieldView = findViewById(R.id.dun_layout_right);
		cardStatView = findViewById(R.id.layout_jipaiqi);
		btn_jipaiqi = (Button) findViewById(R.id.btn_jipaiqi);
		setJipaiqiAvailableOrNotAvailable();
		btn_jipaiqi.setOnClickListener(clickListener);
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
		rechargeBtn = (Button) findViewById(R.id.dzed_recharge_beans_btn);
		rechargeBtn.setOnClickListener(clickListener);
		borrowBeansBtn = (Button) findViewById(R.id.dzed_borrow_beans_btn);
		borrowBeansBtn.setOnClickListener(clickListener);
		rechargeLl.setVisibility(View.GONE);
		setBeiShuNumber(1); // 房间默认倍数
		dishu.setText(String.valueOf(Database.JOIN_ROOM_BASEPOINT)); // 房间默认底数
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
		/** 叫分属性 */
		fen1.setBackgroundResource(R.drawable.fen1_btn_bg);
		fen2.setBackgroundResource(R.drawable.fen2_btn_bg);
		fen3.setBackgroundResource(R.drawable.fen3_btn_bg);
		jiao1 = false;
		jiao2 = false;
		jiao3 = false;
		/** 加倍属性 */
		tiLaBtn2.setBackgroundResource(R.drawable.jiabei_btn_bg_2);
		tiLaBtn4.setBackgroundResource(R.drawable.jiabei_btn_bg_4);
		jiaBei2 = false;
		jiaBei4 = false;
		baoFlag = false;
		baoXiangLayout.setVisibility(View.VISIBLE);
		baoXiangStar.setVisibility(View.INVISIBLE);
		AnimUtils.startAnimationsIn(baoXiangLayout, 300, 50);
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
						if (!baoFlag) {
							baoFlag = true;
							showBaoXiang(true);
						}
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
						callDizhu(fapai); // 叫地主
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
						if (baoXiangTask != null) {
							baoXiangTask.stop(true);
							baoXiangTask = null;
						}
						baoXiangTask = new BaoXiangTask();
						ScheduledTask.addDelayTask(baoXiangTask, 3000);
						addDiZhuCardbg();
						// mainGameGuideVI.setVisibility(View.VISIBLE);
						/** 更新记牌器头像 **/
						refreshJiPaiQiAvatar();
						
						
						
						HashMap<String, String> purchase = new HashMap<String, String>();
						purchase.put("room",Database.JOIN_ROOM.getName());
						purchase.put("account",mGameUser.getAccount());
						purchase.put("userctime",mGameUser.getCreateDate());
						MobclickAgent.onEvent(ctx,"斗地主游戏中",purchase);
						break;
					case 1: // 叫地主更新界面
						Grab grab = (Grab) msg.getData().get("grab");
						if (grab.getNextOrder() == mySelfOrder) { // 上家叫的分
							isTurnMySelf = true;
							stopTimer(-1); // 暂停定时器
						} else { // 下家叫的分
							isTurnMySelf = false;
							stopTimer(1); // 暂停定时器
						}
						truntoCallDizhu(grab);
						break;
					case 2: // 地主产生
						hasDiZhu(msg);
						if (headTask != null) {
							headTask.stop(true);
							headTask = null;
						}
						headTask = new HeadTask();
						ScheduledTask.addDelayTask(headTask, 3000);
						/** 更新记牌器头像 **/
						refreshJiPaiQiAvatar();
						break;
					case 3: // 收到打牌消息
						hiddenPlayBtn();
						Play play = (Play) msg.getData().get("play");
						playCard(play, false);
						setShengxiaPai(play.getCount(), getPerOrder(play.getNextOrder()));
						/** 显示预充值界面 **/
						showPrerechargeDialog();
						/** 显示预充值提示信息布局 **/
						showPrerechargeLl();
						refreshCardCountData();
						/** 更新记牌器头像 **/
						refreshJiPaiQiAvatar();
						break;
					case 4: // 收到打完这盘的牌消息
						MobclickAgent.onEventEnd(ctx,"在线斗地主");
						hiddenPlayBtn();
						LinkedList<Play> playResult = (LinkedList<Play>) msg.getData().get("playResult");
						/** 关闭预充值窗口 **/
						dismissPrechargeDialog();
						/** 清除记牌器数据 **/
						clearJiPaiQiData();
						/** 隐藏记牌器 **/
						setJiPaiQiVisibility(false);
						/** 记牌器按钮不能点 **/
						btn_jipaiqi.setClickable(false);
						if (null != jiPaiQiChargeDialog && jiPaiQiChargeDialog.isShowing())
							jiPaiQiChargeDialog.dismiss();
						setEndDonghua(playResult);
						break;
					case 5:// 收到叫地主定时器消息
						int timeleast = 0;
						// （-1 上家 0 自己 1 下载）
						int callOrder = msg.arg1;
						if (callOrder == 0) { // 自己叫地主
							if (play1Timer != null) {
								timeleast = Integer.parseInt(play1Timer.getText().toString()) - 1;
								if (timeleast == 0) {
									callPoint(0);
									return;
								} else {
									play1Timer.setText(String.valueOf(timeleast));
								}
							}
						} else if (callOrder == 1) {
							if (play2Timer != null) {
								timeleast = Integer.parseInt(play2Timer.getText().toString()) - 1;
								if (timeleast != 0) {
									play2Timer.setText(String.valueOf(timeleast));
								}
							}
						} else if (callOrder == -1) {
							if (play3Timer != null) {
								timeleast = Integer.parseInt(play3Timer.getText().toString()) - 1;
								if (timeleast != 0) {
									play3Timer.setText(String.valueOf(timeleast));
								}
							}
						}
						if (timeleast == 6) { // 播放警告声音
							AudioPlayUtils.getInstance().playSound(R.raw.warn);
						}
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
					case 9: // 地主产生（带踢拉功能）
						hasTiLaDiZhu(msg);
						break;
					case 10: // 接收显示地主的牌（带踢拉功能）
						if (masterOrder == mySelfOrder) { // 停掉计时器
							isTurnMySelf = true;
							stopTimer(0);
							moveMyHead();
						} else if (getNextOrder(masterOrder) == mySelfOrder) { // 上家
							stopTimer(-1);
						} else {
							stopTimer(1);
						}
						LastCards lastCard = (LastCards) msg.getData().get("lastCard");
						// 更新地主牌数量
						TextView masterCountView2 = null;
						if (!isTurnMySelf) {
							masterCountView2 = (TextView) findViewById(1100 + masterOrder);
						} else {
							masterCountView2 = play1SurplusCount;
						}
						masterCountView2.setText("20");
						genxinMycard(lastCard.getId(), lastCard.getLast(), lastCard.getMasterOrder());
						startOtherTimer();
						break;
					case 11:// 接收当前玩家的踢拉选择，提示下家是“踢”或"拉"
						getTiLaMsg(msg);
						/** 更新记牌器头像 **/
						refreshJiPaiQiAvatar();
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
								callBuJiaBei();
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
						/** 更新记牌器头像 **/
						refreshJiPaiQiAvatar();
						break;
					case 17: // 退出游戏返回房间
						DialogUtils.quitGameTip();
						break;
					case 18:// 收到聊天定时器消息
						CmdDetail mess = (CmdDetail) msg.getData().get(CmdUtils.CMD_CHAT);
						/*try {
							mess.setDetail(new String(Base64Util.decode(mess.getDetail()),"UTF-8"));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
						showMessage(mess);
						break;
					case 19:// 收到系统公告
						String pubMess = (String) msg.getData().get("publicmess");
						showPubMess(pubMess);
						break;
					case 20:// 退出游戏
						DialogUtils.exitGame(DoudizhuMainGameActivity.this);
						break;
					case 21:// 设置房间的倍数和底数
						setBeiShuNumber(1); // 房间默认倍数
						dishu.setText(String.valueOf(Database.JOIN_ROOM_BASEPOINT)); // 房间默认底数
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
							// if (null != mGameEndDialog &&
							// mGameEndDialog.isShowing()) {
							// // 提前显示心跳界面，已实现无缝跳转
							// if (task2 != null) {
							// task2.stop(true);
							// task2 = null;
							// }
							// task2 = new AutoTask() {
							// public void run() {
							// runOnUiThread(new Runnable() {
							// public void run() {
							// if (null != mGameEndDialog &&
							// mGameEndDialog.isShowing()) {
							// mGameEndDialog.cancelTimer();
							// mGameEndDialog.dismiss();
							// }
							// showGameOverDialog(rank, prizeGoods2);
							// }
							// });
							// }
							// };
							// ScheduledTask.addDelayTask(task2, 2000);
							// } else {
							// }
							showGameOverDialog(rank, prizeGoods2);
						}
						break;
					// case 23://
					// break;
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
						reSetJiPaiQiDataForRelink(relink);
						refreshJiPaiQiAvatar();
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
							if (headTask != null) {
								headTask.stop(true);
								headTask = null;
							}
						}
						break;
					case 302:// 隐藏宝箱
						if (null != Database.JOIN_ROOM && 6 == Database.JOIN_ROOM.getHomeType()) {
							if (quang < allQuan) {
								AnimUtils.startAnimationsOut(baoXiangLayout, 300, 50);
							}
						} else {
							if (quang < 5) {
								AnimUtils.startAnimationsOut(baoXiangLayout, 300, 50);
							}
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
						setBeiShuNumber(1); // 房间默认倍数
						dishu.setText(String.valueOf(Database.JOIN_ROOM_BASEPOINT)); // 房间默认底数
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
//					case 405:// 隐藏左边美女图片
//						girlLeftFrame.setVisibility(View.GONE);
//						if (selfTask != null) {
//							selfTask.stop(true);
//							selfTask = null;
//						}
//						break;
//					case 406:// 隐藏右边美女图片
//						girlRightFrame.setVisibility(View.GONE);
//						if (rightTask != null) {
//							rightTask.stop(true);
//							rightTask = null;
//						}
//						break;
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
						//tipsMess = EncodeUtils.strDecode(tipsMess);
						/*try {
							tipsMess = new String(Base64Util.decode(tipsMess),"UTF-8");
						} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
						tips.setText("您的金豆超过上限");
						break;
					case 777:// 叫分选项显示
						jiaofenNum = msg.arg1;
						if (jiaofenNum == 1 && fen1.isClickable()) {
							fen1.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.fen1_selected, true));
						} else if (jiaofenNum == 2 && fen2.isClickable()) {
							if (fen1.isClickable()) {
								fen1.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.fen1, true));
							}
							fen2.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.fen2_selected, true));
						} else if (jiaofenNum == 3 && fen3.getVisibility() == View.VISIBLE) {
							if (fen1.isClickable()) {
								fen1.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.fen1, true));
							}
							if (fen2.isClickable()) {
								fen2.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.fen2, true));
							}
							fen3.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.fen3_selected, true));
						} else if (jiaofenNum > 3 && fen3.getVisibility() == View.VISIBLE) {
							callPoint(3);
						}
						Log.i("lin", "a" + jiaofenNum);
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
			 * 地主产生
			 * 
			 * @param msg
			 */
			private void hasDiZhu(Message msg) {
				jiaofenLayout.setVisibility(View.GONE);
				Grab grabMaster = (Grab) msg.getData().get("master");
				setDizhuIcon(grabMaster.getMasterOrder());
				masterOrder = grabMaster.getMasterOrder();
				setJipaiqiAvailableOrNotAvailable();
				isTurnMySelf = false;
				if (masterOrder == mySelfOrder) { // 停掉计时器
					isTurnMySelf = true;
					stopTimer(0);
				} else if (getNextOrder(masterOrder) == mySelfOrder) { // 上家
					stopTimer(-1);
				} else {
					stopTimer(1);
				}
				// 更新地主牌数量
				TextView masterCountView = null;
				if (!isTurnMySelf) {
					masterCountView = (TextView) findViewById(1100 + masterOrder);
				} else {
					masterCountView = play1SurplusCount;
				}
				masterCountView.setText("20");
				int tempRatio = grabMaster.getRatio() / Database.JOIN_ROOM.getRatio();
				ImageView info = new ImageView(ctx);
				// play1PassLayout.removeAllViews();
				// play2PassLayout.removeAllViews();
				// play3PassLayout.removeAllViews();
				callPoints(tempRatio, info, masterOrder, false);
				// 显示"加倍"，"不加倍"
				if (mySelfOrder == masterOrder) {
					info.setPadding(0, 0, 0, 60);
					play1PassLayout.removeAllViews();
					play1PassLayout.addView(info, mst.getAdjustLayoutParamsForImageView(info));
					ActivityUtils.startScaleAnim(play1PassLayout, ctx);// 播放缩放动画
				} else {
					RelativeLayout re = (RelativeLayout) findViewById(masterOrder + 1000);
					if (re != null) {
						re.removeAllViews();
						re.addView(info, mst.getAdjustLayoutParamsForImageView(info));
						ActivityUtils.startScaleAnim(re, ctx);// 播放缩放动画
					}
				}
				setBeiShuNumber(tempRatio);
				genxinMycard(grabMaster.getId(), grabMaster.getLastcards(), grabMaster.getMasterOrder());
				startOtherTimer();
			}

			/**
			 * 地主产生（带踢拉功能）
			 * 
			 * @param msg
			 */
			private void hasTiLaDiZhu(Message msg) {
				jiaofenLayout.setVisibility(View.GONE);
				GenLandowners gld = (GenLandowners) msg.getData().get("gld");
				setDizhuIcon(gld.getMasterOrder());
				masterOrder = gld.getMasterOrder();
				setJipaiqiAvailableOrNotAvailable();
				isTurnMySelf = false;
				int ratio = gld.getRatio() / Database.JOIN_ROOM.getRatio();
				ImageView info = new ImageView(ctx);
				// play1PassLayout.removeAllViews();
				// play2PassLayout.removeAllViews();
				// play3PassLayout.removeAllViews();
				callPoints(ratio, info, masterOrder, false);
				// =====================================================================
				// AudioPlayUtils apu = AudioPlayUtils.getInstance();
				// String gender2 =
				// Database.userMap.get(masterOrder).getGender();
				// if (ratio == 0) {
				// if ("1".equals(gender2)) {
				// AudioPlayUtils.getInstance().playSound(R.raw.nv_bujiao);// 不叫
				// } else {
				// AudioPlayUtils.getInstance()
				// .playSound(R.raw.nan_bujiao);// 不叫
				// }
				// info.setBackgroundDrawable(ImageUtil.getResDrawable(
				// R.drawable.call_bujiao, true));
				// } else if (ratio == 1) {
				// if ("1".equals(gender2)) {
				// apu.playMusic(false, R.raw.nv_1fen); // 别人叫1分
				// } else {
				// apu.playMusic(false, R.raw.nan_1fen); // 别人叫1分
				// }
				// info.setBackgroundDrawable(ImageUtil.getResDrawable(
				// R.drawable.callone, true));
				// } else if (ratio == 2) {
				// if ("1".equals(gender2)) {
				// apu.playMusic(false, R.raw.nv_2fen); // 别人叫2分
				// } else {
				// apu.playMusic(false, R.raw.nan_2fen); // 别人叫2分
				// }
				// info.setBackgroundDrawable(ImageUtil.getResDrawable(
				// R.drawable.calltwo, true));
				// } else if (ratio == 3) {
				// if ("1".equals(gender2)) {
				// apu.playMusic(false, R.raw.nv_3fen); // 别人叫3分
				// } else {
				// apu.playMusic(false, R.raw.nan_3fen); // 别人叫3分
				// }
				// info.setBackgroundDrawable(ImageUtil.getResDrawable(
				// R.drawable.callthree, true));
				// }
				// =====================================================================
				Log.i("jiaofenss", "hasTiLaDiZhu:" + ratio);
				setBeiShuNumber(ratio);
				// 关闭叫地主定时器
				if (mySelfOrder == masterOrder) {// 自己
					stopTimer(0); // 暂停定时器
				} else if (JIABEI2_ID == (1600 + masterOrder)) {// 下家
					stopTimer(1); // 暂停定时器
				} else if (JIABEI3_ID == (1600 + masterOrder)) {// 上家
					stopTimer(-1); // 暂停定时器
				}
				// ActivityUtils.startScaleAnim(play1PassLayout, ctx);//播放缩放动画
				// 开启踢拉定时器
				if (mySelfOrder == masterOrder) {// 自己
					isTurnMySelf = false;
					startTiLaTimer(1);
				} else if (JIABEI2_ID == (1600 + masterOrder)) {// 下家
					isTurnMySelf = false;
					startTiLaTimer(-1);
				} else if (JIABEI3_ID == (1600 + masterOrder)) {// 上家
					isTurnMySelf = true;
					startTiLaTimer(0);
				}
				// 显示"加倍"，"不加倍"
				if (mySelfOrder == masterOrder) {
					info.setPadding(0, 0, 0, 60);
					play1PassLayout.addView(info, mst.getAdjustLayoutParamsForImageView(info));
					ActivityUtils.startScaleAnim(play1PassLayout, ctx);// 播放缩放动画
				} else {
					RelativeLayout re = (RelativeLayout) findViewById(masterOrder + 1000);
					if (re != null) {
						re.removeAllViews();
						re.addView(info, mst.getAdjustLayoutParamsForImageView(info));
						ActivityUtils.startScaleAnim(re, ctx);// 播放缩放动画
					}
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
				// play1PassLayout.removeAllViews();
				// play2PassLayout.removeAllViews();
				// play3PassLayout.removeAllViews();
				Log.i("Ordersss", "加倍玩家的位置:" + tila.getOrder() + "\n下一个踢or拉玩家的位置:" + tila.getNextOrder() + "\n加倍的倍数 (1:不加倍,2:加2倍,4:加4倍):" + tila.getRatio() + "\n下一家是否可踢啦:" + tila.getNextCan());
				String gd = "0";
				if (null != Database.userMap && Database.userMap.containsKey(tila.getOrder())) {
					gd = Database.userMap.get(tila.getOrder()).getGender();
				}
				AudioPlayUtils apu2 = AudioPlayUtils.getInstance();
				if (null != tila.getOrder() && 0 != tila.getRatio()) {
					// (1:不加倍,2:加2倍,4:加4倍,0:没有叫加倍不加倍)
					if (1 != tila.getRatio()) {// 加倍
						if (2 == tila.getRatio()) {
							info.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.double_2, true));
							// 显示已加倍的状态
							if (mySelfOrder == tila.getOrder()) {
								jiabei1Iv.setVisibility(View.VISIBLE);
								jiabei1Iv.setImageResource(R.drawable.jiabei_x_2);
							}
							if (JIABEI2_ID == (1600 + tila.getOrder())) {
								jiabei2Iv.setVisibility(View.VISIBLE);
								jiabei2Iv.setImageResource(R.drawable.jiabei_x_2);
								setTweenAnim(jiabei2Iv, R.anim.jump, IS_NONE);
								DoubleNum2 = 2;
							}
							if (JIABEI3_ID == (1600 + tila.getOrder())) {
								jiabei3Iv.setVisibility(View.VISIBLE);
								jiabei3Iv.setImageResource(R.drawable.jiabei_x_2);
								setTweenAnim(jiabei3Iv, R.anim.jump, IS_NONE);
								DoubleNum3 = 2;
							}
						} else {
							info.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.double_4, true));
							// 显示已加倍的状态
							if (mySelfOrder == tila.getOrder()) {
								jiabei1Iv.setVisibility(View.VISIBLE);
								jiabei1Iv.setImageResource(R.drawable.jiabei_x_4);
							}
							if (JIABEI2_ID == (1600 + tila.getOrder())) {
								jiabei2Iv.setVisibility(View.VISIBLE);
								jiabei2Iv.setImageResource(R.drawable.jiabei_x_4);
								setTweenAnim(jiabei2Iv, R.anim.jump, IS_NONE);
								DoubleNum2 = 4;
							}
							if (JIABEI3_ID == (1600 + tila.getOrder())) {
								setTweenAnim(jiabei3Iv, R.anim.jump, IS_NONE);
								jiabei3Iv.setImageResource(R.drawable.jiabei_x_4);
								jiabei3Iv.setVisibility(View.VISIBLE);
								DoubleNum3 = 4;
							}
						}
						// 声音提示
						if ("1".equals(gd)) {// 女
							// 女声
							apu2.playSound(R.raw.nv_jiabei);
						} else {
							// 男声
							apu2.playSound(R.raw.nan_jiabei);
						}
						// 在对应的玩家头像显示加倍标识
					} else {// 不加倍
						info.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.not_doubling, true));
						if ("1".equals(gd)) {// 女
							// 女声
							apu2.playSound(R.raw.nv_bujiabei);
						} else {
							// 男声
							apu2.playSound(R.raw.nan_bujiabei);
						}
					}
					// 显示"加倍"，"不加倍"
					if (mySelfOrder == tila.getOrder()) {
						play1PassLayout.removeAllViews();
						jiabei1Iv.setVisibility(View.VISIBLE);
						info.setPadding(0, 0, 0, 60);
						play1PassLayout.addView(info, mst.getAdjustLayoutParamsForImageView(info));
						ActivityUtils.startScaleAnim(play1PassLayout, ctx);// 播放缩放动画
					} else {
						RelativeLayout re = (RelativeLayout) findViewById(tila.getOrder() + 1000);
						Log.i("Order", "tila.getOrder():" + tila.getOrder() + "      re:" + re);
						if (re != null) {
							re.removeAllViews();
							re.addView(info, mst.getAdjustLayoutParamsForImageView(info));
							ActivityUtils.startScaleAnim(re, ctx);// 播放缩放动画
						}
					}
					Log.i("Ordersss", "tila.getOrder():" + tila.getOrder() + "      mySelfOrder:" + mySelfOrder + "     masterOrder:" + masterOrder);
				}
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
				// 下一个可否踢or拉
				if (tila.getNextCan() && (null != tila.getNextOrder()) && (mySelfOrder == tila.getNextOrder())) {// 下一个是自己，并且可加倍
					play1PassLayout.removeAllViews();
					tilaLayout.setVisibility(View.VISIBLE);
					if (isTuoguan) {
						callBuJiaBei();
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
		fen1.setClickable(true);
		fen2.setClickable(true);
		fen3.setClickable(true);
		play2IsTuoGuan = false;
		play3IsTuoGuan = false;
		myCardsTouchLayout.removeAllViews();
		play1PassLayout.removeAllViews();
		play2PassLayout.removeAllViews();
		play3PassLayout.removeAllViews();
		jiabei1Iv.setVisibility(View.GONE);
		jiabei2Iv.setVisibility(View.GONE);
		jiabei3Iv.setVisibility(View.GONE);
		wolTv1.setVisibility(View.GONE);
		wolTv2.setVisibility(View.GONE);
		wolTv3.setVisibility(View.GONE);
		beansInsufficientRl.setVisibility(View.GONE);
		setJipaiqiAvailableOrNotAvailable();
		rechargeLl.setVisibility(View.GONE);
		if (selfIsMove) {
			selfIsMove = false;
			nullTv.setVisibility(View.GONE);
			nullTv2.setVisibility(View.VISIBLE);
		}
		cancelTuoGuanState();
		dismissDialog();
		if (isSystemInfo) {
			rotationDizhuRl();
		}
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
		setBeiShuNumber(1);
		/** 重置预充值标志 **/
		clearPrerechargeFlag();
		/** 重置记牌器数据 **/
		clearJiPaiQiData();
		showPrerechargeLl();
		/** **/
		btn_jipaiqi.setClickable(true);
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
	 * 添加地主牌的背景
	 */
	private void addDiZhuCardbg() {
		dizhuPaiLayout.removeAllViews();
		// 初始化，显示地主牌背景
		for (int i = 0; i < 3; i++) {
			Poker ca = new Poker(ctx);
			ca.getPokeImage().setImageDrawable(ImageUtil.getResDrawable(R.drawable.pukes, true));
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mst.adjustXIgnoreDensity(50), mst.adjustYIgnoreDensity(68));
			params.leftMargin = mst.adjustXIgnoreDensity(13 * i);
			dizhuPaiLayout.addView(ca, params);
			firstChupai = false;
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
				// 检测加倍
				checkJiaBei(typeMe, false);
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
			// 检测加倍
			checkJiaBei(typeMe, false);
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
			showPrerechargeDialog();
			showPrerechargeLl();
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

	// 显示表情
//	private class MyAdapter extends BaseAdapter {
//
//		private int[] gifInt;
//		private LayoutInflater mInflater;
//
//		public MyAdapter(int[] gifs) {
//			this.gifInt = gifs;
//			this.mInflater = LayoutInflater.from(DoudizhuMainGameActivity.this);
//		}
//
//		public int getCount() {
//			return gifInt.length;
//		}
//
//		public Object getItem(int position) {
//			return gifInt[position];
//		}
//
//		public long getItemId(int position) {
//			return position;
//		}
//
//		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder holder;
//			if (null == convertView) {
//				holder = new ViewHolder();
//				convertView = mInflater.inflate(R.layout.gif_item, null);
//				holder.iv = (ImageView) convertView.findViewById(R.id.gif1);
//				holder.iv.setBackgroundDrawable(ImageUtil.getDrawableResId(gifInt[position], true, true));
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}
//			return convertView;
//		}
//
//		private class ViewHolder {
//
//			private ImageView iv;
//		}
//	}

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

	/**
	 * 检测是否需要加倍
	 * 
	 * @param type
	 * @param reShow
	 *            是否重连时显示的牌
	 * 
	 */
	public void checkJiaBei(int type, boolean reShow) {
		if (reShow)
			return;
		// 如果是炸弹或者是火箭
		if (type == 6 || type == 13) {
			beishuNumber = String.valueOf(Integer.parseInt(beishuNumber) * 2);
			beishuNumView.setText(beishuNumber);
			showPrerechargeLl();
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
		if (!isSystemInfo) { // 显示系统时间等信息
			rotationDizhuRl();
		}
		
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
		// if (1 == Database.JOIN_ROOM.getRoomType()) {// 超快赛
		// mGameEndDialog = new GameEndDialog(ctx, users, mySelfOrder, handler);
		// mGameEndDialog.setContentView(R.layout.doudizhu_end);
		// android.view.WindowManager.LayoutParams lay =
		// mGameEndDialog.getWindow().getAttributes();
		// setParams(lay);
		// mGameEndDialog.show();
		// hasEnd = false;
		// } else {
		// }
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
	 * 叫地主提示多少分
	 * 
	 * @param ratio
	 * @param ResId
	 * @param perCallOrder
	 *            上次叫分人
	 */
	public void setJiaofenXianshi(int ratio, int perCallOrder) {
		ImageView info = new ImageView(this);
		info.setBackgroundDrawable(ImageUtil.getResDrawable(backId(ratio, perCallOrder), true));
		// play1PassLayout.removeAllViews();
		// play2PassLayout.removeAllViews();
		// play3PassLayout.removeAllViews();
		if (perCallOrder != mySelfOrder) {// 如果是自己叫地主
			RelativeLayout re = (RelativeLayout) findViewById(perCallOrder + 1000); // 叫分的人
			if (re != null) {
				re.removeAllViews();
				// re.addView(info);
				re.addView(info, mst.getAdjustLayoutParamsForImageView(info));
				ActivityUtils.startScaleAnim(re, ctx);// 播放缩放动画
			}
		} else {
			if (3 == ratio) { // 叫三分放在此处是防止，自己点三分的时候出现重复的叫3分声音
				String gender = Database.userMap.get(perCallOrder).getGender();
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playSound(R.raw.nv_3fen);// 叫3分
				} else {
					AudioPlayUtils.getInstance().playSound(R.raw.nan_3fen);// 叫3分
				}
				RelativeLayout re = (RelativeLayout) findViewById(perCallOrder + 1000); // 叫分的人
				if (re != null) {
					re.removeAllViews();
					// re.addView(info);
					re.addView(info, mst.getAdjustLayoutParamsForImageView(info));
					ActivityUtils.startScaleAnim(re, ctx);// 播放缩放动画
				}
			}
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
	 * 通过传入叫分的分值返回图片的id
	 * 
	 * @param ratio
	 * @return
	 */
	public int backId(int ratio, int perCallOrder) {
		int id = 0;
		String gender = Database.userMap.get(perCallOrder).getGender();
		switch (ratio) {
			case 0:
				id = R.drawable.call_bujiao;
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playSound(R.raw.nv_bujiao);// 不叫
				} else {
					AudioPlayUtils.getInstance().playSound(R.raw.nan_bujiao);// 不叫
				}
				break;
			case 1:
				id = R.drawable.callone;
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playSound(R.raw.nv_1fen);// 叫1分
				} else {
					AudioPlayUtils.getInstance().playSound(R.raw.nan_1fen);// 叫1分
				}
				break;
			case 2:
				id = R.drawable.calltwo;
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playSound(R.raw.nv_2fen);// 叫2分
				} else {
					AudioPlayUtils.getInstance().playSound(R.raw.nan_2fen);// 叫2分
				}
				break;
			case 3:
				id = R.drawable.callthree;
				break;
		}
		Log.i("jiaofenss", "backId:" + ratio);
		return id;
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
		if (tilaLayout.getVisibility() == View.VISIBLE && playBtnLayout.getVisibility() != View.VISIBLE && jiaofenLayout.getVisibility() != View.VISIBLE) {
			callBuJiaBei();
		}
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
		jiabei2Iv.setId(JIABEI2_ID);
		jiabei3Iv.setId(JIABEI3_ID);
	}

	/**
	 * 一开始对自己判断是否叫地主
	 * 
	 * @param fapai
	 */
	public void callDizhu(Play fapai) {
		// 如果是轮到自己叫地主,显示叫地主的布局
		if (fapai.isCall()) {
			isTurnMySelf = true;
			jiaofenLayout.setVisibility(View.VISIBLE);
			startQiangTimer(0);// 开启抢地主定时器
		} else {
			isTurnMySelf = false;
			if (fapai.getCallOrder() == getNextOrder(mySelfOrder)) { // 下家
				startQiangTimer(1);
			} else { // 上家
				startQiangTimer(-1);
			}
		}
	}

	/**
	 * 根据服务器发送的消息来判断是否自己叫地主
	 * 
	 * @param grab
	 */
	public void truntoCallDizhu(Grab grab) {
		int currentCallOrder = grab.getNextOrder(); // 当前叫分人的位置编号
		Log.i("jiaofenss", "truntoCallDizhu1");
		// 叫地主显示
		setJiaofenXianshi(grab.getRatio(), getPerOrder(currentCallOrder));
		if (callPoint < grab.getRatio()) {
			callPoint = grab.getRatio();
		} else {
			grab.setRatio(callPoint);
		}
		if (grab.getRatio() != 0) { // 设置房间倍数
			setBeiShuNumber(callPoint);
		}
		if (currentCallOrder == mySelfOrder) { // 轮到自己叫分
			if (isTuoguan) {
				callPoint(0);
				return;
			} else {
				// 叫地主提示
				Log.i("jiaofenss", "truntoCallDizhu2");
				// setJiaofenXianshi(grab.getRatio(),getPerOrder(currentCallOrder));
				startQiangTimer(0); // 开启抢地主定时器
				// 显示叫分栏
				jiaofenLayout.setVisibility(View.VISIBLE);
				// 根据别人叫分情况让某些叫分按钮不可按
				if (grab.getRatio() == 1) { // 上次叫1分
					fen1.setClickable(false);
					fen1.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.fen1_no, true));
				} else if (grab.getRatio() == 2) {// 上次叫2分
					fen1.setClickable(false);
					fen1.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.fen1_no, true));
					fen2.setClickable(false);
					fen2.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.fen2_no, true));
				}
			}
		} else if (currentCallOrder != mySelfOrder) { // 下家叫的
			startQiangTimer(-1);
		}
		Log.i("jiaofenss", "truntoCallDizhu3" + callPoint);
		// 叫地主显示
		// setJiaofenXianshi(grab.getRatio(), getPerOrder(currentCallOrder));
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
	 * 设置地主的头像
	 * 
	 * @param msterOrder
	 */
	public void setDizhuIcon(int msterOrder) {
		if (msterOrder == mySelfOrder) { // 如果自己是地主的话
			GameUser mGameUser = Database.userMap.get(mySelfOrder);
			ActivityUtils.setHead(ctx, play1Icon, mGameUser.getGender(), true, mGameUser.getIqImg(), true);// 地主
			int po1 = getNextOrder(msterOrder);
			GameUser mGameUser1 = Database.userMap.get(po1);
			ImageView icon = (ImageView) findViewById(1300 + po1);
			if (null != icon) {
				ActivityUtils.setHead(ctx, icon, mGameUser1.getGender(), false, mGameUser1.getIqImg(), true);// 农民
			}
			int po2 = getNextOrder(po1);
			GameUser mGameUser2 = Database.userMap.get(po2);
			ImageView icon1 = (ImageView) findViewById(1300 + po2);
			if (null != icon1) {
				ActivityUtils.setHead(ctx, icon1, mGameUser2.getGender(), false, mGameUser2.getIqImg(), true);// 农民
			}
			zhezhao1.setVisibility(View.GONE);
		} else {
			ImageView icon2 = (ImageView) findViewById(1300 + msterOrder);
			if (null != icon2) {
				ActivityUtils.setHead(ctx, icon2, Database.userMap.get(msterOrder).getGender(), true, Database.userMap.get(msterOrder).getIqImg(), true);// 地主
			}
			int po3 = getNextOrder(msterOrder);
			if (po3 == mySelfOrder) {
				ActivityUtils.setHead(ctx, play1Icon, Database.userMap.get(mySelfOrder).getGender(), false, Database.userMap.get(mySelfOrder).getIqImg(), true);// 农民
			} else {
				ImageView icon3 = (ImageView) findViewById(1300 + po3);
				if (null != icon3) {
					ActivityUtils.setHead(ctx, icon3, Database.userMap.get(po3).getGender(), false, Database.userMap.get(po3).getIqImg(), true);// 农民
				}
			}
			int po4 = getNextOrder(po3);
			if (po4 == mySelfOrder) {
				ActivityUtils.setHead(ctx, play1Icon, Database.userMap.get(mySelfOrder).getGender(), false, Database.userMap.get(mySelfOrder).getIqImg(), true);// 农民
			} else {
				ImageView icon4 = (ImageView) findViewById(1300 + po4);
				if (null != icon4) {
					ActivityUtils.setHead(ctx, icon4, Database.userMap.get(po4).getGender(), false, Database.userMap.get(po4).getIqImg(), true);// 农民
				}
			}
			ImageView zhezhao = (ImageView) findViewById(1400 + msterOrder);
			if (null != zhezhao) {
				zhezhao.setVisibility(View.GONE);
			}
		}
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
		// playtask = new DapaiTask(id);
		// timer = new Timer();
		// timer.schedule(playtask, 0, 1000);//
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
			// 添加地主的牌在桌面上
			dizhuPaiLayout.removeAllViews();
			for (int i = 0; i < 3; i++) {
				Poker ca = new Poker(this);
				ca.getPokeImage().setImageDrawable(ImageUtil.getResDrawable(poker[Integer.parseInt(dizhuCard[i])].getBitpamResID(), true));
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mst.adjustXIgnoreDensity(50), mst.adjustYIgnoreDensity(68));
				params.leftMargin = mst.adjustXIgnoreDensity(13 * i);
				dizhuPaiLayout.addView(ca, params);
				firstChupai = false;
			}
			play1PassLayout.removeAllViews();
			showPlayBtn(true);
			if (isTuoguan) { // 如果当前正在托管
				setTuoGuan();
			} else {
				jiaofenLayout.setVisibility(View.GONE);
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
			// 添加地主的牌在桌面上
			dizhuPaiLayout.removeAllViews();
			for (int i = 0; i < 3; i++) {
				Poker ca = new Poker(this);
				// 有过空指针异常，，，需要捕捉parseInt(dizhuCard[i])] dizhuCard[] == ""
				ca.getPokeImage().setImageDrawable(ImageUtil.getResDrawable(poker[Integer.parseInt(dizhuCard[i])].getBitpamResID(), true));
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mst.adjustXIgnoreDensity(50), mst.adjustYIgnoreDensity(68));
				params.leftMargin = mst.adjustXIgnoreDensity(13 * i);
				dizhuPaiLayout.addView(ca, params);
			}
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
					// if (bierenchupai != null) {
					// showPlayBtn(false);
					// }
				}
			}
			int[] cad = new int[otherPlayCards.length];
			for (int i = 0; i < otherPlayCards.length; i++) {
				cad[i] = Integer.parseInt(otherPlayCards[i]);
			}
			bierenchupai = cad;
			// 检测加倍
			checkJiaBei(checkOtherChupai(bierenchupai), reShow);
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
	 * 宝箱定时器
	 * 
	 * @author Administrator
	 */
	class BaoXiangTask extends AutoTask {

		public void run() {
			handler.sendEmptyMessage(302);
			handler.sendEmptyMessage(303);
		}
	}

	/**
	 * 头像定时器
	 * 
	 * @author Administrator
	 */
	class HeadTask extends AutoTask {

		public void run() {
			handler.sendEmptyMessage(301);
		}
	}

	/**
	 * 预充值提示定时器
	 * 
	 * @author xu
	 */
	class PrerechargeTask extends AutoTask {

		public void run() {
			handler.sendEmptyMessage(304);
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
			case DoudizhuRule.wangzha:// 如果是两张牌 王炸
				wangzhaImageView.setVisibility(View.VISIBLE);
				if(value>0)
				{
					value -= 3;
					if(value>=0 && value<sound_three[0].length)
					{
						AudioPlayUtils.getInstance().playSound(sound_three["1".equals(gender)?1:0][value]); // 出牌
					}
				}
				/*if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playMultiMusic2(R.raw.nv_wangzha, R.raw.boombeffect);
				} else {
					AudioPlayUtils.getInstance().playMultiMusic2(R.raw.nan_wangzha, R.raw.boombeffect);
				}*/
				AnimUtils.playAnim(wangzhaImageView, ImageUtil.getResAnimaSoft("wanBomb"), 2000);
				setTweenAnim(wangzhaImageView, R.anim.wangzha_out, IS_WANGZHA_ANIM);
				break;
			case DoudizhuRule.Santiao:// 如果是三张牌
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playSound(R.raw.nv_3dai0);
				} else {
					AudioPlayUtils.getInstance().playSound(R.raw.nan_3dai0);
				}
				break;
			case DoudizhuRule.zhadan: // 如果是四张牌 炸弹
				zhadanIv.setVisibility(View.VISIBLE);
				setTweenAnim(zhadanIv, R.anim.zhadang_play, IS_ZHADAN_ANIM);
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playMultiMusic2(R.raw.nv_bomb, R.raw.boombeffect);
				} else {
					AudioPlayUtils.getInstance().playMultiMusic2(R.raw.nan_bomb, R.raw.boombeffect);
				}
				break;
			case DoudizhuRule.Sandaiyi:// 如果是四张牌 三带一
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playSound(R.raw.nv_3dai1);
				} else {
					AudioPlayUtils.getInstance().playSound(R.raw.nan_3dai1);
				}
				break;
			case DoudizhuRule.Sandaier:// 如果是五张牌 三待二
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playSound(R.raw.nv_3dai2);
				} else {
					AudioPlayUtils.getInstance().playSound(R.raw.nan_3dai2);
				}
				break;
			case DoudizhuRule.sidaiyi: // 如果是6张 "4带2
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playSound(R.raw.nv_4dai2);
				} else {
					AudioPlayUtils.getInstance().playSound(R.raw.nan_4dai2);
				}
				break;
			case DoudizhuRule.shunzi: // 顺牌
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playSound(R.raw.nv_shunzi);
				} else {
					AudioPlayUtils.getInstance().playSound(R.raw.nan_shunzi);
				}
				shunzImageView.setVisibility(View.VISIBLE);
				AnimUtils.playAnim(shunzImageView, ImageUtil.getResAnimaSoft("shunz"), 3000);
				break;
			case DoudizhuRule.liandui: // 如果是6张 连对
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playSound(R.raw.nv_liandui);
				} else {
					AudioPlayUtils.getInstance().playSound(R.raw.nan_liandui);
				}
				break;
			case DoudizhuRule.sidaier: // 检测4帶2對
				if ("1".equals(gender)) {
					AudioPlayUtils.getInstance().playSound(R.raw.nv_4dai22);
				} else {
					AudioPlayUtils.getInstance().playSound(R.raw.nan_4dai22);
				}
				break;
			case DoudizhuRule.feiji: // 如果是6张 飞机
			case DoudizhuRule.feijidaisan: // 飞机带2
			case DoudizhuRule.feijidaidui: // 飞机带4
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

//	// 短语，思考
//	private List<String> getMessData() {
//		String[] items = getResources().getStringArray(R.array.mes_language);
//		List<String> list = Arrays.asList(items);
//		return list;
//	}
//
//	private List<String> getThinkData() {
//		String[] items = getResources().getStringArray(R.array.think_language);
//		List<String> list = Arrays.asList(items);
//		return list;
//	}

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
//			else if (type == Constant.MESSAGE_TYPE_THREE) {// 美女
//				if (layout.getId() == girlLeftFrame.getId()) {
//					leftFrame.setVisibility(View.GONE);
//					String name = mess.getValue().substring(0, mess.getValue().lastIndexOf(".")) + "_left";
//					girlLeftTv.setText(ImageUtil.getGirlSayText(name));
//					layout.setBackgroundDrawable(ImageUtil.getResDrawableByName(name, true, true));
//				}
//				if (layout.getId() == girlRightFrame.getId()) {
//					rightFrame.setVisibility(View.GONE);
//					String name = mess.getValue().substring(0, mess.getValue().lastIndexOf(".")) + "_left";
//					girlRightTv.setText(ImageUtil.getGirlSayText(name));
//					int indext = -1;
//					if (null != cashList && cashList.size() > 3) {
//						for (int i = 0; i < cashList.size() - 3;) {
//							if (null != cashList.get(i) && !cashList.get(i).getImage().isRecycled()) {
//								cashList.get(i).getImage().recycle();
//							}
//							cashList.remove(i);
//							i = 0;
//						}
//					}
//					for (int i = 0, count = cashList.size(); i < count; i++) {
//						if (name.equals(cashList.get(i).getId())) {
//							indext = i;
//							break;
//						}
//					}
//					if (-1 == indext) {
//						Drawable drawable = ImageUtil.getResDrawableByName(name, true, true);
//						// Drawable转Bitmap
//						BitmapDrawable bd = (BitmapDrawable) drawable;
//						int height = Database.SCREEN_HEIGHT;
//						int width = Database.SCREEN_WIDTH;
//						String bitMapKey = name + "_small";
//						Bitmap bitmap = ImageUtil.getBitmap(bitMapKey, false);
//						if (bitmap == null) {
//							bitmap = ImageUtil.resizeBitmap(bd.getBitmap(), width, height - 1);
//							ImageUtil.addBitMap2Cache(bitMapKey, bitmap);
//						}
//						// Bitmap bitmap =
//						// ImageUtil.resizeBitmap(bd.getBitmap(), width, height
//						// - 1);
//						String canvasKey = name + "_canvas";
//						Drawable canvasDrawable = ImageUtil.getDrawableByKey(canvasKey);
//						if (canvasDrawable == null) {
//							Bitmap bitmap1 = Bitmap.createBitmap(width, height - 1, Bitmap.Config.ARGB_8888);
//							Canvas canvas = new Canvas(bitmap1);
//							canvas.save();
//							// 镜像翻转画布
//							canvas.scale(-1.0f, 1.0f, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
//							canvas.drawBitmap(bitmap, 0, 0, null);
//							canvas.restore();
//							cashList.add(new BitmapVO(name, bitmap1));
//							cashList.add(new BitmapVO(name + "1", bitmap));
//							canvasDrawable = ImageUtil.addDrawable2Cache(canvasKey, new BitmapDrawable(bitmap1));
//						}
//						layout.setBackgroundDrawable(canvasDrawable);
//					} else {
//						layout.setBackgroundDrawable(new BitmapDrawable(cashList.get(indext).getImage()));
//					}
//				}
//			}
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
			// else if (messType == Constant.MESSAGE_TYPE_ONE) {
			// TextView myView = new TextView(this);
			// myView.setText(talk);
			// myView.setTextColor(android.graphics.Color.RED);
			// myView.setBackgroundResource(R.drawable.my_think);
			// myView.setGravity(Gravity.CENTER);
			// girlLeftFrame.setVisibility(View.GONE);
			// layout.addView(myView);
			// }
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
//			else if (messType == Constant.MESSAGE_TYPE_THREE) {
//				talk = talk.substring(0, talk.lastIndexOf(".")) + "_left";
//				myFrame.setVisibility(View.GONE);
//				girlLeftTv.setText(ImageUtil.getGirlSayText(talk));
//				layout.setBackgroundDrawable(ImageUtil.getResDrawableByName(talk, true, true));
//				int height = Database.SCREEN_HEIGHT;
//				int width = Database.SCREEN_WIDTH;
//				Log.i("Background", "left-- height:" + height + "  width: " + width + " name: " + talk);
//			}
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
			if (CmdUtils.CMD_GRAB.equals(cmd)) {// 如果是叫地主的话
				hideSlowTip();
				Message message = new Message();
				Grab grab = JsonHelper.fromJson(detail, Grab.class); // jsonData是一个Json对象
				message.what = 1;
				Bundle bundle = new Bundle();
				bundle.putSerializable("grab", grab);
				message.setData(bundle);
				handler.sendMessage(message);
				return;
			}
			if (CmdUtils.CMD_MASTER.equals(cmd)) {// 如果是叫地主成功的话
				hideSlowTip();
				Message message = new Message();
				Grab master = JsonHelper.fromJson(detail, Grab.class); // jsonData是一个Json对象
				message.what = 2;
				Bundle bundle = new Bundle();
				bundle.putSerializable("master", master);
				message.setData(bundle);
				handler.sendMessage(message);
				return;
			}
			if (CmdUtils.CMD_GEN_LANDOWNERS.equals(cmd)) {// 产生地主(带踢拉功能)
				hideSlowTip();
				Message message = new Message();
				GenLandowners gld = JsonHelper.fromJson(detail, GenLandowners.class); // jsonData是一个Json对象
				message.what = 9;
				Bundle bundle = new Bundle();
				bundle.putSerializable("gld", gld);
				message.setData(bundle);
				handler.sendMessage(message);
				return;
			}
			if (CmdUtils.CMD_TILA.equals(cmd)) {// 踢、拉(带踢拉功能)
				hideSlowTip();
				Message message = new Message();
				TiLa tila = JsonHelper.fromJson(detail, TiLa.class); // jsonData是一个Json对象
				message.what = 11;
				Bundle bundle = new Bundle();
				bundle.putSerializable("tila", tila);
				message.setData(bundle);
				handler.sendMessage(message);
				return;
			}
			if (CmdUtils.CMD_SENDLASTCARDS.equals(cmd)) {// 发剩余的牌给地主(带踢拉功能)
				hideSlowTip();
				Message message = new Message();
				LastCards lastCard = JsonHelper.fromJson(detail, LastCards.class); // jsonData是一个Json对象
				message.what = 10;
				Bundle bundle = new Bundle();
				bundle.putSerializable("lastCard", lastCard);
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
					handler.sendEmptyMessage(21);
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
			if (CmdUtils.CMD_HDETAIL.equals(cmd)) { // 加入游戏校验失败，金豆不足
				Room room = JsonHelper.fromJson(detail, Room.class);
				try {
					JDSMSPayUtil.setContext(ctx);
					//进入房间时金豆不足
					double b = RechargeUtils.calRoomJoinMoney(room);	//计算当前房间进入需要的基本金豆
					boolean isRecharge = PayTipUtils.showTip(b,PaySite.ROOM_ITEM_CLICK); //配置的提示方式
					if(!isRecharge){
//						DialogUtils.rechargeTip(room, true, null);		//默认提示方式
					}
				} catch (Exception e) {
					ActivityUtils.finishAcitivity();
				}
				ClientCmdMgr.closeClient();
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
			/** 预充值订单命令 **/
			if (CmdUtils.CMD_PPC.equals(cmd)) {
				JSONObject jsonObject = null;
				String status = null;
				String descrption = null;
				String preOderNo = null;
				try {
					jsonObject = new JSONObject(detail);
					status = jsonObject.getString(PrerechargeManager.PRERECHARGE_ORDER_PARAMS_STATUS);
					descrption = jsonObject.getString(PrerechargeManager.PRERECHARGE_ORDER_PARAMS_DETAIL);
					preOderNo = jsonObject.getString(PrerechargeManager.PRERECHARGE_ORDER_PARAMS_PREORDERNO);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (null != status && status.equalsIgnoreCase(Constant.SUCCESS)) {
					if (null == descrption)
						descrption = Database.currentActivity.getString(R.string.text_prerecharge_order_create_success);
					DialogUtils.toastTip(descrption);
					PrerechargeManager.mPayRecordOrder.setPreOrderNo(preOderNo);
					PrerechargeManager.setPrePay(true);
				} else {
					DialogUtils.toastTip(Database.currentActivity.getString(R.string.text_prerecharge_order_create_failed));
				}
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
			// Message message = new Message();
			// message.what = 22;
			// Bundle bundle = new Bundle();
			// bundle.putString("detail", detail);
			// message.setData(bundle);
			// handler.sendMessage(message);
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
			beishuNumView.setText(beishuNumber); // 重连后的倍数
			showPrerechargeLl();
			dishu.setText(String.valueOf(Database.JOIN_ROOM_BASEPOINT)); // 房间默认底数
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
			if (1 != tempUser.getRatio()) {// 是否加倍
				jiabei1Iv.setVisibility(View.VISIBLE);
				if (2 == tempUser.getRatio()) {
					jiabei1Iv.setImageResource(R.drawable.jiabei_x_2);
				} else {
					jiabei1Iv.setImageResource(R.drawable.jiabei_x_4);
				}
			}
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
			if (1 != tempUser.getRatio()) {// 是否加倍
				jiabei2Iv.setVisibility(View.VISIBLE);
				if (2 == tempUser.getRatio()) {
					jiabei2Iv.setImageResource(R.drawable.jiabei_x_2);
				} else {
					jiabei2Iv.setImageResource(R.drawable.jiabei_x_4);
				}
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
			if (1 != tempUser.getRatio()) {// 是否加倍
				jiabei3Iv.setVisibility(View.VISIBLE);
				if (2 == tempUser.getRatio()) {
					jiabei3Iv.setImageResource(R.drawable.jiabei_x_2);
				} else {
					jiabei3Iv.setImageResource(R.drawable.jiabei_x_4);
				}
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
			if (relStatus == Constant.STATUS_START) { // 开始叫地主
				if (isMyDo) { // 轮到自己叫地主
					Play play = new Play();
					play.setCall(true);
					callDizhu(play);
				}
				return;
			}
			if (relStatus == Constant.STATUS_GRAB) { // 正在叫地主
				if (isMyDo) { // 轮到自己叫地主
					Play play = new Play();
					play.setCall(true);
					callDizhu(play);
				} else { // 其他人叫地主
					isTurnMySelf = false;
					if (relink.getNextPlayOrder() == getNextOrder(mySelfOrder)) { // 下家
						startQiangTimer(1);
					} else { // 上家
						startQiangTimer(-1);
					}
				}
				return;
			}
			if (relStatus == Constant.STATUS_TL) { // 轮到自己加倍
				if (isMyDo) { // 轮到自己加倍
					play1PassLayout.removeAllViews();
					tilaLayout.setVisibility(View.VISIBLE);
					isTurnMySelf = true;
					callBuJiaBei();
				}
				masterOrder = relink.getMasterOrder();
				setDizhuIcon(masterOrder);
				return;
			}
			addDiZhuCardbg();
			if (relStatus == Constant.STATUS_PLAYING) { // 正在打牌时重连
				jiaofenLayout.setVisibility(View.GONE);
				masterOrder = relink.getMasterOrder();
				setDizhuIcon(masterOrder);
				selfIsMove = true;
				nullTv2.setVisibility(View.GONE);
				nullTv.setVisibility(View.VISIBLE);
				try {
					String masterCards = relink.getMasterCard();
					String[] dzCard = masterCards.substring(1, masterCards.length() - 1).split(",");
					// 添加地主的牌在桌面上
					dizhuPaiLayout.removeAllViews();
					for (int i = 0; i < dzCard.length; i++) {
						Poker ca = new Poker(this);
						ca.getPokeImage().setImageDrawable(ImageUtil.getResDrawable(poker[Integer.parseInt(dzCard[i])].getBitpamResID(), true));
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mst.adjustXIgnoreDensity(50), mst.adjustYIgnoreDensity(68));
						params.leftMargin = mst.adjustXIgnoreDensity(13 * i);
						dizhuPaiLayout.addView(ca, params);
					}
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
			setJipaiqiAvailableOrNotAvailable();
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
			// if (0 == Database.USER.getRound()) {
			// rankTop.setVisibility(View.GONE);
			// } else {
			// }
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
			// if (pubTimer != null) {
			// pubTimer.cancel();
			// pubTimer.purge();
			// pubTimer = null;
			// }
			publicLayout.removeAllViews();
			publicLayout.setVisibility(View.VISIBLE);
			marqueeText.currentScrollX = 0;
			marqueeText.setText(mess);
			marqueeText.stopScroll();
			marqueeText.startScroll();
			marqueeText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
			marqueeText.setSingleLine(true);
			marqueeText.setFocusable(true);
			// pubTimer = new Timer();
			int messLong = mess.length();
			int timeValue = messLong / 2;
		
			publicLayout.addView(marqueeText);
			// pubTimer(publicLayout, pubTimer, timeValue);
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

//		private String id;
		private Bitmap image;

//		public BitmapVO(String id, Bitmap image) {
//			this.id = id;
//			this.image = image;
//		}
//
//		public String getId() {
//			return id;
//		}

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
		// 如果商家有出过牌并且不是单牌，并且我点击的牌处于没弹出状态
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
						if (jiaofenLayout.getVisibility() != View.VISIBLE) {// 双击取消选牌
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
						if (playBtnLayout.getVisibility() == View.VISIBLE && jiaofenLayout.getVisibility() != View.VISIBLE && tilaLayout.getVisibility() != View.VISIBLE) {
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
				/** 叫分选择 */
				if (jiaofenLayout.getVisibility() == View.VISIBLE && tilaLayout.getVisibility() != View.VISIBLE && playBtnLayout.getVisibility() != View.VISIBLE) {
					/**间距*/
					int space = jiaofenLayout.getHeight() / 2;
					Log.i("space", "jiaofenLayout...space:  " + space);
					int left = jiaofenLayout.getLeft() - space;
					int right = jiaofenLayout.getRight() + space;
					int top = jiaofenLayout.getTop() - space;
					int bottom = jiaofenLayout.getBottom() + space;
					if (e.getRawX() < left || e.getRawX() > right || e.getRawY() < top || e.getRawY() > bottom) {
						jiao++;
						jiao1 = false;
						jiao2 = false;
						jiao3 = false;
						if (fen1.isClickable()) {
							if (jiao == 1) {
								fen1.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.fen1_touch, true));
								fen2.setBackgroundResource(R.drawable.fen2_btn_bg);
								fen3.setBackgroundResource(R.drawable.fen3_btn_bg);
								jiao1 = true;
							} else if (jiao == 2) {
								fen1.setBackgroundResource(R.drawable.fen1_btn_bg);
								fen2.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.fen2_touch, true));
								fen3.setBackgroundResource(R.drawable.fen3_btn_bg);
								jiao2 = true;
							} else {
								fen1.setBackgroundResource(R.drawable.fen1_btn_bg);
								fen2.setBackgroundResource(R.drawable.fen2_btn_bg);
								fen3.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.fen3_touch, true));
								jiao = 0;
								jiao3 = true;
							}
						} else if (!fen1.isClickable() && fen2.isClickable()) {
							if (jiao == 1) {
								fen2.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.fen2_touch, true));
								fen3.setBackgroundResource(R.drawable.fen3_btn_bg);
								jiao2 = true;
							} else {
								fen2.setBackgroundResource(R.drawable.fen2_btn_bg);
								fen3.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.fen3_touch, true));
								jiao = 0;
								jiao3 = true;
							}
						} else if (!fen1.isClickable() && !fen2.isClickable()) {
							fen3.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.fen3_touch, true));
							jiao = 0;
							jiao3 = true;
						}
					}
					return true;
				}
				/** 加倍选择 */
				if (tilaLayout.getVisibility() == View.VISIBLE && jiaofenLayout.getVisibility() != View.VISIBLE && playBtnLayout.getVisibility() != View.VISIBLE) {
					/**间距*/
					int space = tilaLayout.getHeight() / 2;
					Log.i("space", "tilaLayout--space:  " + space);
					int left = tilaLayout.getLeft() - space;
					int right = tilaLayout.getRight() + space;
					int top = tilaLayout.getTop() - space;
					int bottom = tilaLayout.getBottom() + space;
					if (e.getRawX() < left || e.getRawX() > right || e.getRawY() < top || e.getRawY() > bottom) {
						jia += 2;
						jiaBei2 = false;
						jiaBei4 = false;
						if (jia == 2) {
							tiLaBtn2.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.jiabei_2_touch, true));
							tiLaBtn4.setBackgroundResource(R.drawable.jiabei_btn_bg_4);
							jiaBei2 = true;
						} else if (jia == 4) {
							tiLaBtn2.setBackgroundResource(R.drawable.jiabei_btn_bg_2);
							tiLaBtn4.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.jiabei_4_touch, true));
							jiaBei4 = true;
							jia = 0;
						}
					}
					return true;
				}
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
				if (playBtnLayout.getVisibility() == View.VISIBLE && jiaofenLayout.getVisibility() != View.VISIBLE && tilaLayout.getVisibility() != View.VISIBLE && buchu.getVisibility() != View.GONE) {
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
				// 手势提示 加倍，不加倍
				if (tilaLayout.getVisibility() == View.VISIBLE && playBtnLayout.getVisibility() != View.VISIBLE && jiaofenLayout.getVisibility() != View.VISIBLE) {
					callBuJiaBei();
					return true;
				}
				// 手势不叫分
				if (jiaofenLayout.getVisibility() == View.VISIBLE && tilaLayout.getVisibility() != View.VISIBLE && playBtnLayout.getVisibility() != View.VISIBLE) {
					callPoint(0);
					return true;
				}
			} else if (e1.getY() - e2.getY() > mst.adjustYIgnoreDensity(40)) {// 向上滑动
				// 手势提示 出牌 不出牌
				if (playBtnLayout.getVisibility() == View.VISIBLE && jiaofenLayout.getVisibility() != View.VISIBLE && tilaLayout.getVisibility() != View.VISIBLE) {
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
				// 手势提示 加倍
				if (tilaLayout.getVisibility() == View.VISIBLE && playBtnLayout.getVisibility() != View.VISIBLE && jiaofenLayout.getVisibility() != View.VISIBLE) {
					if (jiaBei2) {
						callJiabei(2);
					} else if (jiaBei4) {
						callJiabei(4);
					}
					return true;
				}
				// 手势叫分
				if (jiaofenLayout.getVisibility() == View.VISIBLE && tilaLayout.getVisibility() != View.VISIBLE && playBtnLayout.getVisibility() != View.VISIBLE) {
					if (jiao1) {
						callPoint(1);
					} else if (jiao2) {
						callPoint(2);
					} else if (jiao3) {
						callPoint(3);
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
		// initAgainGameData();
		// // 加入游戏前校验
		// GenericTask checkJoinTask = new CheckJoinTask();
		// TaskParams params = new TaskParams();
		// params.put("homeCode", Database.JOIN_ROOM_CODE);
		// params.put("passwd", "");
		// checkJoinTask.execute(params);
		// taskManager.addTask(checkJoinTask);
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
				// Resources res = getResources();
				// iv.setImageBitmap(BitmapFactory.decodeResource(res,
				// R.drawable.game_items_pic));
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
						// BitmapDrawable curDrawable =
						// ImageUtil.getcutBitmap(HttpURL.URL_PIC_ALL +
						// girls.get(curPage).get("path"), false);
						// 保存当前的背景图
						// if (curDrawable != null) {
						// HttpCache.saveBitmapData(Constant.GAME_BACK,
						// curDrawable.getBitmap());
						// }
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
		// boolean isOpen =
		// PreferenceHelper.getMyPreference().getSetting().getBoolean("shoushi",
		// true);
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
		// int count =
		// PreferenceHelper.getMyPreference().getSetting().getInt("newImage",
		// 0);
		PreferenceHelper.getMyPreference().getEditor().putInt("newImage", count).commit();
		imageNewIv.setVisibility(View.GONE);
	}

	/**
	 * 移动自己的头像
	 */
	private void moveMyHead() {
		// 定时下移我的头像
		if (headTask != null) {
			headTask.stop(true);
			headTask = null;
		}
		headTask = new HeadTask();
		ScheduledTask.addRateTask(headTask, 1500);
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
					case IS_HEAD_ANIM:
						moveMyHead();
						break;
					case IS_FEIJI_ANIM:
						feijiImageView.setVisibility(View.INVISIBLE);
						break;
					case IS_WANGZHA_ANIM:
						wangzhaImageView.setVisibility(View.INVISIBLE);
						break;
					case IS_BAOXIANG_ANIM:
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
//	private List<Poker> moveCard = new ArrayList<Poker>();// 移动的牌组
//	private List<Poker> moveBefor = new ArrayList<Poker>();// 移动之前的牌组

	@Override
	public void onScrollListenner(int e1x, int e1y, int e2x, int e2y, int startIndex) {
		// int left = myCardsTouchLayout.getLeft();
		// int right = myCardsTouchLayout.getRight();
		// int top = myCardsTouchLayout.getTop();
		// int bottom = myCardsTouchLayout.getBottom();
		//
		// Log.i("OnGestureListener", "onScroll-----e1(" + e1x + ", " + e1y +
		// "),  e2(" + e2x + "," + e2y + ")");
		// if (e2x < left || e2x > right || e2y < top) {
		// if (!isAdd) {
		// Log.i("OnGestureListener", "让牌跟着手的触摸位置移动");
		// //出牌
		// moveCard.clear();
		// moveBefor .clear();
		// isChose=false;//是否弹起
		// for (int i=0;i<nowcard.size();i++) {
		// Poker poker=nowcard.get(i);
		// if (poker.ischeck) {
		// moveCard.add(poker);
		// if(i==startIndex){
		// isChose=true;
		// }
		// }
		// moveBefor.add(poker);
		// }
		// //加上当前选中未弹出的牌
		// if(!isChose && nowcard.size()>startIndex){
		// nowcard.get(startIndex).setRiseParams();
		// moveCard.add(nowcard.get(startIndex));
		// }
		// cardAddMoveView(moveCard);
		// isAdd=true;
		// }
		// play1PassLayout.layout(e2x, e2y+play1PassLayout.getHeight(),
		// e2x+play1PassLayout.getWidth(), e2y);
		// }
	}

	@Override
	public void onTouchUpListenner(float x, float y, int startIndex) {
		// int left = myCardsTouchLayout.getLeft();
		// int right = myCardsTouchLayout.getRight();
		// int top = myCardsTouchLayout.getTop();
		// int bottom = myCardsTouchLayout.getBottom();
		//
		// Log.i("OnGestureListener", "myCardsTouchLayout  位置 x：" + x + "  y:" +
		// y + "   left:" + left + "   right:" + right + "   top:" + top+
		// "  bottom" + bottom);
		// if ((playBtnLayout.getVisibility()==View.VISIBLE )&&(x < left || x >
		// right || y < top)) {
		// Log.i("OnGestureListener", "可以检测出牌");
		// playCard();
		// } else {
		// Log.i("OnGestureListener", "把牌放回手牌区");
		// }
		//
		// // for(int i=0;i<nowcard.size();i++){
		// //
		// // Log.i("OnGestureListener", i+":"+nowcard.get(i).getNumber());
		// // }
		// isAdd=false;
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