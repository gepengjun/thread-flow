package com.gee.thread.flow.executor;

import com.gee.thread.flow.handler.DefaultWorkHandler;
import com.gee.thread.flow.handler.WorkHandler;
import com.gee.thread.flow.work.impl.EndWork;
import com.gee.thread.flow.work.AbstractWork;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-16 20:08:56
 */
public class WorkExecuteDesigner<V> {

    private Set<AbstractWork<V,?,?>> startWorkSet;

    private V variable;

    private EndWork endWork;

    private WorkExecuteDesigner(Set<AbstractWork<V,?,?>> startWorkSet, V variable, EndWork endWork) {
        this.startWorkSet = startWorkSet;
        this.variable = variable;
        this.endWork = endWork;
    }

    public Set<AbstractWork<V,?,?>> getStartWorkSet() {
        return startWorkSet;
    }

    public V getVariable() {
        return variable;
    }

    public EndWork getEndWork() {
        return endWork;
    }

    public static <V> WorkExecutorDesignerBuilder<V> builder(){
        return new WorkExecutorDesignerBuilder<V>();
    }

    public static class WorkExecutorDesignerBuilder<V> {

        private AbstractWork<V,?,?> currentWork;

        private Set<AbstractWork<V,?,?>> startWorkSet = new HashSet<>();

        private V variable;

        private EndWork<V> endWork;

        private WorkHandler<V> commonWorkHandler;

        public WorkExecutorDesignerBuilder<V> variable(V variable){
            this.variable = variable;
            endWork = EndWork.build(variable);
            return this;
        }

        public WorkExecutorDesignerBuilder<V> newStartWork(AbstractWork<V,?,?> work){
            assert work != null;
            this.currentWork = work;
            startWorkSet.add(work);
            return this;
        }

        public WorkExecutorDesignerBuilder<V> currentWork(AbstractWork<V,?,?> work){
            assert currentWork != null;
            assert work != null;
            this.currentWork = work;
            return this;
        }

        public WorkExecutorDesignerBuilder<V>  handler(WorkHandler<V> workHandler){
            assert currentWork != null;
            this.currentWork.setWorkHandler(workHandler);
            return this;
        }

        public WorkExecutorDesignerBuilder<V>  extraProperties(Map<String,?> extraProperties){
            assert currentWork != null;
            this.currentWork.setExtraProperties(extraProperties);
            return this;
        }

        public WorkExecutorDesignerBuilder<V>  commonHandler(WorkHandler<V> workHandler){
            assert workHandler != null;
            this.commonWorkHandler = workHandler;
            return this;
        }

        public WorkExecutorDesignerBuilder<V> next(boolean necessaryForNext, AbstractWork<V,?,?> work){
            commonWorkHandler = Optional.ofNullable(commonWorkHandler).orElse(new DefaultWorkHandler<>());
            if (!Optional.ofNullable(this.currentWork.getWorkHandler()).isPresent()){
                this.currentWork.setWorkHandler(commonWorkHandler);
            }
            currentWork.getWorkExecuteProperties().getNecessaryForNextMap().put(work.getId(), necessaryForNext);
            currentWork.getWorkExecuteProperties().getNextWorks().put(work.getId(),work);
            work.getWorkExecuteProperties().getPreWorks().put(this.currentWork.getId(),this.currentWork);
            return this;
        }

        public WorkExecutorDesignerBuilder<V> endLine(){
            return endLine(true);
        }

        public WorkExecutorDesignerBuilder<V> endLine(boolean necessary){
            assert currentWork != null;
            assert endWork != null;
            commonWorkHandler = Optional.ofNullable(commonWorkHandler).orElse(new DefaultWorkHandler<>());
            if (!Optional.ofNullable(this.currentWork.getWorkHandler()).isPresent()){
                this.currentWork.setWorkHandler(commonWorkHandler);
            }
            this.currentWork.getWorkExecuteProperties().getNecessaryForNextMap().put(endWork.getId(), necessary);
            this.currentWork.getWorkExecuteProperties().getNextWorks().put(endWork.getId(),endWork);

            endWork.getWorkExecuteProperties().getPreWorks().put(this.currentWork.getId(),this.currentWork);
            return this;
        }

        public WorkExecutorDesignerBuilder<V> enableInterrupted(boolean enableInterrupted){
            assert currentWork != null;
            currentWork.getWorkExecuteProperties().setEnableInterrupted(enableInterrupted);
            return this;
        }

        public WorkExecuteDesigner<V> build(){
            return new WorkExecuteDesigner<V>(startWorkSet, variable,endWork);
        }

    }
}
