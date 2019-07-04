package com.wujk.mybatis.db;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.wujk.db.DataBase;
import com.wujk.db.DataBaseManager;
import com.wujk.utils.pojo.ObjectUtil;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * mybatais多数据源
 */
public class MybatisMutiManager<M> extends DataBaseManager<SqlSessionFactory, SqlSession> implements MapperInterface<M>{
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
    public M getMapper(Class<M> clazz, String dataBaseId) {
        SqlSession session = getCurrentSqlSession(dataBaseId);
        try {
            if (session == null || session.getConnection().isClosed()) {
                SqlSessionFactory sessionFactory = getSqlSessionFactory(dataBaseId);
                setCurrentSqlSession(dataBaseId, sessionFactory.openSession());
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return getCurrentSqlSession(dataBaseId).getMapper(clazz);
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
    public Object getDataSourceFromFactory(DataBase dataBase) {
        return ((SqlSessionFactory)getSqlSessionFactory(dataBase.getDataBaseId())).getConfiguration().getEnvironment().getDataSource();
    }

    @Override
    public Object createDataSource(DataBase dataBase) throws SQLException {
        DruidXADataSource dataSource = new DruidXADataSource();
        dataSource.setUrl(dataBase.getUrl());
        dataSource.setUsername(dataBase.getUserName());
        dataSource.setPassword(dataBase.getPassword());
        dataSource.setInitialSize(ObjectUtil.getValue(initialSize, 10));
        dataSource.setMinIdle(ObjectUtil.getValue(minIdle, 50));
        dataSource.setMaxActive(ObjectUtil.getValue(maxActive, 50));
        dataSource.setMaxWait(ObjectUtil.getValue(maxWait, 60000));
        dataSource.setTimeBetweenEvictionRunsMillis(ObjectUtil.getValue(timeBetweenEvictionRunsMillis, 60000));
        dataSource.setMinEvictableIdleTimeMillis(ObjectUtil.getValue(minEvictableIdleTimeMillis, 300000));
        dataSource.setValidationQuery(ObjectUtil.getValue(validationQuery, "SELECT 'x'"));
        dataSource.setTestWhileIdle(ObjectUtil.getValue(testWhileIdle, true));
        dataSource.setTestOnBorrow(ObjectUtil.getValue(testOnBorrow, false));
        dataSource.setTestOnReturn(ObjectUtil.getValue(testOnReturn, false));
        dataSource.setPoolPreparedStatements(ObjectUtil.getValue(poolPreparedStatements, true));
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(
                ObjectUtil.getValue(maxPoolPreparedStatementPerConnectionSize, 20));
        dataSource.setConnectionErrorRetryAttempts(ObjectUtil.getValue(connectionErrorRetryAttempts, 3));
        dataSource.setBreakAfterAcquireFailure(ObjectUtil.getValue(breakAfterAcquireFailure, true));
        dataSource.setDefaultAutoCommit(false);
        dataSource.setRemoveAbandoned(ObjectUtil.getValue(removeAbandoned, false));
        dataSource.setLogAbandoned(ObjectUtil.getValue(logAbandoned, true));
        dataSource.setRemoveAbandonedTimeout(ObjectUtil.getValue(removeAbandonedTimeout, 1800));
        dataSource.setFilters(ObjectUtil.getValue(filters, "stat"));
        return dataSource;
    }

    private Integer initialSize;

    private Integer minIdle;

    private Integer maxActive;

    private Integer maxWait;

    private Long timeBetweenEvictionRunsMillis;

    private Long minEvictableIdleTimeMillis;

    private String validationQuery;

    private Boolean testWhileIdle;

    private Boolean testOnBorrow;

    private Boolean testOnReturn;

    private Boolean poolPreparedStatements;

    private Integer maxPoolPreparedStatementPerConnectionSize;

    private Integer connectionErrorRetryAttempts;

    private Boolean breakAfterAcquireFailure;

    private Boolean removeAbandoned;

    private Boolean logAbandoned;

    private Integer removeAbandonedTimeout;

    private String filters;

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public void setMaxWait(Integer maxWait) {
        this.maxWait = maxWait;
    }

    public void setTimeBetweenEvictionRunsMillis(Long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public void setMinEvictableIdleTimeMillis(Long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public void setTestWhileIdle(Boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public void setTestOnReturn(Boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public void setPoolPreparedStatements(Boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
    }

    public void setMaxPoolPreparedStatementPerConnectionSize(Integer maxPoolPreparedStatementPerConnectionSize) {
        this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
    }

    public void setConnectionErrorRetryAttempts(Integer connectionErrorRetryAttempts) {
        this.connectionErrorRetryAttempts = connectionErrorRetryAttempts;
    }

    public void setBreakAfterAcquireFailure(Boolean breakAfterAcquireFailure) {
        this.breakAfterAcquireFailure = breakAfterAcquireFailure;
    }

    public void setRemoveAbandoned(Boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public void setLogAbandoned(Boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }

    public void setRemoveAbandonedTimeout(Integer removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

}
