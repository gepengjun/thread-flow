package com.gee.thread.parallel;

import com.gee.thread.*;
import com.gee.thread.flow.executor.WorkExecutor;
import com.gee.thread.flow.executor.WorkExecuteDesigner;
import com.gee.thread.flow.executor.ExecuteContext;
import com.gee.thread.translator.*;

import java.util.concurrent.TimeUnit;

/**
 * desc:
 *      并行测试
 * @author gee wrote on  2021-01-16 18:23:18
 */
public class ParallelTest {
    /**
     * 执行线程    A ---
     *            B ---  D
     *            C ---
     * B,C单元执行时间都为2秒,A单元执行时间1秒 对于D单元都非必须
     * D 单元执行0.5秒
     * 谁执行的快, D单元用谁的参数
     *
     * 预期执行: 1.5秒
     */
    private static void testNormal(){
        VariableDemo variableDemo = new VariableDemo("str", "str2", 100, 1000.0);

        WorkA1<VariableDemo> workA1 = WorkA1.build("workA1", new A1V2PTranslator());
        WorkB2<VariableDemo> workB2 = WorkB2.build("workB2", new B2V2PTranslator());
        WorkC2<VariableDemo> workC2 = WorkC2.build("workC2", new C2V2PTranslator());
        WorkD<VariableDemo> workD = WorkD.build("workD", new DV2PTranslator1());

        WorkExecuteDesigner<VariableDemo> workExecuteDesigner = WorkExecuteDesigner.<VariableDemo>builder().variable(variableDemo)
                .commonHandler(new SysOutWorkHandler<>())
                .newStartWork(workA1)
                    .next(false, workD)
                .newStartWork(workB2)
                    .next(false, workD)
                .newStartWork(workC2)
                    .next(false, workD)
                .currentWork(workD).endLine(true)
                .build();

        long start = System.currentTimeMillis();

        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL);

