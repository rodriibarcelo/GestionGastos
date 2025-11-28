package gestiongastos.dominio;

import java.util.Objects;
import java.util.UUID;

public class Persona {
    private UUID id;
    private String nombre;

    public Persona() {}

    public Persona(String nombre) {
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

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Persona)) return false;
        Persona persona = (Persona) o;
        return Objects.equals(id, persona.id);
    }
    @Override public int hashCode() { 
    	return Objects.hash(id); 
    	}
    
    @Override public String toString() { 
    	return nombre; 
    	}
}
