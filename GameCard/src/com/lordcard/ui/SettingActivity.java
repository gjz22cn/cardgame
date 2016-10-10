package com.lordcard.ui;

import com.zzyddz.shui.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lordcard.common.task.GenericTask;
import com.lordcard.common.task.base.TaskParams;
import com.lordcard.common.task.base.TaskResult;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.AudioPlayUtils;
import com.lordcard.common.util.ChannelUtils;
import com.lordcard.common.util.ConfigUtil;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.PatternUtils;
import com.lordcard.common.util.PreferenceHelper;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameTask;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.GameUserAsk;
import com.lordcard.entity.JsonResult;
import com.lordcard.entity.PageQueryResult;
import com.lordcard.network.cmdmgr.CmdUtils;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.ui.base.BaseActivity;
import com.lordcard.ui.view.dialog.AccountBindDialog;
import com.sdk.util.SDKFactory;
import com.umeng.analytics.MobclickAgent;

public class SettingActivity extends BaseActivity implements OnClickListener {

	private Button shoushi = null;//手势
	private Button zhendong, jingyin = null;//震动,静音
	private Button bgmusic = null;
	//	private Button updatebtn = null;
	private Button finish, setLoginout;
	private SeekBar musicControl = null;
	private EditText nickName = null;
	private RadioGroup mRadioGroup = null;
	private String gender = "0";
	private RadioButton man = null;
	private RadioButton woman = null;
	private RadioButton baomi = null;
	private Button chongzhi = null, xiugaimima, tijiao;
	private EditText emailEdit = null, feedBackEdit = null;
	private TextView zhidou, zhishang, zhishangPlTv = null;//金豆，等级,经验进度比
	private ProgressBar zhishangPb;//等级进度
	private TextView gameVersion;
	private ImageView headIv;//头像
	private List<LinearLayout> taskLayoutList = new ArrayList<LinearLayout>();
	private Button gameSetBtn, gameAccountBtn, gameAboutBtn, gameFeedBackBtn;
	private TextView accountView, create_time;
	private ListView feedBackList;
	private List<GameUserAsk> userAskList;
	private FeedBackAdapter feedBackAdapter;
	private AudioManager audiomanage;// 声音管理器
	private RelativeLayout layout;
	private LinearLayout gameSetLayout, gameAccoutLayout, gameAboutLayout, gameFeedBackLayout;

//	private MainMenuBar mMainMenuBar;//菜单栏
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Database.currentActivity = this;
		setContentView(R.layout.setting);
		Bundle bundle = this.getIntent().getExtras();
		layout = (RelativeLayout) findViewById(R.id.set_layout);
		layout.setBackgroundResource(R.drawable.join_bj);
		mst.adjustView(layout);
		// 头部三个按钮
		gameAccountBtn = (Button) findViewById(R.id.set_account);
		gameSetBtn = (Button) findViewById(R.id.set_set);
		gameFeedBackBtn = (Button) findViewById(R.id.set_feedback);
		gameAboutBtn = (Button) findViewById(R.id.set_about);
		gameSetLayout = (LinearLayout) findViewById(R.id.game_set_layout);
		gameAccoutLayout = (LinearLayout) findViewById(R.id.game_account_layout);
		gameAboutLayout = (LinearLayout) findViewById(R.id.game_about_layout);
		gameFeedBackLayout = (LinearLayout) findViewById(R.id.game_feedback_layout);
		findViewById(R.id.set_back).setOnClickListener(this);
		taskLayoutList.add(gameAccoutLayout);
		taskLayoutList.add(gameSetLayout);
		taskLayoutList.add(gameFeedBackLayout);
		taskLayoutList.add(gameAboutLayout);
		// 我的帐号界面
		accountView = (TextView) findViewById(R.id.use_id);
		xiugaimima = (Button) findViewById(R.id.xiugaimima);
		/*if(CGChargeActivity.isYd(this))
		{
			xiugaimima.setEnabled(false);
			xiugaimima.setVisibility(View.INVISIBLE);
			xiugaimima.setText("");
		}*/
		xiugaimima.setOnClickListener(this);
		nickName = (EditText) findViewById(R.id.nicheng);
		mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
		man = (RadioButton) findViewById(R.id.man);
		woman = (RadioButton) findViewById(R.id.woman);
		baomi = (RadioButton) findViewById(R.id.mimi);
		create_time = (TextView) findViewById(R.id.creatTime);
		zhidou = (TextView) findViewById(R.id.setting_zhidou);
		zhishang = (TextView) findViewById(R.id.setting_zhishang);
		zhishangPb = (ProgressBar) findViewById(R.id.setting_iq_pg);
		zhishangPlTv = (TextView) findViewById(R.id.setting_iq_pg_tv);
		chongzhi = (Button) findViewById(R.id.chong_setting);
		finish = (Button) findViewById(R.id.setting_finish_button);
		// setLoginout = (Button) findViewById(R.id.setting_loginout);
		// setLoginout.setOnClickListener(this);
		finish.setOnClickListener(this);
		chongzhi.setOnClickListener(this);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.man:
						gender = "2";
						break;
					case R.id.woman:
						gender = "1";
						break;
					case R.id.mimi:
						gender = "0";
						break;
				}
			}
		});
		shoushi = (Button) findViewById(R.id.shoushi_yingdao);
		zhendong = (Button) findViewById(R.id.zhendong_ToggleButton0);
		jingyin = (Button) findViewById(R.id.setting_jingyin);
		bgmusic = (Button) findViewById(R.id.ToggleButtonbgMusic);
		musicControl = (SeekBar) findViewById(R.id.music_seekBar);
		gameVersion = (TextView) findViewById(R.id.game_version);
		headIv = (ImageView) findViewById(R.id.setting_touxiang);
		String qudaoString = ChannelUtils.getUChannel();
		String pichiString = ChannelUtils.getBatchId();
		String banbenString = ActivityUtils.getVersionName();
		gameVersion.setText("版本：zzyddz"+ "_" + banbenString);
		gameSetBtn.setOnClickListener(this);
		gameAccountBtn.setOnClickListener(this);
		gameAboutBtn.setOnClickListener(this);
		gameFeedBackBtn.setOnClickListener(this);
		shoushi.setOnClickListener(this);
		zhendong.setOnClickListener(this);
		jingyin.setOnClickListener(this);
		bgmusic.setOnClickListener(this);
		emailEdit = (EditText) findViewById(R.id.email_setting);
		//		emailEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
		//			@Override
		//			public void onFocusChange(View v, boolean hasFocus) {
		//				if(!hasFocus){//失去焦点
		//					String email=emailEdit.getText().toString();
		//					if(TextUtils.isEmpty(email) ||!PatternUtils.isEmail(email)){
		//						Toast.makeText(SettingActivity.this, "请输入正确邮箱地址", Toast.LENGTH_SHORT).show();
		//					}
		//				}
		//			}
		//		});
		// 设置初始化
		if (PreferenceHelper.getMyPreference().getSetting().getBoolean("shoushi", true)) {
			shoushi.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.open, true));
		} else {
			shoushi.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.close, true));
		}
		if (PreferenceHelper.getMyPreference().getSetting().getBoolean("zhendong", true)) {
			zhendong.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.open, true));
		} else {
			zhendong.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.close, true));
		}
		if (PreferenceHelper.getMyPreference().getSetting().getBoolean("jingyin", false)) {
			jingyin.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.open, true));
		} else {
			jingyin.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.close, true));
		}
		if (PreferenceHelper.getMyPreference().getSetting().getBoolean("bgmusic", true)) {
			bgmusic.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.open, true));
		} else {
			bgmusic.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.close, true));
		}
		audiomanage = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int max = audiomanage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		musicControl.setMax(max);
		musicControl.setProgress(PreferenceHelper.getMyPreference().getSetting().getInt("music", 0));
		musicControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {}

			public void onStartTrackingTouch(SeekBar seekBar) {}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				Log.i("onProgressChanged", "progress: " + progress);
				PreferenceHelper.getMyPreference().getEditor().putInt("music", progress).commit();
				AudioPlayUtils.getInstance().SetVoice(progress);
			}
		});
		// 反馈初始化
		tijiao = (Button) findViewById(R.id.game_feedback_ok_btn);
		feedBackEdit = (EditText) findViewById(R.id.game_feedback_edt);
		tijiao.setOnClickListener(this);
		feedBackList = (ListView) findViewById(R.id.game_feedback_lsitview);
