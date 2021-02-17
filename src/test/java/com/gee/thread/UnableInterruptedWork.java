package com.gee.thread;

import com.gee.thread.flow.translator.V2PTranslator;
import com.gee.thread.flow.work.AbstractWork;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-23 06:29:42
 */
public class UnableInterruptedWork<V> extends AbstractWork<V, String, String> {

    private String id;

    @Override
    public String process(String param) throws Exception {
        Thread.sleep(500);
        System.out.println(id + " 0.5");
        Thread.sleep(500);
        System.out.println(id + " 1.0");
        Thread.sleep(500);
        System.out.println(id + " 1.5");
        Thread.sleep(500);
        System.out.println(id + " 2.0");
        return null;
    }

    private UnableInterruptedWork(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public static <V> UnableInterruptedWork<V> build(String id, V2PTranslator<V,String> v2PTranslator){
        UnableInterruptedWork<V> instance = new UnableInterruptedWork<>(id);
        instance.setV2PTranslator(v2PTranslator);
        return instance;
    }
}
