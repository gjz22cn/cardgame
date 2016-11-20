package com.lordcard.ui;

import com.zzyddz.shui.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.anim.AnimUtils;
import com.lordcard.common.bean.AssistantBean;
import com.lordcard.common.bean.DataCentreBean;
import com.lordcard.common.mydb.DBHelper;
import com.lordcard.common.util.ActivityPool;
import com.lordcard.common.util.DateUtil;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Database;
import com.lordcard.entity.AssistantBtnContent;
import com.lordcard.ui.base.BaseActivity;

/**
 * 消息中心
 * @author Administrator
 */
public class DataCentreActivity extends BaseActivity implements OnClickListener {

	//private LinearLayout asslayout, xiaomeiLayout; //游戏助理layout
	private LinearLayout xiaomeiLayout; //游戏助理layout
	private ImageView xiaomeiBtn;//游戏助理显示按钮
	private DBHelper dbHelper;
	private ListView assListView, privateListView, announcementListView;
	private List<HashMap<String, Object>> assJsonList;//游戏助理信息包含json，种类，id
	private List<HashMap<String, Object>> assList;//游戏助理信息,已解析
	private List<HashMap<String, Object>> privateList;//个人信息
	private List<HashMap<String, Object>> announcementList;//公告信息
	private int assPosition;
	public static AssistantBtnContent BTN_CONTENT;
	private AssListViewAdapter assListViewadapter;
	private ListViewAdapter listViewAdapter;
	private AListViewAdapter alistViewAdapter;
	private Handler handler;
	private RadioGroup genRadio;
	private RadioButton assistantBtn, privateBtn, announcementBtn;
	private TextView assistantNum, announcementNum, privateNum;
	private TextView noMessageTv;//没信息时的提示

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbHelper = LoginActivity.dbHelper;
		setContentView(R.layout.data_centre);
		initView();
		getAssistantJsonList();
		getPrivateList();
		getGonggaoList();
		assistantNum.setVisibility(View.GONE);
		announcementNum.setVisibility(View.GONE);
		privateNum.setVisibility(View.GONE);
		handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
					case 1:
						String[] values = { Integer.valueOf(Double.valueOf(assList.get(0).get(AssistantBean.AS_ID).toString()).intValue()).toString() };
						DataCentreBean.getInstance().delete(dbHelper, "id=?", values);
						assJsonList.remove(assPosition);
						assListViewadapter.notifyDataSetChanged();
//					if(assJsonList.size()!=0){
//						assistantNum.setText(String.valueOf(assJsonList.size()));	
//					}else{
//						assistantNum.setText("");
//					}
						break;
					case 2:
						//删除个人消息UI更新
						List<HashMap<String, Object>> list = null;
						TextView num = null;
						int type = 0;
						if (msg.arg1 == 1) {
							list = privateList;
							num = privateNum;
							type = 1;
						} else if (msg.arg1 == 2) {
							list = announcementList;
							num = announcementNum;
							type = 2;
						}
						updateNum(list, num, type);
						break;
					case 3:
						//点击消息UI更新
						List<HashMap<String, Object>> list2 = null;
						TextView num2 = null;
						if (msg.arg2 == 1) {
							list2 = privateList;
							num2 = privateNum;
						} else if (msg.arg2 == 2) {
							list2 = announcementList;
							num2 = announcementNum;
						} else if (msg.arg2 == 3) {
							list2 = assJsonList;
							num2 = assistantNum;
						}
						updateClickNum(list2, num2);
						break;
				}
			}
		};
		mst.adjustView(findViewById(R.id.set_layout));
	}

	private void updateClickNum(List<HashMap<String, Object>> list, TextView num) {
		//个人消息未读数字更新
		int k = 0;
		if (list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).get(DataCentreBean.DATA_CLICK).equals("0")) {
					k++;
				}
			}
			if (k != 0) {
				num.setText(String.valueOf(k));
			} else {
				num.setText("");
			}
		} else {
			num.setText("");
		}
	}

	/**
	 * 删除消息UI更新
	 * @param list
	 * @param num
	 */
	public void updateNum(List<HashMap<String, Object>> list, TextView num, int adapterType) {
		String[] value = { Integer.valueOf(Double.valueOf(list.get(assPosition).get(DataCentreBean.DATA_ID).toString()).intValue()).toString() };
		DataCentreBean.getInstance().delete(dbHelper, "id=?", value);
		list.remove(assPosition);
		if (adapterType == 1) {
			listViewAdapter.notifyDataSetChanged();
		} else if (adapterType == 2) {
			alistViewAdapter.notifyDataSetChanged();
		}
//		int j=0;
//		if(list.size()!=0){
//			for (int i = 0; i < list.size(); i++) {
//				
//				if(list.get(i).get(DataCentreBean.DATA_CLICK).equals("0")){
//					j++;
//				}
//			}
//			if(j!=0){
//				num.setText(String.valueOf(j));	
//			}else{
//				num.setText("");
//			}
//		}else{
//			num.setText("");
//			
//		}
	}

	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.xiaomei:
