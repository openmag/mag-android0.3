/*
 * MAGSubmit.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.anheinno.android.libs.HTTPRequestString;
import com.anheinno.android.libs.JSONBrowserLink;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.ui.CustomButtonField;
import com.anheinno.android.libs.ui.EasyDialog;
import com.anheinno.android.libs.ui.EasyDialog.ConfirmDialogListener;
import com.anheinno.android.libs.util.URLUTF8Encoder;


/**
 * MAGSubmit是触发提交包含该组件的MAGPanel的MAGInput组件的输入内容的组件。其呈现形式为一个有文字提示信息的按钮（Button）。<br>
 * SUBTYPE = "SUBMIT"
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGSubmit extends MAGInputBase {
	private String _action;
	private String _url;
	private String _target;
	private String _confirm;
	private String[] _require;

	public MAGSubmit() {
		super();
		_action = null;
		_url = null;
		_target = null;
		_confirm = null;
		_require = null;
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if(!checkMandatory(o, "_action")) {
				return false;
			}
			
			if(!checkMandatory(o, "_url")) {
				return false;
			}

			_action = o.getString("_action");

			_url = getMAGDocument().getAbsoluteURL(o.getString("_url"));
			
			if (o.has("_target")) {
				_target = o.getString("_target");
			} else {
				_target = MAGLink.LINK_TARGET_DEFAULT;
			}
			if(o.has("_confirm")) {
				_confirm = o.getString("_confirm");
			}

			if(o.has("_require")) {
				JSONArray list = o.getJSONArray("_require");
				_require = new String[list.length()];
				for(int i = 0; i < list.length(); i ++) {
					_require[i] = list.getString(i);
				}
			}

			return super.fromJSON(o);
		} catch (JSONException e) {
		}
		return false;
	}

	public String getAttributeValue(String fieldname) {
		if(fieldname.equals("_action")) {
			return _action;
		}else if(fieldname.equals("_url")) {
			return _url;
		}else if(fieldname.equals("_confirm")) {
			return _confirm;
		}else {
			return super.getAttributeValue(fieldname);
		}
	}

	// 2010-5-20 增加只读控制
	public View initField(Context context) {
		CustomButtonField cbf = null;
		if (!isReadOnly()) {
			OnClickListener listener = new OnClickListener() {
				public void onClick(View v) {
					if (_url.length() > 0) {
						if(validateInputs()) {
							if(_confirm != null && _confirm.length() > 0) {
								ConfirmDialogListener  confirm_listener = new ConfirmDialogListener() {
									public void onYes() {
										submitProcess();
									}
									public void onNo() {
										// do nothing
									}
								};
								EasyDialog.confirm(getContext(), _confirm, confirm_listener);
							}else {
								submitProcess();
							}
						}
					} else {
						EasyDialog.shortAlert(getContext(), "submit url is null!");
					}
				}

				
			};
			cbf = new MAGCustomButtonField(context, this, listener);
		} else {
			cbf = new MAGCustomButtonField(context, this, null);
		}
		updateField(cbf);
		return cbf;
	}
	
	private boolean validateInputs() {
		MAGInputInterface[] siblings = getInputSiblings();
		for (int i = 0; siblings != null && i < siblings.length; i++) {
			if (!(siblings[i] instanceof MAGSubmit)) {
				if(_require != null) {
					if(isRequired(siblings[i]) && !siblings[i].validate()) {
						if(siblings[i].getField() != null) {
							siblings[i].getField().requestFocus();
						}
						return false;
					}
				}else {
					if (siblings[i].isRequired() && !siblings[i].validate()) {
						if(siblings[i].getField() != null) {
							siblings[i].getField().requestFocus();
						}
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private void submitProcess() {
		String qstr = "";
		MAGInputInterface[] siblings = getInputSiblings();
		for (int i = 0; siblings != null && i < siblings.length; i++) {
			if (!(siblings[i] instanceof MAGSubmit)) {
				String sub_qstr = siblings[i].getQueryString();
				if(sub_qstr != null && sub_qstr.length() > 0) {
					if (qstr.length() > 0) {
						qstr += "&";
					}
					qstr += sub_qstr;
				}
			}
		}
		
		if (qstr.length() > 0) {
			qstr += "&";
		}
		qstr += getQueryString();
		qstr = _url + "?" + qstr;
		System.out.println("submit open url is " + qstr.toString());
		JSONBrowserLink link = new JSONBrowserLink(getContext());
		link.setURL(qstr);
		link.setExpireMilliseconds(0);
		link.setSaveHistory(false);
		// do not notify submit page change
		link.setNotify(false);
		
		if (_target.equals(MAGLink.LINK_TARGET_SELF)) {
			MAGDocumentField sc = getMAGDocument().getMAGDocumentField();
			sc.syncOpen(link, true, null); //qstr, 0, false, false, true, null);
		} else {
			MAGDocumentScreen sc = new MAGDocumentScreen(getContext(), link, true, getMAGDocumentScreen());
			sc.show();
		}
	}
	
	private boolean isRequired(MAGInputInterface input) {
		for(int i = 0; _require != null && i < _require.length; i ++) {
			if(_require[i].equals(input.id())) {
				return true;
			}
		}
		return false;
	}

	public void updateField(View f) {
			MAGCustomButtonField cbf = (MAGCustomButtonField)f;
			
			cbf.updateStyle();
			
			/*int w = style().getIWidth(getInnerWidth());
			//System.out.println(toString() + " prefered width: w=" + w);
			if (w > 0) {
				cbf.setPreferredWidth(w);
			}
			int h = style().getIHeight(getInnerHeight());
			//System.out.println(toString() + "prefered height: h=" + h);
			if (h > 0) {
				cbf.setPreferredHeight(h);
			}*/

			TextStyleDescriptor style = style().getTextStyle();
			if (style == null) {
				style = MAGStyleRepository.getButtonTextStyle();
			}
			cbf.setTextStyle(style);

			style = style().getTextStyle("focus");
			if (style == null) {
				style = MAGStyleRepository.getFocusButtonTextStyle();
			}
			cbf.setFocusTextStyle(style);

			BackgroundDescriptor bg_desc = style().getBodyBackground();
			if (bg_desc == null) {
				bg_desc = MAGStyleRepository.getButtonBackground();
			}
			cbf.setBackground(bg_desc);
			bg_desc = style().getFocusBodyBackground();
			if (bg_desc == null) {
				bg_desc = MAGStyleRepository.getFocusButtonBackground();
			}
			cbf.setFocusBackground(bg_desc);
	}

	protected boolean setAttribute(String name, String value) {
		if (name.equals("action")) {
			_action = value;
			return true;
		} else if (name.equals("target")) {
			_target = value;
			return true;
		}
		if (super.setAttribute(name, value)) {
			return true;
		}
		return false;
	}

	public String getQueryString() {
		return HTTPRequestString.getQueryString("_action", URLUTF8Encoder.encode(_action));
	}

	public boolean validate() {
		return true;
	}

	public String fetchValue() {
		return null;
	}
}
