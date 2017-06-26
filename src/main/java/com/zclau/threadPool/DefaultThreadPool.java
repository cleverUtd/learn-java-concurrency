package com.zclau.threadPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by liuzicong on 25/6/2017.
 */
public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {

    private static final int MAX_WORKER_SIZE = 10;
    private static final int DEFAULT_WORKER_SIZE = 5;
    private static final int MIN_WORKER_SIZE = 1;

    //任务队列
    private final LinkedList<Job> jobs = new LinkedList<>();
    //工作者队列
    private final List<Worker> workers = Collections.synchronizedList(new ArrayList<>());
    //线程编号生成
    private AtomicLong threadNum = new AtomicLong();

    public DefaultThreadPool() {
        initializeWorkers(DEFAULT_WORKER_SIZE);
    }

    public DefaultThreadPool(int num) {
        int workerNum = num > MAX_WORKER_SIZE ? MAX_WORKER_SIZE : num < MIN_WORKER_SIZE ? MIN_WORKER_SIZE : num;
        initializeWorkers(workerNum);
    }

    private void initializeWorkers(int num) {
        for (int i = 0; i < num; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.incrementAndGet());
            thread.start();
        }
    }


    @Override
    public void execute(Job job) {
        if (job != null) {
            synchronized (jobs) {
                jobs.addLast(job);
                jobs.notify();
            }
        }
    }

    @Override
    public void shutdown() {
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }

    @Override
    public void addWorkers(int num) {
        synchronized (jobs) {
            if (num + workers.size() > MAX_WORKER_SIZE) {
                num = MAX_WORKER_SIZE - workers.size();
            }

            initializeWorkers(num);
        }
    }

    @Override
    public void removeWorker(int num) {
        if(num >= workers.size()) {
            throw new IllegalArgumentException("beyond workNum");
        }

        int count = 0;
        while (count < num) {
            Worker worker = workers.get(count);
            if (workers.remove(worker)) {
                worker.shutdown();
                count++;
            }
        }
    }

    @Override
    public int getJobSize() {
        return jobs.size();
    }



    /**
     * 工作者，负责消费任务
     */
    public class Worker implements Runnable {

        //是否工作
        private volatile boolean running = true;

        @Override
        public void run() {
            while (running) {
                Job job;
                synchronized (jobs) {
                    while (jobs.isEmpty()) {
                        try {
                            jobs.wait();
                        } catch (InterruptedException e) {
                            //感知到外部对WorkerThread的中断操作，返回
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    job = jobs.removeFirst();
                }
                if (job != null) {
                    try {
                        job.run();
                    } catch (Exception e) {
                        //忽略 Job 执行中的Exception
                    }
                }
            }
        }

        public void shutdown() {
            running = false;
        }
    }
}
