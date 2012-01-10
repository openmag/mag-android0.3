/*
 * MAGLayout.java
 *
 * <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import java.util.Vector;

import android.content.Context;

import com.anheinno.android.libs.ui.FullScreen;



/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
class MAGLayout {
	Vector<MAGLayoutLine> _lines;
	int _width;
	int _visible_height;
	public static final int BORDER_WIDTH = 1;

	public MAGLayout(int width, int visible_height) {
		_lines = new Vector<MAGLayoutLine>();
		_width = width;
		_visible_height = visible_height;
	}

	public void add(MAGComponentInterface m) {
		MAGLayoutLine line = null;

		if (_lines.size() == 0) {
			line = new MAGLayoutLine();
			_lines.addElement(line);
		} else {
			line =  _lines.lastElement();
		}

		if ((m.getField() != null)) {
			boolean new_line = false;
			if(m.style().isFillWidth()) {
				if(line.getWidth() >= _width) {
					m.setWidth(_width);
					new_line = true;
				}else {
					m.setWidth(_width - line.getWidth());
				}
			}else {
				m.setWidth(m.style().getWidth(_width));
				if(m.getWidth() + line.getWidth() > _width) {
					new_line = true;
				}
			}
			if(new_line) {
				line = new MAGLayoutLine();
				_lines.addElement(line);
			}
			line.add(m);
		}
	}
	
	public int getPreferredFieldHeight(int row, int idx) {
		int h = 0;
		MAGComponentInterface comp = getComponent(row, idx);
		if(comp.style().isFillHeight()) {
			int top = 0;
			for(int i = 0; i < row; i ++) {
				top += getLineHeight(i);
			}
			h = _visible_height - top;
		}else {
			h = comp.style().getHeight(_visible_height);
		}
		// first save row height
		comp.setRowHeight(h);
		h -= comp.getBorderWidthBottom() + comp.getBorderWidthTop() + comp.getPaddingBottom() + comp.getPaddingTop();
		return h;
	}

	public int getLineCount() {
		return _lines.size();
	}

	private MAGLayoutLine getLine(int row) {
		if (row >= 0 && row < _lines.size()) {
			return _lines.elementAt(row);
		} else {
			return null;
		}
	}

	public int getLineWidth(int row) {
		MAGLayoutLine line = getLine(row);
		if (line != null) {
			return line.getWidth();
		} else {
			return 0;
		}
	}

	public int getLineWidth(int row, int idx) {
		MAGLayoutLine line = getLine(row);
		if (line != null) {
			return line.getWidth(idx);
		} else {
			return 0;
		}
	}

	public int getLineHeight(int row) {
		MAGLayoutLine line = getLine(row);
		if (line != null) {
			return line.getRowHeight();
		} else {
			return 0;
		}
	}

	public void updateLineHeight(int row) {
		MAGLayoutLine line = getLine(row);
		if (line != null) {
			line.updateHeight();
		}
	}

	public int getMAGComponentCount(int row) {
		MAGLayoutLine line = getLine(row);
		if (line != null) {
			return line.getCount();
		} else {
			return 0;
		}
	}
	
	public MAGComponentInterface getComponent(int row, int idx) {
		MAGLayoutLine line = getLine(row);
		if (line != null) {
			return line.getComponent(idx);
		} else {
			return null;
		}
	}

	public int getCompLeft(int row, int idx) {
		MAGLayoutLine line = getLine(row);
		if (line != null) {
			return line.getOffsetLeft(idx);
		} else {
			return 0;
		}
	}

	public int getCompTop(int row, int idx) {
		MAGLayoutLine line = getLine(row);
		if (line != null) {
			return line.getOffsetTop(idx);
		} else {
			return 0;
		}
	}
		
	/*protected MAGComponent findNextFocusUp(int row_idx, int col_idx) {
		int focus_x_cnt = getLine(row_idx).getFocusableCount(col_idx+1);
		row_idx--;
		int focus_cnt = 0;
		while(row_idx >= 0) {
			focus_cnt = getLine(row_idx).getFocusableCount();
			if(focus_cnt == 0) {
				row_idx--;
			}else {
				if(focus_cnt < focus_x_cnt){
					focus_x_cnt = focus_cnt;
				}
				return getLine(row_idx).getFocusableComponent(focus_x_cnt);
			}
		}
		return null;
	}
	protected MAGComponent findNextFocusDown(int row_idx, int col_idx) {
		int focus_x_cnt = getLine(row_idx).getFocusableCount(col_idx+1);
		row_idx++;
		int focus_cnt = 0;
		while(row_idx < _lines.size()) {
			focus_cnt = getLine(row_idx).getFocusableCount();
			if(focus_cnt == 0) {
				row_idx++;
			}else {
				if(focus_cnt < focus_x_cnt){
					focus_x_cnt = focus_cnt;
				}
				return getLine(row_idx).getFocusableComponent(focus_x_cnt);
			}
		}
		return null;
	}
	protected MAGComponent findNextFocusLeft(int row_idx, int col_idx) {
		col_idx--;
		while(row_idx >= 0) {
			while(col_idx >= 0) {
				MAGComponent focus = getComponent(row_idx, col_idx);
				if(focus.visible() && focus.isFocusable()) {
					return focus;
				}
				col_idx--;
			}
			row_idx--;
			if(row_idx >= 0) {
				col_idx = getLine(row_idx).getCount()-1;
			}
		}
		return null;
	}
	protected MAGComponent findNextFocusRight(int row_idx, int col_idx) {
		col_idx++;
		while(row_idx < _lines.size()) {
			while(col_idx < getLine(row_idx).getCount()) {
				MAGComponent focus = getComponent(row_idx, col_idx);
				if(focus.visible() && focus.isFocusable()) {
					return focus;
				}
				col_idx++;
			}
			row_idx++;
			if(row_idx < _lines.size()) {
				col_idx = 0;
			}
		}
		return null;
	}
	protected MAGComponent getFirstFocusableComponent() {
		return findNextFocusRight(0, -1);
	}
	protected MAGComponent getLastFocusableComponent() {
		return findNextFocusLeft(_lines.size()-1, getLine(_lines.size()-1).getCount());
	}*/

	private class MAGLayoutLine {
		private Vector<MAGComponentInterface> _components;
		private int _row_height;

		private MAGLayoutLine() {
			_components = new Vector<MAGComponentInterface>();
			_row_height = 0;
		}

		private void add(MAGComponentInterface m) {
			_components.addElement(m);
		}

		private int getCount() {
			return _components.size();
		}
		
		private MAGComponentInterface getComponent(int idx) {
			return _components.elementAt(idx);
		}
		
		private void updateHeight() {
			int max = 0;
			for (int i = 0; i < getCount(); i++) {
				/*if (max < getComponent(i).getActualHeight()) {
					max = getComponent(i).getActualHeight();
				}*/
				if(max < getComponent(i).getHeight()) {
					max = getComponent(i).getHeight();
				}
			}
			//if (max != _row_height) {
			for (int i = 0; i < getCount(); i++) {
				getComponent(i).setRowHeight(max);
				//System.out.println("component update row hight to " + max);
			}
			_row_height = max;
			//}
		}

		private int getRowHeight() {
			return _row_height;
		}

		private int getWidth() {
			return getWidth(_components.size());
		}

		private int getWidth(int idx) {
			if (idx > _components.size()) {
				idx = _components.size();
			}
			int w = 0;
			for (int i = 0; i < idx; i++) {
				MAGComponent comp = (MAGComponent) _components.elementAt(i);
				w += comp.getWidth();
			}
			return w;
		}

		private int getOffsetLeft(int idx) {
			if (idx < 0 || idx > _components.size()) {
				return -1;
			}
			if (idx == _components.size()) {
				return getWidth();
			}
			int left = getWidth(idx);
			MAGComponent comp = (MAGComponent) _components.elementAt(idx);
			return left + comp.getOffsetLeft();
		}

		private int getOffsetTop(int idx) {
			if (idx < 0 || idx >= _components.size()) {
				return -1;
			}
			MAGComponent comp = (MAGComponent) _components.elementAt(idx);
			if (comp.getField() == null) {
				return 0;
			}else {
				return comp.getOffsetTop();
			}
		}
	}

}
