package com.anheinno.android.libs.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;

import com.anheinno.android.libs.UtilClass;

public class ProgressBarDrawArea implements Runnable {
	
	private Context _context;
	
	private int _width;
	private long _min;
	private long _max;
	private long _value;
	// private boolean _use_timer;
	private TextDrawArea _message;

	private Bitmap _bg_bmp;
	private Bitmap _fg_bmp;

	//private Thread _timer_thread;
	private ProgressBarDrawAreaUpdateInterface _ui;
	private long _ui_start_time;

	private static final BackgroundDescriptor DEFAULT_PROGRESS_BAR_BG;
	private static final BackgroundDescriptor DEFAULT_PROGRESS_BAR_FG;
	private static final TextStyleDescriptor DEFAULT_PROGRESS_BAR_TEXT_STYLE;
	private static final TextStyleDescriptor DEFAULT_PROGRESS_BAR_HALF_TEXT_STYLE;

	static {
		DEFAULT_PROGRESS_BAR_BG = new BackgroundDescriptor("start-color=#EEEEEE end-color=#888888");
		DEFAULT_PROGRESS_BAR_FG = new BackgroundDescriptor("start-color=#002EFF end-color=#002E88");
		DEFAULT_PROGRESS_BAR_TEXT_STYLE = new TextStyleDescriptor("font-scale=0.75 text-align=center padding=1 color=#000000 use-full-width=true");
		DEFAULT_PROGRESS_BAR_HALF_TEXT_STYLE = new TextStyleDescriptor(
				"font-scale=0.75 text-align=center padding=1 color=#FFFFFF use-full-width=true");
	}
	
	private static Handler _handler;
	static {
		_handler = new Handler();
	}

	public ProgressBarDrawArea(Context context, int w) {
		_context = context;
		
		//_width = w;
		_min = 0;
		_max = 0;
		_value = 0;
		// _use_timer = false;

		//_timer_thread = null;
		_ui = null;

		_message = new TextDrawArea(context, "Loading...");
		_message.setStyle(DEFAULT_PROGRESS_BAR_HALF_TEXT_STYLE);
		//_message.setWidth(w);
		_bg_bmp = null;
		_fg_bmp = null;
		
		setWidth(w);
	}

	public void startGauge(String msg, long min, long max) {
		synchronized(this) {
			stopTimer();
			_min = Math.min(min, max);
			_max = Math.max(min, max);
			_message.setText(msg);
			setValue(_min);
			_message.setStyle(DEFAULT_PROGRESS_BAR_TEXT_STYLE);
			_ui = null;
		}
	}

	public void startTimer(String msg, int total_second, ProgressBarDrawAreaUpdateInterface ui) {
		synchronized(this) {
			stopTimer();
			_min = System.currentTimeMillis();
			_max = System.currentTimeMillis() + total_second * 1000;
			_message.setText(msg);
			setValue(_min);
			_message.setStyle(DEFAULT_PROGRESS_BAR_TEXT_STYLE);
			_ui = ui;
			if (_ui != null) {
				_ui.invalidateProgressBarDrawArea();
			}
			/*_timer_thread = new Thread() {
				public void run() {
					// System.out.println("time thread start....");
					while (_ui != null) {
						try {
							sleep(150);
							// System.out.println("timeOut: call invalidateProgressBarDrawArea");
							if (_ui != null) {
								_ui.invalidateProgressBarDrawArea();
							}
						} catch (final InterruptedException ie) {
						} catch (final Exception e) {
							LOG.error(this, "ProgressBarDrawArea sleep", e);
						}
					}
					_timer_thread = null;
				}
			};
			_timer_thread.start();*/
			//UpdateUITimerThread.addUIClient(this);
			_ui_start_time = UtilClass.now();
			scheduleUpdate();
		}
	}

	public void stopTimer() {
		synchronized(this) {
			//UpdateUITimerThread.removeUIClient(this);
			_handler.removeCallbacks(this);
			_ui = null;
			/*if (_timer_thread != null) {
				try {
					if(_timer_thread != null) {
						_timer_thread.interrupt();
					}
					if(_timer_thread != null) {
						_timer_thread.join();
					}
				} catch (final Exception e) {
					LOG.error(this, "stopTimer", e);
				}
			}
			_timer_thread = null;*/
			_ui = null;
			_max = _min = _value = 0;
			_ui_start_time = 0;
		}
	}

	public int getHeight() {
		synchronized(this) {
			if(_min < _max) {
				return (int)_message.getHeight() + 1;
			}else {
				return 0;
			}
		}
	}
	
	public int getWidth() {
		return _width;
	}

	public void setValue(long val) {
		//synchronized(ProgressBarDrawArea.class) {
			while (val < _min) {
				val += _max - _min;
			}
			while (val > _max) {
				val -= _max - _min;
			}
			if (_value - _min < (_max - _min) / 2 && (val - _min) >= (_max - _min) / 2) {
				_message.setStyle(DEFAULT_PROGRESS_BAR_HALF_TEXT_STYLE);
			} else if ((_value - _min) > (_max - _min) / 2 && (val - _min) < (_max - _min) / 2) {
				_message.setStyle(DEFAULT_PROGRESS_BAR_TEXT_STYLE);
			}
			_value = val;
		//}
	}

	private static final Paint _drawline_paint;
	private static final Rect _src_rect;
	private static final Rect _dst_rect;
	static {
		_drawline_paint = new Paint();
		_drawline_paint.setColor(Color.BLACK);
		_drawline_paint.setStyle(Paint.Style.STROKE);
		_src_rect = new Rect();
		_dst_rect = new Rect();
	}
	
	public void draw(Canvas g, int left, int top) {
		if (_max > _min) {
			synchronized(this) {
				if (_bg_bmp == null) {
					_bg_bmp = GradientRectangleFactory.getGradientBitmap(_context, _width, getHeight(), DEFAULT_PROGRESS_BAR_BG);
				}
				if (_fg_bmp == null) {
					_fg_bmp = GradientRectangleFactory.getGradientBitmap(_context, _width, getHeight(), DEFAULT_PROGRESS_BAR_FG);
				}
				if(_bg_bmp != null) {
					g.drawBitmap(_bg_bmp, left, top, null);
				}
				
				/**
				 * 如果_time_progress_step > 0, 则使用时间来计算progress
				 */
				if (_ui != null) {
					setValue(System.currentTimeMillis());
				}
				
				int w = (int)(_max - _min);
				
				if(w > 0) {
					w = (int) ((_value - _min) * _width / w);
					if(w > 0) {
						_src_rect.set(0, 0, w, getHeight());
						_dst_rect.set(left, top, left+w, top + getHeight());
						if(_fg_bmp != null) {
							g.drawBitmap(_fg_bmp, _src_rect, _dst_rect, null);
						}
					}
				}
				
				_message.draw(g, left, top + 1);
	
				g.drawLine(left, top, left + _width, top, _drawline_paint);
			}
		}
	}
	
	public void run() {
		if (_ui != null) {
			_ui.invalidateProgressBarDrawArea();
		}
		if(_ui_start_time <= 0 || UtilClass.now() - _ui_start_time > 5*60*1000) { // exceed 5 minutes
			this.stopTimer();
		}else {
			scheduleUpdate();
		}
	}
	
	private void scheduleUpdate() {
		//System.out.println("Schedule progress bar update!!");
		_handler.postDelayed(this, 200);
	}
	
	public void setWidth(int width) {
		_width = width;
		_message.setWidth(width);
		_bg_bmp = null;
		_fg_bmp = null;
	}
}