/*
 * CNAFOAContactInput.java
 *
 * <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.libs.mag.controls.contacts;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import com.anheinno.android.libs.HTTPRequestString;
import com.anheinno.android.libs.R;
import com.anheinno.android.libs.UtilClass;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.mag.MAGCustominputControl;
import com.anheinno.android.libs.mag.MAGStyleRepository;
import com.anheinno.android.libs.mag.MAGTitleArea;
import com.anheinno.android.libs.ui.CustomButtonField;
import com.anheinno.android.libs.ui.EasyDialog;
import com.anheinno.android.libs.ui.Manager;
import com.anheinno.libs.mag.controls.contacts.ContactScreen.ContactScreenListener;


/**
 * 
 */
public class ContactInput extends MAGCustominputControl {
	
	private ContactInputField _field;
	
	public String getQueryString() {
		return _field.getQueryString();
	}

	public View initControl(Context context) {
		_field = new ContactInputField(context);
		return _field;
	}

	public void onShowUi() {
	}

	public boolean setAttribute(String name, String value) {
		return _field.setAttribute(name, value);
	}

	public void updateControl() {
		_field.updateControl();
	}

	public boolean validate() {
		return _field.validate();
	}
	
	class ContactInputField extends Manager {
		// private LabelField _accountlist;
		private MAGTitleArea _title_area;
		private CustomButtonField _sel_button;
		private BackgroundDescriptor _button_background;
		private BackgroundDescriptor _button_focus_background;
		private TextStyleDescriptor _button_text;
		private TextStyleDescriptor _button_text_focus;
	
		private AddContactListField _listField;
		private String _orgname;
		private String _account;
		private String _value;
		private int _limit;
	
		public static final int MAX_SELECTED_USER_COUNT = 20;
	
		public ContactInputField(Context context) {
			super(context);
			// _sel_button = null;
			_orgname = null;
			_account = null;
			_value = null;
			_limit = MAX_SELECTED_USER_COUNT;
		
			BackgroundDescriptor bg_desc = null;
			bg_desc = _backend.style().getBackground("button");
			if(bg_desc != null) {
				_button_background = bg_desc;
			}else{
				_button_background = MAGStyleRepository.getButtonBackground();
			}
			
			bg_desc =_backend.style().getBackground("focus-button");
			if(bg_desc != null) {
				_button_focus_background = bg_desc;
			}else{
				_button_focus_background = MAGStyleRepository.getFocusButtonBackground();
			}
			
			TextStyleDescriptor style = null;
			style = _backend.style().getTextStyle();
			if(style != null) {
				_button_text = style;
			}else{
				_button_text = MAGStyleRepository.getButtonTextStyle();
			}
			style = _backend.style().getTextStyle("focus");
			if(style != null) {
				_button_text_focus = style;
			}else{
				_button_text_focus = MAGStyleRepository.getFocusButtonTextStyle();
			}
			
			if (_backend.title().length() != 0) {
				_title_area = new MAGTitleArea(_backend);
			}
		
			if (_params.length() >= 4) {
				try {
					_limit = _params.getInt(3);
				} catch (final Exception e) {
					System.out.println("Failed to get limit!");
					_limit = MAX_SELECTED_USER_COUNT;
				}
				System.out.println("XXXX LIMIT = " + _limit);
			}
	
			if (!_backend.isReadOnly()) {
				OnClickListener sel_listener = new OnClickListener() {
					public void onClick(View view) {
						ContactScreenListener listener = new ContactScreenListener() {
							public void onSelectUser(ContactUser user) {
								_orgname = user.get_orgname();
								_account = user.get_account();
								System.out.println("selected: orgname=" + _orgname + " account=" + _account);
								_listField.add(user);
							}
						};
						ContactScreen sc = new ContactScreen(getContext(), UtilClass.JSONArray2StringArray(_params));
						sc.show(_orgname, _account, listener);
					}
				};
				
				_sel_button = new CustomButtonField(getContext(), getContext().getString(R.string.contact_input_select_button_text), sel_listener);
				
				_sel_button.setBackground(_button_background);
				_sel_button.setFocusBackground(_button_focus_background);
				_sel_button.setTextStyle(_button_text);
				_sel_button.setFocusTextStyle(_button_text_focus);
				addView(_sel_button);
			}
	
			_listField = new AddContactListField(getContext(), _backend.isReadOnly(), _limit);
			addView(_listField);
	
			_value = _backend.getInitValue();
			
			updateControl();
		}
	
