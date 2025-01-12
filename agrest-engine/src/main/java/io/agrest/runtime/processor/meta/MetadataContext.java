package io.agrest.runtime.processor.meta;

import io.agrest.MetadataResponse;
import io.agrest.encoder.Encoder;
import io.agrest.meta.AgResource;
import io.agrest.processor.BaseProcessingContext;
import org.apache.cayenne.di.Injector;

import java.util.Collection;

/**
 * @since 1.18
 * @deprecated since 4.1, as Agrest now integrates with OpenAPI 3 / Swagger.
 */
@Deprecated
public class MetadataContext<T> extends BaseProcessingContext<T> {

    private Class<?> resourceType;
    private String baseUri;
    private Encoder encoder;
    private Collection<AgResource<T>> resources;

    public MetadataContext(Class<T> type, Injector injector) {
        super(type, injector);
    }

    /**
     * Returns a new response object reflecting the context state.
     *
     * @return a newly created response object reflecting the context state.
     * @since 1.24
     */
    public MetadataResponse<T> createMetadataResponse() {
        MetadataResponse<T> response = new MetadataResponse<>(getStatus(), getType());
        response.setEncoder(encoder);
        response.setResources(resources);

        return response;
    }

    public void setResource(Class<?> resourceClass) {
        this.resourceType = resourceClass;
    }

    public Class<?> getResource() {
        return resourceType;
    }

    /**
     * @since 1.24
     */
    public Encoder getEncoder() {
        return encoder;
    }

    /**
     * @since 1.24
     */
    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    /**
     * @since 1.24
     */
    public Collection<AgResource<T>> getResources() {
        return resources;
    }

    /**
     * @since 1.24
     */
    public void setResources(Collection<AgResource<T>> resources) {
        this.resources = resources;
    }


    /**
     * @since 5.0
     */
    public String getBaseUri() {
        return baseUri;
    }

    /**
     * @since 5.0
     */
    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }
}
