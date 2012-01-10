package com.anheinno.android.libs.ui;

import java.util.Vector;

import com.anheinno.android.libs.graphics.BitmapRepository;
import com.anheinno.android.libs.graphics.PaintRepository;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
//import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class TreeView<TYPE> extends View {
	private static final float DEFAULT_INDENT = 10f;
	private float _row_height;
	private float _indent;
	private Vector<TreeViewNode> _node_list;
	private int _first_slot_index;
	private TreeViewNode _current_node;
	private TreeViewNode _node_in_touch;
	private TreeViewCallback<TYPE> _callback;
	private boolean _default_expand;
	
	private int _fold_icon_width;
	private int _fold_icon_height;
	
	private static final String FOLD_ICON         = "fold.png";
	private static final String FOLD_FOCUS_ICON   = "fold_focus.png";
	private static final String UNFOLD_ICON       = "unfold.png";
	private static final String UNFOLD_FOCUS_ICON = "unfold_focus.png";
	private static final int PADDING = 3;
	
	public interface TreeViewCallback<TYPE> {
		void drawTreeItem(Canvas canvas, int nid, float left, float top, float width, float height, TYPE data, boolean focus);
	}

	public TreeView(Context context) {
		super(context);
		
		setClickable(true);
		setFocusable(true);
		
		Paint def_paint = PaintRepository.getDefaultPaint(context);
		_row_height = def_paint.getTextSize();
		_indent = DEFAULT_INDENT;
		_node_list = new Vector<TreeViewNode>();
		_first_slot_index = 0;
		_callback = null;
		
		_default_expand = false;
		
		_fold_icon_width = 0;
		_fold_icon_height = 0;
		String[] icons = new String[] {FOLD_ICON, FOLD_FOCUS_ICON, UNFOLD_ICON, UNFOLD_FOCUS_ICON};
		for(int i = 0; i < icons.length; i ++) {
			Bitmap icon = BitmapRepository.getBitmapByName(getContext(), icons[i]);
			if(_fold_icon_width < icon.getWidth()) {
				_fold_icon_width = icon.getWidth();
			}
			if(_fold_icon_height < icon.getHeight()) {
				_fold_icon_height = icon.getHeight();
			}
		}

		_fold_icon_width += 2*PADDING;
		_fold_icon_height += 2*PADDING;

		if(_row_height < _fold_icon_height) {
			_row_height = _fold_icon_height;
		}
		
		reset();
		//System.out.println("TreeView init complete, _node_list size=" + _node_list.size());
	}
	
	public void setDefaultExpanded(boolean default_expand) {
		_default_expand = default_expand;
	}
	
	public void setTreeViewCallback(TreeViewCallback<TYPE> callback) {
		_callback = callback;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		height = (int)(getRoot().getVisibleDescendentCount()*_row_height);
		System.out.println("TreeView: width=" + width + " height=" + height);
		this.setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		getRoot().drawChildren(canvas, 0, 0, getMeasuredWidth());
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//int height = (int)(getRoot().getVisibleDescendentCount()*_row_height);
		onMeasure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	}
	
	@Override
	public final boolean onTouchEvent (MotionEvent event) {
		System.out.println("onTouchEvent " + event);
		requestFocus();
		if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
			TreeViewNode node = getRoot().getChildNodeAtPoint(event.getX(), event.getY());
			if(node != null) {
				System.out.println("node under touch: " + node);
				if(node.itemContainsPoint(event.getX(), event.getY())) {
					if(event.getAction() == MotionEvent.ACTION_DOWN) {
						moveFocus(node);
						return true;
					}
					if(event.getAction() == MotionEvent.ACTION_UP) {
						if(_current_node == node) {
							onSelect(node._node_id, node._data);
							return true;
						}
					}
				} else {
					if(event.getAction() == MotionEvent.ACTION_DOWN) {
						_node_in_touch = node;
						invalidateNode(node);
						return true;
					}else if(event.getAction() == MotionEvent.ACTION_UP) {
						TreeViewNode prev_node = _node_in_touch;
						_node_in_touch = null;
						if(prev_node == node) {
							expandNode(node);
							return true;
						}
						if(prev_node != null) {
							invalidateNode(prev_node);
						}
					}
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
		if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_SPACE) {
			if(_current_node._children != null && _current_node._children.size() > 0) {
				expandNode(_current_node);
			}else {
				onSelect(_current_node._node_id, _current_node._data);
			}
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_ENTER) {
			onSelect(_current_node._node_id, _current_node._data);
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
	
	private void moveFocus(TreeViewNode node) {
		if(_current_node != node) {
			TreeViewNode prev_node = _current_node;
			_current_node = node;
			if(prev_node != null) {
				invalidateNode(prev_node);
			}
			invalidateNode(_current_node);
			/*Rect rect = new Rect();
			if(getGlobalVisibleRect(rect)) {
				if(_current_node._top < rect.bottom) {
					scrollTo(0, (int)(_current_node._top + _row_height));
				}
			}*/
		}
	}
	
	private void expandNode(TreeViewNode node) {
		node.setExpand(!node._expand);
		if(node._expand) {
			onExpand(node._node_id, node._data);
		}else {
			onCollapse(node._node_id, node._data);
		}
		invalidate();
		requestLayout();
	}
	
	private boolean moveFocusUp() {
		TreeViewNode prev = getPreviousNodeInTree(_current_node);
		if(prev != null) {
			moveFocus(prev);
			return true;
		}else {
			return false;
		}
	}
	
	private boolean moveFocusDown() {
		TreeViewNode next = getNextNodeInTree(_current_node);
		if(next != null) {
			moveFocus(next);
			return true;
		}else {
			return false;
		}
	}
	
	protected void onExpand(int nid, TYPE data) {
		System.out.println("onExpand nid=" + nid + " data=" + data);
	}
	
	protected void onCollapse(int nid, TYPE data) {
		System.out.println("onCollapse nid=" + nid + " data=" + data);		
	}
	
	protected void onSelect (int nid, TYPE data) {
		System.out.println("onSelect nid=" + nid + " data=" + data);
	}
	
	private void reset() {
		_node_list.removeAllElements();
		_first_slot_index = 0;
		_current_node = getRoot();
		_current_node.setExpand(true);
	}
	
	private TreeViewNode getRoot() {
		if(_node_list.size() == 0) {
			return getNewNode(null);
		}else {
			return _node_list.elementAt(0);
		}
	}

	private TreeViewNode getNewNode(TYPE data) {
		TreeViewNode node = null;
		if(_first_slot_index >= _node_list.size()) {
			node = new TreeViewNode(_first_slot_index, data);
			_node_list.addElement(node);
		}else {
			node = _node_list.elementAt(_first_slot_index);
			node.revive(_first_slot_index, data);
		}
		_first_slot_index++;
		while(_first_slot_index < _node_list.size() && _node_list.elementAt(_first_slot_index).isValid()) {
			_first_slot_index++;
		}
		// System.out.println("getNewNode: " + node.toString() + " next slot: " + _first_slot_index);
		return node;
	}
	
	private TreeViewNode getNode(int nid) {
		if(nid >= 0 && nid < _node_list.size()) {
			TreeViewNode node = _node_list.elementAt(nid);
			// System.out.println("getNode: " + node.toString());
			if(node.isValid()) {
				return node;
			}
		}
		// System.out.println("cannot getNode for " + nid);
		return null;
	}

	public int addChildNode(int pid, TYPE cookie, boolean request_redraw) {
		TreeViewNode parent = getNode(pid);
		if(parent != null) {
			TreeViewNode child = getNewNode(cookie);
			parent.addChild(child);
			if(request_redraw) {
				requestLayout();
			}
			if(_current_node == getRoot()) {
				_current_node = child;
			}
			System.out.println("addChildNode: " + child.toString());
			return child._node_id;
		} else {
			System.out.println("addChildNode: cannot find parent by " + pid);
			return INVALID_NID;
		}
	}
	
	public int addSiblingNode(int sid, TYPE cookie, boolean request_redraw) {
		TreeViewNode sibling = getNode(sid);
		if(sibling != null && sibling._parent != null) {
			TreeViewNode sibling2 = getNewNode(cookie);
			sibling._parent.addChild(sibling2);
			if(request_redraw) {
				postInvalidate();
			}
			return sibling2._node_id;
		} else {
			return INVALID_NID;
		}
	}
	
	public void deleteAll() {
		reset();
		postInvalidate();
	}

	public void deleteSubtree(int nid, boolean request_redraw) {
		TreeViewNode node = getNode(nid);
		if(node != null) {
			int rid = node.release();
			if(_first_slot_index > rid) {
				_first_slot_index = rid;
			}
			if(request_redraw) {
				postInvalidate();
			}
		}
	}

	public TYPE getCookie(int nid) {
		TreeViewNode node = getNode(nid);
		if(node != null) {
			return node._data;
		}else {
			return null;
		}
	}
	
	public boolean getExpanded(int nid) {
		TreeViewNode node = getNode(nid);
		if(node != null) {
			return node._expand;
		}else {
			return false;
		}
	}
	
	public int getCurrentNode() {
		if(_current_node != null) {
			return _current_node._node_id;
		}else {
			return INVALID_NID;
		}
	}
	
	public boolean isValid(int nid) {
		TreeViewNode node = getNode(nid);
		if(node != null) {
			return true;
		}else {
			return false;
		}
	}
	
	public int getNextSibling(int nid) {
		TreeViewNode node = getNode(nid);
		TreeViewNode sib = getNextSibling(node);
		if(sib != null) {
			return sib._node_id;
		}
		return INVALID_NID;
	}
	
	private TreeViewNode getNextSibling(TreeViewNode node) {
		if(node != null && node._parent != null) {
			int index = node._parent.getChildIndex(node);
			return node._parent.getChildAt(index+1);
		}
		return null;
	}

	public int getParent(int nid) {
		TreeViewNode node = getNode(nid);
		if(node != null && node._parent != null) {
			return node._parent._node_id;
		}else {
			return INVALID_NID;
		}
	}

	public int getPreviousSibling(int nid) {
		TreeViewNode node = getNode(nid);
		TreeViewNode sib = getPreviousSibling(node);
		if(sib != null) {
			return sib._node_id;
		}
		return INVALID_NID;
	}
	
	private TreeViewNode getPreviousSibling(TreeViewNode node) {
		if(node != null && node._parent != null) {
			int index = node._parent.getChildIndex(node);
			return node._parent.getChildAt(index-1);
		}
		return null;
	}
	
	public int getFirstChild(int pid) {
		TreeViewNode node = getNode(pid);
		if(node != null) {
			TreeViewNode child = node.getFirstChild();
			if(child != null) {
				return child._node_id;
			}
		}
		return INVALID_NID;
	}
	
	public int getLastChild(int pid) {
		TreeViewNode node = getNode(pid);
		if(node != null) {
			TreeViewNode child = node.getLastChild();
			if(child != null) {
				return child._node_id;
			}
		}
		return INVALID_NID;
	}
	
	public float getRowHeight() {
		return _row_height;
	}
	
	public float getIndentWidth() {
		return _indent;
	}
	
	public boolean getVisible(int nid) {
		TreeViewNode node = getNode(nid);
		if(node != null) {
			return node.isVisible();
		}else {
			return false;
		}
	}
	
	public void setCurrentNode(int nid) {
		TreeViewNode node = getNode(nid);
		if(node != null) {
			_current_node = node;
			_current_node.setVisible();
			requestLayout();
		}
	}
	
	public void setExpanded(int nid, boolean expanded) {
		TreeViewNode node = getNode(nid);
		if(node != null) {
			node.setVisible();
			node.setExpand(expanded);
			requestLayout();
		}
	}
		
	public void setIndentWidth(int indent) {
		_indent = indent;
		invalidate();
	}
	
	public void setRowHeight(float rowHeight) {
		if(rowHeight > _fold_icon_height) {
			_row_height = rowHeight;
			requestLayout();
		}
	}
	
	private TreeViewNode getNextNodeInTree(TreeViewNode node) {
		TreeViewNode sib = null;
		if(node.hasVisibleChildren()) {
			return node.getFirstChild();
		}
		while((sib = getNextSibling(node)) == null && node != getRoot()) {
			node = node._parent;
		}
		return sib;
	}
	
	private TreeViewNode getLastVisibleChild(TreeViewNode node) {
		if(node.hasVisibleChildren()) {
			return node.getLastChild();
		}else {
			return null;
		}
	}
	
	private TreeViewNode getPreviousNodeInTree(TreeViewNode node) {
		TreeViewNode sib = getPreviousSibling(node);
		if(sib == null) {
			if(node._parent != getRoot()) {
				return node._parent;
			}else {
				return null;
			}
		}else {
			while(sib != null) {
				node = getLastVisibleChild(sib);
				if(node == null) {
					break;
				}else {
					sib = node;
				}
			}
			return sib;
		}
	}
	
	private void invalidateNode(TreeViewNode node) {
		super.invalidate((int)node._left, (int)node._top, (int)(node._left + node._width), (int)(node._top + _row_height));
	}
	
	protected static final int INVALID_NID = -1;
	
	private class TreeViewNode {
		private int _node_id;
		private TreeViewNode _parent;
		private Vector<TreeViewNode> _children;
		private TYPE _data;
		private boolean _expand;
		private int _level;
		private float _left;
		private float _top;
		private float _width;
		
		TreeViewNode(int index, TYPE data) {
			_node_id = index;
			_children = null;
			_data = data;
			_parent = null;
			_expand = _default_expand;
			_level = -1;
			_left = -1;
			_top = -1;
			_width = -1;
		}
		
		void addChild(TreeViewNode child) {
			if(_children == null) {
				_children = new Vector<TreeViewNode>(); 
			}
			_children.addElement(child);
			child._parent = this;
			child._level = _level+1;
		}
		
		void setExpand(boolean expand) {
			_expand = expand;
		}
		
		boolean hasVisibleChildren() {
			if(_expand && _children != null && _children.size() > 0) {
				return true;
			}else {
				return false;
			}
		}
		
		int release() {
			int release_id = _node_id;
			if(_children != null) {
				while(_children.size() > 0) {
					int child_rid = _children.elementAt(0).release();
					if(release_id > child_rid) {
						release_id = child_rid;
					}
				}
				_children = null;
			}
			int index = _parent.getChildIndex(this);
			_parent._children.removeElementAt(index);
			_parent = null;
			_data = null;
			_node_id = INVALID_NID;
			_level = -1;
			_left = -1;
			_top = -1;
			_width = -1;
			return release_id;
		}
		
		boolean isValid() {
			if(_node_id == INVALID_NID) {
				return false;
			}else {
				return true;
			}
		}
		
		void revive(int index, TYPE data) {
			_node_id = index;
			_data = data;
		}
		
		int getChildIndex(TreeViewNode child) {
			if(_children != null && child._parent == this) {
				for(int i = 0; i < _children.size(); i ++) {
					if(_children.elementAt(i) == child) {
						return i;
					}
				}
			}
			return -1;
		}
		
		TreeViewNode getChildAt(int index) {
			if(_children != null && index>= 0 && index < _children.size()) {
				return _children.elementAt(index);
			}else {
				return null;
			}
		}
		
		TreeViewNode getFirstChild() {
			if(_children != null && _children.size() > 0) {
				return _children.firstElement();
			}else {
				return null;
			}
		}
		
		TreeViewNode getLastChild() {
			if(_children != null && _children.size() > 0) {
				return _children.lastElement();
			}else {
				return null;
			}
		}
		
		boolean itemContainsPoint(float x, float y) {
			if(_left >= 0 && _top >= 0 
					&& y >= _top && y < _top + _row_height && 
					(_children == null || _children.size() == 0
						|| (x >= _left + _fold_icon_width && x < _left + _width))
					) {
				return true;
			}else {
				return false;
			}
		}
		
		boolean containsPoint(float x, float y) {
			if(_top >= 0 && y >= _top && y < _top + _row_height) {
				return true;
			}else {
				return false;
			}
		}
		
		boolean subTreeContainsPoint(float x, float y) {
			if(_top >= 0 && y >= _top && y < _top + (1+getVisibleDescendentCount())*_row_height) {
				return true;
			}else {
				return false;
			}
		}
		
		TreeViewNode getChildNodeAtPoint(float x, float y) {
			if(_children != null && _children.size() > 0) {
				for(int i = 0; i < _children.size(); i ++) {
					TreeViewNode node = _children.elementAt(i);
					if(node.subTreeContainsPoint(x, y)) {
						if(node.containsPoint(x, y)) {
							return node;
						}else {
							return _children.elementAt(i).getChildNodeAtPoint(x, y);
						}
					}
				}
			}
			return null;
		}
		
		boolean isVisible() {
			TreeViewNode parent = _parent;
			while(parent != null) {
				if(!parent._expand) {
					return false;
				}
				parent = parent._parent;
			}
			return true;
		}
		
		void setVisible() {
			TreeViewNode parent = _parent;
			while(parent != null) {
				parent._expand = true;
				parent = parent._parent;
			}
		}
		
		int getVisibleDescendentCount() {
			int c = 0;
			for(int i = 0; _children != null && _expand && i < _children.size(); i ++) {
				c += 1 + _children.elementAt(i).getVisibleDescendentCount();
			}
			return c;
		}
				
		void drawChildren(Canvas canvas, float left, float top, float width) {
			if(!_expand) {
				return;
			}
			for(int i = 0; _children != null && i < _children.size(); i ++) {
				TreeViewNode child = _children.elementAt(i);
				child.draw(canvas, left, top, width);
				top += (1 + child.getVisibleDescendentCount())*_row_height;
			}
		}
		
		void draw(Canvas canvas, float left, float top, float width) {
			_left = left;
			_top = top;
			_width = width;
			
			boolean focus = false;
			boolean touch = false;
			if(_current_node == this) {
				focus = true;
			}
			if(_node_in_touch == this) {
				touch = true;
			}
			if(_children != null && _children.size() > 0) {
				String icon_name = null;
				if(touch) {
					if(_expand) {
						icon_name = UNFOLD_FOCUS_ICON;
					}else {
						icon_name = FOLD_FOCUS_ICON;						
					}
				}else {
					if(_expand) {
						icon_name = UNFOLD_ICON;
					}else {
						icon_name = FOLD_ICON;
					}
				}
				Bitmap fold_icon = BitmapRepository.getBitmapByName(getContext(), icon_name);
				canvas.drawBitmap(fold_icon, left + (_fold_icon_width - fold_icon.getWidth())/2, 
						top + (_row_height - fold_icon.getHeight())/2, null);
			}
			if(_callback != null) {
				_callback.drawTreeItem(canvas, _node_id, left + _fold_icon_width, top, width - _fold_icon_width, _row_height, _data, focus);
			}
			drawChildren(canvas, left + _indent, top + _row_height, width - _indent);
		}
		
		public String toString() {
			String str = "";
			if(_expand) {
				str += "[-]";
			}else {
				str += "[+]";
			}
			str += "(" + _level + ")";
			str += "nid:" + _node_id;
			if(_parent != null) {
				str += " pid:" + _parent._node_id;
			}
			if(_children != null && _children.size() > 0) {
				str += " child:" + _children.size();
			}
			if(_data != null) {
				str += " " + _data + "@" + _data.getClass().toString();
			}
			return str;
		}
		
	}
}
