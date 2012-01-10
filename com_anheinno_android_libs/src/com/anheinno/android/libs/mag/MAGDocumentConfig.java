/**
 * MAGDocumentConfig.java
 * 2011-4-25
 */
package com.anheinno.android.libs.mag;

import java.io.File;

import com.anheinno.android.libs.HTTPRequestString;
import com.anheinno.android.libs.JSONBrowserConfig;
import com.anheinno.android.libs.PreferencesStore;
import com.anheinno.android.libs.file.FileUtilityClass;
import com.anheinno.android.libs.log.LOG;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
/**
 * @author shenrh
 * 
 */
public class MAGDocumentConfig extends JSONBrowserConfig{
	
	public static String AUTO_CONFIG_ID = "__auto_config__";
	
	/**
	 * 服务登录URI 范围：模块
	 */
	private static String MAG_LOGINURL = "MAG_LOGINURL";

	public static String getLoginURL(Context con) {
		return PreferencesStore.getString(con, MAG_LOGINURL);
	}

	public static void setLoginURL(Context con, String url) {
		PreferencesStore.put(con, MAG_LOGINURL, url);
	}
	
	public static String getDefaultLoginURL(Context con, String url) {
		return PreferencesStore.getString(con, MAG_LOGINURL, url);
	}

	/**
	 * 首页URL 范围：应用
	 */
	private static String MAG_MAIN_URL = "MAIN_URL";

	public static String getMainEntryURL(Context con) {
		return PreferencesStore.getString(con, MAG_MAIN_URL);
	}

	public static void setMainEntryURL(Context con, String url) {
		String new_url = HTTPRequestString.purify(url);
		PreferencesStore.put(con, MAG_MAIN_URL, new_url);
	}

	/**
	 * 是否退出 范围：模块
	 */
	private static String MAG_QUIT = "MAG_QUIT";

	public static Boolean getisQuit(Context con) {
		return PreferencesStore.getBoolean(con, MAG_QUIT);
	}

	public static void setisQuit(Context con, Boolean quit) {
		PreferencesStore.put(con, MAG_QUIT, quit);
	}

	/**
	 * 账户 范围：模块
	 */
	private static String MAG_USERNAME = "MAG_USERNAME";

	public static String getUsername(Context con) {
		return PreferencesStore.getString(con, MAG_USERNAME);
	}

	public static void setUsername(Context con, String uname) {
		PreferencesStore.put(con, MAG_USERNAME, uname);
	}

	/**
	 * 账户是否已经经过验证 范围：模块
	 */
	private static String MAG_VERIFIED = "MAG_VERIFIED";

	public static boolean isVerified(Context con) {
		return PreferencesStore.getBoolean(con, MAG_VERIFIED);
	}

	public static void setVerified(Context con, boolean verified) {
		PreferencesStore.put(con, MAG_VERIFIED, verified);
	}

	/**
	 * 账户是否和手机绑定 
	 */
	private static String MAG_BIND = "MAG_BIND";

	public static boolean isBind(Context con) {
		return PreferencesStore.getBoolean(con, MAG_BIND);
	}

	public static void setBind(Context con, boolean bind) {
		PreferencesStore.put(con, MAG_BIND, bind);
	}

	/**
	 * 账户密码 
	 */
	private static String MAG_PASSWORD = "MAG_PASSWORD";

	public static String getPassword(Context con) {
		return PreferencesStore.getString(con, MAG_PASSWORD);
	}

	public static void setPassword(Context con, String password) {
		PreferencesStore.put(con, MAG_PASSWORD, password);
	}
	
	/**
	 * 是否总是密码保护
	 * 
	 */
	private static String MAG_PROTECT = "MAG_PROTECT";

	public static boolean isPasswordProtect(Context con) {
		return PreferencesStore.getBoolean(con, MAG_PROTECT);
	}

	public static void setPasswordProtect(Context con, boolean protect) {
		PreferencesStore.put(con, MAG_PROTECT, protect);
	}

	
	/**
	 * 是否开启“显示控件边框”效果
	 * 
	 * 范围：应用
	 */

