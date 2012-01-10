/*
 * MAGFileField.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.anheinno.android.libs.log.LOG;

/**
 * MAGLink对应一个请求一个MAGML文档的链接。与HTML中的超级链接类似，
 * MAGLink是一个可以聚焦（Focusable）的文字区域，点击后将请求新的MAGML。<br>
 * TYPE = "LINK"
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGLink extends MAGLinkableComponent {
	
	public View initField(Context context) {
		OnClickListener dl_listener = new OnClickListener() {
			public void onClick(View v) {
				LOG.info(this, "Request: " + _link + " at " + _target);
				go2Link();
			}
		};
		MAGLinkButtonField button = new MAGLinkButtonField(context, this, dl_listener);
		return button;
	}
	
	public void updateField(View f) {
		super.updateField(f);
		MAGLinkButtonField button = (MAGLinkButtonField)f;
		button.updateStyle();
	}

	@Override
	public void setChecked() {
		super.setChecked();
		((MAGLinkButtonField)getField()).setChecked(true);
	}

	@Override
	public void setUnChecked() {
		((MAGLinkButtonField)getField()).setChecked(false);
		super.setUnChecked();
	}
	
	

}
