package com.anheinno.android.libs.mag;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;

import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.ui.Manager;



public class MAGTieredSelectField extends Manager {
	private MAGTieredSelect _component;
	private SuboptionNode _root;
	private MAGTieredSelectField _manager;
	private boolean _updating_ui;
	
	public MAGTieredSelectField(Context context, MAGTieredSelect sel) {
		super(context);
		_manager = this;
		_component = sel;
		_root = new SuboptionNode();
		_root.setRoot(_component.title(), _component.getOptions());
		if (_component.getInitValue() != null && _component.getInitValue().length() > 0) {
			System.out.println("set init value " + _component.getInitValue());
			try {
				JSONArray value = new JSONArray(_component.getInitValue());
				_root.setSelected(value, 0);
			} catch (final Exception e) {
				LOG.error(this, "constructor", e);
			}
		}
	}
	
	private void startUpdateUI() {
		_updating_ui = true;
	}
	private void stopUpdateUI() {
		_updating_ui = false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if(_updating_ui) {
			return;
		}
		int width = MeasureSpec.getSize(widthMeasureSpec);
		//int height = MeasureSpec.getSize(heightMeasureSpec);
		SuboptionNode node = _root;
		int top = 0;
		synchronized(_manager) {
			while(!_updating_ui && node != null && node.getField() != null) {
				//System.out.println("layout " + node + " " + top);
				int view_width = width;
				if (node._title_area != null) {
					node._title_area.layout(width);
					view_width -= node._title_area.getPreferredWidth();
				}
				measureChild(node.getField(), view_width, false);
				//node.getField().measure(MeasureSpec.makeMeasureSpec(view_width, MeasureSpec.AT_MOST), heightMeasureSpec);
				//layoutChild(node.getField(), width - node._title_area.getPreferredWidth(), height);
				//setPositionChild(node.getField(), node._title_area.getPreferredWidth(), top);
				if(node._title_area != null) {
					top += (node._title_area.getTitleHeight() > node.getField().getMeasuredHeight())?node._title_area.getTitleHeight():node.getField().getMeasuredHeight();
				}else {
					top += node.getField().getMeasuredHeight();
				}
				if(node._sub_options != null && node._selected_index >= 0 && node._selected_index < node._sub_options.length) {
					node = node._sub_options[node._selected_index];
				}else {
					node = null;
				}
			}
		}
		super.setMeasuredDimension(width, top);
	}
	
