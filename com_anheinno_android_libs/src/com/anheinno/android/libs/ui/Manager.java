package com.anheinno.android.libs.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

public abstract class Manager extends ViewGroup {
	
	public Manager(Context context) {
		super(context);
		setWillNotDraw(false);
		setBackgroundColor(Color.TRANSPARENT);
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	protected final void onLayout(boolean changed, int l, int t, int r, int b) {
		layoutChildren(0, 0, getMeasuredWidth(), getMeasuredHeight());
	}
	
	protected abstract void layoutChildren(int left, int top, int width, int height);
	
	protected final void measureChild(View child, int width, boolean fill_parent) {
		int spec = MeasureSpec.AT_MOST;
		if(fill_parent) {
			spec = MeasureSpec.EXACTLY;
		}
		super.measureChild(child, MeasureSpec.makeMeasureSpec(width, spec), 
				MeasureSpec.makeMeasureSpec(65535, MeasureSpec.UNSPECIFIED));
	}
	
	protected final void setChildPosition(View child, int left, int top) {
		child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
	}
	
	@Override
	protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
}
