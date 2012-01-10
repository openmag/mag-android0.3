package com.anheinno.android.libs.ui;

import com.anheinno.android.libs.R;
import com.anheinno.android.libs.graphics.BitmapRepository;
import com.anheinno.android.libs.graphics.GraphicUtilityClass;

import android.R.drawable;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;

public abstract class FullScreen extends Manager {
	private boolean _show_close_menu;
	private boolean _is_cancellable;
	
	private Menu _system_menu;
	private ScreenMenu _screen_menu;
	private boolean _menu_invalidate_flag;
	private ScreenMenuBar _menu_bar;
	
	private boolean _landscape_mode;
		
	public FullScreen(Context context) {
		super(context);
		_show_close_menu = true;
		_is_cancellable = true;
		
		_system_menu = null;
		_screen_menu = new ScreenMenu();
		_menu_invalidate_flag = true;
		_menu_bar = null;
		
		_landscape_mode = false;
		
	}
	
	public void hideCloseMenu() {
		_show_close_menu = false;
		invalidateMenu();
	}
	
	public void setCancellable(boolean cancellable) {
		_is_cancellable = cancellable;
	}

	public final boolean close() {
		return getUiApplication().closeScreen(this);
	}
	
	public final boolean show() {
		return getUiApplication().pushScreen(this);
	}
	
	public final boolean isInShowStack() {
		return getUiApplication().isInShowStack(this);
	}
	
	public final boolean isOnShow() {
		return getUiApplication().isOnShow(this);
	}
	
	public final UiApplication getUiApplication() {
		return (UiApplication)getContext();
	}
	
	private ScreenMenuItem smi_close = new ScreenMenuItem(getContext().getString(R.string.screen_menu_close), ScreenMenu.LESS_USED_MENU_ITEM + 1) {
		protected void initIcons() {
			setMenuIconId(drawable.ic_menu_close_clear_cancel);
			setBarIconName("menubar_icon_close.png");
			setFocusBarIconName("menubar_icon_close_focus.png");
		}
		
		public void run() {
			close();
		}
	};
	
	protected void makeMenu(ScreenMenu menu) {
		if(_show_close_menu) {
			menu.add(smi_close);
		}
	}
	
	protected void onDisplay() {
		if(!GraphicUtilityClass.isNavigation(getContext())) {
			enableMenuBar(4);
		}
	}
	
	protected void onClose() {
		_system_menu = null;
		
		if(_screen_menu != null) {
			_screen_menu.releaseResources();
			_screen_menu = null;
		}
		
		if(_menu_bar != null) {
			_menu_bar.releaseResources();
			_menu_bar = null;
		}
	}
	
	protected void onVisible() {
		invalidateMenu();
	}
	
	protected void onHide() {
	}

	public boolean onKeyUp (KeyEvent event) {
		return false;
	}
	
