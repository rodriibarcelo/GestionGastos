package gestiongastos.vista;

import gestiongastos.dominio.Categoria;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class VistaCategoriaForm {

    private final BorderPane root;

    private final TextField tfNombre;
    private final TextField tfColor;   // se mantiene como texto #RRGGBB para el dominio
    private final ColorPicker cpColor;

    private boolean ok;

    public VistaCategoriaForm(final Categoria original) {
        root = new BorderPane();
        root.setPadding(new Insets(16));

        // ------- Formulario centrado -------
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(8));
        form.setAlignment(Pos.CENTER);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHalignment(HPos.RIGHT);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(c1, c2);

        tfNombre = new TextField();
        tfNombre.setPromptText("Nombre de la categoría");

        tfColor  = new TextField();
        tfColor.setPromptText("#RRGGBB");
        // Valida mientras se escribe: permite vacío/partial y hasta 6 hex
        tfColor.setTextFormatter(new TextFormatter<>(ch -> {
            String nxt = ch.getControlNewText();
            return nxt.matches("#?[0-9a-fA-F]{0,6}") ? ch : null;
        }));

        cpColor = new ColorPicker();
        cpColor.setMaxWidth(Double.MAX_VALUE);

        // Sincronización bidireccional simple (hex <-> picker)
        tfColor.textProperty().addListener((obs, oldV, newV) -> {
            if (newV == null || newV.isBlank()) return;
            String hex = normalizaHex(newV);
            if (hex.length() == 7) { // #RRGGBB completo
                try { cpColor.setValue(Color.web(hex)); } catch (IllegalArgumentException ignored) {}
            }
        });
        cpColor.valueProperty().addListener((obs, o, c) -> {
            if (c != null) tfColor.setText(colorToHex(c));
        });

        form.add(new Label("Nombre:"), 0, 0); form.add(tfNombre, 1, 0);
        form.add(new Label("Color (hex):"), 0, 1); form.add(tfColor, 1, 1);
        form.add(new Label("Selector:"), 0, 2); form.add(cpColor, 1, 2);

        root.setCenter(form);

        // ------- Botonera derecha -------
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setCancelButton(true);
        Button btnAceptar  = new Button("Aceptar");
        btnAceptar.setDefaultButton(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bottom = new HBox(10, spacer, btnCancelar, btnAceptar);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(bottom);

        // ------- Cargar original (si hay) -------
        if (original != null) {
            tfNombre.setText(original.getNombre());
            String hex = normalizaHex(original.getColorHex());
            tfColor.setText(hex);
            try { cpColor.setValue(Color.web(hex)); } catch (Exception ignored) {}
        } else {
            // valor por defecto
            cpColor.setValue(Color.web("#4e8cff"));
            tfColor.setText("#4e8cff");
        }

        // ------- Acciones -------
        btnCancelar.setOnAction(e -> root.getScene().getWindow().hide());
        btnAceptar.setOnAction(e -> {
            if (tfNombre.getText().trim().isEmpty()) {
                Utils.alertWarn("El nombre no puede estar vacío.");
                return;
            }
            String hex = normalizaHex(tfColor.getText());
            if (!hex.matches("#[0-9a-fA-F]{6}")) {
                Utils.alertWarn("Color no válido. Usa formato #RRGGBB.");
                return;
            }
            tfColor.setText(hex); // normalizado
            ok = true;
            root.getScene().getWindow().hide();
        });
    }

    public Parent getRoot() { return root; }

    public boolean isOk() { return ok; }

    public String getNombre() { return tfNombre.getText().trim(); }

    public String getColorHex() { return normalizaHex(tfColor.getText()); }

    // ------- Helpers -------
    private static String normalizaHex(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.isEmpty()) return "";
        if (!s.startsWith("#")) s = "#" + s;
        if (s.length() == 4) { // #RGB -> #RRGGBB
            char r = s.charAt(1), g = s.charAt(2), b = s.charAt(3);
            s = "#" + (""+r+r+g+g+b+b);
        }
        return s;
    }

    private static String colorToHex(Color c) {
        int r = (int)Math.round(c.getRed()*255);
        int g = (int)Math.round(c.getGreen()*255);
        int b = (int)Math.round(c.getBlue()*255);
        return String.format("#%02x%02x%02x", r, g, b);
    }
}
