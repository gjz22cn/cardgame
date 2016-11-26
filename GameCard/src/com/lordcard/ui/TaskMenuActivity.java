package com.lordcard.ui;

import com.zzyddz.shui.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lordcard.broadcast.PackageReceiver;
import com.lordcard.common.task.GenericTask;
import com.lordcard.common.task.base.TaskParams;
import com.lordcard.common.task.base.TaskResult;
import com.lordcard.common.upgrade.UpdateUtils;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.ConfigUtil;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.ImageUtil.ImageCallback;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.PatternUtils;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.DownSoft;
import com.lordcard.entity.GameTask;
import com.lordcard.network.cmdmgr.CmdUtils;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpCallback;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.ui.base.BaseActivity;
import com.lordcard.ui.view.dialog.GameDialog;


/**
 * activity.TaskMenuActivity
 * 
 * @author yinhb <br/>
 *         create at 2012 2012-11-5 上午11:29:46
 */
@SuppressLint("HandlerLeak")
public class TaskMenuActivity extends BaseActivity {

	public LinearLayout pageContainer; // 放置子页面的容器
	private List<RelativeLayout> taskLayoutList = new ArrayList<RelativeLayout>();
	private Button invitationCodeBtn; // 邀请码按钮
	private Button telphoneGetBtn; // 手机获取验证码
	private Button telphoneBtn; // 手机号验证获取
	private Button userinfoBtn; // 用户信息完善
	private Button frientsBtn; // 邀请好友
	private Button downBtn, leftBtn, rightBtn;
	private DownAdapter downAdapter;
	private Button bottInvitationMsgBtn, bottTelphonebtn, bottUserinfoBtn, bottInvitationFriendBtn;
	private EditText codeText = null; // 邀请码
	private EditText phoneText = null; // 手机号输入框
	private EditText authText = null; // 手机号验证码输入框
	private RelativeLayout dowLayout;
	private ViewPager viewpager;
	private List<View> layoutlist;
	private PageAdapter pageadapter;
	private LayoutInflater layoutinflater;
	private List<DownSoft> appList;
	SharedPreferences sharedata;
	private Intent intent = null;
	/** 标签，ems版 **/
	private String emsSwitch;
	private RelativeLayout layout2;
	private int page;
	private TextView codeTipMsg;//
	private TextView telphoneTextView;
	private TextView userinfoLable;
	private TextView frientsLabel;
	private TextView textTip;
	Map<String, String> TaskMenuMap=null;
	/**显示应用下载布局*/
	public static final int HANDLER_WHAT_TASK_MENU_SHOW_DOWNLOAD_VIEW=2100;
	/**设置应用下载列表数据*/
	public static final int HANDLER_WHAT_TASK_MENU_SET_DOWNLOAD_DATA=2101;
	/**更新应用下载列表数据*/
	public static final int HANDLER_WHAT_TASK_MENU_NOTIFY_DOWNLOAD_DATA=2102;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_tip_menu);
		Bundle bundle = this.getIntent().getExtras();
		
		String result= GameCache.getStr(CacheKey.KEY_TEXT_VIEW_MESSAGE_DATA);
		if(!TextUtils.isEmpty(result)){
			TaskMenuMap = JsonHelper.fromJson(result, new TypeToken<Map<String, String>>() {});
		}
		layout2 = (RelativeLayout) findViewById(R.id.task_tip_menu_mst);
		layout2.setBackgroundResource(R.drawable.join_bj);
		mst.adjustView(layout2);
		PackageReceiver.registerReceiver(this);
		emsSwitch = ConfigUtil.getCfg("ems_switch");
// 短信邀请码
		RelativeLayout invitationCodeLayout = (RelativeLayout) findViewById(R.id.invitation_code_layout);
		codeTipMsg = (TextView) findViewById(R.id.code_tip_msg);
		if (null != TaskMenuMap && TaskMenuMap.containsKey(Constant.SMS_INITE_MSG)) {
			codeTipMsg.setText(TextUtils.isEmpty(TaskMenuMap.get(Constant.SMS_INITE_MSG)) ? "" : TaskMenuMap.get(Constant.SMS_INITE_MSG));
		} else {
			codeTipMsg.setText("");
		}
		taskLayoutList.add(invitationCodeLayout);
		invitationCodeBtn = (Button) findViewById(R.id.invitation_code_btn);
		invitationCodeBtn.setOnClickListener(clickListener);
