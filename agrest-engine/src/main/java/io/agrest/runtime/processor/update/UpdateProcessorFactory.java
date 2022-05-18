package io.agrest.runtime.processor.update;

import io.agrest.UpdateStage;
import io.agrest.processor.ExceptionMappingProcessorDecoratorFactory;
import io.agrest.processor.Processor;
import io.agrest.runtime.AgExceptionMappers;

import java.util.EnumMap;

/**
 * @since 2.7
 */
public class UpdateProcessorFactory extends BaseUpdateProcessorFactory {

    public UpdateProcessorFactory(
            EnumMap<UpdateStage, Processor<UpdateContext<?>>> defaultStages,
            AgExceptionMappers exceptionMappers,
            ExceptionMappingProcessorDecoratorFactory processorDecoratorFactory) {
        super(defaultStages, exceptionMappers, processorDecoratorFactory);
    }
}
