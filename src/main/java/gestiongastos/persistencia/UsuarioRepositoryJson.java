package gestiongastos.persistencia;

import com.fasterxml.jackson.core.type.TypeReference;
import gestiongastos.dominio.Usuario;

import java.nio.file.Path;
import java.util.*;

public class UsuarioRepositoryJson implements UsuarioRepository {

    private static final UsuarioRepositoryJson INSTANCE = new UsuarioRepositoryJson();

    private final Path filePath;
    private final List<Usuario> datos;

    private UsuarioRepositoryJson() {
    	this.filePath = Path.of("data", "usuarios.json").toAbsolutePath();
        this.datos = new ArrayList<>(
                JsonStorage.loadList(filePath, new TypeReference<List<Usuario>>() {})
        );
    }

    public static UsuarioRepositoryJson getInstance() {
        return INSTANCE;
    }

    @Override
    public List<Usuario> findAll() {
        return List.copyOf(datos);
    }

    @Override
    public Optional<Usuario> findById(UUID id) {
        return datos.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        return datos.stream()
                .filter(u -> u.getNombreUsuario().equalsIgnoreCase(nombreUsuario))
                .findFirst();
    }

    @Override
    public void save(Usuario usuario) {
        datos.add(usuario);
        flush();
    }


    @Override
    public void update(Usuario usuario) {
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getId().equals(usuario.getId())) {
                datos.set(i, usuario);
                flush();
                return;
            }
        }
    }

    @Override
    public void deleteById(UUID id) {
        datos.removeIf(u -> u.getId().equals(id));
        flush();
    }

    private void flush() {
        JsonStorage.saveList(filePath, datos);
    }

}
