package com.wujk.db;

import javax.sql.DataSource;
import java.sql.SQLException;

public interface DataSourcePool {

    /**
     * 获取连接池
     * @param dataBase
     * @return
     */
    public DataSource getDataSourceFromFactory(DataBase dataBase);

    /**
     * 创建连接池
     * @param dataBase
     * @return
     */
    public DataSource createDataSource(DataBase dataBase) throws SQLException;


}
