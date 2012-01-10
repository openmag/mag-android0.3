package com.anheinno.android.libs.mag;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.anheinno.android.libs.R;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.GraphicUtilityClass;
import com.anheinno.android.libs.ui.FullScreen;
import com.anheinno.android.libs.ui.Manager;

/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 * 
 */
public class MAGColorLabelManager extends Manager {

	private MAGComponent _component;
	private View _field;
	private MAGTitleArea _title_area;
	private boolean _is_oneline;
	private boolean _elastic;

	private BackgroundDescriptor _content_bg;
	private BackgroundDescriptor _content_focus_bg;

	private int _padding_left;
	private int _padding_right;
	private int _padding_top;
	private int _padding_bottom;

	MAGColorLabelManager(Context context, MAGComponent comp) {
		super(context);

		_component = comp;
		if (_component.title() != null && _component.title().length() > 0) {
			_title_area = new MAGTitleArea(_component);
		}
		_field = null;
		_is_oneline = false;
		_content_bg = _component.style().getContentBackground();
		_content_focus_bg = _component.style().getContentFocusBackground();

		_padding_left = _component.style().getContentPaddingLeft();
		_padding_right = _component.style().getContentPaddingRight();
		_padding_top = _component.style().getContentPaddingTop();
		_padding_bottom = _component.style().getContentPaddingBottom();

	}

	public void setField(View f, boolean oneline) {
		setField(f, oneline, false);
	}

