/**
 * PerferencesStore.java
 * 2011-4-23
 */
package com.anheinno.android.libs;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author shenrh
 * 
 */
public class PreferencesStore {
	private static final String OPTIONSSETTING = "config";

	public static boolean isSet(Context con, String key) {
		SharedPreferences options = con.getSharedPreferences(OPTIONSSETTING, Activity.MODE_PRIVATE);
		Map<String, ?> map = options.getAll();
		if(map.containsKey(key)) {
			return true;
		}else {
			return false;
		}
	}

	//////////////////////////////////////////
	// string
	public static void put(Context con, String key, String value) {
		SharedPreferences options = con.getSharedPreferences(OPTIONSSETTING, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = options.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * @param con
	 * @param key
	 * @return 默认值""
	 */
	public static String getString(Context con, String key) {
		SharedPreferences options = con.getSharedPreferences(OPTIONSSETTING, Activity.MODE_PRIVATE);
		return options.getString(key, "");
	}

	public static String getString(Context con, String key, String defvalue) {
		if(!isSet(con, key)) {
			put(con, key, defvalue);
		}
		return getString(con, key);
	}

	///////////////////////////////////////////////
	// int
	public static void put(Context con, String key, int value) {
		SharedPreferences options = con.getSharedPreferences(OPTIONSSETTING, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = options.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * @param con
	 * @param key
	 * @return 默认值 0
	 */
	public static int getInt(Context con, String key) {
		SharedPreferences options = con.getSharedPreferences(OPTIONSSETTING, Activity.MODE_PRIVATE);
		return options.getInt(key, 0);
	}

	public static int getInt(Context con, String key, int defvalue) {
		if(!isSet(con, key)) {
			put(con, key, defvalue);
		}
		return getInt(con, key);
	}

	///////////////////////////////////////////////
	// boolean
	public static void put(Context con, String key, boolean value) {
		SharedPreferences options = con.getSharedPreferences(OPTIONSSETTING, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = options.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * @param con
	 * @param key
	 * @return 默认值false
	 */
	public static boolean getBoolean(Context con, String key) {
		if(isSet(con, key)) {
			SharedPreferences options = con.getSharedPreferences(OPTIONSSETTING, Activity.MODE_PRIVATE);
			return options.getBoolean(key, false);
		}else {
			return false;
		}
	}
	

	public static boolean getBoolean(Context con, String key, boolean defvalue) {
		if(!isSet(con, key)) {
			put(con, key, defvalue);
		}
		return getBoolean(con, key);
	}

	///////////////////////////////////////////
	// float
	public static void put(Context con, String key, float value) {
		SharedPreferences options = con.getSharedPreferences(OPTIONSSETTING, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = options.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	/**
	 * @param con
	 * @param key
	 * @return 默认值 0
	 */
	public static float getFloat(Context con, String key) {
		SharedPreferences options = con.getSharedPreferences(OPTIONSSETTING, Activity.MODE_PRIVATE);
		return options.getFloat(key, 0);
	}

	public static float getFloat(Context con, String key, float defvalue) {
		if(!isSet(con, key)) {
			put(con, key, defvalue);
		}
		return getFloat(con, key);
	}

	//////////////////////////////////////////////
	// long
	public static void put(Context con, String key, long value) {
		SharedPreferences options = con.getSharedPreferences(OPTIONSSETTING, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = options.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	/**
	 * @param con
	 * @param key
	 * @return 默认值0
	 */
	public static long getLong(Context con, String key) {
		SharedPreferences options = con.getSharedPreferences(OPTIONSSETTING, Activity.MODE_PRIVATE);
		return options.getLong(key, 0);
	}

	public static long getLong(Context con, String key, long defvalue) {
		if(!isSet(con, key)) {
			put(con, key, defvalue);
		}
		return getLong(con, key);
	}
}
