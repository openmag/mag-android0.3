/*
 * CNAFOALoginScreen.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import org.json.JSONException;
import org.json.JSONObject;

import com.anheinno.android.libs.HTTPRequestString;
import com.anheinno.android.libs.HttpRequestDialog;
import com.anheinno.android.libs.JSONBrowserConfig;
import com.anheinno.android.libs.JSONBrowserConfigScreen;
import com.anheinno.android.libs.R;
import com.anheinno.android.libs.HttpRequestDialog.HttpRequestDialogListener;
import com.anheinno.android.libs.JSONObjectCacheDatabase;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.GraphicUtilityClass;
import com.anheinno.android.libs.graphics.TextDrawArea;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.ui.EasyDialog;
import com.anheinno.android.libs.ui.FullScreen;
import com.anheinno.android.libs.ui.ScreenMenu;
import com.anheinno.android.libs.ui.ScreenMenuItem;
import com.anheinno.android.libs.ui.TextInputDialog;
import com.anheinno.android.libs.ui.UiApplication;
import com.anheinno.android.libs.ui.TextInputDialog.TextInputDialogListener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
class MAGLoginScreen extends FullScreen {

	private MAGLoginScreenConfiguration _config;
	private MAGLoginScreenListener _callback;
	private View _login_pane;
	private TextDrawArea _title_area;
	
	public interface MAGLoginScreenListener {
		public void onLoginSuccess(String url);
	}

	MAGLoginScreen(Context context, MAGLoginScreenListener callback, MAGLoginScreenConfiguration config) {
		super(context);

		hideCloseMenu();
		setCancellable(false);

		// getUiApplication().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		_callback = callback;

		_config = config;
		if (_config == null) {
			_config = MAGLoginScreenConfiguration.getDefault();
		}
		
		_title_area = new TextDrawArea(context, _config._title, _config._title_style);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_login_pane = inflater.inflate(R.layout.mag_login, null);
		addView(_login_pane);

		/*
		 * ImageButton ib_config = (ImageButton) findViewById(R.id.ib_config);
		 * View.OnClickListener config_listener = new View.OnClickListener() {
		 * public void onClick(View v) { MAGDocumentConfigScreen sc = new
		 * MAGDocumentConfigScreen(getContext()); sc.show(); } };
		 * ib_config.setOnClickListener(config_listener);
		 */

		/*if (config._title != null) {
			TextView tv_login_title = (TextView) findViewById(R.id.tv_login_title);
			tv_login_title.setTextColor(config._text_color);
			tv_login_title.setText(config._title);
		}*/
		
		TextView tv_login_username = (TextView) findViewById(R.id.tv_login_username);
		tv_login_username.setTextColor(config._text_color);
		
		EditText et_username = (EditText) findViewById(R.id.et_username);
		String uname = MAGDocumentConfig.getUsername(context);
		if (uname.length() > 0) {
			et_username.setText(uname);
		} else {
			et_username.requestFocus();
		}

		if (JSONBrowserConfig.isLockAccount(getContext())) {
			et_username.setEnabled(false);
			et_username.setFocusable(false);
			et_username.setFocusableInTouchMode(false);
		}
		
		TextView tv_login_password = (TextView) findViewById(R.id.tv_login_passwd);
		tv_login_password.setTextColor(config._text_color);
		
		EditText et_password = (EditText) findViewById(R.id.et_passwd);
		if (uname.length() > 0) {
			et_password.requestFocus();
		}

		View.OnClickListener login_listener = new View.OnClickListener() {
			public void onClick(View v) {
				//EditText et_username = (EditText) findViewById(R.id.et_username);
				/*
				 * String username = et_username.getText().toString().trim();
				 * 
				 * if (username.length() == 0) { Toast.makeText(getContext(),
				 * getContext
				 * ().getString(R.string.mag_login_screen_check_alert_account),
				 * Toast.LENGTH_SHORT).show(); return; }
				 */

				HTTPRequestString req = new HTTPRequestString();
				if (MAGDocumentConfig.getLoginURL(getContext()).length() == 0 || !req.parse(MAGDocumentConfig.getLoginURL(getContext()))) {
					TextInputDialogListener url_input_listener = new TextInputDialogListener() {
						public void onTextSet(TextInputDialog dialog, String text) {
							HTTPRequestString req = new HTTPRequestString();
							if (text.length() > 0 && req.parse(text)) {
								MAGDocumentConfig.setLoginURL(getContext(), req.getURL());
								login_process(req);
							} else {
								Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
							}
						}
					};
					TextInputDialog input_dialog = new TextInputDialog(getContext(), url_input_listener, getContext().getString(
							R.string.mag_login_screen_input_url_prompt), "https://");
					input_dialog.show();
				} else {
					login_process(req);
				}
			}
		};
		
		CheckBox bind = (CheckBox) findViewById(R.id.btn_bind);
		bind.setTextColor(config._text_color);

		Button login = (Button) findViewById(R.id.btn_login);
		login.setOnClickListener(login_listener);

		View.OnClickListener quit_listener = new View.OnClickListener() {
			public void onClick(View v) {
				((Activity) v.getContext()).finish();
			}
		};
		Button quit = (Button) findViewById(R.id.btn_quit);
		quit.setOnClickListener(quit_listener);
	}

	private void login_process(HTTPRequestString req) {
		try {
			CheckBox bind = (CheckBox) findViewById(R.id.btn_bind);
			EditText username = (EditText) findViewById(R.id.et_username);
			EditText password = (EditText) findViewById(R.id.et_passwd);

			req.addParam("_action", "LOGIN");
			req.addParam("_bind", bind.isChecked() ? "true" : "false");

			HttpRequestDialogListener http_listener = new HttpRequestDialogListener() {

				public void onGetJSONResult(JSONObject json) {
					try {
						CheckBox bind = (CheckBox) findViewById(R.id.btn_bind);
						EditText username = (EditText) findViewById(R.id.et_username);
						EditText password = (EditText) findViewById(R.id.et_passwd);

						String url = json.getString("_redirect");
						HTTPRequestString req = new HTTPRequestString();
						if (req.parse(MAGDocumentConfig.getLoginURL(getContext()))) {
							if (req.relative(url)) {
								url = req.getURL(false);
							}
						}
						System.out.println("Redirect to " + url);

						MAGDocumentConfig.setMainEntryURL(getContext(), url);

						String uname = "";
						if (json.has("_account") && json.getString("_account").length() > 0) {
							uname = json.getString("_account");
						} else {
							uname = username.getText().toString().trim();
						}
						if (!uname.equals(MAGDocumentConfig.getUsername(getContext()))) {
							JSONObjectCacheDatabase.purgeAllObject(getContext());
						}
						MAGDocumentConfig.setUsername(getContext(), uname);

						boolean lock_account = false;
						if (json.has("_lock_account") && json.getString("_lock_account").length() > 0) {
							lock_account = json.getString("_lock_account").equalsIgnoreCase("true") ? true : false;
						}
						JSONBrowserConfig.setLockAccount(getContext(), lock_account);

						if (json.has("_config") && json.getJSONObject("_config") != null) {
							dealLoginConfig(json.getJSONObject("_config"));
						}

						if (bind.isChecked()) {
							MAGDocumentConfig.setBind(getContext(), true);
							JSONObjectCacheDatabase.purgeAllObject(getContext());
						} else {
							MAGDocumentConfig.setBind(getContext(), false);
						}
						MAGDocumentConfig.setPassword(getContext(), password.getText().toString());
						MAGDocumentConfig.setVerified(getContext(), true);
						
						System.out.println("Verified = " + MAGDocumentConfig.isVerified(getContext()));

						if (_callback != null) {
							_callback.onLoginSuccess(url);
						}

						close();

					} catch (final JSONException e) {
						EasyDialog.remind(getContext(), "JSONException: " + e.getMessage());
					}
				}

				public boolean onError(String msg) {
					return false;
				}

			};

			HttpRequestDialog http = new HttpRequestDialog(getContext(), req.getURL(), http_listener);

			http.addHeader("X-Anhe-Account-Username", username.getText().toString().trim());
			http.addHeader("X-Anhe-Account-Password", password.getText().toString());
			http.addHeader("X-Anhe-Push-Protocol", MAGDocumentConfig.getPushProtocol(getContext()));
			http.addHeader("X-Anhe-Push-Server", MAGDocumentConfig.getPushServer(getContext()));

			http.start();

		} catch (Exception e) {
			EasyDialog.remind(getContext(), e.getMessage());
		}
	}

	/**
	 * @param config
	 *            第一次登陆后服务器传回来的配置信息
	 */
	private void dealLoginConfig(JSONObject config) {
		try {
			if (config.has("_cache_enabled") && config.getString("_cache_enabled").length() > 0) {
				boolean _cache_enabled = config.getString("_cache_enabled").equalsIgnoreCase("true") ? true : false;
				JSONBrowserConfig.setCacheEnabled(getContext(), _cache_enabled);
			}

			if (config.has("_cache_default_expire")) {
				int _cache_default_expire = config.getInt("_cache_default_expire");
				JSONBrowserConfig.setCacheExpireHours(getContext(), _cache_default_expire / (1000 * 60 * 60));
			}

			if (config.has("_relay_enabled") && config.getString("_relay_enabled").length() > 0) {
				boolean _relay_enabled = config.getString("_relay_enabled").equalsIgnoreCase("true") ? true : false;
				JSONBrowserConfig.setUseRelay(getContext(), _relay_enabled);
			}

			if (config.has("_relay_server_uri") && config.getString("_relay_server_uri").length() > 0) {
				String _relay_server_uri = config.getString("_relay_server_uri");
				JSONBrowserConfig.setRelayServer(getContext(), _relay_server_uri);
			}

			if (config.has("_service_uri") && config.getString("_service_uri").length() > 0) {
				String _service_uri = config.getString("_service_uri");
				MAGDocumentConfig.setLoginURL(getContext(), _service_uri);
			}

			if (config.has("_password_protected") && config.getString("_password_protected").length() > 0) {
				boolean _password_protected = config.getString("_password_protected").equalsIgnoreCase("true") ? true : false;
				MAGDocumentConfig.setPasswordProtect(getContext(), _password_protected);
			}

			if (config.has("_attachment_service_uri") && config.getString("_attachment_service_uri").length() > 0) {
				String _attachment_service_uri = config.getString("_attachment_service_uri");
				MAGDocumentConfig.setImageServerURL(getContext(), _attachment_service_uri);
			}

			if (config.has("_push_protocol") && config.getString("_push_protocol").length() > 0) {
				String _push_protocol = config.getString("_push_protocol");
				MAGDocumentConfig.setPushProtocol(getContext(), _push_protocol);
			}

			if (config.has("_push_server") && config.getString("_push_server").length() > 0) {
				String _push_server = config.getString("_push_server");
				MAGDocumentConfig.setPushServer(getContext(), _push_server);
			}

		} catch (JSONException e) {
			System.out.println("MAGLoginScreen dealLoginConfig() " + e.toString());
		}
	}

	/*
	 * public boolean onKeyUp (KeyEvent event) { if (event.getKeyCode() ==
	 * KeyEvent.KEYCODE_BACK) { // do nothing return true; } return
	 * super.onKeyUp(event); } public boolean onKeyDown (KeyEvent event) { if
	 * (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { // do nothing return
	 * true; } return super.onKeyDown(event); }
	 */

	protected void measureChildren(int width, int height) {
		synchronized(this) {
			int w_mode = MeasureSpec.AT_MOST;
			int h_mode = MeasureSpec.UNSPECIFIED;
			if(_config._pop_width > 0) {
				width = _config._pop_width - _config._pane_padding_left - _config._pane_padding_right;
				w_mode = MeasureSpec.EXACTLY;
			}else {
				width -= _config._pop_padding_left + _config._pop_padding_right + _config._pane_padding_left + _config._pane_padding_right;
			}
			if(_config._pop_height > 0) {
				height = _config._pop_height - _config._pane_padding_top - _config._pane_padding_bottom;
				//h_mode = MeasureSpec.EXACTLY;
			}else {
				height -= _config._pop_padding_top + _config._pop_padding_bottom + _config._pane_padding_top + _config._pane_padding_bottom;
			}
			_title_area.setWidth(width);
			_login_pane.measure(MeasureSpec.makeMeasureSpec(width, w_mode), MeasureSpec.makeMeasureSpec(height, h_mode));
		}
	}
	
	protected void subLayoutChildren(int l, int t, int width, int height) {
		synchronized(this) {
			int left = _config.getPaneLeft(width, getPaneWidth());
			int top = _config.getPaneTop(height, getPaneHeight());
			
			_login_pane.layout(left + (getPaneWidth() - _login_pane.getMeasuredWidth())/2, (int)(top + _title_area.getHeight()), left + (getPaneWidth() + _login_pane.getMeasuredWidth())/2, top + getPaneHeight());
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		synchronized(this) {
			int screen_width = super.getClientWidth();
			int screen_height = super.getClientHeight();
			
			//System.out.println("screen_width=" + screen_width + " screen_height=" + screen_height);
	
			if(screen_width > 0 && screen_height > 0) {
				if (_config._bg != null) {
					_config._bg.draw(getContext(), canvas, 0, 0, screen_width, screen_height);
				}
		
				if (_login_pane.getMeasuredWidth() > 0 && getPaneHeight() > 0) {
					if(_config._pop_bg != null) {
						_config._pop_bg.draw(getContext(), canvas, 
							_config.getPopLeft(screen_width, getPaneWidth()), 
							_config.getPopTop(screen_height, getPaneHeight()), 
							_config.getPopWidth(getPaneWidth()),
							_config.getPopHeight(getPaneHeight()));
					}
					
					_title_area.draw(canvas, 
							_config.getPaneLeft(screen_width, getPaneWidth()) + 
									(getPaneWidth() - _title_area.getWidth())/2,
							_config.getPaneTop(screen_height, getPaneHeight()));
				}
				
			}
	
			super.onDraw(canvas);
		}
	}
	
	private int getPaneWidth() {
		return (int)Math.max(_login_pane.getMeasuredWidth(), _title_area.getWidth());
	}
	private int getPaneHeight() {
		return (int)(_login_pane.getMeasuredHeight() + _title_area.getHeight());
	}

	ScreenMenuItem _mi_config = new ScreenMenuItem(getContext().getString(R.string.json_screen_menu_options), 2) {
		
		protected void initIcons() {
			setMenuIconId(android.R.drawable.ic_menu_manage);
			setBarIconName("menubar_icon_option.png");
			setFocusBarIconName("menubar_icon_option_focus.png");
		}
		
		public void run() {
			JSONBrowserConfigScreen cfs = new MAGDocumentConfigScreen(getContext());
			cfs.show();
		}
		
	};

	protected void makeMenu(ScreenMenu menu) {
		super.makeMenu(menu);
		menu.add(_mi_config);
	}

	protected void onClose() {

		_config = null;
		_callback = null;
		
		removeView(_login_pane);
		_login_pane = null;
		
		super.onClose();
	}
}
