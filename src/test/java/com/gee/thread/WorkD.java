package com.gee.thread;

import com.gee.thread.flow.translator.V2PTranslator;
import com.gee.thread.flow.work.AbstractWork;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-14 19:41:05
 */
public class WorkD<V> extends AbstractWork<V, Double,Double> {

    private String id;

    @Override
    public Double process(Double param) throws Exception {
        Thread.sleep(500);
        return 4.0;
    }

    private WorkD(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static <V> WorkD<V> build(String id, V2PTranslator<V,Double> v2PTranslator){
        WorkD<V> instance = new WorkD<>(id);
        instance.setV2PTranslator(v2PTranslator);
        return instance;
    }
}
