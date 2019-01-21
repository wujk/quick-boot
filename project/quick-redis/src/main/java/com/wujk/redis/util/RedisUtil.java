package com.wujk.redis.util;

import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {
	
	private RedisTemplate<String, Object> redisTemplate;
	private Jedis jedis;
	private JedisConnectionFactory jedisConnectionFactory;
	private JedisClientConfiguration jedisClientConfiguration;
	private RedisStandaloneConfiguration redisStandaloneConfiguration;
	private JedisPoolConfig jedisPoolConfig;
	
	private int maxIdle = 10;
	private long maxWaitMillis = 10000000;
	private boolean testOnBorrow = true;
	private String hostName = "192.168.140.215";
	private int port = 6379;
	private int index = 3;
	private int maxTotal = 5;
	private String password = "pass";

	public RedisTemplate<String, Object> redisTemplate() {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer=new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        StringRedisSerializer rs=new StringRedisSerializer();
        redisTemplate.setKeySerializer(rs);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(rs);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
	
	public Jedis jedis() {
		return jedis = (Jedis) jedisConnectionFactory().getConnection().getNativeConnection();
	}
	
	public JedisConnectionFactory jedisConnectionFactory() {
		return jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration(), jedisClientConfiguration());
	}
	
	public RedisStandaloneConfiguration redisStandaloneConfiguration() {
		redisStandaloneConfiguration = new RedisStandaloneConfiguration(hostName, port);
		redisStandaloneConfiguration.setDatabase(index);
		redisStandaloneConfiguration.setPassword(password );
		return redisStandaloneConfiguration;
	}
	
	public JedisClientConfiguration jedisClientConfiguration() {
		return jedisClientConfiguration = ((JedisClientConfiguration.JedisPoolingClientConfigurationBuilder)JedisClientConfiguration.builder()).poolConfig(jedisPoolConfig()).build();
	}
	
	public JedisPoolConfig jedisPoolConfig() {
		jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
		jedisPoolConfig.setTestOnBorrow(testOnBorrow);
		jedisPoolConfig.setMaxTotal(maxTotal);
		return jedisPoolConfig;
	}

	public synchronized RedisTemplate<String, Object> getRedisTemplate() {
		if (redisTemplate == null) {
			redisTemplate = redisTemplate();
		}
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public synchronized Jedis getJedis() {
		if (jedis == null) {
			jedis = jedis();
		}
		return jedis;
	}

	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}

	public synchronized JedisConnectionFactory getJedisConnectionFactory() {
		if (jedisConnectionFactory == null) {
			jedisConnectionFactory = jedisConnectionFactory();
		}
		return jedisConnectionFactory;
	}

	public void setJedisConnectionFactory(JedisConnectionFactory jedisConnectionFactory) {
		this.jedisConnectionFactory = jedisConnectionFactory;
	}

	public synchronized JedisClientConfiguration getJedisClientConfiguration() {
		if (jedisClientConfiguration == null) {
			jedisClientConfiguration = jedisClientConfiguration();
		}
		return jedisClientConfiguration;
	}

	public void setJedisClientConfiguration(JedisClientConfiguration jedisClientConfiguration) {
		this.jedisClientConfiguration = jedisClientConfiguration;
	}

	public synchronized RedisStandaloneConfiguration getRedisStandaloneConfiguration() {
		if (redisStandaloneConfiguration == null) {
			redisStandaloneConfiguration = redisStandaloneConfiguration();
		}
		return redisStandaloneConfiguration;
	}

	public void setRedisStandaloneConfiguration(RedisStandaloneConfiguration redisStandaloneConfiguration) {
		this.redisStandaloneConfiguration = redisStandaloneConfiguration;
	}

	public synchronized JedisPoolConfig getJedisPoolConfig() {
		if (jedisPoolConfig == null) {
			jedisPoolConfig = jedisPoolConfig();
		}
		return jedisPoolConfig;
	}

	public void setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {
		this.jedisPoolConfig = jedisPoolConfig;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public long getMaxWaitMillis() {
		return maxWaitMillis;
	}

	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}
	
}
