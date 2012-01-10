package com.anheinno.android.libs.graphics;


import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.util.LUFLimitedHashtable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;


/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class BitmapRepository {
	
	private static final int BITMAP_REPOSITORY_SIZE = 32;
	private static LUFLimitedHashtable<String, Bitmap> _bitmap_tbl;
	static {
		_bitmap_tbl = new LUFLimitedHashtable<String, Bitmap>(BITMAP_REPOSITORY_SIZE, "BitmapRepository");
	}
	
	public static Bitmap getBitmapByName(Context context, String name) {
		if (!_bitmap_tbl.containsKey(name)) {
			Bitmap bm = getBitmapById(context, name);
			if (bm != null) {
				_bitmap_tbl.put(name, bm);
				return bm;
			} else {
				return null;
			}
		} else {
			return _bitmap_tbl.get(name);
		}
	}
	
	private static final String[] image_suffix = {"jpg", "jpeg", "gif", "png"};
	public static final byte[] _image_temp_buffer = new byte[1024*32];
	
	private static String removeSuffix(String name) {
		int dot_pos = name.lastIndexOf('.');
		if(dot_pos > 0) {
			String suffix = name.substring(dot_pos+1);
			for(int i = 0; i < image_suffix.length; i ++) {
				if(suffix.equalsIgnoreCase(image_suffix[i])) {
					name = name.substring(0, dot_pos);
				}
			}
		}
		if(name.length() > 0) {
			return name;
		}else {
			return null;
		}
	}
	
	private static Bitmap getBitmapById(Context context, String name) {
		name = removeSuffix(name);
		if(name == null) {
			return null;
		}
		//System.out.println("getBitmapById: to locate bitmap resource " + name + " for " + context.getPackageName());
		Resources res = context.getResources();
		int id = res.getIdentifier(name, "drawable", context.getPackageName());
		if(id == 0) {
			id = res.getIdentifier(name, "drawable", "com.anheinno.android.libs.mag");
		}
		if(id == 0) {
			id = res.getIdentifier(name, "drawable", "com.anheinno.android.libs");
		}
		if(id != 0) {
			//System.out.println(" find id=" + id);
			synchronized(_image_temp_buffer) {
				BitmapFactory.Options opts = new BitmapFactory.Options();
	            opts.inPurgeable=true;
	            opts.inInputShareable=true;
	            opts.inTempStorage = _image_temp_buffer;
				opts.inJustDecodeBounds = false;
				opts.inDither = false;
				opts.inSampleSize = 1;
				opts.inScaled = false;
				opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
				opts.inDensity = opts.inScreenDensity = opts.inTargetDensity = GraphicUtilityClass.getScreenDensity(context);
				Bitmap bm = BitmapFactory.decodeResource(res, id, opts);
				/*if(bm == null) {
					System.out.println("failed!........");
				}else {
					System.out.println("Success!!!");
				}*/
				return bm;
			}
		}else {
			//System.out.println("Cannot find id!!");
			return null;
		}
	}

	public static Bitmap getBitmapByNameAndWidth(Context context, String name, int width) {
		return getBitmapByNameAndSize(context, name, width, 0);
	}
	public static Bitmap getBitmapByNameAndHeight(Context context, String name, int height) {
		return getBitmapByNameAndSize(context, name, 0, height);
	}
	
	public static Bitmap getBitmapByNameAndSize(Context context, String name, int width, int height) {
		if(width <= 0 && height <= 0) {
			return getBitmapByName(context, name);
		}
		String key = name + "?w=" + width + "&h=" + height;
		if (!_bitmap_tbl.containsKey(key)) {
			Bitmap origin = getBitmapById(context, name);
			if(origin != null) {
				int img_w = origin.getWidth();
				int img_h = origin.getHeight();
				int draw_w = width;
				int draw_h = height;
				if(width == 0) {
					draw_w = img_w * draw_h / img_h + 1;
				}else if(height == 0) {
					draw_h = img_h * draw_w / img_w + 1;
				}
				
				//Bitmap bm = Bitmap.createScaledBitmap(origin, draw_w, draw_h, true);
				try {
					Bitmap bmp = Bitmap.createBitmap(draw_w, draw_h, Bitmap.Config.ARGB_8888);
					Canvas canvas = new Canvas(bmp);
					Matrix draw_matrix = new Matrix();
					draw_matrix.setScale(draw_w*1.0f/img_w, draw_h*1.0f/img_h);
					canvas.drawBitmap(origin, draw_matrix, null);
					origin.recycle();
					origin = null;
					_bitmap_tbl.put(key, bmp);
					return bmp;
				}catch(final IllegalArgumentException e){
					LOG.error("BitmapRepository", "getBitmapByNameAndSize", e);
				}catch(final OutOfMemoryError e){
					LOG.error("BitmapRepository", "getBitmapByNameAndSize: " + e, null);
				}
				return null;
			}else {
				return null;
			}
		}else {
			return (Bitmap) _bitmap_tbl.get(key);
		}
	}
	
	public static void recycle() {
		Object[] bitmaps = _bitmap_tbl.release();
		for(int i = 0; i < bitmaps.length; i ++) {
			if(bitmaps[i] instanceof Bitmap) {
				System.out.println("Recycle i=" + i);
				((Bitmap)bitmaps[i]).recycle();
			}
		}
	}
}
