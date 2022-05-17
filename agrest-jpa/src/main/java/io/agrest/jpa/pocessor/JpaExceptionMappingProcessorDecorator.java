package io.agrest.jpa.pocessor;

import io.agrest.jpa.pocessor.update.stage.JpaUpdateStartStage;
import io.agrest.processor.ExceptionMappingProcessorDecorator;
import io.agrest.processor.ProcessingContext;
import io.agrest.processor.Processor;
import io.agrest.processor.ProcessorOutcome;
import io.agrest.runtime.AgExceptionMappers;

public class JpaExceptionMappingProcessorDecorator extends ExceptionMappingProcessorDecorator  {


    public JpaExceptionMappingProcessorDecorator(Processor delegate, AgExceptionMappers exceptionMappers) {
        super(delegate, exceptionMappers);
    }

    @Override
    public ProcessorOutcome execute(ProcessingContext context) {
        try {
            return delegate.execute(context);
        } catch (Throwable e){
            try {
                System.out.println("Check!!!!!!!!!");
                JpaUpdateStartStage.entityManager(context)
                        .getTransaction()
                        .rollback();
            } finally {
                JpaUpdateStartStage.entityManager(context).close();
            }
            throw e;
        }
    }
}
