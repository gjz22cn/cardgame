package com.lordcard.ui.view.dialog;

///**
// * 修改密码框
// */
//@SuppressLint("DefaultLocale")
//public class UpdatePwdDialog extends Dialog implements OnClickListener {
//
//	private Context context;
//
//	private EditText old_password = null;
//	private EditText new_password = null;
//	private EditText queren_password = null;
//
//	public UpdatePwdDialog(Context context) {
//		super(context, R.style.dialog);
//		this.context = context;
//	}
//
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.pwd_setting_dialog);
//		layout(context);
//	}
//
//	/**
//	 * 布局
//	 */
//	private void layout(final Context context) {
//		Button cancel = (Button) findViewById(R.id.common_cancel);
//		Button ok = (Button) findViewById(R.id.common_ok);
//		cancel.setOnClickListener(this);
//		ok.setOnClickListener(this);
//
//		old_password = (EditText) findViewById(R.id.old_password);
//		new_password = (EditText) findViewById(R.id.new_password);
//		queren_password = (EditText) findViewById(R.id.queren_password);
//
//		SharedPreferences settings = context.getSharedPreferences(Constant.ACCOUNT_PWD, Context.MODE_PRIVATE);
//		String id = Database.USER.getAccount();
//		String name = settings.getString(id, "");
//		old_password.setText(name);
//		old_password.setKeyListener(null);
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.common_cancel:
//			dismiss();
//			break;
//		case R.id.common_ok:
//			okClick();
//			break;
//		default:
//			break;
//		}
//	}
//
//	/** 确定 */
//	public void okClick() {
//		String oldPwd = old_password.getEditableText().toString();
//		final String newPwd = new_password.getEditableText().toString();
//		String querenPwd = queren_password.getEditableText().toString();
//
//		if (TextUtils.isEmpty(oldPwd) || TextUtils.isEmpty(newPwd) || TextUtils.isEmpty(querenPwd)) {
//			DialogUtils.toastTip("请输入完整信息");
//		} else if (newPwd.length() < 4 || newPwd.length() > 12) {
//			DialogUtils.toastTip("密码长度为4-12位");
//		} else if (!newPwd.equals(querenPwd)) {
//			DialogUtils.toastTip("两次输入的密码不一致");
//		} else if (newPwd.equals(oldPwd)) {
//			DialogUtils.toastTip("不能和原先密码相同");
//		} else {
//			dismiss();
//			String result = HttpRequest.updatePassWord(Database.USER.getAccount(), newPwd, oldPwd);
//			JsonResult jsonResult = JsonHelper.fromJson(result,JsonResult.class);
//			if(JsonResult.SUCCESS.equals(jsonResult.getMethodCode())){
//				SharedPreferences preferences = context.getSharedPreferences(Constant.ACCOUNT_PWD, Context.MODE_PRIVATE);
//				Editor editor = preferences.edit();
//				editor.putString(Database.USER.getAccount(), newPwd);
//				editor.commit();
//
//				DialogUtils.toastTip("密码修改成功");
//			}else{
//				DialogUtils.toastTip("密码修改失败");
//			}
//		}
//	};
// }
