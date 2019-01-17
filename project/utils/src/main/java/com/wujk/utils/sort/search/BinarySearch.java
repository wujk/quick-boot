package com.wujk.utils.sort.search;

import java.util.Collection;

import com.wujk.utils.sort.Compare;

public class BinarySearch<T> extends Search<T> implements Compare<T> {

	public BinarySearch(T[] arrays, Object dest, Equals<T> e) {
		super(arrays, dest, e);
	}

	public BinarySearch(Collection<T> collection, Object dest, Equals<T> e) {
		super(collection, dest, e);
	}

	@Override
	protected void search(T[] arrays, Object dest) {
		
	}

	@Override
	public int compare(T t1, T t2) {
		return 0;
	}

}
