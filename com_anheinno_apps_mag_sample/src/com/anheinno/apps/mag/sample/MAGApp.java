package com.anheinno.apps.mag.sample;

import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.mag.MAGDocumentConfig;
import com.anheinno.android.libs.mag.MAGDocumentScreen;
import com.anheinno.android.libs.mag.MAGLoginScreenConfiguration;
//import com.anheinno.android.libs.mag.MAGPushClientApp;
//import com.anheinno.android.libs.mag.MAGPushClientAppRegister;
import com.anheinno.android.libs.ui.UiApplication;
//import com.anheinno.libs.mag.controls.thirdapp.ParamsPreferencesStore;

import android.graphics.Color;
import android.os.Bundle;

public class MAGApp extends UiApplication {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// super.setLandscape();

		//MAGPushClientAppRegister.register();
		// MAGPushClientApp.cleanNotify();
		MAGDocumentConfig.getDefaultRelayServer(this, "https://relay.anhe-inno.com:3100/relay.php");
		MAGDocumentConfig.getDefaultLoginURL(this, "http://192.168.0.201/MAGLIBv0.4/magtest/services.php");
		MAGDocumentConfig.useRelay(this, true);
		
		MAGDocumentScreen screen = new MAGDocumentScreen(this);
		MAGLoginScreenConfiguration login_conf = new MAGLoginScreenConfiguration(this.getString(R.string.txt_login));
		
		login_conf._bg = new BackgroundDescriptor("image=login.jpg image-adjust=horizontal color=#469617");
		login_conf._title_style = new TextStyleDescriptor("icon=icon_read_doc.png icon-position=left padding=10 font-scale=1.2 font-weight=bold color=white");
		
		screen.setLoginConfig(login_conf);
		screen.show();
	}

}