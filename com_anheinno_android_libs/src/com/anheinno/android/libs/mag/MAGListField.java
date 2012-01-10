package com.anheinno.android.libs.mag;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.TextDrawArea;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.mag.MAGList.MAGListOrderbyField;
import com.anheinno.android.libs.ui.CustomButtonField;
import com.anheinno.android.libs.ui.Manager;

public class MAGListField extends Manager implements OnClickListener, MAGContainerLayoutInterface {

	protected MAGList _backend;

	private MAGTitleArea _title_area;
	private TextDrawArea _pager_area;

	private CustomSpinner _orderby_choice;
	private CustomButtonField _order_btn;

	private CustomButtonField _leftpage_btn;
	private CustomButtonField _rightpage_btn;
	private CustomSpinner _page_choice;

	private TextStyleDescriptor _pager_text_style;
	private TextStyleDescriptor _asc_button_style;
	private TextStyleDescriptor _asc_button_focus_style;
	private TextStyleDescriptor _desc_button_style;
	private TextStyleDescriptor _desc_button_focus_style;
	private TextStyleDescriptor _left_button_style;
	private TextStyleDescriptor _left_button_focus_style;
	private TextStyleDescriptor _right_button_style;
	private TextStyleDescriptor _right_button_focus_style;

	private TextStyleDescriptor _checked_button_style;
	private TextStyleDescriptor _checked_button_focus_style;
	private TextStyleDescriptor _unchecked_button_style;
	private TextStyleDescriptor _unchecked_button_focus_style;

	private BackgroundDescriptor _button_background;
	private BackgroundDescriptor _button_focus_background;
	private BackgroundDescriptor _header_background;
	private BackgroundDescriptor _footer_background;

	private CustomButtonField _select_all_btn;
	private CustomButtonField[] _select_btns;

	protected static final int PADDING = 3;

	protected MAGListField(Context context, MAGList backend) {
		super(context);

		_backend = backend;

		_title_area = new MAGTitleArea(backend);
		_pager_area = new TextDrawArea(context, "");

		_button_background = MAGStyleRepository.getButtonBackground();
		_button_focus_background = MAGStyleRepository.getFocusButtonBackground();

		_asc_button_style = MAGStyleRepository.getAscTextStyle();
		_asc_button_focus_style = MAGStyleRepository.getFocusAscTextStyle();
		_desc_button_style = MAGStyleRepository.getDescTextStyle();
		_desc_button_focus_style = MAGStyleRepository.getFocusDescTextStyle();
		_left_button_style = MAGStyleRepository.getLeftTextStyle();
		_left_button_focus_style = MAGStyleRepository.getFocusLeftTextStyle();
		_right_button_style = MAGStyleRepository.getRightTextStyle();
		_right_button_focus_style = MAGStyleRepository.getFocusRightTextStyle();

		_checked_button_style = MAGStyleRepository.getCheckedTextStyle();
		_checked_button_focus_style = MAGStyleRepository.getFocusCheckedTextStyle();
		_unchecked_button_style = MAGStyleRepository.getUncheckedTextStyle();
		_unchecked_button_focus_style = MAGStyleRepository.getFocusUncheckedTextStyle();

		_pager_text_style = TextStyleDescriptor.DEFAULT_TEXT_STYLE;

		_header_background = null;
		_footer_background = null;

		initFields();
	}

	private int getSelectIconSpace() {
		if (isInputMode() && _select_all_btn != null) {
			return _select_all_btn.getMeasuredWidth();
		} else {
			return 0;
		}
	}

	private int getHeaderControlsCount() {
		int controls_count = 0;
		if (isInputMode()) {
			controls_count++;
		}
		if (_backend.getOrderBy() != null) {
			controls_count += 2;
		}
		return controls_count;
	}

