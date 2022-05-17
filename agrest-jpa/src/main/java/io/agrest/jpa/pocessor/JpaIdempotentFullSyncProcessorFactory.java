package io.agrest.jpa.pocessor;

import io.agrest.UpdateStage;
import io.agrest.processor.Processor;
import io.agrest.processor.ProcessorOutcome;
import io.agrest.runtime.AgExceptionMappers;
import io.agrest.runtime.processor.update.IdempotentFullSyncProcessorFactory;
import io.agrest.runtime.processor.update.UpdateContext;

import java.util.EnumMap;

public class JpaIdempotentFullSyncProcessorFactory extends IdempotentFullSyncProcessorFactory {

    private final AgExceptionMappers exceptionMappers;

    public JpaIdempotentFullSyncProcessorFactory(EnumMap<UpdateStage, Processor<UpdateContext<?>>> defaultStages,
                                                 AgExceptionMappers exceptionMappers) {
        super(defaultStages, exceptionMappers);
        this.exceptionMappers = exceptionMappers;
    }


    @Override
    protected Processor<UpdateContext<?>> composeStages(EnumMap<UpdateStage, Processor<UpdateContext<?>>> stages) {

        if (stages.isEmpty()) {
            return c -> ProcessorOutcome.CONTINUE;
        }
        Processor p = null;

        // note that EnumMap iterates in the ordinal order of the underlying enum.
        // This is important for ordering stages...
        for (Processor s : stages.values()) {
            p = p == null ? s : p.andThen(s);
        }

        return new JpaExceptionMappingProcessorDecorator(p, exceptionMappers);
    }
}
