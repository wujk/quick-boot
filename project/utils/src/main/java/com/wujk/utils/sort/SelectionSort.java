package com.wujk.utils.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SelectionSort<T> extends Sort<T> {
	
	public SelectionSort(T[] arrays, Compare<T> c) {
		super(arrays, c);
	}
	
	public SelectionSort(Collection<T> collection, Compare<T> c) {
		super(collection, c);
	}
	
	@Override
	protected void sortArrays(T[] arrays) {
		int startIndex = 0;
		while (startIndex + 1 < arrays.length) {
			int minIndex = startIndex;
			for (int i = startIndex + 1; i < arrays.length; i++) {
				if (getC().compare(arrays[minIndex], arrays[i]) > 0) {
					minIndex = i;
				}
			}
			T temp = arrays[minIndex];
			arrays[minIndex] = arrays[startIndex];
			arrays[startIndex] = temp;
			startIndex ++;
		}
	}

	public static void main(String[] args) {
		Integer[] integer = new Integer[] {3,1,2,4,6,88,91,99,0};
		System.out.println(Arrays.toString(integer));
		new SelectionSort<>(integer, new Compare<Integer>() {
			
			@Override
			public int compare(Integer t1, Integer t2) {
				return t2 - t1;
			}
		});
		System.out.println(Arrays.toString(integer));
		
		List<Integer> list = new ArrayList<Integer>();
		list.add(3);
		list.add(1);
		list.add(2);
		list.add(4);
		list.add(6);
		list.add(881);
		list.add(9);
		list.add(99);
		list.add(0);
		System.out.println(list);
		new SelectionSort<Integer>(list, new Compare<Integer>() {
			
			@Override
			public int compare(Integer t1, Integer t2) {
				return t1 - t2;
			}
		});
		System.out.println(list);
	}
}
