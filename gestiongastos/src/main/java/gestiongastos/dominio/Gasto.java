package gestiongastos.dominio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Gasto {
    private UUID id;
    private BigDecimal cantidad;     // > 0
    private LocalDate fecha;         // jsr310
    private UUID categoriaId;        // referencia
    private String nota;             // opcional
    private UUID cuentaCompartidaId; // opcional
    private UUID personaId;          // opcional (quién lo pagó)

    public Gasto() {
    	
    	
    }

    public Gasto(BigDecimal cantidad, LocalDate fecha, UUID categoriaId, String nota) {
        this.id = UUID.randomUUID();
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.categoriaId = categoriaId;
        this.nota = nota;
    }

    public UUID getId() { 
    	return id; 
    	}
    
    public void setId(UUID id) { 
    	this.id = id; 
    	}

    public BigDecimal getCantidad() { 
    	return cantidad; 
    	}
    
    public void setCantidad(BigDecimal cantidad) { 
    	this.cantidad = cantidad; 
    	}

    public LocalDate getFecha() { 
    	return fecha; 
    	}
    
    public void setFecha(LocalDate fecha) { 
    	this.fecha = fecha; 
    	}

    public UUID getCategoriaId() { 
    	return categoriaId; 
    	}
    
    public void setCategoriaId(UUID categoriaId) {
    	this.categoriaId = categoriaId; 
    	}

    public String getNota() { 
    	return nota; 
    	}
    
    public void setNota(String nota) { 
    	this.nota = nota; 
    	}

    public UUID getCuentaCompartidaId() { 
    	return cuentaCompartidaId; 
    	
    	}
    
    public void setCuentaCompartidaId(UUID cuentaCompartidaId) { 
    	this.cuentaCompartidaId = cuentaCompartidaId; 
    	}

    public UUID getPersonaId() {
    	return personaId; 
    	}
    
    public void setPersonaId(UUID personaId) { 
    	this.personaId = personaId; 
    	}

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gasto)) return false;
        return Objects.equals(id, ((Gasto) o).getId());
    }
    @Override public int hashCode() { 
    	return Objects.hash(id); 
    	}
}
