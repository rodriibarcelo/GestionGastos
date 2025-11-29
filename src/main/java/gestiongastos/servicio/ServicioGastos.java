package gestiongastos.servicio;

import gestiongastos.dominio.Gasto;
import gestiongastos.persistencia.GastoRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ServicioGastos {

    private final GastoRepository repo;

    public ServicioGastos(GastoRepository repo) {
        this.repo = repo;
    }

    public Gasto registrar(Gasto g) {
        repo.save(g);
        return g;
    }

    public List<Gasto> listar() {
        return repo.findAll();
    }

    public void actualizar(UUID id, BigDecimal cantidad, LocalDate fecha, UUID categoriaId, String nota) {

        repo.findById(id).ifPresent(g -> {
            g.setCantidad(cantidad);
            g.setFecha(fecha);
            g.setCategoriaId(categoriaId);
            g.setNota(nota == null ? "" : nota.trim());
            repo.update(g);
        });
    }

    public void borrar(UUID id) {
        repo.deleteById(id);
    }

    public List<Gasto> filtrar(LocalDate desde, LocalDate hasta, UUID categoriaId) {

        return repo.findAll().stream()
                .filter(g -> desde == null || !g.getFecha().isBefore(desde))
                .filter(g -> hasta == null || !g.getFecha().isAfter(hasta))
                .filter(g -> categoriaId == null || categoriaId.equals(g.getCategoriaId()))
                .collect(Collectors.toList());
    }

    public BigDecimal total(LocalDate desde, LocalDate hasta, UUID categoriaId) {

        return filtrar(desde, hasta, categoriaId)
                .stream()
                .map(Gasto::getCantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
