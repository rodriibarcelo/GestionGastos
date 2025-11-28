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
import java.util.UUID;

public class VistaImportarGastos {

    private final BorderPane root;
    private File archivoSeleccionado;

    public VistaImportarGastos() {

        root = new BorderPane();
        root.setPadding(new Insets(16));
        root.setPrefSize(480, 200);

        Label title = new Label("Importar gastos desde fichero");
        BorderPane.setAlignment(title, Pos.CENTER);
        root.setTop(title);

        Label lblArchivo = new Label("Archivo elegido: ninguno");

        Button btnSeleccionar = new Button("Seleccionar archivo…");
        btnSeleccionar.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
            File f = fc.showOpenDialog(root.getScene().getWindow());
            if (f != null) {
                archivoSeleccionado = f;
                lblArchivo.setText("Archivo: " + f.getName());
            }
        });

        Button btnImportar = new Button("Importar");
        btnImportar.setOnAction(e -> {
            if (archivoSeleccionado == null) {
                Utils.alertWarn("Selecciona un archivo primero.");
                return;
            }

            try {
                importarArchivo(archivoSeleccionado.toPath());
            } catch (Exception ex) {
                Utils.alertError("Error al importar: " + ex.getMessage());
            }
        });

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setOnAction(e -> root.getScene().getWindow().hide());

        VBox center = new VBox(12, lblArchivo, btnSeleccionar, btnImportar, btnCerrar);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(16));
        root.setCenter(center);
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

        Utils.alertInfo("Importación completada.\n" +
                        "Gastos importados: " + lista.size());

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
