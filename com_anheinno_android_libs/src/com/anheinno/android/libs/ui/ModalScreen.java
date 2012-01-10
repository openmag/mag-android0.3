package com.anheinno.android.libs.ui;

import com.anheinno.android.libs.R;

import android.app.Dialog;
import android.content.Context;
//import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
//import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ScrollView;

public abstract class ModalScreen extends Dialog {
	private ModalScreenLayout _content_view;
	
	public ModalScreen(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		
		_content_view = new ModalScreenLayout(context);
		super.setContentView(_content_view);		
	}
	
	public void setContentView(View view) {
		_content_view.setContentView(view);
	}
	
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onCreate();
	}
	
	protected abstract void onCreate();
	
	protected boolean onClose() {
		return true;
	}
	
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public boolean onKeyUp (int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			close();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	private final void close() {
		if(onClose()) {
			dismiss();
		}
	}
	
	class ModalScreenLayout extends Manager {
		private ScrollView _content;
		private Button _close_btn;
		private static final int PADDING = 5;
		
		ModalScreenLayout(Context context) {
			super(context);
			
			setPadding(PADDING, PADDING, PADDING, PADDING);
			
			_content = new ScrollView(context);
			addView(_content);
			_content.setPadding(PADDING, PADDING, PADDING, PADDING);
			_content.setScrollbarFadingEnabled(true);
			
			_close_btn = new Button(context);
			addView(_close_btn);
			
			_close_btn.setPadding(PADDING, PADDING, PADDING, PADDING);
			_close_btn.setText(R.string.screen_menu_close);
			View.OnClickListener close_listener = new View.OnClickListener() {
				public void onClick(View v) {
					close();
				}
			};
			_close_btn.setOnClickListener(close_listener);
		}
		
		@Override
		protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);
			
			_close_btn.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
			
			_content.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(height - _close_btn.getMeasuredHeight(), MeasureSpec.AT_MOST));
		
			height = _close_btn.getMeasuredHeight() + _content.getMeasuredHeight();
			this.setMeasuredDimension(width, height);
		}
		
		@Override
		protected void layoutChildren(int l, int t, int width, int height) {
			setChildPosition(_content, l, t);
			setChildPosition(_close_btn, (width - _close_btn.getMeasuredWidth())/2, _content.getMeasuredHeight());
		}
		
		private void setContentView(View view) {
			_content.removeAllViews();
			_content.addView(view);
		}
		
	}
	
}
