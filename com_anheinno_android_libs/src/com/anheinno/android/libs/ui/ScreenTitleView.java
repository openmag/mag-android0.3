package com.anheinno.android.libs.ui;

import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.GraphicUtilityClass;
import com.anheinno.android.libs.graphics.TextDrawArea;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public class ScreenTitleView extends Manager {
	
	private TextDrawArea _title;
	private View _field;
	private int _prefer_height;
	private BackgroundDescriptor _bg_desc;
	
	public static final BackgroundDescriptor DEFAULT_SCREEN_TITLE_BACKGROUND;
	public static final TextStyleDescriptor DEFAULT_SCREEN_TITLE_TEXT_STYLE;
	
	static {
		DEFAULT_SCREEN_TITLE_BACKGROUND = new BackgroundDescriptor("start-color=#BBBBBB end-color=#666666 gradient-dir=horizontal");
		DEFAULT_SCREEN_TITLE_TEXT_STYLE = new TextStyleDescriptor("color=white font-scale=1.2 font-weight=bold padding=5 use-full=true");
	}

	public ScreenTitleView(Context context) {
		super(context);
		init();
		setFocusable(false);
	}

	private void init() {
		_title = new TextDrawArea(getContext(), "", DEFAULT_SCREEN_TITLE_TEXT_STYLE);
		_field = null;
		_prefer_height = 0;
		_bg_desc = DEFAULT_SCREEN_TITLE_BACKGROUND;
	}
	
	public void setPreferredHeight(int height) {
		if(_prefer_height != height) {
			_prefer_height = height;
			_title.setHeight(_prefer_height);
			//System.out.println("ScreenTitleView::setHeight=" + height);
		}
	}
	
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		_title.setWidth(width);
		
		if(_field != null) {
			int field_width = (int)(width - _title.getWidth());
			_field.measure(MeasureSpec.makeMeasureSpec(field_width, MeasureSpec.EXACTLY), 
					MeasureSpec.makeMeasureSpec(65535, MeasureSpec.UNSPECIFIED));
		}
		
		int height = (int) _title.getHeight();
		
		if(_field != null && height < _field.getMeasuredHeight()) {
			height = _field.getMeasuredHeight();
		}
		
		if(height < _prefer_height) {
			height = _prefer_height;
		}
		
		//System.out.println("ScreenTitleView width=" + width + " height=" + height);
		
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		if(_bg_desc != null) {
			//System.out.println("ScreenTitleView onDraw width=" + getMeasuredWidth() + " height=" + getMeasuredHeight());
			_bg_desc.draw(getContext(), canvas, 0, 0, getMeasuredWidth(), getMeasuredHeight());
		}
		_title.draw(canvas, 0, (getMeasuredHeight() - _title.getHeight())/2);
		
		super.onDraw(canvas);
	}

	public void setTextStyle(TextStyleDescriptor style) {
		if(_title.setStyle(style)) {
			//System.out.println("style is set for screentitleview");
			requestLayout();
		}
	}
	
	public void setBackground(BackgroundDescriptor bg) {
		boolean change = false;
		if(_bg_desc != null && bg == null) {
			change = true;
		}else if(_bg_desc == null && bg != null) {
			change = true;
		}else if(_bg_desc != null && bg != null && !_bg_desc.equals(bg)) {
			change = true;
		}
		if(change) {
			_bg_desc = bg;
			postInvalidate();
		}
	}
	
	public synchronized void setField(View field) {
		if(_field != field) {
			if(_field != null) {
				super.removeView(_field);
				_field = null;
			}
			if(field != null) {
				super.addView(field);
				_field = field;
			}
		}
	}
	
	public View getField() {
		return _field;
	}
	
	public void setText(String text) {
		if(_title.setText(text)) {
			postInvalidate();
		}
	}
	
	public String getText() {
		return _title.getText();
	}

	protected synchronized void layoutChildren(int l, int t, int width, int height) {
		if(_field != null) {
			int left = (int)_title.getWidth();
			int top =  (int)((getMeasuredHeight() - _field.getMeasuredHeight())/2);
			//System.out.println("ScreenTitleView layoutChildren: title width=" + left + " title height=" + top);
			setChildPosition(_field, left, top);
		}
	}
}
