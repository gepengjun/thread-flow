package com.gee.thread.flow.work.impl;

import com.gee.thread.flow.executor.ExecuteContext;
import com.gee.thread.flow.common.result.ExecuteState;
import com.gee.thread.flow.common.result.WorkResult;
import com.gee.thread.flow.handler.DefaultWorkHandler;
import com.gee.thread.flow.work.AbstractWork;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-14 19:41:05
 */
public class InitWork<V> extends AbstractWork<V, Object, Object> {

    private static final String INIT_WORK_ID = "initWork";

    private InitWork(){
    }

    @Override
    public Object process(Object param){
        return null;
    }

    @Override
    public void accept(ExecuteContext<V> executeContext, Throwable throwable) {
        initExecuteContext(executeContext);
        WorkResult workResult = executeContext.getWorkResultMap().get(getId());
        workResult.getExecuteState().compareAndSet(ExecuteState.INIT.getCode(), ExecuteState.SUCCESSFUL.getCode());
        executeContext.getGlobalExecuteState().compareAndSet(ExecuteState.INIT.getCode(), ExecuteState.WORKING.getCode());
        this.getWorkHandler().handleAfterFinish(this, executeContext);
    }

    @Override
    public String getId() {
        return InitWork.INIT_WORK_ID;
    }

    public static <V> InitWork<V> build(V v){
        InitWork<V> initWork = new InitWork<>();
        initWork.setWorkHandler(new DefaultWorkHandler<>());
        return initWork;
    }
}
