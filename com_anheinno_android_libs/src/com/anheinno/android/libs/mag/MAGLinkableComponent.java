package com.anheinno.android.libs.mag;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.anheinno.android.libs.JSONBrowserConfig;
import com.anheinno.android.libs.JSONBrowserLink;
import com.anheinno.android.libs.JSONObjectCacheDatabase;
import com.anheinno.android.libs.UtilClass;
import com.anheinno.android.libs.ui.EasyDialog;

public abstract class MAGLinkableComponent extends MAGComponent {
	
	protected JSONBrowserLink _link;
	protected String  _target;

	public static final String LINK_TARGET_SELF = "__self_";
	public static final String LINK_TARGET_NEW = "__new_";
	public static final String LINK_TARGET_BROWSER = "__browser_";
	public static final String LINK_TARGET_SCRIPT = "__script_";
	public static final String LINK_TARGET_CUSTOM_CONTROL = "__custom_control_";
	public static final String LINK_TARGET_DEFAULT = LINK_TARGET_SELF;

	public MAGLinkableComponent() {
		super();
		_link = null;
		_target = null;
		//setCheckable(true);
	}
	
	public boolean fromJSON(JSONObject o) {
		try {
			if(!super.fromJSON(o)) {
				return false;
			}
			
			if(!checkMandatory(o, "_link")) {
				return false;
			}
			
			if (o.has("_target")) {
				_target = o.getString("_target");
			} else {
				_target = LINK_TARGET_DEFAULT;
			}

			_link = new JSONBrowserLink(getContext());
			//System.out.println("link: " + o.getString("_link"));
			_link.setURL(o.getString("_link"));

			if (o.has("_expire")) {
				_link.setExpireMilliseconds(o.getLong("_expire"));
			} else {
				_link.setExpireMilliseconds(JSONBrowserConfig.getCacheExpire(getContext()));
			}
			if(o.has("_notify")) {
				if(o.getString("_notify").equalsIgnoreCase("true")) {
					_link.setNotify(true);
				}else {
					_link.setNotify(false);
				}
			}else {
				_link.setNotify(true);
			}
			if(o.has("_save")) {
				if(o.getString("_save").equalsIgnoreCase("true")) {
					_link.setSaveHistory(true);
				}else {
					_link.setSaveHistory(false);
				}
			}else {
				_link.setSaveHistory(true);
			}
			
			if(!_target.equals(LINK_TARGET_SCRIPT) && !_target.equals(LINK_TARGET_CUSTOM_CONTROL)) {
				_link.setURL(getMAGDocument().getAbsoluteURL(_link.getURL()));
				if(_link.getExpireMilliseconds() > 0) {
					getMAGDocument().addCachedLink(_link.getURL());
				}
			}
			return true;
		} catch (final JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public String getAttributeValue(String fieldname) {
		if(fieldname.equals("_link")) {
			return _link.getURL();
		}else if(fieldname.equals("_target")) {
			return _target;
		}else if(fieldname.equals("_expire")) {
			return "" + _link.getExpireMilliseconds();
		}else if(fieldname.equals("_notify")) {
			return UtilClass.boolean2String(_link.isNotify());
		}else if(fieldname.equals("_save")) {
			return UtilClass.boolean2String(_link.isSaveHistory());
		}else {
			return super.getAttributeValue(fieldname);
		}
	}
	
	protected void go2Link() {
		go2Link(this, _link, _target);
	}
	
	public JSONBrowserLink getLink() {
		return _link;
	}
	
	public static void go2Link(MAGComponent component, JSONBrowserLink link, String target) {
		System.out.println("go2link() _link: " + link.getURL() + " _target:  " + target + " _expire: " + link.getExpireMilliseconds());
		if (target.equals(LINK_TARGET_SELF)) {
			MAGDocument doc = component.getMAGDocument();
			if(doc != null) {
				MAGDocumentField sc = doc.getMAGDocumentField();
				if(sc != null) {
					sc.open(link, false, null);
				}
			}
		} else if (target.equals(LINK_TARGET_NEW)) {
			MAGDocumentScreen ds = new MAGDocumentScreen(component.getContext(), link, false, component.getMAGDocumentScreen());
			ds.show();
		} else if (target.equals(LINK_TARGET_BROWSER)) {
			final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(link.getURL()));
			component.getContext().startActivity(intent);
			/*
			 * save an empty object, so the link will be marked as
			 * VISITED
			 */
			if (JSONBrowserConfig.isCacheEnabled(component.getContext())
					&& link.getExpireMilliseconds() > 0
					&& JSONObjectCacheDatabase.linkState(component.getContext(), link.getURL()).equals(JSONObjectCacheDatabase.URL_LINK_INVALID)) {
				JSONObjectCacheDatabase.saveObject(component.getContext(), link.getURL(), new JSONObject(), link.getExpireMilliseconds(), false);
			}
		} else if (target.equals(LINK_TARGET_CUSTOM_CONTROL)) {
			MAGLinkedCustomControl control = MAGLinkedCustomControl.getControl(component.getContext(), link.getClasspath());
			if(control != null) {
				control.show();
			}
		} else if (target.equals(LINK_TARGET_SCRIPT)) {
			MAGScriptCommand[] cmds = MAGScriptCommand.parseScripts(link.getScripts());
			for (int i = 0; i < cmds.length; i++) {
				if (!component.getMAGDocument().execScriptCommand(cmds[i])) {
					break;
				}
			}
		} else {
			System.out.println("find target MAGDocumentField " + target);
			MAGDocumentScreen screen = component.getMAGDocumentScreen();
			if(screen != null) {
				MAGFrameField container = (MAGFrameField)screen.getMAGDocumentContainer(target);
				if(container != null) {
					System.out.println("find the target MAGDocumentField!!! " + container);
					container.open(link, false, null);
				}else {
					EasyDialog.postInfo(component.getContext(), "Cannot find target " + target);
				}
			}else {
				System.out.println("Cannot locate MAGDocumentScreen!!!");
			}
		}
	}
	
	protected boolean setAttribute(String name, String value) {
		if(name.equals("link")) {
			if(_target.equals(LINK_TARGET_CUSTOM_CONTROL)) {
				_link.setClasspath(value);
			}else if(_target.equals(LINK_TARGET_SCRIPT)) {
				_link.setClasspath(value);
			}else {
				_link.setURL(value);
			}
			return true;
		}else if(name.equals("target")) {
			_target = value;
			return true;
		}else if(name.equals("expire")) {
			_link.setExpireMilliseconds(Long.parseLong(value));
			return true;
		}else if(name.equals("notify")) {
			if(value.equals("true")) {
				_link.setNotify(true);
			}else {
				_link.setNotify(false);
			}
			return true;
		}else if(name.equals("save")) {
			if(value.equals("true")) {
				_link.setSaveHistory(true);
			}else {
				_link.setSaveHistory(false);
			}
		}
		if(super.setAttribute(name, value)) {
			return true;
		}
		return false;
	}
	
	public void updateField(View f) {
		if(_target != null && _target.length() > 0 
				&& !_target.equals(LINK_TARGET_BROWSER)
				&& !_target.equals(LINK_TARGET_CUSTOM_CONTROL)
				&& !_target.equals(LINK_TARGET_NEW)
				&& !_target.equals(LINK_TARGET_SCRIPT)
				&& !_target.equals(LINK_TARGET_SELF)) {
			MAGDocumentScreen screen = getMAGDocumentScreen();
			if(screen != null) {
				MAGFrameField field = (MAGFrameField)screen.getMAGDocumentContainer(_target);
				if(field != null) {
					MAGFrame frame = getMAGFrame();
					if(frame.getField() != field) {
						field.registerLinks(this);
					}
				}
			}
		}
	}

}
