package com.lordcard.entity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * 回弹效果的横向scroolview
 * */
@SuppressLint("HandlerLeak")
public class XiaHorizontalScrollView extends HorizontalScrollView {

//	 private static final String TAG = XiaHorizontalScrollView.class
//	   .getName();
	// context
	Context mContext;
	// the child View
	private View mChildView;
	private boolean handleStop = false;
	private static final int MAX_SCROLL_HEIGHT = 280;

	public XiaHorizontalScrollView(Context context) {
		super(context);
	}

	public XiaHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public XiaHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		// get child View
		if (getChildCount() > 0) {
			this.mChildView = getChildAt(0);
		}
	}

	private float touchX = 0;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (arg0.getAction() == MotionEvent.ACTION_DOWN) {
			// get touch X
			touchX = arg0.getX();
		}
		return super.onInterceptTouchEvent(arg0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mChildView != null) {
			commonOnTouchEvent(ev);
		}
		return super.onTouchEvent(ev);
	}

	// Scroll drag
	private static final float SCROLL_DRAG = 0.4f;

	private void commonOnTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
			case MotionEvent.ACTION_UP:
				if (mChildView.getScrollX() != 0) {
					handleStop = true;
					startAnimation();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				float nowX = ev.getX();
				int deltaX = (int) (touchX - nowX);
				touchX = nowX;
				if (isEdge()) {
					int offset = mChildView.getScrollX();
					if (offset < MAX_SCROLL_HEIGHT && offset > -MAX_SCROLL_HEIGHT) {
						mChildView.scrollBy((int) (deltaX * SCROLL_DRAG), 0);
						handleStop = false;
					}
				}
				break;
			default:
				break;
		}
	}

	/*
	 * whether to edge
	 */
	private boolean isEdge() {
		// get the child view Width
		int childViewWidth = mChildView.getMeasuredWidth();
		// get the ScrollView Width
		int srollViewWidth = this.getWidth();
		// get
		int tempOffset = childViewWidth - srollViewWidth;
		int scrollX = this.getScrollX();
		if (scrollX == 0 || scrollX == tempOffset) {
			return true;
		}
		return false;
	}

	private float RESET_RADIO = 0.9f;

	private void startAnimation() {
		resetChildViewPositionHandler.sendEmptyMessage(0);
	}

	private float childScrollX = 0;
	Handler resetChildViewPositionHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			childScrollX = mChildView.getScrollX();
			if (childScrollX != 0 && handleStop) {
				childScrollX = childScrollX * RESET_RADIO;
				if (Math.abs(childScrollX) <= 2) {
					childScrollX = 0;
				}
				mChildView.scrollTo((int) childScrollX, 0);
				this.sendEmptyMessageDelayed(0, 3);
			}
		};
	};
}