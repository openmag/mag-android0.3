package com.anheinno.android.libs.ui;

import java.util.Stack;
import com.anheinno.android.libs.attachment.AttachmentDownloadService;
import com.anheinno.android.libs.graphics.BitmapRepository;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class UiApplication extends Activity {
	private Stack<FullScreen> _screen_stack;
	private Handler _handler;
	
	public UiApplication() {
		super();
		
		_screen_stack = null;
		_handler = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// enable full screen mode
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (AttachmentDownloadService.getInstance() == null) {
			Intent service = new Intent(this, AttachmentDownloadService.class);
			this.startService(service);
		}
		
		_screen_stack = new Stack<FullScreen>();
		_handler = new Handler();
	}
	
	/**
	 * 设置为横屏显示
	 */
	public void setLandscape() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	/**
	 * 是否横屏显示
	 * 
	 * @return
	 */
	/*public boolean isLandscape() {
		if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			return true;
		} else {
			return false;
		}
	}*/

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		System.out.println("onConfiguration changed!!!!!");
		BitmapRepository.recycle();
		System.gc();
		super.onConfigurationChanged(newConfig);
	}

	public void invokeLater(Runnable run) {
		_handler.post(run);
	}
	
	public void invokeLater(Runnable run, long delay) {
		_handler.postDelayed(run, delay);
	}
	
	protected boolean pushScreen(FullScreen view) {
		if (isInShowStack(view)) {
			return false;
		} else {
			synchronized (_screen_stack) {
				FullScreen current = getCurrentScreen();
				if(current != null) {
					current.onHide();
					hideSoftKeyboard(current);
				}
				_screen_stack.push(view);
				setContentView(view);
				view.onDisplay();
				return true;
			}
		}
	}

	private FullScreen popScreen() {
		synchronized (_screen_stack) {
			if (_screen_stack.size() > 0) {
				FullScreen sc = _screen_stack.pop();
				hideSoftKeyboard(sc);
				sc.onClose();
				if (_screen_stack.size() > 0) {
					FullScreen top = _screen_stack.peek();
					setContentView(top);
					top.onVisible();
				} else {
					finish();
				}
				return sc;
			} else {
				return null;
			}
		}
	}
	
	private void removeScreen(FullScreen screen) {
		synchronized(_screen_stack) {
			if(_screen_stack.size() > 0 && _screen_stack.peek() != screen && _screen_stack.contains(screen)) {
				_screen_stack.remove(screen);
				screen.onClose();
			}
		}
	}

	private void hideSoftKeyboard(FullScreen screen) {
		screen.hideSoftKeyboard();
	}

	protected boolean closeScreen(FullScreen screen) {
		if (isOnShow(screen)) {
			popScreen();
			return true;
		} else {
			removeScreen(screen);
			return false;
		}
	}

	protected boolean isInShowStack(FullScreen screen) {
		synchronized (_screen_stack) {
			for (int i = 0; i < _screen_stack.size(); i++) {
				if (_screen_stack.elementAt(i) == screen) {
					return true;
				}
			}
			return false;
		}
	}

	protected boolean isOnShow(FullScreen screen) {
		synchronized (_screen_stack) {
			if (_screen_stack.peek() == screen) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	private FullScreen getCurrentScreen() {
		if(_screen_stack.size() > 0) {
			return _screen_stack.peek();
		}else {
			return null;
		}
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		synchronized(_screen_stack) {
			FullScreen screen = getCurrentScreen();
			if(screen != null) {
				return screen.prepareMenuOnDemand(menu);
			}else {
				return false;
			}
		}
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		synchronized(_screen_stack) {
			FullScreen screen = getCurrentScreen();
			if(screen != null) {
				return screen.onMenuItemSelected(item);
			}else {
				return false;
			}
		}
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (_screen_stack.size() > 0) {
			FullScreen screen = _screen_stack.peek();
			boolean result = false;
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				// System.out.println("dispatchKeyEvent DOWN fired!!!!!!!!!!!!!!");
				result = screen.onKeyDown(event);
			} else if (event.getAction() == KeyEvent.ACTION_UP) {
				// System.out.println("dispatchKeyEvent UP fired!!!!!!!!!!!!!!");
				result = screen.onKeyUp(event);
			}
			if (result) {
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}
	
	/*public ScreenMenuBar getScreenMenuBar(int show_count) {
		if(_screen_menu_bar == null) {
			_screen_menu_bar = new ScreenMenuBar(this);
		}
		prepareMenuOnDemand(null);
		_screen_menu_bar.resetMenu(_screen_menu, show_count);
		return _screen_menu_bar;
	}*/

	 @Override
	 protected void onResume() {
		System.out.println("XXXXXXXXXXXonResume is called!!!XXXXXXXXXXXXX");
	    System.gc();
	    super.onResume();
	 }
	  
	 @Override
	 protected void onPause() {
		System.out.println("XXXXXXXXXXXXonPause is called!!!XXXXXXXXXXXXX");
	    super.onPause();
	    System.gc();
	 }
	 
	 @Override
	 protected void onDestroy() {
		 System.out.println("XXXXXXXXXonDestroy is called, free memory!!!XXXXXXX");
	     super.onDestroy();
	     
	     releaseResources();
	       
	     BitmapRepository.recycle();
	     System.gc();
	 }
	 
	 private void releaseResources() {
		 synchronized (_screen_stack) {
			 while(_screen_stack.size() > 0) {
				 popScreen();
			 }
			 _screen_stack = null;
		 }
		 _handler = null;
	 }
}