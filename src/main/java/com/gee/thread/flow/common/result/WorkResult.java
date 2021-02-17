package com.gee.thread.flow.common.result;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * desc:
 *  单个单元执行的结果
 * @author gee wrote on  2020-08-30 08:20:14
 */

public class WorkResult<P,R> implements Serializable {

    private static final long serialVersionUID = 7495973221423193540L;
    /**
     * 单元执行状态
     * {@link ExecuteState}
     */
    private AtomicInteger executeState = new AtomicInteger(0);
    /**
     * 执行本单元的参数
     */
    private P param;
    /**
     * 执行耗时操作后的结果
     */
    private R result;

    /**
     * 执行的异常
     */
    private Exception exception;

    public WorkResult() {
    }

    public AtomicInteger getExecuteState() {
        return executeState;
    }

    public R getResult() {
        return result;
    }

    public void setResult(R result) {
        this.result = result;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public P getParam() {
        return param;
    }

    @SuppressWarnings("unchecked")
    public <T> T getParam(Class<T> paramType) {
        return (T) param;
    }

    @SuppressWarnings("unchecked")
    public <T> T getResult(Class<T> resultType) {
        return (T) result;
    }

    public void setParam(P param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "WorkResult{" +
                "executeState=" + executeState +
                ", param=" + param +
                ", result=" + result +
                ", exception=" + exception +
                '}';
    }
}
