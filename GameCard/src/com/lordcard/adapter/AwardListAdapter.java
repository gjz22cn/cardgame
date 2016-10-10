package com.lordcard.adapter;


import com.zzyddz.shui.R;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lordcard.entity.AwardVo;

/**
 * 奖励方案Adapter
 * 
 * @author Administrator
 */
public class AwardListAdapter extends BaseAdapter {

//	private Context context = null;
	private LayoutInflater layoutInflater = null;
	private List<AwardVo> awardList;

	public AwardListAdapter(Context context, List<AwardVo> awardList) {

//		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.awardList = awardList;
	}

	@Override
	public int getCount() {
		return awardList.size();
	}

	@Override
	public Object getItem(int position) {
		return awardList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		private TextView noTv;// 排名
		private TextView nameTv;// 昵称
		private TextView integralTv;// 积分
		private TextView prizeTv;// 奖品
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.award_list_item, null);
			holder = new ViewHolder();
			holder.noTv = (TextView) convertView.findViewById(R.id.ali_no_tv);
			holder.nameTv = (TextView) convertView.findViewById(R.id.ali_name_tv);
			holder.integralTv = (TextView) convertView.findViewById(R.id.ali_integral_tv);
			holder.prizeTv = (TextView) convertView.findViewById(R.id.ali_prize_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AwardVo awardVo = awardList.get(position);
		holder.noTv.setText("" + awardVo.getNo());
		holder.nameTv.setText("" + awardVo.getName());
		holder.integralTv.setText("" + awardVo.getIntegral());
		holder.prizeTv.setText("" + awardVo.getPrize());
		Log.i("convertView", "" + awardVo.getNo() + "" + awardVo.getName() + "" + awardVo.getIntegral() + "" + awardVo.getPrize());
		return convertView;
	}
}
