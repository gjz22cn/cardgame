package com.lordcard.ui.view;

import com.zzyddz.shui.R;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.anim.AnimUtils;
import com.lordcard.common.bean.AssistantBean;
import com.lordcard.common.bean.DataCentreBean;
import com.lordcard.common.schedule.AutoTask;
import com.lordcard.common.schedule.ScheduledTask;
import com.lordcard.common.upgrade.UpdateUtils;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.ImageUtil.ImageCallback;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.Database;
import com.lordcard.entity.AssistantAction;
import com.lordcard.entity.AssistantBtn;
import com.lordcard.entity.AssistantBtnContent;
import com.lordcard.entity.GameIQUpgrade;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.network.http.HttpURL;
import com.lordcard.ui.LoginActivity;
import com.lordcard.ui.dizhu.DoudizhuRoomListActivity;
import com.lordcard.ui.view.dialog.DownloadDialog;
import com.lordcard.ui.view.dialog.GameDialog;
import com.sdk.constant.SDKConfig;
import com.sdk.jd.sms.util.JDSMSPayUtil;
import com.sdk.util.PaySite;
import com.sdk.util.PayTipUtils;
import com.sdk.util.SDKFactory;

public class Assistant extends RelativeLayout implements OnClickListener {

	private Context context;
	private AssistantListview listView;
	ListView view;
	private AssistantGridview gridView;
	private ImageView MMicon, titleImage;
	private TextView titleText;
	public static Handler ASSHANDLER;
	private Handler centreHandler;
	private ProgressDialog progressDialog;
	private AssistantBtnContent btnContent;
	public static List<HashMap<String, Object>> GAME_ASSISTANT;//缓存游戏助理未处理的消息（有处理不用）
	private List<HashMap<String, Object>> game_assistant;
	private LinearLayout xiaomei;//小美女图标
	private View group;
	private LinearLayout left;//分别为消息内容和大美女图标
	RelativeLayout begin;
	private TextView zhezhao, zhezhao2;
	private Button closeBtn;
//	private ImageView xiaomeiImg;
	private List<String> listMessage;
	private int listNum, btxNum;
	public static String ASSID, BTNCODE;
	MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();

