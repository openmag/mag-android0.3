package com.anheinno.android.libs;

import org.json.JSONObject;

import android.content.Context;

import com.anheinno.android.libs.json.JSONSerializable;


public class JSONBrowserLink implements JSONSerializable {
	private String _url;
	//private String _scripts;
	//private String _classpath;
	private long _expire;
	private boolean _notify;
	private boolean _save;

	public JSONBrowserLink(Context context) {
		_url = null;
		//_scripts = null;
		//_classpath = null;
		_expire = JSONBrowserConfig.getCacheExpire(context);
		_notify = false;
		_save = true;
	}

	public JSONBrowserLink(Context context, String url) {
		this(url, JSONBrowserConfig.getCacheExpire(context), false, true);
	}
	
	public JSONBrowserLink(String url, long expire, boolean notify, boolean save) {
		setURL(url);
		setExpireMilliseconds(expire);
		_notify = notify;
		_save = save;
	}
	
	public void setURL(String url) {
		_url = url;
	}
	
	public void setScripts(String scripts) {
		_url = scripts;
	}
	
	public String getScripts() {
		return _url;
	}
	
	public void setClasspath(String classpath) {
		_url = classpath;
	}
	
	public String getClasspath() {
		return _url;
	}
	
	public String getURL() {
		return _url;
	}
	
	public boolean isValidURL() {
		if(_url == null || _url.length() == 0) {
			return false;
		}
		HTTPRequestString req = new HTTPRequestString();
		if(req.parse(_url)) {
			return true;
		}else {
			return false;
		}
	}
	
	public void setExpireMilliseconds(long ms) {
		_expire = ms;
	}
	
	public long getExpireMilliseconds() {
		return _expire;
	}
	
	public void setNotify(boolean notify) {
		_notify = notify;
	}
	
	public boolean isNotify() {
		return _notify;
	}
	
	public void setSaveHistory(boolean save) {
		_save = save;
	}
	
	public boolean isSaveHistory() {
		return _save;
	}

	public boolean deserializeJSON(JSONObject o) {
		try {
			if(o.has("_url")) {
				_url = o.getString("_url");
			}
			if(o.has("_expire")) {
				_expire = o.getLong("_expire");
			}
			if(o.has("_notify")) {
				_notify = o.getBoolean("_notify");
			}
			if(o.has("_save")) {
				_save = o.getBoolean("_save");
			}
			return true;
		}catch(final Exception e) {
			
		}
		return false;
	}

	public JSONObject serializeJSON() {
		try {
			JSONObject json = new JSONObject();
			json.put("_url", _url);
			json.put("_expire", _expire);
			json.put("_notify", _notify);
			json.put("_save", _save);
			return json;
		}catch(final Exception e) {
		}
		return null;
	}
	
	public String toString() {
		return getURL();
	}
}
