package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.google.gson.reflect.TypeToken;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.SettingUtils;
import com.lordcard.common.util.ImageUtil.ImageCallback;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Database;
import com.lordcard.entity.GamePropsType;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.Goods;
import com.lordcard.entity.GoodsDetails;
import com.lordcard.entity.GoodsType;
import com.lordcard.entity.JsonResult;
import com.lordcard.entity.UserGoods;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.lordcard.ui.ShowGirlActivity;
import com.lordcard.ui.StoveActivity;

/**物品囊
 * @author Administrator
 */
public class BagDialog extends Dialog implements OnClickListener {

	private Context context;
	private GoodsType fristGood;
	private List<Goods> textGoods = new ArrayList<Goods>();
	private List<Goods> imageurl = new ArrayList<Goods>();
	private LinearLayout mainLayout;
	private Button rechangeBtn, closeButton;
	private Map<String, String> proposMap;
	private TextView lefttopText;
	private TextView lettopCount;
	private TextView righttopText;
	private TextView righttopCount;
	private TextView leftbottomText;
	private TextView leftbottomCount, textView;
	private ImageView rightHead;
	private TextView rightHeadaname;
	private TextView shengxiaodiscribe;
	private ListView getWay;
	private GridView goodsgrid;
	private GoodsType gridItemMegDetails;
	ArrayAdapter<String> textAdapter;
	private String imageUrl;
	private GoodsAdapter adapter;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private List<Map<String, String>> girlList;
	private ProgressDialog waitDialog;
	private Handler mHandler;
	public static final int DOWN_MEI_NU_OK = 1101;

