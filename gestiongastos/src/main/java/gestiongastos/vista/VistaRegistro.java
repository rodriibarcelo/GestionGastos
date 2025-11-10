package gestiongastos.vista;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class VistaRegistro {

    private final GridPane root;

    public VistaRegistro() {
        root = new GridPane();
        root.setHgap(8);
        root.setVgap(8);
        root.setPadding(new Insets(12));

        TextField tfNombre = new TextField();
        TextField tfApellidos = new TextField();
        TextField tfTelefono = new TextField();
        TextField tfEmail = new TextField();
        PasswordField pf1 = new PasswordField();
        PasswordField pf2 = new PasswordField();

        root.add(new Label("Nombre:"), 0, 0);
        root.add(tfNombre, 1, 0);
        root.add(new Label("Apellidos:"), 0, 1);
        root.add(tfApellidos, 1, 1);
        root.add(new Label("Teléfono:"), 0, 2);
        root.add(tfTelefono, 1, 2);
        root.add(new Label("Email:"), 0, 3);
        root.add(tfEmail, 1, 3);
        root.add(new Label("Contraseña:"), 0, 4);
        root.add(pf1, 1, 4);
        root.add(new Label("Confirmar:"), 0, 5);
        root.add(pf2, 1, 5);

        Button btnCancelar = new Button("Cancelar");
        Button btnAceptar = new Button("Aceptar");
        root.add(btnCancelar, 0, 6);
        root.add(btnAceptar, 1, 6);

        btnCancelar.setOnAction(e -> root.getScene().getWindow().hide());
        btnAceptar.setOnAction(e -> {
            if (tfNombre.getText().trim().isEmpty()
                || tfApellidos.getText().trim().isEmpty()
                || tfTelefono.getText().trim().isEmpty()
                || tfEmail.getText().trim().isEmpty()
                || pf1.getText().isEmpty()
                || pf2.getText().isEmpty()) {
                Utils.alertWarn("Completa todos los campos.");
                return;
            }
            if (!pf1.getText().equals(pf2.getText())) {
                Utils.alertError("Las contraseñas no coinciden.");
                return;
            }
            Utils.alertInfo("Registro (simulado) correcto.");
            root.getScene().getWindow().hide();
        });
    }

    public Parent getRoot() {
        return root;
    }
}
