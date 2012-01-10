package com.anheinno.android.libs;

import org.json.JSONObject;

import com.anheinno.android.libs.graphics.BackgroundDescriptor;

public interface JSONBrowserInterface {

	String getAbsoluteURL(String url);
		
	void open(JSONBrowserLink link, boolean refresh, Object params);
	
	void syncOpen(JSONBrowserLink link, boolean refresh, Object params);
		
	void showJSON(JSONObject o, Object params);
	
	boolean back(boolean refresh);
	
	void refresh();
	
	void reload();
	
	void refresh(int expire);
	
	void setBackground(BackgroundDescriptor desc);
	
	void removeBackground();
	
	String getURL();
	
	JSONObject getData();
	
	void tryReloadPage();
	
	int getHistoryLength();
	
	boolean isLoading();
	
	boolean isRendering();
	
}
