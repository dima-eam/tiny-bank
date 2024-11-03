package org.eam.tinybank.dao;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Common generic in-memory storage, based on {@link java.util.concurrent.ConcurrentMap}, which is used to perform
 * Read-Modify-Write operations atomically. Provides generic API, such as store and update, and not business-specific
 * ones. To ease flow implementation, each method returns an {@link Optional}, so calling code can decide if a record
 * was previously stored or not.
 */
abstract class InMemoryDao<K, V> {

    private final Map<K, V> STORE = new ConcurrentHashMap<>();

    /**
     * Stores given value, if it is not in the storage. Returns empty optional if record is <b>new</b>.
     */
    protected Optional<V> stored(K key, V value) {
        return Optional.ofNullable(STORE.putIfAbsent(key, value));
    }

    /**
     * Replaces value for a given key, if it was stored before. Returns empty optional if record is <b>not found</b>.
     */
    protected Optional<V> updated(K key, Function<V, V> valueMapper) {
        return Optional.ofNullable(STORE.computeIfPresent(key, (k, v) -> valueMapper.apply(v)));
    }

    /**
     * Retrieves a value by given key. Returns empty optional if record is <b>not found</b>.
     */
    protected Optional<V> retrieved(K key) {
        return Optional.ofNullable(STORE.get(key));
    }

}