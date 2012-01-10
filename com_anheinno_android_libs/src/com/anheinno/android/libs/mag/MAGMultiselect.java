/*
 * MAGMultiselect.java
 *
 * <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import java.util.Hashtable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anheinno.android.libs.graphics.GraphicUtilityClass;
import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.ui.EasyDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

/**
 * MAGMultiselect是让用户用一系列预设选项中选择多个选项的输入组件。
 * MAGMultiselect的呈现形式为提示文字后跟由所有预设选项组成的若干个复选框(Checkbox)。<br>
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGMultiselect extends MAGInputBase {
	JSONArray _options;

	MAGMultiselect() {
		super();
		_options = null;
	}

	public boolean fromJSON(JSONObject o) {
		try {

			if (!checkMandatory(o, "_options")) {
				return false;
			}

			_options = o.getJSONArray("_options");

			return super.fromJSON(o);

		} catch (JSONException e) {
		}
		return false;
	}

	// 2010-5-27 增加toJSON
	public JSONObject toJSON() {
		JSONObject obj = super.toJSON();
		try {
			obj.put("_options", _options);
		} catch (JSONException je) {
			System.err.println(je.toString());
		}
		return obj;
	}

	// 2010-5-20 增加只读控制
	public View initField(Context context) {
		// VerticalFieldManager pane = new VerticalFieldManager(_style_bits);
		MAGColorLabelManager clm = new MAGColorLabelManager(context, this);

		LinearLayout pane = new LinearLayout(getContext());
		pane.setOrientation(LinearLayout.VERTICAL);
		clm.setField(pane, false);
		updateField(clm);

		return clm;
	}

	private LinearLayout getVerticalFieldManager() {
		return (LinearLayout) ((MAGColorLabelManager) getField()).getField();
	}

	public void updateField(View f) {
		MAGColorLabelManager clm = (MAGColorLabelManager) f;
		LinearLayout pane = (LinearLayout) clm.getField();
		pane.removeAllViews();

		// long style = _style_bits;
		// if (isReadOnly()) {
		// style |= Field.READONLY;
		// }
		Hashtable<String, String> sel_opts = new Hashtable<String, String>();
		if (getInitValue() != null && getInitValue().length() > 0) {
			try {
				JSONArray sels = new JSONArray(getInitValue());
				for (int i = 0; i < sels.length(); i++) {
					String opt = sels.getString(i);
					sel_opts.put(opt, opt);
				}
			} catch (final Exception e) {
				LOG.error(this, "getInitValue", e);
			}
		}

		for (int i = 0; i < _options.length(); i++) {
			try {
				JSONObject opt = _options.getJSONObject(i);
				CheckBox ck = new CheckBox(getContext());
				ck.setTextColor(style().getTitleColor() == GraphicUtilityClass.INVALID_COLOR ? Color.BLACK : style().getTitleColor());
				ck.setText(opt.getString("_text"));
				ck.setChecked(sel_opts.containsKey(opt.getString("_value")));
				ck.setEnabled(!isReadOnly());
				pane.addView(ck);
			} catch (final Exception e) {
				LOG.error(this, "updateUi", e);
			}
		}
	}

	public String fetchValue() {
		JSONArray array = new JSONArray();
		LinearLayout vfm = getVerticalFieldManager();
		for (int i = 0; i < vfm.getChildCount(); i++) {
			CheckBox cf = (CheckBox) vfm.getChildAt(i);
			if (cf.isChecked()) {
				try {
					array.put(_options.getJSONObject(i).getString("_value"));
				} catch (final Exception e) {
					LOG.error(this, "fetchValue", e);
				}
			}
		}
		return array.toString();
	}

	public boolean validate() {
		LinearLayout vfm = getVerticalFieldManager();
		for (int i = 0; i < vfm.getChildCount(); i++) {
			CheckBox cf = (CheckBox) vfm.getChildAt(i);
			if (cf.isChecked()) {
				return true;
			}
		}
		EasyDialog.longAlert(getContext(), title() + ": Please select at least one option!");
		return false;
	}

}
