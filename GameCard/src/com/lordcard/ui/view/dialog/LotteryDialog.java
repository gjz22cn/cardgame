package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lordcard.common.anim.AnimUtils;
import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.util.AudioPlayUtils;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.GameUserGoods;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.lordcard.ui.interfaces.PrizeInterface;


@SuppressLint("HandlerLeak")
public class LotteryDialog extends Dialog implements PrizeInterface, android.view.View.OnClickListener {

	private Context context;
	private boolean isChouJiang;
	private LinearLayout lotLeft, lotTop, lotRight, lotBottom;
	private Button startBtn, dfBtn, lotCJ5Btn, lotCJ10Btn, lotWP, miss;
	private Lottery tHF5_1, tCJ5_1, tZD_1, tZD5_1, tHF10_1, tCJ_2, tHF_2;
	private Lottery rCJ10_1, rZD10_1, rHF5_1;
	private Lottery bCJ_1, bHF_10, bCJ1_1, bCJ_2, bZD5_1, bXX, bZD100;
	private Lottery lXX, lHF5_1, lCJ1_1;
	private Lottery zhizuan_100, jiangquan_20, zhidou_500, zhizuan_2, jiangquan_10, zhizuan_20, huafei_10;
	private Lottery zhizuan_4, huafei_50, zhidou_1;
	private Lottery zhizuan_5, huafei_1, zhidou_2, jiangquan_5, zhizuan_10, jiangquan_30, zhizuan_500;
	private Lottery xiexie, zhizuan_1, zhidou_5;
	public TextView lotPm, lotJn;
	private int quanNum = 0;
	private String saveLottery;
	public static final String SAVE_LOTTERY = "saveLottery";
	private RelativeLayout layout, luckLayout;
	private Button zhizuanBtn, choujiangBtn;
	private RelativeLayout changeBgLayout;
	public static List<Lottery> lotterItemList; // 抽奖项集合
	public static List<Lottery> zhizuanItemList; // 钻石项集合
	public static int speed; // 抽奖速度
	public static boolean error = false; // 是否异常结束
	public static boolean stop = true; // 是否停止
	public static boolean lucking = false; // 是否下在抽奖
	private int multiple = 0; // 抽奖倍数
	private String luckResult = "bXX_1"; // 抽奖结束
	public int minRunCount, runIndex; // 抽奖最小转动圈数,当前转动圈数
	public static boolean voiceON = true;
	private Map<Integer, Bitmap> bitmapCache;
	//private GoodsDialog gDialog;
	/**
	 * a1 2000金豆抽奖 a2 10000金豆抽奖 a3 20000金豆抽奖 a4 50000金豆抽奖 a5 100000金豆抽奖 a6
	 * 1000000金豆抽奖 a7 5000000金豆抽奖 a8 5000金豆抽奖
	 */
	public String[] a = { "a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8" };
	/**
	 * a1 10000金豆抽奖 a2 20000金豆抽奖 a3 50000金豆抽奖 a4 5000000金豆抽奖
	 */
	public String[] zd = { "a1", "a2", "a3", "a4" };
	/**
	 * b1 1张抽奖券 b2 2张抽奖券 b3 3张抽奖券 b4 5张抽奖券 b5 10张抽奖券 ,b6 20张抽奖券,b7 30张抽奖券,b8
	 * 50张抽奖券
	 */
	public String[] b = { "b1", "b2", "b3", "b4", "b5", "b6", "b7" };
	/**
	 * b1 5张抽奖券 b2 10张抽奖券 b3 20张抽奖券 b4 30张抽奖券
	 */
	public String[] cj = { "b1", "b2", "b3", "b4" };
	/**
	 * h1 1元话费券 h2 10元话费券 h3 50元话费券 h4 0.1元话费券
	 */
	public String[] hf = { "h1", "h2", "h3", "h4" };
	public String[] sx = { "e1", "e2" };
	public String[] hc = { "d1" };
	// d1 合成剂 e1 猪首 e2狗首
	// h1一元话费
	/**
	 * z1 1个钻石 ,z2 2个钻石 ,z3 4个钻石 ,z4 5个钻石 ,z5 10个钻石,z6 20个钻石,z7 100个钻石,z8 500个钻石
	 */
	public String[] zz = { "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8" };
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private RelativeLayout layout2;

	public LotteryDialog(Context context) {
		super(context, R.style.lotdialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lottery_dialog);
		bitmapCache = new HashMap<Integer, Bitmap>();
		layout2 = (RelativeLayout) findViewById(R.id.mm_layout);
		mst.adjustView(layout2, false);
		setInitView();
		queryConpon();
	}

