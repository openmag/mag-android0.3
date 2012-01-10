/**
 * AddContactListField.java
 *
 * Copyright 2007-2010 anhe.
 */
package com.anheinno.libs.mag.controls.contacts;

import java.util.Vector;

import com.anheinno.android.libs.R;
import com.anheinno.android.libs.graphics.Align;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.BitmapRepository;
import com.anheinno.android.libs.graphics.PaintRepository;
import com.anheinno.android.libs.graphics.Paragraph;
import com.anheinno.android.libs.ui.EasyDialog;
import com.anheinno.android.libs.ui.ListField;
import com.anheinno.android.libs.ui.ListField.ListFieldCallback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;



/**
 * 2010-5-7
 * 
 * @author 沈瑞恒
 * 
 * @version 1.0
 * 
 */
public class AddContactListField extends ListField<ContactUser> implements ListFieldCallback<ContactUser> {
	private Vector<ContactUser> _vector = new Vector<ContactUser>();
	private boolean _readonly;
	private int _limit;

	public AddContactListField(Context context, boolean readonly, int limit) {
		super(context);
		this._readonly = readonly;
		this._limit = limit;
		this.init();
	}

	private void init() {
		this.setRowHeight(30);
		this.setSearchable(true);
		this.setCallback(this);
	}

	public Vector<ContactUser> getVector() {
		return _vector;
	}

	public boolean isHave(ContactUser user) {
		int length = _vector.size();
		for (int i = 0; i < length; i++) {
			ContactUser list = _vector.elementAt(i);
			if (user.get_account().equals(list.get_account())) {
				return true;
			}
		}
		return false;
	}

	// public void setListSize() {
	// this.setSize(20);
	// }

	public void add(ContactUser account) {
		if (!isHave(account)) {
			if (_vector.size() >= _limit) {
				if(_limit > 1) {
					EasyDialog.longAlert(getContext(), getContext().getString(R.string.contactinput_maxnumber_alert) + " " + _limit);
					return;
				}else {
					_vector.removeAllElements();
				}
			}
			_vector.addElement(account);
			this.setSize(_vector.size());
			//this.invalidate(vector.size() - 1);
		}
	}

	public void removeUser(int index) {
		if (_vector.size() > 0 && index < _vector.size()) {
			_vector.removeElementAt(index);
			this.setSize(_vector.size());
			this.invalidate();
		}
	}

	public void upUser(int index) {
		if (index > 0 && index < _vector.size()) {
			ContactUser temp = _vector.elementAt(index);
			_vector.removeElementAt(index);
			_vector.insertElementAt(temp, index - 1);
			this.invalidate(index - 1);
			this.invalidate(index);
			setSelectedIndex(index - 1);
		}
	}

	public void downUser(int index) {
		if (index < _vector.size() - 1) {
			ContactUser temp = _vector.elementAt(index);
			_vector.removeElementAt(index);
			_vector.insertElementAt(temp, index + 1);
			this.invalidate(index);
			this.invalidate(index + 1);
			setSelectedIndex(index + 1);
		}
	}

	private static Paragraph _paragraph;	
	private static BackgroundDescriptor _focus_bg;
	private static Paint _paint;
	private static Paint _focus_paint;
	private static final int PADDING = 3;
	
	public void drawListRow(ListField<ContactUser> listField, Canvas g, int index,
			int top, int width, boolean focus) {
		if (_vector.size() == 0) {
			return;
		}
		if(_paragraph == null) {
			_paragraph = new Paragraph(getContext(), "", 0);
			_paragraph.setLineCount(1);
			_focus_bg = new BackgroundDescriptor("color=blue");
			_paint = PaintRepository.getDefaultPaint(getContext());
			_focus_paint = PaintRepository.getFontPaint(getContext(), false, false, false, 1.0f, Color.WHITE, 255);
		}
		
		if (index < _vector.size()) {
			if(focus) {
				_focus_bg.draw(getContext(), g, 0, top, width, getRowHeight());
			}
			ContactUser user = _vector.elementAt(index);
			Bitmap icon = BitmapRepository.getBitmapByName(getContext(), "user_small.png");
			
			g.drawBitmap(icon, PADDING, (getRowHeight() - icon.getHeight())/2, null);
			
			_paragraph.setWidthBound(width - PADDING*3 - icon.getWidth());
			_paragraph.setText(user.get_name());
			if(focus) {
				_paragraph.setPaint(_focus_paint);
			}else {
				_paragraph.setPaint(_paint);
			}
			_paragraph.draw(g, PADDING*2 + icon.getWidth(), (getRowHeight() - _paragraph.getHeight())/2, Align.LEFT);
		}
	}

	public ContactUser get(ListField<ContactUser> listField, int index) {
		if (index < _vector.size()) {
			return _vector.elementAt(index);
		} else {
			return null;
		}
	}

	public int indexOfList(ListField<ContactUser> listField, String prefix, int start) {
		return listField.getSelectedIndex();
	}

	@Override
	protected void onSelect(int index, ContactUser data) {
	}
	
	/*protected boolean keyChar(char character, int status, int time) {
		if (!_readonly) {
			int cur_idx = getSelectedIndex();
			if (character == Keypad.KEY_BACKSPACE) {// 删除
				this.removeUser(cur_idx);
				return true;
			}
			if (character == 'u') {// 向上调
				this.upUser(cur_idx);
				return true;
			}
			if (character == 'd') {// 向下调
				this.downUser(cur_idx);
				return true;
			}
			if (character == 'h' || character == '?') {// 帮助
				UtilClass.showInfo(_resources
						.getString(CONTACTINPUT_HELP));
				return true;
			}
		}
		return super.keyChar(character, status, time);
	}
	
	protected void makeContextMenu(ContextMenu cm) {
		if(_readonly) {
			return;
		}
		int cur_idx = this.getSelectedIndex();
		if(cur_idx >= 0 && cur_idx < getSize()) {
			if(cur_idx > 0 && getSize() > 1) {
				cm.addItem(new UserListContextMenu(cur_idx, 
						_resources.getString(CONTACTINPUT_MOVE_UP),
						this, UserListContextMenu.USER_LIST_CONTEXT_MENU_UP));
			}
			if(cur_idx < this.getSize() - 1 && this.getSize() > 1) {
				cm.addItem(new UserListContextMenu(cur_idx, 
						_resources.getString(CONTACTINPUT_MOVE_DOWN),
						this, UserListContextMenu.USER_LIST_CONTEXT_MENU_DOWN));
			}
			cm.addItem(new UserListContextMenu(cur_idx, 
					_resources.getString(CONTACTINPUT_REMOVE),
					this, UserListContextMenu.USER_LIST_CONTEXT_MENU_REMOVE));
		}
	}
	
	private class UserListContextMenu extends MenuItem {
		private int _idx;
		private AddContactListField _list;
		private int _type;
		public static final int USER_LIST_CONTEXT_MENU_UP = 0;
		public static final int USER_LIST_CONTEXT_MENU_DOWN = 1;
		public static final int USER_LIST_CONTEXT_MENU_REMOVE = 2;
		
		public UserListContextMenu(int idx, String text, AddContactListField list, int type){
			super(text, 65535, 65535);
			_idx = idx;
			_list = list;
			_type = type;
		}

		public void run() {
			if(_type == USER_LIST_CONTEXT_MENU_UP) {
				_list.upUser(_idx);
			}else if(_type == USER_LIST_CONTEXT_MENU_DOWN) {
				_list.downUser(_idx);
			}else if(_type == USER_LIST_CONTEXT_MENU_REMOVE) {
				_list.removeUser(_idx);
			}
		}
		
	}*/
	
}
