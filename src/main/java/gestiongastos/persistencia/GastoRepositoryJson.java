package gestiongastos.persistencia;

import com.fasterxml.jackson.core.type.TypeReference;
import gestiongastos.dominio.Gasto;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GastoRepositoryJson implements GastoRepository {

    private static final GastoRepositoryJson INSTANCE = new GastoRepositoryJson();

    private final Path filePath;
    private final List<Gasto> datos;

    private GastoRepositoryJson() {
    	this.filePath = Path.of("data", "gastos.json").toAbsolutePath();
        this.datos = new ArrayList<>(
                JsonStorage.loadList(filePath, new TypeReference<List<Gasto>>() {})
        );
    }

    public static GastoRepositoryJson getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized List<Gasto> findAll() {
        return List.copyOf(datos);
    }

    @Override
    public synchronized Optional<Gasto> findById(final UUID id) {
        return datos.stream()
                    .filter(g -> g.getId().equals(id))
                    .findFirst();
    }

    @Override
    public synchronized void save(final Gasto gasto) {
        datos.add(gasto);
        flush();
    }

    @Override
    public synchronized void update(final Gasto gasto) {
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getId().equals(gasto.getId())) {
                datos.set(i, gasto);
                flush();
                return;
            }
        }
    }

    @Override
    public synchronized void deleteById(final UUID id) {
        datos.removeIf(g -> g.getId().equals(id));
        flush();
    }

    private void flush() {
        JsonStorage.saveList(filePath, datos);
    }
}
