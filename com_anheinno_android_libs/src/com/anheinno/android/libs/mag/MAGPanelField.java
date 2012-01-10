package com.anheinno.android.libs.mag;



import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.ui.CustomButtonField;
import com.anheinno.android.libs.ui.Manager;


/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGPanelField extends Manager implements MAGContainerLayoutInterface {
	private MAGTitleArea _title_area;
	private MAGLayoutManager _mag_layout;
	private MAGPanel _panel;

	private static TextStyleDescriptor DEFAULT_FOLD_ICON_STYLE;
	private static TextStyleDescriptor DEFAULT_UNFOLD_ICON_STYLE;
	private static TextStyleDescriptor DEFAULT_FOCUS_FOLD_ICON_STYLE;
	private static TextStyleDescriptor DEFAULT_FOCUS_UNFOLD_ICON_STYLE;
	
	static {
		DEFAULT_FOLD_ICON_STYLE = new TextStyleDescriptor("icon=fold.png");
		DEFAULT_UNFOLD_ICON_STYLE = new TextStyleDescriptor("icon=unfold.png");
		DEFAULT_FOCUS_FOLD_ICON_STYLE = new TextStyleDescriptor("icon=fold_focus.png");
		DEFAULT_FOCUS_UNFOLD_ICON_STYLE = new TextStyleDescriptor("icon=unfold_focus.png");
	};
	
	private CustomButtonField _fold_icon;
	
	private TextStyleDescriptor _unfoldbg_desc;
	private TextStyleDescriptor _foldbg_desc;
	private TextStyleDescriptor _founfoldbg_desc;
	private TextStyleDescriptor _fofoldbg_desc;
	
	//private int _icon_padding = 3;

	//public MAGPanelField(MAGPanel panel) {
	public MAGPanelField(Context context) {
		super(context);
		_fold_icon = null;
		_title_area = null;
		_mag_layout = new MAGLayoutManager(context);
		//setPanel(panel);
	}
	
	public void setPanel(MAGPanel panel) {
		_panel = panel;

		if (_panel.title() != null && _panel.title().length() > 0) {
			_title_area = new MAGTitleArea(_panel);
		}else {
			_title_area = null;
		}

		_mag_layout.setContainer(_panel);
		
		removeAllViews();

		if (_panel.showFoldIcon()) {
			TextStyleDescriptor icon_name;
			
			_unfoldbg_desc = null;
			icon_name = _panel.style().getTextStyle("unfold-icon");
			if(icon_name != null) {
				_unfoldbg_desc = icon_name;
			}
			if(_unfoldbg_desc == null) {
				_unfoldbg_desc = DEFAULT_UNFOLD_ICON_STYLE;
			}

			_foldbg_desc = null;
			icon_name = _panel.style().getTextStyle("fold-icon");
			if(icon_name != null) {
				_foldbg_desc = icon_name;
			}
			if(_foldbg_desc == null) {
				_foldbg_desc = DEFAULT_FOLD_ICON_STYLE;
			}

			_founfoldbg_desc = null;
			icon_name = _panel.style().getTextStyle("focus-unfold-icon");
			if(icon_name != null) {
				_founfoldbg_desc = icon_name;
			}
			if(_founfoldbg_desc == null) {
				_founfoldbg_desc = DEFAULT_FOCUS_UNFOLD_ICON_STYLE;
			}

			_fofoldbg_desc = null;
			icon_name = _panel.style().getTextStyle("focus-fold-icon");
			if(icon_name != null) {
				_fofoldbg_desc = icon_name;
			}
			if(_fofoldbg_desc == null) {
				_fofoldbg_desc = DEFAULT_FOCUS_FOLD_ICON_STYLE;
			}

			OnClickListener btn_click = new OnClickListener() {
				public void onClick(View v) {
					if(_panel.showChildren()) {
						_panel.setExpandStatus(MAGPanel.PANEL_COLLAPSE);
						removeView(_mag_layout);
					}else {
						_panel.setExpandStatus(MAGPanel.PANEL_EXPAND);
						addView(_mag_layout);
					}
					updateFoldIcon();
					//requestLayout();
				}
			};
			
			_fold_icon = new CustomButtonField(getContext(), "", btn_click);

			addView(_fold_icon);
			updateFoldIcon();
		}else {
			_fold_icon = null;
		}

		if(_panel.showChildren()) {
			addView(_mag_layout);
		}
		
		//invalidate();
	}

	private void updateFoldIcon() {
		//System.out.println("updateFoldIcon" + _expand);
		if (!_panel.showChildren()) {
			_fold_icon.setTextStyle(_foldbg_desc);
			_fold_icon.setFocusTextStyle(_fofoldbg_desc);
		} else { //if (_expand.equals(MAGPanel.PANEL_EXPAND)) {
			_fold_icon.setTextStyle(_unfoldbg_desc);
			_fold_icon.setFocusTextStyle(_founfoldbg_desc);
		}
		super.forceLayout();
	}
	
	private float getTitleHeight() {
		float title_h = 0;
		if (_title_area != null) {
			title_h = _title_area.getTitleHeight();
		}
		int icon_h = 0;
		if (_fold_icon != null) {
			icon_h = _fold_icon.getMeasuredHeight();
		}
		return Math.max(title_h, icon_h);
	}

	@Override
	protected void onMeasure(int width, int height) {
		width = MeasureSpec.getSize(width);
		height = MeasureSpec.getSize(height);
		
		if (_panel.showFoldIcon()) {
			_fold_icon.measure(width, height);
		}
		
		if (_panel.title() != null && _panel.title().length() > 0) {
			if (_panel.showFoldIcon()) {
				_title_area.layout(width - _fold_icon.getMeasuredWidth());
			} else {
				_title_area.layout(width);
			}
			//System.out.println("MAGPanel: onMeasure: width=" + width + " title_width=" + _title_area.getTitleWidth());
		}
		
		int h = (int)getTitleHeight();

		if(_panel.showChildren()) {
			_mag_layout.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), 
					MeasureSpec.makeMeasureSpec(height,MeasureSpec.UNSPECIFIED));
			h += _mag_layout.getMeasuredHeight();
		}
		//System.out.println("MAGPanelField: width=" + width + " height=" + h);
		setMeasuredDimension(width, (int)h);
	}
	
	@Override
	protected void layoutChildren(int l, int t, int width, int height) {
		int top = 0;
		
		if (_panel.showFoldIcon()) {
			top += (int)((getTitleHeight() - _fold_icon.getMeasuredHeight())/2);
			_fold_icon.layout(0, top, _fold_icon.getMeasuredWidth(), top + _fold_icon.getMeasuredHeight());
		}

		top = (int)getTitleHeight();

		if(_panel.showChildren()) {
			_mag_layout.layout(0, top, _mag_layout.getMeasuredWidth(), top + _mag_layout.getMeasuredHeight());
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (_fold_icon != null || (_panel != null && _panel.title() != null && _panel.title().length() > 0)) {
			BackgroundDescriptor tmp_bg = null;
			
			if(_panel != null && _panel.style() != null) {
				_panel.style().getTitleBackground();
			}
			
			if(tmp_bg == null) {
				tmp_bg = BackgroundDescriptor.getDefaultPanelTitle();
			}
			if(tmp_bg != null) {
				/*Bitmap tmp_bitmap = GradientRectangleFactory.getGradientBitmap(getContext(), getMeasuredWidth(), (int)getTitleHeight(), tmp_bg);
				canvas.drawBitmap(tmp_bitmap, 0, 0, null);*/
				tmp_bg.draw(getContext(), canvas, 0, 0, getMeasuredWidth(), (int)getTitleHeight());
			}
		}
		
		if(_panel != null && _panel.title() != null && _panel.title().length() > 0) {
			//System.out.println("MAGPanel: draw title width=" + getMeasuredWidth() + " title_width=" + _title_area.getTitleWidth());
			int title_x = 0; //(int)(getMeasuredWidth() - _title_area.getTitleWidth())/2;
			if(_fold_icon != null) {
				//System.out.println("MAGPanel: fold_icon_width=" + _fold_icon.getMeasuredWidth());
				title_x += _fold_icon.getMeasuredWidth();
			}
			_title_area.drawTitle(canvas, title_x, (int)((getTitleHeight() - _title_area.getTitleHeight())/2));
		}
		super.onDraw(canvas);		
	}
	
	public void updateUi() {
		invalidate();
	}
	
	public void invalidateMAGComponent(MAGComponentInterface comp) {
		_mag_layout.invalidateMAGComponent(comp);
	}
	
	public void releaseResources() {
		_title_area.releaseResources();
		_title_area = null;
		
		_mag_layout.releaseResources();
		
		_mag_layout = null;
		_panel = null;
		
		removeAllViews();
		
		_fold_icon = null;
	}

}
