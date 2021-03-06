package com.wujk.hibernate.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 单数据源
 * @author CI11951
 *
 */
public class HibernateHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(HibernateHelper.class);
	
	private SessionFactory sessionFactory;
	
	private ThreadLocal<Session> threadLocal = new ThreadLocal<Session>();
	
	public HibernateHelper() {
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("cfg/hibernate.cfg.xml").build();
		try {
			MetadataSources metadataSources = new MetadataSources(registry);
			sessionFactory = metadataSources.buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			logger.error(e.getMessage());
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}
	
	public void rebuildSessionFactory() {
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("cfg/hibernate.cfg.xml").build();
		try {
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			logger.error(e.getMessage());
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}
	
	public Session getSession() {
		Session session = threadLocal.get();
		if (session == null) {
			if (sessionFactory == null) {
				rebuildSessionFactory();
			}
			if (sessionFactory == null) {
				throw new RuntimeException("sessionFactory is null or destory");
			}
			session = sessionFactory.openSession();
			threadLocal.set(session);
		}
		return session;
	}
	
	public Session getCurrentSession() {
		Session session = threadLocal.get();
		if (session == null) {
			if (sessionFactory == null) {
				rebuildSessionFactory();
			}
			if (sessionFactory == null) {
				throw new RuntimeException("sessionFactory is null or destory");
			}
			session = sessionFactory.getCurrentSession();
			threadLocal.set(session);
		}
		return session;
	}
	
	public void closeSession() {
		Session session = threadLocal.get();
		if (session != null) {
			session.close();
		}
	}
	
	public void commitTransaction() {
		Session session = threadLocal.get();
		if (session != null) {
			session.getTransaction().commit();
		}
	}
	
	public void beginTransaction() {
		Session session = threadLocal.get();
		if (session != null) {
			session.beginTransaction();
		}
	}
	
	public void rollBackTransaction() {
		Session session = threadLocal.get();
		if (session != null) {
			session.getTransaction().rollback();
		}
	}
	public static void main(String[] args) {
		new HibernateHelper();
	}
	
}
