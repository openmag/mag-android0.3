package com.anheinno.android.libs.mag;

public interface MAGInputInterface extends MAGComponentInterface {
	
	boolean isReadOnly();
	
	boolean isRequired();
	
	String getQueryString();

	boolean validate();

	void setValue(String value);
	
	String getInitValue();
		
	String fetchValue();
	
}
