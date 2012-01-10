package com.anheinno.android.libs.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.Display;
import android.view.WindowManager;


/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
class GradientRectangle {
	public static final int GRADIENT_HORIZONTAL = 0;
	public static final int GRADIENT_VERTICAL   = 1;
	
	public static final int IMAGE_ITERATE_NONE = 10;
	public static final int IMAGE_ITERATE_HONLY = 11;
	public static final int IMAGE_ITERATE_VONLY = 12;
	public static final int IMAGE_ITERATE_ALL = 13;
	public static final int IMAGE_ITERATE_BITMAPBORDER = 14;

	public static final int IMAGE_HALIGN_CENTER = 20;
	public static final int IMAGE_HALIGN_LEFT = 21;
	public static final int IMAGE_HALIGN_RIGHT = 22;
	public static final int IMAGE_HALIGN_ADJUST = 23;
	
	public static final int IMAGE_VALIGN_MIDDLE = 30;
	public static final int IMAGE_VALIGN_TOP = 31;
	public static final int IMAGE_VALIGN_BOTTOM = 32;
	public static final int IMAGE_VALIGN_ADJUST = 33;
	
	private int _width;
	private int _height;
	private int _corner;
	private int _orient;
	private int[] _start_ARGB;
	private int[] _end_ARGB;
	private int[] _tmp_pixel;
	
	private int _bitmap_border_top;
	private int _bitmap_border_bottom;
	private int _bitmap_border_left;
	private int _bitmap_border_right;
	
	private boolean _transparent_background;

	private String _img_name;
	private int _img_dup;
	private int _img_halign;
	private int _img_valign;
	
	private Bitmap _bitmap;
	
	private Context _context;
	
	protected GradientRectangle(Context context, int w, int h, int c, int o, int sc, int ec, int sa, int ea, String img_name, int img_iterate, int img_hori, int img_vert) {

		_context = context;
		
		_width = w;
		_height = h;
		_corner = c;
		_orient = o;
		
		_img_dup = img_iterate;
		
		if(_img_dup == IMAGE_ITERATE_BITMAPBORDER) {
			_bitmap_border_top = sc;
			_bitmap_border_bottom = ec;
			_bitmap_border_left = sa;
			_bitmap_border_right = ea;
		}else {
			if(!GraphicUtilityClass.isValidColor(sc) || !GraphicUtilityClass.isValidColor(ec)) {
				_transparent_background = true;
				_start_ARGB = GraphicUtilityClass.fromARGB(GraphicUtilityClass.INVALID_COLOR);
				_end_ARGB = GraphicUtilityClass.fromARGB(GraphicUtilityClass.INVALID_COLOR);
			}else {
				_transparent_background = false;
				_start_ARGB = GraphicUtilityClass.fromARGB(GraphicUtilityClass.makeColor(sa, sc));
				_end_ARGB = GraphicUtilityClass.fromARGB(GraphicUtilityClass.makeColor(ea, ec));
			}
		}
		_tmp_pixel = new int[4];
		
		_img_name = img_name;
		
		_img_halign = img_hori;
		_img_valign = img_vert;
	}
	
	public static String getSpecString(int w, int h, int c, int o, int sc, int ec, int sa, int ea, String img_name, int img_iterate, int img_hori, int img_vert) {
		return getSpecString(w, h, c, o, GraphicUtilityClass.makeColor(sa, sc), GraphicUtilityClass.makeColor(ea,ec), img_name, img_iterate, img_hori, img_vert);
	}
	
	private static String getSpecString(int w, int h, int c, int o, int sc, int ec, String img_name, int img_iterate, int img_hori, int img_vert) {
		String key = "w=" + w + ":h=" + h + ":c=" + c + ":d=" + o + ":sc=" + Integer.toHexString(sc) + ":ec=" + Integer.toHexString(ec);
		if(img_name != null) {
			key += ":" + img_name + ":i=" + img_iterate + ":ha=" + img_hori + ":va=" + img_vert;
		}
		return key;
	}

