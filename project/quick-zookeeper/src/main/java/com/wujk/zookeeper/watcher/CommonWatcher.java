package com.wujk.zookeeper.watcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import com.wujk.zookeeper.service.ZookeeperService;

/**
 * 
* @ClassName: CommonWatcher
* @Description: 节点监控
* @author kevin
* @date 2018年5月29日 下午6:12:57
 */
public class CommonWatcher implements Watcher {
	
	private final static String SPLIT = "/";
	private ZookeeperService zookeeper;
	private String rootNode;
	private Map<String, String> dataMap = new HashMap<String, String>();
	
	// 需要监控的节点路径正则表达式如根节点 "\\/[a-zA-Z0-9]{1,}" 可以传入多个
	private String[] regex;
	
	public String[] getRegex() {
		return regex;
	}

	public void setRegex(String... regex) {
		this.regex = regex;
	}
	
	public CommonWatcher() {
		super();
	}

	public CommonWatcher(ZookeeperService zookeeper, String rootNode) {
		super();
		this.zookeeper = zookeeper;
		this.rootNode = rootNode;
	}

	public ZookeeperService getZookeeper() {
		return zookeeper;
	}

	public void setZookeeper(ZookeeperService zookeeper) {
		this.zookeeper = zookeeper;
	}

	public String getRootNode() {
		return rootNode;
	}

	public void setRootNode(String rootNode) {
		this.rootNode = rootNode;
	}

	public void process(WatchedEvent event) {
		if (event.getState() == KeeperState.SyncConnected && event.getType() == EventType.None) {
			String[] nodes = rootNode.split(SPLIT);
			String path = "";
			for (String node : nodes) {
				if (node.length() == 0) {
					continue;
				}
				path = path + SPLIT + node;
				zookeeper.createNode(path, "");
			}
			zookeeper.exists(rootNode, true);
		}
		String path = event.getPath();
		dataMap.clear();
		getChildren(rootNode);
		if (event.getType() == EventType.NodeChildrenChanged) {
			// 监听到子节点变化如增加子节点
			Set<Entry<String, String>> entries = dataMap.entrySet();
			for (Entry<String, String> entry : entries) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (zookeeper.exists(key, false) != null) {
					afterCreateChildNode(zookeeper, key.substring(key.lastIndexOf("/") + 1), key, value);
				}
			}
		}
		if (event.getType() == EventType.NodeDataChanged) {
			// 节点数据变更
			String fullPath = getNode(path, dataMap);
			if (fullPath != null && zookeeper.exists(fullPath, false) != null) {
				afterNodeDataChanged(zookeeper, fullPath.substring(fullPath.lastIndexOf("/") + 1), fullPath, zookeeper.getNodeData(fullPath, true));
			}
		}
		if (event.getType() == EventType.NodeCreated) {
			String fullPath = getNode(path, dataMap);
			if (fullPath != null && zookeeper.exists(fullPath, false) != null) {
				aftercreateNode(zookeeper, fullPath.substring(fullPath.lastIndexOf("/") + 1), fullPath, zookeeper.getNodeData(fullPath, true));
			}
		}
		if (event.getType() == EventType.NodeDeleted) {
			// 监听节点删除
			dataMap.remove(path);
			afterdeleteNode(zookeeper, path.substring(path.lastIndexOf("/") + 1), path, zookeeper.getNodeData(path, true));
		}
		zookeeper.exists(rootNode, true);
	}

	/**
	 * 
	* @Title: getNode
	* @Description: 获取完整nodepath
	* @author kevin
	* @date 2018年5月30日 上午9:57:13
	* @param path
	* @param dataMap
	* @return String
	* @throws
	 */
	private String getNode(String path, Map<String, String> dataMap) {
		if (dataMap != null) {
			Set<String> keys = dataMap.keySet();
			for (String key : keys) {
				if (key.endsWith(path)) {
					return key;
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	* @Title: getChildren
	* @Description: 递归增加节点监控
	* @author kevin
	* @date 2018年5月29日 下午6:04:06
	* @param node void
	* @throws
	 */
	private void getChildren(String node) {
		if (zookeeper.exists(node, true) == null) { // 判断节点是否存在
			return;
		}
		boolean isNeed = false;
		if (regex != null) {
			for (String str : regex) {
				Pattern p = Pattern.compile(str);
				Matcher m = p.matcher(node);
				if (m.matches()) {
					isNeed = true;
					break;
				}
			}
		}
		if (isNeed) {
			String data = zookeeper.getNodeData(node, false);
			dataMap.put(node, data);
		} else {
			String data = zookeeper.getNodeData(node, false);
			if (data != null && data.length() > 0) {
				dataMap.put(node, data);
			}
		}
		List<String> children = zookeeper.getChildren(node, true);
		if (children != null && children.size() > 0) {
			for (String child : children) {
				if (child != null) {
					String path = node + SPLIT + child;
					getChildren(path);
				}
			}
		}
	}
	
	/**
	 * 
	* @Title: afterCreateChildNode
	* @Description: 监听到创建子节点后的操作
	* @author kevin
	* @date 2018年5月29日 下午6:10:03
	* @param nodeData void
	* @throws
	 */
	protected boolean afterCreateChildNode(ZookeeperService zookeeper, String node, String fullPath, String nodeData) {
		return false;
	}
	
	/**
	 * 
	* @Title: afterdeleteNode
	* @Description: 删除节点后的操作
	* @author kevin
	* @date 2018年5月29日 下午6:12:12
	* @param nodeData void
	* @throws
	 */
	protected boolean afterdeleteNode(ZookeeperService zookeeper, String node, String fullPath, String nodeData) {
		return false;
	}
	
	/**
	 * 
	* @Title: afterNodeDataChanged
	* @Description: 节点数据变更
	* @author kevin
	* @date 2018年5月30日 上午9:58:48
	* @param path
	* @param fullPath
	* @param nodeData void
	* @throws
	 */
	protected boolean afterNodeDataChanged(ZookeeperService zookeeper, String node, String fullPath, String nodeData) {
		return false;
	}
	
	/**
	 * 
	* @Title: aftercreateNode
	* @Description: 节点增加
	* @author kevin
	* @date 2018年5月30日 上午10:53:54
	* @param path
	* @param fullPath
	* @param nodeData void
	* @throws
	 */
	protected boolean aftercreateNode(ZookeeperService zookeeper, String node, String fullPath, String nodeData) {
		return false;
	}
}
