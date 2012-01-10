package com.anheinno.android.libs.graphics;


import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;


public class GraphicUtilityClass {

	public static final int INVALID_COLOR = 0x00FFFFFF;
	public static final int TRANSPARENT   = 0x00000000;

	public static int rgb2greyscale(int color) {
    	if(color > 256*256*256) {
            color = color % (256*256*256);
        }
    	int r = color/256/256;
    	int g = (color - r*256*256)/256;
    	int b = color - r*256*256 - g*256;
    	return (int)(0.299 * r + 0.587 * g + 0.114 * b);
    }
    
    public static int getInverseColor(int color) {
        int grey = 255 - rgb2greyscale(color);
        if(grey < 128) {
        	grey = 0;
        }else {
        	grey = 255;
        }
        return (grey << 16) + (grey<<8) + grey;
    }

    public static String colorHTML(int color) {
    	String hex = Integer.toHexString(color & 0xffffff);
    	while(hex.length() < 6) {
    		hex = '0' + hex;
    	}
    	return '#' + hex;
    }
    
    public static int htmlColor(String color) {
        color = color.toLowerCase();
        String[] colors = {"red", "green", "blue", "yellow", "purple", "cyan", "white", "black"};
        int[] codes = {0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFFFF00, 0xFFFF00FF, 0xFF00FFFF, 0xFFFFFFFF, 0xFF000000};
        if(color.startsWith("#")) {
            int a=0;
            int b=0;
            int c=0;
            if(color.length() >= 7) {
                a = Integer.parseInt(color.substring(1, 3), 16);
                b = Integer.parseInt(color.substring(3, 5), 16);
                c = Integer.parseInt(color.substring(5, 7), 16);
            }else if(color.length() >= 4) {
                a = Integer.parseInt(color.substring(1, 2), 16)*16;
                b = Integer.parseInt(color.substring(2, 3), 16)*16;
                c = Integer.parseInt(color.substring(3, 4), 16)*16;
            }
            return Color.rgb(a, b, c);
        }else {
            for(int i = 0; i < colors.length; i ++) {
                if(color.equalsIgnoreCase(colors[i])) {
                    return codes[i];
                }
            }
            return INVALID_COLOR;
        }
    }
    
    public static boolean isValidColor(int clr) {
    	if(clr != INVALID_COLOR) {
    		return true;
    	}else {
    		return false;
    	}
    }

    
	public static int[] fromARGB(int pixel) {
		int[] argb = new int[4];
		argb[0] = (pixel >> 24) & 0xFF;
		argb[1] = (pixel >>16) & 0xFF;
		argb[2] = (pixel >>8) & 0xFF;
		argb[3] = pixel & 0xFF;
		return argb;
	}

	public static int toARGB(int[] argb) {
		return (argb[0] << 24) | (argb[1] << 16) | (argb[2] << 8) | argb[3];
	}
	
	public static int getAlpha(int color) {
		return ((0xff000000 & color) >> 24);
	}
	
	public static int getRGB(int color) {
		return (0xffffff&color);
	}
	
	/*public static void applyAlpha(Bitmap bitmap, int left, int top, int width, int height, Bitmap mask, int mask_left, int mask_top) {
		int[] data = new int[width*height];
		int[] mask_data = new int[width*height];
		
		bitmap.getARGB(data, 0, width, left, top, width, height);
		mask.getARGB(mask_data, 0, width, mask_left, mask_top, width, height);
		for(int j = 0; j < data.length; j ++) {
			data[j] = (data[j] & 0x00FFFFFF) | (mask_data[j]& 0xFF000000);
		}
		bitmap.setARGB(data, 0, width, left, top, width, height);
	}*/
	
	private static Rect __src = new Rect();
	private static Rect __dst = new Rect();
	public static void copyPixels(Canvas c, int left, int top, int width, int height, Bitmap copy_image, int copy_left, int copy_top) {
		synchronized(GraphicUtilityClass.class) {
			__src.set(copy_left, copy_top, copy_left + width, copy_top + height);
			__dst.set(left, top, left + width, top + height);
			c.drawBitmap(copy_image, __src, __dst, null);
		}
	}
	
	public static synchronized int getDisplayWidth(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		return display.getWidth();
	}
	
	public static synchronized int getDisplayHeight(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		return display.getHeight() - getStatusBarHeight(display.getWidth());
	}
	
