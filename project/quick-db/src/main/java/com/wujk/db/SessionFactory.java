package com.wujk.db;

public interface SessionFactory<T> {

    public <T> T sessionFactory();

    public void closeSessionFactory(T sessionFactory);

}
