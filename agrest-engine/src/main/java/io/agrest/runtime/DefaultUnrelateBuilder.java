package io.agrest.runtime;

import io.agrest.SimpleResponse;
import io.agrest.UnrelateBuilder;
import io.agrest.runtime.processor.unrelate.UnrelateContext;
import io.agrest.runtime.processor.unrelate.UnrelateProcessorFactory;

/**
 * @since 5.0
 */
public class DefaultUnrelateBuilder<T> implements UnrelateBuilder<T> {

    private final UnrelateContext<T> context;
    private final UnrelateProcessorFactory processorFactory;

    public DefaultUnrelateBuilder(UnrelateContext<T> context, UnrelateProcessorFactory processorFactory) {
        this.context = context;
        this.processorFactory = processorFactory;
    }

    @Override
    public UnrelateBuilder<T> sourceId(Object id) {
        context.setSourceId(id);
        return this;
    }

    @Override
    public UnrelateBuilder<T> allRelated(String relationship) {
        context.setRelationship(relationship);
        context.setTargetId(null);
        return this;
    }

    @Override
    public UnrelateBuilder<T> related(String relationship, Object targetId) {
        context.setRelationship(relationship);
        context.setTargetId(targetId);
        return this;
    }

    @Override
    public SimpleResponse sync() {
        processorFactory.createProcessor().execute(context);
        return context.createSimpleResponse();
    }
}
