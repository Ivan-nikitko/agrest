package io.agrest.runtime.processor.meta;

import io.agrest.MetadataStage;
import io.agrest.processor.ExceptionMappingProcessorDecoratorFactory;
import io.agrest.processor.Processor;
import io.agrest.runtime.AgExceptionMappers;
import org.apache.cayenne.di.DIRuntimeException;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.di.Provider;

import java.util.EnumMap;

/**
 * @since 2.7
 * @deprecated since 4.1, as Agrest now integrates with OpenAPI 3 / Swagger.
 */
@Deprecated
public class MetadataProcessorFactoryProvider implements Provider<MetadataProcessorFactory> {

    private final EnumMap<MetadataStage, Processor<MetadataContext<?>>> stages;
    private final AgExceptionMappers exceptionMappers;
    private ExceptionMappingProcessorDecoratorFactory processorDecoratorFactory;

    public MetadataProcessorFactoryProvider(
            @Inject CollectMetadataStage collectMetadataStage,

            @Inject AgExceptionMappers exceptionMappers,
            @Inject ExceptionMappingProcessorDecoratorFactory processorDecoratorFactory) {

        this.stages = new EnumMap<>(MetadataStage.class);
        this.stages.put(MetadataStage.COLLECT_METADATA, collectMetadataStage);

        this.exceptionMappers = exceptionMappers;
        this.processorDecoratorFactory = processorDecoratorFactory;
    }

    @Override
    public MetadataProcessorFactory get() throws DIRuntimeException {
        return new MetadataProcessorFactory(stages, exceptionMappers,processorDecoratorFactory);
    }
}
