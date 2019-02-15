package com.wujk.redis.util;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.data.redis.connection.RedisClusterConfiguration;
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
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis操作工具包
 * @author CI11951
 *
 */
public class RedisUtil {
	
	// 普通操作类
	private RedisTemplate<String, Object> redisTemplate;  
	private Jedis jedis;  
	private JedisConnectionFactory jedisConnectionFactory;
	private RedisStandaloneConfiguration redisStandaloneConfiguration;
	
	// 集群操作类
	private RedisTemplate<String, Object> redisTemplateCluster;  
	private JedisCluster jedisCluster;
	private JedisConnectionFactory jedisConnectionFactoryCluster;
	private RedisClusterConfiguration redisClusterConfiguration;
	private Collection<String> clusterNodes = new HashSet<String>(); //节点node
	
	private JedisClientConfiguration jedisClientConfiguration;   // 构建线程池
	private JedisPoolConfig jedisPoolConfig;   // 线程池信息
	
	private int maxIdle = 10;
	private long maxWaitMillis = 10000000;
	private boolean testOnBorrow = true;
	private String hostName = "192.168.140.159";
	private int port = 6379;
	private int index = 0;
	private int maxTotal = 5;
	private String password = "pass";
	
	public RedisTemplate<String, Object> redisTemplateCluster() {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer=new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(jedisConnectionFactoryCluster());
        StringRedisSerializer rs=new StringRedisSerializer();
        redisTemplate.setKeySerializer(rs);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(rs);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

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
	
	public JedisCluster jedisCluster() {
		return jedisCluster = (JedisCluster) jedisConnectionFactoryCluster().getClusterConnection().getNativeConnection();
	}
	
	public JedisConnectionFactory jedisConnectionFactory() {
		return jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration(), jedisClientConfiguration());
	}
	
	public JedisConnectionFactory jedisConnectionFactoryCluster() {
		jedisConnectionFactoryCluster = new JedisConnectionFactory(redisClusterConfiguration(), jedisClientConfiguration());
		jedisConnectionFactoryCluster.afterPropertiesSet();
		return jedisConnectionFactoryCluster;
	}
	
	private RedisClusterConfiguration redisClusterConfiguration() {
		redisClusterConfiguration = new RedisClusterConfiguration(clusterNodes);
		redisClusterConfiguration.setPassword(password);
		return redisClusterConfiguration;
	}

	public RedisStandaloneConfiguration redisStandaloneConfiguration() {
		redisStandaloneConfiguration = new RedisStandaloneConfiguration(hostName, port);
		redisStandaloneConfiguration.setDatabase(index);
		redisStandaloneConfiguration.setPassword(password);
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
	
	public synchronized RedisTemplate<String, Object> getRedisTemplateCluster() {
		if (redisTemplateCluster == null) {
			redisTemplateCluster = redisTemplateCluster();
		}
		return redisTemplateCluster;
	}

	public void setRedisTemplateCluster(RedisTemplate<String, Object> redisTemplateCluster) {
		this.redisTemplateCluster = redisTemplateCluster;
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
	
	public synchronized JedisCluster getJedisCluster() {
		if (jedisCluster == null) {
			jedisCluster = jedisCluster();
		}
		return jedisCluster;
	}

	public void setJedisCluster(JedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
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

	public synchronized JedisConnectionFactory getJedisConnectionFactoryCluster() {
		if (jedisConnectionFactoryCluster == null) {
			jedisConnectionFactoryCluster = jedisConnectionFactoryCluster();
		}
		return jedisConnectionFactoryCluster;
	}

	public void setJedisConnectionFactoryCluster(JedisConnectionFactory jedisConnectionFactoryCluster) {
		this.jedisConnectionFactoryCluster = jedisConnectionFactoryCluster;
	}

	public RedisClusterConfiguration getRedisClusterConfiguration() {
		if (redisClusterConfiguration == null) {
			redisClusterConfiguration = redisClusterConfiguration();
		}
		return redisClusterConfiguration;
	}

	public void setRedisClusterConfiguration(RedisClusterConfiguration redisClusterConfiguration) {
		this.redisClusterConfiguration = redisClusterConfiguration;
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
	
	public void destory() {
		if (jedisConnectionFactory != null) {
			jedisConnectionFactory.destroy();
			jedisConnectionFactory = null;
		}
		if (redisTemplate != null) {
			redisTemplate = null;
		}
	}
	
	public void destoryCluster() {
		if (jedisConnectionFactoryCluster != null) {
			jedisConnectionFactoryCluster.destroy();
			jedisConnectionFactoryCluster = null;
		}
		if (redisTemplateCluster != null) {
			redisTemplateCluster = null;
		}
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

	public Collection<String> getClusterNodes() {
		return clusterNodes;
	}

	public void setClusterNodes(Collection<String> clusterNodes) {
		this.clusterNodes = clusterNodes;
	}
	
}