	@Override
	protected void layoutChildren(int l, int t, int width, int height) {
		if(_updating_ui) {
			return;
		}
		SuboptionNode node = _root;
		int top = 0;
		synchronized(_manager) {
			while(!_updating_ui && node != null && node.getField() != null) {
				//System.out.println("layout " + node + " " + top);
				int view_width = getMeasuredWidth();
				if (node._title_area != null) {
					view_width -= node._title_area.getPreferredWidth();
				}
				setChildPosition(node.getField(), getMeasuredWidth() - node.getField().getMeasuredWidth(), top);
				//node.getField().layout(getMeasuredWidth() - node.getField().getMeasuredWidth(), top, getMeasuredWidth(), top + node.getField().getMeasuredHeight());
				//setPositionChild(node.getField(), node._title_area.getPreferredWidth(), top);
				if(node._title_area != null) {
					top += (node._title_area.getTitleHeight() > node.getField().getMeasuredHeight())?node._title_area.getTitleHeight():node.getField().getMeasuredHeight();
				}else {
					top += node.getField().getMeasuredHeight();
				}
				if(node._sub_options != null && node._selected_index >= 0 && node._selected_index < node._sub_options.length) {
					node = node._sub_options[node._selected_index];
				}else {
					node = null;
				}
			}
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		if(_updating_ui) {
			return;
		}
		synchronized(_manager) {
			_root.subpaint(canvas, 0);
		}
		super.onDraw(canvas);
	}
	
	protected void updateUi() {
		super.removeAllViews();
		//deleteAll();
		_root.initUi();
	}
	
	protected JSONArray getSelectValue() {
		JSONArray array = new JSONArray();
		_root.retrieveSelectValue(array);
		return array;
	}
	
	private static String ROOT = "_ROOT_";
	
	class SuboptionNode implements OnItemSelectedListener {
		private String _text;
		private String _value;
		private MAGTitleArea _title_area;
		//private SuboptionNode _parent;
		private SuboptionNode[] _sub_options;
		private int _selected_index;
		private CustomSpinner _choice_field;
		
		private SuboptionNode() {
			_text = null;
			_value = null;
			//_parent = null;
			_sub_options = null;
			_choice_field = null;
			_selected_index = -1;
			_title_area = null;
		}
		
		private SuboptionNode(JSONObject obj) {
			this();
			if(obj != null) {
				parseJSON(obj);
			}
		}
		
		private void initUi() {
			if(getField() != null) {
				//System.out.println(_text + "/" + _value + " initUi is called");
				_manager.addView(getField());
				if(_sub_options != null && _selected_index >= 0 && _selected_index < _sub_options.length) {
					_sub_options[_selected_index].initUi();
				}
			}
		}
		
		private void retrieveSelectValue(JSONArray array) {
			try {
				if(!_value.equals(ROOT)) {
					array.put(_value);
				}
				if(_sub_options != null && _selected_index >= 0 && _selected_index < _sub_options.length) {
					_sub_options[_selected_index].retrieveSelectValue(array);
				}
			}catch(final Exception e) {
				LOG.error(this, "retriveSelectValue", e);
			}
		}
		
		private void setSelected(JSONArray array, int offset) {
			try {
				if(offset >= 0 && offset < array.length()) {
					String val = array.getString(offset);
					for(int i = 0; i < _sub_options.length; i ++) {
						if(_sub_options[i].match(val)) {
							_selected_index = i;
							getField().setSelection(i); //.setSelectedIndex(i);
							_sub_options[i].setSelected(array, offset+1);
							break;
						}
					}
				}
			}catch(final Exception e) {
				LOG.error(this, "setSelected", e);
			}
		}
		
		private boolean match(String val) {
			if(val.equals(_value)) {
				return true;
			}else {
				return false;
			}
		}
		
		private void setRoot(String title, JSONArray options) {
			_text = ROOT;
			_value = ROOT;
			if(title != null && title.length() > 0) {
				_title_area = new MAGTitleArea(_component);
				_title_area.setText(title);
			}
			//_parent = null;
			_choice_field = null;
			_selected_index = 0;
			try {
				if(options.length() > 0) {
					_sub_options = new SuboptionNode[options.length()];
					for(int i = 0; i < options.length(); i ++) {
						_sub_options[i] = new SuboptionNode(options.getJSONObject(i));
					}
				}
			}catch(final Exception e) {
				LOG.error(this, "setRoot", e);
			}
		}
		
		private CustomSpinner getField() {
			if(_sub_options != null && _sub_options.length > 0) {
				if(_choice_field == null) {
					//_choice_field = new ObjectChoiceField("", _sub_options);
					//_choice_field.setChangeListener(this);
					
					_choice_field = new CustomSpinner(getContext());
					ArrayAdapter<SuboptionNode> adapter = new ArrayAdapter<SuboptionNode>(getContext(), android.R.layout.simple_spinner_item, _sub_options);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					_choice_field.setAdapter(adapter);
					//o.setSelection(adapter.getPosition(tn));
					_choice_field.setOnItemSelectedListener(this);
				}
			}
			return _choice_field;
		}
		
		public String toString() {
			return _text;
		}
		
		private void parseJSON(JSONObject obj) {
			//_parent = parent;
			_selected_index = 0;
			try {
				if(obj.has("_text")) {
					_text = obj.getString("_text");
				}
				if(obj.has("_value")) {
					_value = obj.getString("_value");
				}
				if(_text != null || _value != null) {
					if(_text == null) {
						_text = _value;
					}else if(_value == null) {
						_value = _text;
					}
					//System.out.println("Add option " + _text + "/" + _value);
					if(obj.has("_suboption")) {
						JSONObject subopt = obj.getJSONObject("_suboption");
						if(subopt.has("_options")) {
							JSONArray opts = subopt.getJSONArray("_options");
							if(opts != null && opts.length() > 0) {
								_sub_options = new SuboptionNode[opts.length()];
								for(int i = 0; i < opts.length(); i ++) {
									_sub_options[i] = new SuboptionNode(opts.getJSONObject(i));
								}
								if(subopt.has("_title")) {
									String title = subopt.getString("_title");
									if(title != null && title.length() > 0) {
										_title_area = new MAGTitleArea(_component);
										_title_area.setText(title);
									}
								}
							}
						}
					}
				}
			} catch(final Exception e) {
				LOG.error(this, "parseJSON", e);
			}
		}

		public void onItemSelected(AdapterView<?> parent, View field, int position, long arg3) {
			System.out.println("onItemSelected " + position + " selected current=" + _selected_index);
			//if(field == _choice_field) {
				//int i = _choice_field.getSelectedItemPosition(); //.getSelectedIndex();
				if(_selected_index != position) {
					synchronized(_manager) {
						startUpdateUI();
						if(_selected_index >= 0 && _selected_index < _sub_options.length) {
							_sub_options[_selected_index].removeField();
						}
						_selected_index = position;
						if(_selected_index >= 0 && _selected_index < _sub_options.length) {
							_sub_options[_selected_index].addField();
						}
						stopUpdateUI();
					}
				}
			//}
		}
		
		public void onNothingSelected(AdapterView<?> arg0) {

		}
		
		private void addField() {
			if(getField() != null) {
				_manager.addView(getField());
				if(_sub_options != null && _selected_index >= 0 && _selected_index < _sub_options.length) {
					_sub_options[_selected_index].addField();
				}
			}
		}
		
		private void removeField() {
			if(getField() != null) {
				if(_sub_options != null && _selected_index >= 0 && _selected_index < _sub_options.length) {
					_sub_options[_selected_index].removeField();
				}
				_manager.removeView(getField());
			}
		}
		
		private void subpaint(Canvas canvas, int offset) {
			if(getField() != null) {
				int height = getField().getMeasuredHeight();
				if(_title_area != null) {
					_title_area.drawTitle(canvas, 0, offset);
					if(height < _title_area.getTitleHeight()) {
						height = (int)_title_area.getTitleHeight();
					}
				}
				if(_sub_options != null && _selected_index >= 0 && _selected_index < _sub_options.length) {
					_sub_options[_selected_index].subpaint(canvas, offset + height);
				}
			}
		}
	}
}
