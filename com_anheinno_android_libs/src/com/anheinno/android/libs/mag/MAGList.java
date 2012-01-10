package com.anheinno.android.libs.mag;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;

import com.anheinno.android.libs.UtilClass;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.util.SortedVector;

public class MAGList extends MAGContainerBase {

	private int _current_page;
	private int _items_per_page;
	private MAGListOrderbyField[] _orderby;
	MAGListOrderbyField _current_order_field;
	private boolean _is_descending;
	
	private String _footer;
	
	public MAGList() {
		super();
		
		_current_page = 0;
		_items_per_page = 0;
		_orderby = null;
		_is_descending = true;
		_current_order_field = null;
		
		_footer = null;
	}
	
	public boolean fromJSON(JSONObject o) {
		try {
			if(!super.fromJSON(o)) {
				return false;
			}
			
			if(o.has("_orderby")) {
				JSONArray array = o.getJSONArray("_orderby");
				if(array != null && array.length() > 0) {
					_orderby = new MAGListOrderbyField[array.length()];
					for(int i = 0; i < array.length(); i ++) {
						_orderby[i] = new MAGListOrderbyField(array.getJSONObject(i));
					}
				}
				if(o.has("_descending") && o.getString("_descending").equalsIgnoreCase("true")) {
					_is_descending = true;
				}else {
					_is_descending = false;
				}
			}else {
				_orderby = null;
			}

			if(o.has("_items_per_page")) {
				_items_per_page = o.getInt("_items_per_page");
			}else {
				_items_per_page = childrenNum();
			}

			if(o.has("_footer")) {
				_footer = o.getString("_footer");
			}else {
				_footer = "";
			}
			
			sortChildren();

			return true;
						
		} catch (final JSONException e) {
			LOG.error(this, "fromJSON", e);
		}
		
		return false;
	}
	
	public String getAttributeValue(String fieldname) {
		if(fieldname.equals("_items_per_page")) {
			return "" + _items_per_page;
		}else {
			return super.getAttributeValue(fieldname);
		}
	}
	
	public View initField(Context context) {
		MAGContainerBase.initChildFields(this);
		MAGListField f = new MAGListField(context, this);
		return f;
	}

	public void updateField(View field) {
		super.updateField(field);
		
		MAGListField grid = (MAGListField)field;
		BackgroundDescriptor bg_desc = null;
		bg_desc = style().getBackground("button");
		if(bg_desc != null) {
			grid.setButtonBackground(bg_desc);
		}
		bg_desc = style().getBackground("focus-button");
		if(bg_desc != null) {
			grid.setFocusButtonBackground(bg_desc);
		}
		bg_desc = style().getBackground("header");
		if(bg_desc != null) {
			grid.setHeaderBackground(bg_desc);
		}
		bg_desc = style().getBackground("footer");
		if(bg_desc != null) {
			grid.setFooterBackground(bg_desc);
		}
		
		TextStyleDescriptor style = null;
		style = style().getTextStyle("desc");
		if(style != null) {
			grid.setDescTextStyle(style);
		}
		style = style().getTextStyle("focus-desc");
		if(style != null) {
			grid.setDescFocusTextStyle(style);
		}
		style = style().getTextStyle("asc");
		if(style != null) {
			grid.setAscTextStyle(style);
		}
		style = style().getTextStyle("focus-asc");
		if(style != null) {
			grid.setAscFocusTextStyle(style);
		}
		style = style().getTextStyle("left-page");
		if(style != null) {
			grid.setLeftPageTextStyle(style);
		}
		style = style().getTextStyle("focus-left-page");
		if(style != null) {
			grid.setLeftPageFocusTextStyle(style);
		}
		style = style().getTextStyle("right-page");
		if(style != null) {
			grid.setRightPageTextStyle(style);
		}
		style = style().getTextStyle("focus-right-page");
		if(style != null) {
			grid.setRightPageFocusTextStyle(style);
		}

		style = style().getTextStyle("checked");
		if(style != null) {
			grid.setCheckedTextStyle(style);
		}
		style = style().getTextStyle("focus-checked");
		if(style != null) {
			grid.setCheckedFocusTextStyle(style);
		}
		style = style().getTextStyle("unchecked");
		if(style != null) {
			grid.setUncheckedTextStyle(style);
		}
		style = style().getTextStyle("focus-unchecked");
		if(style != null) {
			grid.setUncheckedFocusTextStyle(style);
		}
		style = style().getTextStyle("footer");
		if(style != null) {
			grid.setPagerTextStyle(style);
		}
		
		grid.updateStyle();
	}
	
