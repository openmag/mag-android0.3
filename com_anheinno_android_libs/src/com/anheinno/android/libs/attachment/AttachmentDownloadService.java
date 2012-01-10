/**
 * AttachmentDownloadManager.java
 *
 * Copyright 2007-2011 anhe.
 */
package com.anheinno.android.libs.attachment;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.RemoteViews;
import com.anheinno.android.libs.HTTPHelper;
import com.anheinno.android.libs.R;
import com.anheinno.android.libs.UtilClass;
import com.anheinno.android.libs.file.FileUtilityClass;

/**
 * 2011-3-17
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 * 
 */
public class AttachmentDownloadService extends Service {
	private static AttachmentDownloadManager _attachmentmanager;
	public static int NOTIFICATION_ID = 8888;

	private NotificationManager _nm;
	private Notification _notification;

	private Handler _handler;

	public void onCreate() {
		_nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		_notification = new Notification(R.drawable.download, "Downloading", System.currentTimeMillis());
		_attachmentmanager = new AttachmentDownloadManager();
		_handler = new Handler();

		start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		_attachmentmanager = null;
		super.onDestroy();
		// 被用户强行关闭后启动
		// this.startService(new Intent(this, AttachmentDownloadService.class));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		_attachmentmanager = null;
		super.finalize();
	}

	public void invokeLater(Runnable run) {
		_handler.post(run);
	}

	public void showNotification() {
		_nm.notify(NOTIFICATION_ID, _notification);
	}

	private void start() {
		Thread th = new Thread(_attachmentmanager);
		th.start();
	}

	public static AttachmentDownloadManager getInstance() {
		return _attachmentmanager;
	}

	public class AttachmentDownloadManager implements Runnable {
		// private Thread _worker_thread;
		private Hashtable<String, DownloadTask> _task_queue; // 所有工作队列，下载完成则从队列清除
		private Vector<DownloadTask> _download_queue;// 待处理下载队列，开始下载则从队列清除
		private Vector<DownloadTask> _start_queue; // 启动序列 用来启动DownloadTask和添加UI
		private TaskWorkerManger _task_mngr = null;

		private Vector<View> _download_view;

		private boolean _isworking;

		private AttachmentDownloadManager() {
			_task_queue = new Hashtable<String, DownloadTask>();
			_download_queue = new Vector<DownloadTask>();
			_start_queue = new Vector<DownloadTask>();
			_task_mngr = new TaskWorkerManger();
			_download_view = new Vector<View>();
			_isworking = false;
		}

		public Vector<View> getDownloadView() {
			return _download_view;
		}

		public synchronized void registerDownloadTask(String title, String url, Hashtable cookie, String dir) {
			System.out.println("AttachmentDownloadManager registerDownloadTask url:" + url);
			if (url == null || url.length() == 0) {
				return;
			}
			if (title == null) {
				title = "";
			}

			synchronized (_task_queue) {
				if (isDownloading(title, url)) {
					// 已经存在
					return;
				}

				_isworking = true;
				AttachmentField amf = new AttachmentField(AttachmentDownloadService.this, title, url, dir);

				DownloadTask data = new DownloadTask(title, url, cookie, dir, amf);
				System.out.println("AttachmentDownloadManager add url:" + url);
				_task_queue.put(data.toString(), data);
				_download_queue.addElement(data);
				_start_queue.addElement(data);

				_task_queue.notifyAll();
			}
		}

		public synchronized boolean cancelDownloadTask(String title, String url) {
			System.out.println("cancelDownloadTask " + title + url);
			synchronized (_task_queue) {
				return _task_mngr.cancelRunningTask(title + url);
			}
		}

		public synchronized boolean isDownloading(String title, String url) {
			int size = _task_queue.size();
			for (int i = 0; i < size; i++) {
				if (_task_queue.containsKey(title + url)) {
					return true;
				}
			}
			return false;
		}

		public synchronized boolean isWorking() {
			return _isworking;
		}

		public String getType(String filename) {
			String filetype = "";
			AttachmentFieldTypeStore store = new AttachmentFieldTypeStore(AttachmentDownloadService.this);
			SQLiteDatabase db = store.getReadableDatabase();
			if(db != null) {
				Cursor cursor = db.query(AttachmentFieldTypeStore.TABLE_NAME, new String[] { "filetype" }, "filename=?", new String[] { filename }, null, null, null, "1");
	
				if (cursor != null) {
					if (cursor.getCount() > 0 && cursor.moveToFirst()) {
						filetype = cursor.getString(cursor.getColumnIndex("filetype"));
					}
					cursor.close();
				}
				db.close();
			}
			return filetype;
		}

