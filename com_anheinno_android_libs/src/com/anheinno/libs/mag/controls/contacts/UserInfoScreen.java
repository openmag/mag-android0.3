/**
 * UserInfoScreen.java
 *
 * Copyright 2007-2010 anhe.
 */
package com.anheinno.libs.mag.controls.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anheinno.android.libs.R;
import com.anheinno.android.libs.ui.ModalScreen;



public class UserInfoScreen extends ModalScreen {
	private ContactUser _user;
	
	UserInfoScreen(Context context, ContactUser u) {
		super(context);
		_user = u;
	}

	@Override
	protected void onCreate() {
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout view = (LinearLayout)inflater.inflate(R.layout.user_info_dialog, null);
		setContentView(view);

		TextView tv_user_info_name = (TextView)findViewById(R.id.tv_user_info_name);
		tv_user_info_name.setText(_user.get_name());
		
		TextView tv_email = new TextView(getContext());
		tv_email.setText(getContext().getString(R.string.user_info_prompt_email) + _user.get_email());
		view.addView(tv_email);

		TextView tv_mobile = new TextView(getContext());
		tv_mobile.setText(getContext().getString(R.string.user_info_prompt_mobile) + _user.get_mobile());
		view.addView(tv_mobile);
		
		TextView tv_workphone = new TextView(getContext());
		tv_workphone.setText(getContext().getString(R.string.user_info_prompt_workphone) + _user.get_workphone());
		view.addView(tv_workphone);
		
		TextView tv_homephone = new TextView(getContext());
		tv_homephone.setText(getContext().getString(R.string.user_info_prompt_homephone) + _user.get_homephone());
		view.addView(tv_homephone);
		
		TextView tv_place = new TextView(getContext());
		tv_place.setText(getContext().getString(R.string.user_info_prompt_place) + _user.get_place());
		view.addView(tv_place);

		TextView tv_account = new TextView(getContext());
		tv_account.setText(getContext().getString(R.string.user_info_prompt_account) + _user.get_account());
		view.addView(tv_account);
		
	}
}
