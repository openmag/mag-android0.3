package com.anheinno.android.libs.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.GraphicUtilityClass;
import com.anheinno.android.libs.graphics.TextDrawArea;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.log.LOG;

public class CustomButtonField extends View implements DelayedLayoutChangeUI {

	//private boolean _is_virtual_focus;
	//private boolean _is_actual_focus;
	
	private boolean _is_focus;
	private boolean _is_checked;
	
	private TextDrawArea _label;
	private TextDrawArea _focus_label;
	private TextDrawArea _checked_label;
	private BackgroundDescriptor _bg_desc;
	private BackgroundDescriptor _focus_bg_desc;
	private BackgroundDescriptor _checked_bg_desc;
	
	private int _prefer_width;
	private int _prefer_height;

	public CustomButtonField(Context context, String label, OnClickListener callback) {
		super(context);

		setFocusable(true);
		setClickable(true);
		
		// setOnFocusChangeListener(this);
		// setOnTouchListener(this);
		// /this.setFocusableInTouchMode(true);

		if (callback != null) {
			this.setOnClickListener(callback);
		}

		_label = new TextDrawArea(context, label);
		
		//_is_virtual_focus = false;
		//_is_actual_focus = false;
		_is_focus = false;
		_is_checked = false;
		
		_focus_label = null;
		_checked_label = null;
		_bg_desc = null;
		_focus_bg_desc = null;
		_checked_bg_desc = null;
		_prefer_width = 0;
		_prefer_height = 0;
	}

	public void setLabel(String label) {
		_label.setText(label);
		if (_focus_label != null) {
			_focus_label.setText(label);
		}
		if (_checked_label != null) {
			_checked_label.setText(label);
		}
	}

	public void setTextStyle(TextStyleDescriptor style) {
		_label.setStyle(style);
	}

	public void setFocusTextStyle(TextStyleDescriptor style) {
		if (_focus_label == null) {
			_focus_label = new TextDrawArea(getContext(), _label.getText());
		}
		_focus_label.setStyle(style);
	}
	
	public void setCheckedTextStyle(TextStyleDescriptor style) {
		if (_checked_label == null) {
			_checked_label = new TextDrawArea(getContext(), _label.getText());
		}
		_checked_label.setStyle(style);
	}

	public void setBackground(BackgroundDescriptor desc) {
		_bg_desc = desc;
		//_focus_bg_desc = _bg_desc;
	}

	public void setFocusBackground(BackgroundDescriptor desc) {
		_focus_bg_desc = desc;
	}
	
	public void setCheckedBackground(BackgroundDescriptor desc) {
		_checked_bg_desc = desc;
	}

	public float getTextLeft() {
		return _label.getTextLeft();
	}

	public float getTextTop() {
		return _label.getTextTop();
	}

