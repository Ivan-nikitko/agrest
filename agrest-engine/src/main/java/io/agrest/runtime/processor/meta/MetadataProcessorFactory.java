package io.agrest.runtime.processor.meta;

import io.agrest.MetadataStage;
import io.agrest.processor.Processor;
import io.agrest.processor.ProcessorFactory;

import java.util.EnumMap;

/**
 * @since 2.7
 */
public class MetadataProcessorFactory extends ProcessorFactory<MetadataStage, MetadataContext<?>> {

    public MetadataProcessorFactory(EnumMap<MetadataStage, Processor<MetadataContext<?>>> defaultStages) {
        super(defaultStages);
    }
}
