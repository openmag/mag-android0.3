/**
 * ImageFieldGauge.java
 *
 * Copyright 2007-2010 anhe.
 */
package com.anheinno.android.libs.mag;

import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import com.anheinno.android.libs.DownloadConsumer;
import com.anheinno.android.libs.DownloadManager;
import com.anheinno.android.libs.HTTPRequestString;
import com.anheinno.android.libs.JSONBrowserConfig;
import com.anheinno.android.libs.R;
import com.anheinno.android.libs.graphics.BitmapRepository;
import com.anheinno.android.libs.graphics.GraphicUtilityClass;
import com.anheinno.android.libs.graphics.ProgressBarDrawArea;
import com.anheinno.android.libs.graphics.ProgressBarDrawAreaUpdateInterface;
import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.ui.DelayedLayoutChangeUI;
import com.anheinno.android.libs.ui.FullScreen;
import com.anheinno.android.libs.ui.ProgressUIInterface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;


/**
 * 2010-5-18
 * 
 * 
 * @version 1.0
 * 
 */
public class MAGImageFieldGauge extends View implements ProgressUIInterface, DownloadConsumer, ProgressBarDrawAreaUpdateInterface, DelayedLayoutChangeUI {
	private MAGImage _component;
	private Bitmap _image;
	//private int _image_size;
	//private int _len;
	private boolean _isonFocus = false;
	private int _state = 0;
	
	private ProgressBarDrawArea _progress_bar;
	
	private static final int MAG_IMAGE_STATE_INIT = 0;
	private static final int MAG_IMAGE_STATE_DOWNLOADING = 1;
	private static final int MAG_IMAGE_STATE_SUCCESS = 2;
	private static final int MAG_IMAGE_STATE_FAILED = 3;

	public MAGImageFieldGauge(Context context, MAGImage img) {
		super(context);
		_component = img;
		_image = null;
		//_len = 0;
		//_image_size = 0;
		_state = MAG_IMAGE_STATE_INIT;
		
		_progress_bar = new ProgressBarDrawArea(getContext(), FullScreen.getFullScreenWidth(getContext()));
		_progress_bar.startTimer("Start loading", 2, this);
	}

	/**
	 * 注册下载任务
	 */
	public void getImage(boolean refresh) {
		if(_state != MAG_IMAGE_STATE_INIT) {
			return;
		}
		_state = MAG_IMAGE_STATE_DOWNLOADING;

		String imgserver_url = MAGDocumentConfig.getImageServerURL(getContext());
		if (imgserver_url.length() > 0) {
			HTTPRequestString req = new HTTPRequestString();
			if (!req.parse(imgserver_url)) {
				return;
			}

			req.addParam("_src", _component.src());
			if (_component.format() != null) {
				req.addParam("_format", _component.format());
			}

			int width = _component.style().getIWidth(_component.getInnerWidth());
			int height = _component.style().getIHeight(_component.getInnerHeight());

			if(width <= 0) {
				width = 200;
			}
			
			System.out.println("MAGImageFieldGauge: width=" + width + " height=" + height);
			req.addParam("_width", "" + width);
			if (height > 0) {
				req.addParam("_height", "" + height);
			}
			// do not notify image changes
			DownloadManager.getDownloadManager().registerDownloadTask(getContext(), req.getURL(true), null, refresh, JSONBrowserConfig.getCacheExpire(getContext()), false,
					this, this, null);
		}
	}

	/**
	 * 刷新图片
	 */
	public void refresh() {
		// _image = null;
		getImage(true);
	}

	/*
	 * 下载任务回调函数
	 * 
	 * @see com.anheinno.libs.DownloadConsumer#dataArrival(java.lang.String)
	 */
	public void dataArrival(JSONObject result, Object params) {
		if (result == null) {
			return;
		}
		String image = null;
		//String type = null;
		try {
			image = result.getString("image");
			//type = result.getString("type");
		} catch (final JSONException e) {
			e.printStackTrace();
		}
		// System.out.println(type);
		
		//synchronized(BitmapRepository._image_temp_buffer) {
			/**/
			
		byte[] image_data = null;
		try {
			image_data = image.getBytes("ISO-8859-1");
		}catch(final Exception e) {
			LOG.error(this, "dataArrival decode image data", e);
		}
		
		if(image_data != null) {
			//for(int i = 0; i < 20 && i < image_data.length; i ++) {
			//	System.out.print(Integer.toHexString(image_data[i] & 0xff) + ' ');
			//}
			BitmapFactory.Options opts = new BitmapFactory.Options();
	        opts.inPurgeable = false;
	        opts.inInputShareable = false;
	        opts.inTempStorage = BitmapRepository._image_temp_buffer; 
			opts.inJustDecodeBounds = false;
			opts.inDither = false;
			opts.inSampleSize = 1;
			opts.inScaled = false;
			opts.inPreferredConfig = null;
			opts.inDensity = opts.inScreenDensity = opts.inTargetDensity = GraphicUtilityClass.getScreenDensity(getContext());
			_image = BitmapFactory.decodeByteArray(image_data, 0, image_data.length, opts);
		}
		//}
		
		if(_image != null) {
		
			_state = MAG_IMAGE_STATE_SUCCESS;
	
			commitLayoutChange();
		}else {
			retrieveError("Decode failed!!!!!");
		}
	}

