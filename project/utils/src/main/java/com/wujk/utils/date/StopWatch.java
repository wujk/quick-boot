package com.wujk.utils.date;
/**
 * 时间监控
 * @author CI11951
 *
 */
public class StopWatch {
	
	private long startTime;
	private long timeoutMills;
	private long endTime;

	public StopWatch() {
		this(System.currentTimeMillis());
	}
	
	public StopWatch(long startTime) {
		this(startTime, 5000);
	}
	
	public StopWatch(long startTime, long timeoutMills) {
		this.startTime = startTime;
		this.timeoutMills = timeoutMills;
	}

	public long getMs() {
		return (endTime = System.currentTimeMillis()) - startTime;
	}

	public void reset() {
		startTime = System.currentTimeMillis();
	}
	
	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public boolean checkTimeout(long timeoutMills) {
		return getMs() > timeoutMills;
	}
	
	public boolean checkTimeout() {
		return checkTimeout(timeoutMills);
	}
}
