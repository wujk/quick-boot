package com.wujk.redis;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wujk.redis.util.RedisUtil;
import com.wujk.utils.pojo.ObjectUtil;
import com.wujk.utils.pojo.Reflections;

/**
 *  计数器
 * @author CI11951
 *
 * @param <T>
 */
public class RedisCount<T> {
	
	private static final Logger logger = LoggerFactory.getLogger(RedisCount.class);
	
	private T floor;
	
	private T top;
	
	private String key;
	
	private RedisUtil redisUtil;
	
	private Class<?> generateType;
	
	private long expire = -1L;

	public RedisCount(String key, T floor, T top) {
		this(key, floor, top, -1L);
	}

	public RedisCount(String key, T floor, T top, long expire) {
		this(key, floor, top, expire, null);
	}

	public RedisCount(String key, T floor, T top, long expire, RedisUtil redisUtil) {
		super();
		generateType = Reflections.getSuperClassGenricType(this.getClass(), 0);
		logger.info("泛型：{}", generateType);
		this.floor = floor;
		this.top = top;
		this.key = key;
		this.expire = expire;
		if (redisUtil == null) {
			redisUtil = new RedisUtil();
		}
		this.redisUtil = redisUtil;
	}

	public T getFloor() {
		return floor;
	}

	public void setFloor(T floor) {
		this.floor = floor;
	}

	public T getTop() {
		return top;
	}

	public void setTop(T top) {
		this.top = top;
	}

	public RedisUtil getRedisUtil() {
		return redisUtil;
	}

	public void setRedisUtil(RedisUtil redisUtil) {
		this.redisUtil = redisUtil;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public long getExpire() {
		return expire;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}

	public boolean count(T delta) {
		redisUtil.getRedisTemplate().opsForValue().setIfAbsent(key, floor, expire, TimeUnit.MILLISECONDS);
		if (Long.class.isAssignableFrom(generateType) || Integer.class.isAssignableFrom(generateType) || Short.class.isAssignableFrom(generateType)) {
			Long now = redisUtil.getRedisTemplate().opsForValue().increment(key, ObjectUtil.getValue(delta, 0L));
			if (ObjectUtil.getValue(floor, 0L).compareTo(now) > 0) {
				logger.info("增加后的值：{}======{}", now, false);
				return false;
			}
			if (ObjectUtil.getValue(top, 0L).compareTo(now) < 0) {
				logger.info("增加后的值：{}======{}", now, false);
				return false;
			}
			logger.info("增加后的值：{}======{}", now, true);
			return true;
		} else if (Double.class.isAssignableFrom(generateType) || Float.class.isAssignableFrom(generateType)) {
			Double now = redisUtil.getRedisTemplate().opsForValue().increment(key, ObjectUtil.getValue(delta, 0.0));
			if (ObjectUtil.getValue(floor, 0D).compareTo(now) > 0) {
				logger.info("增加后的值：{}======{}", now, false);
				return false;
			}
			if (ObjectUtil.getValue(top, 0D).compareTo(now) < 0) {
				logger.info("增加后的值：{}======{}", now, false);
				return false;
			}
			logger.info("增加后的值：{}======{}", now, true);
			return true;
		}
		return false;
	}

}
