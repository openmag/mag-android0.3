package com.anheinno.android.libs;

/**
 * HTTPHelper.java
 * 2011-4-4
 * 未获得手机信息需要传入activity的上下文信息
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.anheinno.android.libs.file.FileUtilityClass;
import com.anheinno.android.libs.graphics.GraphicUtilityClass;
import com.anheinno.android.libs.ui.ProgressUIInterface;
import com.anheinno.android.libs.util.DeviceUuidFactory;
import com.anheinno.android.libs.util.URLUTF8Encoder;
import com.anheinno.android.libs.R;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.os.Build;
import android.provider.Settings.Secure;

/**
 * @author shenrh
 * 
 */
public class HTTPHelper {

	private ProgressUIInterface _progress_ui;
	private Context _context;
	private HttpURLConnection _urlConnection;
	private String _mode;
	private String _params;
	private int _responseCode;
	private String _responseMsg;
	private String _content_type;
	private String _iso_8859_1_result;
	private Hashtable<String, String> _headers;
	private String _url;
	private boolean _stop;

	private static final Charset _default_charset = Charset.forName("ISO-8859-1");
	
	public final static String HTTP_MODE_GET = "GET";
	public final static String HTTP_MODE_POST = "POST";

	public HTTPHelper(Context context, String url, String mode) {
		this(context, url, mode, null);
	}

	public HTTPHelper(Context context, String url, ProgressUIInterface progress_ui) {
		this(context, url, HTTP_MODE_GET, progress_ui);
	}

	public HTTPHelper(Context context, String url, String mode, ProgressUIInterface progress_ui) {
		_context = context;
		_url = url;
		_mode = mode;
		_iso_8859_1_result = "";
		_params = "";
		_responseMsg = null;
		_responseCode = 0;
		_headers = null;
		_stop = false;
		_progress_ui = progress_ui;
	}

	public void addHeader(String name, String val) {
		if (null == _headers) {
			_headers = new Hashtable<String, String>();
		}
		_headers.put(name, val);
	}

	public void addParam(String key, String val) {
		if (_params.length() > 0) {
			_params += "&";
		}
		_params += URLUTF8Encoder.encode(key) + "=" + URLUTF8Encoder.encode(val);
	}

	public boolean send() {
		return send(null);
	}

	public boolean send(String dir) {
		return send(dir, 0);
	}

