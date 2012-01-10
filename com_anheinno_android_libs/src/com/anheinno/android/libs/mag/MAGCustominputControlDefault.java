package com.anheinno.android.libs.mag;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class MAGCustominputControlDefault extends MAGCustominputControl {
	
	@Override
	public View initControl(Context context) {
		TextView tv = new TextView(context);
		tv.setText("ClassNotFound for " + _backend.getControlClassName());
		return tv;
	}

	@Override
	public void updateControl() {
		
	}

	@Override
	public boolean setAttribute(String name, String value) {
		return false;
	}

	@Override
	public String getQueryString() {
		return "";
	}

	@Override
	public boolean validate() {
		return true;
	}

	public void onShowUi() {
	}
}
