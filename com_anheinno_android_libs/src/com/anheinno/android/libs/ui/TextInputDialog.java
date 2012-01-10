package com.anheinno.android.libs.ui;

import com.anheinno.android.libs.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
//import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TextInputDialog extends AlertDialog implements OnClickListener {

	private TextInputDialogListener _callback;
	private final TextView _label;
	private final EditText _editor;

	public interface TextInputDialogListener {
		public void onTextSet(TextInputDialog dialog, String text);
	}
	
	public TextInputDialog(Context context, TextInputDialogListener callback, String label, String text) {
		this(context, android.R.style.Theme_Dialog, callback, label, text);
	}

	public TextInputDialog(Context context, int theme, TextInputDialogListener callback, String label, String text) {
		super(context, theme);

		_callback = callback;
		
		setButton(context.getText(android.R.string.ok), this);
		setButton2(context.getText(android.R.string.cancel), this);
		setIcon(android.R.drawable.edit_text);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.textinputdialog, null);
		setView(view);
		
		_label = (TextView)view.findViewById(R.id.tv_textinput_prompt);
		_editor = (EditText) view.findViewById(R.id.et_textinput);
		
		_label.setText(label);
		_editor.setText(text);
	}
	
	public CharSequence getText() {
		return _editor.getText().toString();
	}


	public void onClick(DialogInterface dialog, int which) {
		if(which == DialogInterface.BUTTON1) {
			_callback.onTextSet(this, _editor.getText().toString());
		}
	}

	/*@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putString(key, _label.getText());
		state.putString(key, _editor.getText());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int year = savedInstanceState.getInt(YEAR);
		int month = savedInstanceState.getInt(MONTH);
		int day = savedInstanceState.getInt(DAY);
		
	}*/
}
