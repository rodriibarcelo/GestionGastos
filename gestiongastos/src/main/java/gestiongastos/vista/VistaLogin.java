package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class VistaLogin {

    private final BorderPane root;
    private Runnable onLoginOk;

    public VistaLogin() {
        this.root = new BorderPane();
        this.root.setPadding(new Insets(12));

        Label title = new Label("Login");
        title.setFont(Font.font(22));
        BorderPane.setAlignment(title, Pos.CENTER);
        root.setTop(title);

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(12));
        root.setCenter(grid);

        Label lUser = new Label("Usuario:");
        TextField tfUser = new TextField();
        Label lPass = new Label("Contraseña:");
        PasswordField pfPass = new PasswordField();

        grid.add(lUser, 0, 0);
        grid.add(tfUser, 1, 0);
        grid.add(lPass, 0, 1);
        grid.add(pfPass, 1, 1);

        Button btnLogin = new Button("Aceptar");
        Button btnCancelar = new Button("Cancelar");
        Button btnRegistrar = new Button("Registrar");

        ToolBar bar = new ToolBar(btnRegistrar, new Separator(), btnCancelar, btnLogin);
        root.setBottom(bar);

        btnCancelar.setOnAction(e -> getStage().close());

        btnRegistrar.setOnAction(e -> {
            VistaRegistro reg = new VistaRegistro();
            Utils.openDialog(root, "Registro de Usuario", reg.getRoot(), 640, 520);
        });

        btnLogin.setDefaultButton(true);
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

                    if (onLoginOk != null) {
                        onLoginOk.run();
                    }
                    getStage().close();
                } else {
                    Utils.alertError("Usuario o contraseña incorrectos.");
                }
            } catch (Exception ex) {
                Utils.alertError("Error al iniciar sesión: " + ex.getMessage());
            }
        });
    }

    public Parent getRoot() {
        return root;
    }

    public void setOnLoginOk(final Runnable onLoginOk) {
        this.onLoginOk = onLoginOk;
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }
}
