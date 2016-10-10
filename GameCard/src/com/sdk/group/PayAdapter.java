package com.sdk.group;

import com.zzyddz.shui.R;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.constant.Constant;
import com.sdk.tianyi.util.TYPayUtil;
import com.sdk.util.vo.PayPoint;
import com.sdk.vac.util.VACPayUtil;


public class PayAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<PayPoint> pointlist;
	private String paySite;

	public PayAdapter(Context context, List<PayPoint> pointlist,String paySite) {
		this.mInflater = LayoutInflater.from(context);
		this.pointlist = pointlist;
		this.paySite = paySite;
	}

	@Override
	public int getCount() {
		return pointlist.size();
	}

	@Override
	public Object getItem(int position) {
		return pointlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.pay_ab_listview_item, null);
			holder.guideItem = (Button) convertView.findViewById(R.id.ab_item_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		PayPoint point = pointlist.get(position);
		holder.guideItem.setText(point.getName()+"="+point.getMoney()+"元");
		holder.guideItem.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				DialogUtils.mesToastTip("支付组件加载中，请稍候...");
				String simType = ActivityUtils.getSimType();
				if(Constant.SIM_UNICOM.equals(simType)){
					VACPayUtil.goPay(pointlist.get(position),paySite);
				}else if(Constant.SIM_TELE.equals(simType)){
					TYPayUtil.goPay(pointlist.get(position),paySite);
				}
				
			}
		});
		return convertView;
	}
	
	public class ViewHolder {
		public Button guideItem;
	}
}



