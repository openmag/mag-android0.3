/*
 * CNAFOAContactScreen.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.libs.mag.controls.contacts;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;

import com.anheinno.android.libs.DownloadManager;
import com.anheinno.android.libs.HTTPRequestString;
import com.anheinno.android.libs.JSONBrowserConfigScreen;
import com.anheinno.android.libs.JSONBrowserLink;
import com.anheinno.android.libs.PreferencesStore;
import com.anheinno.android.libs.graphics.Align;
import com.anheinno.android.libs.graphics.BackgroundDescriptor;
import com.anheinno.android.libs.graphics.BitmapRepository;
import com.anheinno.android.libs.graphics.PaintRepository;
import com.anheinno.android.libs.graphics.Paragraph;
import com.anheinno.android.libs.log.LOG;
import com.anheinno.android.libs.ui.ListField;
import com.anheinno.android.libs.ui.TreeView;
import com.anheinno.android.libs.ui.ListField.ListFieldCallback;
import com.anheinno.android.libs.ui.TreeView.TreeViewCallback;



/**
 * 
 */
public class ContactScreen extends JSONSearchBrowserScreen implements TreeViewCallback<Object>, ListFieldCallback<ContactUser> {

	private static final long ADDRESS_BOOK_EXPIRE = 10 * 365 * 24 * 3600 * 1000L;

	private TreeView<Object> _group_tree;

	private boolean _search_mode;
	private ListField<ContactUser> _search_field;
	private Vector<ContactUser> _search_result;

	private String[] _user_url;
	
	private String _init_org_name;
	private String _init_account;
	
	private static final String USER_ICON = "user_small.png";
	private static final String DEPT_ICON = "icon_dept.png";
		
	private ContactScreenListener _listener;

	public interface ContactScreenListener {
		public void onSelectUser(ContactUser user);
	}

	ContactScreen(Context context, String[] url) {
		// url[0]部门组信息，url[1]员工信息，url[2]要搜索的员工
		super(context);
		initScreen(url);
		//open(url[0], ADDRESS_BOOK_EXPIRE, false, false, false, null);
	}

	public ContactScreen(Context context) {
		super(context);
		String url = PreferencesStore.getString(getContext(), ContactScreenConfig.CONTACT_SERVICE_URL);
		if (url != null && url.length() > 0) {
			String[] urls = new String[3];
			urls[0] = url + "?_action=GROUPDIR";
			urls[1] = url + "?_action=USERLIST";
			urls[2] = url + "?_action=SEARCH";
			initScreen(urls);
		}
	}

	private void initScreen(String[] url) {
		super.getEmbeddedJSONBrowserField().setShowRefreshMenu(false);
		
		this.setBackground(new BackgroundDescriptor("color=white"));
		
		_search_mode = false;
		_search_field = null;
		_search_result = null;
		
		_user_url = url;

		_init_org_name = null;
		_init_account = null;
		_listener = null;

		_group_tree = new TreeView<Object>(getContext()) {
			protected void onSelect (int nid, Object data) {
				selectNode(nid, data);
			}	
		};

		_group_tree.setTreeViewCallback(this);
		_group_tree.setDefaultExpanded(false);
		_group_tree.setIndentWidth(16);
		getEmbeddedJSONBrowserField().setContent(_group_tree);
		
		_group_tree.requestFocus();

		JSONBrowserLink link = new JSONBrowserLink(getContext());
		link.setURL(_user_url[0]);
		link.setExpireMilliseconds(ADDRESS_BOOK_EXPIRE);
		link.setNotify(false);
		link.setSaveHistory(false);
		getEmbeddedJSONBrowserField().open(link, false, null);
	}

	public void show(String orgName, String account, ContactScreenListener listener) {
		_listener = listener;
		_init_org_name = orgName;
		_init_account = account;
		System.out.println("_orgname: " + _init_org_name + " _account: " + _init_account);
		super.show();
	}

	private void requestUserList(int org_nid) {
		ContactOrganization organ = (ContactOrganization) _group_tree.getCookie(org_nid);
		if (organ != null && !organ._visited) {
			if (_user_url.length > 1) {
				String url = _user_url[1] + "&" + HTTPRequestString.getQueryString("_orgname", organ._name, true);
				JSONBrowserLink link = new JSONBrowserLink(getContext());
				link.setURL(url);
				link.setExpireMilliseconds(ADDRESS_BOOK_EXPIRE);
				link.setNotify(false);
				link.setSaveHistory(false);
				getEmbeddedJSONBrowserField().open(link, false, "" + org_nid);
			}
		} else {
			return;
		}
	}

