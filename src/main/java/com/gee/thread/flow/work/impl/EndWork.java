package com.gee.thread.flow.work.impl;

import com.gee.thread.flow.common.result.ExecuteState;
import com.gee.thread.flow.common.result.WorkResult;
import com.gee.thread.flow.executor.ExecuteContext;
import com.gee.thread.flow.handler.DefaultWorkHandler;
import com.gee.thread.flow.work.AbstractWork;

import java.util.concurrent.CountDownLatch;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-14 20:09:09
 */
public class EndWork<V> extends AbstractWork<V, Object, Object> {

    public static final String END_WORK_ID = "endWork";

    private CountDownLatch countDownLatch;

    private EndWork() {
    }

    @Override
    public Object process(Object throwable){
        return null;
    }

    @Override
    public void accept(ExecuteContext executeContext, Throwable throwable) {
        WorkResult<?,?> workResult = executeContext.getWorkResult(getId());
        workResult.getExecuteState().compareAndSet(ExecuteState.WORKING.getCode(), ExecuteState.SUCCESSFUL.getCode());
        executeContext.getGlobalExecuteState().compareAndSet(ExecuteState.WORKING.getCode(), ExecuteState.SUCCESSFUL.getCode());
        countDownLatch.countDown();
    }

    @Override
    public String getId() {
        return EndWork.END_WORK_ID;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public static <V> EndWork<V> build(V v){
        EndWork<V> endWork = new EndWork<>();
        endWork.setWorkHandler(new DefaultWorkHandler<>());
        return endWork;
    }
}
