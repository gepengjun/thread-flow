package com.gee.thread.flow.common.result;

public class CheckResult {

    private ExecuteState expectState;

    private Exception exception;

    private CheckResult(ExecuteState expectState, Exception exception) {
        this.expectState = expectState;
        this.exception = exception;
    }

    public ExecuteState getExpectState() {
        return expectState;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "CheckResult{" +
                "expectState=" + expectState +
                ", exception=" + exception +
                '}';
    }

    public static CheckResult build(ExecuteState expectState){
        return new CheckResult(expectState, null);
    }

    public static CheckResult build(ExecuteState expectState, Exception exception){
        return new CheckResult(expectState, exception);
    }
}
