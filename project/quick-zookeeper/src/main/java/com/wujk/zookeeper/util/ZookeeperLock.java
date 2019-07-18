package com.wujk.zookeeper.util;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

public class ZookeeperLock {

    private Logger logger = LoggerFactory.getLogger(ZookeeperLock.class);

    /**
     * 重试时间
     */
    private static final int DEFAULT_ACQUIRY_RETRY_MILLIS = 100;

    /**
     * 锁的后缀
     */
    private static final String LOCK_SUFFIX = "/_zookeeper_lock";

    private String group;

    /**
     * 锁的key
     */
    private String lockKey;

    /**
     * 完整的锁path路径
     */
    private String lockName;

    /**
     * 线程获取锁的等待时间
     */
    private int timeoutMsecs = 100 * 1000;

    private int SESSION_TIMEOUT = 10000000;

    /**
     * 是否锁定标志
     */
    private volatile boolean locked = false;

    private ZooKeeper zookeeper;

    private CountDownLatch lantch;

    private CountDownLatch lantchWatcher;

    private Watcher watcher = new LockWatcher();

    private String address;

    public ZookeeperLock(String address, String lockKey) {
        this(address, LOCK_SUFFIX, lockKey);
    }

    public ZookeeperLock(String address, String group, String lockKey) {
        if (lockKey.startsWith("/")) {
            this.lockKey = lockKey;
        } else {
            this.lockKey = "/".concat(lockKey);
        }
        if (group.startsWith("/")) {
            this.group = LOCK_SUFFIX.concat(group);
        } else {
            this.group = LOCK_SUFFIX.concat("/").concat(group);
        }
        this.address = address;
        try {
            logger.info("zookeeper初始化");
            lantch = new CountDownLatch(1);
            zookeeper = new ZooKeeper(address, SESSION_TIMEOUT, watcher);
            lantch.await();
        } catch (Exception e) {
            logger.error("初始化zookeeper客户端失败：" + e.getMessage(), e);
        }
    }

    class LockWatcher implements Watcher {

        @Override
        public void process(WatchedEvent event) {
            if (event.getState() == Event.KeeperState.SyncConnected) {
                logger.info("zookeeper连接成功");
                try {
                    Stat stat = zookeeper.exists(LOCK_SUFFIX, false);
                    if (stat == null) {
                        zookeeper.create(LOCK_SUFFIX, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    }
                    Stat groupNode = zookeeper.exists(group, false);
                    if (groupNode == null) {
                        zookeeper.create(group, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                lantch.countDown();
            }
            if (event.getType() == Event.EventType.NodeDeleted) {
                try {
                    locked = getLocked(true);
                    if (locked) {
                        lantchWatcher.countDown();
                    }
                }catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public boolean tryLock() {
        return tryLock(timeoutMsecs);
    }

    public boolean tryLock(int TimeOut) {
        try {
            lockName = zookeeper.create(group.concat(lockKey), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            while (!locked && TimeOut > 0) {
                locked = getLocked(false);
                Thread.sleep(DEFAULT_ACQUIRY_RETRY_MILLIS);
                TimeOut -= DEFAULT_ACQUIRY_RETRY_MILLIS;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return locked;
    }

    public boolean lockWatcher() {
        try {
            lantchWatcher = new CountDownLatch(1);
            lockName = zookeeper.create(group.concat(lockKey), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            String minNode = getMinNode();
            minNode = group.concat("/").concat(minNode);
            logger.info("当前节点" + lockName + ", 当前最小节点监听1：" + minNode);
            if (lockName.equals(minNode)) {
                logger.info("获取锁成功：" + lockName);
                System.out.println("获取锁成功：" + lockName);
                return true;
            } else {
                zookeeper.exists(minNode, true);
                lantchWatcher.await();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return locked;
    }

    private boolean getLocked(boolean needWatcher) throws KeeperException, InterruptedException {
        String minNode = getMinNode();
        minNode = group.concat("/").concat(minNode);
        logger.info("当前节点" + lockName + ", 当前最小节点监听2：" + minNode);
        if (lockName.equals(minNode)) {
            logger.info("获取锁成功：" + lockName);
            System.out.println("获取锁成功：" + lockName);
            return true;
        }
        if (needWatcher) {
            zookeeper.exists(minNode, true);
        }
        return false;
    }

    private String getMinNode() throws KeeperException, InterruptedException {
        List<String> childrens = zookeeper.getChildren(group, false);
        if (childrens != null && childrens.size() > 0) {
            TreeSet<String> sortNode = new TreeSet<>(childrens);
            return sortNode.first();
        }
        return null;
    }

    public void unlock() {
        try {
            logger.info("释放锁：" + lockName);
            System.out.println("释放锁：" + lockName);
            locked = false;
            zookeeper.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void destory() {
        try {
            lantch = new CountDownLatch(1);
            zookeeper = new ZooKeeper(address, SESSION_TIMEOUT, watcher);
            lantch.await();
            if(zookeeper.exists(group, false) != null) {
                zookeeper.delete(group, -1);
            }
            unlock();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getGroup() {
        return group;
    }

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch lantch = new CountDownLatch(50);
        for (int i = 0; i < 50; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ZookeeperLock lock = new ZookeeperLock("127.0.0.1:2181",  "31112350010911110", "/lock");
                    if (lock.lockWatcher()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        lock.unlock();
                        lantch.countDown();
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        lock.unlock();
                        lantch.countDown();
                    }
                }
            }).start();
        }
        lantch.await();
    }
}
