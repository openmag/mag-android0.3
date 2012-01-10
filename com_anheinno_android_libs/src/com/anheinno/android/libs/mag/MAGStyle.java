/*
 * MAGStyle.java
 *
 * <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Paint;

import com.anheinno.android.libs.graphics.Align;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.GraphicUtilityClass;
import com.anheinno.android.libs.graphics.PaintRepository;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.graphics.VAlign;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 每个MAG组件的可选属性_style即为MAGStyle对象。MAGStyle定义了MAG组件的显示样式。
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGStyle {
	private Hashtable<String, String> _styles;

	private static MAGStyle _default_style = null;

	private static String LEFT_ALIGN = "left";
	private static String CENTER_ALIGN = "center";
	private static String RIGHT_ALIGN = "right";
	private static final Align DEFAULT_ALIGN = Align.LEFT;

	private static String TOP_VALIGN = "top";
	private static String MIDDLE_VALIGN = "middle";
	private static String BOTTOM_VALIGN = "bottom";
	private static final VAlign DEFAULT_VALIGN = VAlign.MIDDLE;

	private static String DIR_TOP = "top";
	private static String DIR_BOTTOM = "bottom";
	private static String DIR_LEFT = "left";
	private static String DIR_RIGHT = "right";

	// public static final double DEFAULT_WIDTH = Display.getWidth();

	/**
	 * MAGStyle构造函数，构造一个空MAGStyle
	 * 
	 */
	public MAGStyle() {
		_styles = null;
	}

	/**
	 * MAGStyle构造函数，基于一个已经存在的MAGStyle构造
	 * 
	 * @param clone
	 */
	public MAGStyle(MAGStyle clone) {
		this();
		copy(clone);
	}
	
	/**
	 * 将一个样式的内容拷贝到本样式中
	 * 
	 * @param clone
	 *            待拷贝MAGStyle
	 */
	public void copy(MAGStyle clone) {
		if (clone._styles != null) {
			Enumeration<String> keys = clone._styles.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String val = clone._styles.get(key);
				put(key, val);
			}
		}
	}

	/**
	 * 获得全局默认MAGStyle
	 * 
	 * @return
	 */
	public static MAGStyle getDefault() {
		if(_default_style == null) {
			_default_style = new MAGStyle();
		}
		return _default_style;
	}

	public void put(JSONObject style) {
		try {
			@SuppressWarnings("unchecked")
			Iterator<String> i = style.keys();
			while (i.hasNext()) {
				String key = i.next();
				String val = style.getString(key);
				put(key, val);
			}
		} catch (JSONException e) {
			System.err.println(e.getMessage());
		}
	}

	public String toString() {
		String text = "";
		Enumeration<String> e = _styles.keys();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			String val = _styles.get(key);
			text += key + ":" + val + ";";
		}
		return text;
	}

	public void put(String key, String val) {
		if (_styles == null) {
			_styles = new Hashtable<String, String>();
		}
		_styles.put(key, val);
	}

	public String get(String key) {
		if (_styles != null && _styles.containsKey(key)) {
			return _styles.get(key);
		} else {
			return null;
		}
	}

	public int getInt(String key) {
		String val = get(key);
		if (val != null) {
			return Integer.parseInt(val);
		} else {
			return -1;
		}
	}

	public double getDouble(String key) {
		String val = get(key);
		if (val != null) {
			int div_pos = 0;
			if ((div_pos = val.indexOf('/')) > 0 && div_pos < val.length() - 1) {
				return Double.parseDouble(val.substring(0, div_pos))/Double.parseDouble(val.substring(div_pos+1));
			}else if (val.endsWith("%")) {
				return Double.parseDouble(val.substring(0, val.length() - 1)) / 100.0;
			} else {
				return Double.parseDouble(val);
			}
		} else {
			return -1.0;
		}
	}

	public boolean getBoolean(String key) {
		String val = get(key);
		if (val != null && val.toLowerCase().equals("true")) {
			return true;
		} else {
			return false;
		}
	}

	public Align getAlign() {
		String align = get("align");
		if (align == null) {
			return DEFAULT_ALIGN;
		} else if (align.equals(LEFT_ALIGN)) {
			return Align.LEFT;
		} else if (align.equals(RIGHT_ALIGN)) {
			return Align.RIGHT;
		} else if (align.equals(CENTER_ALIGN)) {
			return Align.CENTER;
		} else {
			return DEFAULT_ALIGN;
		}
	}

	public VAlign getVAlign() {
		String valign = get("valign");
		if (valign == null) {
			return DEFAULT_VALIGN;
		} else if (valign.equals(TOP_VALIGN)) {
			return VAlign.TOP;
		} else if (valign.equals(BOTTOM_VALIGN)) {
			return VAlign.BOTTOM;
		} else if (valign.equals(MIDDLE_VALIGN)) {
			return VAlign.MIDDLE;
		} else {
			return DEFAULT_VALIGN;
		}
	}

	private boolean has(String name) {
		if (get(name) != null) {
			return true;
		} else {
			return false;
		}
	}

	private boolean has4Way(String prefix, String name, String dir) {
		String key = name;
		if (prefix != null && prefix.length() > 0) {
			key = prefix + "-" + name;
		}
		if (has(key + "-" + dir) || has(key)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean has4WayPadding(String prefix, String dir) {
		return has4Way(prefix, "padding", dir);
	}

	private boolean has4WayHintPadding(String dir) {
		return has4WayPadding("hint", dir);
	}

	public boolean hasHintPaddingLeft() {
		return has4WayHintPadding(DIR_LEFT);
	}

	public boolean hasHintPaddingTop() {
		return has4WayHintPadding(DIR_TOP);
	}

	public boolean hasHintPaddingRight() {
		return has4WayHintPadding(DIR_RIGHT);
	}

	public boolean hasHintPaddingBottom() {
		return has4WayHintPadding(DIR_BOTTOM);
	}

	private boolean has4WayBorderWidth(String prefix, String dir) {
		return has4Way(prefix, "border-width", dir);
	}

	private boolean has4WayBorderColor(String prefix, String dir) {
		return has4Way(prefix, "border-color", dir);
	}

	public boolean has4WayHintBorderWidth(String dir) {
		return has4WayBorderWidth("hint", dir);
	}

	public boolean hasHintBorderWidthLeft() {
		return has4WayHintBorderWidth(DIR_LEFT);
	}

	public boolean hasHintBorderWidthTop() {
		return has4WayHintBorderWidth(DIR_TOP);
	}

	public boolean hasHintBorderWidthRight() {
		return has4WayHintBorderWidth(DIR_RIGHT);
	}

	public boolean hasHintBorderWidthBottom() {
		return has4WayHintBorderWidth(DIR_BOTTOM);
	}

	private boolean has4WayHintBorderColor(String dir) {
		return has4WayBorderColor("hint", dir);
	}

	public boolean hasHintBorderColorLeft() {
		return has4WayHintBorderColor(DIR_LEFT);
	}

	public boolean hasHintBorderColorTop() {
		return has4WayHintBorderColor(DIR_TOP);
	}

	public boolean hasHintBorderColorRight() {
		return has4WayHintBorderColor(DIR_RIGHT);
	}

	public boolean hasHintBorderColorBottom() {
		return has4WayHintBorderColor(DIR_BOTTOM);
	}

	private int get4WayInt(String name, int def) {
		int w = getInt(name);
		if (w < 0) {
			w = def;
		}
		return w;
	}

	private int get4WayInt(String name, String dir, int def) {
		int w = getInt(name + "-" + dir);
		if (w < 0) {
			return get4WayInt(name, def);
		} else {
			return w;
		}
	}

	public int getBorderWidthTop() {
		return get4WayBorderWidth(null, DIR_TOP);
	}

	public int getBorderWidthRight() {
		return get4WayBorderWidth(null, DIR_RIGHT);
	}

	public int getBorderWidthBottom() {
		return get4WayBorderWidth(null, DIR_BOTTOM);
	}

	public int getBorderWidthLeft() {
		return get4WayBorderWidth(null, DIR_LEFT);
	}

	private static String PREFIX_HINT = "hint";

	public int getHintBorderWidthTop() {
		return get4WayBorderWidth(PREFIX_HINT, DIR_TOP);
	}

	public int getHintBorderWidthRight() {
		return get4WayBorderWidth(PREFIX_HINT, DIR_RIGHT);
	}

	public int getHintBorderWidthBottom() {
		return get4WayBorderWidth(PREFIX_HINT, DIR_BOTTOM);
	}

	public int getHintBorderWidthLeft() {
		return get4WayBorderWidth(PREFIX_HINT, DIR_LEFT);
	}

	private int get4WayBorderWidth(String prefix, String dir) {
		String key = "border-width";
		if (prefix != null && prefix.length() > 0) {
			key = prefix + "-" + key;
		}
		return get4WayInt(key, dir, 0);
	}

	public int getBorderColorLeft(boolean focus) {
		return get4WayBorderColor(focus ? "focus" : null, DIR_LEFT);
	}

	public int getHintBorderColorLeft() {
		return get4WayBorderColor(PREFIX_HINT, DIR_LEFT);
	}

	public int getBorderColorRight(boolean focus) {
		return get4WayBorderColor(focus ? "focus" : null, DIR_RIGHT);
	}

	public int getHintBorderColorRight() {
		return get4WayBorderColor(PREFIX_HINT, DIR_RIGHT);
	}

	public int getBorderColorTop(boolean focus) {
		return get4WayBorderColor(focus ? "focus" : null, DIR_TOP);
	}

	public int getHintBorderColorTop() {
		return get4WayBorderColor(PREFIX_HINT, DIR_TOP);
	}

	public int getBorderColorBottom(boolean focus) {
		return get4WayBorderColor(focus ? "focus" : null, DIR_BOTTOM);
	}

	public int getHintBorderColorBottom() {
		return get4WayBorderColor(PREFIX_HINT, DIR_BOTTOM);
	}

	private int get4WayBorderColor(String prefix, String dir) {
		int clr = GraphicUtilityClass.INVALID_COLOR;
		if (prefix != null && prefix.length() > 0) {
			clr = get4WayColor(prefix + "-border", dir);
		}
		if (!GraphicUtilityClass.isValidColor(clr)) {
			clr = get4WayColor("border", dir);
		}
		return clr;
	}

	private int get4WayPadding(String prefix, String dir) {
		String key = "padding";
		if (prefix != null && prefix.length() > 0) {
			key = prefix + "-" + key;
		}
		return get4WayInt(key, dir, 0);
	}

	public int getPaddingLeft() {
		return get4WayPadding(null, DIR_LEFT);
	}

	public int getPaddingRight() {
		return get4WayPadding(null, DIR_RIGHT);
	}

	public int getPaddingTop() {
		return get4WayPadding(null, DIR_TOP);
	}

	public int getPaddingBottom() {
		return get4WayPadding(null, DIR_BOTTOM);
	}

	private int get4WayHintPadding(String dir) {
		return get4WayPadding(PREFIX_HINT, dir);
	}

	public int getHintPaddingLeft() {
		return get4WayHintPadding(DIR_LEFT);
	}

	public int getHintPaddingTop() {
		return get4WayHintPadding(DIR_TOP);
	}

	public int getHintPaddingRight() {
		return get4WayHintPadding(DIR_RIGHT);
	}

	public int getHintPaddingBottom() {
		return get4WayHintPadding(DIR_BOTTOM);
	}

	private static String PREFIX_CONTENT = "content";

	private int get4WayContentPadding(String dir) {
		return get4WayPadding(PREFIX_CONTENT, dir);
	}

	public int getContentPaddingLeft() {
		return get4WayContentPadding(DIR_LEFT);
	}

	public int getContentPaddingTop() {
		return get4WayContentPadding(DIR_TOP);
	}

	public int getContentPaddingRight() {
		return get4WayContentPadding(DIR_RIGHT);
	}

	public int getContentPaddingBottom() {
		return get4WayContentPadding(DIR_BOTTOM);
	}

	public boolean isFillWidth() {
		String w_str = get("width");
		if(w_str != null && w_str.equals("*")) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean isFillHeight() {
		String h_str = get("height");
		if(h_str != null && h_str.equals("*")) {
			return true;
		}else {
			return false;
		}
	}
	
	public int getWidth(int parent_width) {
		if(isFillWidth()) {
			return -1;
		}else {
			double w = getDouble("width");
			if (w > 0) {
				if (w >= 0 && w <= 1.0) {
					w = parent_width * w;
				} else if (w > parent_width) {
					w = parent_width;
				}
			} else {
				w = parent_width;
			}
			return (int) w;
		}
	}

	/*public int getInnerWidth(int parent_width) {
		return getWidth(parent_width) - getBorderWidthLeft() - getPaddingLeft() - getPaddingRight() - getBorderWidthRight();
	}*/

	public int getIWidth(int inner_width) {
		double w = getDouble("iwidth");
		if (w > 0) {
			if (w >= 0 && w <= 1) {
				w = inner_width * w;
			} else if (w > inner_width) {
				w = inner_width;
			}
		} else {
			w = 0;
		}
		//System.out.println("Style.getIWidth: " + inner_width + "/" + w + "/" + get("iwidth"));
		return (int) w;
	}

	public int getIHeight(int inner_height) {
		double h = getDouble("iheight");
		if (h > 0) {
			if (h >= 0 && h <= 1) {
				h = inner_height * h;
			} else if (h > inner_height) {
				h = inner_height;
			}
		} else {
			h = 0;
		}
		return (int) h;
	}

	public int getHeight(int full_screen_height) {
		if(isFillHeight()) {
			return -1;
		}else {
			double h = getDouble("height");
			if (h > 0) {
				if (h > 0 && h <= 1.0) {
					h = full_screen_height * h;
				}
			}else {
				h = 0;
			}
			return (int) h;
		}
	}

	public BackgroundDescriptor getHintBackground() {
		return getBackground(PREFIX_HINT);
	}

	public BackgroundDescriptor getBackground() {
		return getBackground(null);
	}

	public BackgroundDescriptor getFocusBackground() {
		return getBackground("focus");
	}
	
	public BackgroundDescriptor getCheckedBackground() {
		return getBackground("checked");
	}

	public BackgroundDescriptor getRowFocusBackground() {
		return getBackground("rowfocus");
	}

	public BackgroundDescriptor getTitleBackground() {
		return getBackground("title");
	}

	public BackgroundDescriptor getStatusBackground() {
		return getBackground("status");
	}

	public BackgroundDescriptor getBodyBackground() {
		return getBackground("body");
	}

	public BackgroundDescriptor getFocusBodyBackground() {
		return getBackground("focus-body");
	}
	
	public BackgroundDescriptor getCheckedBodyBackground() {
		return getBackground("checked-body");
	}

	// 2010-11-26添加MAGInfoGrid单双行颜色变化
	public BackgroundDescriptor getOddBackground() {
		return getBackground("odd");
	}

	public BackgroundDescriptor getEvenBackground() {
		return getBackground("even");
	}

	// 2011-2-23添加MAGInput输入框背景
	public BackgroundDescriptor getContentBackground() {
		return getBackground("content");
	}

	public BackgroundDescriptor getContentFocusBackground() {
		return getBackground("content-focus");
	}

	public BackgroundDescriptor getBackground(String prefix) {
		String key = "background";
		if (prefix != null && prefix.length() > 0) {
			key = prefix + "-" + key;
		}
		String bg_str = get(key);
		if (null != bg_str) {
			return new BackgroundDescriptor(bg_str);
		} else {
			key = "bgcolor";
			if (prefix != null && prefix.length() > 0) {
				key = prefix + "-" + key;
			}
			bg_str = get(key);
			if (null != bg_str) {
				return new BackgroundDescriptor(bg_str);
			} else {
				return null;
			}
		}
	}

	public int getColor() {
		return getColor(null, null);
	}

	public int getFocusColor() {
		return getColor("focus", null);
	}

	public int getTitleColor() {
		return getColor("title", null);
	}

	public int getTitleFocusColor() {
		return getColor("title-focus", null);
	}

	/*
	 * public int getHintColor() { return getColor("hint", null); }
	 */

	private int getColor(String prefix, String suffix) {
		String key = "color";
		if (prefix != null && prefix.length() > 0) {
			key = prefix + "-" + key;
		}
		if (suffix != null && suffix.length() > 0) {
			key += "-" + suffix;
		}
		String clr = get(key);
		if (null != clr) {
			int val = GraphicUtilityClass.htmlColor(clr);
			// System.out.println("style().getColor: " + key + "=" + clr + "/" +
			// Integer.toHexString(val));
			return val;
		} else {
			return GraphicUtilityClass.INVALID_COLOR;
		}
	}

	public int getAlpha() {
		return getAlpha(null, null);
	}

	private int getAlpha(String prefix, String suffix) {
		String key = "alpha";
		if (prefix != null && prefix.length() > 0) {
			key = prefix + "-" + key;
		}
		if (suffix != null && suffix.length() > 0) {
			key += "-" + suffix;
		}
		String alpha = get(key);
		if (null != alpha) {
			return Integer.parseInt(alpha);
		} else {
			return 0xff;
		}
	}

	private int get4WayColor(String prefix, String dir) {
		int color = getColor(prefix, dir);
		if (!GraphicUtilityClass.isValidColor(color)) {
			return getColor(prefix, null);
		} else {
			return color;
		}
	}

	public float getFontSize(Context context) {
		return getFontScale(null) * GraphicUtilityClass.getMinFontSize(context);
	}

	private float getFontScale(String prefix) {
		String key = "font-scale";
		if (prefix != null && prefix.length() > 0) {
			key = prefix + "-" + key;
		}
		String sz = get(key);
		if (null != sz) {
			return Float.parseFloat(sz);
		} else {
			return 1.0f;
		}
	}

	private boolean getFontBold(String prefix) {
		String key = "font-weight";
		if (prefix != null && prefix.length() > 0) {
			key = prefix + "-" + key;
		}
		String bd = get(key);
		if (null != bd) {
			return bd.equals("bold");
		} else {
			return false;
		}
	}

	private boolean getFontItalic(String prefix) {
		String key = "font-style";
		if (prefix != null && prefix.length() > 0) {
			key = prefix + "-" + key;
		}
		String bd = get(key);
		if (null != bd) {
			return bd.equals("italic");
		} else {
			return false;
		}
	}

	private boolean getFontUnderline(String prefix) {
		String key = "text-decoration";
		if (prefix != null && prefix.length() > 0) {
			key = prefix + "-" + key;
		}
		String bd = get(key);
		if (null != bd) {
			return bd.equals("underline");
		} else {
			return false;
		}
	}

	private Paint getFont(Context context, String prefix) {
		return PaintRepository.getFontPaint(context, getFontBold(prefix), getFontItalic(prefix), getFontUnderline(prefix), getFontScale(prefix), getColor(prefix, null), getAlpha(
				prefix, null));
	}

	public Paint getPaint(Context context) {
		return getFont(context, null);
	}

	public Paint getTitlePaint(Context context) {
		return getFont(context, "title");
	}

	public Paint getHintPaint(Context context) {
		return getFont(context, "hint");
	}

	public String getTitleIcon() {
		return get("title-icon");
	}

	public TextStyleDescriptor getStatusTextStyle() {
		return getTextStyle("status");
	}

	private static final String TITLE = "title";

	public TextStyleDescriptor getTitleTextStyle() {
		TextStyleDescriptor style = getTextStyle(TITLE);
		if (style == null) {
			String style_str = "";
			if (getTitleIcon() != null) {
				style_str += " icon=" + getTitleIcon();
			}
			if (GraphicUtilityClass.isValidColor(getTitleColor())) {
				style_str += " color=" + GraphicUtilityClass.colorHTML(getTitleColor());
			}
			if (getFontBold(TITLE)) {
				style_str += " font-weight=bold";
			}
			if (getFontItalic(TITLE)) {
				style_str += " font-style=italic";
			}
			if (getFontUnderline(TITLE)) {
				style_str += " text-decoration=underline";
			}
			if (getFontScale(TITLE) != 1.0f) {
				style_str += " font-scale=" + getFontScale(TITLE);
			}
			style_str += " alpha=" + getAlpha(TITLE, null);

			style = new TextStyleDescriptor(style_str);
		}
		return style;
	}

	public TextStyleDescriptor getHintTextStyle() {
		return getTextStyle("hint");
	}

	public TextStyleDescriptor getTextStyle() {
		return getTextStyle(null);
	}

	public TextStyleDescriptor getTextStyle(String prefix) {
		String key = "text-style";
		if (prefix != null && prefix.length() > 0) {
			key = prefix + "-" + key;
		}
		String str = get(key);
		if (str != null) {
			TextStyleDescriptor tsd = new TextStyleDescriptor(str);
			return tsd;
		} else {
			return null;
		}
	}

	/*
	 * public String getBGImage() { return get("bg-image"); }
	 */

	public int getHintWait() {
		int w = getInt("hint-wait");
		if (w < 0) {
			return 1000;
		} else {
			return w;
		}
	}

	public int getHintDuration() {
		int h = getInt("hint-duration");
		if (h < 0) {
			return 5000;
		} else {
			return h;
		}
	}
}
