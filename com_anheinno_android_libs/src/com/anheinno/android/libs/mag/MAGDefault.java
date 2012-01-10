/*
 * MAGDefault.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.anheinno.android.libs.log.LOG;


/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
class MAGDefault extends MAGComponent {
	private String _type;
	
	public MAGDefault() {
		super();
		_type = null;
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if(!super.fromJSON(o)) {
				return false;
			}
			
			if(!checkMandatory(o, "_type")) {
				return false;
			}
			
			_type = o.getString("_type");
			return true;
		}catch(final Exception e) {
			LOG.error(this, "fromJSON", e);
		}
		return false;
	}

	// 2010-5-27 增加toJSON
	public JSONObject toJSON() {
		JSONObject obj = super.toJSON();
		return obj;
	}

	public View initField(Context context) {
		TextView text = new TextView(context);
		text.setText(title() + "(" + _type + ")");
		return text;
	}

	public void updateField(View f) {
		((TextView)f).setText(title() + "(" + _type + ")");
	}

	public void onShowUi() {
		
	}
}
