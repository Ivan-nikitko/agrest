package io.agrest.jpa.pocessor.update.stage;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.agrest.AgException;
import io.agrest.CompoundObjectId;
import io.agrest.EntityParent;
import io.agrest.EntityUpdate;
import io.agrest.jpa.persister.IAgJpaPersister;
import io.agrest.meta.AgEntity;
import io.agrest.meta.AgRelationship;
import io.agrest.processor.ProcessorOutcome;
import io.agrest.runtime.processor.update.ChangeOperation;
import io.agrest.runtime.processor.update.ChangeOperationType;
import io.agrest.runtime.processor.update.UpdateContext;
import io.agrest.runtime.processor.update.stage.UpdateMergeChangesStage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.PluralAttribute;
import org.apache.cayenne.di.Inject;

/**
 * A processor invoked for {@link io.agrest.UpdateStage#MERGE_CHANGES} stage.
 *
 * @since 5.0
 */
public class JpaMergeChangesStage extends UpdateMergeChangesStage {

    private final Metamodel metamodel;

    public JpaMergeChangesStage(@Inject IAgJpaPersister persister) {
        this.metamodel = persister.metamodel();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ProcessorOutcome execute(UpdateContext<?> context) {
        merge((UpdateContext<Object>)context);
        return ProcessorOutcome.CONTINUE;
    }

    protected void merge(UpdateContext<Object> context) {
        Map<ChangeOperationType, List<ChangeOperation<Object>>> ops = context.getChangeOperations();
        if (ops.isEmpty()) {
            return;
        }

        ObjectRelator relator = createRelator(context);
        for (ChangeOperation<Object> op : ops.get(ChangeOperationType.CREATE)) {
            create(context, relator, op.getUpdate());
        }

        for (ChangeOperation<Object> op : ops.get(ChangeOperationType.UPDATE)) {
            update(context, relator, op.getObject(), op.getUpdate());
        }

        for (ChangeOperation<Object> op : ops.get(ChangeOperationType.DELETE)) {
            delete(context, op.getObject());
        }
    }

    protected void delete(UpdateContext<Object> context, Object o) {
        EntityManager entityManager = JpaUpdateStartStage.entityManager(context);
        entityManager.remove(o);
    }

    protected void create(UpdateContext<Object> context, ObjectRelator relator, EntityUpdate<Object> update) {

        EntityManager entityManager = JpaUpdateStartStage.entityManager(context);
        Object o;
        try {
            o = context.getType().getConstructor().newInstance();
        } catch (Exception e) {
            throw AgException.badRequest(e, "Unable to instantiate object of type: %s", context.getType().getName());
        }

        Map<String, Object> idByAgAttribute = update.getId();

        // set explicit ID
        if (idByAgAttribute != null) {
            if (context.isIdUpdatesDisallowed() && update.isExplicitId()) {
                throw AgException.badRequest("Setting ID explicitly is not allowed: %s", idByAgAttribute);
            }

            AgEntity<Object> agEntity = context.getEntity().getAgEntity();
            EntityType<Object> entity = metamodel.entity(context.getType());
            Map<Attribute<?, ?>, Object> idByJpaAttribute = mapToDbAttributes(agEntity, idByAgAttribute);

            // need to make an additional check that the AgId is unique
            checkExisting(entityManager, agEntity, idByAgAttribute);
            createSingleFromIdValues(entity, idByJpaAttribute, idByAgAttribute, o);
        }

        mergeChanges(context, update, o, relator);
        relator.relateToParent(o);
        entityManager.merge(o);
    }

    protected void update(UpdateContext<Object> context, ObjectRelator relator, Object o, EntityUpdate<Object> update) {
        EntityManager entityManager = JpaUpdateStartStage.entityManager(context);
        mergeChanges(context, update, o, relator);
        relator.relateToParent(o);
        entityManager.merge(o);
    }

    // translate "id" expressed in terms on public Ag names to Cayenne DbAttributes
    private Map<Attribute<?, ?>, Object> mapToDbAttributes(AgEntity<?> agEntity, Map<String, Object> idByAgAttribute) {

        Map<Attribute<?, ?>, Object> idByDbAttribute = new HashMap<>((int) (idByAgAttribute.size() / 0.75) + 1);
        for (Map.Entry<String, Object> e : idByAgAttribute.entrySet()) {
            Attribute<?, ?> attribute = attributeForAgAttribute(agEntity, e.getKey());
            if (attribute == null) {
                throw AgException.badRequest("Not a mapped persistent attribute '%s.%s'", agEntity.getName(), e.getKey());
            }
            idByDbAttribute.put(attribute, e.getValue());
        }

        return idByDbAttribute;
    }

    private void checkExisting(
            EntityManager entityManager,
            AgEntity<Object> agEntity,
            Map<String, Object> idByAgAttribute) {

        // TODO: implement multi-PK case
        Object existing = entityManager.find(agEntity.getType(), idByAgAttribute.values().iterator().next());
        if(existing != null) {
            throw AgException.badRequest("Can't create '%s' with id %s - already exists",
                    agEntity.getName(),
                    CompoundObjectId.mapToString(idByAgAttribute));
        }
    }

    private void createSingleFromIdValues(
            EntityType<Object> entity,
            Map<Attribute<?,?>, Object> idByDbAttribute,
            Map<String, Object> idByAgAttribute,
            Object o) {

        for (Map.Entry<Attribute<?,?>, Object> idPart : idByDbAttribute.entrySet()) {
            Attribute<?,?> attribute = idPart.getKey();
            if (attribute == null) {
                throw AgException.badRequest("Can't create '%s' with id %s - not an ID DB attribute: %s",
                        entity.getName(),
                        CompoundObjectId.mapToString(idByAgAttribute),
                        idPart.getKey());
            }

            writeProperty(o, attribute, idPart.getValue());
        }
    }

    private static Object safeInvoke(Method method, Object object, Object... value) {
        try {
            return method.invoke(object, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static void safeSet(Field field, Object object, Object value) {
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object safeGet(Field field, Object object) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void mergeChanges(UpdateContext<Object> context, EntityUpdate<Object> entityUpdate, Object o, ObjectRelator relator) {

        EntityManager manager = JpaUpdateStartStage.entityManager(context);
        EntityType<Object> entityType = metamodel.entity(entityUpdate.getEntity().getType());

        // attributes
        for (Map.Entry<String, Object> e : entityUpdate.getValues().entrySet()) {
            writeProperty(o, entityType.getAttribute(e.getKey()), e.getValue());
        }

        // relationships
        for (Map.Entry<String, Set<Object>> e : entityUpdate.getRelatedIds().entrySet()) {
            Attribute<?, ?> attribute = entityType.getAttribute(e.getKey());
            AgRelationship agRelationship = entityUpdate.getEntity().getRelationship(e.getKey());

            // sanity check
            if (agRelationship == null) {
                continue;
            }

            final Set<Object> relatedIds = e.getValue();
            if (relatedIds == null || relatedIds.isEmpty() || allElementsNull(relatedIds)) {
                relator.unrelateAll(agRelationship, o);
                continue;
            }

            if (!agRelationship.isToMany() && relatedIds.size() > 1) {
                throw AgException.badRequest(
                        "Relationship is to-one, but received update with multiple objects: %s",
                        agRelationship.getName());
            }


            relator.unrelateAll(agRelationship, o, new RelationshipUpdate() {
                @Override
                public boolean containsRelatedObject(Object relatedObject) {
                    Object id = manager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(relatedObject);
                    return relatedIds.contains(id);
                }

                @Override
                public void removeUpdateForRelatedObject(Object relatedObject) {
                    Object id = manager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(relatedObject);
                    relatedIds.remove(id);
                }
            });

            for (Object relatedId : relatedIds) {
                if (relatedId == null) {
                    continue;
                }

                // TODO: to-many collections will brake here!
                Object related = manager.find(attribute.getJavaType(), relatedId);

                if (related == null) {
                    throw AgException.notFound("Related object '%s' with ID '%s' is not found",
                            metamodel.entity(attribute.getJavaType()).getName(),
                            e.getValue());
                }

                relator.relate(agRelationship, o, related);
            }
        }

        entityUpdate.setMergedTo(o);
    }

    private boolean allElementsNull(Collection<?> elements) {

        for (Object element : elements) {
            if (element != null) {
                return false;
            }
        }

        return true;
    }

    protected ObjectRelator createRelator(UpdateContext<Object> context) {
        final EntityParent<?> parent = context.getParent();
        EntityType<Object> entityType = metamodel.entity(context.getEntity().getType());

        if (parent == null) {
            return new ObjectRelator(entityType);
        }

        EntityManager manager = JpaUpdateStartStage.entityManager(context);

        final Object parentObject = manager.find(parent.getType(), parent.getId());
        if (parentObject == null) {
            throw AgException.notFound("No parent object for ID '%s' and entity '%s'", parent.getId(), entityType.getName());
        }

        Attribute<?, ?> attribute = entityType.getAttribute(parent.getRelationship());
        if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_MANY
                || attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE) {
            return new ObjectRelator(entityType) {
                @Override
                public void relateToParent(Object object) {
                    setToManyTarget(parentObject, (PluralAttribute<?,?,?>)attribute, object);
                }
            };
        } else {
            return new ObjectRelator(entityType) {
                @Override
                public void relateToParent(Object object) {
                    setToOneTarget(parentObject, attribute, object);
                }
            };
        }
    }

    private static void setToOneTarget(Object parent, Attribute<?,?> attribute, Object object) {
        writeProperty(parent, attribute, object);
    }

    @SuppressWarnings("unchecked")
    private static void setToManyTarget(Object parent, PluralAttribute<?,?,?> attribute, Object object) {
        Object collection = readProperty(parent, attribute);
        switch (attribute.getCollectionType()) {
            case MAP:
                // TODO: how do we get the key?
                break;
            case SET:
            case LIST:
            case COLLECTION:
                ((Collection<Object>)collection).add(object);
        }
    }

    private static void removeToManyTarget(Object parent, PluralAttribute<?,?,?> attribute, Object object) {
        Object collection = readProperty(parent, attribute);
        switch (attribute.getCollectionType()) {
            case MAP:
                ((Map<?,?>)collection).entrySet().removeIf(e -> e.getValue() == object);
                break;
            case SET:
            case LIST:
            case COLLECTION:
                ((Collection<?>)collection).remove(object);
                break;
        }
    }

    private static Object readProperty(Object object, Attribute<?,?> attribute) {
        Member javaMember = attribute.getJavaMember();
        if(javaMember instanceof Method) {
            return safeInvoke((Method) javaMember, object);
        } else if(javaMember instanceof Field) {
            return safeGet((Field) javaMember, object);
        } else {
            throw AgException.badRequest("Can't get attribute '%s' for the entity %s",
                    attribute.getName(),
                    attribute.getDeclaringType().getJavaType().getName());
        }
    }

    private static void writeProperty(Object object, Attribute<?, ?> attribute, Object value) {
        Member javaMember = attribute.getJavaMember();
        if(javaMember instanceof Method) {
            safeInvoke((Method) javaMember, object, value);
        } else if(javaMember instanceof Field) {
            safeSet((Field) javaMember, object, value);
        } else {
            throw AgException.badRequest("Can't set attribute '%s' for the entity %s",
                    attribute.getName(),
                    attribute.getDeclaringType().getJavaType().getName());
        }
    }

    protected Attribute<?, ?> attributeForAgAttribute(AgEntity<?> agEntity, String attributeName) {
        return metamodel.entity(agEntity.getType()).getAttribute(attributeName);
    }

    interface RelationshipUpdate {
        boolean containsRelatedObject(Object o);

        void removeUpdateForRelatedObject(Object o);
    }

    static class ObjectRelator {

        private final EntityType<Object> entityType;

        ObjectRelator(EntityType<Object> entityType) {
            this.entityType = entityType;
        }

        void relateToParent(Object object) {
            // do nothing
        }

        void relate(AgRelationship agRelationship, Object object, Object relatedObject) {
            Attribute<? super Object, ?> attribute = entityType.getAttribute(agRelationship.getName());
            if (agRelationship.isToMany()) {
                setToManyTarget(object, (PluralAttribute<?, ?, ?>) attribute, relatedObject);
            } else {
                setToOneTarget(object, attribute, relatedObject);
            }
        }

        void unrelateAll(AgRelationship agRelationship, Object object) {
            unrelateAll(agRelationship, object, null);
        }

        void unrelateAll(AgRelationship agRelationship, Object object, RelationshipUpdate relationshipUpdate) {
            Attribute<? super Object, ?> attribute = entityType.getAttribute(agRelationship.getName());
            if (agRelationship.isToMany()) {
                @SuppressWarnings("unchecked")
                List<Object> relatedObjects = (List<Object>) readProperty(object, attribute);

                for (int i = 0; i < relatedObjects.size(); i++) {
                    Object relatedObject = relatedObjects.get(i);
                    if (relationshipUpdate == null || !relationshipUpdate.containsRelatedObject(relatedObject)) {
                        removeToManyTarget(object, (PluralAttribute<?, ?, ?>) attribute, relatedObject);
                        i--;
                    } else {
                        relationshipUpdate.removeUpdateForRelatedObject(relatedObject);
                    }
                }
            } else {
                setToOneTarget(object, attribute, null);
            }
        }
    }
}