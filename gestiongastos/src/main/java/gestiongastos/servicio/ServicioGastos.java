package gestiongastos.servicio;

import gestiongastos.dominio.Gasto;
import gestiongastos.repositorio.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ServicioGastos {

    private final Repository<Gasto, UUID> repoGastos;

    public ServicioGastos(Repository<Gasto, UUID> repoGastos) {
        this.repoGastos = repoGastos;
    }

    public Gasto registrar(Gasto g) {
        // TODO: validaciones (cantidad>0, fecha no futura, etc.)
        return repoGastos.save(g);
    }

    public List<Gasto> listar() { 
    	
    	return repoGastos.findAll(); 
    	
    	}

    public List<Gasto> filtrar(LocalDate desde, LocalDate hasta, List<UUID> categorias) {
        return repoGastos.findAll().stream()
                .filter(g -> (desde == null || !g.getFecha().isBefore(desde)))
                .filter(g -> (hasta == null || !g.getFecha().isAfter(hasta)))
                .filter(g -> (categorias == null || categorias.isEmpty() || categorias.contains(g.getCategoriaId())))
                .collect(Collectors.toList());
    }
}
