package gestiongastos.alertas;

import gestiongastos.dominio.Gasto;

import java.util.ArrayList;
import java.util.List;

public class GestorAlertas {

    private final List<AlertaStrategy> estrategias = new ArrayList<>();

    public void setEstrategias(List<AlertaStrategy> nuevas) {
        estrategias.clear();
        estrategias.addAll(nuevas);
    }

    public List<String> evaluar(Gasto nuevo, List<Gasto> historico) {
        List<String> mensajes = new ArrayList<>();
        for (AlertaStrategy s : estrategias) {
            s.evaluar(nuevo, historico).ifPresent(mensajes::add);
        }
        return mensajes;
    }
}
