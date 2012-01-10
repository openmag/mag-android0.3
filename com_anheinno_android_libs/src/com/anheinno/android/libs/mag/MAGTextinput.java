/*
 * MAGTextinput.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;

import com.anheinno.android.libs.R;
import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.ui.EasyDialog;

/**
 * MAGTextinput是支持用户输入文字的组件。呈现形式为提示文字后跟文本输入区域。<br>
 * SUBTYPE = "TEXT"
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGTextinput extends MAGInputBase {
	private String _filter;

	public static final String TEXTINPUT_FILTER_EMAIL = "_email";
	public static final String TEXTINPUT_FILTER_FILENAME = "_filename";
	public static final String TEXTINPUT_FILTER_HEXDECIMAL = "_hexdecimal";
	public static final String TEXTINPUT_FILTER_IP = "_ip";
	public static final String TEXTINPUT_FILTER_LOWERCASE = "_lowercase";
	public static final String TEXTINPUT_FILTER_NUMERIC = "_numeric";
	public static final String TEXTINPUT_FILTER_PHONE = "_phone";
	public static final String TEXTINPUT_FILTER_UPPERCASE = "_uppercase";
	public static final String TEXTINPUT_FILTER_URL = "_url";
	public static final String TEXTINPUT_FILTER_PASSWORD = "_password";
	public static final String TEXTINPUT_FILTER_BASIC = "_basic";

	public MAGTextinput() {
		super();
		_filter = null;
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if (o.has("_filter")) {
				_filter = o.getString("_filter");
			} else {
				_filter = "";
			}

			return super.fromJSON(o);
		} catch (final Exception e) {
			LOG.error(this, "fromJSON", e);
		}
		return false;
	}

	public String getAttributeValue(String fieldname) {
		if (fieldname.equals("_filter")) {
			return _filter;
		} else {
			return super.getAttributeValue(fieldname);
		}
	}

	// 2010-5-27 增加toJSON
	public JSONObject toJSON() {
		JSONObject obj = super.toJSON();
		try {
			if (_filter != null && _filter.length() > 0) {
				obj.put("_filter", _filter);
			}
		} catch (final Exception e) {
			LOG.error(this, "toJSON", e);
		}
		return obj;
	}

	// 2010-5-20 增加只读控制
	public View initField(Context context) {
		MAGTextinputField tf = new MAGTextinputField(context, this, getInitValue(), _filter);
		// tf.initTextField();
		updateField(tf);
		return tf;
	}

	public void updateField(View f) {
		MAGTextinputField tf = (MAGTextinputField) f;
		tf.setReadonly(isReadOnly());
		tf.setText(getInitValue());
	}

	public boolean validate() {
		if (((MAGTextinputField) getField()).getText().trim().length() > 0) {
			return true;
		} else {
			if (getInitVmsg() != null && getInitVmsg().length() > 0) {
				EasyDialog.longAlert(getContext(), getInitVmsg());
			} else {
				String data_show = title();
				if (data_show.length() > 1 && (data_show.endsWith(":") || data_show.endsWith("："))) {
					data_show = data_show.substring(0, data_show.length() - 1) + "!";
				}
				EasyDialog.longAlert(getContext(), getContext().getString(R.string.mag_text_input_cannot_be_empty) + data_show);
			}
			return false;
		}
	}

	public String fetchValue() {
		return ((MAGTextinputField) getField()).getText().trim();
	}

}
