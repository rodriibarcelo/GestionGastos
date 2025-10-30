package gestiongastos.vista;

import java.time.LocalDate;
import java.util.UUID;

public class CriteriosFiltroGastos {

    private LocalDate desde;
    private LocalDate hasta;
    private UUID categoriaId;

    public CriteriosFiltroGastos() {
    }

    public LocalDate getDesde() {
        return desde;
    }

    public void setDesde(final LocalDate desde) {
        this.desde = desde;
    }

    public LocalDate getHasta() {
        return hasta;
    }

    public void setHasta(final LocalDate hasta) {
        this.hasta = hasta;
    }

    public UUID getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(final UUID categoriaId) {
        this.categoriaId = categoriaId;
    }
}
