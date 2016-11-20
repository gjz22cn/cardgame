package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.ImageUtil.ImageCallback;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.Goods;
import com.lordcard.entity.PlayFraudReport;
import com.lordcard.entity.UserGoods;
import com.lordcard.network.cmdmgr.CmdDetail;
import com.lordcard.network.cmdmgr.CmdUtils;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.lordcard.ui.dizhu.DoudizhuMainGameActivity;

public class PhotoDialog extends Dialog implements OnClickListener {

	private Context context;
	private List<Goods> textGoods = new ArrayList<Goods>();
	private TextView userTextView;
	private TextView beanTextView;
	private TextView diamondTextView;
	private ImageView sexIv;//性别
	private TextView zhiShangTv;//等级
	private Button reportBtn;//举报
	private LinearLayout reportLl;//举报容器控件
	//private GridView goodsList;
	private LinearLayout mainLayout;
	private List<Goods> imageurl;
	private String showAccount;
	private List<UserGoods> userInfo;
	private GameUser user;
	private ImageView view;//传进来的布局
	private boolean isMaster=false;//是否是地主
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();

	protected PhotoDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
	}

	public PhotoDialog(ImageView view,boolean isMaster,Context context, String showAccount, GameUser user) {
		super(context, R.style.dialog);
		this.context = context;
		this.showAccount = showAccount;
		this.user = user;
		this.view=view;
		this.isMaster=isMaster;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_dialog);
		mainLayout = (LinearLayout) findViewById(R.id.photo_info_layout);
		mainLayout.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.photo_bg, false));
		layout();
		mst.adjustView(mainLayout);
	}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	private void layout() {
		sexIv= (ImageView) findViewById(R.id.pd_sex_iv);
		if(!TextUtils.isEmpty(user.getGender()) && "1".equals(user.getGender())){//女
			sexIv.setImageResource(R.drawable.nv_photo_tip);
		}else{
			sexIv.setImageResource(R.drawable.nan_photo_tip);
		}
		// 左边文字物品栏
		userTextView = (TextView) findViewById(R.id.user_name);
		beanTextView = (TextView) findViewById(R.id.bean_count);
		diamondTextView = (TextView) findViewById(R.id.diamond_count);
		zhiShangTv = (TextView) findViewById(R.id.gift_zhishang);
		zhiShangTv.setText("" + (TextUtils.isEmpty(user.getTitle()) ? "" : user.getTitle()) + "  等级 " + user.getIq());
		userTextView.setText(user.getNickname());
		reportLl = (LinearLayout) findViewById(R.id.report_ll);
		reportBtn = (Button) findViewById(R.id.report_btn);
		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		if (null != cacheUser && cacheUser.getAccount().equals(user.getAccount())) {
			reportLl.setVisibility(View.GONE);
		}
		reportBtn.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				GameDialog gameDialog = new GameDialog(Database.currentActivity, true) {

					public void okClick() {
						//提交举报信息
						CmdDetail chat = new CmdDetail();
						chat.setCmd(CmdUtils.CMD_COMPLAINTS);
						PlayFraudReport pfr = new PlayFraudReport();
						pfr.setDefendant(user.getAccount());
						String dj = JsonHelper.toJson(pfr);
						chat.setDetail(dj);
						CmdUtils.sendMessageCmd(chat);//发送举报信息
						PhotoDialog.this.dismiss();
					}

					public void cancelClick() {
						PhotoDialog.this.dismiss();
					}
				};
				gameDialog.show();
				gameDialog.setText("确定举报此玩家？无故举报会有惩罚哦！");
			}
		});
		//goodsList = (GridView) findViewById(R.id.goods_info);
		//goodsList.setSelector(new ColorDrawable(Color.TRANSPARENT));
		freshUserInfo();
	}

	private void freshUserInfo() {
		new Thread() {

			public void run() {
				final GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("account", showAccount);
				paramMap.put("loginToken",cacheUser.getLoginToken());
				paramMap.put("signKey",cacheUser.getAuthKey());
				paramMap.put("type", String.valueOf(user.getType()));
				paramMap.put("code", String.valueOf(Database.JOIN_ROOM.getCode()));
				paramMap.put("hallCode", String.valueOf(Database.GAME_TYPE));
				try {
					String result = HttpUtils.post(HttpURL.COUPON_INFO_URL, paramMap,true);
					userInfo = JsonHelper.fromJson(result, new TypeToken<List<UserGoods>>() {});
					if (userInfo != null && userInfo.size() > 0) {
						Database.currentActivity.runOnUiThread(new Runnable() {
							public void run() {
								for (int i = 0; i < userInfo.size(); i++) {
									if (userInfo != null && userInfo.get(i).getDisplay().intValue() == 2) {
										imageurl = (userInfo.get(i).getGoods());
									}
									if (userInfo != null && userInfo.get(i).getDisplay().intValue() == 1) {
										//金豆2 抽奖券1 钻石3
										textGoods = (userInfo.get(i).getGoods());
										if (textGoods != null && textGoods.size() > 0) {
											for (int j = 0; j < textGoods.size(); j++) {
												if (textGoods.get(j).getTypeId().trim().equals("2")) {
													if (textGoods.get(j).getCouponNum() > 100000) {
														beanTextView.setText(textGoods.get(j).getCouponNum() / 10000 + "W");
														Log.i("freshUserInfo", "textGoods:" + textGoods.get(j).getCouponNum());
													} else {
														beanTextView.setText("" + textGoods.get(j).getCouponNum());
														Log.i("freshUserInfo", "textGoods:" + textGoods.get(j).getCouponNum());
													}
													if(null != cacheUser && cacheUser.getAccount().equals(user.getAccount())){
														cacheUser.setBean(textGoods.get(j).getCouponNum());
														GameCache.putObj(CacheKey.GAME_USER, cacheUser);
													}
												}
												if (textGoods.get(j).getTypeId().trim().equals("3")) {
													diamondTextView.setText("" + textGoods.get(j).getCouponNum());
												}
											}
										}
									}
								}
								int space = 8;
								int numColumn = 90;
								int size = 0;
								if (imageurl != null && imageurl.size() > 0) {
									size = imageurl.size();
								}
								if (size < 4) {
									size = 4;
								}
								//LayoutParams linearParams = (LinearLayout.LayoutParams) goodsList.getLayoutParams(); // 取控件mGrid当前的布局参数
								//linearParams.width = size * (mst.adjustXIgnoreDensity(numColumn + space)) + 20;
								//goodsList.setLayoutParams(linearParams);
								//goodsList.setNumColumns(size);
								//goodsList.setColumnWidth(mst.adjustXIgnoreDensity(numColumn));
								//goodsList.setHorizontalSpacing(mst.adjustXIgnoreDensity(space));
								//goodsList.setStretchMode(GridView.NO_STRETCH);
								//GoodsValuesAdapter valueAdapter = new GoodsValuesAdapter(imageurl);
								//goodsList.setAdapter(valueAdapter);
							}
						});
					} else {}
				} catch (Exception e) {}
			}
		}.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		dismiss();
		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.photo_info_layout:
				dismiss();
				break;
			default:
				break;
		}
	}

	@Override
	public void dismiss() {
		mst.unRegisterView(mainLayout);
		super.dismiss();
	}
}
