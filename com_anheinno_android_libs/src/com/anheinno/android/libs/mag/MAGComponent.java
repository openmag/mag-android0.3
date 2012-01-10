/*
 * MAGComponent.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.anheinno.android.libs.R;
import com.anheinno.android.libs.graphics.Align;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.GraphicUtilityClass;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.graphics.VAlign;
import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.ui.EasyDialog;
import com.anheinno.android.libs.util.ClassUtility;

/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 * 
 */
public abstract class MAGComponent implements MAGComponentInterface, OnTouchListener, OnFocusChangeListener {

	private static Vector<MAGComponent> _focus_components;
	
	static {
		_focus_components = new Vector<MAGComponent>();
	}
	
	private Context _context;

	// private String _type;
	private String _title;
	private String _hint;
	private String _status;

	private boolean _on_focus;
	private boolean _checked;
	private boolean _visible;

	private String _id;
	private MAGContainerInterface _parent;
	private View _field;
	private MAGStyle _style;

	// protected MAGComponent _self;

	private int _width;
	private int _row_height;
	// private int _actual_width;
	//private int _occupy_width;
	// private int _actual_height;
	//private int _occupy_height;

	// //////////////////////////////
	private int _left;
	private int _top;

	private VAlign _valign;
	private Align _align;

	private int _border_width_left;
	private int _border_width_top;
	private int _border_width_bottom;
	private int _border_width_right;

	private int _border_color_left;
	private int _border_color_top;
	private int _border_color_bottom;
	private int _border_color_right;

	private int _border_focus_color_left;
	private int _border_focus_color_top;
	private int _border_focus_color_bottom;
	private int _border_focus_color_right;

	private int _padding_left;
	private int _padding_top;
	private int _padding_bottom;
	private int _padding_right;
	// ////////////////////////////////

	private static final Align DEFAULT_ALIGN_INIT = null;
	private static final VAlign DEFAULT_VALIGN_INIT = null;
	private static final int DEFAULT_BORDER_WIDTH_INIT = -2;
	private static final int DEFAULT_BORDER_COLOR_INIT = GraphicUtilityClass.INVALID_COLOR;
	private static final int DEFAULT_PADDING_INIT = -2;

	/*protected MAGComponent(Context context) {
		this();
		setContext(context);
	}*/

	public MAGComponent() {

		_context = null;

		// _type = null;
		_title = null;
		_hint = null;
		_status = null;
		_id = null;
		_parent = null;
		_field = null;
		// _self = this;

		_visible = true;
		_on_focus = false;
		_checked = false;
		
		_style = MAGStyle.getDefault();

		_width = 0;
		_row_height = 0;
		// _actual_width = 0;
		//_occupy_width  = 0;
		// _actual_height = 0;
		//_occupy_height = 0;

		_left = 0;
		_top = 0;

		// cached style
		_align = DEFAULT_ALIGN_INIT;
		_valign = DEFAULT_VALIGN_INIT;

		_border_width_left = DEFAULT_BORDER_WIDTH_INIT;
		_border_width_top = DEFAULT_BORDER_WIDTH_INIT;
		_border_width_right = DEFAULT_BORDER_WIDTH_INIT;
		_border_width_bottom = DEFAULT_BORDER_WIDTH_INIT;

		_border_color_left = DEFAULT_BORDER_COLOR_INIT;
		_border_color_top = DEFAULT_BORDER_COLOR_INIT;
		_border_color_right = DEFAULT_BORDER_COLOR_INIT;
		_border_color_bottom = DEFAULT_BORDER_COLOR_INIT;

		_border_focus_color_left = DEFAULT_BORDER_COLOR_INIT;
		_border_focus_color_top = DEFAULT_BORDER_COLOR_INIT;
		_border_focus_color_right = DEFAULT_BORDER_COLOR_INIT;
		_border_focus_color_bottom = DEFAULT_BORDER_COLOR_INIT;

		_padding_left = DEFAULT_PADDING_INIT;
		_padding_top = DEFAULT_PADDING_INIT;
		_padding_right = DEFAULT_PADDING_INIT;
		_padding_bottom = DEFAULT_PADDING_INIT;

	}

