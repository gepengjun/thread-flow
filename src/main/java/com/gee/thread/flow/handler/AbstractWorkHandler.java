package com.gee.thread.flow.handler;

import com.gee.thread.flow.common.exception.SkippedException;
import com.gee.thread.flow.common.exception.GlobalSkippedException;
import com.gee.thread.flow.common.result.CheckResult;
import com.gee.thread.flow.executor.ExecuteContext;
import com.gee.thread.flow.common.result.ExecuteState;
import com.gee.thread.flow.common.result.WorkResult;
import com.gee.thread.flow.work.AbstractWork;
import com.gee.thread.flow.executor.WorkExecuteProperties;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-15 13:42:13
 */
public abstract class AbstractWorkHandler<V> implements WorkHandler<V>{

    /**
     *  handle before the work executes
     * @param currentWork the current work
     * @param executeContext the execute context
     */
    @Override
    public void handleBeforeBegin(AbstractWork<V,?,?> currentWork, ExecuteContext<V> executeContext){
        /*
         * after the current has been WORKING, try to set the previous works's execute state to be EXCEPTIONAL
         * to void execute
         */
        WorkExecuteProperties<V> workExecuteProperties = currentWork.getWorkExecuteProperties();
        if (workExecuteProperties.getPreWorks().size() > 0){
            workExecuteProperties.getPreWorks().forEach((preWorkId, preWork) -> {
                trySkipPrevious(currentWork.getId(), preWork,  executeContext);
            });
        }
        WorkResult<?, ?> workResult = executeContext.getWorkResult(currentWork.getId());
        postBeforeBegin(currentWork.getId(), workResult.getParam());
    }

    protected final void trySkipPrevious(String currentWorkId, AbstractWork<V,?,?> preWork, ExecuteContext<V> executeContext){
        WorkResult<?,?> preWorkResult = executeContext.getWorkResult(preWork.getId());
        SkippedException skippedException = new SkippedException(NEXT_BEGUN + currentWorkId);
        AtomicInteger executeState = preWorkResult.getExecuteState();
        //set the previous works's execute state from INIT to be EXCEPTIONAL to void execute
        WorkExecuteProperties<V> preWorkExecuteProperties = preWork.getWorkExecuteProperties();
        if (executeState.compareAndSet(ExecuteState.INIT.getCode(), ExecuteState.EXCEPTIONAL.getCode())) {
            preWorkResult.setException(skippedException);
        /*
            set the previous works's execute state from WORKING to be EXCEPTIONAL and interrupt the thread
            if enableInterrupted
         */
        }else if (executeState.compareAndSet(ExecuteState.WORKING.getCode(), ExecuteState.EXCEPTIONAL.getCode())){
            if (preWorkExecuteProperties.getEnableInterrupted()){
                preWork.beforeInterrupted();
                Thread asyncThread = preWorkExecuteProperties.getAsyncThread();
                if (asyncThread != null && asyncThread.isAlive()){
                    asyncThread.interrupt();
                }
                Thread selfThread = preWorkExecuteProperties.getSelfThread();
                if (selfThread != null && selfThread.isAlive()){
                    selfThread.interrupt();
                }
            }
            preWorkResult.setException(skippedException);
        }
        if (preWorkExecuteProperties.getPreWorks().size() > 0){
            preWorkExecuteProperties.getPreWorks().forEach((key, work) -> {
                trySkipPrevious(currentWorkId, work, executeContext);
            });
        }
    }

    @Override
    public void handleAfterFinish(AbstractWork<V,?,?> currentWork, ExecuteContext<V> executeContext){
        String currentWorkId = currentWork.getId();
        WorkResult<?, ?> workResult = executeContext.getWorkResult(currentWorkId);
        Exception exception = workResult.getException();
        if (exception instanceof GlobalSkippedException){
            AtomicInteger globalExecuteState = executeContext.getGlobalExecuteState();
            if (globalExecuteState.compareAndSet(ExecuteState.WORKING.getCode(), ExecuteState.EXCEPTIONAL.getCode())) {
                executeContext.setExecuteMessage(WorkHandler.WHOLE_SKIPPED_CAUSED_BY + currentWorkId);
            }
        }
        AtomicInteger executeState = workResult.getExecuteState();
        ExecuteState currentWorkExecuteState;
        if (exception == null) {
            currentWorkExecuteState = ExecuteState.SUCCESSFUL;
        }else {
            currentWorkExecuteState = ExecuteState.EXCEPTIONAL;
        }
        Map<String, ?> extraProperties = currentWork.getExtraProperties();
        postAfterFinishBeforeNextBegin(currentWorkId, currentWorkExecuteState, extraProperties,executeContext);
        editNextWork(currentWork, currentWorkExecuteState, executeContext);
        executeState.compareAndSet(ExecuteState.WORKING.getCode(), currentWorkExecuteState.getCode());
        // after the the current work finished, let the next works decide to execute or direct to be exceptional
        WorkExecuteProperties<V> workExecuteProperties = currentWork.getWorkExecuteProperties();
        if (workExecuteProperties.getNextWorks().size() > 0){
            workExecuteProperties.getNextWorks().forEach((nextWorkId, nextWork) -> {
                WorkExecuteProperties<V> nextWorkExecuteProperties = nextWork.getWorkExecuteProperties();
                AtomicReference<String> preWorkId = nextWorkExecuteProperties.getPreWorkId();
                if (exception == null && !workExecuteProperties.getNecessaryForNextMap().get(nextWorkId)) {
                    preWorkId.compareAndSet(null, currentWorkId);
                }

                CheckResult checkResult = nextWork.getWorkHandler().check(nextWork, executeContext);
                if (checkResult.getExpectState() != ExecuteState.INIT){
                    nextWorkExecuteProperties.setCheckResult(checkResult);
                    CompletableFuture<ExecuteContext<V>> completableFuture = workExecuteProperties.getCompletableFuture()
                            .whenCompleteAsync(nextWork, executeContext.getExecutor());
                    nextWork.getWorkExecuteProperties().setCompletableFuture(completableFuture);
                }
            });
        }
        postAfterFinishAfterNextBegin(currentWorkId, extraProperties,executeContext);
    }

