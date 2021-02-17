package com.gee.thread.translator;

import com.gee.thread.VariableDemo;
import com.gee.thread.flow.executor.ExecuteContext;
import com.gee.thread.flow.translator.V2PTranslator;

/**
 * desc:
 *    D单元接口参数转换器,  A单元必须执行成功, B,C单元哪个执行成功,用哪个
 *    对应 ParallelTest.testOneMust2, testOneOfUnnecessaryException
 * @author gee wrote on  2021-01-17 11:22:07
 */
public class DV2PTranslator3 implements V2PTranslator<VariableDemo, Double> {


    @Override
    public Double translate(String preWorkId, ExecuteContext<VariableDemo> executeContext) {
        System.out.println("DV2PTranslator3 preWorkId: " + preWorkId);
        Double doubleVar = executeContext.getVariable().getDoubleVar();
        String result = executeContext.getResult("workA2", String.class);
         doubleVar += Double.parseDouble(result);
        if ("workB1".equals(preWorkId)){
            Integer workB2Result = executeContext.getResult("workB1", Integer.class);
            return doubleVar + Double.parseDouble(workB2Result.toString());
        }
        if ("workC1".equals(preWorkId)){
            String workB2Result = executeContext.getResult("workC1", String.class);
            return doubleVar + Double.parseDouble(workB2Result);
        }
        return null;
    }
}
