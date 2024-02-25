package com.example.test.Services;

import java.util.List;
import java.util.Optional;

public interface CrudService<T, ID_T> {
    List<T> getAll();

    T create(T entity);

    Optional<T> findById(ID_T id);

    void delete(ID_T id);
}
