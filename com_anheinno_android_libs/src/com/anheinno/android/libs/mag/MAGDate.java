/*
 * MAGDate.java
 *
 * <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import java.util.Calendar;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
//import android.graphics.Rect;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
//import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker.OnTimeChangedListener;

import com.anheinno.android.libs.R;
import com.anheinno.android.libs.log.LOG;

/**
 * 
 * MAGDate是支持用户输入日期，时间或时间日期组合的组件。呈现形式为提示文字后跟时间日期输入区域。<br>
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGDate extends MAGInputBase {
	private String _ui_style;
	private EditText _view;

	private String _date;
	private String _time;

	private int _year = 0;
	private int _month = 0;
	private int _dayOfMonth = 0;
	private int _currentHour = 0;
	private int _currentMinute = 0;

	private static final String MAGDATE_UI_DATE = "__date_";
	private static final String MAGDATE_UI_TIME = "__time_";
	private static final String MAGDATE_UI_DATETIME = "__datetime_";
	private static final String MAGDATE_UI_DEFAULT = MAGDATE_UI_DATETIME;

	public MAGDate() {
		super();
		_ui_style = null;
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if (o.has("_ui")) {
				_ui_style = o.getString("_ui");
			} else {
				_ui_style = MAGDATE_UI_DEFAULT;
			}

			return super.fromJSON(o);
		} catch (JSONException e) {
		}
		return false;
	}

	public String getAttributeValue(String fieldname) {
		if(fieldname.equals("_ui")) {
			return _ui_style;
		}else {
			return super.getAttributeValue(fieldname);
		}
	}
	
	// 2010-5-27 增加toJSON
	public JSONObject toJSON() {
		JSONObject obj = super.toJSON();
		try {
			if (_ui_style != null && _ui_style.length() > 0) {
				obj.put("_ui", _ui_style);
			}
		} catch (final JSONException e) {
			LOG.error(this, "toJSON", e);
		}
		return obj;
	}

	// 2010-5-20 增加只读控制
	public View initField(Context context) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(getDate());
		_year = calendar.get(Calendar.YEAR);
		_month = calendar.get(Calendar.MONTH) + 1;
		_dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		_currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		_currentMinute = calendar.get(Calendar.MINUTE);

		_view = new EditText(getContext()) {
			@Override
			public boolean onTouchEvent(MotionEvent event) {
				if (!isReadOnly()) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						Dialog dialog = getDialog();
						if (dialog != null && !dialog.isShowing()) {
							dialog.show();
						}
					}
				}
				return super.onTouchEvent(event);
			}
		};
		_view.setRawInputType(InputType.TYPE_NULL);

		if (_ui_style != null && _ui_style.equals(MAGDATE_UI_DATE)) {
			_date = _year + "-" + _month + "-" + _dayOfMonth;
			_view.setText(_date);
		} else if (_ui_style != null && _ui_style.equals(MAGDATE_UI_TIME)) {
			_time = _currentHour + ":" + _currentMinute;
			_view.setText(_time);
		} else {
			_date = _year + "-" + _month + "-" + _dayOfMonth;
			_time = _currentHour + ":" + _currentMinute;
			_view.setText(_date + " " + _time);
		}

		MAGColorLabelManager clm = new MAGColorLabelManager(context, this);
		clm.setField(_view, true, true);

		return clm;
	}

	private void parseDateTime(String data_time) {
		if (data_time.contains("-")) {
			int first_ = data_time.indexOf("-");
			String md = data_time.substring(first_ + 1);
			int second_ = md.indexOf("-");

			_year = Integer.parseInt(data_time.substring(0, first_));
			_month = Integer.parseInt(md.substring(0, second_));

			if (md.contains(" ")) {
				int end = md.indexOf(" ");
				_dayOfMonth = Integer.parseInt(md.substring(second_ + 1, end));
			} else {
				_dayOfMonth = Integer.parseInt(md.substring(second_ + 1));
			}
		}

		if (data_time.contains(":")) {
			int start = data_time.indexOf(" ");
			int midd = data_time.indexOf(":");
			_currentHour = Integer.parseInt(data_time.substring(start + 1, midd));
			_currentMinute = Integer.parseInt(data_time.substring(midd + 1));
		}
	}

	private Dialog getDialog() {
		String data_time = _view.getText().toString();
		System.out.println("MAGDate data_time " + data_time);
		parseDateTime(data_time);

		if (_ui_style != null && _ui_style.equals(MAGDATE_UI_DATE)) {

			DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					_view.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
				}
			};
			return new DatePickerDialog(getContext(), onDateSetListener, _year, _month - 1, _dayOfMonth);

		} else if (_ui_style != null && _ui_style.equals(MAGDATE_UI_TIME)) {

			TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					_view.setText(hourOfDay + ":" + minute);
				}
			};
			return new TimePickerDialog(getContext(), onTimeSetListener, _currentHour, _currentMinute, true);

		} else {

			DatePicker dp = new DatePicker(getContext());
			TimePicker tp = new TimePicker(getContext());

			dp.init(_year, _month - 1, _dayOfMonth, new OnDateChangedListener() {
				public void onDateChanged(DatePicker arg0, int year, int monthOfYear, int dayOfMonth) {
					_date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
				}
			});

			tp.setIs24HourView(true);
			tp.setCurrentHour(_currentHour);
			tp.setCurrentMinute(_currentMinute);

			tp.setOnTimeChangedListener(new OnTimeChangedListener() {
				public void onTimeChanged(TimePicker arg0, int currentHour, int currentMinute) {
					_time = currentHour + ":" + currentMinute;
				}
			});

			LinearLayout ly = new LinearLayout(getContext());
			ly.setOrientation(LinearLayout.VERTICAL);
			ly.addView(dp);
			ly.addView(tp);

			Dialog dialog = new AlertDialog.Builder(getContext()).setTitle(_year + "-" + _month + "-" + _dayOfMonth + " " + _currentHour + ":" + _currentMinute)
					.setPositiveButton(getContext().getString(R.string.mag_date_set), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							_view.setText(_date + " " + _time);
						}
					}).setNegativeButton(getContext().getString(R.string.mag_date_cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
						}
					}).setView(ly).create();

			return dialog;
		}
	}

	private long getDate() {
		long date = 0;
		if (getInitValue() != null && getInitValue().length() > 0) {
			date = (long) Double.parseDouble(getInitValue());
		}
		if (0 == date) {
			Date now = new Date();
			date = now.getTime();
		}
		return date;
	}

	public void updateField(View f) {
		// getDateField().setDate(getDate());
	}

	public boolean validate() {
		return true;
	}

	/**
	 * return number of milliseconds since epoch time
	 * 
	 * @return string the integer string
	 * 
	 */
	public String fetchValue() {
		Calendar calendar = Calendar.getInstance();

		String data_time = _view.getText().toString();
		parseDateTime(data_time);

		calendar.set(_year, _month, _dayOfMonth, _currentHour, _currentMinute);

		return "" + calendar.getTimeInMillis();
	}

}
