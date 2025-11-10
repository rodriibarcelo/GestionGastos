package gestiongastos.vista;

import gestiongastos.dominio.Categoria;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class VistaCategoriaForm {

    private final GridPane root;

    private final TextField tfNombre;
    private final TextField tfColor;

    private boolean ok;

    public VistaCategoriaForm(final Categoria original) {
        root = new GridPane();
        root.setHgap(8);
        root.setVgap(8);
        root.setPadding(new Insets(12));

        tfNombre = new TextField();
        tfColor  = new TextField();

        root.add(new Label("Nombre:"), 0, 0);
        root.add(tfNombre, 1, 0);
        root.add(new Label("Color (hex):"), 0, 1);
        root.add(tfColor, 1, 1);

        Button btnCancelar = new Button("Cancelar");
        Button btnAceptar  = new Button("Aceptar");
        root.add(btnCancelar, 0, 2);
        root.add(btnAceptar, 1, 2);

        if (original != null) {
            tfNombre.setText(original.getNombre());
            tfColor.setText(original.getColorHex());
        }

        btnCancelar.setOnAction(e -> root.getScene().getWindow().hide());
        btnAceptar.setOnAction(e -> {
            if (tfNombre.getText().trim().isEmpty()) {
                Utils.alertWarn("El nombre no puede estar vacío.");
                return;
            }
            ok = true;
            root.getScene().getWindow().hide();
        });
    }

    public Parent getRoot() {
        return root;
    }

    public boolean isOk() {
        return ok;
    }

    public String getNombre() {
        return tfNombre.getText().trim();
    }

    public String getColorHex() {
        return tfColor.getText().trim();
    }
}
