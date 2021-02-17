package com.gee.thread;

import com.gee.thread.flow.translator.V2PTranslator;
import com.gee.thread.flow.work.AbstractWork;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-14 19:41:05
 */
public class WorkExceptionC1<V> extends AbstractWork<V, String,String> {

    private String id;

    @Override
    public String process(String param) throws Exception {
        Thread.sleep(1000);
        int a = 1 / 0;
        return "3";
    }

    private WorkExceptionC1(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static <V> WorkExceptionC1<V> build(String id, V2PTranslator<V,String> v2PTranslator){
        WorkExceptionC1<V> instance = new WorkExceptionC1<>(id);
        instance.setV2PTranslator(v2PTranslator);
        return instance;
    }
}
