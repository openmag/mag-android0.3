/*
 * MAGLinkButtonField.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.anheinno.android.libs.JSONObjectCacheDatabase;
import com.anheinno.android.libs.graphics.BitmapRepository;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;

/** 
* @author 安和创新科技（北京）有限公司
* 
* @version 1.0
*/
public class MAGLinkButtonField extends MAGCustomButtonField {
	
	//private MAGLink _mag_link;
	
	private String _state;

	private static Bitmap _newmsg_icon;
	private static TextStyleDescriptor DEFAULT_STYLE_NORMAL;
	private static TextStyleDescriptor DEFAULT_STYLE_VISITED_NORMAL;
	
	static {
		_newmsg_icon = null;
		DEFAULT_STYLE_NORMAL = new TextStyleDescriptor("color=navy font-weight=bold icon-padding=5");
		DEFAULT_STYLE_VISITED_NORMAL = new TextStyleDescriptor("color=navy icon-padding=5");
	}
	
	public MAGLinkButtonField(Context context, MAGLink link, OnClickListener callback) {
		super(context, link, callback);
		if(_newmsg_icon == null) {
			_newmsg_icon = BitmapRepository.getBitmapByName(getContext(), "new_msg.png");
		}
		//_mag_link = link;
		
		if(getMAGLink().getLink().isValidURL()) {
			_state = JSONObjectCacheDatabase.linkState(getContext(), getMAGLink().getLink().getURL());
		}else {
			_state = JSONObjectCacheDatabase.URL_LINK_UNKNOWN;
		}
		//System.out.println("XXXXXXX Link state is " + _state);
		setLinkTextStyle(DEFAULT_STYLE_NORMAL, DEFAULT_STYLE_VISITED_NORMAL);
	}
	
	private MAGLink getMAGLink() {
		return (MAGLink) super.getMAGComponent();
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if(getMAGLink().getLink().isValidURL()) {
			String state = JSONObjectCacheDatabase.linkState(getContext(), getMAGLink().getLink().getURL());
			if (state.equals(JSONObjectCacheDatabase.URL_LINK_UPDATED) && JSONObjectCacheDatabase.needNotify(getContext(), getMAGLink().getLink().getURL())) {
				canvas.drawBitmap(_newmsg_icon, 0, 0, new Paint());
			}
	
			if(!state.equals(_state)) {
				System.out.println("State change .... " + _state + " => " + state);
				_state = state;
				setVisited();
				/*Thread updateThread = new Thread() {
					public void run() {
						_updateStyle();
					}
				};
				updateThread.start();*/
			}

		}
	}

	@Override
	protected boolean isVisited() {
		if(_state.equals(JSONObjectCacheDatabase.URL_LINK_VISITED)) {
			return true;
		}else{
			return false;
		}
	}

	/*private void setCheckable() {
		if(getMAGLink()._link.isValidURL()) {
			//boolean checkable = false;
			MAGDocumentScreen ds = getMAGLink().getMAGDocumentScreen();
			if(ds != null && ds.isURLVisiting(getMAGLink()._link.getURL())) {
				//checkable = true;
				getMAGLink().setCheckable(true);
				getMAGLink().setOnFocus(true, true);
			}
		}
	}*/
	
	/*@Override
	public boolean onTouchEvent(MotionEvent event) {
		setCheckable();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		setCheckable();
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}*/

}
