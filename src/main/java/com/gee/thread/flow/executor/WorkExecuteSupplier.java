package com.gee.thread.flow.executor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * desc:
 * @author gee wrote on  2021-01-14 20:09:09
 */
public class WorkExecuteSupplier<R> implements Supplier<R> {

    private CompletableFuture<R> executeContextCompletableFuture;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public WorkExecuteSupplier(CompletableFuture<R> executeContextCompletableFuture) {
        this.executeContextCompletableFuture = executeContextCompletableFuture;
    }

    @Override
    public R get() {
        R r = null;
        try {
            r = executeContextCompletableFuture.get();
            countDownLatch.await();
        } catch (InterruptedException | ExecutionException ignored) {
        }
        return r;
    }
    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

}
