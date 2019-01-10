package com.wujk.utils.sort;

import java.util.Arrays;
import java.util.Collection;

public class HeapSort<T> extends Sort<T> {

	public HeapSort(T[] arrays, Compare<T> c) {
		super(arrays, c);
	}

	public HeapSort(Collection<T> collection, Compare<T> c) {
		super(collection, c);
	}

	@Override
	protected void sortArrays(T[] arrays) {
		buildHeap(arrays, arrays.length);
		sort(arrays, arrays.length);
	}
	
	/**
	 * 顶元素与底元素互换
	 * @param arrays
	 * @param length
	 */
	private void sort(T[] arrays, int length) {
		if (length > 0) {
			T temp = arrays[0];
			arrays[0] = arrays[length - 1];
			arrays[length - 1] = temp;
			buildHeap(arrays, length - 1);
			sort(arrays, length - 1);
		}
	}

	/**
	 * 建立堆
	 * @param arrays
	 * 从数组中间开始2*i+1是左节点，2*i+2是右节点
	 */
	private void buildHeap(T[] arrays, int length) {
		for (int i = (length - 1) / 2; i >= 0; i--) {
			buildHeap(arrays, i, length);		
		}
	}
	
	private void buildHeap(T[] arrays, int i, int length) {
		for (int j = i; j < length; j = 2*j + 1) {
			int maxIndex = 2 * j + 1;
			if (maxIndex < length) {  // 如果存在左节点
				if (maxIndex + 1 < length) { // 如果存在右节点。 从中取出较大的节点
					int a = getC().compare(arrays[maxIndex], arrays[maxIndex + 1]);  
					if (a < 0) {
						maxIndex = maxIndex + 1;
					}
				}
				int a = getC().compare(arrays[j], arrays[maxIndex]);
				if (a < 0) {
					T temp = arrays[j];
					arrays[j] = arrays[maxIndex];
					arrays[maxIndex] = temp;
				}
			}
		}
	}

	public static void main(String[] args) {
		Integer[] integer = new Integer[] {1,4,2,7,6,5,3};
		System.out.println(Arrays.toString(integer));
		new HeapSort<>(integer, new Compare<Integer>() {
			
			@Override
			public int compare(Integer t1, Integer t2) {
				return t1 - t2;
			}
		});
		System.out.println(Arrays.toString(integer));
	}

}
