package com.wujk.db;

import com.wujk.utils.cache.LruCache;
import com.wujk.utils.date.DateUtil;
import com.wujk.utils.pojo.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 *
* @ClassName: LocalCache
* @Description: 持久化内存，超时校验数据连接2小时可以做成可配置现在还没
* @author kevin
* @date 2019年3月8日 下午2:49:49
 */
public abstract class SpringDataBaseManager<T, S, D> implements SessionFactory<T>, Session<S>, DataSource<D> {

	private Logger logger = LoggerFactory.getLogger(SpringDataBaseManager.class);

	// 加载数据源与校验数据源超时需要互斥，为了获取数据源效率用读写锁，读锁可以多个线程同时加载，写锁只能一个线程获取
	private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	private boolean isholder = true; // 是否开启持久缓存 默认10个大小

	private LruCache<String> cache = null;

	public SpringDataBaseManager() {
		this(true, 30, null);
	}

	public SpringDataBaseManager(boolean isholder) {
		this(isholder, 30, null);
	}

	public SpringDataBaseManager(int number) {
		this(true, number, null);
	}

	public SpringDataBaseManager(boolean isholder, int number, T sqlSessionFactory) {
		this.isholder = isholder;
		if (isholder) {
			logger.info("@@@@@@开启持久缓存，缓存大小" + number + "@@@@@@");
			this.cache = new LruCache<String>(number);
		}
		this.sqlSessionFactory = sqlSessionFactory;
	}
	protected  T sqlSessionFactory;

	protected Map<String, Map<String, Object>> factoryMap = new ConcurrentHashMap<>();

	private static final String TTL = "TTL";

	private static final String FACTORY = "FACTORY";

	protected ThreadLocal<S> sessionMap = new ThreadLocal<S>();

	private Long expireTimeOfFactory = 7200000L;  // 2小时超时时间

	private Long peroidOfFactory = 3600000L;    // 1小时校验时间

	private Long OPERATE_PEROID = 300000L;     // 5分钟

	Timer timmer = new Timer(true);

