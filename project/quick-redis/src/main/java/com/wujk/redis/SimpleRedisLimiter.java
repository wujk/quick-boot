package com.wujk.redis;

/**
 * 限流单位时间内可以访问多少次 如 10s内可以访问100次 new SimpleRedisLimiter("limiter", 0L, 100L, 10000L);
 * @author CI11951
 *
 */
public class SimpleRedisLimiter extends RedisCount<Long> {

	public SimpleRedisLimiter(String key, Long floor, Long top, long expire) {
		super(key, floor, top, expire);
	}

}
