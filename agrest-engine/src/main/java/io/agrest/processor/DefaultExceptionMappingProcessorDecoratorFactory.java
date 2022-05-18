package io.agrest.processor;

import io.agrest.runtime.AgExceptionMappers;


public class DefaultExceptionMappingProcessorDecoratorFactory implements ExceptionMappingProcessorDecoratorFactory{

    @Override
    public Processor get(Processor delegate, AgExceptionMappers exceptionMappers) {
        return new ExceptionMappingProcessorDecorator (delegate, exceptionMappers);
    }
}
