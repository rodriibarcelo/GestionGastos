package gestiongastos.persistencia;

import com.fasterxml.jackson.core.type.TypeReference;
import gestiongastos.dominio.CuentaCompartida;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CuentaCompartidaRepositoryJson implements CuentaCompartidaRepository {

    private final Path filePath;
    private final List<CuentaCompartida> cuentas;

    public CuentaCompartidaRepositoryJson() {
        // data/cuentas_compartidas.json (igual estilo que usuarios.json)
        this.filePath = Path.of("data", "cuentas_compartidas.json").toAbsolutePath();
        this.cuentas = new ArrayList<>(
                JsonStorage.loadList(filePath, new TypeReference<List<CuentaCompartida>>() {})
        );
    }

    @Override
    public List<CuentaCompartida> findAll() {
        return new ArrayList<>(cuentas);
    }

    @Override
    public Optional<CuentaCompartida> findById(UUID id) {
        return cuentas.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    @Override
    public void save(CuentaCompartida cuenta) {
        // si ya existe, la reemplazamos
        cuentas.removeIf(c -> c.getId().equals(cuenta.getId()));
        cuentas.add(cuenta);
        flush();
    }

    @Override
    public void deleteById(UUID id) {
        cuentas.removeIf(c -> c.getId().equals(id));
        flush();
    }

    private void flush() {
        JsonStorage.saveList(filePath, cuentas);
    }
}
