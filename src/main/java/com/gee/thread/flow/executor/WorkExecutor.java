package com.gee.thread.flow.executor;

import com.gee.thread.flow.handler.DefaultWorkHandler;
import com.gee.thread.flow.handler.WorkHandler;
import com.gee.thread.flow.work.impl.EndWork;
import com.gee.thread.flow.work.impl.InitWork;

import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-17 09:21:32
 */
public class WorkExecutor {

    public static final ThreadPoolExecutor COMMON_POOL =
            new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, 1024,
                    15L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(),
                    new DefaultThreadFactory());

    public static <V> ExecuteContext<V> execute(WorkExecuteDesigner<V> workExecuteDesigner){
        return execute(workExecuteDesigner, COMMON_POOL);
    }

    public static <V> ExecuteContext<V> execute(WorkExecuteDesigner<V> workExecuteDesigner,
                                            long timeout, TimeUnit timeUnit){
        return execute(workExecuteDesigner, COMMON_POOL, timeout, timeUnit);
    }

    public static <V> ExecuteContext<V> execute(WorkExecuteDesigner<V> workExecuteDesigner, ThreadPoolExecutor executor){

        ExecuteContext<V> executeContext = ExecuteContext.Builder.build(workExecuteDesigner.getVariable(), executor);
        CompletableFuture<ExecuteContext<V>> completableFuture = getCompletableFuture(workExecuteDesigner, executeContext);
        if (completableFuture == null) {
            return executeContext;
        }
        try {
            executeContext = completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            workExecuteDesigner.getEndWork().getCountDownLatch().countDown();
            executeContext.getGlobalExecuteState().set(3);
            executeContext.setExecuteMessage(e.getMessage());
        }
        return executeContext;
    }

    public static <V> ExecuteContext<V> execute(WorkExecuteDesigner<V> workExecuteDesigner, ThreadPoolExecutor executor,
                                            long timeout, TimeUnit timeUnit){
        ExecuteContext<V> executeContext = ExecuteContext.Builder.build(workExecuteDesigner.getVariable(), executor);
        CompletableFuture<ExecuteContext<V>> completableFuture = getCompletableFuture(workExecuteDesigner,executeContext);
        if (completableFuture == null) {
            return executeContext;
        }
        try {
            executeContext = completableFuture.get(timeout, timeUnit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            workExecuteDesigner.getEndWork().getCountDownLatch().countDown();
            executeContext.getGlobalExecuteState().set(3);
            executeContext.setExecuteMessage(WorkHandler.WHOLE_FUTURE_TIMEOUT);
        }
        return executeContext;
    }

    private static <V> CompletableFuture<ExecuteContext<V>> getCompletableFuture
            (WorkExecuteDesigner<V> workExecuteDesigner,ExecuteContext<V> executeContext){
        if (workExecuteDesigner.getStartWorkSet().size() == 0){
            executeContext.setIfNonStarter();
            return null;
        }
        InitWork<V> initWork = InitWork.build(workExecuteDesigner.getVariable());
        initWork.setWorkHandler(new DefaultWorkHandler<>());
        workExecuteDesigner.getStartWorkSet().forEach(work -> {
            initWork.getWorkExecuteProperties().getNextWorks().put(work.getId(), work);
            initWork.getWorkExecuteProperties().getNecessaryForNextMap().put(work.getId(), true);
            work.getWorkExecuteProperties().getPreWorks().put(initWork.getId(), initWork);
        });

        EndWork endWork = workExecuteDesigner.getEndWork();
        CompletableFuture<ExecuteContext<V>> executeContextCompletableFuture = CompletableFuture.completedFuture(executeContext);
        WorkExecuteSupplier<ExecuteContext<V>> workExecuteSupplier = new WorkExecuteSupplier<>(executeContextCompletableFuture);
        endWork.setCountDownLatch(workExecuteSupplier.getCountDownLatch());

        CompletableFuture<ExecuteContext<V>> completableFuture =
                executeContextCompletableFuture.whenCompleteAsync(initWork, executeContext.getExecutor());
        initWork.getWorkExecuteProperties().setCompletableFuture(completableFuture);
        return CompletableFuture.supplyAsync(workExecuteSupplier, executeContext.getExecutor());
    }
    public static void shutdown() {
        COMMON_POOL.shutdown();
    }

    static class DefaultThreadFactory implements ThreadFactory{
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "ThreadFlow-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }

    }
}
