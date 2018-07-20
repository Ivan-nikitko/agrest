package com.nhl.link.rest.runtime.parser.converter;

import com.nhl.link.rest.meta.Types;
import com.nhl.link.rest.meta.compiler.BeanAnalyzer;
import com.nhl.link.rest.meta.compiler.PropertySetter;
import com.nhl.link.rest.parser.converter.CollectionConverter;
import com.nhl.link.rest.parser.converter.EnumConverter;
import com.nhl.link.rest.parser.converter.JsonValueConverter;
import com.nhl.link.rest.parser.converter.LazyConverter;
import com.nhl.link.rest.parser.converter.PojoConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @since 1.10
 */
public class DefaultJsonValueConverterFactory implements IJsonValueConverterFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJsonValueConverterFactory.class);

    protected Map<Type, JsonValueConverter<?>> convertersByJavaType;

    private JsonValueConverter<?> defaultConverter;

    public DefaultJsonValueConverterFactory(Map<Class<?>, JsonValueConverter<?>> knownConverters,
                                            JsonValueConverter<?> defaultConverter) {

        this.defaultConverter = defaultConverter;

        // creating a concurrent copy of the provided map - we'll be expanding it dynamically.
        this.convertersByJavaType = new ConcurrentHashMap<>(knownConverters);
    }

    @Override
    public JsonValueConverter<?> converter(Type valueType) {
        return getOrCreateConverter(valueType, () -> buildOrDefault(valueType));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<JsonValueConverter<T>> typedConverter(Class<T> valueType) {
        return Optional.ofNullable((JsonValueConverter<T>) getOrCreateConverter(valueType, () -> buildOrNull(valueType)));
    }

    private JsonValueConverter<?> getOrCreateConverter(Type valueType, Supplier<JsonValueConverter<?>> supplier) {
        JsonValueConverter<?> converter = convertersByJavaType.get(valueType);

        if (converter == null) {
            converter = supplier.get();
            if (converter != null) {
                JsonValueConverter<?> existing = convertersByJavaType.putIfAbsent(valueType, converter);
                if (existing != null) {
                    converter = existing;
                }
            }
        }

        return converter;
    }

    private JsonValueConverter<?> buildOrDefault(Type t) {
        return buildConverter(t).orElse(defaultConverter);
    }

    private JsonValueConverter<?> buildOrNull(Type t) {
        return buildConverter(t).orElse(null);
    }

    private Optional<JsonValueConverter<?>> buildConverter(Type t) {
        return Types.getClassForType(t).flatMap(cls -> buildConverter(cls, Optional.of(t)));
    }

    @SuppressWarnings("unchecked")
    private Optional<JsonValueConverter<?>> buildConverter(Class<?> cls, Optional<Type> t) {
        if (cls.isEnum()) {
            return Optional.of(enumConverter(cls));
        }

        if (Collection.class.isAssignableFrom(cls)) {
            Class<?> parameterType;
            if (t.isPresent()) {
                parameterType = Types.getClassForTypeArgument(t.get()).orElse(Object.class);
            } else {
                parameterType = Object.class;
            }

            return Optional.ofNullable(collectionConverter(cls, parameterType));
        }

        return Optional.ofNullable(objectConverter(cls));
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> JsonValueConverter<T> enumConverter(Class<?> enumType) {
        return new EnumConverter<>((Class<T>) enumType);
    }

    @SuppressWarnings("unchecked")
    private <T extends Collection<E>, E> JsonValueConverter<T> collectionConverter(Class<?> containerType, Class<E> elementType) {
        Supplier<T> containerSupplier;
        if (List.class.equals(containerType) || Collection.class.equals(containerType)) {
            containerSupplier = () -> (T) new ArrayList<>();
        } else if (Set.class.equals(containerType)) {
            containerSupplier = () -> (T) new HashSet<>();
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Unsupported collection type: " + containerType.getName());
            }
            return null;
        }
        JsonValueConverter<E> elementConverter = new LazyConverter<>(
                () -> typedConverter(elementType).orElse((JsonValueConverter<E>) defaultConverter));
        return new CollectionConverter<>(containerSupplier, elementConverter);
    }

    private JsonValueConverter<?> objectConverter(Class<?> cls) {
        Map<String, PropertySetter> setters = BeanAnalyzer.findSetters(cls)
                .collect(Collectors.toMap(PropertySetter::getName, Function.identity()));

        if (setters.isEmpty()) {
            return null;

        } else {
            Map<String, JsonValueConverter<?>> propertyConverters = setters.values().stream()
                .collect(Collectors.toMap(PropertySetter::getName, setter -> new LazyConverter<>(() -> buildConverter(setter))));
            return new PojoConverter<>(cls, setters, propertyConverters, defaultConverter);
        }
    }

    private JsonValueConverter<?> buildConverter(PropertySetter setter) {
        return converter(setter.getMethod().getGenericParameterTypes()[0]);
    }
}
