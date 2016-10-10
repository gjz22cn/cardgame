package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameScoreTradeRank;
import com.lordcard.entity.GameUser;
import com.lordcard.network.http.GameCache;
import com.lordcard.ui.view.GameWaitView;

/**
 * 比赛场排名对话框
 * 
 * @author Administrator
 */
public class MatchRankDialog extends Dialog implements OnClickListener {

	private Context context;
	private ListView rankListView;
	private Button closeBtn;
	private List<GameScoreTradeRank> gstList;
	private String code;
	private boolean isFuhe;// 是否符合赛制
	// 适应多屏幕的工具
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();

	public MatchRankDialog(Context context, int theme, String code) {
		super(context, theme);
		this.context = context;
		this.code = code;
	}

	public MatchRankDialog(Context context, int theme, List<GameScoreTradeRank> gstList) {
		super(context, theme);
		this.context = context;
		this.gstList = gstList;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.match_rank_dialog);
		layout(context);
	}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	private void layout(final Context context) {
		rankListView = (ListView) findViewById(R.id.match_rank_list);
		closeBtn = (Button) findViewById(R.id.rank_close_btn);
		closeBtn.setOnClickListener(this);
		TextView num = (TextView) findViewById(R.id.mrd_item_num);
		TextView name = (TextView) findViewById(R.id.mrd_item_name);
		TextView zhidou = (TextView) findViewById(R.id.mrd_item_zhidou);
		ImageView img = (ImageView) findViewById(R.id.mrd_match_ranking);
		int index = -1;
		for (int i = 0, count = gstList.size(); i < count; i++) {
			GameScoreTradeRank gctRank = gstList.get(i);
			GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			if (gctRank.getAccount().equals(cacheUser.getAccount())) {
				num.setText(gctRank.getRank());
				name.setText(gctRank.getNickName());
				zhidou.setText(gctRank.getScore());
				num.setTextColor(context.getResources().getColor(R.color.yellow));
				name.setTextColor(context.getResources().getColor(R.color.yellow));
				zhidou.setTextColor(context.getResources().getColor(R.color.yellow));
				if (Integer.valueOf(gctRank.getRank()).intValue() > 3) {
					img.setVisibility(View.INVISIBLE);
				} else if (Integer.valueOf(gctRank.getRank()).intValue() == -1) {
					num.setText("未报名");
					img.setVisibility(View.INVISIBLE);
				} else {
					img.setVisibility(View.VISIBLE);
				}
				index = i;
			}
		}
		if (-1 != index) {
			gstList.remove(index);
		}
		rankListView.setAdapter(new SignRankAdapter(context, gstList));
		mst.adjustView(findViewById(R.id.mr_dialog_layout));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		dismiss();
		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rank_close_btn:
				dismiss();
				break;
			default:
				break;
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		mst.unRegisterView(findViewById(R.id.mr_dialog_layout));
	}

	private class SignRankAdapter extends BaseAdapter {

		private List<GameScoreTradeRank> gstList;
		private LayoutInflater mInflater;
		private Context context;

		public SignRankAdapter(Context context, List<GameScoreTradeRank> gstList) {
			this.gstList = gstList;
			this.mInflater = LayoutInflater.from(context);
			this.context = context;
		}

		public int getCount() {
			return gstList.size();
		}

		public Object getItem(int position) {
			return gstList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.match_rank_dialog_item, null);
				holder.num = (TextView) convertView.findViewById(R.id.mk_item_num);
				holder.name = (TextView) convertView.findViewById(R.id.mk_item_name);
				holder.zhidou = (TextView) convertView.findViewById(R.id.mk_item_zhidou);
				holder.img = (ImageView) convertView.findViewById(R.id.match_ranking);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			GameScoreTradeRank gctRank = gstList.get(position);
			Log.d("GameScoreTradeRank", "排名  " + position + " : 账号 " + gctRank.getAccount() + "     昵称 " + gctRank.getNickName() + "    排名  " + gctRank.getRank() + "     积分  " + gctRank.getScore());
			GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			if (!gctRank.getAccount().equals(cacheUser.getAccount())) {
				holder.num.setText(gctRank.getRank());
				holder.name.setText(gctRank.getNickName());
				holder.zhidou.setText(gctRank.getScore());
			}
			if (Integer.valueOf(gctRank.getRank()).intValue() > 3) {
				holder.img.setVisibility(View.INVISIBLE);
			} else {
				holder.img.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

		class ViewHolder {

			TextView num; // 排名
			TextView name; // 昵称
			TextView zhidou; //金豆
			ImageView img;
		}
	}
}
