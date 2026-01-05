package gestiongastos.controlador;

import gestiongastos.alertas.AlertaStrategy;
import gestiongastos.alertas.GestorAlertas;
import gestiongastos.dominio.Categoria;
import gestiongastos.dominio.CuentaCompartida;
import gestiongastos.dominio.Gasto;
import gestiongastos.dominio.Notificacion;
import gestiongastos.dominio.Usuario;

import gestiongastos.persistencia.UsuarioRepository;
import gestiongastos.persistencia.UsuarioRepositoryJson;
import gestiongastos.persistencia.CategoriaRepositoryJson;
import gestiongastos.persistencia.CuentaCompartidaRepositoryJson;
import gestiongastos.persistencia.GastoRepositoryJson;
import gestiongastos.persistencia.NotificacionRepository;
import gestiongastos.persistencia.NotificacionRepositoryJson;
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

    private final UsuarioRepository repoUsuarios = UsuarioRepositoryJson.getInstance();
    
    private final NotificacionRepository repoNotificaciones =
            NotificacionRepositoryJson.getInstance();


    private final ServicioGastos servicioGastos =
            new ServicioGastos(GastoRepositoryJson.getInstance());

    private final ServicioCategorias servicioCategorias =
            new ServicioCategorias(CategoriaRepositoryJson.getInstance());

    private final ServicioCuentasCompartidas servicioCuentas =
            new ServicioCuentasCompartidas(new CuentaCompartidaRepositoryJson());

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
    // CUENTAS COMPARTIDAS (utilidades)
    // ============================================================

    private List<UUID> cuentasCompartidasDelUsuarioActual() {

        if (usuarioActual == null) {
            throw new IllegalStateException("No hay usuario autenticado.");
        }

        return servicioCuentas.listar().stream()
                .filter(c -> c.getParticipantes() != null)
                .filter(c -> c.getParticipantes().stream()
                        .anyMatch(p -> usuarioActual.getId().equals(p.getUsuarioId())))
                .map(CuentaCompartida::getId)
                .toList();
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

    public List<String> registrarGasto(final BigDecimal cantidad,
            final LocalDate fecha,
            final UUID categoriaId,
            final String nota) {

    	if (usuarioActual == null) {
    		throw new IllegalStateException("No hay usuario autenticado.");
    	}

    	Gasto g = new Gasto(cantidad, fecha, categoriaId, nota);
    	g.setUsuarioId(usuarioActual.getId());

    	servicioGastos.registrar(g);

    	List<Gasto> historico = servicioGastos.listar().stream()
    			.filter(x -> x.getUsuarioId() != null)
    			.filter(x -> x.getUsuarioId().equals(usuarioActual.getId()))
    			.collect(java.util.stream.Collectors.toList());

    	List<String> mensajes = gestorAlertas.evaluar(g, historico);

    	// Persistir notificaciones en historial
    	for (String msg : mensajes) {
    		repoNotificaciones.save(new gestiongastos.dominio.Notificacion(usuarioActual.getId(), msg));
    	}

    	return mensajes;
    }


    public List<String> registrarGastoCompartido(final BigDecimal cantidad, final LocalDate fecha, final UUID categoriaId,
    		final UUID cuentaCompartidaId, final String nota) {

    // Compatibilidad: si no se indica pagador, asumimos que paga el usuario autenticado
    if (usuarioActual == null) {
        throw new IllegalStateException("No hay usuario autenticado.");
    }

    return registrarGastoCompartido(cantidad, fecha, categoriaId, cuentaCompartidaId, usuarioActual.getId(), nota);
}


//permite indicar explícitamente quién ha pagado dentro de la cuenta
public List<String> registrarGastoCompartido(final BigDecimal cantidad, final LocalDate fecha,final UUID categoriaId,
            final UUID cuentaCompartidaId, final UUID usuarioIdPagador, final String nota) {

    if (usuarioActual == null) {
        throw new IllegalStateException("No hay usuario autenticado.");
    }

    Gasto g = new Gasto(cantidad, fecha, categoriaId, nota);

    //en gasto compartido, el usuarioId representa QUIÉN HA PAGADO
    g.setUsuarioId(usuarioIdPagador);
    g.setCuentaCompartidaId(cuentaCompartidaId);

    servicioGastos.registrar(g);

    // Actualizar saldos en la cuenta compartida
    servicioCuentas.registrarGastoEnCuenta(cuentaCompartidaId, usuarioIdPagador, cantidad);

    // Alertas y notificaciones se evalúan para el usuario autenticado(el que esta usando la app)
    List<Gasto> hist = servicioGastos.listar().stream()
            .filter(x -> x.getUsuarioId() != null)
            .filter(x -> x.getUsuarioId().equals(usuarioActual.getId()))
            .collect(java.util.stream.Collectors.toList());

    List<String> mensajes = gestorAlertas.evaluar(g, hist);

    for (String msg : mensajes) {
        repoNotificaciones.save(new gestiongastos.dominio.Notificacion(usuarioActual.getId(), msg));
    }

    return mensajes;
}


    public List<Gasto> listarGastos() {

        if (usuarioActual == null) {
            throw new IllegalStateException("No hay usuario autenticado.");
        }

        // 1) Gastos personales: solo los del usuario autenticado
        // 2) Gastos compartidos: todos los de las cuentas en las que participa
        List<UUID> cuentas = cuentasCompartidasDelUsuarioActual();

        return servicioGastos.listar().stream()
                .filter(g -> {
                    if (g.getCuentaCompartidaId() == null) {
                        return g.getUsuarioId() != null && g.getUsuarioId().equals(usuarioActual.getId());
                    }
                    return cuentas.contains(g.getCuentaCompartidaId());
                })
                .toList();
    }


    public void actualizarGasto(UUID id, BigDecimal nuevaCantidad, LocalDate nuevaFecha, UUID nuevaCategoriaId, String nuevaNota) {

        // Necesitamos saber si era un gasto compartido para recalcular saldos
        UUID cuentaIdAntes = servicioGastos.listar().stream()
                .filter(g -> g.getId().equals(id))
                .findFirst()
                .map(Gasto::getCuentaCompartidaId)
                .orElse(null);

        servicioGastos.actualizar(id, nuevaCantidad, nuevaFecha, nuevaCategoriaId, nuevaNota);

        // Si es compartido, recalculamos saldos desde cero 
        if (cuentaIdAntes != null) {
            var gastosCuenta = servicioGastos.listar().stream()
                    .filter(g -> cuentaIdAntes.equals(g.getCuentaCompartidaId()))
                    .toList();
            servicioCuentas.recalcularSaldos(cuentaIdAntes, gastosCuenta);
        }
    }

    public void borrarGasto(UUID id) {

        UUID cuentaIdAntes = servicioGastos.listar().stream()
                .filter(g -> g.getId().equals(id))
                .findFirst()
                .map(Gasto::getCuentaCompartidaId)
                .orElse(null);

        servicioGastos.borrar(id);

        if (cuentaIdAntes != null) {
            var gastosCuenta = servicioGastos.listar().stream()
                    .filter(g -> cuentaIdAntes.equals(g.getCuentaCompartidaId()))
                    .toList();
            servicioCuentas.recalcularSaldos(cuentaIdAntes, gastosCuenta);
        }
    }

    // ============================================================
    // FILTROS
    // ============================================================

    public List<Gasto> filtrarGastos(LocalDate d1, LocalDate d2, UUID catId) {

        if (usuarioActual == null) {
            throw new IllegalStateException("No hay usuario autenticado.");
        }

        List<UUID> cuentas = cuentasCompartidasDelUsuarioActual();

        return servicioGastos.filtrar(d1, d2, catId).stream()
                .filter(g -> {
                    if (g.getCuentaCompartidaId() == null) {
                        return g.getUsuarioId() != null && g.getUsuarioId().equals(usuarioActual.getId());
                    }
                    return cuentas.contains(g.getCuentaCompartidaId());
                })
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
    
    //Notificaciones
    public List<Notificacion> listarNotificaciones() {
        if (usuarioActual == null) {
            throw new IllegalStateException("No hay usuario autenticado.");
        }
        return repoNotificaciones.findByUsuario(usuarioActual.getId());
    }

}