		public void run() {
			for (;;) {
				try {
					synchronized (_task_queue) {
						while (_task_queue.size() == 0) {
							_isworking = false;
							System.out.println("AttachmentDownloadManager wait!!!");
							_task_queue.wait();
						}
					}

					if (_task_queue.size() > 0) {
						boolean working = true;
						while (working) {
							if (_task_queue.size() > 0) {
								while (!_start_queue.isEmpty()) {
									synchronized (_start_queue) {
										if (!_start_queue.isEmpty()) {
											_task_mngr.newTaskWorker();

											RemoteViews remoteviews = new RemoteViews(getPackageName(), R.layout.notification);

											remoteviews.setTextViewText(R.id.noti_textview, "新增下载: "
													+ ((DownloadTask) _start_queue.elementAt(0)).getAttachmentField().getTitle());
											_notification.contentView = remoteviews;
											_notification.flags |= Notification.FLAG_AUTO_CANCEL;

											Intent notificationIntent = new Intent(AttachmentDownloadService.this, AttachmentDownloadActivity.class);

											PendingIntent contentIntent = PendingIntent.getActivity(AttachmentDownloadService.this, 0, notificationIntent, 0);
											_notification.contentIntent = contentIntent;

											showNotification();

											_download_view.add(((DownloadTask) _start_queue.elementAt(0)).getAttachmentField());

											_start_queue.removeElementAt(0);
										
										}
									}
								}
								working = true;
							} else {
								working = false;
							}
							System.out.println("AttachmentDownloadManager wait!!!");
							// Thread.sleep(1000);
							synchronized (_task_queue) {
								_task_queue.wait();// ///////////////////////////////////
							}
						}
					}
				} catch (final Exception e) {
					System.out.println("AttachmentDownloadManager run() " + e.toString());
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
						System.out.println("newTaskWorker create!!!");
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
			 * @param url
			 * @return
			 */
			private boolean cancelRunningTask(String titleurl) {
				synchronized (_worker_queue) {
					for (int i = 0; i < _worker_queue.size(); i++) {
						TaskWorker worker = (TaskWorker) _worker_queue.elementAt(i);
						if (worker._task != null && worker._task.titleurl().equals(titleurl)) {
							worker._task.cancelTask();
							return true;
						}
					}
				}
				return false;
			}
		}

		/*
		 * 下载任务worker线程 轮询_task_queue,
		 * 如果_task_queue不为空，则从_task_queue取下一个DownloadTask 执行下载任务，知道_task_queue为空
		 * _task_queue为空后，则将该worker从_task_mngr删除
		 */
		private class TaskWorker extends Thread {
			DownloadTask _task = null;

			public void run() {
				while (!_download_queue.isEmpty()) {
					synchronized (_download_queue) {
						if (!_download_queue.isEmpty()) {
							_task = (DownloadTask) _download_queue.elementAt(0);
							_download_queue.removeElementAt(0);
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

		/**
		 * 2011-3-18
		 * 
		 * @author 安和创新科技（北京）有限公司
		 * 
		 * @version 1.0
		 * 
		 *          存储下载任务数据
		 */
		class DownloadTask {
			private HTTPHelper _http;
			private String _title;
			private String _url;
			private Hashtable<String, String> _cookie;
			private String _dir;
			private AttachmentField _amf;

			public DownloadTask(String title, String url, Hashtable<String, String> cookie, String dir, AttachmentField amf) {
				_title = title;
				_url = url;
				_cookie = cookie;
				_dir = dir;
				_amf = amf;
			}

			public String titleurl() {
				return _title + _url;
			}

			public void cancelTask() {
				_http.stop();
				String downloadDir = _dir + UtilClass.str2digest(_url);
				if (FileUtilityClass.fileExists(downloadDir)) {
					System.out.println("cancelTask : file downloadDir exist, delete!!!");
					FileUtilityClass.deleteFile(downloadDir);
				}
			}

			public AttachmentField getAttachmentField() {
				return _amf;
			}

			public void run() {
				System.out.println("DownloadTask is running.......");
				boolean succ = false;
				_http = new HTTPHelper(AttachmentDownloadService.this, _url, HTTPHelper.HTTP_MODE_GET, _amf);

				String downloadDir = _dir + UtilClass.str2digest(_url);
				// downloadDir = _dir + _title
				
				if (_cookie != null && _cookie.size() > 0) {
					Enumeration e = _cookie.keys();
					while (e.hasMoreElements()) {
						String key = (String) e.nextElement();
						_http.addHeader(key, (String) _cookie.get(key));
						System.out.println("Send cookie: " + key + "=" + _cookie.get(key));
					}
				}

				/*TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				String szImei = TelephonyMgr.getDeviceId(); // Requires
				// READ_PHONE_STATE
				String szAndroidID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

				if (szAndroidID != null) {
					szImei += szAndroidID;
				}

				System.out.println("BOARD=" + Build.BOARD + ";BRAND=" + Build.BRAND + ";DEVICE=" + Build.DEVICE + ";DISPLAY=" + Build.DISPLAY + ";FINGERPINT="
						+ Build.FINGERPRINT + ";HOST=" + Build.HOST + ";ID=" + Build.ID + ";MODEL=" + Build.MODEL + ";PRODUCT=" + Build.PRODUCT + ";TAGS=" + Build.TAGS
						+ ";TIME=" + Build.TIME + ";TYPE=" + Build.TYPE + ";USER=" + Build.USER + ";INCREMENTAL=" + Build.VERSION.INCREMENTAL + ";RELEASE="
						+ Build.VERSION.RELEASE + ";SDK=" + Build.VERSION.SDK);
				String info = szImei + ";" + Build.DEVICE + ";" + Build.BRAND + ";" + Build.VERSION.RELEASE + ";" + getPackageName() + ";" + szImei;

				_http.addHeader("X-Anhe-Handheld-INFO", info);*/

				// 文件已存在
				if (FileUtilityClass.fileExists(downloadDir)) {
					System.out.println("file downloadDir exist!!!");
					FileUtilityClass.deleteFile(downloadDir);
				}
				System.out.println("start downloading .................");
				succ = _http.send(downloadDir);
				int retry = 5;
				while (!succ && retry > 0) {
					if (_http.getUTF8Result().startsWith("IOException")) {
						// java.io.InterruptedIOException: Local connection
						// timed
						// out after ~ 120000
						// 断点续传
						long size = 0;
						if (FileUtilityClass.fileExists(downloadDir)) {
							size = FileUtilityClass.getSize(downloadDir);
							System.out.println("file downloadDir exist!!! size= " + size);
						}
						succ = _http.send(downloadDir, (int) size);
					} else {
						// 其他错误

					}
					retry--;
				}
				if (succ) {
					// AttachmentFieldTypeStore.addFile(UtilClass.str2digest(_url),
					// _http.getContentType());

					AttachmentFieldTypeStore store = new AttachmentFieldTypeStore(AttachmentDownloadService.this);
					SQLiteDatabase db = store.getWritableDatabase();
					ContentValues cv = new ContentValues();
					cv.put("filename", UtilClass.str2digest(_url));

					String type = "text/plain";
					if (_http.getContentType() != null) {
						type = _http.getContentType();
					}
					cv.put("filetype", type);

					db.insert(AttachmentFieldTypeStore.TABLE_NAME, null, cv);
					db.close();
				}
				System.out.println("The end of downloading .................is succ " + succ);
				_amf.setSucc(succ);

				// _notification.contentView
				// AttachmentScreen.getInstance().deleteField(_amf);

				synchronized (_task_queue) {
					_task_queue.notifyAll();
					_task_queue.remove(toString());
				}
			}

			/*
			 * 用来比较是不是同一下载任务
			 */
			public String toString() {
				return _title + _url;
			}
		}

		class AttachmentFieldTypeStore extends SQLiteOpenHelper {
			private static final String DATABASE_NAME = "attachment";
			private static final int DATABASE_VERSION = 1;
			private static final String TABLE_NAME = "file_type";

			private static final String SQL = "CREATE TABLE " + TABLE_NAME + " (filename TEXT, filetype TEXT, PRIMARY KEY (filename));";

			public AttachmentFieldTypeStore(Context context) {
				super(context, DATABASE_NAME, null, DATABASE_VERSION);
			}

			@Override
			public void onCreate(SQLiteDatabase db) {
				db.execSQL(SQL);
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				if (newVersion == DATABASE_VERSION) {
					db.execSQL(SQL);
					db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
				}
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}