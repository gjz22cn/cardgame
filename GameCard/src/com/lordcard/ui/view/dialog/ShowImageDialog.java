package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class ShowImageDialog extends Dialog {

	public ShowImageDialog(Context context) {
		super(context);
		init(context);
	}

	public ShowImageDialog(Activity context, int theme) {
		super(context, theme);
	}

	public ShowImageDialog(Activity context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	private void init(final Context context) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.show_image);
	}
}
