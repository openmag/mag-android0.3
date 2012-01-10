/**
 * DownloadConsumer.java
 *
 * Copyright 2007-2010 anhe.
 */
package com.anheinno.android.libs;

import org.json.JSONObject;

/**
 * 2010-5-21
 *
 * @author 沈瑞恒
 *
 * @version 1.0
 *
 */
public interface DownloadConsumer {
	public void dataArrival(JSONObject result, Object params);
	
	/* 当下载出错时调用 */
	public boolean retrieveError(String msg);
}
