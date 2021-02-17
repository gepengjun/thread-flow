package com.gee.thread.timeout;

import com.gee.thread.*;
import com.gee.thread.flow.executor.ExecuteContext;
import com.gee.thread.flow.executor.WorkExecuteDesigner;
import com.gee.thread.flow.executor.WorkExecutor;
import com.gee.thread.translator.*;

import java.util.concurrent.TimeUnit;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-23 06:06:13
 */
public class TimeoutWorkTest {

    /**
     *  单元执行  A -> B -> C
     *  单元耗时  1    1    1    单位:秒
     *  B单元设置的超时时间为0.5秒, 所以B线程会超时
     */
    private static void timeoutTest(){

        VariableDemo variableDemo = new VariableDemo("str", 100);

        WorkA1<VariableDemo> workA1 = WorkA1.build("workA1", new A1V2PTranslator());
        WorkB1<VariableDemo> workB1 = WorkB1.build("workB1", new A1ToB1V2PTranslator());
        WorkC1<VariableDemo> workC1 = WorkC1.build("workC1", new B1ToC1V2PTranslator());
        workB1.setTimeout(500);

        WorkExecuteDesigner<VariableDemo> workExecuteDesigner = WorkExecuteDesigner.<VariableDemo>builder().variable(variableDemo)
                .newStartWork(workA1)
                .next(true, workB1)
                .currentWork(workB1)
                .next(true, workC1)
                .currentWork(workC1).endLine(true)
                .build();

        long start = System.currentTimeMillis();

//        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner,2900, TimeUnit.MILLISECONDS);
        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner,3100, TimeUnit.MILLISECONDS);
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(executeContext);
        WorkExecutor.shutdown();
    }

    /**
     *  单元执行  A -> B -> C
     *  单元耗时  1    1    1    单位:秒
     *  B单元设置的超时时间为1.5秒
     */
    private static void notTimeoutTest(){

        VariableDemo variableDemo = new VariableDemo("str", 100);

        WorkA1<VariableDemo> workA1 = WorkA1.build("workA1", new A1V2PTranslator());
        WorkB1<VariableDemo> workB1 = WorkB1.build("workB1", new A1ToB1V2PTranslator());
        WorkC1<VariableDemo> workC1 = WorkC1.build("workC1", new B1ToC1V2PTranslator());
        workB1.setTimeout(1500);

        WorkExecuteDesigner<VariableDemo> workExecuteDesigner = WorkExecuteDesigner.<VariableDemo>builder().variable(variableDemo)
                .newStartWork(workA1)
                .next(true, workB1)
                .currentWork(workB1)
                .next(true, workC1)
                .currentWork(workC1).endLine(true)
                .build();

        long start = System.currentTimeMillis();

//        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner,2900, TimeUnit.MILLISECONDS);
        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner,3100, TimeUnit.MILLISECONDS);
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(executeContext);
        WorkExecutor.shutdown();
    }

    /**
     *  单元执行  A -> B -> C
     *  单元耗时  1    1    1    单位:秒
     *  B单元设置的超时时间为1.5秒
     */
    private static void notTimeoutButExceptionTest(){

        VariableDemo variableDemo = new VariableDemo("str", 100);

        WorkA1<VariableDemo> workA1 = WorkA1.build("workA1", new A1V2PTranslator());
        WorkExceptionB1<VariableDemo> workExceptionB1 = WorkExceptionB1.build("workExceptionB1", new A1ToB1V2PTranslator());
        WorkC1<VariableDemo> workC1 = WorkC1.build("workC1", new B1ToC1V2PTranslator());
        workExceptionB1.setTimeout(1500);

        WorkExecuteDesigner<VariableDemo> workExecuteDesigner = WorkExecuteDesigner.<VariableDemo>builder().variable(variableDemo)
                .newStartWork(workA1)
                .next(true, workExceptionB1)
                .currentWork(workExceptionB1)
                .next(true, workC1)
                .currentWork(workC1).endLine(true)
                .build();

        long start = System.currentTimeMillis();

//        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner,2900, TimeUnit.MILLISECONDS);
        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner,3100, TimeUnit.MILLISECONDS);
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(executeContext);
        WorkExecutor.shutdown();
    }

    /**
     *  时间单位: 秒
     *               是否必须  执行时间  是否异常  设置的超时时间
     * 执行线程    A -  是       1        是           -1         --
     *            B -  否       2        否           1.5          --  D 0.5秒
     *            C -  否       2        否           -1         --
     */
    private static void notTimeoutButCanceledTest(){
        VariableDemo variableDemo = new VariableDemo("str", "str2", 100, 1000.0);

        WorkExceptionA1<VariableDemo> workExceptionA1 = WorkExceptionA1.build("workExceptionA1", new A1V2PTranslator());
        WorkB2<VariableDemo> workB2 = WorkB2.build("workB2", new B2V2PTranslator());
        WorkC2<VariableDemo> workC2 = WorkC2.build("workC2", new C2V2PTranslator());
        WorkD<VariableDemo> workD = WorkD.build("workD", new DV2PTranslator3());

        workB2.setTimeout(1500);

        WorkExecuteDesigner<VariableDemo> workExecuteDesigner = WorkExecuteDesigner.<VariableDemo>builder().variable(variableDemo)
                .newStartWork(workExceptionA1)
                .next(true, workD)
                .newStartWork(workB2)
                .next(false, workD)
                .newStartWork(workC2)
                .next(false, workD)
                .currentWork(workD).endLine(true)
                .build();
        long start = System.currentTimeMillis();
        System.out.println(start);
//        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL,1900, TimeUnit.MILLISECONDS);
        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL,21000000, TimeUnit.MILLISECONDS);

        System.out.println(System.currentTimeMillis() - start);
        System.out.println(executeContext);
        WorkExecutor.shutdown();
        System.out.println(System.currentTimeMillis() - start);
    }

    public static void main(String[] args) {
//        timeoutTest();
//        notTimeoutTest();
//        notTimeoutButExceptionTest();
        notTimeoutButCanceledTest();
    }
}
