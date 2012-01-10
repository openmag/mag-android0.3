/*
 * MAGPanelField.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;


import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;

import com.anheinno.android.libs.log.LOG;



/**
 * 和MAGDocument类似，MAGPanel也是一个容器组件。一个MAGPanel对应MAGDocument窗口中的一个现实区域。<br>
 * TYPE = "PANEL"
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGPanel extends MAGContainerBase {
	private Hashtable<String, MAGRadioGroup> _radio_groups;

	public static final String PANEL_FOLDING_DISABLE = "__disable_";// 不用折叠
	public static final String PANEL_EXPAND = "__expand_";// 展开
	public static final String PANEL_COLLAPSE = "__collapse_";// 折叠

	private String _expand;

	public MAGPanel() {
		super();
		_radio_groups = null;
		_expand = PANEL_FOLDING_DISABLE;
	}
	
	public MAGRadioGroup getMAGRadioGroup(String name) {
		if (_radio_groups == null) {
			_radio_groups = new Hashtable<String, MAGRadioGroup>();
		}
		if (!_radio_groups.containsKey(name)) {
			_radio_groups.put(name, new MAGRadioGroup(name));
		}
		return _radio_groups.get(name);
	}

	public String expand() {
		return _expand;
	}

	public String getAttributeValue(String fieldname) {
		if(fieldname.equals("_expand")) {
			return _expand;
		}else {
			return super.getAttributeValue(fieldname);
		}
	}
	
	// 2010-10-21添加_expand折叠panel
	public boolean fromJSON(JSONObject o) {
		try {
			if (o.has("_expand")) {
				_expand = o.getString("_expand");
			}
			return super.fromJSON(o);
		} catch (final JSONException e) {
			LOG.error(this, "fromJSON", e);
		}
		return false;
	}

	// 2010-5-27 增加toJSON
	public JSONObject toJSON() {
		JSONObject obj = super.toJSON();
		try {
			//obj.put("_content", _content);

			obj.put("_expand", _expand);

		} catch (JSONException je) {
			System.err.println(je.toString());
		}
		return obj;
	}

	public View initField(Context context) {

		MAGContainerBase.initChildFields(this);

		if((showFoldIcon() || (title() != null && title().length() > 0))) {
			MAGPanelField panel = new MAGPanelField(context);
			panel.setPanel(this);
			return panel;
		}else {
			MAGLayoutManager panel = new MAGLayoutManager(context);
			panel.setContainer(this);
			return panel;
		}
	}
	
	public void updateField(View f) {
		super.updateField(f);
		if(f instanceof MAGPanelField) {
			((MAGPanelField)f).updateUi();
		}else {
		}
	}
	
	public boolean showChildren() {
		if(_expand.equals(PANEL_FOLDING_DISABLE) || _expand.equals(PANEL_EXPAND)) {
			return true;
		}else {
			return false;
		}
	}
	
	public void setExpandStatus(String status) {
		_expand = status;
	}
	
	public boolean showFoldIcon() {
		if(_expand.equals(PANEL_FOLDING_DISABLE)) {
			return false;
		}else {
			return true;
		}
	}
	
	public void releaseResources() {
		if(_radio_groups != null) {
			_radio_groups.clear();
			_radio_groups = null;
		}
		View field = getField();
		if(field instanceof MAGPanelField) {
			((MAGPanelField)field).releaseResources();
		}else {
			((MAGLayoutManager)field).releaseResources();
		}
		super.releaseResources();
	}

}