	public void setContext(Context context) {
		_context = context;
	}

	public String toString() {
		return type() + " title: " + title();
	}

	public void setParent(MAGContainerInterface p) {
		_parent = p;
		if (_parent != null) {
			setContext(p.getContext());
			_parent.addChild(this);
		}
	}

	public MAGContainerInterface getParent() {
		return _parent;
	}

	public void unlink() {
		if (_parent != null) {
			_parent.removeChild(this);
			_parent = null;
		}
		if (_field != null) {
			// _field.setFocusListener(null);
			_field = null;
		}
	}

	/*
	 * public void log(String msg) { MAGDocumentScreen ds =
	 * getMAGDocumentScreen(); if (ds != null) { String idstr = type() + "(" +
	 * title(); if (idstr.length() > 10) { idstr = idstr.substring(0, 10); }
	 * idstr += ") "; ds.browserLog(idstr + msg); } }
	 */
	
	public static void clearPreviousFocus() {
		synchronized(_focus_components) {
			if(_focus_components.size() > 0) {
				for(int i = 0; i < _focus_components.size(); i ++) {
					MAGComponent comp = _focus_components.elementAt(i);
					comp.setOffFocus();
				}
				_focus_components.removeAllElements();
			}
		}
	}
	
	public static void storeFocusComponent(MAGComponent comp) {
		synchronized(_focus_components) {
			if(!_focus_components.contains(comp)) {
				_focus_components.add(comp);
			}
		}
	}

	public void setOnFocus(boolean showhint, boolean showstatus) {
		_on_focus = true;
		
		if(!(this instanceof MAGContainerInterface)) {
			clearPreviousFocus();
			storeFocusComponent(this);
		}
		
		if (showstatus) {
			/**
			 * 获取status文字，并显示
			 */
			// MAGDocument doc = getMAGDocument();
			MAGDocumentScreen screen = getMAGDocumentScreen();
			MAGComponent status_comp = null;
			if (status() != null && status().length() > 0) {
				status_comp = this;
			}
			int status_height = screen.getStatusHeight();
			if (status_comp != null) {
				screen.setStatusText(status_comp.status());
				TextStyleDescriptor style = status_comp.style().getStatusTextStyle();
				if (style != null) {
					screen.setStatusStyle(style);
				} else {
					screen.removeStatusStyle();
				}
				BackgroundDescriptor desc = status_comp.style().getStatusBackground();
				if (desc != null) {
					screen.setStatusBackground(desc);
				} else {
					screen.removeStatusBackground();
				}
				showstatus = false;
			} else if (_parent == null) {
				if (screen.removeStatusText()) {
					// screen.commitLayoutChange();
				}
			}

			if (screen.getStatusHeight() != status_height) {
				screen.commitLayoutChange();
			}

		}

		if (showhint && hint() != null && hint().length() > 0) {
			updateTooltip();
			showhint = false;
		}
		invalidate();

		if (_parent != null) {
			_parent.setOnFocus(showhint, showstatus);
		}
	}

	/**
	 * 获取tooltip文字(_hint)，并且显示
	 */
	private void updateTooltip() {

		MAGDocumentScreen screen = getMAGDocumentScreen();
		String hint = hint();
		// System.out.println("set Focus there exists hint! " + hint);
		if (hint != null && hint.length() > 0 && screen != null) {
			// screen.registerTooltip(this);
		}
	}

	public void setOffFocus() {
		_on_focus = false;
		// getMAGDocumentScreen().resetTooltip();

		invalidate();

		if (status() != null && status().length() > 0) {
			MAGDocumentScreen screen = getMAGDocumentScreen();
			if(screen != null && screen.getStatusHeight() > 0) {
				screen.removeStatusText();
			}
		}
		
		if (_parent != null) {
			_parent.setOffFocus();
		}

	}
	
	public void setChecked() {
		_checked = true;
		invalidate();
	}
	
	public void setUnChecked() {
		_checked = false;
		invalidate();
	}
	
	public boolean isChecked() {
		return _checked;
	}

	public boolean isOnFocus() {
		return _on_focus;
	}

