package com.lordcard.ui.payrecord;

import com.zzyddz.shui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.task.GenericTask;
import com.lordcard.common.task.base.TaskParams;
import com.lordcard.common.task.base.TaskResult;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.CacheKey;
import com.lordcard.entity.GameUser;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.lordcard.prerecharge.PrerechargeManager;
import com.lordcard.prerecharge.PrerechargeManager.PrerechargeDialogType;
import com.lordcard.ui.base.BaseActivity;

public class PayRecordActivity extends BaseActivity {

	private ListView listView;
	private int currentItemPressItemPosition = -1;
	public MultiScreenTool mst = MultiScreenTool.singleTonVertical();
	public final static int REFRESH_ORDER_RECORD_LIST = 90013;
	public final static int REMOVE_ORDER_ROCORD = 90014;
	RelativeLayout view;

	// LinearLayout view2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_record);
		// findViewById(R.id.mm_pay_layout).setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.mm_bg));
		listView = (ListView) findViewById(R.id.pay_record_listview);
		// listView.setCacheColorHint(0);
		view = (RelativeLayout) findViewById(R.id.mm_pay_layout);
		// view2 = (LinearLayout) findViewById(R.id.pay_middle_layout);
		mst.adjustView(view);
		// mst.adjustView(view2);
		findViewById(R.id.mm_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finishSelf();
			}
		});
		goRecord();
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			currentItemPressItemPosition = position;
			RecordAdapter recordAdapter = (RecordAdapter) listView.getAdapter();
			@SuppressWarnings("unchecked")
			Map<String, String> payRecordOrderInfo = (Map<String, String>) recordAdapter.getItem(position);
			if (null == payRecordOrderInfo)
				return;
			if (payRecordOrderInfo.get(PayRecordUtil.ORDER_TYPE).equalsIgnoreCase(PayRecordUtil.OrderType.Order_prepay.toString()) && payRecordOrderInfo.get(PayRecordUtil.PAY_STATUS).equalsIgnoreCase(PayRecordUtil.RecordStatus.Record_freeze.toString())) {
				PrerechargeManager.createPrerechargeDialog(PrerechargeDialogType.Dialog_record, PayRecordActivity.this, recordAdapter.getPayRecordOrder(position), mHandler, 0).show();
			}
		}
	};
	@SuppressLint("HandlerLeak") private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			int msgId = msg.what;
			switch (msgId) {
				case REFRESH_ORDER_RECORD_LIST:
					refreshList();
					break;
				case REMOVE_ORDER_ROCORD:
					removeListItem();
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		};
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 重写返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			try {
				finishSelf();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 查询充值记录
	 * @param loginToken
	 */
	public void goRecord() {
		GenericTask vacPayTask = new VacPayTask();
		vacPayTask.setFeedback(feedback);
		vacPayTask.execute();
		taskManager.addTask(vacPayTask);
	}

	/**刷新订单记录ListView**/
	public void refreshList() {
		RecordAdapter recordAdapter = (RecordAdapter) listView.getAdapter();
		recordAdapter.notifyDataSetChanged();
	}

	/**删除订单记录**/
	public void removeListItem() {
		if (currentItemPressItemPosition == -1)
			return;
		RecordAdapter recordAdapter = (RecordAdapter) listView.getAdapter();
		recordAdapter.removeDataRecord(currentItemPressItemPosition);
	}

	private class VacPayTask extends GenericTask {

		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				// 先提loginToken
				GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("loginToken",cacheUser.getLoginToken());
				// 后台生成list
				String resultJson = HttpUtils.post(HttpURL.PAY_LOG_URL, paramMap);
				if (HttpRequest.FAIL_STATE.equals(resultJson)) {
					DialogUtils.mesTip("查询记录失败", true);
					return null;
				} else {
					if (null == resultJson || resultJson.trim().equals("")) {
						DialogUtils.toastTip("无充值记录");
					} else {
						final List<PayRecordOrder> payRecordOrders = JsonHelper.fromJson(resultJson, new TypeToken<List<PayRecordOrder>>() {});
						runOnUiThread(new Runnable() {

							public void run() {
								RecordAdapter adapter = new RecordAdapter(payRecordOrders);
								listView.setAdapter(adapter);
								listView.setOnItemClickListener(onItemClickListener);
							}
						});
					}
				}
			} catch (Exception e) {
				DialogUtils.mesTip("查询记录失败", true);
				return TaskResult.FAILED;
			}
			return TaskResult.OK;
		}
	}

	private class RecordAdapter extends BaseAdapter {

		private List<Map<String, String>> payRecordOrdersInfo;
		private List<PayRecordOrder> payRecordOrders;
		private LayoutInflater mInflater;

		public RecordAdapter(List<PayRecordOrder> list) {
			if (list == null) {
				list = new ArrayList<PayRecordOrder>();
			}
			this.payRecordOrders = list;
			this.mInflater = LayoutInflater.from(PayRecordActivity.this);
			payRecordOrdersInfo = PayRecordUtil.listRecord(list);
			payRecordOrders = list;
		}

		/**remove data at position**/
		public void removeDataRecord(int position) {
			/**移除记录**/
			payRecordOrdersInfo.remove(position);
			payRecordOrders.remove(position);
			/**更新列表**/
			notifyDataSetChanged();
		}

		/**获取订单记录信息**/
		public PayRecordOrder getPayRecordOrder(int position) {
			return payRecordOrders.get(position);
		}

		@Override
		public int getCount() {
			return payRecordOrdersInfo.size();
		}

		@Override
		public Object getItem(int position) {
			return payRecordOrdersInfo.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mViewHolder;
			if (null == convertView) {
				mViewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.pay_record_listview_item, null);
				mViewHolder.t1 = (TextView) convertView.findViewById(R.id.record_data);
				mViewHolder.t2 = (TextView) convertView.findViewById(R.id.record_money);
				mViewHolder.t3 = (TextView) convertView.findViewById(R.id.record_zhidou);
				mViewHolder.status = (ImageView) convertView.findViewById(R.id.record_statu);
				convertView.setTag(mViewHolder);
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}
			mViewHolder.t1.setText(payRecordOrdersInfo.get(position).get(PayRecordUtil.PAY_DATE));
			mViewHolder.t2.setText(String.valueOf(payRecordOrdersInfo.get(position).get(PayRecordUtil.MONNEY)));
			mViewHolder.t3.setText(String.valueOf(payRecordOrdersInfo.get(position).get(PayRecordUtil.BEANS)));
			if (payRecordOrdersInfo.get(position).get(PayRecordUtil.PAY_STATUS).equals(PayRecordUtil.RecordStatus.Record_charge.toString())) {
				(convertView.findViewById(R.id.reocord_item)).setBackgroundResource(R.drawable.pre_recharge_item_green_bg);
				mViewHolder.status.setBackgroundDrawable(ImageUtil.getDrawableResId(R.drawable.charge_status_complete, false, false));
			} else {
				(convertView.findViewById(R.id.reocord_item)).setBackgroundResource(R.drawable.pre_recharge_item_bg);
				mViewHolder.status.setBackgroundDrawable(ImageUtil.getDrawableResId(R.drawable.charge_status_freeze, false, false));
			}
			convertView.findViewById(R.id.reocord_item).setPadding(10, 0, 10, 0);
			return convertView;
		}
	}

	class ViewHolder {

		TextView t1, t2, t3;
		ImageView status;
	}
}
