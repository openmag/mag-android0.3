package com.anheinno.android.libs.graphics;

import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class Paragraph {
	private float _width;
	private float _text_width;
	private int _text_spacing = 2;
	private int _line_limit = -1;
	private String _text;
	private Vector<String> _text_vector;
	private Paint _paint = null;
	private boolean _changed = false;

	public static final Align PARAGRAPH_ALIGN_DEFAULT = Align.LEFT;

	public Paragraph(Context context, String text, int width) {
		init(context, text, width, 2);
	}

	public Paragraph(Context context, String text, int width, int spacing) {
		init(context, text, width, spacing);
	}

	private void init(Context context, String text, int width, int spacing) {
		_text = text;
		_text_vector = new Vector<String>();
		_text_spacing = spacing;
		_width = width;

		_changed = true;
		
		_paint = PaintRepository.getDefaultPaint(context);
	}

	public String getText() {
		return _text;
	}

	public void setPaint(Paint paint) {
		if (_paint != paint) {
			_paint = paint;
			_changed = true;
		}
	}

	public void setText(String text) {
		if (!_text.equals(text)) {
			_changed = true;
			_text = text;
		}
	}

	public void setSpacing(int space) {
		if (space != _text_spacing) {
			_text_spacing = space;
			_changed = true;
		}
	}

	public void setLineCount(int cnt) {
		if (cnt != _line_limit) {
			_line_limit = cnt;
			_changed = true;
		}
	}

	public void setWidthBound(int width) {
		// System.out.println("paragraph width=" + width + ": " + getText());
		if (width != _width) {
			_width = width;
			_changed = true;
		}
	}

	private synchronized void arrange() {
		if (_changed == false) {
			// System.out.println("Paragraph: NO changes return");
			return;
		}

		_changed = false;
		_text_vector.removeAllElements();

		int offset = 0;
		int new_offset = 0;
		_text_width = 0;
		for (int i = 1; i <= _text.length(); i++) {
			boolean truncate = false;
			if (i < _text.length()) {
				if (_text.substring(i, i + 1).equals("\n")) {
					truncate = true;
					new_offset = i + 1;
				} else if (i + 1 < _text.length() && _text.substring(i, i + 2).equals("\r\n")) {
					truncate = true;
					new_offset = i + 2;
				} else {
					try {
						if (_paint.measureText(_text, offset, i) <= _width && _paint.measureText(_text, offset, i + 1) > _width) {
							truncate = true;
							new_offset = i;
						}
					} catch (NullPointerException npe) {
					} catch (IllegalArgumentException iae) {
					}
				}
			} else {
				truncate = true;
				new_offset = _text.length();
			}
			if (truncate) {
				if (i == offset) {
					i = offset + 1;
					new_offset = offset + 1;
				}
				String sub_text = _text.substring(offset, i);
				_text_vector.addElement(sub_text);

				if (_text_width < _paint.measureText(sub_text)) {
					_text_width = _paint.measureText(sub_text);
				}

				if (_line_limit > 0 && _text_vector.size() > _line_limit) {
					_text_vector.removeElementAt(_line_limit);
					String last_line = (String) _text_vector.elementAt(_line_limit - 1);
					while (_paint.measureText(last_line + "...") > _text_width) {
						last_line = last_line.substring(0, last_line.length() - 1);
					}
					_text_vector.setElementAt(last_line + "...", _line_limit - 1);
					break;
				}

				offset = new_offset;
				i = offset + 1;
			}
		}
	}

	public float getHeight() {
		arrange();
		return _text_vector.size() * GraphicUtilityClass.getFontHeight(_paint) + (_text_vector.size() - 1) * _text_spacing;
	}

	public float getWidth() {
		arrange();
		return _text_width;
	}

	public void draw(Canvas canvas, float left, float top, Align align) {
		arrange();
		for (int i = 0; i < _text_vector.size(); i++) {
			String substr = (String) _text_vector.elementAt(i);
			float tl = 0;
			switch (align) {
			case LEFT:
				tl = left;
				break;
			case RIGHT:
				tl = left + getWidth() - _paint.measureText(substr);
				break;
			default:
				tl = (getWidth() - _paint.measureText(substr)) / 2 + left;
			}
			
			FontMetrics metric = _paint.getFontMetrics();
			canvas.drawText(substr, tl, top + GraphicUtilityClass.getFontHeight(_paint) - metric.bottom, _paint);
			top += GraphicUtilityClass.getFontHeight(_paint) + _text_spacing;
		}
	}
}
