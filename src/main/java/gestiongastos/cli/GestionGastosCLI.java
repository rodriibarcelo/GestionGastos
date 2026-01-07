package gestiongastos.cli;

import gestiongastos.alertas.AlertaImporteMayor;
import gestiongastos.alertas.AlertaStrategy;
import gestiongastos.alertas.AlertaTotalDiario;
import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Categoria;
import gestiongastos.dominio.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class GestionGastosCLI {

    private final Controlador ctrl;
    private final Scanner sc;

    public GestionGastosCLI(Controlador ctrl, Scanner sc) {
        this.ctrl = ctrl;
        this.sc = sc;
    }

    public void start() {
        System.out.println("=== Gestión de Gastos (CLI) ===");

        while (true) {
            System.out.println("""
                1. Listar gastos
                2. Registrar gasto
                3. Modificar gasto
                4. Borrar gasto
                5. Configurar alertas (para generar notificaciones)
                6. Ver notificaciones
                0. Salir
                """);

            System.out.print("> ");
            String op = sc.nextLine().trim();

            try {
                switch (op) {
                    case "1" -> listar();
                    case "2" -> registrar();
                    case "3" -> modificar();
                    case "4" -> borrar();
                    case "5" -> configurarAlertas();
                    case "6" -> verNotificaciones();
                    case "0" -> {
                        System.out.println("Saliendo...");
                        return;
                    }
                    default -> System.out.println("Opción no válida");
                }
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
                System.out.println();
            }
        }
    }

    private void listar() {
        List<Gasto> gastos = ctrl.listarGastos();

        if (gastos.isEmpty()) {
            System.out.println("No hay gastos.");
            return;
        }

        System.out.println("\nID | FECHA | CANTIDAD | CATEGORÍA_ID | NOTA");
        for (Gasto g : gastos) {
            System.out.println(
                g.getId() + " | " +
                g.getFecha() + " | " +
                g.getCantidad() + " | " +
                g.getCategoriaId() + " | " +
                (g.getNota() == null ? "" : g.getNota())
            );
        }
        System.out.println();
    }

    private void registrar() {
        BigDecimal cantidad = leerBigDecimal("Cantidad: ");
        LocalDate fecha = leerFecha("Fecha (YYYY-MM-DD): ");
        UUID categoriaId = pedirCategoriaId();

        System.out.print("Nota: ");
        String nota = sc.nextLine();

        // Este método devuelve mensajes y además guarda notificaciones si hay alertas
        List<String> mensajes = ctrl.registrarGasto(cantidad, fecha, categoriaId, nota);

        System.out.println("Gasto registrado.");
        if (mensajes.isEmpty()) {
            System.out.println("(Sin alertas)");
        } else {
            for (String m : mensajes) System.out.println("ALERTA: " + m);
        }
        System.out.println();
    }

    private void modificar() {
        listar();

        System.out.print("ID del gasto: ");
        UUID id = UUID.fromString(sc.nextLine().trim());

        BigDecimal cantidad = leerBigDecimal("Nueva cantidad: ");
        LocalDate fecha = leerFecha("Nueva fecha (YYYY-MM-DD): ");
        UUID categoriaId = pedirCategoriaId();

        System.out.print("Nueva nota: ");
        String nota = sc.nextLine();

        ctrl.actualizarGasto(id, cantidad, fecha, categoriaId, nota);
        System.out.println("Gasto actualizado.\n");
    }

    private void borrar() {
        listar();

        System.out.print("ID del gasto a borrar: ");
        UUID id = UUID.fromString(sc.nextLine().trim());

        ctrl.borrarGasto(id);
        System.out.println("Gasto eliminado.\n");
    }

    private void configurarAlertas() {
        System.out.println("\n=== CONFIGURAR ALERTAS ===");
        System.out.println("Nota: si no configuras alertas, no se generarán notificaciones.");

        List<AlertaStrategy> estrategias = new ArrayList<>();

        System.out.print("¿Avisar si un gasto supera un importe? (s/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("s")) {
            BigDecimal limite = leerBigDecimal("  Límite (ej. 100): ");
            estrategias.add(new AlertaImporteMayor(limite));
        }

        System.out.print("¿Avisar si el total diario supera un importe? (s/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("s")) {
            BigDecimal limite = leerBigDecimal("  Límite (ej. 200): ");
            estrategias.add(new AlertaTotalDiario(limite));
        }

        ctrl.configurarAlertas(estrategias);
        System.out.println("Alertas configuradas.\n");
    }

    private void verNotificaciones() {
        System.out.println("\n=== HISTORIAL DE NOTIFICACIONES ===");

        var notifs = ctrl.listarNotificaciones();

        if (notifs.isEmpty()) {
            System.out.println("No hay notificaciones.\n");
            return;
        }

        for (int i = 0; i < notifs.size(); i++) {
            var n = notifs.get(i);
            System.out.println((i + 1) + ". [" + n.getFecha() + "] " + n.getMensaje());
        }
        System.out.println();
    }

    // ----------------- helpers -----------------

    private UUID pedirCategoriaId() {
        List<Categoria> cats = ctrl.listarCategorias();
        if (cats.isEmpty()) throw new IllegalStateException("No hay categorías disponibles.");

        System.out.println("\nCategorías:");
        for (int i = 0; i < cats.size(); i++) {
            System.out.println((i + 1) + ". " + cats.get(i).getNombre() + " (" + cats.get(i).getId() + ")");
        }

        System.out.print("Selecciona categoría (número o UUID; Enter = 1): ");
        String in = sc.nextLine().trim();

        if (in.isBlank()) return cats.get(0).getId();

        // Permitir número
        try {
            int idx = Integer.parseInt(in);
            if (idx < 1 || idx > cats.size()) throw new IllegalArgumentException("Número fuera de rango.");
            return cats.get(idx - 1).getId();
        } catch (NumberFormatException ignore) {
            // Permitir UUID
            return UUID.fromString(in);
        }
    }

    private BigDecimal leerBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String in = sc.nextLine().trim().replace(",", ".");
            try {
                return new BigDecimal(in);
            } catch (Exception e) {
                System.out.println("Número inválido. Ejemplo válido: 12.50");
            }
        }
    }

    private LocalDate leerFecha(String prompt) {
        while (true) {
            System.out.print(prompt);
            String in = sc.nextLine().trim();
            try {
                return LocalDate.parse(in);
            } catch (Exception e) {
                System.out.println("Fecha inválida. Formato: YYYY-MM-DD");
            }
        }
    }
}
