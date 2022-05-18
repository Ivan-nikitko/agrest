package io.agrest.jpa.pocessor;

import io.agrest.jpa.persister.IAgJpaPersister;
import io.agrest.processor.ExceptionMappingProcessorDecorator;
import io.agrest.processor.ProcessingContext;
import io.agrest.processor.Processor;
import io.agrest.processor.ProcessorOutcome;
import io.agrest.runtime.AgExceptionMappers;
import jakarta.persistence.EntityManager;

public class JpaExceptionMappingProcessorDecorator<C extends ProcessingContext<?>> extends ExceptionMappingProcessorDecorator<C> {


    public JpaExceptionMappingProcessorDecorator
            (Processor<C> delegate,
             AgExceptionMappers exceptionMappers) {
        super(delegate, exceptionMappers);
    }

    @Override
    public ProcessorOutcome execute(C context) {
        EntityManager entityManager = null;
        try {
            return delegate.execute(context);
        } catch (Exception e) {
            try {
                entityManager = (EntityManager) context.getAttribute(IAgJpaPersister.ENTITY_MANAGER_KEY);
                if (entityManager != null && entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw exceptionMappers.toAgException(e);
            } finally {
                if (entityManager != null) {
                    entityManager.close();
                }
            }
        }
    }


}
