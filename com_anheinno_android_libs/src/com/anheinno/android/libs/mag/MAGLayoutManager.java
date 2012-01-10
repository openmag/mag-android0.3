package com.anheinno.android.libs.mag;

import android.content.Context;
import android.graphics.Canvas;
import android.view.ViewParent;

import com.anheinno.android.libs.JSONBrowserField;
import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.ui.Manager;

public class MAGLayoutManager extends Manager implements MAGContainerLayoutInterface{
	private MAGContainerInterface _container;
	private MAGLayout _layout;
	private int _width;
	private boolean _add_complete;

	public MAGLayoutManager(Context context) {
		super(context);
		_init();
	}
	
	private final void _init() {		
		_layout = null;
		_width = 0;
		_container = null;
	}
	
	public void setContainer(MAGContainerInterface container) {
			//if(_container != null) {
			//	_container.setField(null);
			//}
		
			releaseResources();
			
			if(container != null) {
				_container = container;
				for(int i = 0; i < container.childrenNum(); i ++) {
					MAGComponentInterface child = container.getChild(i);
					if(child.visible() && child.getField() != null) {
						//System.out.println("Add child " + child.toString() + " field=" + child.getField());			
						addView(child.getField());
					}
				}
				_add_complete = true;
				//_container.setLayoutManager(this);
				//System.out.println("layout completed!");
				//requestLayout();
				postInvalidate();
			}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(_container == null ||!_add_complete) {
			super.setMeasuredDimension(0, 0);
			return;
		}

		synchronized(this) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);
			
			if(getJSONBrowserField() != null) {
				//width = getJSONBrowserField().getVisibleWidth();
				height = getJSONBrowserField().getVisibleHeight();
			}
			
			if(width != _width) {
				_width = width;
				_layout = null;
			}
			
			if (_layout == null) {
				_initLayout(width, height);
			}
			_updateLayout(width, height);
			
			super.setMeasuredDimension(_width, getPreferredHeight());
		}
	}
	
	private JSONBrowserField getJSONBrowserField() {
		ViewParent parent = this;
		while((parent = parent.getParent()) != null) {
			if(parent instanceof JSONBrowserField) {
				return (JSONBrowserField)parent;
			}
		}
		return null;
	}
	
	private void _initLayout(int width, int height) {
		//System.out.println("MAGLayoutManager: width=" + width + " height=" + height);
		_layout = new MAGLayout(width, height);
		int num = _container.childrenNum();
		for (int i = 0; i < num; i++) {
			MAGComponentInterface child = _container.getChild(i);
			_layout.add(child);
		}
	}

	private void _updateLayout(int width, int height) {
		int num = _layout.getLineCount();
		for (int r = 0; r < num; r++) {
			for (int c = 0; c < _layout.getMAGComponentCount(r); c++) {
				MAGComponentInterface child = _layout.getComponent(r, c);
				if (child.getField() != null) {
					int w_mode = MeasureSpec.AT_MOST;
					int h_mode = MeasureSpec.UNSPECIFIED;
					
					//System.out.println("full_screen_height=" + full_screen_height);
					
					int h = _layout.getPreferredFieldHeight(r, c);
					int w = child.getInnerWidth();
					
					if(child instanceof MAGContainerInterface) {
						w_mode = MeasureSpec.EXACTLY;
					}
					if(h > 0) {
						h_mode = MeasureSpec.EXACTLY;
					}
					/*if(child.style().getIWidth(w) > 0) {
						w = child.style().getIWidth(w);
						w_mode = MeasureSpec.EXACTLY;
					}
					if(child.style().getIHeight(h) > 0) {
						h = child.style().getIHeight(h);
						h_mode = MeasureSpec.EXACTLY;
					}*/
					//System.out.println(child + " m_width=" + w + " m_height=" + h);
					child.getField().measure(MeasureSpec.makeMeasureSpec(w, w_mode),
							MeasureSpec.makeMeasureSpec(h, h_mode));
					
				}
			}
			_layout.updateLineHeight(r);
		}
	}

	@Override
	protected void layoutChildren(int ll, int tt, int w, int h) {
		if(_layout == null || !_add_complete) {
			return;
		}
		synchronized(this) {
			//LOG.error(this, "layoutChildren", null);

			int top = 0;

			for (int i = 0; i < _layout.getLineCount(); i++) {
				for (int j = 0; j < _layout.getMAGComponentCount(i); j++) {
					MAGComponentInterface comp = _layout.getComponent(i, j);
					if (comp.getField() != null) {
						//System.out.println("comp=" + comp.toString() + " left=" + _layout.getCompLeft(i, j) + " top=" + _layout.getCompTop(i, j));
						
						comp.setLeft(_layout.getLineWidth(i, j));
						comp.setTop(top);
						
						int l = _layout.getCompLeft(i, j);
						int t = _layout.getCompTop(i, j) + top;
						int r = l + comp.getField().getMeasuredWidth();
						int b = t + comp.getField().getMeasuredHeight();
						
						//System.out.println("comp=" + comp.toString() + " l=" + l + " t=" + t + " r=" + r + " b=" + b);
						comp.getField().layout(l, t, r, b);
					}
				}
				top += _layout.getLineHeight(i);
			}
		}
		
	}

	/*public int getPreferredWidth() {
		if(_width == 0) {
			return GraphicUtilityClass.getDisplayWidth(getContext());
		}else {
			return _width;
		}
	}*/

	public int getPreferredHeight() {
		int h = 0;

		if(_container != null) {
			for (int i = 0; i < _layout.getLineCount(); i++) {
				h += _layout.getLineHeight(i);
			}
		}
		
		return h;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//System.out.println("MAGLayoutManager: onDraw invoked");
		
		if(_container == null || !_add_complete || _layout == null) {
			//System.out.println("MAGLayoutManager: default onDraw invoked");
			super.onDraw(canvas);
			return;
		}

		synchronized(this) {
			for (int i = 0; i < _layout.getLineCount(); i++) {
				for (int j = 0; j < _layout.getMAGComponentCount(i); j++) {
					MAGComponentInterface comp = _layout.getComponent(i, j);
					if(comp.getField() != null) {
						comp.drawBackground(canvas, comp.getLeft(), comp.getTop(), comp.getWidth(), comp.getHeight());
					}
				}
			}
	
			super.onDraw(canvas);
		}
	}

	public void invalidateMAGComponent(MAGComponentInterface comp) {
		invalidate(comp.getLeft(), comp.getTop(), comp.getLeft() + comp.getWidth(), comp.getTop() + comp.getHeight());
	}
	
	public void releaseResources() {
		_layout = null;
		_width = 0;
		_container = null;
		_add_complete = false;

		if(getChildCount() > 0) {
			try {
				//System.out.println("remove all fields (" + getChildCount() + ") ...");
				removeAllViews();
			} catch (final Exception e) {
				LOG.error(this, "deleteAll", null);
			}
			
		}
	}
	
}
