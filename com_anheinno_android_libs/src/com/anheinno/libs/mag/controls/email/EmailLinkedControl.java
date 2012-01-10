/**
 * EmailLinkedControl.java
 *
 * Copyright 2007-2011 anhe.
 */
package com.anheinno.libs.mag.controls.email;

import android.content.Intent;
import com.anheinno.android.libs.mag.MAGLinkedCustomControl;

/**
 * 2011-3-15
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 * 
 */
public class EmailLinkedControl extends MAGLinkedCustomControl {
	public void show() {
		Intent intent = new Intent();
		try {
			intent.setClassName("oms.mail", "oms.mail.Mail");
			getContext().startActivity(intent);
		}catch(final android.content.ActivityNotFoundException e) {
			intent = new Intent();
			intent.setClassName("com.android.email","com.android.email.activity.Welcome");
			getContext().startActivity(intent);
		}
	}
}
