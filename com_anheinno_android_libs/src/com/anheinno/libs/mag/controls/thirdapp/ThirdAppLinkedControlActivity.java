/**
 * ThirdAppLinkedControlActivity.java
 * 2011-9-5
 */
package com.anheinno.libs.mag.controls.thirdapp;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anheinno.android.libs.JSONBrowserLink;
import com.anheinno.android.libs.R;
import com.anheinno.android.libs.mag.MAGDocumentScreen;
import com.anheinno.android.libs.mag.MAGTextinputField;
import com.anheinno.android.libs.ui.UiApplication;

public class ThirdAppLinkedControlActivity extends UiApplication {

	private final static int APILEV_8 = 8;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.anheinno.android.libs.ui.UiApplication#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String data = getIntent().getStringExtra(Intent.EXTRA_TEXT).trim();
		System.out.println("ThirdAppLinkedControlActivity get data: " + data);

		String link = ParamsPreferencesStore.getlink(this);
		if (link != null && link.length() > 0) {
			JSONBrowserLink jlink = new JSONBrowserLink(this);
			jlink.setURL(link);
			jlink.setExpireMilliseconds(0);
			jlink.setSaveHistory(false);
			jlink.setNotify(false);
			MAGDocumentScreen ds = new MAGDocumentScreen(this, jlink, false, null);
			ds.show();

			int count = ds.getChildCount();
			System.out.println("now has View count:" + count);
			for (int i = 0; i < count; i++) {
				View v = ds.getChildAt(i);
				if (v instanceof MAGTextinputField) {
					((MAGTextinputField) v).setText(data);
					break;
				} else {
					System.out.println("View " + v);
				}
			}
		} else {
			setContentView(R.layout.third_app_activity_main);
			
			EditText et = (EditText) findViewById(R.id.editText_data);
			et.setText(data);

			Button button = (Button) findViewById(R.id.button_submit);
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// submit data...
					if (submit()) {
						Toast.makeText(ThirdAppLinkedControlActivity.this, "submit... ok!", Toast.LENGTH_SHORT).show();
						close();
					} else {
						Toast.makeText(ThirdAppLinkedControlActivity.this, "submit... error!", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}

	public boolean submit() {
		boolean succ = false;

		// submit .....
		succ = true;
		
		return succ;
	}

	public void close() {
		final int apilev = Integer.parseInt(Build.VERSION.SDK);
		final String packagename = ParamsPreferencesStore.getPackagename(this);
		if (packagename != null && packagename.length() > 0) {
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			if (apilev < APILEV_8) {
				am.restartPackage(packagename);
			} else {
				// 没有效果
				/*
				 * try { // Method method = //
				 * Class.forName("android.app.ActivityManager"
				 * ).getMethod("forceStopPackage", // String.class); // Method
				 * method = //
				 * Class.forName("android.app.ActivityManager").getMethod
				 * ("killBackgroundProcesses", // String.class); //
				 * method.invoke(am, packagename);
				 * 
				 * int pid = 0; List<RunningAppProcessInfo> list =
				 * am.getRunningAppProcesses(); for
				 * (ActivityManager.RunningAppProcessInfo info : list) {
				 * String[] pkglist = info.pkgList; for (String pkgname :
				 * pkglist) { System.out.println(">>>>>>>>pkgname " + pkgname);
				 * if (pkgname.endsWith(packagename)) { pid = info.pid; break; }
				 * } if (pid != 0) { break; } } System.out.println(packagename +
				 * " pid " + pid); if (pid != 0) { Process.sendSignal(pid,
				 * Process.SIGNAL_QUIT); Process.killProcess(pid);
				 * System.out.println("kill end!"); }
				 * 
				 * } catch (Exception e) { System.out.println("Close app " +
				 * packagename + " error:" + e.toString()); }
				 */
			}
		}
		finish();
	}
}
