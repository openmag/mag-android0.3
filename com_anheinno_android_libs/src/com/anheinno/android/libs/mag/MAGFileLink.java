/*
 * MAGFileLink.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

//import java.io.File;
//import java.net.URI;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import com.anheinno.android.libs.JSONBrowserCookieStore;
import com.anheinno.android.libs.UtilClass;
import com.anheinno.android.libs.attachment.AttachmentDownloadService;
import com.anheinno.android.libs.file.FileUtilityClass;
import com.anheinno.android.libs.ui.EasyDialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 和MAGLinkt类似，MAGFileLink为链接到一个文件下载请求的链接。点击MAGFileLink后，
 * MAG客户端将从MAG网关请求一个除了MAGML类型的文档以外的文件，如一个Microsoft Word文档。<br>
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
class MAGFileLink extends MAGComponent {
	//private MAGFileLink _self;
	private String _link;
	
	private String _download_path;
	private String _download_file;

	MAGFileLink() {
		super();
		_link = null;
		//_self = this;
		_download_path = null;
		_download_file = null;
	}

	public boolean fromJSON(JSONObject o) {
		try {
			if(!super.fromJSON(o)) {
				return false;
			}

			if (!checkMandatory(o, "_link")) {
				return false;
			}

			_link = getMAGDocument().getAbsoluteURL(o.getString("_link"));
			
			_download_path = MAGDocumentConfig.getDownloadDir(getContext());
			_download_file = _download_path + UtilClass.str2digest(_link);
			
			return true;
		} catch (JSONException e) {
		}
		return false;
	}

	public View initField(Context context) {

		OnClickListener btn_click = new OnClickListener() {
			public void onClick(View view) {
				System.out.println("onClick title: " + title() + " " + _link);

//				ImageButtonField bf = (ImageButtonField) getField();
				// bf.setText("downloading....");
				if (AttachmentDownloadService.getInstance().isDownloading(title(), _link)) {
					// AttachmentScreen.getInstance().pushScreen(UiApplication.getUiApplication());
					EasyDialog.shortAlert(getContext(), "Downloading...");
				} else {
					// String info =
					// Integer.toHexString(DeviceInfo.getDeviceId()) +
					// ";" + DeviceInfo.getDeviceName() + ";"
					// + DeviceInfo.getPlatformVersion() + ";" +
					// DeviceInfo.getSoftwareVersion() + ";" +
					// AppUtils.getModuleName() + ";"
					// + UtilClass.getIMSIString();

					Hashtable<String, String> cookie = JSONBrowserCookieStore.getCookies();
					// HttpHeaders header = new HttpHeaders(cookie);
					// header.addProperty("X-Anhe-Handheld-INFO", info);

					// 后台下载
					

					if (FileUtilityClass.fileExists(_download_file)) {
						System.out.println("fileExists open...... file type:" + AttachmentDownloadService.getInstance().getType(UtilClass.str2digest(_link)));
						try {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(Uri.parse("file://" + _download_file), AttachmentDownloadService.getInstance().getType(UtilClass.str2digest(_link)));
							getContext().startActivity(intent);
						} catch (Exception e) {
							EasyDialog.longAlert(getContext(), "系统无法打开此文件!");
						}
					} else {
						System.out.println("MAGFileLink getDownloadDir " + _download_path);
						AttachmentDownloadService.getInstance().registerDownloadTask(title(), _link, cookie, _download_path);
					}
				}
			}
		};
		MAGCustomButtonField ibf = new MAGCustomButtonField(context, this, btn_click) {
			@Override
			protected boolean isVisited() {
				if(FileUtilityClass.fileExists(_download_file)) {
					if(!AttachmentDownloadService.getInstance().isDownloading(title(), _link)) {
						return true;
					}
				}
				return false;
			}
			
			@Override
			public void onDraw(Canvas canvas) {
				super.onDraw(canvas);
				setVisited();
			}
		};
		return ibf;
	}

	public void updateField(View f) {
		MAGCustomButtonField button = (MAGCustomButtonField)f;
		button.updateStyle();
		
		/*button.setLabel(title());*/
		
		/*int w = style().getIWidth(getInnerWidth());
		//System.out.println(toString() + " prefered width: w=" + w);
		if (w > 0) {
			button.setPreferredWidth(w);
		}
		int h = style().getIHeight(getInnerHeight());
		System.out.println(toString() + " innerHeight=" + getInnerHeight() + " prefered height: h=" + h);
		if (h > 0) {
			button.setPreferredHeight(h);
		}*/
		
		/*TextStyleDescriptor style;
		style = style().getTextStyle();
		if(style != null) {
			button.setTextStyle(style);
		}
		style = style().getTextStyle("focus");
		if(style != null) {
			button.setFocusTextStyle(style);
		}
		
		BackgroundDescriptor bg_desc = style().getBodyBackground();
		if(bg_desc != null) {
			button.setBackground(bg_desc);
		}
		bg_desc = style().getFocusBodyBackground();
		if(bg_desc != null) {
			button.setFocusBackground(bg_desc);
		}
		
		button.commitLayoutChange();*/
	}

	public String getAttributeValue(String fieldname) {
		if (fieldname.equals("_link")) {
			return _link;
		} else {
			return super.getAttributeValue(fieldname);
		}
	}
	
	protected boolean setAttribute(String name, String value) {
		if (name.equals("link")) {
			_link = value;
			return true;
		}
		if (super.setAttribute(name, value)) {
			return true;
		}
		return false;
	}

	public void onShowUi() {

	}

}