    /**
     *  在单元结束后, 可以动态的修改后置单元
     * @param currentWork 当前单元
     * @param executeContext  整体的执行上下文
     */
    protected void editNextWork(AbstractWork<V,?,?> currentWork, ExecuteState currentWorkExecuteState,
                                ExecuteContext<V> executeContext){

    }


    @Override
    public CheckResult check(AbstractWork<V,?,?> currentWork, ExecuteContext<V> executeContext) {
        return checkPreWorks(currentWork, executeContext);
    }

    /**
     *  根据前一级的单元执行情况, 判断自身是否应该执行
     *  除DefaultWorkHandler的判断方式外, 可能的需求: 前一级单元都执行失败, 本单元才执行
     * @return  CheckResult
     */
    protected abstract CheckResult checkPreWorks(AbstractWork<V,?,?> currentWork, ExecuteContext<V> executeContext);

    protected abstract void postBeforeBegin(String currentWorkId, Object param);

    /**
     *  post after the current work finished and before the next works begin
     *  pay attention to the current work's execute state is still WORKING saved in the executeContext
     * @param currentWorkId the current work's id
     * @param currentWorkExecuteState final execute state of the current work
     * @param extraProperties extra properties
     * @param executeContext  the execute context
     */
    protected abstract void postAfterFinishBeforeNextBegin(String currentWorkId, ExecuteState currentWorkExecuteState,
                                            Map<String,?> extraProperties, ExecuteContext<V> executeContext);

    /**
     *  post after the current work finished and after the next works begin
     * @param currentWorkId the current work's id
     * @param extraProperties extra properties
     * @param executeContext the execute context
     */
    protected abstract void postAfterFinishAfterNextBegin(String currentWorkId,
                                            Map<String,?> extraProperties, ExecuteContext<V> executeContext);

    /**
     *  update the executeState from INIT to WORKING
     * @param executeState the execute state
     * @return return true if update successfully, otherwise false
     */
    protected final boolean updExecuteStateInit2Working(AtomicInteger executeState) {
        return executeState.compareAndSet(ExecuteState.INIT.getCode(), ExecuteState.WORKING.getCode());
    }

    /**
     *  get all unnecessary previous works's id of the current work
     * @param currentWorkId  the current work's id
     * @param workExecuteProperties the current work's  execute properties
     * @return  all unnecessary previous works's id of the current work
     */
    protected final String getAllPreUnnecessaryIds(String currentWorkId, WorkExecuteProperties<V> workExecuteProperties) {
        List<String> list = new ArrayList<>();
        workExecuteProperties.getPreWorks().forEach((key, work) -> {
            if (!work.getWorkExecuteProperties().getNecessaryForNextMap().get(currentWorkId)){
                list.add(work.getId());
            }
        });
        return list.toString();
    }

    /**
     *  get One of the previous necessary works which execute exceptionally of the current work
     * @param currentWorkId the current work's id
     * @param workExecuteProperties the current work's  execute properties
     * @param executeContext the execute context
     * @return One of the previous necessary works which execute exceptionally of the current work
     */
    protected final AbstractWork<V, ?, ?> oneOfNecessaryException(String currentWorkId
            , WorkExecuteProperties<V> workExecuteProperties, ExecuteContext<V> executeContext) {
        return workExecuteProperties.getPreWorks().values().parallelStream().filter(
                work -> work.getWorkExecuteProperties().getNecessaryForNextMap().get(currentWorkId)
                        && executeContext.getWorkResult(work.getId()).getExecuteState().get()
                        > ExecuteState.SUCCESSFUL.getCode()).findAny().orElse(null);
    }

