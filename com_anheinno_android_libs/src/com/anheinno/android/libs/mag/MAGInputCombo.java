package com.anheinno.android.libs.mag;

import org.json.JSONObject;

import com.anheinno.android.libs.HTTPRequestString;
import com.anheinno.android.libs.log.LOG;

import android.content.Context;
import android.view.View;



public class MAGInputCombo extends MAGContainerBase implements MAGInputInterface {
		
	public boolean fromJSON(JSONObject o) {
		/// super.fromJSON(o) must be called before retrieving "_value"
		if(!super.fromJSON(o)) {
			return false;
		}

		if (o.has("_value")) {
			setValue(MAGInputBase.retriveValueString(o, "_value"));
		}
		return true;
	}

	public String getAttributeValue(String fieldname) {
		return super.getAttributeValue(fieldname);
	}
	
	public View initField(Context context) {
		MAGContainerBase.initChildFields(this);
		
		MAGLayoutManager manager = new MAGLayoutManager(context);
		manager.setContainer(this);
		
		return manager;
	}

	public String fetchValue() {
		try {
			JSONObject json = new JSONObject();
			MAGComponentInterface[] child = getNamedChildren();
			for(int i = 0; i < child.length; i ++) {
				if(child[i] instanceof MAGInputInterface && !(child[i] instanceof MAGSubmit)) {
					json.put(child[i].id(), ((MAGInputInterface)child[i]).fetchValue());
				}
			}
			return json.toString();
		}catch(final Exception e) {
			LOG.error(this, "fetchValue", e);
		}
		return "";
	}

	public String getQueryString() {
		return HTTPRequestString.getQueryString(id(), fetchValue());
	}

	public boolean isReadOnly() {
		return true;
	}

	public boolean isRequired() {
		MAGComponentInterface[] comp = getNamedChildren();
		for(int i = 0; i < comp.length; i ++) {
			if((comp[i] instanceof MAGInputInterface) && !(comp[i] instanceof MAGSubmit) && ((MAGInputInterface)comp[i]).isRequired()) {
				return true;	
			}
		}
		return false;
	}

	public void setValue(String value) {
		try {
			JSONObject json = new JSONObject(value);
			MAGComponentInterface[] child = getNamedChildren();
			for(int i = 0; i < child.length; i ++) {
				if(child[i] instanceof MAGInputInterface && json.has(child[i].id())) {
					((MAGInputInterface)child[i]).setValue(json.getString(child[i].id()));
				}
			}
		}catch(final Exception e) {
			LOG.error(this, "setValue", e);
		}
	}

	public boolean validate() {
		MAGComponentInterface[] child = getNamedChildren();
		for(int i = 0; i < child.length; i ++) {
			if(child[i] instanceof MAGInputInterface && ((MAGInputInterface)child[i]).isRequired() && !((MAGInputInterface)child[i]).validate()) {
				return false;
			}
		}
		return true;
	}

	public String getInitValue() {
		try {
			JSONObject json = new JSONObject();
			MAGComponentInterface[] child = getNamedChildren();
			for(int i = 0; i < child.length; i ++) {
				if(child[i] instanceof MAGInputInterface && !(child[i] instanceof MAGSubmit)) {
					json.put(child[i].id(), ((MAGInputInterface)child[i]).getInitValue());
				}
			}
			return json.toString();
		}catch(final Exception e) {
			LOG.error(this, "fetchValue", e);
		}
		return "";
	}

}
