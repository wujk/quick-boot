package com.wujk.utils.sort.search;

import java.util.Collection;

public class LinearSearch<T> extends Search<T> {

	public LinearSearch(T[] arrays, Object dest, Equals<T> e) {
		super(arrays, dest, e);
	}

	public LinearSearch(Collection<T> collection, Object dest, Equals<T> e) {
		super(collection, dest, e);
	}

	@Override
	protected void search(T[] arrays, Object dest) {
		for (int i = 0; i < arrays.length; i++) {
			if (getE().compare(arrays[i], dest)) {
				addIndex(i);
				addResult(arrays[i]);
			}
		}
	}

	
	public static void main(String[] args) {
		Integer[] integer = new Integer[] {9,1,2,9,6,88,9,99,0};
		LinearSearch<Integer> linearSearch = new LinearSearch<>(integer, 9, new Equals<Integer>() {

			@Override
			public boolean compare(Integer src, Object dest) {
				return src.equals(dest);
			}
		});
		System.out.println(linearSearch.getFirstIndex());
		System.out.println(linearSearch.getLastIndex());
		System.out.println(linearSearch.getFirstResult());
		System.out.println(linearSearch.getLastResult());
	}
}
