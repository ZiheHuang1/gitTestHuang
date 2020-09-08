package com.za;

import sun.tools.jconsole.Worker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author huangzihe
 * @date 2020/8/19 9:16 下午
 */
public class ThreadPool {
    // 任务队列
    private BlockingQueue<Runnable> blockingDeque = new BlockingQueue<>();
    // 线程集合
    private Set<Worker> set =  new HashSet<>();
    // 超时时间
    private long time;
    // 时间单位
    private TimeUnit timeUnit;
    // 核心线程数
    private int coreSize = 2;
    // 最大线程数
    private int maxSize;
    // 线程工厂
    private ThreadFactory  threadFactory;
    // 拒绝策略
    private RejectedExecutionException executionException;

    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool();
        threadPool.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("hh");
        });
        threadPool.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("aa");
        });
        threadPool.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("cc");
        });
        threadPool.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("d");
        });
    }

    /**
     * 执行任务
     * @param runnable
     */
    public void execute(Runnable runnable) {
        if (set.size() < coreSize) {
            Worker worker = new Worker(runnable);
            set.add(worker);
            worker.start();
        } else {
            System.out.println("加队列");
            blockingDeque.put(runnable);
        }
    }

    /**
     *
     */
    class Worker extends Thread {
        private Runnable task;
        public Worker(Runnable task) {
            this.task = task;
        }
        @Override
        public void run() {
            while (task != null || (task = blockingDeque.poll(3, TimeUnit.SECONDS)) != null) {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }
            synchronized (set) {
                set.remove(this);
            }

        }
    }
}


class BlockingQueue<T> {
    // 队列
    private Deque<T> queue = new ArrayDeque();
    // 锁
    private Lock lock = new ReentrantLock();
    // 满条件
    private Condition fullCondition = lock.newCondition();
    //空条件
    private Condition emptyCondition = lock.newCondition();
    // 大小
    private int size = 1;



    public void tryPut(RejectPolicy rejectPolicy, T task) {
        lock.lock();
        try {
            if (queue.size() == size) {
                rejectPolicy.reject(this, task);
            } else {
                queue.addLast(task);
                emptyCondition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 带超时阻塞获取
     * @param time
     * @return
     */
    public T poll(int time, TimeUnit timeUnit) {
        lock.lock();
        try {
            long waitTime = timeUnit.toMillis(time);
            long endTime = System.currentTimeMillis();
            while (queue.isEmpty()) {
                try {
                    if (System.currentTimeMillis() > endTime) {
                        return null;
                    }
                    waitTime = endTime - System.currentTimeMillis();
                    emptyCondition.await(waitTime, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T task = queue.removeFirst();
            fullCondition.signalAll();
            return task;
        } finally {
            lock.unlock();
        }
    }
    /**
     * 无限等待型poll
     * @return
     */
    public T poll() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    emptyCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T task = queue.removeFirst();
            fullCondition.signalAll();
            return task;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 无限等待型put
     * @param t
     */
    public void put(T t) {
        lock.lock();
        try {
            while (size == queue.size()) {
                try {
                    System.out.println("等待加入队列");
                    fullCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(t);
            emptyCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 超时等待型
     */

}
@FunctionalInterface
interface RejectPolicy<T> {
    void reject(BlockingQueue<T> queue, T task);
}