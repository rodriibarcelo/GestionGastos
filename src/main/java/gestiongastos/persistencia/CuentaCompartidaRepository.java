package gestiongastos.persistencia;

import gestiongastos.dominio.CuentaCompartida;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CuentaCompartidaRepository {

    List<CuentaCompartida> findAll();

    Optional<CuentaCompartida> findById(UUID id);

    void save(CuentaCompartida cuenta);

    void deleteById(UUID id);
}
	