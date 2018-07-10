package com.wujk.zookeeper.util;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import com.wujk.zookeeper.service.ZookeeperService;

public class ZookeeperUtil {
	
	private final static String  SYNCHRONIZED = "SYNCHRONIZED";
	
	public static void createNode(String zookeeperAddress, String path, String data) {
		synchronized (SYNCHRONIZED) {
			ZookeeperService zookeeper = new ZookeeperService();
			zookeeper.setAddress(zookeeperAddress);
			zookeeper.setWatcher(new Watcher() {
				
				@Override
				public void process(WatchedEvent event) {
				}
			});
			zookeeper.createConnection();
			zookeeper.deleteNode(path);
			zookeeper.createNode(path, data);
			zookeeper.releaseConnection();
		}
	}

}
