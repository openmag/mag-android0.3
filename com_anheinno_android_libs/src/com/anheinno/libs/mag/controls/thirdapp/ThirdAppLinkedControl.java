/**
 * ThirdAppLinkedControl.java
 * 2011-9-2
 */
package com.anheinno.libs.mag.controls.thirdapp;

import org.json.JSONException;

import com.anheinno.android.libs.mag.MAGLinkedCustomControl;
//import com.anheinno.pam.libs.application.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Toast;

/**
 * @author shenrh
 * @description _params index 0:欲调用第三方应用的packagename 1:调用结束后返回数据的接受URL
 */
public class ThirdAppLinkedControl extends MAGLinkedCustomControl {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anheinno.android.libs.mag.MAGLinkedCustomControl#show()
	 */
	@Override
	public void show() {
		// TODO Auto-generated method stub
		try {
			ParamsPreferencesStore.putlink(getContext(), getdealLink());
			if (_params != null && _params.length() > 0) {
				final String strPackage = _params.getString(0);// "com.intsig.BizCardReader"
				ParamsPreferencesStore.putPackagename(getContext(), strPackage);
				System.out.println("ThirdAppLinkedControl packagename is:" + strPackage);
				PackageManager pm = null;
				PackageInfo pi = null;
				ActivityInfo ai = null;
				Intent intent = null;
				try {
					pm = getContext().getPackageManager();
					pi = pm.getPackageInfo(strPackage, PackageManager.GET_ACTIVITIES);
					ai = pi.activities[0];
					intent = new Intent();
					intent.setComponent(new ComponentName(strPackage, ai.name));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getContext().startActivity(intent);
				} catch (NameNotFoundException e) {
					Toast.makeText(getContext(), "can't find app: " + strPackage, Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
		}
	}

	public String getdealLink() {
		if (_params != null && _params.length() > 1) {
			try {
				return _params.getString(1);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
}
