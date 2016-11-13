package com.lordcard.ui.view;

import com.zzyddz.shui.R;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.anim.AnimUtils;
import com.lordcard.common.task.GenericTask;
import com.lordcard.common.task.TaskManager;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.DateUtil;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.common.util.PatternUtils;
import com.lordcard.common.util.SettingUtils;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUser;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.ui.InviteToDowanloadActivity;
import com.lordcard.ui.SettingActivity;
import com.lordcard.ui.StoveActivity;
import com.lordcard.ui.TaskMenuActivity;
import com.lordcard.ui.base.FastJoinTask;
import com.lordcard.ui.view.dialog.BagDialog;
import com.lordcard.ui.view.dialog.EnvalueDialog;
import com.lordcard.ui.view.dialog.GameDialog;
import com.lordcard.ui.view.dialog.GuideDialog;
import com.lordcard.ui.view.dialog.LotteryDialog;
import com.sdk.group.GroupPayActivity;
import com.sdk.group.GroupPayDetailActivity;
import com.sdk.jd.sms.util.JDSMSPayUtil;
import com.sdk.util.PaySite;
import com.sdk.util.PayTipUtils;
import com.sdk.util.SDKFactory;
import com.umeng.analytics.MobclickAgent;

public class MainMenuBar extends RelativeLayout implements OnClickListener {

	protected TaskManager taskManager = new TaskManager();
	//充值，排名
	private Button addMoneyBtn, rankBtn, goodsBagView, goodsDuihuanView, quickPlay,receiveBeenBtn;
	private ImageView settingIv;//设置
	private TextView zhidouTv;//用户金豆
	private LinearLayout chongzhiLl;//(充值的另一入口)
	//物品宝鉴，游戏指南，好友，金豆赠送，反馈,抽奖，
	private ImageView goodsValueView, goodsGuideView, friendsBtn, zhidouBtn, feedbackBtn,lotteryBtn;
	private LinearLayout goodsLayout;
	//	private TextView transparentTv, lotTv;
	private TextView transparentTv;
	private TextView nameTv;//昵称
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private Context context;
	private GuideDialog guideDialog;
	private EnvalueDialog valueDialog;
	private LotteryDialog lotDialog;
	private BagDialog bagDialog;
	private GenericTask rjoinTask;
	
	private boolean isCheck = false;

	public MainMenuBar(Context context) {
		super(context);
		this.context = context;
	}

	public MainMenuBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		
		isCheck = SettingUtils.getBoolean(SettingUtils.GAME_CHECK);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		/*if(isCheck){
			inflater.inflate(R.layout.main_menu_bar_check, this);
		}else{
			inflater.inflate(R.layout.main_menu_bar, this);
		}*/
		inflater.inflate(R.layout.main_menu_bar, this);
		goodsLayout = (LinearLayout) findViewById(R.id.goods_layout);
		//addMoneyBtn = (Button) findViewById(R.id.menu_add_money_btn);
		//rankBtn = (Button) findViewById(R.id.menu_rank_btn);
		//goodsBagView = (Button) findViewById(R.id.goods_bag_image);
		settingIv = (ImageView) findViewById(R.id.menu_setting_btn);
		zhidouTv = (TextView) findViewById(R.id.menu_zhidou_tv);
		lotteryBtn = (ImageView) findViewById(R.id.menu_lottery_btn);
		//goodsDuihuanView = (Button) findViewById(R.id.duihuan);
		transparentTv = (TextView) findViewById(R.id.menu_transparent_tv);
		friendsBtn = (ImageView) findViewById(R.id.menu_friends_btn);
		zhidouBtn = (ImageView) findViewById(R.id.menu_zhidou_btn);
		goodsValueView = (ImageView) findViewById(R.id.goods_envalues_image);
		goodsGuideView = (ImageView) findViewById(R.id.goods_guide_image);
		feedbackBtn = (ImageView) findViewById(R.id.menu_feedback_btn);
		//quickPlay = (Button) findViewById(R.id.menu_play_btn);
		//receiveBeenBtn = (Button) findViewById(R.id.menu_receive_been_btn);
		//receiveBeenBtn.setOnClickListener(this);
		//quickPlay.setOnClickListener(this);
		//addMoneyBtn.setOnClickListener(this);
		//rankBtn.setOnClickListener(this);
		friendsBtn.setOnClickListener(this);
		zhidouBtn.setOnClickListener(this);
		settingIv.setOnClickListener(this);
		lotteryBtn.setOnClickListener(this);
		transparentTv.setOnClickListener(this);
		//goodsBagView.setOnClickListener(this);
		goodsValueView.setOnClickListener(this);
		goodsGuideView.setOnClickListener(this);
		//goodsDuihuanView.setOnClickListener(this);
		feedbackBtn.setOnClickListener(this);
		mst.adjustView(findViewById(R.id.menu_mst));

