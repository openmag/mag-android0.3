/**
 * AttachmentDownloadActivity.java
 * 2011-6-13
 */
package com.anheinno.android.libs.attachment;

import java.util.Vector;
import android.app.Activity;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * @author shenrh
 * 
 */
public class AttachmentDownloadActivity extends Activity {
	private LinearLayout _layout;
	private Handler _handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ScrollView scrollview = new ScrollView(this);

		_layout = new LinearLayout(this);

		_layout.setOrientation(LinearLayout.VERTICAL);

		scrollview.setBackgroundColor(Color.WHITE);
		scrollview.setScrollbarFadingEnabled(true);

		scrollview.addView(_layout);

		this.setContentView(scrollview);

		_handler = new Handler();
		init();
	}

	private void init() {
		Vector<View> _download_view = AttachmentDownloadService.getInstance().getDownloadView();

		if (_download_view != null) {
			System.out.println("AttachmentDownloadActivity add " + _download_view);
			int size = _download_view.size();
			for (int i = 0; i < size; i++) {
				_layout.addView(_download_view.elementAt(i));
			}
		}
	}

	@Override
	protected void onDestroy() {
		_layout.removeAllViews();
		super.onDestroy();
	}

	public void invokeLater(Runnable run) {
		_handler.post(run);
	}

	/**
	 * 用来打开查看下载进度，下载完成后clean
	 */
	private void cleanNotify() {
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(AttachmentDownloadService.NOTIFICATION_ID);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			System.out.println("AttachmentDownloadActivity onTouchEvent ACTION_DOWN......");
		
		}
		return super.onTouchEvent(event);
	}

}
