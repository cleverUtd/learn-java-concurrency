package com.zclau.threadPool;

/**
 * Created by liuzicong on 26/6/2017.
 */
public class ThreadPoolTest {

    public static void main(String[] args) {
        ThreadPool pool = new DefaultThreadPool();

        Thread t = new Thread(new PrintThread());
        for (int i = 0; i < 5; i++) {
            pool.execute(t);
        }

        pool.shutdown();
    }


    private static class PrintThread implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + ": " + Math.random());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
