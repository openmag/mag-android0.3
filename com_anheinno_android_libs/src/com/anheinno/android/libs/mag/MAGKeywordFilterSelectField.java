/**
 * MAGKeywordFilterSelectField.java
 *
 * Copyright 2007-2011 anhe.
 */

package com.anheinno.android.libs.mag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.util.URLObjectRepository;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * 2011-2-13
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 * 
 */
public class MAGKeywordFilterSelectField extends AutoCompleteTextView {
	private MAGKeywordFilterSelect _keywfs;
	private JSONArray _options;
	private boolean _multichoice;
	private JSONArray _value;
	private FilterScreen _filterscreen;

	private MAGKeywordFilterSelectField _self;

	private boolean _isDownloading;

	private Vector<String> _show_text;
	private Vector<String> _show_value;

	public MAGKeywordFilterSelectField(Context context, MAGKeywordFilterSelect keywfs) {
		super(context);
		_self = this;
		_keywfs = keywfs;
		_multichoice = _keywfs.isMultichoice();

		if (_keywfs.getInitValue() != null && _keywfs.getInitValue().length() > 0) {
			try {
				_value = new JSONArray(_keywfs.getInitValue());
			} catch (final Exception e) {
			}
		}

		_show_text = new Vector<String>();
		_show_value = new Vector<String>();

		// _initString = "";

		if (_value != null) {
			try {
				JSONObject o;
				int size = _value.length();
				String text;
				for (int i = 0; i < size; i++) {
					o = _value.getJSONObject(i);
					text = o.getString("_text");
					if (o.has("_value") && o.getString("_value").length() > 0) {
						_show_value.addElement(o.getString("_value"));
					} else {
						_show_value.addElement(text);
					}
					_show_text.addElement(text);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		// _font = Font.getDefault();
		_isDownloading = true;
	}

	public boolean isDownloading() {
		return _isDownloading;
	}

	/**
	 * 查询数据和控件数据分开下载
	 */
	public void initScreen(String url) {
		int size = _show_text.size();

		// 只支持单选
		String text = "";
		for (int i = 0; i < size; i++) {
			// 初始显示value是否为text
			text = (String) _show_text.elementAt(i);
		}
		_self.setText(text);
		_self.setRawInputType(InputType.TYPE_CLASS_TEXT);

		if (url == null || url.length() == 0) {
			_filterscreen = new FilterScreen(_self, isMultichoice(), initdata());
		} else {
			// _filterscreen = getCachedFilterScreen(url);
			// if (_filterscreen == null) {
			_filterscreen = new FilterScreen(_self, isMultichoice(), url);
			// cacheFilterScreen(url, _filterscreen);
			// }
		}
		_isDownloading = false;

	}

	private Vector<JSONData> initdata() {
		_options = _keywfs.getOptions();

		Vector<JSONData> data = new Vector<JSONData>();
		int size = _options.length();
		for (int i = 0; i < size; i++) {
			try {
				data.addElement(new JSONData(_options.getJSONObject(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public boolean isMultichoice() {
		return _multichoice;
	}

	public synchronized JSONArray getSelectValue() {
		String text = _self.getText().toString();
		if (text != null && text.length() > 0) {
			if (!(_show_text.size() > 0 && text.endsWith(_show_text.elementAt(0)))) {
				ListAdapter listdata = _self.getAdapter();

				int size = listdata.getCount();
				for (int i = 0; i < size; i++) {
					String sel_text = (String) listdata.getItem(i);
					if (text.endsWith(sel_text)) {
						_show_value.add(sel_text);
						break;
					}
				}
			}
		} else {
			_show_value = null;
		}
		JSONArray data = new JSONArray();
		if (_show_value == null) {
			return data;
		}
		int size = _show_value.size();
		String value = null;
		for (int i = 0; i < size; i++) {
			value = (String) _show_value.elementAt(i);
			data.put(value);
		}
		return data;
	}
}

/**
 * options 数据格式化
 */
class JSONData {
	private String _text;
	private String _value;
	private JSONArray _keywords;

	public JSONData(JSONObject o) {
		try {
			_text = o.getString("_text").trim();

			if (o.has("_keywords")) {
				_keywords = o.getJSONArray("_keywords");
			} else {
				_keywords = new JSONArray();
			}

			if (o.has("_value")) {
				_value = o.getString("_value");
			} else {
				_value = o.getString("_text");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String get_keywords() {
		return _keywords.toString();
	}

	public String get_text() {
		return _text;
	}

	public String get_value() {
		return _value;
	}
}


class DataList {
	private Vector<JSONData> _jsondata;

	public DataList(Vector<JSONData> jsondata) {
		_jsondata = jsondata;
	}

	public String[] getValue() {
		int length = _jsondata.size();
		String[] data = new String[length];
		for (int i = 0; i < length; i++) {
			data[i] = _jsondata.elementAt(i).get_value();
		}
		return data;
	}

	public String[] getKeyword() {
		int length = _jsondata.size();
		String[] data = new String[length];
		for (int i = 0; i < length; i++) {
			data[i] = _jsondata.elementAt(i).get_keywords();
		}
		return data;
	}
}

class FilterScreen {
	private DataList _datalist;
	private boolean _multichoice;
	private Vector<String> _value;
	private Vector<String> _text;
	private MAGKeywordFilterSelectField _magfsf;

	public FilterScreen(MAGKeywordFilterSelectField magfsf, boolean multi, Vector<JSONData> data) {
		this(magfsf, multi, data, null);
	}

	public FilterScreen(MAGKeywordFilterSelectField magfsf, boolean multi, String url) {
		this(magfsf, multi, null, url);
	}

	private FilterScreen(MAGKeywordFilterSelectField magfsf, boolean multi, Vector<JSONData> data, String url) {

		// _dfm = new VerticalFieldManager(Manager.VERTICAL_SCROLL |
		// Manager.VERTICAL_SCROLLBAR);

		// _ksf = ksf;
		_multichoice = multi;
		// _keywordFilterField = new KeywordFilterField();

		_magfsf = magfsf;
		// url优先
		if (url != null && url.length() > 0) {
			_datalist = (DataList) URLObjectRepository.get(url);
			// System.out.println("_datalist:" + _datalist.toString());
		} else {
			_datalist = new DataList(data);
		}
		String[] value = _datalist.getValue();
		String[] keyword = _datalist.getKeyword();

		KeyAdapter<String> adapter = new KeyAdapter<String>(magfsf.getContext(), android.R.layout.simple_dropdown_item_1line, value, keyword);
		magfsf.setAdapter(adapter);
		magfsf.setThreshold(2);

		// _keywordFilterField.setSourceList(_datalist, _datalist);
		//
		// CustomKeywordField customSearchField = new CustomKeywordField();
		// _keywordFilterField.setKeywordField(customSearchField);
		//
		// dfm.add(customSearchField);
		// _dfm.add(_keywordFilterField);
		// dfm.add(_dfm);

		_value = null;
		_text = null;
	}

	protected Vector<String> getValue() {
		return _value;
	}

	protected Vector<String> getText() {
		return _text;
	}

	public void doModal(final String data) {
		if (_value != null) {
			_value.removeAllElements();
		}
		if (_text != null) {
			_text.removeAllElements();
		}
		setKeyword("");// 不保留上次查询记录]
		setDefaultFocus(data);
		// UiApplication.getUiApplication().pushModalScreen(_self);
	}

	private void setKeyword(String text) {
		// if (_keywordFilterField != null) {
		// if (text == null || text != null && text.length() == 0) {
		// _keywordFilterField.setKeyword(null);
		// } else {
		// _keywordFilterField.setKeyword(text);
		// }
		// }
	}

	/**
	 * @param value
	 *            初始化选中项
	 */
	private void setDefaultFocus(String value) {
		if (value == null || value.length() == 0) {
			// _keywordFilterField.setSelectedIndex(0);
			return;
		}
		// int index = 0;

		System.out.println("Init select for " + value);

		// ReadableList data = _keywordFilterField.getResultList();
		// int size = data.size();
		//
		// // 查找还需调整
		// for (int i = 0; i < size; i++) {
		// if (value.equalsIgnoreCase(((JSONData) data.getAt(i)).get_value())) {
		// _keywordFilterField.setSelectedIndex(i);
		// break;
		// }
		// }
	}

	public void invokeAction() {
		if (_value == null) {
			_value = new Vector<String>();
		}
		if (_text == null) {
			_text = new Vector<String>();
		}
		if (_multichoice) {

			// //////////////////////////////////////
		} else {
			ListAdapter listada = _magfsf.getAdapter();
			JSONData o = (JSONData) listada.getItem(_magfsf.getSelectionEnd());

			if (o != null) {
				System.out.println("XXX select " + o.get_value() + " / " + o.get_text());
				_value.addElement(o.get_value());
				_text.addElement(o.get_text());
			}
		}
	}

	public boolean onSavePrompt() {
		return true;
	}
}

class KeyAdapter<T> extends BaseAdapter implements Filterable {
	private List<T> _value;
	private List<T> _keyword;
	private final Object _lock = new Object();
	private int _resource;
	private int _dropDownResource;
	private int _fieldId = 0;
	private boolean _notifyOnChange = true;
	private Context _context;
	private ArrayList<T> _originalValues;
	private ArrayFilter _filter;
	private LayoutInflater _inflater;

	public KeyAdapter(Context context, int textViewResourceId) {
		init(context, textViewResourceId, 0, new ArrayList<T>(), new ArrayList<T>());
	}

	public KeyAdapter(Context context, int resource, int textViewResourceId) {
		init(context, resource, textViewResourceId, new ArrayList<T>(), new ArrayList<T>());
	}

	public KeyAdapter(Context context, int textViewResourceId, T[] objects, T[] objects2) {
		init(context, textViewResourceId, 0, Arrays.asList(objects), Arrays.asList(objects2));
	}

	public KeyAdapter(Context context, int resource, int textViewResourceId, T[] objects, T[] objects2) {
		init(context, resource, textViewResourceId, Arrays.asList(objects), Arrays.asList(objects2));
	}

	public KeyAdapter(Context context, int textViewResourceId, List<T> objects, List<T> objects2) {
		init(context, textViewResourceId, 0, objects, objects2);
	}

	public KeyAdapter(Context context, int resource, int textViewResourceId, List<T> objects, List<T> objects2) {
		init(context, resource, textViewResourceId, objects, objects2);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		_notifyOnChange = true;
	}

	public void setNotifyOnChange(boolean notifyOnChange) {
		_notifyOnChange = notifyOnChange;
	}

	private void init(Context context, int resource, int textViewResourceId, List<T> objects, List<T> objects2) {
		_context = context;
		_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_resource = _dropDownResource = resource;
		_value = objects;
		_keyword = objects2;
		_fieldId = textViewResourceId;
	}

	public Context getContext() {
		return _context;
	}

	public int getCount() {
		return _value.size();
	}

	public T getItem(int position) {
		return _value.get(position);
	}

	public int getPosition(T item) {
		return _value.indexOf(item);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, _resource);
	}

	private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
		View view;
		TextView text;

		if (convertView == null) {
			view = _inflater.inflate(resource, parent, false);
		} else {
			view = convertView;
		}

		try {
			if (_fieldId == 0) {
				text = (TextView) view;
			} else {
				text = (TextView) view.findViewById(_fieldId);
			}
		} catch (ClassCastException e) {
			throw new IllegalStateException("ArrayAdapter requires the resource ID to be a TextView", e);
		}

		text.setText(getItem(position).toString());

		return view;
	}

	public void setDropDownViewResource(int resource) {
		this._dropDownResource = resource;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, _dropDownResource);
	}

	public static ArrayAdapter<CharSequence> createFromResource(Context context, int textArrayResId, int textViewResId) {
		CharSequence[] strings = context.getResources().getTextArray(textArrayResId);
		return new ArrayAdapter<CharSequence>(context, textViewResId, strings);
	}

	public Filter getFilter() {
		if (_filter == null) {
			_filter = new ArrayFilter();
		}
		return _filter;
	}

	private class ArrayFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (_originalValues == null) {
				synchronized (_lock) {
					_originalValues = new ArrayList<T>(_value);
				}
			}

			if (prefix == null || prefix.length() == 0) {
				synchronized (_lock) {
					ArrayList<T> list = new ArrayList<T>(_originalValues);
					results.values = list;
					results.count = list.size();
				}
			} else {
				String prefixString = prefix.toString().toLowerCase();

				final ArrayList<T> values = _originalValues;
				final int count = values.size();

				final ArrayList<T> newValues = new ArrayList<T>(count);

				for (int i = 0; i < count; i++) {
					final T value = values.get(i);
					final String valueText = value.toString().toLowerCase();
					try {
						final JSONArray valueKey = new JSONArray((String) _keyword.get(i));
						int size = valueKey.length();

						for (int j = 0; j < size; j++) {
							final String value2 = valueKey.getString(j);

							final String valueText2 = value2.toString().toLowerCase();

							if (valueText2.startsWith(prefixString)) {
								// if (!newValues.contains(value)) {
								newValues.add(value);
								break;
								// }
							} else if (valueText.startsWith(prefixString)) {
								// if (!newValues.contains(value)) {
								newValues.add(value);
								break;
								// }
							}
						}
					} catch (Exception e) {
					}
				}
				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {

			_value = (List<T>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
}
