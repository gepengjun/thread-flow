package com.gee.thread.flow.common.result;

/**
 *  the enum of the state the work executes
 */
public enum ExecuteState {
    INIT(0),
    WORKING(1),
    SUCCESSFUL(2),
    EXCEPTIONAL(3);

    private int code;

    ExecuteState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