	public void setSqlSessionFactory(T sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	/**
	 *
	* @Title: checkFactoryExpire
	* @Description: 数据源监听超时立即删除
	* @author kevin
	* @date 2019年3月7日 下午3:01:24 void
	* @throws
	 */
	public void checkFactoryExpire() {
		logger.info("##############设置数据源监听###################");
		timmer.schedule(new TimerTask() {

			@Override
			public void run() {
				logger.info("执行数据源校验>>>>>>>>>>>>>>>");
				Set<String> keys = factoryMap.keySet();
				Long now = System.currentTimeMillis();
				for (String key : keys) {
					logger.info("校验数据库："+key);
					WriteLock writeLock = readWriteLock.writeLock();
					try {
						writeLock.lock();
						Map<String, Object> map = factoryMap.get(key);
						boolean isCheck = true;
						if (isholder) {
							boolean isExit = cache.containsKey(key);
							isCheck = !isExit;
							logger.info("持久缓存中是否存在：hr_saas" + key + "：" + isExit);
						}
						if (isCheck && map != null) {
							Long ttl = ObjectUtil.getValue(map.get(TTL), 0L);
							boolean isTimeOut = now > ttl;
							logger.info("当前时间：" + DateUtil.format(new Date(now), DateUtil.FORMAT_DATE_YMDHMS) + "，超时时间：" + DateUtil.format(new Date(ttl), DateUtil.FORMAT_DATE_YMDHMS) + "，是否超时：" + isTimeOut);
							if (isTimeOut) { // 超时并且无缓存记录的开始删除数据源
								T sessionFactory = (T) map.get(FACTORY);
								if (sessionFactory != null) {
									closeSessionFactory(sessionFactory);
								}
							}
						}
					} catch (Exception e) {
						logger.info("校验数据库："+key + "，错误信息：" + e.getMessage(), e);
					} finally {
						writeLock.unlock();
					}
				}
			}
		}, 0, peroidOfFactory);
	}

	/**
	 *
	* @Title: getSqlSessionFactory
	* @Description: 获取SqlSessionFactory
	* @author kevin
	* @date 2019年3月7日 下午3:00:57
	* @param dataBaseId
	* @return SqlSessionFactory
	* @throws
	 */
	public <T> T getSqlSessionFactory(String dataBaseId) {
		ReadLock readLock = readWriteLock.readLock();
		try {
			readLock.lock();
			if (dataBaseId == null || "".equals(dataBaseId)) {
				return (T) sqlSessionFactory;
			}
			if (factoryMap.get(dataBaseId) == null) {
				synchronized (FACTORY) {
					if (factoryMap.get(dataBaseId) == null) {
						addDatasource(dataBaseId);
					}
				}
			}
			Map<String, Object> factory = factoryMap.get(dataBaseId);
			if (factory == null) {
				return (T) sqlSessionFactory;
			}
			T sessionFactory = (T) factory.get(FACTORY);
			if (sessionFactory == null) {
				return (T) sqlSessionFactory;
			}
			synchronized (TTL) {
				loadCache(dataBaseId, factory);
			}
			return sessionFactory;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return (T) sqlSessionFactory;
		} finally {
			readLock.unlock();
		}

	}

	/**
	 * 缓存策略
	 * @param dataBaseId
	 * @param factory
	 */
	public void loadCache(String dataBaseId, Map<String, Object> factory) {
		Long oldTTL = ObjectUtil.getValue(factory.get(TTL), 0L); // 获取上次超时时间
		Long newTTL = System.currentTimeMillis() + expireTimeOfFactory; // 获取本次超时时间
		Long diff = newTTL - oldTTL;  // 两次时间相减获取访问间隔时间
		factory.put(TTL, diff < 0 ? oldTTL : newTTL); // 设置超时时间
		if (isholder) { // 开启缓存
			try  {
				if (diff <= OPERATE_PEROID && cache.get(dataBaseId) == null) { // 如果小于5分钟则放入持久缓存
					logger.info("放入持久缓存，5分钟内使用多次" + dataBaseId);
					cache.put(dataBaseId, dataBaseId);
				} else if (diff > OPERATE_PEROID && cache.containsKey(dataBaseId)) {
					logger.info("移除持久缓存，5分钟内未使用" + dataBaseId);
					cache.remove(dataBaseId);
				}
			} catch (Exception e) {
				logger.error("清除持久缓存：" + e.getMessage(), e);
				cache.clear();
			}
		}
	}

	/**
	 *
	* @Title: getBaseSqlSessionFactory
	* @Description: 获取公共库数据源
	* @author kevin
	* @date 2019年3月7日 下午3:00:07
	* @return SqlSessionFactory
	* @throws
	 */
	public <T> T getBaseSqlSessionFactory() {
		return (T) sqlSessionFactory;
	}

	public S getCurrentSqlSession() {
		return (S) sessionMap.get();
	}

	public void setCurrentSqlSession(S session) {
		sessionMap.set(session);
	}

	public Map<String, T> getMap() {
		Map<String, T> resultMap = new HashMap<>();
		Set<String> keys = factoryMap.keySet();
		for (String key : keys) {
			Map<String, Object> map = factoryMap.get(key);
			if (map != null) {
				resultMap.put(key, (T) map.get(FACTORY));
			}
		}
		return resultMap;
	}

	public ThreadLocal<S> getSessionMap() {
		return sessionMap;
	}

	public void setSessionMap(ThreadLocal<S> sessionMap) {
		this.sessionMap = sessionMap;
	}

	/**
	 *
	* @Title: storageDatasource
	* @Description: 初始化所有数据源
	* @author kevin
	* @date 2019年3月7日 下午2:48:36 void
	* @throws
	 */
	public void storageDatasource() {
		logger.info("【storageDatasource】加载所有数据源>>>>>>>start>>>>>>>>");
		ReadLock readLock = readWriteLock.readLock();
		try {
			readLock.lock();
			List<DataBase> dataBases = findDatabase();
			for (DataBase dataBase : dataBases) {
				if (factoryMap.get(dataBase.getDataBaseId()) == null) {
					loadDataSource(dataBase);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			readLock.unlock();
			logger.info("【storageDatasource】加载所有数据源>>>>>>end>>>>>>>");
		}

	}

	/**
	 *
	* @Title: addDatasource
	* @Description: 加载数据源
	* @author kevin
	* @date 2019年3月7日 下午2:48:14
	* @param dataBaseId void
	* @throws
	 */
	public void addDatasource(String dataBaseId) {
		DataBase dataBase = findDatabaseById();
		loadDataSource(dataBase);
	}

    /**
     *
     * @Title: getDatabaseCount
     * @Description: 获取数据源数量
     * @author Jelly
     * @date 2019年5月9日 下午 16:52
     * @throws
     */
	public int getDatabaseCount(){
		return factoryMap.size();
	}

    /**
     *
     * @Title: getDatabaseLog
     * @Description: 返回使用数据源详细
     * @author Jelly
     * @date 2019年5月10日 下午 9:36
     * @throws
     */
    public String getDatabaseLog(){
        return factoryMap.keySet().toString();
    }

	/**
	 *
	* @Title: loadDataSource
	* @Description: 加载数据源
	* @author kevin
	* @date 2019年3月7日 下午3:02:50
	* @param dataBase
	* @return boolean
	* @throws
	 */
	private boolean loadDataSource(DataBase dataBase) {
		try {
			if (dataBase == null) {
				logger.info("数据库配置不存在");
				return false;
			}
			DataSource dataSource = createDataSource(dataBase);
			if (dataSource == null) {
				logger.info("初始化数据源失败：" + dataBase.getDataBaseId());
				return false;
			}
			// 加入sqlfactory缓存
			Map<String, Object> map = new HashMap<>();
			map.put(FACTORY, sessionFactory());
			factoryMap.put(dataBase.getDataBaseId(), map);
			logger.info("数据库初始化完成：" + dataBase.getDataBaseId());
			return true;
		} catch (Exception e) {
			logger.info("初始化数据源失败：" + dataBase.getDataBaseId());
			logger.info(e.getMessage(), e);
			return false;
		}
	}

	/**
	 *
	 * @Title: findDatabase
	 * @Description: 查找所有可用数据源
	 * @author kevin
	 * @date 2019年3月7日 下午2:49:07
	 * @return List<String>
	 * @throws
	 */
	public abstract List<DataBase> findDatabase();

	public abstract DataBase findDatabaseById();

}
