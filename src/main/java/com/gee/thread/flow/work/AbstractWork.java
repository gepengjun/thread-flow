package com.gee.thread.flow.work;

import com.gee.thread.flow.common.result.CheckResult;
import com.gee.thread.flow.common.result.ExecuteState;
import com.gee.thread.flow.common.result.WorkResult;
import com.gee.thread.flow.executor.ExecuteContext;
import com.gee.thread.flow.executor.WorkExecuteProperties;
import com.gee.thread.flow.handler.WorkHandler;
import com.gee.thread.flow.translator.V2PTranslator;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * desc:
 *   V: 线程组启动时, 使用的统一参数
 *   P: 本单元使用的参数
 *   R: 本单元返回类型
 * @author gee wrote on  2021-01-14 19:03:01
 */
public abstract class AbstractWork<V,P,R> implements BiConsumer<ExecuteContext<V>, Throwable>, Work<P,R> {

    /**
     * 参数转换器, 不建议将此属性封装进WorkProperties,
     * 工作单元在不同的 线程编排中可能需要不同的v2PTranslator
     */
    private V2PTranslator<V, P> v2PTranslator;

    private WorkHandler<V> workHandler;

    private WorkExecuteProperties<V> workExecuteProperties = new WorkExecuteProperties<>();

    private Map<String, ?> extraProperties;

    private P param;

    private R r;

    private  Exception exception;
    /**
     * 超时时间, 单位: 毫秒, 默认不超时
     * 如果设置超时时间, 会另起一个线程执行process(param)方法
     */
    private long timeout = -1;

    public void work(ExecuteContext<V> executeContext) {
        try {
            CheckResult checkResult = workExecuteProperties.getCheckResult();
            ExecuteState expectState = checkResult.getExpectState();
            if (expectState == ExecuteState.EXCEPTIONAL) {
                workHandler.handleBeforeBegin(this, executeContext);
                exception = checkResult.getException();
            } else if (expectState == ExecuteState.WORKING) {
                param = this.v2PTranslator.translate(workExecuteProperties.getPreWorkId().get(), executeContext);
                setResultIfWorking(executeContext, param,null, null);
                workHandler.handleBeforeBegin(this, executeContext);
                if (timeout > 0){
                    CompletableFuture<ExecuteContext<V>> completableFuture
                            = CompletableFuture.completedFuture(executeContext)
                            .whenCompleteAsync(new InnerWork(this),executeContext.getExecutor());
                    completableFuture.get(timeout, TimeUnit.MILLISECONDS);
                }else {
                    r = process(param);
                }
            }
        } catch (Exception e) {
            exception = e;
        }
        setResultIfWorking(executeContext, param, r, exception);
        workHandler.handleAfterFinish(this, executeContext);

    }

    @SuppressWarnings("unchecked")
    protected void setResultIfWorking(ExecuteContext<V> executeContext, P param, R r, Exception e) {
        WorkResult workResult = executeContext.getWorkResult(getId());
        if (workResult.getExecuteState().get() == ExecuteState.WORKING.getCode()){
            workResult.setParam(param);
            workResult.setResult(r);
            workResult.setException(e);
        }
    }

    @Override
    public void accept(ExecuteContext<V> executeContext, Throwable throwable) {
        workExecuteProperties.setSelfThread(Thread.currentThread());
        if (throwable == null) {
            this.work(executeContext);
        }
    }

    public void beforeInterrupted(){

    }
    protected void initExecuteContext(ExecuteContext<V> executeContext) {
        if (! executeContext.getWorkResultMap().containsKey(this.getId())){
            WorkResult<P, R> workResult = new WorkResult<>();
            executeContext.getWorkResultMap().put(this.getId(), workResult);
            workExecuteProperties.getNextWorks().forEach((id, work) -> work.initExecuteContext(executeContext));
        }
    }

    /**
     *  work真正执行的方法
     * @param param work的参数
     * @return R
     * @throws Exception Exception
     */
    @Override
    public abstract R process(P param) throws Exception;

    /**
     *  返回work的id
     * @return id
     */
    public abstract String getId();

    public void setV2PTranslator(V2PTranslator<V, P> v2PTranslator) {
        this.v2PTranslator = v2PTranslator;
    }

    public WorkExecuteProperties<V> getWorkExecuteProperties() {
        return workExecuteProperties;
    }

    public WorkHandler<V> getWorkHandler() {
        return workHandler;
    }

    public void setWorkHandler(WorkHandler<V> workHandler) {
        this.workHandler = workHandler;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setExtraProperties(Map<String, ?> extraProperties) {
        this.extraProperties = extraProperties;
    }

    public Map<String, ?> getExtraProperties() {
        return extraProperties;
    }

    private class InnerWork extends AbstractWork<V,P,R>{

        private AbstractWork<V,P,R> parentWork;

        public InnerWork(AbstractWork<V, P, R> parentWork) {
            this.parentWork = parentWork;
        }

        @Override
        public void accept(ExecuteContext<V> executeContext, Throwable throwable) {
            workExecuteProperties.setAsyncThread(Thread.currentThread());
            if (throwable == null) {
                try {
                    r = process(param);
                } catch (Exception e) {
                    exception = e;
                }
            }
        }

        @Override
        public R process(P param) throws Exception {
            return parentWork.process(param);
        }

        @Override
        public String getId() {
            return parentWork.getId();
        }


    }
}