	/**
	 * 开始抽奖
	 * 
	 * @param count
	 */
	public synchronized void startLotZhizuan(int count) {
		if (!lucking) {
			//MobclickAgent.onEvent(CrashApplication.getInstance(), "钻石抽奖" + count + "倍");
			if (!lotJn.getText().toString().equals("")) {
				int tempCoupon = Integer.parseInt(lotJn.getText().toString());
				if (tempCoupon >= count) {
					//MobclickAgent.onEvent(CrashApplication.getInstance(), "幸运大抽奖 " + count + " 张兑奖", 1);
					stop = false;
					lucking = true;
					multiple = count / 10;
					setCoverAllVisible(); // 设置所有的非高亮 开始抽奖
					runIndex = 0;
					speed = 30;
					luckResult = "xiexie"; // 默认不中奖
					minRunCount = (new Random().nextInt(2)) + 2;
					luckCheck(count); // 服务器获取抽奖结束
					setAllZhizuanInvisuble();
					lotJn.setText(String.valueOf(quanNum - count));
					zhizuan_100.handle(false);
				} else {
					Toast.makeText(context, "您的抽奖券数不足", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.dismiss();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 开始抽奖
	 * 
	 * @param count
	 */
	public synchronized void startLottery(int count) {
		if (!lucking) {
			//MobclickAgent.onEvent(CrashApplication.getInstance(), "幸运大抽奖" + count + "倍");
			// } else {
			try {
				int tempCoupon = Integer.parseInt(lotJn.getText().toString());
				if (tempCoupon >= count) {
					//MobclickAgent.onEvent(CrashApplication.getInstance(), "幸运大抽奖 " + count + " 张兑奖", 1);
					stop = false;
					lucking = true;
					multiple = count;
					setCoverAllVisible(); // 设置所有的非高亮 开始抽奖
					runIndex = 0;
					speed = 30;
					luckResult = "lXX"; // 默认不中奖
					minRunCount = (new Random().nextInt(2)) + 2;
					luckJoy(count); // 服务器获取抽奖结束
					setAllInnerInvisuble();
					lotJn.setText(String.valueOf(quanNum - count));
					tHF5_1.handle(true);
				} else {
					Toast.makeText(context, "您的抽奖券数不足", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {}
		}
	}

	/**
	 * 点击抽奖后去查抽奖结果
	 * 
	 * @param count
	 */
	public void luckJoy(final int count) {
		new Thread(new Runnable() {

			public void run() {
				GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("loginToken",cacheUser.getLoginToken());
				paramMap.put("count", String.valueOf(count));
				try {
					String result = HttpUtils.post(HttpURL.COUPON_LUCKY_URL, paramMap);
					if (HttpRequest.FAIL_STATE.equals(result) || HttpRequest.TOKEN_ILLEGAL.equals(result) || HttpRequest.NO_FULL_GOODS.equals(result) || HttpRequest.NULL_PRIZE.equals(result)) {
						handler.sendEmptyMessage(Integer.parseInt(result));
					} else {
						String[] parize = result.split("-");
						String valueName = parize[0];
						List<Lottery> tempList = new ArrayList<Lottery>();
						for (int i = 0; i < lotterItemList.size(); i++) {
							if (lotterItemList.get(i).getValue().equals(valueName)) {
								tempList.add(lotterItemList.get(i));
							}
						}
						if (tempList.size() > 0) {
							int index = (int) (Math.random() * (tempList.size()));
							luckResult = tempList.get(index).getName();
						}
						tempList.clear();
						tempList = null;
					}
				} catch (Exception e) {
					handler.sendEmptyMessage(Integer.parseInt(HttpRequest.NULL_PRIZE)); // 异常处理(记为不中奖)
				}
			}
		}).start();
	}

	/**
	 * 点击抽奖后去查抽奖结果
	 * 
	 * @param count
	 */
	public void luckCheck(final int count) {
		new Thread(new Runnable() {

			public void run() {
				GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("loginToken",cacheUser.getLoginToken());
				paramMap.put("count", String.valueOf(count));
				try {
					String result = HttpUtils.post(HttpURL.COUPON_ZHIZUAN_URL, paramMap);
					if (HttpRequest.FAIL_STATE.equals(result) || HttpRequest.TOKEN_ILLEGAL.equals(result) || HttpRequest.NO_FULL_GOODS.equals(result) || HttpRequest.NULL_PRIZE.equals(result)) {
						handler.sendEmptyMessage(Integer.parseInt(result));
					} else {
						String[] parize = result.split("-");
						String valueName = parize[0];
						List<Lottery> tempList = new ArrayList<Lottery>();
						for (int i = 0; i < zhizuanItemList.size(); i++) {
							if (zhizuanItemList.get(i).getValue().equals(valueName)) {
								tempList.add(zhizuanItemList.get(i));
							}
						}
						if (tempList.size() > 0) {
							int index = (int) (Math.random() * (tempList.size()));
							luckResult = tempList.get(index).getName();
						}
						tempList.clear();
						tempList = null;
					}
				} catch (Exception e) {
					handler.sendEmptyMessage(Integer.parseInt(HttpRequest.NULL_PRIZE)); // 异常处理(记为不中奖)
				}
			}
		}).start();
	}

	/**
	 * 查询抽奖券
	 */
	public void queryConpon() {
		new Thread(new Runnable() {

			public void run() {
				GameUserGoods gameUserGoods = HttpRequest.getGameUserGoods(false);
				if (gameUserGoods != null) {
					quanNum = gameUserGoods.getCouponNum();
					handler.sendEmptyMessage(12);
				}
				gameUserGoods = null;
			}
		}).start();
	}

	/**
	 * 设置所有移动框不可见
	 * 
	 * @param medal
	 */
	public static void setInvisuble(Lottery medal) {
		medal.getInnerLayout().setVisibility(View.INVISIBLE);
	}

	/**
	 * 与主线程交互的hanlder
	 */
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 0:
					queryConpon();
					break;
				case 1:
					stop = true;
					runIndex = 0;
					error = true;
					DialogUtils.mesTip("抽奖失败!", false);
					break;
				case 3:
					stop = true;
					runIndex = 0;
					error = true;
					DialogUtils.reLogin(Database.currentActivity);
					break;
				case 8:
					stop = true;
					runIndex = 0;
					error = true;
					DialogUtils.mesTip("抽奖券不足!", false);
					break;
				case 10:
					if (isChouJiang) {
						luckResult = "lXX";
					} else {
						luckResult = "xiexie";
					}
					break;
				case 12:
					lotJn.setText(String.valueOf(quanNum));
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 解析抽奖结果
	 */
	@Override
	public String prizeName(Lottery lot) {
		String name = lot.getName();
		if (lot.getIndex().equals("start")) {
			++runIndex;
		}
		if (!lot.getValue().equals("10")) {
			lotPm.setText(lot.getDetail() + "*" + multiple);
		} else {
			lotPm.setText(lot.getDetail());
		}
		if (minRunCount == 2) {
			speed += 8;
		} else if (minRunCount == 3) {
			speed += 5;
		} else if (minRunCount == 4) {
			speed += 5;
		} else {
			speed += 3;
		}
		if (name.equals(luckResult)) {
			if (runIndex >= minRunCount) {
				stop = true;
				runIndex = 0;
				lotPm.startAnimation(AnimUtils.getMiniAnimation(300));
				AudioPlayUtils.getInstance().playSoundMusic(false);
				// stopTimer();
				saveLottery(lot);
				queryConpon();
			}
		}
		return name;
	}

	/**
	 * 保存抽奖结果
	 * 
	 * @param lot
	 */
	public void saveLottery(Lottery lot) {
		SharedPreferences sharedData = context.getSharedPreferences(Constant.GAME_ACTIVITE, Context.MODE_PRIVATE);
		saveLottery = sharedData.getString(SAVE_LOTTERY, "");
		if (!saveLottery.contains(lot.getImageName())) {
			if (saveLottery.trim().length() == 0) {
				saveLottery = lot.getImageName();
			} else {
				saveLottery = saveLottery + "," + lot.getImageName();
			}
			Editor editor = sharedData.edit();
			editor.putString(SAVE_LOTTERY, saveLottery.trim());
			editor.commit();
		}
	}

	/**
	 * 初始化界面和监听
	 */
	public void setInitView() {
		isChouJiang = true;
		lotterItemList = new ArrayList<Lottery>();
		layout = (RelativeLayout) findViewById(R.id.lot_bg);
		layout.setBackgroundDrawable(ImageUtil.getDrawableResId(R.drawable.lottery_bg, true, true));
		luckLayout = (RelativeLayout) findViewById(R.id.lot_luck);
		luckLayout.setBackgroundDrawable(ImageUtil.getDrawableResId(R.drawable.lot_right_text, true, true));
		// 抽奖框4边布局
		lotTop = (LinearLayout) findViewById(R.id.lot_top);
		lotLeft = (LinearLayout) findViewById(R.id.lot_left);
		lotRight = (LinearLayout) findViewById(R.id.lot_right);
		lotBottom = (LinearLayout) findViewById(R.id.lot_bottom);
		changeBgLayout = (RelativeLayout) findViewById(R.id.change_table);
		choujiangBtn = (Button) findViewById(R.id.choujiang_btn); // 抽奖
		zhizuanBtn = (Button) findViewById(R.id.zizhuan_btn);// 抽钻石
		choujiangBtn.setOnClickListener(this);
		zhizuanBtn.setOnClickListener(this);
		lotPm = (TextView) findViewById(R.id.lot_pm); // 抽奖物品滚动展示框
		lotJn = (TextView) findViewById(R.id.lot_jn); // 抽奖券数据
		lotWP = (Button) findViewById(R.id.lot_wp); // 物品篮
		miss = (Button) findViewById(R.id.lot_miss); // 返回
		dfBtn = (Button) findViewById(R.id.lot_df); // 兑换
		startBtn = (Button) findViewById(R.id.lot_start); // 开始 2张
		startBtn.setBackgroundResource(R.drawable.lot_start_bg);
		lotCJ5Btn = (Button) findViewById(R.id.lot_jj_5); // 开始 5张
		lotCJ5Btn.setBackgroundResource(R.drawable.lot_jj_5_bg);
		lotCJ10Btn = (Button) findViewById(R.id.lot_jj_10); // 开始 10张
		lotCJ10Btn.setBackgroundResource(R.drawable.lot_jj_10_bg);
		miss.setOnClickListener(this);
		// lotWP.setOnClickListener(this);
		lotWP.setVisibility(View.GONE);
		startBtn.setOnClickListener(this);
		// dfBtn.setOnClickListener(this);
		dfBtn.setVisibility(View.INVISIBLE);
		lotCJ5Btn.setOnClickListener(this);
		lotCJ10Btn.setOnClickListener(this);
		// 初始化抽奖物品
		tHF5_1 = new Lottery(context);
		tCJ5_1 = new Lottery(context);
		tZD_1 = new Lottery(context);
		tZD5_1 = new Lottery(context);
		tHF10_1 = new Lottery(context);
		tCJ_2 = new Lottery(context);
		tHF_2 = new Lottery(context);
		tHF5_1.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_hf_bg, R.drawable.lot_num1, "b_hf_num1.png"));
		tHF5_1.setImageName("lot_hf_1");
		tCJ5_1.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_cj_bg, R.drawable.lot_cj_5, "b_cj_cj_5.png"));
		tCJ5_1.setImageName("lot_cj_5");
		tZD_1.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_zd_bg, R.drawable.lot_zd_500, "b_zd_500.png"));
		tZD_1.setImageName("lot_zd_500");
		tZD5_1.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_hf_bg, R.drawable.lot_num10, "b_hf_num10.png"));
		tZD5_1.setImageName("lot_zd_5");
		tHF10_1.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_cj_bg, R.drawable.lot_cj_1, "b_cj_1.png"));
		tHF10_1.setImageName("lot_hf_10");
		tCJ_2.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_zd_bg, R.drawable.lot_zd_1w, "b_zd_5.png"));
		tCJ_2.setImageName("lot_zd_1w");
		tHF_2.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_hf_bg, R.drawable.lot_num01, "b_hf_num01.png"));
		tHF_2.setImageName("lot_h01");
		LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(mst.adjustXIgnoreDensity(82), mst.adjustYIgnoreDensity(82));
		lotTop.addView(tHF5_1, imageParams);
		mst.setDebugId(tHF5_1);
		lotTop.addView(tCJ5_1, imageParams);
		lotTop.addView(tZD_1, imageParams);
		lotTop.addView(tZD5_1, imageParams);
		lotTop.addView(tHF10_1, imageParams);
		lotTop.addView(tCJ_2, imageParams);
		lotTop.addView(tHF_2, imageParams);
		rCJ10_1 = new Lottery(context);
		rZD10_1 = new Lottery(context);
		rHF5_1 = new Lottery(context);
		rCJ10_1.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_zd_bg, R.drawable.lot_zd_2k, "b_zd_2k.png"));
		rCJ10_1.setImageName("lot_cj_10");
		rZD10_1.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_cj_bg, R.drawable.lot_cj_1, "b_cj_1.png"));
		rZD10_1.setImageName("lot_cj_1");
		rHF5_1.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_zd_bg, R.drawable.lot_zd_1, "b_zd_1.png"));
		rHF5_1.setImageName("lot_hf_5");
		LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(mst.adjustXIgnoreDensity(82), mst.adjustYIgnoreDensity(82));
		rightParams.topMargin = mst.adjustYIgnoreDensity(9);
		lotRight.addView(rCJ10_1, rightParams);
		lotRight.addView(rZD10_1, rightParams);
		lotRight.addView(rHF5_1, rightParams);
		bCJ_1 = new Lottery(context);
		bHF_10 = new Lottery(context);
		bCJ1_1 = new Lottery(context);
		bCJ_2 = new Lottery(context);
		bZD5_1 = new Lottery(context);
		bXX = new Lottery(context);
		bZD100 = new Lottery(context);
		bCJ_1.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_cj_bg, R.drawable.lot_cj_1, "b_cj_1.png"));
		bCJ_1.setImageName("lot_xx");
		bHF_10.getMedalImage().setImageDrawable(ImageUtil.getDrawableResId(R.drawable.lot_stove_bg, true, true));
		bHF_10.setImageName("lot_stove");
		bCJ1_1.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_zd_bg, R.drawable.lot_zd_2, "b_zd_2.png"));
		bCJ1_1.setImageName("lot_cj_1");
		bCJ_2.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_hf_bg, R.drawable.lot_num01, "b_hf_num01.png"));
		bCJ_2.setImageName("lot_h01");
		bZD5_1.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_hf_bg, R.drawable.lot_num50, "b_hf_num50.png"));
		bZD5_1.setImageName("lot_zd_5");
		bXX.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_cj_bg, R.drawable.lot_cj_3, "b_cj_3.png"));
		bXX.setImageName("lot_xx");
		bZD100.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_zd_bg, R.drawable.lot_zd_100, "b_zd_100.png"));
		bZD100.setImageName("lot_cj_5");
		LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(mst.adjustXIgnoreDensity(82), mst.adjustYIgnoreDensity(82));
		lotBottom.addView(bCJ_1, bottomParams);
		lotBottom.addView(bHF_10, bottomParams);
		lotBottom.addView(bCJ1_1, bottomParams);
		lotBottom.addView(bCJ_2, bottomParams);
		lotBottom.addView(bZD5_1, bottomParams);
		lotBottom.addView(bXX, bottomParams);
		lotBottom.addView(bZD100, bottomParams);
		lXX = new Lottery(context);
		lHF5_1 = new Lottery(context);
		lCJ1_1 = new Lottery(context);
		lXX.getMedalImage().setImageDrawable(ImageUtil.getDrawableResId(R.drawable.lot_xx, true, true));
		lXX.setImageName("lot_zd_5");
		lHF5_1.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_zd_bg, R.drawable.lot_zd_5k, "b_zd_5k.png"));
		lHF5_1.setImageName("lot_hf_5");
		lCJ1_1.getMedalImage().setImageBitmap(getBitmap(R.drawable.lot_zd_bg, R.drawable.lot_zd_1w, "b_zd_5.png"));
		lCJ1_1.setImageName("lot_zd_1w");
		LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(mst.adjustXIgnoreDensity(82), mst.adjustYIgnoreDensity(82));
		leftParams.topMargin = mst.adjustYIgnoreDensity(9);
		lotLeft.addView(lXX, leftParams);
		lotLeft.addView(lHF5_1, leftParams);
		lotLeft.addView(lCJ1_1, leftParams);
		setNameValue();
		addLottery();
		for (int i = 0; i < lotterItemList.size(); i++) {
			if (i < lotterItemList.size() - 1) {
				((Lottery) lotterItemList.get(i)).setSuccessor(lotterItemList.get(i + 1));
			} else {
				((Lottery) lotterItemList.get(i)).setSuccessor(lotterItemList.get(0));
			}
		}
		tHF5_1.setIndex("start");
		lXX.setIndex("end");
		setListener();
	}

	/**
	 * 初始化界面和监听
	 */
	public void InitZhizuanView() {
		isChouJiang = false;
		zhizuanItemList = new ArrayList<Lottery>();
		layout = (RelativeLayout) findViewById(R.id.lot_bg);
		layout.setBackgroundDrawable(ImageUtil.getDrawableResId(R.drawable.lottery_bg_1, true, true));
		luckLayout = (RelativeLayout) findViewById(R.id.lot_luck);
		luckLayout.setBackgroundDrawable(ImageUtil.getDrawableResId(R.drawable.lot_right_text, true, true));
		// 抽奖框4边布局
		lotTop = (LinearLayout) findViewById(R.id.lot_top);
		lotLeft = (LinearLayout) findViewById(R.id.lot_left);
		lotRight = (LinearLayout) findViewById(R.id.lot_right);
		lotBottom = (LinearLayout) findViewById(R.id.lot_bottom);
		changeBgLayout = (RelativeLayout) findViewById(R.id.change_table);
		choujiangBtn = (Button) findViewById(R.id.choujiang_btn); // 抽奖
		zhizuanBtn = (Button) findViewById(R.id.zizhuan_btn);// 抽钻石
		choujiangBtn.setOnClickListener(this);
		zhizuanBtn.setOnClickListener(this);
		lotPm = (TextView) findViewById(R.id.lot_pm); // 抽奖物品滚动展示框
		lotJn = (TextView) findViewById(R.id.lot_jn); // 抽奖券数据
		lotWP = (Button) findViewById(R.id.lot_wp); // 物品篮
		miss = (Button) findViewById(R.id.lot_miss); // 返回
		dfBtn = (Button) findViewById(R.id.lot_df); // 兑换
		startBtn = (Button) findViewById(R.id.lot_start); // 开始 2张
		startBtn.setBackgroundResource(R.drawable.lot_jj_10_2_bg);
		lotCJ5Btn = (Button) findViewById(R.id.lot_jj_5); // 开始 5张
		lotCJ5Btn.setBackgroundResource(R.drawable.lot_jj_20_bg);
		lotCJ10Btn = (Button) findViewById(R.id.lot_jj_10); // 开始 10张
		lotCJ10Btn.setBackgroundResource(R.drawable.lot_jj_50_bg);
		miss.setOnClickListener(this);
		lotWP.setOnClickListener(this);
		startBtn.setOnClickListener(this);
		dfBtn.setOnClickListener(this);
		lotCJ5Btn.setOnClickListener(this);
		lotCJ10Btn.setOnClickListener(this);
		// 初始化抽奖物品
		zhizuan_100 = new Lottery(context);
		jiangquan_20 = new Lottery(context);
		zhidou_500 = new Lottery(context);
		zhizuan_2 = new Lottery(context);
		jiangquan_10 = new Lottery(context);
		zhizuan_20 = new Lottery(context);
		huafei_10 = new Lottery(context);
		zhizuan_100.getMedalImage().setImageBitmap(getBitmap(R.drawable.zz_bg2, R.drawable.zd_num_100, "b_zz_100.png"));
		zhizuan_100.setImageName("lot_zz_100");
		jiangquan_20.getMedalImage().setImageBitmap(getBitmap(R.drawable.cj_bg, R.drawable.cj_num_20, "b_cj_20.png"));
		jiangquan_20.setImageName("lot_cj_20");
		zhidou_500.getMedalImage().setImageBitmap(getBitmap(R.drawable.zd_bg, R.drawable.zd_num_500w, "b_zd_500w.png"));
		zhidou_500.setImageName("lot_zd_500");
		zhizuan_2.getMedalImage().setImageBitmap(getBitmap(R.drawable.zz_bg1, R.drawable.zd_num_2, "b_zz_2.png"));
		zhizuan_2.setImageName("lot_zz_2");
		jiangquan_10.getMedalImage().setImageBitmap(getBitmap(R.drawable.cj_bg, R.drawable.cj_num_10, "b_cj_10.png"));
		jiangquan_10.setImageName("lot_cj_10");
		zhizuan_20.getMedalImage().setImageBitmap(getBitmap(R.drawable.zz_bg2, R.drawable.zd_num_20, "b_zz_20.png"));
		zhizuan_20.setImageName("lot_zz_20");
		huafei_10.getMedalImage().setImageBitmap(getBitmap(R.drawable.cz_bg, R.drawable.hf_num_10, "b_cz_10.png"));
		huafei_10.setImageName("lot_hf_10");
		LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(mst.adjustXIgnoreDensity(82), mst.adjustYIgnoreDensity(82));
		lotTop.addView(zhizuan_100, imageParams);
		lotTop.addView(jiangquan_20, imageParams);
		lotTop.addView(zhidou_500, imageParams);
		lotTop.addView(zhizuan_2, imageParams);
		lotTop.addView(jiangquan_10, imageParams);
		lotTop.addView(zhizuan_20, imageParams);
		lotTop.addView(huafei_10, imageParams);
		// 右边
		zhizuan_4 = new Lottery(context);
		huafei_50 = new Lottery(context);
		zhidou_1 = new Lottery(context);
		zhizuan_4.getMedalImage().setImageBitmap(getBitmap(R.drawable.zz_bg1, R.drawable.zd_num_4, "b_zz_4.png"));
		zhizuan_4.setImageName("lot_zz_4");
		huafei_50.getMedalImage().setImageBitmap(getBitmap(R.drawable.cz_bg, R.drawable.hf_num_50, "b_cz_50.png"));
		huafei_50.setImageName("lot_hf_50");
		zhidou_1.getMedalImage().setImageBitmap(getBitmap(R.drawable.zd_bg, R.drawable.zd_num_1w, "b_zd_1w.png"));
		zhidou_1.setImageName("lot_zd_1");
		LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(mst.adjustXIgnoreDensity(82), mst.adjustYIgnoreDensity(82));
		rightParams.topMargin = mst.adjustYIgnoreDensity(9);
		lotRight.addView(zhizuan_4, rightParams);
		lotRight.addView(huafei_50, rightParams);
		lotRight.addView(zhidou_1, rightParams);
		// 下面
		zhizuan_5 = new Lottery(context);
		huafei_1 = new Lottery(context);
		zhidou_2 = new Lottery(context);
		jiangquan_5 = new Lottery(context);
		zhizuan_10 = new Lottery(context);
		jiangquan_30 = new Lottery(context);
		zhizuan_500 = new Lottery(context);
		zhizuan_5.getMedalImage().setImageBitmap(getBitmap(R.drawable.zz_bg1, R.drawable.zd_num_5, "b_zz_5.png"));
		zhizuan_5.setImageName("lot_zz_5");
		huafei_1.getMedalImage().setImageBitmap(getBitmap(R.drawable.cz_bg, R.drawable.hf_num_1, "b_cz_1.png"));
		huafei_1.setImageName("lot_hf_1");
		zhidou_2.getMedalImage().setImageBitmap(getBitmap(R.drawable.zd_bg, R.drawable.zd_num_2w, "b_zd_2w.png"));
		zhidou_2.setImageName("lot_zd_2");
		jiangquan_5.getMedalImage().setImageBitmap(getBitmap(R.drawable.cj_bg, R.drawable.cj_num_5, "b_cj_num_5.png"));
		jiangquan_5.setImageName("lot_cj_5");
		zhizuan_10.getMedalImage().setImageBitmap(getBitmap(R.drawable.zz_bg2, R.drawable.zd_num_10, "b_zz_10.png"));
		zhizuan_10.setImageName("lot_zz_10");
		jiangquan_30.getMedalImage().setImageBitmap(getBitmap(R.drawable.cj_bg, R.drawable.cj_num_30, "b_cj_30.png"));
		jiangquan_30.setImageName("lot_cj_30");
		zhizuan_500.getMedalImage().setImageBitmap(getBitmap(R.drawable.zz_bg2, R.drawable.zd_num_500, "b_zz_500.png"));
		zhizuan_500.setImageName("lot_zz_500");
		LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(mst.adjustXIgnoreDensity(82), mst.adjustYIgnoreDensity(82));
		lotBottom.addView(zhizuan_5, bottomParams);
		lotBottom.addView(huafei_1, bottomParams);
		lotBottom.addView(zhidou_2, bottomParams);
		lotBottom.addView(jiangquan_5, bottomParams);
		lotBottom.addView(zhizuan_10, bottomParams);
		lotBottom.addView(jiangquan_30, bottomParams);
		lotBottom.addView(zhizuan_500, bottomParams);
		xiexie = new Lottery(context);
		zhizuan_1 = new Lottery(context);
		zhidou_5 = new Lottery(context);
		xiexie.getMedalImage().setImageDrawable(ImageUtil.getDrawableResId(R.drawable.xiexie, true, true));
		xiexie.setImageName("lot_xx");
		zhizuan_1.getMedalImage().setImageBitmap(getBitmap(R.drawable.zz_bg1, R.drawable.zd_num_1, "b_zz_1.png"));
		zhizuan_1.setImageName("lot_zz_1");
		zhidou_5.getMedalImage().setImageBitmap(getBitmap(R.drawable.zd_bg, R.drawable.zd_num_5w, "b_zd_5w.png"));
		zhidou_5.setImageName("lot_zd_5");
		LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(mst.adjustXIgnoreDensity(82), mst.adjustYIgnoreDensity(82));
		leftParams.topMargin = mst.adjustYIgnoreDensity(9);
		lotLeft.addView(xiexie, leftParams);
		lotLeft.addView(zhizuan_1, leftParams);
		lotLeft.addView(zhidou_5, leftParams);
		setZhizuanValue();
		addZhizhuan();
		for (int i = 0; i < zhizuanItemList.size(); i++) {
			if (i < zhizuanItemList.size() - 1) {
				((Lottery) zhizuanItemList.get(i)).setSuccessor(zhizuanItemList.get(i + 1));
			} else {
				((Lottery) zhizuanItemList.get(i)).setSuccessor(zhizuanItemList.get(0));
			}
		}
		zhizuan_100.setIndex("start");
		xiexie.setIndex("end");
		setZzListener();
	}

	/**
	 * 配置抽奖参数
	 */
	public void setZhizuanValue() {
		zhizuan_100.setName("zhizuan_100");
		jiangquan_20.setName("jiangquan_20");
		zhidou_500.setName("zhidou_500");
		zhizuan_2.setName("zhizuan_2");
		jiangquan_10.setName("jiangquan_10");
		zhizuan_20.setName("zhizuan_20");
		huafei_10.setName("huafei_10");
		zhizuan_4.setName("zhizuan_4");
		huafei_50.setName("huafei_50");
		zhidou_1.setName("zhidou_1");
		zhizuan_5.setName("zhizuan_5");
		huafei_1.setName("huafei_1");
		zhidou_2.setName("zhidou_2");
		jiangquan_5.setName("jiangquan_5");
		zhizuan_10.setName("zhizuan_10");
		jiangquan_30.setName("jiangquan_30");
		zhizuan_500.setName("zhizuan_500");
		xiexie.setName("xiexie");
		zhizuan_1.setName("zhizuan_1");
		zhidou_5.setName("zhidou_5");
		zhizuan_100.setValue(zz[6]);
		jiangquan_20.setValue(cj[2]);
		zhidou_500.setValue(zd[3]);
		zhizuan_2.setValue(zz[1]);
		jiangquan_10.setValue(cj[1]);
		zhizuan_20.setValue(zz[5]);
		huafei_10.setValue(hf[1]);
		zhizuan_4.setValue(zz[2]);
		huafei_50.setValue(hf[2]);
		zhidou_1.setValue(zd[0]);
		zhizuan_5.setValue(zz[3]);
		huafei_1.setValue(hf[0]);
		zhidou_2.setValue(zd[1]);
		jiangquan_5.setValue(cj[0]);
		zhizuan_10.setValue(zz[4]);
		jiangquan_30.setValue(cj[3]);
		zhizuan_500.setValue(zz[7]);
		xiexie.setValue("10");
		zhizuan_1.setValue(zz[0]);
		zhidou_5.setValue(zd[2]);
		zhizuan_100.setDetail("恭喜您，抽中钻石100个");
		jiangquan_20.setDetail("恭喜您，抽中奖券20张");
		zhidou_500.setDetail("恭喜您，抽中金豆500万");
		zhizuan_2.setDetail("恭喜您，抽中钻石2个");
		jiangquan_10.setDetail("恭喜您，抽中奖券10张");
		zhizuan_20.setDetail("恭喜您，抽中钻石20个");
		huafei_10.setDetail("恭喜您，抽中话费券20元");
		zhizuan_4.setDetail("恭喜您，抽中钻石4个");
		huafei_50.setDetail("恭喜您，抽中话费券50元");
		zhidou_1.setDetail("恭喜您，抽中金豆1万");
		zhizuan_5.setDetail("恭喜您，抽中钻石5个");
		huafei_1.setDetail("恭喜您，抽中话费券1元");
		zhidou_2.setDetail("恭喜您，抽中金豆2万");
		jiangquan_5.setDetail("恭喜您，抽中抽奖券5张");
		zhizuan_10.setDetail("恭喜您，抽中钻石10个");
		jiangquan_30.setDetail("恭喜您，抽中抽奖券30张");
		zhizuan_500.setDetail("恭喜您，抽中钻石500个");
		xiexie.setDetail("再接再厉哦，谢谢您的参与!");
		zhizuan_1.setDetail("恭喜您，抽中钻石1个");
		zhidou_5.setDetail("恭喜您，抽中金豆5万");
	}

	/**
	 * 合成图片
	 * @param background
	 * @param src
	 * @return
	 */
	private Bitmap getBitmap(int background, int src, String name) {
		try {
			if (bitmapCache.containsKey(src)) {
				return bitmapCache.get(src);
			}
			String path = HttpURL.HTTP_PATH + "img/lotterydialog/" + name;
			boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
			if (sdCardExist) {
				Bitmap bitmap = ImageUtil.getImageFromSdCard(path);
				if (null != bitmap) {
					Log.i("prizeImage", "advImageCacheMap从SD卡取：" + path);
					bitmapCache.put(src, bitmap);
					return bitmap;
				}
			} else {
				Bitmap bitmap = ImageUtil.getImageFromData(path);
				if (null != bitmap) {
					Log.i("prizeImage", "advImageCacheMap从内存取：" + path);
					bitmapCache.put(src, bitmap);
					return bitmap;
				}
			}
			BitmapDrawable bg = (BitmapDrawable) ImageUtil.getDrawableResId(background, true, true);
			BitmapDrawable bg1 = (BitmapDrawable) ImageUtil.getDrawableResId(src, true, true);
			Bitmap bp = bg.getBitmap();
			Bitmap bp1 = bg1.getBitmap();
			Bitmap bitmap = Bitmap.createBitmap(bp.getWidth(), bp.getHeight(), Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(bp, 0, 0, null);
			canvas.drawBitmap(bp1, 0, 0, null);
			bitmapCache.put(src, bitmap);
			if (sdCardExist) {
				ImageUtil.saveImageToSdCard(path, bitmap);
			} else {
				ImageUtil.saveImageToData(path, bitmap);
				Log.i("joinRoom", "------------------------保存图片：" + path + "----------------------------------");
			}
			return bitmap;
		} catch (OutOfMemoryError e) {
			return null;
		}
	}

	/**
	 * 配置抽奖参数
	 */
	public void setNameValue() {
		tHF5_1.setName("tHF5_1");
		tCJ5_1.setName("tCJ5_1");
		tZD_1.setName("tZD_1");
		tZD5_1.setName("tZD5_1");
		tHF10_1.setName("tHF10_1");
		tCJ_2.setName("tCJ_2");
		tHF_2.setName("tHF_2");
		rCJ10_1.setName("rCJ10_1");
		rZD10_1.setName("rZD10_1");
		rHF5_1.setName("rHF5_1");
		bZD100.setName("bZD100");
		bXX.setName("bXX");
		bZD5_1.setName("bZD5_1");
		bCJ_2.setName("bCJ_2");
		bCJ1_1.setName("bCJ1_1");
		bHF_10.setName("bHF_10");
		bCJ_1.setName("bCJ_1");
		lCJ1_1.setName("lCJ1_1");
		lHF5_1.setName("lHF5_1");
		lXX.setName("lXX");
		tHF5_1.setValue("tHF5_1");
		tCJ5_1.setValue(b[3]);
		tZD_1.setValue(a[6]);
		tZD5_1.setValue("tZD5_1");
		tHF10_1.setValue(b[0]);
		tCJ_2.setValue(a[3]);
		tHF_2.setValue(sx[1]);
		rCJ10_1.setValue(a[0]);
		rZD10_1.setValue(b[0]);
		rHF5_1.setValue(a[1]);
		bZD100.setValue(a[5]);
		bXX.setValue(b[2]);
		bZD5_1.setValue("bZD5_1");
		bCJ_2.setValue(sx[1]);
		bCJ1_1.setValue(a[2]);
		bHF_10.setValue(hc[0]);
		bCJ_1.setValue(b[0]);
		lCJ1_1.setValue(a[3]);
		lHF5_1.setValue(a[7]);
		lXX.setValue("10");
		tHF5_1.setDetail("恭喜您，抽中话费券1元");
		tCJ5_1.setDetail("恭喜您，抽中奖券5张");
		tZD_1.setDetail("恭喜您，抽中金豆100万");
		tZD5_1.setDetail("恭喜您，抽中话费券10元");
		tHF10_1.setDetail("恭喜您，抽中奖券1张");
		tCJ_2.setDetail("恭喜您，抽中金豆1万");
		tHF_2.setDetail("恭喜您，抽中费券0.1元");
		rCJ10_1.setDetail("恭喜您，抽中金豆5百");
		rZD10_1.setDetail("恭喜您，抽中奖券1张");
		rHF5_1.setDetail("恭喜您，抽中金豆2千");
		bZD100.setDetail("恭喜您，抽中金豆20万");
		bXX.setDetail("恭喜您，抽中奖券3张");
		bZD5_1.setDetail("恭喜您，抽中话费券10元");
		bCJ_2.setDetail("恭喜您，抽中费券0.1元");
		bCJ1_1.setDetail("恭喜您，抽中金豆四千");
		bHF_10.setDetail("恭喜您，抽中合成剂一瓶");
		bCJ_1.setDetail("恭喜您，抽中奖券1张");
		lCJ1_1.setDetail("恭喜您，抽中金豆1万");
		lHF5_1.setDetail("恭喜您，抽中金豆1千");
		lXX.setDetail("再接再厉哦，谢谢您的参与!");
	}

	/**
	 * 设置抽奖监听
	 */
	public void setListener() {
		for (int i = 0; i < lotterItemList.size(); i++) {
			lotterItemList.get(i).addPrizeListener(this);
		}
	}

	/**
	 * 设置抽钻石监听
	 */
	public void setZzListener() {
		for (int i = 0; i < zhizuanItemList.size(); i++) {
			zhizuanItemList.get(i).addPrizeListener(this);
		}
	}

	/**
	 * 添加抽奖单元
	 */
	public void addLottery() {
		lotterItemList.add(tHF5_1);
		lotterItemList.add(tCJ5_1);
		lotterItemList.add(tZD_1);
		lotterItemList.add(tZD5_1);
		lotterItemList.add(tHF10_1);
		lotterItemList.add(tCJ_2);
		lotterItemList.add(tHF_2);
		lotterItemList.add(rCJ10_1);
		lotterItemList.add(rZD10_1);
		lotterItemList.add(rHF5_1);
		lotterItemList.add(bZD100);
		lotterItemList.add(bXX);
		lotterItemList.add(bZD5_1);
		lotterItemList.add(bCJ_2);
		lotterItemList.add(bCJ1_1);
		lotterItemList.add(bHF_10);
		lotterItemList.add(bCJ_1);
		lotterItemList.add(lCJ1_1);
		lotterItemList.add(lHF5_1);
		lotterItemList.add(lXX);
	}

	/**
	 * 添加抽奖单元
	 */
	public void addZhizhuan() {
		zhizuanItemList.add(zhizuan_100);
		zhizuanItemList.add(jiangquan_20);
		zhizuanItemList.add(zhidou_500);
		zhizuanItemList.add(zhizuan_2);
		zhizuanItemList.add(jiangquan_10);
		zhizuanItemList.add(zhizuan_20);
		zhizuanItemList.add(huafei_10);
		zhizuanItemList.add(zhizuan_4);
		zhizuanItemList.add(huafei_50);
		zhizuanItemList.add(zhidou_1);
		zhizuanItemList.add(zhizuan_500);
		zhizuanItemList.add(jiangquan_30);
		zhizuanItemList.add(zhizuan_10);
		zhizuanItemList.add(jiangquan_5);
		zhizuanItemList.add(zhidou_2);
		zhizuanItemList.add(huafei_1);
		zhizuanItemList.add(zhizuan_5);
		zhizuanItemList.add(zhidou_5);
		zhizuanItemList.add(zhizuan_1);
		zhizuanItemList.add(xiexie);
	}

	public static void setAllZhizuanInvisuble() {
		for (int i = 0; i < zhizuanItemList.size(); i++) {
			setInvisuble(zhizuanItemList.get(i));
		}
	}

	public static void setAllInnerInvisuble() {
		for (int i = 0; i < lotterItemList.size(); i++) {
			setInvisuble(lotterItemList.get(i));
		}
	}

	public static void setCoverZhizuanInvisible() {
		for (int i = 0; i < zhizuanItemList.size(); i++) {
			setCoverInvisble(zhizuanItemList.get(i));
		}
	}

	public static void setCoverAllInvisible() {
		for (int i = 0; i < lotterItemList.size(); i++) {
			setCoverInvisble(lotterItemList.get(i));
		}
	}

	/**
	 * 设置所有的抽奖项高亮
	 */
	public void setCoverAllVisible() {
		if (isChouJiang == true) {
			for (int i = 0; i < lotterItemList.size(); i++) {
				setCoverVisible(lotterItemList.get(i));
			}
		} else {
			for (int i = 0; i < zhizuanItemList.size(); i++) {
				setCoverVisible(zhizuanItemList.get(i));
			}
		}
	}

	/**
	 * 设置抽奖项高亮
	 * 
	 * @param lot
	 */
	public void setCoverVisible(Lottery lot) {
		lot.getCoverImage().setVisibility(View.VISIBLE);
	}

	/**
	 * 设置抽奖项去掉高亮
	 * 
	 * @param lot
	 */
	public static void setCoverInvisble(Lottery lot) {
		lot.getCoverImage().setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.choujiang_btn:
				if (stop) {
					//MobclickAgent.onEvent(CrashApplication.getInstance(), "幸运大抽奖");
					changeBgLayout.setBackgroundDrawable(ImageUtil.getDrawableResId(R.drawable.choujiang, true, true));
					lotLeft.removeAllViews();
					lotTop.removeAllViews();
					lotRight.removeAllViews();
					lotBottom.removeAllViews();
					setInitView();
					queryConpon();
				}
				break;
			case R.id.zizhuan_btn:
				if (stop) {
					//MobclickAgent.onEvent(CrashApplication.getInstance(), "钻石抽奖");
					changeBgLayout.setBackgroundDrawable(ImageUtil.getDrawableResId(R.drawable.zhizuan, true, true));
					lotLeft.removeAllViews();
					lotTop.removeAllViews();
					lotRight.removeAllViews();
					lotBottom.removeAllViews();
					InitZhizuanView();
					queryConpon();
				}
				break;
			case R.id.lot_start:
				if (isChouJiang) {
					startLottery(2);
				} else {
					startLotZhizuan(10);
				}
				break;
			case R.id.lot_jj_5:
				if (isChouJiang) {
					startLottery(5);
				} else {
					startLotZhizuan(20);
				}
				break;
			case R.id.lot_jj_10:
				if (isChouJiang) {
					startLottery(10);
				} else {
					startLotZhizuan(50);
				}
				break;
			case R.id.lot_df: // 兑换
				// LotDialog mmDialog = new LotDialog(Database.currentActivity,
				// R.style.dialog, handler);
				// mmDialog.show();
				break;
			case R.id.lot_wp: // 物品篮
				//gDialog = new GoodsDialog(Database.currentActivity);
				//gDialog.show();
				break;
			case R.id.lot_miss: // 返回
				this.dismiss();
				break;
			default:
				break;
		}
	}

	/**
	 * 清除数据
	 */
	private void exit() {
		ImageUtil.releaseDrawable(lotLeft.getBackground());
		ImageUtil.releaseDrawable(lotTop.getBackground());
		ImageUtil.releaseDrawable(lotRight.getBackground());
		ImageUtil.releaseDrawable(lotBottom.getBackground());
		ImageUtil.releaseDrawable(startBtn.getBackground());
		ImageUtil.releaseDrawable(dfBtn.getBackground());
		ImageUtil.releaseDrawable(lotCJ5Btn.getBackground());
		ImageUtil.releaseDrawable(lotCJ10Btn.getBackground());
		ImageUtil.releaseDrawable(lotWP.getBackground());
		ImageUtil.releaseDrawable(miss.getBackground());
		ImageUtil.releaseDrawable(tHF5_1.getMedalImage().getDrawable());
		ImageUtil.releaseDrawable(tCJ5_1.getMedalImage().getDrawable());
		ImageUtil.releaseDrawable(rCJ10_1.getMedalImage().getDrawable());
		ImageUtil.releaseDrawable(rZD10_1.getMedalImage().getDrawable());
		ImageUtil.releaseDrawable(bHF_10.getMedalImage().getDrawable());
		ImageUtil.releaseDrawable(bCJ1_1.getMedalImage().getDrawable());
		ImageUtil.releaseDrawable(bCJ_2.getMedalImage().getDrawable());
		ImageUtil.releaseDrawable(bZD5_1.getMedalImage().getDrawable());
		ImageUtil.releaseDrawable(bXX.getMedalImage().getDrawable());
		ImageUtil.releaseDrawable(bZD100.getMedalImage().getDrawable());
		ImageUtil.releaseDrawable(lXX.getMedalImage().getDrawable());
		ImageUtil.releaseDrawable(lHF5_1.getMedalImage().getDrawable());
		ImageUtil.releaseDrawable(lCJ1_1.getMedalImage().getDrawable());
		Log.i("bitmapCache", "bitmapCache:" + bitmapCache.size());
		if (bitmapCache != null) {
			Iterator<Map.Entry<Integer, Bitmap>> it = bitmapCache.entrySet().iterator();
			while (it.hasNext()) {
				try {
					it.next().getValue().recycle();
				} catch (Exception e) {}
			}
			bitmapCache.clear();
			bitmapCache = null;
		}
		for (int i = 0; i < lotterItemList.size();) {
			lotterItemList.remove(i);
			i = 0;
		}
		lotterItemList = null;
		//		tHF5_1.stopTimer();
		//		zhizuan_100.stopTimer();
		tHF5_1 = null;
		tCJ5_1 = null;
		tZD_1 = null;
		tZD5_1 = null;
		tHF10_1 = null;
		tCJ_2 = null;
		tHF_2 = null;
		rCJ10_1 = null;
		rZD10_1 = null;
		rHF5_1 = null;
		bZD100 = null;
		bXX = null;
		bZD5_1 = null;
		bCJ_2 = null;
		bCJ1_1 = null;
		bHF_10 = null;
		bCJ_1 = null;
		lCJ1_1 = null;
		lHF5_1 = null;
		lXX = null;
		zhizuan_100 = null;
		jiangquan_20 = null;
		zhidou_500 = null;
		zhizuan_2 = null;
		jiangquan_10 = null;
		zhizuan_20 = null;
		huafei_10 = null;
		zhizuan_4 = null;
		huafei_50 = null;
		zhidou_1 = null;
		zhizuan_5 = null;
		huafei_1 = null;
		zhidou_2 = null;
		jiangquan_5 = null;
		zhizuan_10 = null;
		jiangquan_30 = null;
		zhizuan_500 = null;
		xiexie = null;
		zhizuan_1 = null;
		zhidou_5 = null;
	}

	@Override
	public void dismiss() {
		super.dismiss();
		stop = true;
		lucking = false;
		//		Database.StoveDialogShow=false;
	}

	public void onDestory() {
		exit();
		mst = null;
		//gDialog = null;
		lotterItemList = null;
		zhizuanItemList = null;
		bitmapCache = null;
	}
}
