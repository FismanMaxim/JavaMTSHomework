package com.example.test.Models.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Stores information about entity and returns the same object of the entity in get() method
 * @param <T> Type of entity
 */
public abstract class EntityDTO<T> {
    protected T entity;

    @JsonIgnore
    public final T get() {
        if (entity == null) entity = buildEntity();
        return entity;
    }

    protected abstract T buildEntity();
}
