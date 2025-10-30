package gestiongastos.controlador;

import gestiongastos.dominio.Categoria;
import gestiongastos.dominio.Gasto;
import gestiongastos.repositorio.InMemoryRepository;
import gestiongastos.repositorio.Repository;
import gestiongastos.servicio.ServicioCategorias;
import gestiongastos.servicio.ServicioGastos;
import gestiongastos.persistencia.ServicioPersistencia;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Controlador {

    private static final Controlador INSTANCE = new Controlador();

    private final Repository<Gasto, UUID> repoGasto;
    private final Repository<Categoria, UUID> repoCategoria;

    private final ServicioGastos servicioGastos;
    private final ServicioCategorias servicioCategorias;

    private final ServicioPersistencia persistencia;

    private Controlador() {
        this.repoGasto = new InMemoryRepository<>(Gasto::getId);
        this.repoCategoria = new InMemoryRepository<>(Categoria::getId);

        this.servicioGastos = new ServicioGastos(repoGasto);
        this.servicioCategorias = new ServicioCategorias(repoCategoria);

        this.persistencia = new ServicioPersistencia("data");

        cargarDatosIniciales();
        registrarShutdownHook();
    }

    public static Controlador getInstance() {
        return INSTANCE;
    }

    private void cargarDatosIniciales() {
        try {
            List<Categoria> cats = persistencia.cargarCategorias();
            for (Categoria c : cats) {
                repoCategoria.save(c);
            }
            List<Gasto> gastos = persistencia.cargarGastos();
            for (Gasto g : gastos) {
                repoGasto.save(g);
            }
            if (repoCategoria.findAll().isEmpty()) {
                // Semillas mínimas
                servicioCategorias.crear("Alimentación", "#5FBF88");
                servicioCategorias.crear("Transporte", "#4C8BF5");
                servicioCategorias.crear("Ocio", "#F5A24C");
            }
        } catch (Exception e) {
            System.err.println("No se pudieron cargar datos iniciales: " + e.getMessage());
        }
    }

    private void registrarShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                persistencia.guardarCategorias(repoCategoria.findAll());
                persistencia.guardarGastos(repoGasto.findAll());
            } catch (Exception e) {
                System.err.println("No se pudieron guardar datos al cerrar: " + e.getMessage());
            }
        }));
    }

    // ===== API Login  =====
    public boolean loginUsuario(final String usuario, final String password) {
        return "admin".equalsIgnoreCase(usuario) && "admin".equals(password);
    }

    // ===== API Categorías =====
    public List<Categoria> listarCategorias() {
        return servicioCategorias.listarTodas();
    }

    // ===== API Gastos =====
    public Gasto registrarGasto(final BigDecimal cantidad,
                                final LocalDate fecha,
                                final UUID categoriaId,
                                final String nota) {
        Gasto g = new Gasto(cantidad, fecha, categoriaId, nota);
        return servicioGastos.registrar(g);
    }

    public List<Gasto> listarGastos() {
        return servicioGastos.listar();
    }
    
 // ===== API Categorías =====
    public Categoria crearCategoria(final String nombre, final String colorHex) {
        return servicioCategorias.crear(nombre, colorHex);
    }

    public void borrarCategoria(final java.util.UUID id) {
        servicioCategorias.borrar(id);
    }

    public void actualizarCategoria(final java.util.UUID id, final String nuevoNombre, final String nuevoColorHex) {
        servicioCategorias.actualizar(id, nuevoNombre, nuevoColorHex);
    }
    
    
 // ===== FILTROS Y TOTALES =====

    public List<Gasto> filtrarGastos(
            final java.time.LocalDate desde,
            final java.time.LocalDate hasta,
            final java.util.UUID categoriaId
    ) {
        return repoGasto.findAll()
                .stream()
                .filter(g -> desde == null || !g.getFecha().isBefore(desde))
                .filter(g -> hasta == null || !g.getFecha().isAfter(hasta))
                .filter(g -> categoriaId == null || categoriaId.equals(g.getCategoriaId()))
                .collect(java.util.stream.Collectors.toList());
    }

    public java.math.BigDecimal calcularTotal(
            final java.time.LocalDate desde,
            final java.time.LocalDate hasta,
            final java.util.UUID categoriaId
    ) {
        return filtrarGastos(desde, hasta, categoriaId)
                .stream()
                .map(g -> g.getCantidad())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    

    
    
}
