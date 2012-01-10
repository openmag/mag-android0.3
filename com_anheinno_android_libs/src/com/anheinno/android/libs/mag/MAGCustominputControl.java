/*
 * MAGCustominputControl.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import org.json.JSONArray;

import android.content.Context;
import android.view.View;

//import com.anheinno.android.libs.ui.Manager;


/**
 * 2010-11-29
 * 
 * @author 安和创新科技（北京）有限公司
 * @version 1.0
 */
public abstract class MAGCustominputControl {
	protected MAGCustominput _backend;
	protected JSONArray _params;

	public MAGCustominputControl() {
		super();
	}
	
	public void setParams(JSONArray params) {
		_params = params;
	}

	public final void setInput(MAGCustominput input) {
		_backend = input;
	}

	public String id() {
		return _backend.id();
	}

	public abstract View initControl(Context context);

	public abstract void updateControl();

	public abstract boolean setAttribute(String name, String value);

	/**
	 */
	public abstract String getQueryString();

	public abstract boolean validate();

	public abstract void onShowUi();

}
