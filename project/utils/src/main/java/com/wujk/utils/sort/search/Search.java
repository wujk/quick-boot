package com.wujk.utils.sort.search;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public abstract class Search<T> {
	private Equals<T> e;
	private LinkedList<T> results = new LinkedList<T>();
	private LinkedList<Integer> indexs = new LinkedList<Integer>();
	
	public Search(T[] arrays, Object dest, Equals<T> e) {
		this.e = e;
		search(arrays, dest);
	}
	
	public Search(Collection<T> collection, Object dest, Equals<T> e) {
		this.e = e;
		if (collection != null && collection.size() > 1) {
			@SuppressWarnings("unchecked")
			T[] arrays = (T[]) collection.toArray();
			search(arrays, dest);
		}
	}
	
	protected abstract void search(T[] arrays, Object dest);

	public Equals<T> getE() {
		return e;
	}

	public void setE(Equals<T> e) {
		this.e = e;
	}

	public T getFirstResult() {
		return results.getFirst();
	}

	public T getLastResult() {
		return results.getLast();
	}

	public int getFirstIndex() {
		return indexs.getFirst();
	}

	public int getLastIndex() {
		return indexs.getLast();
	}

	public List<T> getResults() {
		return results;
	}

	public List<Integer> getIndexs() {
		return indexs;
	}
	
	public void addIndex(int index) {
		indexs.add(index);
	}
	
	public void addResult(T t) {
		results.add(t);
	}


	interface Equals<T> {
		public boolean compare(T src, Object dest);
	}
	
}
