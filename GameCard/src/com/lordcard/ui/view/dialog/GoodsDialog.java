package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.lordcard.common.util.ImageUtil;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;

/**
 * 物品弹出框
 * 
 * @author Administrator
 * 
 */
public class GoodsDialog extends Dialog implements OnClickListener {

	private Context context;
	// private Button okBtn, freeBtn, czBtn;
	private GridView gridView;
	private GoodsAdapter goodsAdapter;

	protected GoodsDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
		// layout(context);
	}

	//	public GoodsDialog(Context context, int themer) {
	//		super(context, themer);
	//		this.context = context;
	//	}

	public GoodsDialog(Context context) {
		super(context);
		//		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goods_dialog);
		layout(context);
	}

	public void setDismiss() {
		dismiss();
	}

	/**
	 * 布局
	 */
	private void layout(final Context context) {
		Button cancel = (Button) findViewById(R.id.goods_cancel);
		gridView = (GridView) findViewById(R.id.gridviews);
		if (getImageNames() != null) {
			goodsAdapter = new GoodsAdapter(getImageNames());
			gridView.setAdapter(goodsAdapter);
		}
		cancel.setOnClickListener(this);
	}

	private String[] getImageNames() {
		SharedPreferences sharedData = context.getSharedPreferences(Constant.GAME_ACTIVITE, Context.MODE_PRIVATE);
		String imageStr = sharedData.getString(LotteryDialog.SAVE_LOTTERY, "");
		if (imageStr.trim().length() > 0) {
			String[] images = imageStr.split(",");
			return images;
		} else {
			return null;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.goods_cancel:
			setDismiss();
			break;
		default:
			break;
		}
	}

	private class GoodsAdapter extends BaseAdapter {
		private String[] goods;
		private LayoutInflater mInflater;

		public GoodsAdapter(String[] gifs) {
			this.goods = gifs;
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return goods.length;
		}

		public Object getItem(int position) {
			return goods[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.goods_item, null);
			ImageView iv = (ImageView) convertView.findViewById(R.id.goods_item);
			try {
				// iv.setBackgroundDrawable(ImageUtil.getResDrawable(context,
				// goods[position]));
				iv.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.lot_wp,true));
			} catch (Exception e) {
				// e.printStackTrace();
			}
			return convertView;
		}

	}

	@Override
	public void dismiss() {
		super.dismiss();
		goodsAdapter = null;
	}

	public static void finishAcitivity() {
		if (Database.currentActivity != null) {
			Database.currentActivity.finish();
		}
	}

}
