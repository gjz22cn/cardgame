package com.sdk.ssw.pay.ui;

import com.zzyddz.shui.R;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lordcard.common.util.DialogUtils;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUserGoods;
import com.lordcard.net.http.HttpRequest;
import com.lordcard.ui.base.BaseActivity;
import com.lordcard.ui.payrecord.PayRecordActivity;
import com.sdk.ssw.SSW;
import com.sdk.ssw.pay.SSWConstant;
import com.sdk.ssw.uti.SSWPayUtil;

/**
 * @author Administrator
 * 
 */
public class SSWPayActivity extends BaseActivity implements OnClickListener {

	private TextView zhidou,txt;
	private ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_wp);
		zhidou = (TextView) findViewById(R.id.gen_zhi_dou);
		findViewById(R.id.gen_back).setOnClickListener(this);
		findViewById(R.id.gen_record).setOnClickListener(this);
		listView=(ListView) findViewById(R.id.weipai);
		listView.setAdapter(new WPPayAdapter(this, SSWConstant.SSW_LIST));
		mst.adjustView(findViewById(R.id.mm_pay_layout));
		txt=(TextView) findViewById(R.id.TextView1);
		txt.setText("随手玩支付");
		
	}
	@Override
	protected void onStart() {
		super.onStart();
		new Thread() {
			public void run() {
				final GameUserGoods gameUserGoods = HttpRequest.getGameUserGoods();
				if (gameUserGoods != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							long point = gameUserGoods.getBean();
							if (point > 10000) {
								point = point / 10000;
								zhidou.setText(String.valueOf(point) + "W");
							} else {
								zhidou.setText(String.valueOf(Database.USER.getBean()));
							}
						}
					});
				} else {
					DialogUtils.toastTip("获取数据失败");
				}
			};
		}.start();
	}

public class WPPayAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private List<SSW> datalist;

	public WPPayAdapter(Context context, List<SSW> datalist) {
		this.mInflater = LayoutInflater.from(context);
		this.datalist = datalist;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datalist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datalist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=mInflater.inflate(R.layout.pay_wp_listview_item, null);
			holder.guideItem=(Button)convertView.findViewById(R.id.wp_item_btn);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
			holder.guideItem.setText(datalist.get(position).getOrderDesc());
			holder.guideItem.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					new SSWPayUtil().SSWPPay(Database.currentActivity,datalist.get(position).getPrice().toString());
				}
			});
		return convertView;
	}
	
} 
public class ViewHolder {
	public Button guideItem;
}


	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.gen_back:
			finish();
			break;
		case R.id.gen_record:
			Intent intent = new Intent();
			intent.setClass(SSWPayActivity.this, PayRecordActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}
}