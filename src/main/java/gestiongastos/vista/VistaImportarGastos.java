package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Categoria;
import gestiongastos.dominio.Gasto;
import gestiongastos.importador.ImportadorFactory;
import gestiongastos.importador.ImportadorGastos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class VistaImportarGastos {

    private final BorderPane root;
    private File archivoSeleccionado;

    // refs UI (para habilitar/deshabilitar y actualizar texto)
    private final TextField tfArchivo;
    private final Button btnImportar;

    public VistaImportarGastos() {

        root = new BorderPane();
        root.setPadding(new Insets(18));
        root.setPrefSize(560, 280);
        root.setStyle("-fx-background-color: #f6f7fb;");

        // ===== TOP: Título + subtítulo =====
        Label title = new Label("Importar gastos");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: 700;");

        Label subtitle = new Label("Selecciona un fichero CSV y pulsa Importar.");
        subtitle.setStyle("-fx-opacity: 0.75;");

        VBox top = new VBox(4, title, subtitle);
        top.setPadding(new Insets(6, 6, 12, 6));
        root.setTop(top);

        // ===== CARD CENTRAL =====
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setMaxWidth(480);
        card.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-radius: 12;" +
                "-fx-border-color: #e6e6e6;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 14, 0, 0, 4);"
        );

        // Fila selector de archivo
        Label lblArchivo = new Label("Archivo CSV");
        lblArchivo.setStyle("-fx-font-weight: 600;");

        tfArchivo = new TextField();
        tfArchivo.setPromptText("Ningún archivo seleccionado");
        tfArchivo.setEditable(false);
        tfArchivo.setFocusTraversable(false);

        Button btnSeleccionar = new Button("Seleccionar…");
        btnSeleccionar.setMinWidth(110);
        btnSeleccionar.setOnAction(e -> seleccionarArchivo());

        HBox rowArchivo = new HBox(10, tfArchivo, btnSeleccionar);
        rowArchivo.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(tfArchivo, Priority.ALWAYS);

        // Texto ayuda
        Label help = new Label("Formato admitido: .csv");
        help.setStyle("-fx-opacity: 0.7; -fx-font-size: 11px;");

        // Botones
        btnImportar = new Button("Importar");
        btnImportar.setDefaultButton(true);
        btnImportar.setDisable(true);
        btnImportar.setMinWidth(110);
        btnImportar.setStyle(
                "-fx-background-radius: 10;" +
                "-fx-font-weight: 700;" +
                "-fx-padding: 8 14 8 14;"
        );
        btnImportar.setOnAction(e -> onImportar());

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setCancelButton(true);
        btnCerrar.setMinWidth(110);
        btnCerrar.setOnAction(e -> root.getScene().getWindow().hide());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox botones = new HBox(10, spacer, btnCerrar, btnImportar);
        botones.setAlignment(Pos.CENTER_RIGHT);

        // Montaje card
        card.getChildren().addAll(
                lblArchivo,
                rowArchivo,
                help,
                new Separator(),
                botones
        );

        // Centrar card
        StackPane center = new StackPane(card);
        center.setPadding(new Insets(10));
        root.setCenter(center);
    }

    private void seleccionarArchivo() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar fichero CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));

        File f = fc.showOpenDialog(root.getScene().getWindow());
        if (f != null) {
            archivoSeleccionado = f;
            tfArchivo.setText(f.getName());
            btnImportar.setDisable(false);
        }
    }

    private void onImportar() {
        if (archivoSeleccionado == null) {
            // por si acaso
            Utils.alertWarn("Selecciona un archivo primero.");
            return;
        }

        try {
            importarArchivo(archivoSeleccionado.toPath());
        } catch (Exception ex) {
            Utils.alertError("Error al importar: " + ex.getMessage());
        }
    }

    private void importarArchivo(Path path) throws Exception {

        String ext = getExtension(path.toString());
        ImportadorGastos imp = ImportadorFactory.crear(ext);

        List<Gasto> lista = imp.importar(path);

        // resolver categorías por nombre:
        for (Gasto g : lista) {
            String nomCat = g.getCategoriaNombreTemp();
            if (nomCat != null && !nomCat.isBlank()) {
                Optional<Categoria> c = Controlador.getInstance()
                        .listarCategorias()
                        .stream()
                        .filter(cat -> cat.getNombre().equalsIgnoreCase(nomCat))
                        .findFirst();

                if (c.isPresent()) {
                    g.setCategoriaId(c.get().getId());
                } else {
                    Categoria nueva = Controlador.getInstance().crearCategoria(nomCat, "#888888");
                    g.setCategoriaId(nueva.getId());
                }
            }
        }

        for (Gasto g : lista) {
            Controlador.getInstance().registrarGasto(
                    g.getCantidad(),
                    g.getFecha(),
                    g.getCategoriaId(),
                    g.getNota()
            );
        }

        Utils.alertInfo("Importación completada.\nGastos importados: " + lista.size());
        root.getScene().getWindow().hide();
    }

    private String getExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        return (i >= 0) ? fileName.substring(i + 1) : "";
    }

    public Parent getRoot() {
        return root;
    }
}
