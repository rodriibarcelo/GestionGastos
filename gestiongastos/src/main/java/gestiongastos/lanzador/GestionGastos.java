package gestiongastos.lanzador;

import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;
import gestiongastos.vista.LoginWindow;

public class GestionGastos {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();
                UIManager.put("Component.arc", 24);
            } catch (Exception ex) {
                System.err.println("Failed to initialize FlatLaf: " + ex.getMessage());
            }
            LoginWindow login = new LoginWindow();
            login.setLocationRelativeTo(null);
            login.setVisible(true);
        });
    }
}
