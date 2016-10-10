/**
 * BaseDialog.java [v 1.0.0]
 * classes : com.lordcard.ui.view.dialog.BaseDialog
 * auth : yinhongbiao
 * time : 2013 2013-3-25 下午5:32:42
 */
package com.lordcard.ui.view.dialog;

import com.zzyddz.shui.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lordcard.common.util.MultiScreenTool;

/**
 * com.lordcard.ui.view.dialog.AlertDialog
 * 
 * @author Administrator <br/>
 *         create at 2013 2013-3-25 下午5:32:42
 */
public class BaoXiangDialog extends Dialog implements OnClickListener {

	private Context context;
	private TextView showText;
	private boolean canCancel = true; // 是否允许取消
	private MultiScreenTool mst = MultiScreenTool.singleTonHolizontal();
	private RelativeLayout layout;

	protected BaoXiangDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.context = context;
	}

	public BaoXiangDialog(Context context, boolean canCancel) {
		super(context, R.style.dialog);
		this.context = context;
		this.canCancel = canCancel;
	}

	public BaoXiangDialog(Context context) {
		super(context, R.style.dialog);
		this.context = context;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.baoxiang_dialog);
		layout(context);
		layout = (RelativeLayout) findViewById(R.id.mm_layout);
		mst.adjustView(layout);
	}

	/**
	 * 布局
	 */
	private void layout(final Context context) {
		Button cancel = (Button) findViewById(R.id.common_cancel);
		if (!canCancel) {
			cancel.setVisibility(View.GONE);
		}
		Button ok = (Button) findViewById(R.id.common_ok);
		showText = (TextView) findViewById(R.id.common_text);
		cancel.setOnClickListener(this);
		ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_cancel:
			mst.unRegisterView(layout);
			dismiss();
			cancelClick();
			break;
		case R.id.common_ok:
			mst.unRegisterView(layout);
			dismiss();
			okClick();
			break;
		default:
			break;
		}
	}

	public void setText(String content) {
		showText.setText(content);
	}

	/** 确定 */
	public void okClick() {};

	/** 取消 */
	public void cancelClick() {};
}
