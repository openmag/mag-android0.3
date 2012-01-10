/**
 * MAGInfoGridField.java
 *
 * Copyright 2007-2010 anhe.
 */
package com.anheinno.android.libs.mag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.anheinno.android.libs.JSONBrowserConfig;
import com.anheinno.android.libs.JSONBrowserLink;
import com.anheinno.android.libs.UtilClass;
import com.anheinno.android.libs.graphics.Align;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.BitmapRepository;
import com.anheinno.android.libs.graphics.GraphicUtilityClass;
import com.anheinno.android.libs.graphics.PaintRepository;
import com.anheinno.android.libs.graphics.Paragraph;
import com.anheinno.android.libs.graphics.TextDrawArea;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.ui.CustomButtonField;
import com.anheinno.android.libs.ui.Manager;

/**
 * 2010-11-5
 * 
 * @author shenrh
 * 
 * @version 1.0
 * 
 */
public class MAGInfoGridField extends Manager {

	private MAGTitleArea _title_area;
	private TextDrawArea _pager_area;
	private CustomSpinner _show_title;
	private CustomButtonField _select;
	
	private TextStyleDescriptor _selectbg_desc;
	private TextStyleDescriptor _unselectbg_desc;
	private TextStyleDescriptor _foselectbg_desc;
	private TextStyleDescriptor _founselectbg_desc;

	private Bitmap _selectsmbg;
	private Bitmap _unselectsmbg;

	private CustomButtonField _desc;
	private CustomButtonField _leftpage;
	private CustomButtonField _rightpage;

	private TextStyleDescriptor _pager_text_style;
	private TextStyleDescriptor _asc_button_style;
	private TextStyleDescriptor _asc_button_focus_style;
	private TextStyleDescriptor _desc_button_style;
	private TextStyleDescriptor _desc_button_focus_style;
	private TextStyleDescriptor _left_button_style;
	private TextStyleDescriptor _left_button_focus_style;
	private TextStyleDescriptor _right_button_style;
	private TextStyleDescriptor _right_button_focus_style;

	private BackgroundDescriptor _button_background;
	private BackgroundDescriptor _button_focus_background;
	private BackgroundDescriptor _header_background;
	private BackgroundDescriptor _footer_background;

	private boolean _isdesc;// 目前是否是按照降序排序
	private boolean _isAllSelect[];// 记录每一页是否全选

	private JSONArray _value;

	private JSONArray _fields;
	private JSONArray _data;

	private MAGSummaryField _sf[];
	private String _title[];
	private String _type[];
	private String _summary[];// 摘要信息一定显示
	private String _summary1[];// 可选摘要信息1
	private String _summary2[];// 可选摘要信息2
	private JSONArray _showdata[];// 当前排序根据，也会显示
	private String _id[];// 排序后根据_id获取对应数据
	private boolean _allSelect[];// 记录每条数据是否被选中

	private int _row;// 数据总量
	private String _link;
	private String _target;
	private long _expire;
	private boolean _save_history;
	private MAGInfoGrid _infogrid;

	private boolean _ispagination;// 是否分页显示
	private int _numperpage;// 每页最多显示数量
	private int _pageindex;// 每页显示起始下标

	private int _startindex;
	private int _endindex;

	private CustomSpinner _show_page;
	private String _page[];

	private int _borderw;
	// private TextField _tfshow;

	// private Bitmap _focus_bitmap;
	//private int _tx_color;
	//private int _tx_fo_color;

	private int _width;

	private int _padding_bottom;
	private int _padding_mid;
	private int _padding_top;

	private static final int PADDING = 10;

