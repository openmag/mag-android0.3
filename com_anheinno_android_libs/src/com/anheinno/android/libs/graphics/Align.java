package com.anheinno.android.libs.graphics;

import com.anheinno.android.libs.UtilClass;

public enum Align {
	LEFT {
		public String toString() {
			return "left";
		}
	},
	
	RIGHT {
		public String toString() {
	        return "right";
	    }
	},
	
	CENTER {
		public String toString() {
	        return "center";
	    }
	};
	
	public static Align fromString(String name)
	{
	    return UtilClass.getEnumFromString(Align.class, name);
	}
}
