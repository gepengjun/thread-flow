package com.gee.thread.translator;

import com.gee.thread.VariableDemo;
import com.gee.thread.flow.executor.ExecuteContext;
import com.gee.thread.flow.translator.V2PTranslator;

/**
 * desc:
 *    D单元接口参数转换器,  A,B,C单元哪个执行成功,用哪个
 * @author gee wrote on  2021-01-17 11:22:07
 */
public class DV2PTranslator1 implements V2PTranslator<VariableDemo, Double> {


    @Override
    public Double translate(String preWorkId, ExecuteContext<VariableDemo> executeContext) {
        Double doubleVar = executeContext.getVariable().getDoubleVar();
        if (executeContext.getWorkExecuteState("workA1").get() == 2){
            String result = executeContext.getResult("workA1",String.class);
            return doubleVar + Double.parseDouble(result);
        }
        if (executeContext.getWorkExecuteState("workB2").get() == 2){
            Integer result = executeContext.getResult("workB2",Integer.class);
            return doubleVar + Double.parseDouble(result.toString());
        }
        if (executeContext.getWorkExecuteState("workC2").get() == 2){
            String result = executeContext.getWorkResult("workC2").getResult(String.class);
            return doubleVar + Double.parseDouble(result);
        }
        return null;
    }
}
