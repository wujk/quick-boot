package com.wujk.spring.util;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("springContextUtils")
public class SpringContextUtils implements ApplicationContextAware {
	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * 获取applicationContext对象
	 * 
	 * @return
	 */
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 根据bean的id来查找对象
	 * 
	 * @param id
	 * @return
	 */
	public Object getBeanById(String id) {
		return applicationContext.getBean(id);
	}

	/**
	 * 根据bean的class来查找对象
	 * 
	 * @param c
	 * @return
	 */
	public <T> T getBeanByClass(Class<T> c) {
		return applicationContext.getBean(c);
	}

	/**
	 * 根据bean的class来查找所有的对象(包括子类)
	 * 
	 * @param c
	 * @return
	 */
	public <T> Map<String, T> getBeansByClass(Class<T> c) {
		return applicationContext.getBeansOfType(c);
	}

	/**
	 * 获取当前环境
 	 */
	public String[] getActiveProfiles() {
		return applicationContext.getEnvironment().getActiveProfiles();
	}
}
