package gestiongastos.dominio;

import java.math.BigDecimal;
import java.util.UUID;

public class Participacion {

    private UUID usuarioId;
    private UUID cuentaId;
    private double porcentaje; 
    private BigDecimal saldo = BigDecimal.ZERO;


    public Participacion() {
        this.saldo = BigDecimal.ZERO;
    }

    public Participacion(UUID usuarioId, UUID cuentaId, double porcentaje) {
        this.usuarioId = usuarioId;
        this.cuentaId = cuentaId;
        this.porcentaje = porcentaje;
        this.saldo = BigDecimal.ZERO;

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
    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }


    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }
}