	public static synchronized int getRawDisplayHeight(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		return display.getHeight();
	}
	
	public static synchronized boolean isLandscape(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		if(display.getWidth() > display.getHeight()) {
			return true;
		}else {
			return false;
		}
	}
	
	private static DisplayMetrics _display_metrics = null;
	private static float MIN_TOUCH_SIZE = 0.25f;
	private static float MIN_FONT_SIZE = 0.1f;
	public static int STANDARD_DPI = 160;
	
	public static int getScreenDensity(Context context) {
		retrieveDisplayMetrics(context);
		return (int)(_display_metrics.density*DisplayMetrics.DENSITY_DEFAULT);
	}
	public static int getMinTouchWidth(Context context) {
		retrieveDisplayMetrics(context);
		return (int)(MIN_TOUCH_SIZE*_display_metrics.density*STANDARD_DPI);
	}
	
	public static int getMinTouchHeight(Context context) {
		retrieveDisplayMetrics(context);
		return (int)(MIN_TOUCH_SIZE*_display_metrics.density*STANDARD_DPI);
	}
	
	public static int getMinFontSize(Context context) {
		retrieveDisplayMetrics(context);
		float sys_scale = context.getResources().getConfiguration().fontScale;
		return (int)(MIN_FONT_SIZE*_display_metrics.density*STANDARD_DPI*sys_scale);
	}
	
	public static boolean isTouchScreen(Context context) {
		int touchscreen = context.getResources().getConfiguration().touchscreen;
		switch(touchscreen) {
		case Configuration.TOUCHSCREEN_NOTOUCH:
			return false;
		default:
			return true;
		}
	}
	
	public static boolean isNavigation(Context context) {
		int model = getModel();
		if(model == PLAYBOOK || model == LEPAD) {
			return false;
		}else if(model == GT_P7500) {
			return true;
		}
		int nav = context.getResources().getConfiguration().navigation;
		switch(nav) {
		case Configuration.NAVIGATION_NONAV:
			return false;
		default:
			return true;
		}
	}
	
	public static final int GENERAL   = 0;
	public static final int PLAYBOOK  = 1;
	public static final int LEPAD     = 2;
	public static final int GT_P7500  = 3;
	
	public static int getModel() {
		String model = Build.DEVICE.toLowerCase();
		if(model.indexOf("playbook") >= 0) {
			return PLAYBOOK;
		}else if(model.indexOf("lepad") >= 0){
			return LEPAD;
		}else if(model.indexOf("gt-p7500") >= 0) {
			return GT_P7500;
		}else {
			return GENERAL;
		}
	}
	
	private static void retrieveDisplayMetrics(Context context) {
		if(_display_metrics == null) {
			Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
			_display_metrics = new DisplayMetrics();
			display.getMetrics(_display_metrics);
		}
	}
	
	private static int getStatusBarHeight(int width) {
		// 240x320 - 20px 320x480 - 25px 480x800+ - 38px
		int model = getModel();
		if(model == PLAYBOOK) {
			return 0;
		}else if(model == LEPAD) {
			return 50;
		}else if(model == GT_P7500) {
			return 48;
		}else if(width <= 240) {
			return 20;
		}else if(width <= 320) {
			return 25;
		}else {
			return 38;
		}
	}
	/*private static Rect _display_rect = null;
	private static Rect getDisplayRect(Context context) {
		if(_display_rect == null) {
			_display_rect = new Rect();
			Window win = ((Activity)context).getWindow();
			win.getDecorView().getWindowVisibleDisplayFrame(_display_rect);
		}
		return _display_rect;
	}
	public static int getDisplayHeight(Context context) {
		Rect rect = getDisplayRect(context);
		if(rect != null) {
			return rect.height();
		}
		return 0;
	}
	public static int getDisplayWidth(Context context) {
		Rect rect = getDisplayRect(context);
		if(rect != null) {
			return rect.width();
		}
		return 0;
	}*/

	public static int makeColor(int alpha, int color) {
		if(color == INVALID_COLOR) {
			return INVALID_COLOR;
		}else {
			return ((alpha & 0xFF) << 24) | (color & 0x00FFFFFF);
		}
	}

	public static float getFontHeight(Paint p) {
		return p.getTextSize()*1.1f;
	}

}