	public void hide() {
		_visible = false;
	}

	public void show() {
		_visible = true;
	}

	public boolean visible() {
		return _visible;
	}

	public String id() {
		return _id;
	}

	public String title() {
		return _title;
	}

	public String status() {
		return _status;
	}

	public String hint() {
		return _hint;
	}

	public String type() {
		return ClassUtility.getClassName(this);
	}

	public MAGStyle style() {
		return _style;
	}

	public Context getContext() {
		return _context;
	}

	/*private int getParentWidth() {
		if (_parent != null) {
			return _parent.getInnerWidth();
		} else {
			return FullScreen.getFullScreenWidth(getContext());
		}
	}*/

	/*
	 * 当getField()不为空（控件ui初始化后）才能返回正确的值
	 */
	/*public int getInnerWidth() {
		if (_parent == null) {
			return FullScreen.getFullScreenWidth(getContext());
		}
		int w = 0;
		if (getField() != null) {
			w = getWidth() - getBorderWidthLeft() - getBorderWidthRight() - getPaddingLeft() - getPaddingRight();
		}
		return w;
	}*/
	
	/*public int getWidth() {
		int w = 0;
		if (getField() != null) {
			_occupy_width = style().getWidth(getParentWidth());
			w = _occupy_width;
		}
		return w;
	}*/

	public void setWidth(int width) {
		if(getField() != null) {
			_width = width;
		}else {
			_width = 0;
		}
	}
	
	public int getWidth() {
		return _width;
	}
	
	public int getInnerWidth() {
		int w = 0;
		if (getField() != null) {
			w = getWidth() - getBorderWidthLeft() - getBorderWidthRight() - getPaddingLeft() - getPaddingRight();
		}
		return w;
	}

	public int getInnerHeight() {
		int h = 0;
		if (getField() != null) {
			h = getHeight() - getBorderWidthTop() - getBorderWidthBottom() - getPaddingTop() - getPaddingBottom();
			if(h < 0) {
				h = 0;
			}
		}
		return h;
	}
	
	private int getActualHeight() {
		int h = 0;
		if(null != getField()) {
			h = getField().getMeasuredHeight();
			h += getBorderWidthTop() + getBorderWidthBottom() + getPaddingTop() + getPaddingBottom();
		}
		return h;
	}

	public void setRowHeight(int h) {
		_row_height = h;
	}

	public int getHeight() {
		return Math.max(_row_height, getActualHeight());
	}

	public void setLeft(int left) {
		_left = left;
	}

	public void setTop(int top) {
		_top = top;
	}

	public int getLeft() {
		return _left;
	}

	public int getTop() {
		return _top;
	}

	public Align getAlign() {
		if (_align == DEFAULT_ALIGN_INIT) {
			_align = style().getAlign();
		}
		return _align;
	}

	public VAlign getVAlign() {
		if (_valign == DEFAULT_VALIGN_INIT) {
			_valign = style().getVAlign();
		}
		return _valign;
	}

	public int getBorderWidthLeft() {
		if (_border_width_left == DEFAULT_BORDER_WIDTH_INIT) {
			_border_width_left = style().getBorderWidthLeft();
		}
		return _border_width_left;
	}

	public int getBorderWidthTop() {
		if (_border_width_top == DEFAULT_BORDER_WIDTH_INIT) {
			_border_width_top = style().getBorderWidthTop();
		}
		return _border_width_top;
	}

	public int getBorderWidthRight() {
		if (_border_width_right == DEFAULT_BORDER_WIDTH_INIT) {
			_border_width_right = style().getBorderWidthRight();
		}
		return _border_width_right;
	}

	public int getBorderWidthBottom() {
		if (_border_width_bottom == DEFAULT_BORDER_WIDTH_INIT) {
			_border_width_bottom = style().getBorderWidthBottom();
		}
		return _border_width_bottom;
	}

	public int getBorderColorLeft(boolean focus) {
		if (focus) {
			if (_border_focus_color_left == DEFAULT_BORDER_COLOR_INIT) {
				_border_focus_color_left = style().getBorderColorLeft(true);
			}
			return _border_focus_color_left;
		} else {
			if (_border_color_left == DEFAULT_BORDER_COLOR_INIT) {
				_border_color_left = style().getBorderColorLeft(false);
			}
			return _border_color_left;
		}
	}

