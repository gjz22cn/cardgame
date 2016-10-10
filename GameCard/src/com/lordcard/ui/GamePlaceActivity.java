package com.lordcard.ui;

import com.zzyddz.shui.R;
import com.zzyddz.shui.R.color;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.lordcard.adapter.AwardListAdapter;
import com.lordcard.entity.AwardVo;

/**
 * 比赛场
 * 
 * @author Administrator
 */
public class GamePlaceActivity extends Activity implements OnClickListener {

	// 排名、比赛规则、奖励方案
	private Button rankBtn, ruleBtn, awardBtn;
	// 刷新
	private Button refreshBtn;
	// 十分钟超快赛、一周一倍房排名赛、历史记录、“暂时预留”
	private ImageButton tenMinutesIbtn, aWeekIbtn, historyIbtn, Ibtn;
	private List<AwardVo> awardList;
	private ListView awardListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_place_layout);
		initData();
		initView();
	}

	private void initData() {
		awardList = new ArrayList<AwardVo>();
		for (int i = 0; i < 10; i++) {
			awardList.add(new AwardVo(i + 1, "农夫山泉", String.valueOf(10000), "苹果iphone"));
		}
	}

	private void initView() {
		RelativeLayout gpl_Layout = (RelativeLayout) findViewById(R.id.gpl_rl);
		gpl_Layout.setBackgroundResource(R.drawable.gamebg);
		rankBtn = (Button) findViewById(R.id.gpl_rank_btn);
		ruleBtn = (Button) findViewById(R.id.gpl_rule_btn);
		awardBtn = (Button) findViewById(R.id.gpl_award_btn);
		refreshBtn = (Button) findViewById(R.id.gpl_refresh_btn);
		tenMinutesIbtn = (ImageButton) findViewById(R.id.gpl_ten_minutes_ibtn);
		aWeekIbtn = (ImageButton) findViewById(R.id.gpl_one_week_ibtn);
		historyIbtn = (ImageButton) findViewById(R.id.gpl_history_ibtn);
		Ibtn = (ImageButton) findViewById(R.id.gpl_ibtn);
		rankBtn.setOnClickListener(this);
		ruleBtn.setOnClickListener(this);
		awardBtn.setOnClickListener(this);
		refreshBtn.setOnClickListener(this);
		tenMinutesIbtn.setOnClickListener(this);
		aWeekIbtn.setOnClickListener(this);
		historyIbtn.setOnClickListener(this);
		Ibtn.setOnClickListener(this);
		awardListView = (ListView) findViewById(R.id.gpl_award_lv);
		awardListView.setAdapter(new AwardListAdapter(this, awardList));
	}

	@Override
	public void onClick(View v) {
		setClickBtnBg(v.getId());
		switch (v.getId()) {
			case R.id.gpl_rank_btn:// 排名
				break;
			case R.id.gpl_rule_btn:// 比赛规则
				break;
			case R.id.gpl_award_btn:// 奖励方案
				break;
			case R.id.gpl_refresh_btn:// 刷新
				break;
			case R.id.gpl_ten_minutes_ibtn:// 十分钟超快赛
				break;
			case R.id.gpl_one_week_ibtn:// 一周一倍房排名赛
				break;
			case R.id.gpl_history_ibtn:// 历史记录
				break;
			case R.id.gpl_ibtn:// 暂时预留”
				break;
			default:
				break;
		}
	}

	/**
	 * 设置按钮背景状态
	 * 
	 * @param id
	 */
	private void setClickBtnBg(int id) {
		int white = getResources().getColor(color.white);
		int pink = getResources().getColor(color.gpl_top_btn_text_color);
		switch (id) {
			case R.id.gpl_rank_btn:// 排名
				rankBtn.setTextColor(white);
				ruleBtn.setTextColor(pink);
				awardBtn.setTextColor(pink);
				rankBtn.setBackgroundResource(R.drawable.gpl_top_left_select);
				ruleBtn.setBackgroundResource(R.drawable.gpl_top_center);
				awardBtn.setBackgroundResource(R.drawable.gpl_top_right);
				break;
			case R.id.gpl_rule_btn:// 比赛规则
				rankBtn.setBackgroundResource(R.drawable.gpl_top_left);
				ruleBtn.setBackgroundResource(R.drawable.gpl_top_center_select);
				awardBtn.setBackgroundResource(R.drawable.gpl_top_right);
				rankBtn.setTextColor(pink);
				ruleBtn.setTextColor(white);
				awardBtn.setTextColor(pink);
				break;
			case R.id.gpl_award_btn:// 奖励方案
				rankBtn.setBackgroundResource(R.drawable.gpl_top_left);
				ruleBtn.setBackgroundResource(R.drawable.gpl_top_center);
				awardBtn.setBackgroundResource(R.drawable.gpl_top_right_select);
				rankBtn.setTextColor(pink);
				ruleBtn.setTextColor(pink);
				awardBtn.setTextColor(white);
				break;
			case R.id.gpl_ten_minutes_ibtn:// 十分钟超快赛
				tenMinutesIbtn.setBackgroundResource(R.drawable.gp_type_btn_bg_select);
				aWeekIbtn.setBackgroundResource(R.drawable.gp_type_btn_bg);
				historyIbtn.setBackgroundResource(R.drawable.gp_type_btn_bg);
				Ibtn.setBackgroundResource(R.drawable.gp_type_btn_bg);
				tenMinutesIbtn.setImageResource(R.drawable.ten_minutes_select);
				aWeekIbtn.setImageResource(R.drawable.one_week);
				historyIbtn.setImageResource(R.drawable.history);
				Ibtn.setImageResource(R.drawable.ten_minutes);
				break;
			case R.id.gpl_one_week_ibtn:// 一周一倍房排名赛
				tenMinutesIbtn.setBackgroundResource(R.drawable.gp_type_btn_bg);
				aWeekIbtn.setBackgroundResource(R.drawable.gp_type_btn_bg_select);
				historyIbtn.setBackgroundResource(R.drawable.gp_type_btn_bg);
				Ibtn.setBackgroundResource(R.drawable.gp_type_btn_bg);
				tenMinutesIbtn.setImageResource(R.drawable.ten_minutes);
				aWeekIbtn.setImageResource(R.drawable.one_week_select);
				historyIbtn.setImageResource(R.drawable.history);
				Ibtn.setImageResource(R.drawable.ten_minutes);
				break;
			case R.id.gpl_history_ibtn:// 历史记录
				tenMinutesIbtn.setBackgroundResource(R.drawable.gp_type_btn_bg);
				aWeekIbtn.setBackgroundResource(R.drawable.gp_type_btn_bg);
				historyIbtn.setBackgroundResource(R.drawable.gp_type_btn_bg_select);
				Ibtn.setBackgroundResource(R.drawable.gp_type_btn_bg);
				tenMinutesIbtn.setImageResource(R.drawable.ten_minutes);
				aWeekIbtn.setImageResource(R.drawable.one_week);
				historyIbtn.setImageResource(R.drawable.history_select);
				Ibtn.setImageResource(R.drawable.ten_minutes);
				break;
			case R.id.gpl_ibtn:// 暂时预留”
				tenMinutesIbtn.setBackgroundResource(R.drawable.gp_type_btn_bg);
				aWeekIbtn.setBackgroundResource(R.drawable.gp_type_btn_bg);
				historyIbtn.setBackgroundResource(R.drawable.gp_type_btn_bg);
				Ibtn.setBackgroundResource(R.drawable.gp_type_btn_bg_select);
				tenMinutesIbtn.setImageResource(R.drawable.ten_minutes);
				aWeekIbtn.setImageResource(R.drawable.one_week);
				historyIbtn.setImageResource(R.drawable.history);
				Ibtn.setImageResource(R.drawable.ten_minutes_select);
				break;
			default:
				break;
		}
	}
}
