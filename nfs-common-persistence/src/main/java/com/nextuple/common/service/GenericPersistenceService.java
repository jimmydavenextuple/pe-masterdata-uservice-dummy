package com.nextuple.common.service;

import java.util.Set;

public interface GenericPersistenceService<K, E> {

    E create(K key, E entity);

    E update(K key, E entity);

    E get(K key);

    Set<E> getAll(Set<K> keys);

    void delete(K key);
}
