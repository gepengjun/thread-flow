package com.gee.thread;

import com.gee.thread.flow.common.result.ExecuteState;
import com.gee.thread.flow.executor.ExecuteContext;
import com.gee.thread.flow.handler.DefaultWorkHandler;

import java.util.Map;

/**
 * desc:
 *
 * @author gee wrote on  2021-02-17 08:48:41
 */
public class SysOutWorkHandler<V> extends DefaultWorkHandler<V> {
    @Override
    public void postBeforeBegin(String currentWorkId, Object param) {
        System.out.println(System.currentTimeMillis() + ": " + currentWorkId + " postBeforeBegin, param: " + param);
    }

    @Override
    protected void postAfterFinishBeforeNextBegin(String currentWorkId, ExecuteState currentWorkExecuteState,
                                                  Map<String, ?> extraProperties, ExecuteContext<V> executeContext) {
        System.out.println(System.currentTimeMillis() + ": " + currentWorkId + " postAfterFinishBeforeNextBegin, "
                + " currentWorkExecuteState: " + currentWorkExecuteState
                + " result: " + executeContext.getWorkResult(currentWorkId));
    }

    @Override
    protected void postAfterFinishAfterNextBegin(String currentWorkId, Map<String, ?> extraProperties,
                                                 ExecuteContext<V> executeContext) {
        System.out.println(System.currentTimeMillis() + ": " + currentWorkId + " postAfterFinishBeforeNextBegin, "
                + " result: " + executeContext.getWorkResult(currentWorkId));
    }
}
