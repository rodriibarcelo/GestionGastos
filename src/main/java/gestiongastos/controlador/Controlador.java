package gestiongastos.controlador;

import gestiongastos.alertas.AlertaStrategy;
import gestiongastos.alertas.GestorAlertas;
import gestiongastos.dominio.Categoria;
import gestiongastos.dominio.Gasto;
import gestiongastos.dominio.Usuario;

import gestiongastos.persistencia.CategoriaRepository;
import gestiongastos.persistencia.CategoriaRepositoryJson;
import gestiongastos.persistencia.GastoRepository;
import gestiongastos.persistencia.GastoRepositoryJson;
import gestiongastos.persistencia.UsuarioRepository;
import gestiongastos.persistencia.UsuarioRepositoryJson;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Controlador {

    private static final Controlador INSTANCE = new Controlador();

    private final GastoRepository repoGastos = GastoRepositoryJson.getInstance();
    private final CategoriaRepository repoCategorias = CategoriaRepositoryJson.getInstance();
    private final UsuarioRepository repoUsuarios = UsuarioRepositoryJson.getInstance();

    private final GestorAlertas gestorAlertas = new GestorAlertas();


    private Controlador() {
        cargarDatosIniciales();
    }

    public static Controlador getInstance() {
        return INSTANCE;
    }

    // ============================================================
    // INICIALIZACIÓN
    // ============================================================

    private void cargarDatosIniciales() {

        if (repoCategorias.findAll().isEmpty()) {
            crearCategoria("Alimentación", "#5FBF88");
            crearCategoria("Transporte", "#4C8BF5");
            crearCategoria("Ocio", "#F5A24C");
        }

        if (repoUsuarios.findByNombreUsuario("admin").isEmpty()) {
            registrarUsuario("admin", "admin");
        }
    }

    // ============================================================
    // USUARIOS
    // ============================================================

    public boolean registrarUsuario(String username, String password) {
        if (username == null || password == null) return false;

        if (repoUsuarios.findByNombreUsuario(username).isPresent()) {
            return false;
        }

        Usuario u = new Usuario();
        u.setId(UUID.randomUUID());
        u.setNombreUsuario(username);
        u.setPassword(password);

        repoUsuarios.save(u);
        return true;
    }

    public boolean loginUsuario(String username, String password) {
        Optional<Usuario> u = repoUsuarios.findByNombreUsuario(username);
        return u.isPresent() && u.get().getPassword().equals(password);
    }

    // ============================================================
    // CATEGORÍAS
    // ============================================================

    public List<Categoria> listarCategorias() {
        return repoCategorias.findAll();
    }

    public Categoria crearCategoria(String nombre, String colorHex) {
        Categoria c = new Categoria();
        c.setId(UUID.randomUUID());
        c.setNombre(nombre);
        c.setColorHex(colorHex);

        repoCategorias.save(c);
        return c;
    }

    public void actualizarCategoria(UUID id, String nuevoNombre, String nuevoColorHex) {
        repoCategorias.findById(id).ifPresent(c -> {
            c.setNombre(nuevoNombre);
            c.setColorHex(nuevoColorHex);
            repoCategorias.update(c);
        });
    }

    public void borrarCategoria(UUID id) {
        repoCategorias.deleteById(id);
    }

    // ============================================================
    // GASTOS
    // ============================================================

    public List<String> registrarGasto(BigDecimal cantidad, LocalDate fecha, UUID categoriaId, String nota) {

        // 1. Crear gasto
        Gasto g = new Gasto(cantidad, fecha, categoriaId, nota);

        // 2. Guardar
        repoGastos.save(g);

        // 3. Histórico
        List<Gasto> historico = repoGastos.findAll();

        // 4. Alertas
        return gestorAlertas.evaluar(g, historico);
    }

    public List<Gasto> listarGastos() {
        return repoGastos.findAll();
    }

    public void actualizarGasto(UUID id,
                                BigDecimal nuevaCantidad,
                                LocalDate nuevaFecha,
                                UUID nuevaCategoriaId,
                                String nuevaNota) {

        repoGastos.findById(id).ifPresent(g -> {
            g.setCantidad(nuevaCantidad);
            g.setFecha(nuevaFecha);
            g.setCategoriaId(nuevaCategoriaId);
            g.setNota(nuevaNota == null ? "" : nuevaNota.trim());
            repoGastos.update(g);
        });
    }

    public void borrarGasto(UUID id) {
        repoGastos.deleteById(id);
    }

    // ============================================================
    // FILTROS
    // ============================================================

    public List<Gasto> filtrarGastos(LocalDate desde,
                                     LocalDate hasta,
                                     UUID categoriaId) {

        return repoGastos.findAll().stream()
                .filter(g -> desde == null || !g.getFecha().isBefore(desde))
                .filter(g -> hasta == null || !g.getFecha().isAfter(hasta))
                .filter(g -> categoriaId == null || categoriaId.equals(g.getCategoriaId()))
                .collect(Collectors.toList());
    }

    public BigDecimal calcularTotal(LocalDate desde,
                                    LocalDate hasta,
                                    UUID categoriaId) {

        return filtrarGastos(desde, hasta, categoriaId)
                .stream()
                .map(Gasto::getCantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ============================================================
    // ALERTAS
    // ============================================================

    public void configurarAlertas(List<AlertaStrategy> estrategias) {
        gestorAlertas.setEstrategias(estrategias);
    }
}
