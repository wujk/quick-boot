package com.wujk.spring.db.inter;

import com.wujk.spring.db.DataBase;

import java.sql.SQLException;

public interface DataSourcePool {

    /**
     * 获取连接池
     * @param dataBase
     * @return
     */
    public Object getDataSourceFromFactory(DataBase dataBase);

    /**
     * 创建连接池
     * @param dataBase
     * @return
     */
    public Object createDataSource(DataBase dataBase) throws SQLException;


}
