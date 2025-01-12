package io.agrest.resolver;

import io.agrest.NestedResourceEntity;
import io.agrest.ResourceEntity;
import io.agrest.runtime.processor.select.SelectContext;

/**
 * @since 3.4
 */
public abstract class BaseDataResolver {

    protected void afterQueryAssembled(ResourceEntity<?> entity, SelectContext<?> context) {

        ResourceEntity<?> mapBy = entity.getMapBy();
        if (mapBy != null) {
            for (NestedResourceEntity c : mapBy.getChildren().values()) {
                c.getResolver().onParentQueryAssembled(c, context);
            }
        }

        for (NestedResourceEntity c : entity.getChildren().values()) {
            c.getResolver().onParentQueryAssembled(c, context);
        }
    }

    protected <T> void afterDataFetched(ResourceEntity<T> entity, Iterable<T> data, SelectContext<?> context) {

        ResourceEntity<?> mapBy = entity.getMapBy();
        if (mapBy != null) {
            for (NestedResourceEntity c : mapBy.getChildren().values()) {
                c.getResolver().onParentDataResolved(c, data, context);
            }
        }

        for (NestedResourceEntity c : entity.getChildren().values()) {
            c.getResolver().onParentDataResolved(c, data, context);
        }
    }
}
