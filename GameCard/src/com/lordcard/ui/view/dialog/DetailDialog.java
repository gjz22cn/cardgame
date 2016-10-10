package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lordcard.adapter.FGPlaceListAdapter;
import com.lordcard.adapter.FHGPlaceListAdapter;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.CacheKey;
import com.lordcard.constant.Database;
import com.lordcard.entity.GamePrizeRecord;
import com.lordcard.entity.GameRoomRuleDetail;
import com.lordcard.entity.GameScoreTradeRank;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.Room;
import com.lordcard.network.http.GameCache;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.network.http.HttpURL;
import com.lordcard.network.http.HttpUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 比赛场详情对话框
 * 
 * @author Administrator
 */
public class DetailDialog extends Dialog implements OnClickListener {

	private Context context;
	private ListView rankListView, detailListView, recordListView; //排名listView,详情 奖品listView
	private Button closeBtn; //关闭按钮
	private GameRoomRuleDetail gameHallView;//详情内容
	private LinearLayout rankLl, recordLl; //排名ll,
	private RelativeLayout detailLl;//详情ll
	private TextView myNum, myName, myIntegral; //我的排名，我的昵称，我的积分
	private LinearLayout myLl;
	private ImageView myRankIv;// 皇冠
	private TextView detailContent, detailGolds;//详情内容,奖金池
	private TextView signUpNum;//报名人数
	private TextView signUpTv; //报名数分割线
	private TextView goldsImg;//奖金池图片
	private Button signUpBtn; //报名
	private RadioGroup radioGroup; //单选按钮组
	private RadioButton rankRb, detailRb, recordRb; //排名，详情、记录单选钮
	private String code;
	private boolean isFuhe;// 是否符合赛制
	private Handler mHandler;
	private Handler handler;
	private int position;
	private Room room;
	//	private View loadMoreView;
	private Button loadMoreButton;
	private int pageNum = 1;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private String isSignUp;//是否已报名:"0"未报名,"1"已报名
	private List<GamePrizeRecord> recordAllList = new ArrayList<GamePrizeRecord>();
	private SignRecordAdapter adapter;

	public DetailDialog(Context context, int theme, String code) {
		super(context, theme);
		this.context = context;
		this.code = code;
	}

	public DetailDialog(Context context, GameRoomRuleDetail gameHallView, boolean isFuhe, Room room, int position, Handler mHandler, String isSignUp) {
		super(context, R.style.dialog);
		this.context = context;
		this.gameHallView = gameHallView;
		this.isFuhe = isFuhe;
		this.room = room;
		this.position = position;
		this.mHandler = mHandler;
		this.isSignUp = isSignUp;
	}

	public DetailDialog(Context context, GameRoomRuleDetail gameHallView, boolean isFuhe, Room room, int position, Handler mHandler) {
		super(context, R.style.dialog);
		this.context = context;
		this.gameHallView = gameHallView;
		this.isFuhe = isFuhe;
		this.room = room;
		this.position = position;
		this.mHandler = mHandler;
		this.isSignUp = "0";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information_dialog);

