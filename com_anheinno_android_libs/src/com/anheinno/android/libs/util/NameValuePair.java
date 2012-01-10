package com.anheinno.android.libs.util;

public class NameValuePair {
	private String _name;
	private String _value;
	
	public NameValuePair(String val) {
		_name = null;
		_value = null;
		parse(val);
	}
	
	public NameValuePair(String name, String value) {
		_name = name.trim();
		_value = value.trim();
	}
	
	public void parse(String val) {
		int pos = val.indexOf('=');
		if(pos > 0) {
			_name = val.substring(0, pos).trim();
			if(pos + 1 < val.length()) {
				_value = val.substring(pos+1).trim();
			}else {
				_value = null;
			}
		}else {
			_name = null;
			_value = val;
		}
	}
	
	public String getName() {
		return _name;
	}
	
	public String getValueString() {
		return _value;
	}
	
	public int getValueInt() {
		return Integer.parseInt(_value);
	}
	
	public boolean getBoolean() {
		if(_value.equalsIgnoreCase("true")) {
			return true;
		}else {
			return false;
		}
	}
	
	public double getValueDouble() {
		if(_value.endsWith("%")) {
			return Double.parseDouble(_value.substring(0, _value.length()-1))/100;
		}else {
			return Double.parseDouble(_value);
		}
	}
	
	public float getValueFloat() {
		if(_value.endsWith("%")) {
			return Float.parseFloat(_value.substring(0, _value.length()-1))/100;
		}else {
			return Float.parseFloat(_value);
		}
	}
	
	public boolean isName(String n) {
		if(_name != null && _name.equalsIgnoreCase(n)) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean isValue(String v) {
		if(_value != null && _value.equalsIgnoreCase(v)) {
			return true;
		}else {
			return false;
		}
	}
}
