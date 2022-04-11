package io.agrest.jpa.pocessor.update.stage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agrest.AgException;
import io.agrest.EntityUpdate;
import io.agrest.ObjectMapper;
import io.agrest.ObjectMapperFactory;
import io.agrest.jpa.exp.IJpaExpParser;
import io.agrest.jpa.persister.IAgJpaPersister;
import io.agrest.jpa.pocessor.IJpaQueryAssembler;
import io.agrest.protocol.Exp;
import io.agrest.runtime.processor.update.ByIdObjectMapperFactory;
import io.agrest.runtime.processor.update.ChangeOperation;
import io.agrest.runtime.processor.update.ChangeOperationType;
import io.agrest.runtime.processor.update.UpdateContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.metamodel.Metamodel;
import org.apache.cayenne.di.Inject;

/**
 * @since 5.0
 */
public class JpaMapUpdateStage extends JpaMapChangesStage {

    private final IJpaExpParser qualifierParser;
    private final IJpaQueryAssembler queryAssembler;
    protected final Metamodel metamodel;

    public JpaMapUpdateStage(
            @Inject IJpaExpParser qualifierParser,
            @Inject IJpaQueryAssembler queryAssembler,
            @Inject IAgJpaPersister persister) {
        this.qualifierParser = qualifierParser;
        this.queryAssembler = queryAssembler;
        this.metamodel = persister.metamodel();
    }

    protected void map(UpdateContext<Object> context) {

        ObjectMapper<Object> mapper = createObjectMapper(context);

        UpdateMap<Object> updateMap = mutableUpdatesByKey(context, mapper);

        collectUpdateDeleteOps(context, mapper, updateMap);
        collectCreateOps(context, updateMap);
    }

    protected <T> void collectUpdateDeleteOps(
            UpdateContext<T> context,
            ObjectMapper<T> mapper,
            UpdateMap<T> updateMap) {

        List<T> existing = existingObjects(context, updateMap.getIds(), mapper);
        if (existing.isEmpty()) {
            return;
        }

        List<ChangeOperation<T>> updateOps = new ArrayList<>(existing.size());
        for (T o : existing) {
            Object key = mapper.keyForObject(o);
            EntityUpdate<T> update = updateMap.remove(key);

            // a null can only mean some algorithm malfunction
            if (update == null) {
                throw AgException.internalServerError("Invalid key item: %s", key);
            }

            updateOps.add(new ChangeOperation<>(ChangeOperationType.UPDATE, update.getEntity(), o, update));
        }

        context.setChangeOperations(ChangeOperationType.UPDATE, updateOps);
    }

    protected <T> void collectCreateOps(
            UpdateContext<T> context,
            UpdateMap<T> updateMap) {

        if (!updateMap.getNoId().isEmpty()) {
            throw AgException.badRequest("Request is not idempotent.");
        }

        if (!updateMap.getIds().isEmpty()) {

            throw AgException.notFound(
                    "No object for ID '%s' and entity '%s'",
                    updateMap.getIds().iterator().next(),
                    context.getEntity().getName());
        }
    }

    protected <T> ObjectMapper<T> createObjectMapper(UpdateContext<T> context) {
        ObjectMapperFactory mapper = context.getMapper() != null
                ? context.getMapper()
                : ByIdObjectMapperFactory.mapper();
        return mapper.createMapper(context);
    }

    protected <T> UpdateMap<T> mutableUpdatesByKey(
            UpdateContext<T> context,
            ObjectMapper<T> mapper) {

        Collection<EntityUpdate<T>> updates = context.getUpdates();

        // sizing the map with one-update per key assumption
        Map<Object, EntityUpdate<T>> withId = new HashMap<>((int) (updates.size() / 0.75));
        List<EntityUpdate<T>> noId = new ArrayList<>();

        for (EntityUpdate<T> u : updates) {
            Object key = mapper.keyForUpdate(u);

            // The key can be "null", and the update may still be valid. It means it won't match anything in the
            // DB though, and the request can not be idempotent...

            if (key == null) {
                noId.add(u);
            } else {
                withId.merge(key, u, EntityUpdate::merge);
            }
        }

        return new UpdateMap<>(withId, noId);
    }

    protected <T> List<T> existingObjects(
            UpdateContext<T> context,
            Collection<Object> keys,
            ObjectMapper<T> mapper) {

        if (keys.isEmpty()) {
            return Collections.emptyList();
        }

        // TODO: implement this
//        return Collections.emptyList();

        String rootQualifier = qualifierForKeys(keys, mapper);

        EntityManager entityManager = JpaUpdateStartStage.entityManager(context);
        return entityManager.createQuery(
                "select e from " + context.getEntity().getName() + " e where " + rootQualifier,
                        context.getEntity().getType())
                .getResultList();

/*
        // TODO: split query in batches:
        // respect Constants.SERVER_MAX_ID_QUALIFIER_SIZE_PROPERTY
        // property of Cayenne , breaking query into subqueries.
        // Otherwise this operation will not scale.. Though I guess since we are
        // not using streaming API to read data from Cayenne, we are already
        // limited in how much data can fit in the memory map.

        Expression rootQualifier = qualifierForKeys(keys, mapper);
        if (rootQualifier == null) {
            return Collections.emptyList();
        }

        buildRootQuery(context.getEntity(), rootQualifier);

        // TODO: implement entity-tied resolvers for updates to avoid duplicating selecting logic

        List<T> objects = fetchRootEntity(CayenneUpdateStartStage.cayenneContext(context), context.getEntity());
        if (context.isById() && objects.size() > 1) {
            throw AgException.internalServerError(
                    "Found more than one object for ID '%s' and entity '%s'",
                    context.getId(),
                    context.getEntity().getName());
        }

        return objects;
 */
    }

