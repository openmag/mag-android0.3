/**
 * ProgressUIInterfaceGroup.java
 *
 * Copyright 2007-2011 anhe.
 */
package com.anheinno.android.libs;

import java.util.Vector;
import com.anheinno.android.libs.ui.ProgressUIInterface;

/**
 * 2011-7-27
 *
 * @author 安和创新科技（北京）有限公司
 *
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class ProgressUIInterfaceGroup extends Vector<ProgressUIInterface> implements ProgressUIInterface {

	/* (non-Javadoc)
	 * @see com.anheinno.libs.ProgressUIInterface#resetGauge(java.lang.String, int, int, int)
	 */
	public void resetGauge(String msg, int max, int start) {
		for(int i = 0; i < size(); i ++) {
			getUI(i).resetGauge(msg, max, start);
		}
	}

	/* (non-Javadoc)
	 * @see com.anheinno.libs.ProgressUIInterface#resetGauge(java.lang.String, int)
	 */
	public void resetGauge(String msg, int waitSeconds) {
		for(int i = 0; i < size(); i ++) {
			getUI(i).resetGauge(msg, waitSeconds);
		}
	}

	/* (non-Javadoc)
	 * @see com.anheinno.libs.ProgressUIInterface#updateGauge(int)
	 */
	public void updateGauge(int val) {
		for(int i = 0; i < size(); i ++) {
			getUI(i).updateGauge(val);
		}
	}
	
	public ProgressUIInterface getUI(int index) {
		return (ProgressUIInterface)elementAt(index);
	}
}
