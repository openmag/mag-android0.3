package com.anheinno.android.libs.ui;

import com.anheinno.android.libs.graphics.GraphicUtilityClass;
import com.anheinno.android.libs.graphics.PaintRepository;

import android.content.Context;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
//import android.view.View.MeasureSpec;

public abstract class ListField<TYPE> extends View {
	private int _width;
	private int _row_height;
	private int _sel_index;
	private int _size;
	private boolean _searchable;
	private ListFieldCallback<TYPE> _callback;

	private static final int PADDING = 3;
	
	public interface ListFieldCallback<TYPE> {
		void drawListRow(ListField<TYPE> listField, Canvas canvas, int index, int top, int width, boolean focus);
		
		//int getPreferredWidth(ListField<TYPE> listField);
			
		TYPE get(ListField<TYPE> listField, int index);
		
		int indexOfList(ListField<TYPE> listField, String prefix, int start);
	};
	
	public ListField(Context context) {
		super(context);
		
		setClickable(true);
		setFocusable(true);
		
		_width = 0;
		_row_height = (int)(GraphicUtilityClass.getMinFontSize(context) + PADDING*2);
		_sel_index = -1;
		_size = 0;
		_searchable = false;
		_callback = null;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(_callback != null && _size > 0) {
			_width = MeasureSpec.getSize(widthMeasureSpec);
			System.out.println("ListField width=" + _width);
			int height = _size*_row_height;
			this.setMeasuredDimension(_width, height);
		}else {
			this.setMeasuredDimension(0, 0);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(_callback != null && _size > 0) {
			int top = 0;
			boolean focus = false;
			for(int i = 0; i < _size; i ++) {
				if(i == _sel_index) {
					focus = true;
				}else {
					focus = false;
				}
				_callback.drawListRow(this, canvas, i, top, _width, focus);
				top += _row_height;
			}
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		onMeasure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	}
	
	@Override
	public final boolean onTouchEvent (MotionEvent event) {
		System.out.println("onTouchEvent " + event);
		super.requestFocus();
		if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
			int cur_index = (int)(event.getY()/_row_height);
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				moveFocus(cur_index);
				return true;
			}
			if(event.getAction() == MotionEvent.ACTION_UP) {
				//System.out.println("ListField click up sel_index=" + _sel_index + " current=" + cur_index);
				if(_sel_index == cur_index) {
					onSelect(_sel_index, _callback.get(this, _sel_index));
					return true;
				}
			}
		}
		return super.onTouchEvent(event);
	}
	
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public boolean onKeyUp (int keyCode, KeyEvent event) {
		System.out.println("onKeyUp keyCode=" + keyCode + " " + event);
		if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_ENTER) {
			onSelect(_sel_index, _callback.get(this, _sel_index));
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			if(moveFocusUp()) {
				return true;
			}
		}else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			if(moveFocusDown()) {
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
	
	public boolean onTrackballEvent (MotionEvent event) {
		if(event.getX() > 0) {
			if(moveFocusDown()) {
				return true;
			}
		}else if(event.getX() < 0) {
			if(moveFocusUp()) {
				return true;
			}
		}
		return super.onTrackballEvent(event);
	}
	
	private boolean moveFocusDown() {
		if(_sel_index < _size-1) {
			return moveFocus(_sel_index+1);
		}else {
			return false;
		}
	}
	
	private boolean moveFocusUp() {
		if(_sel_index > 0) {
			return moveFocus(_sel_index-1);
		}else {
			return false;
		}
	}
	
	private boolean moveFocus(int sel_index) {
		if(_sel_index != sel_index) {
			System.out.println("moveFocus to " + sel_index);
			int prev_index = _sel_index;
			_sel_index = sel_index;
			invalidate(prev_index);
			invalidate(_sel_index);
			return true;
		}else {
			return false;
		}
	}
	
	protected abstract void onSelect(int index, TYPE data);
	
	public ListFieldCallback<TYPE> getCallback() {
		return _callback;
	}
	
	public void setCallback(ListFieldCallback<TYPE> callback) {
		_callback = callback;
	}
	
	public int getRowHeight() {
		return _row_height;
	}
	
	public boolean setRowHeight(int row_height) {
		if(_row_height != row_height) {
			_row_height = row_height;
			requestLayout();
			return true;
		}else {
			return false;
		}
	}
	
	public int getSelectedIndex() {
		return _sel_index;
	}
	
	public boolean setSelectedIndex(int sel_index) {
		if(_sel_index != sel_index) {
			_sel_index = sel_index;
			return true;
		}else {
			return false;
		}
	}
	
	public int getSize() {
		return _size;
	}
	
	public boolean setSize(int size) {
		return setSize(size, 0);
	}
	
	public boolean setSize(int size, int sel_index) {
		_sel_index = sel_index;
		if(_size != size) {
			_size = size;
			requestLayout();
			return true;
		}else {
			return false;
		}
	}
	
	public void setSearchable(boolean searchable) {
		_searchable = searchable;
	}
	
	public void invalidate(int index) {
		postInvalidate();
	}
}
