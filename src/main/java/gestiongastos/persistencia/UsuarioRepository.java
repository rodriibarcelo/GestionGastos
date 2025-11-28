package gestiongastos.persistencia;

import gestiongastos.dominio.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository {

    List<Usuario> findAll();

    Optional<Usuario> findById(UUID id);

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    void save(Usuario usuario);

    void update(Usuario usuario);

    void deleteById(UUID id);
}