	public boolean send(String dir, int startSize) {
		boolean succ = false;
		OutputStream os = null;
		InputStream is = null;

		_stop = false;

		String req_url = _url;

		if (_mode.equalsIgnoreCase(HTTP_MODE_GET) && _params.length() > 0) {
			req_url += "?" + _params;
		}

		// 中继
		String http_url = req_url;
		//if (_context != null) {
		if (JSONBrowserConfig.useRelay(_context)) {
			http_url = JSONBrowserConfig.getRelayServer(_context);
		}
		//}

		try {
			URL url = new URL(http_url);
			if (JSONBrowserConfig.useRelay(_context)) {
				System.out.println("relay url=" + http_url);
				CustomX509TrustManager.allowAllSSL();
				_urlConnection = (HttpsURLConnection) url.openConnection();
			} else {
				_urlConnection = (HttpURLConnection) url.openConnection();
			}
			System.out.println("url=" + req_url);
			System.out.println("_urlConnection=" + _urlConnection.toString());

			if (_urlConnection != null) {
				if (_context != null) {

					TelephonyManager TelephonyMgr = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
					String szImei = TelephonyMgr.getDeviceId(); // Requires
					// READ_PHONE_STATE
					String szAndroidID = Secure.getString(_context.getContentResolver(), Secure.ANDROID_ID);

					if (szAndroidID != null) {
						szImei += szAndroidID;
					}

					DeviceUuidFactory uuid = new DeviceUuidFactory(_context);
					String info = uuid.getDeviceUuid() // szImei
							+ ";" + Build.DEVICE 
							+ ";" + Build.BOARD
							+ ";" + Build.VERSION.RELEASE + "." + Build.VERSION.INCREMENTAL
							+ ";" + _context.getPackageName()
							+ ";" + szImei 
							+ ";" + "Android"
							+ ";" + (GraphicUtilityClass.isTouchScreen(_context)?"touch":"keyboard") 
							+ ";" + (GraphicUtilityClass.isNavigation(_context)?"nav":"nonav")
							+ ";" + Math.max(GraphicUtilityClass.getDisplayWidth(_context), GraphicUtilityClass.getRawDisplayHeight(_context))
							+ ";" + Math.min(GraphicUtilityClass.getDisplayWidth(_context), GraphicUtilityClass.getRawDisplayHeight(_context))
							;

					_urlConnection.setRequestProperty("X-Anhe-Handheld-INFO", info);
					//LOG.error(this, "device info " + info, null);
					
					//_urlConnection.setRequestProperty("X-Anhe-Handheld-Platform", "Android");

					if (JSONBrowserConfig.useRelay(_context)) {
						_urlConnection.setRequestProperty("X-Anhe-Relay-URL", req_url);
					}
				}

				if (null != _headers) {
					Enumeration<String> e = _headers.keys();
					while (e.hasMoreElements()) {
						String key = (String) e.nextElement();
						_urlConnection.setRequestProperty(key, (String) _headers.get(key));
					}
				}

				if (startSize > 0) {
					System.out.println("http range bytes = " + startSize);
					_urlConnection.setRequestProperty("User-Agent", "NetFox");
					_urlConnection.setRequestProperty("RANGE", "bytes=" + startSize);
				}

				_urlConnection.setUseCaches(false);
				_urlConnection.setAllowUserInteraction(false);
				_urlConnection.setReadTimeout(60000);
				_urlConnection.setConnectTimeout(60000);

				_urlConnection.setRequestMethod(_mode);

				if (_mode.equalsIgnoreCase(HTTP_MODE_POST)) {
					_urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

					os = _urlConnection.getOutputStream();
					System.out.println("Post Data: " + _params);
					int output_offset = 0;
					int output_len = 4096;
					byte[] output_bytes = _params.getBytes();

					if (_progress_ui != null) {
						_progress_ui.resetGauge(_context.getString(R.string.http_message_uploading), output_bytes.length, output_offset);
					}

					while (!_stop && output_offset < output_bytes.length) {
						if (output_offset + output_len > output_bytes.length) {
							output_len = output_bytes.length - output_offset;
						}
						os.write(output_bytes, output_offset, output_len);
						os.flush();
						output_offset += output_len;

						if (_progress_ui != null) {
							_progress_ui.updateGauge(output_offset);
						}
					}
					os.close();
					os = null;
				}

				if (_progress_ui != null) {
					_progress_ui.resetGauge(_context.getString(R.string.http_message_waiting), 2);
				}

				_responseCode = _urlConnection.getResponseCode();
				System.out.println("responseCode is " + _responseCode);

				_responseMsg = _urlConnection.getResponseMessage();
				System.out.println("responseMsg is " + _responseMsg);
				
				// 200
				if (_responseCode == HttpURLConnection.HTTP_OK) {
					int readLength = 0;
					int length = _urlConnection.getContentLength();

					System.out.println("Content-length: " + length);

					if (_progress_ui != null) {
						_progress_ui.resetGauge(_context.getString(R.string.http_message_downloading), length, 0);
					}

					if (length > 0) {
						/*SortedMap<String, Charset> sys_charsets = Charset.availableCharsets();
						Set<String> charset_strs = sys_charsets.keySet();
						for(Iterator<String> iter = charset_strs.iterator(); iter.hasNext(); ) {
							System.out.println("Charset: " + iter.next());
						}*/
						
						is = _urlConnection.getInputStream();

						byte[] buffer = new byte[4096];
						int len = 0;
						StringBuffer tmp_result = new StringBuffer();

						if (dir == null || dir.length() == 0) {
							String _content_encoding = _urlConnection.getHeaderField("X-Anhe-Content-Encoding");
							System.out.println("X-Anhe-Content-Encoding=" + _content_encoding);

							if (_content_encoding != null && _content_encoding.equals("gzip")) {
								GZIPInputStream gzip = new GZIPInputStream(is);
								while (!_stop && (len = gzip.read(buffer)) != -1) {
									tmp_result.append(new String(buffer, 0, len, _default_charset.displayName()));
									readLength += len;
									if (_progress_ui != null) {
										_progress_ui.updateGauge(readLength);
									}
								}
								_iso_8859_1_result = tmp_result.toString();
								System.out.println("_result is " + _iso_8859_1_result);
								gzip.close();
								gzip = null;
							} else {
								while (!_stop && (len = is.read(buffer)) != -1) {
									tmp_result.append(new String(buffer, 0, len, _default_charset.displayName()));
									readLength += len;
									if (_progress_ui != null) {
										_progress_ui.updateGauge(readLength);
									}
								}
								_iso_8859_1_result = tmp_result.toString();
								System.out.println("result is " + _iso_8859_1_result);
							}
						} else {
							// 写本地文件
							System.out.println("Download file length " + length);
							int gauge_length = 0;
							FileUtilityClass file = new FileUtilityClass();
							if (!file.open(dir)) {
								System.out.println("open dir " + dir + " error!");
								return false;
							}

							while (!_stop && (len = is.read(buffer)) != -1) {
								file.write(buffer, 0, len);
								readLength += len;
								if (_progress_ui != null) {
									gauge_length += len;
									_progress_ui.updateGauge(gauge_length);
								}
							}

							// 是否增加实际读取长度的控制
							// if (!_stop&&readLength < length) {
							//								
							// }
							file.close();
							// _result = null;
						}
						is.close();
						is = null;
					}

					String anhe_result = _urlConnection.getHeaderField("X-Anhe-MAG-Result");
					if (anhe_result == null) {
						anhe_result = _urlConnection.getHeaderField("X-Anhe-Result");
					}

					if (dir == null || dir.length() == 0) {
						if (anhe_result != null && anhe_result.equals("TRUE")) {
							_content_type = _urlConnection.getHeaderField("Content-type");
							succ = true;
							System.out.println("Content-type: " + _urlConnection.getHeaderField("Content-Type"));
						}
					} else {
						// 写文件模式
						_content_type = _urlConnection.getHeaderField("Content-type");
						succ = true;
						System.out.println("Content-type: " + _urlConnection.getHeaderField("Content-Type"));
					}
					// 206
				} else if (_responseCode == HttpURLConnection.HTTP_PARTIAL) {

				} else {
					System.out.println("Error Status=" + _responseCode);
					_iso_8859_1_result += getBase64String("Error HTTP response code: " + _responseCode + "/" + _responseMsg);
					//Log.e("httphelper", "_result " + _result);
				}
			} else {
				_iso_8859_1_result += getBase64String("Cannot open HttpConnection...");
				//Log.e("httphelper", "_result " + _result);
			}
		} catch (SocketTimeoutException e) {
			_iso_8859_1_result += getBase64String("SocketTimeoutException " + e.toString());
			//LOG.error(this, "send", e);
		} catch (MalformedURLException e) {
			_iso_8859_1_result += getBase64String("MalformedURLException" + e.toString());
			//LOG.error(this, "send", e);
		} catch (IOException e) {
			_iso_8859_1_result += getBase64String("IOException: " + e.toString());
			//LOG.error(this, "send", e);
		} catch (Exception e) {
			_iso_8859_1_result += getBase64String("Unknown exception: " + e.toString());
			//LOG.error(this, "send " + _result, e);
		} finally {
			if (os != null) {
				try {
					os.close();
					os = null;
				} catch (IOException e) {
				}
			}
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
				}
			}
			if (_urlConnection != null) {
				_urlConnection.disconnect();
			}
		}
		return succ;
	}
	
	private static String getBase64String(String str) {
		return str; //Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
	}

	public void stop() {
		_stop = true;
		if (_urlConnection != null) {
			_urlConnection.disconnect();
		}
	}
	
	public String getISO8859Result() {
		return _iso_8859_1_result;
	}

	public String getUTF8Result() {
		byte[] result = _iso_8859_1_result.getBytes();
		try {
			String str = new String(result, "UTF-8");
			return str;
		}catch(final Exception e) {
			return null;
		}
	}

	public JSONObject getJSONResult() {
		JSONObject ret = null;
		String result = getUTF8Result();
		//System.out.println("UtilClass.cleanText(_result):" + result);
		if (result != null && result.length() > 2) {
			try {
				ret = new JSONObject(result);
			} catch (JSONException e) {
				System.out.println("getJSONResult()" + e.toString());
			}
		}
		return ret;
	}

	public String getResponseMessage() {
		return _responseMsg;
	}

	public String getContentType() {
		return _content_type;
	}

	public int getResponseCode() {
		return _responseCode;
	}

}

class CustomX509TrustManager implements X509TrustManager {
	private static TrustManager[] trustManagers;
	private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

	public boolean isClientTrusted(X509Certificate[] chain) {
		return true;
	}

	public boolean isServerTrusted(X509Certificate[] chain) {
		return true;
	}

	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	}

	public X509Certificate[] getAcceptedIssuers() {
		return _AcceptedIssuers;
	}

	public static void allowAllSSL() {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
		SSLContext context = null;
		if (trustManagers == null) {
			trustManagers = new TrustManager[] { new CustomX509TrustManager() };
		}
		try {
			context = SSLContext.getInstance("TLS");
			context.init(null, trustManagers, new SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	}

}
