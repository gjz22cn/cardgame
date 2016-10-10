package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.ContentDetail;
import com.lordcard.entity.GoodsDetails;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;

public class GuideTextDialog extends Dialog implements OnClickListener {

	OnItemClickListener guideClickListener;
	private Context context;
	private String detailId;
	private TextView toptView;
	private LinearLayout mainLayout;
	private ListView guidelListView;
	private List<GoodsDetails> detailtextList;

	protected GuideTextDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
	}

	public GuideTextDialog(Context context, int theme, String detailId) {
		super(context, theme);
		this.context = context;
		this.detailId = detailId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goods_guide_text);
		mainLayout = (LinearLayout) findViewById(R.id.guide_text_layout);
		mainLayout.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.photo_bg, false));
		layout(context);
	}

	public void setDismiss() {}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	private void layout(final Context context) {
		guidelListView = (ListView) findViewById(R.id.guide_list_detail);
		guidelListView.setEnabled(false);
		toptView = (TextView) findViewById(R.id.envalue_top_text);
		Button backButton = (Button) findViewById(R.id.envalue_back);
		backButton.setOnClickListener(this);
		// 掉接口初始化物品指南选中详细内容
		new Thread() {

			public void run() {
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("detailId", detailId);
				try {
					String result = HttpUtils.post(HttpURL.GUIDE_DETAIL_URL, paramMap,true);
					Database.GUIDE_DETAIL_LIST = JsonHelper.fromJson(result, new TypeToken<ContentDetail>() {});
					detailtextList = JsonHelper.fromJson(Database.GUIDE_DETAIL_LIST.getDescription(), new TypeToken<List<GoodsDetails>>() {});
					Database.currentActivity.runOnUiThread(new Runnable() {

						public void run() {
							toptView.setText(Database.GUIDE_DETAIL_LIST.getTitle());
							if (detailtextList != null && detailtextList.size() > 0) {
								StoveGuideAdapter guideAdapter = new StoveGuideAdapter(context, detailtextList);
								guidelListView.setAdapter(guideAdapter);
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		// Protocol.gameGuideDetail(detailId,new HttpCallback() {
		// @Override
		// public void onSucceed(Object... obj) {
		// // TODO Auto-generated method stub
		// String result = (String) obj[0];
		//
		// Database.GUIDE_DETAIL_LIST = JsonHelper.fromJson(result,new
		// TypeToken<ContentDetail>(){});
		//
		// detailtextList =
		// JsonHelper.fromJson(Database.GUIDE_DETAIL_LIST.getDescription(),
		// new TypeToken<List<GoodsDetails>>(){});
		//
		//
		//
		// Database.currentActivity.runOnUiThread(new Runnable() {
		// public void run() {
		// toptView.setText(Database.GUIDE_DETAIL_LIST.getTitle());
		// if(detailtextList !=null && detailtextList.size() > 0){
		// StoveGuideAdapter guideAdapter = new StoveGuideAdapter(context,
		// detailtextList);
		// guidelListView.setAdapter(guideAdapter);
		// }
		// }
		// });
		//
		//
		// }
		// @Override
		// public void onFailed(Object... obj) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// });
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

	/**
	 * 初始化物品指南
	 */
	private class StoveGuideAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<GoodsDetails> datalist;

		public StoveGuideAdapter(Context context, List<GoodsDetails> datalist) {
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

		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.stove_guide_item, null);
				holder.leftBtn = (TextView) convertView.findViewById(R.id.guide_left_btn);
				holder.leftBtn.setVisibility(View.GONE);
				holder.guideItem = (TextView) convertView.findViewById(R.id.guide_item_data);
				holder.guideItem.setText("    " + datalist.get(position).getText());
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			return convertView;
		}

		@SuppressWarnings("unused")
		public class ViewHolder {

			public TextView guideItem;
			public TextView leftBtn;
		}
	}
}
