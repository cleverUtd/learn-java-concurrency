package com.zclau.Interrupted;

import java.util.concurrent.TimeUnit;

/**
 * Created by liuzicong on 17/4/2017.
 */
public class Interrupted {

    /**
     * 两个线程，前者不断休眠，后者不断运行，然后分别进行中断操作，观察二者的中断标识位
     *
     * @param args
     */

    public static void main(String[] args) throws InterruptedException {
        Thread sleepThread = new Thread(new SleepRunner(), "SleepThread");
        sleepThread.setDaemon(true);
        Thread busyThread = new Thread(new BusyRunner(), "BusyThread");
        busyThread.setDaemon(true);

        sleepThread.start();
        busyThread.start();

        //休眠5秒，让sleepThread和busyThread充分运行
        TimeUnit.SECONDS.sleep(5);
        sleepThread.interrupt();
        busyThread.interrupt();

        System.out.println("SleepThread interrupted is " + sleepThread.isInterrupted());
        System.out.println("BusyThread interrupted is " + busyThread.isInterrupted());

        //防止sleepThread和busyThread立刻退出
        TimeUnit.SECONDS.sleep(2);
    }


    static class SleepRunner implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " 中断啦");
                }
            }
        }
    }


    static class BusyRunner implements Runnable {

        @Override
        public void run() {
            while (true) {

            }
        }
    }
}
