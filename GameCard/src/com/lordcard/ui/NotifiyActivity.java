package com.lordcard.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lordcard.constant.Database;

public class NotifiyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Class<?> clazz = null;
		if (Database.currentActivity == null) {
			clazz = StartActivity.class;
		} else {
			clazz = Database.currentActivity.getClass();
		}
		Intent intent = new Intent(this, clazz);
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		startActivity(intent);
		finish();
	}
}
