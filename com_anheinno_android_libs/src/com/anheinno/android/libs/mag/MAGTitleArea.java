package com.anheinno.android.libs.mag;

import android.graphics.Canvas;

import com.anheinno.android.libs.graphics.TextDrawArea;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;


/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGTitleArea {
	private MAGComponent _component;
	private TextDrawArea _text_area;

	/**
	 * 从MAGComponent创建一个MAGTitleArea实例，用于绘制该MAGComponent 的标题部分
	 * 
	 * @param comp
	 *            创建的MAGTitleArea所属的MAGComponent
	 */
	public MAGTitleArea(MAGComponent comp) {
		_component = comp;
		init();
	}

	private void init() {
		_text_area = new TextDrawArea(_component.getContext(), _component.title());
		StringBuffer buf = new StringBuffer();
		String tmp = null;
		tmp = _component.style().get("title-color");
		if (tmp != null) {
			if (buf.length() > 0) {
				buf.append(' ');
			}
			buf.append("color=");
			buf.append(tmp);
		}
		tmp = _component.style().get("title-icon");
		if (tmp != null) {
			if (buf.length() > 0) {
				buf.append(' ');
			}
			buf.append("icon=");
			buf.append(tmp);
		}
		tmp = _component.style().get("title-padding");
		if (tmp != null) {
			if (buf.length() > 0) {
				buf.append(' ');
			}
			buf.append("padding=");
			buf.append(tmp);
		}
		tmp = _component.style().get("title-align");
		if (tmp != null) {
			if (buf.length() > 0) {
				buf.append(' ');
			}
			buf.append("text-align=");
			buf.append(tmp);
		}
		tmp = _component.style().get("title-valign");
		if (tmp != null) {
			if (buf.length() > 0) {
				buf.append(' ');
			}
			buf.append("text-valign=");
			buf.append(tmp);
		}
		tmp = _component.style().get("title-icon-valign");
		if (tmp != null) {
			if (buf.length() > 0) {
				buf.append(' ');
			}
			buf.append("icon-valign=");
			buf.append(tmp);
		}
		tmp = _component.style().get("title-font-scale");
		if (tmp != null) {
			if (buf.length() > 0) {
				buf.append(' ');
			}
			buf.append("font-scale=");
			buf.append(tmp);
		}
		tmp = _component.style().get("title-font-weight");
		if (tmp != null) {
			if (buf.length() > 0) {
				buf.append(' ');
			}
			buf.append("font-weight=");
			buf.append(tmp);
		}
		tmp = _component.style().get("title-font-style");
		if (tmp != null) {
			if (buf.length() > 0) {
				buf.append(' ');
			}
			buf.append("font-style=");
			buf.append(tmp);
		}
		tmp = _component.style().get("title-text-decoration");
		if (tmp != null) {
			if (buf.length() > 0) {
				buf.append(' ');
			}
			buf.append("text-decoration=");
			buf.append(tmp);
		}
		TextStyleDescriptor style = new TextStyleDescriptor(buf.toString());
		tmp = _component.style().get("title-text-style");
		if (tmp != null) {
			style.parse(tmp);
		}

		_text_area.setStyle(style);
	}

	private int calculateWidth(int width) {
		double w = _component.style().getDouble("title-width");
		if (w > 0) {
			if (w <= 1) {
				return (int) (width * w);
			} else {
				return (int) w;
			}
		} else {
			return width;
		}
	}

	private int calculateHeight() {
		int th = _component.style().getInt("title-height");
		if (th <= 0) {
			th = 0;
		}
		return th;
	}

	/**
	 * 初始化布局
	 * 
	 * @param width
	 *            分配给该MAGComponent的标题区域的宽度
	 */
	public void layout(int width) {
		int occupy_width = calculateWidth(width);
		int occupy_height = calculateHeight();

		_text_area.setWidth(occupy_width);
		_text_area.setHeight(occupy_height);
	}

	/**
	 * 获得标题区域的实际高度，必须在layout之后调用
	 * 
	 * @return 返回改标题区域实际的高度像素值
	 */
	public float getTitleHeight() {
		return _text_area.getHeight();
	}

	/**
	 * 在layout之前，获得该标题区域期望占据的区域的宽度
	 * 
	 * @return 返回期望占据的区域宽度的像素值
	 */
	public float getPreferredWidth() {
		return _text_area.getPreferredWidth();
	}

	/**
	 * 获得标题区域的实际宽度，必须在layout之后调用
	 * 
	 * @return 返回改标题区域实际的宽度像素值
	 */
	public float getTitleWidth() {
		return _text_area.getWidth();
	}

	/**
	 * 在给定的Graphics对象上绘制该标题区域
	 * 
	 * @param g
	 *            绘图的Graphics对象
	 * @param left
	 *            左偏移量像素值
	 * @param top
	 *            顶偏移量像素值
	 */
	public void drawTitle(Canvas g, int left, int top) {
		_text_area.draw(g, left, top);
	}

	/**
	 * 2011/2/24 更改text内容
	 * @param text
	 */
	public void setText(String text) {
		_text_area.setText(text);
	}
	
	public void releaseResources() {
		_component = null;
		_text_area.releaseResources();
	}
}
