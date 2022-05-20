package io.agrest.runtime.processor.delete;

import io.agrest.DeleteStage;
import io.agrest.processor.ExceptionMappingProcessorDecoratorFactory;
import io.agrest.processor.Processor;
import io.agrest.processor.ProcessorFactory;
import io.agrest.runtime.AgExceptionMappers;

import java.util.EnumMap;

/**
 * @since 2.7
 */
public class DeleteProcessorFactory extends ProcessorFactory<DeleteStage, DeleteContext<?>> {

    public DeleteProcessorFactory(
            EnumMap<DeleteStage, Processor<DeleteContext<?>>> defaultStages,
            AgExceptionMappers exceptionMappers,
            ExceptionMappingProcessorDecoratorFactory processorDecoratorFactory) {
        super(defaultStages, exceptionMappers, processorDecoratorFactory);
    }
}
