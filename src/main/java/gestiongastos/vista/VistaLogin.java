package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class VistaLogin {

    private final BorderPane root;
    private Runnable onLoginOk;   

    public VistaLogin() {
        root = new BorderPane();
        root.setPadding(new Insets(16));
        root.setPrefSize(480, 300);

        // --- Título ---
        Label title = new Label("Inicio de sesión");
        title.setFont(Font.font(20));
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(8, 0, 12, 0));
        root.setTop(title);

        // --- Formulario ---
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(12));
        form.setAlignment(Pos.CENTER);

        Label lblUsuario = new Label("Usuario:");
        TextField tfUsuario = new TextField();
        tfUsuario.setPromptText("ejemplo@correo.com");

        Label lblPass = new Label("Contraseña:");
        PasswordField pfPass = new PasswordField();

        form.add(lblUsuario, 0, 0);
        form.add(tfUsuario, 1, 0);
        form.add(lblPass,    0, 1);
        form.add(pfPass,     1, 1);

        StackPane center = new StackPane(form);
        center.setPadding(new Insets(8, 16, 16, 16));
        root.setCenter(center);

        // --- Botones ---
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setCancelButton(true);

        Button btnRegistrar = new Button("Registrarse");
        Button btnAceptar = new Button("Entrar");
        btnAceptar.setDefaultButton(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottom = new HBox(10, btnRegistrar, spacer, btnCancelar, btnAceptar);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(10, 8, 10, 8));
        root.setBottom(bottom);

        // --- Acciones ---
        btnCancelar.setOnAction(e -> root.getScene().getWindow().hide());

        // Registro
        btnRegistrar.setOnAction(e -> {
            VistaRegistro v = new VistaRegistro();
            Utils.openDialog(root, "Registro", v.getRoot(), 650, 450);
        });

        // Login
        btnAceptar.setOnAction(e -> {
            String usuario = tfUsuario.getText().trim();
            String password = pfPass.getText();

            if (usuario.isEmpty() || password.isEmpty()) {
                Utils.alertWarn("Introduce usuario y contraseña.");
                return;
            }

            boolean ok;
            try {
                ok = Controlador.getInstance().loginUsuario(usuario, password);
            } catch (Exception ex) {
                Utils.alertError("Error al iniciar sesión: " + ex.getMessage());
                return;
            }

            if (ok) {
                if (onLoginOk != null) {
                    onLoginOk.run();
                }
                root.getScene().getWindow().hide();
            } else {
                Utils.alertError("Usuario o contraseña incorrectos.");
            }
        });
    }

    public Parent getRoot() {
        return root;
    }

    public void setOnLoginOk(final Runnable r) {
        this.onLoginOk = r;
    }
}