		setUserInfo();
	}

	/**
	 * 设置用户信息
	 */
	public void setUserInfo() {
		nameTv = (TextView) findViewById(R.id.menu_name_tv);
		chongzhiLl = (LinearLayout) findViewById(R.id.chongzhi_ll);
		chongzhiLl.setOnClickListener(this);
		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		if (null != cacheUser) {
			zhidouTv.setText(PatternUtils.changeZhidou(0 > cacheUser.getBean() ? 0 : cacheUser.getBean())); //金豆
			nameTv.setText("" + cacheUser.getNickname());
			String gender = cacheUser.getGender();
			//性别 0保密1女2男
			if ("1".equals(gender)) {
				settingIv.setImageResource(R.drawable.nv_user_img);
			} else {
				settingIv.setImageResource(R.drawable.nan_user_img);
			}
		}
	}

	public LinearLayout getGoodsLayout() {
		return goodsLayout;
	}

	public TextView getTransparentTv() {
		return transparentTv;
	}

	@Override
	public void onClick(View v) {
		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		
		switch (v.getId()) {
			//case R.id.menu_add_money_btn:// 充值
			case R.id.chongzhi_ll:
				if (ActivityUtils.simExist() && ActivityUtils.getSimType() != Constant.SIM_OTHER) {
					Intent detailIntent = new Intent();
					detailIntent.putExtra("key", String.valueOf(Constant.SIM_MOBILE));
					detailIntent.setClass(getContext(), GroupPayDetailActivity.class);
					getContext().startActivity(detailIntent);
				} else {
					Toast.makeText(getContext(), "请插入sim卡", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.menu_rank_btn:// 背包
				MobclickAgent.onEvent(context, "工具栏背包");
				if (goodsLayout.getVisibility() == View.VISIBLE) {
					goneLayout();
				} else {
					visibleLayout();
				}
				break;
			case R.id.menu_friends_btn:// 好友
				MobclickAgent.onEvent(context, "工具栏好友");
				goneLayout();
				Intent friendsIntent = new Intent();
				friendsIntent.setClass(getContext(), InviteToDowanloadActivity.class);
				getContext().startActivity(friendsIntent);
				break;
			case R.id.menu_zhidou_btn:// 赠送金豆
				MobclickAgent.onEvent(context, "工具栏赠送金豆");
				goneLayout();
				Intent taskIt = new Intent();
				taskIt.setClass(getContext(), TaskMenuActivity.class);
				getContext().startActivity(taskIt);
				break;
			case R.id.menu_setting_btn:// 设置
				MobclickAgent.onEvent(context, "工具栏工具栏设置");
				Intent settingIntent = new Intent();
				settingIntent.setClass(getContext(), SettingActivity.class);
				getContext().startActivity(settingIntent);
				break;
			case R.id.menu_lottery_btn:// 抽奖
				MobclickAgent.onEvent(context, "工具栏抽奖");
				if (null != cacheUser && cacheUser.getIq() < 5) {
					DialogUtils.mesToastTip("参与抽奖需要5级以上的等级！");
				} else {
					if (lotDialog == null) {
						lotDialog = new LotteryDialog(getContext());
					}
					LotteryDialog.voiceON = true;
					if (!lotDialog.isShowing()) {
						lotDialog.show();
					}
				}
				break;
			case R.id.menu_transparent_tv:// 透明层
				if (goodsLayout.getVisibility() == View.VISIBLE) {
					goneLayout();
				}
				break;
			case R.id.goods_bag_image:// 物品囊
				MobclickAgent.onEvent(context, "工具栏物品囊");
				if (bagDialog != null) {
					bagDialog.dismiss();
					bagDialog = null;
				}
				bagDialog = new BagDialog(getContext());
				goneLayout();
				bagDialog.show();
				break;
			case R.id.goods_envalues_image:// 物品宝鉴
				MobclickAgent.onEvent(context, "工具栏宝鉴");
				goneLayout();
				if (valueDialog != null) {
					valueDialog.dismiss();
					valueDialog = null;
				}
				valueDialog = new EnvalueDialog(getContext());
				valueDialog.show();
				break;
			case R.id.goods_guide_image:// 游戏指南
				MobclickAgent.onEvent(context, "工具栏游戏指南");
				goneLayout();
				if (guideDialog != null) {
					guideDialog.dismiss();
					guideDialog = null;
				}
				guideDialog = new GuideDialog(getContext());
				guideDialog.show();
				break;
			case R.id.menu_feedback_btn:// 游戏反馈
				MobclickAgent.onEvent(context, "工具栏反馈");
				goneLayout();
				Bundle bundle = new Bundle();
				bundle.putInt("page", 2);
				Intent setIntent = new Intent();
				setIntent.setClass(getContext(), SettingActivity.class);
				setIntent.putExtras(bundle);
				getContext().startActivity(setIntent);
				break;
			case R.id.duihuan:// 物品合成
				MobclickAgent.onEvent(context, "工具栏合成炉");
				goneLayout();
				Intent stoveIntent = new Intent();
				stoveIntent.setClass(getContext(), StoveActivity.class);
				getContext().startActivity(stoveIntent);
				break;
			case R.id.menu_play_btn:// 快速开始
				MobclickAgent.onEvent(context, "工具栏快速游戏");
				goneLayout();
				if (cacheUser == null) {
					DialogUtils.mesTip("用户信息过期，请重新登录", true);
				} else {
					FastJoinTask.fastJoin();
				}
				break;
		}
	}

	private void goneLayout() {
		goodsLayout.setVisibility(View.GONE);
		transparentTv.setVisibility(View.GONE);
	}

	private void visibleLayout() {
		goodsLayout.setVisibility(View.VISIBLE);
		transparentTv.setVisibility(View.VISIBLE);
	}
	
	public void showTokenBeanDialog(final String msg){
		Database.currentActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				GameDialog gameDialog = new GameDialog(context, true) {
					public void okClick() {
						JDSMSPayUtil.setContext(context);
						PayTipUtils.showTip(0,PaySite.ROOM_RECEIVE_BEEN);
					}
					@Override
					public void cancelClick() {
						super.cancelClick();
					}
				};
				
				gameDialog.show();
				gameDialog.setCancelText("关闭");
				gameDialog.setOkText("充金豆");
				gameDialog.setText(msg);
				gameDialog.setOkButtonBg(R.drawable.select1_btn_bg);
				gameDialog.setCancelButtonBg(R.drawable.select_btn_bg);
			}
		});
	}

	public void onDestory() {
		taskManager.cancelAll();
		taskManager = null;
		if (mst != null) {
			mst = null;
		}
		guideDialog = null;
		valueDialog = null;
		if (lotDialog != null) {
			lotDialog.onDestory();
			lotDialog = null;
		}
		bagDialog = null;
		this.context = null;
		if (rjoinTask != null) {
			rjoinTask.cancel(true);
			rjoinTask = null;
		}
	}
}
