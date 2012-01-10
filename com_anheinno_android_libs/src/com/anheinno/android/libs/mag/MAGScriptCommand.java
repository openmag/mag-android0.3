/*
 * MAGScriptCommand.java
 *
 * ?<your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.mag;

import java.util.*;

/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
class MAGScriptCommand {
	String[] _keys;

	MAGScriptCommand(String[] ks) {
		super();
		_keys = ks;
	}

	MAGScriptCommand(Vector<String> v) {
		super();
		_keys = null;
		parseVector(v);
	}

	public void parseVector(Vector<String> v) {
		_keys = new String[v.size()];
		for (int i = 0; i < v.size(); i++) {
			_keys[i] = (String) v.elementAt(i);
		}
	}

	public String getFirstObject() {
		if (_keys[1].equals("(")) {
			return "this";
		} else if (_keys[1].equals(".")) {
			return _keys[0];
		} else {
			return null;
		}
	}

	public String getMethodName() {
		for (int i = 0; i < _keys.length; i++) {
			if (_keys[i].equals("(") && i > 0) {
				return _keys[i - 1];
			}
		}
		return null;
	}

	public MAGScriptCommand getNextLevel() {
		if (_keys[1].equals(".")) {
			String[] newk = new String[_keys.length - 2];
			for (int i = 2; i < _keys.length; i++) {
				newk[i - 2] = _keys[i];
			}
			return new MAGScriptCommand(newk);
		}
		return null;
	}

	public String getParameter(int idx) {
		int i = 0;
		while (i < _keys.length && !_keys[i].equals("(")) {
			i++;
		}
		if (idx + i + 1 < _keys.length && !_keys[idx + i + 1].equals(")")) {
			return _keys[idx + i + 1];
		}
		return null;
	}

	public String toString() {
		String obj = getFirstObject();
		String method = getMethodName();
		if (obj == null) {
			return "object is null";
		} else {
			if (method == null) {
				return obj + " call null method ";
			} else {
				return obj + " call method " + method;
			}
		}
	}

	public int getParameterNum() {
		int i = 0;
		while (i < _keys.length && !_keys[i].equals("(")) {
			i++;
		}
		int j = i + 1;
		while (j < _keys.length && !_keys[j].equals(")")) {
			j++;
		}
		return j - i - 1;
	}

	public static MAGScriptCommand[] parseScripts(String s) {
		System.out.println("Process " + s);
		Vector<MAGScriptCommand> cmds = new Vector<MAGScriptCommand>();
		Vector<String> sv = new Vector<String>();
		String tmp = "";
		String sepchar = " \r\n\t\0,";
		String keychar = "().";
		String quotechar = "\"\'";
		boolean inquote = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (inquote) {
				if (c == '\\') {
					i++;
					c = s.charAt(i);
					switch (c) {
					case 'n':
						tmp += "\n";
						break;
					case 't':
						tmp += "\t";
						break;
					case 'r':
						tmp += "\r";
						break;
					case '\'':
						tmp += "\'";
						break;
					case '\"':
						tmp += "\"";
						break;
					}
				} else if (quotechar.indexOf(c) >= 0) {
					if (tmp.length() > 0) {
						sv.addElement(tmp);
						tmp = "";
					}
					inquote = false;
				} else {
					tmp += c;
				}
			} else {
				if (quotechar.indexOf(c) >= 0) {
					if (tmp.length() > 0) {
						sv.addElement(tmp);
						tmp = "";
					}
					inquote = true;
				} else if (sepchar.indexOf(c) >= 0) {
					if (tmp.length() > 0) {
						sv.addElement(tmp);
						tmp = "";
					}
				} else if (keychar.indexOf(c) >= 0) {
					if (tmp.length() > 0) {
						sv.addElement(tmp);
						tmp = "";
					}
					sv.addElement("" + c);
					tmp = "";
				} else if (c == ';') {
					if (tmp.length() > 0) {
						sv.addElement(tmp);
						tmp = "";
					}
					if (sv.size() > 0) {
						cmds.addElement(new MAGScriptCommand(sv));
						sv.removeAllElements();
					}
				} else {
					tmp += c;
				}
			}
		}
		if (sv.size() > 0) {
			cmds.addElement(new MAGScriptCommand(sv));
			sv.removeAllElements();
		}
		MAGScriptCommand[] scripts = new MAGScriptCommand[cmds.size()];
		for (int i = 0; i < cmds.size(); i++) {
			scripts[i] = (MAGScriptCommand) cmds.elementAt(i);
			System.out.println(scripts[i].toString());
		}
		return scripts;
	}

}
