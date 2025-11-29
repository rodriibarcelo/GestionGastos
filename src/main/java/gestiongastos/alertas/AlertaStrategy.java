package gestiongastos.alertas;

import gestiongastos.dominio.Gasto;
import java.util.List;
import java.util.Optional;

public interface AlertaStrategy {

    Optional<String> evaluar(Gasto nuevoGasto, List<Gasto> historico);

}
