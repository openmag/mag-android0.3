package com.anheinno.android.libs.mag;

import android.content.Context;

import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.ui.CustomButtonField;

public class MAGCustomButtonField extends CustomButtonField {

	private MAGComponentInterface _component;

	private TextStyleDescriptor _style_normal;
	private TextStyleDescriptor _style_visited;
	private TextStyleDescriptor _style_checked;
	private TextStyleDescriptor _style_focus_normal;
	private TextStyleDescriptor _style_focus_visited;
	
	private int _visited;
	
	private static final int STATUS_UNKNOWN = -1;
	private static final int STATUS_VISITED = 0;
	private static final int STATUS_NOT_VISITED = 1;
	
	
	public MAGCustomButtonField(Context context, MAGComponentInterface comp, OnClickListener callback) {
		super(context, "", callback);
		
		_component = comp;
		_visited = STATUS_UNKNOWN;
	}
	
	public MAGComponentInterface getMAGComponent() {
		return _component;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int w = _component.style().getIWidth(_component.getInnerWidth());
		if (w > 0) {
			setPreferredWidth(w);
		}
		int h = _component.style().getIHeight(_component.getInnerHeight());
		if (h > 0) {
			setPreferredHeight(h);
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	protected void setLinkTextStyle(TextStyleDescriptor style, TextStyleDescriptor visited_style) {
		_style_normal = style;
		_style_visited = visited_style;
		_style_focus_normal = style;
		_style_focus_visited = visited_style;
		_style_checked = style;
	}
	
	protected void setLinkTextStyle(TextStyleDescriptor style) {
		_style_normal = style;
		_style_visited = style;
		_style_focus_normal = style;
		_style_focus_visited = style;
		_style_checked = style;
	}

	protected void setLinkFocusTextStyle(TextStyleDescriptor style, TextStyleDescriptor visited_style) {
		_style_focus_normal = style;
		_style_focus_visited = visited_style;
		_style_checked = style;
	}

	protected void setLinkFocusTextStyle(TextStyleDescriptor style) {
		_style_focus_normal = style;
		_style_focus_visited = style;
		_style_checked = style;
	}
	
	protected void setLinkCheckedTextStyle(TextStyleDescriptor style) {
		_style_checked = style;
	}

	public void updateStyle() {
		setLabel(_component.title());		
		
		TextStyleDescriptor style, visited_style;
		style = _component.style().getTextStyle();
		visited_style = _component.style().getTextStyle("visited");
		if(style != null) {
			if(visited_style != null) {
				setLinkTextStyle(style, visited_style);
			}else {
				setLinkTextStyle(style);
			}
		}
		
		style = _component.style().getTextStyle("focus");
		visited_style = _component.style().getTextStyle("focus-visited");
		if(style != null) {
			if(visited_style != null) {
				setLinkFocusTextStyle(style, visited_style);
			}else {
				setLinkFocusTextStyle(style);
			}
		}
		
		style = _component.style().getTextStyle("checked");
		if(style != null) {
			setLinkCheckedTextStyle(style);
		}
		
		String bg;
		bg = _component.style().get("link-bg-image");
		if(bg != null) {
			setBackground(new BackgroundDescriptor(bg));
		}
		bg = _component.style().get("focus-bg-image");
		if(bg != null) {
			setFocusBackground(new BackgroundDescriptor(bg));
		}
		
		BackgroundDescriptor bg_desc = _component.style().getBodyBackground();
		if(bg_desc != null) {
			setBackground(bg_desc);
		}
		
		bg_desc = _component.style().getFocusBodyBackground();
		if(bg_desc != null) {
			setFocusBackground(bg_desc);
		}
		
		bg_desc = _component.style().getCheckedBodyBackground();
		if(bg_desc != null) {
			setCheckedBackground(bg_desc);
		}
		
		setVisited();
	}
	
	protected void setVisited() {
		if(_visited == STATUS_UNKNOWN || (_visited==STATUS_VISITED)!= isVisited()) {
			_visited = isVisited()? STATUS_VISITED:STATUS_NOT_VISITED;
			if (isVisited()) {
				setTextStyle(_style_visited);
				setFocusTextStyle(_style_focus_visited);
			}else {
				setTextStyle(_style_normal);
				setFocusTextStyle(_style_focus_normal);
			}
			setCheckedTextStyle(_style_checked);
			commitLayoutChange();
		}
	}
	
	protected boolean isVisited() {
		return false;
	}
	
}
