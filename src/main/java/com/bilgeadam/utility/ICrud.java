package com.bilgeadam.utility;

import java.util.List;
import java.util.Optional;

public interface ICrud<T,ID> {

    T save(T entity);

    Iterable<T> saveAll(Iterable<T> entites);

    void delete(T entity);
    void deleteById(ID id);

    Optional<T> findByID(ID id);

    boolean existById(ID id);

    List<T> findAll();

    List<T> findByEntity(T entity);
    List<T> findByColumnNameAndValue(String columnName, String value);
}
