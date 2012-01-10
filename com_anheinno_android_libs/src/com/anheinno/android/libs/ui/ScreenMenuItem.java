package com.anheinno.android.libs.ui;

import android.view.MenuItem;

public abstract class ScreenMenuItem implements Runnable {

	private int _order;
	private String _title;
	private MenuItem _sys_menu;
	private boolean _checkable;
	private boolean _checked;
	
	private String _menu_icon_name;
	private int _menu_icon_id;
	private String _bar_icon_name;
	private String _focus_bar_icon_name;

	public ScreenMenuItem(String title, int order) {
		this(title, false, order);
		_checkable = false;
	}

	public ScreenMenuItem(String title, boolean checked, int order) {
		_order = order;
		_title = title;
		_sys_menu = null;
		_checkable = true;
		_checked = checked;
		
		_menu_icon_name = null;
		_menu_icon_id = 0;
		_bar_icon_name = null;
		_focus_bar_icon_name = null;

		initIcons();
	}
	
	protected abstract void initIcons();

	protected final int compareTo(ScreenMenuItem mi) {
		if (_order != mi._order) {
			return _order - mi._order;
		}
		return getTitle().compareTo(mi.getTitle());
	}

	public String getTitle() {
		return _title;
	}

	public String getMenuIconName() {
		return _menu_icon_name;
	}
	
	public int getMenuIconId() {
		return _menu_icon_id;
	}

	public String getBarIconName() {
		return _bar_icon_name;
	}
	
	public String getFocusBarIconName() {
		return _focus_bar_icon_name;
	}
	
	public final void setSystemMenuItem(MenuItem mi) {
		_sys_menu = mi;
	}

	protected final boolean isSameMenuItem(MenuItem mi) {
		if (_sys_menu != null && _sys_menu == mi) {
			return true;
		} else {
			return false;
		}
	}

	public final void setTitle(String title) {
		_title = title;
		if (_sys_menu != null) {
			_sys_menu.setTitle(title);
		}
	}
	
	public final void setMenuIconName(String name) {
		_menu_icon_name = name;
	}
	
	public final void setMenuIconId(int id) {
		_menu_icon_id = id;
	}

	public final void setBarIconName(String name) {
		_bar_icon_name = name;
	}

	public final void setFocusBarIconName(String name) {
		_focus_bar_icon_name = name;
	}

	public final boolean isChecked() {
//		if (_sys_menu != null) {
//			return _sys_menu.isChecked();
//		} else {
//			return false;
//		}
		return _checked;
	}

	public final void setChecked(boolean checked) {
		_checked = checked;
		if (_sys_menu != null) {
			_sys_menu.setChecked(checked);
		}
	}

	public final boolean isCheckable() {
		return _checkable;
	}

}
