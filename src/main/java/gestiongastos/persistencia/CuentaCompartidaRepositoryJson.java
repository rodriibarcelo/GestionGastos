package gestiongastos.persistencia;

import com.fasterxml.jackson.core.type.TypeReference;
import gestiongastos.dominio.CuentaCompartida;

import java.nio.file.Path;
import java.util.*;

public class CuentaCompartidaRepositoryJson {

    private static final CuentaCompartidaRepositoryJson INSTANCE = new CuentaCompartidaRepositoryJson();

    private final Path filePath;
    private final List<CuentaCompartida> datos;

    private CuentaCompartidaRepositoryJson() {
        this.filePath = Path.of("data", "cuentas.json").toAbsolutePath();
        this.datos = new ArrayList<>(
                JsonStorage.loadList(filePath, new TypeReference<List<CuentaCompartida>>() {})
        );
    }

    public static CuentaCompartidaRepositoryJson getInstance() {
        return INSTANCE;
    }

    public List<CuentaCompartida> findAll() {
        return List.copyOf(datos);
    }

    public Optional<CuentaCompartida> findById(UUID id) {
        return datos.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public void save(CuentaCompartida c) {
        datos.add(c);
        flush();
    }

    public void update(CuentaCompartida c) {
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getId().equals(c.getId())) {
                datos.set(i, c);
                flush();
                return;
            }
        }
    }

    private void flush() {
        JsonStorage.saveList(filePath, datos);
    }
}
