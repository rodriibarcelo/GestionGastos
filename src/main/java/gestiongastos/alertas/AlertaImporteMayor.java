package gestiongastos.alertas;

import gestiongastos.dominio.Gasto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AlertaImporteMayor implements AlertaStrategy {

    private final BigDecimal limite;

    public AlertaImporteMayor(BigDecimal limite) {
        this.limite = limite;
    }

    @Override
    public Optional<String> evaluar(Gasto nuevo, List<Gasto> historico) {
        if (nuevo.getCantidad().compareTo(limite) > 0) {
            return Optional.of("El gasto (" + nuevo.getCantidad()
                    + "€) supera el límite de " + limite + "€");
        }
        return Optional.empty();
    }
}
