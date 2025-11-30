package gestiongastos.dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CuentaCompartida {

    private UUID id;
    private String nombre;
    private List<Participacion> participantes = new ArrayList<>();

    public CuentaCompartida() {
    }

    public CuentaCompartida(String nombre) {
        this.id = UUID.randomUUID();
        this.nombre = nombre;
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

    public void addParticipante(Participacion p) {
        this.participantes.add(p);
    }
    
    @Override
    public String toString() {
        return nombre != null ? nombre : "(sin nombre)";
    }

}
