package com.anheinno.android.libs.mag;

import org.json.JSONObject;

import com.anheinno.android.libs.JSONBrowserLink;
import com.anheinno.android.libs.log.LOG;

import android.content.Context;
import android.view.View;

public class MAGFrame extends MAGComponent implements MAGContainerInterface {
	
	private JSONBrowserLink _link;
	private MAGDocument _mag_document;
	
	public MAGFrame() {
		super();
		
		_mag_document = null;
		_link = null;
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if(super.fromJSON(o)) {
				
				if(!super.checkMandatory(o, "_link")) {
					return false;
				}
				
				_link = new JSONBrowserLink(getContext());
				_link.setURL(getMAGDocument().getAbsoluteURL(o.getString("_link")));
				
				if(_link.isValidURL()) {
					if(o.has("_expire")) {
						_link.setExpireMilliseconds(o.getLong("_expire"));
					}
					if(o.has("_notify")) {
						_link.setNotify(o.getString("_notify").equalsIgnoreCase("true"));
					}
					if(o.has("_save")) {
						_link.setSaveHistory(o.getString("_save").equalsIgnoreCase("true"));
					}
					
					if(_link.getExpireMilliseconds() > 0) {
						getMAGDocument().addCachedLink(_link.getURL());
					}
					
				}
				
				return true;
			}else {
				return false;
			}
		}catch(final Exception e) {
			LOG.error(this, "fromJSON", e);
			return false;
		}
	}
	
	public JSONBrowserLink getLink() {
		return _link;
	}

	public View initField(Context con) {
		MAGFrameField panel = new MAGFrameField(con, this);
		getMAGDocumentScreen().registerMAGDocumentContainer(panel);
		return panel;
	}
	
	public void updateField(View field) {
		getMAGDocumentScreen().disableScroll();
		if(getLink() != null && getLink().isValidURL()) {
			((MAGFrameField)getField()).open(getLink(), false, null);
		}
	}

	public void addChild(MAGComponentInterface child) {
		if(child instanceof MAGDocument) {
			if(_mag_document != null) {
				removeMAGDocument();
			}
			_mag_document = (MAGDocument) child;
		}
	}

	public void removeChild(MAGComponentInterface child) {
		if(_mag_document == child) {
			removeMAGDocument();
		}
	}
	
	private void removeMAGDocument() {
		//_mag_document.getMAGDocumentField().unload();
		if(_mag_document != null) {
			//_mag_document.releaseResources();
			_mag_document = null;
		}
	}

	public int childrenNum() {
		if(_mag_document != null) {
			return 1;
		}else {
			return 0;
		}
	}

	public MAGComponentInterface getChild(int idx) {
		if(_mag_document != null && idx == 0) {
			return _mag_document;
		}else {
			return null;
		}
	}

	public MAGComponentInterface getChild(String id) {
		if(_mag_document != null && _mag_document.id() != null && _mag_document.id().equals(id)) {
			return _mag_document;
		}else {
			return null;
		}
	}

	public MAGComponentInterface[] getNamedChildren() {
		if(_mag_document != null && _mag_document.id() != null && _mag_document.id().length() > 0) {
			return new MAGComponentInterface[] {_mag_document};
		}else {
			return null;
		}
	}

	public void invalidateChild(MAGComponentInterface comp) {
		if(_mag_document != null && _mag_document == comp) {
			((MAGFrameField)getField()).invalidateMAGComponent(comp);
		}
	}
	
	public void releaseResources() {
		_link = null;
		
		if(_mag_document != null) {
			_mag_document.releaseResources();
			_mag_document = null;
		}
		
		((MAGFrameField)getField()).releaseResources();
		
		super.releaseResources();
	}

}