	public String getSpecString() {
		int start_color = GraphicUtilityClass.INVALID_COLOR;
		int end_color = GraphicUtilityClass.INVALID_COLOR;
		if(_start_ARGB != null) {
			start_color = GraphicUtilityClass.toARGB(_start_ARGB);
		}
		if(_end_ARGB != null) {
			end_color = GraphicUtilityClass.toARGB(_end_ARGB);
		}
		return getSpecString(_width, _height, _corner, _orient, start_color, end_color, _img_name, _img_dup, _img_halign, _img_valign);
	}

	public Bitmap getBitmap() {
		if (_bitmap == null) {
			generateBitmap();
		}
		return _bitmap;
	}

	private static int[] _data = null;

	private void generateBitmap() {
		
		synchronized(GradientRectangle.class) {
		
			if(_data == null) {
				Display display = ((WindowManager) _context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
				_data = new int[display.getWidth()*display.getHeight()];
			}
			
			//System.out.println("_width=" + _width + " _height=" + _height);
			
			for(int i = 0; i < _width*_height; i ++) {
				_data[i] = 0x00000000;
			}
			
			if(_img_dup == IMAGE_ITERATE_BITMAPBORDER) {
				//for(int i = 0; i < _width*_height; i ++) {
				//	data[i] = 0x00FFFFFF;
				//}
			}else if(_transparent_background) {
				//for(int i = 0; i < data.length; i ++) {
				//	data[i] = 0x00FFFFFF;
				//}
			}else {
			//if(!_transparent_background || _img_dup != IMAGE_ITERATE_BITMAPBORDER) {
				if(GraphicUtilityClass.toARGB(_start_ARGB) != GraphicUtilityClass.toARGB(_end_ARGB) || _corner > 0) {
					if(_orient == GRADIENT_HORIZONTAL) {
						for(int r = 0; r < _height; r ++) {
							int argb = getARGBAt(r, 0);
							int start = getStartX(r);
							int end = _width - 1 - start;
							for(int c = start; c <= end; c++) {
								_data[r*_width+c] = argb;
							}
						}
					}else {
						for(int c = 0; c < _width; c ++) {
							int argb = getARGBAt(0, c);
							int start = getStartY(c);
							int end = _height - 1 - start;
							for(int r = start; r <= end; r++) {
								_data[r*_width+c] = argb;
							}
						}
					}
				}else {
					int argb = GraphicUtilityClass.toARGB(_start_ARGB);
					//System.out.println("color=" + Integer.toHexString(argb));
					for(int i = 0; i < _width*_height; i ++) {
						_data[i] = argb;
					}
				}
				
			}
	
			_bitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);
			_bitmap.setDensity(GraphicUtilityClass.getScreenDensity(_context));
			
			_bitmap.setPixels(_data, 0, _width, 0, 0, _width, _height);
			
			if(_img_name != null) {
				pasteImage();
			}
		}
	}
	
	private int getStartX(int row) {
		int c = 0;
		while(c < _corner && !inDrawingArea(row, c)) {
			c ++;
		}
		return c;
	}
	
	private int getStartY(int col) {
		int r = 0;
		while(r < _corner && !inDrawingArea(r, col)) {
			r ++;
		}
		return r;
	}
	
