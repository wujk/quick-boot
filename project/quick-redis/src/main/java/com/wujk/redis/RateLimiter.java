package com.wujk.redis;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wujk.utils.date.StopWatch;
import com.wujk.utils.pojo.ObjectUtil;
import com.wujk.utils.thread.ThreadUtil;

/**
 * 令牌桶
 * @author CI11951
 *
 */
public class RateLimiter extends RedisCount<Double> {
	private static final Logger logger = LoggerFactory.getLogger(RedisCount.class);
	
	private BigDecimal rate = new BigDecimal(5.0); // 每秒产生的速率
	private BigDecimal take = new BigDecimal(1);
	private BigDecimal total;
	private long timeout = 5;   // 5秒超时时间
	private volatile long refreshTime = System.currentTimeMillis();
	
	private StopWatch stopWatch;
	
	public RateLimiter() {
		this(5D);
	}
	
	public RateLimiter(Double rate) {
		this(rate, 5, "limiter", 0D, 100D);
	}
	
	public RateLimiter(Double rate, long timeout) {
		this(rate, timeout, "limiter", 0D, 100D);
	}
	
	public RateLimiter(Double rate, Double take, long timeout) {
		this(take, rate, timeout, "limiter", 0D, 100D);
	}

	public RateLimiter(Double rate, long timeout, String key, Double floor, Double top) {
		this(key, floor, top);
		this.rate = new BigDecimal(rate);
	}
	
	public RateLimiter(Double take, Double rate, long timeout, String key, Double floor, Double top) {
		this(rate, timeout, key, floor, top);
		this.take = new BigDecimal(take);
	}
	
	public RateLimiter(String key, Double floor, Double top) {
		super(key, floor, top, -1);
		getRedisUtil().getRedisTemplate().opsForValue().set(getKey(), top);
	}
	
	private void refreshTokens() {
		long diff = stopWatch.getMs();
		refreshTime = stopWatch.getEndTime();
		logger.info(Thread.currentThread().getName() + "：" + (diff / 1000) + "s后：");
		BigDecimal increateCount = rate.multiply(new BigDecimal(diff).divide(new BigDecimal(1000)));
		total = new BigDecimal(ObjectUtil.getValue(getRedisUtil().getRedisTemplate().opsForValue().get(getKey()), getFloor()));
		total = total.add(increateCount);
		if (total.doubleValue() > getTop()) {
			total = new BigDecimal(getTop());
		}
	}
	
	public synchronized boolean tryAcquire() {
		stopWatch = new StopWatch(refreshTime);
		refreshTokens();
		boolean acquire = count(take.doubleValue());
		getRedisUtil().getRedisTemplate().opsForValue().set(getKey(), total.doubleValue());
		return acquire;
	}
	
	public synchronized boolean acquire() {
		stopWatch = new StopWatch(refreshTime, timeout * 1000);
		refreshTokens();
		boolean acquire = false;
		while (!(acquire = count(take.doubleValue()))) {
			ThreadUtil.sleep(1000);
			refreshTokens();
			if (stopWatch.checkTimeout()) {
				logger.info("timeOut.......");
				break;
			}
		}
		getRedisUtil().getRedisTemplate().opsForValue().set(getKey(), total.doubleValue());
		return acquire;
	}
	
	@Override
	public boolean count(Double delta) {
		delta = Math.abs(delta);
		logger.info("令牌总数：{}，需消耗：{}", total, delta);
		if (total.subtract(new BigDecimal(delta)).compareTo(new BigDecimal(getTop())) > 0 || total.subtract(new BigDecimal(delta)).compareTo(new BigDecimal(getFloor())) < 0) {
			logger.info("wait。。。。。");
			return false;
		}
		total = total.subtract(new BigDecimal(delta));
		return true;
	}
	
	public static void main(String[] args) {
		RateLimiter limiter = new RateLimiter(5D);
		for (int a= 0; a < 10000; a++) {
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					System.out.println(limiter.acquire());
				}
			}).start();
		}
	}
	
}
