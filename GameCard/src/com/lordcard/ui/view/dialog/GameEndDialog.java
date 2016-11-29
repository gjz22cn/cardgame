package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lordcard.common.schedule.AutoTask;
import com.lordcard.common.schedule.ScheduledTask;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.AudioPlayUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.common.util.PatternUtils;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.Play;
import com.lordcard.network.http.GameCache;
import com.lordcard.ui.base.IGameView;


/**
 * 比赛场游戏结束界面
 * @author Administrator
 *
 */
public class GameEndDialog extends Dialog implements IGameView, android.view.View.OnClickListener {

	/**退出结束对话框*/
	private static final int EXIT = 1212;
	/**返回游戏大厅*/
	private static final int GO_BACK = 1211;
	/**再来一局*/
	private static final int GO_AGAIN = 1213;
	/**显示IQ升级对话框(小级)*/
	private static final int SHOW_IQ_GRADE_MIN = 1214;
	/**升级(大级)*/
	public static final int GED_SHOW_IQ_GRADE_MAX = 1215;
	/**显示充值提示对话框*/
	//public static final int SHOW_ADD_BEEN = 1217;
//	/**显示等待对话框*/
//	private static final int VISIBLE_VIEW = 121;
	/**将按钮设成可用*/
	public static final int ENABLE_DIALOG = 1216;
	private int nextPlay = -1;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private Context context;
	private int order;
	private int gameNo=1;
	private int maxGameNum=10;
	private LinkedList<Play> users = null;
	private Handler mHandler;
	private Handler handler;
	private RelativeLayout mainLayout;
	private ImageView headIv1, headIv2, headIv3 = null;// 玩家头像
	private ImageView titlaSexIv;//标题人物图片
	private ImageView imgFreeze;
	private TextView nameTv1, nameTv2, nameTv3 = null;// 玩家昵称
	private TextView zhiDouTv1, zhiDouTv2, zhiDouTv3 = null;//金豆
	private TextView zhiZuangTv1, zhiZuangTv2, zhiZuangTv3 = null;//钻石
	private TextView zhiLiTv1, zhiLiTv2, zhiLiTv3 = null;//经验
	private TextView nowZhiDouTv, nowZhiZuangTv, ZhiShangTv = null;//总倍数，底注，当前金豆，当前钻石，等级
	private Button againBtn, backBtn;//关闭，再来一局，返回，微博分享
	private RelativeLayout bottomLl;//底部跳转按钮容器控件
	private IqGradeDialog mIqMaxGradeDialog = null;
	private GameIqUpgradeDialog mIqMinUpgradeDialog = null;
	private AutoTask goOutTask = null;//退出游戏到大厅定时器
	private AutoTask showMinUpgradeTask = null;//显示升小级对话框定时器
	private AutoTask showMaxUpgradeTask = null;//显示升大级对话框定时器
	private AutoTask toWaitViewTask = null;//跳转到等待界面定时器

	public GameEndDialog(Context context, List<Play> list, int order, Handler handler) {
		super(context, R.style.process_dialog);
		this.context = context;
		this.order = order;
		this.users = new LinkedList<Play>(list);
		this.handler = handler;
	}