	public void expandNode(int nid, Object obj) {
		if (obj instanceof ContactOrganization) {
			requestUserList(nid);
		}
	}
	
	private void selectNode(int nid, Object obj) {
		if (obj instanceof ContactOrganization) {
			requestUserList(nid);
			//_groupTree.setExpanded(nid, !_groupTree.getExpanded(nid));
		} else {
			ContactUser user = (ContactUser) obj;
			selectUser(user);
		}
	}
	
	private void selectUser(ContactUser user) {
		if (_listener != null) {
			_listener.onSelectUser(user);
			close();
		} else {
			UserInfoScreen sc = new UserInfoScreen(getContext(), user);
			sc.show();
		}
	}

	public void showJSON(JSONObject o, Object params) {
		try {
			if(_search_mode) {
				JSONArray result = o.getJSONArray("data");
				if(_search_result == null) {
					_search_result = new Vector<ContactUser>();
				}else {
					_search_result.removeAllElements();
				}
				for(int i = 0; i < result.length(); i ++) {
					_search_result.addElement(new ContactUser(result.getJSONObject(i), null));
				}
				getUiApplication().invokeLater(new Runnable() {
					public void run() {
						populateSearchList();						
					}
				});
			}else {
				if (params == null) {
					JSONArray groupdir = o.getJSONArray("data");
					saveGroupData(groupdir);
				} else if (params instanceof String) {
					JSONArray userlist = o.getJSONArray("data");
					getUiApplication().invokeLater(new populateUserListTask(userlist, Integer.parseInt((String) params)));
				}
			}
		} catch (final JSONException e) {
			LOG.error(this, "showJSON", e);
		}
	}

	private ContactOrganization _root;
	
	private void saveGroupData(JSONArray array) {
		if (array.length() <= 0) {
			return;
		}
		try {
			ContactOrganization first = new ContactOrganization(array.getJSONObject(0));
			_root = new ContactOrganization(first.getRootName());
			for (int i = 0; i < array.length(); i++) {
				//System.out.println("Add group " + array.getJSONObject(i));
				ContactOrganization organ = new ContactOrganization(array.getJSONObject(i));
				_root.addChild(organ);
			}
			getUiApplication().invokeLater(new Runnable() {
				public void run() {
					populateGroupTree(_root, 0);
					
					if (_init_org_name != null && _init_org_name.length() > 0 && _init_account != null && _init_account.length() > 0) {
						selectAccount(_init_org_name, _init_account);
					} else {
						selectAccount(_root._name, null);
					}
					
					_group_tree.requestLayout();	
				}
			});

		} catch (final Exception e) {
			//EasyDialog.remind(getContext(), e.toString());
			LOG.error(this, "saveGroupData", e);
		}
	}

	private void populateGroupTree(ContactOrganization node, int pid) {
		int nid = _group_tree.addChildNode(pid, node, false);
		if (node._children != null) {
			for (int i = 0; i < node._children.size(); i++) {
				ContactOrganization subn = (ContactOrganization) node._children.elementAt(i);
				populateGroupTree(subn, nid);
			}
		}
	}

	private void selectAccount(String oname, String acc) {
		selectAccount_internal(0, oname, acc);
	}

	private boolean selectAccount_internal(int pid, String oname, String acc) {
		int cid = _group_tree.getFirstChild(pid);
		while (cid != -1) {
			Object obj = _group_tree.getCookie(cid);
			if (obj instanceof ContactOrganization) {
				ContactOrganization organ = (ContactOrganization) obj;
				System.out.println("org: " + organ._name + " oname=" + oname);
				if (organ._name.equals(oname)) {
					requestUserList(cid);
					return true;
				} else if (oname.startsWith(organ._name)) {
					if (selectAccount_internal(cid, oname, acc)) {
						return true;
					}
				}
			}
			cid = _group_tree.getNextSibling(cid);
		}
		return false;
	}

	class populateUserListTask implements Runnable {
		private JSONArray _array;
		private int _nid;
		
		populateUserListTask(JSONArray array, int nid) {
			_array = array;
			_nid = nid;
		}
		
		public void run() {
			populateUserList(_array, _nid);
		}
	}
	
