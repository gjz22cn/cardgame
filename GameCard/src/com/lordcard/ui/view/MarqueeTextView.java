package com.lordcard.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author chenpenglong
 * @Description 文字滚动效果
 */
public class MarqueeTextView extends TextView {
	public MarqueeTextView(Context context) {
		super(context);
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFocused() {
		return true;
	}
}
