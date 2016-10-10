package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.MultiScreenTool;

public class SendMessDialog extends Dialog {

	// private Context context;
	private RelativeLayout mainLayout;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();

	protected SendMessDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// this.context = context;
		// layout(context);
	}

	public SendMessDialog(Context context, int theme) {
		super(context, theme);
		// this.context = context;
	}

	public SendMessDialog(Context context) {
		super(context);
		// this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.liaotian_menu);
		mainLayout = (RelativeLayout) findViewById(R.id.liaotian_layout);
		mainLayout.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.liaotian_bj_1,true));
		// layout(context);
		mst.adjustView(mainLayout);
	}

	public void setDismiss() {

	}

	/**
	 * 布局
	 * 
	 * @param context
	 */
	// private void layout(final Context context) {
	// Button zhendong = (Button) findViewById(R.id.checkzhendong);
	// Button bgMusic = (Button) findViewById(R.id.bgmusic);
	// SeekBar music = (SeekBar) findViewById(R.id.musicControl);
	// Button sure = (Button) findViewById(R.id.sure);
	// Button cancel = (Button) findViewById(R.id.cancel);
	//
	// // 设置初始化
	//
	// if (MySharedPreferences.getMyPreference(context).getSetting()
	// .getBoolean("zhendong", true)) {
	// zhendong.setBackgroundDrawable(ImageUtil.getResDrawable(context,
	// R.drawable.open));
	// } else {
	// zhendong.setBackgroundDrawable(ImageUtil.getResDrawable(context,
	// R.drawable.close));
	// }
	//
	// if (MySharedPreferences.getMyPreference(context).getSetting()
	// .getBoolean("bgmusic", true)) {
	// bgMusic.setBackgroundDrawable(ImageUtil.getResDrawable(context,
	// R.drawable.open));
	// } else {
	// bgMusic.setBackgroundDrawable(ImageUtil.getResDrawable(context,
	// R.drawable.close));
	// }
	// music.setProgress(MySharedPreferences.getMyPreference(context)
	// .getSetting().getInt("music", 0));
	//
	// music.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	// @Override
	// public void onStopTrackingTouch(SeekBar seekBar) {
	// }
	//
	// @Override
	// public void onStartTrackingTouch(SeekBar seekBar) {
	// }
	//
	// @Override
	// public void onProgressChanged(SeekBar seekBar, int progress,
	// boolean fromUser) {
	// MySharedPreferences.getMyPreference(context).getEditor()
	// .putInt("music", progress).commit();
	// }
	// });
	// // 设置监听器
	// zhendong.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// if (MySharedPreferences.getMyPreference(context).getSetting()
	// .getBoolean("zhendong", true)) {
	// MySharedPreferences.getMyPreference(context).getEditor()
	// .putBoolean("zhendong", false).commit();
	// v.setBackgroundDrawable(ImageUtil.getResDrawable(context,
	// R.drawable.close));
	// } else {
	// MySharedPreferences.getMyPreference(context).getEditor()
	// .putBoolean("zhendong", true).commit();
	// v.setBackgroundDrawable(ImageUtil.getResDrawable(context,
	// R.drawable.open));
	// }
	// }
	// });
	// bgMusic.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// if (MySharedPreferences.getMyPreference(context).getSetting()
	// .getBoolean("bgmusic", true)) {
	// MySharedPreferences.getMyPreference(context).getEditor()
	// .putBoolean("bgmusic", false).commit();
	// v.setBackgroundDrawable(ImageUtil.getResDrawable(context,
	// R.drawable.close));
	// } else {
	// MySharedPreferences.getMyPreference(context).getEditor()
	// .putBoolean("bgmusic", true).commit();
	// v.setBackgroundDrawable(ImageUtil.getResDrawable(context,
	// R.drawable.open));
	// }
	// }
	// });
	//
	// music.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	// @Override
	// public void onStopTrackingTouch(SeekBar seekBar) {
	// }
	//
	// @Override
	// public void onStartTrackingTouch(SeekBar seekBar) {
	// }
	//
	// @Override
	// public void onProgressChanged(SeekBar seekBar, int progress,
	// boolean fromUser) {
	// MySharedPreferences.getMyPreference(context).getEditor()
	// .putInt("music", progress).commit();
	// }
	// });
	//
	// sure.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// MySharedPreferences.getMyPreference(context).getEditor()
	// .commit();
	//
	// AudioPlayUtils.getInstance(context).SetVoice(
	// MySharedPreferences.getMyPreference(context)
	// .getSetting().getInt("music", 0));
	// if (!MySharedPreferences.getMyPreference(context).getSetting()
	// .getBoolean("bgmusic", true)) {
	// AudioPlayUtils.getInstance(context).stopBgMusic();
	// } else {
	// if (!AudioPlayUtils.getInstance(context).isBgisPlaying()) {
	// if (Database.currentActivity.getClass().equals(
	// MeteoroidActivity.class)) {// 是否是麻将页面
	// AudioPlayUtils.getInstance(context).playBgMusic(
	// R.raw.mg_bg);
	// } else {
	// AudioPlayUtils.getInstance(context).playBgMusic(
	// R.raw.game_waiting);
	// }
	// }
	// }
	//
	// setDismiss();
	// }
	// });
	//
	// cancel.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// setDismiss();
	// }
	// });
	// }

	@Override
	public void dismiss() {
		mst.unRegisterView(mainLayout);
		super.dismiss();
		ImageUtil.releaseDrawable(mainLayout.getBackground());
	}

}
