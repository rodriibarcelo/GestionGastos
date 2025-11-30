package gestiongastos.servicio;

import gestiongastos.dominio.CuentaCompartida;
import gestiongastos.dominio.Participacion;
import gestiongastos.persistencia.CuentaCompartidaRepository;
import gestiongastos.persistencia.CuentaCompartidaRepositoryJson;

import java.util.List;
import java.util.UUID;

public class ServicioCuentasCompartidas {

    private final CuentaCompartidaRepository repo;

    public ServicioCuentasCompartidas() {
        this.repo = CuentaCompartidaRepositoryJson.getInstance();
    }

    // ===== LISTAR =====
    public List<CuentaCompartida> listar() {
        return repo.findAll();
    }

    // ===== CREAR =====
    public CuentaCompartida crear(String nombre, List<Participacion> participantes) {
        CuentaCompartida c = new CuentaCompartida();
        c.setId(UUID.randomUUID());
        c.setNombre(nombre);
        c.setParticipantes(participantes); // <-- AQUÍ ESTÁ LA CLAVE
        repo.save(c);
        return c;
    }

    // ===== ACTUALIZAR =====
    public void actualizar(CuentaCompartida cuenta) {
        repo.update(cuenta);
    }

    // ===== BORRAR =====
    public void borrar(UUID id) {
        repo.deleteById(id);
    }

    public CuentaCompartida buscarPorId(UUID id) {
        return repo.findById(id).orElse(null);
    }
}
