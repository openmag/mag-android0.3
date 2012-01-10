/*
 * HTTPRequestString.java
 *
 * <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs;

import java.util.Hashtable;
import java.util.Enumeration;

import com.anheinno.android.libs.util.SortedStringVector;
import com.anheinno.android.libs.util.URLUTF8Encoder;


/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
public class HTTPRequestString {
    private static final int STATE_PROTOCOL = 0;
    private static final int STATE_SERVER = 1;
    private static final int STATE_PORT   = 2;
    private static final int STATE_SCRIPT = 3;
    private static final int STATE_PARAMS  = 4;
    
    String    _protocol;
    String    _server;
    String    _port;
    String    _script;
    Hashtable<String, String> _params;
   
    public HTTPRequestString() {
        _protocol = null;
        _server = null;
        _port = null;
        _script = null;
        _params = new Hashtable<String, String>();
    }
    
    public void setProtocol(String p) {
        _protocol = p;
    }
    public String getProtocol() {
        return _protocol;
    }
    
    public void setServer(String s) {
        _server = s;
    }
    public String getServer() {
        return _server;
    }
    
    public void setPort(int p) {
        _port = "" + p;
    }
    public String getPort() {
        return _port;
    }
    
    public void setScript(String s) {
        _script = s;
    }
    public String getScript() {
        return _script;
    }
    
    public void addParam(String key, String val) {
        _params.put(key, val);
    }
    
    public String getURL() {
        return getURL(true);
    }
    
    public boolean relative(String path) {
    	if(path == null || path.length() == 0) {
    		_params.clear();
    		return true;
    	}else if(path.startsWith("http://") || path.startsWith("https://")) {
    		return parse(path);
    	}else if(path.startsWith("/")) {
    		_script = null;
    		_params.clear();
    		return parse(getURL() + path);
    	}else if(path.startsWith("?")) {
    		_params.clear();
    		return parse(getURL() + path);
    	}else {
    		int slash = _script.lastIndexOf('/');
    		if(slash >= 0) {
    			path = _script.substring(0, slash+1) + path;	
    		}
    		_script = null;
    		_params.clear();
    		return parse(getURL() + path);
    	}
    }
    
    public String getURL(boolean encode) {
        StringBuffer url = new StringBuffer();
        url.append(_protocol);
        url.append("://");
        url.append(_server);
        if(_port != null && _port.length() > 0 && !_port.equals("80")) {
        	url.append(":");
        	url.append(_port);
        }
        url.append("/");
        if(_script != null) {
        	url.append(_script);
        }
        if(_params.size() > 0) {
        	url.append("?");
        	url.append(getQueryString(_params, encode));
        }
        return url.toString();
    }
    
    public static String purify(String url) {
    	if(url != null) {
	        HTTPRequestString req = new HTTPRequestString();
	        if(req.parse(url)) {
	        	return req.getURL(false);
	        }else {
	        	//System.out.println("Error parse URL " + url);
	        	return null;
	        }
    	}else {
    		return null;
    	}
    }

    public static String getQueryString(String key, String val) {
        return getQueryString(key, val, true);
    }
        
    public static String getQueryString(String key, String val, boolean encode) {
    	return getQueryStringBuffer(key, val, encode).toString();
    }
    public static StringBuffer getQueryStringBuffer(String key, String val, boolean encode) {
    	StringBuffer buf = new StringBuffer();
        if(encode) {
        	key = URLUTF8Encoder.encode(key);
        	val = URLUTF8Encoder.encode(val);
        }
        buf.append(key);
        buf.append('=');
        buf.append(val);
        return buf;
    }

    public static String getQueryString(Hashtable<String, String> params) {
        return getQueryString(params, true);
    }
    
    public static String getQueryString(Hashtable<String, String> params, boolean encode) {
        StringBuffer pam_str = new StringBuffer();
        SortedStringVector keys = new SortedStringVector();
        Enumeration<String> e = params.keys();
        while(e.hasMoreElements()) {
            String key = e.nextElement();
            keys.addSorted(key);
        }
        for(int i = 0; i < keys.size(); i ++) {
            String key = (String)keys.elementAt(i);
            String val = (String)params.get(key);
            if(pam_str.length() != 0) {
            	pam_str.append('&');
            }
            pam_str.append(getQueryStringBuffer(key, val, encode));
        }
        return pam_str.toString();
    }
    
    public boolean parse(String url) {
    	return parse(url, STATE_PROTOCOL);
    }
    
    private boolean parse(String url, int init_state) {
        int state = init_state;
        int i = 0;

        StringBuffer tmp = new StringBuffer();

        _protocol = null;
        _server = null;
        _port = null;
        _script = null;
        _params.clear();
        
        for(i = 0; i < url.length() && state != STATE_PARAMS; i ++) {
            char ch = url.charAt(i);
            switch(state) {
                case STATE_PROTOCOL:
                    if(Character.isLetter(ch)) {
                    	tmp.append(url.charAt(i));
                    }else if(ch == ':') {
                    	_protocol = tmp.toString();
                    	tmp.delete(0, tmp.length());
                        state = STATE_SERVER;
                    }else {
                        return false;
                    }
                    break;
                case STATE_SERVER:
                    if(ch == '/') {
                        if(tmp.length() == 0) {
                        }else {
                        	_server = tmp.toString();
                        	tmp.delete(0, tmp.length());
                            state = STATE_SCRIPT;
                        }
                    }else if(ch == ':') {
                    	_server = tmp.toString();
                    	tmp.delete(0, tmp.length());
                        state = STATE_PORT;
                    }else if(Character.isDigit(ch) || Character.isLetter(ch) || ch == '.' || ch == '-') {
                        tmp.append(url.charAt(i));
                    }else {
                        return false;
                    }
                    break;
                 case STATE_PORT:
                    if(Character.isDigit(ch)) {
                    	tmp.append(url.charAt(i));
                    }else if(ch == '/') {
                    	_port = tmp.toString();
                    	tmp.delete(0, tmp.length());
                        state = STATE_SCRIPT;
                    }else {
                        return false;
                    }
                    break;
                case STATE_SCRIPT:
                    if(ch == '?') {
                    	_script = tmp.toString();
                    	tmp.delete(0, tmp.length());
                        state = STATE_PARAMS;
                    }else if(ch == '/') {
                        if(tmp.length() > 0 && tmp.charAt(tmp.length()-1) != ch) {
                        	tmp.append(url.charAt(i));
                        }
                    }else {
                    	tmp.append(url.charAt(i));
                    }
                    break;
            }
        }
        
        if(i >= url.length() && tmp.length() > 0) {
        	if(state == STATE_SERVER) {
        		_server = tmp.toString();
        	}else if(state == STATE_PORT) {
        		_port = tmp.toString();
        	}else if(state == STATE_SCRIPT) {
        		_script = tmp.toString();
        	}
        }
                
        //System.out.println("protocol=" + _protocol + " server: " + _server + " port:" + _port + " script:" + _script);
        while(i < url.length() && url.charAt(i) == '?') {
        	i++;
        }
        
        if(i < url.length()) {
            String[] sep = {"&"};
            String[] params = UtilClass.strSplit(url, sep, i);
            for(i = 0; i < params.length; i ++) {
                int pos = params[i].indexOf("=");
                if(pos > 0) {
                    _params.put(params[i].substring(0, pos), params[i].substring(pos+1));
                    //System.out.println(params[i].substring(0, pos) + "=" + params[i].substring(pos+1));
                }
            }
        }
        
        return true;
    }
}
