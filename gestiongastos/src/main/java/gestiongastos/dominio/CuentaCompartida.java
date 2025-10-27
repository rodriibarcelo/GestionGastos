package gestiongastos.dominio;

import java.util.*;

public class CuentaCompartida {
    public enum TipoDistribucion { EQUITATIVA, PORCENTAJE }

    private UUID id;
    private String nombre;
    private List<Persona> personas = new ArrayList<>();             // fija tras crear
    private TipoDistribucion tipo = TipoDistribucion.EQUITATIVA;
    private Map<UUID, Integer> porcentajes = new HashMap<>();       // suma 100 si PORCENTAJE

    public CuentaCompartida() {}

    public CuentaCompartida(String nombre, List<Persona> personas, TipoDistribucion tipo) {
        this.id = UUID.randomUUID();
        this.nombre = nombre;
        this.personas = new ArrayList<>(Objects.requireNonNull(personas));
        this.tipo = tipo == null ? TipoDistribucion.EQUITATIVA : tipo;
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

    public List<Persona> getPersonas() { 
    	return Collections.unmodifiableList(personas); 
    }
    
    public void setPersonas(List<Persona> personas) { 
    	this.personas = new ArrayList<>(personas); 
    }

    public TipoDistribucion getTipo() { 
    	
    	return tipo; 
    }
    
    public void setTipo(TipoDistribucion tipo) { 
    	this.tipo = tipo; 
    	}

    public Map<UUID, Integer> getPorcentajes() { 
    	return porcentajes; 
    	}
    
    public void setPorcentajes(Map<UUID, Integer> porcentajes) { 
    	this.porcentajes = porcentajes; 
    	}

    // Utilidad: valida suma
    public boolean porcentajesValidos() {
        if (tipo != TipoDistribucion.PORCENTAJE) return true;
        return porcentajes.values().stream().mapToInt(Integer::intValue).sum() == 100
                && porcentajes.keySet().containsAll(idsPersonas());
    }

    private Set<UUID> idsPersonas() {
        Set<UUID> ids = new HashSet<>();
        for (Persona p : personas) ids.add(p.getId());
        return ids;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CuentaCompartida)) return false;
        return Objects.equals(id, ((CuentaCompartida) o).id);
    }
    @Override public int hashCode() { 
    	return Objects.hash(id); 
    	}
}
