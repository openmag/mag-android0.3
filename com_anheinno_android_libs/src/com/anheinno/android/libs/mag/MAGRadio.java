package com.anheinno.android.libs.mag;

import org.json.JSONException;
import org.json.JSONObject;
import com.anheinno.android.libs.HTTPRequestString;
import com.anheinno.android.libs.UtilClass;
import com.anheinno.android.libs.graphics.GraphicUtilityClass;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;

/**
 * MAGRadio是让用户从若干选项中选择一个选项的输入组件。和MAGSelect不同，一个MAGRadio是一个Radio输入控件，
 * 多个MAGRadio组成一个选项组。<br>
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGRadio extends MAGInputBase implements View.OnClickListener {
	private String _group = null;
	private boolean _checked = false;

	public MAGRadio() {
		super();
		_group = null;
		_checked = false;
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if (!checkMandatory(o, "_group")) {
				return false;
			}
			if (!checkMandatory(o, "_value")) {
				return false;
			}

			_group = o.getString("_group");

			if (o.has("_checked") && o.getString("_checked").equalsIgnoreCase("true")) {
				_checked = true;
			} else {
				_checked = false;
			}

			return super.fromJSON(o);

		} catch (JSONException e) {
		}
		return false;
	}

	public String getAttributeValue(String fieldname) {
		if(fieldname.equals("_group")) {
			return _group;
		}else if(fieldname.equals("_checked")) {
			return UtilClass.boolean2String(_checked);
		}else {
			return super.getAttributeValue(fieldname);
		}
	}
	
	// 2010-5-27 增加toJSON
	public JSONObject toJSON() {
		JSONObject obj = super.toJSON();
		try {
			obj.put("_group", _group);
			obj.put("_checked", _checked);
		} catch (JSONException je) {
			System.err.println(je.toString());
		}
		return obj;
	}

	// 2010-5-20 增加只读控制
	public View initField(Context context) {
		if (getParent() instanceof MAGPanel) {
			RadioButton rbf = new RadioButton(getContext());
			rbf.setEnabled(!isReadOnly());
			if(style().getColor() != GraphicUtilityClass.INVALID_COLOR) {
				rbf.setTextColor(style().getColor());
			}
			rbf.setText(title());
			rbf.setSelected(_checked);
			rbf.setEnabled(!isReadOnly());

			if(_group != null && _group.length() > 0) {
				MAGPanel panel = getMAGPanel();
				MAGRadioGroup rbg = panel.getMAGRadioGroup(_group);
				if(rbg != null) {
					rbg.addMAGRadio(this);
					rbf.setOnClickListener(this);
				}
			}
			return rbf;
		} else {
			return null;
		}
	}

	public void updateField(View f) {
		if (f != null) {
			((RadioButton) f).setText(title());
		}
	}

	protected boolean setAttribute(String name, String value) {
		if (name.equals("group")) {
			// UtilClass.showInfo("group attribute is readonly!");
			return false;
		}
		if (super.setAttribute(name, value)) {
			return true;
		}
		return false;
	}

	public String getQueryString() {
		if (getField() != null) {
			RadioButton rbf = (RadioButton) getField();
			if (rbf.isChecked()) {
				return HTTPRequestString.getQueryString(_group, getInitValue());
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	public boolean validate() {
		return true;
	}

	public String fetchValue() {
		return getInitValue();
	}

	public void onClick(View v) {
		//System.out.println(this + " is clicked!!!!!!");
		MAGPanel panel = getMAGPanel();
		if(panel != null) {
			MAGRadioGroup group = panel.getMAGRadioGroup(_group);
			if(group != null) {
				group.check(this);
			}
		}
	}
	
	public void setChecked(boolean checked) {
		//System.out.println(this + (checked?"checked":"unchecked"));
		RadioButton rb = (RadioButton)getField();
		rb.setChecked(checked);
		//rb.postInvalidate();
	}
}
