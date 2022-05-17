package io.agrest.jpa.pocessor.update.provider;

import io.agrest.UpdateStage;
import io.agrest.jpa.pocessor.JpaIdempotentFullSyncProcessorFactory;
import io.agrest.processor.Processor;
import io.agrest.runtime.AgExceptionMappers;
import io.agrest.runtime.processor.update.IdempotentFullSyncProcessorFactory;
import io.agrest.runtime.processor.update.UpdateContext;
import io.agrest.runtime.processor.update.UpdateFlavorDIKeys;
import io.agrest.runtime.processor.update.provider.IdempotentFullSyncProcessorFactoryProvider;
import io.agrest.runtime.processor.update.stage.*;
import org.apache.cayenne.di.DIRuntimeException;
import org.apache.cayenne.di.Inject;
import org.apache.cayenne.di.Provider;

import java.util.EnumMap;
/*

public class JpaIdempotentFullSyncProcessorFactoryProvider extends IdempotentFullSyncProcessorFactoryProvider {

    public JpaIdempotentFullSyncProcessorFactoryProvider(UpdateStartStage startStage,
                                                         UpdateParseRequestStage parseRequestStage,
                                                         UpdateCreateResourceEntityStage createResourceEntityStage,
                                                         UpdateApplyServerParamsStage applyServerParamsStage,
                                                         UpdateMapChangesStage mapChangesStage,
                                                         UpdateAuthorizeChangesStage authorizeChangesStage,
                                                         UpdateMergeChangesStage mergeStage,
                                                         UpdateCommitStage commitStage,
                                                         UpdateFillResponseStage fillResponseStage,
                                                         UpdateFilterResultStage filterResultStage,
                                                         UpdateEncoderInstallStage encoderInstallStage,
                                                         AgExceptionMappers exceptionMappers) {
        super(startStage,
                parseRequestStage,
                createResourceEntityStage,
                applyServerParamsStage,
                mapChangesStage,
                authorizeChangesStage,
                mergeStage,
                commitStage,
                fillResponseStage,
                filterResultStage,
                encoderInstallStage,
                exceptionMappers);
    }

    @Override
    public IdempotentFullSyncProcessorFactory get() throws DIRuntimeException {
        return new JpaIdempotentFullSyncProcessorFactory(idempotentFullSyncStages, exceptionMappers);
    }
}
*/

public class JpaIdempotentFullSyncProcessorFactoryProvider implements Provider<IdempotentFullSyncProcessorFactory> {

    protected final AgExceptionMappers exceptionMappers;
    protected final EnumMap<UpdateStage, Processor<UpdateContext<?>>> idempotentFullSyncStages;

    public JpaIdempotentFullSyncProcessorFactoryProvider(
            @Inject UpdateStartStage startStage,
            @Inject UpdateParseRequestStage parseRequestStage,
            @Inject UpdateCreateResourceEntityStage createResourceEntityStage,
            @Inject UpdateApplyServerParamsStage applyServerParamsStage,
            @Inject(UpdateFlavorDIKeys.IDEMPOTENT_FULL_SYNC) UpdateMapChangesStage mapChangesStage,
            @Inject UpdateAuthorizeChangesStage authorizeChangesStage,
            @Inject UpdateMergeChangesStage mergeStage,
            @Inject UpdateCommitStage commitStage,
            @Inject(UpdateFlavorDIKeys.IDEMPOTENT_FULL_SYNC) UpdateFillResponseStage fillResponseStage,
            @Inject UpdateFilterResultStage filterResultStage,
            @Inject UpdateEncoderInstallStage encoderInstallStage,
            @Inject AgExceptionMappers exceptionMappers
    ) {

        this.exceptionMappers = exceptionMappers;

        this.idempotentFullSyncStages = new EnumMap<>(UpdateStage.class);
        this.idempotentFullSyncStages.put(UpdateStage.START, startStage);
        this.idempotentFullSyncStages.put(UpdateStage.PARSE_REQUEST, parseRequestStage);
        this.idempotentFullSyncStages.put(UpdateStage.CREATE_ENTITY, createResourceEntityStage);
        this.idempotentFullSyncStages.put(UpdateStage.APPLY_SERVER_PARAMS, applyServerParamsStage);
        this.idempotentFullSyncStages.put(UpdateStage.MAP_CHANGES, mapChangesStage);
        this.idempotentFullSyncStages.put(UpdateStage.AUTHORIZE_CHANGES, authorizeChangesStage);
        this.idempotentFullSyncStages.put(UpdateStage.MERGE_CHANGES, mergeStage);
        this.idempotentFullSyncStages.put(UpdateStage.COMMIT, commitStage);
        this.idempotentFullSyncStages.put(UpdateStage.FILL_RESPONSE, fillResponseStage);
        this.idempotentFullSyncStages.put(UpdateStage.FILTER_RESULT, filterResultStage);
        this.idempotentFullSyncStages.put(UpdateStage.ENCODE, encoderInstallStage);
    }

    @Override
    public IdempotentFullSyncProcessorFactory get() throws DIRuntimeException {
        return new JpaIdempotentFullSyncProcessorFactory(idempotentFullSyncStages, exceptionMappers);
    }
}