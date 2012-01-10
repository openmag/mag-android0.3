package com.anheinno.android.libs.ui;

import com.anheinno.android.libs.R;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.GraphicUtilityClass;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public class ScreenMenuBar extends Manager implements View.OnClickListener {
	private int _max_show_item_count;
	private ScreenMenu _menu;
	private CustomButtonField[] _buttons;
	private boolean _landscape_mode;
	private BackgroundDescriptor _bg_desc;
	
	private static BackgroundDescriptor DEFAULT_MENU_BAR_BACKGROUND;
	static {
		DEFAULT_MENU_BAR_BACKGROUND = new BackgroundDescriptor("image=menubar_background.png duplicate=horizontal image-adjust=vertical");
	}
	private static BackgroundDescriptor DEFAULT_MENU_BAR_BACKGROUND_VERTICAL;
	static {
		DEFAULT_MENU_BAR_BACKGROUND_VERTICAL = new BackgroundDescriptor("image=menubar_background_vertical.png duplicate=vertical image-adjust=horizontal");
	}
	
	public ScreenMenuBar(Context context) {
		super(context);
		_max_show_item_count = 0;
		_menu = null;
		_buttons = null;
		//_horizontal = false;
		_bg_desc = DEFAULT_MENU_BAR_BACKGROUND;
	}
	
	public void resetMenu(ScreenMenu menu) {
		this.resetMenu(menu, _max_show_item_count);
	}
	
	public void setLandscapeMode(boolean landscape) {
		if(landscape != _landscape_mode) {
			_landscape_mode = landscape;
		}
		if(_landscape_mode) {
			_bg_desc = DEFAULT_MENU_BAR_BACKGROUND_VERTICAL;
		}else {
			_bg_desc = DEFAULT_MENU_BAR_BACKGROUND;
		}
	}
	
	private boolean needShowMore() {
		if(_max_show_item_count > _menu.count()) {
			return false;
		}else {
			return true;
		}
	}

	private static final String BASE_TEXT_STYLE       = "padding=5 font-scale=0.5 icon-position=top text-color=#BBBBBB text-align=center line-limit=1";
	private static final String BASE_FOCUS_TEXT_STYLE = "padding=5 font-scale=0.5 icon-position=top text-color=#FFFFFF text-align=center line-limit=1";

	public void resetMenu(ScreenMenu menu, int show_count) {
		synchronized(this) {
			removeAllViews();
			
			_menu = menu;
			_max_show_item_count = show_count;
			
			int max_count = _max_show_item_count;
			if(max_count > menu.count()) {
				max_count = menu.count();
			}
			_buttons = new CustomButtonField[max_count + (needShowMore()? 1:0)];
			System.out.println("resetMenu: max_count=" + max_count);
			for(int i = 0; i < max_count; i ++) {
				ScreenMenuItem item = menu.getItem(i);
				_buttons[i] = new CustomButtonField(getContext(), item.getTitle(), this);
				String text_style       = BASE_TEXT_STYLE;
				String focus_text_style = BASE_FOCUS_TEXT_STYLE;
				if(item.getBarIconName() != null && item.getBarIconName().length() > 0) {
					text_style += " icon=" + item.getBarIconName();
				}
				_buttons[i].setTextStyle(new TextStyleDescriptor(text_style));
				if(item.getFocusBarIconName() != null && item.getFocusBarIconName().length() > 0) {
					focus_text_style += " icon=" + item.getFocusBarIconName();
				}else if(item.getBarIconName() != null && item.getBarIconName().length() > 0) {
					focus_text_style += " icon=" + item.getBarIconName();
				}
				_buttons[i].setFocusTextStyle(new TextStyleDescriptor(focus_text_style));
				addView(_buttons[i]);
			}
			if(needShowMore()) {
				_buttons[max_count] = new CustomButtonField(getContext(), getContext().getString(R.string.screen_menu_bar_item_more), this);
				String text_style       = BASE_TEXT_STYLE + " icon=menubar_icon_more.png";
				String focus_text_style = BASE_FOCUS_TEXT_STYLE + " icon=menubar_icon_more.png";
				_buttons[max_count].setTextStyle(new TextStyleDescriptor(text_style));
				_buttons[max_count].setFocusTextStyle(new TextStyleDescriptor(focus_text_style));
				addView(_buttons[max_count]);
			}
			
		}
	}

	protected void layoutChildren(int l, int t, int width, int height) {
		synchronized(this) {
			if(_buttons != null && _buttons.length > 0) {
				int h_interval = 0;
				int v_interval = 0;
				if(_landscape_mode) {
					v_interval = getMeasuredHeight()/_buttons.length;
				}else {
					h_interval = getMeasuredWidth()/_buttons.length;
				}
				//System.out.println("ScreenMenuBar: layoutChildren _buttons length=" + _buttons.length);
				int left, top;
				for(int i = _buttons.length - 1; i >= 0; i --) {
					if(_landscape_mode) {
						left = (getMeasuredWidth() - _buttons[i].getMeasuredWidth())/2;
						top = v_interval*i + (v_interval - _buttons[i].getMeasuredHeight())/2;
					}else {
						left = h_interval*i + (h_interval - _buttons[i].getMeasuredWidth())/2;
						top = (getMeasuredHeight() - _buttons[i].getMeasuredHeight())/2;
					}
					_buttons[i].layout(left, top, left + _buttons[i].getMeasuredWidth(), top + _buttons[i].getMeasuredHeight());
				}
			}
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		synchronized(this) {
			//System.out.println("ScreenMenuBar: onMeasure _buttons length=" + _buttons.length);
			if(_buttons != null && _buttons.length > 0) {
				int h_interval = 0;
				int v_interval = 0;
				if(_landscape_mode) {
					v_interval = height/_buttons.length;
				}else {
					h_interval = width/_buttons.length;
				}
				int max_size = 0;
				for(int i = 0; i < _buttons.length; i ++) {
					if(_landscape_mode) {
						_buttons[i].measure(MeasureSpec.makeMeasureSpec(GraphicUtilityClass.getMinTouchWidth(getContext()), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(v_interval, MeasureSpec.AT_MOST));
						if(max_size < _buttons[i].getMeasuredWidth()) {
							max_size = _buttons[i].getMeasuredWidth();
						}
					}else {
						_buttons[i].measure(MeasureSpec.makeMeasureSpec(h_interval, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
						if(max_size < _buttons[i].getMeasuredHeight()) {
							max_size = _buttons[i].getMeasuredHeight();
						}
					}
				}
				if(_landscape_mode) {
					super.setMeasuredDimension(max_size, height);
				}else {
					super.setMeasuredDimension(width, max_size);
				}
			}else {
				super.setMeasuredDimension(0, 0);
			}
		}
	}

	public void onClick(View v) {
		for(int i = 0; _buttons != null && i < _buttons.length; i ++) {
			if(v == _buttons[i]) {
				if(i == _buttons.length - 1 && needShowMore()) {
					((UiApplication)getContext()).openOptionsMenu();
				}else {
					if(_menu != null) {
						_menu.getItem(i).run();
					}
				}
			}
		}
	}
	
	public static int getMenuBarWidth(Context context) {
		if(GraphicUtilityClass.isLandscape(context)) {
			return GraphicUtilityClass.getMinTouchWidth(context);
		}else {
			return GraphicUtilityClass.getDisplayWidth(context);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(_bg_desc != null) {
			_bg_desc.draw(getContext(), canvas, 0, 0, getMeasuredWidth(), getMeasuredHeight());
		}
		super.onDraw(canvas);
	}
	
	public void releaseResources() {
		_menu = null;
		removeAllViews();
		_buttons = null;
		_bg_desc = null;
	}

}