//			xiaomeiBtn.setClickable(false);
//			AnimUtils.startAnimationsOutLeft(xiaomeiLayout, 800);
//			    asslayout.removeAllViews();
//				Assistant assistant = new Assistant(Database.currentActivity,dbHelper,BTN_CONTENT,GAME_ASSISTANT,xiaomeiLayout,xiaomeiBtn);
//				asslayout.addView(assistant);	
				break;
			case R.id.set_back:
				finishSelf();
				break;
			default:
				break;
		}
	};

	/**
	 * 初始化UI
	 */
	private void initView() {
		findViewById(R.id.set_back).setOnClickListener(this);
		xiaomeiLayout = (LinearLayout) findViewById(R.id.xiao_LinearLayout);
		xiaomeiBtn = (ImageView) findViewById(R.id.xiaomei);
		xiaomeiBtn.setVisibility(View.GONE);
		xiaomeiBtn.setOnClickListener(this);
		assListView = (ListView) findViewById(R.id.show_assistant);
		announcementListView = (ListView) findViewById(R.id.show_system_data);
		privateListView = (ListView) findViewById(R.id.show_private_data);
		assistantNum = (TextView) findViewById(R.id.num_assistant);
		announcementNum = (TextView) findViewById(R.id.num_announcement);
		privateNum = (TextView) findViewById(R.id.num_private);
		//assistantBtn = (RadioButton) findViewById(R.id.gen_assistant);
		privateBtn = (RadioButton) findViewById(R.id.gen_private);
		announcementBtn = (RadioButton) findViewById(R.id.gen_announcement);
		noMessageTv=(TextView) findViewById(R.id.no_message_tv);
		genRadio = (RadioGroup) findViewById(R.id.gen_radio);
		genRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
					case R.id.gen_announcement://系统公告
						assListView.setVisibility(View.GONE);
						announcementListView.setVisibility(View.VISIBLE);
						privateListView.setVisibility(View.GONE);
						setNomessageTvVisibleOrGone(announcementList);
						break;
					case R.id.gen_private://个人信息
						assListView.setVisibility(View.GONE);
						announcementListView.setVisibility(View.GONE);
						privateListView.setVisibility(View.VISIBLE);
						setNomessageTvVisibleOrGone(privateList);
						break;
					default:
						break;
				}
			}

		});
	}
	/**
	 * 隐藏或显示空信息提示布局
	 */
	private void setNomessageTvVisibleOrGone(List<HashMap<String, Object>> list) {
		if(list.size()<1){
			noMessageTv.setVisibility(View.VISIBLE);
		}else{
			noMessageTv.setVisibility(View.GONE);
		}
	}

	public class AssListViewAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<HashMap<String, Object>> datalist;

		public AssListViewAdapter(Context context, List<HashMap<String, Object>> datalist) {
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

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.data_centre_assistant_item, null);
				holder.guideItem = (Button) convertView.findViewById(R.id.cancel_btn);
				holder.titleText = (TextView) convertView.findViewById(R.id.title);
				holder.titmeText = (TextView) convertView.findViewById(R.id.time);
				holder.assistantlayout = (LinearLayout) convertView.findViewById(R.id.assistant_item_show);
				holder.imageViewRead = (ImageView) convertView.findViewById(R.id.img_read);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			//设置已读或未读
			if (datalist.get(position).get(DataCentreBean.DATA_CLICK).equals("0")) {
				holder.imageViewRead.setBackgroundResource(R.drawable.no_read);
			} else {
				holder.imageViewRead.setBackgroundResource(R.drawable.already_read);
			}
			//获取游戏助理json
			String json = (String) datalist.get(position).get(DataCentreBean.DATA_CONTENT);
			//解析json
			final List<HashMap<String, Object>> list2 = JsonHelper.fromJson(json, new TypeToken<List<HashMap<String, Object>>>() {});
			//获取游戏助理点击json
			final AssistantBtnContent content = JsonHelper.fromJson(String.valueOf(list2.get(0).get(AssistantBean.AS_BTNAC)), AssistantBtnContent.class);
			BTN_CONTENT = content;
			//设置标题和时间
			holder.titleText.setText(String.valueOf(list2.get(0).get(AssistantBean.AS_TITLE)));
			String time = String.valueOf(list2.get(0).get(AssistantBean.AS_TIME));
			SimpleDateFormat format = new SimpleDateFormat(DateUtil.NOCHAR_PATTERN);
			SimpleDateFormat format2 = new SimpleDateFormat(DateUtil.TIMESTAMP_PATTERNR);
			try {
				Date d = format.parse(time);
				String timeFormat = format2.format(d);
				holder.titmeText.setText("有效期: " + timeFormat);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			//删除事件
			holder.guideItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					assList = list2;
					assPosition = position;
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
				}
			});
			//游戏助理生成事件
			holder.assistantlayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					assList = list2;
					assPosition = position;
					String id = Integer.valueOf(Double.valueOf(datalist.get(position).get(DataCentreBean.DATA_ID).toString()).intValue()).toString();
					//更新数据库 标志为已读
					DataCentreBean.getInstance().update(dbHelper, new String[] { id }, new String[] { "1" });
					datalist.get(position).put(DataCentreBean.DATA_CLICK, "1");
