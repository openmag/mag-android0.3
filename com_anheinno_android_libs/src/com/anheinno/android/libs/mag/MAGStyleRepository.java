package com.anheinno.android.libs.mag;

import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;

public class MAGStyleRepository {

	private static final BackgroundDescriptor DEFAULT_BUTTON_BACKGROUND;
	private static final BackgroundDescriptor DEFAULT_BUTTON_FOCUS_BACKGROUND;
	private static final BackgroundDescriptor DEFAULT_BUTTON_BACKGROUND_6;
	private static final BackgroundDescriptor DEFAULT_BUTTON_FOCUS_BACKGROUND_6;
	private static final TextStyleDescriptor DEFAULT_TEXT_STYLE;
	private static final TextStyleDescriptor DEFAULT_TEXT_STYLE_6;
	private static final TextStyleDescriptor DEFAULT_FOCUS_TEXT_STYLE;
	private static final TextStyleDescriptor DEFAULT_FOCUS_TEXT_STYLE_6;

	static {
		DEFAULT_BUTTON_BACKGROUND = new BackgroundDescriptor(
				"image=bitmapborder_gray.png duplicate=bitmap-border border-top=11 border-left=12 border-right=12 border-bottom=15");
		DEFAULT_BUTTON_FOCUS_BACKGROUND = new BackgroundDescriptor(
				"image=bitmapborder_blue.png duplicate=bitmap-border border-top=11 border-left=12 border-right=12 border-bottom=15");
		// left=10 top=22 bottom=10 right=10
		DEFAULT_BUTTON_BACKGROUND_6 = new BackgroundDescriptor(
				"image=bitmapborder_grey_6.png duplicate=bitmap-border border-top=22 border-left=10 border-right=10 border-bottom=10");
		DEFAULT_BUTTON_FOCUS_BACKGROUND_6 = new BackgroundDescriptor(
				"image=bitmapborder_blue_6.png duplicate=bitmap-border border-top=22 border-left=10 border-right=10 border-bottom=10");

		DEFAULT_TEXT_STYLE = new TextStyleDescriptor(
				"color=white padding-top=10 padding-bottom=10 padding-left=13 padding-right=15 use-full=false");
		DEFAULT_TEXT_STYLE_6 = new TextStyleDescriptor(
				"color=black padding-top=13 padding-bottom=10 padding-left=13 padding-right=15 use-full=false");

		DEFAULT_FOCUS_TEXT_STYLE = new TextStyleDescriptor(
				"color=white padding-top=10 padding-bottom=10 padding-left=13 padding-right=15 use-full=false");
		DEFAULT_FOCUS_TEXT_STYLE_6 = new TextStyleDescriptor(
				"color=white padding-top=13 padding-bottom=10 padding-left=13 padding-right=15 use-full=false");
	}

	public static BackgroundDescriptor getButtonBackground() {
		if (getOSMajorVersion() < 6) {
			return DEFAULT_BUTTON_BACKGROUND;
		} else {
			return DEFAULT_BUTTON_BACKGROUND_6;
		}
	}

	public static int getOSMajorVersion() {
		return 6;
	}

	public static BackgroundDescriptor getFocusButtonBackground() {
		if (getOSMajorVersion() < 6) {
			return DEFAULT_BUTTON_FOCUS_BACKGROUND;
		} else {
			return DEFAULT_BUTTON_FOCUS_BACKGROUND_6;
		}
	}

	public static TextStyleDescriptor getFocusButtonTextStyle() {
		if (getOSMajorVersion() < 6) {
			return DEFAULT_FOCUS_TEXT_STYLE;
		} else {
			return DEFAULT_FOCUS_TEXT_STYLE_6;
		}
	}

	public static TextStyleDescriptor getButtonTextStyle() {
		if (getOSMajorVersion() < 6) {
			return DEFAULT_TEXT_STYLE;
		} else {
			return DEFAULT_TEXT_STYLE_6;
		}
	}

	private static final TextStyleDescriptor DEFAULT_INSERT_BUTTON_STYLE;
	private static final TextStyleDescriptor DEFAULT_INSERT_BUTTON_FOCUS_STYLE;
	private static final TextStyleDescriptor DEFAULT_DELETE_BUTTON_STYLE;
	private static final TextStyleDescriptor DEFAULT_DELETE_BUTTON_FOCUS_STYLE;
	private static final TextStyleDescriptor DEFAULT_UP_BUTTON_STYLE;
	private static final TextStyleDescriptor DEFAULT_UP_BUTTON_FOCUS_STYLE;
	private static final TextStyleDescriptor DEFAULT_DOWN_BUTTON_STYLE;
	private static final TextStyleDescriptor DEFAULT_DOWN_BUTTON_FOCUS_STYLE;

