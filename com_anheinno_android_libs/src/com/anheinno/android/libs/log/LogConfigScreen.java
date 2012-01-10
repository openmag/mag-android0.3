package com.anheinno.android.libs.log;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.anheinno.android.libs.R;
import com.anheinno.android.libs.file.FileExplorer;
import com.anheinno.android.libs.file.FileExplorer.FileExplorerListener;
import com.anheinno.android.libs.file.FileUtilityClass;
import com.anheinno.android.libs.ui.ModalScreen;

public class LogConfigScreen extends ModalScreen {

	public LogConfigScreen(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	protected void initConfig() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.log_config_dialog, null);
		setContentView(view);
		
		updateLogDirInfo();
		
		CheckBox cb_log_error = (CheckBox)findViewById(R.id.cb_log_error);
		CheckBox cb_log_warning = (CheckBox)findViewById(R.id.cb_log_warning);
		CheckBox cb_log_trace = (CheckBox)findViewById(R.id.cb_log_trace);
		CheckBox cb_log_info = (CheckBox)findViewById(R.id.cb_log_info);
		CheckBox cb_log_debug = (CheckBox)findViewById(R.id.cb_log_debug);
		
		Button bt_choose_log_dir = (Button) findViewById(R.id.bt_choose_log_dir);
		bt_choose_log_dir.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				FileExplorerListener listener = new FileExplorerListener() {
					public void onFileSelected(String dir) {
						LogConfig.setLogDir(getContext(), dir);
						updateLogDirInfo();
					}
				};
				FileExplorer explorer = new FileExplorer(getContext(), LogConfig.getLogDir(getContext()), true, true, listener);
				explorer.show();
			}
		});
	}
	
	private void updateLogDirInfo() {
		TextView tv_log_dir = (TextView)findViewById(R.id.tv_log_dir);
		tv_log_dir.setText(getContext().getString(R.string.log_config_dialog_log_dir_size) + FileUtilityClass.getSize(LogConfig.getLogDir(getContext()))
				+ "\n"
				+ getContext().getString(R.string.log_config_dialog_log_directory));		
	
		Button bt_choose_log_dir = (Button) findViewById(R.id.bt_choose_log_dir);
		bt_choose_log_dir.setText(LogConfig.getLogDir(getContext()));
	}

	protected void onCreate() {
		initConfig();
	}

	protected boolean onClose() {
		CheckBox cb_log_error = (CheckBox)findViewById(R.id.cb_log_error);
		CheckBox cb_log_warning = (CheckBox)findViewById(R.id.cb_log_warning);
		CheckBox cb_log_trace = (CheckBox)findViewById(R.id.cb_log_trace);
		CheckBox cb_log_info = (CheckBox)findViewById(R.id.cb_log_info);
		CheckBox cb_log_debug = (CheckBox)findViewById(R.id.cb_log_debug);

		return true;
	}
	
}
