package com.anheinno.android.libs;


import java.util.Hashtable;

public class JSONBrowserCookieStore {
	private static Hashtable<String, String> _cookie;
	static {
		_cookie = new Hashtable<String, String>();
	}
	
	public static Hashtable<String, String> getCookies() {
		return _cookie;
	}

	public static void addCookie(String key, String val) {
		System.out.println("Add cookie: " + key + "=" + val);
		_cookie.put(key, val);
	}

	public static void removeCookie(String key) {
		_cookie.remove(key);
	}

	public static void clearCookie() {
		_cookie.clear();
	}

	/*public static void copyCookies(JSONBrowserScreen sc) {
		Enumeration<String> e = sc._cookie.keys();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			addCookie(key, sc._cookie.get(key));
			System.out.println("Copy cookie: " + key + "=" + sc._cookie.get(key));
		}
	}*/
}
