/*
 * MAGCustominput.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anheinno.android.libs.ui.EasyDialog;

import android.content.Context;
import android.view.View;



/**
 * MAGCustominput是用于呈现用户定制的输入控件的输入组件。<br>
 * SUBTYPE = "CUSTOM"
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGCustominput extends MAGInputBase {
	private String _classname;
	//private String _value;
	private JSONArray _params;
	private MAGCustominputControl _control;

	public MAGCustominput() {
		super();
		_classname = null;
		_params = null;
		_control = null;
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if(!checkMandatory(o, "_classname")) {
				return false;
			}
			
			_classname = o.getString("_classname");
			
			if (o.has("_params")) {
				_params = o.getJSONArray("_params");
			}

			return super.fromJSON(o);
		} catch (JSONException e) {
		}
		return false;
	}

	public String getAttributeValue(String fieldname) {
		if(fieldname.equals("_classname")) {
			return _classname;
		}else {
			return super.getAttributeValue(fieldname);
		}
	}
	
	public String getControlClassName() {
		return _classname;
	}
	
	// 2010-5-27 增加toJSON
	public JSONObject toJSON() {
		JSONObject obj = super.toJSON();
		try {
			obj.put("_classname", _classname);

			if (_params != null && _params.length() > 0) {
				obj.put("_params", _params);
			}
		} catch (JSONException je) {
			System.err.println(je.toString());
		}
		return obj;
	}

	public View initField(Context context) {
		try {
			Class<MAGCustominputControl> control_class = (Class<MAGCustominputControl>)Class.forName(_classname);
			_control = (MAGCustominputControl) control_class.newInstance();
			_control.setParams(_params);
			_control.setInput(this);
			return _control.initControl(context);
		} catch (ClassNotFoundException e) {
			_control = new MAGCustominputControlDefault();
			_control.setParams(_params);
			_control.setInput(this);
			return _control.initControl(context);
		} catch (InstantiationException e2) {
			EasyDialog.remind(getContext(), "InstantiationException: " + e2.getMessage());
		} catch (IllegalAccessException e3) {
			EasyDialog.remind(getContext(), "IllegalAccessException: " + e3.getMessage() + ", please make this class public.");
		}
		return null;
	}

	public void updateField(View field) {
		_control.updateControl();
	}

	protected boolean setAttribute(String name, String value) {
		if (name.equals("classname")) {
			EasyDialog.remind(getContext(), "classname is readonly attribute");
			return false;
		} else if (name.equals("params")) {
			try {
				_params = new JSONArray(value);
			} catch (JSONException je) {
				return false;
			}
			return true;
		} else if (_control.setAttribute(name, value)) {
			return true;
		}
		if (super.setAttribute(name, value)) {
			return true;
		}
		return false;
	}

	public String getQueryString() {
		return _control.getQueryString();
	}

	public boolean validate() {
		return _control.validate();
	}

	public String fetchValue() {
		return null;
	}
	
	public void releaseResources() {
		_params = null;
		_control = null;
		super.releaseResources();
	}
}
