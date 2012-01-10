package com.anheinno.android.libs.ui;

import com.anheinno.android.libs.util.SortedVector;

public class ScreenMenu {
	
	private SortedVector<ScreenMenuItem> _menu_list;
	
	public static final int MOST_USED_MENU_ITEM = 10000;
	public static final int USER_DEFINED_MENU_ITEM = 20000;
	public static final int LESS_USED_MENU_ITEM = 30000;
	public static final int DEBUG_MENU_ITEM = 60000;

	public ScreenMenu() {
		_menu_list = new SortedVector<ScreenMenuItem>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -4809717888756468034L;

			@Override
			protected int compare(ScreenMenuItem o1, ScreenMenuItem o2) {
				return o1.compareTo(o2);
			}
		};
	}
	
	public void add(ScreenMenuItem mi) {
		_menu_list.addSorted(mi);
	}
	
	public int count() {
		return _menu_list.size();
	}
	
	public ScreenMenuItem getItem(int index) {
		if(index >= 0 && index < _menu_list.size()) {
			return _menu_list.elementAt(index);
		}else {
			return null;
		}
	}
	
	public void reset() {
		_menu_list.removeAllElements();
	}
	
	public void releaseResources() {
		reset();
	}
}
