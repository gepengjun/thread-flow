package com.gee.thread.flow.executor;

import com.gee.thread.flow.common.result.CheckResult;
import com.gee.thread.flow.work.AbstractWork;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * desc:
 *   封装工作单元执行时的属性
 * @author gee wrote on  2021-01-20 08:49:26
 */
public class WorkExecuteProperties<V> {

    private Map<String, AbstractWork<V,?,?>> nextWorks = new HashMap<>();

    private Map<String, AbstractWork<V,?,?>> preWorks = new HashMap<>();

    private Map<String, Boolean> necessaryForNextMap = new HashMap<>();

    /**
     *  前置任务有非必须的单元时, 此属性记录前置任务第一个非必须且执行成功的单元id
     */
    private AtomicReference<String> preWorkId = new AtomicReference<>();

    private CheckResult checkResult;

    private CompletableFuture<ExecuteContext<V>> completableFuture;

    private Thread selfThread;

    private Thread asyncThread;

    private Boolean enableInterrupted = false;

    public Map<String, AbstractWork<V,?,?>> getNextWorks() {
        return nextWorks;
    }

    public Map<String, AbstractWork<V,?,?>> getPreWorks() {
        return preWorks;
    }

    public Map<String, Boolean> getNecessaryForNextMap() {
        return necessaryForNextMap;
    }

    public AtomicReference<String> getPreWorkId() {
        return preWorkId;
    }

    public CheckResult getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(CheckResult checkResult) {
        this.checkResult = checkResult;
    }

    public CompletableFuture<ExecuteContext<V>> getCompletableFuture() {
        return completableFuture;
    }

    public void setCompletableFuture(CompletableFuture<ExecuteContext<V>> completableFuture) {
        this.completableFuture = completableFuture;
    }

    public Thread getSelfThread() {
        return selfThread;
    }

    public void setSelfThread(Thread selfThread) {
        this.selfThread = selfThread;
    }

    public Thread getAsyncThread() {
        return asyncThread;
    }

    public void setAsyncThread(Thread asyncThread) {
        this.asyncThread = asyncThread;
    }

    public Boolean getEnableInterrupted() {
        return enableInterrupted;
    }

    public void setEnableInterrupted(Boolean enableInterrupted) {
        this.enableInterrupted = enableInterrupted;
    }
}
