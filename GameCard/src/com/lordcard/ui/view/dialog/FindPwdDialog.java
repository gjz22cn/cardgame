package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.umeng.analytics.MobclickAgent;
import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.common.util.PatternUtils;
import com.lordcard.entity.JsonResult;
import com.lordcard.network.http.HttpRequest;

/**
 * 切换账号对话框
 * 
 * @ClassName: ChangeAccountDialog
 * @Description: TODO
 * @author zhenggang
 * @date 2013-5-29 下午3:04:28
 */
public class FindPwdDialog extends Dialog implements OnClickListener {
	//	private Context context;
	private EditText accountEdt, emailEdt;
	private Button okBtn, closeBtn;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();

	public FindPwdDialog(Context context) {
		super(context, R.style.dialog);
		//		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_password_dialog);
		layout();
	}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	private void layout() {
		accountEdt = (EditText) findViewById(R.id.login_dl_account);
		emailEdt = (EditText) findViewById(R.id.login_dl_account_pwd);

		okBtn = (Button) findViewById(R.id.login_dl_login_btn);
		okBtn.setText("确定");
		okBtn.setOnClickListener(this);
		closeBtn = (Button) findViewById(R.id.login_dl_close_btn);
		closeBtn.setOnClickListener(this);
		mst.adjustView(findViewById(R.id.gamelogin_dialog_layout));

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_dl_login_btn:// 确定
			mst.unRegisterView(findViewById(R.id.gamelogin_dialog_layout));
			okClick();
			break;
		case R.id.login_dl_close_btn:// 退出
			mst.unRegisterView(findViewById(R.id.gamelogin_dialog_layout));
			dismiss();
			break;
		default:
			break;
		}
	}

	/** 确定 */
	public void okClick() {
		String accountStr = accountEdt.getText().toString();
		String emailStr = emailEdt.getText().toString();
		if (TextUtils.isEmpty(accountStr)) {
			DialogUtils.mesTip("账户不能为空", false);
		} else if (accountStr.length() < 4 || accountStr.length() > 12) {
			DialogUtils.mesTip("账户必须为4-12位", false);
		} else if (TextUtils.isEmpty(emailStr)) {
			DialogUtils.mesTip("邮箱不能为空", false);
		} else if (!PatternUtils.isEmail(emailStr)) {
			DialogUtils.mesTip("邮箱格式不对", false);
		} else if (emailStr.length() > 30 || emailStr.length() < 6) {
			DialogUtils.mesTip("邮箱长度需在6~30之间", false);
		} else {
			Context ctx = CrashApplication.getInstance();
			MobclickAgent.onEvent(ctx, "找回密码确定");
			fetrievePwd(accountStr, emailStr);
		}
	};

	/**
	 * 密码找回
	 * 
	 * @param map
	 */
	public void fetrievePwd(final String account, final String email) {
		new Thread() {
			public void run() {
				String result = HttpRequest.fetrievePwd(account, email);
				JsonResult jsonResult = JsonHelper.fromJson(result, JsonResult.class);
				if (JsonResult.SUCCESS.equals(jsonResult.getMethodCode())) {
					DialogUtils.mesTip(jsonResult.getMethodMessage(), false);
				} else {
					DialogUtils.mesTip(jsonResult.getMethodMessage(), false);
				}
			};
		}.start();
	}
}
