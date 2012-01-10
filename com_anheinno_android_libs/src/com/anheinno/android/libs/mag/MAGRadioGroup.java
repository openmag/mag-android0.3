package com.anheinno.android.libs.mag;

import java.util.Vector;

public class MAGRadioGroup {
	private String _name;
	private Vector<MAGRadio> _radios;
	
	public MAGRadioGroup(String name) {
		_name = name;
		_radios = new Vector<MAGRadio>();
	}
	
	public void addMAGRadio(MAGRadio radio) {
		if(!_radios.contains(radio)) {
			_radios.addElement(radio);
		}
	}
	
	public void check(MAGRadio radio) {
		for(int i = 0; i < _radios.size(); i ++) {
			if(_radios.elementAt(i) != radio) {
				_radios.elementAt(i).setChecked(false);
			}else {
				_radios.elementAt(i).setChecked(true);
			}
		}
	}
}
