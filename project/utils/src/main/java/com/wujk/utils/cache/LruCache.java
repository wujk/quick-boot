package com.wujk.utils.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LruCache<T> {
	private Node head;
	private Node end;
	//缓存存储上限
	private int limit;

	private Map<String, Node> hashMap;

	public LruCache(int limit) {
		this.limit = limit;
		hashMap = new ConcurrentHashMap<String, Node>();
	}

	public synchronized T get(String key) {
		Node node = hashMap.get(key);
		if (node == null){
			return null;
		}
		refreshNode(node);
		return node.value;
	}

	public synchronized void put(String key, T value) {
		Node node = hashMap.get(key);
		if (node == null) {
			//如果key不存在，插入key-value
			if (hashMap.size() >= limit) {
				if (head != null) {
					String oldKey = removeNode(head);
					hashMap.remove(oldKey);
				}
			}
			node = new Node(key, value);
			addNode(node);
			hashMap.put(key, node);
		}else {
			//如果key存在，刷新key-value
			node.value = value;
			refreshNode(node);
		}
	}

	public synchronized void remove(String key) {
		Node node = hashMap.get(key);
		if (node != null) {
			removeNode(node);
			hashMap.remove(key);
		}
	}

	public synchronized void clear() {
		hashMap.clear();
		head = null;
		end = null;
	}

	/**
	 * 刷新被访问的节点位置
	 * @param  node 被访问的节点
	 */
	private void refreshNode(Node node) {
		//如果访问的是尾节点，无需移动节点
		if (node == end) {
			return;
		}
		//移除节点
		removeNode(node);
		//重新插入节点
		addNode(node);
	}

	/**
	 * 删除节点
	 * @param  node 要删除的节点
	 */

	private String removeNode(Node node) {
		if (node == end) {
			//移除尾节点
			end = end.pre;
		}else if(node == head){
			//移除头节点
			head = head.next;
		} else {
			//移除中间节点
			node.pre.next = node.next;
			node.next.pre = node.pre;
		}
		return node.key;
	}

	/**
	 * 尾部插入节点
	 * @param  node 要插入的节点
	 */
	private void addNode(Node node) {
		if(end != null) {
			end.next = node;
			node.pre = end;
			node.next = null;
		}
		end = node;
		if(head == null){
			head = node;
		}
	}

	class Node {
		Node(String key, T value){
			this.key = key;
			this.value = value;
		}
		public Node pre;
		public Node next;
		public String key;
		public T value;
	}

	public boolean containsKey(String key) {
		return hashMap.containsKey(key);
	}

	public static void main(String[] args) {
		LruCache<Integer> cache = new LruCache<Integer>(5);
		cache.remove("1");
		cache.put("1", 1);
		cache.put("2", 1);
		cache.get("1");
	}

}
