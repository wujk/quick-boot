package com.wujk.utils.date;
/**
 * 时间监控
 * @author CI11951
 *
 */
public class StopWatch {
	
	private long startTime;
	private long timeoutMills;

	public StopWatch() {
		this(5000);
	}
	
	public StopWatch(long timeoutMills) {
		startTime = System.currentTimeMillis();
		this.timeoutMills = timeoutMills;
	}

	public long getMs() {
		return System.currentTimeMillis() - startTime;
	}

	public void reset() {
		startTime = System.currentTimeMillis();
	}
	
	public long getStartTime() {
		return startTime;
	}

	public boolean checkTimeout(long timeoutMills) {
		return getMs() > timeoutMills;
	}
	
	public boolean checkTimeout() {
		return checkTimeout(timeoutMills);
	}
}
