package gestiongastos.lanzador;

import gestiongastos.controlador.Controlador;
import gestiongastos.vista.VistaLogin;
import gestiongastos.vista.VistaMain;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GestionGastosApp extends Application {

    @Override
    public void start(final Stage primaryStage) {
        // Inicializa datos / singleton
        Controlador.getInstance();

        VistaLogin login = new VistaLogin();
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(login.getRoot(), 420, 320));
        primaryStage.show();

        login.setOnLoginOk(() -> {
            try {
                VistaMain main = new VistaMain();
                Stage stage = new Stage();
                stage.setTitle("Gestión de Gastos");
                stage.setScene(new Scene(main.getRoot(), 960, 620));
                stage.show();
            } finally {
                primaryStage.close();
            }
        });
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