	public int getBorderColorTop(boolean focus) {
		if (focus) {
			if (_border_focus_color_top == DEFAULT_BORDER_COLOR_INIT) {
				_border_focus_color_top = style().getBorderColorTop(true);
			}
			return _border_focus_color_top;
		} else {
			if (_border_color_top == DEFAULT_BORDER_COLOR_INIT) {
				_border_color_top = style().getBorderColorTop(false);
			}
			return _border_color_top;
		}
	}

	public int getBorderColorRight(boolean focus) {
		if (focus) {
			if (_border_focus_color_right == DEFAULT_BORDER_COLOR_INIT) {
				_border_focus_color_right = style().getBorderColorRight(true);
			}
			return _border_focus_color_right;
		} else {
			if (_border_color_right == DEFAULT_BORDER_COLOR_INIT) {
				_border_color_right = style().getBorderColorRight(false);
			}
			return _border_color_right;
		}
	}

	public int getBorderColorBottom(boolean focus) {
		if (focus) {
			if (_border_focus_color_bottom == DEFAULT_BORDER_COLOR_INIT) {
				_border_focus_color_bottom = style().getBorderColorBottom(true);
			}
			return _border_focus_color_bottom;
		} else {
			if (_border_color_bottom == DEFAULT_BORDER_COLOR_INIT) {
				// System.out.println(Integer.toHexString(_border_color_bottom));
				_border_color_bottom = style().getBorderColorBottom(false);
				// System.out.println(Integer.toHexString(_border_color_bottom));
			}
			return _border_color_bottom;
		}
	}

	public void setPaddingLeft(int padding) {
		_padding_left = padding;
	}

	public int getPaddingLeft() {
		if (_padding_left == DEFAULT_PADDING_INIT) {
			_padding_left = style().getPaddingLeft();
		}
		return _padding_left;
	}

	public int getPaddingTop() {
		if (_padding_top == DEFAULT_PADDING_INIT) {
			_padding_top = style().getPaddingTop();
		}
		return _padding_top;
	}

	public int getPaddingRight() {
		if (_padding_right == DEFAULT_PADDING_INIT) {
			_padding_right = style().getPaddingRight();
		}
		return _padding_right;
	}

	public int getPaddingBottom() {
		if (_padding_bottom == DEFAULT_PADDING_INIT) {
			_padding_bottom = style().getPaddingBottom();
		}
		return _padding_bottom;
	}

	/**
	 * 获得该component内部显示tooltip窗口的起始垂直偏移量
	 * 
	 * @return 垂直偏移量像素值
	 */
	public int getHintVerticalOffset() {
		/*
		 * if(_field != null) { return _field.getHeight() / 2; } else { return
		 * 0; }
		 */
		return -1;
	}

	/**
	 * 获得该component内部显示tooltip窗口的起始水平偏移量
	 * 
	 * @return 水平偏移量像素值
	 */
	public int getHintHorizontalOffset() {
		/*
		 * if (null != _field) { return _field.getWidth() / 2; } else { return
		 * 0; }
		 */
		return -1;
	}

	public int getOffsetLeft() {
		if (null != getField()) {
			//System.out.println(this + " align=" + getAlign() + " width=" + getWidth() + " actual_width=" + getField().getMeasuredWidth());
			switch (getAlign()) {
			case CENTER:
				return (getWidth() - getField().getMeasuredWidth()) / 2;
			case RIGHT:
				return getWidth() - getField().getMeasuredWidth() - getBorderWidthRight() - getPaddingRight();
			case LEFT:
			default:
				return getBorderWidthLeft() + getPaddingLeft();
			}
		} else {
			return 0;
		}
	}

