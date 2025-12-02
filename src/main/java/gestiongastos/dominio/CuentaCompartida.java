package gestiongastos.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CuentaCompartida {

    private UUID id;
    private String nombre;
    private List<Participacion> participantes = new ArrayList<>();

    public CuentaCompartida() {
        // Necesario para JSON
    }

    public CuentaCompartida(String nombre, List<Participacion> participantes) {
        this.id = UUID.randomUUID();
        this.nombre = nombre;
        this.participantes = participantes != null ? participantes : new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Participacion> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<Participacion> participantes) {
        this.participantes = participantes;
    }
    
    public void registrarGasto(BigDecimal importe, UUID usuarioIdPagador) {
        if (participantes == null || participantes.isEmpty()) return;

        for (Participacion p : participantes) {
            // Parte que corresponde a este participante según su porcentaje
            BigDecimal parte = importe
                    .multiply(BigDecimal.valueOf(p.getPorcentaje()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (p.getUsuarioId().equals(usuarioIdPagador)) {
                // Ha pagado TODO el gasto, pero solo le correspondía "parte"
                // saldo += (importe - parte)
                BigDecimal nuevoSaldo = p.getSaldo().add(importe.subtract(parte));
                p.setSaldo(nuevoSaldo);
            } else {
                // Este no ha pagado; solo debe su parte
                BigDecimal nuevoSaldo = p.getSaldo().subtract(parte);
                p.setSaldo(nuevoSaldo);
            }
        }
    }
}
