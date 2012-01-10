/*
 * FileExplorer.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.file;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.anheinno.android.libs.R;
import com.anheinno.android.libs.graphics.Align;
import com.anheinno.android.libs.graphics.BitmapRepository;
import com.anheinno.android.libs.graphics.PaintRepository;
import com.anheinno.android.libs.graphics.Paragraph;
import com.anheinno.android.libs.ui.EasyDialog;
import com.anheinno.android.libs.ui.ModalScreen;
import com.anheinno.android.libs.ui.TreeView;



/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class FileExplorer extends ModalScreen {
	private DirTreeField _dir_tree;
	private boolean _dir_only;
	private boolean _writable;
	private String _dir;
	private FileExplorerListener _listener;
	
	public interface FileExplorerListener {
		void onFileSelected(String dir);
	}

	public FileExplorer(Context context, String initdir, boolean dironly, boolean writable, FileExplorerListener listener) {
		super(context);
		_dir = initdir;
		_dir_only = dironly;
		_writable = writable;
		_listener = listener;
	}

	protected void onCreate() {
		_dir_tree = new DirTreeField(getContext(), _dir_only);
		_dir_tree.setIndentWidth(16);
		_dir_tree.setDefaultExpanded(false);
		setContentView(_dir_tree);
		
		_dir_tree.initTree();
		_dir_tree.setCurrentDir(_dir);
	}

	private static Paragraph _paragraph;
	private static Paint _bg_paint;
	private static Paint _fg_paint;
	private static Paint _fg_focus_paint;
	private static final int PADDING = 3;
	static {
		_bg_paint = new Paint();
		_bg_paint.setColor(Color.BLUE);
		_bg_paint.setStyle(Paint.Style.FILL);
	}
	
	class FileNode {
		String _name;
		boolean _isdir;
		boolean _explored;
		int _nid;

		public FileNode(String name, boolean isdir) {
			_name = name;
			_isdir = isdir;
			_explored = false;
			_nid = 0;
			_paragraph = null;
		}
		
		public String toString() {
			return _name;
		}
		
		public void draw(Canvas canvas, float left, float top, float width, float height, boolean focus) {
			if(_paragraph == null) {
				_paragraph = new Paragraph(getContext(), "", 0);
				_paragraph.setLineCount(1);
			}
			_paragraph.setText(_name);
			_paragraph.setWidthBound((int)width);
			
			String icon_name;
			if (_isdir) {
				icon_name = "icon_folder.png";
			} else {
				icon_name = FileUtilityClass.getFileIcon(_name);
			}
			
			if(_fg_paint == null) {
				_fg_paint = PaintRepository.getFontPaint(getContext(), false, false, false, 1.0F, Color.BLACK, 255);
				_fg_focus_paint = PaintRepository.getFontPaint(getContext(), false, false, false, 1.0F, Color.WHITE, 255);
			}
			
			if(focus) {
				_paragraph.setPaint(_fg_focus_paint);
				canvas.drawRect(left, top, left + width, top + height, _bg_paint);
			}else {
				_paragraph.setPaint(_fg_paint);
			}
			
			Bitmap icon = BitmapRepository.getBitmapByName(getContext(), icon_name);
			canvas.drawBitmap(icon, left + PADDING, top + (height - icon.getHeight())/2, null);
			_paragraph.draw(canvas, left + icon.getWidth() + 2*PADDING, top + (height - _paragraph.getHeight())/2, Align.LEFT);
		}
	}

	class DirTreeField extends TreeView<FileNode> implements TreeView.TreeViewCallback<FileNode> {
		private boolean _dironly;
		//private static final String _prefix = "file:///";
		//DirTreeField _self;

		DirTreeField(Context context, boolean dironly) {
			super(context);
			setTreeViewCallback(this);
			_dironly = dironly;
			//_self = this;
		}
		
		String purifyDirName(String dir) {
			while (dir.startsWith("/")) {
				dir = dir.substring(1);
			}
			while (dir.endsWith("/")) {
				dir = dir.substring(0, dir.length() - 1);
			}
			return dir;
		}

		void addRootDir(File f) {
			String dirname = f.getAbsolutePath();
			dirname = purifyDirName(dirname);
			FileNode fn = new FileNode(dirname, true);
			System.out.println("Add root directory: " + dirname);
			fn._nid = addChildNode(0, fn, true);
			if(isValid(fn._nid)) {
				addChildFileNode(fn);
			}else {
				System.out.println("Failed to addChildNode!");
			}
		}
		
		void initTree() {
			File f = Environment.getExternalStorageDirectory();
			if(f != null) {
				addRootDir(f);
			}
			f = Environment.getDataDirectory();
			if(f != null) {
				addRootDir(f);
			}
			f = Environment.getRootDirectory();
			if(f != null) {
				addRootDir(f);
			}
		}

		public void setCurrentDir(String dir) {
			int nid = setCurrentDir_internal(dir, 0);
			if (nid > 0) {
				setCurrentNode(nid);
				System.out.println("setCurrentDir: " + getNodePathString(nid));
			}
		}

		private int setCurrentDir_internal(String dir, int pid) {
			if(dir != null) {
				dir = purifyDirName(dir);
			}
			if(dir == null || dir.length() == 0) {
				return pid;
			}
			int cid = getFirstChild(pid);
			int next_cid = INVALID_NID;
			int offset = 0;
			while(isValid(cid)) {
				FileNode fn = getCookie(cid);
				if (!fn._explored) {
					addChildFileNode(fn);
				}
				if(dir.startsWith(fn._name) && offset < fn._name.length()) {
					next_cid = cid;
					offset = fn._name.length();
				}
				cid = getNextSibling(cid);
			}
			if (next_cid != -1) {
				return setCurrentDir_internal(dir.substring(offset), next_cid);
			}
			return pid;
		}
				
		@Override
		protected void onExpand(int nid, FileNode data) {
			if (getFirstChild(nid) != -1) {
				int cnid = getFirstChild(nid);
				do {
					FileNode cn = (FileNode) getCookie(cnid);
					if (!cn._explored && cn._isdir) {
						addChildFileNode(cn);
					}
					cnid = getNextSibling(cnid);
				} while (cnid != -1);
				//setExpanded(nid, !getExpanded(nid));
			}
			super.onCollapse(nid, data);
		}
		
		@Override
		protected void onCollapse(int nid, FileNode data) {
			// do nothing
			super.onCollapse(nid, data);
		}
		
		@Override
		protected void onSelect (int nid, FileNode fn) {
			if ((_dironly && fn._isdir) || (!_dironly && !fn._isdir)) {
				String dir = getNodePathString(nid);
				File f = new File(dir);
				if(!_writable || f.canWrite()) {
					dismiss();
					if(_listener != null) {
						_listener.onFileSelected(dir);
					}
				}else {
					EasyDialog.longAlert(getContext(), dir + " " + getContext().getString(R.string.file_explorer_file_not_wriable));
				}
				System.out.println(dir);			
			}
		}

		/*public boolean onKeyUp (int keyCode, KeyEvent event) {
			if(keyCode == KeyEvent.KEYCODE_ENTER) {
				onSelect();
				return true;
			}else if(keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
				onExpand();
				return true;
			}
			return super.onKeyUp(keyCode, event);
		}*/

		/*public void newSubdir() {
			int nid = getCurrentNode();
			FileNode fn = (FileNode) getCookie(nid);
			if (fn._isdir) {
				TextInputDialog id = new TextInputDialog(_resources.getString(FILE_EXPLORER_NEW_DIR_PROMPT), "", TextInputDialog.FILTER_FILENAME);
				if (id.doModal() && id.getText().length() > 0) {
					String newdir = id.getText();
					String dir = getNodePathString(nid);
					if (FileUtilityClass.mkdir(_prefix + dir + newdir + "/")) {
						refreshChildFileNode(fn);
						setExpanded(fn._nid, true);
					}
				} else {
					UtilClass.showInfo(_resources.getString(FILE_EXPLORER_NEW_DIR_EMPTY_PROMPT));
				}
			} else {
				UtilClass.showInfo(_resources.getString(FILE_EXPLORER_NEW_DIR_NOTDIR));
			}
		}*/

		/*public void delDirectory() {
			int nid = getCurrentNode();
			FileNode fn = (FileNode) getCookie(nid);
			boolean done = false;
			String dir = getNodePathString(nid);
			if (fn._isdir) {
				if (FileUtilityClass.isEmptyDir(getContext(), _prefix + dir)) {
					if (FileUtilityClass.deleteFile(_prefix + dir)) {
						done = true;
					}
				} else {
					UtilClass.showInfo(_resources.getString(FILE_EXPLORER_CANNOT_DELETE_DIR));
				}
			} else {
				if (FileUtilityClass.deleteFile(_prefix + dir)) {
					done = true;
				}
			}
			if (done) {
				deleteSubtree(nid, true);
			}
		}*/

		void refreshChildFileNode(FileNode n) {
			int cid;
			while ((cid = getFirstChild(n._nid)) != -1) {
				deleteSubtree(cid, false);
			}
			addChildFileNode(n);
		}

		void addChildFileNode(FileNode n) {
			int pid = n._nid;
			String dir = getNodePathString(pid);
			System.out.println("current node: " + dir);
			File fconn = null;
			try {
				fconn = new File(dir);
				if (fconn.exists() && fconn.isDirectory()) {
					File[] sub_files = fconn.listFiles();
					for(int i = 0; sub_files != null && i < sub_files.length; i ++) {
						File sub = sub_files[i];
						if(!_dironly || sub.isDirectory()) {
							System.out.println("Add " + sub.getName());
							FileNode fn = new FileNode(purifyDirName(sub.getName()), sub.isDirectory());
							fn._nid = addChildNode(pid, fn, false);
						}
					}
				}
			} catch (final Exception ioe) {
				EasyDialog.remind(getContext(), ioe.toString());
			} finally {
				n._explored = true;
				postInvalidate();
			}
		}

		String getNodePathString(int nid) {
			System.out.println("getNodePathString: nid=" + nid);
			String dir = "";
			while (nid != 0) {
				FileNode n = getCookie(nid);
				if (dir.length() == 0) {
					dir = n._name;
					if (n._isdir) {
						dir = dir + "/";
					}
				} else {
					dir = n._name + "/" + dir;
				}
				nid = getParent(nid);
				System.out.println("getNodePathString: nid=" + nid);
			}
			return "/" + dir;
		}

		void reinitTree() {
			int nid = getCurrentNode();
			String dir = getNodePathString(nid);
			deleteAll();
			initTree();
			setCurrentDir(dir);
		}

		/*private MenuItem _mi_expand = new MenuItem(_resources.getString(FILE_EXPLORER_EXPAND), 0, 0) {
			public void run() {
				_self.onExpand();
			}
		};
		private MenuItem _mi_select = new MenuItem(_resources.getString(FILE_EXPLORER_SELECT), 1, 1) {
			public void run() {
				_self.onSelect();
			}
		};
		private MenuItem _mi_newdir = new MenuItem(_resources.getString(FILE_EXPLORER_NEW_DIRECTORY), 2, 2) {
			public void run() {
				_self.newSubdir();
			}
		};
		private MenuItem _mi_deldir = new MenuItem(_resources.getString(FILE_EXPLORER_DELETE), 3, 3) {
			public void run() {
				_self.delDirectory();
			}
		};
		private MenuItem _mi_showfile = new MenuItem(_resources.getString(FILE_EXPLORER_SHOW_FILE), 4, 4) {
			public void run() {
				_dironly = false;
				reinitTree();
			}
		};
		private MenuItem _mi_hidefile = new MenuItem(_resources.getString(FILE_EXPLORER_HIDE_FILE), 5, 5) {
			public void run() {
				_dironly = true;
				reinitTree();
			}
		};*/

		public boolean containsChild(int nid) {
			int cid = getFirstChild(nid);
			return !(cid == INVALID_NID);
		}

		/*protected void makeContextMenu(ContextMenu contextMenu) {
			int nid = getCurrentNode();
			FileNode fn = (FileNode) getCookie(nid);
			if (containsChild(nid)) {
				contextMenu.addItem(_mi_expand);
			}
			if ((_dironly && fn._isdir) || (!_dironly && !fn._isdir)) {
				contextMenu.addItem(_mi_select);
			}
			if (fn._isdir) {
				contextMenu.addItem(_mi_newdir);
			}
			if (!containsChild(nid)) {
				contextMenu.addItem(_mi_deldir);
			}
			if (_dironly) {
				contextMenu.addItem(_mi_showfile);
			} else {
				contextMenu.addItem(_mi_hidefile);
			}
		}*/

		public void drawTreeItem(Canvas canvas, int nid, float left, float top,
				float width, float height, FileNode fn, boolean focus) {
			System.out.println("draw " + fn);
			fn.draw(canvas, left, top, width, height, focus);
		}
	}
}
