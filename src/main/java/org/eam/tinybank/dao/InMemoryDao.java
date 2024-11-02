package org.eam.tinybank.dao;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

abstract class InMemoryDao<K, V> {

    private final Map<K, V> STORE = new ConcurrentHashMap<>();

    void store(K key, V value) {
        STORE.putIfAbsent(key, value);
    }

    Optional<V> update(K key, V value) {
        return Optional.ofNullable(STORE.computeIfPresent(key, (k, v) -> value));
    }

}