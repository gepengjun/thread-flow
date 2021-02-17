package com.gee.thread;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-16 21:15:21
 */
public class VariableDemo {

    public String str;

    public String str2;

    private Integer integer;

    private Double doubleVar;

    public VariableDemo() {
    }

    public VariableDemo(String str, Integer integer) {
        this.str = str;
        this.integer = integer;
    }

    public VariableDemo(String str, String str2, Integer integer, Double doubleVar) {
        this.str = str;
        this.str2 = str2;
        this.integer = integer;
        this.doubleVar = doubleVar;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public String getStr2() {
        return str2;
    }

    public void setStr2(String str1) {
        this.str2 = str1;
    }

    public Double getDoubleVar() {
        return doubleVar;
    }

    public void setDoubleVar(Double doubleVar) {
        this.doubleVar = doubleVar;
    }

    @Override
    public String toString() {
        return "VariableDemo{" +
                "str='" + str + '\'' +
                ", str2='" + str2 + '\'' +
                ", integer=" + integer +
                ", doubleVar=" + doubleVar +
                '}';
    }


}