	public int getOffsetTop() {
		if (null != getField()) {
			int top = getBorderWidthTop() + getPaddingTop();
			int bot = getBorderWidthBottom() + getPaddingBottom();

			// System.out.println("Component height: " + getRowHeight() +
			// " Field height: " + getField().getHeight() + " valign: " +
			// valign + " top=" + top + " bot=" + bot);

			switch (getVAlign()) {
			case MIDDLE:
				return top + (getHeight() - top - bot - getField().getMeasuredHeight()) / 2;
			case BOTTOM:
				return getHeight() - getField().getMeasuredHeight() - bot;
			case TOP:
			default:
				return top;
			}
		} else {
			// System.out.println("comp " + toString() + " has no field!!!");
			return 0;
		}
	}

	/**
	 * 获取通一个MAGContainer下id不为空的兄弟组件数组
	 * 
	 * @return 如果所有兄弟组件都没有设置id，则返回null
	 */
	public MAGComponentInterface[] getNamedSiblings() {
		if (_parent != null) {
			return _parent.getNamedChildren();
		} else {
			return null;
		}
	}

	/**
	 * 返回当前组件所在MAGDocument组件实例
	 * 
	 * @return 如果该组件还未加入一个MAGDocument，则返回null
	 */
	private MAGDocument __getMAGDocument(boolean top) {
		MAGComponentInterface comp = this;
		while (comp != null) {
			if (comp instanceof MAGDocument && (!top || comp.getParent() == null)) {
				return (MAGDocument) comp;
			}
			comp = comp.getParent();
		}
		return null;
	}
	
	public MAGDocument getMAGDocument() {
		return __getMAGDocument(false);
	}
	
	public MAGDocument getTopMAGDocument() {
		return __getMAGDocument(true);
	}
	
	public MAGDocumentScreen getMAGDocumentScreen() {
		MAGDocument document = getTopMAGDocument();
		if(document != null) {
			return (MAGDocumentScreen)document.getMAGDocumentField().getMAGDocumentContainer();
		}else {
			return null;
		}
	}

	/**
	 * 返回当前组件坐在的MAGDocument的MAGDocumentScreen窗口
	 * 
	 * @return 如果该组件还未加入一个MAGDocument，则返回null 如果该MAGDocument还未初始化其可视控件，则返回null
	 * 
	 */
	/*public JSONBrowserInterface getJSONBrowser() {
		MAGFrame frame = getMAGFrame();
		if(frame != null) {
			return (JSONBrowserInterface)frame.getField();
		}else {
			MAGDocument doc = getMAGDocument();
			if (doc != null) {
				return (JSONBrowserInterface) doc.getField();
			} else {
				return null;
			}
		}
	}*/

	/**
	 * 返回包含该组件的最下级的MAGPanel实例
	 * 
	 * @return 如果该组件没有在MAGPanel中，则返回null
	 */
	public MAGFrame getMAGFrame() {
		MAGComponentInterface comp = this;
		while (comp != null) {
			if (comp instanceof MAGFrame) {
				return (MAGFrame) comp;
			}
			comp = comp.getParent();
		}
		return null;
	}
	
	/**
	 * 返回包含该组件的最下级的MAGPanel实例
	 * 
	 * @return 如果该组件没有在MAGPanel中，则返回null
	 */
	public MAGPanel getMAGPanel() {
		MAGComponentInterface comp = this;
		while (comp != null) {
			if (comp instanceof MAGPanel) {
				return (MAGPanel) comp;
			}
			comp = comp.getParent();
		}
		return null;
	}

	/*
	 * public MAGPanelField getMAGPanelField() { MAGPanel panel = getMAGPanel();
	 * if (panel != null) { return (MAGPanelField) panel.getField(); } return
	 * null; }
	 */

	/*
	 * public boolean hasField(View f) { View mag_field = getField(); if
	 * (mag_field != null) { while (f != null && f != mag_field) { f =
	 * f.getParent(); } if (f != null) { return true; } } return false; }
	 */

	/*
	 * public MAGComponent getLeafComponentWithField(Field f) { MAGComponent
	 * comp = null; if (this instanceof MAGContainerInterface) {
	 * MAGContainerInterface mc = (MAGContainerInterface) this; for (int i = 0;
	 * i < mc.childrenNum(); i++) { MAGComponent child = mc.getChild(i);
	 * MAGComponent c = child.getLeafComponentWithField(f); if (c != null) {
	 * comp = c; break; } } }else if (this.hasField(f)) { comp = this; } return
	 * comp; }
	 */