    /**
     *  any of the previous necessary works of the current work is still INIT or WORKING
     * @param currentWorkId the current work's id
     * @param workExecuteProperties the current work's  execute properties
     * @param executeContext the execute context
     * @return true if any of the previous necessary works of the current work is still INIT or WORKING,
     *          otherwise false
     */
    protected final boolean anyNecessaryUnfinished(String currentWorkId, WorkExecuteProperties<V> workExecuteProperties,
                                                   ExecuteContext<V> executeContext) {
        return workExecuteProperties.getPreWorks().values().parallelStream().anyMatch(work ->
                work.getWorkExecuteProperties().getNecessaryForNextMap().get(currentWorkId)
                        && executeContext.getWorkResult(work.getId()).getExecuteState().get()
                        < ExecuteState.SUCCESSFUL.getCode());
    }

    /**
     *  none of the previous unnecessary works of the current work  executes successfully
     * @param currentWorkId the current work's id
     * @param workExecuteProperties the current work's  execute properties
     * @param executeContext the execute context
     * @return true if none of the previous unnecessary works of the current work  executes successfully,
     *          otherwise false
     */
    protected final boolean noneUnnecessarySuccess(String currentWorkId, WorkExecuteProperties<V> workExecuteProperties,
                                                   ExecuteContext<V> executeContext) {
        return workExecuteProperties.getPreWorks().values().parallelStream().noneMatch(work ->
                !work.getWorkExecuteProperties().getNecessaryForNextMap().get(currentWorkId)
                        && executeContext.getWorkResult(work.getId()).getExecuteState().get()
                        == ExecuteState.SUCCESSFUL.getCode());
    }

    /**
     *  any of the previous unnecessary works of the current work is still INIT or WORKING
     * @param currentWorkId  the current work's id
     * @param workExecuteProperties  the current work's  execute properties
     * @param executeContext the execute context
     * @return true if any of the previous unnecessary works of the current work is still INIT or WORKING,
     *          otherwise false
     */
    protected final boolean anyUnnecessaryUnfinished(String currentWorkId
            , WorkExecuteProperties<V> workExecuteProperties, ExecuteContext<V> executeContext) {
        return workExecuteProperties.getPreWorks().values().parallelStream().anyMatch(work ->
                !work.getWorkExecuteProperties().getNecessaryForNextMap().get(currentWorkId)
                        && executeContext.getWorkResult(work.getId()).getExecuteState().get()
                        < ExecuteState.SUCCESSFUL.getCode());
    }

    /**
     *  the previous unnecessary works of the current work exists and have been exceptional
     * @param currentWorkId the current work's id
     * @param workExecuteProperties the current work's  execute properties
     * @param executeContext the execute context
     * @return true if the previous unnecessary works of the current work exists and have been exceptional
     *          otherwise false
     */
    protected final boolean allUnnecessaryException(String currentWorkId, WorkExecuteProperties<V> workExecuteProperties,
                                                    ExecuteContext<V> executeContext) {

        long unnecessary = workExecuteProperties.getPreWorks().values().parallelStream()
                .filter(work -> !work.getWorkExecuteProperties().getNecessaryForNextMap().get(currentWorkId)).count();
        long unnecessaryExceptionCount = workExecuteProperties.getPreWorks().values().parallelStream()
                .filter(work -> !work.getWorkExecuteProperties().getNecessaryForNextMap().get(currentWorkId)
                        && executeContext.getWorkResult(work.getId()).getExecuteState().get()
                        == ExecuteState.EXCEPTIONAL.getCode()).count();
        return unnecessary > 0 && unnecessary == unnecessaryExceptionCount;
    }

    /**
     *  the previous unnecessary works of the current work exists
     * @param currentWorkId the current work's id
     * @param workExecuteProperties the current work's  execute properties
     * @return true if the previous unnecessary works of the current work exists
     *          otherwise false
     */
    protected final boolean existUnnecessary(String currentWorkId, WorkExecuteProperties<V> workExecuteProperties){
        return workExecuteProperties.getPreWorks().values().parallelStream()
                .anyMatch(work -> !work.getWorkExecuteProperties().getNecessaryForNextMap().get(currentWorkId));
    }

    /**
     *  any of the previous works of the current work is still INIT or WORKING
     * @param preWorks the previous works of the current work
     * @param executeContext the execute context
     * @return true if any of the previous works of the current work is still INIT or WORKING
     *          otherwise false
     */
    protected boolean anyPreUnfinished(Map<String, AbstractWork<V, ?, ?>> preWorks, ExecuteContext<V> executeContext){
        return preWorks.values().stream().anyMatch(preWork ->
                executeContext.getWorkResult(preWork.getId()).getExecuteState().get() < 2
        );
    }
}
