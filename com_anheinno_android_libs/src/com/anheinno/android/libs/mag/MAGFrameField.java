package com.anheinno.android.libs.mag;

import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;

import com.anheinno.android.libs.JSONBrowserLink;
import com.anheinno.android.libs.graphics.PaintRepository;
import com.anheinno.android.libs.ui.Manager;

public class MAGFrameField extends Manager implements MAGDocumentContainerFieldInterface, MAGContainerLayoutInterface {
	
	private MAGDocumentField _field;
	private MAGFrame _component;
	private Vector<MAGLinkableComponent> _active_links;
	
	public MAGFrameField(Context context, MAGFrame component) {
		super(context);
		_component = component;
		_active_links = null;
		
		_field = new MAGDocumentField(context, this);
		addView(_field);
	}

	@Override
	protected void layoutChildren(int l, int t, int width, int height) {
		_field.layout(0, 0, _field.getMeasuredWidth(), _field.getMeasuredHeight());
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		_field.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), 
				MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
		setMeasuredDimension(_field.getMeasuredWidth(), _field.getMeasuredHeight());
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawText("MAGFrameField",0, 0, PaintRepository.getDefaultPaint(getContext()));
		super.onDraw(canvas);
	}

	public MAGContainerInterface getMAGContainer() {
		return _component;
	}

	public void unload() {
		if(_active_links != null) {
			_active_links.removeAllElements();
			_active_links = null;
		}
		_field.unload();
	}

	public void reset() {
		
	}

	public void invalidateMAGComponent(MAGComponentInterface comp) {
		_field.postInvalidate();
	}

	//public MAGDocumentField getMAGDocumentField() {
		//System.out.println("return _field " + _field);
	//	return _field;
	//}
	
	protected void registerLinks(MAGLinkableComponent comp) {
		if(_active_links == null) {
			_active_links = new Vector<MAGLinkableComponent>();
		}
		if(!_active_links.contains(comp)) {
			_active_links.add(comp);
			if(_field.getURL() != null && _field.getURL().equals(comp._link.getURL())) {
				checkMAGLinks(comp._link);
			}
		}
	}
	
	private void checkMAGLinks(JSONBrowserLink link) {
		if(_active_links != null) {
			System.out.println(_component + " active links: " + _active_links.size());
			for(int i = 0; i < _active_links.size(); i ++) {
				MAGLinkableComponent comp = _active_links.elementAt(i);
				System.out.println("link url: " + comp._link.getURL());
				// System.out.println("frame url: " + link.getURL());
				
				if(comp._link.getURL().equals(link.getURL())) {
					System.out.println("find the link!!!");
					comp.setChecked();
				}else {
					System.out.println("NOT find the link????");
					if(comp instanceof MAGLink) {
						((MAGLink)comp).setUnChecked();
					}else {
						comp.setUnChecked();
					}
					
				}
			}
		}
	}
	
	public void open(JSONBrowserLink link, boolean refresh, Object params) {
		checkMAGLinks(link);
		_field.open(link, refresh, params);
	}

	public synchronized void releaseResources() {
		removeView(_field);
		_field = null;
		
		if(_active_links != null) {
			_active_links.removeAllElements();
			_active_links = null;
		}
		
		_component = null;
	}
}
