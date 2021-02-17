package com.gee.thread;

import com.gee.thread.flow.translator.V2PTranslator;
import com.gee.thread.flow.work.AbstractWork;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-14 19:41:05
 */
public class WorkB1<V> extends AbstractWork<V, Integer, Integer> {

    private String id;

    @Override
    public Integer process(Integer param) throws Exception {
        Thread.sleep(1000);
        return 2;
    }

    private WorkB1(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static <V> WorkB1<V> build(String id, V2PTranslator<V,Integer> v2PTranslator){
        WorkB1<V> instance = new WorkB1<>(id);
        instance.setV2PTranslator(v2PTranslator);
        return instance;
    }
}
