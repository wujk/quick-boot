package com.wujk.spring.db;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.transaction.PlatformTransactionManager;

public class dbAop {

    protected Object invoke(ProceedingJoinPoint pjp, Transactional transactional, PlatformTransactionManager platformTransactionManager) {
        Object obj = null;
        try {
            transactional.createTransactional(platformTransactionManager);
            obj = pjp.proceed();
            transactional.commit();
        } catch (Throwable e) {
            transactional.rollback();
            throw new Exception(e);
        } finally {
            return obj;
        }
    }

}
