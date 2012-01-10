/**
 * MAGInfoGrid.java
 *
 * Copyright 2007-2010 anhe.
 */
package com.anheinno.android.libs.mag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;

import com.anheinno.android.libs.R;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.ui.EasyDialog;

/**
 * 2010-11-5<br>
 * 
 * MAGInfoGrid用于呈现可自定义排序的表格，支持多行数据选择。<br>
 * 
 * @author shenrh
 * 
 * @version 1.0
 * 
 */
public class MAGInfoGrid extends MAGInputBase {
	private JSONArray _fields;
	private JSONArray _data;
	private int _item_per_page;// 每页最多显示数量
	private boolean _delayed_sorting;
	private String _footer;

	public MAGInfoGrid() {
		super();
		_fields = null;
		_data = null;
		_item_per_page = -1;
		_delayed_sorting = false;
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if (!checkMandatory(o, "_fields")) {
				return false;
			}

			if (!checkMandatory(o, "_data")) {
				return false;
			}

			_fields = o.getJSONArray("_fields");
			_data = o.getJSONArray("_data");

			if (o.has("_number")) {
				_item_per_page = o.getInt("_number");
			}

			if (o.has("delayed-sorting") && o.getString("delayed-sorting").equalsIgnoreCase("true")) {
				_delayed_sorting = true;
			}

			if (o.has("_footer")) {
				_footer = o.getString("_footer");
			} else {
				_footer = "";
			}

			return super.fromJSON(o);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return false;
	}

	protected String getFooter() {
		return _footer;
	}

	public boolean isDelayedSort() {
		return _delayed_sorting;
	}

	public JSONArray getFields() {
		return _fields;
	}

	public JSONArray getData() {
		return _data;
	}

	public int getNumperPage() {
		return _item_per_page;
	}

	public MAGStyle getStyle() {
		return style();
	}

	public boolean validate() {
		JSONArray data = ((MAGInfoGridField) getField()).getSelectId();
		if (data.length() > 0) {
			return true;
		} else {
			if (getInitVmsg() != null && getInitVmsg().length() > 0) {
				EasyDialog.longAlert(getContext(), getInitVmsg());
			} else {
				String data_show = title();
				if (data_show.length() > 1 && (data_show.endsWith(":") || data_show.endsWith("："))) {
					data_show = data_show.substring(0, data_show.length() - 1) + "!";
				}
				EasyDialog.longAlert(getContext(), getContext().getString(R.string.mag_text_input_cannot_be_empty) + data_show);
			}
			return false;
		}
	}

	public View initField(Context con) {
		MAGInfoGridField grid = new MAGInfoGridField(con, this);
		BackgroundDescriptor bg_desc = null;
		bg_desc = style().getBackground("button");
		if (bg_desc != null) {
			grid.setButtonBackground(bg_desc);
		}
		bg_desc = style().getBackground("focus-button");
		if (bg_desc != null) {
			grid.setFocusButtonBackground(bg_desc);
		}
		bg_desc = style().getBackground("footer");
		if (bg_desc != null) {
			grid.setFooterBackground(bg_desc);
		}
		bg_desc = style().getBackground("header");
		if (bg_desc != null) {
			grid.setHeaderBackground(bg_desc);
		}

		TextStyleDescriptor style = null;
		style = style().getTextStyle("desc");
		if (style != null) {
			grid.setDescTextStyle(style);
		}
		style = style().getTextStyle("focus-desc");
		if (style != null) {
			grid.setDescFocusTextStyle(style);
		}
		style = style().getTextStyle("asc");
		if (style != null) {
			grid.setAscTextStyle(style);
		}
		style = style().getTextStyle("focus-asc");
		if (style != null) {
			grid.setAscFocusTextStyle(style);
		}
		style = style().getTextStyle("left-page");
		if (style != null) {
			grid.setLeftPageTextStyle(style);
		}
		style = style().getTextStyle("focus-left-page");
		if (style != null) {
			grid.setLeftPageFocusTextStyle(style);
		}
		style = style().getTextStyle("right-page");
		if (style != null) {
			grid.setRightPageTextStyle(style);
		}
		style = style().getTextStyle("focus-right-page");
		if (style != null) {
			grid.setRightPageFocusTextStyle(style);
		}
		style = style().getTextStyle("footer");
		if (style != null) {
			grid.setPagerTextStyle(style);
		}

		grid.updateStyle();
		return grid;
	}

	public void updateField(View f) {
		((MAGInfoGridField) f).updateUi();
	}

	/*protected void go2link(JSONBrowserLink link, String target) {
		if (target.equals(MAGLink.LINK_TARGET_SELF)) {
			MAGDocumentField df = getMAGDocument().getMAGDocumentField();
			df.open(link, expire, false, save, false);
		} else if (target.equals(MAGLink.LINK_TARGET_NEW)) {
			MAGDocumentScreen ds = new MAGDocumentScreen(getContext(), link, expire, false, save, false, getMAGScreen());
			ds.show();
		} else if (target.equals(MAGLink.LINK_TARGET_BROWSER)) {
			final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(link));
			getContext().startActivity(intent);
			if (JSONBrowserConfig.isCacheEnabled(getContext()) && expire > 0
					&& JSONObjectCacheDatabase.linkState(getContext(), link).equals(JSONObjectCacheDatabase.URL_LINK_INVALID)) {
				JSONObjectCacheDatabase.saveObject(getContext(), link, new JSONObject(), expire, false);
			}
		} else if (target.equals(MAGLink.LINK_TARGET_CUSTOM_CONTROL)) {
			MAGLinkedCustomControl control = MAGLinkedCustomControl.getControl(getContext(), link);
			if (control != null) {
				control.show();
			}
		} else if (target.equals(MAGLink.LINK_TARGET_SCRIPT)) {
			MAGScriptCommand[] cmds = MAGScriptCommand.parseScripts(link);
			for (int i = 0; i < cmds.length; i++) {
				if (!this.getMAGDocument().execScriptCommand(cmds[i])) {
					break;
				}
			}
		}
	}*/

	// public String hint() {
	// return ((MAGInfoGridField) getField()).getHint();
	// }

	public void onShowUi() {

	}

	public String fetchValue() {
		JSONArray data = ((MAGInfoGridField) getField()).getSelectId();
		return data.toString();
	}

}