	protected MAGListOrderbyField[] getOrderBy() {
		return _orderby;
	}
	
	protected int getPageCount() {
		int pages = childrenNum()/_items_per_page;
		if(childrenNum() > pages*_items_per_page) {
			return pages+1;
		}else {
			return pages;
		}
	}
	
	protected String getFooter() {
		return _footer;
	}
	
	protected int getCurrentPage() {
		return _current_page;
	}
	
	protected boolean setCurrentPage(int pages) {
		if(_current_page != pages) {
			_current_page = pages;
			return true;
		}else {
			return false;
		}
	}
	
	protected int getPageOffset() {
		return _current_page*_items_per_page;
	}
	
	protected int getPageItemCount() {
		int item_count = childrenNum() - getPageOffset();
		if(item_count > _items_per_page) {
			return _items_per_page;
		}else {
			return item_count;
		}
	}
	
	protected boolean isDescending() {
		return _is_descending;
	}
	
	protected boolean setDescending(boolean is_descending) {
		if(is_descending != _is_descending) {
			_is_descending = is_descending;
			sortChildren();
			return true;
		}else {
			return false;
		}
	}
	
	protected boolean setOrderCondition(MAGListOrderbyField cond) {
		if(_current_order_field != cond) {
			_current_order_field = cond;
			sortChildren();
			return true;
		}else {
			return false;
		}
	}
	
	protected void sortChildren() {
		if(_current_order_field == null) {
			_current_order_field = _orderby[0];
		}
		SortedMAGComponents list = new SortedMAGComponents();
		for(int i = 0; i < childrenNum(); i ++) {
			list.addSorted(getChild(i), true);
		}
		Vector<MAGComponentInterface> sorted_children = new Vector<MAGComponentInterface>(childrenNum());
		for(int i = 0; i < list.size(); i ++) {
			sorted_children.addElement((MAGComponentInterface) list.elementAt(i));
		}
		super.replaceChildren(sorted_children);
	}
	
	@SuppressWarnings("serial")
	class SortedMAGComponents extends SortedVector<MAGComponentInterface> {

		protected int compare(MAGComponentInterface o1, MAGComponentInterface o2) {
			MAGComponent child1 = (MAGComponent)o1;
			MAGComponent child2 = (MAGComponent)o2;
			String val1 = child1.getAttributeValue(_current_order_field._field);
			String val2 = child2.getAttributeValue(_current_order_field._field);
			int result = 0;
			if(_current_order_field._type.equals(DATATYPE_NUMERIC)) {
				try {
					double val1_d = Double.parseDouble(UtilClass.parseNumber(val1));
					double val2_d = Double.parseDouble(UtilClass.parseNumber(val2));
					result = (int) (val1_d - val2_d);
				} catch (final Exception e) {
					result = 0;
				}
			}else {
				if(val1 == null && val2 == null) {
					result = 0;
				}else if(val1 == null && val2 != null) {
					result = -1;
				}else if(val1 != null && val2 == null) {
					result = 1;
				}else {
					result = val1.compareTo(val2);
				}
			}
			if(_is_descending) {
				return result;
			}else {
				return -result;
			}
		}
	}
	
	protected static String DATATYPE_NUMERIC = "NUMERIC";
	protected static String DATATYPE_TEXT = "TEXT";
	
	protected class MAGListOrderbyField {
		private String _label;
		private String _field;
		private String _type;
		
		MAGListOrderbyField(JSONObject o) {
			try {
				_label = o.getString("_label");
				_field = o.getString("_field");
				if(o.has("_type")) {
					if(o.getString("_type").equals(DATATYPE_TEXT)) {
						_type = DATATYPE_TEXT;
					}else {
						_type = DATATYPE_NUMERIC;
					}
				}else {
					_type = DATATYPE_TEXT;
				}
			}catch(final Exception e) {
				LOG.error(this, "constructor", e);
			}
		}
		
		public String toString() {
			return _label;
		}
	}
}
