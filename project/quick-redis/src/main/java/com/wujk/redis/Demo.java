package com.wujk.redis;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import com.wujk.redis.util.RedisUtil;

public class Demo {

	public static void main(String[] args) throws IOException {
		RedisUtil redisUtil = new RedisUtil();
		Collection<String> clusterNodes = new HashSet<>();
		clusterNodes.add("192.168.140.159:7001");
		clusterNodes.add("192.168.140.159:7002");
		clusterNodes.add("192.168.140.159:7003");
		clusterNodes.add("192.168.140.159:7004");
		clusterNodes.add("192.168.140.159:7005");
		clusterNodes.add("192.168.140.159:7006");
		redisUtil.setClusterNodes(clusterNodes );
		Integer a = (Integer) redisUtil.getRedisTemplateCluster().opsForValue().get("a");
		redisUtil.destoryCluster();
		//redisUtil.getRedisTemplateCluster().opsForValue().set("b", 10000);
		
		/*String a = redisUtil.getJedisCluster().get("a");
		
		redisUtil.getJedisCluster().close();*/
		System.out.println(a);
		a = (Integer) redisUtil.getRedisTemplateCluster().opsForValue().get("a");
		System.out.println(a);
		redisUtil.destoryCluster();
		a = (Integer) redisUtil.getRedisTemplateCluster().opsForValue().get("a");
		System.out.println(a);
		redisUtil.destoryCluster();
	}

}
