package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.ImageUtil.ImageCallback;
import com.lordcard.common.util.ImageUtil.ImageGroupCallback;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameIQ;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.Grade;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.network.http.HttpURL;

public class IqGradeDialog extends Dialog {

	private Context context;
	private ListView mListView;
	private RelativeLayout mainLayout;
	private ScrollAdapter mScrollAdapter = null;
	private List<Grade> data;
	private Button backBtn;
	private int myGrade = -1;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private Handler mHandler;
	private static int positions = -1;
	private ProgressBar zhiLiPb;// 经验进度
	private TextView zhiShangTv, zhiliTv;// 等级值,经验进度值
	private int inIq = -1;
	private static final int DP_WIDTH = 20, DP_HEIGHT = 24;

	public IqGradeDialog(Context context, int myGrade) {
		super(context, R.style.dialog);
		this.context = context;
		this.myGrade = myGrade;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.iq_grade_dialog);
		mainLayout = (RelativeLayout) findViewById(R.id.scorll_ll);
		mst.adjustView(mainLayout);
		mListView = (ListView) findViewById(R.id.scorll_list);
		backBtn = (Button) findViewById(R.id.scorll_back);
		zhiLiPb = (ProgressBar) findViewById(R.id.igd_iq_pg);
		zhiShangTv = (TextView) findViewById(R.id.igd_zhishang_tv2);
		zhiliTv = (TextView) findViewById(R.id.igd_iq_pg_tv);
		GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
		if (null != cacheUser) {
			zhiShangTv.setText("" + cacheUser.getIq());
			zhiLiPb.setMax(100);
			float step = cacheUser.getNextIntellect() / 100f;
			zhiLiPb.setProgress(Math.round(cacheUser.getIntellect() / step));
			zhiliTv.setText("" + cacheUser.getIntellect() + "/" + cacheUser.getNextIntellect());
		}
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		if (null == Database.IQ_DATA || Database.IQ_DATA.size() == 0) {
			new GameIqAcync().execute();
		} else {
			setListView();
		}
	}

	/**
	 * 初始化listView
	 */
	private void setListView() {
		mScrollAdapter = new ScrollAdapter(context, Database.IQ_DATA);
		mListView.setAdapter(mScrollAdapter);
		for (int i = 0; i < Database.IQ_DATA.size(); i++) {
			GameIQ mGrade = Database.IQ_DATA.get(i);
			if (null != mGrade && inIq == mGrade.getIq()) {// 当前自己的IQ级别
				positions = i;
				mListView.setSelectionFromTop(positions, 20);
				mScrollAdapter.notifyDataSetChanged();
				break;
			}
		}
	}

	/**
	 * 加载IQ数据
	 * 
	 * @author Administrator
	 */
	class GameIqAcync extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Database.IQ_DATA = HttpRequest.getGameIq();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (null != Database.IQ_DATA) {
				setListView();
			}
		}
	}

	class ScrollAdapter extends BaseAdapter {

		List<GameIQ> data;
		Context context;
		private LayoutInflater layoutInflater = null;

		public ScrollAdapter(Context context, List<GameIQ> data) {
			this.context = context;
			this.data = data;
			this.layoutInflater = LayoutInflater.from(context);
			for (GameIQ gameIQ : data) {
				if (myGrade >= gameIQ.getIq()) {
					inIq = gameIQ.getIq();
					break;
				}
			}
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder mViewHolder;
			if (null == convertView) {
				convertView = layoutInflater.inflate(R.layout.iq_grade_list_item, null);
				mViewHolder = new ViewHolder();
				mViewHolder.bgRl = (RelativeLayout) convertView.findViewById(R.id.igli_bg_rl);
				mViewHolder.dunpaiRl = (RelativeLayout) convertView.findViewById(R.id.igli_dunpai_rl);
				mViewHolder.manIv = (ImageView) convertView.findViewById(R.id.igli_man_iv);
				mViewHolder.titleTv = (TextView) convertView.findViewById(R.id.igli_title_tv);
				mViewHolder.explanationTv = (TextView) convertView.findViewById(R.id.igli_explanation_tv);
				mViewHolder.explanationTv2 = (TextView) convertView.findViewById(R.id.igli_explanation_tv2);
				mViewHolder.zhishangTv = (TextView) convertView.findViewById(R.id.igli_zhishang_tv);
				convertView.setTag(mViewHolder);
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}
			GameIQ mGrade = data.get(position);
			Log.i("myGrade", " positions=" + position);
			if (null != mGrade) {
				if (inIq == mGrade.getIq()) {// 当前自己的IQ级别
					mViewHolder.bgRl.setBackgroundResource(R.drawable.iq_grade_list_item_bg);
					mViewHolder.zhishangTv.setTextColor(Color.WHITE);
				} else {
					mViewHolder.bgRl.setBackgroundColor(Color.TRANSPARENT);
					mViewHolder.zhishangTv.setTextColor(android.graphics.Color.parseColor("#ffad7e65"));
				}
				mViewHolder.titleTv.setText(TextUtils.isEmpty(mGrade.getTitle()) ? "" : mGrade.getTitle());
				mViewHolder.explanationTv.setText(TextUtils.isEmpty(mGrade.getTitleDesc()) ? "" : mGrade.getTitleDesc());
				mViewHolder.explanationTv2.setText(TextUtils.isEmpty(mGrade.getTitlePerson()) ? "" : mGrade.getTitlePerson());
				mViewHolder.zhishangTv.setText("等级:" + mGrade.getIq());
				try {
					Map<String, String> dpMap = JsonHelper.fromJson(mGrade.getLevelImg(), new TypeToken<Map<String, String>>() {});
					for (Entry<String, String> entry : dpMap.entrySet()) {
						final int count = TextUtils.isEmpty(entry.getKey()) ? 0 : Integer.parseInt(entry.getKey());
						String path = entry.getValue();// 图片链接
						if (!TextUtils.isEmpty(path)) {
							ImageUtil.setImgs(HttpURL.URL_PIC_ALL + path, mViewHolder.dunpaiRl, new ImageGroupCallback() {

								public void imageLoaded(Bitmap bitmap, ViewGroup view) {
									view.removeAllViews();
									if (null != bitmap) {
										for (int i = 0; i < count; i++) {
											ImageView img = new ImageView(context);
											img.setBackgroundDrawable(new BitmapDrawable(bitmap));
											RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mst.adjustXIgnoreDensity(DP_WIDTH), mst.adjustYIgnoreDensity(DP_HEIGHT));
											params.leftMargin = mst.adjustXIgnoreDensity(DP_WIDTH * i);
											img.setLayoutParams(params);
											view.addView(img);
										}
									}
								}
							});
						}
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				setHeadImg(mViewHolder, mGrade);
			}
			return convertView;
		}

		/**
		 * 设置等级等级图标
		 * 
		 * @param mViewHolder
		 * @param mGrade
		 */
		private void setHeadImg(ViewHolder mViewHolder, GameIQ mGrade) {
			if (null != mGrade.getTitleImgList() && mGrade.getTitleImgList().size() >= 2 && !TextUtils.isEmpty(mGrade.getTitleImgList().get(0)) && !TextUtils.isEmpty(mGrade.getTitleImgList().get(1))) {
				String path = HttpURL.URL_PIC_ALL + mGrade.getTitleImgList().get(1);
				if (inIq >= mGrade.getIq()) {// 达到过的等级
					path = HttpURL.URL_PIC_ALL + mGrade.getTitleImgList().get(0);
				}
				ImageUtil.setImg(path, mViewHolder.manIv, new ImageCallback() {

					public void imageLoaded(Bitmap bitmap, ImageView view) {
						if (null != bitmap) {
							view.setImageBitmap(bitmap);
						}
					}
				});
			} else {
				if (myGrade >= mGrade.getIq()) {// 达到过的等级
					mViewHolder.manIv.setImageResource(R.drawable.img_iq_title);
				} else {
					mViewHolder.manIv.setImageResource(R.drawable.img_iq_title_);
				}
			}
		}

		class ViewHolder {

			private RelativeLayout bgRl, dunpaiRl;// item背景
			private ImageView manIv;// 人物头像
			private TextView titleTv, explanationTv, explanationTv2, zhishangTv;// 等级名称，说明,说明2,等级值
		}
	}
}
