/**
 * ParamsPreferencesStore.java
 * 2011-9-5
 */
package com.anheinno.libs.mag.controls.thirdapp;

import android.content.Context;

import com.anheinno.android.libs.PreferencesStore;

/**
 * @author shenrh
 * 
 */
public class ParamsPreferencesStore {
	private static final String KEY_lINK = "THIRDAPPLINK";

	public static void putlink(Context con, String link) {
		PreferencesStore.put(con, KEY_lINK, link);
	}

	public static String getlink(Context con) {
		return PreferencesStore.getString(con, KEY_lINK);
	}
	
	
	private static final String KEY_PACKAGENAME = "PACKAGENAME";
	
	public static void putPackagename(Context con, String name) {
		PreferencesStore.put(con, KEY_PACKAGENAME, name);
	}
	
	public static String getPackagename(Context con) {
		return PreferencesStore.getString(con, KEY_PACKAGENAME);
	}
}
