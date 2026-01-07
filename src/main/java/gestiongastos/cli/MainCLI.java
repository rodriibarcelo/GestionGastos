package gestiongastos.cli;

import gestiongastos.controlador.Controlador;

import java.util.Scanner;

public class MainCLI {

    public static void main(String[] args) {

        Controlador ctrl = Controlador.getInstance();
        Scanner sc = new Scanner(System.in);

        login(ctrl, sc);

        // Delegamos el menú a GestionGastosCLI
        new GestionGastosCLI(ctrl, sc).start();
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

        System.out.println("Login correcto.\n");
    }
}
