package io.agrest.meta;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @since 2.0
 */
public abstract class BaseLazyAgEntity<T, E extends AgEntity<T>> {

    private Supplier<E> delegateSupplier;
    private volatile E delegate;

    public BaseLazyAgEntity(Supplier<E> delegateSupplier) {
        this.delegateSupplier = Objects.requireNonNull(delegateSupplier);
    }

    protected final E getDelegate() {

        if (delegate == null) {
            synchronized (this) {
                if (delegate == null) {
                    delegate = delegateSupplier.get();
                    delegateSupplier = null;
                }
            }
        }
        return delegate;
    }
}
