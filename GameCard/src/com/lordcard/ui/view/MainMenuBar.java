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
import com.lordcard.ui.TaskMenuActivity;
import com.lordcard.ui.base.FastJoinTask;
import com.lordcard.ui.view.dialog.GameDialog;
import com.lordcard.ui.view.dialog.LotteryDialog;
import com.sdk.util.SDKFactory;


public class MainMenuBar extends RelativeLayout implements OnClickListener {

	protected TaskManager taskManager = new TaskManager();
	//充值，排名
	private Button addMoneyBtn, rankBtn, goodsBagView, goodsDuihuanView, quickPlay,receiveBeenBtn;
	private ImageView settingIv;//设置
	private TextView zhidouTv;//用户金豆
	private LinearLayout chongzhiLl;//(充值的另一入口)
	//物品宝鉴，游戏指南，好友，金豆赠送，反馈,抽奖，
	//private ImageView zhidouBtn, feedbackBtn;
	private TextView transparentTv;
	private TextView nameTv;//昵称
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private Context context;
	private LotteryDialog lotDialog;
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
		inflater.inflate(R.layout.main_menu_bar, this);
		//goodsLayout = (LinearLayout) findViewById(R.id.goods_layout);
		settingIv = (ImageView) findViewById(R.id.menu_setting_btn);
		zhidouTv = (TextView) findViewById(R.id.menu_zhidou_tv);
		//lotteryBtn = (ImageView) findViewById(R.id.menu_lottery_btn);
		transparentTv = (TextView) findViewById(R.id.menu_transparent_tv);
		//friendsBtn = (ImageView) findViewById(R.id.menu_friends_btn);
		//zhidouBtn = (ImageView) findViewById(R.id.menu_zhidou_btn);
		//feedbackBtn = (ImageView) findViewById(R.id.menu_feedback_btn);
		//friendsBtn.setOnClickListener(this);
		//zhidouBtn.setOnClickListener(this);
		settingIv.setOnClickListener(this);
		//lotteryBtn.setOnClickListener(this);
		transparentTv.setOnClickListener(this);
		//feedbackBtn.setOnClickListener(this);
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

	public TextView getTransparentTv() {
		return transparentTv;
	}

	@Override
	public void onClick(View v) {
		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		
		switch (v.getId()) {
			//case R.id.menu_add_money_btn:// 充值
			case R.id.chongzhi_ll:
				break;
			case R.id.menu_zhidou_btn:// 赠送金豆
				//MobclickAgent.onEvent(context, "工具栏赠送金豆");
				goneLayout();
				Intent taskIt = new Intent();
				taskIt.setClass(getContext(), TaskMenuActivity.class);
				getContext().startActivity(taskIt);
				break;
			case R.id.menu_setting_btn:// 设置
				//MobclickAgent.onEvent(context, "工具栏工具栏设置");
				Intent settingIntent = new Intent();
				settingIntent.setClass(getContext(), SettingActivity.class);
				getContext().startActivity(settingIntent);
				break;
			case R.id.menu_transparent_tv:// 透明层
				break;
			case R.id.menu_feedback_btn:// 游戏反馈
				//MobclickAgent.onEvent(context, "工具栏反馈");
				goneLayout();
				Bundle bundle = new Bundle();
				bundle.putInt("page", 2);
				Intent setIntent = new Intent();
				setIntent.setClass(getContext(), SettingActivity.class);
				setIntent.putExtras(bundle);
				getContext().startActivity(setIntent);
				break;
			case R.id.menu_play_btn:// 快速开始
				//MobclickAgent.onEvent(context, "工具栏快速游戏");
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
		transparentTv.setVisibility(View.GONE);
	}

	private void visibleLayout() {
		transparentTv.setVisibility(View.VISIBLE);
	}

	public void onDestory() {
		taskManager.cancelAll();
		taskManager = null;
		if (mst != null) {
			mst = null;
		}
		if (lotDialog != null) {
			lotDialog.onDestory();
			lotDialog = null;
		}
		this.context = null;
		if (rjoinTask != null) {
			rjoinTask.cancel(true);
			rjoinTask = null;
		}
	}
}
