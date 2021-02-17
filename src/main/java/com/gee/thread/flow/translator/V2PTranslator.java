package com.gee.thread.flow.translator;

import com.gee.thread.flow.executor.ExecuteContext;

/**
 * desc:
 *
 * @author gee wrote on  2021-01-16 21:04:21
 */
public interface V2PTranslator<V, P> {
    P translate(String preWorkId, ExecuteContext<V> executeContext);
}