	public boolean retrieveError(String msg) {
		_state = MAG_IMAGE_STATE_FAILED;
		String msg_txt = msg;
		try {
			JSONObject o = new JSONObject(msg);
			if(o.has("_msg")) {
				msg_txt = o.getString("_msg");
			}
		} catch(final Exception e) {
			
		}
		resetGauge(getContext().getString(R.string.mag_image_download_failed) + ": " + msg_txt, 10);
		postInvalidate();
		return false;
	}

	public void updateGauge(int val) {
		_progress_bar.setValue(val);
		invalidateProgressBarDrawArea();
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		if(_progress_bar != null) {
			//System.out.println("progress bar width=" + width);
			_progress_bar.setWidth(width);
		}
		
		super.setMeasuredDimension(getPreferredWidth(), getPreferredHeight());

		if(_state == MAG_IMAGE_STATE_INIT) {
			getImage(false);
		}
		
	}

	private int getPreferredWidth() {
		int width = 0;
		if (_image != null) {
			width = _image.getWidth();
		}else if(_progress_bar != null) {
			int iwidth = _progress_bar.getWidth(); //_component.style().getIWidth(_component.getInnerWidth());
			if (iwidth > 0) {
				width = iwidth;
			}
		}
		return width;
	}

	private int getPreferredHeight() {
		int height = 0;
		if (_image != null) {
			height = _image.getHeight(); // * getPreferredWidth() / _image.getWidth();
		} else {
			height = _progress_bar.getHeight() * 2;
		}
		/*int iheight = _component.style().getIHeight(_component.getHeight());
		if (iheight > 0) {
			height = iheight;
		}*/
		return height;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		if(gainFocus) {
			onFocus();
		}else {
			onUnfocus();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//LOG.error(this, "onFocusChanged is called", null);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			onFocus();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			onUnfocus();
		}
		return super.onTouchEvent(event);
	}

	private void onFocus() {
		_isonFocus = true;
		postInvalidate();
	}

	private void onUnfocus() {
		_isonFocus = false;
		postInvalidate();
	}

	/*
	 * 获得焦点后显示的菜单选项
	 * 
	 * @see
	 * net.rim.device.api.ui.Field#makeContextMenu(net.rim.device.api.ui.ContextMenu
	 * )
	 */
	/*protected void makeContextMenu(ContextMenu cm) {
		cm.addItem(new MenuItem(_resources.getString(MAG_IMAGE_REFRESH), 256, 256) {
			public void run() {
				refresh();
			}
		});
	}*/

	
	private static final Paint _focus_border_paint;
	//private static final Paint _failed_paint;
	
	static {
		_focus_border_paint = new Paint();
		_focus_border_paint.setColor(Color.BLUE);
		_focus_border_paint.setStyle(Style.STROKE);
		
		//_failed_paint = new Paint();
		//_failed_paint.setColor(Color.RED);
		//_failed_paint.setStyle(Style.STROKE);
	}
	
	protected synchronized void onDraw(Canvas canvas) {		
		
		switch (_state) {
		case MAG_IMAGE_STATE_DOWNLOADING:
			if(_progress_bar != null) {
				_progress_bar.draw(canvas, 0, getMeasuredHeight() - _progress_bar.getHeight());
			}
			break;
		case MAG_IMAGE_STATE_SUCCESS:
			if(_image != null) {
				canvas.drawBitmap(_image, 0, 0, null);
			}
			//graphics.drawBitmap(0, 0, getPreferredWidth(), getPreferredHeight(), , 0, 0);
			break;
		case MAG_IMAGE_STATE_FAILED:
			if(_progress_bar != null) {
				_progress_bar.draw(canvas, 0, getMeasuredHeight() - _progress_bar.getHeight());
			}
			break;
		default:
			break;
		}

		//if (_isonFocus) {
		//	canvas.drawRect(0, 0, getPreferredWidth(), getPreferredHeight(), _focus_border_paint);
		//}
	}

	public void resetGauge(String msg, int waitSeconds) {
		_progress_bar.startTimer(msg, waitSeconds, this);
		commitLayoutChange();
	}

	public void invalidateProgressBarDrawArea() {
		if(_progress_bar != null && _progress_bar.getHeight() > 0) {
			//postInvalidate(0, getMeasuredHeight() - _progress_bar.getHeight(), getMeasuredWidth(), _progress_bar.getHeight());
			postInvalidate();
		}
	}

	public void resetGauge(String msg, int max, int start) {
		_progress_bar.startGauge(msg, 0, max);
		_progress_bar.setValue(start);
		commitLayoutChange();
	}
	
	public void commitLayoutChange() {
		FullScreen screen = FullScreen.getFullScreen(this);
		if(screen != null) {
			screen.getUiApplication().invokeLater(new layoutRunnable(this));
		}
	}
	
	static class layoutRunnable implements Runnable {
		private MAGImageFieldGauge _field;
		layoutRunnable(MAGImageFieldGauge field) {
			_field = field;
		}
		public void run() {
			_field.requestLayout();
		}
	}
}
