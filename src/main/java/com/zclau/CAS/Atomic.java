package com.zclau.CAS;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by liuzicong on 20/6/2017.
 */
public class Atomic {

    private static final int MAX_THREADS = 3;
    private static final int TASK_COUNT = 3;
    private static final int TARGET_COUNT = 10000000;

    private AtomicLong acount = new AtomicLong(0L);
    private long count = 0;

    static CountDownLatch cdlsync = new CountDownLatch(TASK_COUNT);
    static CountDownLatch cdlatomic = new CountDownLatch(TASK_COUNT);


    private synchronized long inc() {
        return ++count;
    }

    private synchronized long getCount() {
        return count;
    }

    public void clearCount() {
        count = 0;
    }

    public class SyncThread implements Runnable {
        private String name;
        private long starttime;
        Atomic out;

        public SyncThread(long starttime, Atomic out) {
            this.starttime = starttime;
            this.out = out;
        }

        @Override
        public void run() {
            long v = out.getCount();
            while (v < TARGET_COUNT) {
                v = out.inc();
            }
            long endtime = System.currentTimeMillis();
            System.out.println("SyncThread spend: " + (endtime - starttime) + " ms" + " v=" + v);
            cdlsync.countDown();
        }
    }

    public void testSync() throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(MAX_THREADS, MAX_THREADS, 15, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(100));
        long starttime = System.currentTimeMillis();
        SyncThread sync = new SyncThread(starttime, this);
        for (int i = 0; i < TASK_COUNT; i++) {
            executor.submit(sync);
        }
        cdlsync.await();
        executor.shutdown();
    }


    public class AtomicThread implements Runnable {

        private String name;
        private long starttime;

        public AtomicThread(long starttime) {
            this.starttime = starttime;
        }

        @Override
        public void run() {
            long v = acount.get();
            while (v < TARGET_COUNT) {
                v = acount.incrementAndGet();
            }
            long endtime = System.currentTimeMillis();
            System.out.println("AtomicThread spend: " + (endtime - starttime) + " ms" + " v=" + v);
            cdlatomic.countDown();
        }
    }

    public void testAtomic() throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(MAX_THREADS, MAX_THREADS, 15, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(100));
        long starttime = System.currentTimeMillis();
        AtomicThread atomic = new AtomicThread(starttime);
        for (int i = 0; i < TASK_COUNT; i++) {
            executor.submit(atomic);
        }
        cdlatomic.await();
        executor.shutdown();
    }


    private LongAdder lacount = new LongAdder();
    static CountDownLatch cdladdr = new CountDownLatch(TASK_COUNT);

    public class LongAddrThreead implements Runnable {

        private long starttime;

        public LongAddrThreead(long starttime) {
            this.starttime = starttime;
        }

        @Override
        public void run() {
            long v = lacount.sum();
            while (v < TARGET_COUNT) {
                lacount.increment();
                v = lacount.sum();
            }
            long endtime = System.currentTimeMillis();
            System.out.println("LongAddr spend: " + (endtime - starttime) + " ms" + " v=" + v);
            cdladdr.countDown();
        }
    }

    public void testAtomicLong() throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(MAX_THREADS, MAX_THREADS, 15, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(100));
        long starttime = System.currentTimeMillis();
        LongAddrThreead atomic = new LongAddrThreead(starttime);
        for (int i = 0; i < TASK_COUNT; i++) {
            executor.submit(atomic);
        }
        cdladdr.await();
        executor.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        Atomic a = new Atomic();
        a.testSync();
        a.testAtomic();
        a.testAtomicLong();
    }
}
