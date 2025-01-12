package io.agrest.runtime.entity;

import io.agrest.NestedResourceEntity;
import io.agrest.ResourceEntity;
import io.agrest.RootResourceEntity;
import io.agrest.ToManyResourceEntity;
import io.agrest.ToOneResourceEntity;
import io.agrest.meta.AgDataMap;
import io.agrest.meta.AgEntityOverlay;
import org.apache.cayenne.di.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @since 2.13
 */
public class MapByMerger implements IMapByMerger {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapByMerger.class);

    private final AgDataMap dataMap;

    public MapByMerger(@Inject AgDataMap dataMap) {
        this.dataMap = dataMap;
    }

    @Override
    public <T> void merge(ResourceEntity<T> entity, String mapByPath, Map<Class<?>, AgEntityOverlay<?>> overlays) {
        if (mapByPath == null) {
            return;
        }

        if (entity == null) {
            LOGGER.info("Ignoring 'mapBy : {}' for non-relationship property", mapByPath);
            return;
        }

        if (entity instanceof NestedResourceEntity && !((NestedResourceEntity) entity).getIncoming().isToMany()) {
            LOGGER.info("Ignoring 'mapBy : {}' for to-one relationship property", mapByPath);
            return;
        }

        ResourceEntity<?> mapByCompanionEntity = entity instanceof NestedResourceEntity
                ? mapByCompanionEntity((NestedResourceEntity) entity)
                : mapByCompanionEntity((RootResourceEntity) entity);

        new ResourceEntityTreeBuilder(mapByCompanionEntity, dataMap, overlays).inflatePath(mapByPath);
        entity.mapBy(mapByCompanionEntity, mapByPath);
    }

    protected <T> RootResourceEntity<?> mapByCompanionEntity(RootResourceEntity<T> entity) {
        return new RootResourceEntity<>(entity.getAgEntity());
    }

    protected <T> NestedResourceEntity<?> mapByCompanionEntity(NestedResourceEntity<T> entity) {
        return entity instanceof ToOneResourceEntity
                ? new ToOneResourceEntity<>(entity.getAgEntity(), entity.getParent(), entity.getIncoming())
                : new ToManyResourceEntity<>(entity.getAgEntity(), entity.getParent(), entity.getIncoming());
    }
}