// 手机号完善
		RelativeLayout telphoneLayout = (RelativeLayout) findViewById(R.id.telphone_layout);
		telphoneTextView = (TextView) findViewById(R.id.telphone_lable_auth);
		if (null != TaskMenuMap && TaskMenuMap.containsKey(Constant.SMS_INITE_CODE)) {
			telphoneTextView.setText(TextUtils.isEmpty(TaskMenuMap.get(Constant.SMS_INITE_CODE)) ? "" : TaskMenuMap.get(Constant.SMS_INITE_CODE));
		} else {
			telphoneTextView.setText("");
		}
		if ("close".equals(emsSwitch)) {
			telphoneTextView.setText(R.string.telphone_label_cyp);
			if (null != TaskMenuMap && TaskMenuMap.containsKey(Constant.SMS_INITE_INPUT)) {
				telphoneTextView.setText(TextUtils.isEmpty(TaskMenuMap.get(Constant.SMS_INITE_INPUT)) ? "" : TaskMenuMap.get(Constant.SMS_INITE_INPUT));
			} else {
				telphoneTextView.setText("");
			}
		}
		taskLayoutList.add(telphoneLayout);
		telphoneGetBtn = (Button) findViewById(R.id.telphone_btn_get);
		telphoneGetBtn.setOnClickListener(clickListener);
		telphoneBtn = (Button) findViewById(R.id.telphone_btn);
		telphoneBtn.setOnClickListener(clickListener);
// 基本信息完善
		RelativeLayout userinfoLayout = (RelativeLayout) findViewById(R.id.userinfo_layout);
		userinfoLable = (TextView) findViewById(R.id.userinfo_lable);
		if (null != TaskMenuMap && TaskMenuMap.containsKey(Constant.FILL_IN_INFO)) {
			userinfoLable.setText(TextUtils.isEmpty(TaskMenuMap.get(Constant.FILL_IN_INFO)) ? "" : TaskMenuMap.get(Constant.FILL_IN_INFO));
		} else {
			userinfoLable.setText("");
		}
		taskLayoutList.add(userinfoLayout);
		userinfoBtn = (Button) findViewById(R.id.userinfo_btn);
		userinfoBtn.setOnClickListener(clickListener);
// 邀请好友
		RelativeLayout frientsLayout = (RelativeLayout) findViewById(R.id.frients_layout);
		frientsLabel = (TextView) findViewById(R.id.frients_label);
		if (null != TaskMenuMap && TaskMenuMap.containsKey(Constant.INVITE_FRIEND)) {
			frientsLabel.setText(TextUtils.isEmpty(TaskMenuMap.get(Constant.INVITE_FRIEND)) ? "" : TaskMenuMap.get(Constant.INVITE_FRIEND));
		} else {
			frientsLabel.setText("");
		}
		taskLayoutList.add(frientsLayout);
		TextView frientsTextView = (TextView) findViewById(R.id.frients_label);
		if ("close".equals(emsSwitch)) {
			frientsTextView.setText(R.string.frients_label_msg);
		}
		frientsBtn = (Button) findViewById(R.id.frients_btn);
		frientsBtn.setOnClickListener(clickListener);
		codeText = (EditText) findViewById(R.id.invitation_code_text); // 邀请码
		phoneText = (EditText) findViewById(R.id.telphone_text); // 手机号输入框
		authText = (EditText) findViewById(R.id.telphone_text_auth);
// 下载应用
		textTip = (TextView) findViewById(R.id.text_tip);
		if (null != TaskMenuMap && TaskMenuMap.containsKey(Constant.APP_DOWNLOAD)) {
			textTip.setText(TextUtils.isEmpty(TaskMenuMap.get(Constant.APP_DOWNLOAD)) ? "" : TaskMenuMap.get(Constant.APP_DOWNLOAD));
		} else {
			textTip.setText("");
		}
		dowLayout = (RelativeLayout) findViewById(R.id.download_layout);
		taskLayoutList.add(dowLayout);
		downBtn = (Button) findViewById(R.id.download_btn);
		downBtn.setOnClickListener(clickListener);
		leftBtn = (Button) findViewById(R.id.left_move_btn);
		rightBtn = (Button) findViewById(R.id.right_move_btn);
		leftBtn.setOnClickListener(clickListener);
		rightBtn.setOnClickListener(clickListener);
		layoutlist = new ArrayList<View>();
		viewpager = (ViewPager) findViewById(R.id.viewpagerLayout);
		layoutinflater = getLayoutInflater();
