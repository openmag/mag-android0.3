/**
 * MAGKeywordFilterSelect.java
 *
 * Copyright 2007-2011 anhe.
 */
package com.anheinno.android.libs.mag;

import java.util.Hashtable;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.anheinno.android.libs.DownloadConsumer;
import com.anheinno.android.libs.DownloadManager;
import com.anheinno.android.libs.HTTPRequestString;
import com.anheinno.android.libs.R;
import com.anheinno.android.libs.ui.EasyDialog;
import com.anheinno.android.libs.ui.UiApplication;
import com.anheinno.android.libs.util.URLObjectRepository;

/**
 * 2011-2-13
 * 
 * MAGKeywordFilterSelect为允许用户从多个选项中，通过关键字过滤选项，并选择一个或多个选项的输入组件。呈现形式为，用户点击该组件后，
 * 弹出一个选项列表，用户可以输入关键字过滤选项。如果多选，则选项前有复选框（checkbox），否则没有。<br>
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 * 
 */
public class MAGKeywordFilterSelect extends MAGInputBase implements DownloadConsumer {
	private static final int CACHE_EXPIRE = 60 * 24 * 3600 * 1000;

	private JSONArray _options;
	private boolean _multichoice;
	private String _url;

	private boolean _isHasSrc;
	private boolean _isHasSrcData;

	public MAGKeywordFilterSelect() {
		super();
		_isHasSrc = false;
		_isHasSrcData = false;
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if (o.has("_options")) {
				_options = o.getJSONArray("_options");
			}
			if (o.has("_src")) {
				_url = o.getString("_src");

				HTTPRequestString req = new HTTPRequestString();
				if (req.parse(_url)) {
					_url = req.getURL();
				}
				_isHasSrc = true;
			}

			if (o.has("_multichoice")) {
				if (o.getString("_multichoice").equals("true")) {
					_multichoice = true;
				} else {
					_multichoice = false;
				}
			} else {
				_multichoice = false;
			}
			super.fromJSON(o);

		} catch (JSONException e) {
		}

		return true;
	}

	public String getAttributeValue(String fieldname) {
		return super.getAttributeValue(fieldname);
	}

	private void getData() {
		if (_isHasSrc) {
			Hashtable<String, String> cookie = new Hashtable<String, String>();
			String ea_email = getActivateEmail();
			if (ea_email != null && ea_email.length() > 0) {
				cookie.put("X-Anhe-Account-Username", ea_email);
				cookie.put("X-Anhe-Account-Password", ea_email);
				cookie.put("X-Anhe-Push-Protocol", MAGDocumentConfig.getPushProtocol(getContext()));
				cookie.put("X-Anhe-Push-Server", MAGDocumentConfig.getPushServer(getContext()));
			}

			if (!URLObjectRepository.has(_url)) {
				_isHasSrcData = false;
				DownloadManager.getDownloadManager().registerDownloadTask(getContext(), _url, cookie, false, CACHE_EXPIRE, false, null, this, null);
			} else {
				_isHasSrcData = true;
			}
		}
	}

	public String fetchValue() {
		JSONArray data = ((MAGKeywordFilterSelectField) (((MAGColorLabelManager) getField()).getField())).getSelectValue();
		String value = null;

		if (data.length() > 0) {
			if (_multichoice) {
				value = data.toString();
			} else {
				try {
					value = data.getString(0);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else {
			value = "";
		}
		return value;
	}

	public boolean validate() {
		JSONArray data = ((MAGKeywordFilterSelectField) (((MAGColorLabelManager) getField()).getField())).getSelectValue();
		if (data.length() > 0) {
			return true;
		} else {
			if (getInitVmsg() != null && getInitVmsg().length() > 0) {
				EasyDialog.longAlert(getContext(), getInitVmsg());
			} else {
				String data_show = title();
				if (data_show.length() > 1 && (data_show.endsWith(":") || data_show.endsWith("："))) {
					data_show = data_show.substring(0, data_show.length() - 1) + "!";
				}
				EasyDialog.longAlert(getContext(), getContext().getString(R.string.mag_text_input_cannot_be_empty) + data_show);
			}
			return false;
		}
	}

	private static String getActivateEmail() {
		// if (Session.getDefaultInstance() != null &&
		// Session.getDefaultInstance().getServiceConfiguration() != null) {
		// return
		// Session.getDefaultInstance().getServiceConfiguration().getEmailAddress();
		// } else {
		return null;
		// }
	}

	public View initField(Context context) {
		MAGColorLabelManager clm = new MAGColorLabelManager(context, this);
		MAGKeywordFilterSelectField magkfs = new MAGKeywordFilterSelectField(context, this);
		magkfs.setText(getContext().getString(R.string.mag_keywordrfilterselect_select_screen));
		magkfs.setRawInputType(InputType.TYPE_NULL);

		if (!_isHasSrc) {
			magkfs.initScreen(null);
		}
		clm.setField(magkfs, true, true);
		
		getData();
		if (_isHasSrc && _isHasSrcData) {
			startShow();
		}
		return clm;
	}

	public boolean isMultichoice() {
		return _multichoice;
		// return false;
	}

	public JSONArray getOptions() {
		return _options;
	}

	public void updateField(View f) {
		// ((MAGKeywordFilterSelectField) (((MAGColorLabelManager)
		// f).getField())).updateUi();
	}

	public void dataArrival(JSONObject a, Object params) {
		try {
			if (!checkMandatory(a, "_options")) {
				return;
			}

			JSONArray options = a.getJSONArray("_options");
			Vector<JSONData> data = new Vector<JSONData>();
			int size = options.length();
			for (int i = 0; i < size; i++) {
				try {
					data.addElement(new JSONData(options.getJSONObject(i)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			URLObjectRepository.put(_url, new DataList(data));
		} catch (JSONException e) {
			System.out.println("MAGKeywordFilterSelect startShow()" + e.toString());
			e.printStackTrace();
		}
		startShow();
	}

	private void startShow() {
		((UiApplication) getContext()).invokeLater(new Runnable() {
			public void run() {
				MAGKeywordFilterSelectField view = ((MAGKeywordFilterSelectField) (((MAGColorLabelManager) getField()).getField()));
				view.initScreen(_url);
			}
		});
	}

	public boolean retrieveError(String msg) {
		return false;
	}

	public void onShowUi() {

	}
}

// class SrcRepository {
// private static Hashtable _src_tbl;
// static {
// _src_tbl = new Hashtable();
// }
//
// public static boolean isHasSrc(String src) {
// if (_src_tbl.containsKey(src)) {
// return true;
// } else {
// return false;
// }
// }
//
// public static JSONArray getSrcByName(String name) {
// if (_src_tbl.containsKey(name)) {
// return (JSONArray) _src_tbl.get(name);
// } else {
// return null;
// }
// }
//
// public static void setSrcByName(String name, JSONArray options) {
// if (!_src_tbl.containsKey(name)) {
// _src_tbl.put(name, options);
// }
// }
//
// }