	/*
	 * this method must be called in UI Thread
	 */
	public void setField(View f, boolean oneline, boolean elastic) {
		removeAllViews();
		if (f != null) {
			_field = f;
			_is_oneline = oneline;
			_elastic = elastic;
			/*int width_params = LayoutParams.WRAP_CONTENT;
			if (_elastic) {
				width_params = LayoutParams.FILL_PARENT;
			}*/
			addView(_field);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		int width_fill_mode = MeasureSpec.AT_MOST;
		if (_elastic) {
			width_fill_mode = MeasureSpec.EXACTLY;
		}

		if (_field != null) {
			if (_title_area != null) {
				if (_is_oneline) {
					if (_component.style().get("title-width") != null) {
						_title_area.layout(width);
					} else {
						_title_area.layout(width - _padding_left - _padding_right);
					}
					if (width > _title_area.getTitleWidth() + _padding_left + _padding_right) {
						_field.measure(
								MeasureSpec.makeMeasureSpec((int) (width - _title_area.getTitleWidth() - _padding_left - _padding_right), width_fill_mode),
								MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));
					} else {
						_is_oneline = false;
					}
				}
				if (!_is_oneline) {
					_title_area.layout(width);
					_field.measure(MeasureSpec.makeMeasureSpec((int) (width - _padding_left - _padding_right), width_fill_mode), 
							MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));
				}
			} else {
				_field.measure(MeasureSpec.makeMeasureSpec((int) (width - _padding_left - _padding_right), width_fill_mode), 
							MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));
			}
		} else {
			if (_title_area != null) {
				_title_area.layout(width);
			}
		}
		this.setMeasuredDimension((int) getPreferredWidth(), (int) getPreferredHeight());
	}

	@Override
	protected void layoutChildren(int l, int t, int width, int height) {
		if (_field != null) {
			int content_left = l;
			int content_top = t;

			if (_title_area != null) {
				if (_is_oneline) {
					content_left = (int) _title_area.getTitleWidth() + _padding_left;
					content_top = _padding_top;

				} else {
					content_left = _padding_left;
					content_top = (int) _title_area.getTitleHeight() + _padding_top;
				}
			} else {
				content_left = _padding_left;
				content_top = _padding_top;
			}

			_field.layout(content_left, content_top, content_left + _field.getMeasuredWidth(), content_top + _field.getMeasuredHeight());
		}
	}

	public float getPreferredWidth() {
		// if (_width == 0) {
		if (_is_oneline) {
			float w = 0;
			/**
			 * padding依附于控件，只有控件存在时，才有padding
			 */
			if (_field != null) {
				w += _field.getMeasuredWidth() + _padding_left + _padding_right;
			}
			if (_title_area != null) {
				w += _title_area.getTitleWidth();
			}
			if (w > FullScreen.getFullScreenWidth(getContext())) {
				w = FullScreen.getFullScreenWidth(getContext());
			}
			return w;
		} else {
			float w = 0;
			if (_title_area != null) {
				w = _title_area.getTitleWidth();
			}
			int w2 = 0;
			if (_field != null) {
				w2 = _field.getMeasuredWidth() + _padding_left + _padding_right;
			}
			return Math.max(w, w2);
		}
		// } else {
		// return _width;
		// }
	}

	public View getField() {
		return _field;
	}

	public float getPreferredHeight() {
		if (_is_oneline) {
			float th = 0;
			if (_title_area != null) {
				th = _title_area.getTitleHeight();
			}
			float fh = 0;
			if (_field != null) {
				fh += _field.getMeasuredHeight() + _padding_top + _padding_bottom;
			}
			return Math.max(th, fh);
		} else {
			float h = 0;
			/**
			 * padding依附于控件，只有控件存在时，才有padding
			 */
			if (_field != null) {
				h += _field.getMeasuredHeight() + _padding_top + _padding_bottom;
			}
			if (_title_area != null) {
				h += _title_area.getTitleHeight();
			}
			return h;
		}
	}

	private static final DashPathEffect _title_effect;
	private static final DashPathEffect _content_effect;
	private static final Paint _border_paint;
	private static final Rect _border_rect;
	static {
		_title_effect = new DashPathEffect(new float[] { 5, 5 }, 0);
		_content_effect = new DashPathEffect(new float[] { 3, 3 }, 0);
		_border_paint = new Paint();
		_border_rect = new Rect();
		_border_paint.setStyle(Paint.Style.STROKE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float title_width = 0;
		float title_height = 0;
		int title_top = 0;

		if (_title_area != null) {
			if (_is_oneline) {
				int tmp_top = (int) ((getMeasuredHeight() - _title_area.getTitleHeight()) / 2);
				if (title_top < tmp_top) {
					title_top = tmp_top;
				}
			}
			_title_area.drawTitle(canvas, 0, title_top);
			title_width = _title_area.getTitleWidth();
			title_height = _title_area.getTitleHeight();
		}

		if (MAGDocumentConfig.isShowComponentBorder(getContext())) {
			if (_title_area != null) {
				_border_paint.setColor(Color.CYAN);
				_border_paint.setPathEffect(_title_effect);
				_border_rect.set(0, title_top, (int) title_width, (int) title_height + title_top);
				canvas.drawRect(_border_rect, _border_paint);
			}

			if (_field != null) {
				_border_paint.setColor(Color.MAGENTA);
				_border_paint.setPathEffect(_content_effect);
				_border_rect.set(_field.getLeft(), _field.getTop(), _field.getLeft() + _field.getMeasuredWidth(), _field.getTop() + _field.getMeasuredHeight());
				canvas.drawRect(_border_rect, _border_paint);
			}
		}

		BackgroundDescriptor bg_desc = null;

		if (_field != null && _field.isFocused()) {
			if (_content_focus_bg != null) {
				bg_desc = _content_focus_bg;
			}
		} else {
			if (_content_bg != null) {
				bg_desc = _content_bg;
			}
		}
		if (bg_desc != null) {
			_field.setBackgroundResource(R.drawable.touming);// _field.setBackgroundResource(0)
			// 会造成不断重绘

			int left, top, width, height;
			if (_is_oneline) {
				left = (int) title_width;
				top = 0;
				width = (int) (getPreferredWidth() - title_width);
				height = (int) (getPreferredHeight());
			} else {
				left = 0;
				top = (int) title_height;
				width = (int) getPreferredWidth();
				height = (int) (getPreferredHeight() - title_height);
			}
			bg_desc.draw(getContext(), canvas, left, top, width, height);
		}
		super.onDraw(canvas);
	}
}