// switchPage(0);// 默认打开第0页
		bottInvitationMsgBtn = (Button) findViewById(R.id.invitation_msg_btn);
		bottTelphonebtn = (Button) findViewById(R.id.bott_telphone_btn);
		bottUserinfoBtn = (Button) findViewById(R.id.bott_userinfo_btn);
		bottInvitationFriendBtn = (Button) findViewById(R.id.invitation_friend_btn);
		bottInvitationMsgBtn.setOnClickListener(clickListener);
		bottTelphonebtn.setOnClickListener(clickListener);
		bottUserinfoBtn.setOnClickListener(clickListener);
		bottInvitationFriendBtn.setOnClickListener(clickListener);
		findViewById(R.id.set_back).setOnClickListener(clickListener);
		if ("close".equals(emsSwitch)) {
			twoPage();
		} else {
			bottInvitationMsgBtn.setBackgroundResource(R.drawable.gpl_top_left_select);
		}
		intent = getIntent();
		if (intent.getBooleanExtra("award", false)) {
			twoPage();
		}
		appsThread();
		if (bundle != null) {
			page = bundle.getInt("page");
			if (page == 1) {
				bottInvitationMsgBtn.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
				bottTelphonebtn.setTextColor(Color.WHITE);
				twoPage();
			} else if (page == 4) {
				//				 下载类型 2
				bottInvitationMsgBtn.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
				HttpRequest.openApiSwith("2", new HttpCallback() {

					public void onSucceed(Object... obj) {
						final String isOpen = (String) obj[0];
						
						Message msg=new Message();
						Bundle b=new Bundle();
						msg.what=HANDLER_WHAT_TASK_MENU_SHOW_DOWNLOAD_VIEW;
						b.putString("isOpen", isOpen);
						msg.setData(b);
						handler.sendMessage(msg);
//						runOnUiThread(new Runnable() {
//							public void run() {
//								downBtn.setTextColor(Color.WHITE);
//								bottInvitationMsgBtn.setBackgroundResource(R.drawable.gpl_top_left);
//								bottTelphonebtn.setBackgroundResource(R.drawable.gpl_top_center);
//								bottUserinfoBtn.setBackgroundResource(R.drawable.gpl_top_center);
//								bottInvitationFriendBtn.setBackgroundResource(R.drawable.gpl_top_center);
//								downBtn.setBackgroundResource(R.drawable.gpl_top_right_select);
//								getPageView(4);
//								
//								if ("1".equals(isOpen)) {
//									if (null != TaskMenuMap && TaskMenuMap.containsKey("app_download")) {
//										textTip.setText(TextUtils.isEmpty(TaskMenuMap.get("app_download")) ? "" : TaskMenuMap.get("app_download"));
//									} else {
//										textTip.setText("");
//									}
//									findViewById(R.id.ug_download).setVisibility(View.VISIBLE);
//									findViewById(R.id.viewpagerLayout).setVisibility(View.VISIBLE);
//									findViewById(R.id.ug_download2).setVisibility(View.VISIBLE);
//									//MobclickAgent.onEvent(TaskMenuActivity.this, "应用下载");
//								}else if ("0".equals(isOpen)){
//									if (null != TaskMenuMap && TaskMenuMap.containsKey("fun_no_open")) {
//										textTip.setText(TextUtils.isEmpty(TaskMenuMap.get("fun_no_open")) ? "" : TaskMenuMap.get("fun_no_open"));
//									} else {
//										textTip.setText("");
//									}
//									findViewById(R.id.ug_download).setVisibility(View.GONE);
//									findViewById(R.id.viewpagerLayout).setVisibility(View.GONE);
//									findViewById(R.id.ug_download2).setVisibility(View.GONE);
//								}
//							}
//						});
					}

					public void onFailed(Object... obj) {}
				});
			} else {}
		}
	}

	/**
	 * 应用列表数据的线程
	 */
	private void appsThread() {
		new Thread(new Runnable() {

			public void run() {
				String result = HttpRequest.getAppsInfo();
				if (!TextUtils.isEmpty(result)) {
					appList = JsonHelper.fromJson(result, new TypeToken<List<DownSoft>>() {});
					Message message = new Message();
					message.what = HANDLER_WHAT_TASK_MENU_SET_DOWNLOAD_DATA;
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case HANDLER_WHAT_TASK_MENU_SET_DOWNLOAD_DATA:
					if(appList != null)
					{
						setDownViewData(appList);
						pageadapter = new PageAdapter();
						viewpager.setAdapter(pageadapter);
					}
					break;
				case HANDLER_WHAT_TASK_MENU_NOTIFY_DOWNLOAD_DATA:
					downAdapter.notifyDataSetChanged();
					String path = msg.getData().getString("APKpath");
					Intent finishIntent = ActivityUtils.getInstallIntent(new File(path));
					TaskMenuActivity.this.startActivity(finishIntent);
					break;
				case HANDLER_WHAT_TASK_MENU_SHOW_DOWNLOAD_VIEW:
					String isOpen=msg.getData().getString("isOpen");
					showDownLoadView(isOpen);
					break;
			}
		}

		/**
		 * 显示应用下载布局
		 * @param isOpen
		 */
		private void showDownLoadView(String isOpen) {
			downBtn.setTextColor(Color.WHITE);
			bottInvitationMsgBtn.setBackgroundResource(R.drawable.gpl_top_left);
			bottTelphonebtn.setBackgroundResource(R.drawable.gpl_top_center);
			bottUserinfoBtn.setBackgroundResource(R.drawable.gpl_top_center);
			bottInvitationFriendBtn.setBackgroundResource(R.drawable.gpl_top_right);
			downBtn.setBackgroundResource(R.drawable.gpl_top_right_select);
			getPageView(4);
			//
			if ("1".equals(isOpen)) {
				if (null != TaskMenuMap && TaskMenuMap.containsKey(Constant.APP_DOWNLOAD)) {
					textTip.setText(TextUtils.isEmpty(TaskMenuMap.get(Constant.APP_DOWNLOAD)) ? "" : TaskMenuMap.get(Constant.APP_DOWNLOAD));
				} else {
					textTip.setText("");
				}
				findViewById(R.id.ug_download).setVisibility(View.VISIBLE);
				findViewById(R.id.viewpagerLayout).setVisibility(View.VISIBLE);
				findViewById(R.id.ug_download2).setVisibility(View.VISIBLE);
				//MobclickAgent.onEvent(TaskMenuActivity.this, "应用下载");
			}else if ("0".equals(isOpen)){
				if (null != TaskMenuMap && TaskMenuMap.containsKey(Constant.FUN_NO_OPEN)) {
					textTip.setText(TextUtils.isEmpty(TaskMenuMap.get(Constant.FUN_NO_OPEN)) ? "" : TaskMenuMap.get(Constant.FUN_NO_OPEN));
				} else {
					textTip.setText("");
				}
				findViewById(R.id.ug_download).setVisibility(View.GONE);
				findViewById(R.id.viewpagerLayout).setVisibility(View.GONE);
				findViewById(R.id.ug_download2).setVisibility(View.GONE);
			}
		}
	};

	/**
	 * 设置应用下载列表数据
	 * @param apps
	 */
	public void setDownViewData(List<DownSoft> apps) {
		Database.packageNames.clear();
		if (apps.size() == 0) {
			return;
		}
		int index = apps.size() / 5;
		if (apps.size() % 5 != 0) {
			index++;
		}
		sharedata = this.getSharedPreferences("apps", 0);
		for (int i = 0; i < index; i++) {
			View view = layoutinflater.inflate(R.layout.grid_view, null);
			final GridView gridView = (GridView) view.findViewById(R.id.gv_apps);
			final List<DownSoft> softs = new ArrayList<DownSoft>();
			for (int j = i * 5; j < (i + 1) * 5 && j < apps.size(); j++) {
				softs.add(apps.get(j));
				Database.packageNames.add(apps.get(j).getPackageName());
			}
			downAdapter = new DownAdapter(this, softs);
			gridView.setAdapter(downAdapter);
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
					if (arg1.findViewById(R.id.apk_down).getVisibility() == View.VISIBLE) {
						arg1.findViewById(R.id.apk_down).setVisibility(View.GONE);
					} else {
						arg1.findViewById(R.id.apk_down).setVisibility(View.VISIBLE);
						PackageInfo packageInfo = null;
						try {
							packageInfo = TaskMenuActivity.this.getPackageManager().getPackageInfo(softs.get(arg2).getPackageName(), 0);
						} catch (NameNotFoundException e) {}
						if (null == packageInfo) {
							((Button) arg1.findViewById(R.id.apk_down)).setText("下载");
						} else {
							((Button) arg1.findViewById(R.id.apk_down)).setText("打开");
						}
					}
					for (int j = 0; j < gridView.getChildCount(); j++) {
						if (j != arg2) {
							gridView.getChildAt(j).findViewById(R.id.apk_down).setVisibility(View.GONE);
						}
					}
				}
			});
			layoutlist.add(view);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//mst.unRegisterView(layout2);
		Database.packageNames.clear();
		PackageReceiver.unregisterReceiver(this);
		//		if(mMainMenuBar != null){
		//			mMainMenuBar.onDestory();
		//			mMainMenuBar = null;
		//		}
	}

	/**
	 * 用于获取intent和pageView， 类似于单例模式，使得对象不用重复创建，同时，保留上一个对象的状态
	 * 当重新访问时，仍保留原来数据状态，如文本框里面的值。
	 * 
	 * @param pageID
	 *            选中的tab序号（0~2）
	 * @return
	 */
	private void getPageView(int pageID) {
		for (int i = 0; i < taskLayoutList.size(); i++) {
			if (pageID == i) {
				taskLayoutList.get(i).setVisibility(View.VISIBLE);
			} else {
				taskLayoutList.get(i).setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 点击事件
	 */
	OnClickListener clickListener = new OnClickListener() {

		public void onClick(View v) {
			if (v.getId() == R.id.invitation_msg_btn || v.getId() == R.id.bott_telphone_btn || v.getId() == R.id.bott_userinfo_btn || v.getId() == R.id.invitation_friend_btn || v.getId() == R.id.download_btn) {
				bottInvitationMsgBtn.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
				bottTelphonebtn.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
				bottUserinfoBtn.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
				bottInvitationFriendBtn.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
				downBtn.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
			}
			switch (v.getId()) {
				case R.id.set_back:
					//MobclickAgent.onEvent(TaskMenuActivity.this, "赠送返回");
					finishSelf();
					break;
				case R.id.invitation_code_btn:// 邀请码按钮
					//MobclickAgent.onEvent(TaskMenuActivity.this, "短信邀请码获取金豆");
					String code = codeText.getText().toString();
					if (!TextUtils.isEmpty(code)) {
						// 提交邀请码
						GenericTask submitInviteCodeTask = new SubmitInviteCode();
						submitInviteCodeTask.setFeedback(feedback);
						TaskParams params = new TaskParams();
						params.put("code", code);
						submitInviteCodeTask.execute(params);
						taskManager.addTask(submitInviteCodeTask);
					} else {
						DialogUtils.mesTip("邀请码不能为空!", false);
					}
					break;
				case R.id.telphone_btn_get:// 手机获取验证码
					String mobile = phoneText.getText().toString();
					boolean isMobile = PatternUtils.validMobiles(mobile);
					if (!isMobile) {
						DialogUtils.mesTip("请输入正确的手机号码!", false);
					} else {
						if (ActivityUtils.simExist()) { // 判断SIM卡是否能使用
							//MobclickAgent.onEvent(TaskMenuActivity.this, "手机号获取验证码");
							String timeStr = String.valueOf(System.currentTimeMillis());
							String validatorCode = timeStr.substring(timeStr.length() - 6);
							Database.PHONE_VALIDATOR_CODE = validatorCode;
							Database.PHONE_VALIDATOR_VALUE = mobile;
							DialogUtils.toastTip("短信发送中...");
							if ("close".equals(emsSwitch)) {
								sendSMS("掌中游斗地主金豆大赠送，完善手机号获取金豆验证码:" + validatorCode, mobile);
							} else {
								SmsManager smsManager = SmsManager.getDefault();
								if (smsManager == null) {
									return;
								}
								Intent intent = new Intent(Constant.ACTION_SMS_SEND);
								PendingIntent sentIntent = PendingIntent.getBroadcast(TaskMenuActivity.this, 0, intent, 0);
								smsManager.sendTextMessage(mobile, null, "掌中游斗地主金豆大赠送，完善手机号获取金豆验证码:" + validatorCode, sentIntent, null);
							}
						}
					}
					break;
				case R.id.telphone_btn: // 手机号验证
					//MobclickAgent.onEvent(TaskMenuActivity.this, "手机号验证码获取金豆");
					String auth = authText.getText().toString();
					if (!TextUtils.isEmpty(auth) && auth.equals(Database.PHONE_VALIDATOR_CODE)) {
						// 提交手机号码
						GenericTask submitPhoneTask = new SubmitPhoneTask();
						submitPhoneTask.setFeedback(feedback);
						TaskParams params = new TaskParams();
						params.put("mobile", Database.PHONE_VALIDATOR_VALUE);
						params.put("child", 2);
						submitPhoneTask.execute(params);
						taskManager.addTask(submitPhoneTask);
					} else {
						DialogUtils.mesTip("无效验证码!", false);
					}
					break;
				case R.id.userinfo_btn:// 用户信息完善
					//MobclickAgent.onEvent(TaskMenuActivity.this, "完善个人资料获取金豆");
					Intent userIt = new Intent();
					userIt.setClass(TaskMenuActivity.this, SettingActivity.class);
					startActivity(userIt);
					break;
				case R.id.invitation_msg_btn:
					bottInvitationMsgBtn.setTextColor(Color.WHITE);
					if ("close".equals(emsSwitch)) {
						DialogUtils.toastTip("此功能暂未开通");
					} else {
						//MobclickAgent.onEvent(TaskMenuActivity.this, "邀请好友送金豆");
						bottInvitationMsgBtn.setBackgroundResource(R.drawable.gpl_top_left_select);
						bottTelphonebtn.setBackgroundResource(R.drawable.gpl_top_center);
						bottUserinfoBtn.setBackgroundResource(R.drawable.gpl_top_center);
						bottInvitationFriendBtn.setBackgroundResource(R.drawable.gpl_top_right);
						downBtn.setBackgroundResource(R.drawable.gpl_top_right);
						getPageView(0);
					}
					break;
				case R.id.bott_telphone_btn:
					bottTelphonebtn.setTextColor(Color.WHITE);
					//MobclickAgent.onEvent(TaskMenuActivity.this, "本机号码");
					twoPage();
					break;
				case R.id.bott_userinfo_btn:
					//MobclickAgent.onEvent(TaskMenuActivity.this, "完善信息");
					bottUserinfoBtn.setTextColor(Color.WHITE);
					bottInvitationMsgBtn.setBackgroundResource(R.drawable.gpl_top_left);
					bottTelphonebtn.setBackgroundResource(R.drawable.gpl_top_center);
					bottUserinfoBtn.setBackgroundResource(R.drawable.gpl_top_center_select);
					bottInvitationFriendBtn.setBackgroundResource(R.drawable.gpl_top_right);
					downBtn.setBackgroundResource(R.drawable.gpl_top_right);
					getPageView(2);
					break;
				case R.id.invitation_friend_btn:
					//MobclickAgent.onEvent(TaskMenuActivity.this, "邀请好友");
					bottInvitationFriendBtn.setTextColor(Color.WHITE);
					bottInvitationMsgBtn.setBackgroundResource(R.drawable.gpl_top_left);
					bottTelphonebtn.setBackgroundResource(R.drawable.gpl_top_center);
					bottUserinfoBtn.setBackgroundResource(R.drawable.gpl_top_center);
					bottInvitationFriendBtn.setBackgroundResource(R.drawable.gpl_top_right);
					downBtn.setBackgroundResource(R.drawable.gpl_top_right);
					// downBtn.setBackgroundResource(R.drawable.caidang05);
					getPageView(3);
					break;
				case R.id.download_btn:
					//				 下载类型 2
					HttpRequest.openApiSwith("2", new HttpCallback() {

						public void onSucceed(Object... obj) {
							String isOpen = (String) obj[0];
							Message msg=new Message();
							Bundle b=new Bundle();
							msg.what=HANDLER_WHAT_TASK_MENU_SHOW_DOWNLOAD_VIEW;
							b.putString("isOpen", isOpen);
							msg.setData(b);
							handler.sendMessage(msg);
//							runOnUiThread(new Runnable() {
//								public void run() {
//									downBtn.setTextColor(Color.WHITE);
//									bottInvitationMsgBtn.setBackgroundResource(R.drawable.gpl_top_left);
//									bottTelphonebtn.setBackgroundResource(R.drawable.gpl_top_center);
//									bottUserinfoBtn.setBackgroundResource(R.drawable.gpl_top_center);
//									bottInvitationFriendBtn.setBackgroundResource(R.drawable.gpl_top_center);
//									downBtn.setBackgroundResource(R.drawable.gpl_top_right_select);
//									getPageView(4);
//									//
//									if ("1".equals(isOpen)) {
//										if (null != TaskMenuMap && TaskMenuMap.containsKey("app_download")) {
//											textTip.setText(TextUtils.isEmpty(TaskMenuMap.get("app_download")) ? "" : TaskMenuMap.get("app_download"));
//										} else {
//											textTip.setText("");
//										}
//										findViewById(R.id.ug_download).setVisibility(View.VISIBLE);
//										findViewById(R.id.viewpagerLayout).setVisibility(View.VISIBLE);
//										findViewById(R.id.ug_download2).setVisibility(View.VISIBLE);
//										//MobclickAgent.onEvent(TaskMenuActivity.this, "应用下载");
//									}else if ("0".equals(isOpen)){
//										if (null != TaskMenuMap && TaskMenuMap.containsKey("fun_no_open")) {
//											textTip.setText(TextUtils.isEmpty(TaskMenuMap.get("fun_no_open")) ? "" : TaskMenuMap.get("fun_no_open"));
//										} else {
//											textTip.setText("");
//										}
//										findViewById(R.id.ug_download).setVisibility(View.GONE);
//										findViewById(R.id.viewpagerLayout).setVisibility(View.GONE);
//										findViewById(R.id.ug_download2).setVisibility(View.GONE);
//									}
//									
//								}
//							});
						}

						public void onFailed(Object... obj) {}
					});
					break;
				case R.id.left_move_btn:
					if (viewpager != null) {
						if (viewpager.getCurrentItem() - 1 >= 0) {
							viewpager.setCurrentItem(viewpager.getCurrentItem() - 1);
						}
					}
					break;
				case R.id.right_move_btn:
					if (viewpager != null) {
						if (viewpager.getCurrentItem() <= viewpager.getChildCount()) {
							viewpager.setCurrentItem(viewpager.getCurrentItem() + 1);
						}
					}
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 发送短信
	 * 
	 * @param smsBody
	 * @param smsContact
	 */
	private void sendSMS(String smsBody, String smsContact) {
		Uri smsToUri = Uri.parse("smsto:" + smsContact);
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		intent.putExtra("sms_body", smsBody);
		startActivity(intent);
	}

	public void twoPage() {
		getPageView(1);
		bottInvitationMsgBtn.setBackgroundResource(R.drawable.gpl_top_left);
		bottTelphonebtn.setBackgroundResource(R.drawable.gpl_top_center_select);
		bottUserinfoBtn.setBackgroundResource(R.drawable.gpl_top_center);
		bottInvitationFriendBtn.setBackgroundResource(R.drawable.gpl_top_right);
		downBtn.setBackgroundResource(R.drawable.gpl_top_right);
	}

	public class PageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return layoutlist.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(layoutlist.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(layoutlist.get(arg1), 0);
			return layoutlist.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {}
	}

	private class DownAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<DownSoft> softs;
		private Context context;

		public DownAdapter(Context context, List<DownSoft> softs) {
			this.mInflater = LayoutInflater.from(context);
			this.softs = softs;
			this.context = context;
		}

		@Override
		public int getCount() {
			return softs.size();
		}

		@Override
		public Object getItem(int position) {
			return softs.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.down_gridview_item, null);
				holder.apkPic = (ImageView) convertView.findViewById(R.id.apk_pic);
				holder.apkName = (TextView) convertView.findViewById(R.id.apk_name);
				holder.downBtn = (Button) convertView.findViewById(R.id.apk_down);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final DownSoft soft = softs.get(position);
			String url = soft.getDownUrl() + soft.getIconName();
			ImageUtil.setImg(url, holder.apkPic, new ImageCallback() {

				public void imageLoaded(Bitmap bitmap, ImageView view) {
					view.setScaleType(ScaleType.FIT_XY);
					view.setImageBitmap(bitmap);
				}
			});
			holder.apkName.setText(soft.getName());
			holder.downBtn.setVisibility(View.INVISIBLE);
			holder.downBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int count = sharedata.getInt(soft.getPackageName(), 0);
					count++;
					SharedPreferences.Editor editor = sharedata.edit();
					editor.putInt(soft.getPackageName(), count);
					editor.commit();
					if (((Button) v).getText().toString().equals("下载中...")) {
						return;
					}
					if (((Button) v).getText().toString().equals("打开")) {
						Intent startIntent = context.getPackageManager().getLaunchIntentForPackage(soft.getPackageName());
						context.startActivity(startIntent);
					} else {
						if (!ActivityUtils.isOpenWifi()) {
							GameDialog gameDialog = new GameDialog(Database.currentActivity) {

								public void okClick() {
									Toast.makeText(TaskMenuActivity.this, "应用正在下载中...", Toast.LENGTH_SHORT).show();
									UpdateUtils.downApk(TaskMenuActivity.this, soft.getName(), soft.getDownUrl(), soft.getApkName(), handler);
								}
							};
							gameDialog.show();
							gameDialog.setText("你当前网络环境是在非wifi下，请问是否继续下载？");
						} else {
							Toast.makeText(TaskMenuActivity.this, soft.getName() + "应用正在下载中...", Toast.LENGTH_SHORT).show();
							((Button) v).setText("下载中...");
							UpdateUtils.downApk(TaskMenuActivity.this, soft.getName(), soft.getDownUrl(), soft.getApkName(), handler);
						}
					}
				}
			});
			//mst.adjustView(parent);
			return convertView;
		}

		public class ViewHolder {

			public ImageView apkPic;
			public TextView apkName;
			public Button downBtn;
		}
	}

	/**
	 * 提交邀请码获取金豆
	 */
	private class SubmitInviteCode extends GenericTask {

		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				TaskParams param = null;
				if (params.length <= 0) {
					return TaskResult.FAILED;
				}
				param = params[0];
				String result = HttpRequest.submitInviteCode(param.getString("code"));
				if (TextUtils.isEmpty(result) || HttpRequest.FAIL_STATE.equals(result)) { // 失败
					DialogUtils.mesTip("提交邀请码失败，请稍候在试!", false);
				} else if (HttpRequest.TOKEN_ILLEGAL.equals(result)) { // 用户登录Token过期
					DialogUtils.reLogin(Database.currentActivity);
				} else {
					final GameTask resultTask = JsonHelper.fromJson(result, GameTask.class);
					if (null != resultTask) {
						runOnUiThread(new Runnable() {

							public void run() {
								if (CmdUtils.SUCCESS_CODE.equals(resultTask.getValue())) {// 成功
									DialogUtils.mesTip("恭喜您，获得系统赠送的" + resultTask.getCount() + "金豆!", false);
									codeText.setText("");
									// 登记到友盟
									//MobclickAgent.onEvent(TaskMenuActivity.this, "短信邀请码获取金豆:成功");
								} else if (CmdUtils.FAIL_CODE.equals(resultTask.getValue())) { // 失败
									DialogUtils.mesTip("金豆获取失败，请稍候再试!", false);
									// 登记到友盟
									//MobclickAgent.onEvent(TaskMenuActivity.this, "短信邀请码获取金豆:失败");
								} else if ("2".equals(resultTask.getValue())) { // 失效
									DialogUtils.mesTip("无效的邀请码!", false);
									// 登记到友盟
									//MobclickAgent.onEvent(TaskMenuActivity.this, "短信邀请码获取金豆:失效");
								}
							}
						});
					}
				}
			} catch (Exception e) {}
			return TaskResult.OK;
		}
	}

	/**
	 * 提交手机号
	 */
	private class SubmitPhoneTask extends GenericTask {

		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				TaskParams param = null;
				if (params.length <= 0) {
					return TaskResult.FAILED;
				}
				param = params[0];
				String result = HttpRequest.submitPhone(param.getInt("child"), param.getString("mobile"));
				if (HttpRequest.FAIL_STATE.equals(result)) { // 失败
					DialogUtils.mesTip("提交手机号失败，请稍候在试!", false);
				} else if (HttpRequest.TOKEN_ILLEGAL.equals(result)) { // 用户登录Token过期
					DialogUtils.reLogin(Database.currentActivity);
				} else {
					GameTask resultTask = JsonHelper.fromJson(result, GameTask.class);
					if (resultTask.getChild() == 2 && CmdUtils.FAIL_CODE.equals(resultTask.getValue())) { // 验证失败
						DialogUtils.mesTip("验证失败，请稍候再试!", false);
					} else if (resultTask.getChild() == 2 && CmdUtils.SUCCESS_CODE.equals(resultTask.getValue())) { // 验证成功
						DialogUtils.mesTip("恭喜您，获得系统赠送的" + resultTask.getCount() + "金豆!", false);
					} else if (resultTask.getChild() == 2 && "2".equals(resultTask.getValue())) { // 验证码无效
						DialogUtils.mesTip("该手机号已经绑定，无需再次提交!", false);
					}
				}
			} catch (Exception e) {}
			return TaskResult.OK;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 重写返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
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
}
