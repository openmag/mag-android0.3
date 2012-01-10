/**
 * ContactUser.java
 *
 * Copyright 2007-2010 anhe.
 */
package com.anheinno.libs.mag.controls.contacts;

import org.json.JSONException;
import org.json.JSONObject;

public class ContactUser {

	private String _name = "";
	private String _account = "";
	private String _place = "";
	private String _homephone = "";
	private String _mobile = "";
	private String _workphone = "";
	private String _email = "";
	private String _orgname = "";// search 时用

	ContactUser(JSONObject o, String orgname) {
		super();
		fromJSON(o);
		if(orgname != null && orgname.length() > 0) {
			_orgname = orgname;
		}
	}
	
	ContactUser(String name, String account, String orgname) {
		super();
		_name = name;
		_account = account;
		_orgname = orgname;
	}

	/**
	 * @return the _name
	 */
	public String get_name() {
		return _name;
	}

	/**
	 * @return the _account
	 */
	public String get_account() {
		return _account;
	}

	/**
	 * @return the _place
	 */
	public String get_place() {
		return _place;
	}

	/**
	 * @return the _homephone
	 */
	public String get_homephone() {
		return _homephone;
	}

	/**
	 * @return the _mobile
	 */
	public String get_mobile() {
		return _mobile;
	}

	/**
	 * @return the _workphone
	 */
	public String get_workphone() {
		return _workphone;
	}

	/**
	 * @return the _email
	 */
	public String get_email() {
		return _email;
	}

	/**
	 * @return the _orgname
	 */
	public String get_orgname() {
		return _orgname;
	}

	public void fromJSON(JSONObject o) {
		try {
			if (o.has("name")) {
				_name = o.getString("name");
				if (o.has("account")) {
					_account = o.getString("account");
				}
				if (o.has("orgname")) {
					_orgname = o.getString("orgname");
				}
				if (o.has("place")) {
					_place = o.getString("place");
				}
				if (o.has("homephone")) {
					_homephone = o.getString("homephone");
				}
				if (o.has("mobile")) {
					_mobile = o.getString("mobile");
				}
				if (o.has("workphone")) {
					_workphone = o.getString("workphone");
				}
				if (o.has("email")) {
					_email = o.getString("email");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
