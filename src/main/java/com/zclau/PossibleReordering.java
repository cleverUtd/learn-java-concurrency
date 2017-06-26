package com.zclau;

/**
 * Created by liuzicong on 6/6/2017.
 */
public class PossibleReordering {

    static int x = 0, y = 0;
    static int a = 0, b = 0;

    public static void main(String[] args) throws InterruptedException {


        int count = 0;
        while (true) {
            count++;

            Thread one = new Thread(() -> {
                a = 1;
                x = b;
            });

            Thread two = new Thread(() -> {
                b = 1;
                y = a;
            });

            one.start();
            two.start();

            one.join();
            two.join();
            System.out.println("第 " + count + " 次" + "（x=" + x + " y=" + y + ")");
            if (x == 0 && y == 0) {
                break;
            }
        }
    }

}
