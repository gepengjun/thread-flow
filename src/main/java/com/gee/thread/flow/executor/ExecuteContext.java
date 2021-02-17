package com.gee.thread.flow.executor;

import com.gee.thread.flow.common.result.ExecuteState;
import com.gee.thread.flow.common.result.WorkResult;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * desc:
 *  总的执行结果
 * @author gee wrote on  2020-08-30 08:20:14
 */

public class ExecuteContext<V> implements Serializable {

    private static final String NONE_STARTER = "execute skipped because none starter has been set";
    private static final long serialVersionUID = -1327492165034638074L;
    /**
     * 总的执行结果
     * {@link ExecuteState}
     */
    private AtomicInteger globalExecuteState = new AtomicInteger(0);

    private String executeMessage;

    private V variable;

    private transient Executor executor;

    private Map<String, WorkResult<?,?>> workResultMap = new HashMap<>();

    public Map<String, WorkResult<?,?>> getWorkResultMap() {
        return workResultMap;
    }

    private ExecuteContext(V variable) {
        this.variable = variable;
    }

    public AtomicInteger getGlobalExecuteState() {
        return globalExecuteState;
    }

    public String getExecuteMessage() {
        return executeMessage;
    }

    public void setExecuteMessage(String executeMessage) {
        this.executeMessage = executeMessage;
    }

    public V getVariable() {
        return variable;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public String toString() {
        return "ExecuteContext{" +
                "globalExecuteState=" + globalExecuteState +
                ", executeMessage=" + executeMessage +
                ", variable=" + variable +
                ", workResultMap=" + workResultMap +
                '}';
    }
    public WorkResult<?,?> getWorkResult(String id){
        return getWorkResultMap().get(id);
    }

    public void setIfNonStarter(){
        this.globalExecuteState.set(ExecuteState.EXCEPTIONAL.getCode());
        this.executeMessage = NONE_STARTER;
    }
    public <R> R getResult(String workId, Class<R> resultType){
        return getWorkResult(workId).getResult(resultType);
    }

    public AtomicInteger getWorkExecuteState(String workId){
        return getWorkResult(workId).getExecuteState();
    }

    public static class Builder<V>{

        public static <V> ExecuteContext<V> build(V variable, Executor executor){
            ExecuteContext<V> executeContext = new ExecuteContext<>(variable);
            executeContext.setExecutor(executor);
            return executeContext;
        }
    }
}
