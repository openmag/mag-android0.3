package com.anheinno.android.libs.util;

import com.anheinno.android.libs.log.LOG;


public class ClassUtility {
	
	public static String getClassName(Object o) {
		String name = o.getClass().getName();
		int pos = name.lastIndexOf('.');
		if(pos < 0) {
			pos = -1;
		}
		return name.substring(pos + 1);
	}
	
	public static String getPackageName(Object o) {
		String name = o.getClass().getName();
		int pos = name.lastIndexOf('.');
		if(pos <= 0) {
			return "";
		}else {
			return name.substring(0, pos);
		}
	}
	
	public static Class<?> getSiblingClass(Object o, String classname) {
		return getSiblingClass(getPackageName(o), classname);
	}
	
	public static Class<?> getSiblingClass(String packagename, String classname) {
		StringBuffer fullname = new StringBuffer(packagename);
		if(fullname.length() > 0) {
			fullname.append('.');
		}
		fullname.append(classname);
		return getClass(fullname.toString());
	}
	
	public static Class<?> getClass(String classname) {
		try {
			return Class.forName(classname);
		}catch(final Exception e) {
			LOG.error("ClassUtility", "getClass " + classname + " error", e);
		}
		return null;
	}
	
}
