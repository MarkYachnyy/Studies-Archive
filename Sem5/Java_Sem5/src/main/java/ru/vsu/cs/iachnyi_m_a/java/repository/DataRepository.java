package ru.vsu.cs.iachnyi_m_a.java.repository;

import java.util.List;
import java.util.Optional;

public interface DataRepository<E, I> {
    Optional<E> findById(I id);
    List<E> findAll();
    E save(E entity);
    void delete(I id);
}
