package com.anheinno.android.libs.ui;

import java.util.Vector;

//import com.anheinno.libs.log.EventLogUtility;
//import com.anheinno.libs.log.LOG;

//import net.rim.device.api.ui.UiApplication;
/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class UpdateUITimerThread extends Thread {
	private static Vector<UpdateUIClientInterface> _ui_clients;
	private static Thread _instance = null;
	private static boolean _stop;
	private static final int _ui_intval = 1000;

	static {
		_ui_clients = new Vector<UpdateUIClientInterface>(2);
	}

	public static void addUIClient(UpdateUIClientInterface client) {
		synchronized (_ui_clients) {
			_ui_clients.addElement(client);
			if (_ui_clients.size() == 1) {
				_stop = false;
				_instance = new UpdateUITimerThread();
				_instance.start();
			}
		}
	}

	public static void removeUIClient(UpdateUIClientInterface client) {
		synchronized (_ui_clients) {
			_ui_clients.removeElement(client);
			if (_ui_clients.size() == 0) {
				clean();
			}
		}
	}

	public void run() {
		while (!_stop) {
//			try {
				while (!_stop) {
					synchronized (_ui_clients) {
						for (int i = 0; i < _ui_clients.size(); i++) {
							UpdateUIClientInterface ui = (UpdateUIClientInterface) _ui_clients.elementAt(i);
							// synchronized(UiApplication.getEventLock()) {
							ui.doUpdate();
							// }
						}
					}

					try {
						sleep(_ui_intval);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
//			} catch (final Exception e) {
//				System.out.println("UpdateUITimerThread:" + e.toString());
//				try {
//					sleep(1000);
//				} catch (InterruptedException e1) {
//					e1.printStackTrace();
//				}
//			}
		}
	}

	public static void clean() {
		if (_instance == null) {
			return;
		}
		_stop = true;
		try {
			_instance.join();
		} catch (final Exception e) {
			// LOG.error("UpdateUITimerThread", "clean", e);
		} finally {
			_instance = null;
		}
	}
}