	protected boolean checkMandatory(JSONObject o, String attr) {
		if (!o.has(attr)) {
			String info = getClass().getName() + " " + getContext().getString(R.string.mag_component_miss_attribute) + " \"" + attr + "\"";
			
			LOG.error(this, info, null);
			
			EasyDialog.postInfo(getContext(), info);
			
			return false;
		} else {
			return true;
		}
	}
	
	public boolean fromJSON(JSONObject o) {
		try {
			if (!checkMandatory(o, "_type")) {
				return false;
			}

			// _type = o.getString("_type");

			if (o.has("_title")) {
				_title = o.getString("_title");
			} else {
				_title = "";
			}

			if (o.has("_hint")) {
				_hint = o.getString("_hint");
			} else {
				_hint = "";
			}

			if (o.has("_status")) {
				_status = o.getString("_status");
			} else {
				_status = "";
			}

			if (o.has("_id")) {
				_id = o.getString("_id");
			} else {
				_id = "";
			}
			if (o.has("_class") && o.has("_style") && !(this instanceof MAGDocument)) {
				_style = new MAGStyle(retrieveStyle(o.getString("_class")));
				JSONObject style = o.getJSONObject("_style");
				_style.put(style);
			} else if (o.has("_class")) {
				if (!(this instanceof MAGDocument)) {
					_style = retrieveStyle(o.getString("_class"));
				}
			} else if (o.has("_style")) {
				JSONObject style = o.getJSONObject("_style");
				_style = new MAGStyle();
				_style.put(style);
			}
			return true;
		} catch (final JSONException e) {
			LOG.error(this, "fromJSON", e);
		}
		return false;
	}
	
	private MAGStyle retrieveStyle(String styleclass) {
		MAGComponentInterface comp = this;
		while(comp != null) {
			if(comp instanceof MAGDocument && ((MAGDocument)comp).getStyle(styleclass) != null) {
				return ((MAGDocument)comp).getStyle(styleclass);
			}
			comp = comp.getParent();
		}
		return MAGStyle.getDefault();
	}

	public String getAttributeValue(String fieldname) {
		if (fieldname.equals("_type")) {
			return ClassUtility.getClassName(this);
		} else if (fieldname.equals("_title")) {
			return _title;
		} else if (fieldname.equals("_hint")) {
			return _hint;
		} else if (fieldname.equals("_status")) {
			return _status;
		} else if (fieldname.equals("id")) {
			return _id;
		} else {
			return null;
		}
	}

