package io.agrest.runtime.processor.meta;

import io.agrest.MetadataStage;
import io.agrest.processor.ExceptionMappingProcessorDecoratorFactory;
import io.agrest.processor.Processor;
import io.agrest.processor.ProcessorFactory;
import io.agrest.runtime.AgExceptionMappers;

import java.util.EnumMap;

/**
 * @since 2.7
 * @deprecated since 4.1, as Agrest now integrates with OpenAPI 3 / Swagger.
 */
@Deprecated
public class MetadataProcessorFactory extends ProcessorFactory<MetadataStage, MetadataContext<?>> {

    public MetadataProcessorFactory(
            EnumMap<MetadataStage, Processor<MetadataContext<?>>> defaultStages,
            AgExceptionMappers exceptionMappers,
            ExceptionMappingProcessorDecoratorFactory processorDecoratorFactory) {
        super(defaultStages, exceptionMappers, processorDecoratorFactory);
    }
}