		RelativeLayout rl = (RelativeLayout) findViewById(R.id.exchange_dialog_layout);
		mst.adjustView(rl);
		layout();
	}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	private void layout() {

		//详情
		detailContent = (TextView) findViewById(R.id.if_dialog_detail_content_tv);
		goldsImg = (TextView) findViewById(R.id.all_prize_tip_layout);
		detailGolds = (TextView) findViewById(R.id.if_dialog_detail_prize_tv);
		signUpNum = (TextView) findViewById(R.id.if_dialog_detail_join_num_tv);
		signUpTv = (TextView) findViewById(R.id.if_dialog_detail_join_num_slip_tv);
		if (0 != gameHallView.getApplyNum()) {
			signUpNum.setText("当前报名人数：" + gameHallView.getApplyNum() + "人");
		} else {
			signUpTv.setVisibility(View.GONE);
			signUpNum.setVisibility(View.GONE);
		}
		detailListView = (ListView) findViewById(R.id.if_dialog_detail_list);
		signUpBtn = (Button) findViewById(R.id.if_dialog_detail_signup_btn);
		detailLl = (RelativeLayout) findViewById(R.id.if_dialog_detail_rl);
		rankLl = (LinearLayout) findViewById(R.id.if_dialog_rank_ll);
		detailLl.setVisibility(View.VISIBLE);
		rankLl.setVisibility(View.INVISIBLE);
		if ("0".equals(isSignUp)) {//未报名
			signUpBtn.setText("报名");
		} else if ("1".equals(isSignUp)) {//已报名
			signUpBtn.setText("参赛");
		}

		//排名
		myLl = (LinearLayout) findViewById(R.id.if_dialog_rank_my_ll);
		myNum = (TextView) findViewById(R.id.if_dialog_rank_my_num);
		myName = (TextView) findViewById(R.id.if_dialog_rank_my_name);
		myIntegral = (TextView) findViewById(R.id.if_dialog_rank_my_integral);
		myRankIv = (ImageView) findViewById(R.id.if_dialog_rank_my_ranking);
		rankListView = (ListView) findViewById(R.id.if_dialog_rank_list);

		//记录
		recordLl = (LinearLayout) findViewById(R.id.if_dialog_record_ll);
		recordListView = (ListView) findViewById(R.id.if_dialog_record_list);
		loadMoreButton = (Button) findViewById(R.id.get_more_data);
		loadMoreButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pageNum++;
				getRecord();
				MobclickAgent.onEvent(context, "详细获取更多记录");
			}
		});

		initHandler();
		//公共
		closeBtn = (Button) findViewById(R.id.if_dialog_close_btn);
		closeBtn.setOnClickListener(this);
		radioGroup = (RadioGroup) findViewById(R.id.if_dialog_rg);
		detailRb = (RadioButton) findViewById(R.id.if_dialog_detail_rb);
		recordRb = (RadioButton) findViewById(R.id.if_dialog_record__rb);
		rankRb = (RadioButton) findViewById(R.id.if_dialog_rank_rb);

		detailRb.setClickable(true);
		radioGroup.check(R.id.if_dialog_detail_rb);
		if (!isFuhe) {//普通赛制让排名变灰（不可选）
			rankRb.setVisibility(View.INVISIBLE);
			rankRb.setClickable(false);
		} else {
			rankRb.setClickable(true);
		}
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.if_dialog_detail_rb:
					rankLl.setVisibility(View.INVISIBLE);
					detailLl.setVisibility(View.VISIBLE);
					recordLl.setVisibility(View.INVISIBLE);
					radioGroup.check(R.id.if_dialog_detail_rb);
					Log.i("radioGroup", "详情");
					break;
				case R.id.if_dialog_record__rb:
					Log.i("radioGroup", "记录");
					recordLl.setVisibility(View.VISIBLE);
					detailLl.setVisibility(View.INVISIBLE);
					rankLl.setVisibility(View.INVISIBLE);
					radioGroup.check(R.id.if_dialog_record__rb);
					if (recordAllList == null || recordAllList.size() == 0) {
						getRecord();
					}
					break;
				case R.id.if_dialog_rank_rb:
					Log.i("radioGroup", "排名");
					rankLl.setVisibility(View.VISIBLE);
					detailLl.setVisibility(View.INVISIBLE);
					recordLl.setVisibility(View.INVISIBLE);
					radioGroup.check(R.id.if_dialog_rank_rb);
					getRnk();
					break;
				}
			}
		});
		//初始化详情内容
		List<Map<String, String>> testList = gameHallView.getPrizeGoods();
		PrizeAdapter valueAdapter = new PrizeAdapter(context, testList);
		new Utility().setListViewHeightBasedOnChildren(detailListView);
		ViewGroup.LayoutParams params =  detailListView.getLayoutParams();
		params.height=mst.adjustYIgnoreDensity(testList.size()*(30)+(testList.size()-1)*5);
		detailListView.setDividerHeight(mst.adjustYIgnoreDensity(5));
		detailListView.setLayoutParams(params);
		detailListView.setAdapter(valueAdapter);
		detailContent.setText(gameHallView.getRoomDetail());
		if (isFuhe) {
			String zhidou = "";
			if (null != room.getPrizePool()) {
				zhidou += room.getPrizePool() + "金豆";
			}
			if (null != room.getMaxAward() && !"".equals(room.getMaxAward().trim())) {
				zhidou += "," + room.getMaxAward().trim();
			}
			detailGolds.setText(zhidou);
		} else {
			goldsImg.setVisibility(View.INVISIBLE);
			detailGolds.setText("最高奖励:" + room.getMaxAward());
			detailGolds.setVisibility(View.INVISIBLE);
		}
		signUpBtn.setOnClickListener(this);

		//		private RadioButton rankRb,detailRb; //排名，详情单选钮
	}

	/**
	 * 初始化Handler
	 */
	private void initHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1010://设置排名信息
					String rank = msg.getData().getString("rank");
					Log.i("radioGroup", "rank" + rank);
					List<GameScoreTradeRank> gstList = null;
					if (rank != null && !"-1".equals(rank) && !"1".equals(rank)) {
						gstList = JsonHelper.fromJson(rank, new TypeToken<List<GameScoreTradeRank>>() {
						});
					} else {
						DialogUtils.toastTip("获取排名失败！");
					}
					if (gstList != null) {
						GameScoreTradeRank gctRank = gstList.get(0);
						if ("-1".equals(gctRank.getRank())) {//未报名
							myName.setText("您未报名，暂无排名信息");
							myLl.setVisibility(View.GONE);
							myNum.setVisibility(View.GONE);
							myRankIv.setVisibility(View.GONE);
							myIntegral.setVisibility(View.GONE);
						} else {
							myNum.setText(gctRank.getRank());
							myName.setText(gctRank.getNickName());
							myIntegral.setText(gctRank.getScore());
							if (Integer.valueOf(gctRank.getRank()).intValue() > 3) {
								myRankIv.setVisibility(View.INVISIBLE);
							} else {
								myRankIv.setVisibility(View.VISIBLE);
							}
						}
						gstList.remove(0);
						rankListView.setAdapter(new SignRankAdapter(context, gstList));
					} else {
						DialogUtils.toastTip("获取排名失败！");
					}
					break;
				case 1020://设置排名信息
					String record = msg.getData().getString("record");
					Log.i("radioGroup", "record" + record);
					List<GamePrizeRecord> recordList = null;
					if (record != null && !"1".equals(record)) {
						recordList = JsonHelper.fromJson(record, new TypeToken<List<GamePrizeRecord>>() {
						});
						if (recordList != null && recordList.size() > 0) {
							for (int i = 0; i < recordList.size(); i++) {
								recordAllList.add(recordList.get(i));
							}

						} else {
							loadMoreButton.setClickable(false);
							if (pageNum != 1) {
								DialogUtils.toastTip("亲，没有更多数据了...");
							}
						}
					} else {
						if (pageNum == 1) {
							loadMoreButton.setClickable(false);
						} else {
							loadMoreButton.setClickable(false);
							DialogUtils.toastTip("亲，没有更多数据了...");
						}
					}
					if (recordAllList != null && recordAllList.size() > 0) {
						adapter = new SignRecordAdapter(context, recordAllList);
						recordListView.setAdapter(adapter);
						//						adapter.notifyDataSetChanged();
					} else {
					}
					break;
				default:
					break;
				}
			}
		};
	}

	/**
	 * 获取排名信息
	 */
	private void getRnk() {
		// 显示排名信息
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String rank = HttpRequest.getFuheRank(room.getCode());
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("rank", rank);
					msg.what = 1010;
					msg.setData(b);
					handler.sendMessage(msg);
				} catch (Exception e) {
				}
			}
		}).start();
	}

	/**
	 * 获取记录信息(复合赛制)
	 */
	private void getRecord() {
		// 显示记录信息
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
					Map<String, String> param = new HashMap<String, String>();
					param.put("roomCode", room.getCode());
					param.put("hallCode", String.valueOf(Database.GAME_TYPE));
					param.put("loginToken",cacheUser.getLoginToken());
					param.put("pageNo", String.valueOf(pageNum));
					String url = HttpURL.HTTP_PATH + "/game/prizerecord/getlistbypage.sc";
					String result = HttpUtils.post(url, param,true);
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("record", result);
					msg.what = 1020;
					msg.setData(b);
					handler.sendMessage(msg);
				} catch (Exception e) {
				}
			}
		}).start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//		dismiss();
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dismiss();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.if_dialog_close_btn://关闭对话框
			dismiss();
			break;
		case R.id.if_dialog_detail_signup_btn: //报名
			Message message = new Message();
			if (isFuhe) {// 复合赛制
				MobclickAgent.onEvent(context, "详细复合报名");
				message.what = FHGPlaceListAdapter.WHAT1;
				Bundle bundle = new Bundle();
				bundle.putInt(FHGPlaceListAdapter.POSITION, position);
				message.setData(bundle);
				mHandler.sendMessage(message);

			} else {
				message.what = FGPlaceListAdapter.WHAT2;
				Bundle bundle = new Bundle();
				bundle.putInt(FGPlaceListAdapter.POSITION, position);
				message.setData(bundle);
				mHandler.sendMessage(message);
			}
			dismiss();
			break;
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	/**
	 * 排名Adapter
	 * @author Administrator
	 *
	 */
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
			Log.i("GameScoreTradeRank", "排名  " + position + " : 账号 " + gctRank.getAccount() + "     昵称 " + gctRank.getNickName() + "    排名  "
					+ gctRank.getRank() + "     积分  " + gctRank.getScore());
			holder.num.setText(gctRank.getRank());
			holder.name.setText(gctRank.getNickName());
			holder.zhidou.setText(gctRank.getScore());
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

	/**
	 * 记录Adapter
	 * @author Administrator
	 *
	 */
	private class SignRecordAdapter extends BaseAdapter {
		private List<GamePrizeRecord> gstList;
		private LayoutInflater mInflater;
		private Context context;

		public SignRecordAdapter(Context context, List<GamePrizeRecord> gstList) {
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
				convertView = mInflater.inflate(R.layout.match_rank_item, null);
				holder.roomName = (TextView) convertView.findViewById(R.id.mk_room_name);
				holder.time = (TextView) convertView.findViewById(R.id.mk_item_num);
				holder.rank = (TextView) convertView.findViewById(R.id.mk_item_name);
				holder.prize = (TextView) convertView.findViewById(R.id.mk_item_zhidou);
				holder.img = (ImageView) convertView.findViewById(R.id.match_ranking);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			GamePrizeRecord gctRank = gstList.get(position);
			holder.roomName.setText(gctRank.getRoomName());
			holder.time.setText(gctRank.getTime());
			holder.rank.setText(gctRank.getRank());
			holder.prize.setText(gctRank.getPrize());
			holder.img.setVisibility(View.GONE);
			return convertView;
		}

		class ViewHolder {
			TextView roomName;//赛场名称
			TextView time; // 时间
			TextView rank; // 排名
			TextView prize; // 奖励
			ImageView img;
		}
	}

	/**
	 * 奖品Adapter
	 */
	private class PrizeAdapter extends BaseAdapter {
		private List<Map<String, String>> gifInt;
		private LayoutInflater mInflater;
		private Context context;

		public PrizeAdapter(Context context, List<Map<String, String>> goodbagList) {
			this.gifInt = goodbagList;
			this.context = context;
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
			//			mViewHolder.text.setTextColor(color.gold);
			//			mViewHolder.text.setTextSize(R.dimen.text_size15);
			mViewHolder.text.setText(gifInt.get(position).get("rankText") + " : " + gifInt.get(position).get("prizeText"));
			return convertView;
		}

		class ViewHolder {
			private TextView text;

		}
	}

	/**
	 * 用于重新计算listview高度，
	 * 解决scrollview嵌套listview问题
	 * @author Administrator
	 */
	public class Utility {
		public void setListViewHeightBasedOnChildren(ListView listView) {
			ListAdapter listAdapter = listView.getAdapter();
			if (listAdapter == null) {
				// pre-condition
				return;
			}

			int totalHeight = 0;
			for (int i = 0; i < listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			listView.setLayoutParams(params);
		}
	}
}
