package com.anheinno.android.libs.graphics;

import com.anheinno.android.libs.UtilClass;

public enum VAlign {
	TOP {
		public String toString() {
			return "top";
		}
	}, 
	MIDDLE{
		public String toString() {
			return "middle";
		}
	}, 
	BOTTOM {
		public String toString() {
			return "bottom";
		}
	};
	
	public static VAlign fromString(String name)
	{
	    return UtilClass.getEnumFromString(VAlign.class, name);
	}
}
