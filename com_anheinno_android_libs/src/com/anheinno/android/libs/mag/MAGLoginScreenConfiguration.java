package com.anheinno.android.libs.mag;

import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.Align;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.graphics.VAlign;

import android.graphics.Color;


public class MAGLoginScreenConfiguration {

	public int _pop_width;
	public int _pop_height;
	public int _pop_padding_left;
	public int _pop_padding_top;
	public int _pop_padding_right;
	public int _pop_padding_bottom;
	public Align _pop_align;
	public VAlign _pop_valign;
	public BackgroundDescriptor _pop_bg;

	public int _pane_padding_left;
	public int _pane_padding_top;
	public int _pane_padding_right;
	public int _pane_padding_bottom;
	public Align _pane_align;
	public VAlign _pane_valign;

	public String _title;
	public TextStyleDescriptor _title_style;
	public int _text_color;
	
	public BackgroundDescriptor _bg;
	
	private static final BackgroundDescriptor DEFAULT_BG;
	private static final BackgroundDescriptor DEFAULT_POP_BG;
	private static final TextStyleDescriptor  DEFAULT_TITLE;
	
	static {
		DEFAULT_BG = new BackgroundDescriptor("color=blue");
		DEFAULT_POP_BG = new BackgroundDescriptor("image=login_bg.png duplicate=bitmap-border border-top=19 border-left=15 border-right=15 border-bottom=24");
		DEFAULT_TITLE = new TextStyleDescriptor("font-scale=1.2 font-weight=bold color=white");
	}
	
	public MAGLoginScreenConfiguration(String title_txt) {
		_title = title_txt;
		_title_style = DEFAULT_TITLE;

		_pop_width = -1;
		_pop_height = -1;
		_pop_padding_left = 0;
		_pop_padding_top = 0;
		_pop_padding_right = 0;
		_pop_padding_bottom = 0;
		_pop_align = Align.CENTER;
		_pop_valign = VAlign.MIDDLE;
		_pop_bg = DEFAULT_POP_BG;

		_pane_padding_left   = 30;
		_pane_padding_top    = 30;
		_pane_padding_right  = 30;
		_pane_padding_bottom = 30;
		_pane_align = Align.CENTER;
		_pane_valign = VAlign.MIDDLE;

		_text_color = Color.WHITE;
		
		_bg = DEFAULT_BG;
	}
	
	public void setPopPadding(int padding) {
		_pop_padding_left = padding;
		_pop_padding_top = padding;
		_pop_padding_right = padding;
		_pop_padding_bottom = padding;
	}
	
	public void setPanePadding(int padding) {
		_pane_padding_left = padding;
		_pane_padding_top = padding;
		_pane_padding_right = padding;
		_pane_padding_bottom = padding;
	}
	
	public int getPaneLeft(int screen_width, int pane_width) {
		int offset = getPopLeft(screen_width, pane_width);
		int pop_width = getPopWidth(pane_width);
		switch(_pane_align) {
		case LEFT:
			return offset + _pane_padding_left;
		case RIGHT:
			return offset + pop_width - _pane_padding_right - pane_width;
		case CENTER:
		default:
			return offset + (pop_width - _pane_padding_right - _pane_padding_left - pane_width)/2 + _pane_padding_left;
		}
	}
	
	public int getPopLeft(int screen_width, int pane_width) {
		int width = getPopWidth(pane_width);
		switch(_pop_align) {
		case LEFT:
			return _pop_padding_left;
		case RIGHT:
			return screen_width - _pop_padding_right - width;
		case CENTER:
		default:
			return (screen_width - _pop_padding_left - _pop_padding_right - width)/2 + _pop_padding_left;
		}
	}
	
	public int getPopWidth(int pane_width) {
		return Math.max(_pop_width, pane_width + _pane_padding_left + _pane_padding_right);
	}
	
	public int getPaneTop(int screen_height, int pane_height) {
		int offset = getPopTop(screen_height, pane_height);
		int pop_height = getPopHeight(pane_height);
		switch(_pane_valign) {
		case TOP:
			return offset + _pane_padding_top;
		case BOTTOM:
			return offset + pop_height - _pane_padding_bottom - pane_height;
		case MIDDLE:
		default:
			return offset + (pop_height - _pane_padding_top - _pane_padding_bottom - pane_height)/2 + _pane_padding_top;
		}
	}
	
	public int getPopTop(int screen_height, int pane_height) {
		int height = getPopHeight(pane_height);
		switch(_pop_valign) {
		case TOP:
			return _pop_padding_top;
		case BOTTOM:
			return screen_height - _pop_padding_bottom - height;
		case MIDDLE:
		default:
			return (screen_height - _pop_padding_top - _pop_padding_bottom - height)/2 + _pop_padding_top;
		}
	}
	
	public int getPopHeight(int pane_height) {
		return Math.max(_pop_height, pane_height + _pane_padding_top + _pane_padding_bottom);
	}
	
	private static final MAGLoginScreenConfiguration _default;
	static {
		_default = new MAGLoginScreenConfiguration("MAG Client Login");
	}
	public static MAGLoginScreenConfiguration getDefault() {
		return _default; 
	}

}
