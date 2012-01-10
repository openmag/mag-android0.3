/**
 * MAGLinkedCustomControl.java
 *
 * Copyright 2007-2010 anhe.
 */
package com.anheinno.android.libs.mag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.ui.EasyDialog;

import android.content.Context;
import android.util.Log;

/**
 * 2010-4-28
 *
 * @author 沈瑞恒
 *
 * @version 1.0
 *
 */


public abstract class MAGLinkedCustomControl {
	private Context _context;
	protected JSONArray _params;
	
	public void setParams(JSONArray params) {
		_params = params;
	}
	
	private void setContext(Context context) {
		_context = context;
	}
	
	public Context getContext() {
		return _context;
	}
	
	public abstract void show();

	public static MAGLinkedCustomControl getControl(Context context, String link) {
    	String classname = null;
		JSONArray params = null;
		try {
			JSONObject class_url = new JSONObject(link);
			if (class_url.has("class")) {
				classname = class_url.getString("class");
			}
			if (class_url.has("params")) {
				params = class_url.getJSONArray("params");
			}
		} catch (final JSONException e1) {
			LOG.error("MAGLinkedCustomControl", "getControl from " + link, e1);
		}
		try {
			Class<MAGLinkedCustomControl> control_class = (Class<MAGLinkedCustomControl>)Class.forName(classname);
			// System.out.println(classname);
			MAGLinkedCustomControl control = control_class.newInstance();
			if(control != null) {
				control.setParams(params);
				control.setContext(context);
				return control;
			}
		} catch (ClassNotFoundException e) {
			EasyDialog.remind(context, "ClassNotFoundException: " + e.getMessage());
		} catch (InstantiationException e2) {
			EasyDialog.remind(context, "InstantiationException: " + e2.getMessage());
		} catch (IllegalAccessException e3) {
			EasyDialog.remind(context, "IllegalAccessException: "	+ e3.getMessage() + ", please make this class public.");
		} catch (final Exception e4) {
			LOG.error("MAGLinkedCustomControl", "getControl from " + link, e4);
		}
		return null;
    }
}
