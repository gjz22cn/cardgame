package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.google.gson.reflect.TypeToken;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.ImageUtil.ImageCallback;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.GoodsDetails;
import com.lordcard.entity.GoodsType;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.lordcard.ui.StoveActivity;

/**
 *  物品鉴定对话框
 * @author Administrator
 */
public class EnvalueDialog extends Dialog implements OnClickListener {

	private Context context;
	private LinearLayout mainLayout;
	private Integer Typeid;
	private Button stoveButton, closeBtn;
	private GridView animalimgList;
	private GoodsValuesAdapter goodsValuesAdapter;
	// 生肖头像
	private ImageView headImage;
	// 生肖名称
	private TextView headTextView;
	// 生肖内容
	private TextView contentView;
	// 获取渠道
	private ListView evaluesView;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private TextAdapter textAdapter;

	public EnvalueDialog(Context context) {
		super(context, R.style.dialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goods_evaluate);
		mainLayout = (LinearLayout) findViewById(R.id.envalues_list_layout);
		mainLayout.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.photo_bg, false));
		mst.adjustView(mainLayout);
		layout(context);
	}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	private void layout(final Context context) {
		closeBtn = (Button) findViewById(R.id.evalues_close);
		closeBtn.setOnClickListener(this);
		stoveButton = (Button) findViewById(R.id.stove_btn);
		stoveButton.setOnClickListener(this);
		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		if (cacheUser != null && cacheUser.getIq() < 3) {
			stoveButton.setVisibility(View.INVISIBLE);
		}
		// 生肖头像
		headImage = (ImageView) findViewById(R.id.shengxiao_head);
		// 生肖名称
		headTextView = (TextView) findViewById(R.id.shengxiao_text);
		// 生肖内容
		contentView = (TextView) findViewById(R.id.shengxiao_content);
		// 获取渠道
		evaluesView = (ListView) findViewById(R.id.evalues_list_view);
		evaluesView.setEnabled(false);
		animalimgList = (GridView) findViewById(R.id.valuesgrid);
		animalimgList.setSelector(new ColorDrawable(Color.TRANSPARENT));
		if (Database.GOODS_EVALUE_LIST != null && Database.GOODS_EVALUE_LIST.size() > 0) {
			initView();
		} else {
			new Thread() {

				public void run() {
					try {
						String result = HttpUtils.post(HttpURL.EVALUES_IMG_URL, null, true);
						if (!TextUtils.isEmpty(result)) {
							Database.GOODS_EVALUE_LIST = JsonHelper.fromJson(result, new TypeToken<List<GoodsType>>() {});
							Database.currentActivity.runOnUiThread(new Runnable() {

								public void run() {
									if (Database.GOODS_EVALUE_LIST != null && Database.GOODS_EVALUE_LIST.size() > 0) {
										initView();
									}
								}
							});
						}
					} catch (Exception e) {}
				}
			}.start();
		}
		animalimgList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, final int posision, long arg3) {
				Typeid = Database.GOODS_EVALUE_LIST.get(posision).getCompositeType();
				headTextView.setText(Database.GOODS_EVALUE_LIST.get(posision).getName());
				contentView.setText(Database.GOODS_EVALUE_LIST.get(posision).getTitle());
				Database.GOODS_EVALUE_DETAIL = JsonHelper.fromJson(Database.GOODS_EVALUE_LIST.get(posision).getDescription(), new TypeToken<List<GoodsDetails>>() {});
				List<GoodsDetails> textList = Database.GOODS_EVALUE_DETAIL;
				List<String> textList0 = new ArrayList<String>();
				if (textList != null && textList.size() > 0) {
					for (int i = 0; i < textList.size(); i++) {
						textList0.add(textList.get(i).getText());
					}
					//					TextAdapter textAdapter = new TextAdapter(textList);
					textAdapter = new TextAdapter(textList);
					evaluesView.setAdapter(textAdapter);
				}
				ImageUtil.setImg(HttpURL.URL_PIC_ALL + Database.GOODS_EVALUE_LIST.get(posision).getPicPath(), headImage, new ImageCallback() {

					public void imageLoaded(Bitmap bitmap, ImageView view) {
						view.setScaleType(ScaleType.FIT_XY);
						view.setImageBitmap(bitmap);
					}
				});
			}
		});
	}

	/**
	 * 初始化GradView
	 */
	private void initView() {
		contentView.setText(Database.GOODS_EVALUE_LIST.get(0).getTitle());
		headTextView.setText(Database.GOODS_EVALUE_LIST.get(0).getName());
		Typeid = Database.GOODS_EVALUE_LIST.get(0).getCompositeType();
		ImageUtil.setImg(HttpURL.URL_PIC_ALL + Database.GOODS_EVALUE_LIST.get(0).getPicPath(), headImage, new ImageCallback() {

			public void imageLoaded(Bitmap bitmap, ImageView view) {
				view.setScaleType(ScaleType.FIT_XY);
				view.setImageBitmap(bitmap);
			}
		});
		Database.GOODS_EVALUE_DETAIL = JsonHelper.fromJson(Database.GOODS_EVALUE_LIST.get(0).getDescription(), new TypeToken<List<GoodsDetails>>() {});
		List<GoodsDetails> textList = Database.GOODS_EVALUE_DETAIL;
		List<String> textList1 = new ArrayList<String>();
		for (int i = 0; i < textList.size(); i++) {
			textList1.add(textList.get(i).getText());
		}
		//		TextAdapter textAdapter = new TextAdapter(textList);
		textAdapter = new TextAdapter(textList);
		evaluesView.setAdapter(textAdapter);
		int space = 8;
		int numColumn = 97;
		int size = Database.GOODS_EVALUE_LIST.size();
		LayoutParams linearParams = (LinearLayout.LayoutParams) animalimgList.getLayoutParams(); // 取控件mGrid当前的布局参数
		linearParams.width = size * (mst.adjustXIgnoreDensity(numColumn + space)) + 20;
		animalimgList.setLayoutParams(linearParams);
		animalimgList.setNumColumns(size);
		animalimgList.setColumnWidth(mst.adjustXIgnoreDensity(numColumn));
		animalimgList.setHorizontalSpacing(mst.adjustXIgnoreDensity(space));
		animalimgList.setStretchMode(GridView.NO_STRETCH);
		goodsValuesAdapter = new GoodsValuesAdapter(Database.GOODS_EVALUE_LIST);
		animalimgList.setAdapter(goodsValuesAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.stove_btn:
				MobclickAgent.onEvent(context, "宝鉴合成");
				int page = 2;//默认为数码合成
				if (null != Typeid) {
					if (Typeid.intValue() == 1 || Typeid.intValue() == 4) {
						page = 0;
					} else if (Typeid.intValue() == 2) {
						page = 0;
					} else if (Typeid.intValue() == 3) {
						page = 2;
					}
					//mst.unRegisterView(mainLayout);
					Bundle bundle = new Bundle();
					bundle.putInt("page", page);
					Intent stoveIntent = new Intent();
					stoveIntent.setClass(context, StoveActivity.class);
					stoveIntent.putExtras(bundle);
					context.startActivity(stoveIntent);
				}
				break;
			case R.id.evalues_close:
				dismiss();
				break;
			default:
				break;
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		mst = null;
		goodsValuesAdapter = null;
		textAdapter = null;
	}

	/**
	 * 初始化物品宝鉴
	 */
	private class GoodsValuesAdapter extends BaseAdapter {

		private List<GoodsType> gifInt;
		private LayoutInflater mInflater;

		public GoodsValuesAdapter(List<GoodsType> goodbagList) {
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
			convertView = mInflater.inflate(R.layout.goods_gif_item, null);
			final ImageView iv = (ImageView) convertView.findViewById(R.id.goodsview);
			ImageUtil.setImg(HttpURL.URL_PIC_ALL + gifInt.get(position).getPicPath(), iv, new ImageCallback() {

				public void imageLoaded(final Bitmap bitmap, final ImageView view) {
					view.setImageBitmap(bitmap);
				}
			});
			return convertView;
		}
	}

	/**
	 * 初始化物品宝鉴
	 */
	private class TextAdapter extends BaseAdapter {

		private List<GoodsDetails> gifInt;
		private LayoutInflater mInflater;

		public TextAdapter(List<GoodsDetails> goodbagList) {
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
			ViewHolder mViewHolder;
			if (null == convertView) {
				mViewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.text_list_item, null);
				mViewHolder.text = (TextView) convertView.findViewById(R.id.evalues_text);
				convertView.setTag(mViewHolder);
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}
			mViewHolder.text.setText(gifInt.get(position).getText());
			return convertView;
		}

		class ViewHolder {

			private TextView text;// 最高奖励(普通赛制)
		}
	}
}
