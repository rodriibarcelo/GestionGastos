package gestiongastos.servicio;

import gestiongastos.dominio.CuentaCompartida;
import gestiongastos.dominio.Participacion;
import gestiongastos.persistencia.CuentaCompartidaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ServicioCuentasCompartidas {

    private final CuentaCompartidaRepository repo;

    public ServicioCuentasCompartidas(CuentaCompartidaRepository repo) {
        this.repo = repo;
    }

    public List<CuentaCompartida> listar() {
        return repo.findAll();
    }

 // ===== CREAR =====
    public CuentaCompartida crear(String nombre, List<Participacion> participantes) {

        if (participantes == null || participantes.isEmpty()) {
            throw new IllegalArgumentException("La cuenta debe tener al menos una persona");
        }

        // 1) Si TODOS los porcentajes son 0 => distribución equitativa por defecto
        boolean todosCero = participantes.stream()
                .allMatch(p -> p.getPorcentaje() == 0.0);

        if (todosCero) {
            double base = 100.0 / participantes.size();
            double acumulado = 0.0;

            for (int i = 0; i < participantes.size(); i++) {
                double valor;
                if (i == participantes.size() - 1) {
                    // el último ajusta para que la suma sea EXACTAMENTE 100
                    valor = 100.0 - acumulado;
                } else {
                    valor = Math.round(base * 100.0) / 100.0; // 2 decimales
                    acumulado += valor;
                }
                participantes.get(i).setPorcentaje(valor);
            }
        }

        // 2) Validar que la suma de porcentajes es 100%
        double suma = participantes.stream()
                .mapToDouble(Participacion::getPorcentaje)
                .sum();

        if (Math.abs(suma - 100.0) > 0.01) {  // pequeña tolerancia por decimales
            throw new IllegalArgumentException(
                    "La suma de los porcentajes de la cuenta debe ser 100% (ahora es " + suma + "%)"
            );
        }

        // 3) Crear y guardar la cuenta
        CuentaCompartida c = new CuentaCompartida();
        c.setId(UUID.randomUUID());
        c.setNombre(nombre);
        c.setParticipantes(participantes);
        repo.save(c);
        return c;
    }


    public void deleteById(UUID id) {
        repo.deleteById(id); // 🔥 CORRECTO → deleteById NO existe en el repo
    }

    public void actualizarCuenta(UUID id, String nuevoNombre, List<Participacion> nuevasParticipaciones) {
        CuentaCompartida c = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

        c.setNombre(nuevoNombre);
        c.setParticipantes(nuevasParticipaciones);

        repo.save(c);
    }
    public void registrarGastoEnCuenta(UUID cuentaId, UUID usuarioIdPagador, BigDecimal importe) {
        CuentaCompartida cuenta = repo.findById(cuentaId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

        cuenta.registrarGasto(importe, usuarioIdPagador);
        repo.save(cuenta); 
    }

	public void actualizar(CuentaCompartida sel) {
		repo.save(sel);
		
	}
}
