package com.zclau.Interrupted;

import java.util.concurrent.TimeUnit;

/**
 * Created by liuzicong on 17/4/2017.
 */
public class Shutdown {

    public static void main(String[] args) throws InterruptedException {
        Runner one = new Runner();

        Thread counterThread = new Thread(one, "CounterThread");
        counterThread.start();
        //睡眠1秒，main线程对counterThread进行中断，使counterThread能够感知中断而结束
        TimeUnit.SECONDS.sleep(1);
        counterThread.interrupt();

        Runner two = new Runner();
        counterThread = new Thread(two, "CounterThread");
        counterThread.start();

        //睡眠1秒，main线程对counterThread进行中断，使counterThread能够感知 on 为 false 而结束
        TimeUnit.SECONDS.sleep(1);
        two.cancel();
    }


    static class Runner implements Runnable {

        private long i;
        private volatile boolean on = true;

        @Override
        public void run() {
            while (on && !Thread.currentThread().isInterrupted()) {
                i++;
            }
            System.out.println("Count i = " + i);
        }

        public void cancel() {
            on = false;
        }
    }
}
