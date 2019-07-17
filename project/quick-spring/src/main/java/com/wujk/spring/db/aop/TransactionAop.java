package com.wujk.spring.db.aop;

import com.wujk.spring.db.Transactional;
import com.wujk.spring.db.dbAop;
import com.wujk.spring.db.jta.AtomikosEnable;
import com.wujk.spring.util.SpringContextUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Aspect
public class TransactionAop extends dbAop {
    private Logger logger = LoggerFactory.getLogger(TransactionAop.class);

    @Resource(name = "transactionManager")
    private DataSourceTransactionManager dataSourceTransactionManager;

    @Resource(name = "springContextUtils")
    private SpringContextUtils springContextUtils;

    @Autowired
    private Transactional transactional;

    @Pointcut("@annotation(com.wujk.spring.db.DataSourceEnable)")
    private void transactional() {
    }

    @Around("transactional()")
    public Object beforeInsertMethods(ProceedingJoinPoint pjp) {
        try {
            AtomikosEnable atomikosEnable = ((MethodSignature)pjp.getSignature()).getMethod().getAnnotation(AtomikosEnable.class);
            String transactionManagerName = atomikosEnable.transactionManagerName();
            logger.info("transactionManagerName：" + transactionManagerName);
            if (!"transactionManager".equals(transactionManagerName)) {
                dataSourceTransactionManager = (DataSourceTransactionManager)springContextUtils.getBeanById(transactionManagerName);
                if (dataSourceTransactionManager == null) {
                    throw new RuntimeException(transactionManagerName + "事务管理器不存在");
                }
            }
            return invoke(pjp, transactional, dataSourceTransactionManager);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

}
