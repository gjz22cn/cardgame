package com.lordcard.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * 
 * @author linguanyu
 *	不可滚动的Gridview
 */
public class AssistantGridview extends GridView {

	public AssistantGridview(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public AssistantGridview(Context context, AttributeSet attrs) {

		super(context, attrs);

	}

	public AssistantGridview(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		if (ev.getAction() == MotionEvent.ACTION_MOVE) {

			return true; //��ֹGridView����

		}

		return super.dispatchTouchEvent(ev);

	}
}
