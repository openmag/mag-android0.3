/*
 * JSONBrowserConfigScreen.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs;

import com.anheinno.android.libs.ui.ModalScreen;
import com.anheinno.android.libs.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class JSONBrowserConfigScreen extends ModalScreen {

	private boolean _enable_purge;
	private LinearLayout _layout;

	public JSONBrowserConfigScreen(Context context) {
		super(context);

		_enable_purge = true;
		_layout = null;
	}

	private void updateCacheDirSize() {
		TextView tv_cache_dir_size = (TextView) findViewById(R.id.tv_cache_dir_size);
		tv_cache_dir_size.setText(getContext().getString(R.string.json_config_screen_cache_size) + JSONObjectCacheDatabase.getCacheSize(getContext()) + "/"
				+ JSONObjectCacheDatabase.getCacheCount(getContext()));
	}

	protected void disablePurge() {
		_enable_purge = false;
	}

	protected void addPane(View view) {
		_layout.addView(view);
	}

	protected void initConfig() {
		_layout = new LinearLayout(getContext());
		setContentView(_layout);
		_layout.setOrientation(LinearLayout.VERTICAL);

		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.json_browser_options, null);
		addPane(view);

		CheckBox cb_cache_enabled = (CheckBox) findViewById(R.id.cb_cache_enabled);
		if (cb_cache_enabled != null) {
			if (JSONBrowserConfig.isCacheEnabled(getContext())) {
				cb_cache_enabled.setChecked(true);
			} else {
				cb_cache_enabled.setChecked(false);
			}
		} else {

		}

		updateCacheDirSize();

		EditText et_expire = (EditText) findViewById(R.id.et_expire_hours);
		et_expire.setText("" + JSONBrowserConfig.getCacheExpireHours(getContext()));

		Button purge_button = (Button) findViewById(R.id.bt_purge_cache);
		if (_enable_purge) {
			purge_button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					JSONObjectCacheDatabase.purgeAllObject(getContext());
					updateCacheDirSize();
				}
			});
		} else {
			purge_button.setVisibility(View.GONE);
		}

		// 中继服务器
		CheckBox cb_relay_enabled = (CheckBox) findViewById(R.id.cb_relay_enable);
		if (cb_relay_enabled != null) {
			if (JSONBrowserConfig.useRelay(getContext())) {
				cb_relay_enabled.setChecked(true);
			} else {
				cb_relay_enabled.setChecked(false);
			}
		}

		EditText et_relay_service_uri = (EditText) findViewById(R.id.et_relay_service_uri);
		et_relay_service_uri.setText(JSONBrowserConfig.getRelayServer(getContext()));
		
	}

	protected void onCreate() {
		initConfig();
	}

	protected boolean saveConfig() {
		CheckBox cb_cache_enabled = (CheckBox) findViewById(R.id.cb_cache_enabled);
		JSONBrowserConfig.setCacheEnabled(getContext(), cb_cache_enabled.isChecked());

		CheckBox cb_relay_enabled = (CheckBox) findViewById(R.id.cb_relay_enable);
		JSONBrowserConfig.setUseRelay(getContext(), cb_relay_enabled.isChecked());

		int expire_hour = 0;
		try {
			EditText et_expire = (EditText) findViewById(R.id.et_expire_hours);
			expire_hour = Integer.parseInt(et_expire.getText().toString());
		} catch (final Exception e) {
		}
		JSONBrowserConfig.setCacheExpireHours(getContext(), expire_hour);

		String relay_server_uri = "";
		try {
			EditText et_relay_service_uri = (EditText) findViewById(R.id.et_relay_service_uri);
			relay_server_uri = et_relay_service_uri.getText().toString();
		} catch (final Exception e) {
		}
		JSONBrowserConfig.setRelayServer(getContext(), relay_server_uri);

		return true;
	}

	protected boolean onClose() {
		return saveConfig();
	}

}
