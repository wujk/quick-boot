package com.wujk.utils.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 冒泡排序
 * @author CI11951
 * @param <T>
 */
public class BubbleSort<T> {
	
	private Compare<T> c;
	
	public BubbleSort(T[] arrays, Compare<T> c) {
		this.c = c;
		if (arrays != null && arrays.length > 1) {
			sortArrays(arrays);
		}
	}
	
	public BubbleSort(Collection<T> collection, Compare<T> c) {
		this.c = c;
		if (collection != null && collection.size() > 1) {
			@SuppressWarnings("unchecked")
			T[] arrays = (T[]) collection.toArray();
			sortArrays(arrays);
			collection.clear();
			collection.addAll(Arrays.asList(arrays));
		}
	}
	
	private void sortArrays(T[] arrays) {
		T temp = null;
		boolean isOver = true;
		for (int i = arrays.length - 1; i >= 1; i--) {
			for (int j = i - 1; j >=0;) {
				int a = c.compare(arrays[i], arrays[j]);
				if (a < 0) {
					isOver = false;
					temp = arrays[j];
					arrays[j] = arrays[i];
					arrays[i] = temp;
				}
				break;
			}
		}
		if (!isOver) {
			sortArrays(arrays);
		}
	}

	public static void main(String[] args) {
		Integer[] integer = new Integer[] {3,1,2,4,6,88,9,99,0};
		System.out.println(Arrays.toString(integer));
		new BubbleSort<>(integer, new Compare<Integer>() {
			
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
		list.add(88);
		list.add(9);
		list.add(99);
		list.add(0);
		System.out.println(list);
		new BubbleSort<Integer>(list, new Compare<Integer>() {
			
			@Override
			public int compare(Integer t1, Integer t2) {
				return t1 - t2;
			}
		});
		System.out.println(list);
	}
}
