package io.agrest.runtime.encoder;

import io.agrest.AgException;
import io.agrest.NestedResourceEntity;
import io.agrest.ResourceEntity;
import io.agrest.converter.valuestring.ValueStringConverters;
import io.agrest.encoder.DataResponseEncoder;
import io.agrest.encoder.EncodableProperty;
import io.agrest.encoder.Encoder;
import io.agrest.encoder.EntityEncoder;
import io.agrest.encoder.EntityNoIdEncoder;
import io.agrest.encoder.GenericEncoder;
import io.agrest.encoder.ListEncoder;
import io.agrest.encoder.MapByEncoder;
import io.agrest.meta.AgAttribute;
import io.agrest.meta.AgRelationship;
import io.agrest.processor.ProcessingContext;
import io.agrest.property.PropertyReader;
import io.agrest.runtime.semantics.IRelationshipMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

/**
 * @since 3.4
 */
public class DataEncoderFactory {

    protected final IEncodablePropertyFactory propertyFactory;
    protected final IRelationshipMapper relationshipMapper;
    protected final ValueStringConverters converters;

    public DataEncoderFactory(
            IEncodablePropertyFactory propertyFactory,
            ValueStringConverters converters,
            IRelationshipMapper relationshipMapper) {

        this.propertyFactory = propertyFactory;
        this.relationshipMapper = relationshipMapper;
        this.converters = converters;
    }

    public <T> Encoder encoder(ResourceEntity<T> entity, ProcessingContext<T> context) {
        Encoder dataEncoder = dataEncoder(entity, context);
        return new DataResponseEncoder("data", dataEncoder, "total", GenericEncoder.encoder());
    }

    protected Encoder dataEncoder(ResourceEntity<?> entity, ProcessingContext<?> context) {
        Encoder elementEncoder = collectionElementEncoder(entity, context);
        Encoder listEncoder = new ListEncoder(elementEncoder);
        return entity.getMapBy() != null ? mapByEncoder(entity, listEncoder, context) : listEncoder;
    }

    protected Encoder toOneEncoder(ResourceEntity<?> resourceEntity, ProcessingContext<?> context) {
        return entityEncoder(resourceEntity, context);
    }

    protected Encoder nestedToManyEncoder(ResourceEntity<?> entity, ProcessingContext<?> context) {
        Encoder elementEncoder = collectionElementEncoder(entity, context);
        ListEncoder listEncoder = new ListEncoder(elementEncoder);
        return entity.getMapBy() != null ? mapByEncoder(entity, listEncoder, context) : listEncoder;
    }

    protected Encoder collectionElementEncoder(ResourceEntity<?> resourceEntity, ProcessingContext<?> context) {
        return entityEncoder(resourceEntity, context);
    }

    /**
     * Recursively builds an Encoder for the ResourceEntity tree.
     *
     * @param entity root entity to be encoded
     * @return a new Encoder for the provided ResourceEntity tree
     */
    protected Encoder entityEncoder(ResourceEntity<?> entity, ProcessingContext<?> context) {

        Map<String, EncodableProperty> encoders = propertyEncoders(entity, context);

        EncodableProperty ide = entity.isIdIncluded()
                ? propertyFactory.getIdProperty(entity).orElse(null)
                : null;

        return ide != null
                ? new EntityEncoder(ide, encoders)
                : new EntityNoIdEncoder(encoders);
    }

    protected Map<String, EncodableProperty> propertyEncoders(ResourceEntity<?> entity, ProcessingContext<?> context) {
        List<Map.Entry<String, EncodableProperty>> entries = new ArrayList<>();

        for (AgAttribute attribute : entity.getAttributes().values()) {
            EncodableProperty property = propertyFactory.getAttributeProperty(entity, attribute);
            entries.add(entry(attribute.getName(), property));
        }

        for (Map.Entry<String, NestedResourceEntity<?>> e : entity.getChildren().entrySet()) {

            // read relationship vis child entity to account for overlays
            AgRelationship relationship = entity.getChild(e.getKey()).getIncoming();

            Encoder encoder = relationship.isToMany()
                    ? nestedToManyEncoder(e.getValue(), context)
                    : toOneEncoder(e.getValue(), context);

            EncodableProperty property = propertyFactory.getRelationshipProperty(
                    entity,
                    relationship,
                    encoder,
                    context);

            entries.add(entry(e.getKey(), property));
        }

        switch (entries.size()) {
            case 0:
                return Collections.emptyMap();
            case 1:
                return Map.ofEntries(entries.get(0));
            default:
                // sort properties alphabetically to ensure predictable and user-friendly JSON
                entries.sort(Map.Entry.comparingByKey());
                Map<String, EncodableProperty> sorted = new LinkedHashMap<>((int) Math.ceil(entries.size() / 0.75));
                entries.forEach(e -> sorted.put(e.getKey(), e.getValue()));

                return sorted;
        }
    }

    protected MapByEncoder mapByEncoder(ResourceEntity<?> entity, Encoder encoder, ProcessingContext<?> context) {
        return mapByEncoder(entity.getMapBy(), new ArrayList<>(), encoder, entity.getMapByPath(), context);
    }

    protected MapByEncoder mapByEncoder(
            ResourceEntity<?> mapBy,
            List<PropertyReader> readerChain,
            Encoder encoder,
            String mapByPath,
            ProcessingContext<?> context) {

        // map by id
        if (mapBy.isIdIncluded()) {
            validateLeafMapBy(mapBy, mapByPath);
            readerChain.add(mapBy.getAgEntity().getIdReader());

            return new MapByEncoder(mapByPath,
                    readerChain,
                    encoder,
                    true,
                    converters.getConverter(Object.class));
        }

        // map by property
        if (!mapBy.getAttributes().isEmpty()) {
            validateLeafMapBy(mapBy, mapByPath);

            Map.Entry<String, AgAttribute> attribute = mapBy.getAttributes().entrySet().iterator().next();
            readerChain.add(propertyFactory.getAttributeProperty(mapBy, attribute.getValue()).getReader());
            return new MapByEncoder(mapByPath,
                    readerChain,
                    encoder,
                    false,
                    converters.getConverter(attribute.getValue().getType()));
        }

        // descend into relationship
        if (!mapBy.getChildren().isEmpty()) {

            Map.Entry<String, NestedResourceEntity<?>> child = mapBy.getChildren().entrySet().iterator().next();

            // TODO: to account for overlaid relationships (and avoid NPEs), we should not access agEntity...
            //  instead should look for incoming rel of a child ResourceEntity.. Is is present in MapBy case?
            AgRelationship relationship = mapBy.getChild(child.getKey()).getIncoming();
            readerChain.add(propertyFactory.getRelationshipProperty(mapBy, relationship, null, context).getReader());

            return mapByEncoder(mapBy.getChildren().get(child.getKey()), readerChain, encoder, mapByPath, context);
        }

        // map by relationship (implicitly by id)
        readerChain.add(mapBy.getAgEntity().getIdReader());

        return new MapByEncoder(mapByPath,
                readerChain,
                encoder,
                true,
                converters.getConverter(Object.class));
    }

    protected void validateLeafMapBy(ResourceEntity<?> mapBy, String mapByPath) {

        if (!mapBy.getChildren().isEmpty()) {

            String pathSegment = (mapBy instanceof NestedResourceEntity)
                    ? ((NestedResourceEntity<?>) mapBy).getIncoming().getName()
                    : "";

            throw AgException.badRequest(
                    "'mapBy' path segment '%s' should not have children. Full 'mapBy' path: %s",
                    pathSegment,
                    mapByPath);
        }
    }
}
