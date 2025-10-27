package gestiongastos.controlador;

public class Controlador {
    private static final Controlador INSTANCE = new Controlador();
    private Controlador() {}

    public static Controlador getInstance() {
        return INSTANCE;
    }

    public boolean loginUsuario(String usuario, String password) {
        return "admin".equalsIgnoreCase(usuario) && "admin".equals(password);
    }
}
