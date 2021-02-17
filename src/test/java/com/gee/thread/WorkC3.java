package com.gee.thread;

import com.gee.thread.flow.translator.V2PTranslator;
import com.gee.thread.flow.work.AbstractWork;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-14 19:41:05
 */
public class WorkC3<V> extends AbstractWork<V, String,String> {

    private String id;

    @Override
    public String process(String param) throws Exception {
        Thread.sleep(3000);
        return "CCC";
    }

    private WorkC3(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static <V> WorkC3<V> build(String id, V2PTranslator<V,String> v2PTranslator){
        WorkC3<V> instance = new WorkC3<>(id);
        instance.setV2PTranslator(v2PTranslator);
        return instance;
    }

}
