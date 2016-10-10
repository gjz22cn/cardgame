package com.lordcard.ui;

import com.zzyddz.shui.R;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.lordcard.common.util.ImageUtil;
import com.sdk.util.SDKFactory;

public class GameGuideView extends Activity implements OnGestureListener {
	/** Called when the activity is first created. */
	private int[] imageId = new int[] {/*R.drawable.login_guide1,R.drawable.login_guide2,R.drawable.login_guide3 xs_del*/};
	private List<ImageView> points;
	ViewFlipper mViewFlipper;
	private GestureDetector mGestureDetector = null;;
	LinearLayout layout = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.firstguide);

		mViewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
		// 生成GestureDetector对象，用于检测手势事件
		mGestureDetector = new GestureDetector(this);
		// 添加用于切换的图片;

		for (int i = 0; i < imageId.length; i++) {
			ImageView mImageView = new ImageView(this);
			mImageView.setBackgroundDrawable(ImageUtil.getResDrawable(imageId[i],false));
			mImageView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			mViewFlipper.addView(mImageView);
		}
	}

	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (e1.getX() - e2.getX() > 120) {// 向右滑动
			if (mViewFlipper.getDisplayedChild() == imageId.length - 1) {
				mViewFlipper.stopFlipping();
				goToLogin();
			} else {
				mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
				mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
				mViewFlipper.showNext();
			}
		} else if (e2.getX() - e1.getX() > 120) {// 向左滑动
			if (mViewFlipper.getDisplayedChild() == 0) {
				mViewFlipper.stopFlipping();
				Toast.makeText(this, "亲，已经是第一张了", 500).show();
			} else {
				mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
				mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
				mViewFlipper.showPrevious();
			}
		}
		return true;
	}

	public void onLongPress(MotionEvent e) {}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	public void onShowPress(MotionEvent e) {}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.mGestureDetector.onTouchEvent(event);
	}

	private void goToLogin() {
		// ActivityUtils.createShortCut(GameGuideView.this,R.drawable.icon,R.string.app_name);
		Intent intent = new Intent();
		intent.setClass(GameGuideView.this, SDKFactory.getLoginView());
		intent.putExtra("auto_login", true);
		startActivity(intent);
		finish();
	}

}
