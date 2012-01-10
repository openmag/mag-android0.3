package com.anheinno.android.libs.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;

import com.anheinno.android.libs.UtilClass;
import com.anheinno.android.libs.util.Direction;
import com.anheinno.android.libs.util.NameValuePair;

public class TextStyleDescriptor {

	private Direction _padding;

	private Direction _text_padding;
	private int _text_spacing;
	private int _text_line_limit;
	private Align _text_align;
	private VAlign _text_valign;
	private int _text_color;
	private int _text_alpha;

	private String _icon_name;
	private Direction _icon_padding;
	private Align _icon_align;
	private VAlign _icon_valign;
	private int _icon_position;

	private float _text_scale;
	private boolean _text_bold;
	private boolean _text_italic;
	private boolean _text_underline;

	private boolean _use_fullwidth;
	private boolean _use_fullheight;

	// for cache only
	protected float _width;
	protected float _height;
	protected float _icon_left;
	protected float _icon_top;
	protected float _icon_width;
	protected float _icon_height;
	protected float _text_left;
	protected float _text_top;
	protected float _text_width;
	protected float _text_height;

	public static final int ICON_POSITION_LEFT = 0;
	public static final int ICON_POSITION_RIGHT = 1;
	public static final int ICON_POSITION_TOP = 2;
	public static final int ICON_POSITION_BOTTOM = 3;
	public static final int ICON_POSITION_DEFAULT = ICON_POSITION_LEFT;

	public static TextStyleDescriptor DEFAULT_TEXT_STYLE;

	static {
		DEFAULT_TEXT_STYLE = new TextStyleDescriptor();
	}

	public TextStyleDescriptor() {
		init();
	}

	public TextStyleDescriptor(String str) {
		init();
		parse(str);
	}

	public boolean equals(TextStyleDescriptor style) {
		if (!_padding.equals(style._padding)) {
			return false;
		}

		if (!_text_padding.equals(style._text_padding)) {
			return false;
		}

		if (_text_spacing != style._text_spacing) {
			return false;
		}
		if (_text_line_limit != style._text_line_limit) {
			return false;
		}
		if (_text_align != style._text_align) {
			return false;
		}
		if (_text_valign != style._text_valign) {
			return false;
		}
		if (_text_color != style._text_color) {
			return false;
		}
		if (_text_alpha != style._text_alpha) {
			return false;
		}

		if (_icon_name == null && style._icon_name != null) {
			return false;
		}

		if (_icon_name != null && style._icon_name == null) {
			return false;
		}

		if (_icon_name != null && style._icon_name != null && !_icon_name.equals(style._icon_name)) {
			return false;
		}

		if (!_icon_padding.equals(style._icon_padding)) {
			return false;
		}
		if (_icon_align != style._icon_align) {
			return false;
		}
		if (_icon_valign != style._icon_valign) {
			return false;
		}
		if (_icon_position != style._icon_position) {
			return false;
		}

		if (_text_scale != style._text_scale) {
			return false;
		}
		if (_text_bold != style._text_bold) {
			return false;
		}
		if (_text_italic != style._text_italic) {
			return false;
		}
		if (_text_underline != style._text_underline) {
			return false;
		}

		return true;
	}

	private void init() {
		_padding = new Direction(2);

		_text_padding = new Direction(0);
		_text_spacing = 1;
		_text_line_limit = -1;
		_text_align = Align.LEFT;
		_text_valign = VAlign.MIDDLE;
		_text_color = GraphicUtilityClass.INVALID_COLOR;
		_text_alpha = 0xFF;

		_icon_name = null;
		_icon_padding = new Direction(0);
		_icon_align = Align.CENTER;
		_icon_valign = VAlign.MIDDLE;
		_icon_position = ICON_POSITION_DEFAULT;

		_text_scale = 1.0f;
		_text_bold = false;
		_text_italic = false;
		_text_underline = false;

		_use_fullwidth = false;
		_use_fullheight = false;
	}

