package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.Goods;
import com.lordcard.entity.GoodsPart;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;


public class StoveDialog extends Dialog implements OnClickListener {

	private Context context;
	private List<Goods> userCountGoods;
	private Integer goodscount;
	//	private RelativeLayout mainLayout;
	private Button stoveCloseBtn;
	private String nameStr;
	private String typeid;
	private Integer type;
	private List<Goods> stove_check_count; // 合成物品的数量信息
	private Button stoveButton;
	private List<GoodsPart> goodstuff;//材料数据
	private ImageView stoveResultText;
	private ListView stoveStuffList;
	private LinearLayout zhishangLl;
	private TextView zhishangNowTv,zhishangNeedTv;//当前自己等级，合成此物需要等级
	private StoveStuffAdapter stoveStuffAdapter;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private RelativeLayout layout;

	protected StoveDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
	}

	public StoveDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public StoveDialog(Context context, int dialog, String goodsName, Integer type, String goodsid, Integer goodscount, List<GoodsPart> goods) {
		super(context, dialog);
		goodstuff=new ArrayList<GoodsPart>();
		this.context = context;
		if(null != goods){
			for(int i=0;i<goods.size();i++){
				goodstuff.add(goods.get(i));
			}
		}
		this.nameStr = goodsName;
		this.typeid = goodsid;
		this.goodscount = goodscount;
		this.type = type;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stove_dialog);
		//		mainLayout = (RelativeLayout) findViewById(R.id.stove_bg);
		// mainLayout.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.liaotian_bj_1));
		layout(context);
		layout = (RelativeLayout) findViewById(R.id.stove_dialog_layout);
		mst.adjustView(layout);
	}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	private void layout(final Context context) {
		((TextView) findViewById(R.id.dialog_title_tv)).setText(nameStr);
		stoveStuffList = (ListView) findViewById(R.id.stove_stuff_list);
		stoveButton = (Button) findViewById(R.id.stove_stuff_btn);
		stoveResultText = (ImageView) findViewById(R.id.stove_result_text);
		zhishangLl=(LinearLayout) findViewById(R.id.stove_zhishang_layout);
		zhishangNowTv= (TextView) findViewById(R.id.sd_zhishang_now);
		zhishangNeedTv= (TextView) findViewById(R.id.sd_zhishang_need);
		stoveCloseBtn = (Button) findViewById(R.id.dialog_close_btn);
		stoveCloseBtn.setOnClickListener(this);

		// 设置监听器
		stoveButton.setOnClickListener(this);
		stoveCloseBtn.setOnClickListener(this);

		new Thread() {
			public void run() {
				GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("loginToken",cacheUser.getLoginToken());
				paramMap.put("goodsId", typeid);
				paramMap.put("count", String.valueOf(goodscount));
				paramMap.put("type", String.valueOf(type));
				try {
					String result = HttpUtils.post(HttpURL.STOVE_CHECK_URL, paramMap);
					stove_check_count = JsonHelper.fromJson(result, new TypeToken<List<Goods>>() {
					});
					userCountGoods = stove_check_count;
					Database.currentActivity.runOnUiThread(new Runnable() {
						public void run() {
							Boolean isEnough = false;
							if (goodstuff != null && userCountGoods != null) {
								isEnough = true;
								for (int i = 0; i < goodstuff.size(); i++) {
									if (goodstuff.size() > i && userCountGoods.size() > i) {
										if (goodstuff.get(i).getFromCount().intValue() > userCountGoods.get(i).getCouponNum()) {
											isEnough = false;
										}
									}
								}
							}
							//查找等级在物品列表中的位置
							int indext=-1;
							FOR:
							for (int i = 0; i < goodstuff.size(); i++) {
								if (!TextUtils.isEmpty(goodstuff.get(i).getFromName()) &&"等级".equals(goodstuff.get(i).getFromName().trim())) {
									indext=i;
									break FOR;
								}
							}
							//将等级的值提取出来手动设置到对话框，并且删除对应列表中的记录，以防止物品列表再次显示
							if(indext != -1){
								if(indext<userCountGoods.size()){
									zhishangNowTv.setText(""+userCountGoods.get(indext).getCouponNum());
								}
								zhishangNeedTv.setText(""+goodstuff.get(indext).getFromCount().toString());
								zhishangLl.setVisibility(View.VISIBLE);
								userCountGoods.remove(indext);
								goodstuff.remove(indext);
							}
							if (isEnough) {

								stoveResultText.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.enough,true));
								stoveButton.setVisibility(View.VISIBLE);

							} else {

								stoveResultText.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.unenough,true));
								stoveButton.setVisibility(View.INVISIBLE);

							}

							stoveStuffAdapter = new StoveStuffAdapter(context, goodstuff, userCountGoods);
							stoveStuffList.setAdapter(stoveStuffAdapter);
						}
					});

				} catch (Exception e) {
				}
			}
		}.start();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.stove_stuff_btn:
			//MobclickAgent.onEvent(context, "合成" + nameStr);
			GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			stove(typeid,cacheUser.getLoginToken());
			break;
		case R.id.dialog_close_btn:
			dismiss();
			break;
		default:
			break;
		}
	}

	private void stove(String goodsid, String longinToken) {
		new Thread() {
			public void run() {
				GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("loginToken",cacheUser.getLoginToken());
				paramMap.put("goodsId", typeid);
				paramMap.put("count", String.valueOf(goodscount));
				paramMap.put("type", String.valueOf(type));
				try {
					String result = HttpUtils.post(HttpURL.STOVE_RESULT_URL, paramMap);
					Looper.prepare();
					if (result.equals("0")) {
						Database.currentActivity.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(context, "恭喜您成功合成", Toast.LENGTH_SHORT).show();
								dismiss();
							}
						});
					}
					if (result.equals("1")) {
						Database.currentActivity.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(context, "合成失败，请确认你的数据正确", Toast.LENGTH_SHORT).show();
								dismiss();
							}
						});

					}

				} catch (Exception e) {
				}
			}
		}.start();

	}

	@Override
	public void dismiss() {
		mst.unRegisterView(layout);
		//		Database.StoveDialogShow = false;
		if(null !=stoveStuffAdapter){
			stoveStuffAdapter.setDatalist(null);
			stoveStuffAdapter.setUserCount(null);
			stoveStuffAdapter = null;
		}
		super.dismiss();
	}

	private class StoveStuffAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<GoodsPart> datalist;
		private List<Goods> userCount;

		public StoveStuffAdapter(Context context, List<GoodsPart> datalist, List<Goods> userCountGoods) {
			this.mInflater = LayoutInflater.from(context);
			this.datalist = datalist;
			this.userCount = userCountGoods;
		}

		@Override
		public int getCount() {
			return datalist.size();
		}

		@Override
		public Object getItem(int position) {
			return datalist.get(position);
		}

		public void setDatalist(List<GoodsPart> datalist) {
			this.datalist = datalist;
		}

		public void setUserCount(List<Goods> userCount) {
			this.userCount = userCount;
		}

		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.stove_stuff_item, null);
				holder.stoveGoodsName = (TextView) convertView.findViewById(R.id.stove_stuff_name);
				holder.stoveStuffCount = (TextView) convertView.findViewById(R.id.stove_stuff_count);
				holder.stoveStuffNeed = (TextView) convertView.findViewById(R.id.stove_stuff_need);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.stoveGoodsName.setText(datalist.get(position).getFromName());
			if (userCount != null && userCount.size() > 0) {
				holder.stoveStuffCount.setText(userCount.get(position).getCouponNum() + "");// 此处需要跟后台商量
			}
			holder.stoveStuffNeed.setText(datalist.get(position).getFromCount().toString());

			return convertView;
		}

		public class ViewHolder {
			public TextView stoveGoodsName, stoveStuffCount, stoveStuffNeed;
		}
	}

}
