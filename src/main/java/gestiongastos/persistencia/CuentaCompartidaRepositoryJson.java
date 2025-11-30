package gestiongastos.persistencia;

import com.fasterxml.jackson.core.type.TypeReference;
import gestiongastos.dominio.CuentaCompartida;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CuentaCompartidaRepositoryJson implements CuentaCompartidaRepository {

    private static final CuentaCompartidaRepositoryJson INSTANCE = new CuentaCompartidaRepositoryJson();

    private final Path filePath;
    private final List<CuentaCompartida> datos;

    private CuentaCompartidaRepositoryJson() {
        this.filePath = Path.of("data", "cuentas_compartidas.json");
        this.datos = new ArrayList<>(
                JsonStorage.loadList(filePath, new TypeReference<List<CuentaCompartida>>() {})
        );
    }

    public static CuentaCompartidaRepositoryJson getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized List<CuentaCompartida> findAll() {
        return List.copyOf(datos);
    }

    @Override
    public synchronized Optional<CuentaCompartida> findById(UUID id) {
        return datos.stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst();
    }

    @Override
    public synchronized void save(CuentaCompartida cuenta) {
        datos.add(cuenta);
        flush();
    }

    @Override
    public synchronized void update(CuentaCompartida cuenta) {
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getId().equals(cuenta.getId())) {
                datos.set(i, cuenta);
                flush();
                return;
            }
        }
    }

    @Override
    public synchronized void deleteById(UUID id) {
        datos.removeIf(c -> c.getId().equals(id));
        flush();
    }

    private void flush() {
        JsonStorage.saveList(filePath, datos);
    }
}
