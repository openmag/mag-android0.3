package com.anheinno.android.libs.ui;

import com.anheinno.android.libs.R;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

public class EasyDialog {
	
	static abstract class DelayedMessage implements Runnable {
		private String _msg;
		private Context _context;
		
		DelayedMessage(Context con, String msg) {
			_context = con;
			_msg = msg;
		}
	
		protected String getMessage() {
			return _msg;
		}
		
		protected Context getContext() {
			return _context;
		}
	}
	
	public static void info(Context con, int id) {
		info(con, con.getString(id));
	}
	
	public static void info(Context con, String msg) {
		Builder build = new Builder(con);
		build.setTitle(con.getString(R.string.information_dialog_title));
		build.setMessage(msg);
		build.setIcon(android.R.drawable.ic_dialog_info);
		build.setNeutralButton(android.R.string.ok, null);
		build.show();
	}
	
	public static void postInfo(Context con, int id) {
		postInfo(con, con.getString(id));
	}
	
	public static void postInfo(Context con, String msg) {
		if(con instanceof UiApplication) {
			((UiApplication)con).invokeLater(new DelayedMessage(con, msg) {
				public void run() {
					info(getContext(), getMessage());
				}
			});
		}
	}

	public static void about(Context con, String msg) {
		Builder build = new Builder(con);
		build.setTitle(con.getString(R.string.json_screen_menu_about));
		build.setMessage(msg);
		build.setIcon(con.getResources().getDrawable(R.drawable.logo));
		build.setNeutralButton(android.R.string.ok, null);
		build.show();
	}
	
	public static void shortAlert(Context con, int id) {
		shortAlert(con, con.getString(id));
	}
	
	public static void shortAlert(Context con, String msg) {
		Toast.makeText(con, msg, Toast.LENGTH_SHORT).show();
	}

	public static void longAlert(Context con, int id) {
		longAlert(con, con.getString(id));
	}
	public static void longAlert(Context con, String msg) {
		Toast.makeText(con, msg, Toast.LENGTH_LONG).show();
	}

	public static void remind(Context con, int id) {
		remind(con, con.getString(id));
	}
	
	public static void remind(Context con, String msg) {
		Builder build = new Builder(con);
		build.setTitle(con.getString(R.string.alert_dialog_title));
		build.setMessage(msg);
		build.setIcon(android.R.drawable.ic_dialog_alert);
		build.setNeutralButton(android.R.string.ok, null);
		build.show();
	}
	
	public static void confirm(Context con, int id, final ConfirmDialogListener callback) {
		confirm(con, con.getString(id), callback);
	}
	
	public static void confirm(Context con, String msg, final ConfirmDialogListener callback) {
		Builder build = new Builder(con);
		build.setTitle(con.getString(R.string.confirm_dialog_title));
		build.setMessage(msg);
		build.setIcon(android.R.drawable.ic_dialog_alert);
		OnClickListener listener = new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(which == DialogInterface.BUTTON_POSITIVE) {
					callback.onYes();
				}else if(which == DialogInterface.BUTTON_NEGATIVE) {
					callback.onNo();
				}
			}
		};
		build.setPositiveButton(android.R.string.yes, listener);
		build.setNegativeButton(android.R.string.no, listener);
		build.show();
	}
	
	public interface ConfirmDialogListener {
		public void onYes();
		public void onNo();
	}
}
