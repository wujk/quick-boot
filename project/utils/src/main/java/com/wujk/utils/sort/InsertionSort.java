package com.wujk.utils.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class InsertionSort<T> extends Sort<T> {
	
	public InsertionSort(T[] arrays, Compare<T> c) {
		super(arrays, c);
	}
	
	public InsertionSort(Collection<T> collection, Compare<T> c) {
		super(collection, c);
	}
	
	@Override
	protected void sortArrays(T[] arrays) {
		for (int i = 1; i < arrays.length; i++) {
			int index = i;
			for (int j = i - 1; j >= 0; j--) {
				if (getC().compare(arrays[index], arrays[j]) < 0) {
					T temp = arrays[index];
					arrays[index] = arrays[j];
					arrays[j] = temp;
					index = j;
				} else {
					break;
				}
			}
		}
	}
	

	public static void main(String[] args) {
		Integer[] integer = new Integer[] {3,1,2,4,6,88,9,99,0};
		System.out.println(Arrays.toString(integer));
		new InsertionSort<>(integer, new Compare<Integer>() {
			
			@Override
			public int compare(Integer t1, Integer t2) {
				return t1 - t2;
			}
		});
		System.out.println(Arrays.toString(integer));
		
		List<Integer> list = new ArrayList<Integer>();
		list.add(3);
		list.add(1);
		list.add(2);
		list.add(2);
		list.add(6);
		list.add(88);
		list.add(9);
		list.add(99);
		list.add(0);
		System.out.println(list);
		new InsertionSort<Integer>(list, new Compare<Integer>() {
			
			@Override
			public int compare(Integer t1, Integer t2) {
				return t1 - t2;
			}
		});
		System.out.println(list);
	}

	
	
}
