package gestiongastos.servicio;

import gestiongastos.dominio.Categoria;
import gestiongastos.repositorio.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ServicioCategorias {

    private final Repository<Categoria, UUID> repo;

    public ServicioCategorias(final Repository<Categoria, UUID> repo) {
        this.repo = repo;
    }

    public Categoria crear(final String nombre, final String colorHex) {
        Categoria c = new Categoria(nombre, colorHex);
        repo.save(c);
        return c;
    }
    
    public void borrar(final UUID id) {
        repo.deleteById(id);
    }

    public void actualizar(final UUID id, final String nombre, final String colorHex) {
        repo.findById(id).ifPresent(c -> {
            c.setNombre(nombre);
            c.setColorHex(colorHex);
            repo.save(c);
        });
    }

    

    public List<Categoria> listarTodas() {
        return repo.findAll();
    }

    public Optional<Categoria> buscarPorId(final UUID id) {
        return repo.findById(id);
    }
}
