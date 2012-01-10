/**
 * DownloadManager.java
 *
 * Copyright 2007-2010 anhe.
 */
package com.anheinno.android.libs;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.ui.ProgressUIInterface;
import com.anheinno.android.libs.R;

/**
 * 2010-5-21
 * 
 * @author 沈瑞恒
 * 
 * @version 1.0
 * 
 */
public class DownloadManager {

	private static DownloadManager _SingDM = null;

	private Vector<DownloadTask> _task_queue; // 工作队列
	private TaskWorkerManger _task_mngr = null;

	private DownloadManager() {
		_task_queue = new Vector<DownloadTask>();
		_task_mngr = new TaskWorkerManger();
	}

	/**
	 * 单例
	 * 
	 * @return
	 */
	public static DownloadManager getDownloadManager() {
		if (_SingDM == null) {
			_SingDM = new DownloadManager();
		}
		return _SingDM;
	}

	/**
	 * HTTP连接要把cookie值加入HTTP header中 调用HTTPHelper来获取数据
	 * 数据获取后，调用DownloadConsumer的dataarrival方法处理数据。
	 * 
	 * @param url
	 *            download URL,
	 *            例如：http://219.237.0.122/service.php?_action=MAINSCREEN
	 * @param cookies
	 *            HTTP header, 例如： X-Anhe-Account-Username: qiujian mode:
	 *            只支持GET方式
	 * @param refresh
	 *            如果refresh为true，则总是发起HTTP连接，获取数据
	 *            否则，首先查看缓存，如果有数据，则不发起请求，从缓存读取数据，并返回.否则，还是发起HTTP连接，获取数据
	 * @param expire
	 *            请求URI的缓存有效期 refresh: 是否刷新缓存
	 * @param ui
	 * @param dc
	 */
	public synchronized void registerDownloadTask(Context con, String url, Hashtable<String, String> cookies, boolean refresh, long expire, boolean notify,
			ProgressUIInterface ui, DownloadConsumer dc, Object context) {

		if (ui != null) {
			// ui.resetGauge(_resources.getString(HTTP_MESSAGE_RETRIEVING), 0,
			// 100, 0);
		}
		synchronized (_task_queue) {
			// 2011-4-19
			boolean find_same = false;
			DownloadTask dt = new DownloadTask(con, url, cookies, refresh, expire, notify, ui, dc, context);

			for (int i = 0; i < _task_queue.size(); i++) {
				DownloadTask dt_tmp = (DownloadTask) _task_queue.elementAt(i);
				if (dt_tmp.isSameTask(dt)) {
					find_same = true;
					dt_tmp.appendTask(dt);
				}
			}
			if (!find_same) {
				find_same = _task_mngr.appendTask(dt);
			}
			if (!find_same) {
				_task_queue.addElement(dt);
				_task_mngr.newTaskWorker();
			}
		}
	}

	public boolean cancelDownloadTask(DownloadConsumer consumer) {
		synchronized (_task_queue) {
			boolean deleted = false;

			for (int i = 0; i < _task_queue.size(); i++) {
				DownloadTask task = (DownloadTask) _task_queue.elementAt(i);
				if (task.removeConsumer(consumer)) {
					if (task.noConsumer()) {
						_task_queue.removeElementAt(i);
						deleted = true;
						i--;
					}
				}
			}

			boolean del2 = _task_mngr.cancelRunningTask(consumer);

			return (deleted || del2);
		}
	}

	class DownloadTaskConsumer {
		private DownloadConsumer _dc;
		private Object _context;

		DownloadTaskConsumer(DownloadConsumer dc, Object obj) {
			_dc = dc;
			_context = obj;
		}
	}

	class DownloadTask {
		private HTTPHelper _http;
		private Hashtable<String, String> _cookies;
		private boolean _refresh;
		private Vector<DownloadTaskConsumer> _consumers;
		private ProgressUIInterfaceGroup _ui_list; // ProgressUIInterface _ui;
		private long _expire;
		private boolean _notify;
		private String _url;
		private Context _app_context;
		private boolean _cancelled;
		private boolean _start_consuming;

		// private DBTableDescriptor _db_descriptor;

		public DownloadTask(Context con, String url, Hashtable<String, String> cookies, boolean refresh, long expire, boolean notify, ProgressUIInterface ui,
				DownloadConsumer dc, Object context) {
			this._app_context = con;
			this._url = url;
			this._cookies = cookies;
			this._refresh = refresh;
			this._expire = expire;
			this._notify = notify;
			this._cancelled = false;
			this._start_consuming = false;

			this._ui_list = new ProgressUIInterfaceGroup();
			if (ui != null) {
				_ui_list.addElement(ui);
			}
			this._consumers = new Vector<DownloadTaskConsumer>();
			_consumers.addElement(new DownloadTaskConsumer(dc, context));
			// this._db_descriptor = null;
		}

