package com.gee.thread.flow.work;

/**
 *  工作单元
 * @param <P>  参数类型
 * @param <R>  结果类型
 */
public interface Work<P,R> {

    /**
     *  执行耗时操作
     */
    R process(P param) throws Exception;

}
