package com.wujk.spring.db.jta.aop;

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
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import java.util.Map;

@Component
@Aspect
public class AtomikosTransactionAop extends dbAop {
    private Logger logger = LoggerFactory.getLogger(AtomikosTransactionAop.class);

    @Resource(name = "atomikosJta")
    private PlatformTransactionManager platformTransactionManager;

    @Resource(name = "springContextUtils")
    private SpringContextUtils springContextUtils;

    @Autowired
    private Transactional transactional;

    @Pointcut("@annotation(com.wujk.spring.db.jta.AtomikosEnable)")
    private void transactional() {
    }

    @Around("transactional()")
    public Object tx(ProceedingJoinPoint pjp) {
        try {
            AtomikosEnable atomikosEnable = ((MethodSignature)pjp.getSignature()).getMethod().getAnnotation(AtomikosEnable.class);
            String transactionManagerName = atomikosEnable.transactionManagerName();
            logger.info("transactionManagerName：" + transactionManagerName);
            if (!"atomikosJta".equals(transactionManagerName)) {
                platformTransactionManager = (PlatformTransactionManager)springContextUtils.getBeanById(transactionManagerName);
                if (platformTransactionManager == null) {
                    throw new RuntimeException(transactionManagerName + "事务管理器不存在");
                }
            }
            return invoke(pjp, transactional, platformTransactionManager);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
