package com.wujk.mybatis.db;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.wujk.spring.db.DataBase;
import com.wujk.spring.db.DataBaseManager;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.util.List;
import java.util.UUID;

/**
 * mybatais多数据源
 */
public class MybatisMutiXAManager extends MybatisMutiManager {
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
    public DataSource createDataSource(DataBase dataBase) {
        return createAtomikosDataSourceBean((XADataSource)createDataSource(dataBase));
    }

    public AtomikosDataSourceBean createAtomikosDataSourceBean(XADataSource xaDataSource) {
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setXaDataSource(xaDataSource);
        atomikosDataSourceBean.setUniqueResourceName(UUID.randomUUID().toString());
        return atomikosDataSourceBean;
    }

    @Override
    public Object getDataSource(SqlSessionFactory sessionFactory) {
        return ((AtomikosDataSourceBean) sessionFactory.getConfiguration().getEnvironment().getDataSource()).getXaDataSource();
    }

}
