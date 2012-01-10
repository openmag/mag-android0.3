package com.anheinno.android.libs;

import org.json.JSONObject;

import com.anheinno.android.libs.log.LOG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JSONObjectCacheDatabase {

	public static final String URL_LINK_UNKNOWN = "URL_UNKNOWN";
	public static final String URL_LINK_INVALID = "URL_INVALID";
	public static final String URL_LINK_VISITED = "URL_VISITED";
	public static final String URL_LINK_UPDATED = "URL_UPDATED";

	public static void saveObject(Context context, String url, JSONObject o, long expire, boolean notify) {
		JSONCacheObjectInfo jco = findCacheObject(context, url);
		if (jco != null) {
			jco._object = o;
			jco._expire = expire;
			jco._notify = notify;
			updateCacheObject(context, jco, true);
		} else {
			jco = new JSONCacheObjectInfo(url, o, expire, notify);
			insertCacheObject(context, jco);
		}
		System.out.println("JSONObjectCacheFactory.saveObject: " + url + " " + notify);
	}

	public static String linkState(Context context, String url) {
		url = HTTPRequestString.purify(url);
		if (url != null) {
			JSONCacheObjectInfo jco = findCacheObject(context, url);
			if (jco != null) {
				// System.out.println("linkState: " + jco);
				String ret = jco.getState();
				if (ret.equals(URL_LINK_INVALID)) {
					deleteCacheObject(context, url);
				}
				return ret;
			} else {
				// System.out.println("linkState: Cannot find url " + url);
				return URL_LINK_INVALID;
			}
		} else {
			return URL_LINK_UNKNOWN;
		}
	}

	public static boolean needNotify(Context context, String url) {
		JSONCacheObjectInfo jco = findCacheObject(context, url);
		if (jco != null) {
			return jco.needNotify();
		}
		return false;
	}

	public static void recursiveUpdate(Context context, String url) {
		JSONCacheObjectInfo jco = findCacheObject(context, url);
		if (jco != null) {
			jco.update(-1);
			updateCacheObject(context, jco, false);
		} else {
			System.out.println("JSONObjectCacheDatabase recursiveUpdate url " + url + " jco is null");
		}
	}

	public static void updateObject(Context context, String url, JSONObject o, long expire, boolean notify) {
		JSONCacheObjectInfo jco = findCacheObject(context, url);
		if (jco != null) {
			jco._object = o;
			jco.update(expire);
			jco._notify = notify;
			updateCacheObject(context, jco, true);
		} else {
			jco = new JSONCacheObjectInfo(url, o, expire, notify);
			insertCacheObject(context, jco);
		}
	}

	public static boolean needPrefetchObject(Context context, String url) {
		JSONCacheObjectInfo jco = findCacheObject(context, url);
		if (!jco.needPrefetch()) {
			return false;
		}
		return true;
	}

	public static void invalidateObject(Context context, String url) {
		JSONCacheObjectInfo jco = findCacheObject(context, url);
		jco.invalidate();
	}

	public static JSONObject getObject(Context context, String url, boolean prefetch) {
		JSONCacheObjectInfo jco = findCacheObject(context, url);
		if (jco != null) {
			if (!jco.isExpired()) {
				if (prefetch && jco.needPrefetch()) {
					// System.out.println("JSONObjectCahceDatabase: getObject: find object, need prefetch");
					return null;
				} else {
					// System.out.println("JSONObjectCahceDatabase: getObject: find object"
					// + jco.toString());
					jco.hit();
					updateCacheObject(context, jco, false);
					return jco._object;
				}
			} else {
				// System.out.println("JSONObjectCahceDatabase: getObject: find object, expires");
				deleteCacheObject(context, url);
				return null;
			}
		} else {
			// System.out.println("JSONObjectCahceDatabase: getObject: cannot find object");
			return null;
		}
	}

	public static void purgeObject(Context context, String url) {
		deleteCacheObject(context, url);
	}

	public static void purgeAllObject(Context context) {
		deleteCacheObject(context, null);
	}

	public static int getCacheSize(Context context) {
		JSONObjectCacheOpenHelper store = new JSONObjectCacheOpenHelper(context);
		SQLiteDatabase db = store.getReadableDatabase();
		int result = 0;
		Cursor cursor = db.query(JSONObjectCacheOpenHelper.TABLE_NAME, new String[] { "SUM(LENGTH(jsonobject))" }, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				result = cursor.getInt(0);
			}
			cursor.close();
		}
		db.close();
		return result;
	}

	public static int getCacheCount(Context context) {
		JSONObjectCacheOpenHelper store = new JSONObjectCacheOpenHelper(context);
		SQLiteDatabase db = store.getReadableDatabase();
		int result = 0;
		Cursor cursor = db.query(JSONObjectCacheOpenHelper.TABLE_NAME, new String[] { "COUNT(*)" }, null, null, null, null, null);
		if (cursor != null) {
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				result = cursor.getInt(0);
			}
			cursor.close();
		}
		db.close();
		return result;
	}

	private static JSONCacheObjectInfo findCacheObject(Context context, String url) {
		url = HTTPRequestString.purify(url);
		JSONCacheObjectInfo result = null;
		if (url != null) {
			JSONObjectCacheOpenHelper store = new JSONObjectCacheOpenHelper(context);
			SQLiteDatabase db = store.getReadableDatabase();
			Cursor cursor = db.query(JSONObjectCacheOpenHelper.TABLE_NAME, new String[] { "url", "expire", "when_expire", "when_visited", "hitcount", "notify", "isnew",
					"jsonobject" }, "url=?", new String[] { url }, null, null, null, "1");
			if (cursor != null) {
				if (cursor.getCount() > 0 && cursor.moveToFirst()) {
					result = new JSONCacheObjectInfo(cursor);
				}
				cursor.close();
			}
			db.close();
		}
		return result;
	}

	private static void deleteCacheObject(Context context, String url) {
		if (url != null) {
			url = HTTPRequestString.purify(url);
		}
		JSONObjectCacheOpenHelper store = new JSONObjectCacheOpenHelper(context);
		SQLiteDatabase db = store.getWritableDatabase();
		if (url != null) {
			db.delete(JSONObjectCacheOpenHelper.TABLE_NAME, "url=?", new String[] { url });
		} else {
			db.delete(JSONObjectCacheOpenHelper.TABLE_NAME, null, null);
		}
		db.close();
	}

	private static void updateCacheObject(Context context, JSONCacheObjectInfo info, boolean update_content) {
		JSONObjectCacheOpenHelper store = new JSONObjectCacheOpenHelper(context);
		SQLiteDatabase db = store.getWritableDatabase();
		try {
			db.update(JSONObjectCacheOpenHelper.TABLE_NAME, info.getUpdateContentValues(update_content), "url=?", new String[] { info._url });
		} catch (final Exception e2) {
			LOG.error("JSONObjectCacheDatabase", "storeCacheObject::update", e2);
		}
		db.close();
	}

	private static void insertCacheObject(Context context, JSONCacheObjectInfo info) {
		JSONObjectCacheOpenHelper store = new JSONObjectCacheOpenHelper(context);
		SQLiteDatabase db = store.getWritableDatabase();
		try {
			db.insertOrThrow(JSONObjectCacheOpenHelper.TABLE_NAME, null, info.getInsertContentValues());
		} catch (final Exception e) {
			LOG.error("JSONObjectCacheDatabase", "storeCacheObject::insert", e);
		}
		db.close();
	}

	static class JSONCacheObjectInfo {
		String _url;
		JSONObject _object;
		long _expire;
		long _when_expire;
		long _when_visited;
		int _hit;
		boolean _new;
		boolean _notify;

		public String toString() {
			return "hit=" + _hit + " isnew=" + _new + " expire=" + _expire + " when_expire=" + _when_expire + " when_visited=" + _when_visited + " url=" + _url;
		}

		JSONCacheObjectInfo(Cursor cursor) {
			try {
				// url, expire, when_expire, when_visited, hitcount, notify,
				// isnew, jsonobject
				_url = cursor.getString(cursor.getColumnIndex("url"));
				String json = cursor.getString(cursor.getColumnIndex("jsonobject"));
				if (json != null && json.length() > 0) {
					_object = new JSONObject(json);
				} else {
					_object = null;
				}
				_expire = cursor.getLong(cursor.getColumnIndex("expire"));
				_when_expire = cursor.getLong(cursor.getColumnIndex("when_expire"));
				_when_visited = cursor.getLong(cursor.getColumnIndex("when_visited"));
				_hit = cursor.getInt(cursor.getColumnIndex("hitcount"));
				_new = (cursor.getInt(cursor.getColumnIndex("isnew")) > 0);
				_notify = (cursor.getInt(cursor.getColumnIndex("notify")) > 0);
			} catch (final Exception e) {
				LOG.error(this, "create", e);
			}
		}

		JSONCacheObjectInfo(String url, JSONObject obj, long exp, boolean notify) {
			_url = HTTPRequestString.purify(url);
			_object = obj;
			_when_visited = UtilClass.now();
			_when_expire = _when_visited + exp;
			_expire = exp;
			_hit = 1;
			_notify = notify;
			_new = false;
		}

		ContentValues getUpdateContentValues(boolean update_content) {
			// url, expire, when_expire, when_visited, hitcount, notify, isnew,
			// jsonobject
			// _url = HTTPRequestString.purify(_url);
			ContentValues cv = new ContentValues();
			cv.put("expire", _expire);
			cv.put("when_expire", _when_expire);
			cv.put("when_visited", _when_visited);
			cv.put("hitcount", _hit);
			cv.put("notify", (_notify ? 1 : 0));
			cv.put("isnew", (_new ? 1 : 0));
			if (update_content) {
				cv.put("jsonobject", _object.toString());
			}
			return cv;
		}

		ContentValues getInsertContentValues() {
			ContentValues cv = getUpdateContentValues(true);
			cv.put("url", _url);
			return cv;
		}

		boolean isExpired() {
			return (UtilClass.now() > _when_expire);
		}

		boolean needPrefetch() {
			return (UtilClass.now() > (_when_expire - _expire / 2));
		}

		void invalidate() {
			_when_expire = UtilClass.now() + _expire / 4;
		}

		void hit() {
			_hit++;
			_when_visited = UtilClass.now();
			_new = false;
		}

		public void update(long exp) {
			if (exp > 0) {
				_when_expire = UtilClass.now() + exp;
				_expire = exp;
			}
			_new = true;
		}

		public boolean needNotify() {
			return _notify;
		}

		public String getState() {
			if (isExpired()) {
				return URL_LINK_INVALID;
			} else {
				if (_new) {
					return URL_LINK_UPDATED;
				} else {
					return URL_LINK_VISITED;
				}
			}
		}
	}

	static class JSONObjectCacheOpenHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "json_cache";
		private static final int DATABASE_VERSION = 1;
		private static final String TABLE_NAME = "json_cache_tbl";

		// long _expire;
		// long _when_expire;
		// long _when_visited;
		// int _hit;
		// boolean _new;
		// boolean _notify;

		private static final String DICTIONARY_TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + "url TEXT, " + "expire LONG, " + "when_expire LONG, "
				+ "when_visited LONG, " + "hitcount INT, " + "notify INT, " + "isnew INT, " + "jsonobject TEXT, " + "PRIMARY KEY (url)" + ");";

		private static final String DICTIONARY_TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

		JSONObjectCacheOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DICTIONARY_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (newVersion == DATABASE_VERSION) {
				db.execSQL(DICTIONARY_TABLE_DROP);
				db.execSQL(DICTIONARY_TABLE_CREATE);
			}
		}
	}
}
