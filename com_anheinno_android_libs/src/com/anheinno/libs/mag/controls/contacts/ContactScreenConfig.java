package com.anheinno.libs.mag.controls.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.anheinno.android.libs.JSONBrowserConfigScreen;
import com.anheinno.android.libs.PreferencesStore;
import com.anheinno.android.libs.R;



public class ContactScreenConfig extends JSONBrowserConfigScreen {

	public static final String CONTACT_SERVICE_URL = "CONTACT_SERVICE_URL";
	
	public ContactScreenConfig(Context context) {
		super(context);
	}

	protected void initConfig() {
		super.initConfig();
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.textinputdialog, null);
		addPane(view);
		
		TextView tv_textinput_prompt = (TextView) findViewById(R.id.tv_textinput_prompt);
		tv_textinput_prompt.setText(getContext().getString(R.string.contact_config_screen_service_url_prompt));
		
		EditText et_textinput = (EditText) findViewById(R.id.et_textinput);
		String url = PreferencesStore.getString(getContext(), CONTACT_SERVICE_URL);
		et_textinput.setText(url);
	}
	
	protected boolean saveConfig() {
		EditText et_textinput = (EditText) findViewById(R.id.et_textinput);
		PreferencesStore.put(getContext(), CONTACT_SERVICE_URL, et_textinput.getText().toString());
		
		return super.saveConfig();
	}

}
