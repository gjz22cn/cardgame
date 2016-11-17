package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Database;
import com.lordcard.entity.SignVo;
import com.lordcard.network.http.GameCache;
import com.lordcard.ui.view.AlignLeftGallery;
import com.sdk.constant.SDKConfig;
import com.sdk.constant.SDKConstant;
import com.sdk.util.RechargeUtils;
import com.sdk.util.SDKFactory;


/**
 * 签名对话框
 * 
 * @ClassName: SignDialog
 * @Description: TODO
 * @author zhenggang
 * @date 2013-5-9 下午6:16:39
 */
public class SignDialog extends Dialog implements android.view.View.OnClickListener {

	private String day[] = { "1天", "2天", "3天", "4天", "5天+" };
	private String signGoldTvKey[] = { "sign_one_day", "sign_two_day", "sign_three_day", "sign_four_day", "sign_five_day" };
	private int signGoldIcon[] = { R.drawable.species02_ico, R.drawable.species03_ico, R.drawable.species04_ico, R.drawable.bean_ico, R.drawable.bean_ico };
	private boolean isSign[] = { false, false, false, false, false };
	private Context context;
	private AlignLeftGallery mGallery;
	private GalleryAdapter mGalleryAdapter;
	private List<SignVo> signList;
	private List<String> signContent;
	private Button backBtn, okBtn, closeBtn;
	private Handler mHandler;
	private int signItem = 10;
	private int signCount;
	private boolean signSuccess;
	private TextView showText;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private RelativeLayout layout;

	public SignDialog(final Context context, int theme, int signCount, boolean signSuccess) {
		super(context, theme);
		this.context = context;
		signList = new ArrayList<SignVo>();
		signContent = new ArrayList<String>();
		this.signSuccess = signSuccess;
		this.signCount = signCount;
		initData();
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_dialog);
		((TextView) findViewById(R.id.dialog_title_tv)).setText("每日签到");
		layout = (RelativeLayout) findViewById(R.id.exchange_dialog_layout);
		mst.adjustView(layout);
		showText = (TextView) findViewById(R.id.sign_dialog_text1_tv);
		backBtn = (Button) findViewById(R.id.sign_dialog_back_btn);
		backBtn.setOnClickListener(this);
		//okBtn = (Button) findViewById(R.id.sign_dialog_ok_btn);
		//okBtn.setOnClickListener(this);
		closeBtn = (Button) findViewById(R.id.dialog_close_btn);
		closeBtn.setOnClickListener(this);
		mGallery = (AlignLeftGallery) findViewById(R.id.sign_gallery);
		mGalleryAdapter = new GalleryAdapter(context, signList);
		mGallery.setAdapter(mGalleryAdapter);
		if (signSuccess) {
			showText.setText("您已经连续登录" + signList.get(signCount).getDay() + ",将获得" + signContent.get(signCount));
			Toast.makeText(context, "恭喜您成功签到！", 500).show();
		}
		String msgTip = context.getString(R.string.sign_msg);		
	}

	private void initData() {
		for (int i = 0; i < 5; i++) {
			String signReward1 = "";
			String result = GameCache.getStr(CacheKey.KEY_TEXT_VIEW_MESSAGE_DATA);
			Map<String, String> TaskMenuMap = null;
			if (!TextUtils.isEmpty(result)) {
				TaskMenuMap = JsonHelper.fromJson(result, new TypeToken<Map<String, String>>() {});
			}
			if (null != TaskMenuMap && TaskMenuMap.containsKey(signGoldTvKey[i])) {
				signReward1 = TaskMenuMap.get(signGoldTvKey[i]);
				signReward1 = TextUtils.isEmpty(signReward1) ? "" : signReward1;
			}
			signList.add(new SignVo(day[i], signGoldIcon[i], signReward1, isSign[i]));
			signContent.add(signReward1);
		}
		setSignCount(signCount);
	}

	public void setSignCount(int signCount) {
		this.signCount = signCount >= 5 ? 4 : signCount;
		for (int i = 0; i < signList.size(); i++) {
			// (i==(signCount-1))&&(i!=4))i天已经签到，并且i天不等于5天+
			signList.get(i).setSign((i <= this.signCount));
		}
	}

	public class MyGallery extends Gallery {

		public MyGallery(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		public MyGallery(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public MyGallery(Context context) {
			super(context);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (velocityX > 0) {
				super.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
			} else {
				super.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
			}
			return false;
		}
	}

	private class GalleryAdapter extends BaseAdapter {

		private List<SignVo> mSignList;
		private LayoutInflater mInflater;

		public GalleryAdapter(Context context, List<SignVo> mSignList) {
			this.mSignList = mSignList;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mSignList.size();
		}

		@Override
		public Object getItem(int position) {
			return mSignList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.gallery_item, null);
				holder.img1 = (ImageView) convertView.findViewById(R.id.gallery_item_img1);
				holder.img2 = (ImageView) convertView.findViewById(R.id.gallery_item_img2);
				holder.transparent_img = (ImageView) convertView.findViewById(R.id.gallery_item_transparent_iv);
				holder.tv1 = (TextView) convertView.findViewById(R.id.gallery_item_tv1);
				holder.tv2 = (TextView) convertView.findViewById(R.id.gallery_item_tv2);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			SignVo signVo = mSignList.get(position);
			if (signVo.isSign()) {
				holder.transparent_img.setVisibility(View.INVISIBLE);
				holder.img1.setVisibility(View.VISIBLE);
			} else {
				holder.transparent_img.setVisibility(View.VISIBLE);
				holder.img1.setVisibility(View.INVISIBLE);
			}
			holder.img2.setImageResource(signVo.getImgId());
			holder.tv1.setText(signVo.getDay());
			holder.tv2.setText(signVo.getContent());
			// mst.adjustView(parent);
			// mst.unRegisterView(convertView);
			return convertView;
		}

		class ViewHolder {

			ImageView img1, img2, transparent_img;
			TextView tv1, tv2;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sign_dialog_back_btn:
				mst.unRegisterView(layout);
				dismiss();
				break;
			/*case R.id.sign_dialog_ok_btn:
				// 进入充值界面
				//MobclickAgent.onEvent(context, "签到充值");
				SDKConfig.SIGN_DIALOG = true;
				JDSMSPayUtil.setContext(context);
				//点金快速支付暂时固定5元
				PayTipUtils.showTip(0,PaySite.SIGN_IN); //配置的提示方式			
				mst.unRegisterView(layout);
				dismiss();
				break;*/
			case R.id.dialog_close_btn:
				mst.unRegisterView(layout);
				dismiss();
				break;
			default:
				break;
		}
	}
}
