package com.anheinno.android.libs.mag;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.anheinno.android.libs.JSONBrowserConfigScreen;
import com.anheinno.android.libs.R;

public class MAGDocumentConfigScreen extends JSONBrowserConfigScreen {

	public MAGDocumentConfigScreen(Context context) {
		super(context);
	}

	protected void initConfig() {
		/*
		 * 记住用户后就会有推送机制，此时禁止本地清空防止与服务器端数据不一致
		 */
		if(MAGDocumentConfig.isBind(getContext())) {
			super.disablePurge();
		}
		
		super.initConfig();

		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.mag_document_options, null);
		addPane(view);

		TextView tv_bind_account = (TextView)findViewById(R.id.tv_bind_account);
		if (MAGDocumentConfig.isBind(getContext())) {		
			if (MAGDocumentConfig.isVerified(getContext())) {
				tv_bind_account.setText(getContext().getString(R.string.mag_config_screen_bind_account) + MAGDocumentConfig.getUsername(getContext()));
			} else {
				tv_bind_account.setText(getContext().getString(R.string.mag_config_screen_not_bind_account) + MAGDocumentConfig.getUsername(getContext()) + ", but not verified yet!");
			}
		} else {
			tv_bind_account.setText(getContext().getString(R.string.mag_config_screen_not_bind_account));
		}

		CheckBox cb_passwd_protect = (CheckBox)findViewById(R.id.cb_passwd_protect);
		if(MAGDocumentConfig.isPasswordProtect(getContext())) {
			cb_passwd_protect.setChecked(true);
		}else {
			cb_passwd_protect.setChecked(false);
		}

		EditText et_service_uri = (EditText) findViewById(R.id.et_service_uri);
		et_service_uri.setText(MAGDocumentConfig.getLoginURL(getContext()));
		
		TextView tv_attachment_dir = (TextView) findViewById(R.id.tv_attachment_dir);
		tv_attachment_dir.setText(getContext().getString(R.string.mag_config_screen_attachment_download_dir) + MAGDocumentConfig.getDownloadDir(getContext()));
		
		TextView tv_push_protocol = (TextView) findViewById(R.id.tv_push_protocol);
		tv_push_protocol.setText(getContext().getString(R.string.mag_config_screen_push_protocol) + MAGDocumentConfig.getPushProtocol(getContext()));
		
		EditText et_push_server = (EditText) findViewById(R.id.et_push_server);
		et_push_server.setText(MAGDocumentConfig.getPushServer(getContext()));
		
		EditText et_attachment_uri = (EditText) findViewById(R.id.et_attachment_uri);
		et_attachment_uri.setText(MAGDocumentConfig.getImageServerURL(getContext()));
		
	}

	protected boolean saveConfig() {
		
		CheckBox cb_passwd_protect = (CheckBox)findViewById(R.id.cb_passwd_protect);
		MAGDocumentConfig.setPasswordProtect(getContext(), cb_passwd_protect.isChecked());
		
		EditText et_service_uri = (EditText) findViewById(R.id.et_service_uri);
		MAGDocumentConfig.setLoginURL(getContext(), et_service_uri.getText().toString().trim());
	
		//MAGDocumentConfig.setDownloadDir(_download_dir_btn.getLabel());
		
		EditText et_attachment_uri = (EditText) findViewById(R.id.et_attachment_uri);
		MAGDocumentConfig.setImageServerURL(getContext(), et_attachment_uri.getText().toString().trim());
		
		//MAGDocumentConfig.setPushProtocol(MAGDocumentConfig.CACHE_CONFIG_PROTOCOLS[_push_proto_fld.getSelectedIndex()]);
		
		EditText et_push_server = (EditText) findViewById(R.id.et_push_server);
		MAGDocumentConfig.setPushServer(getContext(), et_push_server.getText().toString().trim());

		return super.saveConfig();

	}

}
