package com.wujk.db;

public interface DataSource<D> {

    /**
     * 获取连接池
     * @param dataBase
     * @return
     */
    public D getDataSource(DataBase dataBase);

    /**
     * 创建连接池
     * @param dataBase
     * @param <D>
     * @return
     */
    public <D> D createDataSource(DataBase dataBase);


}
