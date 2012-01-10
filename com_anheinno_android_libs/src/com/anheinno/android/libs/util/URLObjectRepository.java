/**
 * URLObjectRepository.java
 *
 * Copyright 2007-2011 anhe.
 */
package com.anheinno.android.libs.util;

import java.util.Hashtable;

import com.anheinno.android.libs.HTTPRequestString;

/**
 * 2011-4-19
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 * 
 */
public class URLObjectRepository {
	private static Hashtable<String, Object> _src_tbl;
	static {
		_src_tbl = new Hashtable<String, Object>();
	}

	public static boolean has(String url) {
		HTTPRequestString req = new HTTPRequestString();
		if (req.parse(url)) {
			url = req.getURL();
			if (_src_tbl.containsKey(url)) {
				return true;
			}
		}
		return false;
	}

	public static Object get(String url) {
		System.out.println("URLObjectRepository get url:" + url);
		HTTPRequestString req = new HTTPRequestString();
		if (req.parse(url)) {
			url = req.getURL();
			if (_src_tbl.containsKey(url)) {
				return _src_tbl.get(url);
			}
		}
		return null;
	}

	public static void put(String url, Object options) {
		System.out.println("URLObjectRepository save url:" + url);
		HTTPRequestString req = new HTTPRequestString();
		if (req.parse(url)) {
			url = req.getURL();
			if (!_src_tbl.containsKey(url)) {
				_src_tbl.put(url, options);
			}else{
				System.out.println("URLObjectRepository save url, aleardy has:" + url);
			}
		}
	}
}
