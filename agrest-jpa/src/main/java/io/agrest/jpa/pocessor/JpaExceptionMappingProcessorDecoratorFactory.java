package io.agrest.jpa.pocessor;

import io.agrest.processor.ExceptionMappingProcessorDecoratorFactory;
import io.agrest.processor.Processor;
import io.agrest.runtime.AgExceptionMappers;

public class JpaExceptionMappingProcessorDecoratorFactory implements ExceptionMappingProcessorDecoratorFactory {

    @Override
    public Processor get(Processor delegate, AgExceptionMappers exceptionMappers) {
        return new JpaExceptionMappingProcessorDecorator<>(delegate, exceptionMappers);
    }
}
