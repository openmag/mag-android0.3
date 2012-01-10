/**
 * LogConfig.java
 * 2011-4-25
 */
package com.anheinno.android.libs.log;

import android.content.Context;
import android.os.Environment;

import com.anheinno.android.libs.PreferencesStore;

/**
 * @author shenrh
 * 
 */
public class LogConfig {
	
	private static String LOG_LEVEL = "LOG_LEVEL";

	public static int getLogLevel(Context con) {
		return PreferencesStore.getInt(con, LOG_LEVEL, -1);
	}

	public static void setLogLevel(Context con, int level) {
		PreferencesStore.put(con, LOG_LEVEL, level);
	}

	private static String LOG_DIR_NAME = "LOG_DIR_NAME";

	public static String getLogDir(Context con) {
		String def = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + con.getPackageName();
		return PreferencesStore.getString(con, LOG_DIR_NAME, def);
	}

	public static void setLogDir(Context con, String dir) {
		PreferencesStore.put(con, LOG_DIR_NAME, dir);
	}
	
}
