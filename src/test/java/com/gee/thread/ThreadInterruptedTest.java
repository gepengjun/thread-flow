package com.gee.thread;

import com.gee.thread.flow.executor.WorkExecutor;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-23 09:58:06
 */
public class ThreadInterruptedTest {

    /**
     *
     */
    private static void threadInterruptTest(){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(0);
                    Thread.sleep(1000);
                    System.out.println(1);
                    Thread.sleep(1000);
                    System.out.println(2);
                    Thread.sleep(1000);
                    System.out.println(3);
                    Thread.sleep(1000);
                    System.out.println(4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try {
            Thread.sleep(1100);
            thread.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("finish");
    }

    private static void threadInPoolInterruptTest(){
        Thread thread = new Thread(() -> {
            try {
                System.out.println(0);
                Thread.sleep(1000);
                System.out.println(1);
                Thread.sleep(1000);
                System.out.println(2);
                Thread.sleep(1000);
                System.out.println(3);
                Thread.sleep(1000);
                System.out.println(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        WorkExecutor.COMMON_POOL.execute(thread);
        try {
            Thread.sleep(1100);
            thread.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("finish");
        WorkExecutor.shutdown();
    }

    /**
     * 不在线程池中, CompletableFuture会被中断
     */
    private static void completableFutureCancelTest(){
        CompletableFuture<Object> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(0);
            try {
                Thread.sleep(1000);
                System.out.println(1);
                Thread.sleep(1000);
                System.out.println(2);
                Thread.sleep(1000);
                System.out.println(3);
                Thread.sleep(1000);
                System.out.println(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        });

        try {
            Thread.sleep(1100);
            completableFuture.cancel(true);
            completableFuture.get();
            Thread.sleep(3000);
        } catch (InterruptedException | ExecutionException | CancellationException e) {
            System.out.println("catch  CancellationException");
            e.printStackTrace();
        }
        WorkExecutor.shutdown();
        System.out.println("finish");
    }

    /**
     *  在线程池中CompletableFuture.cancel并不会中断线程
     */
    private static void completableFutureCancelInPoolTest(){
        CompletableFuture<Object> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(0);
            try {
                Thread.sleep(1000);
                System.out.println(1);
                Thread.sleep(1000);
                System.out.println(2);
                Thread.sleep(1000);
                System.out.println(3);
                Thread.sleep(1000);
                System.out.println(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }, WorkExecutor.COMMON_POOL);

        try {
            Thread.sleep(1100);
            completableFuture.cancel(true);
            completableFuture.get();
            Thread.sleep(3000);
        } catch (InterruptedException | ExecutionException | CancellationException e) {
            System.out.println("catch  CancellationException");
            e.printStackTrace();
        }
        WorkExecutor.shutdown();
        System.out.println("finish");
    }


    public static void main(String[] args) {
//        threadInterruptTest();
//        threadInPoolInterruptTest();
//        completableFutureCancelTest();
        completableFutureCancelInPoolTest();
    }
}
