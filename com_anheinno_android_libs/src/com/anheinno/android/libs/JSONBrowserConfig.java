package com.anheinno.android.libs;

import android.content.Context;

public class JSONBrowserConfig {
	
	/**
	 * 缓存目录 范围：全局
	 */
	// private static String CACHE_CONFIG_DIR = "CACHE_CONFIG_DIR";
	// private static String CACHE_DIR = "magcache";
	//
	// public static String getPathConfig(String path) {
	// if (!FileUtilityClass.isDir(path)) {
	// if (FileUtilityClass.fileExists(path)) {
	// path = "/store/";
	// } else if (!FileUtilityClass.mkdir(path)) {
	// path = "/store/";
	// }
	// }
	// return path;
	// }
	//
	// private static File getCacheFileDesc(Context con) {
	// return con.getDir(CACHE_DIR, Context.MODE_PRIVATE);
	// }
	// public static String getCacheDir(Context con) {
	// return getCacheFileDesc(con).getAbsolutePath() + "/";
	// return PreferencesStore.getString(con, CACHE_CONFIG_DIR,
	// Environment.getExternalStorageDirectory().getName() + "/anhe/cache/");
	// }
	// public static String getCacheCanonicalPath(Context con) {
	// try {
	// return getCacheFileDesc(con).getCanonicalPath();
	// }catch(final Exception e) {
	// return "";
	// }
	// }

	/*
	 * public static void setCacheDir(Context con, String dir) {
	 * PreferencesStore.put(con, CACHE_CONFIG_DIR, dir); }
	 */

	/**
	 * 是否启用缓存 范围：模块
	 */
	private static String CACHE_CONFIG_ENABLE = "CACHE_CONFIG_ENABLE";

	public static boolean isCacheEnabled(Context con) {
		return PreferencesStore.getBoolean(con, CACHE_CONFIG_ENABLE, true);
	}

	public static void setCacheEnabled(Context con, boolean enable) {
		PreferencesStore.put(con, CACHE_CONFIG_ENABLE, enable);
	}

	/**
	 * 账户是否锁定 范围：模块
	 */
	private static String MAG_LOCK_ACCOUNT = "MAG_LOCK_ACCOUNT";

	public static boolean isLockAccount(Context con) {
		return PreferencesStore.getBoolean(con, MAG_LOCK_ACCOUNT);
	}

	public static void setLockAccount(Context con, boolean lock) {
		PreferencesStore.put(con, MAG_LOCK_ACCOUNT, lock);
	}

	/**
	 * 缺省缓存时间 范围：模块
	 */
	private static String CACHE_CONFIG_EXPIRE = "CACHE_CONFIG_EXPIRE";

	public static int getCacheExpireHours(Context con) {
		return PreferencesStore.getInt(con, CACHE_CONFIG_EXPIRE, 72);
	}

	public static long getCacheExpire(Context con) {
		return getCacheExpireHours(con) * 3600 * 1000L;
	}

	public static void setCacheExpireHours(Context con, int hour) {
		PreferencesStore.put(con, CACHE_CONFIG_EXPIRE, hour);
	}

	/**
	 * 缓存URL 范围 全局
	 */
	/*
	 * private static String CACHE_CONFIG_URL = "CACHE_CONFIG_URL";
	 * 
	 * public static JSONArray getCacheURL(Context con) { String data =
	 * PreferencesStore.getString(con, CACHE_CONFIG_URL, ""); if (data.length()
	 * > 0) { try { return new JSONArray(data); } catch (JSONException e) {
	 * e.printStackTrace(); } } return null; }
	 * 
	 * public static void setCacheURL(Context con, String value) {
	 * PreferencesStore.put(con, CACHE_CONFIG_URL, value); }
	 * 
	 * public static void addCacheURL(Context con, String value) { JSONArray
	 * data = getCacheURL(con); if (data == null) { data = new JSONArray();
	 * data.put(value); } else { data.put(value); } PreferencesStore.put(con,
	 * CACHE_CONFIG_URL, data.toString()); }
	 * 
	 * public static void removeCacheURL(Context con, String value) { JSONArray
	 * data = getCacheURL(con); JSONArray newdata = new JSONArray(); try { if
	 * (data == null) { return; } else { int size = data.length(); for (int i =
	 * 0; i < size; i++) { if(!data.getString(i).equals(value)){
	 * newdata.put(data.getString(i)); } } } } catch (JSONException e) {
	 * e.printStackTrace(); } PreferencesStore.put(con, CACHE_CONFIG_URL,
	 * newdata.toString()); }
	 */

	// /**
	// * 使用的链路属性 范围：模块
	// */
	// private static String CACHE_CONFIG_LINKTYPE = "CACHE_CONFIG_LINKTYPE";
	//
	// public static String getLinkType() {
	//		
	// return AppConfig.getModuleConfigWithDefault(CACHE_CONFIG_LINKTYPE,
	// HTTPHelper.LINK_BES);
	// }
	//
	// public static void setLinkType(String val) {
	// AppConfig.setModuleConfig(CACHE_CONFIG_LINKTYPE, val);
	// }
	/**
	 * 是否使用中继Relay 属性范围：模块
	 */

	private static String CACHE_CONFIG_USE_RELAY = "CACHE_CONFIG_USE_RELAY";

	public static boolean useRelay(Context con) {
		return PreferencesStore.getBoolean(con, CACHE_CONFIG_USE_RELAY);
	}
	
	public static boolean useRelay(Context con, boolean defvalue) {
		return PreferencesStore.getBoolean(con, CACHE_CONFIG_USE_RELAY, defvalue);
	}

	public static void setUseRelay(Context con, boolean use_relay) {
		PreferencesStore.put(con, CACHE_CONFIG_USE_RELAY, use_relay);
	}

	/**
	 * 中继服务器地址 属性范围：模块
	 */

	private static String CACHE_CONFIG_RELAY_SERVER = "CACHE_CONFIG_RELAY_SERVER";

	public static String getRelayServer(Context con) {
		return PreferencesStore.getString(con, CACHE_CONFIG_RELAY_SERVER);
	}
	
	public static String getDefaultRelayServer(Context con, String defvalue) {
		return PreferencesStore.getString(con, CACHE_CONFIG_RELAY_SERVER, defvalue);
	}

	public static void setRelayServer(Context con, String server) {
		PreferencesStore.put(con, CACHE_CONFIG_RELAY_SERVER, server);
	}

}