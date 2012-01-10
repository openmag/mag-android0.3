package com.anheinno.android.libs.mag;


import android.content.Context;
import android.view.View;

import com.anheinno.android.libs.JSONObjectCacheDatabase;
import com.anheinno.android.libs.ui.ScreenMenu;
import com.anheinno.android.libs.ui.ScreenMenuItem;


/**
 * 和MAGLink类似，MAGMenuItem为在系统菜单中的一个可以点击的菜单选项。<br>
 * TYPE = "MENUITEM"
 * 
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class MAGMenuItem extends MAGLinkableComponent {
	
	private ScreenMenuItem _menuItem;

	public MAGMenuItem() {
		super();
		_menuItem = null;
	}

	public View initField(Context context) {
		String menu_text = title();
		if(_link.isNotify()) {
			String state = JSONObjectCacheDatabase.linkState(getContext(), _link.getURL());
			if(state.equals(JSONObjectCacheDatabase.URL_LINK_UPDATED)) {
				menu_text += "*";
			}
		}
		_menuItem = new ScreenMenuItem(menu_text, ScreenMenu.USER_DEFINED_MENU_ITEM) {
			protected void initIcons() {
				setMenuIconName("menubar_icon_android.png");
				setBarIconName("menubar_icon_android.png");
				setFocusBarIconName("menubar_icon_android_focus.png");
			}
			
			public void run() {
				go2Link();
			}
		};
		getMAGDocument().addMenuItem(_menuItem);
		return null;
	}

	public void updateField(View f) {
		super.updateField(f);
		getMAGDocumentScreen().invalidateMenu();
	}

}
