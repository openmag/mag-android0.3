package com.anheinno.android.libs.util;

public class Direction {
	public int _top;
	public int _bottom;
	public int _left;
	public int _right;
	
	public Direction(int v) {
		set(v);
	}
	
	public void set(int v) {
		_top = _bottom = _left = _right = v;
	}
	
	public boolean equals(Direction dir) {
		if(_top != dir._top) {
			return false;
		}
		if(_bottom != dir._bottom) {
			return false;
		}
		if(_left != dir._left) {
			return false;
		}
		if(_right != dir._right) {
			return false;
		}
		return true;
	}
}