	private void initFields() {
		if (isInputMode()) {
			_select_all_btn = new CustomButtonField(getContext(), "", this);
			addView(_select_all_btn);
			updateSelectAllIcon();

			_select_btns = new CustomButtonField[getInputBackend().childrenNum()];
		}

		if (_backend.getOrderBy() != null) {
			_orderby_choice = new CustomSpinner(getContext());
			ArrayAdapter<MAGListOrderbyField> adapter = new ArrayAdapter<MAGListOrderbyField>(getContext(), android.R.layout.simple_spinner_item, _backend.getOrderBy());
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			_orderby_choice.setAdapter(adapter);

			addView(_orderby_choice);
			_orderby_choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> adapter, View field, int position, long l) {
					if (_orderby_choice != null) {
						MAGListOrderbyField order_cond = (MAGListOrderbyField) adapter.getItemAtPosition(position);
						if (_backend.setOrderCondition(order_cond)) {
							onOrderChanged();
						}
					}
				}

				public void onNothingSelected(AdapterView<?> adapterView) {
				}
			});

			_order_btn = new CustomButtonField(getContext(), "", this);
			addView(_order_btn);
			_order_btn.setBackground(_button_background);
			_order_btn.setFocusBackground(_button_focus_background);
			if (_backend.isDescending()) {
				_order_btn.setTextStyle(_desc_button_style);
				_order_btn.setFocusTextStyle(_desc_button_focus_style);
			} else {
				_order_btn.setTextStyle(_asc_button_style);
				_order_btn.setFocusTextStyle(_asc_button_focus_style);
			}
		} else {
			_orderby_choice = null;
			_order_btn = null;
		}

		int pages = _backend.getPageCount();
		if (pages > 1) {
			_leftpage_btn = new CustomButtonField(getContext(), "", this);
			addView(_leftpage_btn);
			_leftpage_btn.setBackground(_button_background);
			_leftpage_btn.setFocusBackground(_button_focus_background);
			_leftpage_btn.setTextStyle(_left_button_style);
			_leftpage_btn.setFocusTextStyle(_left_button_focus_style);

			Object[] page_objects = new Object[pages];
			for (int i = 0; i < pages; i++) {
				page_objects[i] = new Integer(i + 1);
			}
			_page_choice = new CustomSpinner(getContext());

			ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(getContext(), android.R.layout.simple_spinner_item, page_objects);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			_page_choice.setAdapter(adapter);

			addView(_page_choice);
			_page_choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> adapter, View field, int position, long l) {
					if (_page_choice != null) {
						int pages = ((Integer) adapter.getItemAtPosition(position)).intValue();
						if (_backend.setCurrentPage(pages - 1)) {
							onPageChanged();
						}
					}
				}

				public void onNothingSelected(AdapterView<?> adapterView) {
				}
			});

			_rightpage_btn = new CustomButtonField(getContext(), "", this);
			addView(_rightpage_btn);
			_rightpage_btn.setBackground(_button_background);
			_rightpage_btn.setFocusBackground(_button_focus_background);
			_rightpage_btn.setTextStyle(_right_button_style);
			_rightpage_btn.setTextStyle(_right_button_focus_style);
		} else {
			_leftpage_btn = null;
			_rightpage_btn = null;
			_page_choice = null;
		}

		if (_backend.getFooter() != null) {
			_pager_area.setText(_backend.getFooter());
			_pager_area.setStyle(_pager_text_style);
		}

		updateShowingChildren();
	}

	private boolean isInputMode() {
		if (_backend instanceof MAGInputList) {
			return true;
		} else {
			return false;
		}
	}

	private MAGInputList getInputBackend() {
		return (MAGInputList) _backend;
	}

	private void setChildrenPositions() {
		int width = getMeasuredWidth();
		int body_height = 0;
		int footer_height = 0;
		int child_offset = 0;
		int child_count = getChildCount();

		child_offset += getHeaderControlsCount();
		child_count -= getHeaderControlsCount();

		if (isInputMode()) {
			_select_all_btn.layout(0, (getHeaderHeight() - _select_all_btn.getMeasuredHeight()) / 2, _select_all_btn.getMeasuredWidth(),
					(getHeaderHeight() - _select_all_btn.getMeasuredHeight()) / 2 + _select_all_btn.getMeasuredHeight());
		}

		if (_backend.getOrderBy() != null) {
			_orderby_choice.layout(width - _orderby_choice.getMeasuredWidth() - _order_btn.getMeasuredWidth() - 2 * PADDING, (getHeaderHeight() - _orderby_choice
					.getMeasuredHeight()) / 2, width - _order_btn.getMeasuredWidth() - 2 * PADDING, (getHeaderHeight() - _orderby_choice.getMeasuredHeight()) / 2
					+ _orderby_choice.getMeasuredHeight());

			_order_btn.layout(width - _order_btn.getMeasuredWidth() - PADDING, (getHeaderHeight() - _order_btn.getMeasuredHeight()) / 2, width - PADDING,
					(getHeaderHeight() - _order_btn.getMeasuredHeight()) / 2 + _order_btn.getMeasuredHeight());
		}

		if (_backend.getPageCount() > 1) {
			child_count -= 3;
		}

		for (int i = _backend.getPageOffset(); i < _backend.getPageOffset() + _backend.getPageItemCount(); i++) {
			if ((isInputMode() && (i - _backend.getPageOffset()) * 2 + 1 < child_count) || (!isInputMode() && i - _backend.getPageOffset() < child_count)) {
				MAGComponentInterface comp = _backend.getChild(i);

				if (isInputMode()) {
					View view = getChildAt(child_offset + (i - _backend.getPageOffset()) * 2);
					view.layout(comp.getBorderWidthLeft(), getHeaderHeight() + body_height + comp.getOffsetTop(), comp.getBorderWidthLeft() + view.getMeasuredWidth(),
							getHeaderHeight() + body_height + comp.getOffsetTop() + view.getMeasuredHeight());
				}

				View f = comp.getField();
				if (getSelectIconSpace() > 0) {
					comp.setPaddingLeft(getSelectIconSpace());
				}
				comp.setLeft(0);
				comp.setTop(body_height + getHeaderHeight());

				f.layout(comp.getOffsetLeft(), getHeaderHeight() + body_height + comp.getOffsetTop(), comp.getOffsetLeft() + f.getMeasuredWidth(), getHeaderHeight()
						+ body_height + comp.getOffsetTop() + f.getMeasuredHeight());

				comp.setRowHeight(f.getHeight() + comp.getPaddingTop() + comp.getPaddingBottom() + comp.getBorderWidthTop() + comp.getBorderWidthBottom());

				body_height += comp.getHeight();
			} else {
				break;
			}
		}

		if (_backend.getPageCount() > 1) {
			footer_height = Math.max(_leftpage_btn.getMeasuredHeight(), Math.max(_rightpage_btn.getMeasuredHeight(), _page_choice.getMeasuredHeight())) + 2 * PADDING;

			_leftpage_btn.layout(width - _leftpage_btn.getMeasuredWidth() - _rightpage_btn.getMeasuredWidth() - _page_choice.getMeasuredWidth() - 3 * PADDING,
					getHeaderHeight() + body_height + (footer_height - _leftpage_btn.getMeasuredHeight()) / 2, width - _rightpage_btn.getMeasuredWidth()
							- _page_choice.getMeasuredWidth() - 3 * PADDING, getHeaderHeight() + body_height + (footer_height - _leftpage_btn.getMeasuredHeight()) / 2
							+ _leftpage_btn.getMeasuredHeight());
			_page_choice.layout(width - _rightpage_btn.getMeasuredWidth() - _page_choice.getMeasuredWidth() - 2 * PADDING, getHeaderHeight() + body_height
					+ (footer_height - _page_choice.getMeasuredHeight()) / 2, width - _rightpage_btn.getMeasuredWidth() - 2 * PADDING, getHeaderHeight() + body_height
					+ (footer_height - _page_choice.getMeasuredHeight()) / 2 + _page_choice.getMeasuredHeight());
			_rightpage_btn.layout(width - _rightpage_btn.getMeasuredWidth() - PADDING, getHeaderHeight() + body_height
					+ (footer_height - _rightpage_btn.getMeasuredHeight()) / 2, width - PADDING, getHeaderHeight() + body_height
					+ (footer_height - _rightpage_btn.getMeasuredHeight()) / 2 + _rightpage_btn.getMeasuredHeight());
		}
	}

	protected int getHeaderHeight() {
		int header_height = (int) _title_area.getTitleHeight();
		if (_backend.getOrderBy() != null) {
			header_height = Math.max(Math.max(_orderby_choice.getMeasuredHeight(), _order_btn.getMeasuredHeight()), header_height) + 2 * PADDING;
		}
		if (isInputMode()) {
			header_height = Math.max(header_height, _select_all_btn.getMeasuredHeight());
		}
		return header_height;
	}

	@Override
	protected void layoutChildren(int l, int t, int width, int height) {
		setChildrenPositions();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int body_height = 0;
		int order_area_width = 0;
		int pager_area_width = 0;
		int child_offset = 0;
		int child_count = getChildCount();

		child_offset += getHeaderControlsCount();
		child_count -= getHeaderControlsCount();

		if (isInputMode()) {
			_select_all_btn.measure(width, height);
		}

		if (_backend.getOrderBy() != null) {
			_orderby_choice.measure(_orderby_choice.getMeasuredWidth(), height);
			_order_btn.measure(width, height);
			order_area_width = _orderby_choice.getMeasuredWidth() + _order_btn.getMeasuredWidth() - 3 * PADDING;
		}
		_title_area.layout(width - getSelectIconSpace() - order_area_width);

		if (_backend.getPageCount() > 1) {
			child_count -= 3;
		}

		for (int i = _backend.getPageOffset(); i < _backend.getPageOffset() + _backend.getPageItemCount(); i++) {
			if ((isInputMode() && (i - _backend.getPageOffset()) * 2 + 1 < child_count) || (!isInputMode() && i - _backend.getPageOffset() < child_count)) {

				MAGComponentInterface comp = _backend.getChild(i);

				if (isInputMode()) {
					(getChildAt(child_offset + (i - _backend.getPageOffset()) * 2)).measure(width, height);
				}

				View f = comp.getField();

				if (getSelectIconSpace() > 0) {
					comp.setPaddingLeft(getSelectIconSpace());
				}
				comp.setLeft(0);
				comp.setTop(body_height + getHeaderHeight());

				f.measure(width - comp.getPaddingLeft() - comp.getPaddingRight() - comp.getBorderWidthLeft() - comp.getBorderWidthRight(), height);

				comp.setRowHeight(f.getMeasuredHeight() + comp.getPaddingTop() + comp.getPaddingBottom() + comp.getBorderWidthTop() + comp.getBorderWidthBottom());

				body_height += comp.getHeight();

			} else {
				break;
			}
		}

		if (_backend.getPageCount() > 1) {
			_leftpage_btn.measure(width, height);
			_rightpage_btn.measure(width, height);
			_page_choice.measure(_page_choice.getMeasuredWidth(), height);
			pager_area_width = _leftpage_btn.getMeasuredWidth() + _rightpage_btn.getMeasuredWidth() + _page_choice.getMeasuredWidth();
		}
		_pager_area.setWidth(pager_area_width);
		// setChildrenPositions();

		setMeasuredDimension(width, getHeaderHeight() + body_height + getFooterHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int body_height = 0;
		int footer_height = 0;
		int child_offset = 0;
		int child_count = getChildCount();

		child_offset += getHeaderControlsCount();
		child_count -= getHeaderControlsCount();

		if (_backend.getOrderBy() != null) {
			if (_header_background != null) {
				_header_background.draw(getContext(), canvas, 0, 0, getMeasuredWidth(), getHeaderHeight());
			}
		}

		_title_area.drawTitle(canvas, getSelectIconSpace(), (int) ((getHeaderHeight() - _title_area.getTitleHeight()) / 2));

		if (_backend.getPageCount() > 1) {
			child_count -= 3;
		}

		for (int i = _backend.getPageOffset(); i < _backend.getPageOffset() + _backend.getPageItemCount(); i++) {
			if ((isInputMode() && (i - _backend.getPageOffset()) * 2 + 1 < child_count) || (!isInputMode() && i - _backend.getPageOffset() < child_count)) {
				MAGComponentInterface comp = _backend.getChild(i);
				comp.drawBackground(canvas, 0, getHeaderHeight() + body_height, getMeasuredWidth(), comp.getHeight());
				body_height += comp.getHeight();
			} else {
				break;
			}
		}

		if (_backend.getPageCount() > 1 && _footer_background != null) {
			footer_height = Math.max(_leftpage_btn.getMeasuredHeight(), Math.max(_rightpage_btn.getMeasuredHeight(), _page_choice.getMeasuredHeight())) + 2 * PADDING;
			_footer_background.draw(getContext(), canvas, 0, getHeaderHeight() + body_height, getMeasuredWidth(), footer_height);
		}
		_pager_area.draw(canvas, 0, getHeaderHeight() + body_height + (getFooterHeight() - _pager_area.getHeight()) / 2);

		super.onDraw(canvas);
	}

	protected int getFooterHeight() {
		int footer_height = (int) _pager_area.getHeight();
		if (_backend.getPageCount() > 1) {
			footer_height = Math.max(footer_height, Math.max(_leftpage_btn.getMeasuredHeight(), Math.max(_rightpage_btn.getMeasuredHeight(), _page_choice
					.getMeasuredHeight()))
					+ 2 * PADDING);
		}
		return footer_height;
	}

	public void onClick(View field) {
		if (_order_btn != null && field == _order_btn) {
			if (_backend.setDescending(!_backend.isDescending())) {
				updateOrderbyIcon();
				onOrderChanged();
			}
		} else if (_leftpage_btn != null && _leftpage_btn == field) {
			int pages = _page_choice.getSelectedItemPosition();
			if (pages > 0) {
				_page_choice.setSelection(pages - 1);
			}
		} else if (_rightpage_btn != null && _rightpage_btn == field) {
			int pages = _page_choice.getSelectedItemPosition();
			if (pages < _backend.getPageCount() - 1) {
				_page_choice.setSelection(pages + 1);
			}
		} else if (isInputMode()) {
			if (field == _select_all_btn) {
				getInputBackend().selectAll();
				updateSelectAllIcon();
				for (int i = getInputBackend().getPageOffset(); i < getInputBackend().getPageOffset() + getInputBackend().getPageItemCount(); i++) {
					updateSelectIcon(i);
				}
				invalidate();
			} else {
				boolean changed = false;
				for (int i = 0; i < _select_btns.length; i++) {
					if (field == _select_btns[i]) {
						changed = true;
						getInputBackend().setSelected(i, !getInputBackend().isSelected(i));
						updateSelectIcon(i);
					}
				}
				if (changed) {
					updateSelectAllIcon();
					invalidate();
				}
			}
		}
	}

	protected void onOrderChanged() {
		int pages = 0;
		if(_page_choice != null) {
			pages = _page_choice.getSelectedItemPosition();
		}
		System.out.println("onOrderChanged " + pages);
		if (pages == 0) {
			onPageChanged();
		} else {
			_page_choice.setSelection(0);
		}
	}

	protected void onPageChanged() {
		updateShowingChildren();
		if (isInputMode()) {
			updateSelectAllIcon();
		}
	}

	private void updateShowingChildren() {
		int child_offset = 0;
		int child_count = getChildCount();

		child_offset += getHeaderControlsCount();
		child_count -= getHeaderControlsCount();

		if (_backend.getPageCount() > 1) {
			child_count -= 3;
		}

		if (child_count > 0) {
			removeViewsInLayout(child_offset, child_count);
		}

		for (int i = _backend.getPageOffset(); i < _backend.getPageOffset() + _backend.getPageItemCount(); i++) {
			MAGComponentInterface comp = _backend.getChild(i);
			if (isInputMode()) {
				if (_select_btns[i] == null) {
					_select_btns[i] = new CustomButtonField(getContext(), "", this);
				}

				addView(_select_btns[i], child_offset + (i - _backend.getPageOffset()) * 2);
				addView(comp.getField(), child_offset + (i - _backend.getPageOffset()) * 2 + 1);
				updateSelectIcon(i);
			} else {
				addView(comp.getField(), child_offset + i - _backend.getPageOffset());
			}
		}
	}

	private void updateOrderbyIcon() {
		if (!_backend.isDescending()) {
			_order_btn.setTextStyle(_asc_button_style);
			_order_btn.setFocusTextStyle(_asc_button_focus_style);
		} else {
			_order_btn.setTextStyle(_desc_button_style);
			_order_btn.setFocusTextStyle(_desc_button_focus_style);
		}
	}

	private void updateSelectAllIcon() {
		if (getInputBackend().isAllSelected()) {
			_select_all_btn.setTextStyle(_checked_button_style);
			_select_all_btn.setFocusTextStyle(_checked_button_focus_style);
		} else {
			_select_all_btn.setTextStyle(_unchecked_button_style);
			_select_all_btn.setFocusTextStyle(_unchecked_button_focus_style);
		}
	}

	private void updateSelectIcon(int index) {
		if (getInputBackend().isSelected(index)) {
			_select_btns[index].setTextStyle(_checked_button_style);
			_select_btns[index].setFocusTextStyle(_checked_button_focus_style);
		} else {
			_select_btns[index].setTextStyle(_unchecked_button_style);
			_select_btns[index].setFocusTextStyle(_unchecked_button_focus_style);
		}
	}

	protected void updateStyle() {
		_order_btn.setBackground(this._button_background);
		_order_btn.setFocusBackground(this._button_focus_background);
		updateOrderbyIcon();
		if (_leftpage_btn != null) {
			_leftpage_btn.setBackground(this._button_background);
			_leftpage_btn.setFocusBackground(this._button_focus_background);
			_leftpage_btn.setTextStyle(this._left_button_style);
			_leftpage_btn.setFocusTextStyle(this._left_button_focus_style);
		}
		if (_rightpage_btn != null) {
			_rightpage_btn.setBackground(this._button_background);
			_rightpage_btn.setFocusBackground(this._button_focus_background);
			_rightpage_btn.setTextStyle(this._right_button_style);
			_rightpage_btn.setFocusTextStyle(this._right_button_focus_style);
		}

		if (isInputMode()) {
			updateSelectAllIcon();
			for (int i = 0; _select_btns != null && i < _select_btns.length; i++) {
				if (_select_btns[i] != null) {
					updateSelectIcon(i);
				}
			}
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

	protected void setHeaderBackground(BackgroundDescriptor desc) {
		_header_background = desc;
	}

	protected void setFooterBackground(BackgroundDescriptor desc) {
		_footer_background = desc;
	}

	protected void setCheckedTextStyle(TextStyleDescriptor style) {
		_checked_button_style = style;
	}

	protected void setCheckedFocusTextStyle(TextStyleDescriptor style) {
		_checked_button_focus_style = style;
	}

	protected void setUncheckedTextStyle(TextStyleDescriptor style) {
		_unchecked_button_style = style;
	}

	protected void setUncheckedFocusTextStyle(TextStyleDescriptor style) {
		_checked_button_style = style;
	}

	protected void setPagerTextStyle(TextStyleDescriptor style) {
		_pager_text_style = style;
	}

	public void invalidateMAGComponent(MAGComponentInterface comp) {
		invalidate(comp.getLeft(), comp.getTop(), comp.getWidth(), comp.getHeight());
	}
}
