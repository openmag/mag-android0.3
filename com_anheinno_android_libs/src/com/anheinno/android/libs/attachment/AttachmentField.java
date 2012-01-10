/**
 * AttachmentField.java
 *
 * Copyright 2007-2011 anhe.
 */
package com.anheinno.android.libs.attachment;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import com.anheinno.android.libs.graphics.Align;
import com.anheinno.android.libs.graphics.GraphicUtilityClass;
import com.anheinno.android.libs.graphics.PaintRepository;
import com.anheinno.android.libs.graphics.Paragraph;
import com.anheinno.android.libs.graphics.ProgressBarDrawArea;
import com.anheinno.android.libs.graphics.ProgressBarDrawAreaUpdateInterface;
import com.anheinno.android.libs.ui.ProgressUIInterface;

/**
 * 2011-3-21
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 * 
 */
public class AttachmentField extends View implements Runnable, ProgressUIInterface, ProgressBarDrawAreaUpdateInterface {
	private ProgressBarDrawArea _progress_bar;
	private int _file_size;
	private float _font_height;
	private String _title;
	private String _url;
	private String _range;

	private String _dir;

	private Paragraph _ptitle;

	private boolean _isEnd;
	private boolean _isSucc;

	private int _gauge_len;;

	//private Context _context;

	public AttachmentField(Context context, String title, String url, String dir) {
		super(context);
//		setFocusable(true);
//		setClickable(true);
		//_context = context;
		_font_height = GraphicUtilityClass.getMinFontSize(getContext());
		_file_size = 0;
		_title = title;
		_url = url;
		_dir = dir;
		_isEnd = false;
		_isSucc = false;
		_range = "0%";
		_gauge_len = GraphicUtilityClass.getDisplayWidth(context) / 3;
		_progress_bar = new ProgressBarDrawArea(context, _gauge_len);
		// 初始有一个进度框
		_progress_bar.startGauge("......", 0, 1);

		_ptitle = new Paragraph(getContext(), _title, GraphicUtilityClass.getDisplayWidth(context) - _gauge_len);
	}

	/**
	 * 用于取消正在进行的下载
	 * 
	 * @return
	 */
	public String getTitle() {
		return _title;
	}

	public String getUrl() {
		return _url;
	}

	public String getDir() {
		return _dir;
	}

	public void invalidateProgressBarDrawArea() {
		this.invalidate();
	}

	public void run() {
		requestLayout();
	}

	protected void requestLayoutLater() {
		postInvalidate();// 重绘，可在子线程中更新界面
		((AttachmentDownloadService) getContext()).invokeLater(this);
	}

	public void resetGauge(String msg, int waitSeconds) {

	}

	public void resetGauge(String msg, int max, int start) {
		_file_size = max;
		_progress_bar.startGauge(" ", 0, max);
		_progress_bar.setValue(start);

		requestLayoutLater();
	}

	public void updateGauge(int val) {
		if (_file_size > 0) {
			_range = val * 100 / _file_size + "%";
			if (val == _file_size) {
				_isEnd = true;
			}
			_progress_bar.setValue(val);
			requestLayoutLater();
		}
	}

	public boolean isEnd() {
		return _isEnd;
	}

	public void setSucc(boolean succ) {
		_isSucc = succ;
	}

	public boolean isSuccess() {
		return _isSucc;
	}

	/**
	 * 没有下载完整的结束
	 */
	public void setEnd() {
		_isEnd = true;
	}

	public int getPreferredWidth() {
		int w = GraphicUtilityClass.getDisplayWidth(getContext());
		return w;
	}

	public float getPreferredHeight() {
		float h = _font_height * 2;
		return h;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (int) getPreferredHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float border = 3;

//		if (isFocused()) {
//			Paint paint = PaintRepository.DEFAULT_PAINT;
//			int savecolor = paint.getColor();
//			paint.setColor(Color.GRAY);
//			canvas.drawRect(border, (getPreferredHeight() - _font_height) / 2, GraphicUtilityClass.getDisplayWidth(getContext()) - border,
//					PaintRepository.DEFAULT_FONT_SIZE + (getPreferredHeight() - _font_height) / 2 - PaintRepository.DEFAULT_PAINT.getFontMetrics().bottom + 2 * border, paint);
//			paint.setColor(savecolor);
//		}

		_ptitle.setLineCount(1);
		_ptitle.setWidthBound((int) (getPreferredWidth() - _gauge_len - PaintRepository.getDefaultPaint(getContext()).measureText("(" + _range + ")") - 3 * border));
		_ptitle.draw(canvas, border, (getPreferredHeight() - _font_height) / 2, Align.LEFT);
		
		// title = title + "(" + _range + ")";
		canvas.drawText("(" + _range + ")" ,2 * border + _ptitle.getWidth(),1 + GraphicUtilityClass.getMinFontSize(getContext()) + (getPreferredHeight() - _font_height) / 2
				- PaintRepository.getDefaultPaint(getContext()).getFontMetrics().bottom, PaintRepository.getDefaultPaint(getContext()));

		_progress_bar
				.draw(canvas, (int) (GraphicUtilityClass.getDisplayWidth(getContext()) - _gauge_len - border), (int) ((getPreferredHeight() - _font_height) / 2));
	}

	// protected void paint(Graphics graphics) {
	// int saved_color = graphics.getColor();
	// int border = 3;
	//
	// // String title = _title;
	// // int font_length = Font.getDefault().getAdvance(title);
	// // while (font_length > getPreferredWidth() - _gauge_len -
	// // Font.getDefault().getAdvance("(" + _range + ")") - 2 * border) {
	// // title = title.substring(0, title.length() - 1);
	// // font_length = Font.getDefault().getAdvance(title);
	// // }
	//
	// _ptitle.setLineCount(1);
	// _ptitle.setWidthBound(getPreferredWidth() - _gauge_len -
	// Font.getDefault().getAdvance("(" + _range + ")") - 2 * border);
	// _ptitle.draw(graphics, border, (getPreferredHeight() - _font_height) / 2,
	// DrawStyle.LEFT);
	//
	// // title = title + "(" + _range + ")";
	// graphics.drawText("(" + _range + ")", border + _ptitle.getWidth(),
	// (getPreferredHeight() - _font_height) / 2);
	//
	// _progress_bar.draw(graphics, Display.getWidth() - _gauge_len - border,
	// (getPreferredHeight() - _font_height) / 2 + 1);
	//
	// if (isFocus()) {
	// graphics.setColor(Color.NAVY);
	// graphics.drawLine(1, getPreferredHeight() - 3, getPreferredWidth() - 2,
	// getPreferredHeight() - 3);
	// graphics.drawRect(1, 1, getPreferredWidth() - 2, getPreferredHeight() -
	// 2);
	// }
	// graphics.setColor(saved_color);
	// }
}
