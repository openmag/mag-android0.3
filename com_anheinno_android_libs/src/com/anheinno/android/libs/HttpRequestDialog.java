package com.anheinno.android.libs;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.anheinno.android.libs.HTTPHelper;
import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.ui.EasyDialog;
import com.anheinno.android.libs.ui.ProgressUIInterface;

/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class HttpRequestDialog extends AsyncTask<Context, Integer, JSONObject> implements ProgressUIInterface, OnCancelListener {

	private Context _context;
	protected HTTPHelper _http;
	//private JSONObject _data;
	private HttpRequestDialogListener _rs;
	private ProgressDialog _progress_dialog;
	
	private static final int PROGRESS_TYPE_RESET = 0;
	private static final int PROGRESS_TYPE_RESET_INDETERMIN = 1;
	private static final int PROGRESS_TYPE_UPDATE = 2;
	
	private String _progress_message;

	public interface HttpRequestDialogListener {
		public void onGetJSONResult(JSONObject o);
		public boolean onError(String msg);
	}
	
	public HttpRequestDialog(Context con, String url, HttpRequestDialogListener rs) {
		this(con, url, HTTPHelper.HTTP_MODE_GET, rs);
	}
	
	public HttpRequestDialog(Context con, String url, String mode, HttpRequestDialogListener rs) {
		_context = con;
		_rs = rs;
		_http = new HTTPHelper(con, url, mode, this);
		_progress_message = null;
	}

	public void addHeader(String name, String val) {
		_http.addHeader(name, val);
	}

	public void start() {
		execute(_context);
	}

	public void updateGauge(int val) {
		publishProgress(new Integer[] {PROGRESS_TYPE_UPDATE, val});
	}

	public void resetGauge(String msg, int max, int start) {
		_progress_message = msg;
		publishProgress(new Integer[] {PROGRESS_TYPE_RESET, max, start});
	}

	public void resetGauge(String msg, int wait_seconds) {
		_progress_message = msg;
		publishProgress(new Integer[] {PROGRESS_TYPE_RESET_INDETERMIN});
	}

	@Override
	protected JSONObject doInBackground(Context... params) {
		boolean ret = _http.send();
		if (ret) {
			return _http.getJSONResult();
		} 
		return null;
	}
	
	@Override
	protected void onPreExecute()  {
		super.onPreExecute();
		_progress_dialog = new ProgressDialog(_context);
		_progress_dialog.setIndeterminate(true);
		_progress_dialog.setCancelable(true);
		_progress_dialog.setMessage(_context.getString(R.string.http_request_dialog_start_string));
		_progress_dialog.setOnCancelListener(this);
		_progress_dialog.show();
	}
	
	@Override
	protected void onProgressUpdate(Integer... values)  {
		super.onProgressUpdate(values);
		switch(values[0]) {
		case PROGRESS_TYPE_RESET:
			_progress_dialog.setMessage(_progress_message);
			_progress_dialog.setIndeterminate(false);
			_progress_dialog.setMax(values[1]);
			break;
		case PROGRESS_TYPE_RESET_INDETERMIN:
			_progress_dialog.setMessage(_progress_message);
			_progress_dialog.setIndeterminate(true);
			break;
		case PROGRESS_TYPE_UPDATE:
			_progress_dialog.setProgress(values[1]);
			break;
		}
 	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
	
	@Override
	protected void onPostExecute(JSONObject result)  {
		_progress_dialog.dismiss();
		if(result != null) {
			if(_rs != null) {
				_rs.onGetJSONResult(result);
			}else {
				EasyDialog.info(_context, result.toString());
			}
		}else {
			String http_result = _http.getUTF8Result();
			if (http_result.length() > 0) {
				String msg = null;
				try {
					result = new JSONObject(http_result);
					if (result.has("_msg")) {
						msg = result.getString("_msg");
					} else {
						msg = http_result;
					}
				} catch (final JSONException je) {
					msg = http_result + je.toString();
				}
				if(_rs == null || !_rs.onError(msg)) {
					EasyDialog.remind(_context, msg);
				}
			}
		}
	}

	public void onCancel(DialogInterface dialog) {
		LOG.error(this, "HttpRequest is cancelled!", null);
		_http.stop();
		this.cancel(true);
	}
}
