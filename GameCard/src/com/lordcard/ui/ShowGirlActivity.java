package com.lordcard.ui;

import com.zzyddz.shui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.gson.reflect.TypeToken;
import com.lordcard.common.exception.CrashApplication;
import com.lordcard.common.schedule.AutoTask;
import com.lordcard.common.schedule.ScheduledTask;
import com.lordcard.common.util.ActivityPool;
import com.lordcard.common.util.ActivityUtils;
import com.lordcard.common.util.ImageUtil;
import com.lordcard.common.util.JsonHelper;
import com.lordcard.common.util.MultiScreenTool;
import com.lordcard.constant.Database;
import com.lordcard.network.http.HttpURL;
import com.lordcard.ui.base.BaseActivity;

public class ShowGirlActivity extends BaseActivity implements OnGestureListener, OnClickListener {

	LinearLayout layout = null;
	ImageView imageView = null;
	Button pullButton;
	private SlidingDrawer loginSD;
	AlphaAnimation alphaAnimation = null;
	private boolean first;
	private ViewFlipper viewFlipper = null;
	private GestureDetector gestureDetector = null;
	private TextView girlBtmView;
	private LinearLayout topallLayout;
	private RelativeLayout topLayout;
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private RelativeLayout girlButtom;//菜单栏
	private Button closeButton, backBtn;
	private int clicknum = 0;//计数用来显示隐藏上面的布局
	private List<String> allContent = new ArrayList<String>();//图片对应的文字
	private List<Map<String, String>> girlList;//图集类
	private List<ImageView> girlView = new ArrayList<ImageView>();
	private TextView page;//第几张
	private int curPage;//当前第几张，做标志用
	private boolean canFlipper = true;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_girl_layout);
		RelativeLayout mainlayout = (RelativeLayout) findViewById(R.id.set_layout);
		mst.adjustView(mainlayout);
		girlButtom = (RelativeLayout) findViewById(R.id.girl_buttom_layout);
		page = (TextView) findViewById(R.id.page_num);
		girlBtmView = (TextView) findViewById(R.id.girl_buttom_textview);
		viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
		closeButton = (Button) findViewById(R.id.close_buttom);
		closeButton.setOnClickListener(this);
		topallLayout = (LinearLayout) findViewById(R.id.login_sliding);
		topLayout = (RelativeLayout) findViewById(R.id.login_sliding_content);
		pullButton = (Button) findViewById(R.id.login_sliding_handle);
		backBtn = (Button) findViewById(R.id.back_btn);
		backBtn.setOnClickListener(this);
		pullButton.setOnClickListener(this);
		if (Database.TOOL != null && Database.TOOL.getType().equals("1")) {
			girlList = new ArrayList<Map<String, String>>();
			try {
				girlList = JsonHelper.fromJson(Database.TOOL.getContent(), new TypeToken<List<Map<String, String>>>() {});
			} catch (Exception e) {
				// TODO: handle exception
			}
			for (int i = 0; i < 3; i++) {
				ImageView image = new ImageView(this);
				girlView.add(image);
			}
			boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
			for (int i = 0; i < girlList.size(); i++) {
				if (null != ImageUtil.getGirlBitmap(girlList.get(i).get("path"), ActivityUtils.isWifiActive(), true)) {
					allContent.add(girlList.get(i).get("desc"));
				} else {
					if (ActivityUtils.isWifiActive()) {
						ImageUtil.downMMImg(HttpURL.URL_PIC_ALL + girlList.get(i).get("path"), null);
					}
				}
			}
		}
		gestureDetector = new GestureDetector(this);
		for (int i = 0; i < girlView.size(); i++) {
			int point = curPage - 1;
			if (point < 0) {
				point = girlList.size() - 1;
			}
			Drawable draw1 = ImageUtil.getcutBitmap(HttpURL.URL_PIC_ALL + girlList.get(i).get("path"), ActivityUtils.isWifiActive());
			if (null != draw1) {
				girlView.get(i).setBackgroundDrawable(draw1);
			}
			girlView.get(i).setScaleType(ImageView.ScaleType.FIT_XY);
			viewFlipper.addView(girlView.get(i), new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}
		refresh();
		readdView();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	/**
	 * 刷新对应的文字和页面
	 * */
	private void refresh() {
		int pg = curPage + 1;
		page.setText("第" + pg + "/" + girlList.size() + "张");
		girlBtmView.setText(allContent.get(curPage));
	}

	/**
	 * 重新加载ViewFiper前面一个和后面一个imageview的背景
	 * */
	private void readdView() {
		ScheduledTask.addDelayTask(new AutoTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (viewFlipper != null && girlList != null) {
							canFlipper = true;
							int curId = ShowGirlActivity.this.viewFlipper.getDisplayedChild();
							int point = curPage - 1;
							int postion = curPage + 1;
							int curpoint = curId - 1;
							int curpostion = curId + 1;
							if (point < 0) {
								point = girlList.size() - 1;
							}
							if (postion > girlList.size() - 1) {
								postion = 0;
							}
							if (curpoint < 0) {
								curpoint = 2;
							}
							if (curpostion > 2) {
								curpostion = 0;
							}
							Drawable draw = ImageUtil.getcutBitmap(HttpURL.URL_PIC_ALL + girlList.get(point).get("path"), ActivityUtils.isWifiActive());
							if (null != draw) {
								ShowGirlActivity.this.viewFlipper.getChildAt(curpoint).setBackgroundDrawable(draw);
							}
							Drawable draw1 = ImageUtil.getcutBitmap(HttpURL.URL_PIC_ALL + girlList.get(postion).get("path"), ActivityUtils.isWifiActive());
							if (null != draw1) {
								ShowGirlActivity.this.viewFlipper.getChildAt(curpostion).setBackgroundDrawable(draw1);
							}
							Log.d("forTag", " curId : " + curId);
							for (int i = 0; i < girlList.size(); i++) {
								if (i == point || i == curPage || i == postion) {} else {
									ImageUtil.clearsingleCache(HttpURL.URL_PIC_ALL + girlList.get(i).get("path"));
								}
							}
						}
					}
				});
			}
		}, 700);
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.login_sliding_handle:
				clicknum++;
				if (clicknum % 2 == 1) {
					pullButton.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.gone_button, true));
					topLayout.setVisibility(View.VISIBLE);
				} else {
					topLayout.setVisibility(View.GONE);
					pullButton.setBackgroundDrawable(ImageUtil.getResDrawable(R.drawable.pull_button, true));
				}
				break;
			case R.id.close_buttom:
				girlButtom.setVisibility(View.GONE);
				break;
			case R.id.back_btn:
				finish();
				break;
			default:
				break;
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		//对手指滑动的距离进行了计算，如果滑动距离大于120像素，就做切换动作，否则不做任何切换动作。
		// 从左向右滑动
		if (arg0.getX() - arg1.getX() > 120) {
			// 添加动画
			if (canFlipper) {
				canFlipper = false;
				this.viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
				this.viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
				this.viewFlipper.showNext();
				curPage = curPage + 1;
				if (curPage >= girlList.size()) {
					curPage = 0;
				}
				refresh();
				readdView();
			}
			return true;
		}// 从右向左滑动
		else if (arg0.getX() - arg1.getX() < -120) {
			if (canFlipper) {
				canFlipper = false;
				this.viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
				this.viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
				this.viewFlipper.showPrevious();
				curPage = curPage - 1;
				if (curPage < 0) {
					curPage = girlList.size() - 1;
				}
				refresh();
				readdView();
			}
			return true;
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.gestureDetector.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageUtil.releaseDrawable(pullButton.getBackground());
		this.gestureDetector = null;
		allContent = null;
		girlList = null;
		imageView = null;
		backBtn.setOnClickListener(null);
		pullButton.setOnClickListener(null);
		if (viewFlipper != null) {
			viewFlipper.removeAllViews();
			viewFlipper = null;
		}
		ImageUtil.clearGirlBitMapCache();
		ActivityPool.remove(this);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		final Context ctx = CrashApplication.getInstance();
		// 重写返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
