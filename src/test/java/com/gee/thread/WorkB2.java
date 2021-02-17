package com.gee.thread;

import com.gee.thread.flow.translator.V2PTranslator;
import com.gee.thread.flow.work.AbstractWork;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-14 19:41:05
 */
public class WorkB2<V> extends AbstractWork<V, Integer,Integer> {

    private String id;

    @Override
    public Integer process(Integer param) throws Exception {
        Thread.sleep(2000);
        return 22;
    }

    private WorkB2(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static <V> WorkB2<V> build(String id, V2PTranslator<V,Integer> v2PTranslator){
        WorkB2<V> instance = new WorkB2<>(id);
        instance.setV2PTranslator(v2PTranslator);
        return instance;
    }
}
