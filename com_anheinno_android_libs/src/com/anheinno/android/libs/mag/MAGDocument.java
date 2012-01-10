/*
 * CNAFOADocViewScreen.java
 *
 * Anhe Innovation Technology, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import java.util.Hashtable;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anheinno.android.libs.HTTPRequestString;
import com.anheinno.android.libs.JSONBrowserLink;
import com.anheinno.android.libs.JSONObjectCacheDatabase;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.TextStyleDescriptor;
import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.ui.EasyDialog;
import com.anheinno.android.libs.ui.FullScreen;
import com.anheinno.android.libs.ui.ScreenMenuItem;

import android.content.Context;
import android.view.View;

/**
 * MAGDocument为一个容器组件，代表一个MAGML文档。一个MAGML文档对应BlackBerry应用程序的一个全屏显示的窗口。<br>
 * TYPE = "DOCUMENT"
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGDocument extends MAGContainerBase {

	private String _scripts;
	private Vector<String> _cached_urls;
	private Hashtable<String, MAGStyle> _style_table;
	private Vector<ScreenMenuItem> _menu_list;
	//private String _current_url;

	private MAGDocument() {
		super();
		_scripts = null;
		_cached_urls = null;
		_style_table = null;
	}
	
	public MAGDocument(Context context, MAGDocumentField field) {
		this();
		setContext(context);
		setField(field);
	}

	public boolean fromJSON(JSONObject o) {
		try {

			if (o.has("_style_tbl")) {
				JSONArray arr = o.getJSONArray("_style_tbl");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject style_obj = arr.getJSONObject(i);
					if (style_obj.has("_name") && style_obj.has("_style")) {
						String name = style_obj.getString("_name");
						MAGStyle style = new MAGStyle();
						style.put(style_obj.getJSONObject("_style"));
						if (_style_table == null) {
							_style_table = new Hashtable<String, MAGStyle>();
						}
						_style_table.put(name, style);
						System.out.println("XXXX Reg class " + name);
					}
				}
				/*
				 * if (o.has("_class")) { _style =
				 * getStyle(o.getString("_class")); }
				 */
			}

			return super.fromJSON(o);

		} catch (final JSONException e) {
			LOG.error(this, "fromJSON", e);
		}
		return false;
	}

	public MAGStyle getStyle(String name) {
		if (_style_table != null && _style_table.containsKey(name)) {
			MAGStyle style = (MAGStyle) _style_table.get(name);
			// System.out.println("Get style " + name + ": " +
			// style.toString());
			return style;
		} else {
			return null;
		}
	}

	// 2010-5-27 增加toJSON
	public JSONObject toJSON() {
		JSONObject obj = super.toJSON();
		return obj;
	}

	public void addCachedLink(String url) {
		if (_cached_urls == null) {
			_cached_urls = new Vector<String>();
		}
		_cached_urls.addElement(HTTPRequestString.purify(url));
	}

	public int getCachedLinkCount() {
		if (_cached_urls == null) {
			return 0;
		} else {
			return _cached_urls.size();
		}
	}

	protected void addMenuItem(ScreenMenuItem menu) {
		if (_menu_list == null) {
			_menu_list = new Vector<ScreenMenuItem>();
		}
		_menu_list.addElement(menu);
	}

	public Vector<ScreenMenuItem> getMenuList() {
		return _menu_list;
	}

	public String getCachedLinkAt(int idx) {
		if (_cached_urls != null && idx >= 0 && idx < _cached_urls.size()) {
			return (String) _cached_urls.elementAt(idx);
		} else {
			return null;
		}
	}

	public void registScripts(String s) {
		System.out.println("registScripts " + s);
		if (_scripts == null) {
			_scripts = "";
		}
		_scripts += s;
	}

	public void execScripts() {
		System.out.println("Start execScripts");
		MAGScriptCommand[] cmds = MAGScriptCommand.parseScripts(_scripts);
		for (int i = 0; i < cmds.length; i++) {
			if (!execScriptCommand(cmds[i])) {
				break;
			}
		}
	}
	
	public MAGDocumentField getMAGDocumentField() {
		return (MAGDocumentField)getField();
	}
	
	public String getURL() {
		MAGDocumentField field = getMAGDocumentField();
		if(field != null) {
			return field.getURL();
		}else {
			return null;
		}
	}

	protected String getAbsoluteURL(String url) {
		MAGDocumentField field = getMAGDocumentField();
		if(field != null) {
			return field.getAbsoluteURL(url);
		}else {
			return null;
		}
	}

	public boolean execScriptCommand(MAGScriptCommand cmd) {
		System.out.println("Exec " + cmd.toString() + " #Params: " + cmd.getParameterNum());
		String obj = cmd.getFirstObject();
		if (obj != null) {
			if (obj.equals("this")) {
				String func = cmd.getMethodName();
				if (func.equals("close")) {
					getMAGDocumentField().unloadContainer();
					return true;
				} else if (func.equals("alert")) {
					EasyDialog.remind(getContext(), cmd.getParameter(0));
					return true;
				} else if (func.equals("open") || func.equals("popup")) {
					JSONBrowserLink link = new JSONBrowserLink(getContext());
					
					boolean refresh = false;
					if (cmd.getParameterNum() >= 1) {
						MAGDocumentField field = getMAGDocumentField();
						if(field != null) {
							link.setURL(getMAGDocumentField().getAbsoluteURL(cmd.getParameter(0)));
							if(link.isValidURL()) {
								if (cmd.getParameterNum() >= 2) {
									refresh = cmd.getParameter(1).equals("true");
								}
								if (cmd.getParameterNum() >= 3) {
									link.setExpireMilliseconds(Long.parseLong(cmd.getParameter(2)));
								}
								if (cmd.getParameterNum() >= 4) {
									link.setSaveHistory(cmd.getParameter(3).equals("true"));
								}
								if (cmd.getParameterNum() >= 5) {
									link.setNotify(cmd.getParameter(4).equals("true"));
								}
								
								if (func.equals("open")) {
									getMAGDocumentField().open(link, refresh, null);
								} else {
									MAGDocumentScreen screen = new MAGDocumentScreen(getContext(), link, refresh, getMAGDocumentScreen());
									screen.show();
								}
							}
						}
					}
					return true;
				} else if (func.equals("refresh")) {
					getMAGDocumentField().refresh();
					return true;
				} else if (func.equals("back")) {
					// boolean cache = true;
					boolean refresh = false;
					// boolean save = true;
					if (cmd.getParameterNum() >= 1) {
						refresh = cmd.getParameter(0).equals("true");
					}
					/*
					 * if (cmd.getParameterNum() >= 2) { cache =
					 * cmd.getParameter(1).equals("true"); } if
					 * (cmd.getParameterNum() >= 3) { save =
					 * cmd.getParameter(2).equals("true"); }
					 */
					getMAGDocumentField().back(refresh);
					return true;
				} else if (func.equals("invalidate")) {
					if (cmd.getParameterNum() >= 1) {
						String url = cmd.getParameter(0);
						url = getAbsoluteURL(url);
						System.out.println("invalidate URL: " + url);
						JSONObjectCacheDatabase.invalidateObject(getContext(), url);
					}
					return true;
				}
			} else if (obj.equals("parent")) {
				MAGScriptCommand newcmd = cmd.getNextLevel();
				if (newcmd != null) {
					MAGDocument parent_doc = getParentMAGDocument();
					if (parent_doc == null) {
						EasyDialog.remind(getContext(), "There is no parent!");
					} else if (parent_doc.execScriptCommand(newcmd)) {
						return true;
					}
				}
			}
		}
		return super.execScriptCommand(cmd);
	}
	
	private MAGDocument getParentMAGDocument() {
		if(getParent() == null) {
			// this is the top most MAGDocument
			MAGDocumentScreen screen = getMAGDocumentScreen();
			return screen.getMAGParentDocument();
		}else {
			MAGDocument doc = getParent().getMAGDocument();
			return doc;
		}
	}

	public void updateField(View f) {
		if (f == null) {
			return;
		}
		super.updateField(f);

		BackgroundDescriptor bg = style().getBackground();
		if (bg != null) {
			getMAGDocumentField().setBackground(bg);
		}

		if(getTopMAGDocument() == this) {
			MAGDocumentScreen screen = getMAGDocumentScreen();
			
			screen.setTitleText(title());
	
			bg = style().getTitleBackground();
			if (bg != null) {
				screen.setTitleBackground(bg);
			}
	
			TextStyleDescriptor ts = style().getTitleTextStyle();
			if (ts != null) {
				screen.setTitleTextStyle(ts);
			}
	
			if (style().getInt("title-height") > 0) {
				screen.setTitleHeight(style().getInt("title-height"));
			}
			
			//if (style().get("menu_bar") != null && style().get("menu_bar").equals("enable")) {
			//	screen.enableMenuBar(4);
			//}
			
			getMAGDocumentScreen().invalidateMenu();

			screen.commitLayoutChange();
		}
	}

	public View initField(Context context) {
		BackgroundDescriptor bg = style().getBackground();
		if (bg != null) {
			getMAGDocumentField().setBackground(bg);
		} else {
			getMAGDocumentField().removeBackground();
		}

		MAGContainerBase.initChildFields(this);

		return getMAGDocumentField();
	}

	public void initUi() {
		updateField(getMAGDocumentField());
		execMAGScript();
	}

	protected void execMAGScript() {
		if (_scripts != null && _scripts.length() > 0) {
			System.out.println("ExecScript...");
			FullScreen screen = FullScreen.getFullScreen(getField());
			if(screen != null) {
				screen.getUiApplication().invokeLater(new Runnable() {
					public void run() {
						execScripts();
					}
				});
			}
		}
	}

	public static boolean updateInfectedMAGDocuments(Context context, String root, String url) {
		String root_str = "_root_";
		Vector<String> stack = new Vector<String>();
		Hashtable<String, String> visited = new Hashtable<String, String>();
		stack.addElement(root);
		visited.put(root, root_str);
		while (stack.size() > 0 && !visited.containsKey(url)) {
			String cur_url = (String) stack.lastElement();
			stack.removeElementAt(stack.size() - 1);
			JSONObject o = JSONObjectCacheDatabase.getObject(context, cur_url, false);
			if (o != null) {
				MAGDocument doc = new MAGDocument(context, null);
				doc.fromJSON(o);
				for (int i = 0; i < doc.getCachedLinkCount(); i++) {
					String new_url = doc.getCachedLinkAt(i);
					visited.put(new_url, cur_url);
					System.out.println("Check Link: " + url);
					System.out.println("Child Link: " + new_url);
					if (url.equals(new_url)) {
						System.out.println("Find the link!");
						break;
					} else {
						stack.addElement(new_url);
					}
				}
			}
		}
		if (visited.containsKey(url)) {
			System.out.println("Find the link, update from " + root_str + " to " + url);
			while (!root_str.equals(visited.get(url))) {
				url = (String) visited.get(url);
				JSONObjectCacheDatabase.recursiveUpdate(context, url);
			}
			return true;
		} else {
			return false;
		}
	}
	
	public void releaseResources() {
		_scripts = null;
		
		if(_cached_urls != null) {
			_cached_urls.clear();
			_cached_urls = null;
		}
		if(_style_table != null) {
			_style_table.clear();
			_style_table = null;
		}
		if(_menu_list != null) {
			_menu_list.clear();
			_menu_list = null;
		}
		
		super.releaseResources();
	}

}
