package com.wujk.spring.db;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Component
public class Transactional {

    private ThreadLocal<DefaultTransactionDefinition> d = new ThreadLocal<DefaultTransactionDefinition>();

    private ThreadLocal<TransactionStatus> t = new ThreadLocal<TransactionStatus>();

    private ThreadLocal<PlatformTransactionManager> p = new ThreadLocal<PlatformTransactionManager>();

    public void createTransactional(PlatformTransactionManager platformTransactionManager) {
        TransactionStatus ts = t.get();
        if (ts == null) {
            DefaultTransactionDefinition def = d.get();
            if (def == null) {
                def = new DefaultTransactionDefinition();
                d.set(def);
            }
            PlatformTransactionManager _platformTransactionManager = p.get();
            if (_platformTransactionManager == null) {
                _platformTransactionManager = platformTransactionManager;
                p.set(_platformTransactionManager);
            }
            ts = _platformTransactionManager.getTransaction(def);
            t.set(ts);
        }
    }

    public void commit() {
        check();

    }

    public void rollback() {
        check();
        p.get().rollback(t.get());
    }

    private void check() {
        PlatformTransactionManager platformTransactionManager = p.get();
        if (platformTransactionManager == null) {
            throw new RuntimeException("PlatformTransactionManager is null");
        }
        TransactionStatus ts = t.get();
        if (ts == null) {
            throw new RuntimeException("TransactionStatus is null");
        }
    }
}
