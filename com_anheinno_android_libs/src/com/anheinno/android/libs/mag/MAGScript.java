/*
 * MAGScript.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;



/**
 * MAGScript是一个非显示组件。和HTML中的script元素类似，一个MAGScript组件包含了一系列脚本语句，用于触发MAG客户端的一系列行为。<br>
 * TYPE = "SCRIPT"
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
class MAGScript extends MAGComponent {
	String _scripts;
	//public static final String TYPE = "SCRIPT";

	MAGScript() {
		super();
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if(!super.fromJSON(o)) {
				return false;
			}
			
			if(!checkMandatory(o, "_scripts")) {
				return false;
			}

			_scripts = o.getString("_scripts");
			return true;
		} catch (final JSONException e) {
		}
		return false;
	}

	public String getAttributeValue(String fieldname) {
		if(fieldname.equals("_scripts")) {
			return _scripts;
		}else {
			return super.getAttributeValue(fieldname);
		}
	}
	
	// 2010-5-27 增加toJSON
	public JSONObject toJSON() {
		JSONObject obj = super.toJSON();
		/*try {
			obj.put("_group", _scripts);
		} catch (JSONException je) {
			System.err.println(je.toString());
		}*/
		return obj;
	}

	public View initField(Context context) {
		getMAGDocument().registScripts(_scripts);
		return null;
	}

	public void updateField(View f) {
	}

	public void onShowUi() {
		
	}

}
