package gestiongastos.alertas;

import gestiongastos.dominio.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AlertaTotalDiario implements AlertaStrategy {

    private final BigDecimal limite;

    public AlertaTotalDiario(BigDecimal limite) {
        this.limite = limite;
    }

    @Override
    public Optional<String> evaluar(Gasto nuevo, List<Gasto> historico) {

        LocalDate fecha = nuevo.getFecha();

        BigDecimal acumulado = historico.stream()
                .filter(g -> g.getFecha().equals(fecha))
                .map(Gasto::getCantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(nuevo.getCantidad());

        if (acumulado.compareTo(limite) > 0) {
            return Optional.of("⚠ El total de hoy (" + acumulado
                    + "€) supera el límite de " + limite + "€");
        }

        return Optional.empty();
    }
}
