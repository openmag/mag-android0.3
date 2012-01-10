/**
 * MAGTieredSelect.java
 *
 * Copyright 2007-2011 anhe.
 */
package com.anheinno.android.libs.mag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;

/**
 * 2011-2-9
 * 
 * MAGTieredselect是让用户用从多个关联的下拉列表组中选择一个选项的输入组件，关联是指选定第一个选项后，第二个下拉列表的选项会跟随变化。
 * MAGTieredselect的呈现形式为提示文字后跟多个。<br>
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 * 
 */

public class MAGTieredSelect extends MAGInputBase {
	private JSONArray _options;

	public MAGTieredSelect() {
		super();
		_options = null;
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if(!checkMandatory(o, "_options")) {
				return false;
			}
			
			_options = o.getJSONArray("_options");

			return super.fromJSON(o);

		} catch (JSONException e) {
		}
		return false;
	}

	public String fetchValue() {
		JSONArray data = ((MAGTieredSelectField)getField()).getSelectValue();
		return data.toString();
	}

	public MAGStyle getStyle() {
		return style();
	}

	public boolean validate() {
		return true;
	}

	public View initField(Context context) {
		return new MAGTieredSelectField(context, this);
	}
	
	public JSONArray getOptions() {
		return _options;
	}
	
	public void updateField(View f) {
		MAGTieredSelectField tsf = (MAGTieredSelectField)f;
		tsf.updateUi();
	}

	public void onShowUi() {
		MAGTieredSelectField tsf = (MAGTieredSelectField)getField();
		tsf.updateUi();
	}

}
