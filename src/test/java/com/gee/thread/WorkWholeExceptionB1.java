package com.gee.thread;

import com.gee.thread.flow.common.exception.GlobalSkippedException;
import com.gee.thread.flow.translator.V2PTranslator;
import com.gee.thread.flow.work.AbstractWork;

/**
 * desc:
 *
 * @author gee wrote on  2021-02-04 17:21:00
 */
public class WorkWholeExceptionB1<V> extends AbstractWork<V, Integer,String> {

    private String id;

    @Override
    public String process(Integer param) throws Exception {
        Thread.sleep(1000);
        throw new GlobalSkippedException();
    }

    private WorkWholeExceptionB1(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static <V> WorkWholeExceptionB1<V> build(String id, V2PTranslator<V,Integer> v2PTranslator){
        WorkWholeExceptionB1<V> instance = new WorkWholeExceptionB1<>(id);
        instance.setV2PTranslator(v2PTranslator);
        return instance;
    }
}
