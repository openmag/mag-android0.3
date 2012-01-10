/*
 * MAGHiddenInput.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import android.content.Context;
import android.view.View;



/**
 * MAGHidden用于在客户端存储信息，类比于HTML的Hidden输入元素。<br>
 * SUBTYPE = "HIDDEN"
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
class MAGHiddenInput extends MAGInputBase {

	public View initField(Context context) {
		return null;
	}

	public void updateField(View f) {
	}

	public boolean validate() {
		return true;
	}

	public String fetchValue() {
		return getInitValue();
	}
}
