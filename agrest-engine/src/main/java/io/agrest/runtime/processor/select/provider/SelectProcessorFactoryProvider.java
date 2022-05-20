package io.agrest.runtime.processor.select.provider;

import io.agrest.SelectStage;
import io.agrest.processor.ExceptionMappingProcessorDecoratorFactory;
import io.agrest.processor.Processor;
import io.agrest.runtime.AgExceptionMappers;
import io.agrest.runtime.processor.select.SelectContext;
import io.agrest.runtime.processor.select.SelectProcessorFactory;
import io.agrest.runtime.processor.select.stage.*;
import org.apache.cayenne.di.DIRuntimeException;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.di.Provider;

import java.util.EnumMap;

/**
 * @since 2.7
 */
public class SelectProcessorFactoryProvider implements Provider<SelectProcessorFactory> {

    private final EnumMap<SelectStage, Processor<SelectContext<?>>> stages;
    private final AgExceptionMappers exceptionMappers;
    private ExceptionMappingProcessorDecoratorFactory processorDecoratorFactory;

    public SelectProcessorFactoryProvider(
            @Inject SelectStartStage startStage,
            @Inject SelectCreateResourceEntityStage createResourceEntityStage,
            @Inject SelectApplyServerParamsStage applyServerParamsStage,
            @Inject SelectAssembleQueryStage assembleQueryStage,
            @Inject SelectFetchDataStage fetchDataStage,
            @Inject SelectFilterResultStage filterResultStage,
            @Inject SelectEncoderInstallStage encoderInstallStage,

            @Inject AgExceptionMappers exceptionMappers,
            @Inject ExceptionMappingProcessorDecoratorFactory processorDecoratorFactory) {

        this.exceptionMappers = exceptionMappers;
        this.processorDecoratorFactory = processorDecoratorFactory;

        stages = new EnumMap<>(SelectStage.class);
        stages.put(SelectStage.START, startStage);
        stages.put(SelectStage.CREATE_ENTITY, createResourceEntityStage);
        stages.put(SelectStage.APPLY_SERVER_PARAMS, applyServerParamsStage);
        stages.put(SelectStage.ASSEMBLE_QUERY, assembleQueryStage);
        stages.put(SelectStage.FETCH_DATA, fetchDataStage);
        stages.put(SelectStage.FILTER_RESULT, filterResultStage);
        stages.put(SelectStage.ENCODE, encoderInstallStage);
    }

    @Override
    public SelectProcessorFactory get() throws DIRuntimeException {
        return new SelectProcessorFactory(stages, exceptionMappers, processorDecoratorFactory);
    }
}
