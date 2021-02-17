package com.gee.thread.interrupted;

import com.gee.thread.*;
import com.gee.thread.flow.executor.ExecuteContext;
import com.gee.thread.flow.executor.WorkExecuteDesigner;
import com.gee.thread.flow.executor.WorkExecutor;
import com.gee.thread.translator.A1V2PTranslator;
import com.gee.thread.translator.B2V2PTranslator;
import com.gee.thread.translator.C2V2PTranslator;
import com.gee.thread.translator.DV2PTranslator3;

import java.util.concurrent.TimeUnit;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-23 06:29:16
 */
public class InterruptedTest {

    /**
     * 执行线程                        A ---
     *                                B ---  D
     *            UnableInterruptedWork ---
     * B单元执行时间为2秒,对于D单元非必须
     * UnableInterruptedWork 执行时间为2秒,对于D单元非必须
     * A单元执行时间1秒 对于D单元必须, 会异常
     * B 单元可以被中断, UnableInterruptedWork不可被中断
     * D 单元执行0.5秒
     */
    private static void testOneNecessaryException() throws InterruptedException {
        VariableDemo variableDemo = new VariableDemo("str", "str2", 100, 1000.0);

        WorkExceptionA1<VariableDemo> workExceptionA1 = WorkExceptionA1.build("workExceptionA1", new A1V2PTranslator());
        EnableInterruptedWork<VariableDemo> enableInterruptedWork = EnableInterruptedWork.build("enableInterruptedWork", new C2V2PTranslator());
        UnableInterruptedWork<VariableDemo> unableInterruptedWork = UnableInterruptedWork.build("unableInterruptedWork", new C2V2PTranslator());
        WorkD<VariableDemo> workD = WorkD.build("workD", new DV2PTranslator3());

        WorkExecuteDesigner<VariableDemo> workExecuteDesigner = WorkExecuteDesigner.<VariableDemo>builder().variable(variableDemo)
                .newStartWork(workExceptionA1)
                .next(true, workD)
                .newStartWork(enableInterruptedWork)
                .enableInterrupted(true)
                .next(false, workD)
                .newStartWork(unableInterruptedWork)
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
    }

    public static void main(String[] args) throws InterruptedException {
        testOneNecessaryException();
    }
}
