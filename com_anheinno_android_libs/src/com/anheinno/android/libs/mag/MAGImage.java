/**
 * MAGImage.java
 *
 * Copyright 2007-2010 anhe.
 */
package com.anheinno.android.libs.mag;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;



/**
 * 2010-5-13<br>
 * 
 * MAGImage是一个显示图片的组件。一个MAGImage对应由抬头提示和正文内容组成的一行或一段文字。<br>
 * TYPE = "IMG"<br>
 * 
 * 
 * @version 1.0
 * 
 */
public class MAGImage extends MAGComponent {
	private String _src;
	private String _format;
	//public static final String TYPE = "IMG";

	public MAGImage() {
		super();
		_src = null;
		_format = null;
	}

	public String src() {
		return _src;
	}

	public String format() {
		return _format;
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if(!super.fromJSON(o)) {
				return false;
			}
			
			if(!checkMandatory(o, "_src")) {
				return false;
			}
			
			_src = o.getString("_src");
			
			if (o.has("_format")) {
				_format = o.getString("_format");
			}
			
			return true;
		} catch (final JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getAttributeValue(String fieldname) {
		if(fieldname.equals("_src")) {
			return _src;
		}else if(fieldname.equals("_format")) {
			return _format;
		}else {
			return super.getAttributeValue(fieldname);
		}
	}

	// 2010-5-27 增加toJSON
	public JSONObject toJSON() {
		JSONObject obj = super.toJSON();
		try {
			obj.put("_src", _src);
			if (_format != null) {
				obj.put("_format", _format);
			}
		} catch (JSONException je) {
			System.err.println(je.toString());
		}
		return obj;
	}

	public View initField(Context con) {
		return new MAGImageFieldGauge(con, this);
	}

	public void updateField(View f) {
	}

	protected boolean setAttribute(String name, String value) {
		// if (name.equals("src")) {
		// _src = value;
		// return true;
		// }
		// if (super.setAttribute(name, value)) {
		// return true;
		// }
		return false;
	}

}
