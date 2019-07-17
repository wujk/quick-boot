package com.wujk.spring.db;

import com.wujk.spring.db.jta.AtomikosEnable;
import com.wujk.spring.util.SpringContextUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;

@Component
@Aspect
public class TransactionAop {
    private Logger logger = LoggerFactory.getLogger(TransactionAop.class);

    @Resource(name = "transactionManager")
    private DataSourceTransactionManager dataSourceTransactionManager;

    @Resource(name = "springContextUtils")
    private SpringContextUtils springContextUtils;

    @Pointcut("@annotation(com.wujk.spring.db.DataSourceEnable)")
    private void transactional() {
    }

    @Around("transactional()")
    public Object beforeInsertMethods(ProceedingJoinPoint pjp) throws Exception {
        AtomikosEnable atomikosEnable = ((MethodSignature)pjp.getSignature()).getMethod().getAnnotation(AtomikosEnable.class);
        String transactionManagerName = atomikosEnable.transactionManagerName();
        logger.info("transactionManagerName：" + transactionManagerName);
        if (!"transactionManager".equals(transactionManagerName)) {
            dataSourceTransactionManager = (DataSourceTransactionManager)springContextUtils.getBeanById(transactionManagerName);
            if (dataSourceTransactionManager == null) {
                throw new RuntimeException(transactionManagerName + "事务管理器不存在");
            }
        }
        Object obj = null;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus ts = dataSourceTransactionManager.getTransaction(def);
        try {
            obj = pjp.proceed();
            dataSourceTransactionManager.commit(ts);
        } catch (Throwable e) {
            dataSourceTransactionManager.rollback(ts);
            logger.error(e.getMessage(), e);
            throw new Exception(e);
        } finally {
            return obj;
        }
    }
}
