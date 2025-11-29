package gestiongastos.dominio;

import java.util.UUID;

public class Participacion {

    private UUID usuarioId;
    private UUID cuentaId;
    private double porcentaje; // 0–100

    public Participacion() {
    }

    public Participacion(UUID usuarioId, UUID cuentaId, double porcentaje) {
        this.usuarioId = usuarioId;
        this.cuentaId = cuentaId;
        this.porcentaje = porcentaje;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UUID getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(UUID cuentaId) {
        this.cuentaId = cuentaId;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }
}
