package gestiongastos.persistencia;

import gestiongastos.dominio.Gasto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GastoRepository {

    List<Gasto> findAll();

    Optional<Gasto> findById(UUID id);

    void save(Gasto gasto);

    void update(Gasto gasto);

    void deleteById(UUID id);
}
