/**
 * LUFLimitedHashtable.java
 *
 * Copyright 2007-2011 anhe.
 */
package com.anheinno.android.libs.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * 2011-6-2
 *
 * @author 安和创新科技（北京）有限公司
 *
 * @version 1.0
 *
 */
public class LUFLimitedHashtable<K, V> {
	
	private Hashtable<K, CountedObject> _table;
	private int _size_limit;
	private String _id;
	
	private class CountedObject {
		private V _object;
		private int _count;
		private long _last_visited;
		
		CountedObject(V o) {
			_object = o;
			_count = 1;
			_last_visited = System.currentTimeMillis();
		}
		
		V getObject() {
			_count++;
			_last_visited = System.currentTimeMillis();
			return _object;
		}
	}
	
	public LUFLimitedHashtable(int limit, String id) {
		super();
		_table = new Hashtable<K, CountedObject>(limit);
		_size_limit = limit;
		_id = id;
	}
	
	public void put(K key, V obj) {
		saveObject(key, obj);
	}
	
	public V get(K key) {
		return getObject(key);
	}
	
	public boolean containsKey(K key) {
		return _table.containsKey(key);
	}
	
	private synchronized void saveObject(K key, V bmp) {
		while(_table.size() >= _size_limit) {
			System.out.println("[" + _id + "] Oversize hashtable, remove Least Used First Object!!!!");
			System.out.println(bmp.toString());
			Enumeration<K> keys = _table.keys();
			K minkey = null;
			int min_count = -1;
			long oldest_used = System.currentTimeMillis();
			while(keys.hasMoreElements()) {
				K bmpkey = keys.nextElement();
				CountedObject br = _table.get(bmpkey);
				if(min_count < 0 || min_count > br._count) {
					min_count = br._count;
					minkey = bmpkey;
				}else if(oldest_used > br._last_visited) {
					oldest_used = br._last_visited;
					minkey = bmpkey;
				}
			}
			if(minkey != null) {
				CountedObject rmobj = _table.remove(minkey);
				System.out.println("rmobj: " + rmobj.getObject().toString());
				rmobj = null;
				System.gc();
			}
		}
		CountedObject br = new CountedObject(bmp);
		_table.put(key, br);
	}
	
	private synchronized V getObject(K key) {
		if(_table.containsKey(key)) {
			CountedObject br = _table.get(key);
			return br.getObject();
		}else {
			return null;
		}
	}
	
	public synchronized Object[] release() {
		Object[] array = new Object[_table.size()];
		Collection<CountedObject> vals = _table.values();
		Iterator<CountedObject> iter = vals.iterator();
		int i = 0;
		while(iter.hasNext()) {
			CountedObject co = iter.next();
			array[i] = co._object;
			co._object = null;
			i++;
		}
		_table.clear();
		return array;
	}
}
