package com.anheinno.android.libs.mag;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.OnClickListener;

import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.ui.CustomButtonField;
import com.anheinno.android.libs.ui.Manager;



public class MAGInputDuplicatorField extends Manager implements MAGContainerLayoutInterface, OnClickListener {
	
	private MAGInputDuplicator _duplicator;
	private CustomButtonField _insert_button;
	private MAGTitleArea _title_area;
	
	private BackgroundDescriptor _button_background;
	private BackgroundDescriptor _button_focus_background;
	private TextStyleDescriptor _insert_button_style;
	private TextStyleDescriptor _insert_button_focus_style;
	private TextStyleDescriptor _delete_button_style;
	private TextStyleDescriptor _delete_button_focus_style;
	private TextStyleDescriptor _up_button_style;
	private TextStyleDescriptor _up_button_focus_style;
	private TextStyleDescriptor _down_button_style;
	private TextStyleDescriptor _down_button_focus_style;
	

	protected MAGInputDuplicatorField(Context context, MAGInputDuplicator duplicator) {
		super(context);
		
		_duplicator = duplicator;
		
		if(duplicator.title() != null && duplicator.title().length() > 0) {
			_title_area = new MAGTitleArea(duplicator);
		}else {
			_title_area = null;
		}
		
		_button_background = MAGStyleRepository.getButtonBackground();
		_button_focus_background = MAGStyleRepository.getFocusButtonBackground();
		
		_insert_button_style = MAGStyleRepository.getPlusTextStyle();
		_insert_button_focus_style = MAGStyleRepository.getFocusPlusTextStyle();
		_delete_button_style = MAGStyleRepository.getMinusTextStyle();
		_delete_button_focus_style = MAGStyleRepository.getFocusMinusTextStyle();
		_up_button_style = MAGStyleRepository.getAscTextStyle();
		_up_button_focus_style = MAGStyleRepository.getFocusAscTextStyle();
		_down_button_style = MAGStyleRepository.getDescTextStyle();
		_down_button_focus_style = MAGStyleRepository.getFocusDescTextStyle();
		

	}
	
	public void onClick(View field) {
		insertInputControl(null);
	}
	
	protected void setButtonBackground(BackgroundDescriptor desc) {
		_button_background = desc;
		_button_focus_background = desc;
		for(int i = 0; i < getChildCount(); i ++) {
			View f = getChildAt(i);
			if(f instanceof MAGInputDuplicatorSubField) {
				((MAGInputDuplicatorSubField)f).setButtonBackground(desc);
			}
		}
	}
	protected BackgroundDescriptor getButtonBackground() {
		return _button_background;
	}
	protected void setButtonFocusBackground(BackgroundDescriptor desc) {
		_button_focus_background = desc;
		for(int i = 0; i < getChildCount(); i ++) {
			View f = getChildAt(i);
			if(f instanceof MAGInputDuplicatorSubField) {
				((MAGInputDuplicatorSubField)f).setButtonFocusBackground(desc);
			}
		}
	}
	protected BackgroundDescriptor getButtonFocusBackground() {
		return _button_focus_background;
	}
	protected void setUpButtonStyle(TextStyleDescriptor desc) {
		_up_button_style = desc;
		_up_button_focus_style = desc;
		for(int i = 0; i < getChildCount(); i ++) {
			View f = getChildAt(i);
			if(f instanceof MAGInputDuplicatorSubField) {
				((MAGInputDuplicatorSubField)f).setUpButtonStyle(desc);
			}
		}
	}
	protected TextStyleDescriptor getUpButtonStyle() {
		return _up_button_style;
	}
	protected void setUpButtonFocusStyle(TextStyleDescriptor desc) {
		_up_button_focus_style = desc;
		for(int i = 0; i < getChildCount(); i ++) {
			View f = getChildAt(i);
			if(f instanceof MAGInputDuplicatorSubField) {
				((MAGInputDuplicatorSubField)f).setUpButtonFocusStyle(desc);
			}
		}
	}
	protected TextStyleDescriptor getUpButtonFocusStyle() {
		return _up_button_focus_style;
	}
	protected void setDownButtonStyle(TextStyleDescriptor desc) {
		_down_button_style = desc;
		_down_button_focus_style = desc;
		for(int i = 0; i < getChildCount(); i ++) {
			View f = getChildAt(i);
			if(f instanceof MAGInputDuplicatorSubField) {
				((MAGInputDuplicatorSubField)f).setDownButtonStyle(desc);
			}
		}
	}
	protected TextStyleDescriptor getDownButtonStyle() {
		return _down_button_style;
	}
	protected void setDownButtonFocusStyle(TextStyleDescriptor desc) {
		_down_button_focus_style = desc;
		for(int i = 0; i < getChildCount(); i ++) {
			View f = getChildAt(i);
			if(f instanceof MAGInputDuplicatorSubField) {
				((MAGInputDuplicatorSubField)f).setDownButtonFocusStyle(desc);
			}
		}
	}
	protected TextStyleDescriptor getDownButtonFocusStyle() {
		return _down_button_focus_style;
	}
	protected void setInsertButtonStyle(TextStyleDescriptor desc) {
		_insert_button_style = desc;
		_insert_button_focus_style = desc;
		_insert_button.setTextStyle(desc);
		_insert_button.setFocusTextStyle(desc);
		_insert_button.commitLayoutChange();
		for(int i = 0; i < getChildCount(); i ++) {
			View f = getChildAt(i);
			if(f instanceof MAGInputDuplicatorSubField) {
				((MAGInputDuplicatorSubField)f).setInsertButtonStyle(desc);
			}
		}
	}
	protected TextStyleDescriptor getInsertButtonStyle() {
		return _insert_button_style;
	}
	protected void setInsertButtonFocusStyle(TextStyleDescriptor desc) {
		_insert_button_focus_style = desc;
		_insert_button.setFocusTextStyle(desc);
		_insert_button.commitLayoutChange();
		for(int i = 0; i < getChildCount(); i ++) {
			View f = getChildAt(i);
			if(f instanceof MAGInputDuplicatorSubField) {
				((MAGInputDuplicatorSubField)f).setInsertButtonFocusStyle(desc);
			}
		}
	}
	protected TextStyleDescriptor getInsertButtonFocusStyle() {
		return _insert_button_focus_style;
	}
	protected void setDeleteButtonStyle(TextStyleDescriptor desc) {
		_delete_button_style = desc;
		_delete_button_focus_style = desc;
		for(int i = 0; i < getChildCount(); i ++) {
			View f = getChildAt(i);
			if(f instanceof MAGInputDuplicatorSubField) {
				((MAGInputDuplicatorSubField)f).setDeleteButtonStyle(desc);
			}
		}
	}
	protected TextStyleDescriptor getDeleteButtonStyle() {
		return _delete_button_style;
	}
	protected void setDeleteButtonFocusStyle(TextStyleDescriptor desc) {
		_delete_button_focus_style = desc;
		for(int i = 0; i < getChildCount(); i ++) {
			View f = getChildAt(i);
			if(f instanceof MAGInputDuplicatorSubField) {
				((MAGInputDuplicatorSubField)f).setDeleteButtonFocusStyle(desc);
			}
		}
	}
	protected TextStyleDescriptor getDeleteButtonFocusStyle() {
		return _delete_button_focus_style;
	}
	
