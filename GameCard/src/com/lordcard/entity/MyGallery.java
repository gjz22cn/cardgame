package com.lordcard.entity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class MyGallery extends Gallery {

	public MyGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyGallery(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (velocityX > 0) {
			super.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
		} else {

			super.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
		}
		return false;
	}
}