	public boolean onKeyDown (KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if(_is_cancellable) {
				close();
			}
			System.out.println("FullScreen Back Key is pressed!!");
			return true;
		}
		return false;
	}
	
	public static int getFullScreenWidth(Context context) {
		int width = GraphicUtilityClass.getDisplayWidth(context);
		if(!GraphicUtilityClass.isNavigation(context) && GraphicUtilityClass.isLandscape(context)) {
			width -= ScreenMenuBar.getMenuBarWidth(context);
		}
		return width;
	}
	
	public int getFullScreenHeight() {
		int height = GraphicUtilityClass.getDisplayHeight(getContext());
		if(!GraphicUtilityClass.isNavigation(getContext()) && !GraphicUtilityClass.isLandscape(getContext())) {
			height -= _menu_bar.getMeasuredHeight();
		}
		return height;
	}
	
	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
		int width = GraphicUtilityClass.getDisplayWidth(getContext());
		int height = GraphicUtilityClass.getDisplayHeight(getContext());

		super.setMeasuredDimension(width, height);
		
		if(width > height) {
			_landscape_mode = true;
		}else {
			_landscape_mode = false;
		}

		int _menu_bar_width = 0;
		int _menu_bar_height = 0;
		
		if(_menu_bar != null) {
			_menu_bar.setLandscapeMode(_landscape_mode);
			
			if(_landscape_mode) {
				_menu_bar.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), 
						MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
				_menu_bar_width  = _menu_bar.getMeasuredWidth();
				_menu_bar_height = 0;
			}else {
				_menu_bar.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
						MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
				_menu_bar_width  = 0;
				_menu_bar_height = _menu_bar.getMeasuredHeight();
			}
		}

		System.out.println("measureChildren: width=" + (width - _menu_bar_width) + " height=" + (height - _menu_bar_height));
		measureChildren(width - _menu_bar_width, height - _menu_bar_height);
	}
	
	@Override
	protected void layoutChildren(int l, int t, int width, int height) {
		int _menu_bar_width  = 0;
		int _menu_bar_height = 0;

		if(_menu_bar != null) {
			if(_landscape_mode) {
				setChildPosition(_menu_bar,  l + width - _menu_bar.getMeasuredWidth(), t);
				_menu_bar_width  = _menu_bar.getMeasuredWidth();
				_menu_bar_height = 0;
			}else {
				setChildPosition(_menu_bar, l, t + height - _menu_bar.getMeasuredHeight());
				_menu_bar_width  = 0;
				_menu_bar_height = _menu_bar.getMeasuredHeight();
			}
		}
		subLayoutChildren(l, t, width - _menu_bar_width, height - _menu_bar_height);
	}
	
	protected abstract void subLayoutChildren(int l, int t, int width, int height);
	
	public int getClientWidth() {
		int width = getMeasuredWidth();
		if(_menu_bar != null && _landscape_mode) {
			if(width > _menu_bar.getMeasuredWidth()) {
				width -= _menu_bar.getMeasuredWidth();
			}
		}
		return width;
	}
	
	public int getClientHeight() {
		int height = getMeasuredHeight();
		if(_menu_bar != null && !_landscape_mode) {
			if(height > _menu_bar.getMeasuredHeight()) {
				height -= _menu_bar.getMeasuredHeight();
			}
		}
		return height;
	}
	
	protected abstract void measureChildren(int width, int height);

	public void hideSoftKeyboard() {
		InputMethodManager inputMgr = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMgr != null) {
			inputMgr.hideSoftInputFromWindow(getWindowToken(), 0);
		}
	}
	
	public static FullScreen getFullScreen(View view) {
		ViewParent parent = view.getParent();
		while(parent != null) {
			if(parent instanceof FullScreen) {
				return (FullScreen)parent;
			}
			parent = parent.getParent();
		}
		return null;
	}
	
	public void invalidateMenu() {
		_menu_invalidate_flag = true;
		if(_menu_bar != null) {
			prepareMenuOnDemand(_system_menu);
		}
	}
	
	protected final boolean prepareMenuOnDemand(Menu menu) {
		if((_system_menu == null && menu != null ) || (_system_menu != null && menu != null && _system_menu != menu)) {
			_system_menu = menu;
			_menu_invalidate_flag = true;
		}
		if (_menu_invalidate_flag) {
			if(prepareMenu(menu)) {
				return true;
			}else {
				return false;
			}
		} else {
			if (_screen_menu.count() > 0) {
				return true;
			}
			return false;
		}
	}
	
	private boolean prepareMenu(Menu menu) {
		synchronized (_screen_menu) {
			_menu_invalidate_flag = false;
			_screen_menu.reset();
			makeMenu(_screen_menu);
			onMenuUpdated(_screen_menu);
			if(menu != null) {
				menu.clear();
				for (int i = 0; i < _screen_menu.count(); i++) {
					ScreenMenuItem smi = _screen_menu.getItem(i);
					MenuItem mi = menu.add(smi.getTitle());
	
					int icon_id = smi.getMenuIconId();
					if (icon_id != 0) {
						mi.setIcon(icon_id);
					} else {
						String icon_name = smi.getMenuIconName();
						if (icon_name != null && icon_name.length() > 0) {
							Bitmap icon = BitmapRepository.getBitmapByName(getContext(), icon_name);
							if (icon != null) {
								BitmapDrawable bd = new BitmapDrawable(icon);
								mi.setIcon(bd);
							}
						}
					}
					if (smi.isCheckable()) {
						mi.setCheckable(true);
						mi.setChecked(smi.isChecked());
					} else {
						mi.setCheckable(false);
					}
					smi.setSystemMenuItem(mi);
				}
			}
			if(_screen_menu.count() > 0) {
				return true;
			}else {
				return false;
			}
		}
	}
	
	protected boolean onMenuItemSelected(MenuItem item) {
		synchronized (_screen_menu) {
			for (int i = 0; i < _screen_menu.count(); i++) {
				ScreenMenuItem smi = _screen_menu.getItem(i);
				if (smi.isSameMenuItem(item)) {
					smi.run();
					return true;
				}
			}
			return false;
		}
	}
	
	public void enableMenuBar(int show_count) {
		if(_menu_bar == null) {
			_menu_bar = new ScreenMenuBar(getContext());
			super.addView(_menu_bar);
			prepareMenuOnDemand(null);
		}
	}
	
	public void disableMenuBar() {
		if(_menu_bar != null) {
			super.removeView(_menu_bar);
		}
		_menu_bar = null;
	}
	
	protected void onMenuUpdated(ScreenMenu menu) {
		if(_menu_bar != null) {
			_menu_bar.resetMenu(menu, 5);
		}
	}
	

	
}