	protected void initField(Context context) {
		
		if(_duplicator.isInsertable()) {
			_insert_button = new CustomButtonField(getContext(), "", this);
			_insert_button.setBackground(_button_background);
			_insert_button.setFocusBackground(_button_focus_background);
			_insert_button.setTextStyle(_insert_button_style);
			_insert_button.setFocusTextStyle(_insert_button_focus_style);
			addView(_insert_button);
		}else {
			_insert_button = null;
		}
		
		try {

			for(int i = 0; i < _duplicator.childrenNum(); i ++) {
				MAGComponent comp = _duplicator.getChild(i);
				if(comp.getField() == null) {
					View f = comp.initField(context);
					if(f != null) {
						comp.setField(f);
						System.out.println("MAGDuplicator: XXXXXXXXXXXXXXXTemplate init succ");
					}else {
						System.out.println("MAGDuplicator: XXXXXXXXXXXXXXXTemplate init fails!!!!!");
					}
				}
				addView(new MAGInputDuplicatorSubField(context, this, comp));
			}
			
		}catch(final Exception e) {
			LOG.error(this, "updateField: error", e);
		}

	}

	protected void onDraw(Canvas g) {
		int title_height = 0;
		
		if(_insert_button != null) {
			title_height = _insert_button.getHeight();
		}
		if(_title_area != null && title_height < _title_area.getTitleHeight()) {
			title_height = (int)_title_area.getTitleHeight();
		}
		
		if(title_height > 0) {
			BackgroundDescriptor bg = _duplicator.style().getTitleBackground();
			if(bg != null) {
				bg.draw(getContext(), g, 0, 0, getWidth(), title_height);
			}
		}
		
		if(_title_area != null) {
			_title_area.drawTitle(g, 0, (int)(title_height - _title_area.getTitleHeight())/2);
		}
		
		super.onDraw(g);
	}
	
