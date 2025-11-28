package gestiongastos.persistencia;

import com.fasterxml.jackson.core.type.TypeReference;
import gestiongastos.dominio.Categoria;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoriaRepositoryJson implements CategoriaRepository {

    private static final CategoriaRepositoryJson INSTANCE = new CategoriaRepositoryJson();

    private final Path filePath;
    private final List<Categoria> datos;

    private CategoriaRepositoryJson() {
    	this.filePath = Path.of("data", "categorias.json").toAbsolutePath();
        this.datos = new ArrayList<>(
                JsonStorage.loadList(filePath, new TypeReference<List<Categoria>>() {})
        );
    }

    public static CategoriaRepositoryJson getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized List<Categoria> findAll() {
        return List.copyOf(datos);
    }

    @Override
    public synchronized Optional<Categoria> findById(final UUID id) {
        return datos.stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst();
    }

    @Override
    public synchronized void save(final Categoria categoria) {
        datos.add(categoria);
        flush();
    }

    @Override
    public synchronized void update(final Categoria categoria) {
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getId().equals(categoria.getId())) {
                datos.set(i, categoria);
                flush();
                return;
            }
        }
    }

    @Override
    public synchronized void deleteById(final UUID id) {
        datos.removeIf(c -> c.getId().equals(id));
        flush();
    }

    private void flush() {
        JsonStorage.saveList(filePath, datos);
    }
}