	// 2010-5-27 增加toJSON
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		try {
			// obj.put("_type", _type);
			if (_title != null && _title.length() > 0) {
				obj.put("_title", _title);
			}
		} catch (JSONException je) {
			System.err.println(je.toString());
		}
		return obj;
	}

	public synchronized final void setField(View f) {
		// MAGDocument should never call this method
		if(!(this instanceof MAGDocument)) {
			if(_field != null) {
				if(_field.getBackground() != null) {
					_field.getBackground().setCallback(null);
				}
				if(_field instanceof ViewGroup) {
					((ViewGroup)_field).removeAllViews();
				}
				_field.setOnFocusChangeListener(null);
				_field.setOnTouchListener(null);
				_field = null;
			}
			
			if(f != null) {
				_field = f;
				//if (!(this instanceof MAGContainerInterface)) {
				_field.setOnFocusChangeListener(this);
				_field.setOnTouchListener(this);
				//}
			}
		}else {
			_field = f;
		}
	}

	public final View getField() {
		return _field;
	}

	public boolean execScriptCommand(MAGScriptCommand cmd) {
		System.out.println("Exec " + cmd.toString() + " #Params: " + cmd.getParameterNum());
		String obj = cmd.getFirstObject();
		if (obj != null) {
			if (obj.equals("this")) {
				String func = cmd.getMethodName();
				if (func.equals("set")) {
					if (cmd.getParameterNum() == 2 && setAttribute(cmd.getParameter(0), cmd.getParameter(1))) {
						updateField(getField());
						return true;
					}
				}
			}
		}
		return false;
	}

	protected boolean setAttribute(String name, String value) {
		if (name.equals("title")) {
			this._title = value;
			return true;
		}
		return false;
	}

	public static MAGComponentInterface parseJSON(MAGContainerInterface p, JSONObject o) {
		try {
			if (!o.has("_type")) {
				return null;
			}
			String type = o.getString("_type");
			if (type == null || type.length() == 0) {
				return null;
			}
			
			MAGComponent comp = null;

			Class<?> mag_class = ClassUtility.getSiblingClass("com.anheinno.android.libs.mag", type);
			if (mag_class != null) {
				try {
					comp = (MAGComponent) mag_class.newInstance();
				} catch (final Exception e) {
					LOG.error(mag_class.getName(), "castClass", e);
				}
			}

			if (comp == null) {
				comp = new MAGDefault();
			}

			comp.setParent(p);

			comp.fromJSON(o);
			
			return comp;

		} catch (final Exception e) {
			LOG.error("MAGComponent", "parseJSON", e);
		}
		return null;
	}

	//public void setUIFocus() {
		//setOnFocus(true, true);
		//if (_field != null) {
			// _field.setFocus();
			//_field.requestFocusFromTouch();
		//}
	//}

	public void onFocusChange(View v, boolean hasFocus) {
		if (v == this.getField()) {
			System.out.println(this + " onFocusChange " + v + " focus=" + hasFocus);
			if (hasFocus) {
				System.out.println("Gain focus: " + this);
				setOnFocus(true, true);
			} else {
				//if(!_checkable) {
					System.out.println("Lost focus: " + this);
					setOffFocus();
				//}
			}
		} else {
			LOG.error(this, "View is not this component: " + this, null);
			//setOffFocus();
		}
	}

	public boolean onTouch(View v, MotionEvent event) {
		System.out.println(this + " onTouch " + event.toString());
		if(v == this.getField()) {
			if (v.isFocusable()) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					setOnFocus(true, true);
					//return true;
				} else if(event.getAction() == MotionEvent.ACTION_UP) {
					//if(!_checkable) {
						setOffFocus();
					//}
					//return true;
				}
				// if (event.getAction() == MotionEvent.ACTION_UP) {
				// setOffFocus();
				// }
			}
		}
		return false;
	}

	public void invalidate() {
		if (_parent != null) {
			_parent.invalidateChild(this);
		} else if (getMAGDocumentScreen() != null) {
			getMAGDocumentScreen().invalidate();
		}
	}
	
	//public void setCheckable(boolean checkable) {
	//	_checkable = checkable;
	//}

	private void drawBorder(Canvas canvas, int l, int t, int w, int h, boolean focus) {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);

		// System.out.println("borderColor left=" +
		// Integer.toHexString(getBorderColorLeft(focus)) + " top=" +
		// Integer.toHexString(getBorderColorTop(focus)) + " bottom=" +
		// Integer.toHexString(getBorderColorBottom(focus)) + " right=" +
		// Integer.toHexString(getBorderColorRight(focus)));
		// System.out.println("borderWidth left=" + getBorderWidthLeft() +
		// " top=" + getBorderWidthTop() + " bottom=" + getBorderWidthBottom() +
		// " right=" + getBorderWidthRight());

		if (GraphicUtilityClass.isValidColor(getBorderColorTop(focus)) && getBorderWidthTop() > 0) {
			// System.out.println("drawborderTop: l=" + l + " t=" + t + " w=" +
			// w + " h=" + h);
			paint.setColor(getBorderColorTop(focus));
			canvas.drawRect(l, t, l + w, t + getBorderWidthTop(), paint);
		}
		if (GraphicUtilityClass.isValidColor(getBorderColorLeft(focus)) && getBorderWidthLeft() > 0) {
			// System.out.println("drawborderLeft: l=" + l + " t=" + t + " w=" +
			// w + " h=" + h);
			paint.setColor(getBorderColorLeft(focus));
			canvas.drawRect(l, t, l + getBorderWidthLeft(), t + h, paint);
		}
		if (GraphicUtilityClass.isValidColor(getBorderColorRight(focus)) && getBorderWidthRight() > 0) {
			// System.out.println("drawborderRight: l=" + l + " t=" + t + " w="
			// + w + " h=" + h);
			paint.setColor(getBorderColorRight(focus));
			canvas.drawRect(l + w - getBorderWidthRight(), t, l + w, t + h, paint);
		}
		if (GraphicUtilityClass.isValidColor(getBorderColorBottom(focus)) && getBorderWidthBottom() > 0) {
			// System.out.println("drawborderBottom: l=" + l + " t=" + t + " w="
			// + w + " h=" + h);
			paint.setColor(getBorderColorBottom(focus));
			canvas.drawRect(l, t + h - getBorderWidthBottom(), l + w, t + h, paint);
		}
	}

	private static final DashPathEffect _outter_effect;
	private static final DashPathEffect _inner_effect;
	private static final DashPathEffect _view_effect;
	private static final Paint _border_paint;
	private static final Rect _border_rect;
	static {
		_outter_effect = new DashPathEffect(new float[] { 2, 2 }, 0);
		_inner_effect = new DashPathEffect(new float[] { 2, 2 }, 0);
		_view_effect = new DashPathEffect(new float[] { 5, 5 }, 0);
		_border_paint = new Paint();
		_border_paint.setStyle(Paint.Style.STROKE);
		_border_rect = new Rect();
	}

	public void drawBackground(Canvas canvas, int l, int t, int w, int h) {
		
		//System.out.println(this.toString() + " left=" + l + " top=" + t + " width=" + w + " height=" + h);
		int draw_w = w - getBorderWidthLeft() - getBorderWidthRight();
		int draw_h = h - getBorderWidthTop() - getBorderWidthBottom();

		BackgroundDescriptor tmp_bg = null;
		if (isOnFocus()) {
			tmp_bg = style().getFocusBackground();
		}else if(isChecked()) {
			tmp_bg = style().getCheckedBackground();
			if(tmp_bg == null) {
				tmp_bg = style().getFocusBackground();
			}
		}
		if (tmp_bg == null) {
			tmp_bg = style().getBackground();
		}
		if (tmp_bg != null) {
			/*
			 * Bitmap tmp_bitmap =
			 * GradientRectangleFactory.getGradientBitmap(getContext(), draw_w,
			 * draw_h, tmp_bg); if (tmp_bitmap != null) {
			 * canvas.drawBitmap(tmp_bitmap, l + getBorderWidthLeft(), t +
			 * getBorderWidthTop(), null); }
			 */
			tmp_bg.draw(getContext(), canvas, l + getBorderWidthLeft(), t + getBorderWidthTop(), draw_w, draw_h);
		}

		if (isOnFocus()) {
			drawBorder(canvas, l, t, w, h, true);
		} else {
			drawBorder(canvas, l, t, w, h, false);
		}

		if (MAGDocumentConfig.isShowComponentBorder(getContext())) {
			_border_paint.setColor(Color.RED);
			_border_paint.setPathEffect(_outter_effect);
			_border_rect.set(l, t, l + w, t + h);
			canvas.drawRect(_border_rect, _border_paint);

			_border_paint.setColor(Color.GREEN);
			_border_paint.setPathEffect(_inner_effect);
			_border_rect.set(l + getBorderWidthLeft() + getPaddingLeft(), t + getBorderWidthTop() + getPaddingTop(), l + w - getBorderWidthRight() - getPaddingRight(), t
					+ h - getBorderWidthBottom() - getPaddingBottom());
			canvas.drawRect(_border_rect, _border_paint);

			View view = getField();
			if (view != null) {
				_border_paint.setColor(Color.YELLOW);
				_border_paint.setPathEffect(_view_effect);
				_border_rect.set(view.getLeft(), view.getTop(), view.getLeft() + view.getMeasuredWidth(), view.getTop() + view.getMeasuredHeight());
				canvas.drawRect(_border_rect, _border_paint);
			}

		}
	}
	
	public void releaseResources() {
		//LOG.error(this, "releaseResources is called!!!", null);
		setField(null);
		_context = null;
		_style = null;
		_parent = null;
	}

}