//						Message msg = new Message();
//						msg.what = 3;
//						msg.arg2=3;
//						handler.sendMessage(msg);
					holder.imageViewRead.setBackgroundResource(R.drawable.already_read);
					if (xiaomeiLayout.getVisibility() == View.VISIBLE) {
						AnimUtils.startAnimationsOutLeft(xiaomeiLayout, 500);
					}
					//asslayout.removeAllViews();
					//Assistant assistant = new Assistant(Database.currentActivity, handler, content, list2, xiaomeiLayout, xiaomeiBtn);
					//asslayout.addView(assistant);
				}
			});
			return convertView;
		}
	}

	public class ListViewAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private List<HashMap<String, Object>> datalist;

		public ListViewAdapter(Context context, List<HashMap<String, Object>> datalist) {
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

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.data_centre_private_item, null);
				holder.guideItem = (Button) convertView.findViewById(R.id.cancel_btn);
				holder.titleText = (TextView) convertView.findViewById(R.id.title);
				holder.titmeText = (TextView) convertView.findViewById(R.id.time);
				holder.assistantlayout = (LinearLayout) convertView.findViewById(R.id.item_show);
				holder.imageViewRead = (ImageView) convertView.findViewById(R.id.img_read);
				holder.contextText = (TextView) convertView.findViewById(R.id.content);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Log.i("lll", "getView" + position);
			//设置已读或未读
			if (datalist.get(position).get(DataCentreBean.DATA_CLICK).equals("0")) {
				holder.imageViewRead.setBackgroundResource(R.drawable.no_read);
			} else {
				holder.imageViewRead.setBackgroundResource(R.drawable.already_read);
			}
			if (holder.contextText.getVisibility() == View.VISIBLE) {
				holder.contextText.setVisibility(View.GONE);
			}
			String content = (String) datalist.get(position).get(DataCentreBean.DATA_CONTENT);
			holder.contextText.setText(content);
			//设置标题和时间
			holder.titleText.setText(datalist.get(position).get(DataCentreBean.DATA_TITLE) + "");
			String time = datalist.get(position).get(DataCentreBean.DATA_TIME) + "";
			SimpleDateFormat format = new SimpleDateFormat(DateUtil.NOCHAR_PATTERNYMD);
			SimpleDateFormat format2 = new SimpleDateFormat(DateUtil.DEFAULT_PATTERN);
			try {
				Date d = format.parse(time);
				String timeFormat = format2.format(d);
				holder.titmeText.setText(timeFormat);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			//删除事件
			holder.guideItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					assPosition = position;
					Log.i("lll", "guideItem" + position);
					Message msg = new Message();
					msg.what = 2;
					msg.arg1 = 1;
					handler.sendMessage(msg);
				}
			});
			//显示文本
			holder.assistantlayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (holder.contextText.getVisibility() == View.GONE) {
						holder.contextText.setVisibility(View.VISIBLE);
					} else {
						holder.contextText.setVisibility(View.GONE);
					}
					Log.i("lll", "显示文本" + position);
					String id = Integer.valueOf(Double.valueOf(datalist.get(position).get(DataCentreBean.DATA_ID).toString()).intValue()).toString();
					//更新数据库 标志为已读
					DataCentreBean.getInstance().update(dbHelper, new String[] { id }, new String[] { "1" });
					datalist.get(position).put(DataCentreBean.DATA_CLICK, "1");
					holder.imageViewRead.setBackgroundResource(R.drawable.already_read);
