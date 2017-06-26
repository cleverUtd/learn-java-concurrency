package com.zclau.CAS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liuzicong on 17/4/2017.
 */
public class Counter {

    private AtomicInteger ai = new AtomicInteger(0);
    private int i;

    public static void main(String[] args) {
        final Counter counter = new Counter();
        List<Thread> ts = new ArrayList<>(600);
        long start = System.currentTimeMillis();

        for (int j = 0; j < 100; j++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int k = 0; k < 10000; k++) {
                        counter.count();
                        counter.safeCount();
                    }
                }
            });
            ts.add(t);
        }

        for (Thread t : ts) {
            t.start();
        }

        for (Thread t : ts) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(counter.i);
        System.out.println(counter.ai.get());
        System.out.println(System.currentTimeMillis() - start);
    }



    private void safeCount() {
        for (; ; ) {
            int i = ai.get();
            boolean suc = ai.compareAndSet(i, ++i);
            if (suc) {
                break;
            }
        }
    }

    private void count() {
        i++;
    }
}