	/*
	 * public boolean onKeyUp (int keyCode, KeyEvent event) { return
	 * super.onKeyUp(keyCode, event); }
	 * 
	 * protected boolean keyChar(char character, int status, int time) { if
	 * (character == Keypad.KEY_ENTER || character == Keypad.KEY_SPACE) {
	 * fieldChangeNotify(0); return true; } return super.keyChar(character,
	 * status, time); }
	 */

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		//LOG.error(this, "onFocusChanged is called", null);
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		onFocusChanged(gainFocus, false);
	}

	private void onFocusChanged(boolean gainFocus, boolean cause_by_touch) {
		if (gainFocus) {
			//_is_virtual_focus = true;
			//_is_actual_focus  = true;
			_is_focus = true;
		} else {
			/*if(!cause_by_touch) {
				_is_virtual_focus = false;
			}else {
				FullScreen.getFullScreen(this).getUiApplication().invokeLater(new Runnable() {
					public void run() {
						_is_virtual_focus = false;
						postInvalidate();
					}
				}, 500);
			}
			_is_actual_focus  = false;*/
			_is_focus = false;
		}
		postInvalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//LOG.error(this, "onFocusChanged is called", null);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			onFocusChanged(true, true);
			invalidate();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			onFocusChanged(false, true);
			invalidate();
		}
		return super.onTouchEvent(event);
	}
	
	private boolean isButtonFocused() {
		return _is_focus; //(_is_virtual_focus || _is_actual_focus);
	}
	
	private boolean isButtonChecked() {
		return _is_checked;
	}
	
	public void setChecked(boolean checked) {
		_is_checked = checked;
		postInvalidate();
	}

	private float getActualWidth() {
		float txt_w = 0;
		if (isButtonFocused()) {
			if (_focus_label != null) {
				txt_w = _focus_label.getWidth();
			} else {
				txt_w = _label.getWidth();
			}
		} else if(isButtonChecked()) {
			if (_checked_label != null) {
				txt_w = _checked_label.getWidth();
			}else if (_focus_label != null) {
				txt_w = _focus_label.getWidth();
			} else {
				txt_w = _focus_label.getWidth();
			}
		} else {
			txt_w = _label.getWidth();
		}
		// System.out.println("text width: " + txt_w);
		return Math.max(GraphicUtilityClass.getMinTouchWidth(getContext()), Math.max(_prefer_width, txt_w));
	}

	private float getActualHeight() {
		float txt_h = 0;
		if (isButtonFocused()) {
			if (_focus_label != null) {
				txt_h = _focus_label.getHeight();
			} else {
				txt_h = _label.getHeight();
			}
		} else if (isButtonChecked()) {
			if (_checked_label != null) {
				txt_h = _checked_label.getHeight();
			} else if (_focus_label != null) {
				txt_h = _focus_label.getHeight();
			} else {
				txt_h = _label.getHeight();
			}
		} else {
			txt_h = _label.getHeight();
		}
		return Math.max(GraphicUtilityClass.getMinTouchHeight(getContext()), Math.max(_prefer_height, txt_h));
	}

	public void setPreferredWidth(int width) {
		if (width != _prefer_width) {
			_prefer_width = width;
			setLabelWidth(_prefer_width);
		}
	}

	public void setPreferredHeight(int height) {
		if (height != _prefer_height) {
			_prefer_height = height;
			setLabelHeight(_prefer_height);
		}
	}

	private void setLabelWidth(int width) {
		_label.setWidth(width);
		if (_focus_label != null) {
			_focus_label.setWidth(width);
		}
		if (_checked_label != null) {
			_checked_label.setWidth(width);
		}
	}

	private void setLabelHeight(int height) {
		_label.setHeight(height);
		if (_focus_label != null) {
			_focus_label.setHeight(height);
		}
		if (_checked_label != null) {
			_checked_label.setHeight(height);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// System.out.println("CustomButtonField: layout width=" + width +
		// " height=" + height + " prefer_width=" + _prefer_width +
		// " prefer_height=" + _prefer_height);
		if (_prefer_height == 0) {
			setLabelHeight(0);
		}

		if (_prefer_width == 0) {
			setLabelWidth(MeasureSpec.getSize(widthMeasureSpec));
		}

		this.setMeasuredDimension((int) getActualWidth(), (int) getActualHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		BackgroundDescriptor bg_desc = null;
		TextDrawArea text_area = null;
		
		if (isButtonFocused()) {
			if (_focus_bg_desc != null) {
				bg_desc = _focus_bg_desc;
			} else {
				bg_desc = _bg_desc;
			}
			if (_focus_label != null) {
				text_area = _focus_label;
			} else {
				text_area = _label;
			}
		} else if (isButtonChecked()) {
			if (_checked_bg_desc != null) {
				bg_desc = _checked_bg_desc;
			} else if (_focus_bg_desc != null) {
				bg_desc = _focus_bg_desc;
			} else {
				bg_desc = _bg_desc;
			}
			if (_checked_label != null) {
				text_area = _checked_label;
			} else if (_focus_label != null) {
				text_area = _focus_label;
			} else {
				text_area = _label;
			}
		} else {
			if (_bg_desc != null) {
				bg_desc = _bg_desc;
			}
			text_area = _label;
		}
		
		if (bg_desc != null) {
			bg_desc.draw(getContext(), canvas, 0, 0, getMeasuredWidth(), getMeasuredHeight());
		}
		text_area.draw(canvas, (getMeasuredWidth() - text_area.getWidth())/2, (getMeasuredHeight() - text_area.getHeight())/2);
	}

//	public boolean onTouch(View v, MotionEvent event) {
//		if (v == this) {
//			if (event.getAction() == MotionEvent.ACTION_DOWN) {
//				onFocusChanged(true);
//			} else if (event.getAction() == MotionEvent.ACTION_UP) {
//				onFocusChanged(false);
//			}
//		}
//		return false;
//	}

	/*public void onFocusChange(View v, boolean hasFocus) {
		if (v == this) {
			onFocusChanged(hasFocus);
		}
	}*/

	public void commitLayoutChange() {
		FullScreen screen = FullScreen.getFullScreen(this);
		if(screen != null) {
			screen.getUiApplication().invokeLater(new Runnable() {
				public void run() {
					requestLayout();
				}
			});
		}
	}

}