	protected BagDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
	}

	public BagDialog(Context context) {
		super(context, R.style.dialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goods_list);
		mainLayout = (LinearLayout) findViewById(R.id.goods_list_layout);
		mainLayout.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.photo_bg, false));
		layout(context);
		mst.adjustView(mainLayout);
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case DOWN_MEI_NU_OK://下载图片更新按钮状态
						rechangeBtn.setText("浏览");
						break;
					default:
						break;
				}
			}
		};
	}

	public void setDismiss() {}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	private void layout(final Context context) {
		// 左边文字物品栏
		lefttopText = (TextView) findViewById(R.id.top_left_text);
		lettopCount = (TextView) findViewById(R.id.top_left_count);
		righttopText = (TextView) findViewById(R.id.top_right_text);
		righttopCount = (TextView) findViewById(R.id.top_right_count);
		leftbottomText = (TextView) findViewById(R.id.bottom_left_text);
		leftbottomCount = (TextView) findViewById(R.id.bottom_left_count);
		textView = (TextView) findViewById(R.id.values_textview);
		
		boolean isCheck = SettingUtils.getBoolean(SettingUtils.GAME_CHECK);
		if(isCheck){
			textView.setVisibility(View.GONE);
		}
		
		closeButton = (Button) findViewById(R.id.bag_close);
		closeButton.setOnClickListener(this);
		// 右边物品信息
		rightHead = (ImageView) findViewById(R.id.shengxiao_head);
		rightHeadaname = (TextView) findViewById(R.id.shengxiao_name);
		shengxiaodiscribe = (TextView) findViewById(R.id.shengxiao_describe);
		getWay = (ListView) findViewById(R.id.shengxiao_way);
		List<GoodsDetails> listdDetal = new ArrayList<GoodsDetails>();
		listdDetal.add(new GoodsDetails());
		listdDetal.add(new GoodsDetails());
		listdDetal.get(0).setText("");
		listdDetal.get(1).setText("");
		TextAdapter textAdapter = new TextAdapter(listdDetal);
		getWay.setAdapter(textAdapter);
		// textAdapter = new ArrayAdapter<String>(context,
		// R.layout.getway_list_item, textList);
		// getWay.setAdapter(textAdapter);
		int totalHeight = 0;
		for (int i = 0, len = textAdapter.getCount(); i < len; i++) { // stAdapter.getCount()返回数据项的数目
			View listItem = textAdapter.getView(i, null, getWay);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = getWay.getLayoutParams();
		params.height = mst.adjustYIgnoreDensity(totalHeight + (getWay.getDividerHeight() * (textAdapter.getCount())) + 80);
		getWay.setLayoutParams(params);
		getWay.setEnabled(false);
		goodsgrid = (GridView) findViewById(R.id.goodsgridviews);
		goodsgrid.setPadding(1, 1, 1, 1);
		adapter = new GoodsAdapter(imageurl);
		goodsgrid.setAdapter(adapter);
		textView.setOnClickListener(this);
		// 跳转物品宝鉴
		rechangeBtn = (Button) findViewById(R.id.recharge_btn);
		rechangeBtn.setOnClickListener(this);
		// fresHandler.sendEmptyMessage(0);
		freshBag();
		goodsgrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				if (arg2 < imageurl.size()) {
					new Thread() {

						public void run() {
							Map<String, String> paramMap = new HashMap<String, String>();
							paramMap.put("typeId", imageurl.get(arg2).getTypeId());
							try {
								String result = HttpUtils.post(HttpURL.GOODS_BAG_MSG, paramMap, true);
								gridItemMegDetails = JsonHelper.fromJson(result, new TypeToken<GoodsType>() {});
								Database.currentActivity.runOnUiThread(new Runnable() {

									public void run() {
										String prpos = gridItemMegDetails.getProps();
										proposMap = JsonHelper.fromJson(prpos, new TypeToken<Map<String, String>>() {});
										if (proposMap != null) {
											if (proposMap.get("oper").equals("0")) {
												rechangeBtn.setVisibility(View.GONE);
											} else {
												rechangeBtn.setVisibility(View.VISIBLE);
												rechangeBtn.setText(proposMap.get("name"));
											}
										} else {
											rechangeBtn.setVisibility(View.GONE);
										}
										ImageUtil.setImg(HttpURL.URL_PIC_ALL + gridItemMegDetails.getPicPath(), rightHead, new ImageCallback() {

											public void imageLoaded(Bitmap bitmap, ImageView view) {
												view.setScaleType(ScaleType.FIT_XY);
												view.setImageBitmap(bitmap);
											}
										});
										rightHeadaname.setText(gridItemMegDetails.getName());
										shengxiaodiscribe.setText(gridItemMegDetails.getTitle());
										List<GoodsDetails> gridItemDec = JsonHelper.fromJson(gridItemMegDetails.getDescription(), new TypeToken<List<GoodsDetails>>() {});
										if (gridItemDec == null) {
											return;
										}
										TextAdapter textAdapter = new TextAdapter(gridItemDec);
										getWay.setAdapter(textAdapter);
										int totalHeight = 0;
										for (int i = 0, len = textAdapter.getCount(); i < len; i++) { // stAdapter.getCount()返回数据项的数目
											View listItem = textAdapter.getView(i, null, getWay);
											listItem.measure(0, 0); // 计算子项View
																	// 的宽高
											totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
										}
										ViewGroup.LayoutParams params = getWay.getLayoutParams();
										params.height = mst.adjustYIgnoreDensity(totalHeight + (getWay.getDividerHeight() * (textAdapter.getCount())) + 80);
										// stView.getDividerHeight()获取子项间分隔符占用的高度
										// params.height最后得到整个ListView完整显示需要的高度
										getWay.setLayoutParams(params);
										getWay.setEnabled(false);
									}
								});
							} catch (Exception e) {}
						}
					}.start();
				}
			}
		});
	}

	private void freshBag() {
		// 掉后台 获取包裹物品信息和第一个物品信息
		new Thread() {

			public void run() {
				GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
				if (cacheUser == null) {
					return;
				}
				String longinString = cacheUser.getLoginToken();
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("loginToken", longinString);
				try {
					String result = HttpUtils.post(HttpURL.GOODS_BAG_URL, paramMap, true);
					Database.GOODS_BAG_LIST = JsonHelper.fromJson(result, new TypeToken<List<UserGoods>>() {});
					if (Database.GOODS_BAG_LIST != null && Database.GOODS_BAG_LIST.size() > 0) {
						Database.currentActivity.runOnUiThread(new Runnable() {

							public void run() {
								for (int i = 0; i < Database.GOODS_BAG_LIST.size(); i++) {
									if (Database.GOODS_BAG_LIST != null && Database.GOODS_BAG_LIST.get(i).getDisplay().intValue() == 2) {
										imageurl = (Database.GOODS_BAG_LIST.get(i).getGoods());
										fristGood = Database.GOODS_BAG_LIST.get(i).getGoodsType();
									}
									if (Database.GOODS_BAG_LIST != null && Database.GOODS_BAG_LIST.get(i).getDisplay().intValue() == 1) {
										textGoods = (Database.GOODS_BAG_LIST.get(i).getGoods());
										if (textGoods != null && textGoods.size() > 0) {
											lefttopText.setText(textGoods.get(0).getName() + ":");
											lettopCount.setText(textGoods.get(0).getCouponNum() + "");
										}
										if (textGoods != null && textGoods.size() > 1) {
											righttopText.setText(textGoods.get(1).getName() + ":");
											righttopCount.setText(textGoods.get(1).getCouponNum() + "");
										}
										if (textGoods != null && textGoods.size() > 2) {
											leftbottomText.setText(textGoods.get(2).getName() + ":");
											leftbottomCount.setText(textGoods.get(2).getCouponNum() + "");
										}
									}
								}
								goodsgrid.setPadding(1, 1, 1, 1);
								adapter = new GoodsAdapter(imageurl);
								goodsgrid.setAdapter(adapter);
								if (fristGood != null) {
									String prpo = fristGood.getProps();
									proposMap = JsonHelper.fromJson(prpo, new TypeToken<Map<String, String>>() {});
									if (proposMap != null) {
										if (proposMap.get("oper").equals("0")) {
											rechangeBtn.setVisibility(View.GONE);
										} else {
											rechangeBtn.setVisibility(View.VISIBLE);
											rechangeBtn.setText(proposMap.get("name"));
										}
									} else {}
									// 设置图片
									ImageUtil.setImg(HttpURL.URL_PIC_ALL + fristGood.getPicPath(), rightHead, new ImageCallback() {

										public void imageLoaded(Bitmap bitmap, ImageView view) {
											view.setScaleType(ScaleType.FIT_XY);
											view.setImageBitmap(bitmap);
										}
									});
									rightHeadaname.setText(fristGood.getName());
									shengxiaodiscribe.setText(fristGood.getTitle());
									List<GoodsDetails> test = JsonHelper.fromJson(fristGood.getDescription(), new TypeToken<List<GoodsDetails>>() {});
									if (test == null) {
										rechangeBtn.setVisibility(View.GONE);
										return;
									}
									TextAdapter textAdapter = new TextAdapter(test);
									getWay.setAdapter(textAdapter);
									int totalHeight = 0;
									for (int i = 0, len = textAdapter.getCount(); i < len; i++) { // stAdapter.getCount()返回数据项的数目
										View listItem = textAdapter.getView(i, null, getWay);
										listItem.measure(0, 0); // 计算子项View 的宽高
										totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
									}
									ViewGroup.LayoutParams params = getWay.getLayoutParams();
									params.height = mst.adjustYIgnoreDensity(totalHeight + (getWay.getDividerHeight() * (textAdapter.getCount() - 1)) + 80);
									// stView.getDividerHeight()获取子项间分隔符占用的高度
									// params.height最后得到整个ListView完整显示需要的高度
									getWay.setLayoutParams(params);
								}
							}
						});
					} else {
						rechangeBtn.setVisibility(View.GONE);
					}
				} catch (Exception e) {}
			}
		}.start();
	}

	private Handler fresHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					freshBag();
					break;
				case 1:
					freshBag();
					break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.values_textview:
				MobclickAgent.onEvent(context, "包裹宝鉴");
				EnvalueDialog valueDialog = new EnvalueDialog(context);
				valueDialog.show();
				break;
			case R.id.bag_close:
				dismiss();
				break;
			case R.id.recharge_btn:
				Database.currentActivity.runOnUiThread(new Runnable() {

					public void run() {
						if (gridItemMegDetails == null) {
							gridItemMegDetails = fristGood;
						}
						if (proposMap != null && gridItemMegDetails != null) {
							if (gridItemMegDetails.getPropsSign() == 1) {
								try {
									GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
									//如果为1  是道具
									waitDialog = DialogUtils.getWaitProgressDialog(context, "请稍后...");
									waitDialog.show();
									Map<String, String> paramMap = new HashMap<String, String>();
									paramMap.put("typeId", gridItemMegDetails.getId());
									paramMap.put("loginToken",cacheUser.getLoginToken());
									String result = HttpUtils.post(HttpURL.DAOJU_DETAIL_URL, paramMap,true);
									if (result != null && !result.equals("1")) {//1为返回失败
										Map<String, String> toolDetail = JsonHelper.fromJson(result, new TypeToken<Map<String, String>>() {});
										if ("1".equals(toolDetail.get("type"))) {
											Database.TOOL = JsonHelper.fromJson(toolDetail.get("data"), new TypeToken<GamePropsType>() {});
											if (Database.TOOL.getType().equals("1")) {
												girlList = new ArrayList<Map<String, String>>();
												try {
													girlList = JsonHelper.fromJson(Database.TOOL.getContent(), new TypeToken<List<Map<String, String>>>() {});
												} catch (Exception e) {
													// TODO: handle exception
												}
												boolean hasAll = true;
												for (int i = 0; i < girlList.size(); i++) {
													if (null != ImageUtil.getGirlBitmap(HttpURL.URL_PIC_ALL + girlList.get(i).get("path"), false, true)) {} else {
														hasAll = false;
													}
												}
												if (hasAll) {
													if (waitDialog != null && waitDialog.isShowing()) {
														waitDialog.dismiss();
													}
													Intent intent = new Intent();
													intent.setClass(context, ShowGirlActivity.class);
													context.startActivity(intent);
												} else {
													//没有wifi 提示用户是否下载
													if (waitDialog != null && waitDialog.isShowing()) {
														waitDialog.dismiss();
													}
													GameDialog gameDialog = new GameDialog(Database.currentActivity) {

														public void okClick() {
															for (int i = 0; i < girlList.size(); i++) {
																ImageUtil.downMMImg(HttpURL.URL_PIC_ALL + girlList.get(i).get("path"), mHandler);
																rechangeBtn.setText("下载中..");
															}
															Database.LASTPIC = HttpURL.URL_PIC_ALL + girlList.get(girlList.size() - 1).get("path");
														};
													};
													gameDialog.show();
													gameDialog.setText("该图集未下载完成，是否继续下载？");
												}
											}
										}
									} else {
										DialogUtils.mesToastTip("获取数据失败！");
										if (waitDialog != null && waitDialog.isShowing()) {
											waitDialog.dismiss();
										}
									}
								} catch (Exception e) {
									if (waitDialog != null && waitDialog.isShowing()) {
										waitDialog.dismiss();
									}
									// TODO: handle exception
								}
							}
							if (proposMap.get("oper").equals("1") && Integer.valueOf(gridItemMegDetails.getValue()) >= 30) {// 话费
								MobclickAgent.onEvent(context, "包裹话费券充值");
								RechangeDialog rechangeDialog = new RechangeDialog(gridItemMegDetails.getValue(), fresHandler, context, R.style.dialog, gridItemMegDetails.getId());
								rechangeDialog.show();
							}
							if (proposMap.get("oper").equals("1") && Integer.valueOf(gridItemMegDetails.getValue()) < 30) {// 话费
								MobclickAgent.onEvent(context, "包裹话费合成");
								Bundle bundle = new Bundle();
								bundle.putInt("page", 2);
								Intent stoveIntent = new Intent();
								stoveIntent.setClass(context, StoveActivity.class);
								stoveIntent.putExtras(bundle);
								context.startActivity(stoveIntent);
							}
							if (proposMap.get("oper").equals("3")) {// 填地址
								MobclickAgent.onEvent(context, "包裹领取物品");
								ExchangeDialog exchangeDialog = new ExchangeDialog(fresHandler, context, R.style.dialog, gridItemMegDetails.getId());
								exchangeDialog.show();
							}
							if (proposMap.get("oper").equals("2")) {// 合成
								MobclickAgent.onEvent(context, "包物品合成");
								if (gridItemMegDetails.getCompositeType().intValue() == 2) {// 十二生肖
									Bundle bundle = new Bundle();
									bundle.putInt("page", 2);
									Intent stoveIntent = new Intent();
									stoveIntent.setClass(context, StoveActivity.class);
									stoveIntent.putExtras(bundle);
									context.startActivity(stoveIntent);
								}
							}
							if (proposMap.get("oper").equals("5")) {// 合成
								MobclickAgent.onEvent(context, "积分重置");
								waitDialog = DialogUtils.getWaitProgressDialog(Database.currentActivity, "请稍后...");
								waitDialog.show();
								new Thread() {

									public void run() {
										try {
											GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
											String longinString = cacheUser.getLoginToken();
											Map<String, String> paramMap = new HashMap<String, String>();
											paramMap.put("loginToken", longinString);
											paramMap.put("goodsCode", gridItemMegDetails.getId());
											String result = HttpUtils.post(HttpURL.GOODS_BAG_CALLBACK, paramMap,true);
											if (!TextUtils.isEmpty(result)) {
												JsonResult jsonResult = JsonHelper.fromJson(result, JsonResult.class);
												if (JsonResult.SUCCESS.equals(jsonResult.getMethodCode())) {
													final String mess = jsonResult.getMethodMessage();
													if (!TextUtils.isEmpty(mess)) {
														DialogUtils.mesToastTip(mess);
													}
												}
											}
										} catch (Exception e) {
											Database.currentActivity.runOnUiThread(new Runnable() {

												public void run() {
													if (waitDialog != null && waitDialog.isShowing()) {
														waitDialog.dismiss();
													}
												}
											});
										} finally {
											Database.currentActivity.runOnUiThread(new Runnable() {

												public void run() {
													if (waitDialog != null && waitDialog.isShowing()) {
														waitDialog.dismiss();
													}
												}
											});
										}
									};
								}.start();
							}
						} else {}
					}
				});
				break;
			default:
				break;
		}
	}

	@Override
	public void dismiss() {
		mst.unRegisterView(mainLayout);
		this.fristGood = null;
		adapter = null;
		waitDialog = null;
		super.dismiss();
	}

	/**
	 * 初始化物品栏
	 */
	private class GoodsAdapter extends BaseAdapter {

		private List<Goods> gifInt;
		private LayoutInflater mInflater;

		public GoodsAdapter(List<Goods> goodbagList) {
			this.gifInt = goodbagList;
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			if (gifInt != null && gifInt.size() >= 8) {
				return gifInt.size();
			} else {
				return 8;
			}
		}

		public Object getItem(int position) {
			return gifInt.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.goods_gif_item, null);
				if (gifInt != null && position < gifInt.size()) {
					imageUrl = HttpURL.URL_PIC_ALL + gifInt.get(position).getPicPath();
				}
				ImageView iv = (ImageView) convertView.findViewById(R.id.goodsview);
				iv.setTag(imageUrl);
				TextView tv = (TextView) convertView.findViewById(R.id.goodstextview);
				if (gifInt != null && position < gifInt.size()) {
					tv.setText("" + gifInt.get(position).getCouponNum());
					ImageUtil.setImg(imageUrl, iv, new ImageCallback() {

						public void imageLoaded(Bitmap bitmap, ImageView view) {
							ImageView imageViewByTag = (ImageView) goodsgrid.findViewWithTag(imageUrl);
							if (imageViewByTag != null) {
								view.setImageBitmap(bitmap);
							}
						}
					});
				} else {
					iv.setImageBitmap(null);
				}
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			return convertView;
		}

		public class ViewHolder {}
	}

	private class TextAdapter extends BaseAdapter {

		private List<GoodsDetails> gifInt;
		private LayoutInflater mInflater;

		public TextAdapter(List<GoodsDetails> goodbagList) {
			this.gifInt = goodbagList;
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return gifInt.size();
		}

		public Object getItem(int position) {
			return gifInt.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mViewHolder;
			if (null == convertView) {
				mViewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.text_list_item, null);
				mViewHolder.text = (TextView) convertView.findViewById(R.id.evalues_text);
				convertView.setTag(mViewHolder);
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}
			mViewHolder.text.setText(gifInt.get(position).getText());
			return convertView;
		}

		class ViewHolder {

			private TextView text;// 最高奖励(普通赛制)
		}
	}
}
