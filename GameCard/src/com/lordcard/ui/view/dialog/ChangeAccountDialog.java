package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.EncodeUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Constant;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.JsonResult;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.ui.LoginActivity;

/**
 * 切换账号对话框
 * 
 * @ClassName: ChangeAccountDialog
 * @Description: TODO
 * @author zhenggang
 * @date 2013-5-29 下午3:04:28
 */
public class ChangeAccountDialog extends Dialog implements OnClickListener, OnItemClickListener, OnDismissListener {
	private Context context;
	private EditText accountEdt, passwordEdt;
	private Button loginBtn, closeBtn,regBtn;
	private TextView forgetPasswordTv, titleTv;
	private Handler handler;
	private ProgressDialog progressDialog;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private RelativeLayout layout;
	private Thread loginThread;

	private ArrayList<Map<String, String>> countList = new ArrayList<Map<String, String>>();
	private ImageView mImageView;//下拉箭头

	private PopupWindow mPopup;

	private boolean mShowing;
	private PptAdapter mAdapter;
	private ListView mListView;
	private boolean mInitPopup;
	private Handler mHander;
	private static final int REFRESH = 12200;//刷新
	private static final int HIT_PPT = 12201;//隐藏PopupWindow
	public static final String ACCOUNT_PWD = "account_pwd";
	public static final String ACCOUNT_SORT = "account_sort";
	private String myselfAccount;
	public static boolean isInput = false;//是否输入账号

	public ChangeAccountDialog(Context context) {
		super(context, R.style.dialog);
		this.context = context;
	}