//						Message msg = new Message();
//						msg.what = 3;
//						msg.arg2=1;
//						handler.sendMessage(msg);
				}
			});
			return convertView;
		}
	}

	//公告
	public class AListViewAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<HashMap<String, Object>> datalist;

		public AListViewAdapter(Context context, List<HashMap<String, Object>> datalist) {
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

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.data_centre_private_item, null);
				holder.guideItem = (Button) convertView.findViewById(R.id.cancel_btn);
				holder.titleText = (TextView) convertView.findViewById(R.id.title);
				holder.titmeText = (TextView) convertView.findViewById(R.id.time);
				holder.assistantlayout = (LinearLayout) convertView.findViewById(R.id.item_show);
				holder.imageViewRead = (ImageView) convertView.findViewById(R.id.img_read);
				holder.contextText = (TextView) convertView.findViewById(R.id.content);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Log.i("lll", "getView" + position);
			//设置已读或未读
			if (datalist.get(position).get(DataCentreBean.DATA_CLICK).equals("0")) {
				holder.imageViewRead.setBackgroundResource(R.drawable.no_read);
			} else {
				holder.imageViewRead.setBackgroundResource(R.drawable.already_read);
			}
			if (holder.contextText.getVisibility() == View.VISIBLE) {
				holder.contextText.setVisibility(View.GONE);
			}
			String content = (String) datalist.get(position).get(DataCentreBean.DATA_CONTENT);
			holder.contextText.setText(content);
			//设置标题和时间
			holder.titleText.setText(datalist.get(position).get(DataCentreBean.DATA_TITLE) + "");
			String time = datalist.get(position).get(DataCentreBean.DATA_TIME) + "";
			SimpleDateFormat format = new SimpleDateFormat(DateUtil.NOCHAR_PATTERNYMD);
			SimpleDateFormat format2 = new SimpleDateFormat(DateUtil.DEFAULT_PATTERN);
			try {
				Date d = format.parse(time);
				String timeFormat = format2.format(d);
				holder.titmeText.setText(timeFormat);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			//删除事件
			holder.guideItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					assPosition = position;
					Log.i("lll", "guideItem" + position);
//						
					Message msg = new Message();
					msg.what = 2;
					msg.arg1 = 2;
					handler.sendMessage(msg);
				}
			});
			//显示文本
			holder.assistantlayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (holder.contextText.getVisibility() == View.GONE) {
						holder.contextText.setVisibility(View.VISIBLE);
					} else {
						holder.contextText.setVisibility(View.GONE);
					}
					Log.i("lll", "显示文本" + position);
					String id = Integer.valueOf(Double.valueOf(datalist.get(position).get(DataCentreBean.DATA_ID).toString()).intValue()).toString();
					//更新数据库 标志为已读
					DataCentreBean.getInstance().update(dbHelper, new String[] { id }, new String[] { "1" });
					datalist.get(position).put(DataCentreBean.DATA_CLICK, "1");
					holder.imageViewRead.setBackgroundResource(R.drawable.already_read);
				}
			});
			return convertView;
		}
	}

	public class ViewHolder {

		public Button guideItem;
		public TextView titleText, titmeText, contextText;
		public LinearLayout assistantlayout;
		public ImageView imageViewRead;
	}

	/**
	 * 获取游戏助理json数据，过期的删除
	 */
	public void getAssistantJsonList() {
		String[] values = { DataCentreBean.RACE_AS };
		assJsonList = DataCentreBean.getInstance().findList(dbHelper, values);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		long str = Long.valueOf(formatter.format(curDate));
		for (int i = 0; i < assJsonList.size(); i++) {
			String json = (String) assJsonList.get(i).get(DataCentreBean.DATA_CONTENT);
			List<HashMap<String, Object>> list = JsonHelper.fromJson(json, new TypeToken<List<HashMap<String, Object>>>() {});
			String time = String.valueOf(list.get(0).get(AssistantBean.AS_TIME));
			if (Long.valueOf(time) - str < 0) {
				String[] valuess = { Integer.valueOf(Double.valueOf(list.get(0).get(AssistantBean.AS_ID).toString()).intValue()).toString() };
				DataCentreBean.getInstance().delete(dbHelper, "id=?", valuess);
			}
		}
		assJsonList = DataCentreBean.getInstance().findList(dbHelper, values);
		assListViewadapter = new AssListViewAdapter(this, assJsonList);
		assListView.setAdapter(assListViewadapter);
	}

	/**
	 * 消息只显示30条
	 */
	public void getPrivateList() {
		String[] valuePrivate = { DataCentreBean.RACE_PR };
		privateList = DataCentreBean.getInstance().findList(dbHelper, valuePrivate);
		if (privateList.size() > 30) {
			for (int i = 0; i < privateList.size() - 30; i++) {
				String[] values = { Integer.valueOf(Double.valueOf(privateList.get(i).get(DataCentreBean.DATA_ID).toString()).intValue()).toString() };
				DataCentreBean.getInstance().delete(dbHelper, "id=?", values);
			}
		}
		privateList = DataCentreBean.getInstance().findList(dbHelper, valuePrivate);
		listViewAdapter = new ListViewAdapter(this, privateList);
		privateListView.setAdapter(listViewAdapter);
	}

	/**
	 * 消息只显示30条
	 */
	public void getGonggaoList() {
		String[] valuePrivate = { DataCentreBean.RACE_SYS };
		announcementList = DataCentreBean.getInstance().findList(dbHelper, valuePrivate);
		if (announcementList.size() > 30) {
			for (int i = 0; i < announcementList.size() - 30; i++) {
				String[] values = { Integer.valueOf(Double.valueOf(announcementList.get(i).get(DataCentreBean.DATA_ID).toString()).intValue()).toString() };
				DataCentreBean.getInstance().delete(dbHelper, "id=?", values);
			}
		}
		announcementList = DataCentreBean.getInstance().findList(dbHelper, valuePrivate);
		alistViewAdapter = new AListViewAdapter(this, announcementList);
		announcementListView.setAdapter(alistViewAdapter);
		setNomessageTvVisibleOrGone(announcementList);
	}

	public void finishSelf() {
		ActivityPool.remove(this);
		this.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
