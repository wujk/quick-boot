package com.wujk.spring.db.distributed.aop.client;

import com.wujk.mybatis.db.MybatisMutiManager;
import com.wujk.spring.db.DataBaseManager;
import com.wujk.spring.db.dbAop;
import com.wujk.spring.db.distributed.DistributedClientEnable;
import com.wujk.spring.db.distributed.tx.ZookeeperTx;
import com.wujk.zookeeper.util.ZookeeperLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class DistributedClientAop {

    private Logger logger = LoggerFactory.getLogger(DistributedClientAop.class);

    private final static String clientId = "locked";

    @Autowired
    private DataBaseManager dataBaseManager;

    @Pointcut("@annotation(com.wujk.spring.db.distributed.DistributedClientEnable) && args(groupId)")
    private void transactionalClient(String groupId) {
    }

    @Around("transactionalClient(groupId)")
    public Object createTransactionalClient(ProceedingJoinPoint pjp, String groupId) {
        Object obj = null;
        ZookeeperLock lock = null;
        ZookeeperTx tx = null;

        try {
            DistributedClientEnable distributedClientEnable = ((MethodSignature)pjp.getSignature()).getMethod().getAnnotation(DistributedClientEnable.class);
            if (groupId == null) {
                groupId = distributedClientEnable.groupId();
            }
            logger.info("groupId:" + groupId);
            lock = new ZookeeperLock("127.0.0.1:2181",  groupId, clientId);
            if (lock.lockWatcher()) {
                tx = new ZookeeperTx("127.0.0.1:2181", lock.getGroup());
                String nodeData = tx.getNodeData();
                if (!ZookeeperTx.TxStatus.ABNORMAL.value.equals(nodeData)) {
                    obj = pjp.proceed();
                    tx.setSessions(dataBaseManager.getSessionMap().get());
                    if (!ZookeeperTx.TxStatus.ABNORMAL.value.equals(nodeData) && !ZookeeperTx.TxStatus.NORMAL.value.equals(nodeData)) {
                        tx.setNodeData(ZookeeperTx.TxStatus.NORMAL.value);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            tx.setNodeData(ZookeeperTx.TxStatus.ABNORMAL.value);
        } finally {
            lock.unlock();
            return obj;
        }
    }
}