        System.out.println(System.currentTimeMillis() - start);
        System.out.println(executeContext);
        WorkExecutor.shutdown();
    }

    /**
     * 执行线程    A ---
     *            B ---  D
     *            C ---
     * B,C单元执行时间都为2秒,对于D单元非必须
     * A单元执行时间1秒 对于D单元必须
     * D 单元执行0.5秒
     * A单元必须执行成功,
     * B,C单元谁先执行成功,谁执行D
     *
     * 预期执行 2.5秒
     */
    private static void testOneNecessary(){
        VariableDemo variableDemo = new VariableDemo("str", "str2", 100, 1000.0);

        WorkA1<VariableDemo> workA1 = WorkA1.build("workA1", new A1V2PTranslator());
        WorkB2<VariableDemo> workB2 = WorkB2.build("workB2", new B2V2PTranslator());
        WorkC2<VariableDemo> workC2 = WorkC2.build("workC2", new C2V2PTranslator());
        WorkD<VariableDemo> workD = WorkD.build("workD", new DV2PTranslator1());

        WorkExecuteDesigner<VariableDemo> workExecuteDesigner = WorkExecuteDesigner.<VariableDemo>builder().variable(variableDemo)
                .commonHandler(new SysOutWorkHandler<>())
                .newStartWork(workA1)
                    .next(true, workD)
                .newStartWork(workB2)
                    .next(false, workD)
                .newStartWork(workC2)
                    .next(false, workD)
                .currentWork(workD).endLine(true)
                .build();

        long start = System.currentTimeMillis();

//        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL,2400, TimeUnit.MILLISECONDS);
        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL,2600, TimeUnit.MILLISECONDS);

        System.out.println(System.currentTimeMillis() - start);
        System.out.println(executeContext);
        WorkExecutor.shutdown();
    }

    /**
     * 执行线程    A ---
     *            B ---  D
     *            C ---
     * B,C单元执行时间都为1秒,对于D单元非必须
     * A单元执行时间2秒 对于D单元必须
     * D 单元执行0.5秒
     * B,C单元先执行完成,A执行完成后执行D
     * A单元必须执行成功,
     */
    private static void testOneNecessary2(){
        VariableDemo variableDemo = new VariableDemo("str", "str2", 100, 1000.0);

        WorkA2<VariableDemo> workA2 = WorkA2.build("workA2", new A1V2PTranslator());
        WorkB1<VariableDemo> workB1 = WorkB1.build("workB1", new B2V2PTranslator());
        WorkC1<VariableDemo> workC1 = WorkC1.build("workC1", new C2V2PTranslator());
        WorkD<VariableDemo> workD = WorkD.build("workD", new DV2PTranslator3());

        WorkExecuteDesigner<VariableDemo> workExecuteDesigner = WorkExecuteDesigner.<VariableDemo>builder().variable(variableDemo)
                .commonHandler(new SysOutWorkHandler<>())
                .newStartWork(workA2)
                    .next(true, workD)
                .newStartWork(workB1)
                    .next(false, workD)
                .newStartWork(workC1)
                    .next(false, workD)
                .currentWork(workD).endLine(true)
                .build();
        long start = System.currentTimeMillis();

//        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL,2400, TimeUnit.MILLISECONDS);
        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL,2600, TimeUnit.MILLISECONDS);

        System.out.println(System.currentTimeMillis() - start);
        System.out.println(executeContext);
        WorkExecutor.shutdown();
    }

    /**
     * 执行线程    A ---
     *            B ---  D
     *            C ---
     * B,C单元执行时间都为2秒,对于D单元非必须
     * A单元执行时间1秒 对于D单元必须, 会异常
     * D 单元执行0.5秒
     */
    private static void testOneNecessaryException(){
        VariableDemo variableDemo = new VariableDemo("str", "str2", 100, 1000.0);

        WorkExceptionA1<VariableDemo> workExceptionA1 = WorkExceptionA1.build("workExceptionA1", new A1V2PTranslator());
        WorkB2<VariableDemo> workB2 = WorkB2.build("workB2", new B2V2PTranslator());
        WorkC2<VariableDemo> workC2 = WorkC2.build("workC2", new C2V2PTranslator());
        WorkD<VariableDemo> workD = WorkD.build("workD", new DV2PTranslator3());

        WorkExecuteDesigner<VariableDemo> workExecuteDesigner = WorkExecuteDesigner.<VariableDemo>builder().variable(variableDemo)
                .commonHandler(new SysOutWorkHandler<>())
                .newStartWork(workExceptionA1)
                    .next(true, workD)
                .newStartWork(workB2)
                    .next(false, workD)
                .newStartWork(workC2)
                    .next(false, workD)
                .currentWork(workD).endLine(true)
                .build();
        long start = System.currentTimeMillis();

//        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL,1900, TimeUnit.MILLISECONDS);
        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL,21000000, TimeUnit.MILLISECONDS);

        System.out.println(System.currentTimeMillis() - start);
        System.out.println(executeContext);
        WorkExecutor.shutdown();
    }

    /**
     * 执行线程    A ---
     *            B ---  D
     *            C ---
     * B,C单元执行时间都为1秒,对于D单元非必须, 其中B单元会异常
     * A单元执行时间2秒 对于D单元必须
     * D 单元执行0.5秒
     */
    private static void testOneOfUnnecessaryException(){
        VariableDemo variableDemo = new VariableDemo("str", "str2", 100, 1000.0);

        WorkA2<VariableDemo> workA2 = WorkA2.build("workA2", new A1V2PTranslator());
        WorkExceptionB1<VariableDemo> workExceptionB1 = WorkExceptionB1.build("workExceptionB1", new B2V2PTranslator());
        WorkC1<VariableDemo> workC1 = WorkC1.build("workC1", new C2V2PTranslator());
        WorkD<VariableDemo> workD = WorkD.build("workD", new DV2PTranslator3());

        WorkExecuteDesigner<VariableDemo> workExecuteDesigner = WorkExecuteDesigner.<VariableDemo>builder().variable(variableDemo)
                .commonHandler(new SysOutWorkHandler<>())
                .newStartWork(workA2)
                    .next(true, workD)
                .newStartWork(workExceptionB1)
                    .next(false, workD)
                .newStartWork(workC1)
                    .next(false, workD)
                .currentWork(workD).endLine(true)
                .build();
        long start = System.currentTimeMillis();

//        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL,2400, TimeUnit.MILLISECONDS);
        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL,2600, TimeUnit.MILLISECONDS);

        System.out.println(System.currentTimeMillis() - start);
        System.out.println(executeContext);
        WorkExecutor.shutdown();
    }

    /**
     * 执行线程    A ---
     *            B ---  D
     *            C ---
     * B,C单元执行时间都为1秒,对于D单元非必须, 其中B,C单元会异常
     * A单元执行时间2秒 对于D单元必须
     * D 单元执行0.5秒
     *  预期运行:  A为Skipped ,B,C异常, D为Skipped
     */
    private static void testAllOfUnnecessaryException(){
        VariableDemo variableDemo = new VariableDemo("str", "str2", 100, 1000.0);

        WorkA2<VariableDemo> workA2 = WorkA2.build("workA2", new A1V2PTranslator());
        WorkExceptionB1<VariableDemo> workExceptionB1 = WorkExceptionB1.build("workExceptionB1", new B2V2PTranslator());
        WorkExceptionC1<VariableDemo> workExceptionC1 = WorkExceptionC1.build("workExceptionC1", new C2V2PTranslator());
        WorkD<VariableDemo> workD = WorkD.build("workD", new DV2PTranslator3());

        WorkExecuteDesigner<VariableDemo> workExecuteDesigner = WorkExecuteDesigner.<VariableDemo>builder().variable(variableDemo)
                .commonHandler(new SysOutWorkHandler<>())
                .newStartWork(workA2)
                    .next(true, workD)
                .newStartWork(workExceptionB1)
                    .next(false, workD)
                .newStartWork(workExceptionC1)
                    .next(false, workD)
                .currentWork(workD).endLine(true)
                .build();
        long start = System.currentTimeMillis();

//        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL,2400, TimeUnit.MILLISECONDS);
        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL,2600, TimeUnit.MILLISECONDS);

        System.out.println(System.currentTimeMillis() - start);
        System.out.println(executeContext);
        WorkExecutor.shutdown();
    }

    /**
     * 执行线程    A ---
     *            B ---  D
     *            C ---
     * A,B,C单元对于D单元必须
     * 预期: A,B,C都执行成功, D再执行
     */
    private static void testAllNecessary(){
        VariableDemo variableDemo = new VariableDemo("str", "str2", 100, 1000.0);

        WorkA1<VariableDemo> workA1 = WorkA1.build("workA1", new A1V2PTranslator());
        WorkB2<VariableDemo> workB2 = WorkB2.build("workB2", new B2V2PTranslator());
        WorkC2<VariableDemo> workC2 = WorkC2.build("workC2", new C2V2PTranslator());
        WorkD<VariableDemo> workD = WorkD.build("workD", new DV2PTranslator3());

        WorkExecuteDesigner<VariableDemo> workExecuteDesigner = WorkExecuteDesigner.<VariableDemo>builder().variable(variableDemo)
                .commonHandler(new SysOutWorkHandler<>())
                .newStartWork(workA1)
                .next(true, workD)
                .newStartWork(workB2)
                .next(true, workD)
                .newStartWork(workC2)
                .next(true, workD)
                .currentWork(workD).endLine(true)
                .build();

        long start = System.currentTimeMillis();

//        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL,2400, TimeUnit.MILLISECONDS);
        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL,2600, TimeUnit.MILLISECONDS);

        System.out.println(System.currentTimeMillis() - start);
        System.out.println(executeContext);
        WorkExecutor.shutdown();
    }

    /**
     * 执行线程    A ---
     *            B ---  D
     *            C ---
     * A,B,C单元对于D单元必须
     * 其中B会执行失败
     * 预期: A,B,C都执行成功, D再执行
     */
    private static void testAllNecessaryAndOneException(){
        VariableDemo variableDemo = new VariableDemo("str", "str2", 100, 1000.0);

        WorkA1<VariableDemo> workA1 = WorkA1.build("workA1", new A1V2PTranslator());
        WorkExceptionB1<VariableDemo> workExceptionB1 = WorkExceptionB1.build("workExceptionB1", new B2V2PTranslator());
        WorkC2<VariableDemo> workC2 = WorkC2.build("workC2", new C2V2PTranslator());
        WorkD<VariableDemo> workD = WorkD.build("workD", new DV2PTranslator3());

        WorkExecuteDesigner<VariableDemo> workExecuteDesigner = WorkExecuteDesigner.<VariableDemo>builder().variable(variableDemo)
                .commonHandler(new SysOutWorkHandler<>())
                .newStartWork(workA1)
                .next(true, workD)
                .newStartWork(workExceptionB1)
                .next(true, workD)
                .newStartWork(workC2)
                .next(true, workD)
                .currentWork(workD).endLine(true)
                .build();

        long start = System.currentTimeMillis();

//        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL,2400, TimeUnit.MILLISECONDS);
        ExecuteContext<?> executeContext = WorkExecutor.execute(workExecuteDesigner, WorkExecutor.COMMON_POOL,2600, TimeUnit.MILLISECONDS);

        System.out.println(System.currentTimeMillis() - start);
        System.out.println(executeContext);
        WorkExecutor.shutdown();
    }
    public static void main(String[] args) {
//        testNormal();
//        testOneNecessary();
//        testOneNecessary2();
//        testOneNecessaryException();
//        testOneOfUnnecessaryException();
//        testAllOfUnnecessaryException();
//        testAllNecessary();
        testAllNecessaryAndOneException();
    }
}