//		mMainMenuBar = (MainMenuBar) findViewById(R.id.main_page_bottom_rl);
		if (bundle != null) {
			gameFeedBackBtn.setTextColor(Color.WHITE);
			gameAccountBtn.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
			gameSetBtn.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
			gameFeedBackBtn.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
			gameAboutBtn.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
			int page = bundle.getInt("page");
			switch (page) {
				case 0:
					gameAccountBtn.setTextColor(Color.WHITE);
					gameAccountBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_left_select, true));
					gameSetBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center, true));
					gameFeedBackBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center, true));
					gameAboutBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_right, true));
					break;
				case 1:
					gameSetBtn.setTextColor(Color.WHITE);
					gameAccountBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_left, true));
					gameSetBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center_select, true));
					gameFeedBackBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center, true));
					gameAboutBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_right, true));
					break;
				case 2:
					gameFeedBackBtn.setTextColor(Color.WHITE);
					gameAccountBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_left, true));
					gameSetBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center, true));
					gameFeedBackBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center_select, true));
					gameAboutBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_right, true));
					break;
				case 3:
					gameAboutBtn.setTextColor(Color.WHITE);
					gameAccountBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_left, true));
					gameSetBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center, true));
					gameFeedBackBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center, true));
					gameAboutBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_right_select, true));
					break;
				default:
					break;
			}
			getPageView(page);
			// findViewById(R.id.set_bttom).setVisibility(View.GONE);
			if (userAskList == null) { // 第下次加载自己的提问
				LoadMyAskTask loadMyAskTask = new LoadMyAskTask();
				loadMyAskTask.execute(1);
			}
		}
	}

	protected void onStart() {
		super.onStart();
		GenericTask loadUserInfoTask = new LoadUserInfoTask();
		loadUserInfoTask.setFeedback(feedback);
		loadUserInfoTask.execute();
		taskManager.addTask(loadUserInfoTask);
		int currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC);
		musicControl.setProgress(currentVolume);
		PreferenceHelper.getMyPreference().getEditor().putInt("music", currentVolume).commit();
	}

	private void setUserInfo() {
		GameUser gameUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		if (null == gameUser || TextUtils.isEmpty(gameUser.getCreateDate()) || TextUtils.isEmpty(gameUser.getAccount()) || TextUtils.isEmpty(gameUser.getGender())) {
			return;
		}
		// 格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat(" ", Locale.SIMPLIFIED_CHINESE);
		sdf.applyPattern("yyyy/MM/dd");// 设置日期显示格式
		Date date0 = new Date();
		long test = Long.parseLong(gameUser.getCreateDate());// 这个表示秒数，这里如果直接用*1000的毫秒会越界，所以用秒
		date0.setTime(test); // 因为settime的时候需要使用毫秒数，所以要用秒*1000
		String dateStr1 = sdf.format(date0);
		create_time.setText(dateStr1);
		accountView.setText(gameUser.getAccount());
		if (!gameUser.getNickname().equals(gameUser.getAccount())) {
			nickName.setText(gameUser.getNickname());
		}
		// 设置性别
		gender = gameUser.getGender();
		if (gender.equals("") || gender.equals("0")) {
			baomi.setChecked(true);
		} else if (gender.equals("1")) {
			woman.setChecked(true);
		} else if (gender.equals("2")) {
			man.setChecked(true);
		}
		emailEdit.setText(gameUser.getEmail());
		zhidou.setText(String.valueOf(gameUser.getBean()));
		zhishang.setText("" + gameUser.getIq());
		zhishangPb.setMax(100);
		float step = gameUser.getNextIntellect() / 100f;
		zhishangPb.setProgress(Math.round(gameUser.getIntellect() / step));
		zhishangPlTv.setText("" + gameUser.getIntellect() + "/" + gameUser.getNextIntellect());
		if (!TextUtils.isEmpty(gameUser.getGender()) && "1".equals(gameUser.getGender())) {// 女性
			headIv.setBackgroundResource(R.drawable.nv_photo_tip);
		} else {
			headIv.setBackgroundResource(R.drawable.nan_photo_tip);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.set_account || v.getId() == R.id.set_set || v.getId() == R.id.set_about || v.getId() == R.id.set_feedback) {
			gameAccountBtn.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
			gameSetBtn.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
			gameFeedBackBtn.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
			gameAboutBtn.setTextColor(getResources().getColor(R.color.gpl_top_btn_text_color));
		}
		switch (v.getId()) {
			case R.id.set_account:
				MobclickAgent.onEvent(SettingActivity.this, "我的账号");
				gameAccountBtn.setTextColor(Color.WHITE);
				gameAccountBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_left_select, true));
				gameSetBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center, true));
				gameFeedBackBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center, true));
				gameAboutBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_right, true));
				// findViewById(R.id.set_bttom).setVisibility(View.VISIBLE);
				// findViewById(R.id.setting_finish_button).setVisibility(View.VISIBLE);
				getPageView(0);
				break;
			case R.id.set_set:
				MobclickAgent.onEvent(SettingActivity.this, "S游戏设置");
				gameSetBtn.setTextColor(Color.WHITE);
				gameAccountBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_left, true));
				gameSetBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center_select, true));
				gameFeedBackBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center, true));
				gameAboutBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_right, true));
				// findViewById(R.id.set_bttom).setVisibility(View.VISIBLE);
				// findViewById(R.id.setting_finish_button).setVisibility(View.GONE);
				getPageView(1);
				break;
			case R.id.set_about:
				MobclickAgent.onEvent(SettingActivity.this, "关于游戏");
				gameAboutBtn.setTextColor(Color.WHITE);
				gameAccountBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_left, true));
				gameSetBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center, true));
				gameFeedBackBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center, true));
				gameAboutBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_right_select, true));
				// findViewById(R.id.set_bttom).setVisibility(View.VISIBLE);
				// findViewById(R.id.setting_finish_button).setVisibility(View.GONE);
				getPageView(3);
				break;
			case R.id.set_feedback:
				MobclickAgent.onEvent(SettingActivity.this, "设置反馈");
				gameFeedBackBtn.setTextColor(Color.WHITE);
				gameAccountBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_left, true));
				gameSetBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center, true));
				gameFeedBackBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_center_select, true));
				gameAboutBtn.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gpl_top_right, true));
				getPageView(2);
				// findViewById(R.id.set_bttom).setVisibility(View.GONE);
				if (userAskList == null) { // 第下次加载自己的提问
					LoadMyAskTask loadMyAskTask = new LoadMyAskTask();
					loadMyAskTask.execute(1);
				}
				break;
			case R.id.game_feedback_ok_btn: // 提交反馈问题
				MobclickAgent.onEvent(SettingActivity.this, "提交反馈");
				String question = feedBackEdit.getText().toString();
				if (TextUtils.isEmpty(question)) {
					DialogUtils.mesTip("您提交的反馈问题不能为空!", false);
				} else {
					// 提交反馈问题
					GenericTask submitAskTask = new SubmitAskTask();
					submitAskTask.setFeedback(feedback);
					TaskParams pms = new TaskParams();
					pms.put("question", question);
					submitAskTask.execute(pms);
					taskManager.addTask(submitAskTask);
				}
				break;
			case R.id.shoushi_yingdao://手势引导
				boolean shoushitishi = PreferenceHelper.getMyPreference().getSetting().getBoolean("shoushi", true);
				PreferenceHelper.getMyPreference().getEditor().putBoolean("shoushi", !shoushitishi).commit();//手势总开关
				if (PreferenceHelper.getMyPreference().getSetting().getBoolean("shoushi", true)) {
					PreferenceHelper.getMyPreference().getEditor().putInt("newImage", 3).commit();//新图鉴
					PreferenceHelper.getMyPreference().getEditor().putInt("pointTable", 2).commit();//轻敲桌面
					PreferenceHelper.getMyPreference().getEditor().putInt("slideLeftRight", 1).commit();//左右滑动
					PreferenceHelper.getMyPreference().getEditor().putInt("slideDown", 2).commit();//向下滑动
					PreferenceHelper.getMyPreference().getEditor().putInt("slideUp", 2).commit();//向上滑动
					PreferenceHelper.getMyPreference().getEditor().putInt("slideLeftRight", 1).commit();//向左右滑动
					shoushi.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.open, true));
				} else {
					PreferenceHelper.getMyPreference().getEditor().putInt("newImage", 0).commit();//新图鉴
					PreferenceHelper.getMyPreference().getEditor().putInt("pointTable", 0).commit();//轻敲桌面
					PreferenceHelper.getMyPreference().getEditor().putInt("slideLeftRight", 0).commit();//左右滑动
					PreferenceHelper.getMyPreference().getEditor().putInt("slideDown", 0).commit();//向下滑动
					PreferenceHelper.getMyPreference().getEditor().putInt("slideUp", 0).commit();//向上滑动
					PreferenceHelper.getMyPreference().getEditor().putInt("slideLeftRight", 0).commit();//向左右滑动
					shoushi.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.close, true));
				}
				break;
			case R.id.zhendong_ToggleButton0://震动
				boolean zhengDong = PreferenceHelper.getMyPreference().getSetting().getBoolean("zhendong", true);
				PreferenceHelper.getMyPreference().getEditor().putBoolean("zhendong", !zhengDong).commit();
				if (PreferenceHelper.getMyPreference().getSetting().getBoolean("zhendong", true)) {
					zhendong.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.open, true));
				} else {
					zhendong.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.close, true));
				}
				break;
			case R.id.setting_jingyin://静音
				boolean jingYin = PreferenceHelper.getMyPreference().getSetting().getBoolean("jingyin", false);
				PreferenceHelper.getMyPreference().getEditor().putBoolean("jingyin", !jingYin).commit();
				if (PreferenceHelper.getMyPreference().getSetting().getBoolean("jingyin", false)) {
					jingyin.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.open, true));
				} else {
					jingyin.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.close, true));
				}
				break;
			case R.id.ToggleButtonbgMusic://背景音乐
				boolean beijing = PreferenceHelper.getMyPreference().getSetting().getBoolean("bgmusic", true);
				PreferenceHelper.getMyPreference().getEditor().putBoolean("bgmusic", !beijing).commit();
				if (PreferenceHelper.getMyPreference().getSetting().getBoolean("bgmusic", true)) {
					bgmusic.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.open, true));
				} else {
					bgmusic.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.close, true));
				}
				break;
			case R.id.xiugaimima:
				MobclickAgent.onEvent(SettingActivity.this, "设置绑定账号");
				AccountBindDialog bindDialog = new AccountBindDialog(SettingActivity.this);
				if (null != bindDialog && !bindDialog.isShowing()) {
					bindDialog.show();
				}
				break;
			case R.id.chong_setting:
				MobclickAgent.onEvent(SettingActivity.this, "修改密码");
				recharge();
				break;
			case R.id.set_back:
				MobclickAgent.onEvent(SettingActivity.this, "设置返回");
				finishSelf();
				break;
			case R.id.setting_finish_button://保存设置
				final String email = emailEdit.getText().toString();
				if (nickName.getText().toString().trim().equals("")) {
					Toast.makeText(SettingActivity.this, "请输入您的的昵称！", Toast.LENGTH_SHORT).show();
					return;
				} else if (PatternUtils.hasIllegalWords(nickName.getText().toString().trim())) {//是否存在非法字符
					Toast.makeText(SettingActivity.this, "昵称不能有特殊字符！", Toast.LENGTH_SHORT).show();
					return;
				} else if (PatternUtils.hasSensitivword(nickName.getText().toString().trim())) {//是否存在非法字符
					Toast.makeText(SettingActivity.this, "昵称不能有敏感信息！", Toast.LENGTH_SHORT).show();
					return;
				} 
				else if (!PatternUtils.isEmail(email)) { // 如果邮箱是空的话
					Toast.makeText(SettingActivity.this, "请输入正确邮箱地址", Toast.LENGTH_SHORT).show();
					return;
				} else {
					submitInfo(email);
//				if(gameUser.getAccount().equals(gameUser.getNickname())){
//					//账号==呢称，说明用户还没改过昵称，不扣金豆
//				}else{//改过一次昵称之后，再改需要支付20000金豆(防止恶意该昵称)
//					GameDialog gameDialog = new GameDialog(Database.currentActivity, true) {
//						public void okClick() {//确定
//							submitInfo(email);
//						}
//					};
//					gameDialog.show();
//					gameDialog.setText("此次修改昵称需花费20000金豆，确定修改？");
//				}
				}
				break;
		}
	}

	/**
	 * 提交用户信息
	 * @param email
	 */
	private void submitInfo(final String email) {
		MobclickAgent.onEvent(SettingActivity.this, "设置提交");
		nickName.setText(PatternUtils.replaceBlank(nickName.getText().toString()));
		GenericTask completePersonalInfoTask = new CompletePersonalInfoTask();
		completePersonalInfoTask.setFeedback(feedback);
		TaskParams params = new TaskParams();
		params.put("email", email);
		completePersonalInfoTask.execute(params);
		taskManager.addTask(completePersonalInfoTask);
	};

	/**
	 * 注销
	 */
	public void logout() {
		Toast.makeText(getApplicationContext(), "退出登录成功", Toast.LENGTH_SHORT).show();
		GameCache.remove(CacheKey.GAME_USER);
		Intent in1 = new Intent();
		in1.putExtra("logout", "logout");
		in1.setClass(getApplicationContext(), SDKFactory.getLoginView());
		startActivity(in1);
	}

	private void getPageView(int pageID) {
		for (int i = 0; i < taskLayoutList.size(); i++) {
			if (pageID == i) {
				taskLayoutList.get(i).setVisibility(View.VISIBLE);
			} else {
				taskLayoutList.get(i).setVisibility(View.GONE);
			}
		}
	}

	private void recharge() {
		Intent in = new Intent();
		in.setClass(getApplicationContext(), SDKFactory.getPayView());
		startActivity(in);
	}

	/**
	 * 完善资料
	 */
	private class CompletePersonalInfoTask extends GenericTask {

		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				TaskParams param = null;
				if (params.length <= 0) {
					return TaskResult.FAILED;
				}
				param = params[0];
				GameUser updateUser = (GameUser)GameCache.getObj(CacheKey.GAME_USER);
				updateUser.setNickname(nickName.getText().toString());
				updateUser.setEmail(param.getString("email"));
				updateUser.setGender(gender);
				updateUser.setPhoneNum("");
				updateUser.setHeadImage("");
				String jsonResult = HttpRequest.updateCustomer(updateUser);
				if (!TextUtils.isEmpty(jsonResult)) {
					JsonResult result = JsonHelper.fromJson(jsonResult, JsonResult.class);
					if (JsonResult.SUCCESS.equals(result.getMethodCode())) { // 成功
						GameUser gu = JsonHelper.fromJson(result.getMethodMessage(), GameUser.class);
						GameCache.putObj(CacheKey.GAME_USER,gu);
						// 送金豆(第一次)
						String sendBeanResult = HttpRequest.completePersonalInfo();
						if (HttpRequest.FAIL_STATE.equals(sendBeanResult)) { // 失败
							DialogUtils.mesTip("完善资料失败!", false);
						} else if (HttpRequest.SENSITIVE_WORDS.equals(sendBeanResult)) { // 敏感字符
							DialogUtils.mesTip("禁止敏感字符!", false);
						} else if (HttpRequest.TOKEN_ILLEGAL.equals(sendBeanResult)) { // 用户登录Token过期
							DialogUtils.reLogin(Database.currentActivity);
						} else {
							final GameTask resultTask = JsonHelper.fromJson(sendBeanResult, GameTask.class);
							runOnUiThread(new Runnable() {

								public void run() {
									if (GameTask.TASK_TYPE[3] == resultTask.getType()) { // 完善资料
										if (CmdUtils.FAIL_CODE.equals(resultTask.getValue())) { // 送豆6失败
										} else if (CmdUtils.SUCCESS_CODE.equals(resultTask.getValue())) {
											GameUser gameUser = (GameUser)GameCache.getObj(CacheKey.GAME_USER);
											long bean = gameUser.getBean() + resultTask.getCount();
											gameUser.setBean(bean);
											GameCache.putObj(CacheKey.GAME_USER,gameUser);
											
											zhidou.setText(String.valueOf(gameUser.getBean()));
											if (!TextUtils.isEmpty(gameUser.getGender()) && "1".equals(gameUser.getGender())) {// 女性
												headIv.setBackgroundResource(R.drawable.nv_photo_tip);
											} else {
												headIv.setBackgroundResource(R.drawable.nan_photo_tip);
											}
											DialogUtils.toastTip("恭喜您首次完善资料获取" + resultTask.getCount() + "金豆!");
										} else if ("2".equals(resultTask.getValue())) {
											DialogUtils.toastTip("完善资料成功");
										}
									}
								}
							});
						}
					} else {
						DialogUtils.mesTip(result.getMethodMessage(), false);
					}
				}
			} catch (Exception e) {
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}
	}

	private class LoadUserInfoTask extends GenericTask {

		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				GameUser gameUser = (GameUser)GameCache.getObj(CacheKey.GAME_USER);
				if (gameUser != null) {
					gameUser = HttpRequest.getGameUserDetail(gameUser.getLoginToken());
					gameUser.setRound(0);
					GameCache.putObj(CacheKey.GAME_USER,gameUser);
				}
			} catch (Exception e) {
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}
		
		@Override
		protected void onPostExecute(TaskResult result) {
			super.onPostExecute(result);
			setUserInfo(); // 设置内容
		}
		
	}

	/**
	 * 加载我的提问
	 */
	private class LoadMyAskTask extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			try {
				int pageNo = params[0];
				PageQueryResult queryResult = HttpRequest.getMyAsk(pageNo);
				userAskList = queryResult.getDataList();
			} catch (Exception e) {}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (null != userAskList) {
				feedBackAdapter = new FeedBackAdapter(SettingActivity.this, userAskList);
				feedBackList.setAdapter(feedBackAdapter);
			}
		}
	}

	/**
	 * 提交我的提问
	 */
	private class SubmitAskTask extends GenericTask {

		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				String question = params[0].getString("question");
				boolean result = HttpRequest.submitMyAsk(question);
				if (result) {
					PageQueryResult queryResult = HttpRequest.getMyAsk(1);
					userAskList = (List<GameUserAsk>) queryResult.getDataList();
					runOnUiThread(new Runnable() {

						public void run() {
							if (null != userAskList) {
								feedBackAdapter = new FeedBackAdapter(SettingActivity.this, userAskList);
								feedBackList.setAdapter(feedBackAdapter);
								feedBackEdit.setText("");
							}
						}
					});
				}
			} catch (Exception e) {
				return TaskResult.FAILED;
			}
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

	/**
	 * 反馈Adapter
	 */
	private class FeedBackAdapter extends BaseAdapter {

		private Context context;
		private List<GameUserAsk> userAskList;
		private LayoutInflater mInflater;

		public FeedBackAdapter(Context context, List<GameUserAsk> userAskList) {
			this.context = context;
			this.userAskList = userAskList;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return userAskList.size();
		}

		@Override
		public Object getItem(int position) {
			return userAskList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.feedback_item, null);
				holder.questionImg = (ImageView) convertView.findViewById(R.id.feedback_item_question_img);
				holder.questionTv = (TextView) convertView.findViewById(R.id.feedback_item_question_tv);
				holder.answerImg = (ImageView) convertView.findViewById(R.id.feedback_item_answer_img);
				holder.answerTv = (TextView) convertView.findViewById(R.id.feedback_item_answer_tv);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			GameUserAsk gameUserAsk = userAskList.get(position);
			holder.questionImg.setImageResource(R.drawable.question1);
			holder.questionTv.setText(gameUserAsk.getQuestion());
			holder.answerImg.setImageResource(R.drawable.answer);
			if (GameUserAsk.ST_AN_YES.equals(gameUserAsk.getStatus())) {// 已解答
				holder.answerTv.setText(gameUserAsk.getAnswer());
			} else {
				holder.answerTv.setText("");
			}
			mst.adjustView(parent);
			return convertView;
		}

		class ViewHolder {

			private ImageView questionImg;
			private TextView questionTv;
			private ImageView answerImg;
			private TextView answerTv;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != taskLayoutList) {
			taskLayoutList = null;
		}
		if (null != userAskList) {
			userAskList = null;
		}
		ImageUtil.releaseDrawable(layout.getBackground());
//		if(mMainMenuBar != null){
//			mMainMenuBar.onDestory();
//			mMainMenuBar = null;
//		}
	}
}
