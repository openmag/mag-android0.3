package com.anheinno.android.libs.file;

/**
 * FileUtilityClass.java
 * 2011-4-4
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.os.Environment;

import com.anheinno.android.libs.log.LOG;

/**
 * @author shenrh
 * 
 */
public class FileUtilityClass {
	private RandomAccessFile _accessor;
//	private int _filesize;

	public static String getFileIcon(String fname) {
		String fn = fname.toLowerCase();
		String icon = "icon_unknown.png";
		if (fn.endsWith(".doc")) {
			icon = "icon_doc.png";
		} else if (fn.endsWith(".ppt")) {
			icon = "icon_ppt.png";
		} else if (fn.endsWith(".pdf")) {
			icon = "icon_pdf.png";
		} else if (fn.endsWith(".txt")) {
			icon = "icon_txt.png";
		} else if (fn.endsWith(".xls")) {
			icon = "icon_xsl.png";
		}
		return icon;
	}
	
	public static boolean deleteFile(String dir) {
		boolean succ = false;

		File file = new File(dir);
		file.delete();

		return succ;
	}

	public static String open_read(String dir) {
		System.out.println("read file " + dir);
		// if (Environment.getExternalStorageState() ==
		// Environment.MEDIA_MOUNTED)// sdcard可用
		// Environment.getExternalStorageDirectory();

		String data = null;
		try {
			File file = new File(dir);
			FileInputStream is = new FileInputStream(file);
			byte[] tempdata = new byte[is.available()];

			is.read(tempdata);

			is.close();
			data = new String(tempdata);
		} catch (FileNotFoundException e) {
			LOG.error("FileUtilityClass", "open_read", e);
			// e.printStackTrace();
		} catch (IOException e) {
			LOG.error("FileUtilityClass", "open_read", e);
			// e.printStackTrace();
		}
		return data;
	}

	public static void open_write(String data, String dir) {
		System.out.println("write file " + dir);
		try {
			mkdir(dirName(dir));
			//File _log_dir = new File(dirName(dir));
			/*if (_log_dir != null) {
				mkdirs(_log_dir);
			}*/

			File file = new File(dir);
			// String command = "chmod 777 " + file.getAbsolutePath();
			// Runtime runtime = Runtime.getRuntime();
			// runtime.exec(command);

			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			FileOutputStream os = new FileOutputStream(file);

			os.write(data.getBytes());
			os.flush();

			os.close();
			os = null;
		} catch (FileNotFoundException e) {
			LOG.error("FileUtilityClass", "open_write", e);
			// e.printStackTrace();
		} catch (IOException e) {
			LOG.error("FileUtilityClass", "open_write", e);
			// e.printStackTrace();
		}
	}

	/**
	 * @param data
	 * @param dir
	 * 
	 *            如果文件已存在则从文件末尾开始写
	 */
	public static void open_write_end(String data, String dir) {
		try {
			/*File _log_dir = new File(dirName(dir));
			if (_log_dir != null) {
				mkdirs(_log_dir);
			}*/
			mkdir(dirName(dir));

			File log_conn = new File(dir);
			if (!log_conn.exists()) {
				log_conn.createNewFile();
			}
			RandomAccessFile accessor = new RandomAccessFile(log_conn, "rw");
			accessor.seek(accessor.length());
			accessor.write(data.getBytes());
			accessor.close();
			accessor = null;
		} catch (FileNotFoundException e) {
			LOG.error("FileUtilityClass", "open_write_end", e);
			// e.printStackTrace();
		} catch (IOException e) {
			LOG.error("FileUtilityClass", "open_write_end", e);
			// e.printStackTrace();
		}
	}

	/*private static boolean mkdirs(File file) {
		// 根目录sdcard存在
		if (!file.exists()) {
			File parent = file.getParentFile();
			if (parent != null) {
				// 有父目录
				if (!parent.exists()) {
					mkdirs(parent);
				}
				// LOG.info("FileUtilityClass", "mkdirs " +
				// file.getAbsolutePath());
				file.mkdirs();
			}
		}
	}*/

	public static boolean mkdir(String dir) {
		int offset = 1;
		if(dir == null || dir.length() == 0) {
			return false;
		}
		if(!dir.startsWith("/")) {
			dir = "/" + dir;
		}
		if(!dir.endsWith("/")) {
			dir = dir + "/";
		}
		if(pathExists(dir, true)) {
			return true;
		}
		while((offset = dir.indexOf("/", offset)) >= 0) {
			String parent_dir = dir.substring(0, offset);
			try {
				File p_f = new File(parent_dir);
				if(!p_f.exists()) {
					if(!p_f.mkdirs()) {
						LOG.error("FileUtilityClass", "mkdirs " + parent_dir + " fails!", null);
						return false;
					}
				}
			}catch(final Exception e) {
				LOG.error("FileUtilityClass", "mkdirs " + parent_dir + " fails!!!", e);
				return false;
			}
			offset += 1;
		}
		return true;
	}

	public static String dirName(String path) {
		if (!path.startsWith("/")) {
			path = '/' + path;
		}
		int end = path.lastIndexOf('/');
		return path.substring(0, end + 1);
	}

	public static String fileName(String path) {
		int end = path.lastIndexOf('/');
		return path.substring(end + 1);
	}

	public static boolean isDir(String path) {
		return pathExists(path, true);
	}

	public static boolean fileExists(String path) {
		return pathExists(path, false);
	}

	public static String cleanPath(String path) {
		return dirName(path) + fileName(path);
	}

	private static boolean pathExists(String path, boolean isDir) {
		path = cleanPath(path);
		boolean ret = false;
		File file = null;
		try {
			file = new File(path);
			if (file.exists()) {
				if (!isDir || file.isDirectory()) {
					ret = true;
				}
			}
		} catch (Exception e) {
			LOG.error(FileUtilityClass.class, "pathExists", e);
		}
		return ret;
	}

	// 以下三个方法用于打开一次持续写入的情况
	public boolean open(String dir) {
		return open(dir, 0);
	}

	public boolean open(String dir, int startSize) {
		try {
			File _log_dir = new File(dirName(dir));
			if (!_log_dir.exists()) {
				_log_dir.mkdir();
			}

			File log_conn = new File(dir);
			if (!log_conn.exists()) {
				log_conn.createNewFile();
			}
//			_filesize = 0;
			_accessor = new RandomAccessFile(log_conn, "rw");
			_accessor.seek(startSize);

			return true;
		} catch (FileNotFoundException e) {
			LOG.error("FileUtilityClass", "open " + dir, e);
			// e.printStackTrace();
		} catch (IOException e) {
			LOG.error("FileUtilityClass", "open" + dir, e);
			// e.printStackTrace();
		}
		return false;
	}

	public void write(byte[] data,int offset,int length) {
		if (_accessor != null) {
			try {
//				_filesize += length;
				_accessor.write(data,offset,length);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		if (_accessor != null) {
			try {
				_accessor.close();
				_accessor = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static long getSize(String dir) {
		long size = -1;
		File fconn = null;
		try {
			fconn = new File(dir);
			if (fconn.exists()) {
				if (fconn.isDirectory()) {
					String[] subdirs = fconn.list();
					size = 0L;
					for (int i = 0; i < subdirs.length; i++) {
						size += getSize(dir + "/" + subdirs[i]);
					}
				} else {
					size = fconn.length();
				}
			}
		} catch (Exception e) {
			LOG.error("FileUtilityClass", "getSize", e);
			// UtilClass.showInfo(e.getMessage());
		}
		return size;
	}
}
