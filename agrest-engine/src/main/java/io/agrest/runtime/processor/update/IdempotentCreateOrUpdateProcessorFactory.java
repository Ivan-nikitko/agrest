package io.agrest.runtime.processor.update;

import io.agrest.UpdateStage;
import io.agrest.processor.ExceptionMappingProcessorDecoratorFactory;
import io.agrest.processor.Processor;
import io.agrest.runtime.AgExceptionMappers;

import java.util.EnumMap;

/**
 * @since 5.0
 */
public class IdempotentCreateOrUpdateProcessorFactory extends BaseUpdateProcessorFactory {

    public IdempotentCreateOrUpdateProcessorFactory(
            EnumMap<UpdateStage, Processor<UpdateContext<?>>> defaultStages,
            AgExceptionMappers exceptionMappers,
            ExceptionMappingProcessorDecoratorFactory processorDecoratorFactory) {
        super(defaultStages, exceptionMappers, processorDecoratorFactory);
    }
}
