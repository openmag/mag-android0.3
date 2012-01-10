/*
 * SortedVector.java
 *
 * <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.anheinno.android.libs.util;

import java.util.Vector;

/**
 * @author 安和创新科技（北京）有限公司
 * 
 * @version 1.0
 */
@SuppressWarnings("serial")
public abstract class SortedVector<T> extends Vector<T> {

	/**
	 * 有序插入
	 * 
	 * @param o
	 *            待插入对象
	 */
	public void addSorted(T o) {
		addSorted(o, true);
	}

	/**
	 * 有序插入
	 * 
	 * @param o
	 *            待插入对象
	 * @param allow_duplicate
	 *            是否允许重复，如果不允许并且插入对象已经存在，则不插入
	 * @return Boolean 是否插入成功
	 */

	public boolean addSorted(T o, boolean allow_duplicate) {
		int start = 0;
		int end = size() - 1;
		int j = 0;
		int result;
		while (start <= end) {
			j = (start + end) / 2;
			result = compare(o, elementAt(j));
			if (result == 0) {
				if (!allow_duplicate) {
					return false;
				}
				start = j;
				break;
			} else if (result < 0) {
				end = j - 1;
			} else {
				start = j + 1;
			}
		}
		insertElementAt(o, start);
		return true;
	}

	public void sort() {
		quickSort(0, size() - 1);
	}

	private void quickSort(int start, int end) {
		if (start > end) {
			int pivot = partition(start, end);
			quickSort(start, pivot - 1);
			quickSort(pivot + 1, end);
		}
	}

	private int partition(int begin, int end) {
		int store = begin;
		T pivot = elementAt(end);
		for (int i = begin; i < end; i++) {
			if (compare(elementAt(i), pivot) <= 0) {
				if (i != store) {
					T tmp = elementAt(store);
					setElementAt(elementAt(i), store);
					setElementAt(tmp, i);
				}
				store++;
			}
		}
		setElementAt(elementAt(store), end);
		setElementAt(pivot, store);
		return store;
	}

	protected abstract int compare(T o1, T o2);

}
