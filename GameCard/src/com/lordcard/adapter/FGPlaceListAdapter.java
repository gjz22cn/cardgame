package com.lordcard.adapter;


import com.zzyddz.shui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.task.GenericTask;
import com.lordcard.common.task.TaskFeedback;
import com.lordcard.common.task.TaskManager;
import com.lordcard.common.task.base.TaskParams;
import com.lordcard.common.task.base.TaskResult;
import com.lordcard.common.util.DialogUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.ImageUtil.ImageCallback;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.constant.Constant;
import com.lordcard.constant.Database;
import com.lordcard.entity.GameRoomRuleDetail;
import com.lordcard.entity.Room;
import com.lordcard.entity.RoomSignup;
import com.lordcard.network.http.HttpRequest;
import com.lordcard.ui.base.FastJoinTask;
import com.lordcard.ui.dizhu.DoudizhuRoomListActivity;
import com.lordcard.ui.view.dialog.DetailDialog;
import com.lordcard.ui.view.notification.NotificationService;
import com.sdk.jd.sms.util.JDSMSPayUtil;
import com.sdk.util.PaySite;
import com.sdk.util.PayTipUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 快速比赛场Adapter
 * @author Administrator
 */
public class FGPlaceListAdapter extends BaseAdapter {

	public static final String JOIN_ERROR = "11"; // 报名失败
	public static final String JOIN_SUCCESS = "12"; // 报名成功
	public static final String NOT_APPLYTERM = "31"; // 报名条件不足
	public static final String NOT_APPLYFEE = "32"; // 报名费用不足
	public static final String NOT_MATCHTIME = "33"; // 已报名时间未到
	public static final String JOIN_MATCHTIME = "34"; // 已报名比赛中
	// handler的what
//	public static final int WHAT1 = 11000; //复合赛制报名
	public static final int WHAT2 = 11001;//普通赛制报名
	public static final int WHAT_JOIN_SUCCESS = 11003; // 报名成功
	public static final String POSITION = "position";
	private Map<String, String> picMap;
	private TaskFeedback feedback = TaskFeedback.getInstance(TaskFeedback.DIALOG_MODE);
	private Context context;
	private List<Room> gamePlaceDate;
	private LayoutInflater layoutInflater = null;
	private TaskManager taskManager;
	private HashMap<String, TextView> refreshTvList;
	private HashMap<String, Button> signUpBtnList;
	private Handler mHandler;
	private Handler handler;
	private GenericTask rjoinTask;

