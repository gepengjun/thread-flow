package com.gee.thread.flow.handler;

import com.gee.thread.flow.common.exception.SkippedException;
import com.gee.thread.flow.common.result.CheckResult;
import com.gee.thread.flow.executor.ExecuteContext;
import com.gee.thread.flow.common.result.ExecuteState;
import com.gee.thread.flow.work.AbstractWork;
import com.gee.thread.flow.executor.WorkExecuteProperties;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-14 19:52:51
 */
public class DefaultWorkHandler<V> extends AbstractWorkHandler<V> {
    /**
     *  check the execute state of the previous to decide to execute or direct to be exceptional
     * @param currentWork the current work
     * @return CheckResult
     */
    @Override
    public CheckResult checkPreWorks(AbstractWork<V,?,?> currentWork, ExecuteContext<V> executeContext) {
        String currentWorkId = currentWork.getId();
        AtomicInteger currentExecuteState = executeContext.getWorkResultMap().get(currentWorkId).getExecuteState();
        if (executeContext.getGlobalExecuteState().get() > ExecuteState.SUCCESSFUL.getCode() &&
                updExecuteStateInit2Working(currentExecuteState)){
            SkippedException skippedException = new SkippedException(executeContext.getExecuteMessage());
            return CheckResult.build(ExecuteState.EXCEPTIONAL, skippedException);
        }

        if (currentExecuteState.get() != ExecuteState.INIT.getCode()){
            return CheckResult.build(ExecuteState.INIT);
        }
        WorkExecuteProperties<V> workExecuteProperties = currentWork.getWorkExecuteProperties();
        if (workExecuteProperties.getPreWorks().size() > 0) {

            AbstractWork<V, ?, ?> work = oneOfNecessaryException(currentWorkId, workExecuteProperties, executeContext);
            if (work != null && updExecuteStateInit2Working(currentExecuteState)){
                SkippedException skippedException = new SkippedException(PREVIOUS_NECESSARY_EXCEPTION + work.getId());
                return CheckResult.build(ExecuteState.EXCEPTIONAL, skippedException);
            }

            boolean allUnnecessaryException = allUnnecessaryException(currentWorkId, workExecuteProperties, executeContext);
            if (allUnnecessaryException
                    && updExecuteStateInit2Working(currentExecuteState)){
                String message = ALL_PREVIOUS_UNNECESSARY_EXCEPTION + getAllPreUnnecessaryIds(currentWorkId, workExecuteProperties);
                SkippedException skippedException = new SkippedException(message);
                return CheckResult.build(ExecuteState.EXCEPTIONAL, skippedException);
            }

            boolean anyNecessaryUnfinished = anyNecessaryUnfinished(currentWorkId, workExecuteProperties, executeContext);
            /*
                the current work decides not to execute or be exceptional's situations:
                1. the current work has unfinished work which is necessary and if he current work has previous
                   unnecessary works, and none of the  previous unnecessary works is exceptional
                2.  he current work has previous unnecessary works, and none of them executes successfully,
                    and exist at least one which is unfinished of previous unnecessary works
             */
            if ((anyNecessaryUnfinished && !allUnnecessaryException)
                    || (anyUnnecessaryUnfinished(currentWorkId, workExecuteProperties,executeContext)
                    && noneUnnecessarySuccess(currentWorkId, workExecuteProperties, executeContext))){
                return CheckResult.build(ExecuteState.INIT);
            }
        }
        return updExecuteStateInit2Working(currentExecuteState)
                ? CheckResult.build(ExecuteState.WORKING) : CheckResult.build(ExecuteState.INIT);
    }

    @Override
    public void postBeforeBegin(String currentWorkId, Object param){
    }

    @Override
    protected void postAfterFinishBeforeNextBegin(String currentWorkId, ExecuteState currentWorkExecuteState,
                                   Map<String,?> extraProperties, ExecuteContext<V> executeContext){

    }

    @Override
    protected void postAfterFinishAfterNextBegin(String currentWorkId, Map<String, ?> extraProperties,
                                                 ExecuteContext<V> executeContext) {
    }

}
