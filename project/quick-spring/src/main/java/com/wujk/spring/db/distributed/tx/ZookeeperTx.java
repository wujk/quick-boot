package com.wujk.spring.db.distributed.tx;

import org.apache.ibatis.session.SqlSession;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class ZookeeperTx {

    private Logger logger = LoggerFactory.getLogger(ZookeeperTx.class);

    public enum TxStatus {

        CREATE("CREATE"), NORMAL("NORMAL"), ABNORMAL("ABNORMAL"), SUCCESS("SUCCESS"), ERROR("ERROR");

        public String value;

        TxStatus(String value) {
          this.value = value;
        }

    }

    private final static String UTF_8 = "UTF-8";

    private int SESSION_TIMEOUT = 10000000;

    private ZooKeeper zookeeper;

    private String node;

    Map<String, SqlSession> sessions = null;

    public void setSessions(Map<String, SqlSession> sessions) {
        this.sessions = sessions;
    }

    public ZookeeperTx(String address, String node) {
        try {
            this.node = node;
            logger.info("ZookeeperTx初始化");
            CountDownLatch lantch = new CountDownLatch(1);
            zookeeper = new ZooKeeper(address, SESSION_TIMEOUT, (WatchedEvent event) -> {
                try {
                    if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                        logger.info("ZookeeperTx连接成功");
                        lantch.countDown();
                    }
                    if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
                        String nodeData = getNodeData();
                        logger.info("ZookeeperTx节点数据：" + nodeData);
                        if (TxStatus.SUCCESS.value.equals(nodeData)) {
                            if (sessions != null) {
                                Set<Map.Entry<String, SqlSession>> entries = sessions.entrySet();
                                for (Map.Entry<String, SqlSession> entry : entries) {
                                    SqlSession session = entry.getValue();
                                    session.commit();
                                    boolean sessionIsClosed = session.getConnection().isClosed();
                                    logger.info(entry.getKey() + " session is closed: " + sessionIsClosed);
                                    session.close();
                                }
                            }
                            zookeeper.close();
                            logger.info("commit......");
                            return;
                        } else if (TxStatus.ERROR.value.equals(nodeData)) {
                            if (sessions != null) {
                                Set<Map.Entry<String, SqlSession>> entries = sessions.entrySet();
                                for (Map.Entry<String, SqlSession> entry : entries) {
                                    SqlSession session = entry.getValue();
                                    session.rollback();
                                    boolean sessionIsClosed = session.getConnection().isClosed();
                                    logger.info(entry.getKey() + " session is closed: " + sessionIsClosed);
                                    session.close();
                                }
                            }
                            zookeeper.close();
                            logger.info("rollback......");
                            return;
                        }
                    }
                    zookeeper.exists(node, true);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            });
            lantch.await();
        } catch (Exception e) {
            logger.error("初始化ZookeeperTx客户端失败：" + e.getMessage(), e);
        }
    }

    public void setNodeData(String status) {
        try {
            Stat stat = zookeeper.exists(node, true);
            if (stat != null) {
                zookeeper.setData(node, status.getBytes(UTF_8), -1);
            }
        } catch (Exception e) {
            logger.error("ZookeeperTx.setNodeData" + e.getMessage(), e);
        }

    }

    public String getNodeData() {
        try {
            Stat stat = zookeeper.exists(node, true);
            if (stat != null) {
                byte[] data = zookeeper.getData(node, true, stat);
                if (data != null) {
                    String nodeData = new String(data, UTF_8);
                    return nodeData;
                }
            }
        } catch (Exception e) {
            logger.error("ZookeeperTx.getNodeData" + e.getMessage(), e);
        }
        return null;
    }

    public void close() {
        try {
            zookeeper.close();
        } catch (Exception e) {
            logger.error("ZookeeperTx.close" + e.getMessage(), e);
        }

    }

}