	public void parse(String conf) {
		String[] sep = { " ", "\n", "\r", ";", "&" };
		String[] dat = UtilClass.strSplit(conf, sep, 0);
		NameValuePair nv = null;
		for (int i = 0; i < dat.length; i++) {
			if (nv == null) {
				nv = new NameValuePair(dat[i]);
			} else {
				nv.parse(dat[i]);
			}
			if (nv.isName("padding")) {
				_padding.set(nv.getValueInt());
			} else if (nv.isName("padding-left")) {
				_padding._left = nv.getValueInt();
			} else if (nv.isName("padding-right")) {
				_padding._right = nv.getValueInt();
			} else if (nv.isName("padding-top")) {
				_padding._top = nv.getValueInt();
			} else if (nv.isName("padding-bottom")) {
				_padding._bottom = nv.getValueInt();
			} else if (nv.isName("text-spacing")) {
				_text_spacing = nv.getValueInt();
			} else if (nv.isName("line-limit")) {
				_text_line_limit = nv.getValueInt();
			} else if (nv.isName("text-padding")) {
				_text_padding.set(nv.getValueInt());
			} else if (nv.isName("text-padding-left")) {
				_text_padding._left = nv.getValueInt();
			} else if (nv.isName("text-padding-right")) {
				_text_padding._right = nv.getValueInt();
			} else if (nv.isName("text-padding-top")) {
				_text_padding._top = nv.getValueInt();
			} else if (nv.isName("text-padding-bottom")) {
				_text_padding._bottom = nv.getValueInt();
			} else if (nv.isName("text-align")) {
				Align al = Align.fromString(nv.getValueString());
				if (al != null) {
					_text_align = al;
				}
				if(_text_align != Align.CENTER) {
					_use_fullwidth = true;
				}
			} else if (nv.isName("text-valign")) {
				VAlign va = VAlign.fromString(nv.getValueString());
				if (va != null) {
					_text_valign = va;
				}
				if(_text_valign != VAlign.MIDDLE) {
					_use_fullheight = true;
				}
			} else if (nv.isName("color") || nv.isName("text-color")) {
				_text_color = GraphicUtilityClass.htmlColor(nv.getValueString());
			} else if (nv.isName("alpha") || nv.isName("text-alpha")) {
				_text_alpha = nv.getValueInt();
				if (_text_alpha < 0 || _text_alpha > 0xFF) {
					_text_alpha = 0xFF;
				}
			} else if (nv.isName("icon")) {
				_icon_name = nv.getValueString();
			} else if (nv.isName("icon-padding")) {
				_icon_padding.set(nv.getValueInt());
			} else if (nv.isName("icon-padding-left")) {
				_icon_padding._left = nv.getValueInt();
			} else if (nv.isName("icon-padding-right")) {
				_icon_padding._right = nv.getValueInt();
			} else if (nv.isName("icon-padding-top")) {
				_icon_padding._top = nv.getValueInt();
			} else if (nv.isName("icon-padding-bottom")) {
				_icon_padding._bottom = nv.getValueInt();
			} else if (nv.isName("icon-align")) {
				Align al = Align.fromString(nv.getValueString());
				if (al != null) {
					_icon_align = al;
				}
			} else if (nv.isName("icon-valign")) {
				VAlign va = VAlign.fromString(nv.getValueString());
				if (va != null) {
					_icon_valign = va;
				}
			} else if (nv.isName("icon-position")) {
				if (nv.isValue("top")) {
					_icon_position = ICON_POSITION_TOP;
				} else if (nv.isValue("left")) {
					_icon_position = ICON_POSITION_LEFT;
				} else if (nv.isValue("right")) {
					_icon_position = ICON_POSITION_RIGHT;
				} else if (nv.isValue("bottom")) {
					_icon_position = ICON_POSITION_BOTTOM;
				}
			} else if (nv.isName("font-scale")) {
				_text_scale = nv.getValueFloat();
			} else if (nv.isName("font-weight")) {
				if (nv.isValue("bold")) {
					_text_bold = true;
				} else {
					_text_bold = false;
				}
			} else if (nv.isName("font-style")) {
				if (nv.isValue("italic")) {
					_text_italic = true;
				} else {
					_text_italic = false;
				}
			} else if (nv.isName("text-decoration")) {
				if (nv.isValue("underline")) {
					_text_underline = true;
				} else {
					_text_underline = false;
				}
			} else if (nv.isName("use-full")) {
				if (nv.isValue("true")) {
					_use_fullwidth = true;
					_use_fullheight = true;
				}
			} else if (nv.isName("use-full-width")) {
				if (nv.isValue("true")) {
					_use_fullwidth = true;
				}
			} else if (nv.isName("use-full-height")) {
				if (nv.isValue("true")) {
					_use_fullheight = true;
				}
			}
		}
	}

