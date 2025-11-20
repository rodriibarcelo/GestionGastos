package gestiongastos.vista;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class VistaRegistro {

    private final BorderPane root;

    public VistaRegistro() {
        root = new BorderPane();
        root.setPadding(new Insets(16));
        root.setPrefSize(640, 420);

        // --- Título ---
        Label title = new Label("Registro de Usuario");
        title.setFont(Font.font(20));
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(8, 0, 12, 0));
        root.setTop(title);

        // --- Formulario centrado ---
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(12));
        form.setAlignment(Pos.CENTER);

        // Columnas: labels derecha; campos crecen
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHalignment(HPos.RIGHT);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(c1, c2);

        // Campos
        TextField tfNombre    = new TextField();
        tfNombre.setPromptText("Nombre");
        TextField tfApellidos = new TextField();
        tfApellidos.setPromptText("Apellidos");
        TextField tfTelefono  = new TextField();
        tfTelefono.setPromptText("Ej. 600123123");
        // Solo dígitos (máx. 15)
        tfTelefono.setTextFormatter(new TextFormatter<>(ch ->
            ch.getControlNewText().matches("\\d{0,15}") ? ch : null));

        TextField tfEmail     = new TextField();
        tfEmail.setPromptText("ejemplo@correo.com");
        PasswordField pf1     = new PasswordField();
        pf1.setPromptText("Contraseña");
        PasswordField pf2     = new PasswordField();
        pf2.setPromptText("Repite la contraseña");

        // Añadir al grid
        form.add(new Label("Nombre:"),        0, 0); form.add(tfNombre,    1, 0);
        form.add(new Label("Apellidos:"),     0, 1); form.add(tfApellidos, 1, 1);
        form.add(new Label("Teléfono:"),      0, 2); form.add(tfTelefono,  1, 2);
        form.add(new Label("Email:"),         0, 3); form.add(tfEmail,     1, 3);
        form.add(new Label("Contraseña:"),    0, 4); form.add(pf1,         1, 4);
        form.add(new Label("Confirmar:"),     0, 5); form.add(pf2,         1, 5);

        // Envolver para centrar realmente
        StackPane center = new StackPane(form);
        center.setPadding(new Insets(8, 16, 16, 16));
        StackPane.setAlignment(form, Pos.CENTER);
        root.setCenter(center);

        // --- Botonera derecha ---
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setCancelButton(true);
        Button btnAceptar  = new Button("Aceptar");
        btnAceptar.setDefaultButton(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottom = new HBox(10, spacer, btnCancelar, btnAceptar);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(10, 8, 10, 8));
        root.setBottom(bottom);

        // --- Acciones ---
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
            // Validación sencilla de email
            if (!tfEmail.getText().matches(".+@.+\\..+")) {
                Utils.alertWarn("Email no válido.");
                return;
            }
            Utils.alertInfo("Registro (simulado) correcto.");
            root.getScene().getWindow().hide();
        });
    }

    public Parent getRoot() { return root; }
}
