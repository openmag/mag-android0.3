/*
 * MAGTextField.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import org.json.JSONException;
import org.json.JSONObject;

import com.anheinno.android.libs.graphics.GraphicUtilityClass;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;

import android.content.Context;
import android.graphics.Color;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

/**
 * MAGText是一个显示组件。一个MAGText对应由抬头提示和正文内容组成的一行或一段文字。<br>
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGText extends MAGComponent {
	private String _text;
	//private boolean _active_text;
	private static final String FOCUS_STYLE_NORMAL = "normal";
	private static final String FOCUS_STYLE_NONE = "none";
	private static final String FOCUS_STYLE_ACTIVE = "active";
	private static final String FOCUS_STYLE_DEFAULT = FOCUS_STYLE_NORMAL;

	public MAGText() {
		super();
		_text = null;
		//_active_text = true;
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if (!super.fromJSON(o)) {
				return false;
			}
			if (o.has("_text")) {
				_text = o.getString("_text");
			} else {
				_text = "";
			}
			return true;
		} catch (final JSONException e) {
		}
		return false;
	}

	public View initField(Context context) {
		MAGColorLabelManager clm = new MAGColorLabelManager(context, this);
		TextView tv = new TextView(context);
		clm.setField(tv, true);

		int color = Color.BLACK;
		if (GraphicUtilityClass.isValidColor(style().getColor())) {
			color = style().getColor();
		}
		tv.setTextColor(color);
		tv.setTextSize(style().getFontSize(getContext()) * 3 / 4);
		String focus_style = style().get("focus");
		if (focus_style == null) {
			focus_style = FOCUS_STYLE_DEFAULT;
		}
		if (focus_style.equals(FOCUS_STYLE_NONE)) {
			tv.setFocusable(false);
		} else if (focus_style.equals(FOCUS_STYLE_NORMAL)) {
			tv.setFocusable(true);
		} else if (focus_style.equals(FOCUS_STYLE_ACTIVE)) {
			tv.setFocusable(true);
			tv.setAutoLinkMask(Linkify.ALL);
		}

		TextStyleDescriptor style = style().getTextStyle();
		if (style != null) {
			tv.setTextColor(style.getColor());
			tv.setTextSize(style.getFontSize(getContext()) * 3 / 4);
		}

		if (_text.length() > 0) {
			tv.setText(_text);
		}

		return clm;
	}

	public String getAttributeValue(String fieldname) {
		if (fieldname.equals("_text")) {
			return _text;
		} else {
			return super.getAttributeValue(fieldname);
		}
	}

	public void updateField(View f) {
		// MAGColorLabelManager clm = (MAGColorLabelManager) f;
		//
		// if (_active_text) {
		// ((TextView) clm.getField()).setText(_text);
		// }
	}

}
