package com.anheinno.android.libs.mag;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.widget.EditText;

/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGTextinputField extends MAGColorLabelManager {

	private EditText _editor;
	private String _value;
	private String _filter;

	MAGTextinputField(Context context, MAGTextinput input, String value, String filter) {
		super(context, input);
		_filter = filter;
		_editor = null;
		_value = value;
	
		boolean oneline = true;

		_editor = new EditText(getContext());

		if(input.style().getContentBackground() != null) {
			_editor.setBackgroundColor(Color.TRANSPARENT);
		}
		_editor.setText(_value);

		if (_filter.length() == 0) {
			// _editor = new QuickNotesEditField(value, style);
			oneline = false;
		} else if (_filter.equals(MAGTextinput.TEXTINPUT_FILTER_PASSWORD)) {
			// _editor.setTransformationMethod(new
			// PasswordTransformationMethod());
			_editor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		} else {
			// _editor = new FocusEditField(value, style |
			// EditField.NO_NEWLINE);

			if (_filter.equals(MAGTextinput.TEXTINPUT_FILTER_EMAIL)) {
				// _editor.setFilter(new EmailAddressTextFilter());
				_editor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			} else if (_filter.equals(MAGTextinput.TEXTINPUT_FILTER_FILENAME)) {
				// _editor.setFilter(new FilenameTextFilter());
			} else if (_filter.equals(MAGTextinput.TEXTINPUT_FILTER_HEXDECIMAL)) {
				// _editor.setFilter(new HexadecimalTextFilter());
			} else if (_filter.equals(MAGTextinput.TEXTINPUT_FILTER_IP)) {
				// _editor.setFilter(new IPTextFilter());
			} else if (_filter.equals(MAGTextinput.TEXTINPUT_FILTER_LOWERCASE)) {
				// _editor.setFilter(new LowercaseTextFilter());
			} else if (_filter.equals(MAGTextinput.TEXTINPUT_FILTER_NUMERIC)) {
				// InputFilter[] fi = {new DigitsKeyListener()};
				// _editor.setFilters(fi);
				_editor.setInputType(InputType.TYPE_CLASS_NUMBER);
			} else if (_filter.equals(MAGTextinput.TEXTINPUT_FILTER_PHONE)) {
				// _editor.setFilter(new PhoneTextFilter());
				_editor.setInputType(InputType.TYPE_CLASS_PHONE);
			} else if (_filter.equals(MAGTextinput.TEXTINPUT_FILTER_UPPERCASE)) {
				// _editor.setFilter(new UppercaseTextFilter());
			} else if (_filter.equals(MAGTextinput.TEXTINPUT_FILTER_URL)) {
				// _editor.setFilter(new URLTextFilter());
				_editor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
			}
		}
		this.setField(_editor, oneline, true);
	}

	public String getText() {
		return _editor.getText().toString();
	}

	public void setReadonly(boolean on) {
		// _editor.setEditable(!on);
	}

	public void setText(String text) {
		_editor.setText(text);
	}

	/*
	 * static class FocusPasswordEditField extends PasswordEditField {
	 * 
	 * FocusPasswordEditField(String value, long style) { super("", value, 256,
	 * style); }
	 * 
	 * protected void paint(Graphics g) { super.paint(g); if (isFocus() &&
	 * isEditable()) { drawFocusBG(this, g); } } }
	 * 
	 * static class FocusEditField extends EditField { private boolean _is_focus
	 * = false;
	 * 
	 * FocusEditField(String value, long style) { super("", value, 1024, style);
	 * }
	 * 
	 * protected void onFocus(int direction) { _is_focus = true; invalidate(); }
	 * 
	 * protected void onUnfocus() { _is_focus = false; invalidate(); }
	 * 
	 * protected void paint(Graphics g) { if (_is_focus && isEditable()) {
	 * drawFocusBG(this, g); } super.paint(g); } }
	 * 
	 * static class FocusEmailAddressEditField extends EmailAddressEditField {
	 * private boolean _is_focus = false;
	 * 
	 * FocusEmailAddressEditField(String value, long style) { super("", value,
	 * 256, style); }
	 * 
	 * protected void onFocus(int direction) { _is_focus = true; invalidate(); }
	 * 
	 * protected void onUnfocus() { _is_focus = false; invalidate(); }
	 * 
	 * protected void paint(Graphics g) { if (_is_focus && isEditable()) {
	 * drawFocusBG(this, g); } super.paint(g); } }
	 * 
	 * private static void drawFocusBG(Field f, Graphics g) { int saved_color =
	 * g.getColor(); XYRect rect = new XYRect();
	 * 
	 * // g.setColor(Color.LIGHTYELLOW); // g.fillRect(0, 0, f.getWidth(),
	 * f.getHeight()); // g.setColor(Color.TAN); // g.drawRect(0, 0,
	 * f.getWidth(), f.getHeight());
	 * 
	 * f.getFocusRect(rect); g.setColor(Color.ROYALBLUE); g.fillRect(rect.x,
	 * rect.y, rect.width, rect.height); g.setColor(saved_color); }
	 * 
	 * static class QuickNotesEditField extends AutoTextEditField { private
	 * static final int _base = 256; private boolean _is_focus = false;
	 * 
	 * QuickNotesEditField(String text, long style) { super("", text, 4096,
	 * style); }
	 * 
	 * protected void makeContextMenu(ContextMenu cm) {
	 * 
	 * String[] phrases =
	 * UtilClass.getLines(MAGTextinputQuickNoteScreen.getQuicknotes()); int i;
	 * 
	 * int defaultIdx = 0; for (i = 0; i < phrases.length; i++) { cm.addItem(new
	 * MenuItem(phrases[i], _base + i, _base + i) { public void run() {
	 * QuickNotesEditField editor = (QuickNotesEditField) getTarget();
	 * MAGTextinputQuickNoteScreen.saveLast(toString());
	 * editor.setText(editor.getText() + toString()); } }); if
	 * (phrases[i].equals(MAGTextinputQuickNoteScreen.getLast())) { defaultIdx =
	 * i; } } cm.addItem(MenuItem.separator(_base + i)); cm.addItem(new
	 * MenuItem(_resources.getString(MAG_TEXT_INPUT_QUICKNOTES), _base + i + 1,
	 * _base + i + 1) { public void run() { MAGTextinputQuickNoteScreen sc = new
	 * MAGTextinputQuickNoteScreen();
	 * UiApplication.getUiApplication().pushScreen(sc); } });
	 * cm.setDefault(defaultIdx); }
	 * 
	 * protected void onFocus(int direction) { _is_focus = true; invalidate(); }
	 * 
	 * protected void onUnfocus() { _is_focus = false; invalidate(); }
	 * 
	 * protected void paint(Graphics g) { if (_is_focus && isEditable()) {
	 * drawFocusBG(this, g); } super.paint(g); } }
	 */
}
