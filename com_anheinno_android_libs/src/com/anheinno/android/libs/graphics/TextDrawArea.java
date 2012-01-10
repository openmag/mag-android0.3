package com.anheinno.android.libs.graphics;


import com.anheinno.android.libs.ui.FullScreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;



public class TextDrawArea {
	
	private Paragraph _block;
	private TextStyleDescriptor _style = TextStyleDescriptor.DEFAULT_TEXT_STYLE;
	private int _prefer_width  = 0;
	private int _prefer_height = 0;
	private boolean _change = false;
	
	// cache style dimension, as _style is shared
	private float _width;
	private float _height;
	private float _icon_left;
	private float _icon_top;
	private float _text_left;
	private float _text_top;
	
	private Context _context;

	public TextDrawArea(Context context, String text) {
		_context = context;
		setText(text);
	}
	
	public TextDrawArea(Context context, String text, TextStyleDescriptor style) {
		_context = context;
		setText(text);
		setStyle(style);
	}

	public boolean setText(String text) {
		if(text == null) {
			text = "";
		}
		if(_block == null) {
			_block = new Paragraph(_context, text, FullScreen.getFullScreenWidth(_context));
			_change = true;
			return true;
		}else if(!_block.getText().equals(text)) {
			_block.setText(text);
			_change = true;
			return true;
		}else {
			return false;
		}
	}
	
	public String getText() {
		return _block.getText();
	}
	
	public boolean setStyle(String style_str) {
		TextStyleDescriptor style = new TextStyleDescriptor(style_str);
		return setStyle(style);
	}
	
	public boolean setStyle(TextStyleDescriptor style) {
		if(_style == null || (style != null && !_style.equals(style))) {
			_style = style;
			_change = true;
			return true;
		}else {
			return false;
		}
	}
	
	public boolean setWidth(int width) {
		if(width != _prefer_width) {
			_prefer_width = width;
			_change = true;
			return true;
		}else {
			return false;
		}
	}
	
	public boolean setHeight(int height) {
		if(_prefer_height != height) {
			_prefer_height = height;
			_change = true;
			return true;
		}else {
			return false;
		}
	}

	private void layout() {
		if(!_change) {
			return;
		}
		
		_change = false;

		//System.out.println("layout: width=" + _prefer_width + _block.getText());
		_block.setPaint(_style.getPaint(_context));
		_block.setWidthBound(_style.getMaxTextWidth(_context, _prefer_width));
		_block.setSpacing(_style.getTextSpacing());
		_block.setLineCount(_style.getTextLineLimit());
		
		_style.layout(_context, _prefer_width, _prefer_height, _block.getWidth(), _block.getHeight());
		_width = _style._width;
		_height = _style._height;
		_text_left = _style._text_left;
		_text_top  = _style._text_top;
		_icon_left = _style._icon_left;
		_icon_top  = _style._icon_top;
	}

	public float getTextTop() {
		layout();
		return _text_top;
	}

	public float getTextLeft() {
		layout();
		return _text_left;
	}
	
	public float getWidth() {
		layout();
		return _width;
	}
 
	public float getHeight() {
		layout();
		return _height;
	}

	public float getPreferredWidth() {
		float width = _style.getPaint(_context).measureText(_block.getText());
		if (width > FullScreen.getFullScreenWidth(_context)) {
			width = FullScreen.getFullScreenWidth(_context)*1.0f;
		}
		return width;
	}

	public void draw(Canvas canvas, float left, float top) {
		layout();

		//System.out.println("Text: " + _block.getText());
		Bitmap icon = _style.getIcon(_context);
		if (icon != null) {
			canvas.drawBitmap(icon, left + _icon_left, top + _icon_top, null);
		}

		_block.draw(canvas, left + _text_left, top + _text_top, _style.getTextAlign());	
	}
	
	public void releaseResources() {
		_block = null;
		_style = null;
		_context = null;
	}
}
