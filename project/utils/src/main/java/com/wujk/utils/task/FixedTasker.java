package com.wujk.utils.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FixedTasker implements Runnable {
	public static final ArrayList<FixedTask> nodo = new ArrayList<FixedTask>();
	public static final ArrayList<FixedTask> running = new ArrayList<FixedTask>();
	private static Lock lock = new ReentrantLock();// 锁对象
	private static Condition join = lock.newCondition();
	private static Condition take = lock.newCondition();
	public static boolean over = false;
	private static ScheduledExecutorService exec = Executors.newScheduledThreadPool(10);
	
	public static void addTask(FixedTask task) {
		lock.lock();
		try {
			nodo.add(task);
			take.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	public static void removeTask(FixedTask task) {
		lock.lock();
		try {
			nodo.remove(task);
			join.signalAll();
			take.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public static int getTaskQuantity() {
		return nodo.size();
	}

	public void startTask(FixedTasker tasker) {
		over = false;
		//read();
		exec.execute(tasker);
		System.out.println("Start FixedTask.....");
	}
	
	public static void stopTimerTask(String id) {
		lock.lock();
		try {
			Iterator<FixedTask> ites = running.iterator();
			while (ites.hasNext()) {
				FixedTask runn = ites.next();
				if (id.equals(runn.getId())) {
					runn.stopSchedule();
					ites.remove();
				}
			}
			join.signalAll();
			take.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
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
		//save();
		for (FixedTask runns : running)
			runns.stopSchedule();
		running.clear();
		nodo.clear();
		exec.shutdown();
		System.out.println("End FixedTask.....");
	}
	
	@Override
	public void run() {
		while (!over) {
			lock.lock();
			try {
				while (nodo.isEmpty() && !over) {
					take.await();
				}
				if (over)
					break;
				
				FixedTask task = nodo.remove(0);
				task.startSchedule();
				running.add(task);
				join.signalAll();
				take.signalAll();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
		System.out.println("Finished FixedTask");
	}
	
	public static void save() {
		try{
			FixedTask[] objNoDo = new FixedTask[nodo.size()];
			nodo.toArray(objNoDo);
			FixedTask[] objRun = new FixedTask[running.size()];
			running.toArray(objRun);
			String packageName = FixedTasker.class.getPackage().getName();
			String dir = packageName.replaceAll("\\.", "/");
			Enumeration<URL> urls = FixedTasker.class.getClassLoader().getResources(dir);
			while (urls.hasMoreElements()) {
				URL url = (URL) urls.nextElement();
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(url.getFile(), "nodo.xlh")));
				oos.writeObject(objNoDo);
				oos.close();
				oos = new ObjectOutputStream(new FileOutputStream(new File(url.getFile(), "run.xlh")));
				oos.writeObject(objRun);
				oos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void read() {
		try{
			String packageName = FixedTasker.class.getPackage().getName();
			String dir = packageName.replaceAll("\\.", "/");
			Enumeration<URL> urls = FixedTasker.class.getClassLoader().getResources(dir);
			while (urls.hasMoreElements()) {
				URL url = (URL) urls.nextElement();
				File file = new File(url.getFile(), "nodo.xlh"); 
				if (file.exists()) {
					ObjectInputStream oos = new ObjectInputStream(new FileInputStream(file));
					FixedTask[] objNoDo = (FixedTask[])oos.readObject();
					oos.close();
					nodo.addAll(Arrays.asList(objNoDo));
				}
				file = new File(url.getFile(), "run.xlh"); 
				if (file.exists()) {
					ObjectInputStream oos = new ObjectInputStream(new FileInputStream(new File(url.getFile(), "run.xlh")));
					FixedTask[] objRun = (FixedTask[])oos.readObject();
					oos.close();
					nodo.addAll(Arrays.asList(objRun));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void reload(List<FixedTask> newTasks) {
		lock.lock();
		try{
			for (FixedTask task : newTasks) {
				String id = task.getId();
				Iterator<FixedTask> ites = running.iterator();
				while (ites.hasNext()) {
					FixedTask runn = ites.next();
					if (id.equals(runn.getId())) {
						runn.stopSchedule();
						ites.remove();
					}
				}
				ites = nodo.iterator();
				while (ites.hasNext()) {
					FixedTask runn = ites.next();
					if (id.equals(runn.getId())) {
						ites.remove();
					}
				}
			}
			nodo.addAll(newTasks);	
			join.signalAll();
			take.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

}
