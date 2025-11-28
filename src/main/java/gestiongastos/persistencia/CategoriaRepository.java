package gestiongastos.persistencia;

import gestiongastos.dominio.Categoria;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaRepository {

    List<Categoria> findAll();

    Optional<Categoria> findById(UUID id);

    void save(Categoria categoria);

    void update(Categoria categoria);

    void deleteById(UUID id);
}
