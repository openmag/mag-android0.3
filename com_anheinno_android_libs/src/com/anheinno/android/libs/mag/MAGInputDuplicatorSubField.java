package com.anheinno.android.libs.mag;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.ui.CustomButtonField;
import com.anheinno.android.libs.ui.Manager;


public class MAGInputDuplicatorSubField extends Manager {
	protected static final int SPACE = 3;
	
	private MAGInputDuplicatorField _parent;
	private MAGComponent _component;
	
	private CustomButtonField[] _buttons;
	
	private static final int UP_INDEX     = 0;
	private static final int INSERT_INDEX = 1;
	private static final int DELETE_INDEX = 2;
	private static final int DOWN_INDEX   = 3;
		
	public MAGInputDuplicatorSubField(Context context, MAGInputDuplicatorField parent, MAGComponent comp) {
		super(context);
		
		_parent = parent;
		_component = comp;
		
		if(_component.getField() != null) {
			addView(_component.getField());
		}
		
		_buttons = new CustomButtonField[4];
		
		boolean[] enables = new boolean[4];
		
		if(_parent.isInsertable()) {
			enables[INSERT_INDEX] = true;
		}else {
			enables[INSERT_INDEX] = false;
		}
		if(_parent.isDeleteable()) {
			enables[DELETE_INDEX] = true;
		}else {
			enables[DELETE_INDEX] = false;
		}
		if(_parent.isSortable()) {
			enables[UP_INDEX] = true;
			enables[DOWN_INDEX] = true;
		}else {
			enables[UP_INDEX] = false;
			enables[DOWN_INDEX] = false;
		}
		
		OnClickListener[] change_listener = new OnClickListener[4];
		change_listener[0] = new OnClickListener() {
			public void onClick(View v) {
				up();
			}
		};
		change_listener[1] = new OnClickListener() {
			public void onClick(View v) {
				insert();
			}
		};
		change_listener[2] = new OnClickListener() {
			public void onClick(View v) {
				delete();
			}
		};
		change_listener[3] = new OnClickListener() {
			public void onClick(View v) {
				down();
			}
		};
		
		TextStyleDescriptor[] button_style = new TextStyleDescriptor[] {
				_parent.getUpButtonStyle(),
				_parent.getInsertButtonStyle(),
				_parent.getDeleteButtonStyle(),
				_parent.getDownButtonStyle()
		};
		TextStyleDescriptor[] button_focus_style = new TextStyleDescriptor[] {
				_parent.getUpButtonFocusStyle(),
				_parent.getInsertButtonFocusStyle(),
				_parent.getDeleteButtonFocusStyle(),
				_parent.getDownButtonFocusStyle()
		};
		
		for(int i = 0; i < _buttons.length; i ++) {
			if(enables[i]) {
				_buttons[i] = new CustomButtonField(context, "", change_listener[i]);
				_buttons[i].setBackground(_parent.getButtonBackground());
				_buttons[i].setFocusBackground(_parent.getButtonFocusBackground());
				_buttons[i].setTextStyle(button_style[i]);
				_buttons[i].setFocusTextStyle(button_focus_style[i]);
				addView(_buttons[i]);
			}else {
				_buttons[i] = null;
			}
		}

	}
	
	private void up() {
		_parent.upInputControl(this);
	}
	
	private void down() {
		_parent.downInputControl(this);
	}
	
	private void delete() {
		_parent.deleteInputControl(this);
	}
	
	private void insert() {
		_parent.insertInputControl(this);
	}
	
