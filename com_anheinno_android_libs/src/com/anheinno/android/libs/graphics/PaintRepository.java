package com.anheinno.android.libs.graphics;


import com.anheinno.android.libs.util.LUFLimitedHashtable;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class PaintRepository {
	private static final int PAINT_REPOSITORY_LIMIT = 64;
	private static LUFLimitedHashtable<String, Paint> _font_table = null;
//	public static final float DEFAULT_FONT_SIZE = 0;
	
	private static Paint _default_paint = null;
	
	private static String getFontID(boolean bold, boolean italic, boolean underline, float rate, int color, int alpha) {
		return "f_" + bold + "_" + "_" + italic + "_" + underline + "_" + GraphicUtilityClass.makeColor(alpha, color) + "_" + rate;
	}
	
	private static Paint makePaint(Context context, boolean bold, boolean italic, boolean underline, float rate, int color, int alpha) {
		Paint p = new Paint();
		p.setAntiAlias(true);
		p.setTextAlign(Paint.Align.LEFT);
		if(bold) {
			p.setFakeBoldText(true);
			p.setTypeface(Typeface.DEFAULT_BOLD);
		}else {
			p.setTypeface(Typeface.SANS_SERIF);
		}
		if(italic) {
			p.setTextSkewX(-0.25f);
		}else {
			p.setTextSkewX(0.0f);
		}
		if(underline) {
			p.setUnderlineText(true);
		}
		if(GraphicUtilityClass.isValidColor(color)) {
			int clr = GraphicUtilityClass.makeColor(alpha, color);
			p.setColor(clr);
		}
		Paint def_paint = getDefaultPaint(context);
		p.setTextSize(def_paint.getTextSize()*rate);
		p.setTextScaleX(1.0f);
		return p;
	}
	
	/**
	 * get cached Font Paint object
	 * 
	 * @param bold
	 * @param italic
	 * @param underline
	 * @param rate
	 * @param color
	 * @param alpha
	 * @return
	 */
	public static Paint getFontPaint(Context context, boolean bold, boolean italic, boolean underline, float rate, int color, int alpha) {
		String id = getFontID(bold, italic, underline, rate, color, alpha);
		if(_font_table == null) {
			_font_table = new LUFLimitedHashtable<String, Paint>(PAINT_REPOSITORY_LIMIT, "PaintRepository");
		}
		if(!_font_table.containsKey(id)) {
			_font_table.put(id, makePaint(context, bold, italic, underline, rate, color, alpha));
		}
		return _font_table.get(id);
	}
	
	public static Paint getDefaultPaint(Context context) {
		if(_default_paint == null) {
			_default_paint = new Paint();
			_default_paint.setAntiAlias(true);
			_default_paint.setTextAlign(Paint.Align.LEFT);
			_default_paint.setTextSize(GraphicUtilityClass.getMinFontSize(context));
		}
		return _default_paint;
	}
	
}
