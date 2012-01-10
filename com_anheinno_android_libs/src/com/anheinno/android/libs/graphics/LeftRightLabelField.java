/*
 * LeftRightLabelField.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.view.View;

/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class LeftRightLabelField extends View {
	private String _left;
	private String _right;
	private int _padding = 2;
	private float _height;
	private Paint _paint;
	private int _color;

	/**
	 * @param context
	 */
	public LeftRightLabelField(Context context, String left, String right) {
		super(context);
		_left = left;
		_right = right;
		_paint = new Paint();
		_color = 0xff8a8a8a;
	}

	public void setText(String l, String r) {
		_left = l;
		_right = r;
		invalidate();
	}

	public void setFont(float size) {
		if (size > 1.0f) {
			_height = size;
		} else {
			_height = GraphicUtilityClass.getMinFontSize(getContext()) * size;
		}
	}

	public void setColor(int color) {
		if (color != GraphicUtilityClass.INVALID_COLOR) {
			_color = color;
			invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		_paint.setTextSize(_height);
		_paint.setColor(_color);
		_paint.setTextAlign(Paint.Align.LEFT);
		canvas.drawText(_left, _padding, _padding + GraphicUtilityClass.getFontHeight(_paint) - _paint.getFontMetrics().bottom, _paint);
		_paint.setTextAlign(Paint.Align.RIGHT);
		canvas.drawText(_right, getMeasuredWidth() - _padding, _padding + GraphicUtilityClass.getFontHeight(_paint) - _paint.getFontMetrics().bottom, _paint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (int) getPreferredHeight());
	}

	public float getPreferredHeight() {
		return _padding * 2 + _height;
	}
}
