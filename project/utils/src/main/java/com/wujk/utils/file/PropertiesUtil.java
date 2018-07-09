package com.wujk.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Set;

import com.wujk.utils.constant.MARKS;
import com.wujk.utils.pojo.ObjectUtil;

/**
 * Properties操作
 * @author eeesys
 *
 */
public class PropertiesUtil {
	
	/**
	 * 获取系统属性值
	 * @param attr
	 * @return
	 */
	public static String getSyetemAttributeValue(String attr) {
		return System.getProperty(attr, "no this attribute");
	}
	
	/**
	 * 获取系统属性的健
	 * @return
	 */
	public static Set<Object> getSyetemAttributes() {
		return System.getProperties().keySet();
	}
	
	/**
	 * 通过类获取包下的Properties
	 * @param clazz
	 * @param filename
	 * @return
	 */
	public static Properties getPackageProperties(Class<?> clazz, String filename) {
		Properties pro = getProperties(FileUtil.getPackagePath(clazz) + MARKS.SLASH.getValue() + filename);
		return ObjectUtil.isEmpty(pro) ? new Properties() : pro;
	}
	
	/**
	 * 通过系统下的Properties
	 * @param filename
	 * @return
	 */
	public static Properties getSystemProperties(String filename) {
		Properties pro = getProperties(FileUtil.getSystemPath() + MARKS.SLASH.getValue() + filename);
		return ObjectUtil.isEmpty(pro) ? new Properties() : pro;
	}
	
	/**
	 * 通过默认路径下的Properties
	 * @param filename
	 * @return
	 */
	public static Properties getDefaultProperties(String filename) {
		Properties pro = getProperties(FileUtil.getDefaultPath() + MARKS.SLASH.getValue() + filename);
		return ObjectUtil.isEmpty(pro) ? new Properties() : pro;
	}
	
	/**
	 * 文件路径获取Properties
	 * @return
	 */
	public static Properties getProperties(String path) {
		File file = new File(path);
		return getProperties(file);
	}
	
	/**
	 * 文件获取Properties
	 * @param file
	 * @return
	 */
	public static Properties getProperties(File file) {
		Properties pro = new Properties();
		try {
			pro.load(new FileInputStream(file));
			return pro;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 根据类赋值
	 * @param clazz
	 * @param filename
	 * @param key
	 * @param value
	 */
	public static void setPackageProperties(Class<?> clazz, String filename, String key, String value) {
		setProperties(FileUtil.getPackagePath(clazz) + MARKS.SLASH.getValue() + filename, key, value);
	}
	
	/**
	 * 系统路径赋值
	 * @param filename
	 * @param key
	 * @param value
	 */
	public static void setSystemProperties(String filename, String key, String value) {
		try {
			setProperties(FileUtil.getSystemPath() + MARKS.SLASH.getValue() + filename, key, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 默认路径赋值
	 * @param filename
	 * @param key
	 * @param value
	 */
	public static void setDefaultProperties(String filename, String key, String value) {
		try {
			setProperties(FileUtil.getDefaultPath() + MARKS.SLASH.getValue() + filename, key, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 根据文件路径赋值
	 * @param path
	 * @param key
	 * @param value
	 */
	public static void setProperties(String path, String key, String value) {
		File file = new File(path);
		setProperties(file, key, value);
	}
	
	/**
	 * 根据文件赋值
	 * @param file
	 * @param key
	 * @param value
	 */
	public static void setProperties(File file, String key, String value) {
		Properties pro = new Properties();
		pro.setProperty(key, value);
		try {
			pro.store(new FileOutputStream(file, true), "###");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
