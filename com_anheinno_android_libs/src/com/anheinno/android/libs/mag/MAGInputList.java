package com.anheinno.android.libs.mag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;

import com.anheinno.android.libs.HTTPRequestString;
import com.anheinno.android.libs.R;
import com.anheinno.android.libs.UtilClass;
import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.ui.EasyDialog;

public class MAGInputList extends MAGList implements MAGInputInterface {

	private boolean _read_only;
	private boolean _required;
	private String _value;
	private String _vmsg;
	private boolean[] _selected;

	public boolean fromJSON(JSONObject o) {
		try {
			if (!super.fromJSON(o)) {
				return false;
			}

			if (o.has("_nonempty") && UtilClass.string2Boolean(o.getString("_nonempty"))) {
				_required = true;
			}

			if (o.has("_readonly") && UtilClass.string2Boolean(o.getString("_readonly"))) {
				_read_only = true;
			}

			if (o.has("_value")) {
				setValue(MAGInputBase.retriveValueString(o, "_value"));
			} else {
				setValue("");
			}

			
			if(o.has("_vmsg")) {
				setVmsg(o.getString("_vmsg"));
			}else {
				setVmsg("");
			}
			
			return true;
		} catch (JSONException e) {
			LOG.error(this, "fromJSON", e);
		}
		return false;
	}

	public String getAttributeValue(String fieldname) {
		if (fieldname.equals("_value")) {
			return fetchValue();
		} else if (fieldname.equals("_readonly")) {
			return UtilClass.boolean2String(_read_only);
		} else if (fieldname.equals("_nonempty")) {
			return UtilClass.boolean2String(_required);
		} else {
			return super.getAttributeValue(fieldname);
		}
	}

	public View initField(Context context) {
		MAGContainerBase.initChildFields(this);
		MAGListField f = new MAGListField(context, this);
		return f;
	}

	public String fetchValue() {
		try {
			JSONArray array = new JSONArray();
			for (int i = 0; i < childrenNum(); i++) {
				MAGComponentInterface comp = getChild(i);
				if (_selected[i] && comp.id() != null && comp.id().length() > 0) {
					array.put(comp.id());
				}
			}
			return array.toString();
		} catch (final Exception e) {

		}
		return null;
	}

	public String getInitValue() {
		return _value;
	}

	public String getQueryString() {
		String value = fetchValue();
		if (value != null) {
			return HTTPRequestString.getQueryString(id(), value);
		} else {
			return null;
		}
	}

	public boolean isReadOnly() {
		return _read_only;
	}

	public boolean isRequired() {
		return _required;
	}

	public void setValue(String value) {
		_value = value;

		_selected = new boolean[childrenNum()];
		for (int i = 0; i < childrenNum(); i++) {
			MAGComponentInterface comp = getChild(i);
			if (comp.id() != null && comp.id().length() > 0 && isComponentSelected(comp.id())) {
				_selected[i] = true;
			} else {
				_selected[i] = false;
			}
		}
	}

	public void setVmsg(String vmsg) {
		_vmsg = vmsg;
	}
	
	public String getInitVmsg() {
		return _vmsg;
	}
	
	private boolean isComponentSelected(String id) {
		try {
			JSONArray array = new JSONArray(_value);
			for (int i = 0; i < array.length(); i++) {
				if (array.getString(i).equals(id)) {
					return true;
				}
			}
		} catch (final Exception e) {

		}
		return false;
	}

	public boolean validate() {
		// JSONArray data = ((MAGInfoGridField) getField()).getSelectId();
		// if (data.length() > 0) {
		// return true;
		// } else {
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
		// }
	}

	protected boolean isAllSelected() {
		for (int i = super.getPageOffset(); i < super.getPageOffset() + super.getPageItemCount(); i++) {
			if (!_selected[i]) {
				return false;
			}
		}
		return true;
	}

	protected void selectAll() {
		boolean set = true;
		if (isAllSelected()) {
			set = false;
		}
		for (int i = super.getPageOffset(); i < super.getPageOffset() + super.getPageItemCount(); i++) {
			_selected[i] = set;
		}
	}

	protected void setSelected(int index, boolean selected) {
		_selected[index] = selected;
	}

	protected boolean isSelected(int index) {
		if (index >= 0 && index < _selected.length) {
			return _selected[index];
		} else {
			return false;
		}
	}

	protected void sortChildren() {
		String val = fetchValue();
		super.sortChildren();
		setValue(val);
	}

}
