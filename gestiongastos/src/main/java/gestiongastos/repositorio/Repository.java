package gestiongastos.repositorio;

import java.util.*;

public interface Repository<T, K> {
    T save(T entity);
    Optional<T> findById(K id);
    List<T> findAll();
    void deleteById(K id);
    void deleteAll();
}