	protected void setButtonBackground(BackgroundDescriptor desc) {
		for(int i = 0; i < _buttons.length; i++) {
			if(_buttons[i] != null) {
				_buttons[i].setBackground(desc);
				_buttons[i].setFocusBackground(desc);
			}
		}
	}
	protected void setButtonFocusBackground(BackgroundDescriptor desc) {
		for(int i = 0; i < _buttons.length; i++) {
			if(_buttons[i] != null) {
				_buttons[i].setFocusBackground(desc);
			}
		}	
	}
	protected void setUpButtonStyle(TextStyleDescriptor style) {
		if(_buttons[UP_INDEX] != null) {
			_buttons[UP_INDEX].setTextStyle(style);
			_buttons[UP_INDEX].setFocusTextStyle(style);
			_buttons[UP_INDEX].commitLayoutChange();
		}
	}
	protected void setUpButtonFocusStyle(TextStyleDescriptor style) {
		if(_buttons[UP_INDEX] != null) {
			_buttons[UP_INDEX].setFocusTextStyle(style);
			_buttons[UP_INDEX].commitLayoutChange();
		}
	}
	protected void setInsertButtonStyle(TextStyleDescriptor style) {
		if(_buttons[INSERT_INDEX] != null) {
			_buttons[INSERT_INDEX].setTextStyle(style);
			_buttons[INSERT_INDEX].setFocusTextStyle(style);
			_buttons[INSERT_INDEX].commitLayoutChange();
		}
	}
	protected void setInsertButtonFocusStyle(TextStyleDescriptor style) {
		if(_buttons[INSERT_INDEX] != null) {
			_buttons[INSERT_INDEX].setFocusTextStyle(style);
			_buttons[INSERT_INDEX].commitLayoutChange();
		}
	}
	protected void setDeleteButtonStyle(TextStyleDescriptor style) {
		if(_buttons[DELETE_INDEX] != null) {
			_buttons[DELETE_INDEX].setTextStyle(style);
			_buttons[DELETE_INDEX].setFocusTextStyle(style);
			_buttons[DELETE_INDEX].commitLayoutChange();
		}
	}
	protected void setDeleteButtonFocusStyle(TextStyleDescriptor style) {
		if(_buttons[DELETE_INDEX] != null) {
			_buttons[DELETE_INDEX].setFocusTextStyle(style);
			_buttons[DELETE_INDEX].commitLayoutChange();
		}
	}
	protected void setDownButtonStyle(TextStyleDescriptor style) {
		if(_buttons[DOWN_INDEX] != null) {
			_buttons[DOWN_INDEX].setTextStyle(style);
			_buttons[DOWN_INDEX].setFocusTextStyle(style);
			_buttons[DOWN_INDEX].commitLayoutChange();
		}
	}
	protected void setDownButtonFocusStyle(TextStyleDescriptor style) {
		if(_buttons[DOWN_INDEX] != null) {
			_buttons[DOWN_INDEX].setFocusTextStyle(style);
			_buttons[DOWN_INDEX].commitLayoutChange();
		}
	}

	@Override
	protected void layoutChildren(int l, int t, int width, int h) {
	
		if(_component.getField() != null) {
			View field = _component.getField();
			field.layout(_component.getOffsetLeft(), _component.getOffsetTop(), _component.getOffsetLeft() + field.getMeasuredWidth(), _component.getOffsetTop() + field.getMeasuredHeight());
		}

		int button_height = 0;
		for(int i = 0; i < _buttons.length; i ++) {
			if(_buttons[i] != null) {
				if(button_height < _buttons[i].getMeasuredHeight()) {
					button_height = _buttons[i].getMeasuredHeight();
				}
			}
		}
		
		int height = _component.getHeight() + SPACE + button_height;
		int button_top  = height - _component.getBorderWidthBottom() - _component.getPaddingBottom() - button_height;
		int button_left = _component.getBorderWidthLeft() + _component.getPaddingLeft() + SPACE;
		

		for(int i = 0; i < _buttons.length; i ++) {
			if(_buttons[i] != null) {
				int top = button_top + (button_height - _buttons[i].getMeasuredHeight())/2;
				_buttons[i].layout(button_left, top, button_left + _buttons[i].getMeasuredWidth(), top + _buttons[i].getMeasuredHeight());
				button_left += SPACE + _buttons[i].getMeasuredWidth();
			}
		}
	}
	
	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = 65535; //MeasureSpec.getSize(heightMeasureSpec);

		width -= _component.getBorderWidthLeft() + _component.getBorderWidthRight()
				 + _component.getPaddingLeft() + _component.getPaddingRight();
		
		if(_component.getField() != null) {
			/*measureChild(_component.getField(), MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
					MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));*/
			/*_component.getField().measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));*/
			measureChild(_component.getField(), width, true);
		}

		int button_height = 0;
		for(int i = 0; i < _buttons.length; i ++) {
			if(_buttons[i] != null) {
				measureChild(_buttons[i], MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
						MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
				if(button_height < _buttons[i].getMeasuredHeight()) {
					button_height = _buttons[i].getMeasuredHeight();
				}
			}
		}
		
		height = _component.getHeight() + SPACE + button_height;
		
		this.setMeasuredDimension(width, height);
	}
	
	protected void onDraw(Canvas g) {
		_component.drawBackground(g, 0, 0, getWidth(), getHeight());
		super.onDraw(g);
	}
	
	protected boolean isComponent(MAGComponentInterface comp) {
		if(_component == comp) {
			return true;
		}else {
			return false;
		}
	}
}
