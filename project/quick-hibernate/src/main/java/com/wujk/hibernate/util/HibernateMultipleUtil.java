package com.wujk.hibernate.util;

import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wujk.utils.constant.MARKS;
import com.wujk.utils.file.FileUtil;
import com.wujk.utils.file.PropertiesUtil;
import com.wujk.utils.pojo.ObjectUtil;
/**
 * 单数据源
 * @author CI11951
 *
 */
public class HibernateMultipleUtil {
	
	private static final String DBNAME = "db_name";
	
	private static final Logger logger = LoggerFactory.getLogger(HibernateMultipleUtil.class);
	
	private static final ConcurrentHashMap<String, SessionFactory> dataSoures = new ConcurrentHashMap<String, SessionFactory>();
	
	private static ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();
	
	static {
		String path = FileUtil.getPath("cfg/dataSources.properties");
		Properties pro = PropertiesUtil.getSystemProperties("cfg/dataSources.properties");
		Enumeration<?> enumeration = pro.propertyNames();
		if (!enumeration.hasMoreElements()) {
			throw new RuntimeException("请配置" + path);
		}
		String dbNames = pro.getProperty(DBNAME);
		if (ObjectUtil.isEmpty(dbNames)) {
			throw new RuntimeException("请配置" + path);
		}
		String[] strs = dbNames.split(MARKS.COMMA.getValue());
		for (String str : strs) {
			String dbConfigPath = pro.getProperty(str);
			final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure(dbConfigPath).build();
			try {
				MetadataSources metadataSources = new MetadataSources(registry);
				SessionFactory sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
				dataSoures.put(str, sessionFactory);
			} catch (Exception e) {
				logger.error(e.getMessage());
				StandardServiceRegistryBuilder.destroy(registry);
			}
		}
		
	}
	
	public static void rebuildAllSessionFactory() {
		String path = FileUtil.getPath("cfg/dataSources.properties");
		Properties pro = PropertiesUtil.getSystemProperties("cfg/dataSources.properties");
		Enumeration<?> enumeration = pro.propertyNames();
		if (!enumeration.hasMoreElements()) {
			throw new RuntimeException("请配置" + path);
		}
		String dbNames = pro.getProperty(DBNAME);
		if (ObjectUtil.isEmpty(dbNames)) {
			throw new RuntimeException("请配置" + path);
		}
		String[] strs = dbNames.split(MARKS.COMMA.getValue());
		for (String str : strs) {
			String dbConfigPath = pro.getProperty(str);
			final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure(dbConfigPath).build();
			try {
				MetadataSources metadataSources = new MetadataSources(registry);
				SessionFactory sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
				dataSoures.put(str, sessionFactory);
			} catch (Exception e) {
				logger.error(e.getMessage());
				StandardServiceRegistryBuilder.destroy(registry);
			}
		}
	}
	
	public static void rebuildSessionFactory(String dbName) {
		String path = FileUtil.getPath("cfg/dataSources.properties");
		Properties pro = PropertiesUtil.getSystemProperties("cfg/dataSources.properties");
		Enumeration<?> enumeration = pro.propertyNames();
		if (!enumeration.hasMoreElements()) {
			throw new RuntimeException("请配置" + path);
		}
		String dbConfigPath = pro.getProperty(dbName);
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure(dbConfigPath).build();
		try {
			MetadataSources metadataSources = new MetadataSources(registry);
			SessionFactory sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
			dataSoures.put(dbName, sessionFactory);
		} catch (Exception e) {
			logger.error(e.getMessage());
			StandardServiceRegistryBuilder.destroy(registry);
		}
		
	}
	
	public static Session getSession(String dbName) {
		Session session = threadLocal.get();
		if (session == null) {
			SessionFactory sessionFactory = dataSoures.get(dbName);
			if (sessionFactory == null) {
				rebuildSessionFactory(dbName);
			}
			sessionFactory = dataSoures.get(dbName);
			if (sessionFactory == null) {
				throw new RuntimeException("sessionFactory is null or destory");
			}
			session = sessionFactory.openSession();
			threadLocal.set(session);
		}
		return session;
	}
	
	public static Session getCurrentSession(String dbName) {
		Session session = threadLocal.get();
		if (session == null) {
			SessionFactory sessionFactory = dataSoures.get(dbName);
			if (sessionFactory == null) {
				rebuildSessionFactory(dbName);
			}
			sessionFactory = dataSoures.get(dbName);
			if (sessionFactory == null) {
				throw new RuntimeException("sessionFactory is null or destory");
			}
			session = sessionFactory.getCurrentSession();
			threadLocal.set(session);
		}
		return session;
	}
	
	public static void closeSession() {
		Session session = threadLocal.get();
		if (session != null) {
			session.close();
		}
	}
	
	public static void commitTransaction() {
		Session session = threadLocal.get();
		if (session != null) {
			session.getTransaction().commit();
		}
	}
	
	public static void beginTransaction() {
		Session session = threadLocal.get();
		if (session != null) {
			session.beginTransaction();
		}
	}
	
	public static void rollBackTransaction() {
		Session session = threadLocal.get();
		if (session != null) {
			session.getTransaction().rollback();
		}
	}
	
}
