package com.anheinno.android.libs.json;

import org.json.JSONObject;

/**
 * 定义一个可以存储为JSON对象和从JSON对象反解析回来的对象接口
 * 
 * @author Anhe Innovation Technology
 * @author qiujian
 * @version 1.0.0.0
 * @date 2010/1/30
 * 
 */

public interface JSONSerializable {
	
	/**
	 * Deserialize一个对象
	 * 
	 * @param JSON对象
	 * @return boolean，是否解析成功
	 */
	public boolean deserializeJSON(JSONObject o);
	
	/**
	 * Serialize一个对象的方法
	 * 
	 * @return Serialize后的对象
	 */
	public JSONObject serializeJSON();
}
