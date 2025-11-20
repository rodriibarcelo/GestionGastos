package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class VistaLogin {

    private final BorderPane root;
    private Runnable onLoginOk;

    public VistaLogin() {
        this.root = new BorderPane();
        this.root.setPadding(new Insets(16));
        this.root.setPrefSize(520, 320); // tamaño cómodo inicial

        // --- Título ---
        Label title = new Label("Login");
        title.setFont(Font.font(22));
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(8, 0, 12, 0));
        root.setTop(title);

        // --- Formulario centrado ---
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));
        grid.setAlignment(Pos.CENTER); // ¡clave para centrar!

        // Columnas: labels a la derecha; campos crecen
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHalignment(javafx.geometry.HPos.RIGHT);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c1, c2);

        Label lUser = new Label("Usuario:");
        TextField tfUser = new TextField();
        tfUser.setPromptText("Tu usuario");
        tfUser.setPrefColumnCount(20);

        Label lPass = new Label("Contraseña:");
        PasswordField pfPass = new PasswordField();
        pfPass.setPromptText("••••••••");

        grid.add(lUser, 0, 0);
        grid.add(tfUser, 1, 0);
        grid.add(lPass, 0, 1);
        grid.add(pfPass, 1, 1);

        // Envolver el grid para que quede realmente centrado en la ventana
        StackPane center = new StackPane(grid);
        center.setPadding(new Insets(8, 16, 16, 16));
        StackPane.setAlignment(grid, Pos.CENTER);
        root.setCenter(center);

        // --- Botonera alineada a la derecha ---
        Button btnRegistrar = new Button("Registrar");
        Button btnCancelar  = new Button("Cancelar");
        Button btnLogin     = new Button("Aceptar");
        btnLogin.setDefaultButton(true);
        btnCancelar.setCancelButton(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottom = new HBox(10, btnRegistrar, spacer, btnCancelar, btnLogin);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(10, 8, 10, 8));
        root.setBottom(bottom);

        // --- Acciones ---
        btnCancelar.setOnAction(e -> getStage().close());

        btnRegistrar.setOnAction(e -> {
            VistaRegistro reg = new VistaRegistro();
            Utils.openDialog(root, "Registro de Usuario", reg.getRoot(), 640, 520);
        });

        btnLogin.setOnAction(e -> {
            String u = tfUser.getText().trim();
            String p = pfPass.getText();
            if (u.isEmpty() || p.isEmpty()) {
                Utils.alertWarn("Introduce usuario y contraseña.");
                return;
            }
            try {
                boolean ok = Controlador.getInstance().loginUsuario(u, p);
                if (ok) {
                    if (onLoginOk != null) onLoginOk.run();
                    getStage().close();
                } else {
                    Utils.alertError("Usuario o contraseña incorrectos.");
                }
            } catch (Exception ex) {
                Utils.alertError("Error al iniciar sesión: " + ex.getMessage());
            }
        });
    }

    public Parent getRoot() { return root; }

    public void setOnLoginOk(final Runnable onLoginOk) { this.onLoginOk = onLoginOk; }

    private Stage getStage() { return (Stage) root.getScene().getWindow(); }
}