	public Paint getPaint(Context context) {
		return PaintRepository.getFontPaint(context, _text_bold, _text_italic, _text_underline, _text_scale, _text_color, _text_alpha);
	}

	public float getFontSize(Context context) {
		return _text_scale * GraphicUtilityClass.getMinFontSize(context);
	}

	public Bitmap getIcon(Context context) {
		if (_icon_name != null) {
			//System.out.println("Icon name: " + _icon_name);
			return BitmapRepository.getBitmapByName(context, _icon_name);
		} else {
			return null;
		}
	}

	public int getTextSpacing() {
		return _text_spacing;
	}

	public int getTextLineLimit() {
		return _text_line_limit;
	}

	public int getMaxTextWidth(Context context, int width) {
		int w = width - _padding._left - _padding._right - _text_padding._left - _text_padding._right;
		if (getIcon(context) != null && isHorizontal()) {
			w -= getIcon(context).getWidth() + _icon_padding._left + _icon_padding._right;
		}
		if (w < 0) {
			w = 0;
		}
		return w;
	}

	public int getColor() {
		return _text_color;
	}

	public void layout(Context context, int width, int height, float text_width, float text_height) {
		// System.out.println("layout: width=" + width + " height=" + height +
		// " txt_w=" + text_width + " txt_h=" + text_height);

		ObjectSpace _icon_ospace;
		ObjectSpace _text_ospace;

		_text_ospace = new ObjectSpace(text_width, text_height, _text_padding);
		if (getIcon(context) != null) {
			_icon_ospace = new ObjectSpace(getIcon(context).getWidth(), getIcon(context).getHeight(), _icon_padding);
		} else {
			_icon_ospace = new ObjectSpace(0, 0, _icon_padding);
		}

		_width = getActualWidth(_icon_ospace, _text_ospace);
		_height = getActualHeight(_icon_ospace, _text_ospace);

		if (_width < width && _use_fullwidth) {
			_width = width;
		}
		if (_height < height && _use_fullheight) {
			_height = height;
		}

		ObjectSpace[] cells3 = new ObjectSpace[3];
		ObjectSpace[] cells4 = new ObjectSpace[4];
		if (getIcon(context) == null) {
			if (_text_align == Align.LEFT) {
				cells3[0] = _text_ospace;
				cells3[1] = cells3[2] = null;
			} else if (_text_align == Align.RIGHT) {
				cells3[2] = _text_ospace;
				cells3[0] = cells3[1] = null;
			} else { // if(_text_align == Align.CENTER) {
				cells3[1] = _text_ospace;
				cells3[0] = cells3[2] = null;
			}
			calculateOffset(_width, cells3, true);

			if (_text_valign == VAlign.TOP) {
				cells3[0] = _text_ospace;
				cells3[1] = cells3[2] = null;
			} else if (_text_valign == VAlign.BOTTOM) {
				cells3[2] = _text_ospace;
				cells3[0] = cells3[1] = null;
			} else { // if(_text_valign == VAlign.MIDDLE) {
				cells3[1] = _text_ospace;
				cells3[0] = cells3[2] = null;
			}
			calculateOffset(_height, cells3, false);
		} else if (isHorizontal()) {
			if (_icon_position == ICON_POSITION_LEFT) {
				cells4[0] = _icon_ospace;
				if (_text_align == Align.LEFT) {
					cells4[1] = _text_ospace;
					cells4[2] = cells4[3] = null;
				} else if (_text_align == Align.RIGHT) {
					cells4[3] = _text_ospace;
					cells4[1] = cells4[2] = null;
				} else { // CENTER
					cells4[2] = _text_ospace;
					cells4[1] = cells4[3] = null;
				}
				calculateOffset(_width, cells4, true);
			} else {
				cells4[3] = _icon_ospace;
				if (_text_align == Align.LEFT) {
					cells4[0] = _text_ospace;
					cells4[1] = cells4[2] = null;
				} else if (_text_align == Align.RIGHT) {
					cells4[2] = _text_ospace;
					cells4[0] = cells4[1] = null;
				} else { // CENTER
					cells4[1] = _text_ospace;
					cells4[0] = cells4[2] = null;
				}
				calculateOffset(_width, cells4, true);
			}
			if (_icon_valign == VAlign.TOP) {
				cells3[0] = _icon_ospace;
				cells3[1] = cells3[2] = null;
			} else if (_icon_valign == VAlign.BOTTOM) {
				cells3[2] = _icon_ospace;
				cells3[0] = cells3[1] = null;
			} else {
				cells3[1] = _icon_ospace;
				cells3[0] = cells3[2] = null;
			}
			calculateOffset(_height, cells3, false);
			if (_text_valign == VAlign.TOP) {
				cells3[0] = _text_ospace;
			} else if (_text_valign == VAlign.BOTTOM) {
				cells3[2] = _text_ospace;
			} else {
				cells3[1] = _text_ospace;
			}
			calculateOffset(_height, cells3, false);
		} else {
			if (_icon_position == ICON_POSITION_TOP) {
				cells4[0] = _icon_ospace;
				if (_text_valign == VAlign.TOP) {
					cells4[1] = _text_ospace;
					cells4[2] = cells4[3] = null;
				} else if (_text_valign == VAlign.BOTTOM) {
					cells4[3] = _text_ospace;
					cells4[1] = cells4[2] = null;
				} else {
					cells4[2] = _text_ospace;
					cells4[1] = cells4[3] = null;
				}
				calculateOffset(_height, cells4, false);
			} else {
				cells4[3] = _icon_ospace;
				if (_text_valign == VAlign.TOP) {
					cells4[0] = _text_ospace;
					cells4[1] = cells4[2] = null;
				} else if (_text_valign == VAlign.BOTTOM) {
					cells4[2] = _text_ospace;
					cells4[0] = cells4[1] = null;
				} else {
					cells4[1] = _text_ospace;
					cells4[0] = cells4[2] = null;
				}
				calculateOffset(_height, cells4, false);
			}
			if (_icon_align == Align.LEFT) {
				cells3[0] = _icon_ospace;
				cells3[1] = cells3[2] = null;
			} else if (_icon_align == Align.RIGHT) {
				cells3[2] = _icon_ospace;
				cells3[0] = cells3[1] = null;
			} else {
				cells3[1] = _icon_ospace;
				cells3[0] = cells3[2] = null;
			}
			calculateOffset(_width, cells3, true);
			if (_text_align == Align.LEFT) {
				cells3[0] = _text_ospace;
				cells3[1] = cells3[2] = null;
			} else if (_text_align == Align.RIGHT) {
				cells3[2] = _text_ospace;
				cells3[0] = cells3[1] = null;
			} else {
				cells3[1] = _text_ospace;
				cells3[0] = cells3[2] = null;
			}
			calculateOffset(_width, cells3, true);
		}
		_icon_left = _icon_ospace._left;
		_icon_top = _icon_ospace._top;
		_icon_width = _icon_ospace._width;
		_icon_height = _icon_ospace._height;

		_text_left = _text_ospace._left;
		_text_top = _text_ospace._top;
		_text_width = _text_ospace._width;
		_text_height = _text_ospace._height;
	}