		public String url() {
			return _url;
		}

		private boolean isSameTask(DownloadTask task) {
			if (!_url.equals(task._url)) {
				return false;
			}

			if (_refresh != task._refresh) {
				return false;
			}

			if (_expire != task._expire) {
				return false;
			}

			if (_notify != task._notify) {
				return false;
			}

			// ... compare cookie
			if (_cookies.size() != task._cookies.size()) {
				return false;
			}

			Enumeration<String> cookie_keys = _cookies.keys();
			while (cookie_keys.hasMoreElements()) {
				String name = (String) cookie_keys.nextElement();
				if (!task._cookies.containsKey(name)) {
					return false;
				}
				if (!_cookies.get(name).equals(task._cookies.get(name))) {
					return false;
				}
			}

			return true;
		}

		private boolean appendTask(DownloadTask dt) {
			synchronized (_consumers) {
				if (!_start_consuming) {
					for (int i = 0; i < dt._ui_list.size(); i++) {
						_ui_list.addElement(dt._ui_list.elementAt(i));
					}
					_consumers.addElement(dt._consumers.elementAt(0));
					return true;
				} else {
					return false;
				}
			}
		}

		private boolean removeConsumer(DownloadConsumer dc) {
			synchronized (_consumers) {
				if (!_start_consuming) {
					int i = 0;
					for (i = 0; i < _consumers.size(); i++) {
						DownloadTaskConsumer dtc = (DownloadTaskConsumer) _consumers.elementAt(i);
						if (dtc._dc == dc) {
							break;
						}
					}
					if (i < _consumers.size()) {
						_consumers.removeElementAt(i);
						return true;
					}
				}
			}
			return false;
		}

		private boolean noConsumer() {
			synchronized (_consumers) {
				if (_consumers.size() == 0) {
					return true;
				}
			}
			return false;
		}

		public void run() {
			JSONObject _data = null;
			/*
			 * 首先在JSONObjectCacheFactory中找对应URI
			 */
			if (_url == null || _url.length() == 0) {
				return;
			}

			_ui_list.resetGauge(_app_context.getString(R.string.http_message_retrieving), 2);

			if (JSONBrowserConfig.isCacheEnabled(_app_context)) {
				if (!_refresh) {
					_data = JSONObjectCacheDatabase.getObject(_app_context, _url, true);
				} else {
					JSONObjectCacheDatabase.purgeObject(_app_context, _url);
					_data = null;
				}
			} else {
				_data = null;
			}

			if (_data == null) {
				_http = new HTTPHelper(_app_context, _url, _ui_list);
				if (_expire > 0) {
					_http.addHeader("X-Anhe-Link-Expire", "" + _expire);
				}
				if (_cookies != null && _cookies.size() > 0) {
					System.out.println("Cookie size: " + _cookies.size());
					Enumeration<String> e = _cookies.keys();
					while (e.hasMoreElements()) {
						String key = (String) e.nextElement();
						_http.addHeader(key, _cookies.get(key));
						System.out.println("Send cookie: " + key + "=" + _cookies.get(key));
					}
				}
				boolean ret = _http.send();
				System.out.println("_http send:" + ret);
				if (ret) {
					System.out.println("ContentType:" + getContentType());
					if (getContentType().startsWith("image/")) {
						JSONObject o = new JSONObject();
						try {
							o.put("image", _http.getISO8859Result());
							//o.put("type", getContentType());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						_data = o;
						//System.out.println("image" + o.toString());
					} else {
						_data = _http.getJSONResult();
					}
					System.out.println("data:" + _data);
					System.out.println("expire: " + _expire);
					// 服务器返回200但数据有误
					if (_data == null) {
						retrieveError(_http.getUTF8Result());
					} else if (JSONBrowserConfig.isCacheEnabled(_app_context) && _expire > 0) {
						JSONObjectCacheDatabase.saveObject(_app_context, _url, _data, _expire, _notify);
					}
				} else if (!_cancelled) {
					String http_result = _http.getUTF8Result();
					if (!retrieveError(http_result)) {
						try {
							if (http_result.length() > 0) {
								JSONObject msg = new JSONObject(http_result);
								if (msg.has("_msg")) {
									LOG.warning(this, msg.getString("_msg"));
								} else {
									LOG.warning(this, http_result);
								}
							}
						} catch (final JSONException je) {
							LOG.warning(this, http_result + je.toString());
						}
					}
//					EasyDialog.info(_app_context, _http.getResult());
				}
			}

			if (_data != null) {
				/**
				 * 如果是DB数据表描述，则注册数据表同步任务
				 */
				// if (_data.has(DBTableDescriptor.DB_DESC_SIGNATURE_NAME)) {
				// try {
				// String ct = (String)
				// _data.get(DBTableDescriptor.DB_DESC_SIGNATURE_NAME);
				// if (ct != null &&
				// ct.equals(DBTableDescriptor.DB_DESC_SIGNATURE)) {
				// DBTableDescriptor db_desc = new DBTableDescriptor();
				// if (db_desc.parseConfig(_data)) {
				// DBDownloadManager dm = DBDownloadManager.getInstance();
				// dm.registerDownloadTask(db_desc);
				// }
				// }
				// } catch (final Exception e) {
				//
				// }
				// }
				dataArrival(_data);
			}
		}

		private boolean retrieveError(String msg) {
			boolean consumed = false;
			synchronized (_consumers) {
				_start_consuming = true;
				for (int i = 0; i < _consumers.size(); i++) {
					DownloadTaskConsumer dtc = (DownloadTaskConsumer) _consumers.elementAt(i);
					if (dtc._dc.retrieveError(msg)) {
						consumed = true;
					}
				}
			}
			return consumed;
		}

		private void dataArrival(JSONObject data) {
			synchronized (_consumers) {
				_start_consuming = true;
				for (int i = 0; i < _consumers.size(); i++) {
					DownloadTaskConsumer dtc = (DownloadTaskConsumer) _consumers.elementAt(i);
					dtc._dc.dataArrival(data, dtc._context);
				}
			}
		}

		public String getContentType() {
			String type = _http.getContentType();
			if (type != null) {
				return type.toLowerCase();
			} else {
				return null;
			}
		}

		public void cancelTask() {
			_cancelled = true;
			if (_http != null) {
				_http.stop();
			}
		}
	}

