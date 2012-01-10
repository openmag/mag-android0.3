package com.anheinno.android.libs.mag;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public interface MAGComponentInterface {

	String id();
	
	String title();
	
	Context getContext();

	void setWidth(int width);
	
	int getWidth();
	
	int getInnerWidth();
	
	int getInnerHeight();
	
	int getLeft();
	
	void setLeft(int left);
	
	int getTop();
	
	void setTop(int top);
	
	//int getActualHeight();
	
	void setRowHeight(int h);

	int getHeight();
	
	int getBorderWidthLeft();
	
	int getBorderWidthTop();
	
	int getBorderWidthRight();
	
	int getBorderWidthBottom();
	
	int getOffsetTop();
	
	int getOffsetLeft();
	
	int getPaddingTop();
	
	int getPaddingBottom();
	
	int getPaddingRight();
	
	int getPaddingLeft();
	
	void setPaddingLeft(int padding);

	MAGStyle style();

	MAGContainerInterface getParent();
	
	MAGDocument getMAGDocument();

	View initField(Context con);

	void updateField(View field);

	void setOffFocus();
	
	void setOnFocus(boolean showhint, boolean showstatus);
	
	void setChecked();
	
	void setUnChecked();

	View getField();
	
	void setField(View f);

	void unlink();

	String getAttributeValue(String fieldname);
	
	boolean visible();
	
	void drawBackground(Canvas canvas, int l, int t, int w, int h);
	
	void releaseResources();
}
