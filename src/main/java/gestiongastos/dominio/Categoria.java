package gestiongastos.dominio;

import java.util.Objects;
import java.util.UUID;

public class Categoria {
    private UUID id;
    private String nombre;
    private String colorHex; // opcional 

    public Categoria() { 
    	//json
    }

    public Categoria(String nombre, String colorHex) {
        this.id = UUID.randomUUID();
        this.nombre = nombre;
        this.colorHex = colorHex;
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

    public String getColorHex() {
    	return colorHex;
    }
    
    public void setColorHex(String colorHex) { 
    	this.colorHex = colorHex; 
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categoria)) return false;
        Categoria that = (Categoria) o;
        return Objects.equals(id, that.id);
    }
    @Override public int hashCode() {
    	return Objects.hash(id);
    }

    @Override public String toString() { 
    	return nombre; 
    }
}
