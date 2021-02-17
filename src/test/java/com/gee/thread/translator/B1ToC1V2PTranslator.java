package com.gee.thread.translator;

import com.gee.thread.VariableDemo;
import com.gee.thread.flow.executor.ExecuteContext;
import com.gee.thread.flow.common.result.ExecuteState;
import com.gee.thread.flow.translator.V2PTranslator;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-16 21:12:25
 */
public class B1ToC1V2PTranslator implements V2PTranslator<VariableDemo,String> {

    @Override
    public String translate(String preWorkId, ExecuteContext<VariableDemo> executeContext) {
        if (executeContext.getWorkExecuteState("workB1").get() == ExecuteState.SUCCESSFUL.getCode()){
            Integer workB1Result = executeContext.getResult("workB1",Integer.class);
            return executeContext.getVariable().getStr() + workB1Result.toString();
        }
        return null;
    }
}