	private void pasteImage() {
		int left = 0;
		int top = 0;
		int l_off = 0;
		int t_off = 0;
		int w = 0;
		int h = 0;
		int wi = 1;
		int hi = 1;
		Bitmap image = null;
		
		//System.out.println("pasteImage: " + getSpecString());
		/*if(_img_dup == IMAGE_ITERATE_BITMAPBORDER) {
			image = BitmapRepository.getBitmapByName(_context, _img_name);
		}else*/
		if(_img_halign == IMAGE_HALIGN_ADJUST && _img_valign == IMAGE_VALIGN_ADJUST) {
			//System.out.println("adjust image " + _img_name + " width=" + _width + " height=" + _height);
			image = BitmapRepository.getBitmapByNameAndSize(_context, _img_name, _width, _height);
		}else if(_img_halign == IMAGE_HALIGN_ADJUST) {
			//System.out.println("adjust image " + _img_name + " width=" + _width);
			image = BitmapRepository.getBitmapByNameAndWidth(_context, _img_name, _width);
		}else if(_img_valign == IMAGE_VALIGN_ADJUST) {
			//System.out.println("adjust image " + _img_name + " height=" + _height);
			image = BitmapRepository.getBitmapByNameAndHeight(_context, _img_name, _height);
		}else {
			//System.out.println("image " + _img_name + " no adjust!");
			image = BitmapRepository.getBitmapByName(_context, _img_name);
		}
		
		if(image == null) {
			return;
		}
		
		if(_img_dup == IMAGE_ITERATE_BITMAPBORDER) {
			drawBitmapBorder(image);
			return;
		}
		
		if(_img_dup == IMAGE_ITERATE_ALL || _img_dup == IMAGE_ITERATE_HONLY) {
			wi = getTimes(_width, image.getWidth());
		}else {
			wi = 1;
		}
		if(_img_dup == IMAGE_ITERATE_ALL ||  _img_dup == IMAGE_ITERATE_VONLY) {
			hi = getTimes(_height, image.getHeight());
		}else {
			hi = 1;
		}
		
		if(image.getWidth() >= _width) {
			left = 0;
			w = _width;
			if(_img_halign == IMAGE_HALIGN_CENTER){
				l_off = (image.getWidth() - _width)/2;
			}else if(_img_halign == IMAGE_HALIGN_RIGHT) {
				l_off = image.getWidth() - _width;
			}else {
				l_off = 0;
			}
		}else {
			w = image.getWidth();
			l_off = 0;
			if(wi > 1) {
				left = 0;
			}else if(_img_halign == IMAGE_HALIGN_CENTER){
				left = (_width - image.getWidth())/2;
			}else if(_img_halign == IMAGE_HALIGN_RIGHT) {
				left = _width - image.getWidth();
			}else {
				left = 0;
			}
		}
		if(image.getHeight() >= _height) {
			top = 0;
			h = _height;
			if(_img_valign == IMAGE_VALIGN_MIDDLE){
				t_off = (image.getHeight() - _height)/2;
			}else if(_img_valign == IMAGE_VALIGN_BOTTOM) {
				t_off = image.getHeight() - _height;
			}else {
				t_off = 0;
			}
		}else {
			h = image.getHeight();
			t_off = 0;
			if(hi > 1) {
				top = 0;
			}else if(_img_valign == IMAGE_VALIGN_MIDDLE){
				top = (_height - image.getHeight())/2;
			}else if(_img_valign == IMAGE_VALIGN_BOTTOM) {
				top = _height - image.getHeight();
			}else {
				top = 0;
			}
		}

		Canvas canvas = new Canvas(_bitmap);
		
		Rect src = new Rect(l_off, t_off, l_off + w, t_off + h);
		Rect dst = new Rect();
		int y = top;
		for(int r = 0; r < hi; r++) {
			int x = left;
			for(int c = 0; c < wi; c ++) {
				//if(_transparent_background) {
				//	GraphicUtilityClass.copyPixels(_bitmap, x, y, w, h, image, l_off, t_off);
				//}else {
					dst.set(x, y, x + w, y + h);
					canvas.drawBitmap(image, src, dst, null);			
				//}
				x += w;	
			}
			y += h;
		}
	}
	
	private int getBitmapBorderLeft() {
		return _bitmap_border_left;
	}
	private int getBitmapBorderRight() {
		return _bitmap_border_right;
	}
	private int getBitmapBorderTop() {
		return _bitmap_border_top;
	}
	private int getBitmapBorderBottom() {
		return _bitmap_border_bottom;
	}
	
