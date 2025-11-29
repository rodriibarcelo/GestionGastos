package gestiongastos.servicio;

import gestiongastos.dominio.Categoria;
import gestiongastos.persistencia.CategoriaRepository;

import java.util.List;
import java.util.UUID;

public class ServicioCategorias {

    private final CategoriaRepository repo;

    public ServicioCategorias(CategoriaRepository repo) {
        this.repo = repo;
    }

    public Categoria crear(String nombre, String colorHex) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }

        Categoria c = new Categoria();
        c.setId(UUID.randomUUID());
        c.setNombre(nombre);
        c.setColorHex(colorHex);

        repo.save(c);
        return c;
    }

    public List<Categoria> listarTodas() {
        return repo.findAll();
    }

    public void actualizar(UUID id, String nuevoNombre, String nuevoColorHex) {

        repo.findById(id).ifPresent(c -> {
            if (nuevoNombre == null || nuevoNombre.isBlank()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío.");
            }

            c.setNombre(nuevoNombre);
            c.setColorHex(nuevoColorHex);
            repo.update(c);
        });
    }

    public void borrar(UUID id) {
        repo.deleteById(id);
    }
}
