package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.ImageUtil.ImageCallback;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.ContentDetail;
import com.lordcard.entity.ContentTitle;
import com.lordcard.entity.GoodsDetails;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;

public class GuideImgDialog extends Dialog implements OnClickListener {

	OnItemClickListener guideClickListener;
	private Context context;
	private String detailId;
	private LinearLayout mainLayout;
	private List<ContentTitle> guideDatalist;
	private TextView toptView;
	private List<GoodsDetails> detailtextList;
	private List<String> picurlList = new ArrayList<String>();
	private List<Bitmap> picList = new ArrayList<Bitmap>();

	protected GuideImgDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
	}

	public GuideImgDialog(Context context, int theme, String detailId) {
		super(context, theme);
		this.context = context;
		this.detailId = detailId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goods_guide_img);
		mainLayout = (LinearLayout) findViewById(R.id.guide_image_layout);
		mainLayout.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.photo_bg, false));
		layout(context);
	}

	public void setDismiss() {
		super.dismiss();
	}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	private void layout(final Context context) {
		toptView = (TextView) findViewById(R.id.envalue_top_text);
		Button backButton = (Button) findViewById(R.id.envalue_back);
		backButton.setOnClickListener(this);
		// 掉接口初始化物品指南选中详细内容
		new Thread() {

			public void run() {
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("detailId", detailId);
				try {
					String result = HttpUtils.post(HttpURL.GUIDE_DETAIL_URL, paramMap, false);
					Database.GUIDE_DETAIL_LIST = JsonHelper.fromJson(result, new TypeToken<ContentDetail>() {});
					Database.GUIDE_DESCRIBLE_LIST = JsonHelper.fromJson(Database.GUIDE_DETAIL_LIST.getDescription(), new TypeToken<List<GoodsDetails>>() {});
					detailtextList = Database.GUIDE_DESCRIBLE_LIST;
					Database.currentActivity.runOnUiThread(new Runnable() {

						public void run() {
							toptView.setText(Database.GUIDE_DETAIL_LIST.getTitle());
							// guideAdapter = new ImageAdapter(context,
							// detailtextList);
							// //
							// guidelListView.setDivider(ImageUtil.getResDrawable(context,
							// R.drawable.stove_line));
							// viewFlipper.setAdapter(guideAdapter);
							((Gallery) findViewById(R.id.viewflipper)).setAdapter(new ImageAdapter(context, detailtextList));
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 重写返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			try {
				GuideDialog guideDialog = new GuideDialog(context);
				guideDialog.show();
				dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.envalue_back:
				GuideDialog guideDialog = new GuideDialog(context);
				guideDialog.show();
				dismiss();
				break;
			default:
				break;
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		if (null != guideDatalist) {
			guideDatalist.clear();
			guideDatalist = null;
		}
		if (null != detailtextList) {
			detailtextList.clear();
			detailtextList = null;
		}
		if (null != picurlList) {
			picurlList.clear();
			picurlList = null;
		}
		if (null != picList) {
			picList.clear();
			picList = null;
		}
//		ImageUtil.clearAdvImageCacheMap();
	}

	private class ImageAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<GoodsDetails> datalist;

		public ImageAdapter(Context context, List<GoodsDetails> datalist) {
			this.mInflater = LayoutInflater.from(context);
			this.datalist = datalist;
		}

		public int getCount() {
			return datalist.size();
		}

		public Object getItem(int position) {
			return datalist.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.guide_image_item, null);
				holder.image = (ImageView) convertView.findViewById(R.id.guide_view);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ImageUtil.setImg(HttpURL.URL_PIC_ALL + datalist.get(position).getText(), holder.image, new ImageCallback() {

				public void imageLoaded(Bitmap bitmap, ImageView view) {
					view.setScaleType(ScaleType.FIT_XY);
					view.setImageBitmap(bitmap);
				}
			});
			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}
	}
}
