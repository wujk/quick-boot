package com.wujk.spring.db.distributed;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Component
public class Transactional {

    private DefaultTransactionDefinition defaultTransactionDefinition;

    private TransactionStatus transactionStatus;

    private PlatformTransactionManager platformTransactionManager;

    public void createTransactional(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
        defaultTransactionDefinition = new DefaultTransactionDefinition();
        transactionStatus = platformTransactionManager.getTransaction(defaultTransactionDefinition);
    }

    public void commit() {
        check();
        platformTransactionManager.commit(transactionStatus);
    }

    public void rollback() {
        check();
        platformTransactionManager.rollback(transactionStatus);
    }

    private void check() {
        if (platformTransactionManager == null) {
            throw new RuntimeException("PlatformTransactionManager is null");
        }
        if (transactionStatus == null) {
            throw new RuntimeException("TransactionStatus is null");
        }
    }
}
