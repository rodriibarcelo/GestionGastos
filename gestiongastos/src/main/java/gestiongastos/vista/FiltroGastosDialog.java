package gestiongastos.vista;

import gestiongastos.controlador.Controlador;
import gestiongastos.dominio.Categoria;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;

public class FiltroGastosDialog {

    private final GridPane root;

    private final DatePicker dpDesde;
    private final DatePicker dpHasta;
    private final ComboBox<Object> cbCategoria; 

    private boolean ok;
    private CriteriosFiltroGastos criterios;
    
    public FiltroGastosDialog() { 
        this((javafx.stage.Stage) null); 
    }
    

    public FiltroGastosDialog(final javafx.stage.Stage owner) {
        root = new GridPane();
        root.setHgap(8);
        root.setVgap(8);
        root.setPadding(new Insets(12));

        dpDesde = new DatePicker();
        dpHasta = new DatePicker();

        cbCategoria = new ComboBox<>();
        cbCategoria.setItems(FXCollections.observableArrayList());
        cbCategoria.getItems().add("Todas");
        cbCategoria.getItems().addAll(Controlador.getInstance().listarCategorias());
        cbCategoria.getSelectionModel().selectFirst();

        root.add(new Label("Desde:"), 0, 0);
        root.add(dpDesde, 1, 0);
        root.add(new Label("Hasta:"), 0, 1);
        root.add(dpHasta, 1, 1);
        root.add(new Label("Categoría:"), 0, 2);
        root.add(cbCategoria, 1, 2);

        Button btnCancelar = new Button("Cancelar");
        Button btnAplicar = new Button("Aplicar");
        root.add(btnCancelar, 0, 3);
        root.add(btnAplicar, 1, 3);

        btnCancelar.setOnAction(e -> {
            ok = false;
            root.getScene().getWindow().hide();
        });

        btnAplicar.setOnAction(e -> onAplicar());
    }

    private void onAplicar() {
        CriteriosFiltroGastos c = new CriteriosFiltroGastos();
        LocalDate d1 = dpDesde.getValue();
        LocalDate d2 = dpHasta.getValue();
        c.setDesde(d1);
        c.setHasta(d2);

        Object sel = cbCategoria.getSelectionModel().getSelectedItem();
        if (sel instanceof Categoria cat) {
            c.setCategoriaId(cat.getId());
        } else {
            c.setCategoriaId(null); // "Todas"
        }

        this.criterios = c;
        this.ok = true;
        root.getScene().getWindow().hide();
    }

    public Parent getRoot() {
        return root;
    }

    public boolean isOk() {
        return ok;
    }

    public CriteriosFiltroGastos getCriterios() {
        return criterios;
    }
}
