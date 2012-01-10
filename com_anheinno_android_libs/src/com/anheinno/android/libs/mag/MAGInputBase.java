/*
 * MAGInput.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.anheinno.android.libs.HTTPRequestString;
import com.anheinno.android.libs.UtilClass;
import com.anheinno.android.libs.log.LOG;



/**
 * TYPE = "INPUT"
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public abstract class MAGInputBase extends MAGComponent implements MAGInputInterface {

	private boolean _read_only;
	private boolean _required;
	private String _value;
	private String _vmsg;

	public MAGInputBase() {
		super();
		_value = null;
		_vmsg = null;
		_read_only = false;
		_required = false;
	}

	public boolean fromJSON(JSONObject o) {
		try {
			super.fromJSON(o);
			
			if (o.has("_nonempty") && o.getString("_nonempty").equalsIgnoreCase("true")) {
				_required = true;
			}
			
			if (o.has("_readonly") && o.getString("_readonly").equalsIgnoreCase("true")) {
				_read_only = true;
			}
			
			if(o.has("_value")) {
				setValue(retriveValueString(o, "_value"));
			}else {
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
		if(fieldname.equals("_value")) {
			return _value;
		}else if(fieldname.equals("_readonly")) {
			return UtilClass.boolean2String(_read_only);
		}else if(fieldname.equals("_nonempty")) {
			return UtilClass.boolean2String(_required);
		}else {
			return super.getAttributeValue(fieldname);
		}
	}

	// 2010-5-27 增加toJSON
	public JSONObject toJSON() {
		JSONObject obj = super.toJSON();
		try {
			if (_read_only) {
				obj.put("_readonly", "true");
			} else {
				obj.put("_readonly", "false");
			}
			if(_required) {
				obj.put("_nonempty", "true");
			}else {
				obj.put("_nonempty", "false");
			}
			if(_value != null && _value.length() > 0) {
				obj.put("_value", _value);
			}
		} catch (JSONException je) {
			System.err.println(je.toString());
		}
		return obj;
	}

	public MAGInputInterface[] getInputSiblings() {
		Vector<MAGInputInterface> sib = new Vector<MAGInputInterface>();
		MAGComponentInterface[] namedsib = getNamedSiblings();
		for (int i = 0; namedsib != null && i < namedsib.length; i++) {
			if (namedsib[i] instanceof MAGInputInterface) {
				sib.addElement((MAGInputInterface)namedsib[i]);
			}
		}
		if (sib.size() > 0) {
			MAGInputInterface[] ret = new MAGInputInterface[sib.size()];
			sib.copyInto(ret);
			return ret;
		} else {
			return null;
		}
	}

	protected boolean setAttribute(String name, String value) {
		if (name.equals("readonly")) {
			if (value.equals("true")) {
				_read_only = true;
			} else {
				_read_only = false;
			}
			return true;
		} else if (name.equals("nonempty")) {
			if (value.equals("true")) {
				_required = true;
			} else {
				_required = false;
			}
			return true;
		}
		if (super.setAttribute(name, value)) {
			return true;
		}
		return false;
	}

	public boolean isReadOnly() {
		return _read_only;
	}

	public boolean isRequired() {
		return _required;
	}
	
	public void setValue(String value) {
		_value = value;
	}
	
	public String getInitValue() {
		return _value;
	}
	
	public void setVmsg(String vmsg) {
		_vmsg = vmsg;
	}
	
	public String getInitVmsg() {
		return _vmsg;
	}
	
	public String getQueryString() {
		return HTTPRequestString.getQueryString(id(), fetchValue());
	}
	
	public static String retriveValueString(JSONObject o, String key) {
		try {
			try {
				return o.getJSONObject(key).toString();
			}catch(final JSONException e) {
				try {
					return o.getJSONArray(key).toString();
				}catch(final JSONException je) {
					return o.getString(key);
				}
			}
		}catch(final Exception e) {
			LOG.error("MAGInputBase", "retriveValueString", e);
		}
		return "";
	}
}
