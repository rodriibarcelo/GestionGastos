package gestiongastos.cli;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class MainCLI {

    public static void main(String[] args) {

        Controlador ctrl = Controlador.getInstance();
        Scanner sc = new Scanner(System.in);
        
        login(ctrl,sc);

        boolean salir = false;

        while (!salir) {
            System.out.println("\n=== GESTIÓN DE GASTOS (CLI) ===");
            System.out.println("1. Listar gastos");
            System.out.println("2. Registrar gasto");
            System.out.println("3. Modificar gasto");
            System.out.println("4. Borrar gasto");
            System.out.println("5. Ver notificaciones");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            String opcion = sc.nextLine();

            try {
                switch (opcion) {
                    case "1" -> listarGastos(ctrl);
                    case "2" -> registrarGasto(ctrl, sc);
                    case "3" -> modificarGasto(ctrl, sc);
                    case "4" -> borrarGasto(ctrl, sc);
                    case "5" -> verNotificaciones(ctrl);
                    case "0" -> salir = true;
                    default -> System.out.println("Opción no válida");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }

        sc.close();
        System.out.println("Saliendo...");
    }
    
    private static void login(final Controlador ctrl, final Scanner sc) {

        boolean ok = false;

        while (!ok) {
            System.out.println("\n=== LOGIN (CLI) ===");
            System.out.print("Usuario: ");
            String user = sc.nextLine().trim();

            System.out.print("Contraseña: ");
            String pass = sc.nextLine();

            ok = ctrl.loginUsuario(user, pass);

            if (!ok) {
                System.out.println("Credenciales incorrectas. Inténtalo de nuevo.");
            }
        }
    }


    private static void listarGastos(Controlador ctrl) {
        List<Gasto> gastos = ctrl.listarGastos();
        if (gastos.isEmpty()) {
            System.out.println("No hay gastos.");
            return;
        }

        gastos.forEach(g ->
            System.out.println(
                g.getId() + " | " +
                g.getFecha() + " | " +
                g.getCantidad() + " | " +
                g.getNota()
            )
        );
    }

    private static void registrarGasto(Controlador ctrl, Scanner sc) {

        System.out.print("Cantidad: ");
        BigDecimal cantidad = new BigDecimal(sc.nextLine());

        System.out.print("Fecha (YYYY-MM-DD): ");
        LocalDate fecha = LocalDate.parse(sc.nextLine());

        System.out.print("ID categoría: ");
        UUID categoriaId = UUID.fromString(sc.nextLine());

        System.out.print("Nota: ");
        String nota = sc.nextLine();

        var mensajes = ctrl.registrarGasto(cantidad, fecha, categoriaId, nota);

        System.out.println("Gasto registrado.");
        mensajes.forEach(m -> System.out.println("ALERTA: " + m));
    }

    private static void modificarGasto(Controlador ctrl, Scanner sc) {

        System.out.print("ID del gasto: ");
        UUID id = UUID.fromString(sc.nextLine());

        System.out.print("Nueva cantidad: ");
        BigDecimal cantidad = new BigDecimal(sc.nextLine());

        System.out.print("Nueva fecha (YYYY-MM-DD): ");
        LocalDate fecha = LocalDate.parse(sc.nextLine());

        System.out.print("Nueva categoría ID: ");
        UUID categoriaId = UUID.fromString(sc.nextLine());

        System.out.print("Nueva nota: ");
        String nota = sc.nextLine();

        ctrl.actualizarGasto(id, cantidad, fecha, categoriaId, nota);
        System.out.println("Gasto actualizado.");
    }

    private static void borrarGasto(Controlador ctrl, Scanner sc) {

        System.out.print("ID del gasto: ");
        UUID id = UUID.fromString(sc.nextLine());

        ctrl.borrarGasto(id);
        System.out.println("Gasto eliminado.");
    }
    
    private static void verNotificaciones(Controlador ctrl) {

        try {
            System.out.println();
            System.out.println("=== HISTORIAL DE NOTIFICACIONES ===");

            var notifs = ctrl.listarNotificaciones();

            if (notifs.isEmpty()) {
                System.out.println("No hay notificaciones.");
                System.out.println();
                return;
            }

            for (int i = 0; i < notifs.size(); i++) {
                var n = notifs.get(i);
                System.out.println((i + 1) + ". " + n.getMensaje());
            }

            System.out.println();

        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
            System.out.println();
        }
    }


}
