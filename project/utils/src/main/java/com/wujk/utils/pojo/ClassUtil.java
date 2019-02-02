package com.wujk.utils.pojo;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.wujk.utils.pojo.ObjectUtil;

/**
 * 在理解这两种反射机制之前，需要弄清楚java类的加载机制.
 * 
 * 装载：通过类的全限定名获取二进制字节流（二进制的class文件），将二进制字节流转换成方法区中的运行时数据结构，在内存中生成Java.lang.class对象。这个时候该类型没有被分配内存，设置默认值，也没有初始化。
 * 
 * 链接：执行下面的校验、准备和解析步骤，其中解析步骤是可以选择的；
 * 
 * 校验：检查导入类或接口的二进制数据的正确性；（文件格式验证，元数据验证，字节码验证，符号引用验证）
 * 
 * 准备：给类的静态变量分配并初始化存储空间；
 * 
 * 解析：将常量池中的符号引用转成直接引用；
 * 
 * 初始化：激活类的静态变量的初始化Java代码和静态Java代码块，并初始化程序员设置的变量值。
 * 
 * 1.Class.forName返回的Class对象可以决定是否初始化。而ClassLoader.loadClass返回的类型绝对不会初始化，最多只会做连接操作。
 * 2.Class.forName可以决定由哪个classLoader来请求这个类型。而ClassLoader.loadClass是用当前的classLoader去请求。
 * 
 * @author CI11951
 *
 */
public final class ClassUtil {

	/**
	 * 获取类加载器
	 */
	public static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	
	/**
	 * className:类的全限定名，如：com.org.prj
	 * initialize:如果为true，则会在返回Class对象之前，对该类型做连接，校验，初始化操作。(如：执行static块中的代码)，initialize默认需要初始化。
	 * loader:用自定义的类加载器来请求这个类型；当然，你也可以传入null，用bootstrap加载器
	 * 
	 * 由于Class.forName默认是需要初始化，一旦初始化，就会触发目标对象的 static块代码执行，static参数也也会被再次初始化。
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> classForName(String className) throws ClassNotFoundException {
		return Class.forName(className);
	}
	
	public static Class<?> classForName(String className, boolean initialize) throws ClassNotFoundException {
		return classForName(className, initialize);
	}

	public static Class<?> classForName(String className, boolean initialize, ClassLoader loader)
			throws ClassNotFoundException {
		if (loader == null) {
			loader = getClassLoader();
		}
		return Class.forName(className, initialize, loader);
	}

	/**
	 * className:类的全限定名，如：com.org.prj
	 * 
	 * resolve：表示是否需要连接该类型。 仅仅是连接（这里面包括校验class文件，准备分配内存，类型常量池的替换），并不会初始化该类型。
	 * 
	 * resolve默认是不链接，不进行链接意味着不进行包括初始化等一些列步骤，那么静态块和静态对象就不会得到执行。
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> loadClass(String className) throws ClassNotFoundException {
		return getClassLoader().loadClass(className);
	}
	
	/**
	 * 获取指定包名下的所有类
	 */
	public static Set<Class<?>> getClassSet(String packageName) {
		Set<Class<?>> classSet = new HashSet<Class<?>>();
		try {
			Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				if (url != null) {
					String protocol = url.getProtocol();
					if (protocol.equals("file")) {
						String packagePath = url.getPath().replaceAll("%20", " ");
						addClass(classSet, packagePath, packageName);
					} else if (protocol.equals("jar")) {
						JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
						if (jarURLConnection != null) {
							JarFile jarFile = jarURLConnection.getJarFile();
							if (jarFile != null) {
								Enumeration<JarEntry> jarEntries = jarFile.entries();
								while (jarEntries.hasMoreElements()) {
									JarEntry jarEntry = jarEntries.nextElement();
									String jarEntryName = jarEntry.getName();
									if (jarEntryName.endsWith(".class")) {
										String className = jarEntryName.substring(0, jarEntryName.lastIndexOf("."))
												.replaceAll("/", ".");
										doAddClass(classSet, className);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return classSet;
	}

	private static void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
		File[] files = new File(packagePath).listFiles(new FileFilter() {
			public boolean accept(File file) {
				return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
			}
		});
		for (File file : files) {
			String fileName = file.getName();
			if (file.isFile()) {
				String className = fileName.substring(0, fileName.lastIndexOf("."));
				if (!ObjectUtil.isEmpty(packageName)) {
					className = packageName + "." + className;
				}
				doAddClass(classSet, className);
			} else {
				String subPackagePath = fileName;
				if (!ObjectUtil.isEmpty(packagePath)) {
					subPackagePath = packagePath + "/" + subPackagePath;
				}
				String subPackageName = fileName;
				if (!ObjectUtil.isEmpty(packageName)) {
					subPackageName = packageName + "." + subPackageName;
				}
				addClass(classSet, subPackagePath, subPackageName);
			}
		}
	}

	private static void doAddClass(Set<Class<?>> classSet, String className) {
		try {
			Class<?> cls = classForName(className, false);
			classSet.add(cls);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
