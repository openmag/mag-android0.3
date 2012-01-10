package com.anheinno.android.libs.ui;

import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.GraphicUtilityClass;
//import com.anheinno.android.libs.graphics.ProgressBarDrawArea;
//import com.anheinno.android.libs.graphics.ProgressBarDrawAreaUpdateInterface;
import com.anheinno.android.libs.graphics.TextDrawArea;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import android.content.Context;
import android.graphics.Canvas;
//import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.ScrollView;

/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public abstract class CustomTitleMainScreen extends FullScreen implements DelayedLayoutChangeUI, Runnable {
	
	private ViewGroup _content_view;
	private ScreenTitleView _title_view;
	private TextDrawArea _status_area;
	private BackgroundDescriptor _status_bg_color;
	private BackgroundDescriptor _default_bg_image;
	
	//private BackgroundDescriptor _bg_color;
	//private ProgressBarDrawArea _progress_bar;

	private static TextStyleDescriptor DEFAULT_STATUS_TEXT_STYLE;
	
	static {
		DEFAULT_STATUS_TEXT_STYLE = new TextStyleDescriptor("color=#000066 font-scale=90% text-align=center text-valign=middle use-full-width=true");
	}

	public CustomTitleMainScreen(Context context) {
		super(context);
		
		_title_view = null;
		_status_area = null;
		_status_bg_color = null;

		// _bg_color = null;

		//_content_view = new ScrollView(getContext()) {
			
			/*@Override
			protected void onMeasure(int maxWidth, int maxHeight) {
				int width = GraphicUtilityClass.getDisplayWidth(getContext());
				int height = GraphicUtilityClass.getDisplayHeight(getContext());
				
				if(_title_view != null && _title_view.getText() != null && _title_view.getText().length() > 0) {
					height -= _title_view.getMeasuredHeight();
				}
				float bottom_space = 0;
				if(_status_area != null && _status_area.getText() != null && _status_area.getText().length() > 0) {
					bottom_space = _status_area.getHeight();
				}
				if(_progress_bar != null && bottom_space < _progress_bar.getHeight()) {
					bottom_space = _progress_bar.getHeight();
				}
				height -= bottom_space;
				
				super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), 
						MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
				
				//Log.d("MainScreen", "CustomTitleMainScreen: width=" + width + " height=" + height);
				
				setMeasuredDimension(width, height);
			}*/
			
			/*protected boolean keyChar(char ch, int status, int time) {
				if (status == KeypadListener.STATUS_ALT) {
					char ch2 = Keypad.getUnaltedChar(ch);
					if (ch2 == 'T') { // ALT+t
						// to top
						scroll(Manager.TOPMOST);
						return true;
					} else if (ch2 == 'B') { // ALT + b
						// to bottom
						scroll(Manager.BOTTOMMOST);
						return true;
					} else if (ch2 == ' ') { // ALT + SPACE
						scroll(Manager.DOWNWARD);
						return true;
					}
				}
				return super.keyChar(ch, status, time);
			}*/
			
		//};
		
		//_content_view.setBackgroundColor(Color.TRANSPARENT);
		//_content_view.setWillNotDraw(false);
		//_content_view.setVerticalFadingEdgeEnabled(false);
		//_content_view.setScrollbarFadingEnabled(true);
		//super.addView(getContentView());

		//_progress_bar = new ProgressBarDrawArea(getContext(), GraphicUtilityClass.getDisplayWidth(getContext()));
	}
	

	protected final void setContentView(ViewGroup content_view) {
		if(_content_view != null) {
			super.removeView(_content_view);
			_content_view = null;
		}
		_content_view = content_view;
		super.addView(_content_view);
	}
	
	protected final ViewGroup getContentView() {
		return _content_view;
	}
		
	protected void subLayoutChildren(int l, int t, int width, int height) {
		int top = t;
		int bottom = 0;
		
		if(_title_view != null && _title_view.getText() != null && _title_view.getText().length() > 0) {
			_title_view.layout(0, top, width, top + _title_view.getMeasuredHeight());
			top += _title_view.getMeasuredHeight();
		}
		
		if(_status_area != null && _status_area.getText() != null && _status_area.getText().length() > 0) {
			bottom = (int)_status_area.getHeight();
		}
		
		//if(_progress_bar != null && bottom < _progress_bar.getHeight()) {
		//	bottom = _progress_bar.getHeight();
		//}

		if(_content_view != null) {
			_content_view.layout(l, top, width, height - bottom);
		}
	}
	
	@Override
	protected void measureChildren (int width, int height) {
		int top = 0;
		int bottom = 0;
		
		if(_title_view != null && _title_view.getText() != null && _title_view.getText().length() > 0) {
			_title_view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), 
				MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
			//Log.d("onMeasure", "title measureHeight=" + _title_view.getMeasuredHeight() + " height=" + _title_view.getHeight());
			top = _title_view.getMeasuredHeight();
		}
		
		if(_status_area != null && _status_area.getText() != null && _status_area.getText().length() > 0) {
			_status_area.setWidth(width);
			bottom = (int)_status_area.getHeight();
		}
		//if(_progress_bar != null && bottom < _progress_bar.getHeight()) {
		//	bottom = _progress_bar.getHeight();
		//}
		
		height -= top + bottom;
		
		//Log.v("MainScreen::onMeasure", "height=" + height);
		
		if(_content_view != null) {
			_content_view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), 
				MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//System.out.println("CustomTitleMainScreen::onDraw is invoked!");
		int width  = getClientWidth();
		int height = getClientHeight();
		
		int top = 0;
		int bg_height = height;

		if(_default_bg_image != null) {
			if(_title_view != null && _title_view.getText() != null && _title_view.getText().length() > 0) {
				top = _title_view.getHeight();
				bg_height -= top;
			}
			_default_bg_image.draw(getContext(), canvas, 0, top, width, bg_height);
		}
		
		super.onDraw(canvas);
		
		if(_status_area != null && _status_area.getText() != null && _status_area.getText().length() > 0) {
			top = (int)(height - _status_area.getHeight());
			if(_status_bg_color != null) {
				_status_bg_color.draw(getContext(), canvas, 0, top, width, (int)_status_area.getHeight());
			}
			_status_area.draw(canvas, (getMeasuredWidth() - _status_area.getWidth())/2, top);
		}
		
		//_progress_bar.draw(canvas, 0, GraphicUtilityClass.getDisplayHeight(getContext()) - _progress_bar.getHeight());
	}
	
	private void newTitle() {
		if(_title_view == null) {
			_title_view = new ScreenTitleView(getContext());
			super.addView(_title_view, 0);
		}else {
			//_title_view.setField(null);
			//_title_view.setText(null);
			//requestLayoutLater();
		}
	}
	
	private void cleanTitle() {
		if(_title_view != null) {
			//if(super.getChildAt(0) == _title_view) {
				super.removeView(_title_view);
				_title_view = null;
			//}
		}
	}
	
	public void setTitleText(String text) {
		if(text != null && text.length() > 0) {
			newTitle();
			_title_view.setText(text);
		}else {
			cleanTitle();
		}
	}
	
	public void setTitleField(View field) {
		if(field != null) {
			newTitle();
			_title_view.setField(field);
		}else {
			cleanTitle();
		}
	}
	
	/**
	 * 
	 * 设置状态栏文字，缺省没有状态栏
	 * 
	 * @param text 状态栏文字
	 */
	public synchronized void setStatusText(String text) {
		if(_status_area == null) {
			_status_area = new TextDrawArea(getContext(), text, DEFAULT_STATUS_TEXT_STYLE);
			_status_area.setWidth(getClientWidth());
		}else {
			_status_area.setText(text);
		}
	}
	
	public void setStatusStyle(TextStyleDescriptor style) {
		if(_status_area != null) {
			_status_area.setStyle(style);
		}
	}
	
	public void removeStatusStyle() {
		setStatusStyle(DEFAULT_STATUS_TEXT_STYLE);
	}
	
	public synchronized boolean removeStatusText() {
		if(_status_area != null && _status_area.getText() != null) {
			_status_area.setText(null);
			_status_area.setStyle(DEFAULT_STATUS_TEXT_STYLE);
			_status_bg_color = null;
			return true;
		}else {
			return false;
		}
	}
	
	public void setStatusBackground(BackgroundDescriptor desc) {
		_status_bg_color = desc;
		if(_status_area != null && _status_area.getText() != null) {
			int height = getClientHeight();
			int width = getClientWidth();
			postInvalidate(0, (int)(height - _status_area.getHeight()), width, height);
		}
	}
	
	public void removeStatusBackground() {
		setStatusBackground(null);
	}
	
	public void setTitleTextStyle(TextStyleDescriptor style) {
		if(_title_view != null) {
			_title_view.setTextStyle(style);
			requestLayoutLater();
		}
	}

	public void setTitleBackground(BackgroundDescriptor desc) {
		if(_title_view != null) {
			_title_view.setBackground(desc);
		}
	}
	
	public void setTitleHeight(int height) {
		if(_title_view != null) {
			_title_view.setPreferredHeight(height);
			requestLayoutLater();
		}
	}
	
	public void setDefaultBGImage(BackgroundDescriptor bg) {
		_default_bg_image = bg;
	}
	
	public BackgroundDescriptor getDefaultBGImage() {
		return _default_bg_image;
	}
	
	/*public void setBackground(BackgroundDescriptor desc) {
		_bg_color = desc;
		postInvalidate();
	}
	public void removeBackground() {
		_bg_color = null;
		postInvalidate();
	}*/
	
	public void resetScreen() {
		//cleanTitle();
		removeStatusText();
		//removeBackground();
		invalidateMenu();
	}
		
	/*protected boolean setFocus(XYRect rect, int vscroll) {
		return _setFocus((Field)_verticalManager, 0, 0, rect, vscroll);
	}
	
	private boolean _setFocus(Field f, int left, int top, XYRect rect, int vscroll) {
		//System.out.println(f.toString() + " left=" + left + " top=" + top);
		XYRect f_rect = new XYRect();
		f.getExtent(f_rect);
		f_rect.x += left;
		f_rect.y += top;
		if(f == _verticalManager) {
			//System.out.println("The field is _verticalManager " + f_rect.height + " height=" + _verticalManager.getHeight() + " visibleHeight=" + _verticalManager.getVisibleHeight() + " verticalScroll=" + _verticalManager.getVerticalScroll() + " virtualHeight=" + _verticalManager.getVirtualHeight());
			//f_rect.height = _verticalManager.getVirtualHeight();
			if(vscroll >= 0 && f_rect.height < vscroll + _verticalManager.getHeight()) {
				f_rect.height = vscroll + _verticalManager.getHeight();
			}
		}
		//System.out.println("Rect: x=" + f_rect.x + " y=" + f_rect.y + " w=" + f_rect.width + " h=" + f_rect.height);
		if(f_rect.contains(rect)) {
			if(f instanceof Manager) {
				//System.out.println("This is a manager!");
				Manager m = (Manager)f;
				for(int i = 0; i < m.getFieldCount(); i ++) {
					Field child = m.getField(i);
					if(_setFocus(child, f_rect.x, f_rect.y, rect, vscroll)) {
						return true;
					}
				}
			}else {
				//System.out.println("This is a field!");
				//if(!f.isFocus()) {
					synchronized(Application.getEventLock()) {
						f.setFocus();
						if(vscroll > 0) {
							_verticalManager.setVerticalScroll(vscroll);
						}
					}
					return true;
				//}
			}
		}
		return false;
	}

	protected XYRect getFocus() {
		int left = 0;
		int top = 0;
		if(_title_field != null) {
			top = _title_field.getHeight();
		}
		XYRect rect = new XYRect();
		Field f = _verticalManager;
		while(f instanceof Manager) {
			f = ((Manager)f).getFieldWithFocus();
			if(f != null) {
				f.getExtent(rect);
				left += rect.x;
				top += rect.y;
			}
		}
		if(f != null) {
			rect.x = left;
			rect.y = top;
			return rect;
		}else {
			return null;
		}
	}*/

	public void commitLayoutChange() {
		requestLayout();
	}
	
	//public final void setContent(View child) {
	//	removeContent();
	//	getContentView().addView(child);
		//_content_view.fullScroll(FOCUS_UP);				
	//}
	
	//private final void removeContent () {
	//	if(getContentView().getChildCount() > 0) {
	//		getContentView().removeAllViews();
	//	}
	//}
	
	/*public void startProgressDeterministic(String msg, int max, int value) {
		_progress_bar.startGauge(msg, 0, max);
		_progress_bar.setValue(value);
		invalidateProgressBarDrawArea();
		requestLayoutLater();
	}
	
	public void startProgressIndeterministic(String msg, int seconds) {
		_progress_bar.startTimer(msg, seconds, this);
		invalidateProgressBarDrawArea();
		requestLayoutLater();
	}
	
	public void invalidateProgressBarDrawArea() {
		// System.out.println("invalidate progress bar draw area....");
		postInvalidate(0, GraphicUtilityClass.getDisplayHeight(getContext()) - _progress_bar.getHeight(), GraphicUtilityClass.getDisplayWidth(getContext()), _progress_bar.getHeight());
	}
	
	public void updateProgress(int val) {
		_progress_bar.setValue(val);
		invalidateProgressBarDrawArea();
	}
	
	public void stopProgress() {
		_progress_bar.stopTimer();
		invalidateProgressBarDrawArea();
		requestLayoutLater();
	}*/

	public void run() {
		requestLayout();
	}

	protected void requestLayoutLater() {
		getUiApplication().invokeLater(this);
	}
	
	
	public int getStatusHeight() {
		if(_status_area != null && _status_area.getText() != null && _status_area.getText().length() > 0) {
			return (int) _status_area.getHeight();
		}else {
			return 0;
		}
	}
	
	protected void onClose() {
		_content_view = null;
		_title_view = null;
		if(_status_area != null) {
			_status_area.releaseResources();
			_status_area = null;
		}
		_status_bg_color = null;
		_default_bg_image = null;
		
		super.onClose();
	}
}
