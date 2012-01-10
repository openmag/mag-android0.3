/*
 * HTTPRequestScreen.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs;

import org.json.JSONObject;
import android.content.Context;
import android.view.KeyEvent;

import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.ui.CustomTitleMainScreen;
import com.anheinno.android.libs.ui.ScreenMenu;

/**
 * 创建一个通过HTTP协议请求JSON代码，缓存，并且显示的基础窗口
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public abstract class JSONBrowserScreen extends CustomTitleMainScreen implements JSONBrowserInterface {
	private JSONBrowserScreen _self;

	protected final class EmbeddedJSONBrowserField extends JSONBrowserField {

		public EmbeddedJSONBrowserField(Context context) {
			super(context);
		}

		public void showJSON(JSONObject o, Object params) {
			_self.showJSON(o, params);
		}
		
	}
	
	public JSONBrowserScreen(Context context) {
		super(context);
		_self = this;
		EmbeddedJSONBrowserField ejb = new EmbeddedJSONBrowserField(context);
		super.setContentView(ejb);
	}

	public final JSONBrowserField getEmbeddedJSONBrowserField() {
		return (JSONBrowserField)super.getContentView();
	}

	public int getHistoryLength() {
		return getEmbeddedJSONBrowserField().getHistoryLength();
	}

	public void open(JSONBrowserLink link, boolean refresh, Object params) {
		getEmbeddedJSONBrowserField().open(link, refresh, params);
	}

	public void syncOpen(JSONBrowserLink link, boolean refresh, Object params) {
		getEmbeddedJSONBrowserField().syncOpen(link, refresh, params);
	}

	public String getAbsoluteURL(String url) {
		return getEmbeddedJSONBrowserField().getAbsoluteURL(url);
	}

	// 刷新页面
	public void refresh() {
		refresh(-1);
	}

	/**
	 * 重新从缓存加载内容
	 * 
	 */
	public void reload() {
		getEmbeddedJSONBrowserField().reload();
	}

	/**
	 * 刷新，重新从网络加载内容
	 * 
	 * @param expire
	 */

	public void refresh(int expire) {
		getEmbeddedJSONBrowserField().refresh(expire);
	}

	public boolean back(boolean refresh) {
		return getEmbeddedJSONBrowserField().back(refresh);
	}

	protected void makeMenu(ScreenMenu menu) {
		super.makeMenu(menu);
		getEmbeddedJSONBrowserField().prepareMenu(menu);
	}

	@Override
	public boolean onKeyDown(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if(getEmbeddedJSONBrowserField().onKeyDown(event.getKeyCode(), event)) {
				return true;
			}
		}
		return super.onKeyDown(event);
	}

	public void tryReloadPage() {
		getEmbeddedJSONBrowserField().tryReloadPage();
	}

	public JSONObject getData() {
		return getEmbeddedJSONBrowserField().getData();
	}

	public String getURL() {
		return getEmbeddedJSONBrowserField().getURL();
	}

	public boolean isLoading() {
		return getEmbeddedJSONBrowserField().isLoading();
	}

	public void setBackground(BackgroundDescriptor desc) {
		getEmbeddedJSONBrowserField().setBackground(desc);
	}

	public void removeBackground() {
		getEmbeddedJSONBrowserField().removeBackground();
	}

	public boolean isRendering() {
		return getEmbeddedJSONBrowserField().isRendering();
	}

}
