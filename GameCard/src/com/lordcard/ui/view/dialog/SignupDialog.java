package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.entity.Room;


public abstract class SignupDialog extends Dialog implements OnClickListener {

	private Context context;
	// private RelativeLayout sign_bg;
	private Boolean isPlayTime;
	private Button signOkBtn, signCancleBtn;
	private Room room;
	private TextView signText, signFee, signBase, signTime;
	private RelativeLayout mainLayout;
	private int type;
	private LinearLayout signTimesLayout, signdeitailLayout;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private LinearLayout layout;

	protected SignupDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
	}

	public SignupDialog(Context context, boolean isPlayTime, Room room, int type) {
		super(context, R.style.dialog);
		this.isPlayTime = isPlayTime;
		this.context = context;
		this.room = room;
		this.type = type;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_layout_item);
		mainLayout = (RelativeLayout) findViewById(R.id.sign_bg);
		//		mainLayout.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.baoming_bg));
		layout(context);
		layout = (LinearLayout) findViewById(R.id.mm_layout);
		mst.adjustView(layout);
	}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	private void layout(final Context context) {
		// sign_bg = (RelativeLayout) findViewById(R.id.sign_bg);
		signFee = (TextView) findViewById(R.id.sign_fee_text);
		signTimesLayout = (LinearLayout) findViewById(R.id.sign_times_layout);
		signdeitailLayout = (LinearLayout) findViewById(R.id.sign_detail_layout);
		signBase = (TextView) findViewById(R.id.sign_base_layout);
		signOkBtn = (Button) findViewById(R.id.sign_btn);
		signCancleBtn = (Button) findViewById(R.id.sign_cancel_btn);
		signText = (TextView) findViewById(R.id.sign_text);
		signTime = (TextView) findViewById(R.id.sign_time_limit);
		if (type == 1) {
			signBase.setText("" + room.getBasePoint());
			signFee.setText("每局" + room.getCommissionNum() + "金豆");
			signTimesLayout.setVisibility(View.VISIBLE);
			signdeitailLayout.setVisibility(View.GONE);
			signText.setText("满3人即时开赛，地主胜送2个钻石，农民胜送1个钻石。");
		} else if (type == 2) {
			signBase.setText("" + room.getBasePoint());
			Map<String, String> timeMap = new HashMap<String, String>();
			timeMap = JsonHelper.fromJson(room.getStartRace(), new TypeToken<Map<String, String>>() {
			});
			signFee.setText(timeMap.get("signUp") + "金豆");
			signTimesLayout.setVisibility(View.GONE);
			signdeitailLayout.setVisibility(View.VISIBLE);
			signTime.setText("每天" + timeMap.get("startDate") + "准时开赛，" + timeMap.get("endDate") + "结束。");
			if (isPlayTime) {
				signOkBtn.setText("加  入");
				signText.setText("合成剂专场,玩家可报名参加该专场比赛，每场赢家奖合成剂（平民奖一瓶，地主奖2瓶）。现在是加入时间，点击加入可直接进入专场。");
			} else {
				signOkBtn.setText("报  名");
				signText.setText("合成剂专场,玩家可报名参加该专场比赛，每场赢家奖合成剂（平民奖一瓶，地主奖2瓶）。现在是报名时间，报名成功后请准时参加比赛。");
			}
		}
		signOkBtn.setOnClickListener(this);
		signCancleBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sign_btn:
			mst.unRegisterView(layout);
			//MobclickAgent.onEvent(context, "合成剂专场报名");
			askJoin(room);
			break;
		case R.id.sign_cancel_btn:
			dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public void dismiss() {
		mst.unRegisterView(layout);
		super.dismiss();
	}

	public abstract void askJoin(Room room);
}
