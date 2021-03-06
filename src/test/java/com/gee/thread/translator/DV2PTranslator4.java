package com.gee.thread.translator;

import com.gee.thread.VariableDemo;
import com.gee.thread.flow.executor.ExecuteContext;
import com.gee.thread.flow.translator.V2PTranslator;

/**
 * desc:
 *    D单元接口参数转换器,  A单元必须执行成功, B,C单元哪个执行成功,用哪个
 *    对应 ParallelTest.testAllMust
 * @author gee wrote on  2021-01-17 11:22:07
 */
public class DV2PTranslator4 implements V2PTranslator<VariableDemo, Double> {


    @Override
    public Double translate(String preWorkId, ExecuteContext<VariableDemo> executeContext) {
        Double doubleVar = executeContext.getVariable().getDoubleVar();
        String result = executeContext.getResult("workA1", String.class);
        doubleVar += Double.parseDouble(result);
        Integer workB2Result = executeContext.getResult("workB2", Integer.class);
        doubleVar += Double.parseDouble(workB2Result.toString());
        String workC1Result = executeContext.getResult("workC2", String.class);
        return doubleVar + Double.parseDouble(workC1Result);
    }
}