	public Assistant(final Context context, final Handler centreHandler, AssistantBtnContent btnContent, List<HashMap<String, Object>> game_assistant, LinearLayout layout, ImageView xiaomeiImg) {
		super(context);
		this.context = context;
		this.btnContent = btnContent;
		this.game_assistant = game_assistant;
		this.xiaomei = layout;
//		this.xiaomeiImg = xiaomeiImg;
		this.centreHandler = centreHandler;
		if (Database.currentActivity.getLocalClassName().equals(DoudizhuRoomListActivity.class.getName())) {
			GAME_ASSISTANT = game_assistant;
		}
		listNum = 0;
		btxNum = 0;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		group=inflater.inflate(R.layout.assistant_view,null);
		progressDialog = DialogUtils.getWaitProgressDialog(Database.currentActivity, "请稍后.....");
		progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					zhezhao.setVisibility(View.GONE);
					zhezhao2.setVisibility(View.GONE);
				}
				return false;
			}
		});
		initView();
		ASSHANDLER = new Handler() {

			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case 1:
						// 打开应用包
						String path = msg.getData().getString("APKpath");
						Intent finishIntent = ActivityUtils.getInstallIntent(new File(path));
						Database.currentActivity.startActivity(finishIntent);
						break;
					case 2:
						// 动画显示
						if (Database.currentActivity.getClass().equals(DoudizhuRoomListActivity.class)) {
//						Database.ASSCLOSE=false;//放在这里和定时器有点冲突 不能实时
							Database.ADD_DATA_CENTRE = true;//默认可以加入消息中心，如果Assistant中处理过消息这不可加入消息中心
						}
						AnimUtils.startAnimationsInLeft(left, 400);
						AnimUtils.startAnimationsInBottom(begin, 400, 0);
						zhezhao.setVisibility(View.VISIBLE);
						break;
					case 3:
						assistantDismiss();
						break;
				}
			}
		};
	}

	public void initView() {
		this.addView(group);
		listView = (AssistantListview) group.findViewById(R.id.llayout);
		gridView = (AssistantGridview) group.findViewById(R.id.glayout);
		MMicon = (ImageView) group.findViewById(R.id.pic);
		titleImage = (ImageView) group.findViewById(R.id.title_img);
		titleText = (TextView) group.findViewById(R.id.title_tx);
		closeBtn = (Button) group.findViewById(R.id.as_close);
		closeBtn.setOnClickListener(this);
		group.findViewById(R.id.menu_transparent_tv2).setOnClickListener(null);
		left = (LinearLayout) group.findViewById(R.id.left);
		begin = (RelativeLayout) group.findViewById(R.id.begin);
		zhezhao = (TextView) group.findViewById(R.id.menu_transparent_tv2);
		zhezhao2 = (TextView) group.findViewById(R.id.menu_transparent_tv4);
		// title为文本或图片:1文本,2:图片,3消息
		if (Integer.valueOf(Double.valueOf(game_assistant.get(0).get(AssistantBean.AS_DISPLAY).toString()).intValue()).toString().equals("1")) {
			titleText.setVisibility(View.VISIBLE);
			titleText.setText((CharSequence) game_assistant.get(0).get(AssistantBean.AS_CONTENT));
		} else if (Integer.valueOf(Double.valueOf(game_assistant.get(0).get(AssistantBean.AS_DISPLAY).toString()).intValue()).toString().equals("2")) {
			titleImage.setVisibility(View.VISIBLE);
			String path2 = HttpURL.URL_PIC_ALL + game_assistant.get(0).get(AssistantBean.AS_CONTENT).toString();
			ImageUtil.setImg(path2, titleImage, new ImageCallback() {

				public void imageLoaded(Bitmap bitmap, ImageView view) {
					view.setScaleType(ScaleType.FIT_XY);
					view.setImageBitmap(bitmap);
					//大美女图标
					String path = HttpURL.URL_PIC_ALL + game_assistant.get(0).get(AssistantBean.AS_ICON).toString();
					ImageUtil.setImg(path, MMicon, new ImageCallback() {

						public void imageLoaded(Bitmap bitmap, ImageView view) {
							view.setScaleType(ScaleType.FIT_XY);
							view.setImageBitmap(bitmap);
							Message msg = new Message();
							msg.what = 2;
							ASSHANDLER.sendMessage(msg);
						}
					});
				}
			});
		} else {
			listMessage = JsonHelper.fromJson(String.valueOf(game_assistant.get(0).get(AssistantBean.AS_CONTENT)), new TypeToken<List<String>>() {});
			titleText.setVisibility(View.VISIBLE);
			titleText.setText(listMessage.get(0));
		}
		// 设置按钮1:横排,2:竖排
		if (btnContent.getDisplay().equals(1)) {
			listView.setVisibility(View.GONE);
			gridView.setVisibility(View.VISIBLE);
			gridView.setNumColumns(btnContent.getAsstBtns().size());
			gridView.setAdapter(new HorizontalAdapter(Database.currentActivity, btnContent.getAsstBtns()));
		} else {
			//一个按钮 25dp 40px 高
			gridView.setVisibility(View.GONE);
			VerticalAdapter adapter = new VerticalAdapter(context, btnContent.getAsstBtns());
			listView.setAdapter(adapter);
			View item = adapter.getView(0, null, listView);
			item.measure(0, 0);
			int px = item.getMeasuredHeight(); //获取listview item的高度
			//				Toast.makeText(Database.currentActivity, "px"+px, Toast.LENGTH_LONG).show();
			ViewGroup.LayoutParams params = listView.getLayoutParams();
			listView.setDividerHeight(mst.adjustYIgnoreDensity(20));
			if (btnContent.getAsstBtns().size() == 1) {
				params.height = mst.adjustYIgnoreDensity((btnContent.getAsstBtns().size()) * 20) + px * (btnContent.getAsstBtns().size());
			} else {
				params.height = mst.adjustYIgnoreDensity((btnContent.getAsstBtns().size() - 1) * 20) + px * (btnContent.getAsstBtns().size());
			}
			listView.setLayoutParams(params);
			listView.setVisibility(View.VISIBLE);
		}
		if (!Integer.valueOf(Double.valueOf(game_assistant.get(0).get(AssistantBean.AS_DISPLAY).toString()).intValue()).toString().equals("2")) {
			//大美女图标
			String path = HttpURL.URL_PIC_ALL + game_assistant.get(0).get(AssistantBean.AS_ICON).toString();
			ImageUtil.setImg(path, MMicon, new ImageCallback() {

				public void imageLoaded(Bitmap bitmap, ImageView view) {
					view.setScaleType(ScaleType.FIT_XY);
					view.setImageBitmap(bitmap);
					Message msg = new Message();
					msg.what = 2;
					ASSHANDLER.sendMessage(msg);
				}
			});
		}
		//		//小美女图标
		//		String path3 = HttpURL.URL_PIC_ALL + game_assistant.get(0).get(AssistantBean.AS_SMALL_ICON).toString();
		//		ImageUtil.setImg(path3, xiaomeiImg, new ImageCallback() {
		//			public void imageLoaded(Bitmap bitmap, ImageView view) {
		//				view.setScaleType(ScaleType.FIT_XY);
		//				view.setImageBitmap(bitmap);
		//			
		//			}
		//		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.as_close:
				closeBtn.setClickable(false);
				AnimUtils.startAnimationsOutLeft(left, 400);
				AnimUtils.startAnimationsOutBttom(begin, 400, 0, 850, true);
				zhezhao.setVisibility(View.GONE);
				AnimUtils.startAnimationsInLeft(xiaomei, 400);
				Database.ASSCLOSE = true;
				//点击关闭存入消息中心
				if (Database.ADD_DATA_CENTRE && Assistant.GAME_ASSISTANT != null && Database.currentActivity.getClass().equals(DoudizhuRoomListActivity.class)) {
					String json = JsonHelper.toJson(Assistant.GAME_ASSISTANT);
					ContentValues values = new ContentValues();
					values.put(DataCentreBean.DATA_ID, Integer.parseInt(Assistant.GAME_ASSISTANT.get(0).get(AssistantBean.AS_ID).toString()));
					values.put(DataCentreBean.DATA_CONTENT, json);
					values.put(DataCentreBean.DATA_RACE, DataCentreBean.RACE_AS);
					values.put(DataCentreBean.DATA_CLICK, "0");
					String[] id = { Assistant.GAME_ASSISTANT.get(0).get(AssistantBean.AS_ID).toString().toString() };
					DataCentreBean.getInstance().save(LoginActivity.dbHelper, values, id);
				}
			default:
				break;
		}
	}

	public class VerticalAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<AssistantBtn> datalist;

		public VerticalAdapter(Context context, List<AssistantBtn> datalist) {
			this.mInflater = LayoutInflater.from(context);
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

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.assistant_view_item_h, null);
				holder.guideItem = (Button) convertView.findViewById(R.id.btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.guideItem.setText(datalist.get(position).getBtnText());
			holder.guideItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					progressDialog.show();
					zhezhao2.setVisibility(View.VISIBLE);
					zhezhao2.setOnClickListener(null);
					dealAction(position);
				}
			});
			return convertView;
		}
	}

	public class ViewHolder {

		public Button guideItem;
	}

	public class HorizontalAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<AssistantBtn> datalist;

		public HorizontalAdapter(Context context, List<AssistantBtn> datalist) {
			this.mInflater = LayoutInflater.from(context);
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

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.assistant_view_item_s, null);
				holder.guideItem = (Button) convertView.findViewById(R.id.btnv2);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			//				String btntex=datalist.get(position).getBtnText();
			final String[] btnTex = datalist.get(position).getBtnText().split(",");
			if (btnTex[0] == null) {
				btnTex[0] = "确定";
			}
			holder.guideItem.setText(btnTex[0]);
			holder.guideItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (Integer.valueOf(Double.valueOf(game_assistant.get(0).get(AssistantBean.AS_DISPLAY).toString()).intValue()).toString().equals("3")) {
						if (listNum < listMessage.size() - 1) {
							listNum++;
							btxNum++;
							if (btxNum >= btnTex.length) {
								btxNum = btnTex.length - 1;
							}
							titleText.setText(listMessage.get(listNum));
							if (btnTex[btxNum] == null) {
								btnTex[btxNum] = "确定";
							}
							holder.guideItem.setText(btnTex[btxNum]);
						} else {
							holder.guideItem.setClickable(false);
							assistantDismiss();
						}
					} else {
						progressDialog.show();
						zhezhao2.setVisibility(View.VISIBLE);
						zhezhao2.setOnClickListener(null);
						dealAction(position);
					}
				}
			});
			return convertView;
		}
	}

	//st:0是成功1是失败 11是已经存在不可再次购买5是没有足够的金豆 
	// mn:后台配置充值金额  ms:各种提示   
	public void dealAction(final int position) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("asstId", Integer.valueOf(Double.valueOf(game_assistant.get(0).get(AssistantBean.AS_ID).toString()).intValue()).toString());
					paramMap.put("code", btnContent.getAsstBtns().get(position).getCode());
					paramMap.put("payType","-1");		//不存在的type 让后台根据需要充值的金豆直接生成金额
					paramMap.put("joinCode", (String) game_assistant.get(0).get(AssistantBean.AS_JOINCODE));
					final String resultJson = HttpRequest.clickBtn(paramMap);
					Database.currentActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							progressDialog.dismiss();
							if (!TextUtils.isEmpty(resultJson) && !resultJson.equals("1")) {
								final Map<String, String> result = JsonHelper.fromJson(resultJson, new TypeToken<Map<String, String>>() {});
								if (result != null) {
									tisp(result.get("ms"));
									if (result.get("st").equals("1")) {
										assistantDismiss();
										return;
									}
									if (result.containsKey("iq") && !TextUtils.isEmpty(result.get("iq"))) {
										Log.d("Bean", "" + result.get("iq"));
										final GameIQUpgrade mGameIQUpgrade = JsonHelper.fromJson(result.get("iq"), GameIQUpgrade.class);
										if (null != mGameIQUpgrade && mGameIQUpgrade.isUpgrade()) {
											AutoTask goOutTask = new AutoTask() {

												public void run() {
													Message msg = new Message();
													Bundle b = new Bundle();
													b.putString("getCelebratedText", mGameIQUpgrade.getCelebratedText());
													msg.what = DoudizhuRoomListActivity.HANDLER_WHAT_ROOM_LIST_SHOW_IQ_GRADE_MIN;
													msg.setData(b);
													centreHandler.sendMessage(msg);
												}
											};
											ScheduledTask.addDelayTask(goOutTask, 500);
											int isTitle = mGameIQUpgrade.getIsTitle() == null ? 0 : mGameIQUpgrade.getIsTitle();
											if (1 == isTitle) {
												AutoTask goOutTask1 = new AutoTask() {

													public void run() {
														centreHandler.sendEmptyMessage(DoudizhuRoomListActivity.HANDLER_WHAT_ROOM_LIST_SHOW_IQ_GRADE_MAX);
													}
												};
												ScheduledTask.addDelayTask(goOutTask1, 1);
											}
										}
										if (null != mGameIQUpgrade && 0 != mGameIQUpgrade.getIntellect() && 0 != mGameIQUpgrade.getNextIntellect()) {
											centreHandler.sendEmptyMessage(DoudizhuRoomListActivity.HANDLER_WHAT_ROOM_LIST_SET_IQ_GRADE_PG);
										}
									}
									if (result.get("ac").equals(AssistantAction.AC_ONCLICK)) {
										assistantDismiss();
									} else if (result.get("ac").equals(AssistantAction.AC_GIVE)) {
										assistantDismiss();
									} else if (result.get("ac").equals(AssistantAction.AC_PAY)) {
										if (result.get("st").equals("1")) {
											assistantDismiss();
										} else if (result.get("st").equals("0")) {
											// 充值两万金豆送美女图鉴
											zhezhao2.setVisibility(View.GONE);
											SDKConfig.SIGN_PAY_CODE = result.get("pc");
											Database.currentActivity.runOnUiThread(new Runnable() {

												public void run() {
													try {
														ASSID = String.valueOf(Double.valueOf(String.valueOf(game_assistant.get(0).get(AssistantBean.AS_ID))).intValue());
														BTNCODE = btnContent.getAsstBtns().get(position).getCode();
														
//														SDKFactory.fastPay(Integer.parseInt(result.get("mn")), SDKConstant.ASST);
														JDSMSPayUtil.setContext(context);
														PayTipUtils.showTip(Integer.parseInt(result.get("mn")),PaySite.GAME_HELP_CLICK); //配置的提示方式
													} catch (Exception e) {
														// TODO: handle exception
													} finally {
														assistantDismiss();
													}
												}
											});
										} else if (result.get("st").equals("11")) {
											zhezhao2.setVisibility(View.GONE);
											assistantDismiss();
										}
									} else if (result.get("ac").equals(AssistantAction.AC_BUY)) {
										if (result.get("st").equals("11")) {
											// 已有物品
											zhezhao2.setVisibility(View.GONE);
										} else if (result.get("st").equals("5")) {
											//金豆不足 提示充值
											zhezhao2.setVisibility(View.GONE);
											SDKConfig.SIGN_PAY_CODE = result.get("pc");
//											chonzhi(result.get("mn"), SDKConstant.ASST);
											JDSMSPayUtil.setContext(context);
											PayTipUtils.showTip(Integer.parseInt(result.get("mn")),PaySite.GAME_HELP_CLICK); //配置的提示方式
										} else if (result.get("st").equals("0")) {
											// 购买物品成功
											assistantDismiss();
											// 购买物品成功且下载图片
											if (!result.get("pics").trim().equals("")) {
												List<String> list = JsonHelper.fromJson(result.get("pics"), new TypeToken<List<String>>() {});
												String text = null;
												if (ActivityUtils.isOpenWifi()) {
													text = "当前为wifi环境,请放心下载";
												} else {
													text = "当前为2G/3G环境,会耗一定的流量，是否下载？";
												}
												downloadPic(text, list);
											}
										}
									} else if (result.get("ac").equals(AssistantAction.AC_FORWARD)) {
										// 跳转网页
										if (result.get("st").equals("0")) {
											assistantDismiss();
											goWeb(result.get("url"));
										}
									} else if (result.get("ac").equals(AssistantAction.AC_DOWN)) {
										// 下载应用
										if (result.get("st").equals("0")) {
											assistantDismiss();
											download(result.get("url"));
										}
									} else if (result.get("ac").equals(AssistantAction.AC_SKIP)) {
										// 跳转游戏界面
										if (result.get("st").equals("0")) {
											assistantDismiss();
											goActivity(result.get("af"), Integer.valueOf(result.get("page")));
										}
									} else if (result.get("ac").equals(AssistantAction.AC_EXIT)) {} else if (result.get("ac").equals(AssistantAction.AC_SIGN)) {
										assistantDismiss();
										//不报名
									} else if (result.get("ac").equals("oc")) {
										assistantDismiss();
									}
								}
							} else {
								AnimUtils.startAnimationsOutLeft(left, 400);
								AnimUtils.startAnimationsOutBttom(begin, 400, 0, 850, true);
								zhezhao.setVisibility(View.GONE);
								DialogUtils.toastTip("游戏助理获取数据失败");
								Database.ASSCLOSE = true;
							}
						}
					});
				} catch (Exception e) {
					AnimUtils.startAnimationsOutLeft(left, 400);
					AnimUtils.startAnimationsOutBttom(begin, 400, 0, 850, true);
					zhezhao.setVisibility(View.GONE);
					DialogUtils.toastTip("游戏助理获取数据失败");
					progressDialog.dismiss();
					Database.ASSCLOSE = true;
				}
			}
		}).start();
	}

	/**
	 * 后台返回提示
	 * @param tis
	 */
	public void tisp(String tis) {
		if (tis != null) {
			if (!tis.trim().equals("")) {
				DialogUtils.toastTip(tis);
			}
		}
	}

	/**
	 * 跳转网页
	 * @param url
	 */
	public void goWeb(String url) {
		try {
			Uri uri = Uri.parse(url);
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			Database.currentActivity.startActivity(it);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 跳转游戏页面
	 * @param ClassName
	 */
	public void goActivity(String name, int code) {
		Intent it = new Intent();
		if (name.equals("pay")) {
			it.setClass(Database.currentActivity, SDKFactory.getPayView());
		} else {
			Bundle bundle = new Bundle();
			bundle.putInt("page", code);
			it.putExtras(bundle);
			try {
				Class class1 = Class.forName(name);
				it.setClass(Database.currentActivity, class1);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		Database.currentActivity.startActivity(it);
	}

	/**
	 * 下载应用
	 * @param url
	 */
	public void download(final String url) {
		final String apkName = url.substring(url.lastIndexOf("/") + 1);
		final String url2 = url.replace(apkName, "");
		if (!ActivityUtils.isOpenWifi()) {
			GameDialog gameDialog = new GameDialog(Database.currentActivity) {

				public void okClick() {
					Toast.makeText(Database.currentActivity, "应用正在下载中...", Toast.LENGTH_SHORT).show();
					UpdateUtils.downApkAssistant(Database.currentActivity, "精品", url2, apkName, ASSHANDLER);
				}
			};
			gameDialog.show();
			gameDialog.setText("你当前网络环境是在非wifi下，请问是否继续下载？");
		} else {
			Toast.makeText(Database.currentActivity, "应用正在下载中...", Toast.LENGTH_SHORT).show();
			UpdateUtils.downApkAssistant(Database.currentActivity, "精品", url2, apkName, ASSHANDLER);
		}
	}

//	/**
//	 * 充值框
//	 */
//	public void chonzhi(final String money, final String place) {
//		Database.currentActivity.runOnUiThread(new Runnable() {
//
//			public void run() {
//				FastPayDialog dialog = new FastPayDialog(Database.currentActivity, money, place);
//				dialog.show();
//			}
//		});
//	}

	/**
	 * 下载图框
	 */
	public void downloadPic(final String text, final List<String> list) {
		Database.currentActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				DownloadDialog dialog = new DownloadDialog(Database.currentActivity, text, list);
				dialog.show();
			}
		});
	}

	/**
	 * 行为处理完后助理消失方法
	 */
	public void assistantDismiss() {
		AnimUtils.startAnimationsOutLeft(left, 400);
		AnimUtils.startAnimationsOutBttom(begin, 400, 0, 850, true);
		zhezhao.setVisibility(View.GONE);
		Database.ADD_DATA_CENTRE = false;//不可加入消息中心
		GAME_ASSISTANT = null;
		Database.ASSCLOSE = true;
		//处理消息中心的删除按钮
		if (centreHandler != null) {
			Message msg = new Message();
			msg.what = 1;
			centreHandler.sendMessage(msg);
		}
	}
}
