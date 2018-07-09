package com.wujk.utils.task;

import java.io.Serializable;
import java.util.TimerTask;

import it.sauronsoftware.cron4j.Scheduler;

public abstract class FixedTask extends TimerTask implements Serializable {
	
	private static final long serialVersionUID = -4354115540466993324L;
	public String id; // 标记任务id
	public String trigger; // 触发时间
	public Object[] objs;
	private transient Scheduler scheduler;

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	public Object[] getObjs() {
		return objs;
	}

	public void setObjs(Object[] objs) {
		this.objs = objs;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void startSchedule() {
		if (scheduler == null) 
			scheduler = new Scheduler();
		scheduler.schedule(trigger, this);
		scheduler.start();
	}
	
	public void stopSchedule() {
		if (scheduler != null) {
			scheduler.stop();
			scheduler = null;
		}
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

	public FixedTask() {
		super();
	}
	
	public FixedTask(String id, String cron) {
		this(id, cron, new Object[] {});
	}
	
	public FixedTask(String id, String cron, Object... objects) {
		this.id = id;
		this.trigger = cron;
		this.objs = objects;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
