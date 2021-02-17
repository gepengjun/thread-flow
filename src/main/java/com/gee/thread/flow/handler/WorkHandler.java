package com.gee.thread.flow.handler;

import com.gee.thread.flow.common.result.CheckResult;
import com.gee.thread.flow.executor.ExecuteContext;
import com.gee.thread.flow.work.AbstractWork;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-14 19:06:41
 */
public interface WorkHandler<V> {

    String WHOLE_SKIPPED_CAUSED_BY = "The whole thread flow has been exceptional caused by the work which expects,named ";

    String PREVIOUS_NECESSARY_EXCEPTION = "previous necessary work executed exceptionally which named ";

    String ALL_PREVIOUS_UNNECESSARY_EXCEPTION = "all previous unnecessary works executed exceptionally: ";

    String NEXT_BEGUN = "next work has begun which is named ";

    String WHOLE_FUTURE_TIMEOUT = "the whole future executes timeout";

    CheckResult check(AbstractWork<V,?,?> currentWork, ExecuteContext<V> executeContext);

    void handleBeforeBegin(AbstractWork<V,?,?> currentWork, ExecuteContext<V> executeContext);

    void handleAfterFinish(AbstractWork<V,?,?> currentWork, ExecuteContext<V> executeContext);
}
