package com.lordcard.ui;

import com.zzyddz.shui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.ContentTitle;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.Goods;
import com.lordcard.entity.GoodsDetails;
import com.lordcard.entity.GoodsExchange;
import com.lordcard.entity.GoodsPart;
import com.lordcard.entity.GoodsTypeDetail;
import com.lordcard.entity.UserGoods;
import com.lordcard.network.base.ThreadPool;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpCallback;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.lordcard.ui.base.BaseActivity;
import com.lordcard.ui.view.dialog.DigtailStoveDialog;
import com.lordcard.ui.view.dialog.StoveDialog;
import com.umeng.analytics.MobclickAgent;

public class StoveActivity extends BaseActivity implements OnClickListener {

	private List<LinearLayout> taskLayoutList = new ArrayList<LinearLayout>();
	private LinearLayout beanLayout, digitallLayout, helplayout;
	private ListView beanlListView, digitaListView, helpListView;
	private StoveAdapter beanAdapter, digitalAdapter;
	private Button beanView, digitalView, helpbutton;
	private StoveDialog stvDialog;
	private DigtailStoveDialog digitalDetail;
	private GoodsTypeDetail digiDetail;
	private int page;
	private List<Goods> imageurl = new ArrayList<Goods>();
	private int beanSelect, animalSelect, degtailSelect;
	private RelativeLayout mainLayout;
//	private MainMenuBar mMainMenuBar;
	private List<ContentTitle> helpDatalist;
	private List<TextView> detailList;
	private List<GoodsDetails> detailtextList;
	private final int HC_BEAN = 0, HC_DIGITAL = 1;//合成类型:金豆、数码、物品
	//	private  Map<Integer, Bitmap> beanMap; //金豆图片Map
	//	private  Map<Integer, Bitmap> digitalMap; //数码图片Map
	//	private  Map<Integer, Bitmap> animalMap; //物品图片Map
	//
	private Map<Integer, View> beanViewMap; //金豆ViewMap
	private Map<Integer, View> digitalViewMap; //数码ViewMap
	private HelpAdapter guideAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Database.currentActivity = this;
		//		Database.StoveDialogShow = false;
		setContentView(R.layout.goods_stove);
		Bundle bundle = this.getIntent().getExtras();
		mainLayout = (RelativeLayout) findViewById(R.id.stove_layout);
		mst.adjustView(mainLayout);
		mainLayout.setBackgroundResource(R.drawable.join_bj);
		beanView = (Button) findViewById(R.id.stove_bean);
		digitalView = (Button) findViewById(R.id.stove_digital);
		helpbutton = (Button) findViewById(R.id.stove_help);
		beanView.setOnClickListener(this);
		digitalView.setOnClickListener(this);
		helpbutton.setOnClickListener(this);
		findViewById(R.id.set_back).setOnClickListener(this);
//		mMainMenuBar = (MainMenuBar) findViewById(R.id.main_page_bottom_rl);
		beanLayout = (LinearLayout) findViewById(R.id.game_bean_layout);
		digitallLayout = (LinearLayout) findViewById(R.id.game_digital_layout);
		helplayout = (LinearLayout) findViewById(R.id.game_help_layout);
		taskLayoutList.add(digitallLayout);
		taskLayoutList.add(beanLayout);
		taskLayoutList.add(helplayout);
		//		beanMap = new HashMap<Integer, Bitmap>();
		//		digitalMap = new HashMap<Integer, Bitmap>();
		//		animalMap = new HashMap<Integer, Bitmap>();
		beanViewMap = new HashMap<Integer, View>();
		digitalViewMap = new HashMap<Integer, View>();
		beanlListView = (ListView) findViewById(R.id.bean_stove_list);
		digitaListView = (ListView) findViewById(R.id.digital_stove_list);
		helpListView = (ListView) findViewById(R.id.help_list_view);
		if (bundle != null) {
			page = bundle.getInt("page");
			if (page == 0) {
				beanView.setTextColor(Color.WHITE);
				beanView.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center_select, true));
				digitalView.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_left, true));
				helpbutton.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_right, true));
				getPageView(1);
				if (Database.GOODS_STOVE_BEAN != null) {
					handler.sendEmptyMessage(0);
				} else {
					gameStove("1", new HttpCallback() {

						public void onSucceed(Object... obj) {
							try {
								String result = (String) obj[0];
								Database.GOODS_STOVE_BEAN = JsonHelper.fromJson(result, new TypeToken<List<GoodsExchange>>() {});
								handler.sendEmptyMessage(0);
							} catch (Exception e) {}
						}

						public void onFailed(Object... obj) {}
					});
				}
			} else if (page == 3) {
				helpbutton.setTextColor(Color.WHITE);
				beanView.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center, true));
				digitalView.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_left, true));
				helpbutton.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_right_select, true));
				gethelp();
				getPageView(2);
			} else if (page == 2) {
				digitalView.setTextColor(Color.WHITE);
				beanView.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center, true));
				digitalView.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_left_select, true));
				helpbutton.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_right, true));
				getPageView(0);
				if (Database.GOODS_STOVE_DIGITAL != null) {
					handler.sendEmptyMessage(2);
				} else {
					gameStove("3", new HttpCallback() {

						public void onSucceed(Object... obj) {
							try {
								String result = (String) obj[0];
								Database.GOODS_STOVE_DIGITAL = JsonHelper.fromJson(result, new TypeToken<List<GoodsExchange>>() {});
								handler.sendEmptyMessage(2);
							} catch (Exception e) {}
						}

						public void onFailed(Object... obj) {}
					});
				}
			}
		} else {
			digitalView.setTextColor(Color.WHITE);
			if (Database.GOODS_STOVE_DIGITAL != null) {
				handler.sendEmptyMessage(2);
			} else {
				gameStove("3", new HttpCallback() {

					public void onSucceed(Object... obj) {
						try {
							String result = (String) obj[0];
							Database.GOODS_STOVE_DIGITAL = JsonHelper.fromJson(result, new TypeToken<List<GoodsExchange>>() {});
							handler.sendEmptyMessage(2);
						} catch (Exception e) {}
					}

					public void onFailed(Object... obj) {}
				});
			}
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					if (beanSelect != 1) {
						beanSelect = 1;
						if (null != Database.GOODS_STOVE_BEAN) {
							beanAdapter = new StoveAdapter(StoveActivity.this, Database.GOODS_STOVE_BEAN, HC_BEAN);
							beanlListView.setAdapter(beanAdapter);
						}
					}
					break;
				case 1:
//				if (animalSelect != 2) {
//					animalSelect = 2;
//					if (null != Database.GOODS_STOVE_ANIMAL) {
//						animalAdapter = new StoveAdapter(StoveActivity.this, Database.GOODS_STOVE_ANIMAL, HC_ANIMAL);
//						animalslListView.setAdapter(animalAdapter);
//					}
//				}
					break;
				case 2:
					if (degtailSelect != 3) {
						degtailSelect = 3;
						if (null != Database.GOODS_STOVE_DIGITAL) {
							digitalAdapter = new StoveAdapter(StoveActivity.this, Database.GOODS_STOVE_DIGITAL, HC_DIGITAL);
							digitaListView.setAdapter(digitalAdapter);
						}
					}
					break;
				case 3:
					//				if (!Database.StoveDialogShow) {
					//					stvDialog.show();
					//					Database.StoveDialogShow = true;
					//				}
					if (stvDialog != null && !stvDialog.isShowing()) {
						stvDialog.show();
					}
					break;
				case 4:
					//				if(digitalDetail == null){
					//					digitalDetail = new DigtailStoveDialog(Database.currentActivity, R.style.dialog, digiDetail);
					//				}
					//				if(!digitalDetail.isShowing()){
					//					digitalDetail.show();
					//				}
					if (digitalDetail != null) {
						digitalDetail.dismiss();
						digitalDetail = null;
					}
					digitalDetail = new DigtailStoveDialog(Database.currentActivity, R.style.dialog, digiDetail);
					digitalDetail.show();
					//				if (!Database.StoveDialogShow) {
					//					digitalDetail.show();
					//					Database.StoveDialogShow = true;
					//				}
					break;
			}
		}
	};

	public void onClick(View v) {
		if (v.getId() == R.id.stove_digital || v.getId() == R.id.stove_bean || v.getId() == R.id.stove_help) {
			beanView.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
			digitalView.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
			helpbutton.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
		}
		switch (v.getId()) {
			case R.id.set_back:
				MobclickAgent.onEvent(CrashApplication.getInstance(), "合成返回");
				finishSelf();
				break;
			case R.id.stove_bean:
				MobclickAgent.onEvent(CrashApplication.getInstance(), "金豆合成");
				beanView.setTextColor(Color.WHITE);
				beanView.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center_select, true));
				digitalView.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_left, true));
				helpbutton.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_right, true));
				getPageView(1);
				if (Database.GOODS_STOVE_BEAN != null) {
					handler.sendEmptyMessage(0);
				} else {
					gameStove("1", new HttpCallback() {

						public void onSucceed(Object... obj) {
							try {
								String result = (String) obj[0];
								Database.GOODS_STOVE_BEAN = JsonHelper.fromJson(result, new TypeToken<List<GoodsExchange>>() {});
								handler.sendEmptyMessage(0);
							} catch (Exception e) {}
						}

						public void onFailed(Object... obj) {}
					});
				}
				break;
			case R.id.stove_digital:
				MobclickAgent.onEvent(CrashApplication.getInstance(), "数码合成");
				digitalView.setTextColor(Color.WHITE);
				beanView.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center, true));
				digitalView.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_left_select, true));
				helpbutton.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_right, true));
				getPageView(0);
				if (Database.GOODS_STOVE_DIGITAL != null) {
					handler.sendEmptyMessage(2);
				} else {
					gameStove("3", new HttpCallback() {

						public void onSucceed(Object... obj) {
							try {
								String result = (String) obj[0];
								Database.GOODS_STOVE_DIGITAL = JsonHelper.fromJson(result, new TypeToken<List<GoodsExchange>>() {});
								handler.sendEmptyMessage(2);
							} catch (Exception e) {}
						}

						public void onFailed(Object... obj) {}
					});
				}
				break;
			case R.id.stove_help:
				MobclickAgent.onEvent(CrashApplication.getInstance(), "合成帮助");
				helpbutton.setTextColor(Color.WHITE);
				beanView.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center, true));
				digitalView.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_left, true));
				helpbutton.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_right_select, true));
				gethelp();
				getPageView(2);
				break;
			case R.id.stove_layout:
				if (digitalDetail != null) {
					digitalDetail.dismiss();
				}
				break;
			default:
				break;
		}
	};

	public void getPageView(int pageID) {
		for (int i = 0; i < taskLayoutList.size(); i++) {
			if (pageID == i) {
				taskLayoutList.get(i).setVisibility(View.VISIBLE);
			} else {
				taskLayoutList.get(i).setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 合成list公用Adapter
	 * @author Administrator
	 */
	private class StoveAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<GoodsExchange> datalist;
		private int type;//listview类型  HC_BEAN：金豆合成   HC_DIGITAL：数码合成  HC_ANIMAL：物品合成 

		/**
		 * @param context
		 * @param datalist
		 * @param type:0物品合成   1：数码合成  2：金豆合成
		 */
		public StoveAdapter(Context context, List<GoodsExchange> datalist, int type) {
			this.mInflater = LayoutInflater.from(context);
			this.type = type;
			this.datalist = datalist;
		}

		public int getCount() {
			return datalist.size();
		}

		public Object getItem(int position) {
			return datalist.get(position);
		}

		public void setDatalist(List<GoodsExchange> datalist) {
			this.datalist = datalist;
		}

		public long getItemId(int position) {
			return position;
		}

		/**
		 * 释放对应的Bitmap缓存
		 * @param type
		 */
		private void releaseCurrentViews(int type) {
			ListView nowListView;
			Map<Integer, View> nowViewMap;
			//			Map<Integer, Bitmap> nowImgMap;
			switch (type) {
				case HC_BEAN://金豆合成
					nowListView = beanlListView;
					nowViewMap = beanViewMap;
					//				nowImgMap = beanMap;
					break;
				case HC_DIGITAL://数码合成
					nowListView = digitaListView;
					nowViewMap = digitalViewMap;
					//				nowImgMap = digitalMap;
					break;
			}
			//			if (nowViewMap.size() > 12) {
			//				for (int i = 1; i < nowListView.getFirstVisiblePosition() - 2; i++) {
			//					View v = nowViewMap.remove(i);
			//					if (null != v && !v.isShown()) {
			//						v.destroyDrawingCache();
			//						if (nowImgMap != null) {
			//							Bitmap bitmap = nowImgMap.remove(i);
			//							if (null != bitmap) {
			//								bitmap.recycle();
			//							}
			//						}
			//					}
			//				}
			//				for (int i = nowListView.getLastVisiblePosition() + 2; i < datalist.size(); i++) {
			//					View v = nowViewMap.remove(i);
			//					if (null != v && !v.isShown()) {
			//						v.destroyDrawingCache();
			//						if (nowImgMap != null) {
			//							Bitmap bitmap = nowImgMap.remove(i);
			//							if (null != bitmap) {
			//								bitmap.recycle();
			//							}
			//						}
			//					}
			//				}
			//			}
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			Map<Integer, View> viewMap;
			switch (type) {
				case HC_BEAN://金豆合成
					viewMap = beanViewMap;
					break;
				default://数码合成
					viewMap = digitalViewMap;
					break;
			}
			if (!viewMap.containsKey(position) || viewMap.get(position) == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.stove_list_item, null);
				holder.leftiImageView = (ImageView) convertView.findViewById(R.id.stove_left_img);
				holder.rightBtn = (Button) convertView.findViewById(R.id.stove_right_btn);
				holder.stovenick = (TextView) convertView.findViewById(R.id.stove_item_nick);
				holder.stovestuff = (TextView) convertView.findViewById(R.id.stove_item_stuff);
				convertView.setTag(holder);
				viewMap.put(position, convertView);
				releaseCurrentViews(type);//释放不可见的view  
			} else {
				holder = (ViewHolder) viewMap.get(position).getTag();
				convertView = viewMap.get(position);
			}
			try {
				String path = HttpURL.URL_PIC_ALL + datalist.get(position).getPicPath();
				Log.i("bitmapCache", "HttpURL.URL_PIC_ALL: " + HttpURL.URL_PIC_ALL + "            \n datalist.get(position).getPicPath():" + datalist.get(position).getPicPath());
				switch (type) {
					case HC_BEAN://金豆合成
						setImgNoCache(holder.leftiImageView, path, position);
						//(final ImageView view,final String path,final int type,final int position, final Map<Integer, Bitmap> map) {
						break;
					case HC_DIGITAL://数码合成
						setImgNoCache(holder.leftiImageView, path, position);
						break;
				}
				String stovecount = "";
				if (datalist.get(position).getCount() != null && datalist.get(position).getCount().intValue() != 1) {
					stovecount = datalist.get(position).getCount().intValue() + "";
				}
				holder.stovenick.setText(stovecount + datalist.get(position).getTypeName());
				List<GoodsPart> goodsParts = datalist.get(position).getGoodsParts();
				String stoveAllstuff = "";
				for (int i = 0; i < goodsParts.size(); i++) {
					if (goodsParts.get(i).getUnit() != null) {
						if (!TextUtils.isEmpty(goodsParts.get(i).getFromName().trim()) && !"等级".equals(goodsParts.get(i).getFromName().trim())) {
							stoveAllstuff += "+" + goodsParts.get(i).getFromCount().toString() + goodsParts.get(i).getUnit() + goodsParts.get(i).getFromName();
						}
					} else {
						if (!TextUtils.isEmpty(goodsParts.get(i).getFromName().trim()) && !"等级".equals(goodsParts.get(i).getFromName().trim())) {
							stoveAllstuff += "+" + goodsParts.get(i).getFromCount().toString() + goodsParts.get(i).getFromName();
						}
					}
				}
				stoveAllstuff = stoveAllstuff.replaceFirst("\\+", " ");
				if (datalist.get(position).getAbout() != null) {
					stoveAllstuff += datalist.get(position).getAbout();
				}
				holder.stovestuff.setText(stoveAllstuff, TextView.BufferType.SPANNABLE);
				if (HC_DIGITAL == type) {//数码合成
					freshBag(holder.stovestuff, stoveAllstuff);
				}
				holder.leftiImageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (HC_DIGITAL == type) {//数码合成
							ThreadPool.startWork(new Runnable() {

								public void run() {
									MobclickAgent.onEvent(CrashApplication.getInstance(), "数码头像");
									Map<String, String> paramMap = new HashMap<String, String>();
									paramMap.put("goodsId", datalist.get(position).getTypeId());
									String result = HttpUtils.post(HttpURL.STOVE_DIGIDETAIL, paramMap, true);
									digiDetail = JsonHelper.fromJson(result, new TypeToken<GoodsTypeDetail>() {});
									handler.sendEmptyMessage(4);
								}
							});
						}
					}
				});
				holder.rightBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						MobclickAgent.onEvent(CrashApplication.getInstance(), "合成按钮");
						stvDialog = new StoveDialog(Database.currentActivity, R.style.dialog, datalist.get(position).getTypeName(), datalist.get(position).getType(), datalist.get(position).getTypeId(), datalist.get(position).getCount(), datalist.get(position).getGoodsParts());
						handler.sendEmptyMessage(3);
					}
				});
			} catch (Exception e) {}
			mst.adjustView(parent);
			return convertView;
		}

		@SuppressWarnings("unused")
		public class ViewHolder {

			public TextView stovenick, stovestuff;
			public ImageView leftiImageView;
			public Button rightBtn;
		}
	}

	private void gameStove(final String type, final HttpCallback callback) {
		ThreadPool.startWork(new Runnable() {

			public void run() {
				try {
					String url = HttpURL.STOVE_BEAN_URL + "?type=" + type;
					String result = HttpUtils.post(url, null);
					callback.onSucceed(result);
				} catch (Exception e) {
					callback.onFailed();
				}
			}
		});
	}

	private void freshBag(final TextView tv, final String stuff) {
		ThreadPool.startWork(new Runnable() {

			public void run() {
				GameUser gameUser = (GameUser)GameCache.getObj(CacheKey.GAME_USER);
				if (null == gameUser || null ==gameUser.getLoginToken()) {
					return;
				}
				String longinString = gameUser.getLoginToken();
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("loginToken", longinString);
				try {
					String result = HttpUtils.post(HttpURL.GOODS_BAG_URL, paramMap);
					Database.GOODS_BAG_LIST = JsonHelper.fromJson(result, new TypeToken<List<UserGoods>>() {});
					if (Database.GOODS_BAG_LIST != null && Database.GOODS_BAG_LIST.size() > 0) {
						Database.currentActivity.runOnUiThread(new Runnable() {

							public void run() {
								for (int i = 0; i < Database.GOODS_BAG_LIST.size(); i++) {
									if (Database.GOODS_BAG_LIST != null && Database.GOODS_BAG_LIST.get(i).getDisplay().intValue() == 2) {
										imageurl = (Database.GOODS_BAG_LIST.get(i).getGoods());
									}
								}
								if (imageurl != null && imageurl.size() > 0) {
									for (int j = 0; j < imageurl.size(); j++) {
										if (stuff.indexOf(imageurl.get(j).getName()) != -1) {
											int star = stuff.indexOf(imageurl.get(j).getName());
											int end = stuff.indexOf(imageurl.get(j).getName()) + imageurl.get(j).getName().length();
											Spannable spn = (Spannable) tv.getText();
											spn.setSpan(new ForegroundColorSpan(Color.YELLOW), star, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
											spn.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), star, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
										}
									}
								}
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void gethelp() {
		if (Database.STOVE_GUIDE_LIST != null && Database.STOVE_GUIDE_LIST.size() > 0) {
			helpDatalist = Database.STOVE_GUIDE_LIST;
			HelpAdapter guideAdapter = new HelpAdapter(this, helpDatalist);
			helpListView.setAdapter(guideAdapter);
		} else {
			ThreadPool.startWork(new Runnable() {

				public void run() {
					try {
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("type", Constant.GUIDE_TYPE_TWO);
						String result = HttpUtils.post(HttpURL.GAME_GUIDE_URL, paramMap, true);
						Database.STOVE_GUIDE_LIST = JsonHelper.fromJson(result, new TypeToken<List<ContentTitle>>() {});
						helpDatalist = Database.STOVE_GUIDE_LIST;
						if (helpDatalist != null && helpDatalist.size() > 0) {
							Database.currentActivity.runOnUiThread(new Runnable() {

								public void run() {
									guideAdapter = new HelpAdapter(StoveActivity.this, helpDatalist);
									helpListView.setAdapter(guideAdapter);
								}
							});
						}
					} catch (Exception e) {}
				}
			});
		}
	}

	/**
	 * 初始化物品指南
	 */
	private class HelpAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<ContentTitle> datalist;

		public HelpAdapter(Context context, List<ContentTitle> datalist) {
			this.mInflater = LayoutInflater.from(context);
			this.datalist = datalist;
			detailList = new ArrayList<TextView>();
		}

		public void setDatalist(List<ContentTitle> datalist) {
			this.datalist = datalist;
		}

		@Override
		public int getCount() {
			return datalist.size();
		}

		@Override
		public Object getItem(int position) {
			return datalist.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.stove_guide_item, null);
				holder.guideItem = (TextView) convertView.findViewById(R.id.guide_item_data);
				holder.deitail = (TextView) convertView.findViewById(R.id.guide_text_data);
				holder.imageView = (ImageView) convertView.findViewById(R.id.guide_left_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.deitail.setVisibility(View.VISIBLE);
			detailList.add(holder.deitail);
			holder.guideItem.setText(datalist.get(position).getTitle().replace("?", "") + "  ");
			detailtextList = JsonHelper.fromJson(helpDatalist.get(position).getDescription(), new TypeToken<List<GoodsDetails>>() {});
			if (detailtextList != null && detailtextList.size() > 0) {
				holder.deitail.setText(detailtextList.get(0).getText());
			}
			return convertView;
		}

		@SuppressWarnings("unused")
		public class ViewHolder {

			public TextView guideItem;
			public TextView deitail;
			public ImageView imageView;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//		mst.unRegisterView(mainLayout);
		taskLayoutList = null;
		imageurl = null;
		helpDatalist = null;
		detailList = null;
		detailtextList = null;
		try {
			if (null != beanViewMap) {
				Iterator<Map.Entry<Integer, View>> it = beanViewMap.entrySet().iterator();
				while (it.hasNext()) {
					try {
						View v = it.next().getValue();
						if (null != v && !v.isShown()) {
							v.destroyDrawingCache();
						}
					} catch (Exception e) {}
				}
				beanViewMap.clear();
				beanViewMap = null;
			}
			if (null != digitalViewMap) {
				Iterator<Map.Entry<Integer, View>> it = digitalViewMap.entrySet().iterator();
				while (it.hasNext()) {
					try {
						View v = it.next().getValue();
						if (null != v && !v.isShown()) {
							v.destroyDrawingCache();
						}
					} catch (Exception e) {}
				}
				digitalViewMap.clear();
				digitalViewMap = null;
			}
			//			if (beanMap != null) {
			//				Iterator<Map.Entry<Integer, Bitmap>> it = beanMap.entrySet().iterator();
			//				while (it.hasNext()) {
			//					try {
			//						it.next().getValue().recycle();
			//					} catch (Exception e) {
			//					}
			//				}
			//				beanMap.clear();
			//			}
			//			if (digitalMap != null) {
			//				Iterator<Map.Entry<Integer, Bitmap>> it = digitalMap.entrySet().iterator();
			//				while (it.hasNext()) {
			//					try {
			//						it.next().getValue().recycle();
			//					} catch (Exception e) {
			//					}
			//				}
			//				digitalMap.clear();
			//			}
			//			if (animalMap != null) {
			//				Iterator<Map.Entry<Integer, Bitmap>> it = animalMap.entrySet().iterator();
			//				while (it.hasNext()) {
			//					try {
			//						it.next().getValue().recycle();
			//					} catch (Exception e) {
			//					}
			//				}
			//				animalMap.clear();
			//			}
			//
			//			beanMap = null;
			//			digitalMap = null;
			//			animalMap = null;
			if (beanAdapter != null) {
				beanAdapter.setDatalist(null);
				beanAdapter = null;
			}
			if (digitalAdapter != null) {
				digitalAdapter.setDatalist(null);
				digitalAdapter = null;
			}
			if (guideAdapter != null) {
				guideAdapter.setDatalist(null);
				guideAdapter = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//		Database.StoveDialogShow = false;
		//		ImageUtil.clearAdvImageCacheMap();
//		if(mMainMenuBar != null){
//			mMainMenuBar.onDestory();
//			mMainMenuBar = null;
//		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 重写返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			MobclickAgent.onEvent(CrashApplication.getInstance(), "合成实反键");
//			if (mMainMenuBar.getGoodsLayout().getVisibility() == View.VISIBLE) {
//				mMainMenuBar.getGoodsLayout().setVisibility(View.GONE);
//				mMainMenuBar.getTransparentTv().setVisibility(View.GONE);
//				return true;
//			} else {
//				try {
//					finishSelf();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
			try {
				finishSelf();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setImgNoCache(final ImageView view, final String path, final int position) {
		Bitmap bitmap = ImageUtil.getBitmap(path, false);
		if (bitmap != null && !bitmap.isRecycled()) {
			try {
				view.setScaleType(ScaleType.FIT_XY);
				view.setImageBitmap(bitmap);
			} catch (Exception e) {
				System.out.println("========复复引用 ================");
			}
		} else {
			final Handler handler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					//					map.put(position, (Bitmap) msg.obj);
					view.setScaleType(ScaleType.FIT_XY);
					//					view.setImageBitmap(map.get(position));
					view.setImageBitmap((Bitmap) msg.obj);
				}
			};
			new Thread() {

				public void run() {
					Bitmap tempBitmap = ImageUtil.getBitMapFromNetWork(path);
					Message message = handler.obtainMessage(0, tempBitmap);
					handler.sendMessage(message);
				}
			}.start();
		}
		//		//判断缓存是否存在
		//		if (null != map && map.containsKey(position)) {
		//			view.setScaleType(ScaleType.FIT_XY);
		//			view.setImageBitmap(map.get(position));
		//			return;
		//		}
		//
		//		//判断SD卡或内存卡是否存在
		//		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
		//		if (sdCardExist) {
		//			if (null != ImageUtil.getImageFromSdCard(path)) {
		//				Log.i("prizeImage", "advImageCacheMap从SD卡取：" + path);
		//				map.put(position, ImageUtil.getImageFromSdCard(path));
		//				view.setScaleType(ScaleType.FIT_XY);
		//				view.setImageBitmap(map.get(position));
		//				return;
		//			}
		//		} else {
		//			if (null != ImageUtil.getImageFromData(path)) {
		//				Log.i("prizeImage", "advImageCacheMap从内存取：" + path);
		//				map.put(position, ImageUtil.getImageFromData(path));
		//				view.setScaleType(ScaleType.FIT_XY);
		//				view.setImageBitmap(map.get(position));
		//				return;
		//			}
		//		}
		//		//开线程下载
		//		new Thread() {
		//			@Override
		//			public void run() {
		//				byte[] data = null;
		//				try {
		//					data = DownloadUtils.getImage(path);
		//				} catch (Exception e) {
		//				}
		//				if (data != null && data.length != 0) {
		//					BitmapFactory.Options options = new BitmapFactory.Options();
		//					options.inPreferredConfig = Bitmap.Config.RGB_565;
		//					options.inSampleSize = 2;
		//					Bitmap tempBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		//					boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
		//					if (sdCardExist) {
		//						ImageUtil.saveImageToSdCard(path, tempBitmap);
		//					} else {
		//						ImageUtil.saveImageToData(path, tempBitmap);
		//						Log.i("joinRoom", "------------------------保存图片：" + path + "----------------------------------");
		//					}
		//					Message message = handler.obtainMessage(0, tempBitmap);
		//					handler.sendMessage(message);
		//				}
		//			}
		//		}.start();
	}
}
