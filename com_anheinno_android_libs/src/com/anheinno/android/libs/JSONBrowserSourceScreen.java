package com.anheinno.android.libs;

import org.json.JSONObject;

import com.anheinno.android.libs.ui.ModalScreen;
import com.anheinno.android.libs.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class JSONBrowserSourceScreen extends ModalScreen {
	private JSONBrowserInterface _parent;
	
	public JSONBrowserSourceScreen(Context context, JSONBrowserInterface parent) {
		super(context);
		this._parent = parent;
	}
	
	protected void onCreate() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.json_source_screen, null);
		setContentView(view);
		
		TextView tv_source_url = (TextView) findViewById(R.id.tv_source_url);
		String url = _parent.getURL();
		tv_source_url.setText("URL: " + url);
		
		TextView tv_source_codes = (TextView) findViewById(R.id.tv_source_codes);
		JSONObject json = _parent.getData();
		if(json != null) {
			tv_source_codes.setText(json.toString());
		}else {
			tv_source_codes.setText("No data");
		}
	}
}
