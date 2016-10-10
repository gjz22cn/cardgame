package com.lordcard.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * 
 * @author linguanyu
 *	不可滚动的listview
 */
public class AssistantListview extends ListView {

	public AssistantListview(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public AssistantListview(Context context, AttributeSet attrs) {

		super(context, attrs);

	}

	public AssistantListview(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		if (ev.getAction() == MotionEvent.ACTION_MOVE) {

			return true;

		}

		return super.dispatchTouchEvent(ev);

	}
}