	private static final TextStyleDescriptor DEFAULT_INSERT_BUTTON_STYLE_6;
	private static final TextStyleDescriptor DEFAULT_INSERT_BUTTON_FOCUS_STYLE_6;
	private static final TextStyleDescriptor DEFAULT_DELETE_BUTTON_STYLE_6;
	private static final TextStyleDescriptor DEFAULT_DELETE_BUTTON_FOCUS_STYLE_6;
	private static final TextStyleDescriptor DEFAULT_UP_BUTTON_STYLE_6;
	private static final TextStyleDescriptor DEFAULT_UP_BUTTON_FOCUS_STYLE_6;
	private static final TextStyleDescriptor DEFAULT_DOWN_BUTTON_STYLE_6;
	private static final TextStyleDescriptor DEFAULT_DOWN_BUTTON_FOCUS_STYLE_6;

	static {
		DEFAULT_INSERT_BUTTON_STYLE = new TextStyleDescriptor(
				"icon=plus_sign.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_INSERT_BUTTON_FOCUS_STYLE = new TextStyleDescriptor(
				"icon=plus_sign_m.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_DELETE_BUTTON_STYLE = new TextStyleDescriptor(
				"icon=minus_sign.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_DELETE_BUTTON_FOCUS_STYLE = new TextStyleDescriptor(
				"icon=minus_sign_m.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_UP_BUTTON_STYLE = new TextStyleDescriptor(
				"icon=arrow_long_top.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_UP_BUTTON_FOCUS_STYLE = new TextStyleDescriptor(
				"icon=arrow_long_top_m.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_DOWN_BUTTON_STYLE = new TextStyleDescriptor(
				"icon=arrow_long_down.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_DOWN_BUTTON_FOCUS_STYLE = new TextStyleDescriptor(
				"icon=arrow_long_down_m.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");

		DEFAULT_INSERT_BUTTON_STYLE_6 = new TextStyleDescriptor(
				"icon=plus_sign_6.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_INSERT_BUTTON_FOCUS_STYLE_6 = new TextStyleDescriptor(
				"icon=plus_sign_m_6.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_DELETE_BUTTON_STYLE_6 = new TextStyleDescriptor(
				"icon=minus_sign_6.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_DELETE_BUTTON_FOCUS_STYLE_6 = new TextStyleDescriptor(
				"icon=minus_sign_m_6.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_UP_BUTTON_STYLE_6 = new TextStyleDescriptor(
				"icon=arrow_long_top_6.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_UP_BUTTON_FOCUS_STYLE_6 = new TextStyleDescriptor(
				"icon=arrow_long_top_m_6.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_DOWN_BUTTON_STYLE_6 = new TextStyleDescriptor(
				"icon=arrow_long_down_6.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_DOWN_BUTTON_FOCUS_STYLE_6 = new TextStyleDescriptor(
				"icon=arrow_long_down_m_6.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
	}

	private static final TextStyleDescriptor DEFAULT_LEFT_BUTTON_STYLE;
	private static final TextStyleDescriptor DEFAULT_LEFT_BUTTON_FOCUS_STYLE;
	private static final TextStyleDescriptor DEFAULT_RIGHT_BUTTON_STYLE;
	private static final TextStyleDescriptor DEFAULT_RIGHT_BUTTON_FOCUS_STYLE;

	private static final TextStyleDescriptor DEFAULT_LEFT_BUTTON_STYLE_6;
	private static final TextStyleDescriptor DEFAULT_LEFT_BUTTON_FOCUS_STYLE_6;
	private static final TextStyleDescriptor DEFAULT_RIGHT_BUTTON_STYLE_6;
	private static final TextStyleDescriptor DEFAULT_RIGHT_BUTTON_FOCUS_STYLE_6;

	static {
		DEFAULT_LEFT_BUTTON_STYLE = new TextStyleDescriptor(
				"icon=arrow_short_left.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_LEFT_BUTTON_FOCUS_STYLE = new TextStyleDescriptor(
				"icon=arrow_short_left_m.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_RIGHT_BUTTON_STYLE = new TextStyleDescriptor(
				"icon=arrow_short_right.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_RIGHT_BUTTON_FOCUS_STYLE = new TextStyleDescriptor(
				"icon=arrow_short_right_m.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");

		DEFAULT_LEFT_BUTTON_STYLE_6 = new TextStyleDescriptor(
				"icon=arrow_short_left_6.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_LEFT_BUTTON_FOCUS_STYLE_6 = new TextStyleDescriptor(
				"icon=arrow_short_left_m_6.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_RIGHT_BUTTON_STYLE_6 = new TextStyleDescriptor(
				"icon=arrow_short_right_6.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
		DEFAULT_RIGHT_BUTTON_FOCUS_STYLE_6 = new TextStyleDescriptor(
				"icon=arrow_short_right_m_6.png padding-top=13 padding-bottom=13 padding-left=15 padding-right=15 use-full=false");
	}

	public static TextStyleDescriptor getPlusTextStyle() {
		if (getOSMajorVersion() < 6) {
			return DEFAULT_INSERT_BUTTON_FOCUS_STYLE;
		} else {
			return DEFAULT_INSERT_BUTTON_FOCUS_STYLE_6;
		}
	}

	public static TextStyleDescriptor getFocusPlusTextStyle() {
		if (getOSMajorVersion() < 6) {
			return DEFAULT_INSERT_BUTTON_STYLE;
		} else {
			return DEFAULT_INSERT_BUTTON_STYLE_6;
		}
	}

	public static TextStyleDescriptor getMinusTextStyle() {
		if (getOSMajorVersion() < 6) {
			return DEFAULT_DELETE_BUTTON_FOCUS_STYLE;
		} else {
			return DEFAULT_DELETE_BUTTON_FOCUS_STYLE_6;
		}
	}

	public static TextStyleDescriptor getFocusMinusTextStyle() {
		if (getOSMajorVersion() < 6) {
			return DEFAULT_DELETE_BUTTON_STYLE;
		} else {
			return DEFAULT_DELETE_BUTTON_STYLE_6;
		}
	}

	public static TextStyleDescriptor getAscTextStyle() {
		if (getOSMajorVersion() < 6) {
			return DEFAULT_UP_BUTTON_FOCUS_STYLE;
		} else {
			return DEFAULT_UP_BUTTON_FOCUS_STYLE_6;
		}
	}

	public static TextStyleDescriptor getFocusAscTextStyle() {
		if (getOSMajorVersion() < 6) {
			return DEFAULT_UP_BUTTON_STYLE;
		} else {
			return DEFAULT_UP_BUTTON_STYLE_6;
		}
	}

	public static TextStyleDescriptor getDescTextStyle() {
		if (getOSMajorVersion() < 6) {
			return DEFAULT_DOWN_BUTTON_FOCUS_STYLE;
		} else {
			return DEFAULT_DOWN_BUTTON_FOCUS_STYLE_6;
		}
	}

	public static TextStyleDescriptor getFocusDescTextStyle() {
		if (getOSMajorVersion() < 6) {
			return DEFAULT_DOWN_BUTTON_STYLE;
		} else {
			return DEFAULT_DOWN_BUTTON_STYLE_6;
		}
	}

	public static TextStyleDescriptor getLeftTextStyle() {
		if (getOSMajorVersion() < 6) {
			return DEFAULT_LEFT_BUTTON_FOCUS_STYLE;
		} else {
			return DEFAULT_LEFT_BUTTON_FOCUS_STYLE_6;
		}
	}

	public static TextStyleDescriptor getFocusLeftTextStyle() {
		if (getOSMajorVersion() < 6) {
			return DEFAULT_LEFT_BUTTON_STYLE;
		} else {
			return DEFAULT_LEFT_BUTTON_STYLE_6;
		}
	}

	public static TextStyleDescriptor getRightTextStyle() {
		if (getOSMajorVersion() < 6) {
			return DEFAULT_RIGHT_BUTTON_FOCUS_STYLE;
		} else {
			return DEFAULT_RIGHT_BUTTON_FOCUS_STYLE_6;
		}
	}

	public static TextStyleDescriptor getFocusRightTextStyle() {
		if (getOSMajorVersion() < 6) {
			return DEFAULT_RIGHT_BUTTON_STYLE;
		} else {
			return DEFAULT_RIGHT_BUTTON_STYLE_6;
		}
	}
	
	private static final TextStyleDescriptor CHECKED_TEXT_STYLE;
	private static final TextStyleDescriptor CHECKED_FOCUS_TEXT_STYLE;
	private static final TextStyleDescriptor UNCHECKED_TEXT_STYLE;
	private static final TextStyleDescriptor UNCHECKED_FOCUS_TEXT_STYLE;
	
	static {
		CHECKED_TEXT_STYLE = new TextStyleDescriptor("icon=select.png padding=5");
		CHECKED_FOCUS_TEXT_STYLE = new TextStyleDescriptor("icon=select_focus.png padding=5");
		UNCHECKED_TEXT_STYLE = new TextStyleDescriptor("icon=unselect.png padding=5");
		UNCHECKED_FOCUS_TEXT_STYLE = new TextStyleDescriptor("icon=unselect_focus.png padding=5");
	}
	
	public static TextStyleDescriptor getCheckedTextStyle() {
		return CHECKED_TEXT_STYLE;
	}
	public static TextStyleDescriptor getFocusCheckedTextStyle() {
		return CHECKED_FOCUS_TEXT_STYLE;
	}
	public static TextStyleDescriptor getUncheckedTextStyle() {
		return UNCHECKED_TEXT_STYLE;
	}
	public static TextStyleDescriptor getFocusUncheckedTextStyle() {
		return UNCHECKED_FOCUS_TEXT_STYLE;
	}

}