	private void drawBitmapBorder(Bitmap image) {
		int i_w = image.getWidth() - getBitmapBorderLeft() - getBitmapBorderRight();
		int i_h = image.getHeight() - getBitmapBorderTop() - getBitmapBorderBottom();
		
		if(i_w <= 0 || i_h <= 0) {
			return;
		}
		
		Canvas canvas = new Canvas(_bitmap);
		
		if(getBitmapBorderLeft() > 0 && getBitmapBorderTop() > 0) {
			GraphicUtilityClass.copyPixels(canvas, 0, 0, getBitmapBorderLeft(), getBitmapBorderTop(), image, 0, 0);
		}
		if(getBitmapBorderRight() > 0 && getBitmapBorderTop() > 0) {
			GraphicUtilityClass.copyPixels(canvas, _bitmap.getWidth() - getBitmapBorderRight(), 0, getBitmapBorderRight(), getBitmapBorderTop(), image, image.getWidth() - getBitmapBorderRight(), 0);
		}
		if(getBitmapBorderLeft() > 0 && getBitmapBorderBottom() > 0) {
			GraphicUtilityClass.copyPixels(canvas, 0, _bitmap.getHeight() - getBitmapBorderBottom(), getBitmapBorderLeft(), getBitmapBorderBottom(), image, 0, image.getHeight() - getBitmapBorderBottom());
		}
		if(getBitmapBorderRight() > 0 && getBitmapBorderBottom() > 0) {
			GraphicUtilityClass.copyPixels(canvas, _bitmap.getWidth() - getBitmapBorderRight(), _bitmap.getHeight() - getBitmapBorderBottom(), getBitmapBorderRight(), getBitmapBorderBottom(), image, image.getWidth() - getBitmapBorderRight(), image.getHeight() - getBitmapBorderBottom());
		}
		int d_w = i_w;
		int d_h = i_h;
		for(int g_left = getBitmapBorderLeft(); g_left < _width - getBitmapBorderRight(); g_left+= i_w) {
			d_w = i_w;
			if(d_w > _width - getBitmapBorderRight() - g_left) {
				d_w = _width - getBitmapBorderRight() - g_left;
			}
			for(int g_top = getBitmapBorderTop(); g_top < _height - getBitmapBorderBottom(); g_top += i_h) {
				d_h = i_h;
				if(d_h > _height - getBitmapBorderBottom() - g_top) {
					d_h = _height - getBitmapBorderBottom() - g_top;
				}
				//g.drawBitmap(g_left, g_top, d_w, d_h, image, getBitmapBorderLeft(), getBitmapBorderTop());
				GraphicUtilityClass.copyPixels(canvas, g_left, g_top, d_w, d_h, image, getBitmapBorderLeft(), getBitmapBorderTop());
			}
		}
		for(int g_left = getBitmapBorderLeft(); g_left < _width - getBitmapBorderRight(); g_left+= i_w) {
			d_w = i_w;
			if(d_w > _width - getBitmapBorderRight() - g_left) {
				d_w = _width - getBitmapBorderRight() - g_left;
			}
			if(getBitmapBorderTop() > 0) {
				GraphicUtilityClass.copyPixels(canvas, g_left, 0, d_w, getBitmapBorderTop(), image, getBitmapBorderLeft(), 0);
			}
			if(getBitmapBorderBottom() > 0) {
				GraphicUtilityClass.copyPixels(canvas, g_left, _height - getBitmapBorderBottom(), d_w, getBitmapBorderBottom(), image, getBitmapBorderLeft(), image.getHeight() - getBitmapBorderBottom());
			}
		}
		for(int g_top = getBitmapBorderTop(); g_top < _height - getBitmapBorderBottom(); g_top += i_h) {
			d_h = i_h;
			if(d_h > _height - getBitmapBorderBottom() - g_top) {
				d_h = _height - getBitmapBorderBottom() - g_top;
			}
			if(getBitmapBorderLeft() > 0) {
				GraphicUtilityClass.copyPixels(canvas, 0, g_top, getBitmapBorderLeft(), d_h, image, 0, getBitmapBorderTop());
			}
			if(getBitmapBorderRight() > 0) {
				GraphicUtilityClass.copyPixels(canvas, _width - getBitmapBorderRight(), g_top, getBitmapBorderRight(), d_h, image, image.getWidth() - getBitmapBorderRight(), getBitmapBorderTop());
			}
		}
	}
	