	@Override
	protected void layoutChildren(int left, int ttop, int width, int height) {
		System.out.println("MAGInputDuplicatorField layoutChildren........");
		//int width = getMeasuredWidth();
		int title_height = 0;
		int start_index = 0;
		
		if(_insert_button != null) {
			start_index = 1;
			title_height = _insert_button.getMeasuredHeight();
			if(_title_area != null && title_height < _title_area.getTitleHeight()) {
				title_height = (int)_title_area.getTitleHeight();
			}
			
			int l = width - _insert_button.getMeasuredWidth() - MAGInputDuplicatorSubField.SPACE;
			int t = (title_height - _insert_button.getMeasuredHeight())/2;
			_insert_button.layout(l, t, l + _insert_button.getMeasuredWidth(), t + _insert_button.getMeasuredHeight());
		}
		
		int top = title_height;
		
		for(int i = start_index; i < getChildCount(); i ++) {
			View field = getChildAt(i);
			field.layout(0, top, field.getMeasuredWidth(), top + field.getMeasuredHeight());
			top += getChildAt(i).getMeasuredHeight();
		}
	}
	
	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = 65535; //MeasureSpec.getSize(heightMeasureSpec);

		int start_index = 0;
		int title_height = 0;
		int title_width = 0;
		
		if(_insert_button != null) {
			start_index = 1;
			measureChild(_insert_button, MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
					MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
			title_height = _insert_button.getMeasuredHeight();
			title_width = _insert_button.getMeasuredWidth();
		}
		
		if(_title_area != null) {
			_title_area.layout(width - title_width  - MAGInputDuplicatorSubField.SPACE*2);
			if(title_height < _title_area.getTitleHeight()) {
				title_height = (int)_title_area.getTitleHeight();
			}
		}
		
		int top = title_height;
		
		for(int i = start_index; i < getChildCount(); i ++) {
			View field = getChildAt(i);
			/*measureChild(field, MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));*/
			measureChild(field, width, true);
			top += getChildAt(i).getMeasuredHeight();
		}
		
		this.setMeasuredDimension(width, top);
	}
	
	private int getSubFieldIndex(MAGInputDuplicatorSubField field) {
		for(int i = 0; i < getChildCount(); i ++) {
			if(getChildAt(i) == field) {
				return i;
			}
		}
		return -1;
	}
	
	protected void upInputControl(MAGInputDuplicatorSubField field) {
		int f_index = getSubFieldIndex(field);
		int comp_index = f_index;
		if(!(getChildAt(0) instanceof MAGInputDuplicatorSubField)) {
			comp_index--;
		}
		if(_duplicator.upInputControl(comp_index)) {
			removeViews(f_index, 1);
			addView(field, f_index-1);
			field.requestFocus();
		}
	}
	
	protected void downInputControl(MAGInputDuplicatorSubField field) {
		int f_index = getSubFieldIndex(field);
		int comp_index = f_index;
		if(!(getChildAt(0) instanceof MAGInputDuplicatorSubField)) {
			comp_index--;
		}
		if(_duplicator.downInputControl(comp_index)) {
			removeViews(f_index, 1);
			addView(field, f_index+1);
			field.requestFocus();
		}
	}
	
	protected void deleteInputControl(MAGInputDuplicatorSubField field) {
		int f_index = getSubFieldIndex(field);
		int comp_index = f_index;
		if(!(getChildAt(0) instanceof MAGInputDuplicatorSubField)) {
			comp_index--;
		}
		if(_duplicator.deleteInputControl(comp_index)) {
			removeViews(f_index, 1);
		}
	}
	
	protected void insertInputControl(MAGInputDuplicatorSubField field) {
		int field_index = 0;
		int comp_index = 0;
		
		if(field != null) {
			field_index = getSubFieldIndex(field) + 1;
			comp_index = field_index;
			if(!(getChildAt(0) instanceof MAGInputDuplicatorSubField)) {
				comp_index--;
			}
		}else {
			if(!(getChildAt(0) instanceof MAGInputDuplicatorSubField)) {
				field_index = 1;
				comp_index = 0;
			}else {
				field_index = 0;
				comp_index = 0;
			}
		}
		System.out.println("input count before " + _duplicator.childrenNum() + " field_index=" + field_index + " comp_index=" + comp_index);
		if(_duplicator.insertInputControl(comp_index)) {
			System.out.println("input count after " + _duplicator.childrenNum());
			MAGComponent comp = _duplicator.getChild(comp_index);
			if(comp.getField() == null) {
				View f = comp.initField(getContext());
				if(f != null) {
					comp.setField(f);
				}
			}
			MAGInputDuplicatorSubField pane = new MAGInputDuplicatorSubField(getContext(), this, comp);
			addView(pane, field_index);
			pane.requestFocus();
		}
	}
	
	protected boolean isInsertable() {
		return _duplicator.isInsertable();
	}
	protected boolean isDeleteable() {
		return _duplicator.isDeletable();
	}
	protected boolean isSortable() {
		return _duplicator.isSortable();
	}
	
	public void invalidateMAGComponent(MAGComponentInterface comp) {
		for(int i = 0; i < getChildCount(); i ++) {
			View f = getChildAt(i);
			if(f instanceof MAGInputDuplicatorSubField) {
				if(((MAGInputDuplicatorSubField)f).isComponent(comp)) {
					((MAGInputDuplicatorSubField)f).invalidate();
					break;
				}
			}
		}
	}
	
}
