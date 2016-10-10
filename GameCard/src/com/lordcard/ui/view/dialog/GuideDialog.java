package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.ContentTitle;
import com.lordcard.entity.GoodsDetails;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.umeng.analytics.MobclickAgent;
/**
 * 游戏指南
 * @author Administrator
 *
 */
public class GuideDialog extends Dialog implements OnClickListener {

	OnItemClickListener guideClickListener;
	private Context context;
	private List<ContentTitle> guideDatalist;
	private ListView guidelListView;
	private List<TextView> detailList;
	private List<GoodsDetails> detailtextList;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private LinearLayout layout;
	private StoveGuideAdapter guideAdapter;

	protected GuideDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public GuideDialog(Context context) {
		super(context, R.style.dialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goods_guide);
		layout(context);
		layout = (LinearLayout) findViewById(R.id.envalues_list_layout);
		mst.adjustView(layout);
	}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	private void layout(final Context context) {
		guidelListView = (ListView) findViewById(R.id.guide_list_view);
//		TextView toptView = (TextView) findViewById(R.id.envalue_top_text);
		Button backButton = (Button) findViewById(R.id.evalues_close);
		backButton.setOnClickListener(this);
		if (Database.GAME_GUIDE_LIST != null && Database.GAME_GUIDE_LIST.size() > 0) {
			guideDatalist = Database.GAME_GUIDE_LIST;
			guideAdapter = new StoveGuideAdapter(context, guideDatalist);
			guidelListView.setAdapter(guideAdapter);
		} else {
			new Thread() {

				public void run() {
					try {
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("type", Constant.GUIDE_TYPE_ONE);
						String result = HttpUtils.post(HttpURL.GAME_GUIDE_URL, paramMap, true);
						Database.GAME_GUIDE_LIST = JsonHelper.fromJson(result, new TypeToken<List<ContentTitle>>() {});
						guideDatalist = Database.GAME_GUIDE_LIST;
						if (guideDatalist != null && guideDatalist.size() > 0) {
							Database.currentActivity.runOnUiThread(new Runnable() {

								public void run() {
									guideAdapter = new StoveGuideAdapter(context, guideDatalist);
									guidelListView.setAdapter(guideAdapter);
								}
							});
						}
					} catch (Exception e) {}
				}
			}.start();
		}
		guidelListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				try {
					MobclickAgent.onEvent(context, "游戏指南选项");
					if (detailList.get(position).getVisibility() == View.GONE) {
						detailList.get(position).setVisibility(View.VISIBLE);
					} else {
						detailList.get(position).setVisibility(View.GONE);
					}
				} catch (Exception e) {}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.evalues_close:
				dismiss();
				break;
			default:
				break;
		}
	}

	/**
	 * 初始化物品指南
	 */
	private class StoveGuideAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<ContentTitle> datalist;

		public StoveGuideAdapter(Context context, List<ContentTitle> datalist) {
			this.mInflater = LayoutInflater.from(context);
			this.datalist = datalist;
			detailList = new ArrayList<TextView>();
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
				holder.guideItem = (TextView) convertView.findViewById(R.id.guide_item_data);
				holder.deitail = (TextView) convertView.findViewById(R.id.guide_text_data);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			detailList.add(holder.deitail);
			holder.guideItem.setText(datalist.get(position).getTitle() + ">>");
			detailtextList = JsonHelper.fromJson(guideDatalist.get(position).getDescription(), new TypeToken<List<GoodsDetails>>() {});
			if (detailtextList != null && detailtextList.size() > 0) {
				holder.deitail.setText(detailtextList.get(0).getText());
			}
			return convertView;
		}

		public class ViewHolder {

			public TextView guideItem;
			public TextView deitail;
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		if (mst == null) {
			mst.unRegisterView(layout);
			mst = null;
		}
		guideAdapter = null;
		guideDatalist = null;
		detailtextList = null;
//		if (null != userCountGoods) {
//			userCountGoods.clear();
//			userCountGoods = null;
//		}
//		if (null != goodstuff) {
//			goodstuff.clear();
//			goodstuff = null;
//		}
//		if (null != guideDatalist) {
//			guideDatalist.clear();
//			guideDatalist = null;
//		}
//		if (null != detailtextList) {
//			detailtextList.clear();
//			detailtextList = null;
//		}
//		if (null != detailList) {
//			detailList.clear();
//			detailList = null;
//		}
	}
}
