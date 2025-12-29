package gestiongastos.cli;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Categoria;
import gestiongastos.dominio.Gasto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class GestionGastosCLI {

    private final Controlador ctrl = Controlador.getInstance();
    private final Scanner sc = new Scanner(System.in);

    public void start() {
        System.out.println("=== Gestión de Gastos (CLI) ===");

        while (true) {
            System.out.println("""
                1. Listar gastos
                2. Registrar gasto
                3. Modificar gasto
                4. Borrar gasto
                0. Salir
                """);

            System.out.print("> ");
            String op = sc.nextLine();

            switch (op) {
                case "1" -> listar();
                case "2" -> registrar();
                case "3" -> modificar();
                case "4" -> borrar();
                case "0" -> {
                    System.out.println("Saliendo...");
                    return;
                }
                default -> System.out.println("Opción no válida");
            }
        }
    }

    private void listar() {
        List<Gasto> gastos = ctrl.listarGastos();
        gastos.forEach(g ->
            System.out.println(g.getId() + " | " + g.getFecha() + " | " + g.getCantidad())
        );
    }

    private void registrar() {
        System.out.print("Cantidad: ");
        BigDecimal cantidad = new BigDecimal(sc.nextLine());

        System.out.print("Fecha (YYYY-MM-DD): ");
        LocalDate fecha = LocalDate.parse(sc.nextLine());

        Categoria cat = ctrl.listarCategorias().get(0); // simplificado
        System.out.print("Nota: ");
        String nota = sc.nextLine();

        ctrl.registrarGasto(cantidad, fecha, cat.getId(), nota);
        System.out.println("Gasto registrado.");
    }

    private void modificar() {
        listar();
        System.out.print("ID del gasto: ");
        UUID id = UUID.fromString(sc.nextLine());

        System.out.print("Nueva cantidad: ");
        BigDecimal cantidad = new BigDecimal(sc.nextLine());

        System.out.print("Nueva fecha (YYYY-MM-DD): ");
        LocalDate fecha = LocalDate.parse(sc.nextLine());

        Categoria cat = ctrl.listarCategorias().get(0);
        System.out.print("Nueva nota: ");
        String nota = sc.nextLine();

        ctrl.actualizarGasto(id, cantidad, fecha, cat.getId(), nota);
        System.out.println("Gasto actualizado.");
    }

    private void borrar() {
        listar();
        System.out.print("ID del gasto a borrar: ");
        UUID id = UUID.fromString(sc.nextLine());
        ctrl.borrarGasto(id);
        System.out.println("Gasto eliminado.");
    }
}
