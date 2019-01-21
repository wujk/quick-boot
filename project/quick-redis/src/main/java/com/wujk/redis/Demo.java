package com.wujk.redis;

public class Demo extends RedisCount<Long> {

	public Demo(String key, Long floor, Long top) {
		super(key, floor, top);
	}
	
	public static void main(String[] args) {
		Demo demo = new Demo("a", 0L, 10L);
		for (int a= 0; a < 100; a++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					demo.count(1L);
				}
			}).start();
		}
		
	}

}