	/**
	 * 
	 * 管理所有下载worker线程的容器
	 * 
	 */
	private class TaskWorkerManger {
		private Vector<TaskWorker> _worker_queue;
		private static final int TASK_WORKER_MAX_NUMBER = 3;

		TaskWorkerManger() {
			_worker_queue = new Vector<TaskWorker>();
		}

		/**
		 * 
		 * 当注册了新的下载任务后，调用此方法 如果当前活动的worker线程数量超过TASK_WORKER_MAX_NUMBER，
		 * 则不再启动新的worker线程 否则，启动一个新的worker线程
		 * 
		 */
		private void newTaskWorker() {
			synchronized (_worker_queue) {
				if (_worker_queue.size() < TASK_WORKER_MAX_NUMBER) {
					TaskWorker new_worker = new TaskWorker();
					_worker_queue.addElement(new_worker);
					new_worker.start();
				}
			}
		}

		/**
		 * 
		 * 
		 * 当一个worker线程结束后，调用此方法，将此worker线程从 worker管理器中删除
		 * 
		 * @param worker
		 */
		private void removeTaskWorker(TaskWorker worker) {
			synchronized (_worker_queue) {
				_worker_queue.removeElement(worker);
			}
		}

		/**
		 * 
		 * @param consumer
		 * @return
		 */
		private boolean cancelRunningTask(DownloadConsumer consumer) {
			boolean deleted = false;
			synchronized (_worker_queue) {
				for (int i = 0; i < _worker_queue.size(); i++) {
					TaskWorker worker = (TaskWorker) _worker_queue.elementAt(i);
					if (worker._task != null && worker._task.removeConsumer(consumer)) {
						if (worker._task.noConsumer()) {
							worker._task.cancelTask();
							deleted = true;
						}
					}
				}
			}
			return deleted;
		}

		private boolean appendTask(DownloadTask dt) {
			boolean append = false;
			synchronized (_worker_queue) {
				for (int i = 0; i < _worker_queue.size(); i++) {
					TaskWorker worker = (TaskWorker) _worker_queue.elementAt(i);
					if (worker._task != null && worker._task.isSameTask(dt)) {
						append = worker._task.appendTask(dt);
						break;
					}
				}
			}
			return append;
		}
	}

	/*
	 * 下载任务worker线程 轮询_task_queue,
	 * 如果_task_queue不为空，则从_task_queue取下一个DownloadTask 执行下载任务，直到_task_queue为空
	 * _task_queue为空后，则将该worker从_task_mngr删除
	 */
	private class TaskWorker extends Thread {
		DownloadTask _task = null;

		public void run() {
			while (!_task_queue.isEmpty()) {
				synchronized (_task_queue) {
					if (!_task_queue.isEmpty()) {
						_task = (DownloadTask) _task_queue.elementAt(0);
						_task_queue.removeElementAt(0);
					}
				}
				if (_task != null) {
					_task.run();
					_task = null;
				}
			}
			_task_mngr.removeTaskWorker(this);
		}

	}
}
