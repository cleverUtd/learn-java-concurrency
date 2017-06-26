package com.zclau.synchronize;

/**
 * Created by liuzicong on 16/4/2017.
 */
public class SynchronizedTest {

    static String name = "name";

    public synchronized void normalMethod() {
        System.out.println(Thread.currentThread().getName() + "--->" + name);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public synchronized static void staticMethod() {
        System.out.println(Thread.currentThread().getName() + "--->" + name);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void syncBlock() {
        synchronized (this) {
            System.out.println(Thread.currentThread().getName() + "--->" + name);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        SynchronizedTest st = new SynchronizedTest();

         Thread t1 = new Thread(new Runnable() {
             @Override
             public void run() {
                 st.normalMethod();
             }
         });
         t1.setName("thread---1");
         t1.start();

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                st.syncBlock();
            }
        });
        t2.setName("thread---2");
        t2.start();

        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
               staticMethod();
            }
        });
        t3.setName("thread---3");
        t3.start();
    }
}
