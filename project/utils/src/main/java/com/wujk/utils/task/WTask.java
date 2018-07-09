package com.wujk.utils.task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class WTask {
    // 开始的倒数锁
    private CountDownLatch begin = null;
    // 结束的倒数锁
    private CountDownLatch end = null;
    private ExecutorService executor = null;
    private Object[] result;

    public WTask(int taskNum) {
        super();
        begin = new CountDownLatch(1);
        end = new CountDownLatch(taskNum);
        executor = Executors.newFixedThreadPool(taskNum <= 0 ? 1 : taskNum);
        result = new Object[taskNum];
    }

    public void submitTask(Runnable runnable) {
        executor.submit(runnable);
    }

    public void runTask() {
        begin.countDown();
        try {
            end.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

    public Object[] getResult() {
        return result;
    }

    public abstract class  Operate {
        public abstract Object operate(Object... objects);
    }

    class MyRunnable implements Runnable {
        private Operate operate;
        private Object[] objects;
        private int index;

        public MyRunnable(Operate operate, int index, Object... objects) {
            this.operate = operate;
            this.objects = objects;
            this.index = index;
        }

        @Override
        public void run() {
            try {
                begin.await();
                result[index] = operate.operate(objects);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                end.countDown();
            }
        }
    }
}
