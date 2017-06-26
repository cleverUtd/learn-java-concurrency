package com.zclau.threadLocal;

/**
 * Created by liuzicong on 17/4/2017.
 */
public class TestThreadLocal {

    static final ThreadLocal<Integer> value = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return 0;
        }
    };


    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(new MyThread(i + 1)).start();
        }
    }

    static class MyThread implements Runnable {

        private int index;

        public MyThread(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            System.out.println("线程" + index + "的初始value:" + value.get());
            for (int i = 0; i < 10; i++) {
                value.set(value.get() + i);
            }
            System.out.println("线程" + index + "的累加value:" + value.get());
        }
    }
}