	public synchronized void populateUserList(JSONArray array, int nid) {
		try {
			ContactOrganization organ = (ContactOrganization) _group_tree.getCookie(nid);
			if (organ != null && organ._visited == false) {
				organ._visited = true;
				int cur_id = -1;
				if (array != null) {
					for (int i = 0; i < array.length(); i++) {
						ContactUser u = new ContactUser(array.getJSONObject(i), organ._name);
						int cid = _group_tree.addChildNode(nid, u, false);
						if (_init_account != null && u.get_account().equals(_init_account)) {
							cur_id = cid;
						}
					}
				}
				if (cur_id != -1) {
					_group_tree.setCurrentNode(cur_id);
				}else {
					_group_tree.setExpanded(nid, true);
				}
			}
		} catch (final Exception e) {
			LOG.error(this, "populateUserList", e);
		}
	}

	class ContactOrganization {
		String _name;
		Vector<Object> _children;
		boolean _visited;

		ContactOrganization(JSONObject o) {
			super();
			fromJSON(o);
			_children = null;
			_visited = false;
		}

		ContactOrganization(String name) {
			super();
			_name = name;
			_children = null;
			_visited = false;
		}

		public void fromJSON(JSONObject o) {
			try {
				_name = o.getString("_g");
			} catch (JSONException e) {
			}
		}

		public String getRootName() {
			return getLayeredName(0);
		}

		private void addChild(ContactOrganization ch) {
			if (ch._name.startsWith(_name + '/')) {
				if (ch._name.substring(_name.length() + 1).indexOf('/') < 0) {
					// leaf node
					if (_children == null) {
						_children = new Vector<Object>();
					}
					_children.addElement(ch);
				} else {
					// branch node
					int i = 0;
					for (i = 0; _children != null && i < _children.size(); i++) {
						ContactOrganization subo = (ContactOrganization) _children.elementAt(i);
						if (ch._name.startsWith(subo._name + '/')) {
							subo.addChild(ch);
							break;
						}
					}
					if (_children == null || i >= _children.size()) {
						ContactOrganization subo = new ContactOrganization(ch.getLayeredName(getLayer() + 1));
						if (_children == null) {
							_children = new Vector<Object>();
						}
						_children.addElement(subo);
						subo.addChild(ch);
					}
				}
			}
		}

		public String getName() {
			if (_name.indexOf('/') > 0) {
				return _name.substring(_name.lastIndexOf('/') + 1);
			} else {
				return _name;
			}
		}

		public String getLayeredName(int layer) {
			return getOrgLayeredName(_name, layer);
		}

		public int getLayer() {
			return getOrgLayer(_name);
		}
	}

	private static String getOrgLayeredName(String orgname, int layer) {
		int start_idx = -1;
		while (layer >= 0) {
			start_idx = orgname.indexOf('/', start_idx + 1);
			layer--;
		}
		if (start_idx < 0) {
			start_idx = orgname.length();
		}
		return orgname.substring(0, start_idx);
	}

	public static int getOrgLayer(String orgname) {
		int start_idx = 0;
		int layer = 0;
		while ((start_idx = orgname.indexOf('/', start_idx)) > 0) {
			start_idx++;
			layer++;
		}
		return layer;
	}
	
	protected JSONBrowserConfigScreen getConfigScreen() {
		return new ContactScreenConfig(getContext());
	}

	private static Paragraph _paragraph;
	
	private static  BackgroundDescriptor _focus_bg;
	private static  Paint _paint;
	private static  Paint _focus_paint;
	private static  Paint _search_paint;
	private static  Paint _search_focus_paint;
	private static  Paint _line_paint;
	
	private void initPaints() {
		if(_paragraph == null) {
			_paragraph = new Paragraph(getContext(), "", 0);
			_paragraph.setLineCount(1);
			
			_focus_bg = new BackgroundDescriptor("color=blue");
			_paint = PaintRepository.getDefaultPaint(getContext());
			_focus_paint = PaintRepository.getFontPaint(getContext(), false, false, false, 1.0f, Color.WHITE, 255);
			_search_paint = PaintRepository.getFontPaint(getContext(), true, false, false, 1.1f, Color.BLACK, 255);
			_search_focus_paint = PaintRepository.getFontPaint(getContext(), true, false, false, 1.0f, Color.WHITE, 255);
			_line_paint = new Paint();
			_line_paint.setStyle(Paint.Style.STROKE);
			_line_paint.setColor(Color.BLACK);
			_line_paint.setAlpha(128);
			
		}
	}
	
