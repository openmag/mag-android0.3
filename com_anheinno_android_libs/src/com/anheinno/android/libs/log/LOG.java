/**
 * LOG.java
 * 2011-4-21
 */
package com.anheinno.android.libs.log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

import com.anheinno.android.libs.util.ClassUtility;

import android.os.Environment;
import android.util.Log;

/**
 * LOG level 要在程序启动或者变化后指定，LOG没有content不能从配置文件读出level
 * 
 * @author 
 * 
 */
public class LOG {
	private String _log_path;
	private static int _level = 3;
	private static LOG _default_log;
	private File _log_conn;
	private RandomAccessFile _accessor;

	private static StringBuffer _log_buffer = new StringBuffer();

	public static final int LOG_LEVEL_DEBUG = 5;
	public static final int LOG_LEVEL_INFO = 4;
	public static final int LOG_LEVEL_TRACE = 3;
	public static final int LOG_LEVEL_WARNING = 2;
	public static final int LOG_LEVEL_ERROR = 1;
	public static final int LOG_LEVEL_NOLOG = 0;

	private static char[] LOG_CHAR = { 'X', 'E', 'W', 'T', 'I', 'D' };
	public static String[] LOG_STRING = { "NOLOG", "ERROR", "WARNING", "TRACE", "INFO", "DEBUG" };

	private LOG() {
		init(getLogPath());
		// getLogLevel()
	}

	private void init(String path) {
		_log_path = path;
		_accessor = null;
		_log_conn = null;
		openLog();
	}

	public static void setLogLevel(int level) {
		_level = level;
	}

	private void openLog() {
		if (_log_path != null && _log_path.length() > 0) {
			try {
				// FILEHelper.mkdir(FILEHelper.dirName(_log_path));

				File _log_dir = new File(getLogDir());
				if (!_log_dir.exists()) {
					_log_dir.mkdir();
				}

				_log_conn = new File(_log_path);
				if (!_log_conn.exists()) {
					_log_conn.createNewFile();
				}
				if (!_log_conn.isDirectory() && _log_conn.canWrite()) {
					_accessor = new RandomAccessFile(_log_conn, "rw");
					_accessor.seek(_accessor.length());
				}
			} catch (final Exception e) {
				Log.e("LOG", "openLog " + e.toString());
			}
		}
	}

	private void closeLog() {
		if (_accessor != null) {
			try {
				_accessor.close();
				_accessor = null;
				_log_conn = null;
			} catch (final Exception e) {
				Log.e("LOG", "closeLog " + e.toString());
			}
		}
	}

	private static String getLogDir() {
		String path = "/" + Environment.getExternalStorageDirectory().getName() + "/" + "MagLog";

		return path;
	}

	private static String getLogName() {
		Calendar cal = Calendar.getInstance();
		return int2str(cal.get(Calendar.YEAR), 4) + int2str(cal.get(Calendar.MONTH) + 1, 2) + int2str(cal.get(Calendar.DAY_OF_MONTH), 2)
				+ int2str(cal.get(Calendar.HOUR_OF_DAY), 2) + ".txt";
	}

	private static String int2str(int val, int w) {
		String str = String.valueOf(val);
		while (str.length() < w) {
			str = '0' + str;
		}
		return str;
	}

	private static String getLogPath() {
		return getLogDir() + "/" + getLogName();
	}

	public static int getLogLevel() {
		return _level;
	}

	public static void debug(Object o, String msg) {
		logStr(o, msg, LOG_LEVEL_DEBUG);
	}

	public static void debug(Object o, byte[] b, int off, int len) {
		logBytes(o, b, off, len, LOG_LEVEL_DEBUG);
	}

	public static void info(Object o, String msg) {
		logStr(o, msg, LOG_LEVEL_INFO);
	}

	public static void info(Object o, byte[] b, int off, int len) {
		logBytes(o, b, off, len, LOG_LEVEL_INFO);
	}

	public static void trace(Object o, String msg) {
		logStr(o, msg, LOG_LEVEL_TRACE);
	}

	public static void trace(Object o, byte[] b, int off, int len) {
		logBytes(o, b, off, len, LOG_LEVEL_TRACE);
	}

	public static void error(Object o, String msg, Exception e) {
		String errstr = "";
		if (e != null) {
			errstr = " Exception:" + e.toString() + " " + e.getMessage();
		}
		logStr(o, msg + errstr, LOG_LEVEL_ERROR);
	}

	public static void warning(Object o, String msg) {
		logStr(o, msg, LOG_LEVEL_WARNING);
	}

	private static synchronized void logBytes(Object o, byte[] b, int off, int len, int level) {
		StringBuffer tmp = new StringBuffer();
		for (int i = off; i < off + len; i++) {
			String str = Integer.toHexString(b[i] & 0xff);
			if (str.length() < 2) {
				tmp.append('0');
			}
			tmp.append(str);
		}
		logStr(o, tmp.toString(), level);
	}

	private static synchronized void logStr(Object o, String str, int level) {
		System.out.println("LOG:" + getNameString(o) + " " + str + " " + level);
		if (_default_log != null && !_default_log.isCurrent()) {
			_default_log.closeLog();
			_default_log = null;
		}
		if (_default_log == null) {
			_default_log = new LOG();
		}

		if (level <= LOG._level && _default_log != null && (_default_log._accessor != null)) {
			_log_buffer.delete(0, _log_buffer.length());
			_log_buffer.append(getTimeStr());
			_log_buffer.append(LOG_CHAR[level]);
			_log_buffer.append('[');
			_log_buffer.append(getNameString(o));
			_log_buffer.append(']');
			_log_buffer.append(str);
			if (_default_log._accessor != null) {
				try {
					_default_log._accessor.write((_log_buffer.toString() + "\n").getBytes());
				} catch (IOException e) {
					Log.e("shenrh", "logStr " + e.toString());
					e.printStackTrace();
				}
			}
			switch(level) {
			case LOG_LEVEL_ERROR:
				Log.e(getNameString(o), str); break;
			case LOG_LEVEL_WARNING:
				Log.w(getNameString(o), str); break;
			case LOG_LEVEL_INFO:
				Log.i(getNameString(o), str); break;
			case LOG_LEVEL_TRACE:
				Log.v(getNameString(o), str); break;
			case LOG_LEVEL_DEBUG:
				Log.d(getNameString(o), str); break;
			default:
				Log.i(getNameString(o), str);
			}
		}
	}

	static String getNameString(Object o) {
		if (o != null) {
			if (o instanceof String) {
				return (String) o;
			} else {
				return ClassUtility.getClassName(o);
			}
		}
		return "";
	}

	private static String getTimeStr() {
		Calendar cal = Calendar.getInstance();
		return int2str(cal.get(Calendar.MINUTE), 2) + int2str(cal.get(Calendar.SECOND), 2) + "." + int2str(cal.get(Calendar.MILLISECOND), 3);
	}

	private boolean isCurrent() {
		if (_log_path.equals(getLogPath())) {
			return true;
		} else {
			return false;
		}
	}
}
