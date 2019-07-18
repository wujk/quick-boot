package com.wujk.mybatis.db.distributed.aop.group;

import com.wujk.mybatis.db.distributed.tx.MybatisZookeeperTx;
import com.wujk.spring.db.distributed.DistributedEnable;
import com.wujk.spring.db.distributed.tx.ZookeeperTx;
import com.wujk.zookeeper.util.ZookeeperLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Aspect
public class DistributedGroupAop {

    private Logger logger = LoggerFactory.getLogger(DistributedGroupAop.class);

    private final static String clientId = "locked";


    @Pointcut("@annotation(com.wujk.spring.db.distributed.DistributedEnable) && args(groupId)")
    private void transactionalGroup(String groupId) {
    }

    @Around("transactionalGroup(groupId)")
    public Object createTransactionalGroup(ProceedingJoinPoint pjp, String groupId) {
        Object obj = null;
        ZookeeperLock lock = null;
        MybatisZookeeperTx tx = null;
        try {
            DistributedEnable distributedEnable = ((MethodSignature)pjp.getSignature()).getMethod().getAnnotation(DistributedEnable.class);
            if (groupId == null) {
                groupId = distributedEnable.groupId();
                if ("".equals(groupId)) {
                    groupId = UUID.randomUUID().toString();
                }
            }
            logger.info("groupId:" + groupId);
            Object[] args = pjp.getArgs();
            args[0] = groupId;
            lock = new ZookeeperLock("127.0.0.1:2181",  groupId, clientId);
            tx = new MybatisZookeeperTx("127.0.0.1:2181", lock.getGroup());
            obj = pjp.proceed(args);
            if(lock.lockWatcher()) {
                String nodeData = tx.getNodeData();
                if (ZookeeperTx.TxStatus.NORMAL.value.equals(nodeData)) {
                    tx.setNodeData(ZookeeperTx.TxStatus.SUCCESS.value);
                } else {
                    tx.setNodeData(ZookeeperTx.TxStatus.ERROR.value);
                }
            }
        } catch (Exception e) {
            String nodeData = tx.getNodeData();
            if (ZookeeperTx.TxStatus.ABNORMAL.value.equals(nodeData)) {
                tx.setNodeData(ZookeeperTx.TxStatus.ERROR.value);
            }
            logger.error(e.getMessage(), e);
        } finally {
            lock.unlock();
            tx.close();
            return obj;
        }
    }

}

