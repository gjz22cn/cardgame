package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.EncodeUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.JsonResult;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.ui.LoginActivity;

/**
 * 账号绑定对话框
 * 
 * @ClassName: AccountBindDialog
 * @Description: TODO
 * @author zhenggang
 * @date 2013-5-17 下午7:43:39
 */
public class AccountBindDialog extends Dialog implements OnClickListener {

	private Context context;
	//游戏IDTv,内容Tv,标题Tv
	private TextView gameAccountTv, contentTv, titleTv;
	//绑定账号Edt,新密码Edt,确认密码Edt
	private EditText relaAccountEdt, newPasswordEdt, checkPasswordEdt;
	//绑定账号检测Img,新密码检测Img,确认密码检测Img
	private ImageView newAccountOkImg, checkPasswordOkImg, newPasswordOkImg;
	private Button okButton, closeBtn;
	private LinearLayout mLinearLayout1, mLinearLayout2;
	private TextView gameIdTv, gameAccountTv1;
	// 适应多屏幕的工具
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	// private final int msg1=10000;

	private Handler handler;

	public AccountBindDialog(Context context) {
		super(context, R.style.dialog);
		this.context = context;
	}

	public AccountBindDialog(Context context, Handler handler) {
		super(context, R.style.dialog);
		this.context = context;
		this.handler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_binding_dialog);
		layout(context);
	}

	public void initView() {
		mLinearLayout1.setVisibility(View.INVISIBLE);
		mLinearLayout2.setVisibility(View.VISIBLE);
		checkPasswordOkImg.setVisibility(View.INVISIBLE);
		titleTv.setText("账号绑定");
		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		gameAccountTv.setText(cacheUser.getAccount());
		contentTv.setText(R.string.account_bind_text);
		if (!TextUtils.isEmpty(cacheUser.getRelaAccount())) {
			relaAccountEdt.setText(cacheUser.getRelaAccount());
		}
		newPasswordEdt.setText("");
		checkPasswordEdt.setText("");
	}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	private void layout(final Context context) {
		mLinearLayout1 = (LinearLayout) findViewById(R.id.recharge_name_layout1);
		mLinearLayout2 = (LinearLayout) findViewById(R.id.recharge_name_layout2);
		mLinearLayout1.setVisibility(View.INVISIBLE);
		mLinearLayout2.setVisibility(View.VISIBLE);
		titleTv = (TextView) findViewById(R.id.dialog_title_tv);
		titleTv.setText("账号绑定");
		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		gameAccountTv = (TextView) findViewById(R.id.game_account);
		gameAccountTv.setText(cacheUser.getAccount());
		contentTv = (TextView) findViewById(R.id.recharge_text_layout);
		contentTv.setText(R.string.account_bind_text);
		relaAccountEdt = (EditText) findViewById(R.id.game_rela_account);
		newAccountOkImg = (ImageView) findViewById(R.id.new_account_ok_img);
		relaAccountEdt.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {//失去焦点
					String result = relaAccountEdt.getText().toString();
					newAccountOkImg.setVisibility(View.VISIBLE);
					if (TextUtils.isEmpty(result) || result.length() < 6 || result.length() > 8) {
						//账号为空 || 账号长度<6 || 账号长度>8
						newAccountOkImg.setImageResource(R.drawable.no);
					} else {
						newAccountOkImg.setImageResource(R.drawable.yes);
					}
				} else {
					newAccountOkImg.setVisibility(View.INVISIBLE);
				}
			}
		});
		if (!TextUtils.isEmpty(cacheUser.getRelaAccount())) {
			relaAccountEdt.setText(cacheUser.getRelaAccount());
		}

		newPasswordEdt = (EditText) findViewById(R.id.new_password_edt);
		newPasswordOkImg = (ImageView) findViewById(R.id.new_password_ok_img);
		newPasswordEdt.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {//失去焦点
					String result = newPasswordEdt.getText().toString();
					newPasswordOkImg.setVisibility(View.VISIBLE);
					if (TextUtils.isEmpty(result) || result.length() < 6 || result.length() > 12) {
						//密码1为空 || 密码1长度<4 || 密码1长度>12
						newPasswordOkImg.setImageResource(R.drawable.no);
					} else {
						newPasswordOkImg.setImageResource(R.drawable.yes);
					}
				} else {
					newPasswordOkImg.setVisibility(View.INVISIBLE);
				}
			}
		});
		checkPasswordEdt = (EditText) findViewById(R.id.check_password_edt);
		checkPasswordOkImg = (ImageView) findViewById(R.id.check_password_ok_img);
		checkPasswordEdt.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {//失去焦点
					String result1 = newPasswordEdt.getText().toString();
					String result2 = checkPasswordEdt.getText().toString();
					checkPasswordOkImg.setVisibility(View.VISIBLE);
					if (TextUtils.isEmpty(result1) || TextUtils.isEmpty(result2) || result2.length() < 6 || result2.length() > 12
							|| !result1.equals(result2)) {
						//密码1为空 ||  密码2为空 || 密码2长度<4 || 密码2长度>12||密码2长度!=密码1
						checkPasswordOkImg.setImageResource(R.drawable.no);
					} else {
						checkPasswordOkImg.setImageResource(R.drawable.yes);
					}
				} else {
					checkPasswordOkImg.setVisibility(View.INVISIBLE);
				}
			}
		});
		closeBtn = (Button) findViewById(R.id.dialog_close_btn);
		closeBtn.setOnClickListener(this);
		okButton = (Button) findViewById(R.id.ok_btn);
		okButton.setOnClickListener(this);

		gameIdTv = (TextView) findViewById(R.id.game_id);
		gameAccountTv1 = (TextView) findViewById(R.id.game_account1);
		if (cacheUser != null) {
			gameIdTv.setText(cacheUser.getAccount());
			String relaAccount = cacheUser.getRelaAccount();
			if (relaAccount != null) {
				gameAccountTv1.setText(relaAccount);
			}
		}
		mst.adjustView(findViewById(R.id.account_binding_dialog_rl));
	}

	public void setContentTv(String content) {
		contentTv.setText(content);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok_btn:
			if (View.VISIBLE == mLinearLayout2.getVisibility()) {
				final String relaAccount = relaAccountEdt.getText().toString();
				final String newPassword = newPasswordEdt.getText().toString();
				String checkPassword = checkPasswordEdt.getText().toString();

				if (TextUtils.isEmpty(relaAccount) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(checkPassword)) {
					DialogUtils.toastTip("绑定账号或密码不能为空");
					return;
				}
				GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
				String accTemp = cacheUser.getAccount();
				if (!TextUtils.isEmpty(cacheUser.getRelaAccount())) {
					accTemp = cacheUser.getRelaAccount();
				}
				//本地的账号密码
				final String oldAccPwd = ActivityUtils.getUserPwd(accTemp).replaceAll("\\|", "\\\\\\|");

				final GameUser gameUser = new GameUser();
				gameUser.setAccount(cacheUser.getAccount());

				if (!TextUtils.isEmpty(newPassword)) {
					if (newPassword.length() < 6 || newPassword.length() > 12) {
						DialogUtils.toastTip("密码长度为6-12位");
						return;
					}

					if (newPassword.equals(checkPassword)) {
						gameUser.setUserPwd(EncodeUtils.MD5(checkPassword));
					} else {
						DialogUtils.toastTip("两次输入的密码不一致");
						return;
					}
				}

				if (!TextUtils.isEmpty(relaAccount)) {
					if (relaAccount.length() < 6 || relaAccount.length() > 8) {
						DialogUtils.toastTip("账号长度为6-8位");
						return;
					}
					gameUser.setRelaAccount(relaAccount);
				}

				// 提交数据到服务器
				new Thread() {
					public void run() {
						String result = HttpRequest.updateCustomer(gameUser);
						JsonResult jsonResult = JsonHelper.fromJson(result, JsonResult.class);
						if (JsonResult.SUCCESS.equals(jsonResult.getMethodCode())) {
							String gameUserJson = jsonResult.getMethodMessage();
							Log.i("gameUserJson", "绑定账号gameUserJson: " + gameUserJson);
							GameUser gameUser1 = JsonHelper.fromJson(gameUserJson, GameUser.class);

							if (!TextUtils.isEmpty(gameUser1.getMd5Pwd())) {
								GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
								String account = gameUser1.getAccount();
								if (!TextUtils.isEmpty(gameUser1.getRelaAccount())) {
									account = gameUser1.getRelaAccount();
									cacheUser.setRelaAccount(account);
									GameCache.putObj(CacheKey.GAME_USER,cacheUser);
								}

								String newAccPwd = account + "\\|" + gameUser1.getMd5Pwd();

								String newLocalUsePwd = ActivityUtils.getAccount().replaceAll(oldAccPwd, newAccPwd);
								SharedPreferences preferences = CrashApplication.getInstance().getSharedPreferences(Constant.LOCAL_ACCOUNTS,
										Context.MODE_PRIVATE);
								Editor editor = preferences.edit();
								editor.putString("acount", newLocalUsePwd);
								editor.commit();

								//								String[] accounts=ActivityUtils.getAccount().split("/");
								//								if(null != accounts){
								//									String[] acc=accounts[0].split("\\|");
								//									String nowAccount=(null ==Database.USER.getRelaAccount() || "".equals(Database.USER.getRelaAccount()) ) ? 
								//											Database.USER.getAccount() : Database.USER.getRelaAccount();
								//									if(acc[0].equals(nowAccount)){//如果是本地注册的账号，直接替换,否则直接删除
								//										String localAccount=ActivityUtils.getAccount().replaceAll(account+"\\|"+gameUser1.getMd5Pwd(), acc[0]);
								//										SharedPreferences preferences = CrashApplication.getInstance().getSharedPreferences(Constant.LOCAL_ACCOUNTS, Context.MODE_PRIVATE);
								//										Editor editor = preferences.edit();
								//										editor.putString("acount",localAccount);
								//										editor.commit();
								//									}else{
								//										ActivityUtils.removeAccount(Database.USER.getRelaAccount());
								//										ActivityUtils.saveAccount(account, gameUser1.getMd5Pwd());
								//									}
								//								}
							}
							//							Database.USER.setRelaAccount(relaAccount);
							Database.currentActivity.runOnUiThread(new Runnable() {
								public void run() {
									GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
									MobclickAgent.onEvent(context, "邀绑定账号提交数据");
									DialogUtils.toastTip("修改成功");
									gameIdTv.setText(cacheUser.getAccount());
									gameAccountTv1.setText(cacheUser.getRelaAccount());
									titleTv.setText("绑定成功");
									contentTv.setText(R.string.account_bind_ok_text);
									gameAccountTv1.setText(relaAccountEdt.getText().toString());
									gameIdTv.setText(cacheUser.getAccount());
									mLinearLayout1.setVisibility(View.VISIBLE);
									mLinearLayout2.setVisibility(View.INVISIBLE);
									ActivityUtils.BindAccount();//保存在SharedPreferences中，作为取消自动弹出绑定对话框的提示
								}
							});

							if (handler != null) {
								handler.sendEmptyMessage(LoginActivity.HANDLER_WHAT_LOGIN_UPDATE_USER); // 通知页面更新展示账号
							}
						} else {
							DialogUtils.toastTip(jsonResult.getMethodMessage());
						}
					};
				}.start();
			} else {
				dismiss();
			}
			break;
		case R.id.dialog_close_btn:
			dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public void dismiss() {
		mst.unRegisterView(findViewById(R.id.account_binding_dialog_rl));
		super.dismiss();
	}
}
