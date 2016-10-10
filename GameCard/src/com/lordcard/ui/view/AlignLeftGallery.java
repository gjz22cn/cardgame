package com.lordcard.ui.view;

import java.lang.reflect.Field;

import android.R.attr;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

public class AlignLeftGallery extends Gallery {
	public interface IOnItemClickListener {
		public void onItemClick(int position);
	}

	private static final String TAG = "AlignLeftGallery";
	private Camera mCamera;
	private int mWidth;
	private int mPaddingLeft;
	private boolean flag;
	private static int firstChildWidth;
	private static int firstChildPaddingLeft;
	private int offsetX;

	private IOnItemClickListener mListener;

	public AlignLeftGallery(Context context) {
		super(context);
		mCamera = new Camera();
		this.setStaticTransformationsEnabled(true);
	}

	public AlignLeftGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		mCamera = new Camera();
		setAttributesValue(context, attrs);
		this.setStaticTransformationsEnabled(true);
	}

	public AlignLeftGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mCamera = new Camera();
		setAttributesValue(context, attrs);
		this.setStaticTransformationsEnabled(true);
	}

	private void setAttributesValue(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, new int[] { attr.paddingLeft });
		mPaddingLeft = typedArray.getDimensionPixelSize(0, 0);
		typedArray.recycle();
	}

	protected boolean getChildStaticTransformation(View child, Transformation t) {
		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);
		mCamera.save();
		final Matrix imageMatrix = t.getMatrix();
		if (flag) {
			firstChildWidth = getChildAt(0).getWidth();
			Log.i(TAG, "firstChildWidth = " + firstChildWidth);
			firstChildPaddingLeft = getChildAt(0).getPaddingLeft();
			flag = false;
		}
		offsetX = firstChildWidth / 2 + firstChildPaddingLeft + mPaddingLeft - mWidth / 2;
		mCamera.translate(offsetX, 0f, 0f);
		mCamera.getMatrix(imageMatrix);
		mCamera.restore();
		return true;
	}

	public void setOnItemClickListener(IOnItemClickListener listener) {
		mListener = listener;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Log.i(TAG, "onSingleTapUp----------------------");
		try {
			Field f = AlignLeftGallery.class.getSuperclass().getDeclaredField("mDownTouchPosition");
			f.setAccessible(true);
			int position = f.getInt(this);
			Log.i(TAG, "mDownTouchPosition = " + position);
			if (null != mListener && position >= 0) {
				mListener.onItemClick(position);
			}
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchFieldException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e2) {
			e2.printStackTrace();
		} catch (IllegalAccessException e3) {
			e3.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(TAG, "onTouchEvent----------------------");
		event.offsetLocation(-offsetX, 0);
		return super.onTouchEvent(event);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.i(TAG, "onSizeChanged------- w = " + w + " h = " + h + "oldw = " + oldw + "oldh = " + oldh);
		if (!flag) {
			mWidth = w;
			getLayoutParams().width = mWidth;
			flag = true;
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}
}