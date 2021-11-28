package io.agrest.meta;

import java.util.HashMap;
import java.util.Map;

class AgEntityOverlayResolver {

    final AgDataMap dataMap;
    final Map<String, AgIdPart> ids;
    final Map<String, AgAttribute> attributes;
    final Map<String, AgRelationship> relationships;

    AgEntityOverlayResolver(AgDataMap dataMap, AgEntity<?> sourceEntity) {
        this.dataMap = dataMap;

        this.ids = new HashMap<>();
        sourceEntity.getIdParts().forEach(p -> ids.put(p.getName(), p));

        this.attributes = new HashMap<>();
        sourceEntity.getAttributes().forEach(a -> attributes.put(a.getName(), a));

        this.relationships = new HashMap<>();
        sourceEntity.getRelationships().forEach(r -> relationships.put(r.getName(), r));
    }

    void loadAttributeOverlay(AgAttributeOverlay overlay) {
        attributes.put(overlay.getName(), overlay.resolve(attributes.get(overlay.getName())));
    }

    void loadRelationshipOverlay(AgRelationshipOverlay overlay) {
        relationships.put(overlay.getName(), overlay.resolve(relationships.get(overlay.getName()), dataMap));
    }

    void makeUnreadable(String name) {
        AgIdPart id = ids.get(name);
        if (id != null) {
            if (id.isReadable()) {
                ids.put(name, new DefaultAgIdPart(name, id.getType(), false, id.isWritable(), id.getReader(), id.getPathExp()));
            }

            return;
        }

        AgAttribute a = attributes.get(name);
        if (a != null) {
            if (a.isReadable()) {
                attributes.put(name, new DefaultAgAttribute(name, a.getType(), false, a.isWritable(), a.getPropertyReader()));
            }

            return;
        }

        AgRelationship r = relationships.get(name);
        if (r != null) {
            if (r.isReadable()) {
                relationships.put(name, new DefaultAgRelationship(name, r.getTargetEntity(), r.isToMany(), false, r.isWritable(), r.getResolver()));
            }

            return;
        }
    }

    void makeUnwritable(String name) {
        AgIdPart id = ids.get(name);
        if (id != null) {
            if (id.isWritable()) {
                ids.put(name, new DefaultAgIdPart(name, id.getType(), id.isReadable(), false, id.getReader(), id.getPathExp()));
            }

            return;
        }

        AgAttribute a = attributes.get(name);
        if (a != null) {
            if (a.isWritable()) {
                attributes.put(name, new DefaultAgAttribute(name, a.getType(), a.isReadable(), false, a.getPropertyReader()));
            }

            return;
        }

        AgRelationship r = relationships.get(name);
        if (r != null) {
            if (r.isWritable()) {
                relationships.put(name, new DefaultAgRelationship(name, r.getTargetEntity(), r.isToMany(), r.isReadable(), false, r.getResolver()));
            }

            return;
        }
    }
}