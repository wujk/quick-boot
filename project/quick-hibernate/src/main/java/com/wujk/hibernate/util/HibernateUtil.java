package com.wujk.hibernate.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
	
	private static SessionFactory sessionFactory;
	
	static {
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
		try {
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			logger.error(e.getMessage());
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}
	
	public static void rebuildSessionFactory() {
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
		try {
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			logger.error(e.getMessage());
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}
	
	public static Session getSession() {
		if (sessionFactory == null) {
			rebuildSessionFactory();
		}
		if (sessionFactory == null) {
			throw new RuntimeException("sessionFactory is null or destory");
		}
		return sessionFactory.openSession();
	}
	
	public static Session getCurrentSession() {
		if (sessionFactory == null) {
			rebuildSessionFactory();
		}
		if (sessionFactory == null) {
			throw new RuntimeException("sessionFactory is null or destory");
		}
		return sessionFactory.getCurrentSession();
	}
	
	public static void closeSession(Session session) {
		if (session != null) {
			session.close();
		}
	}
	
	public static void commitTransaction(Session session) {
		if (session != null) {
			session.getTransaction().commit();
		}
	}
	
	public static void beginTransaction(Session session) {
		if (session != null) {
			session.beginTransaction();
		}
	}
	
	public static void rollBackTransaction(Session session) {
		if (session != null) {
			session.getTransaction().rollback();
		}
	}

}
