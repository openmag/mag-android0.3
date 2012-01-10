/*
 * MAGNoteField.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import org.json.JSONException;
import org.json.JSONObject;

import com.anheinno.android.libs.graphics.LeftRightLabelField;

import android.content.Context;
import android.view.View;


/**
 * AGScript是一个非显示组件。和HTML中的script元素类似，
 * 一个MAGScript组件包含了一系列脚本语句，用于触发MAG客户端的一系列行为。<br>
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGNote extends MAGComponent {
	private String _note;

	public MAGNote() {
		super();
		_note = null;
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if(!super.fromJSON(o)) {
				return false;
			}
			if (o.has("_note")) {
				_note = o.getString("_note");
			} else {
				_note = "";
			}
			return true;
		} catch (final JSONException e) {
		}
		return false;
	}

	// 2010-5-27 增加toJSON
	public JSONObject toJSON() {
		JSONObject obj = super.toJSON();
		try {
			if (_note != null && _note.length() > 0) {
				obj.put("_note", _note);
			}
		} catch (JSONException je) {
			System.err.println(je.toString());
		}
		return obj;
	}

	public View initField(Context context) {
		LeftRightLabelField lrlf = new LeftRightLabelField(context,title(), _note);
		lrlf.setFont(0.7f);
		lrlf.setColor(style().getColor());
		return lrlf;
	}

	public void updateField(View f) {
		((LeftRightLabelField) f).setText(title(), _note);
	}

	public String getAttributeValue(String fieldname) { 
		if(fieldname.equals("_note")) {
			return _note;
		}else {
			return super.getAttributeValue(fieldname);
		}
	}
	
	protected boolean setAttribute(String name, String value) {
		if (name.equals("_note")) {
			_note = value;
			return true;
		}
		if (super.setAttribute(name, value)) {
			return true;
		}
		return false;
	}

}
