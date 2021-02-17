package com.gee.thread.seq;

import com.gee.thread.*;
import com.gee.thread.flow.executor.ExecuteContext;
import com.gee.thread.flow.executor.WorkExecuteDesigner;
import com.gee.thread.flow.executor.WorkExecutor;
import com.gee.thread.translator.A1ToB1V2PTranslator;
import com.gee.thread.translator.A1V2PTranslator;
import com.gee.thread.translator.B1ToC1V2PTranslator;

import java.util.concurrent.TimeUnit;

/**
 * desc:
 *      the test of the works which execute sequence
 * @author gee wrote on  2021-01-14 19:42:38
 */
public class SeqTest {

    /**
     *  order of execution         A -> B -> C
     *  the cost of the execution  1    1    1    unit: second
     */
    private static void seqTest(){

        VariableDemo variableDemo = new VariableDemo("str", 100);

        WorkA1<VariableDemo> workA1 = WorkA1.build("workA1", new A1V2PTranslator());
        WorkB1<VariableDemo> workB1 = WorkB1.build("workB1", new A1ToB1V2PTranslator());
        WorkC1<VariableDemo> workC1 = WorkC1.build("workC1", new B1ToC1V2PTranslator());

        WorkExecuteDesigner<VariableDemo> workExecuteDesigner = WorkExecuteDesigner.<VariableDemo>builder().variable(variableDemo)
                .commonHandler(new SysOutWorkHandler<>())
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
     *  order of execution              A -> B -> C
     *  the cost of the execution       1    1    1    unit: second
     *  execute exceptionally           N    Y    N
     */
    private static void seqExceptionTest(){

        WorkA1<VariableDemo> workA1 = WorkA1.build("workA1", new A1V2PTranslator());
        WorkExceptionB1<VariableDemo> workExceptionB1 = WorkExceptionB1.build("workExceptionB1", new A1ToB1V2PTranslator());
        WorkC1<VariableDemo> workC1 = WorkC1.build("workC1", new B1ToC1V2PTranslator());

        VariableDemo variableDemo = new VariableDemo("str", 100);
        WorkExecuteDesigner<VariableDemo> workExecuteDesigner = WorkExecuteDesigner.<VariableDemo>builder().variable(variableDemo)
                .commonHandler(new SysOutWorkHandler<>())
                .newStartWork(workA1)
                    .next(true, workExceptionB1)
                .currentWork(workExceptionB1)
                    .next(true, workC1)
                .currentWork(workC1).endLine(true)
                .build();
        long start = System.currentTimeMillis();
//      ExecuteContext<?>  executeContext = WorkExecutor.execute(workExecuteDesigner,1900, TimeUnit.MILLISECONDS);
        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner,2100, TimeUnit.MILLISECONDS);
            WorkExecutor.shutdown();
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(executeContext);
    }

    private static void seqWholeExceptionTest() {
        WorkA1<VariableDemo> workA1 = WorkA1.build("workA1", new A1V2PTranslator());
        WorkWholeExceptionB1<VariableDemo> workWholeExceptionB1 =
                WorkWholeExceptionB1.build("workWholeExceptionB1", new A1ToB1V2PTranslator());
        WorkC1<VariableDemo> workC1 = WorkC1.build("workC1", new B1ToC1V2PTranslator());

        VariableDemo variableDemo = new VariableDemo("str", 100);
        WorkExecuteDesigner<VariableDemo> workExecuteDesigner = WorkExecuteDesigner.<VariableDemo>builder()
                .variable(variableDemo)
                .commonHandler(new SysOutWorkHandler<>())
                .newStartWork(workA1)
                .next(true, workWholeExceptionB1)
                .currentWork(workWholeExceptionB1)
                .next(true, workC1)
                .currentWork(workC1).endLine(true)
                .build();
        long start = System.currentTimeMillis();
//      ExecuteContext<?>  executeContext = WorkExecutor.execute(workExecuteDesigner,1900, TimeUnit.MILLISECONDS);
        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner,2100, TimeUnit.MILLISECONDS);
        WorkExecutor.shutdown();
        System.out.println("cost " + (System.currentTimeMillis() - start));
        System.out.println(executeContext);
    }

    public static void main(String[] args) {
//        seqTest();
        seqExceptionTest();
//        seqWholeExceptionTest();
    }


}
