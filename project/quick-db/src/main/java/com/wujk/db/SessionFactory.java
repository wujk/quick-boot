package com.wujk.db;

import javax.sql.DataSource;

public interface SessionFactory<T> {

    public T createSessionFactory(DataSource dataSource) throws Exception;

    public T getBaseSessionFactory();

    public void closeSessionFactory(T sessionFactory);

}
