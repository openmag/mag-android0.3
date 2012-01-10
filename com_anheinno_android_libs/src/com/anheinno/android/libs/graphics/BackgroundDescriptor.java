package com.anheinno.android.libs.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.anheinno.android.libs.UtilClass;
import com.anheinno.android.libs.util.NameValuePair;


/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class BackgroundDescriptor {
	protected int _start_color;
	protected int _end_color;
	protected int _orient;
	protected int _corner;
	protected int _start_alpha;
	protected int _end_alpha;
	protected String _img_name;
	protected int _img_dup;
	protected int _img_valign;
	protected int _img_halign;
	
	public BackgroundDescriptor() {
		init();
	}
	
	public BackgroundDescriptor(int sc, int ec, int o, int c, int sa, int ea, String img_name, int img_dup, int img_hori, int img_vert) {
		init(sc, ec, o, c, sa, ea, img_name, img_dup, img_hori, img_vert);
	}
	
	public boolean equals(BackgroundDescriptor desc) {
		if(_start_color != desc._start_color) {
			return false;
		}
		if(_end_color != desc._end_color) {
			return false;
		}
		if(_orient != desc._orient) {
			return false;
		}
		if(_corner != desc._corner) {
			return false;
		}
		if(_start_alpha != desc._start_alpha) {
			return false;
		}
		if(_end_alpha != desc._end_alpha) {
			return false;
		}
		if(_img_name == null && desc._img_name != null) {
			return false;
		}
		if(_img_name != null && desc._img_name == null) {
			return false;
		}
		if(!_img_name.equals(desc._img_name)) {
			return false;
		}
		if(_img_dup != desc._img_dup) {
			return false;
		}
		if(_img_valign != desc._img_valign) {
			return false;
		}
		if(_img_halign != desc._img_halign) {
			return false;
		}
		return true;

	}
	
	private static BackgroundDescriptor _def_doc_title = null;
	private static BackgroundDescriptor _def_panel_title = null;
	private static BackgroundDescriptor _def_panel_bg = null;
	private static BackgroundDescriptor _def_hint_bg = null;
	
	private static final int ROYALBLUE = 0x2B60DE;
	private static final int DARKBLUE = 0x15317E;
	private static final int SKYBLUE = 0x82CAFF;
	private static final int AZURE = 0xF0FFFF;
	private static final int CORNSILK = 0xFFF8DC;
	public static BackgroundDescriptor getDefaultDocumentTitle() {
		if(_def_doc_title == null) {
			_def_doc_title = new BackgroundDescriptor(ROYALBLUE, DARKBLUE, GradientRectangle.GRADIENT_HORIZONTAL, 0, 255, 255, null, 0, 0, 0);
		}
		return _def_doc_title;
	}
	public static BackgroundDescriptor getDefaultPanelTitle() {
		if(_def_panel_title == null) {
			_def_panel_title = new BackgroundDescriptor(SKYBLUE, SKYBLUE, GradientRectangle.GRADIENT_VERTICAL, 0, 255, 0, null, 0, 0, 0);
		}
		return _def_panel_title;
	}
	/*public static BackgroundDescriptor getDefaultPanel() {
		if(_def_panel_bg == null) {
			_def_panel_bg = new BackgroundDescriptor(AZURE, AZURE, GradientRectangle.GRADIENT_HORIZONTAL, 0, 255, 255, null, 0, 0, 0);
		}
		return _def_panel_bg;
	}*/
	public static BackgroundDescriptor getDefaultHint() {
		if(_def_hint_bg == null) {
			_def_hint_bg = new BackgroundDescriptor(CORNSILK, CORNSILK, GradientRectangle.GRADIENT_HORIZONTAL, 0, 128, 128, null, 0, 0, 0);
		}
		return _def_hint_bg;
	}
	
	private void init(int sc, int ec, int o, int c, int sa, int ea, String img_name, int img_dup, int img_hori, int img_vert) {
		_start_color = sc;
		_end_color = ec;
		_orient = o;
		_corner = c;
		_start_alpha = sa;
		_end_alpha = ea;
		_img_name = img_name;
		_img_dup = img_dup;
		_img_halign = img_hori;
		_img_valign = img_vert;
	}
	
	private void init() {
		_start_color = GraphicUtilityClass.INVALID_COLOR;
		_end_color = GraphicUtilityClass.INVALID_COLOR;
		_orient = GradientRectangle.GRADIENT_HORIZONTAL;
		_corner = 0;
		_start_alpha = 255;
		_end_alpha = 255;
		_img_name = null;
		_img_dup = GradientRectangle.IMAGE_ITERATE_NONE;
		_img_halign = GradientRectangle.IMAGE_HALIGN_CENTER;
		_img_valign = GradientRectangle.IMAGE_VALIGN_MIDDLE;
	}
	
	public BackgroundDescriptor(String str) {
		init();
		parse(str);
	}
	
	public void parse(String str) {
		String[] sep = {" ", "\n", "\r"};
		String[] dat = UtilClass.strSplit(str, sep, 0);

		boolean first_color = true;
		boolean first_alpha = true;
		NameValuePair nv = null;
		for(int i = 0; i < dat.length; i ++) {
			if(nv == null) {
				nv = new NameValuePair(dat[i]);
			}else {
				nv.parse(dat[i]);
			}
			if(nv.isName("gradient-dir")) {
				if(nv.isValue("vertical")) {
					_orient = GradientRectangle.GRADIENT_VERTICAL;
				}else if(nv.isValue("horizontal")) {
					_orient = GradientRectangle.GRADIENT_HORIZONTAL;
				}
			}else if(nv.isName("image-align")) {
				if(nv.isValue("left")) {
					_img_halign = GradientRectangle.IMAGE_HALIGN_LEFT;
				}else if(nv.isValue("right")) {
					_img_halign = GradientRectangle.IMAGE_HALIGN_RIGHT;
				}else if(nv.isValue("center")) {
					_img_halign = GradientRectangle.IMAGE_HALIGN_CENTER;
				}
			}else if(nv.isName("image-valign")) {
				if(nv.isValue("top")) {
					_img_valign = GradientRectangle.IMAGE_VALIGN_TOP;
				}else if(nv.isValue("bottom")) {
					_img_valign = GradientRectangle.IMAGE_VALIGN_BOTTOM;
				}else if(nv.isValue("middle")) {
					_img_valign = GradientRectangle.IMAGE_VALIGN_MIDDLE;
				}
			}else if(nv.isName("image-adjust")) {
				if(nv.isValue("all") || nv.isValue("adjust")) {
					_img_halign = GradientRectangle.IMAGE_HALIGN_ADJUST;
					_img_valign = GradientRectangle.IMAGE_VALIGN_ADJUST;
				}else if(nv.isValue("horizontal") || nv.isValue("adjust-horizontal")) {
					_img_halign = GradientRectangle.IMAGE_HALIGN_ADJUST;
				}else if(nv.isValue("vertical") || nv.isValue("adjust-vertical")) {
					_img_valign = GradientRectangle.IMAGE_VALIGN_ADJUST;
				}
			}else if(nv.isName("duplicate")) {
				if(nv.isValue("none") || nv.isValue("no-duplicate")) {
					_img_dup = GradientRectangle.IMAGE_ITERATE_NONE;
				}else if(nv.isValue("horizontal") || nv.isValue("duplicate-horizontal")) {
					_img_dup = GradientRectangle.IMAGE_ITERATE_HONLY;
				}else if(nv.isValue("vertical") || nv.isValue("duplicate-vertical")) {
					_img_dup = GradientRectangle.IMAGE_ITERATE_VONLY;
				}else if(nv.isValue("all") || nv.isValue("duplicate")) {
					_img_dup = GradientRectangle.IMAGE_ITERATE_ALL;
				}else if(nv.isValue("bitmap-border")) {
					_img_dup = GradientRectangle.IMAGE_ITERATE_BITMAPBORDER;
				}
			}else if(nv.isName("border")) {
				setBitmapBorderTop(nv.getValueInt());
				setBitmapBorderBottom(nv.getValueInt());
				setBitmapBorderLeft(nv.getValueInt());
				setBitmapBorderRight(nv.getValueInt());
			}else if(nv.isName("border-top")) {
				setBitmapBorderTop(nv.getValueInt());
			}else if(nv.isName("border-bottom")) {
				setBitmapBorderBottom(nv.getValueInt());
			}else if(nv.isName("border-left")) {
				setBitmapBorderLeft(nv.getValueInt());
			}else if(nv.isName("border-right")) {
				setBitmapBorderRight(nv.getValueInt());
			}else if(nv.isName("image")) {
				_img_name = nv.getValueString();
			}else if(nv.isName("corner")) {
				_corner = nv.getValueInt();
				if(_corner < 0) {
					_corner = 0;
				}
			}else if(nv.isName("alpha") || nv.isName("start-alpha")) {
				_start_alpha = _end_alpha = nv.getValueInt();
			}else if(nv.isName("end-alpha")) {
				_end_alpha = nv.getValueInt();
			}else if(nv.isName("color") || nv.isName("start-color")) {
				_start_color = _end_color = GraphicUtilityClass.htmlColor(nv.getValueString());
			}else if(nv.isName("end-color")) {
				_end_color = GraphicUtilityClass.htmlColor(nv.getValueString());
			}else if(nv.isValue("vertical")) {
				_orient = GradientRectangle.GRADIENT_VERTICAL;
			}else if(nv.isValue("horizontal")){
				_orient = GradientRectangle.GRADIENT_HORIZONTAL;
			}else if(nv.isValue("left")) {
				_img_halign = GradientRectangle.IMAGE_HALIGN_LEFT;
			}else if(nv.isValue("right")) {
				_img_halign = GradientRectangle.IMAGE_HALIGN_RIGHT;
			}else if(nv.isValue("center")) {
				_img_halign = GradientRectangle.IMAGE_HALIGN_CENTER;
			}else if(nv.isValue("adjust")) {
				_img_halign = GradientRectangle.IMAGE_HALIGN_ADJUST;
				_img_valign = GradientRectangle.IMAGE_VALIGN_ADJUST;
			}else if(nv.isValue("adjust-horizontal")) {
				_img_halign = GradientRectangle.IMAGE_HALIGN_ADJUST;
			}else if(nv.isValue("adjust-vertical")) {
				_img_valign = GradientRectangle.IMAGE_VALIGN_ADJUST;
			}else if(nv.isValue("top")) {
				_img_valign = GradientRectangle.IMAGE_VALIGN_TOP;
			}else if(nv.isValue("middle")) {
				_img_valign = GradientRectangle.IMAGE_VALIGN_MIDDLE;
			}else if(nv.isValue("bottom")) {
				_img_valign = GradientRectangle.IMAGE_VALIGN_BOTTOM;
			}else if(nv.isValue("no-duplicate")) {
				_img_dup = GradientRectangle.IMAGE_ITERATE_NONE;
			}else if(nv.isValue("duplicate")) {
				_img_dup = GradientRectangle.IMAGE_ITERATE_ALL;
			}else if(nv.isValue("duplicate-horizontal")) {
				_img_dup = GradientRectangle.IMAGE_ITERATE_HONLY;
			}else if(nv.isValue("duplicate-vertical")) {
				_img_dup = GradientRectangle.IMAGE_ITERATE_VONLY;
			}else if(dat[i].toLowerCase().endsWith(".png")) {
				_img_name = nv.getValueString();
			}else if(dat[i].toLowerCase().endsWith(".jpg")) {
				_img_name = nv.getValueString();
			}else if(dat[i].toLowerCase().endsWith(".gif")) {
				_img_name = nv.getValueString();
			}else if(dat[i].toLowerCase().endsWith("px")) {
				_corner = getCorner(nv.getValueString());
			}else if(Character.isDigit(dat[i].charAt(0))) {
				if(first_alpha) {
					_start_alpha = _end_alpha = getAlpha(dat[i]);
					first_alpha = false;
				}else {
					_end_alpha = getAlpha(dat[i]);
				}
			}else {
				if(first_color) {
					try {
						int clr = GraphicUtilityClass.htmlColor(dat[i]);
						_start_color = _end_color = clr;
						first_color = false;
					}catch(final Exception e) {}
				}else {
					try {
						int clr = GraphicUtilityClass.htmlColor(dat[i]);
						_end_color = clr;
					}catch(final Exception e) {}
				}
			}
		}

		//System.out.println("BGparse: " + str + "=>" + toString());
	}
	

	private void setBitmapBorderLeft(int val) {
		_start_alpha = val;
	}
	private void setBitmapBorderRight(int val) {
		_end_alpha = val;
	}
	private void setBitmapBorderTop(int val) {
		_start_color = val;
	}
	private void setBitmapBorderBottom(int val) {
		_end_color = val;
	}
	
	private static int getAlpha(String str) {
		int alpha = Integer.parseInt(str);
		if(alpha > 255) {
			alpha = 255;
		}
		return alpha;
	}
	
	private static int getCorner(String str) {
		int cr = Integer.parseInt(str.substring(0, str.length()-2));
		if(cr < 0) {
			cr = 0;
		}
		return cr;
	}
	
	public String toString() {
		return GraphicUtilityClass.colorHTML(_start_color) + " " + GraphicUtilityClass.colorHTML(_end_color) + " " + _orient + " " + _start_alpha + " " + _end_alpha + _img_halign + _img_valign;
	}
	
	public int getMinimalWidth(Context context) {
		if(_img_name != null) {
			Bitmap bmp = BitmapRepository.getBitmapByName(context, _img_name);
			if(bmp != null) {
				return bmp.getWidth();
			}
		}
		return 0;
	}
	
	public int getMinimalHeight(Context context) {
		if(_img_name != null) {
			Bitmap bmp = BitmapRepository.getBitmapByName(context, _img_name);
			if(bmp != null) {
				return bmp.getHeight();
			}
		}
		return 0;
	}

	public void draw(Context context, Canvas g, int left, int top, int width, int height) {
		// System.out.println("generate bg: width=" + width + " height=" + height);
		if(GraphicUtilityClass.isValidColor(_start_color) && _start_color == _end_color && _start_alpha == _end_alpha && _corner == 0 && _img_name == null) {
			Paint p = new Paint();
			p.setStyle(Paint.Style.FILL);
			p.setColor(GraphicUtilityClass.makeColor(_start_alpha, _start_color));
			g.drawRect(left, top, left + width, top + height, p);
		}else {
			if(width > GraphicUtilityClass.getDisplayWidth(context)) {
				width = GraphicUtilityClass.getDisplayWidth(context);
			}
			if(height > GraphicUtilityClass.getDisplayHeight(context)) {
				height = GraphicUtilityClass.getDisplayHeight(context);
			}
			Bitmap bmp = GradientRectangleFactory.getGradientBitmap(context, width, height, this);
			if(bmp != null) {
				g.drawBitmap(bmp, left, top, null);
			}
		}
	}
}