	private static String MAG_DEBUG_SHOW_COMPONENT_BORDER = "MAG_DEBUG_SHOW_COMPONENT_BORDER";

	public static void enableShowComponentBorder(Context con) {
		PreferencesStore.put(con, MAG_DEBUG_SHOW_COMPONENT_BORDER, true);
	}

	public static void disableShowComponentBorder(Context con) {
		PreferencesStore.put(con, MAG_DEBUG_SHOW_COMPONENT_BORDER, false);
	}

	public static boolean isShowComponentBorder(Context con) {
		return PreferencesStore.getBoolean(con, MAG_DEBUG_SHOW_COMPONENT_BORDER);
	}

	/**
	 * 附件处理服务器URI 
	 * 范围：模块
	 */
	private static String MAG_IMGSERVER_URL = "MAG_IMGSERVER_URL";

	public static String getImageServerURL(Context con) {
		String url = PreferencesStore.getString(con, MAG_IMGSERVER_URL);
		if(url == null) {
			return "";
		}else {
			return url;
		}
	}

	public static void setImageServerURL(Context con, String url) {
		PreferencesStore.put(con, MAG_IMGSERVER_URL, HTTPRequestString.purify(url));
	}
	
	/**
	 * 下载缓存目录 
	 * 范围：全局
	 */
	private static String MAG_DOWNLOAD_DIR = "MAG_DOWNLOAD_DIR";

	public static String getDownloadDir(Context context) {
		String dir = PreferencesStore.getString(context, MAG_DOWNLOAD_DIR);
		if(dir == null || dir.length() == 0) {
			File sd_dir = Environment.getExternalStorageDirectory();
			if(sd_dir != null) {
				if(sd_dir.canRead()) {
					String state = Environment.getExternalStorageState();
					if(state.equals(Environment.MEDIA_MOUNTED)) {
						dir = sd_dir.getAbsolutePath();
						dir += "/" + context.getPackageName() + "/attachment/";
						if(FileUtilityClass.mkdir(dir)) {
							return dir;
						}
					}
				}else {
					LOG.error("MAGDocumentConfig","getDownloadDir fails: ExternalStorageDirectory is not accessible", null);
				}
			}else {
				LOG.error("MAGDocumentConfig","getDownloadDir fails: no ExternalStorageDirectory", null);
			}
		}else {
			if(FileUtilityClass.mkdir(dir)) {
				return dir;
			}
		}
		return null;
	}

	public static void setDownloadDir(Context context, String dir) {
		PreferencesStore.put(context, MAG_DOWNLOAD_DIR, dir);
	}

	/**
	 * 推送协议和服务器配置 
	 * 范围：全局
	 */
	private static String CACHE_CONFIG_PUSH_PROTOCOL = "CACHE_CONFIG_PUSH_PROTOCOL";
	private static String CACHE_CONFIG_PUSH_SERVER = "CACHE_CONFIG_PUSH_SERVER";

	private static String CACHE_CONFIG_PROTOCOL_PAG = "PAG";

	public static String getPushProtocol(Context con) {
		String protocol = PreferencesStore.getString(con, CACHE_CONFIG_PUSH_PROTOCOL);
		if(protocol == null || protocol.length() == 0) {
			return CACHE_CONFIG_PROTOCOL_PAG;
		}
		return protocol;
	}

	public static void setPushProtocol(Context con, String protocol) {
		PreferencesStore.put(con, CACHE_CONFIG_PUSH_PROTOCOL, protocol);
	}

	public static String getPushServer(Context con) {
		String addr = PreferencesStore.getString(con, CACHE_CONFIG_PUSH_SERVER);
		if(addr == null) {
			return "";
		}
		return addr;
	}

	public static void setPushServer(Context con, String addr) {
		PreferencesStore.put(con, CACHE_CONFIG_PUSH_SERVER, addr);
	}
}
