package com.wujk.utils.task;

import java.io.Serializable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class DelayedTask implements Delayed, Runnable, Serializable {
	
	private static final long serialVersionUID = 3579355574737991977L;
	public String id; // 标记任务id
	public long trigger = 0L; // 触发时间
	private int delta;
	public Object[] objs;

	public DelayedTask() {
		super();
	}

	public DelayedTask(String id, int delayInSeconds) {
		this(id, delayInSeconds, new Object[] {});
	}

	public DelayedTask(String id, int delayInSeconds, Object... objects) {
		super();
		this.id = id;
		this.delta = delayInSeconds;
		this.trigger = System.nanoTime()
				+ TimeUnit.NANOSECONDS.convert(delta,
						TimeUnit.MILLISECONDS);
		this.objs = objects;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getTrigger() {
		return trigger;
	}

	public void setTrigger(long trigger) {
		this.trigger = trigger;
	}

	public int getDelta() {
		return delta;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}

	public Object[] getObjs() {
		return objs;
	}

	public void setObjs(Object[] objs) {
		this.objs = objs;
	}
	
	@Override
	public void run() {
		try {
			excute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public abstract void excute();

	@Override
	public int compareTo(Delayed o) {
		DelayedTask task = (DelayedTask) o;
		if (trigger < task.trigger)
			return -1;
		if (trigger > task.trigger)
			return 1;
		return 0;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(trigger - System.nanoTime(),
				TimeUnit.NANOSECONDS);
	}

}