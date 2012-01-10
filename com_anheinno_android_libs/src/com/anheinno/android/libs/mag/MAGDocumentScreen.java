/*
 * MAGDocumentScreen.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

import com.anheinno.android.libs.HTTPRequestString;
import com.anheinno.android.libs.HttpRequestDialog;
import com.anheinno.android.libs.HttpRequestDialog.HttpRequestDialogListener;
//import com.anheinno.android.libs.JSONBrowserConfig;
import com.anheinno.android.libs.JSONBrowserConfigScreen;
import com.anheinno.android.libs.JSONBrowserCookieStore;
import com.anheinno.android.libs.JSONBrowserLink;
import com.anheinno.android.libs.JSONObjectCacheDatabase;
import com.anheinno.android.libs.R;
import com.anheinno.android.libs.attachment.AttachmentDownloadActivity;
import com.anheinno.android.libs.attachment.AttachmentDownloadService;
import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.mag.MAGLoginScreen.MAGLoginScreenListener;
import com.anheinno.android.libs.ui.CustomTitleMainScreen;
import com.anheinno.android.libs.ui.EasyDialog.ConfirmDialogListener;
import com.anheinno.android.libs.ui.ScreenMenu;
import com.anheinno.android.libs.ui.ScreenMenuItem;
import com.anheinno.android.libs.ui.TextInputDialog;
import com.anheinno.android.libs.ui.TextInputDialog.TextInputDialogListener;
import com.anheinno.android.libs.ui.EasyDialog;

/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGDocumentScreen extends CustomTitleMainScreen implements MAGDocumentContainerFieldInterface {

	private JSONBrowserLink _init_link;
	private boolean _init_refresh;

	private MAGDocumentScreen _parent;
	private MAGLoginScreenConfiguration _login_config;

	/* for performance tuning */
	public long _when_layout;
	public long _when_layout_complete;
	/* end for performance tuning */
	
	private Hashtable<String, MAGDocumentContainerFieldInterface> _mag_frame_tbl;

	//private long _when_last_move_focus = 0;
	//private boolean _tooltip_showed = false;
	//private MAGComponent _tooltip_show_component = null;
	// private boolean _manual_show_tooltip = false;
	//private TooltipScreen _tooltip = null;


	public MAGDocumentScreen(Context context) {
		super(context);
		
		MAGDocumentField field = new MAGDocumentField(context, this) {
			@Override
			public boolean onKeyDown(int keycode, KeyEvent event) {
				//System.out.println("MAGDocumentScreen::onKeyEvent keyCode=" + event.getKeyCode() + "/" + KeyEvent.KEYCODE_CLEAR + "/" + KeyEvent.KEYCODE_BACK );
				if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
					if (!isLoading() && getHistoryLength() == 0 && 
							_parent == null && !MAGDocumentConfig.isBind(getContext())) {
						ConfirmDialogListener quit_listener = new ConfirmDialogListener() {
							public void onYes() {
								close();
							}
							public void onNo() {
								// do nothing
							}
						};
						EasyDialog.confirm(getContext(), getContext().getString(R.string.mag_screen_confirm_quit), quit_listener);						
						return true;
					}
				} //else if (event.getKeyCode() == KeyEvent.FLAG_CANCELED_LONG_PRESS) {
					/*MAGComponent comp = getLeafComponentWithFocus();
					System.out.println("current focus component " + comp);
					if (comp != null) {
						showTooltip(comp, true);
					}*/
				//}
				return super.onKeyDown(keycode, event);
			}
		};
		super.setContentView(field);
		//getMAGDocumentField().setMAGDocumentContainerInterface(this);
		super.invalidateMenu();
		_parent = null;
		_init_link = null;
		_init_refresh = false;
	}
	
	public MAGDocumentScreen(Context context, JSONBrowserLink link, boolean refresh, MAGDocumentScreen parent) {
		this(context);
		_init_link = link;
		_init_refresh = refresh;
		setParentMAGDocumentScreen(parent);
		//initPage(url, JSONBrowserConfig.getCacheExpire(getContext()), notify, true, false);
	}

	/*
	 * public MAGDocumentScreen(String url, boolean notify, MAGDocumentScreen p)
	 * {
	 * 		super(); initMAGDocumentScreen(p); initPage(url,
	 * 		JSONBrowserConfig.getCacheExpire(), notify, false); 
	 * }
	 */

	private MAGDocumentField getMAGDocumentField() {
		return (MAGDocumentField)getContentView();
	}

	private void setParentMAGDocumentScreen(MAGDocumentScreen p) {
		//_mag_layout = new MAGLayoutManager(getContext());
		/* {
			protected void onDraw(Canvas g) {
				MAGDocument doc = getMAGDocument();
				if (doc != null) {
					int w = getPreferredWidth();
					int h = getPreferredHeight();

					BackgroundDescriptor tmp_bg = doc.style().getBackground();
					if (tmp_bg != null && w > 0 && h > 0) {
						Bitmap tmp_bitmap = GradientRectangleFactory.getGradientBitmap(getContext(), w, h, tmp_bg);
						g.drawBitmap(tmp_bitmap, 0, 0, null);
					}
				}
				super.onDraw(g);
			}
		};*/
		/*_tooltip = new TooltipScreen() {
			protected boolean keyDown(int keycode, int time) {
				super.hideTooltip();
				return _self.keyDown(keycode, time);
			}

			protected boolean navigationMovement(int dx, int dy, int status, int time) {
				if (super.navigationMovement(dx, dy, status, time)) {
					return true;
				} else {
					super.hideTooltip();
					return _self.navigationMovement(dx, dy, status, time);
				}
			}

			protected boolean navigationClick(int status, int time) {
				super.hideTooltip();
				return _self.navigationClick(status, time);
			}

			protected boolean keyChar(char ch, int status, int time) {
				super.hideTooltip();
				return _self.keyChar(ch, status, time);
			}
		};*/
		_parent = p;
		if (_parent != null) {
			//p.resetTooltip();
			setDefaultBGImage(p.getDefaultBGImage());
			setLoginConfig(p._login_config);
			hideCloseMenu();
		}
	}

	/*private void initPage(String url, long expire, boolean notify, boolean savehistory, boolean refresh) {
		_init_link.setURL(url);
		_init_link.setExpireMilliseconds(expire);
		_init_link.setNotify(notify);
		_init_link.setSaveHistory(savehistory);
		_init_refresh = refresh;
	}*/

	@Override
	protected void onDisplay() {
		super.onDisplay();
		//_when_last_move_focus = System.currentTimeMillis();
		//UpdateUITimerThread.addUIClient(this);
		MAGComponent.clearPreviousFocus();
		initShow();
	}
	
	@Override
	protected void onClose() {
		MAGComponent.clearPreviousFocus();
		
		if (_parent == null) {
			if (!MAGDocumentConfig.isBind(getContext())) {
				MAGDocumentConfig.setVerified(getContext(), false);
			}

			/*MAGPushClientApp _push_app = MAGPushClientApp.waitForSingleton();
			if (_push_app != null) {
				MAGAppDescription apps = _push_app.getMAGAppDescription();
				apps.updateIcon(AppUtils.getAppName(), false, null);
				_push_app.cancelNotification();
			}*/
		}else {
			_parent = null;
		}
		
		getMAGDocumentField().releaseResources();
		
		_init_link = null;
		_login_config = null;
		if(_mag_frame_tbl != null) {
			_mag_frame_tbl.clear();
			_mag_frame_tbl = null;
		}

		super.onClose();
	}

	private void initShow() {
		boolean is_verified = MAGDocumentConfig.isVerified(getContext());
		System.out.println("new MAGDocumentScreen verified=" + is_verified);
		if (is_verified) {
			boolean password_verified = true;
			// 当且仅当当前窗口是根窗口时进行password protect验证
			if (_parent == null && MAGDocumentConfig.isPasswordProtect(getContext())) {
				password_verified = false;
				do {
					TextInputDialogListener tid_listener = new TextInputDialogListener() {
						public void onTextSet(TextInputDialog dialog, String text) {
							if (text.equals(MAGDocumentConfig.getPassword(getContext()))) {
								//dialog.dismiss();
								initLoadPage();
							} else {
								Toast t = Toast.makeText(getContext(), getContext().getString(R.string.mag_document_screen_password_wrong_prompt), Toast.LENGTH_SHORT);
								t.show();
							}
						}
					};
					TextInputDialog tid = new TextInputDialog(getContext(), tid_listener, getContext().getString(R.string.mag_document_screen_password_protect_prompt), "");
					tid.show();
				} while (!password_verified);
			}else {
				initLoadPage();
			}
		} else {
			login();
		}
	}

	public void setLoginConfig(MAGLoginScreenConfiguration config) {
		_login_config = config;

		if (_login_config != null) {
			setDefaultBGImage(_login_config._bg);
		}
	}

	public void login() {
		MAGLoginScreenListener login_listener = new MAGLoginScreenListener() {
			public void onLoginSuccess(String url) {
				initLoadPage();
			}
		};
		MAGLoginScreen login = new MAGLoginScreen(getContext(), login_listener, _login_config);
		login.show();
	}

	private void initLoadPage() {
		JSONBrowserCookieStore.addCookie("X-Anhe-Account-Username", MAGDocumentConfig.getUsername(getContext()));
		JSONBrowserCookieStore.addCookie("X-Anhe-Account-Password", MAGDocumentConfig.getPassword(getContext()));

		if (_init_link == null) {
			_init_link = new JSONBrowserLink(getContext());
			_init_link.setURL(MAGDocumentConfig.getMainEntryURL(getContext()));
		}
		
		getMAGDocumentField().open(_init_link, _init_refresh, null);
	}

	private void relogin() {
		try {
			HTTPRequestString req = new HTTPRequestString();
			if (req.parse(MAGDocumentConfig.getLoginURL(getContext()))) {
				req.addParam("_action", "LOGOUT");

				HttpRequestDialogListener http_listener = new HttpRequestDialogListener() {

					public void onGetJSONResult(JSONObject o) {
						onLogoutSuccess();
					}

					public boolean onError(String msg) {
						EasyDialog.confirm(getContext(), getContext().getString(R.string.mag_document_screen_logout_failure_prompt), new ConfirmDialogListener() {
							public void onYes() {
								onLogoutSuccess();
							}
							public void onNo() {
							}
						});
						return false;
					}
					
				};
				
				HttpRequestDialog http = new HttpRequestDialog(getContext(), req.getURL(), http_listener);
				
				http.addHeader("X-Anhe-Account-Username", MAGDocumentConfig.getUsername(getContext()));
				http.addHeader("X-Anhe-Account-Password", MAGDocumentConfig.getPassword(getContext()));

				http.start();								
			}
		} catch (Exception e) {
			LOG.error(this, "relogin", e);
		}
	}

	private void onLogoutSuccess() {
		if (_parent != null) {
			_parent.onLogoutSuccess();
			close();
		} else {
			resetScreen();

			JSONObjectCacheDatabase.purgeAllObject(getContext());
			MAGDocumentConfig.setVerified(getContext(), false);
			MAGDocumentConfig.setBind(getContext(), false);

			getMAGDocumentField().unload();
			_init_link = null;
			login();
		}
	}

	/*public void resetScreen() {
		// _mag_layout.setContainer(null);
		resetTooltip();
		super.resetScreen();
	}*/

	//public synchronized void showJSON(JSONObject o, Object params) {
	//	resetScreen();
		
		
		//initFocusComponent();
	//}

	/*private void initFocusComponent() {
		MAGComponent comp = getLeafComponentWithFocus();
		if (comp != null) {
			comp.setOnFocus();
		}
	}*/

	public MAGDocument getMAGDocument() {
		return getMAGDocumentField().getMAGDocument();
	}

	public MAGDocument getMAGParentDocument() {
		if (_parent != null) {
			return _parent.getMAGDocument();
		} else {
			return null;
		}
	}

	/**
	 */
	private ScreenMenuItem _mi_relogin = new ScreenMenuItem(getContext().getString(R.string.mag_screen_menu_logout), ScreenMenu.LESS_USED_MENU_ITEM + 10) {

		protected void initIcons() {
			setMenuIconName("menubar_icon_logout.png");
			setBarIconName("menubar_icon_logout.png");
			setFocusBarIconName("menubar_icon_logout_focus.png");
		}
		
		public void run() {
			relogin();
		}
		
	};
	
	private ScreenMenuItem _mi_download_mgr = new ScreenMenuItem(getContext().getString(R.string.mag_screen_menu_download_manager), ScreenMenu.LESS_USED_MENU_ITEM+9) {
		protected void initIcons() {
			setMenuIconName("menubar_icon_download.png");
			setBarIconName("menubar_icon_download.png");
			setFocusBarIconName("menubar_icon_download_focus.png");
		}
		
		public void run() {
			Intent notificationIntent = new Intent(getContext(), AttachmentDownloadActivity.class);
			getContext().startActivity(notificationIntent);
		}
	};
	
	private ScreenMenuItem _mi_show_debug_border = new ScreenMenuItem(getContext().getString(R.string.mag_screen_menu_show_debug_border), MAGDocumentConfig.isShowComponentBorder(getContext()), ScreenMenu.DEBUG_MENU_ITEM + 10) {

		protected void initIcons() {
			setMenuIconName("menubar_icon_showborder.png");
			setBarIconName("menubar_icon_showborder.png");
			setFocusBarIconName("menubar_icon_showborder_focus.png");
		}
		
		public void run() {
			if(isChecked()) {
				MAGDocumentConfig.disableShowComponentBorder(getContext());
				setChecked(false);
			}else {
				MAGDocumentConfig.enableShowComponentBorder(getContext());
				setChecked(true);
			}
			postInvalidate();
		}
	};
	
	private ScreenMenuItem _mi_close_subscreen = new ScreenMenuItem(getContext().getString(R.string.json_screen_menu_back), ScreenMenu.MOST_USED_MENU_ITEM + 1) {
		
		@Override
		protected void initIcons() {
			setMenuIconId(android.R.drawable.ic_menu_revert);
			setBarIconName("menubar_icon_back.png");
			setFocusBarIconName("menubar_icon_back_focus.png");
		}
		
		public void run() {
			close();
		}
		
	};

	protected void makeMenu(ScreenMenu menu) {
		super.makeMenu(menu);
		
		if(getMAGDocumentField() != null) {
			
			if(getMAGDocumentField().getHistoryLength() == 0 && _parent != null) {
				menu.add(_mi_close_subscreen);
			}
			
			getMAGDocumentField().prepareMenu(menu);
			
			menu.add(_mi_relogin);
			
			menu.add(_mi_download_mgr);

			if (getMAGDocumentField().getMAGDocument() != null) {
				Vector<ScreenMenuItem> menu_list = getMAGDocumentField().getMAGDocument().getMenuList();
				if (menu_list != null) {
					for (int i = 0; i < menu_list.size(); i++) {
						menu.add(menu_list.elementAt(i));
					}
				}
			}

			menu.add(_mi_show_debug_border);
		}
		
	}

	@Override
	public boolean onKeyDown(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if(getMAGDocumentField() != null && getMAGDocumentField().onKeyDown(event.getKeyCode(), event)) {
				return true;
			}
		}
		return super.onKeyDown(event);
	}
	
	/*private MAGComponent getLeafComponentWithFocus() {
		View f = getLeafFieldWithFocus();
		if (f != null) {
			// System.out.println("Field class " + f.getClass().getName());
			if (getMAGDocument() != null) {
				return getMAGDocument().getLeafComponentWithField(f);
			}
		}
		// System.out.println("getLeafComponentWithFocus: Cannot find focused field!");
		return null;
	}*/

	// protected boolean keyDown(int keycode, int time) {
	// char ch = Keypad.getAltedChar(KeypadUtil.getKeyChar(keycode,
	// KeypadUtil.MODE_EN_LOCALE));
	//
	// if (ch == '#') {
	// //MAGComponent comp = getMAGDocument().getLeafComponentWithFocus();
	// MAGComponent comp = getLeafComponentWithFocus();
	// System.out.println("current focus component " + comp);
	// if (comp != null) {
	// showTooltip(comp, true);
	// }
	// }
	// return super.keyDown(keycode, time);
	// }

	/*private int getHintScreenLeft(XYRect focus, MAGComponent comp) {
		int x = comp.getHintHorizontalOffset();
		if (x >= 0) {
			return focus.x + x;
		} else {
			int offset = 30;
			if (offset > focus.width / 2) {
				offset = focus.width / 2;
			}
			if (focus.x > Display.getWidth() - focus.x - focus.width) {
				return focus.x + focus.width - offset;
			} else {
				return focus.x + offset;
			}
		}
	}

	private int getHintScreenTop(XYRect focus, MAGComponent comp) {
		int y = comp.getHintVerticalOffset();
		if (y >= 0) {
			return focus.y + y;
		} else {
			int offset = 30;
			if (offset > focus.height / 2) {
				offset = focus.height / 2;
			}
			if (focus.y > Display.getHeight() - focus.y - focus.height) {
				return focus.y + focus.height - offset;
			} else {
				return focus.y + offset;
			}
		}
	}

	private void showTooltip(MAGComponent comp, boolean manual) {
		if (comp.hint() != null && comp.hint().length() > 0) {
			XYRect rect = new XYRect();

			getFocusRect(rect);
			// System.out.println("FocusRECT: x=" + rect.x + " y=" + rect.y +
			// " w=" + rect.width + " h=" + rect.height);

			boolean hint_transprent = comp.style().getBoolean("hint-background-transparent");

			_tooltip.reset(comp.hint(), getHintScreenLeft(rect, comp), getHintScreenTop(rect, comp), hint_transprent);

			BackgroundDescriptor bg = comp.style().getHintBackground();

			// BackgroundDescriptor bg = new
			// BackgroundDescriptor("image=bitmapborder.png color=#000000 duplicate=bitmap-border border-top=14 border-left=13 border-bottom=16 border-right=17");
			if (bg != null) {
				_tooltip.setBgColor(bg);
			}

			TextStyleDescriptor style = comp.style().getHintTextStyle();
			// TextStyleDescriptor style = new
			// TextStyleDescriptor("font-scale=0.8 text-align=left text-valign=middle padding-top=14 padding-bottom=16 padding-left=13 padding-right=17");
			if (style != null) {
				_tooltip.setTextStyle(style);
			}

			if (comp.style().hasHintBorderColorLeft()) {
				_tooltip.setBorderColorLeft(comp.style().getHintBorderColorLeft());
			}
			if (comp.style().hasHintBorderColorTop()) {
				_tooltip.setBorderColorTop(comp.style().getHintBorderColorTop());
			}
			if (comp.style().hasHintBorderColorRight()) {
				_tooltip.setBorderColorRight(comp.style().getHintBorderColorRight());
			}
			if (comp.style().hasHintBorderColorBottom()) {
				_tooltip.setBorderColorBottom(comp.style().getHintBorderColorBottom());
			}

			if (comp.style().hasHintBorderWidthLeft()) {
				_tooltip.setBorderWidthLeft(comp.style().getHintBorderWidthLeft());
			}
			if (comp.style().hasHintBorderWidthTop()) {
				_tooltip.setBorderWidthTop(comp.style().getHintBorderWidthTop());
			}
			if (comp.style().hasHintBorderWidthRight()) {
				_tooltip.setBorderWidthRight(comp.style().getHintBorderWidthRight());
			}
			if (comp.style().hasHintBorderWidthBottom()) {
				_tooltip.setBorderWidthBottom(comp.style().getHintBorderWidthBottom());
			}

			if (comp.style().hasHintPaddingLeft()) {
				_tooltip.setPaddingLeft(comp.style().getHintPaddingLeft());
			}
			if (comp.style().hasHintPaddingTop()) {
				_tooltip.setPaddingTop(comp.style().getHintPaddingTop());
			}
			if (comp.style().hasHintPaddingRight()) {
				_tooltip.setPaddingRight(comp.style().getHintPaddingRight());
			}
			if (comp.style().hasHintPaddingBottom()) {
				_tooltip.setPaddingBottom(comp.style().getHintPaddingBottom());
			}

			// System.out.println("Tooltip show!!!!!!!!!!!!!!!!");
			_tooltip.show(manual);
			_tooltip_showed = true;
		}
	}

	public void registerTooltip(MAGComponent comp) {
		synchronized (_tooltip) {
			_tooltip_show_component = comp;
			_tooltip_showed = false;
			_when_last_move_focus = System.currentTimeMillis();
		}
	}

	public void resetTooltip() {
		synchronized (_tooltip) {
			_tooltip.hide();
			_tooltip_showed = false;
			_when_last_move_focus = -1;
			_tooltip_show_component = null;
		}
	}*/

	//public synchronized void doUpdate() {
		/*synchronized (_tooltip) {
			MAGComponent comp = _tooltip_show_component;
			// System.out.println("doUpdate comp=" + comp +
			// " _when_last_move_focus=" + _when_last_move_focus);

			if (comp != null && _when_last_move_focus > 0) {
				long ellapse = System.currentTimeMillis() - _when_last_move_focus;
				int wait = comp.style().getHintWait();
				int duration = comp.style().getHintDuration();
				// System.out.println("wait=" + wait + " duration=" + duration +
				// " _tooltip_showed=" + _tooltip_showed + " showthis=" +
				// GraphicUtilityClass.isScreenShown(this));
				if (!_tooltip_showed) {
					if (wait > 0 && ellapse > wait && GraphicUtilityClass.isScreenShown(this)) {
						showTooltip(comp, false);
					}
				} else {
					if (_tooltip != null && _tooltip.isShown() && duration > 0 && ellapse > wait + duration && !_tooltip.isShowManually()) {
						UiApplication.getUiApplication().invokeLater(new Runnable() {
							public void run() {
								_tooltip.hide();
							}
						});
					}
				}
			}
		}*/
	//}

	protected JSONBrowserConfigScreen getConfigScreen() {
		return new MAGDocumentConfigScreen(getContext());
	}

	public MAGContainerInterface getMAGContainer() {
		return null;
	}

	public void unload() {
		getMAGDocumentField().unload();
		this.close();
	}

	public void reset() {
		super.resetScreen();
	}
	
	/*public void invalidateMAGComponent(MAGComponentInterface comp) {
		if(_mag_layout != null) {
			_mag_layout.invalidateMAGComponent(comp);
		}
	}*/
	
	protected void registerMAGDocumentContainer(MAGDocumentContainerFieldInterface container) {
		String id = container.getMAGContainer().id();
		if(id != null && id.length() > 0) {
			if(_mag_frame_tbl == null) {
				_mag_frame_tbl = new Hashtable<String, MAGDocumentContainerFieldInterface>(2);
			}
			_mag_frame_tbl.put(id, container);
		}
	}
	
	protected MAGDocumentContainerFieldInterface getMAGDocumentContainer(String id) {
		if(_mag_frame_tbl != null && id != null && id.length() > 0 && _mag_frame_tbl.containsKey(id)) {
			return _mag_frame_tbl.get(id);
		}else {
			return null;
		}
	}
	
	/*protected boolean isURLVisiting(String url) {
		if(_mag_frame_tbl != null) {
			Enumeration<String> frame_id = _mag_frame_tbl.keys();
			while(frame_id.hasMoreElements()) {
				String id = frame_id.nextElement();
				MAGDocumentContainerFieldInterface frame = _mag_frame_tbl.get(id);
				if(frame.getMAGDocumentField().getURL().equals(url)) {
					return true;
				}
 			}
		}
		return false;
	}*/

	public void disableScroll() {
		getMAGDocumentField().disableScroll();
	}
}
