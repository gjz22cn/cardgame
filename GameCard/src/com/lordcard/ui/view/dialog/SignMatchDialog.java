package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lordcard.adapter.FGPlaceListAdapter;
import com.lordcard.adapter.FHGPlaceListAdapter;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameRoomRuleDetail;
import com.lordcard.entity.GameScoreTradeRank;
import com.lordcard.entity.Room;
import com.lordcard.network.http.HttpRequest;
import com.umeng.analytics.MobclickAgent;

/**
 * 比赛场详情对话框(待删除)
 * 
 * @author Administrator
 */
public class SignMatchDialog extends Dialog implements OnClickListener {

	private Context context;
	private ListView rankListView;
	private TextView content;
	private Button backButton;
	private Button closeBtn;
	private Button signButton;
	private GameRoomRuleDetail gameHallView;
	private boolean isFuhe;// 是否为符合赛制
	private Room room;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private RelativeLayout layout;
	private int position;
	private Handler mHandler;
	private List<GameScoreTradeRank> gstList;

	protected SignMatchDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
	}

	public SignMatchDialog(Context context, int theme, GameRoomRuleDetail gameHallView, boolean isFuhe, Room room, int position, Handler mHandler) {
		super(context, theme);
		this.context = context;
		this.gameHallView = gameHallView;
		this.isFuhe = isFuhe;
		this.room = room;
		this.position = position;
		this.mHandler = mHandler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_match_dialog);
		layout(context);
		layout = (RelativeLayout) findViewById(R.id.exchange_dialog_layout);
		mst.adjustView(layout);
	}

	public void setDismiss() {

	}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	private void layout(final Context context) {
		List<Map<String, String>> testList = gameHallView.getPrizeGoods();
		String title = "";
		if (isFuhe) {
			title = "复合赛详情";
		} else {
			title = "普通赛详情";
		}
		((TextView) findViewById(R.id.dialog_title_tv)).setText(title);
		rankListView = (ListView) findViewById(R.id.sign_rank_list);
		SignRankAdapter valueAdapter = new SignRankAdapter(testList);
		rankListView.setAdapter(valueAdapter);

		content = (TextView) findViewById(R.id.sign_dialog_content);
		content.setText(gameHallView.getRoomDetail());

		backButton = (Button) findViewById(R.id.sign_rank_detail_btn);
		if (isFuhe) {
			backButton.setText("排名");
		} else {
			backButton.setText("返回");
		}
		backButton.setOnClickListener(this);

		signButton = (Button) findViewById(R.id.sign_rank_ok_btn);
		signButton.setOnClickListener(this);

		closeBtn = (Button) findViewById(R.id.dialog_close_btn);
		closeBtn.setOnClickListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		dismiss();
		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sign_rank_detail_btn:
			if (isFuhe) {// 复合赛制
				// 显示排名信息
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {

							String rank = HttpRequest.getFuheRank(room.getCode());
							if (rank != null) {
								gstList = JsonHelper.fromJson(rank, new TypeToken<List<GameScoreTradeRank>>() {
								});
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
						if (gstList != null) {
							Database.currentActivity.runOnUiThread(new Runnable() {
								public void run() {
									MatchRankDialog mrdDialog = new MatchRankDialog(context, R.style.dialog, gstList);
									mrdDialog.show();
								}
							});
						} else {
							DialogUtils.mesTip("获取排名失败！", false);
							dismiss();
						}

					}
				}).start();

			} else {
				dismiss();
			}
			break;
		case R.id.sign_rank_ok_btn:
			Message message = new Message();
			if (isFuhe) {// 复合赛制
				MobclickAgent.onEvent(context, "复活赛排名");
				message.what = FHGPlaceListAdapter.WHAT1;
				Bundle bundle = new Bundle();
				bundle.putInt(FHGPlaceListAdapter.POSITION, position);
				message.setData(bundle);
				mHandler.sendMessage(message);
				dismiss();
			} else {
				message.what = FGPlaceListAdapter.WHAT2;
				Bundle bundle = new Bundle();
				bundle.putInt(FGPlaceListAdapter.POSITION, position);
				message.setData(bundle);
				mHandler.sendMessage(message);
				dismiss();
			}
			break;
		case R.id.dialog_close_btn:
			dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public void dismiss() {
		mst.unRegisterView(layout);
		mst = null;
		super.dismiss();
	}

	/**
	 * 初始化物品栏
	 */
	private class SignRankAdapter extends BaseAdapter {
		private List<Map<String, String>> gifInt;
		private LayoutInflater mInflater;

		public SignRankAdapter(List<Map<String, String>> goodbagList) {
			this.gifInt = goodbagList;
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return gifInt.size();
		}

		public Object getItem(int position) {
			return gifInt.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.match_list_item, null);
			TextView tv = (TextView) convertView.findViewById(R.id.match_text);
			tv.setText(gifInt.get(position).get("rankText") + " : " + gifInt.get(position).get("prizeText"));
			return convertView;
		}

	}
}