	private boolean isHorizontal() {
		if ((_icon_position == ICON_POSITION_LEFT || _icon_position == ICON_POSITION_RIGHT)) {
			return true;
		} else {
			return false;
		}
	}

	private float getActualHeight(ObjectSpace _icon_ospace, ObjectSpace _text_ospace) {
		float icon_h = _icon_ospace.getHeight();
		float text_h = _text_ospace.getHeight();
		if (isHorizontal()) {
			return Math.max(icon_h, text_h) + _padding._top + _padding._bottom;
		} else {
			return _padding._top + icon_h + text_h + _padding._bottom;
		}
	}

	private float getActualWidth(ObjectSpace _icon_ospace, ObjectSpace _text_ospace) {
		float icon_w = _icon_ospace.getWidth();
		float text_w = _text_ospace.getWidth();
		if (isHorizontal()) {
			return _padding._left + icon_w + text_w + _padding._right;
		} else {
			return _padding._left + Math.max(icon_w, text_w) + _padding._right;
		}
	}

	private class ObjectSpace {
		float _width;
		float _height;
		float _left;
		float _top;
		Direction _padding;

		ObjectSpace(float w, float h, Direction pad) {
			_width = w;
			_height = h;
			_left = 0;
			_top = 0;
			_padding = pad;
		}

