/*
 * SortedStringVector.java
 *
 * <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.util;


/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class SortedStringVector extends SortedVector<String> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7274632719090781617L;

	public int compare(String o1, String o2) {
        String s1 = (String)o1;
        String s2 = (String)o2;
        return s1.compareTo(s2);
    }
} 
