package com.anheinno.android.libs.mag;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;

import com.anheinno.android.libs.R;
import com.anheinno.android.libs.UtilClass;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.ui.EasyDialog;

public class MAGInputDuplicator extends MAGInputBase implements MAGContainerInterface {
	private boolean _insertable;
	private boolean _deleteable;
	private boolean _sortable;
	private int _min_ctrl_cnt;
	private int _max_ctrl_cnt;
	private JSONObject _template;
	private Vector<MAGInputInterface> _input_list;

	public MAGInputDuplicator() {
		super();
		_insertable = true;
		_deleteable = true;
		_sortable = true;
		_min_ctrl_cnt = -1;
		_max_ctrl_cnt = -1;
		_template = null;
		_input_list = new Vector<MAGInputInterface>();
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if (!checkMandatory(o, "_template")) {
				return false;
			}

			_template = o.getJSONObject("_template");

			if (o.has("_insert")) {
				_insertable = o.getString("_insert").equalsIgnoreCase("true");
			} else {
				_insertable = true;
			}
			if (o.has("_delete")) {
				_deleteable = o.getString("_delete").equalsIgnoreCase("true");
			} else {
				_deleteable = true;
			}
			if (o.has("_sort")) {
				_sortable = o.getString("_sort").equalsIgnoreCase("true");
			} else {
				_sortable = true;
			}

			if (o.has("_min_count")) {
				_min_ctrl_cnt = o.getInt("_min_count");
			}

			if (o.has("_max_count")) {
				_max_ctrl_cnt = o.getInt("_max_count");
			}

			return super.fromJSON(o);

		} catch (final Exception e) {
			LOG.error(this, "fromJSON", e);
		}
		return false;
	}

	public String getAttributeValue(String fieldname) {
		if (fieldname.indexOf('.') > 0) {
			return MAGContainerBase.getChildrenAttributeValue(this, fieldname);
		} else if (fieldname.equals("_insert")) {
			return UtilClass.boolean2String(_insertable);
		} else if (fieldname.equals("_delete")) {
			return UtilClass.boolean2String(_deleteable);
		} else if (fieldname.equals("_sort")) {
			return UtilClass.boolean2String(_sortable);
		} else if (fieldname.equals("_min_count")) {
			return "" + _min_ctrl_cnt;
		} else if (fieldname.equals("_max_count")) {
			return "" + _max_ctrl_cnt;
		} else {
			return super.getAttributeValue(fieldname);
		}
	}

	protected boolean isInsertable() {
		return _insertable;
	}

	protected boolean isDeletable() {
		return _deleteable;
	}

	protected boolean isSortable() {
		return _sortable;
	}

	// 2010-5-20 增加只读控制
	public View initField(Context context) {
		MAGInputDuplicatorField f = new MAGInputDuplicatorField(context, this);
		f.initField(getContext());
		updateField(f);
		return f;
	}

	public void updateField(View f) {
		MAGInputDuplicatorField dupf = (MAGInputDuplicatorField) f;

		BackgroundDescriptor bg_desc = null;
		TextStyleDescriptor txt_desc = null;

		bg_desc = style().getBackground("button");
		if (bg_desc != null) {
			dupf.setButtonBackground(bg_desc);
		}
		bg_desc = style().getBackground("focus-button");
		if (bg_desc != null) {
			dupf.setButtonFocusBackground(bg_desc);
		}

		txt_desc = style().getTextStyle("up");
		if (txt_desc != null) {
			dupf.setUpButtonStyle(txt_desc);
		}

		txt_desc = style().getTextStyle("focus-up");
		if (txt_desc != null) {
			dupf.setUpButtonFocusStyle(txt_desc);
		}

		txt_desc = style().getTextStyle("insert");
		if (txt_desc != null) {
			dupf.setInsertButtonStyle(txt_desc);
		}

		txt_desc = style().getTextStyle("focus-insert");
		if (txt_desc != null) {
			dupf.setInsertButtonFocusStyle(txt_desc);
		}

		txt_desc = style().getTextStyle("delete");
		if (txt_desc != null) {
			dupf.setDeleteButtonStyle(txt_desc);
		}

		txt_desc = style().getTextStyle("focus-delete");
		if (txt_desc != null) {
			dupf.setDeleteButtonFocusStyle(txt_desc);
		}

		txt_desc = style().getTextStyle("down");
		if (txt_desc != null) {
			dupf.setDownButtonStyle(txt_desc);
		}

		txt_desc = style().getTextStyle("focus-down");
		if (txt_desc != null) {
			dupf.setDownButtonFocusStyle(txt_desc);
		}

	}

	public boolean validate() {
		if (_input_list.size() == 0) {
			if (getInitVmsg() != null && getInitVmsg().length() > 0) {
				EasyDialog.longAlert(getContext(), getInitVmsg());
			} else {
				String data_show = title();
				if (data_show.length() > 1 && (data_show.endsWith(":") || data_show.endsWith("："))) {
					data_show = data_show.substring(0, data_show.length() - 1) + "!";
				}
				EasyDialog.longAlert(getContext(), getContext().getString(R.string.mag_input_duplicator_cannot_be_empty_prompt) + data_show);
			}
			return false;
		} else {
			for (int i = 0; i < _input_list.size(); i++) {
				MAGInputInterface input = (MAGInputInterface) _input_list.elementAt(i);
				if (!input.validate()) {
					return false;
				}
			}
			return true;
		}
	}

	public String fetchValue() {
		try {
			JSONArray value = new JSONArray();
			for (int i = 0; i < _input_list.size(); i++) {
				MAGInputInterface input = (MAGInputInterface) _input_list.elementAt(i);
				value.put(input.fetchValue());
			}
			return value.toString();
		} catch (final Exception e) {
			LOG.error(this, "fetchValue", e);
		}
		return "";
	}

	public void setValue(String value) {
		try {
			_input_list.removeAllElements();
			JSONArray list = null;
			if (value != null && value.length() > 0) {
				list = new JSONArray(value);
			} else {
				list = new JSONArray();
			}
			// System.out.println("_template=" + _template.toString());
			for (int i = 0; i < list.length(); i++) {
				// System.out.println("init input control " +
				// list.getString(i));
				MAGInputInterface input = (MAGInputInterface) MAGComponent.parseJSON(this, _template);
				if (input != null) {
					input.setValue(list.getString(i));
				}
			}
		} catch (final Exception e) {
			LOG.error(this, "setValue", e);
		}
	}

	public void addChild(MAGComponentInterface child) {
		_input_list.addElement((MAGInputInterface) child);
	}

	public int childrenNum() {
		return _input_list.size();
	}

	public MAGComponent getChild(int idx) {
		if (idx >= 0 && idx < _input_list.size()) {
			return (MAGComponent) _input_list.elementAt(idx);
		}
		return null;
	}

	public void removeChild(MAGComponentInterface child) {
		_input_list.removeElement(child);
	}

	public MAGComponent getChild(String id) {
		return null;
	}

	public MAGComponent[] getNamedChildren() {
		return null;
	}

	public void invalidateChild(MAGComponentInterface comp) {

	}

	public void removeLayoutManager() {
	}

	public void setLayoutManager(MAGLayoutManager manager) {
	}

	protected boolean upInputControl(int index) {
		if (index > 0 && index < _input_list.size()) {
			MAGInputInterface tmp = _input_list.elementAt(index);
			_input_list.removeElementAt(index);
			_input_list.insertElementAt(tmp, index - 1);
			return true;
		} else {
			return false;
		}
	}

	protected boolean downInputControl(int index) {
		if (index < _input_list.size() - 1 && index >= 0) {
			MAGInputInterface tmp = _input_list.elementAt(index);
			_input_list.removeElementAt(index);
			_input_list.insertElementAt(tmp, index + 1);
			return true;
		} else {
			return false;
		}
	}

	protected boolean deleteInputControl(int index) {
		if (index >= 0 && index < _input_list.size() && (_min_ctrl_cnt <= 0 || _input_list.size() > _min_ctrl_cnt)) {
			getChild(index).unlink();
			return true;
		} else {
			if (_min_ctrl_cnt > 0 && _input_list.size() <= _min_ctrl_cnt) {
				EasyDialog.longAlert(getContext(), getContext().getString(R.string.mag_input_duplicator_exceed_min_prompt) + " " + _min_ctrl_cnt + "!");
			}
			return false;
		}
	}

	protected boolean insertInputControl(int index) {
		if (index >= 0 && index <= _input_list.size() && (_max_ctrl_cnt <= 0 || _input_list.size() < _max_ctrl_cnt)) {
			MAGInputInterface input = (MAGInputInterface) MAGComponent.parseJSON(this, _template);
			System.out.println("_input_list size before " + _input_list.size());
			/**
			 * input has been appended to _input_list should be removed first
			 * then insert into the right position
			 */
			_input_list.removeElementAt(_input_list.size() - 1);
			_input_list.insertElementAt(input, index);
			System.out.println("_input_list size after " + _input_list.size());
			return true;
		} else {
			if (_max_ctrl_cnt > 0 && _input_list.size() >= _max_ctrl_cnt) {
				EasyDialog.longAlert(getContext(), getContext().getString(R.string.mag_input_duplicator_exceed_max_prompt) + " " + _min_ctrl_cnt + "!");
			}
			return false;
		}
	}

}
