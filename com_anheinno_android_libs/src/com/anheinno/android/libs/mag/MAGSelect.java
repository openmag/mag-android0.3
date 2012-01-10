/*
 * MAGSelect.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anheinno.android.libs.R;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

/**
 * MAGSelect是让用户用一系列预设选项中选择唯一选择的输入组件。 MAGSelect的呈现形式为提示文字后跟包含所有预设选项的下拉列表
 * 或若干个单选按钮(Radio Buttons)。<br>
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGSelect extends MAGInputBase {
	private JSONArray _options;
	private String _ui;
	// private String _onchange;
	private View _view;
	private boolean _oneline;

	private static final String MAGSELECT_UI_AUTO = "__auto_";
	private static final String MAGSELECT_UI_LIST = "__list_";
	private static final String MAGSELECT_UI_RADIO = "__radio_";
	private static final String MAGSELECT_UI_DEFAULT = MAGSELECT_UI_AUTO;
	private static final int MAGSELECT_UI_OPTION_NUM = 4;

	MAGSelect() {
		super();
		_options = null;
		_ui = null;
		_oneline = false;
		// _onchange = null;
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if (!checkMandatory(o, "_options")) {
				return false;
			}

			if (o.has("_ui")) {
				_ui = o.getString("_ui");
			} else {
				_ui = MAGSELECT_UI_DEFAULT;
			}
			_options = o.getJSONArray("_options");

			return super.fromJSON(o);
		} catch (JSONException e) {
		}
		return false;
	}

	public String getAttributeValue(String fieldname) {
		return super.getAttributeValue(fieldname);
	}
	
	private boolean isRadio() {
		boolean radio = true;
		if (_ui.equals(MAGSELECT_UI_AUTO)) {
			if (_options.length() >= MAGSELECT_UI_OPTION_NUM) {
				radio = false;
			}
		} else if (_ui.equals(MAGSELECT_UI_LIST)) {
			radio = false;
		} else if (_ui.equals(MAGSELECT_UI_RADIO)) {
			radio = true;
		}
		return radio;
	}

	// 2010-5-20 增加只读控制
	public View initField(Context context) {
		MAGColorLabelManager clm = new MAGColorLabelManager(context, this);

		if (isRadio()) {
			_view = new LinearLayout(getContext());
			_oneline = false;
		} else {
			_view = new CustomSpinner(getContext());
//			_view.setBackgroundResource(R.drawable.touming);// 透明背景替换原背景

			_oneline = true;
		}

		clm.setField(_view, _oneline);
		updateField(clm);

		return clm;
	}

	public void updateField(View f) {
		Option[] options = new Option[_options.length()];
		int sel_index = -1;
		for (int i = 0; _options != null && i < _options.length(); i++) {
			try {
				String txt = _options.getJSONObject(i).getString("_text");
				String val = _options.getJSONObject(i).getString("_value");
				Option opt = new Option(txt, val);
				options[i] = opt;
				if (getInitValue().equals(val)) {
					sel_index = i;
				}
			} catch (JSONException e) {

			}
		}
		MAGColorLabelManager clm = (MAGColorLabelManager) f;
		if (isRadio()) {
			LinearLayout pane = (LinearLayout) clm.getField();
			pane.removeAllViews();
			RadioGroup group = new RadioGroup(getContext());
			for (int i = 0; i < options.length; i++) {
				// pane.addView(new RadioButton(options[i]._text, group,
				// (sel_index == i), _style_bits));
				RadioButton rb = new RadioButton(getContext());
				rb.setTextColor(Color.BLACK);
				rb.setText(options[i]._text);
				if (sel_index == i) {
					rb.setSelected(true);
				}
				group.addView(rb);
			}
			pane.addView(pane);

			if (isReadOnly()) {
				pane.setEnabled(false);
			}
		} else {
			CustomSpinner ocf = (CustomSpinner) clm.getField();
			ArrayAdapter<Option> adapter = new ArrayAdapter<Option>(getContext(), android.R.layout.simple_spinner_item, options);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			ocf.setAdapter(adapter);

			if (sel_index >= 0) {
				ocf.setSelection(sel_index);
			}
			ocf.setEnabled(!isReadOnly());
		}
	}

	protected boolean setAttribute(String name, String value) {
		if (name.equals("options")) {
			try {
				_options = new JSONArray(value);
			} catch (JSONException e) {
				return false;
			}
			return true;
		}
		if (super.setAttribute(name, value)) {
			return true;
		}
		return false;
	}

	private CustomSpinner getObjectChoiceField() {
		return (CustomSpinner) ((MAGColorLabelManager) getField()).getField();
	}

	private RadioGroup getVerticalFieldManager() {
		return (RadioGroup) ((MAGColorLabelManager) getField()).getField();
	}

	private int getSelectedIndex() {
		int sel_index = -1;
		if (!isRadio()) {
			CustomSpinner ocf = getObjectChoiceField();
			sel_index = ocf.getSelectedItemPosition();
		} else {
			RadioGroup vfm = getVerticalFieldManager();
			for (int i = 1; i < vfm.getChildCount(); i++) {
				RadioButton rbf = (RadioButton) vfm.getChildAt(i);
				if (rbf.isSelected()) {
					sel_index = i - 1;
					break;
				}
			}
		}
		return sel_index;
	}

	public String fetchValue() {
		int sel_index = getSelectedIndex();
		if (sel_index >= 0 && sel_index < _options.length()) {
			String val = "";
			try {
				val = _options.getJSONObject(sel_index).getString("_value");
			} catch (JSONException je) {
			}
			return val;
		} else {
			return null;
		}
	}

	public boolean validate() {
		return (fetchValue() != null);
	}

	static class Option {
		String _text;
		String _value;

		Option(String t, String v) {
			_text = t;
			_value = v;
		}

		public String toString() {
			return _text;
		}

		public String getValue() {
			return _value;
		}
	}
}

class CustomSpinner extends Spinner {
	private Context _context;
	private BackgroundDescriptor _background;
	private BackgroundDescriptor _fo_background;
	private boolean _isonTouch;
	private boolean _isBeingBuild;

	public CustomSpinner(Context context) {
		super(context);
		_context = context;
		_isonTouch = false;
		_isBeingBuild = true;
		_background = MAGStyleRepository.getButtonBackground();//new BackgroundDescriptor("image=bitmapborder_grey_6.png duplicate=bitmap-border border-top=19 border-left=11 border-right=11 border-bottom=19");
		_fo_background = MAGStyleRepository.getFocusButtonBackground();//new BackgroundDescriptor("image=bitmapborder_blue_6.png duplicate=bitmap-border border-top=19 border-left=11 border-right=11 border-bottom=19");
		setBackgroundResource(R.drawable.touming);
	}
	
	public boolean isNeedUpdate(){
		return !_isBeingBuild;
	}

	public void changeUpdateState(){
		_isBeingBuild = !_isBeingBuild;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			_isonTouch = true;
		} else {
			_isonTouch = false;
		}
		invalidate();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint paint = new Paint();
		if (_isonTouch) {
			_fo_background.draw(_context, canvas, 0, 0, getMeasuredWidth(), getMeasuredHeight());
		} else {
			_background.draw(_context, canvas, 0, 0, getMeasuredWidth(), getMeasuredHeight());
		}

		Bitmap bitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.arrow_short_down_m_6);
		canvas.drawBitmap(bitmap, getMeasuredWidth() - bitmap.getWidth() - 10, (getMeasuredHeight() - bitmap.getHeight()) / 2, paint);
	}
}