		public void updateControl() {
			if (_value != null && _value.length() > 0) {
				try {
					JSONArray io = new JSONArray(_value);
					JSONObject o = null;
					int length = io.length();
					for (int i = 0; i < length; i++) {
						o = io.getJSONObject(i);
						_account = o.getString("_account");
						_orgname = o.getString("_orgname");
						if (_account != null && _account.length() > 0) {
							_listField.add(new ContactUser(o.getString("_name"), _account, _orgname));
						}
					}
				} catch (JSONException e) {
				}
			}
		}
	
		public boolean setAttribute(String name, String value) {
			if (name.equals("value")) {
				_value = value;
				return true;
			}
			return false;
		}
	
		private static final int PADDING = 3;
		
		@Override
		protected void layoutChildren(int l, int r, int width, int height) {
			int top = 0;
			int left = PADDING;
			if(!_backend.isReadOnly()) {
				top += PADDING;
				left = getMeasuredWidth() - _sel_button.getMeasuredWidth() - PADDING;
				_sel_button.layout(left, top, left + _sel_button.getMeasuredWidth(), top + _sel_button.getMeasuredHeight());
				top += _sel_button.getMeasuredHeight();
			}
			if(_title_area != null && top < _title_area.getTitleHeight()) {
				top = (int)_title_area.getTitleHeight();
			}
			top += PADDING;
			left = PADDING;
			_listField.layout(left, top, left + _listField.getMeasuredWidth(), top + _listField.getMeasuredHeight());
		}
	
		@Override
		protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);
			
			if (_title_area != null) {
				_title_area.layout(width);
			}
	
			if (!_backend.isReadOnly()) {
				measureChild(_sel_button, MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
			}
	
			measureChild(_listField, MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
	
			this.setMeasuredDimension(width, getPreferredHeight());
		}
	
		public int getPreferredHeight() {
			int h = (int)_title_area.getTitleHeight();
			if (!_backend.isReadOnly()) {
				if(h < _sel_button.getMeasuredHeight() + PADDING) {
					h = _sel_button.getMeasuredHeight() + PADDING;
				}
			}
			return _listField.getMeasuredHeight() + h;
		}
	
		public String getValue() {
			Vector<ContactUser> vector = _listField.getVector();
			JSONArray jsonarray = new JSONArray();
			if (vector != null) {
				JSONObject jsonobject = null;
				int length = vector.size();
				for (int i = 0; i < length; i++) {
					jsonobject = new JSONObject();
					ContactUser list = vector.elementAt(i);
					try {
						jsonobject.put("_name", list.get_name());
						jsonobject.put("_account", list.get_account());
						jsonobject.put("_orgname", list.get_orgname());
						jsonarray.put(jsonobject);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			return jsonarray.toString();
		}
	
		public String getQueryString() {
			return HTTPRequestString.getQueryString(id() + "_value", getValue());
		}
	
		public boolean validate() {
			if (_listField.getVector().size() == 0) {
				EasyDialog.longAlert(getContext(), getContext().getString(R.string.contact_input_invalid_prompt));
				return false;
			} else {
				return true;
			}
		}
	
		@Override
		protected void onDraw(Canvas canvas) {
			if (_title_area != null) {
				_title_area.drawTitle(canvas, 0, 0);
			}
			super.onDraw(canvas);
		}
	}
}