		float getWidth() {
			if (_width > 0) {
				return _width + _padding._left + _padding._right;
			} else {
				return 0;
			}
		}

		float getHeight() {
			if (_height > 0) {
				return _height + _padding._top + _padding._bottom;
			} else {
				return 0;
			}
		}
	}

	private void calculateOffset(float total, ObjectSpace[] obj, boolean horizontal) {
		int sepcnt = 0;
		float space = total;
		int offset = 0;
		if (horizontal) {
			space -= _padding._left + _padding._right;
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] == null) {
					sepcnt++;
				} else {
					space -= obj[i].getWidth();
				}
			}
			// System.out.println("total=" + total + " space=" + space +
			// " count=" + sepcnt);
			space = space / sepcnt;
			offset = _padding._left;
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] != null) {
					obj[i]._left = offset + obj[i]._padding._left;
					// System.out.println("left=" + obj[i]._left);
					offset += obj[i].getWidth();
				} else {
					offset += space;
				}
			}
		} else {
			space -= _padding._top + _padding._bottom;
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] == null) {
					sepcnt++;
				} else {
					space -= obj[i].getHeight();
				}
			}
			// System.out.println("total=" + total + " space=" + space +
			// " count=" + sepcnt);
			space = space / sepcnt;
			offset = _padding._top;
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] != null) {
					obj[i]._top = offset + obj[i]._padding._top;
					// System.out.println("top=" + obj[i]._top);
					offset += obj[i].getHeight();
				} else {
					offset += space;
				}
			}
		}
	}

	/*
	 * private void layoutHorizontal(int width, int height, ObjectSpace
	 * _icon_ospace, ObjectSpace _text_ospace) {
	 * //System.out.println("layoutHorizontal width=" + width + " height=" +
	 * height);
	 * 
	 * if(getIcon() == null) { ObjectSpace[] cell_1 = new ObjectSpace[3];
	 * if(_text_align == DrawStyle.LEFT) { cell_1[0] = _text_ospace; }else
	 * if(_text_align == DrawStyle.RIGHT) { cell_1[2] = _text_ospace; }else {
	 * cell_1[1] = _text_ospace; } calculateOffset(width, cell_1, true); }else {
	 * ObjectSpace[] cell_2 = new ObjectSpace[6]; if(_text_align ==
	 * DrawStyle.LEFT) { if(_icon_position == ICON_POSITION_LEFT) { cell_2[3] =
	 * _text_ospace; }else { cell_2[0] = _text_ospace; } }else if(_text_align ==
	 * DrawStyle.RIGHT) { if(_icon_position == ICON_POSITION_LEFT) { cell_2[5] =
	 * _text_ospace; }else { cell_2[2] = _text_ospace; } }else {
	 * if(_icon_position == ICON_POSITION_LEFT) { cell_2[4] = _text_ospace;
	 * }else { cell_2[1] = _text_ospace; } } if(_icon_align == DrawStyle.LEFT) {
	 * if(_icon_position == ICON_POSITION_LEFT) { cell_2[0] = _icon_ospace;
	 * }else { cell_2[3] = _icon_ospace; } }else if(_icon_align ==
	 * DrawStyle.RIGHT) { if(_icon_position == ICON_POSITION_LEFT) { cell_2[2] =
	 * _icon_ospace; }else { cell_2[5] = _icon_ospace; } }else {
	 * if(_icon_position == ICON_POSITION_LEFT) { cell_2[1] = _icon_ospace;
	 * }else { cell_2[4] = _icon_ospace; } } calculateOffset(width, cell_2,
	 * true); }
	 * 
	 * if(getIcon() != null) { ObjectSpace[] cell_1 = new ObjectSpace[3];
	 * if(_icon_valign == DrawStyle.TOP) { cell_1[0] = _icon_ospace; }else
	 * if(_icon_valign == DrawStyle.BOTTOM) { cell_1[2] = _icon_ospace; }else {
	 * cell_1[1] = _icon_ospace; } calculateOffset(height, cell_1, false); }
	 * ObjectSpace[] cell_1 = new ObjectSpace[3]; if(_text_valign ==
	 * DrawStyle.TOP) { cell_1[0] = _text_ospace; }else if(_text_valign ==
	 * DrawStyle.BOTTOM) { cell_1[2] = _text_ospace; }else { cell_1[1] =
	 * _text_ospace; } calculateOffset(height, cell_1, false); }
	 */

	/*
	 * private void layoutVertical(int width, int height, ObjectSpace
	 * _icon_ospace, ObjectSpace _text_ospace) { if(getIcon() == null) {
	 * ObjectSpace[] cell_1 = new ObjectSpace[3]; if(_text_valign ==
	 * DrawStyle.TOP) { cell_1[0] = _text_ospace; }else if(_text_valign ==
	 * DrawStyle.BOTTOM) { cell_1[2] = _text_ospace; }else { cell_1[1] =
	 * _text_ospace; } calculateOffset(height, cell_1, false); }else {
	 * ObjectSpace[] cell_2 = new ObjectSpace[6]; if(_text_valign ==
	 * DrawStyle.TOP) { if(_icon_position == ICON_POSITION_TOP) { cell_2[3] =
	 * _text_ospace; }else { cell_2[0] = _text_ospace; } }else if(_text_valign
	 * == DrawStyle.BOTTOM) { if(_icon_position == ICON_POSITION_TOP) {
	 * cell_2[5] = _text_ospace; }else { cell_2[2] = _text_ospace; } }else {
	 * if(_icon_position == ICON_POSITION_TOP) { cell_2[4] = _text_ospace; }else
	 * { cell_2[1] = _text_ospace; } } if(_icon_valign == DrawStyle.TOP) {
	 * if(_icon_position == ICON_POSITION_TOP) { cell_2[0] = _icon_ospace; }else
	 * { cell_2[3] = _icon_ospace; } }else if(_icon_valign == DrawStyle.BOTTOM)
	 * { if(_icon_position == ICON_POSITION_TOP) { cell_2[2] = _icon_ospace;
	 * }else { cell_2[5] = _icon_ospace; } }else { if(_icon_position ==
	 * ICON_POSITION_TOP) { cell_2[1] = _icon_ospace; }else { cell_2[4] =
	 * _icon_ospace; } } calculateOffset(height, cell_2, false); }
	 * 
	 * if(getIcon() != null) { ObjectSpace[] cell_1 = new ObjectSpace[3];
	 * if(_icon_align == DrawStyle.LEFT) { cell_1[0] = _icon_ospace; }else
	 * if(_icon_align == DrawStyle.RIGHT) { cell_1[2] = _icon_ospace; }else {
	 * cell_1[1] = _icon_ospace; } calculateOffset(width, cell_1, true); }
	 * ObjectSpace[] cell_1 = new ObjectSpace[3]; if(_text_align ==
	 * DrawStyle.LEFT) { cell_1[0] = _text_ospace; }else if(_text_align ==
	 * DrawStyle.RIGHT) { cell_1[2] = _text_ospace; }else { cell_1[1] =
	 * _text_ospace; } calculateOffset(width, cell_1, true); }
	 */

	public Align getTextAlign() {
		return _text_align;
	}
}
