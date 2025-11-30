package gestiongastos.controlador;

import gestiongastos.alertas.AlertaStrategy;
import gestiongastos.alertas.GestorAlertas;
import gestiongastos.dominio.Categoria;
import gestiongastos.dominio.Gasto;
import gestiongastos.dominio.Usuario;

import gestiongastos.persistencia.UsuarioRepository;
import gestiongastos.persistencia.UsuarioRepositoryJson;
import gestiongastos.persistencia.CategoriaRepositoryJson;
import gestiongastos.persistencia.GastoRepositoryJson;

import gestiongastos.servicio.ServicioCategorias;
import gestiongastos.servicio.ServicioCuentasCompartidas;
import gestiongastos.servicio.ServicioGastos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Controlador {

    private static final Controlador INSTANCE = new Controlador();

    // (no hay ServicioUsuarios)
    private final UsuarioRepository repoUsuarios = UsuarioRepositoryJson.getInstance();

    private final ServicioGastos servicioGastos =
            new ServicioGastos(GastoRepositoryJson.getInstance());

    private final ServicioCategorias servicioCategorias =
            new ServicioCategorias(CategoriaRepositoryJson.getInstance());

    private final ServicioCuentasCompartidas servicioCuentas =
            new ServicioCuentasCompartidas();
    
    private Usuario usuarioActual;


    private final GestorAlertas gestorAlertas = new GestorAlertas();

    private Controlador() {
        cargarDatosIniciales();
    }

    public static Controlador getInstance() {
        return INSTANCE;
    }

    public ServicioCuentasCompartidas getServicioCuentas() {
        return servicioCuentas;
    }

    // ============================================================
    // INICIALIZACIÓN
    // ============================================================

    private void cargarDatosIniciales() {

        if (servicioCategorias.listarTodas().isEmpty()) {
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
        boolean ok = u.isPresent() && u.get().getPassword().equals(password);
        if (ok) usuarioActual = u.get();
        return ok;
    }

    
    public List<Usuario> listarUsuarios() {
        return repoUsuarios.findAll();
    }
    
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }


    // ============================================================
    // CATEGORÍAS
    // ============================================================

    public List<Categoria> listarCategorias() {
        return servicioCategorias.listarTodas();
    }

    public Categoria crearCategoria(String nombre, String colorHex) {
        return servicioCategorias.crear(nombre, colorHex);
    }

    public void actualizarCategoria(UUID id, String nuevoNombre, String nuevoColorHex) {
        servicioCategorias.actualizar(id, nuevoNombre, nuevoColorHex);
    }

    public void borrarCategoria(UUID id) {
        servicioCategorias.borrar(id);
    }

    // ============================================================
    // GASTOS
    // ============================================================

    public List<String> registrarGasto(BigDecimal cantidad, LocalDate fecha, UUID categoriaId, String nota) {

        Gasto g = new Gasto(cantidad, fecha, categoriaId, nota);
        g.setUsuarioId(usuarioActual.getId());
        servicioGastos.registrar(g);

        return gestorAlertas.evaluar(g, servicioGastos.listar());
    }

    public List<String> registrarGastoCompartido(BigDecimal cantidad, LocalDate fecha, UUID categoriaId, UUID cuentaCompartidaId, String nota) {

        Gasto g = new Gasto(cantidad, fecha, categoriaId, nota);
        g.setCuentaCompartidaId(cuentaCompartidaId);

        servicioGastos.registrar(g);

        return gestorAlertas.evaluar(g, servicioGastos.listar());
    }

    public List<Gasto> listarGastos() {
        return servicioGastos.listar().stream()
                .filter(g -> g.getUsuarioId().equals(usuarioActual.getId()))
                .toList();
    }


    public void actualizarGasto(UUID id, BigDecimal nuevaCantidad, LocalDate nuevaFecha, UUID nuevaCategoriaId, String nuevaNota) {
        servicioGastos.actualizar(id, nuevaCantidad, nuevaFecha, nuevaCategoriaId, nuevaNota);
    }

    public void borrarGasto(UUID id) {
        servicioGastos.borrar(id);
    }

    // ============================================================
    // FILTROS
    // ============================================================

    public List<Gasto> filtrarGastos(LocalDate d1, LocalDate d2, UUID catId) {
        return servicioGastos.filtrar(d1, d2, catId).stream()
                .filter(g -> g.getUsuarioId().equals(usuarioActual.getId()))
                .toList();
    }


    public BigDecimal calcularTotal(LocalDate desde, LocalDate hasta, UUID categoriaId) {
        return servicioGastos.total(desde, hasta, categoriaId);
    }

    // ============================================================
    // ALERTAS
    // ============================================================

    public void configurarAlertas(List<AlertaStrategy> estrategias) {
        gestorAlertas.setEstrategias(estrategias);
    }
}
