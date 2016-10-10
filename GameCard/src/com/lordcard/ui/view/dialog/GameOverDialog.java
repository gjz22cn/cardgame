package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lordcard.common.task.TaskFeedback;
import com.lordcard.common.task.TaskManager;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.CacheKey;
import com.lordcard.entity.GameUser;
import com.lordcard.entity.PrizeGoods;
import com.lordcard.network.cmdmgr.ClientCmdMgr;
import com.lordcard.network.http.GameCache;
import com.lordcard.ui.interfaces.InitMainGameInterface;

/**
 * 淘汰或胜利对话框
 * 
 * @author Administrator
 */
public class GameOverDialog extends Dialog implements android.view.View.OnClickListener {
	private ListView listview;
	private List<PrizeGoods> prizeGoods;
	private Context context;
	private TaskManager taskManager;
	private int rank;
	private TextView tv;
	private TaskFeedback feedback = TaskFeedback.getInstance(TaskFeedback.DIALOG_MODE);
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private RelativeLayout layout;
	private InitMainGameInterface initInterface;

	public GameOverDialog(Context context, int theme, List<PrizeGoods> prizeGoods, TaskManager taskManager, String rank) {
		super(context, theme);
		this.prizeGoods = prizeGoods;
		this.context = context;
		this.rank = Integer.parseInt(rank);
		this.taskManager = taskManager;
	}
	
	public void setInterface( InitMainGameInterface initInterface){
		this.initInterface=initInterface;
		}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_place_end_dailog);
		Log.i("jddd", "------------------------------淘汰对话框");
		TextView tv = (TextView) findViewById(R.id.gped_tv1);
		//输(赢)Icon
		ImageView winOrLoseIconIv = (ImageView) findViewById(R.id.gped_win_or_lose_icon_Iv);
		//排名布局
		LinearLayout prizeLl = (LinearLayout) findViewById(R.id.gped_prize_ll);
		String result="";
		if(rank<=3){
			result="恭喜,您在此轮比赛中获得第" +rank+ "名,太给力啦！\n";
			winOrLoseIconIv.setBackgroundResource(R.drawable.game_place_dialog_win_icon);
			tv.setTextColor(context.getResources().getColor(R.color.orange_red));
		}else{
			result="对不起，由于您在此轮比赛中排名第" +rank+ "名，此轮被淘汰了，加油哦！";
			winOrLoseIconIv.setBackgroundResource(R.drawable.game_place_dialog_lose_icon);
			tv.setTextColor(context.getResources().getColor(R.color.black));
		}
		if(0 == prizeGoods.size()){
			//未获得奖品
			result=result+"您暂无任何奖品。";
			prizeLl.setVisibility(View.INVISIBLE);
		}else{
			//获得奖品
			prizeLl.setVisibility(View.VISIBLE);
			listview = (ListView) findViewById(R.id.gped_lv);
			listview.setAdapter(new GameOverAdapter(context, prizeGoods));
		}
		tv.setText(result);
		Button back = (Button) findViewById(R.id.gped_cancel);
		back.setOnClickListener(this);
		Button sigup = (Button) findViewById(R.id.gped_sign_up);
		sigup.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.envalues_list_layout);
		mst.adjustView(layout);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gped_cancel: // 返回
			mst.unRegisterView(layout);
			dismiss();
			ClientCmdMgr.closeClient();
//			 CmdUtils.exitDizhu();
			 ActivityUtils.finishAcitivity();
			 GameUser cacheUser = (GameUser) GameCache.getObj(CacheKey.GAME_USER);
			 cacheUser.setRound(0);
			 GameCache.putObj(CacheKey.GAME_USER,cacheUser);
			 
			break;
		case R.id.gped_sign_up: // 报名
			mst.unRegisterView(layout);
//			joinRoom(Database.JOIN_ROOM);
			initInterface.InitMainGame();			
			dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		}
		return true;
	}

	private class GameOverAdapter extends BaseAdapter {
		private List<PrizeGoods> prizeGoods;
		private LayoutInflater mInflater;

		public GameOverAdapter(Context context, List<PrizeGoods> prizeGoods) {
			this.prizeGoods = prizeGoods;
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return prizeGoods.size();
		}

		public Object getItem(int position) {
			return prizeGoods.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.text_list_item, null);
				holder.tv = (TextView) convertView.findViewById(R.id.evalues_text);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			PrizeGoods pg = prizeGoods.get(position);
			holder.tv.setText("" + (position + 1) + " ，" + pg.getGoodsName() + pg.getCount() + pg.getUnit());
			return convertView;
		}

		class ViewHolder {
			TextView tv;
		}

	}
}
