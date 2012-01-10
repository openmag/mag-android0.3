package com.anheinno.android.libs.graphics;

import android.content.Context;
import android.graphics.Bitmap;

import com.anheinno.android.libs.util.LUFLimitedHashtable;


/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
class GradientRectangleFactory {
	private static final int CACHE_SIZE = 32;

	private static LUFLimitedHashtable<String, GradientRectangle> _warehouse_table;
	static {
		_warehouse_table = new LUFLimitedHashtable<String, GradientRectangle>(CACHE_SIZE, "GradientRectangleFactory");
	}
	private static int _hit_rate = 0;
	private static int _miss_rate = 0;
	
	private static GradientRectangle getGradientRectangle(Context context, int w, int h, BackgroundDescriptor desc) {
		return getGradientRectangle(context, w, h, desc._corner, desc._orient, desc._start_color, desc._end_color, desc._start_alpha, desc._end_alpha, desc._img_name, desc._img_dup, desc._img_halign, desc._img_valign);
	}
	
	/*private static GradientRectangle getGradientRectangle(int w, int h, int c, int o, int sc, int ec, int sa, int ea) {
		return getGradientRectangle(w, h, c, o, sc, ec, sa, ea, null, 0, 0, 0);
	}*/
	
	private static GradientRectangle getGradientRectangle(Context context, int w, int h, int c, int o, int sc, int ec, int sa, int ea, String imgname, int imgdup, int imghori, int imgvert) {
		String spec = GradientRectangle.getSpecString(w, h, c, o, sc, ec, sa, ea, imgname, imgdup, imghori, imgvert);
		if(_warehouse_table.containsKey(spec)) {
			_hit_rate++;
			return _warehouse_table.get(spec);
		}
		_miss_rate++;
		//System.out.println("miss: " + _miss_rate + " hit: " + _hit_rate);
		GradientRectangle rect = new GradientRectangle(context, w, h, c, o, sc, ec, sa, ea, imgname, imgdup, imghori, imgvert);
		saveGradientRectangle(rect);
		return rect;
	}
	
	private static void saveGradientRectangle(GradientRectangle rect) {
		synchronized(_warehouse_table) {
			// A FIFO warehouse
			String spec = null;
			/*while(_warehouse.size() >= CACHE_SIZE) {
				spec = (String)_warehouse.elementAt(0);
				_warehouse.removeElementAt(0);
				_warehouse_table.remove(spec);
			}*/
			spec = rect.getSpecString();
			//_warehouse.addElement(spec);
			_warehouse_table.put(spec, rect);
		}
	}
	
	protected static Bitmap getGradientBitmap(Context context, int w, int h, BackgroundDescriptor desc) {
		if(w > 0 && h > 0) {
			return getGradientRectangle(context, w, h, desc).getBitmap();
		}else {
			return null;
		}
	}
	/*public static Bitmap getGradientBitmap(int w, int h, int c, int o, int sc, int ec, int sa, int ea) {
		return getGradientBitmap(w, h, c, o, sc, ec, sa, ea, null, 0, 0, 0);
	}
	public static Bitmap getGradientBitmap(int w, int h, int c, int o, int sc, int ec, int sa, int ea, String imgname, int imgdup, int imghori, int imgvert) {
		return getGradientRectangle(w, h, c, o, sc, ec, sa, ea, imgname, imgdup, imghori, imgvert).getBitmap();
	}*/
}
