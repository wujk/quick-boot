package com.wujk.zookeeper.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

public class ZookeeperService {

	private final Logger logger = Logger.getLogger(this.getClass());
	
	private final static String PARENTNODE = null;
	/**
	 * 定义session失效时间 10s
	 */
	private int SESSION_TIMEOUT = 10000000;
	/**
	 * zookeeper服务器地址
	 */
	private String address;

	private String parentPath = PARENTNODE;

	private Watcher watcher;
	/**
	 * ZooKeeper变量
	 */
	private ZooKeeper zk = null;
	
	private CountDownLatch lantch = new CountDownLatch(1);

	/**
	 * 创建节点
	 * 
	 * @param path
	 *            节点路径
	 * @param data
	 *            数据内容
	 * @param acl
	 *            访问控制列表
	 * @param createMode
	 *            znode创建类型
	 * @return
	 */
	public boolean createNode(String path, String data, List<ACL> acl, CreateMode createMode) {
		try {
			// 设置监控(由于zookeeper的监控都是一次性的，所以每次必须设置监控)
			Stat stat = exists(path, true);
			if (stat == null) {
				String resultPath = zk.create(path, data.getBytes(), acl, createMode);
				logger.info(String.format("节点创建成功，path: %s，data: %s", resultPath, data));
			} else {
				logger.info(String.format("节点，path: %s 已经创建", path));
			}
		} catch (Exception e) {
			logger.error("节点创建失败", e);
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param path
	 * @param data
	 * @return
	 */
	public boolean createNode(String path, String data) {
		try {
			// 设置监控(由于zookeeper的监控都是一次性的，所以每次必须设置监控)
			Stat stat = exists(path, true);
			if (stat == null) {
				String resultPath = zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
				logger.info(String.format("节点创建成功，path: %s，data: %s", resultPath, data));
			} else {
				logger.info(String.format("节点，path: %s 已经创建", path));
			}
		} catch (Exception e) {
			logger.error("节点创建失败", e);
			return false;
		}
		return true;
	}

	/**
	 * 更新指定节点数据内容
	 * 
	 * @param path
	 *            节点路径
	 * @param data
	 *            数据内容
	 * @return
	 */
	public boolean updateNode(String path, String data) {
		try {
			Stat stat = zk.setData(path, data.getBytes(), -1);
			logger.info("更新节点数据成功，path：" + path + ", stat: " + stat);
		} catch (Exception e) {
			logger.error("更新节点数据失败", e);
			return false;
		}
		return true;
	}

	/**
	 * 删除指定节点
	 * 
	 * @param path
	 *            节点path
	 */
	public void deleteNode(String path) {
		try {
			Stat stat = exists(path, true);
			if (stat != null) {
				zk.delete(path, -1);
				logger.info("删除节点成功，path：" + path);
			}
		} catch (Exception e) {
			logger.error("删除节点失败", e);
		}
	}

	/**
	 * 读取节点数据
	 * 
	 * @param path
	 *            节点路径
	 * @param needWatch
	 *            是否监控这个目录节点，这里的 watcher是在创建ZooKeeper实例时指定的watcher
	 * @return
	 */
	public String getNodeData(String path, boolean needWatch) {
		try {
			Stat stat = exists(path, needWatch);
			if (stat != null) {
				return new String(zk.getData(path, watcher, stat));
			}
		} catch (Exception e) {
			logger.error("读取节点数据内容失败", e);
		}
		return null;
	}

	/**
	 * 获取子节点
	 * 
	 * @param path
	 *            节点路径
	 * @param needWatch
	 *            是否监控这个目录节点，这里的 watcher是在创建ZooKeeper实例时指定的watcher
	 * @return
	 */
	public List<String> getChildren(String path, boolean needWatch) {
		try {
			List<String> list = zk.getChildren(path, needWatch);
			for (String str : list) {
				zk.getChildren(path + "/" + str, needWatch);
			}
			return list;
		} catch (Exception e) {
			logger.error("获取子节点失败", e);
			return null;
		}
	}

	/**
	 * 判断znode节点是否存在
	 * 
	 * @param path
	 *            节点路径
	 * @param needWatch
	 *            是否监控这个目录节点，这里的 watcher是在创建ZooKeeper实例时指定的watcher
	 * @return
	 */
	public Stat exists(String path, boolean needWatch) {
		try {
			return zk.exists(path, needWatch);
		} catch (Exception e) {
			logger.error("判断znode节点是否存在发生异常", e);
		}

		return null;
	}

	/**
	 * 创建ZK连接
	 * 
	 * @param connectAddr
	 * @param sessionTimeout
	 */
	public void createConnection() {
		try {
			logger.info("开始连接ZK服务器...");
			zk = new ZooKeeper(address, SESSION_TIMEOUT, watcher);
			lantch.countDown();
			Stat stat = this.exists(parentPath, true);
			if(stat != null){
				this.getChildren(parentPath, true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 关闭ZK连接
	 */
	public void releaseConnection() {
		if (zk != null) {
			try {
				zk.close();
				logger.info("ZK连接关闭成功");
			} catch (InterruptedException e) {
				logger.error("ZK连接关闭失败", e);
			}
		}
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Watcher getWatcher() {
		return watcher;
	}

	public void setWatcher(Watcher watcher) {
		this.watcher = watcher;
	}
}
