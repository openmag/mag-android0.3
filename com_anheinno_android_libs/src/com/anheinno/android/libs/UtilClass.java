/*
 * UtilClass.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs;



import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public final class UtilClass {
  
    public static String strPadding(int num, int len, char c) {
    	String s = "" + num;
    	while(s.length() < len) {
    		s = c + s;
    	}
    	return s;
    }
    
    public static long now() {
    	return System.currentTimeMillis();
    }
    
    public static String inet_ntoa(byte[] b) {
        return Integer.toString(0xff & (int)b[0]) + "." + Integer.toString(0xff & (int)b[1]) + "." + Integer.toString(0xff & (int)b[2]) + "." + Integer.toString(0xff & (int)b[3]);
    }
    
    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }
    
    public static String[] getLines(String str) {
        String[] sep = {"\n", "\r\n"};
        return strSplit(str, sep, 0);
    }
    
    public static String[] strSplit(String str, String[] sep, int offset) {
        Vector<String> result = new Vector<String>();
        while(offset < str.length()) {
        	int findpos = -1;
        	int seplen = 0;
            for(int i = 0; i < sep.length; i ++) {
                int pos = str.indexOf(sep[i], offset);
                if(pos >= 0 && (findpos < 0 || findpos > pos)) {
                	findpos = pos;
                	seplen = sep[i].length();
                }
            }
            if(findpos >= 0) {
            	if(findpos > offset) {
            		result.addElement(str.substring(offset, findpos));
            	}
            	offset = findpos + seplen;
            }else if(offset < str.length()){
            	result.addElement(str.substring(offset));
            	offset = str.length();
            }
        }
        String ret[] = new String[result.size()];
        result.copyInto(ret);
        return ret;
    }
            
    
    private static byte[] str2bytes(String str) {
    	try {
	    	MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.reset();
			byte[] buffer = str.getBytes();
			md.update(buffer);
			return md.digest();	
    	}catch(final NoSuchAlgorithmException e) {
    		System.out.println(e.toString());
    		return null;
    	}
    }
    
    public static long str2long(String str) {
        byte[] hashValBytes = str2bytes(str);
        long hashValLong = 0;
        for( int i = 0; i < 8; i++ ) {
            hashValLong |= ((long)(hashValBytes[i]) & 0x0FF)<<(8*i);
        }
        return hashValLong;
    }
    
    public static String str2digest(String str) {
        byte[] hashValBytes = str2bytes(str);
        return convertToHex(hashValBytes);
    }

    /*public static String getIMSIString() {
    	try {
	    	byte[] imsi = SIMCardInfo.getIMSI();
	    	StringBuffer imsi_str = new StringBuffer();
	    	for(int i = 0; i < imsi.length; i ++) {
	    		String hex = Integer.toHexString(imsi[i]);
	    		imsi_str.append(hex);
	    	}
	    	return imsi_str.toString();
    	}catch(final Exception e) {
    		return "";
    	}
    }*/

    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
        if( c != null && string != null ) {
            try {
                return Enum.valueOf(c, string.trim().toUpperCase());
            }catch(IllegalArgumentException ex) {
            }
        }
        return null;
    }
    
	public static String[] JSONArray2StringArray(JSONArray arr) {
		String[] str_arr = new String[arr.length()];
		for(int i = 0; i < str_arr.length; i ++) {
			try {
				str_arr[i] = arr.getString(i);
			}catch(JSONException e) {
			}
		}
		return str_arr;
	}

	public static String parseNumber(String data) {
		if (data == null) {
			return null;
		}
		StringBuffer number = new StringBuffer();
		int length = data.length();

		for (int i = 0; i < length; i++) {
			char c = data.charAt(i);
			if (c >= '0' && c <= '9' || c == '.') {
				number.append(c);
			}
		}
		return number.toString();
	}
	
	private static final String TRUE = "true";
    private static final String FALSE = "false";
    public static String boolean2String(boolean b) {
    	if(b) {
    		return TRUE;
    	}else {
    		return FALSE;
    	}
    }
    
    public static boolean string2Boolean(String str) {
    	if(str.equalsIgnoreCase(TRUE)) {
    		return true;
    	}else {
    		return false;
    	}
    }
}