    protected String qualifierForKeys(Collection<Object> keys, ObjectMapper<?> mapper) {
        StringBuilder str = new StringBuilder();
        for(Object key: keys) {
            if (key != null) {
                Exp e = mapper.expressionForKey(key);
                if (e != null) {
                    str.append(qualifierParser.parse(e).getExp());
                }
            }
        }
        return str.toString();
    }

/*
    protected Expression qualifierForKeys(Collection<Object> keys, ObjectMapper<?> mapper) {
        List<Expression> expressions = new ArrayList<>(keys.size());
        for (Object key : keys) {
            // update keys can be null... see a note in "mutableUpdatesByKey"
            if (key != null) {
                Exp e = mapper.expressionForKey(key);
                if (e != null) {
                    expressions.add(qualifierParser.parse(e));
                }
            }
        }

        return expressions.isEmpty() ? null : ExpressionFactory.or(expressions);
    }

    protected <T> ObjectSelect<T> buildRootQuery(RootResourceEntity<T> entity, Expression qualifier) {

        ObjectSelect<T> query = ObjectSelect.query(entity.getType()).where(qualifier);

        for (Map.Entry<String, NestedResourceEntity<?>> e : entity.getChildren().entrySet()) {
            buildNestedQuery(e.getValue(), query.getWhere());
        }

        CayenneProcessor.getRootEntity(entity).setSelect(query);
        return query;
    }

    protected ColumnSelect<Object[]> buildNestedQuery(
            NestedResourceEntity<?> entity,
            Expression parentQualifier) {

        ObjEntity parentObjEntity = entityResolver.getObjEntity(entity.getParent().getName());
        ObjRelationship incomingObjRelationship = parentObjEntity.getRelationship(entity.getIncoming().getName());

        // relationship may not be mapped in Cayenne...
        if (incomingObjRelationship == null) {
            return null;
        }

        ColumnSelect<Object[]> query = ObjectSelect.query(entity.getType())
                .where(translateExpressionToSource(incomingObjRelationship, parentQualifier))
                .columns(queryAssembler.queryColumns(entity));

        for (Map.Entry<String, NestedResourceEntity<?>> e : entity.getChildren().entrySet()) {
            buildNestedQuery(e.getValue(), query.getWhere());
        }

        CayenneProcessor.getNestedEntity(entity).setSelect(query);
        return query;
    }

    protected <T> List<T> fetchRootEntity(ObjectContext context, RootResourceEntity<T> entity) {

        List<T> objects = CayenneProcessor.getRootEntity(entity).getSelect().select(context);
        for (NestedResourceEntity<?> c : entity.getChildren().values()) {
            fetchNestedEntity(context, c);
        }

        return objects;
    }

    protected <T> void fetchNestedEntity(ObjectContext context, NestedResourceEntity<T> entity) {

        CayenneNestedResourceEntityExt ext = CayenneProcessor.getNestedEntity(entity);
        if (ext != null && ext.getSelect() != null) {
            List<Object[]> objects = ext.getSelect().select(context);
            assignChildrenToParent(entity, objects);

            for (NestedResourceEntity<?> c : entity.getChildren().values()) {
                fetchNestedEntity(context, c);
            }
        }
    }

    protected <T> void assignChildrenToParent(NestedResourceEntity<T> entity, List<Object[]> objects) {

        ResourceEntity<?> parentEntity = entity.getParent();
        for (Object[] row : objects) {

            if (row.length == 2) {
                entity.addData(new SimpleObjectId(row[1]), (T) row[0]);
            } else if (row.length > 2) {

                Map<String, Object> compoundKeys = new LinkedHashMap<>();
                AgAttribute[] idAttributes = parentEntity.getAgEntity().getIdParts().toArray(new AgAttribute[0]);
                if (idAttributes.length == (row.length - 1)) {
                    for (int i = 1; i < row.length; i++) {
                        compoundKeys.put(idAttributes[i - 1].getName(), row[i]);
                    }
                }
                entity.addData(new CompoundObjectId(compoundKeys), (T) row[0]);
            }
        }
    }

    protected Expression translateExpressionToSource(ObjRelationship relationship, Expression expression) {
        return expression != null
                ? relationship.getSourceEntity().translateToRelatedEntity(expression, relationship.getName())
                : null;
    }
 */
}