	/**
	 * @param context
	 * @param taskManager
	 * @param isFuhe  是否是复合赛场 true:复合赛场，false:普通赛场
	 * @param handler
	 * @param rightList
	 */
	@SuppressLint("HandlerLeak")
	public FGPlaceListAdapter(Context context, TaskManager taskManager, final Handler handler) {
		this.handler = handler;
		this.context = context;
		this.taskManager = taskManager;
		this.layoutInflater = LayoutInflater.from(context);
		initData();
		refreshTvList = new HashMap<String, TextView>();
		signUpBtnList = new HashMap<String, Button>();
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
//					case WHAT1:// 复合赛制报名
//						int position = (Integer) msg.getData().get(POSITION);
//						joinFuhe(position);
//						break;
					case WHAT2:// 普通赛制报名
						int position1 = (Integer) msg.getData().get(POSITION);
						joinPutong(position1);
						break;
					case WHAT_JOIN_SUCCESS:
						try {
							int po = msg.getData().getInt(POSITION);
							String roomCode = gamePlaceDate.get(po).getCode();
							for (int i = 0, count = Database.ROOM_SIGN_UP.size(); i < count; i++) {
								if (roomCode.equals(Database.ROOM_SIGN_UP.get(i).getRoomCode())) {
									Database.ROOM_SIGN_UP.get(i).setSignUp("1");
								}
							}
							signUpBtnList.get(roomCode).setBackgroundDrawable(ImageUtil.getDrawableResId(R.drawable.green_btn_bg, true, true));
							signUpBtnList.get(roomCode).setText("参赛");
							handler.sendEmptyMessage(DoudizhuRoomListActivity.HANDLER_WHAT_ROOM_LIST_REFRESH_FAST);
						} catch (Exception e) {}
						break;
				}
			}
		};
	}

	public void onDestory() {
		this.layoutInflater = null;
		mHandler = null;
		if (rjoinTask != null) {
			rjoinTask.cancel(true);
			rjoinTask = null;
		}
		if (null != gamePlaceDate) {
			gamePlaceDate.clear();
			gamePlaceDate = null;
		}
		if (null != refreshTvList) {
			refreshTvList.clear();
			refreshTvList = null;
		}
		if (null != signUpBtnList) {
			signUpBtnList.clear();
			signUpBtnList = null;
		}
	}

	/**
	 * 初始化数据
	 * @param isFuhe
	 * @param rightList
	 */
	public void initData() {
		if (Database.HALL_CACHE != null) {
			try {
				if (null != gamePlaceDate) {
					gamePlaceDate.clear();
					gamePlaceDate = null;
				}
				gamePlaceDate = new ArrayList<Room>();
				List<Room> gameFastDate = Database.HALL_CACHE.getFastRoomList();
				for (int i = 0; i < gameFastDate.size(); i++) {
					gamePlaceDate.add(gameFastDate.get(i));
				}
				Database.FAST_EXPLAIN = new HashMap<String, GameRoomRuleDetail>();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getCount() {
		return gamePlaceDate.size();
	}

	@Override
	public Object getItem(int position) {
		return gamePlaceDate.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder;
		if (null == convertView) {
			convertView = layoutInflater.inflate(R.layout.game_place_item, null);
			mViewHolder = new ViewHolder();
			mViewHolder.prizebg = (ImageView) convertView.findViewById(R.id.game_pi_bg_iv);
			mViewHolder.maxPrizeTv = (TextView) convertView.findViewById(R.id.game_pi_max_prize_tv);
			mViewHolder.prizePoolTv = (TextView) convertView.findViewById(R.id.game_pi_zhidou_tv);
			mViewHolder.titalIv = (ImageView) convertView.findViewById(R.id.game_pi_bg_tital_iv);
			mViewHolder.prizebeanIv = (ImageView) convertView.findViewById(R.id.game_pi_prize_bean_iv);
			mViewHolder.refreshBtn = (Button) convertView.findViewById(R.id.game_pi_refresh_ib);
			mViewHolder.expBtn = (Button) convertView.findViewById(R.id.game_pi_explanation_btn);
			mViewHolder.signUpBtn = (Button) convertView.findViewById(R.id.game_pi_sign_up_btn);
			mViewHolder.timeDesc = (TextView) convertView.findViewById(R.id.game_pi_bg_time_desc);
			mViewHolder.timeText = (TextView) convertView.findViewById(R.id.game_pi_bg_time_text);
			mViewHolder.timeSlip = (TextView) convertView.findViewById(R.id.game_pi_bg_slip);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		Room room = gamePlaceDate.get(position);
		//换底图
		try {
			picMap = JsonHelper.fromJson(room.getResHall(), new TypeToken<Map<String, String>>() {});
			String itemName = picMap.get("roomItemH");
			String itemClickName = picMap.get("roomItemV");
			String url = room.getResHallUrl();
			String imgClickurl = url + itemClickName;
			String imageUrl = url + itemName;
			ImageUtil.setImg(imageUrl, mViewHolder.prizebg, new ImageCallback() {

				public void imageLoaded(Bitmap bitmap, ImageView view) {
					view.setScaleType(ScaleType.FIT_XY);
					view.setImageBitmap(bitmap);
				}
			});//底图
			ImageUtil.setImg(imgClickurl, mViewHolder.titalIv, new ImageCallback() {

				public void imageLoaded(Bitmap bitmap, ImageView view) {
					view.setScaleType(ScaleType.FIT_XY);
					view.setImageBitmap(bitmap);
				}
			});//房间名
			mViewHolder.prizePoolTv.setText("最高奖励:");
			mViewHolder.prizebeanIv.setVisibility(View.VISIBLE);
			mViewHolder.maxPrizeTv.setText(room.getMaxAward());
			mViewHolder.refreshBtn.setVisibility(View.GONE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mViewHolder.timeDesc.setVisibility(View.GONE);
		mViewHolder.timeText.setVisibility(View.GONE);
		mViewHolder.timeSlip.setVisibility(View.GONE);
		// 详情
		mViewHolder.expBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String roomCode = gamePlaceDate.get(position).getCode();
				MobclickAgent.onEvent(context,"快速详情");
				if (Database.FAST_EXPLAIN != null && Database.FAST_EXPLAIN.containsKey(roomCode)) {
					showDetailDialog(position, Database.FAST_EXPLAIN.get(roomCode));
				} else {
					GetGameRuleTask gameRule = new GetGameRuleTask(position);
					gameRule.execute(roomCode);
				}
			}
		});
		// 报名
		mViewHolder.signUpBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					MobclickAgent.onEvent(context,"快速报名");
					joinPutong(position);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return convertView;
	}

	class ViewHolder {

		private TextView maxPrizeTv;// 最高奖励(普通赛制)
		private TextView prizePoolTv;// 奖金池：金豆，钻石(符合赛制)
		private TextView timeDesc;//赛场时间说明(符合赛制)
		private TextView timeText; // 赛场时间文本(符合赛制)
		private TextView timeSlip;//分割线(符合赛制)
		private ImageView titalIv;// 标题图片
		private ImageView prizebeanIv;// "金豆'图片
		private ImageView prizebg;// 背景图片
		private Button refreshBtn;// 刷新
		private Button expBtn;// 详情
		private Button signUpBtn;// 报名
	}

	/**
	 * 弹出详情
	 * @param position
	 */
	private void showDetailDialog(int position, GameRoomRuleDetail roomRuleDetail) {
		if (Math.abs(System.currentTimeMillis() - Constant.CLICK_TIME) >= Constant.SPACING_TIME) {
			Constant.CLICK_TIME=System.currentTimeMillis();
			//普通赛制
			DetailDialog smd = new DetailDialog(context, roomRuleDetail, false, gamePlaceDate.get(position), position, mHandler);
			smd.show();
		}
	}

	/**
	 * 普通赛制报名
	 * 
	 * @param position
	 */
	private void joinPutong(final int position1) {
		int position = position1;
		SginUpTask signUp = new SginUpTask();
		signUp.setFeedback(feedback);
		TaskParams params = new TaskParams();
		params.put("joinRoom", gamePlaceDate.get(position));
		params.put("position", position);
		signUp.execute(params);
		taskManager.addTask(signUp);
	}

	/**
	 * 获取比赛规则说明
	 * 
	 * @author Administrator
	 */
	private class GetGameRuleTask extends AsyncTask<String, Void, String> {

		private int position;
		private String roomCode;

		public GetGameRuleTask(int position) {
			this.position = position;
		}

		@Override
		protected String doInBackground(String... params) {
			roomCode = params[0];
			return HttpRequest.getGameRule(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				if (!TextUtils.isEmpty(result) && !"fail".equals(result)) {
					GameRoomRuleDetail gameHallView = JsonHelper.fromJson(result, GameRoomRuleDetail.class);
					Database.FAST_EXPLAIN.put(roomCode, gameHallView);
					showDetailDialog(position, gameHallView);
				} else {
					Toast.makeText(context, "详情信息获取失败，请稍后再试",Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 加入房间
	 * 
	 * @param room
	 */
	private synchronized void joinRoom(final Room room) {
		Database.GAME_BG_DRAWABLEID = R.drawable.join_bj;
		// 加入游戏前校验
		Database.currentActivity.runOnUiThread(new Runnable() {
			public void run() {
				FastJoinTask.joinRoom(room);
				NotificationService.joinRoomCode = room.getCode();
			}
		});
	}

	/**
	 * 刷新奖金池
	 * 
	 * @author Administrator
	 */
	public class RefreshGoldTask extends AsyncTask<Integer, Void, String> {

		private int position;

		@Override
		protected String doInBackground(Integer... params) {
			this.position = params[0];
			return HttpRequest.getPrizePool(gamePlaceDate.get(position).getCode());
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				if (null != result && !"1".equals(result)) {
					//{"prizePool":"10000","maxAward":"美女一个"}
					Map<String, String> map = JsonHelper.fromJson(result, new TypeToken<Map<String, String>>() {});
					String zhidou = "";
					if (map.containsKey("prizePool") && !"".equals(map.get("prizePool").trim())) {
						zhidou += map.get("prizePool").trim() + "金豆";
						long pool = Integer.parseInt(map.get("prizePool").trim());
						gamePlaceDate.get(position).setPrizePool(pool);
					}
					if (map.containsKey("maxAward") && !"".equals(map.get("maxAward").trim())) {
						zhidou += "," + map.get("maxAward");
						gamePlaceDate.get(position).setMaxAward(map.get("maxAward").trim());
					}
					refreshTvList.get(gamePlaceDate.get(position).getCode()).setText(zhidou);
					handler.sendEmptyMessage(DoudizhuRoomListActivity.HANDLER_WHAT_ROOM_LIST_REFRESH_FAST);
				}
			} catch (Exception e) {}
		}
	}
	/**
	 * 报名请求
	 * @author Administrator
	 */
	private class SginUpTask extends GenericTask {
		@Override
		protected TaskResult _doInBackground(TaskParams... params) {
			try {
				TaskParams param = null;
				if (params.length <= 0) {
					return TaskResult.FAILED;
				}
				param = params[0];
				Room room = (Room) param.get("joinRoom");
				int position = (Integer) param.get("position");
				String result = HttpRequest.signUp(room.getCode()).trim();
				Map<String, String> map = JsonHelper.fromJson(result, new TypeToken<Map<String, String>>() {});
				String status = null;
//				String stopTime = null;
				if (map.containsKey("status")) {
					status = map.get("status").trim();
				}
				if (map.containsKey("stopTime")) {
//					stopTime = map.get("stopTime").trim();
				}
				if (status != null && !JOIN_ERROR.equals(status.trim())) {
					status = status.trim();
					if (status.equals(JOIN_SUCCESS)) {
						joinRoom(room);
					}
					if (status.equals(NOT_APPLYTERM) || status.equals(NOT_APPLYFEE)){
//						DialogUtils.toastTip("报名失败，请查看详情！");
						if (map.containsKey("tp") && "5".equals(map.get("tp"))) {//金豆不足
							if (map.containsKey("mn") && map.containsKey("pc")) {
								if (!TextUtils.isEmpty(map.get("mn").trim())) {
									JDSMSPayUtil.setContext(context);
									int money = Integer.parseInt(map.get("mn").trim());
									PayTipUtils.showTip(money,PaySite.GAME_SIGN_UP);  //配置的提示方式
//									PlayViewUtils.openFastPayDialog(map.get("mn").trim(), map.get("pc").trim());
								}
							}
						} else {//其他条件不足
							DialogUtils.toastTip("" + map.get("mes"));
						}
					}
					if (status.equals(NOT_MATCHTIME)) {
						Message msg = new Message();
						Bundle b = new Bundle();
						b.putInt(POSITION, position);
						msg.setData(b);
						msg.what = WHAT_JOIN_SUCCESS;
						mHandler.sendMessage(msg);
//						DialogUtils.toastTip("您已经报名，现在不是参赛时间，请在相应时间入场比赛！");
						DialogUtils.toastTip(map.get("mes"), 1000, Gravity.CENTER);
					}
					if (status.equals(JOIN_MATCHTIME)) {
						joinRoom(room);
					}
				} else {
//					DialogUtils.toastTip("报名失败，请稍后再试！");
					DialogUtils.toastTip(map.get("mes"), 1000, Gravity.CENTER);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return TaskResult.FAILED;
			}
			feedback.cancel(null);
			return TaskResult.OK;
		}
	}

	/**
	 * 更新报名状态
	 * @param roomSignUp
	 */
	public void ChangeSignUp() {
		if (null != Database.ROOM_SIGN_UP) {
			for (int i = 0, count = Database.ROOM_SIGN_UP.size(); i < count; i++) {
				RoomSignup roomSignup = Database.ROOM_SIGN_UP.get(i);
				String roomCode = roomSignup.getRoomCode();
				if (signUpBtnList.containsKey(roomCode)) {//存在此房间的报名状态
					if (roomSignup.getSignUp().equals("0")) {
						signUpBtnList.get(roomCode).setText("报名");
						signUpBtnList.get(roomCode).setBackgroundResource(R.drawable.red_ok_btn);
					} else if (roomSignup.getSignUp().equals("1")) {
						signUpBtnList.get(roomCode).setBackgroundResource(R.drawable.green_btn_bg);
						signUpBtnList.get(roomCode).setText("参赛");
					}
				}
			}
		}
		handler.sendEmptyMessage(DoudizhuRoomListActivity.HANDLER_WHAT_ROOM_LIST_REFRESH_FAST);
	}
}
