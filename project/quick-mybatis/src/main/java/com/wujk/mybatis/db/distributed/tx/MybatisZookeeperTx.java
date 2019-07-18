package com.wujk.mybatis.db.distributed.tx;

import com.wujk.mybatis.db.distributed.aop.group.DistributedGroupAop;
import com.wujk.spring.db.distributed.tx.ZookeeperTx;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MybatisZookeeperTx extends ZookeeperTx<SqlSession> {

    private Logger logger = LoggerFactory.getLogger(MybatisZookeeperTx.class);

    public MybatisZookeeperTx(String address, String node) {
        super(address, node);
    }

    @Override
    public void commit(SqlSession session) {
        try {
            session.commit();
            boolean sessionIsClosed = session.getConnection().isClosed();
            logger.info("session is closed: " + sessionIsClosed);
            session.close();
        } catch (Exception e) {
            logger.error("MybatisZookeeperTx.commit:" + e.getMessage(), e);
        }

    }

    @Override
    public void rollback(SqlSession session) {
        try {
            session.rollback();
            boolean sessionIsClosed = session.getConnection().isClosed();
            logger.info("session is closed: " + sessionIsClosed);
            session.close();
        } catch (Exception e) {
            logger.error("MybatisZookeeperTx.rollback:" + e.getMessage(), e);
        }
    }
}
