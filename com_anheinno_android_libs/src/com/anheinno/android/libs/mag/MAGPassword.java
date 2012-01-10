/*
 * MAGPassword.java
 *
 * <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import android.content.Context;
import android.view.View;

/**
 * MAGPassword是支持用户输入密码文字的组件。呈现形式为提示文字后跟密码文本输入区域。<br>
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGPassword extends MAGTextinput {
	
	public View initField(Context context) {
		MAGTextinputField tf = new MAGTextinputField(context, this, getInitValue(), MAGTextinput.TEXTINPUT_FILTER_PASSWORD);
		return tf;
	}
}
