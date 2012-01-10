package com.anheinno.libs.mag.controls.contacts;

import android.content.Context;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

import com.anheinno.android.libs.JSONBrowserScreen;
import com.anheinno.android.libs.R;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.ui.EasyDialog;

public abstract class JSONSearchBrowserScreen extends JSONBrowserScreen {

	private EditText _find_text;
	
	private static final TextStyleDescriptor CONTACT_SEARCH_TITLE_STYLE;
	static {
		CONTACT_SEARCH_TITLE_STYLE = new TextStyleDescriptor("icon=search_icon.png padding=2");
	}

	public JSONSearchBrowserScreen(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		_find_text = null;
		setTitleText(" ");
		setTitleTextStyle(CONTACT_SEARCH_TITLE_STYLE);
	}
	
	protected void onDisplay() {
		super.onDisplay();
		_find_text = new EditText(getContext()) {
			@Override
		    public InputConnection onCreateInputConnection(final EditorInfo outAttrs)
		    {
		        outAttrs.imeOptions |= EditorInfo.IME_ACTION_GO;
		        return (super.onCreateInputConnection(outAttrs));
		    }
			
			@Override
			public boolean onKeyDown (int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_SEARCH) {
					hideSoftKeyboard();
					onSearch(_find_text.getText().toString());
					return true;
				}
				return super.onKeyDown(keyCode, event);
			}
		};
		setTitleField(_find_text);
		_find_text.setLines(1);
		_find_text.setBackgroundColor(Color.TRANSPARENT);
		_find_text.setFocusable(true);
		//_find_text.setText("Hello!!!");
		_find_text.setHint(getContext().getString(R.string.contact_screen_search_text_hint));	
	}
	
	@Override
	public boolean onKeyDown(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if(_find_text.getText().length() > 0) {
				_find_text.setText("");
				_find_text.requestFocus();
				onCancelSearch();
				return true;
			}
		}
		return super.onKeyDown(event);
	}
	
	protected abstract void onSearch(String text);
	protected abstract void onCancelSearch();

}