	/*private static void copyImageData(int[] bg_data, int width, int height, int left, int top, int[] img_data, int img_width, int img_height) {
		int cw = img_width;
		int ch = img_height;
		if(cw > width - left) {
			cw = width - left;
		}
		if(ch > height - top) {
			ch = height - top;
		}
		for(int r = 0; r < ch; r ++) {
			for(int c = 0; c < cw; c ++) {
				int bg_idx = (top + r)*width + (left + c);
				int img_idx = r*img_width + c;
				bg_data[bg_idx] = pixel_paste(bg_data[bg_idx], img_data[img_idx]);
			}
		}
	}
	
	private static int pixel_paste(int bg_pixel, int img_pixel) {
		int[] bg_argb = fromARGB(bg_pixel);
		int[] img_argb = fromARGB(img_pixel);
		//System.out.println("bg alpha=" + bg_argb[0] + " image alpha=" + img_argb[0]);
		bg_argb[3] = (img_argb[3]*img_argb[0] + bg_argb[3]*(255 - img_argb[0]))/255;
		bg_argb[2] = (img_argb[2]*img_argb[0] + bg_argb[2]*(255 - img_argb[0]))/255;
		bg_argb[1] = (img_argb[1]*img_argb[0] + bg_argb[1]*(255 - img_argb[0]))/255;
		bg_argb[0] = img_argb[0] + bg_argb[0];
		if(bg_argb[0] > 255) {
			bg_argb[0] = 255;
		}
		return toARGB(bg_argb);
	}*/
	
	private static int getTimes(int n1, int n2) {
		int t = n1/n2;
		if(t*n2 < n1) {
			t++;
		}
		return t;
	}
	
	/*private int getPixelAt(int r, int c) {
		if(inDrawingArea(r, c)) {
			return getARGBAt(r, c);
		}else {
			return TRANSPARENT;
		}
	}
	
	private int getColorAt(int r, int c) {
		int argb = getARGBAt(r, c);
		return argb;
	}
	
	private int getAlphaAt(int r, int c) {
		int argb = getARGBAt(r, c);
		return (argb>>24)&0xFF;
	}*/
	
	private int getARGBAt(int r, int c) {
		if(_orient == GRADIENT_HORIZONTAL) {
			return getMiddleColor(r, _height, _start_ARGB, _end_ARGB);
		} else {
			return getMiddleColor(c, _width, _start_ARGB, _end_ARGB);
		}
	}

	private int getMiddleColor(int m, int h, int[] s, int[] e) {
		for (int i = 0; i < 4; i++) {
			_tmp_pixel[i] = getMiddle(m, h, s[i], e[i]);
		}
		//System.out.println("A:" + _tmp_pixel[0] + " R:" + _tmp_pixel[1] + " G:" + _tmp_pixel[2] + " B:" + _tmp_pixel[3]);
		return GraphicUtilityClass.toARGB(_tmp_pixel);
	}

	private int getMiddle(int m, int h, int s, int e) {
		return s + (e - s) * m / h;
	}

	private boolean inDrawingArea(int r, int c) {
		if (_corner > 0) {
			if (r < _corner) {
				if (c < _corner) {
					return inCircle(r, c, _corner, _corner, _corner);
				} else if (c > _width - _corner) {
					return inCircle(r, c, _corner, _width - _corner, _corner);
				} else {
					return true;
				}
			} else if (r > _height - _corner) {
				if (c < _corner) {
					return inCircle(r, c, _height - _corner, _corner, _corner);
				} else if (c > _width - _corner) {
					return inCircle(r, c, _height - _corner, _width - _corner, _corner);
				} else {
					return true;
				}
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	private static boolean inCircle(int x, int y, int c_x, int c_y, int r) {
		if ((x - c_x) * (x - c_x) + (y - c_y) * (y - c_y) < r * r) {
			return true;
		} else {
			return false;
		}
	}
}
