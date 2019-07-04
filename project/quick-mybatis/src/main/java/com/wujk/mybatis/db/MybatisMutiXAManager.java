package com.wujk.mybatis.db;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.wujk.db.DataBase;
import com.wujk.db.DataBaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * mybatais多数据源
 */
public class MybatisMutiXAManager<M> extends MybatisMutiManager<M> {
    private Logger logger = LoggerFactory.getLogger(DataBaseManager.class);

    @Override
    public List<DataBase> findDatabase() {
        return null;
    }

    @Override
    public DataBase findDatabaseById() {
        return null;
    }


    @Override
    public DataSource createDataSource(DataBase dataBase) throws SQLException {
        return createAtomikosDataSourceBean((XADataSource)createDataSource(dataBase));
    }

    public AtomikosDataSourceBean createAtomikosDataSourceBean(XADataSource xaDataSource) {
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setXaDataSource(xaDataSource);
        atomikosDataSourceBean.setUniqueResourceName(UUID.randomUUID().toString());
        return atomikosDataSourceBean;
    }

}
