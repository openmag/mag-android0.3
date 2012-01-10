package com.anheinno.android.libs.json;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class JSONUtils {
	public static String[] JSONArray2StringArray(JSONArray arr) {
		String[] str_arr = new String[arr.length()];
		for(int i = 0; i < str_arr.length; i ++) {
			try {
				str_arr[i] = arr.getString(i);
			}catch(JSONException e) {
			}
		}
		return str_arr;
	}
}
