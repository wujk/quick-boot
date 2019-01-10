package com.wujk.utils.sort;

import java.util.Arrays;
import java.util.Collection;

public abstract class Sort<T> {
	
	private Compare<T> c;
	
	public Sort(T[] arrays, Compare<T> c) {
		this.c = c;
		if (arrays != null && arrays.length > 1) {
			sortArrays(arrays);
		}
	}
	
	public Sort(Collection<T> collection, Compare<T> c) {
		this.c = c;
		if (collection != null && collection.size() > 1) {
			@SuppressWarnings("unchecked")
			T[] arrays = (T[]) collection.toArray();
			sortArrays(arrays);
			collection.clear();
			collection.addAll(Arrays.asList(arrays));
		}
	}
	
	protected abstract void sortArrays(T[] arrays);

	public Compare<T> getC() {
		return c;
	}

}
