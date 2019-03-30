package com.zclau.completableFuture;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * Created by liuzicong on 3/7/2017.
 */
public class Shop {

    String name;

    public Shop() {}
    public Shop(String name) {
        this.name = name;
    }



    public double getPrice(String product) {
        return calculatePrice(product);
    }

    public Future<Double> getPriceAsync(String product) {
        //创建CompletableFuture对象，它会包含计算的结果
        CompletableFuture<Double> futurePrice = new CompletableFuture<>();
        //在另一个线程中以异步方式执行计算
        new Thread(() -> {
            try {
                double price = calculatePrice(product);
                futurePrice.complete(price); //如果价格计算正常结束，完成Future操作并设置商品价格
            } catch (Exception ex) {
                futurePrice.completeExceptionally(ex); //否则抛出导致失败的异常
            }

        }).start();

        return futurePrice;
    }

    public Future<Double> getPriceAsync_1(String product) {
        return CompletableFuture.supplyAsync(() -> calculatePrice(product));
    }

    private double calculatePrice(String product) {
        delay();
//        if (new Random().nextInt(10) < 5) {
//            throw new RuntimeException("product not available");
//        }
        return new Random().nextDouble() * product.charAt(0) + product.charAt(1);
    }

    public static void delay() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * @return 字符串列表中包括商店的名称，该商店中指定商品的价格
     */
    public List<String> findPricesUsingStream(List<Shop> shops, String product) {
        return shops.stream().
                        map(shop -> String.format("%s price is %.2f", shop.name, shop.calculatePrice(product))).
                        collect(toList());
    }


    public List<String> findPricesUsingParallelSteam(List<Shop> shops, String product) {
        return shops.parallelStream().
                        map(shop -> String.format("%s price is %.2f", shop.name, shop.calculatePrice(product))).
                        collect(toList());
    }

    public List<String> findPricesUsingCompletableFuture(List<Shop> shops, String product) {
        List<CompletableFuture<String>> priceFutures = shops.stream()
                        .map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %.2f",
                                        shop.name, shop.calculatePrice(product))))
                        .collect(Collectors.toList());

        return priceFutures.stream().map(CompletableFuture::join).collect(toList());
    }

    public List<String> findPricesUsingCompletableFuture(List<Shop> shops, String product, Executor executor) {
        List<CompletableFuture<String>> priceFutures = shops.stream()
                        .map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %.2f",
                                        shop.name, shop.calculatePrice(product)), executor))
                        .collect(Collectors.toList());

        return priceFutures.stream().map(CompletableFuture::join).collect(toList());
    }



    public void testCompletableFuture() {
        long start = System.currentTimeMillis();
        //        Future<Double> futurePrice = shop.getPriceAsync("my favorite product");
        Future<Double> futurePrice = getPriceAsync_1("my favorite product");
        long invocationtime = System.currentTimeMillis() - start;
        System.out.println("Invocation returned after " + invocationtime + " ms");


        try {
            double price = futurePrice.get();
            System.out.printf("Price is %.2f%n", price);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        long retrievalTime = System.currentTimeMillis() - start;
        System.out.println("Price returned after " + retrievalTime + " ms");
    }


    public void testFindPricesUsingStream() {
        List<Shop> shops = Arrays.asList(new Shop("BestPrice"),
                        new Shop("LetsSaveBig"),
                        new Shop("MyFavShop"),
                        new Shop("BuyItAll"));

        long start = System.currentTimeMillis();
        System.out.println(new Shop().findPricesUsingStream(shops, "iphone7"));
        long duration = System.currentTimeMillis() - start;
        System.out.println("Done in " + duration + " ms");
    }


    public void testFindPricesUsingParallelStream() {
        List<Shop> shops = Arrays.asList(new Shop("BestPrice"),
                        new Shop("LetsSaveBig"),
                        new Shop("MyFavShop"),
                        new Shop("BuyItAll"),
                        new Shop("Java8 In Action"));

        long start = System.currentTimeMillis();
        System.out.println(new Shop().findPricesUsingParallelSteam(shops, "iphone7"));
        long duration = System.currentTimeMillis() - start;
        System.out.println("Done in " + duration + " ms");
    }

    public void compareParalleStreamAndCompletableFuture() {
        List<Shop> shops = Arrays.asList(new Shop("BestPrice"),
                        new Shop("LetsSaveBig"),
                        new Shop("MyFavShop"),
                        new Shop("BuyItAll"),
                        new Shop("Burberry"),
                        new Shop("Coach"),
                        new Shop("Nike"),
                        new Shop("Addidas"),
                        new Shop("Apple"));

        long start = System.currentTimeMillis();
        System.out.println(new Shop().findPricesUsingParallelSteam(shops, "iphone7"));
        long duration = System.currentTimeMillis() - start;
        System.out.println("Done in " + duration + " ms");

        Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        start = System.currentTimeMillis();
        System.out.println(new Shop().findPricesUsingCompletableFuture(shops, "iphone7", executor));
        duration = System.currentTimeMillis() - start;
        System.out.println("Done in " + duration + " ms");



    }

    public static void main(String[] args) {
        new Shop().compareParalleStreamAndCompletableFuture();
    }
}
