/**
 * CFLDContactScreen.java
 *
 * Copyright 2007-2010 anhe.
 */
package com.anheinno.libs.mag.controls.contacts;

import com.anheinno.android.libs.UtilClass;
import com.anheinno.android.libs.mag.MAGLinkedCustomControl;

/**
 * 2010-4-28
 * 
 * @author 沈瑞恒
 * 
 * @version 1.0
 * 
 */
public class ContactLinkedControl extends MAGLinkedCustomControl {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anheinno.libs.mag.MAGLinkedCustomControl#show()
	 */
	public void show() {
		ContactScreen contactScreen = new ContactScreen(getContext(), UtilClass.JSONArray2StringArray(_params));
		contactScreen.show("", "", null);
	}

}