	/**
	 * @param arg0
	 */
	protected MAGInfoGridField(Context con, MAGInfoGrid infogrid) {
		super(con);
		_infogrid = infogrid;
		_borderw = 4;
		_fields = _infogrid.getFields();
		_data = _infogrid.getData();
		_row = _data.length();// 获取行数

		_pageindex = 0;
		_startindex = 0;
		_endindex = _row;

		_padding_bottom = 6;
		_padding_top = 6;
		_padding_mid = 3;

		if (_infogrid.getInitValue() != null && _infogrid.getInitValue().length() > 0) {
			try {
				_value = new JSONArray(_infogrid.getInitValue());
			} catch (final Exception e) {
				LOG.error(this, "constructor", e);
			}
		}

		_allSelect = new boolean[_row];
		for (int i = 0; i < _row; i++) {
			_allSelect[i] = false;
		}

		/*if (style().getColor() > 0) {
			_tx_color = style().getColor();
		} else {
			_tx_color = -1;
		}
		if (style().getFocusColor() > 0) {
			_tx_fo_color = style().getFocusColor();
		} else {
			_tx_fo_color = -1;
		}*/

		_numperpage = _infogrid.getNumperPage();
		if (_numperpage > 0) {
			_ispagination = true;
		} else {
			_ispagination = false;
		}

		// 每页显示数据起始结束index
		if (_ispagination) {
			_startindex = _numperpage * _pageindex;
			_endindex = _row > _numperpage * (_pageindex + 1) ? _numperpage * (_pageindex + 1) : _row;
		}

		_title_area = new MAGTitleArea(_infogrid);
		_pager_area = new TextDrawArea(con, "");

		setFields();

		_button_background = MAGStyleRepository.getButtonBackground();
		_button_focus_background = MAGStyleRepository.getFocusButtonBackground();

		_show_title = new CustomSpinner(con);

		ArrayAdapter<String> adapter_title = new ArrayAdapter<String>(con, android.R.layout.simple_spinner_item, _title);
		adapter_title.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_show_title.setAdapter(adapter_title);

		_selectbg_desc = new TextStyleDescriptor("icon=select.png");
		_unselectbg_desc = new TextStyleDescriptor("icon=unselect.png");
		_foselectbg_desc = new TextStyleDescriptor("icon=select_focus.png");
		_founselectbg_desc = new TextStyleDescriptor("icon=unselect_focus.png");

		_selectsmbg   = BitmapRepository.getBitmapByName(con, "select_small.png");
		_unselectsmbg = BitmapRepository.getBitmapByName(con, "unselect_small.png");

		_asc_button_style = MAGStyleRepository.getAscTextStyle();
		_asc_button_focus_style = MAGStyleRepository.getFocusAscTextStyle();
		_desc_button_style = MAGStyleRepository.getDescTextStyle();
		_desc_button_focus_style = MAGStyleRepository.getFocusDescTextStyle();
		_left_button_style = MAGStyleRepository.getLeftTextStyle();
		_left_button_focus_style = MAGStyleRepository.getFocusLeftTextStyle();
		_right_button_style = MAGStyleRepository.getRightTextStyle();
		_right_button_focus_style = MAGStyleRepository.getFocusRightTextStyle();

		_pager_text_style = TextStyleDescriptor.DEFAULT_TEXT_STYLE;

		if (_infogrid.getFooter() != null) {
			_pager_area.setText(_infogrid.getFooter());
			_pager_area.setStyle(_pager_text_style);
		}

		OnClickListener listener = new OnClickListener() {
			public void onClick(View field) {
				if (field == _desc) {
					_isdesc = !_isdesc;
					update(_isdesc);
				} else if (field == _select) {
					_isAllSelect[_pageindex] = !_isAllSelect[_pageindex];
					select(_pageindex);
				} else if (field == _leftpage) {
					if (_pageindex > 0) {
						_pageindex--;
						_show_page.setSelection(_pageindex);
					}
				} else if (field == _rightpage) {
					if (_pageindex < _show_page.getCount() - 1) {
						_pageindex++;
						_show_page.setSelection(_pageindex);
					}
				}
			}

		};
		
		_select = new CustomButtonField(con, "", listener);

		_desc = new CustomButtonField(con, "", listener);
		_desc.setBackground(_button_background);
		_desc.setFocusBackground(_button_focus_background);

		_show_title.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapter, View field, int position, long l) {
				if (_show_title != null) {
					if (_infogrid.isDelayedSort()) {
						updateDelay();
					} else {
						_isdesc = true;
						update(_isdesc);
					}
				}
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		});

		addView(_select);
		addView(_show_title);
		addView(_desc);
		setSummary();
		/*** 默认按照_title的第一项按降序排序 ***/
		_isdesc = true;
		upDescIcon();
		sort(_isdesc);

		if (_ispagination) {
			int num = _row / _numperpage;
			if (_row % _numperpage > 0) {
				num++;
			}
			_page = new String[num];
			_isAllSelect = new boolean[num];
			for (int i = 1; i <= num; i++) {
				_page[i - 1] = i + "";
			}
			_show_page = new CustomSpinner(con);
			ArrayAdapter<String> adapter_page = new ArrayAdapter<String>(con, android.R.layout.simple_spinner_item, _page);
			adapter_page.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			_show_page.setAdapter(adapter_page);

			_show_page.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> adapter, View field, int position, long l) {
					if (_ispagination && _show_page != null) {
						_pageindex = _show_page.getSelectedItemPosition();

						_startindex = _numperpage * _pageindex;
						_endindex = _row > _numperpage * (_pageindex + 1) ? _numperpage * (_pageindex + 1) : _row;
						setShow();
						upSelectIcon();
						// System.out.println("_pageindex:" + _pageindex);
					}
				}

				public void onNothingSelected(AdapterView<?> adapterView) {
				}
			});

			_leftpage = new CustomButtonField(con, "", listener);
			_leftpage.setBackground(_button_background);
			_leftpage.setFocusBackground(_button_focus_background);
			_leftpage.setTextStyle(_left_button_style);
			_leftpage.setFocusTextStyle(_left_button_focus_style);

			_rightpage = new CustomButtonField(con, "", listener);
			_rightpage.setBackground(_button_background);
			_rightpage.setFocusBackground(_button_focus_background);
			_rightpage.setTextStyle(_right_button_style);
			_rightpage.setFocusTextStyle(_right_button_focus_style);

		} else {
			_isAllSelect = new boolean[1];
		}

		_sf = new MAGSummaryField[_row];
		// 初始化显示按照field第一个数据降序显示
		setShow();

		int isSelectSize = _isAllSelect.length;
		for (int i = 0; i < isSelectSize; i++) {
			// 默认未全选
			_isAllSelect[i] = false;
		}
		select(_pageindex);
	}

	public void select(int page) {
		if (_isAllSelect[page]) {
			_select.setFocusTextStyle(_foselectbg_desc);
			_select.setTextStyle(_selectbg_desc);
		} else {
			_select.setFocusTextStyle(_founselectbg_desc);
			_select.setTextStyle(_unselectbg_desc);
		}

		for (int i = _startindex; i < _endindex; i++) {
			if (_sf[i] != null) {
				_sf[i].setselsct(_isAllSelect[page]);
				// _sf[i].updateUi();
			}
		}
		this.invalidate();
	}

	public void update(boolean desc) {
		upDescIcon();
		sort(desc);
		setShow();
		updateSelect();
		requestLayout();
	}

	public void updateDelay() {
		int order_cond = _show_title.getSelectedItemPosition();
		for (int i = 0; i < _row; i++) {
			_sf[i].updateData(order_cond);
		}
	}

	/**
	 * 更新每一页是否全选
	 */
	private void updateSelect() {
		int pagesize = _isAllSelect.length;
		for (int i = 0; i < pagesize; i++) {
			int startindex = _numperpage * i;
			int endindex = _row > _numperpage * (i + 1) ? _numperpage * (i + 1) : _row;

			for (int j = startindex; j < endindex; j++) {
				if (!_sf[j].isSelect()) {
					_isAllSelect[i] = false;
					break;
				}
				if (j == endindex - 1) {
					_isAllSelect[i] = true;
				}
			}
		}
		upSelectIcon();
	}

	private void upDescIcon() {
		if (_isdesc) {
			_desc.setTextStyle(_asc_button_style);
			_desc.setFocusTextStyle(_asc_button_focus_style);
		} else {
			_desc.setTextStyle(_desc_button_style);
			_desc.setFocusTextStyle(_desc_button_focus_style);
		}
	}

	private void upSelectIcon() {
		if (_isAllSelect[_pageindex]) {
			_select.setFocusTextStyle(_foselectbg_desc);
			_select.setTextStyle(_selectbg_desc);
		} else {
			_select.setFocusTextStyle(_founselectbg_desc);
			_select.setTextStyle(_unselectbg_desc);
		}
		_select.postInvalidate();
	}

	/**
	 * 当某记录选择/取消时，检测是否还设置全选按钮
	 * 
	 * @param select
	 *            当前记录的变化是否选择
	 */
	private void checkAllSelect() {
		_isAllSelect[_pageindex] = true;
		for (int i = _startindex; i < _endindex; i++) {
			if (!_sf[i]._sfisselect) {
				_isAllSelect[_pageindex] = false;
				break;
			}
		}
		upSelectIcon();
	}

	/**
	 * 每次变化后重新布局
	 */
	private void setShow() {
		for (int i = 0; i < _row; i++) {
			removeView(_sf[i]);
		}

		for (int i = 0; i < _row; i++) {
			MAGSummaryField sf = new MAGSummaryField(getContext(), i);
			_sf[i] = sf;
			_sf[i].setsummary(_summary[i], _summary1[i], _summary2[i]);
			_sf[i].setdata(_showdata[i], _show_title.getSelectedItemPosition());
			_sf[i].setid(_id[i]);
			_sf[i].setselsct(_allSelect[i]);
		}

		for (int i = _startindex; i < _endindex; i++) {
			addView(_sf[i]);
		}
		if (_ispagination) {
			removeView(_leftpage);
			removeView(_show_page);
			removeView(_rightpage);
			addView(_leftpage);
			addView(_show_page);
			addView(_rightpage);
		}
	}

	/**
	 * @return MAGStyle
	 */
	private MAGStyle style() {
		return _infogrid.getStyle();
	}

	/**
	 * 排序 _id[]也要跟着排序变化，用来标记数据
	 * 
	 * @param desc
	 *            是否升降序
	 */
	private void sort(boolean desc) {
		// String title = _title[index];
		// 重新读取源数据然后再排序，现在是否选择也要跟着同步排序
		int index = _show_title.getSelectedItemPosition();
		setSummary();
		setShowData();
		String type = _type[index].toLowerCase();
		// type:int 数字
		// 字典序 string date
		Qsort(index, _showdata, _id, _summary, _summary1, _summary2, _allSelect, 0, _row - 1, desc, type);
	}

	private void Qsort(int index, JSONArray[] data, String[] id, String[] summary, String[] summary1, String[] summary2, boolean[] select, int low, int high,
			boolean desc, String type) {
		if (low < high) {
			JSONArray pivotKey = data[low];
			String idpivotKey = id[low];
			String supivotKey = summary[low];
			String supivotKey1 = summary1[low];
			String supivotKey2 = summary2[low];
			boolean sepivotKey = select[low];
			int i = low;
			int j = high;
			try {
				while (i < j) {
					if (type.equals("int")) {
						if (desc) {
							while (i < j
									&& Double.parseDouble(UtilClass.parseNumber(data[j].getString(index))) < Double.parseDouble(UtilClass.parseNumber(pivotKey
											.getString(index)))) {
								j--;
							}
						} else {
							while (i < j
									&& Double.parseDouble(UtilClass.parseNumber(data[j].getString(index))) > Double.parseDouble(UtilClass.parseNumber(pivotKey
											.getString(index)))) {
								j--;
							}
						}
						if (i < j) {
							data[i] = data[j];
							id[i] = id[j];
							summary[i] = summary[j];
							summary1[i] = summary1[j];
							summary2[i] = summary2[j];
							select[i] = select[j];
							i++;
						}
						// Double.parseDouble Integer.parseInt
						if (desc) {
							while (i < j
									&& Double.parseDouble(UtilClass.parseNumber(data[i].getString(index))) > Double.parseDouble(UtilClass.parseNumber(pivotKey
											.getString(index)))) {
								i++;
							}
						} else {
							while (i < j
									&& Double.parseDouble(UtilClass.parseNumber(data[i].getString(index))) < Double.parseDouble(UtilClass.parseNumber(pivotKey
											.getString(index)))) {
								i++;
							}
						}
						if (i < j) {
							data[j] = data[i];
							id[j] = id[i];
							summary[j] = summary[i];
							summary1[j] = summary1[i];
							summary2[j] = summary2[i];
							select[j] = select[i];
							j--;
						}
					} else {
						if (desc) {
							// System.out.println("i=" + i + "j=" + j);
							while (i < j && data[j].getString(index).compareTo(pivotKey.getString(index)) < 0) {
								j--;
							}
						} else {
							while (i < j && data[j].getString(index).compareTo(pivotKey.getString(index)) > 0) {
								j--;
							}
						}
						if (i < j) {
							data[i] = data[j];
							id[i] = id[j];
							summary[i] = summary[j];
							summary1[i] = summary1[j];
							summary2[i] = summary2[j];
							select[i] = select[j];
							i++;
						}
						if (desc) {
							while (i < j && data[i].getString(index).compareTo(pivotKey.getString(index)) > 0) {
								i++;
							}
						} else {
							while (i < j && data[i].getString(index).compareTo(pivotKey.getString(index)) < 0) {
								i++;
							}
						}
						if (i < j) {
							data[j] = data[i];
							id[j] = id[i];
							summary[j] = summary[i];
							summary1[j] = summary1[i];
							summary2[j] = summary2[i];
							select[j] = select[i];
							j--;
						}
					}
				}
			} catch (Exception e) {
			}
			data[i] = pivotKey;
			id[i] = idpivotKey;
			summary[i] = supivotKey;
			summary1[i] = supivotKey1;
			summary2[i] = supivotKey2;
			select[i] = sepivotKey;
			Qsort(index, data, id, summary, summary1, summary2, select, low, i - 1, desc, type);
			Qsort(index, data, id, summary, summary1, summary2, select, i + 1, high, desc, type);
		}
	}

	/**
	 * 获取fields数据
	 */
	private void setFields() {
		int num = _fields.length();
		_title = new String[num];
		_type = new String[num];
		JSONObject title;
		try {
			for (int i = 0; i < num; i++) {
				title = _fields.getJSONObject(i);
				_title[i] = title.getString("_title");
				_type[i] = title.getString("_type");
			}
		} catch (JSONException e) {
			System.out.println("setfields is error!!!");
			e.printStackTrace();
		}
	}

	/**
	 * 设置summary和id，id用来在排序时记录
	 */
	private void setSummary() {
		String[] oldid = _id;
		boolean[] oldallSelect = _allSelect;

		_summary = new String[_row];
		_summary1 = new String[_row];
		_summary2 = new String[_row];
		_id = new String[_row];
		_allSelect = new boolean[_row];
		JSONObject summary;
		try {
			for (int i = 0; i < _row; i++) {
				summary = _data.getJSONObject(i);
				_summary[i] = summary.getString("_summary");

				if (summary.has("_summary1")) {
					_summary1[i] = summary.getString("_summary1");
				} else {
					_summary1[i] = "";
				}

				if (summary.has("_summary2")) {
					_summary2[i] = summary.getString("_summary2");
				} else {
					_summary2[i] = "";
				}

				_id[i] = summary.getString("_id");// _id一定要有
			}
		} catch (JSONException e) {
			System.out.println("MAGInfoGridField setsummary is error!!!");
			e.printStackTrace();
		}

		if (oldid != null && oldallSelect != null) {
			// 设置重新排序后每一项的选择情况 根据每项数据的ID查找，所以_id数据一定要唯一
			for (int i = 0; i < _row; i++) {
				for (int j = 0; j < _row; j++) {
					if (oldid[j].endsWith(_id[i])) {
						_allSelect[i] = oldallSelect[j];
						break;
					}
				}
			}
		} else {
			// 第一次初始化 oldid == null
			if (_value != null && _value.length() > 0) {
				int length = _value.length();
				for (int i = 0; i < length; i++) {
					try {
						String id = _value.getString(i);
						for (int j = 0; j < _row; j++) {
							if (id.endsWith(_id[j])) {
								_allSelect[j] = true;
								break;
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 根据ObjectChoiceField中选中的title取相应数据，_data数据顺序由title描述数据顺序决定
	 * 
	 * @param index
	 *            _data数据
	 */
	private void setShowData() {
		_showdata = new JSONArray[_row];
		JSONObject data;
		try {
			for (int i = 0; i < _row; i++) {
				data = _data.getJSONObject(i);
				_showdata[i] = data.getJSONArray("_data");
			}
		} catch (JSONException e) {
			System.out.println("setshowdata is error!!!");
			e.printStackTrace();
		}
	}

	private int getheight() {
		return (int) ((GraphicUtilityClass.getMinFontSize(getContext()) * 1.8f) + _padding_bottom + _padding_mid + _padding_top);
	}

	public void updateUi() {
		this.invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		_width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		_select.measure(_select.getMeasuredWidth(), height);
		_desc.measure(_desc.getMeasuredWidth(), height);
		_show_title.measure(_show_title.getMeasuredWidth(), height);

		for (int i = _startindex; i < _endindex; i++) {
			_sf[i].measure(_width, height - _show_title.getMeasuredHeight());
		}

		if (_ispagination) {
			_rightpage.measure(_rightpage.getMeasuredWidth(), height);
			_show_page.measure(_show_page.getMeasuredWidth(), height);
			_leftpage.measure(_leftpage.getMeasuredWidth(), height);
		}

		setMeasuredDimension(getPreferredWidth(), getPreferredHeight());
	}

	@Override
	protected void layoutChildren(int l, int t, int width, int height) {
		if (_title_area != null) {
			_title_area.layout(_width - _show_title.getMeasuredWidth() - _select.getMeasuredWidth() - _desc.getMeasuredWidth() - (_borderw << 1));
		}
		// 全选按钮
		_select.layout(_borderw, (getHeaderHeight() - _select.getMeasuredHeight()) / 2, _borderw + _select.getMeasuredWidth(), (getHeaderHeight() - _select
				.getMeasuredHeight())
				/ 2 + _select.getMeasuredHeight());
		// 排序按钮
		_desc.layout(_width - _desc.getMeasuredWidth() - _borderw, (getHeaderHeight() - _desc.getMeasuredHeight()) / 2, _width - _borderw, (getHeaderHeight() - _desc
				.getMeasuredHeight())
				/ 2 + _desc.getMeasuredHeight());
		// title
		_show_title.layout(_width - _show_title.getMeasuredWidth() - _desc.getMeasuredWidth() - 2 * _borderw, (getHeaderHeight() - _show_title.getMeasuredHeight()) / 2,
				_width - _desc.getMeasuredWidth() - 2 * _borderw, (getHeaderHeight() + _show_title.getMeasuredHeight()) / 2);

		// 具体数据
		for (int i = _startindex; i < _endindex; i++) {
			_sf[i].layout(0, getHeaderHeight() + (i - _startindex) * getheight(), _sf[i].getMeasuredWidth(), getHeaderHeight() + (i - _startindex) * getheight()
					+ _sf[i].getMeasuredHeight());
		}

		// 分页显示
		if (_ispagination) {
			_rightpage.layout(_width - _rightpage.getMeasuredWidth() - _borderw, getHeaderHeight() + (_endindex - _startindex) * getheight()
					+ (getFooterHeight() - _rightpage.getMeasuredHeight()) / 2, _width - _borderw, getHeaderHeight() + (_endindex - _startindex) * getheight()
					+ (getFooterHeight() + _rightpage.getMeasuredHeight()) / 2);
			_show_page.layout(_width - _show_page.getMeasuredWidth() - _rightpage.getMeasuredWidth() - 2 * _borderw, getHeaderHeight() + (_endindex - _startindex)
					* getheight() + (getFooterHeight() - _show_page.getMeasuredHeight()) / 2, _width - _rightpage.getMeasuredWidth() - 2 * _borderw, getHeaderHeight()
					+ (_endindex - _startindex) * getheight() + (getFooterHeight() + _show_page.getMeasuredHeight()) / 2);
			_leftpage.layout(_width - _show_page.getMeasuredWidth() - _rightpage.getMeasuredWidth() - _leftpage.getMeasuredWidth() - 3 * _borderw, getHeaderHeight()
					+ (_endindex - _startindex) * getheight() + (getFooterHeight() - _leftpage.getMeasuredHeight()) / 2, _width - _rightpage.getMeasuredWidth()
					- _leftpage.getMeasuredWidth() - 3 * _borderw, getHeaderHeight() + (_endindex - _startindex) * getheight()
					+ (getFooterHeight() + _leftpage.getMeasuredHeight()) / 2);
			_pager_area.setWidth(_width - _show_page.getMeasuredWidth() - _rightpage.getMeasuredWidth() - _leftpage.getMeasuredWidth());
		}
	}

	public int getPreferredWidth() {
		return _width;
	}

	public int getPreferredHeight() {
		int h = getHeaderHeight() + getFooterHeight();
		h += (_endindex - _startindex) * getheight();

		return h;
	}

	public void setLink() {
		int index = 0;
		View field = this.getFocusedChild();
		for (int i = 0; i < _row; i++) {
			if (field == _sf[i]) {
				index = i;
				break;
			}
		}

		String id;
		JSONObject temp;
		try {
			for (int i = 0; i < _row; i++) {
				temp = _data.getJSONObject(i);
				id = temp.getString("_id");
				if (_id[index].equals(id)) {
					_link = temp.getString("_link");
					if (temp.has("_target")) {
						_target = temp.getString("_target");
					} else {
						_target = MAGLinkableComponent.LINK_TARGET_DEFAULT;
					}
					if (temp.has("_expire")) {
						_expire = temp.getInt("_expire");
					} else {
						_expire = JSONBrowserConfig.getCacheExpire(getContext());
					}
					if (temp.has("_save")) {
						if (temp.get("_save").equals("true")) {
							_save_history = true;
						} else {
							_save_history = false;
						}
					} else {
						_save_history = true;
					}
					if (_target.equals(MAGLinkableComponent.LINK_TARGET_NEW) || _target.equals(MAGLinkableComponent.LINK_TARGET_SELF)) {
						_link = _infogrid.getMAGDocument().getAbsoluteURL(_link);
					}
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected int getHeaderHeight() {
		int header_height = 0;
		if (_title_area != null) {
			header_height = (int) _title_area.getTitleHeight();
		}
		header_height = Math.max(Math.max(_desc.getHeight(), _show_title.getHeight()), header_height) + 2 * PADDING;

		return header_height;
	}

	protected int getFooterHeight() {
		int footer_height = 0;

		if (_ispagination) {
			footer_height = (int) _pager_area.getHeight();
			footer_height = Math.max(footer_height, Math.max(_leftpage.getHeight(), Math.max(footer_height, _show_page.getHeight())) + 2 * PADDING);
		}

		return footer_height;
	}

	protected void onDraw(Canvas g) {
		if (getHeaderHeight() > 0 && _header_background != null) {
			_header_background.draw(getContext(), g, 0, 0, getMeasuredWidth(), getHeaderHeight());
		}

		if (_title_area != null) {
			_title_area.drawTitle(g, (int) _select.getMeasuredWidth() + _borderw, (int) (getHeaderHeight() - _title_area.getTitleHeight()) / 2);
		}

		if (getFooterHeight() > 0) {
			if (_footer_background != null) {
				_footer_background.draw(getContext(), g, 0, getPreferredHeight() - getFooterHeight(), getMeasuredWidth(), getFooterHeight());
			}
			_pager_area.draw(g, 0, getPreferredHeight() - getFooterHeight() + (getFooterHeight() - _pager_area.getHeight()) / 2);
		}

		super.onDraw(g);
		g.drawLine(0, getHeaderHeight(), getPreferredWidth(), getHeaderHeight(), PaintRepository.getDefaultPaint(getContext()));
		g.drawLine(0, getPreferredHeight() - getFooterHeight(), getPreferredWidth(), getPreferredHeight() - getFooterHeight(), PaintRepository.getDefaultPaint(getContext()));
	}

	/**
	 * @return 提交id
	 */
	public JSONArray getSelectId() {
		JSONArray id = new JSONArray();
		for (int i = 0; i < _row; i++) {
			if (_sf[i].isSelect()) {
				id.put(_sf[i].getid());
			}
		}
		return id;
	}

	final class MAGSummaryField extends View {
		private int _sfwidth;
		private JSONArray _sfdata;
		// private String _sfsummary;
		private Paragraph _sfpdata;
		private Paragraph _sfpsummary;
		private Paragraph _sfpsummary1;
		private Paragraph _sfpsummary2;
		private String _sfid;
		private boolean _sfisselect;
		// private Bitmap _bitmap;
		private int _index;

		private boolean _isonfocus;

		private Paint _font_large;
		private Paint _font_small;
		private Paint _font_gray_small;
		
		private int _click_x;

		private MAGSummaryField(Context con, int index) {
			super(con);
			_isonfocus = false;
			_sfisselect = false;
			_index = index;

			_font_large = PaintRepository.getFontPaint(getContext(), false, false, false, 1.0f, Color.BLACK, 255);
			_font_small = PaintRepository.getFontPaint(getContext(), false, false, false, 0.8f, Color.BLACK, 255);
			_font_gray_small = PaintRepository.getFontPaint(getContext(), false, false, false, 0.8f, Color.BLACK, 150);

			_sfpsummary  = new Paragraph(getContext(), "", 65535);
			_sfpsummary1 = new Paragraph(getContext(), "", 65535);
			_sfpsummary2 = new Paragraph(getContext(), "", 65535);
			_sfpdata     = new Paragraph(getContext(), "", 65535);

			setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Bitmap icon;
					if (_sfisselect) {
						icon = _selectsmbg;
					} else {
						icon = _unselectsmbg;
					}
					
					if(_click_x >= 0 && _click_x < icon.getWidth() + 2*_borderw) {
						selectItem();
					}else {
						showlink();
					}
				}
			});
			
			_click_x = -1;
		}

		/**
		 * @return 是否被选择，返回true后就可由getid()获得相应id
		 */
		private boolean isSelect() {
			return _sfisselect;
		}

		private String getid() {
			return _sfid;
		}

		/**
		 * 记录id用来表示是否被选择
		 * 
		 * @param id
		 */
		private void setid(String id) {
			_sfid = id;
		}

		/**
		 * 除summary外要显示的数据
		 * 
		 * @param data
		 */
		private void setdata(JSONArray data, int index) {
			_sfdata = data;
			try {
				_sfpdata.setText(data.getString(index));
			} catch (Exception e) {
			}
			_sfpdata.setPaint(_font_small);
			_sfpdata.setLineCount(1);
		}

		private void updateData(int index) {
			try {
				_sfpdata.setText(_sfdata.getString(index));
				invalidate();
			} catch (Exception e) {
			}
		}

		private void setsummary(String summary, String summary1, String summary2) {
			_sfpsummary.setText(summary);
			_sfpsummary.setPaint(_font_large);
			_sfpsummary.setLineCount(1);

			_sfpsummary1.setText(summary1);
			_sfpsummary1.setPaint(_font_gray_small);
			_sfpsummary1.setLineCount(1);

			_sfpsummary2.setText(summary2);
			_sfpsummary2.setPaint(_font_gray_small);
			_sfpsummary2.setLineCount(1);
		}

		/**
		 * 在某条数据上按点击显示_link指示界面
		 */
		private void showlink() {
			setLink();
			if (_link.length() > 0) {
				JSONBrowserLink link = new JSONBrowserLink(getContext());
				link.setURL(_link);
				link.setExpireMilliseconds(_expire);
				link.setSaveHistory(_save_history);
				MAGLinkableComponent.go2Link(_infogrid, link, _target);
				//_infogrid.showlink(_link, _target, _expire, _save_history);
			}
		}

		/**
		 * 设置全选与取消
		 */
		private void setselsct(boolean select) {
			_sfisselect = select;
			_allSelect[_index] = _sfisselect;
		}

		private void selectItem() {
			_sfisselect = !_sfisselect;
			_allSelect[_index] = _sfisselect;

			checkAllSelect();// 需要访问_allSelect来确定是否全选
			this.invalidate();
		}

		/*
		 * 空格做选择，轨迹球进入_link
		 */
		// protected boolean keyChar(char character, int status, int time) {
		// if (character == Keypad.KEY_SPACE) {
		// select();
		// return true;
		// }
		// return super.keyChar(character, status, time);
		// }
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
				_isonfocus = true;
				invalidate();
				_click_x = (int)event.getX();
				
			} else if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
				_isonfocus = false;
				invalidate();
			}
			
			return super.onTouchEvent(event);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			_sfwidth = MeasureSpec.getSize(widthMeasureSpec);
			//int height = MeasureSpec.getSize(heightMeasureSpec);

			int use_width = _sfwidth - 5 * _borderw;
			
			_sfpdata.setWidthBound(use_width - _select.getMeasuredWidth());
			_sfpsummary1.setWidthBound(use_width - _select.getMeasuredWidth());

			_sfpsummary.setWidthBound((int) Math.max(use_width - _select.getMeasuredWidth() - _sfpsummary1.getWidth(), 1));
			_sfpsummary2.setWidthBound((int) Math.max(use_width - _select.getMeasuredWidth() - _sfpdata.getWidth(), 1));

			setMeasuredDimension(_sfwidth, (int) getPreferredHeight());
		}

		public int getPreferredHeight() {
			return getheight();
		}

		public int getPreferredWidth() {
			return _sfwidth;
		}

		@Override
		protected void onDraw(Canvas graphics) {
			float height = GraphicUtilityClass.getMinFontSize(getContext());

			BackgroundDescriptor focusbg = style().getRowFocusBackground();

			BackgroundDescriptor background;
			if (_index % 2 == 0) {
				background = style().getOddBackground();
			} else {
				background = style().getEvenBackground();
			}

			if (_isonfocus) {
				if (focusbg != null) {
					focusbg.draw(getContext(), graphics, 0, 0, getPreferredWidth(), getPreferredHeight());
				}
			} else if (background != null) {
				background.draw(getContext(), graphics, 0, 0, getPreferredWidth(), getPreferredHeight());
			}

			int summary_left = 0;

			Bitmap icon;
			if (_sfisselect) {
				icon = _selectsmbg;
			} else {
				icon = _unselectsmbg;
			}
			summary_left = icon.getWidth();
			graphics.drawBitmap(icon, _borderw, _padding_top + (_sfpsummary.getHeight() - icon.getHeight()) / 2, null);

			_sfpsummary.draw(graphics, summary_left + (_borderw*2), _padding_top, Align.LEFT);
			
			_sfpsummary1.draw(graphics, getMeasuredWidth() - _sfpsummary1.getWidth() - _borderw, _padding_top
					+ (_sfpsummary.getHeight() - _sfpsummary1.getHeight()) / 2, Align.LEFT);

			_sfpdata.draw(graphics, getMeasuredWidth() - _sfpdata.getWidth() - _borderw, height + _padding_top + _padding_mid, Align.LEFT);
			_sfpsummary2.draw(graphics, icon.getWidth() + (_borderw*2), height + _padding_top + _padding_mid, Align.LEFT);
		}
	}

	protected void updateStyle() {
		_desc.setBackground(this._button_background);
		_desc.setFocusBackground(this._button_focus_background);
		upDescIcon();
		if (_leftpage != null) {
			_leftpage.setBackground(this._button_background);
			_leftpage.setFocusBackground(this._button_focus_background);
			_leftpage.setTextStyle(this._left_button_style);
			_leftpage.setFocusTextStyle(this._left_button_focus_style);
		}
		if (_rightpage != null) {
			_rightpage.setBackground(this._button_background);
			_rightpage.setFocusBackground(this._button_focus_background);
			_rightpage.setTextStyle(this._right_button_style);
			_rightpage.setFocusTextStyle(this._right_button_focus_style);
		}
		_pager_area.setStyle(_pager_text_style);
	}

	protected void setButtonBackground(BackgroundDescriptor desc) {
		_button_background = desc;
	}

	protected void setFocusButtonBackground(BackgroundDescriptor desc) {
		_button_focus_background = desc;
	}

	protected void setAscTextStyle(TextStyleDescriptor style) {
		_asc_button_style = style;
	}

	protected void setAscFocusTextStyle(TextStyleDescriptor style) {
		_asc_button_focus_style = style;
	}

	protected void setDescTextStyle(TextStyleDescriptor style) {
		_desc_button_style = style;
	}

	protected void setDescFocusTextStyle(TextStyleDescriptor style) {
		_desc_button_focus_style = style;
	}

	protected void setLeftPageTextStyle(TextStyleDescriptor style) {
		_left_button_style = style;
	}

	protected void setLeftPageFocusTextStyle(TextStyleDescriptor style) {
		_left_button_focus_style = style;
	}

	protected void setRightPageTextStyle(TextStyleDescriptor style) {
		_right_button_style = style;
	}

	protected void setRightPageFocusTextStyle(TextStyleDescriptor style) {
		_right_button_focus_style = style;
	}

	protected void setFooterBackground(BackgroundDescriptor desc) {
		_footer_background = desc;
	}

	protected void setHeaderBackground(BackgroundDescriptor desc) {
		_header_background = desc;
	}

	protected void setPagerTextStyle(TextStyleDescriptor style) {
		_pager_text_style = style;
	}

}