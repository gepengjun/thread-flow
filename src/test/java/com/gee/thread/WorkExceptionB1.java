package com.gee.thread;

import com.gee.thread.flow.translator.V2PTranslator;
import com.gee.thread.flow.work.AbstractWork;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-14 19:41:05
 */
public class WorkExceptionB1<V> extends AbstractWork<V, Integer,String> {

    private String id;

    @Override
    public String process(Integer param) throws Exception {
        Thread.sleep(1000);
        int a = 1 / 0;
        return "2";
    }

    private WorkExceptionB1(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static <V> WorkExceptionB1<V> build(String id, V2PTranslator<V,Integer> v2PTranslator){
        WorkExceptionB1<V> instance = new WorkExceptionB1<>(id);
        instance.setV2PTranslator(v2PTranslator);
        return instance;
    }
}
