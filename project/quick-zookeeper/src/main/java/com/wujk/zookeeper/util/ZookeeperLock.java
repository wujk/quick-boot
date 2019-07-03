package com.wujk.zookeeper.util;

import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

public class ZookeeperLock {

    private final Logger logger = Logger.getLogger(ZookeeperLock.class);

    /**
     * 重试时间
     */
    private static final int DEFAULT_ACQUIRY_RETRY_MILLIS = 100;

    /**
     * 锁的后缀
     */
    private static final String LOCK_SUFFIX = "/_zookeeper_lock";

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

    private CountDownLatch lantch = new CountDownLatch(1);

    private CountDownLatch lantchWatcher;

    private Watcher watcher = new LockWatcher();

    public ZookeeperLock(String address, String lockKey) {
        this.lockKey = lockKey;
        try {
            logger.info("zookeeper初始化");
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
                    lantch.countDown();
                    Stat stat = zookeeper.exists(LOCK_SUFFIX, false);
                    if (stat == null) {
                        zookeeper.create(LOCK_SUFFIX, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    }

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
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
            lockName = zookeeper.create(LOCK_SUFFIX.concat(lockKey), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
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
            lockName = zookeeper.create(LOCK_SUFFIX.concat(lockKey), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            String minNode = getMinNode();
            minNode = LOCK_SUFFIX.concat("/").concat(minNode);
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
        minNode = LOCK_SUFFIX.concat("/").concat(minNode);
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
        List<String> childrens = zookeeper.getChildren(LOCK_SUFFIX, false);
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
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch lantch = new CountDownLatch(50);
        for (int i = 0; i < 50; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ZookeeperLock lock = new ZookeeperLock("127.0.0.1:2181", "/lock");
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
