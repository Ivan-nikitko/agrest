package io.agrest.runtime.processor.select.stage;

import io.agrest.AgRequest;
import io.agrest.RootResourceEntity;
import io.agrest.meta.AgDataMap;
import io.agrest.meta.AgEntity;
import io.agrest.meta.AgEntityOverlay;
import io.agrest.processor.Processor;
import io.agrest.processor.ProcessorOutcome;
import io.agrest.runtime.entity.IExcludeMerger;
import io.agrest.runtime.entity.IExpMerger;
import io.agrest.runtime.entity.IIncludeMerger;
import io.agrest.runtime.entity.IMapByMerger;
import io.agrest.runtime.entity.ISizeMerger;
import io.agrest.runtime.entity.ISortMerger;
import io.agrest.runtime.processor.select.SelectContext;
import org.apache.cayenne.di.Inject;

/**
 * @since 2.13
 */
public class SelectCreateResourceEntityStage implements Processor<SelectContext<?>> {

    private AgDataMap dataMap;
    private IExpMerger expMerger;
    private ISortMerger sortMerger;
    private IMapByMerger mapByMerger;
    private ISizeMerger sizeMerger;
    private IIncludeMerger includeMerger;
    private IExcludeMerger excludeMerger;

    public SelectCreateResourceEntityStage(
            @Inject AgDataMap dataMap,
            @Inject IExpMerger expMerger,
            @Inject ISortMerger sortMerger,
            @Inject IMapByMerger mapByMerger,
            @Inject ISizeMerger sizeMerger,
            @Inject IIncludeMerger includeMerger,
            @Inject IExcludeMerger excludeMerger) {

        this.dataMap = dataMap;
        this.sortMerger = sortMerger;
        this.expMerger = expMerger;
        this.mapByMerger = mapByMerger;
        this.sizeMerger = sizeMerger;
        this.includeMerger = includeMerger;
        this.excludeMerger = excludeMerger;
    }

    @Override
    public ProcessorOutcome execute(SelectContext<?> context) {
        doExecute(context);
        return ProcessorOutcome.CONTINUE;
    }

    protected <T> void doExecute(SelectContext<T> context) {
        AgEntityOverlay<T> overlay = context.getEntityOverlay(context.getType());
        AgEntity<T> entity = dataMap.getEntity(context.getType());

        RootResourceEntity<T> resourceEntity = new RootResourceEntity<>(
                overlay != null ? overlay.resolve(dataMap, entity) : entity
        );

        AgRequest request = context.getRequest();

        sizeMerger.merge(resourceEntity, request.getStart(), request.getLimit());
        includeMerger.merge(resourceEntity, request.getIncludes(), context.getEntityOverlays());
        excludeMerger.merge(resourceEntity, request.getExcludes());
        sortMerger.merge(resourceEntity, request.getOrderings());
        mapByMerger.merge(resourceEntity, request.getMapBy(), context.getEntityOverlays());
        expMerger.merge(resourceEntity, request.getExp());

        context.setEntity(resourceEntity);
    }
}
