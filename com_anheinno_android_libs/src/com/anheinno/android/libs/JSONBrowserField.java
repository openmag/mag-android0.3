/*
 * HTTPRequestScreen.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import org.json.JSONObject;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;

import com.anheinno.android.libs.HttpRequestDialog.HttpRequestDialogListener;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.GraphicUtilityClass;
import com.anheinno.android.libs.graphics.ProgressBarDrawArea;
import com.anheinno.android.libs.graphics.ProgressBarDrawAreaUpdateInterface;
import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.log.LogConfigScreen;
import com.anheinno.android.libs.ui.DelayedLayoutChangeUI;
import com.anheinno.android.libs.ui.EasyDialog;
import com.anheinno.android.libs.ui.FullScreen;
import com.anheinno.android.libs.ui.Manager;
import com.anheinno.android.libs.ui.ProgressUIInterface;
import com.anheinno.android.libs.ui.ScreenMenu;
import com.anheinno.android.libs.ui.ScreenMenuItem;
import com.anheinno.android.libs.R;

/**
 * 创建一个通过HTTP协议请求JSON代码，缓存，并且显示的基础窗口
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public abstract class JSONBrowserField extends Manager implements JSONBrowserInterface, DownloadConsumer, ProgressUIInterface, ProgressBarDrawAreaUpdateInterface, DelayedLayoutChangeUI {
	private JSONBrowserField _self = this;
	
	private JSONBrowserScreenState _current_state;
	private JSONBrowserScreenState _future_state;

	private Stack<JSONBrowserScreenState> _prev_urls;

	private static final int BROWSER_STATE_INIT = 0;
	private static final int BROWSER_STATE_LOADING = 1;
	private static final int BROWSER_STATE_RENDERING = 2;
	private static final int BROWSER_STATE_BROWSING = 3;

	private int _state;
	
	private ScrollView _scroll_panel;
	private View _content;
	
	private ProgressBarDrawArea _progress_bar;
	private BackgroundDescriptor _bg;
	
	private int _visible_width;
	private int _visible_height;

	private boolean _show_refresh_menu = true;
	
	public JSONBrowserField(Context context) {
		super(context);
		
		_scroll_panel = new ScrollView(context);
		_scroll_panel.setWillNotDraw(false);
		_scroll_panel.setBackgroundColor(Color.TRANSPARENT);
		_scroll_panel.setFocusable(true);
		_scroll_panel.setFocusableInTouchMode(true);
		_scroll_panel.setVerticalFadingEdgeEnabled(true);
		_scroll_panel.setScrollbarFadingEnabled(true);
		super.addView(_scroll_panel);
		
		_init();
	}

	private void _init() {
		_prev_urls = new Stack<JSONBrowserScreenState>();
		_state = BROWSER_STATE_INIT;
		_current_state = null;
		_progress_bar = new ProgressBarDrawArea(getContext(), FullScreen.getFullScreenWidth(getContext()));
		_bg = null;
		_show_refresh_menu = true;
		_visible_width  = 0;
		_visible_height = 0;
	}

	protected void reset() {
		_init();
	}

	protected void startDownloading(String msg) {
		if (msg == null) {
			msg = getContext().getString(R.string.http_message_retrieving);
		}
		startProgressIndeterministic(msg, 2);
		_state = BROWSER_STATE_LOADING;
	}

	protected void endDownloading() {
		_state = BROWSER_STATE_BROWSING;
		stopProgress();
	}

	public int getHistoryLength() {
		return _prev_urls.size();
	}

	private void retrieveData(final JSONBrowserScreenState state, boolean refresh, boolean sync) {
		if (_state == BROWSER_STATE_LOADING) {
			DownloadManager.getDownloadManager().cancelDownloadTask(this);
		} else if (_state == BROWSER_STATE_RENDERING) {
			return;
		}

		_future_state = state;
		startDownloading(null);
		if (sync) {
			HttpRequestDialogListener http_listener = new HttpRequestDialogListener() {
				public void onGetJSONResult(JSONObject o) {
					dataArrival(o, state);
				}

				public boolean onError(String msg) {
					retrieveError("");
					return false;
				}
			};
			HttpRequestDialog ss = new HttpRequestDialog(getContext(), state.getURL(), http_listener);
			// RequestStatusScreen ss = new RequestStatusScreen(state.getURL(),
			// JSONBrowserConfig.getLinkType(),
			// JSONBrowserConfig.drawProgressBar());
			if (state.getExpire() > 0) {
				ss.addHeader("X-Anhe-Link-Expire", "" + state.getExpire());
			}
			Hashtable<String, String> _cookie = JSONBrowserCookieStore.getCookies();
			if (_cookie != null && _cookie.size() > 0) {
				System.out.println("sync Cookie size: " + _cookie.size());
				Enumeration<String> e = _cookie.keys();
				while (e.hasMoreElements()) {
					String key = e.nextElement();
					ss.addHeader(key, _cookie.get(key));
					System.out.println("sync Send cookie: " + key + "=" + _cookie.get(key));
				}
			}
			ss.start();
		} else {
			DownloadManager.getDownloadManager().registerDownloadTask(getContext(), state.getURL(), JSONBrowserCookieStore.getCookies(), refresh, state.getExpire(), state.isNotify(), this, this,
					state);
		}
	}

	public void dataArrival(JSONObject result, Object param) {
		if (_future_state == null || _future_state != param) {
			return;
		}
		// 2011-1-23
		_state = BROWSER_STATE_RENDERING;
		JSONBrowserScreenState state = (JSONBrowserScreenState) param;

		if (_current_state != null && _current_state.needSave() && _current_state != state) {
			_prev_urls.push(_current_state);
		}

		state.setData(result);
		_current_state = state;

		startProgressIndeterministic(getContext().getString(R.string.http_message_rendering), 2);

		FullScreen.getFullScreen(this).getUiApplication().invokeLater(new Runnable() {
			public void run() {
				showJSON(_current_state.getData(), _current_state.getParams());
			}
		});
	}

	public boolean retrieveError(String msg) {
		if(getContext() != null) {
			LOG.error(this, "retriveError", null);
			EasyDialog.postInfo(getContext(), getContext().getString(R.string.json_screen_retrieve_error_message) + msg);
			endDownloading();
		}
		return false;
	}

	public void __open(String ourl, long expire, boolean notify, boolean saveHistory, boolean refresh, Object params, boolean sync) {
		String url = getAbsoluteURL(ourl);
		System.out.println("__open: " + url);
		if (url != null) {
			JSONBrowserScreenState state = new JSONBrowserScreenState(url, expire, notify, saveHistory, params);
			if (state.isValid()) {
				retrieveData(state, refresh, sync);
			}
		} else {
			EasyDialog.longAlert(getContext(), getContext().getString(R.string.json_screen_invalid_url_prompt) + ourl);
		}
	}
	
	public void open(JSONBrowserLink link, boolean refresh, Object params) {
		if(link.isValidURL()) {
			__open(link.getURL(), link.getExpireMilliseconds(), link.isNotify(), link.isSaveHistory(), refresh, params, false);
		}
	}
	
	public void syncOpen(JSONBrowserLink link, boolean refresh, Object params) {
		if(link.isValidURL()) {
			__open(link.getURL(), link.getExpireMilliseconds(), link.isNotify(), link.isSaveHistory(), refresh, params, true);
		}
	}

	public String getAbsoluteURL(String url) {
		HTTPRequestString req = new HTTPRequestString();
		if (_current_state != null && _current_state.isValid()) {
			if (req.parse(_current_state.getURL())) {
				if (req.relative(url)) {
					return req.getURL(false);
				}
			}
		} else if (req.parse(url)) {
			return req.getURL(false);
		}
		return null;
	}

	// 刷新页面
	public void refresh() {
		refresh(-1);
	}

	/**
	 * 重新从缓存加载内容
	 * 
	 */
	public void reload() {
		if (_current_state != null && _current_state.isValid()) {
			retrieveData(_current_state, false, false);
		}
	}

	/**
	 * 刷新，重新从网络加载内容
	 * 
	 * @param expire
	 */

	public void refresh(int expire) {
		if (_current_state != null && _current_state.isValid()) {
			if (expire > 0) {
				_current_state.setExpire(expire);
			}
			retrieveData(_current_state, true, false);
		}
	}

	public boolean back(boolean refresh) {
		if (_prev_urls.size() > 0) {
			_current_state.setSaveHistory(false);
			JSONBrowserScreenState state = (JSONBrowserScreenState) _prev_urls.pop();
			retrieveData(state, refresh, false);
			return true;
		} else {
			return false;
		}
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (_state == BROWSER_STATE_LOADING) {
				abortDownloading();
				return true;
			} else if (getHistoryLength() > 0) {
				back(false);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		return super.onKeyLongPress(keyCode, event);
	}

	public void tryReloadPage() {
		if (getURL() != null && JSONObjectCacheDatabase.needPrefetchObject(getContext(), getURL())) {
			reload();
		}
	}

	/*
	 * clear shadows under title bar
	 */
	// protected void applyTheme() {
	// leave this empty
	// }

	// 2010-5-26 更新进度条
	public void resetGauge(String msg, int max, int start) {
		startProgressDeterministic(msg, max, start);
	}

	public void resetGauge(String msg, int wait_seconds) {
		startProgressIndeterministic(msg, wait_seconds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anheinno.libs.ProgressUIInterface#updateGauge(int)
	 */
	public void updateGauge(int val) {
		updateProgress(val);
	}

	public JSONObject getData() {
		if (_current_state != null && _current_state.isValid()) {
			return _current_state.getData();
		} else {
			return null;
		}
	}

	public String getURL() {
		if (_current_state != null && _current_state.isValid()) {
			return _current_state.getURL();
		} else {
			return null;
		}
	}

	public boolean isLoading() {
		if (_state == BROWSER_STATE_LOADING ) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isRendering() {
		if(_state == BROWSER_STATE_RENDERING) {
			return true;
		}else {
			return false;
		}
	}
	
	public void startProgressDeterministic(String msg, int max, int value) {
		_progress_bar.startGauge(msg, 0, max);
		_progress_bar.setValue(value);
		commitLayoutChange();
		//invalidateProgressBarDrawArea();
	}
	
	public void startProgressIndeterministic(String msg, int seconds) {
		_progress_bar.startTimer(msg, seconds, this);
		commitLayoutChange();
		//invalidateProgressBarDrawArea();
	}
	
	public void invalidateProgressBarDrawArea() {
		// System.out.println("invalidate progress bar draw area....");
		if(_progress_bar != null && _progress_bar.getHeight() > 0) {
			//postInvalidate(0, getMeasuredHeight() - _progress_bar.getHeight(), getMeasuredWidth(), _progress_bar.getHeight());
			postInvalidate();
		}
	}
	
	public void updateProgress(int val) {
		_progress_bar.setValue(val);
		invalidateProgressBarDrawArea();
	}
	
	public void stopProgress() {
		_progress_bar.stopTimer();
		invalidateProgressBarDrawArea();
		commitLayoutChange();
	}
	
	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		_visible_width = width;
		_visible_height = height - _progress_bar.getHeight();
		
		if(_scroll_panel != null) {
			_scroll_panel.measure(MeasureSpec.makeMeasureSpec(_visible_width, MeasureSpec.EXACTLY), 
				MeasureSpec.makeMeasureSpec(_visible_height, MeasureSpec.EXACTLY));
		}else if(_content != null){
			_content.measure(MeasureSpec.makeMeasureSpec(_visible_width, MeasureSpec.EXACTLY), 
					MeasureSpec.makeMeasureSpec(_visible_height, MeasureSpec.EXACTLY));
		}
		
		_progress_bar.setWidth(width);
		
		super.setMeasuredDimension(width, height);
	}
	
	public int getVisibleWidth() {
		return _visible_width;
	}
	
	public int getVisibleHeight() {
		return _visible_height;
	}
	
	@Override
	protected synchronized void layoutChildren(int left, int top, int width, int height) {
		if(_scroll_panel != null) {
			setChildPosition(_scroll_panel, left, top);
		}else if(_content != null) {
			setChildPosition(_content, left, top);
		}
	}

	protected synchronized void onDraw(Canvas canvas) {		
		if(_bg != null) {
			_bg.draw(getContext(), canvas, 0, 0, getMeasuredWidth(), getMeasuredHeight());
		}
		
		super.onDraw(canvas);
		
		if(_progress_bar != null) {
			_progress_bar.draw(canvas, 0, getMeasuredHeight() - _progress_bar.getHeight());
		}
	}


	public void setBackground(BackgroundDescriptor bg) {
		_bg = bg;
		postInvalidate();
	}
	
	public void removeBackground() {
		_bg = null;
		postInvalidate();
	}
	
	
	//// menu
	
	protected JSONBrowserConfigScreen getConfigScreen() {
		return new JSONBrowserConfigScreen(getContext());
	}

	private ScreenMenuItem _mi_status = new ScreenMenuItem(getContext().getString(R.string.json_screen_menu_status), ScreenMenu.DEBUG_MENU_ITEM + 1) {
		
		@Override
		protected void initIcons() {
			setMenuIconId(android.R.drawable.ic_menu_info_details);
			setBarIconName("menubar_icon_devstatus.png");
			setFocusBarIconName("menubar_icon_devstatus_focus.png");
		}
		
		public void run() {
			String info = "BOARD=" + Build.BOARD + ";BRAND=" + Build.BRAND + ";DEVICE=" + Build.DEVICE + ";DISPLAY=" + Build.DISPLAY + ";FINGERPINT=" + Build.FINGERPRINT
					+ ";HOST=" + Build.HOST + ";ID=" + Build.ID + ";MODEL=" + Build.MODEL + ";PRODUCT=" + Build.PRODUCT + ";TAGS=" + Build.TAGS + ";TIME=" + Build.TIME
					+ ";TYPE=" + Build.TYPE + ";USER=" + Build.USER + ";INCREMENTAL=" + Build.VERSION.INCREMENTAL + ";RELEASE=" + Build.VERSION.RELEASE + ";SDK="
					+ Build.VERSION.SDK + ";CODENAME=" + Build.VERSION.CODENAME;
			
			Configuration conf = getContext().getResources().getConfiguration();
			info += ";FontScale=" + conf.fontScale;
			info += ";keyboard=";
			switch(conf.keyboard) {
			case Configuration.KEYBOARD_12KEY:
				info += "KEYBOARD_12KEY";break;
			case Configuration.KEYBOARD_NOKEYS:
				info += "KEYBOARD_NOKEYS";break;
			case Configuration.KEYBOARD_QWERTY:
				info += "KEYBOARD_QWERTY";break;
			}
			info += ";touchscreen=";
			switch(conf.touchscreen) {
			case Configuration.TOUCHSCREEN_FINGER:
				info += "TOUCHSCREEN_FINGER";break;
			case Configuration.TOUCHSCREEN_NOTOUCH:
				info += "TOUCHSCREEN_NOTOUCH";break;
			case Configuration.TOUCHSCREEN_STYLUS:
				info += "TOUCHSCREEN_STYLUS";break;
			}
			info += ";navigation=";
			switch(conf.navigation) {
			case Configuration.NAVIGATION_DPAD:
				info += "NAVIGATION_DPAD";break;
			case Configuration.NAVIGATION_NONAV:
				info += "NAVIGATION_NONAV";break;
			case Configuration.NAVIGATION_TRACKBALL:
				info += "NAVIGATION_TRACKBALL";break;
			case Configuration.NAVIGATION_WHEEL:
				info += "NAVIGATION_WHEEL";break;
			}
			info += ";locale=" + conf.locale.getDisplayName();
			info += ";mcc=" + Integer.toHexString(conf.mcc);
			info += ";mnc=" + Integer.toHexString(conf.mnc);
			
			Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
			info += ";display_width=" + display.getWidth();
			info += ";display_height=" + display.getHeight();
			
			info += ";view_area_width=" + FullScreen.getFullScreenWidth(getContext());
			info += ";view_area_height=" + (FullScreen.getFullScreen(JSONBrowserField.this).getFullScreenHeight());
			
			DisplayMetrics metrics = new DisplayMetrics();
			display.getMetrics(metrics);
			info += ";widthPixels=" + metrics.widthPixels;
			info += ";heightPixels=" + metrics.heightPixels;
			info += ";density=" + metrics.density;
			info += ";xdpi=" + metrics.xdpi;
			info += ";ydpi=" + metrics.ydpi;
			info += ";touchWidth=" + GraphicUtilityClass.getMinTouchWidth(getContext());
			info += ";touchHeight=" + GraphicUtilityClass.getMinTouchHeight(getContext());
			info += ";minFontSize=" + GraphicUtilityClass.getMinFontSize(getContext());

			EasyDialog.info(getContext(), info);
		}
		
	};

	private ScreenMenuItem _mi_config = new ScreenMenuItem(getContext().getString(R.string.json_screen_menu_options), ScreenMenu.LESS_USED_MENU_ITEM + 1) {
		
		@Override
		protected void initIcons() {
			setMenuIconId(android.R.drawable.ic_menu_manage);
			setBarIconName("menubar_icon_option.png");
			setFocusBarIconName("menubar_icon_option_focus.png");
		}
		
		public void run() {
			JSONBrowserConfigScreen cfs = getConfigScreen();
			cfs.show();
		}
	};

	private ScreenMenuItem _mi_refresh = new ScreenMenuItem(getContext().getString(R.string.json_screen_menu_refresh), ScreenMenu.MOST_USED_MENU_ITEM + 2) {
		
		@Override
		protected void initIcons() {
			setMenuIconId(android.R.drawable.ic_menu_rotate);
			setBarIconName("menubar_icon_refresh.png");
			setFocusBarIconName("menubar_icon_refresh_focus.png");
		}
		
		public void run() {
			refresh();
		}
		
	};

	private ScreenMenuItem _mi_back = new ScreenMenuItem(getContext().getString(R.string.json_screen_menu_back), ScreenMenu.MOST_USED_MENU_ITEM + 1) {
		
		@Override
		protected void initIcons() {
			setMenuIconId(android.R.drawable.ic_menu_revert);
			setBarIconName("menubar_icon_back.png");
			setFocusBarIconName("menubar_icon_back_focus.png");
		}
		
		public void run() {
			back(false);
		}
		
	};

	// MenuItem _mi_linktype = new JSONBrowserLinkTypeMenuItem();

	// MenuItem _mi_showattachment =
	// AttachmentScreen.getInstance().getMenu(UiApplication.getUiApplication());

	public void setShowRefreshMenu(boolean show) {
		_show_refresh_menu = show;
	}

	private ScreenMenuItem _mi_log = new ScreenMenuItem(getContext().getString(R.string.json_screen_menu_log_config), ScreenMenu.DEBUG_MENU_ITEM + 5) {
		
		@Override
		protected void initIcons() {
			setMenuIconId(android.R.drawable.ic_menu_edit);
			setBarIconName("menubar_icon_log.png");
			setFocusBarIconName("menubar_icon_log_focus.png");
		}
		
		public void run() {
			LogConfigScreen ls = new LogConfigScreen(getContext());
			ls.show();
		}
		
	};

	private ScreenMenuItem _mi_source = new ScreenMenuItem(getContext().getString(R.string.json_screen_menu_show_source), ScreenMenu.DEBUG_MENU_ITEM + 6) {
		
		@Override
		protected void initIcons() {
			setMenuIconId(android.R.drawable.ic_menu_slideshow);
			setBarIconName("menubar_icon_src.png");
			setFocusBarIconName("menubar_icon_src_focus.png");
		}
		
		public void run() {
			JSONBrowserSourceScreen src = new JSONBrowserSourceScreen(getContext(), _self);
			src.show();
		}
	};

	private ScreenMenuItem _mi_about = new ScreenMenuItem(getContext().getString(R.string.json_screen_menu_about), ScreenMenu.LESS_USED_MENU_ITEM + 7) {
		
		@Override
		protected void initIcons() {
			setMenuIconId(android.R.drawable.ic_menu_help);
			setBarIconName("menubar_icon_help.png");
			setFocusBarIconName("menubar_icon_help_focus.png");
		}
		
		public void run() {
			EasyDialog.about(getContext(), getContext().getString(R.string.copyright));
		}
		
	};
	
	private ScreenMenuItem _mi_cancel = new ScreenMenuItem(getContext().getString(R.string.json_screen_menu_cancel_downloading), ScreenMenu.MOST_USED_MENU_ITEM + 0) {

		@Override
		protected void initIcons() {
			setMenuIconId(android.R.drawable.ic_menu_delete);
			setBarIconName("menubar_icon_abort.png");
			setFocusBarIconName("menubar_icon_abort_focus.png");
		}
		
		public void run() {
			abortDownloading();
		}
		
	};
	
	private boolean abortDownloading() {
		if (_state == BROWSER_STATE_LOADING) {
			DownloadManager.getDownloadManager().cancelDownloadTask(this);
			return true;
		}else {
			return false;
		}
	}

	public void prepareMenu(ScreenMenu menu) {
		
		if (_state == BROWSER_STATE_LOADING) {
			menu.add(_mi_cancel);
		}
		
		if (getHistoryLength() > 0) {
			menu.add(_mi_back);
		}

		if (_show_refresh_menu && _current_state != null && _current_state.isValid()) {
			menu.add(_mi_refresh);
			// menu.setDefault(_mi_refresh);
		}

		// menu.addSeparator();

		if (!JSONBrowserConfig.isLockAccount(getContext())) {
			menu.add(_mi_config);
		}
		// if (AppConfig.isDebugMode()) {
		// menu.add(_mi_linktype);
		menu.add(_mi_status);
		// menu.add(_mi_showattachment);
		// menu.add(LOG.getMenu());
		menu.add(_mi_log);
		menu.add(_mi_source);
		menu.add(_mi_about);
		// }
	}
	
	public synchronized void setContent(View content) {
		if(_scroll_panel != null) {
			if(_scroll_panel.getChildCount() > 0) {
				_scroll_panel.removeAllViews();
			}
			if(content != null) {
				LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				_scroll_panel.addView(content, params);
			}
		}else {
			if(_content != null) {
				removeView(_content);
			}
			if(content != null) {
				addView(content);
			}
		}
		_content = content;
	}
	
	public void commitLayoutChange() {
		FullScreen screen = FullScreen.getFullScreen(this);
		if(screen != null) {
			screen.getUiApplication().invokeLater(new layoutRunnable(this));
		}
	}
	
	static class layoutRunnable implements Runnable {
		private JSONBrowserField _field;
		layoutRunnable(JSONBrowserField field) {
			_field = field;
		}
		public void run() {
			_field.requestLayout();
		}
	}
	
	public void setScrollPosition(int x, int y) {
		if(_scroll_panel != null) {
			_scroll_panel.scrollTo(x, y);
		}
	}
	
	public synchronized void disableScroll() {
		if(_scroll_panel != null) {
			_scroll_panel.removeAllViews();
			super.removeView(_scroll_panel);
			_scroll_panel = null;
			if(_content != null) {
				super.addView(_content);
			}
		}
	}
	
	public void releaseResources() {
		System.out.println("JSONBrowserField::releaseResources is called");
		
		_self = null;
		
		_current_state = null;
		_future_state = null;

		_prev_urls.clear();

		if(_scroll_panel != null) {
			_scroll_panel.removeAllViews();
			_scroll_panel = null;
		}
		removeAllViews();
		_content = null;
		
		_progress_bar = null;
		_bg = null;
		
	}

}
