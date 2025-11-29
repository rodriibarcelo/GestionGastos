package gestiongastos.servicio;

import gestiongastos.dominio.CuentaCompartida;
import gestiongastos.dominio.Participacion;
import gestiongastos.persistencia.CuentaCompartidaRepositoryJson;

import java.util.List;
import java.util.UUID;

public class ServicioCuentasCompartidas {

    private final CuentaCompartidaRepositoryJson repo;

    public ServicioCuentasCompartidas() {
        this.repo = CuentaCompartidaRepositoryJson.getInstance();
    }

    public CuentaCompartida crear(String nombre, List<Participacion> participantes) {

        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre no puede estar vacío.");

        if (participantes == null || participantes.isEmpty())
            throw new IllegalArgumentException("Debe haber al menos un participante.");

        CuentaCompartida c = new CuentaCompartida(nombre);
        participantes.forEach(c::addParticipante);

        repo.save(c);
        return c;
    }

    public List<CuentaCompartida> listar() {
        return repo.findAll();
    }

    public void actualizar(CuentaCompartida c) {
        repo.update(c);
    }

    public CuentaCompartida buscarPorId(UUID id) {
        return repo.findById(id).orElse(null);
    }
}
