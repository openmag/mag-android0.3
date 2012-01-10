package com.anheinno.android.libs;

import org.json.JSONObject;


public class JSONBrowserScreenState {
	private String _url;
	private long _expire;
	private boolean _notify;
	private boolean _save;
	private Object _params;
	private JSONObject _data;
	//private XYRect _focus_rect;
	private int _scroll_pos;

	public JSONBrowserScreenState(String url, long expire, boolean notify, boolean save, Object params) {
		_init(url, expire, notify, save, params);
	}
	
	private void _init(String url, long expire, boolean notify, boolean save, Object params) {
		_expire = expire;
		_notify = notify;
		//_focus_rect = null;
		_save = save;
		_params = params;
		_data = null;
		setURL(url);
	}
	
	public boolean isValid() {
		if(_url != null) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean setURL(String url) {
		if(url != null && url.length() > 0) {
			HTTPRequestString req = new HTTPRequestString();
			if(req.parse(url)) {
				_url = req.getURL();
				return true;
			}
		}
		_url = null;
		return false;
	}

	public String getURL() {
		return _url;
	}
	
	public void setExpire(long exp) {
		_expire = exp;
	}
	public long getExpire() {
		return _expire;
	}
	
	public boolean isNotify() {
		return _notify;
	}
	
	public void setSaveHistory(boolean save) {
		_save = save;
	}
	
	public boolean needSave() {
		return _save;
	}
	
	public Object getParams() {
		return _params;
	}
	
	public void setData(JSONObject data) {
		_data = data;
	}
	
	public JSONObject getData() {
		return _data;
	}
	
	/*public XYRect getFocusRect() {
		return _focus_rect;
	}
	public void setFocusRect(XYRect rect) {
		_focus_rect = rect;
	}*/
	
	public int getScrollPosition() {
		return _scroll_pos;
	}
	public void setScrollPosition(int pos) {
		_scroll_pos = pos;
	}
}
