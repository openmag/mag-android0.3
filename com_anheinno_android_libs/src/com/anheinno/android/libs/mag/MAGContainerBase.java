package com.anheinno.android.libs.mag;

import java.util.Hashtable;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.content.Context;
import android.view.View;

import com.anheinno.android.libs.R;
import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.ui.EasyDialog;


/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 * 
 */
public abstract class MAGContainerBase extends MAGComponent implements MAGContainerInterface {

	private Vector<MAGComponentInterface> _children;
	private Hashtable<String, MAGComponentInterface> _childrenhash;

	public MAGContainerBase() {
		super();
		_init();
	}
	
	private final void _init() {
		_children = null;
		_childrenhash = null;
	}
	
	public void addChild(MAGComponentInterface child) {
		if (_children == null) {
			_children = new Vector<MAGComponentInterface>();
		}
		_children.addElement(child);
	}
	
	public void removeChild(MAGComponentInterface child) {
		if(child.id() != null && child.id().length() > 0 && _childrenhash != null) {
			_childrenhash.remove(child.id());
		}
		_children.removeElement(child);
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if(!super.fromJSON(o)) {
				return false;
			}
			if(!checkMandatory(o, "_content")) {
				return false;
			}

			JSONArray content = o.getJSONArray("_content");
			
			for (int i = 0; i < content.length(); i++) {
				MAGComponent.parseJSON(this, content.getJSONObject(i));
			}
			return true;
		} catch (final JSONException e) {
			LOG.error(this, "fromJSON", e);
		}
		return false;
	}
	
	public static String getChildrenAttributeValue(MAGContainerInterface comp, String fieldname) {
		int pos = 0;
		if((pos = fieldname.indexOf('.')) > 0) {
			if(fieldname.startsWith("_content[")) {
				int index = Integer.parseInt(fieldname.substring(fieldname.indexOf('[')+1, fieldname.indexOf(']')));
				if(index >= 0 && index < comp.childrenNum()) {
					return comp.getChild(index).getAttributeValue(fieldname.substring(pos+1));
				}else {
					return null;
				}
			}else {
				String id = fieldname.substring(0, pos);
				MAGComponentInterface child = comp.getChild(id);
				if(child != null) {
					return child.getAttributeValue(fieldname.substring(pos+1));
				}else {
					return null;
				}
			}
		}else {
			return null;
		}
	}
	
	public String getAttributeValue(String fieldname) {
		if(fieldname.indexOf('.') > 0) {
			return getChildrenAttributeValue(this, fieldname);
		}else {
			return super.getAttributeValue(fieldname);
		}
	}
	
	private void scanNamedChildren() {
		if (_childrenhash == null) {
			_childrenhash = new Hashtable<String, MAGComponentInterface>();
		}
		for(int i = 0; _children != null && i < _children.size(); i ++) {
			MAGComponent child = (MAGComponent)_children.elementAt(i);
			if (child.id() != null && child.id().length() > 0) {
				if (_childrenhash.containsKey(child.id())) {
					EasyDialog.remind(getContext(), "ID:" + child.id() + " "
							+ getContext().getString(R.string.mag_component_same_id));
				} else {
					_childrenhash.put(child.id(), child);
				}
			}
		}
	}
	
//	public synchronized void setLayoutManager(MAGLayoutManager manager) {
		//_manager = manager;
//	}
	
//	public synchronized void removeLayoutManager() {
//		_manager = null;
//	}
//	
//	public synchronized void invalidateChild(MAGComponent comp) {
//		if(_manager != null) {
//			_manager.invalidateMAGComponent(comp);
//		}
//	}

	public synchronized void invalidateChild(MAGComponentInterface comp) {
		if(getField() != null) {
			((MAGContainerLayoutInterface)getField()).invalidateMAGComponent(comp);
		}
	}
	
	public static void initChildFields(MAGContainerInterface container) {
		for (int i = 0; i < container.childrenNum(); i++) {
			View f = container.getChild(i).initField(container.getContext());
			if(f != null) {
				//System.out.println("Component " + container.getChild(i).toString() + " view inited " + f.toString());
				container.getChild(i).setField(f);
			}
		}
	}
	
	//abstract protected void addChildField(Field f);
	
	/*public void setChildFocusListener() {
		for(int i = 0; _children != null && i < _children.size(); i ++) {
			MAGComponent child = (MAGComponent)_children.elementAt(i);
			if(child instanceof MAGContainerInterface) {
				((MAGContainerInterface)child).setChildFocusListener();
			}else if(child.visible()) {
				Field f = child.getField();
				if(f != null) {
					f.setFocusListener(child);
				}
			}
		}
	}*/

	public int childrenNum() {
		if (_children == null) {
			return 0;
		} else {
			return _children.size();
		}
	}

	public MAGComponentInterface getChild(int idx) {
		if(_children != null && idx >= 0 && idx < _children.size()) {
			return (MAGComponentInterface) _children.elementAt(idx);
		}else {
			return null;
		}
	}

	public MAGComponentInterface getChild(String id) {
		if( _childrenhash == null) {
			scanNamedChildren();
		}
		if (_childrenhash != null && _childrenhash.containsKey(id)) {
			return (MAGComponentInterface) _childrenhash.get(id);
		} else {
			return null;
		}
	}
	
	public MAGComponentInterface[] getNamedChildren() {
		if (_childrenhash == null) {
			scanNamedChildren();
		}
		MAGComponentInterface[] siblings = new MAGComponentInterface[_childrenhash.size()];
		int sibling_idx = 0;
		for(int i = 0; i < _children.size(); i ++) {
			MAGComponentInterface tmp = getChild(i);
			if(tmp.id() != null && tmp.id().length() > 0) {
				if(tmp == _childrenhash.get(tmp.id())) {
					siblings[sibling_idx] = tmp;
					sibling_idx++;
				}
			}
		}
		return siblings;
	}
	
	/*public void onShowUi() {
		for(int i = 0; _children != null && i < _children.size(); i ++) {
			MAGComponent comp = getChild(i);
			if(comp.getField() != null && comp.visible()) {
				comp.onShowUi();
			}
		}
	}*/
	
	public void updateField(View f) {
		for(int i = 0; _children != null && i < _children.size(); i ++) {
			MAGComponentInterface comp = getChild(i);
			if(comp.getField() != null && comp.visible()) {
				comp.updateField(comp.getField());
			}
		}
	}
	
	/*public MAGComponent getLeafComponentWithFocus() {
		for(int i = 0; i < childrenNum(); i ++) {
			MAGComponent child = getChild(i);
			if(child instanceof MAGContainer) {
				child = ((MAGContainer)child).getLeafComponentWithFocus();
				if(child != null) {
					return child;
				}
			}else if(child.isOnFocus()) {
				return child;
			}
		}
		return null;
	}*/

	protected void replaceChildren(Vector<MAGComponentInterface> children) {
		_children = children;
	}
	
	public void releaseResources() {
		while(childrenNum() > 0) {
			MAGComponentInterface comp = getChild(childrenNum() - 1);
			comp.releaseResources();
			removeChild(comp);
		}
		super.releaseResources();
	}
	
}
