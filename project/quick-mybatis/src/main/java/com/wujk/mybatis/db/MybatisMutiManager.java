package com.wujk.mybatis.db;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.wujk.db.DataBase;
import com.wujk.db.DataBaseManager;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class MybatisMutiManager extends DataBaseManager<SqlSessionFactory, SqlSession> {
    private Logger logger = LoggerFactory.getLogger(DataBaseManager.class);

    @Override
    public List<DataBase> findDatabase() {
        return null;
    }

    @Override
    public DataBase findDatabaseById() {
        return null;
    }

    private SqlSessionFactory getSqlSessionFactory(DataSource dataSource) throws Exception {
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resourcePatternResolver.getResources("classpath*:mapper/*/*.xml");
        factoryBean.setMapperLocations(resources);
        factoryBean.setTypeHandlersPackage("com.wujk.*.mapper");
        factoryBean.setDataSource(dataSource);
        if (interceptors.size() > 0) {
            Interceptor[] plugins = new Interceptor[interceptors.size()];
            interceptors.toArray(plugins);
            factoryBean.setPlugins(plugins);
        }
        return factoryBean.getObject();
    }


    @Override
    public SqlSessionFactory createSessionFactory(DataSource dataSource) throws Exception {
        return getSqlSessionFactory(dataSource);
    }

    @Override
    public void closeSessionFactory(SqlSessionFactory sessionFactory) {
        DruidXADataSource source = (DruidXADataSource) sessionFactory.getConfiguration().getEnvironment().getDataSource();
        logger.info("activeCount:" + source.getActiveCount());
        if (source != null && source.getActiveCount() <= 0) {
            logger.info("删除数据源：" + source.getUrl());
            source.close();
            source = null;
        }
    }

    @Override
    public DataSource getDataSourceFromFactory(DataBase dataBase) {
        return ((SqlSessionFactory)getSqlSessionFactory(dataBase.getDataBaseId())).getConfiguration().getEnvironment().getDataSource();
    }

    @Override
    public DataSource createDataSource(DataBase dataBase) {
        return null;
    }
}
