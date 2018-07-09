package com.wujk.utils.task;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DelayedTasker implements Runnable {
	public static final DelayQueue<DelayedTask> queue = new DelayQueue<DelayedTask>();
	private static Lock lock = new ReentrantLock();// 锁对象
	private static Condition join = lock.newCondition();
	private static Condition take = lock.newCondition();
	public static boolean over = false;
	private static ExecutorService exec = Executors.newCachedThreadPool();

	public static void addTask(DelayedTask task) {
		lock.lock();
		try {
			queue.put(task);
			take.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public static void removeTask(DelayedTask task) {
		lock.lock();
		try {
			queue.remove(task);
			join.signalAll();
			take.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public static int getTaskQuantity() {
		return queue.size();
	}

	public void startTask(DelayedTasker tasker) {
		over = false;
		exec.execute(tasker);
		System.out.println("Start DelayedTask.....");
	}

	public void stopTask() {
		lock.lock();
		try {
			over = true;
			join.signalAll();
			take.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		exec.shutdown();
		System.out.println("End DelayedTask.....");
		queue.clear();
	}

	public void run() {
		while (!over) {
			lock.lock();
			try {
				while (queue.isEmpty() && !over) {
					take.await();
				}
				if (over)
					break;
				queue.take().run();
				join.signalAll();
				take.signalAll();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
		System.out.println("Finished DelayedTask");
	}
}
