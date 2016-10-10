package com.lordcard.ui.view;

import com.zzyddz.shui.R;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.ImageUtil.ImageCallback;
import com.lordcard.constant.Constant;

@SuppressLint({ "HandlerLeak", "ViewConstructor" })
public class ADWideget extends RelativeLayout {

	private ViewFlipper flipper = null;
	private JSONObject ad = null;
	private JSONArray lists = null;
	private Handler handler = null;
	private int sleepTime = 10;

	public ADWideget(Context context, JSONObject ad) {
		this(context, ad, false);
	}

	public ADWideget(Context context, JSONObject ad, boolean isLoop) {
		super(context);
		this.ad = ad;
		layout(context);
		checkTime();

	}

	public ADWideget(Context context, AttributeSet attrs, JSONObject ad) {
		super(context, attrs);
		this.ad = ad;
		layout(context);
		checkTime();
	}

	private void checkTime() {
		handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				flipper.setVisibility(View.GONE);
			}

		};
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000 * sleepTime);
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	public void layout(final Context context) {

		flipper = new ViewFlipper(context);
		try {
			// 取得广告的list
			lists = ad.getJSONArray("adJsonArray");
			for (int i = 0; i < lists.length(); i++) {// 添加到flipper
				final JSONObject obj = lists.getJSONObject(i);
				ImageView image = new ImageView(context);
				LayoutParams params = new LayoutParams((int) context.getResources().getDimension(R.dimen.ad_width), (int) context.getResources()
						.getDimension(R.dimen.ad_height));
				image.setLayoutParams(params);
				ImageUtil.setImg(obj.getString("picAddr"), image, new ImageCallback() {
					public void imageLoaded(Bitmap bitmap, ImageView view) {
						view.setScaleType(ScaleType.FIT_XY);
						view.setImageBitmap(bitmap);
					}
				});
				flipper.addView(image, params);

				image.setOnClickListener(new ClickListener(context, obj, -1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Animation uInAnim = AnimationUtils.loadAnimation(context, R.anim.push_up_in); // 向右滑动左侧进入的渐变效果（alpha 0.1 -> 1.0）
		Animation uOutAnim = AnimationUtils.loadAnimation(context, R.anim.push_up_out); // 向右滑动右侧滑出的渐变效果（alpha 1.0 -> 0.1）

		RelativeLayout.LayoutParams params1 = new LayoutParams((int) context.getResources().getDimension(R.dimen.ad_width), (int) context
				.getResources().getDimension(R.dimen.ad_height));

		flipper.setInAnimation(uInAnim);
		flipper.setOutAnimation(uOutAnim);
		flipper.setAutoStart(true);// 自动播放
		flipper.setFlipInterval(5000);// 每隔3秒播放下一张
		flipper.startFlipping();
		addView(flipper, params1);

	}

	class ClickListener implements OnClickListener {

		private Context cxt = null;
		private JSONObject advObject = null;
		private int advType; // 广告类型

		public ClickListener(Context cxt, JSONObject advObject, int advType) {
			this.advObject = advObject;
			this.cxt = cxt;
			this.advType = advType;
		}

		public void onClick(View v) {
			try {
				Intent intent = new Intent();
				String link = advObject.getString("link"); // 链接地址
				String linkMenu = advObject.getString("linkMenu"); // 链接类型
				if ("0".equals(linkMenu)) { // 外部链接
					intent.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse(link);
					intent.setData(content_url);
					intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
				} else if ("1".equals(linkMenu)) { // 商城
					if (ActivityUtils.checkApkExist(Constant.PACKAGE_NAME_SHOP)) {
						intent.setClassName(Constant.PACKAGE_NAME_SHOP, Constant.THREE_URI_SHOP_DETAIL);
						Bundle bundle = new Bundle();
						bundle.putString("goodsId", link.substring(link.lastIndexOf(":") + 1));
						intent.putExtras(bundle);
						cxt.startActivity(intent);
					} else {
						if (advType == 4) { // 游戏兑奖
							Toast.makeText(cxt, "请下载爱是商城,在商城兑奖", Toast.LENGTH_LONG).show();
							return;
						}
					}

				} else if ("2".equals(linkMenu)) { // 商城 快讯
					if (ActivityUtils.checkApkExist(Constant.PACKAGE_NAME_SHOP)) {
						intent.setClassName(Constant.PACKAGE_NAME_SHOP, Constant.THREE_URI_SHOP_NEW);
						Bundle bundle = new Bundle();
						bundle.putString("newId", link.substring(link.lastIndexOf(":") + 1));
						intent.putExtras(bundle);
						cxt.startActivity(intent);
					} else {
						if (advType == 4) { // 游戏兑奖 提示
							Toast.makeText(cxt, "请下载爱是商城,在商城兑奖", Toast.LENGTH_LONG).show();
							return;
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