	public void drawTreeItem(Canvas g, int nid, float left, float top,
			float width, float height, Object obj, boolean focus) {
		
		initPaints();
		
		String icon_file;
		String name;
		if (obj instanceof ContactOrganization) {
			ContactOrganization organ = (ContactOrganization) obj;
			icon_file = DEPT_ICON;
			name = organ.getName();
		} else {
			ContactUser u = (ContactUser) obj;
			icon_file = USER_ICON;
			name = u.get_name();
		}
		if(focus) {
			_focus_bg.draw(getContext(), g, (int)left, (int)top, (int)width, (int)height);
			_paragraph.setPaint(_focus_paint);
		}else {
			_paragraph.setPaint(_paint);
		}
		Bitmap icon = BitmapRepository.getBitmapByName(getContext(), icon_file);
		g.drawBitmap(icon, left + PADDING,  top + (_group_tree.getRowHeight() - icon.getHeight())/2, null);
		_paragraph.setText(name);
		_paragraph.setWidthBound((int)(width - icon.getWidth() - PADDING*2));
		
		_paragraph.draw(g, left + PADDING*2 + icon.getWidth(), top + (_group_tree.getRowHeight() - _paragraph.getHeight())/2, Align.LEFT);
	}
	
	protected void onSearch(String text) {
		_search_mode = true;
		String url = _user_url[2] + "&" + HTTPRequestString.getQueryString("_search", text, true);
		JSONBrowserLink link = new JSONBrowserLink(getContext());
		link.setURL(url);
		link.setExpireMilliseconds(0);
		link.setNotify(false);
		link.setSaveHistory(false);
		getEmbeddedJSONBrowserField().open(link, false, null);
	}
	
	protected void onCancelSearch() {
		_search_mode = false;
		getUiApplication().invokeLater(new Runnable() {
			public void run() {
				getEmbeddedJSONBrowserField().setContent(_group_tree);
				_group_tree.requestFocus();
			}
		});
	}

	private static final int PADDING = 3;
	
	public void drawListRow(ListField<ContactUser> listField, Canvas canvas,
			int index, int top, int width, boolean focus) {
		if(focus) {
			_focus_bg.draw(getContext(), canvas, 0, top, width, listField.getRowHeight());	
		}
		
		Bitmap icon = BitmapRepository.getBitmapByName(getContext(), USER_ICON);
		canvas.drawBitmap(icon, PADDING, top + PADDING, null);
		
		ContactUser user = get(listField, index);
		_paragraph.setWidthBound((int)(width - icon.getWidth() - PADDING*2));

		if(focus) {
			_paragraph.setPaint(_search_focus_paint);
		}else {
			_paragraph.setPaint(_search_paint);			
		}
		_paragraph.setText(user.get_name());
		_paragraph.draw(canvas, 2*PADDING + icon.getWidth(), top + PADDING, Align.LEFT);
		
		if(focus) {
			_paragraph.setPaint(_focus_paint);
		}else {
			_paragraph.setPaint(_paint);
		}
		_paragraph.setText(user.get_orgname());
		_paragraph.draw(canvas, 2*PADDING + icon.getWidth(), top + listField.getRowHeight() - PADDING - _paragraph.getHeight(), Align.LEFT);
	
		canvas.drawLine(0, top + listField.getRowHeight(), width, top + listField.getRowHeight(), _line_paint);
	}

	public ContactUser get(ListField<ContactUser> listField, int index) {
		if(_search_result != null && index >= 0 && index < _search_result.size()) {
			return _search_result.elementAt(index);
		}else {
			return null;
		}
	}

	public int indexOfList(ListField<ContactUser> listField, String prefix,
			int start) {
		return 0;
	}
	
	private void populateSearchList() {
		if(_search_field == null) {
			_search_field = new ListField<ContactUser>(getContext()) {
				protected void onSelect(int index, ContactUser data) {
					selectUser(data);
				}
			};
			Bitmap icon = BitmapRepository.getBitmapByName(getContext(), USER_ICON);
			int row_height = icon.getHeight();
			if(row_height < _paint.getTextSize() + _search_paint.getTextSize() + PADDING) {
				row_height = (int)(_paint.getTextSize() + _search_paint.getTextSize() + PADDING);
			}
			_search_field.setRowHeight(row_height + PADDING*2);
			_search_field.setCallback(this);
		}
		getEmbeddedJSONBrowserField().setContent(_search_field);
		_search_field.requestFocus();
		if(_search_result != null) {
			_search_field.setSize(_search_result.size());
		}
	}

}
