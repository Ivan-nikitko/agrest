package io.agrest.processor;

import io.agrest.runtime.AgExceptionMappers;

public interface ExceptionMappingProcessorDecoratorFactory <C extends ProcessingContext<?>> {

    Processor<C> get(Processor<C> delegate, AgExceptionMappers exceptionMappers);
}