	public ChangeAccountDialog(Context context, Handler handler) {
		super(context, R.style.dialog);
		this.context = context;
		this.handler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_login_dialog);
		initDate();
		layout();
		layout = (RelativeLayout) findViewById(R.id.login_dialog_wh_mst);
		mst.adjustView(layout);
		mImageView = (ImageView) findViewById(R.id.show);
		mImageView.setOnClickListener(this);
		mHander = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case REFRESH:
					mAdapter.notifyDataSetChanged();
					break;
				case HIT_PPT:
					if (mPopup.isShowing()) {
						mPopup.dismiss();
					}
					break;
				default:
					break;
				}
			};
		};
	}

	/**
	 * 初始化数据
	 */
	private void initDate() {
		String accounts = ActivityUtils.getAccount();
		if (null != accounts) {//本地不为空
			String[] str = accounts.split("/");
			if (str.length > 1) {
				for (int i = str.length - 1; i >= 1; i--) {//倒叙排列，最近登录的显示在最前面(本机账号除外)
					String[] accPwd = (str[i]).split("\\|");
					if (accPwd.length != 2) {
						continue;
					}
					Map<String, String> map = new HashMap<String, String>();
					map.put(ACCOUNT_PWD, accPwd[1]);
					map.put(ACCOUNT_SORT, accPwd[0]);
					//					Log.i("countList", "账号列表 "+i+" 账号："+accPwd[0]+"   密码："+ accPwd[1]);
					countList.add(map);
				}
			} else {
				String[] accPwd = (str[0]).split("\\|");
				Map<String, String> map = new HashMap<String, String>();
				map.put(ACCOUNT_PWD, accPwd[1]);
				map.put(ACCOUNT_SORT, accPwd[0]);
				countList.add(map);
			}
			String[] accPwd = (str[0]).split("\\|");
			myselfAccount = accPwd[0];
		}
	}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	private void layout() {
		titleTv = (TextView) findViewById(R.id.dialog_title_tv);
		titleTv.setText("账号登录");

		accountEdt = (EditText) findViewById(R.id.login_dl_account);
		if (countList.size() > 0) {
			accountEdt.setText(countList.get(countList.size() - 1).get(ACCOUNT_SORT));
		}

		passwordEdt = (EditText) findViewById(R.id.login_dl_account_pwd);
		passwordEdt.setHint("密码");
		if (countList.size() > 0) {
			//因为直接显示MD5的密码，太长了，所以此处用假密码填充，登录的时候，再获取MD5的密码去注册
			passwordEdt.setText(Constant.DEFAULT_PWD);
		}
		forgetPasswordTv = (TextView) findViewById(R.id.login_dl_forget_pwd);
		forgetPasswordTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		forgetPasswordTv.setText("找回密码");
		forgetPasswordTv.setOnClickListener(this);
		loginBtn = (Button) findViewById(R.id.login_dl_login_btn);
		loginBtn.setText("登录");
		loginBtn.setOnClickListener(this);
		closeBtn = (Button) findViewById(R.id.dialog_close_btn);
		closeBtn.setOnClickListener(this);
		regBtn = (Button) findViewById(R.id.game_register);
		regBtn.setOnClickListener(this);
		regBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		

	}

	//	private Drawable getDrawable(Context context, int imgID) {
	//		Drawable drawable = context.getResources().getDrawable(imgID);
	//		// / 这一步必须要做,否则不会显示.
	//		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
	//		return drawable;
	//	}

	@Override
	public void onClick(View v) {
		Context ctx = CrashApplication.getInstance();
		switch (v.getId()) {
		case R.id.login_dl_login_btn:// 登录
			////MobclickAgent.onEvent(ctx, "切换账号登录");
			mst.unRegisterView(layout);
			login();
			break;
		case R.id.dialog_close_btn:// 退出
			accountEdt.setText("");
			passwordEdt.setText("");
			mst.unRegisterView(layout);
			dismiss();
			break;
		case R.id.login_dl_forget_pwd:// 忘记密码
			////MobclickAgent.onEvent(ctx, "忘记密码");
			FindPwdDialog findPwdDialog = new FindPwdDialog(context);
			findPwdDialog.show();
			break;
		case R.id.game_register://注册
			////MobclickAgent.onEvent(ctx, "用户注册");
			dismiss();
			handler.sendEmptyMessage(LoginActivity.HANDLER_WHAT_LOGIN_RESIGSTER_USER);
			break;	
			
		case R.id.show:// 弹出下拉列表
			////MobclickAgent.onEvent(ctx, "切换账号下拉框");
			if (countList != null && countList.size() > 0 && !mInitPopup) {
				mInitPopup = true;
				initPopup();
			}
			if (mPopup != null) {
				if (!mShowing) {
					mPopup.showAsDropDown(accountEdt, 0, -3);
					mShowing = true;
					mImageView.setBackgroundResource(R.drawable.ppt_up);
				} else {
					mPopup.dismiss();
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 弹出下拉列表
	 */
	private void initPopup() {
		mAdapter = new PptAdapter(context);
		mListView = new ListView(context);
		mListView.setCacheColorHint(Color.TRANSPARENT);//去除滑动时的背景色
		mListView.setBackgroundResource(R.drawable.lv_item_bg);
		mListView.setVerticalScrollBarEnabled(false);//去除滚动条
		mListView.setDivider(null);//去除listview的Item间的分割线
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		int height = ViewGroup.LayoutParams.WRAP_CONTENT;
		int width = accountEdt.getWidth();
		mPopup = new PopupWindow(mListView, width, height, true);
		mPopup.setOutsideTouchable(true);
		mPopup.setAnimationStyle(android.R.style.Animation_Dialog);
		mPopup.setBackgroundDrawable(new BitmapDrawable());
		mPopup.setOnDismissListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		accountEdt.setText(countList.get(position).get(ACCOUNT_SORT));
		//因为直接显示MD5的密码，太长了，所以此处用假密码填充，登录的时候，再获取MD5的密码去注册
		passwordEdt.setText(Constant.DEFAULT_PWD);
		mPopup.dismiss();
	}

	@Override
	public void onDismiss() {
		mShowing = false;
		mImageView.setBackgroundResource(R.drawable.ppt_down);
	}

	/**
	 *  登录
	 */
	private void login() {
		final String account = accountEdt.getText().toString();
		String pws = "";
		FOR: for (int i = 0; i < countList.size(); i++) {
			//账号是选择本地的，切密码是我们设的加密码，说明用户没有改用户和密码，就直接从本地账号列表中获取密码
			if (account.equals(countList.get(i).get(ACCOUNT_SORT)) && passwordEdt.getText().toString().equals(Constant.DEFAULT_PWD)) {
				pws = countList.get(i).get(ACCOUNT_PWD);//直接从本地账号列表中获取
				break FOR;
			}
		}

		//自己输入的账号，就直接获取EditTextView中输入的
		if ("".equals(pws)) {
			pws = passwordEdt.getText().toString();
		}
		final String password = pws;
		if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
			DialogUtils.toastTip("账号或密码不能为空");
			return;
		}

		progressDialog = DialogUtils.getWaitProgressDialog(context, "请稍候...");
		progressDialog.show();
		loginThread = new Thread() {
			public void run() {
				String accounts = ActivityUtils.getAccount();
				String pws = "";
				String account1 = account;
				if (accounts.contains(account + "|" + password)) {//用户是选择的用户名和密码
					pws = password;
				} else {
					pws = EncodeUtils.MD5(password);
				}
				String result = HttpRequest.login(account1, pws);
				JsonResult jsonResult = JsonHelper.fromJson(result, JsonResult.class);
				progressDialog.dismiss();
				if (null != jsonResult &&!TextUtils.isEmpty(jsonResult.getMethodCode())&&JsonResult.SUCCESS.equals(jsonResult.getMethodCode())) {
					String gameUserJson = jsonResult.getMethodMessage();
					GameUser gameUser = JsonHelper.fromJson(gameUserJson, GameUser.class);
//					Database.USER = gameUser;
					if (null == gameUser) {
						Log.e("debugs", "ChangeAccountDialog---Database.USER  清空");
					}else{
						GameCache.putObj(CacheKey.GAME_USER,gameUser);
					}
//					Database.SIGN_KEY = gameUser.getAuthKey();

					String account = gameUser.getAccount();
					if (!TextUtils.isEmpty(gameUser.getRelaAccount())) {
						account = gameUser.getRelaAccount();
					}
					ActivityUtils.removeAccount(account);
					ActivityUtils.saveAccount(account, gameUser.getMd5Pwd());
					if (handler != null) {
						handler.sendEmptyMessage(LoginActivity.HANDLER_WHAT_LOGIN_UPDATE_USER); // 通知页面更新展示账号
					}
					dismiss();
				} else {
					if(null != jsonResult && !TextUtils.isEmpty(jsonResult.getMethodMessage())){
						DialogUtils.mesTip(jsonResult.getMethodMessage(), false);
					}
				}
			};
		};
		loginThread.start();
	}

	private class PptAdapter extends BaseAdapter {
		//		private Context context;
		private LayoutInflater layoutInflater = null;

		public PptAdapter(Context context) {
			//			this.context = context;
			this.layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return countList.size();
		}

		@Override
		public Object getItem(int position) {
			return countList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder mViewHolder = null;
			if (null == convertView) {
				convertView = layoutInflater.inflate(R.layout.cad_edit_ppt_lv_item, null);
				mViewHolder = new ViewHolder();
				mViewHolder.ccountTv = (TextView) convertView.findViewById(R.id.cad_edit_ppt_lv_id_tv);
				mViewHolder.removeIv2 = (ImageView) convertView.findViewById(R.id.cad_edit_ppt_lv_delet_iv2);
				mViewHolder.removeIv = (ImageView) convertView.findViewById(R.id.cad_edit_ppt_lv_delet_iv);
				convertView.setTag(mViewHolder);
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}
			String account = countList.get(position).get(ACCOUNT_SORT);
			mViewHolder.ccountTv.setText(account);
			mViewHolder.removeIv.setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ActivityUtils.removeAccount(countList.get(position).get(ACCOUNT_SORT));
					countList.remove(position);
					if (0 == countList.size()) {
						mHander.sendEmptyMessage(HIT_PPT);
					}
					mHander.sendEmptyMessage(REFRESH);
				}
			});

			//如果是注册账号则不显示删除按钮(list中最后一个是注册账号)
			//			Map<String, String> map=countList.get(position);
			//			Iterator i=map.entrySet().iterator();
			//			String account=null;
			//			while(i.hasNext()){
			//				Map.Entry<String, String> e=(Map.Entry<String, String>)i.next();
			//				 account=e.getKey();
			//			}
			//该账号与注册账号相同
			if (null != myselfAccount && null != account && myselfAccount.equals(account)) {
				mViewHolder.removeIv.setVisibility(View.INVISIBLE);
				mViewHolder.removeIv2.setVisibility(View.INVISIBLE);
			} else {
				mViewHolder.removeIv.setVisibility(View.VISIBLE);
				mViewHolder.removeIv2.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

		private class ViewHolder {
			TextView ccountTv;//账号
			ImageView removeIv2;//删除2
			ImageView removeIv;//删除
		}
	}

}