	@SuppressLint("HandlerLeak")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(new BitmapDrawable());
		setContentView(R.layout.doudizhu_end);
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case EXIT:
						handler.sendEmptyMessage(24);
						dismiss();
						release();
						break;
					case GO_BACK://返回游戏大厅
						release();
						handler.sendEmptyMessage(25);
						dismiss();
						break;
					case GO_AGAIN://再来一局
						dismiss();
						release();
						//handler.sendEmptyMessage(26);
						handler.sendEmptyMessage(24);
						break;
					case SHOW_IQ_GRADE_MIN://显示IQ升级对话框(小级)
						String result = msg.getData().getString("getCelebratedText");
						boolean isDiZhu = msg.getData().getBoolean("isCall", false);
						String gender = Database.userMap.get(order).getGender();//性别 0保密1女2男
						Map<String, String> headPath = Database.userMap.get(order).getIqImg();
						if (null == mIqMinUpgradeDialog || !mIqMinUpgradeDialog.isShowing()) {
							mIqMinUpgradeDialog = new GameIqUpgradeDialog(context, mHandler, result, gender, isDiZhu, headPath, GED_SHOW_IQ_GRADE_MAX);
							android.view.WindowManager.LayoutParams lay = mIqMinUpgradeDialog.getWindow().getAttributes();
							setParams(lay);
							Window window = mIqMinUpgradeDialog.getWindow();
							window.setGravity(Gravity.CENTER); //此处可以设置dialog显示的位置
							window.setWindowAnimations(R.style.mystyle2); //添加动画
							mIqMinUpgradeDialog.show();
						}
						break;
					case GED_SHOW_IQ_GRADE_MAX://升级(大级)
						if (null == mIqMaxGradeDialog || !mIqMaxGradeDialog.isShowing()) {
							//弹出等级等级对话框
							int iq = -1;
							GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
							if (null != cacheUser) {
								iq = cacheUser.getIq();
							}
							mIqMaxGradeDialog = new IqGradeDialog(context, iq);
							mIqMaxGradeDialog.setContentView(R.layout.iq_grade_dialog);
							android.view.WindowManager.LayoutParams lay1 = mIqMaxGradeDialog.getWindow().getAttributes();
							setParams(lay1);
							Window window1 = mIqMaxGradeDialog.getWindow();
							window1.setGravity(Gravity.BOTTOM); //此处可以设置dialog显示的位置
							window1.setWindowAnimations(R.style.mystyle); //添加动画
							mIqMaxGradeDialog.show();
						}
						break;
					case ENABLE_DIALOG:
						enableButtons(true);
						break;
					default:
						break;
				}
			}
		};
		for (Play end : users) {
			gameNo = end.getGameNo();
		}
		initView();
		refreshData();
		//3分钟后自动离开
		stopGoOutTask();
		goOutTask = new AutoTask() {

			public void run() {
				mHandler.sendEmptyMessage(GO_BACK);
			}
		};
		ScheduledTask.addDelayTask(goOutTask, (1000 * 60 * 3));
	}

	public void initView() {
		mainLayout = (RelativeLayout) findViewById(R.id.dzed_top_layout);
		mst.adjustView(mainLayout);
		headIv1 = (ImageView) findViewById(R.id.dzed_head_1);
		headIv2 = (ImageView) findViewById(R.id.dzed_head_2);
		headIv3 = (ImageView) findViewById(R.id.dzed_head_3);
		titlaSexIv = (ImageView) findViewById(R.id.dzed_lose_win_head_iv);
		bottomLl = (RelativeLayout) findViewById(R.id.dzed_c_rl_bottom_ll);
		/**是否显示冻结图标**/
		imgFreeze = (ImageView) findViewById(R.id.dzed_img_freeze);
		imgFreeze.setVisibility(View.GONE);
		nameTv1 = (TextView) findViewById(R.id.dzed_nickname_1);
		nameTv2 = (TextView) findViewById(R.id.dzed_nickname_2);
		nameTv3 = (TextView) findViewById(R.id.dzed_nickname_3);
		zhiDouTv1 = (TextView) findViewById(R.id.dzed_zhidou_1);
		zhiDouTv2 = (TextView) findViewById(R.id.dzed_zhidou_2);
		zhiDouTv3 = (TextView) findViewById(R.id.dzed_zhidou_3);
		zhiZuangTv1 = (TextView) findViewById(R.id.dzed_zhizuang_1);
		zhiZuangTv2 = (TextView) findViewById(R.id.dzed_zhizuang_2);
		zhiZuangTv3 = (TextView) findViewById(R.id.dzed_zhizuang_3);
		zhiLiTv1 = (TextView) findViewById(R.id.dzed_zhili_1);
		zhiLiTv2 = (TextView) findViewById(R.id.dzed_zhili_2);
		zhiLiTv3 = (TextView) findViewById(R.id.dzed_zhili_3);
		nowZhiDouTv = (TextView) findViewById(R.id.dzed_now_zhidou);
		nowZhiZuangTv = (TextView) findViewById(R.id.dzed_now_zhizuang);
		ZhiShangTv = (TextView) findViewById(R.id.dzed_now_zhishang);
		againBtn = (Button) findViewById(R.id.dzed_again);
		againBtn.setOnClickListener(this);
		backBtn = (Button) findViewById(R.id.dzed_back);
		backBtn.setOnClickListener(this);

		// 如果是超快赛，则定时跳转到等待界面
		if (1 == Database.JOIN_ROOM.getRoomType()) {
			bottomLl.setVisibility(View.INVISIBLE);
			stopToWaitViewTask();
			toWaitViewTask = new AutoTask() {

				public void run() {
					mHandler.sendEmptyMessage(EXIT);
				}
			};
			ScheduledTask.addDelayTask(toWaitViewTask, 3000);
		}
		if (gameNo == maxGameNum) {
			againBtn.setVisibility(View.INVISIBLE);
		} else {
			backBtn.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 将按钮设成是否可用
	 * @param isEnable
	 */
	private void enableButtons(boolean isEnable) {
		if (againBtn != null)
			againBtn.setEnabled(isEnable);
		if (backBtn != null)
			backBtn.setEnabled(isEnable);
	}

	/**
	 * 刷新结算数据
	 */
	public void refreshData() {
		// 判断是否有人逃跑
		boolean hasEscape = false;
		for (Play end : users) {
			if (end.isEscape())
				// 有人逃跑
				hasEscape = true;
		}
		// 判断是否是地主赢
		boolean isdizhu = false;
		if (hasEscape) {
			for (Play end : users) {
				// 有人逃跑，且不是地主
				if (end.isEscape() && !end.isCall()) {
					isdizhu = true;
				}
			}
		} else {
			for (Play end : users) {
				if (end.getCount() == 0 && end.isCall()) {
					isdizhu = true;
				}
			}
		}
		for (Play end : users) {
			// 如果是自己的顺序
			if (end.getOrder() == order) {
				theFirstItem(isdizhu, end);
			} else if (nextPlay == -1) {
				theSecondItem(isdizhu, end);
			} else {
				theThreeItem(isdizhu, end);
			}
		}
	}

	/**设置第一项的数据
	 * @param isdizhu
	 * @param dizhuStove
	 * @param onlyDizhu
	 * @param end
	 */
	@SuppressWarnings("unchecked")
	private void theFirstItem(boolean isdizhu, final Play end) {
		boolean isWin = false;
		zhiDouTv1.setText(PatternUtils.changeZhidou(Math.round(end.getPayment())));
		nameTv1.setText(end.getNickMap().get(end.getOrder()));
		zhiZuangTv1.setText(PatternUtils.changeZhidou(Math.round(end.getPayment())));
		AudioPlayUtils.isPlay = true;
		AudioPlayUtils.isGameEnd = true;
		nowZhiDouTv.setText("" + PatternUtils.changeZhidou(0 > Math.round(end.getBean()) ? 0 : Math.round(end.getBean())));
		nowZhiZuangTv.setText("" + PatternUtils.changeZhidou(0 > Database.userMap.get(order).getDiamSum() ? 0 : Database.userMap.get(order).getDiamSum()));
		ZhiShangTv.setText("" + end.getIq());
		//修改缓存数据
		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		if (null != cacheUser) {
			long bean = (long) end.getBean();
			cacheUser.setBean(bean);
			GameCache.putObj(CacheKey.GAME_USER, cacheUser);
		}
		/**如果当前智不足以支付在本房间再玩一局，弹出提示充值对话框*/
		if (null != Database.JOIN_ROOM && end.getBean() < Database.JOIN_ROOM.getLimit()) {
			Log.d("freshUserInfo", "end.getBean()<Database.JOIN_ROOM.getLimit()：" + (end.getBean() < Database.JOIN_ROOM.getLimit()));
		} else {
			Log.i("freshUserInfo", "Database.JOIN_ROOM==null");
		}
		if (null != Database.JOIN_ROOM) {
			Log.i("freshUserInfo", "end.getBean():" + end.getBean() + "<Database.JOIN_ROOM.getLimit():" + Database.JOIN_ROOM.getLimit());
		}
		//是否升级
		if (end.isUpgrade()) {
			stopGoOutTask2();
			showMinUpgradeTask = new AutoTask() {

				public void run() {
					Message msg = new Message();
					Bundle b = new Bundle();
					//提示内容
					b.putString("getCelebratedText", end.getCelebratedText());
					//是否是地主
					b.putBoolean("isCall", end.isCall());
					msg.what = SHOW_IQ_GRADE_MIN;
					msg.setData(b);
					mHandler.sendMessage(msg);
				}
			};
			ScheduledTask.addDelayTask(showMinUpgradeTask, (700));
			int isTitle = end.getIsTitle() == null ? 0 : end.getIsTitle();
			if (1 == isTitle) {
				stopshowUpgradeTask();
				showMaxUpgradeTask = new AutoTask() {

					public void run() {
						mHandler.sendEmptyMessage(GED_SHOW_IQ_GRADE_MAX);
					}
				};
				ScheduledTask.addDelayTask(showMaxUpgradeTask, (200));
			}
		}
		zhiLiTv1.setText("" + end.getAddIntellect());
		if (end.isCall()) { // 自己是地主
			ActivityUtils.setHead(context, headIv1, Database.userMap.get(end.getOrder()).getGender(), true, Database.userMap.get(end.getOrder()).getIqImg(), false);
			if (isdizhu) { // 地主赢 
				findViewById(R.id.dzed_lose_win).setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.game_end_dialog_win_text, true));
				findViewById(R.id.dzed_lose_win_top_iv).setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.iq_grade_dialog_win_bg, true));
				AudioPlayUtils.getInstance().playMusic(false, R.raw.win); // 胜利
				setTitalSex(Database.userMap.get(end.getOrder()).getGender(), true, true);
				isWin = true;
			} else {
				findViewById(R.id.dzed_lose_win).setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.game_end_dialog_lose_text, true));
				findViewById(R.id.dzed_lose_win_top_iv).setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.iq_grade_dialog_lose_bg, true));
				AudioPlayUtils.getInstance().playMusic(false, R.raw.lose); // 失败
				setTitalSex(Database.userMap.get(end.getOrder()).getGender(), false, false);
			}
		} else { // 自己是农民
			ActivityUtils.setHead(context, headIv1, Database.userMap.get(end.getOrder()).getGender(), false, Database.userMap.get(end.getOrder()).getIqImg(), false);
			if (isdizhu) { // 地主赢
				findViewById(R.id.dzed_lose_win).setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.game_end_dialog_lose_text, true));
				findViewById(R.id.dzed_lose_win_top_iv).setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.iq_grade_dialog_lose_bg, true));
				AudioPlayUtils.getInstance().playMusic(false, R.raw.lose); // 失败
				setTitalSex(Database.userMap.get(end.getOrder()).getGender(), true, false);
			} else {
				findViewById(R.id.dzed_lose_win).setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.game_end_dialog_win_text, true));
				findViewById(R.id.dzed_lose_win_top_iv).setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.iq_grade_dialog_win_bg, true));
				AudioPlayUtils.getInstance().playMusic(false, R.raw.win);
				setTitalSex(Database.userMap.get(end.getOrder()).getGender(), false, true);
				isWin = true;
			}
		}
		AudioPlayUtils.isPlay = false;
		//局数、输赢局数、金豆输赢数、经验增长数、当前金豆数
		try {
			int playInningsCount = 0;
			int loseInningsCount = 0;
			int winInningsCount = 0;
			int iqRiseCount = 0;
			int iq = 0;
			HashMap<String, String> gamePlayMsgMap = (HashMap<String, String>) GameCache.getObj(CacheKey.KEY_PLAY_GAME_MSG);
			if (null != gamePlayMsgMap) {
				if (gamePlayMsgMap.containsKey(Constant.KEY_COUNT_PLAY_INNINGS)) {
					playInningsCount = Integer.parseInt(gamePlayMsgMap.get(Constant.KEY_COUNT_PLAY_INNINGS));
				}
				playInningsCount += 1;
				if (isWin) {
					if (gamePlayMsgMap.containsKey(Constant.KEY_COUNT_WIN_INNINGS)) {
						winInningsCount = Integer.parseInt(gamePlayMsgMap.get(Constant.KEY_COUNT_WIN_INNINGS));
					}
					winInningsCount += 1;
				} else {
					if (gamePlayMsgMap.containsKey(Constant.KEY_COUNT_LOSE_INNINGS)) {
						loseInningsCount = Integer.parseInt(gamePlayMsgMap.get(Constant.KEY_COUNT_LOSE_INNINGS));
					}
					loseInningsCount += 1;
				}
				if (gamePlayMsgMap.containsKey(Constant.KEY_COUNT_IQ_RISE)) {
					iqRiseCount = Integer.parseInt(gamePlayMsgMap.get(Constant.KEY_COUNT_IQ_RISE));
				}
				if (gamePlayMsgMap.containsKey(Constant.KEY_IQ)) {
					iq = Integer.parseInt(gamePlayMsgMap.get(Constant.KEY_IQ));
				}
				if (0 == iq) {//之前没记录过等级
					iqRiseCount = end.getIq();
				} else {
					iqRiseCount = end.getIq() - iq;
				}
			}
			gamePlayMsgMap.put(Constant.KEY_COUNT_PLAY_INNINGS, String.valueOf(playInningsCount));
			gamePlayMsgMap.put(Constant.KEY_COUNT_WIN_INNINGS, String.valueOf(winInningsCount));
			gamePlayMsgMap.put(Constant.KEY_COUNT_LOSE_INNINGS, String.valueOf(loseInningsCount));
			gamePlayMsgMap.put(Constant.KEY_COUNT_IQ_RISE, String.valueOf(iqRiseCount));
			gamePlayMsgMap.put(Constant.KEY_IQ, String.valueOf(end.getIq()));
			GameCache.putObj(CacheKey.KEY_PLAY_GAME_MSG, gamePlayMsgMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setParams(android.view.WindowManager.LayoutParams lay) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		Rect rect = new Rect();
		View view = getWindow().getDecorView();
		view.getWindowVisibleDisplayFrame(rect);
		lay.height = dm.heightPixels - rect.top;
		lay.width = dm.widthPixels;
	}

	/**
	 * 设置标题人物图片
	 * @param gender 性别
	 * @param isDizhu 是否是地主
	 * @param iswin 是否赢
	 */
	private void setTitalSex(String gender, boolean isDizhu, boolean iswin) {
		if (isDizhu) {//是否是地主
			if (gender.equals("1")) {//性别 0保密/1女/2男
				if (iswin) {
					titlaSexIv.setImageDrawable(ImageUtil.getResDrawable(R.drawable.game_end_dialog_man_win, true));
				} else {
					titlaSexIv.setImageDrawable(ImageUtil.getResDrawable(R.drawable.game_end_dialog_man_lose, true));
				}
			} else {
				if (iswin) {
					titlaSexIv.setImageDrawable(ImageUtil.getResDrawable(R.drawable.game_end_dialog_man_win, true));
				} else {
					titlaSexIv.setImageDrawable(ImageUtil.getResDrawable(R.drawable.game_end_dialog_man_lose, true));
				}
			}
		} else {
			if (gender.equals("1")) {//性别 0保密/1女/2男
				if (iswin) {
					titlaSexIv.setImageDrawable(ImageUtil.getResDrawable(R.drawable.game_end_dialog_man_win, true));
				} else {
					titlaSexIv.setImageDrawable(ImageUtil.getResDrawable(R.drawable.game_end_dialog_man_lose, true));
				}
			} else {
				if (iswin) {
					titlaSexIv.setImageDrawable(ImageUtil.getResDrawable(R.drawable.game_end_dialog_man_win, true));
				} else {
					titlaSexIv.setImageDrawable(ImageUtil.getResDrawable(R.drawable.game_end_dialog_man_lose, true));
				}
			}
		}
	}

	/**
	 * 设置第三项的数据
	 * @param isdizhu
	 * @param dizhuStove
	 * @param onlyDizhu
	 * @param end
	 */
	private void theThreeItem(boolean isdizhu, Play end) {
		zhiDouTv3.setText(PatternUtils.changeZhidou(Math.round(end.getPayment())));
		zhiZuangTv3.setText(PatternUtils.changeZhidou(Math.round(end.getPayment())));
		nameTv3.setText(end.getNickMap().get(end.getOrder()));
		zhiLiTv3.setText("" + end.getAddIntellect());
		if (end.isCall()) { // 自己是地主
			ActivityUtils.setHead(context, headIv3, Database.userMap.get(end.getOrder()).getGender(), true, Database.userMap.get(end.getOrder()).getIqImg(), false);
		} else { // 自己是农民
			ActivityUtils.setHead(context, headIv3, Database.userMap.get(end.getOrder()).getGender(), false, Database.userMap.get(end.getOrder()).getIqImg(), false);
		}
	}

	/**
	 * 设置第二项的数据
	 * @param isdizhu
	 * @param dizhuStove
	 * @param onlyDizhu
	 * @param end
	 */
	private void theSecondItem(boolean isdizhu, Play end) {
		nextPlay = end.getOrder();
		zhiDouTv2.setText(PatternUtils.changeZhidou(Math.round(end.getPayment())));
		zhiZuangTv2.setText(PatternUtils.changeZhidou(Math.round(end.getPayment())));
		nameTv2.setText(end.getNickMap().get(end.getOrder()));
		zhiLiTv2.setText("" + end.getAddIntellect());
		if (end.isCall()) { // 自己是地主
			ActivityUtils.setHead(context, headIv2, Database.userMap.get(end.getOrder()).getGender(), true, Database.userMap.get(end.getOrder()).getIqImg(), false);
		} else { // 自己是农民
			ActivityUtils.setHead(context, headIv2, Database.userMap.get(end.getOrder()).getGender(), false, Database.userMap.get(end.getOrder()).getIqImg(), false);
		}
	}

	public boolean isWinGame() {
		boolean isWin = false;
		// 判断是否有人逃跑
		boolean hasEscape = false;
		for (Play end : users) {
			if (end.isEscape())
				hasEscape = true; // 有人逃跑
		}
		// 判断是否是地主赢
		boolean islandLordWin = false;
		if (hasEscape) {
			for (Play end : users) {
				if (end.isEscape() && !end.isCall()) { // 有人逃跑，且不是地主
					islandLordWin = true;
				}
			}
		} else {
			for (Play end : users) {
				if (end.getCount() == 0 && end.isCall()) {
					islandLordWin = true;
				}
			}
		}
		for (Play endPlay : users) {
			if (endPlay.getOrder() == order) {
				isWin = (endPlay.isCall()) ? islandLordWin : (!islandLordWin);
			}
		}
		return isWin;
	}

	private void release() {
		AudioPlayUtils.isGameEnd = false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mHandler.sendEmptyMessage(GO_BACK);
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dzed_again://再来一局
				mHandler.sendEmptyMessage(GO_AGAIN);
				break;
			case R.id.dzed_back://返回大厅
				mHandler.sendEmptyMessage(GO_BACK);
				//发消息关闭游戏界面
				break;
			default:
				break;
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		if (null != mIqMaxGradeDialog && mIqMaxGradeDialog.isShowing()) {
			mIqMaxGradeDialog.dismiss();
			mIqMaxGradeDialog = null;
		}
		if (null != mIqMinUpgradeDialog && mIqMinUpgradeDialog.isShowing()) {
			mIqMinUpgradeDialog.dismiss();
			mIqMinUpgradeDialog = null;
		}
		stopGoOutTask();
		stopshowUpgradeTask();
		stopGoOutTask2();
		stopToWaitViewTask();
	}

	private void stopGoOutTask() {
		if (goOutTask != null) {
			goOutTask.stop(true);
			goOutTask = null;
		}
	}

	private void stopshowUpgradeTask() {
		if (showMinUpgradeTask != null) {
			showMinUpgradeTask.stop(true);
			showMinUpgradeTask = null;
		}
	}

	private void stopGoOutTask2() {
		if (showMaxUpgradeTask != null) {
			showMaxUpgradeTask.stop(true);
			showMaxUpgradeTask = null;
		}
	}

	private void stopToWaitViewTask() {
		if (null != toWaitViewTask) {
			toWaitViewTask.stop(true);
			toWaitViewTask = null;
		}
	}
}
